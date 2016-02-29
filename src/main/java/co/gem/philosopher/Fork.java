package co.gem.philosopher;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Philip on 2/26/16.
 */
public class Fork {
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * blocking lock acquisition
     */
    public void take() {
        lock.lock();
    }

    public void drop() {
        lock.unlock();
    }

    public Boolean isTaken() {
        return lock.isLocked();
    }
}
