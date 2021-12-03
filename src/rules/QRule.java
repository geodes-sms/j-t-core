package rules;

import tcore.Composer;
import tcore.LHS;
import tcore.messages.Packet;
import tcore.strategy.Matcher;

/**
 * Finds a match for the LHS.
 *
 *
 * Class name Query changed to QRule.
 *
 * @author Pierre-Olivier Talbot
 * @author Sebastien Ehouan
 */
public class QRule extends Composer {

    private String name;
    private Matcher matcher;

    QRule(String name, LHS lhs, boolean useVF2) {
        this.matcher = new Matcher(lhs, Integer.MAX_VALUE, useVF2);
        this.name = name;
    }

    /**
     * Tries to find matches for the LFS. Returns null if none is found.
     *
     * @param p The input packet.
     * @return The original packet p if matches are found, null otherwise.
     */
    @Override
    public Packet packetIn(Packet p) {
        Packet s = new Packet(p);
        p = matcher.packetIn(p);
        if (p.getCurrentMatchSet().getMatches().isEmpty()) {
            return null;
        } else {
            return s;
        }
    }

    @Override
    public Packet nextIn(Packet p) {
        return packetIn(p);
    }

    /**
     * Get name.
     * 
     * @return
     */
    public String getName() {
        return name;
    }
}
