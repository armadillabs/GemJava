package co.gem.philosopher;

import co.paralleluniverse.actors.ActorSpec;
import co.paralleluniverse.actors.behaviors.Supervisor;
import co.paralleluniverse.actors.behaviors.SupervisorActor;
import co.paralleluniverse.fibers.SuspendExecution;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

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

        Supervisor sup = new SupervisorActor(
                SupervisorActor.RestartStrategy.ALL_FOR_ONE,
                new Supervisor.ChildSpec("Waiter",
                        Supervisor.ChildMode.PERMANENT,
                        5,
                        1,
                        TimeUnit.SECONDS,
                        3,
                        ActorSpec.of(Waiter.class))).spawnThread();


        int k = 0;
        for (Name name : Name.values()) {
            Philosopher thisP = new Philosopher(name, table, waiter, k);
//            sup.addChild(new Supervisor.ChildSpec(name.toString(),
//                    Supervisor.ChildMode.PERMANENT,
//                    5,
//                    1,
//                    TimeUnit.SECONDS,
//                    3,
//                    ActorSpec.of(Philosopher.class,
//                            name, table, waiter, k)));
            thisP.spawnThread();
            k++;
        }

    }
}
