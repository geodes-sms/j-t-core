package rules;

import tcore.LHS;
import tcore.RHS;
import tcore.Rollbacker;
import tcore.messages.Packet;

/**
 * Applies the transformation as long as matches can be found with roll-back capability.
 *
 * @author Pierre-Olivier Talbot
 * @author An Li
 */
public class XSRule extends SRule {

    XSRule(String name, LHS lhs, RHS rhs, boolean withResolver, String nameAlgo) {
        super(name, lhs, rhs, withResolver, nameAlgo);
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
