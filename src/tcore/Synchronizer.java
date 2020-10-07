package tcore;

import org.jetbrains.annotations.Nullable;
import tcore.messages.Match;
import tcore.messages.Packet;
import tcore.messages.exceptions.MergingFailedException;
import tcore.messages.exceptions.SynchronizerNotReadyException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * T-Core primitive meant for merging a set of {@link Packet} resulting from concurrent executions.
 *
 * @author Pierre-Olivier Talbot
 * @since 2017-12-01
 */
public class Synchronizer extends ControlPrimitive {

    private int numberOfThreads;
    private Method customMerge;
    private Packet mergedPacket;

    /**
     * @param numberOfThreads
     * @param customMerge
     */
    public Synchronizer(int numberOfThreads, @Nullable Method customMerge) {
        if (numberOfThreads < 2) {
            throw new IllegalArgumentException("The number of threads in a Synchronizer must be greater or equal than 2.");
        }
        this.numberOfThreads = numberOfThreads;
        this.customMerge = customMerge;
    }

    /**
     * Merges the {@link Packet}s in the successList.
     *
     * @return The resulting merged packet.
     */
    public synchronized Packet merge() {
        if (successList.size() + failList.size() != numberOfThreads) {
            isSuccess = false;
            exception = new SynchronizerNotReadyException();
            return null;
        }

        if (customMerge != null) {
            try {
                customMerge();
                return mergedPacket;
            } catch (InvocationTargetException | IllegalAccessException e) {
                exception = e;
                isSuccess = false;
            }
        } else if (defaultMerge()) {
            isSuccess = true;
            return mergedPacket;
        }

        return null;
    }

    /**
     * TODO: 2017-12-09 Add support for multiple MatchSets merging.
     *
     * @return true if the merging process was successful, false otherwise.
     */
    protected boolean defaultMerge() {
        mergedPacket = new Packet(successList.get(0));
        for (Packet p : successList) {
            if (mergedPacket.getModel() != p.getModel()) {
                exception = new MergingFailedException("The packets don't share the same model.");
                return false;
            }

            for (Match match : p.getCurrentMatchSet().getMatches()) {
                if (!mergedPacket.getCurrentMatchSet().getMatches().contains(match)) {
                    mergedPacket.getCurrentMatchSet().getMatches().add(match);
                }
            }
        }

        return true;
    }

    /**
     * Custom merging method invoked using the reflection API.
     *
     * @return true if the custom method is implemented, false otherwise.
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    protected boolean customMerge() throws InvocationTargetException, IllegalAccessException {
        if (customMerge == null) {
            return false;
        } else {
            mergedPacket = (Packet) customMerge.invoke(this, (Object) null);
            return true;
        }
    }
}
