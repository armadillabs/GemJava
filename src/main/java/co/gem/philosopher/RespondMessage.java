package co.gem.philosopher;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by Philip on 2/29/16.
 */
@Data
public class RespondMessage implements Serializable {
    private Waiter originator;
    // waiter >> philosopher states: 10 = eat
    private int state;
    public RespondMessage(Waiter originator, int state) {
        this.originator = originator;
        this.state = state;
    }

}
