package rules;

import tcore.LHS;
import tcore.RHS;
import tcore.Rollbacker;
import tcore.messages.Packet;

/**
 * Applies the transformation on one match with roll-back capability.
 *
 * @author Pierre-Olivier Talbot
 * @author An Li
 */
public class XRule extends ARule {


    XRule(String name, LHS lhs, RHS rhs, boolean withResolver, boolean useVF2) {
        super(name, lhs, rhs, withResolver, useVF2);
    }


    @Override
    public Packet packetIn(Packet p) {
        Rollbacker rollbacker = new Rollbacker(1);
        rollbacker.packetIn(p);

        p = super.packetIn(p);

        if (isSuccess) {
            return p;
        } else {
            return rollbacker.restore();
        }
    }

    @Override
    public Packet nextIn(Packet p) {
        return super.nextIn(p);
    }
}

