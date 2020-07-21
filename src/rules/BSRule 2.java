package rules;

import tcore.Composer;
import tcore.messages.Packet;

import java.util.ArrayList;

/**
 * Selects a branch in which the matcher succeeds, as long as matches can be found.
 *
 * @author Pierre-Olivier Talbot
 */
public class BSRule extends Composer {

    private BRule bRule;
    private int maxIterations;
    private int iterationCounter;

    BSRule(ArrayList<ARule> branches) {
        bRule = new BRule(branches);
        maxIterations = Integer.MAX_VALUE;
        iterationCounter = 0;
    }

    @Override
    public Packet packetIn(Packet p) {
        boolean found = true;
        Packet currentPacket = new Packet(p);
        while (found && iterationCounter < maxIterations) {
            currentPacket = bRule.packetIn(currentPacket);
            if (!bRule.isSuccess()) {
                if (bRule.getException() == null) {
                    found = false;
                } else {
                    isSuccess = false;
                    exception = bRule.getException();
                    return p;
                }
            }
            iterationCounter++;
        }
        isSuccess = true;
        exception = null;
        return currentPacket;
    }

    @Override
    public Packet nextIn(Packet p) {
        return packetIn(p);
    }
}
