package tcore;

import tcore.messages.Cancel;
import tcore.messages.Packet;
import tcore.messages.exceptions.EmptyControlPrimitiveException;

import java.util.ArrayList;
import java.util.Random;

/**
 * T-Core primitive meant for choosing a {@link Packet} to process next in a concurrency paradigm.
 *
 * @author Pierre-Olivier Talbot
 * @since 2017-12-01
 */
public class Selector extends ControlPrimitive {

    public ArrayList<LHS> exclusions;
    private Random r;

    public Selector(ArrayList<LHS> exclusions) {
        super();
        this.exclusions = exclusions;
        r = new Random();
    }

    public synchronized Packet select() {
        exception = null;
        if (successList.isEmpty()) {
            if (failList.isEmpty()) {
                isSuccess = false;
                exception = new EmptyControlPrimitiveException();
                return null;
            } else {
                isSuccess = false;
                return failList.get(r.nextInt(failList.size()));
            }
        } else {
            isSuccess = true;
            return successList.get(r.nextInt(successList.size()));
        }
    }

    public synchronized Cancel cancel() {
        isSuccess = false;
        exception = null;
        successList.clear();
        failList.clear();
        return new Cancel(exclusions);
    }
}
