package tcore.messages;

import tcore.LHS;
import tcore.Model;

import java.util.ArrayList;

/**
 * The principal message to exchange information between two {@link tcore.Primitive}.
 *
 * @author Pierre-Olivier Talbot
 * @version 0.5
 * @since 2017-12-08
 */
public class Packet {
    private ArrayList<MatchSet> matchSets;
    private MatchSet currentMatchSet;
    private Model model;
    private LHS lhs;


    public Packet(Model m) {
        super();
        model = m;
        matchSets = new ArrayList<>();
    }

    /**
     * Copy constructor.
     *
     * @param p The packet to copy.
     */
    public Packet(Packet p) {
        super();
        model = new Model(p.getModel().getName(), p.getModel().getModelPath(), p.getModel().getMetamodel());
        matchSets = new ArrayList<>();
        for (MatchSet ms : p.matchSets) {
            matchSets.add(new MatchSet(ms));
        }
        int indexOfCurrent = p.matchSets.indexOf(getCurrentMatchSet());
        currentMatchSet = matchSets.get(indexOfCurrent);
    }

    /**
     * Removes the dirty status from every node from each Match inside this packet.
     */
    public void clean() {
        for (MatchSet ms : matchSets) {
            for (Match m : ms.getMatches()) {
                m.clean();
            }
        }
    }


    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public LHS getLhs() {
        return lhs;
    }

    public void setLhs(LHS lhs) {
        this.lhs = lhs;
    }

    public ArrayList<MatchSet> getMatchSets() {
        return matchSets;
    }

    public MatchSet getCurrentMatchSet() {
        return currentMatchSet;
    }

    public void setCurrentMatchSet(MatchSet currentMatchSet) {
        this.currentMatchSet = currentMatchSet;
    }

    public void addMatchSet(MatchSet ms) {
        matchSets.add(ms);
    }

    public void removeMatchSet(MatchSet ms) {
        matchSets.remove(ms);
    }
}
