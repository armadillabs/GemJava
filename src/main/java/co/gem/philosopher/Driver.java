package co.gem.philosopher;

import co.paralleluniverse.fibers.SuspendExecution;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

/**
 * Created by Philip on 2/26/16.
 * This is the driver program
 */
@Slf4j
public class Driver {

    public static void main(String[] args) throws SuspendExecution, InterruptedException, ExecutionException {
        Table table = new Table(Name.values().length);
        Waiter waiter = new Waiter();
        waiter.spawnThread();

        int k = 0;
        for (Name name : Name.values()) {
            Philosopher thisP = new Philosopher(name, table, waiter, k);
            thisP.spawnThread();
            //thisP.join();
            k++;
        }
        waiter.join();
    }
}
