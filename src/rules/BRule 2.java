package rules;

import tcore.Composer;
import tcore.messages.Packet;

import java.util.ArrayList;
import java.util.Random;

/**
 * Selects a branch in which the matcher succeeds.
 * // TODO: 2017-12-13 Implement using concurrency and a Selector.
 *
 * @author Pierre-Olivier Talbot
 */
public class BRule extends Composer {

    protected ArrayList<ARule> branches;
    protected Random r;

    BRule(ArrayList<ARule> branches) {
        this.branches = new ArrayList<>(branches);
        r = new Random();

    }

    @Override
    public Packet packetIn(Packet p) {
        isSuccess = false;
        exception = null;

        Packet copyPacket;

        while (!isSuccess && !branches.isEmpty()) {
            copyPacket = new Packet(p);
            ARule randomARule = branches.get(r.nextInt(branches.size()));
            copyPacket = randomARule.packetIn(copyPacket);

            if (!randomARule.isSuccess()) {
                if (randomARule.getException() != null) {
                    isSuccess = false;
                    exception = randomARule.getException();
                    return p;
                } else {
                    branches.remove(randomARule);
                }
            } else {
                isSuccess = true;
                return copyPacket;
            }
        }

        isSuccess = false;
        return p;
    }

    @Override
    public Packet nextIn(Packet p) {
        return packetIn(p);
    }
}
