package rules;

import tcore.Composer;
import tcore.LHS;
import tcore.messages.Packet;
import tcore.strategy.Matcher;

/**
 * Finds a match for the LHS.
 *
 * @author Pierre-Olivier Talbot
 */
public class Query extends Composer {

    private String name;
    private Matcher matcher;

    Query(String name, LHS lhs) {
        this.matcher = new Matcher(lhs, Integer.MAX_VALUE);
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

    public String getName() {
        return name;
    }
}
