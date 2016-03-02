package co.gem.philosopher;

import co.paralleluniverse.actors.BasicActor;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.Suspendable;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Philip on 2/26/16.
 * This is the client actor that represents the philosopher, and subclasses the {@link BasicActor} class.
 */
@Slf4j
@ToString
@Getter
public class Philosopher extends BasicActor {
    private Name label;
    private Table table;
    private Waiter waiter;
    private int position;
    private Fork lFork, rFork;
    private Random rn;
    private AtomicInteger count;
    private static int randTime = 1000;

    public Philosopher(Name label, Table table, Waiter waiter, int position) {
        super(label.toString());
        this.label = label;
        this.table = table;
        this.waiter = waiter;
        this.lFork = table.getLeftFork(position);
        this.rFork = table.getRightFork(position);
        this.position = position;
        //count = new AtomicInteger(0);
        rn = new Random(1);
        log.info("{} has awakened.", label);

        // start by thinking
        try {
            think();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (SuspendExecution suspendExecution) {
            suspendExecution.printStackTrace();
        }
    }

    @Override
    protected Void doRun() throws InterruptedException, SuspendExecution {

        for (;;) {

            // receive message from waiter to eat
            // send message to waiter for request to eat
            RespondMessage m = (RespondMessage) receive();

            if (m.getState() == 10) eat();

            // if (m.getState() == -1 || count.get() >= 10) break;
            if (false) break;
        }
        log.info("{} stopped.", label);
        return null;
    }

    /**
     * Constructs the message that's bound for the waiter to request for
     * @return
     */
    private RequestMessage requestToEatMesg() {
        return new RequestMessage(this, 1);
    }

    /**
     * Constructs the message that releases the philosopher from eating
     * @return
     */
    private RequestMessage doneEatingMesg() {
        return new RequestMessage(this, 2);
    }

    /**
     * sends message to the waiter actor
     * @throws SuspendExecution
     */
    private void requestToEat() throws SuspendExecution {
        log.info("{} requests to eat.", label);
        waiter.ref().send(requestToEatMesg());
    }

    /**
     * done eating
     * @throws SuspendExecution
     */
    private void doneEating() throws SuspendExecution {
        log.info("{} is done eating.", label);
        waiter.ref().send(doneEatingMesg());
    }

    /**
     * Blocking method that requests for shared resources
     */
    private void takeForks() {
        lFork.take();
        log.info("{} got hold of his left fork.", label);
        rFork.take();
        log.info("{} got hold of his right fork.", label);
    }

    /**
     * non-blocking call that drops the forks
     */
    private void dropForks() {
        lFork.drop();
        rFork.drop();
    }

    /**
     * think state that messages the waiter to serve this philosopher
     * @throws InterruptedException
     * @throws SuspendExecution
     */
    @Suspendable
    private void think() throws InterruptedException, SuspendExecution {
        log.info("{} is thinking.", label);
        Thread.sleep(rn.nextInt(randTime)+1);
        requestToEat();
    }

    /**
     * initiated by the waiter, this is arbitrated by the waiter to make sure we don't run into deadlock.
     */
    @Suspendable
    private void eat() throws SuspendExecution, InterruptedException {
        takeForks();
        log.info("{} is eating.", label);
        //count.addAndGet(1);
        Thread.sleep(rn.nextInt(randTime)+1);
        dropForks();
        doneEating();
        think();
    }
}
