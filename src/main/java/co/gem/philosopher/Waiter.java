package co.gem.philosopher;

import co.paralleluniverse.actors.BasicActor;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.Sets;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * Created by Philip on 2/26/16.
 * This is the Waiter actor that arbitrates requests from Philosophers for locking
 */
@Slf4j
@ToString
public class Waiter extends BasicActor<RequestMessage, Void> {

    private Set<Name> eaters = Sets.newHashSet();

    @Override
    @Suspendable
    protected Void doRun() throws InterruptedException, SuspendExecution {
        // just limit the number of rounds for now
        for (int k = 0; k<100; k++) {
            RequestMessage m = receive();
            log.info("Waiter received request: {}", m);
            int curState = m.getState();
            Name curName = m.getOriginator().getLabel();
            if (curState == -1) break;
            // philosopher requests to eat
            if (curState == 1 && !eaters.contains(curName)) {
                eaters.add(curName);
                log.info("waiter serving {}", curName);
                //m.getOriginator().ref().send(new RespondMessage(this, 10), 30, TimeUnit.SECONDS);
                RespondMessage servingMessage = new RespondMessage(this, 10);

                while (!m.getOriginator().ref().trySend(servingMessage)) {
                    m.getOriginator().ref().trySend(servingMessage);
                }
                //m.getOriginator().ref().send(new RespondMessage(this, 10));
            }
            else if (curState == 2) {
                log.info("waiter cleaning up {}", curName);
                eaters.remove(curName);
            }
        }
        log.info("waiter exhausted.");
        return null;
    }
}
