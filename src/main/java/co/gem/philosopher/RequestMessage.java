package co.gem.philosopher;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by Philip on 2/26/16.
 */
@Data
public class RequestMessage implements Serializable {
    private Philosopher originator;
    // philosopher >> waiter states: 1 = eat, 2 = done eating
    private int state;
    public RequestMessage(Philosopher originator, int state) {
        this.originator = originator;
        this.state = state;
    }
}
