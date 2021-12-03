package rules;

import tcore.LHS;
import tcore.RHS;
import tcore.messages.Packet;

/**
 * Applies the transformation as long as matches can be found.
 *
 * @author Pierre-Olivier Talbot
 */
public class SRule extends ARule {

    SRule(String name, LHS lhs, RHS rhs, boolean withResolver, boolean useVF2) {
        super(name, lhs, rhs, withResolver, useVF2);
    }

    @Override
    public Packet packetIn(Packet p) {
        boolean first = true;
        Packet copy = new Packet(p);
        while (isSuccess || first) {
            copy = p;
            p = super.packetIn(p);

            if (first && !isSuccess) {
                isSuccess = false;
                return copy;
            } else {
                first = false;
            }
        }

        isSuccess = true;
        return copy;
    }
}
