package rules;

import tcore.LHS;
import tcore.RHS;
import tcore.Rollbacker;
import tcore.messages.Packet;

/**
 * Applies the transformation on all matches found with roll-back capability.
 *
 * @author Pierre-Olivier Talbot
 */
public class XFRule extends FRule {

    XFRule(String name, LHS lhs, RHS rhs, boolean withResolver, boolean useVF2) {
        super(name, lhs, rhs, withResolver, useVF2);
    }

    @Override
    public Packet packetIn(Packet p) {
        Rollbacker rollbacker = new Rollbacker(Integer.MAX_VALUE);
        rollbacker.packetIn(p);

        p = super.packetIn(p);

        if (isSuccess) {
            return p;
        } else {
            return rollbacker.restore();
        }
    }
}
