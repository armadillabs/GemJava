package co.gem.philosopher;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

/**
 * Created by Philip on 2/26/16.
 */
@Data
@Slf4j
public class Table {
    private ArrayList<Fork> forkArrayList;
    private int seats;

    public Table(int seats) {
        forkArrayList = Lists.newArrayList();
        this.seats = seats;
        for (int k=0; k<seats; k++) {
            forkArrayList.add(new Fork());
        }
    }

    public Fork getLeftFork(int position) {
        return forkArrayList.get((((position-1) % seats) + seats) % seats);
    }

    public Fork getRightFork(int position) {
        return forkArrayList.get(position % seats);
    }
}
