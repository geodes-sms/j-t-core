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


    /**
     * @param m
     */
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


    /**
     * Get the model.
     * 
     * @return
     */
    public Model getModel() {
        return model;
    }

    /**
     * Set model.
     * 
     * @param model
     */
    public void setModel(Model model) {
        this.model = model;
    }

    /**
     * Get LHS.
     * 
     * @return
     */
    public LHS getLhs() {
        return lhs;
    }

    /**
     * Set LHS.
     * 
     * @param lhs
     */
    public void setLhs(LHS lhs) {
        this.lhs = lhs;
    }

    /**
     * Get the match sets.
     * 
     * @return
     */
    public ArrayList<MatchSet> getMatchSets() {
        return matchSets;
    }

    /**
     * Get the current match set.
     * 
     * @return
     */
    public MatchSet getCurrentMatchSet() {
        return currentMatchSet;
    }

    /**
     * Set the current macth set.
     * 
     * @param currentMatchSet
     */
    public void setCurrentMatchSet(MatchSet currentMatchSet) {
        this.currentMatchSet = currentMatchSet;
    }

    /**
     * Add match set.
     * 
     * @param ms
     */
    public void addMatchSet(MatchSet ms) {
        matchSets.add(ms);
    }

    /**
     * Remove match set.
     * 
     * @param ms
     */
    public void removeMatchSet(MatchSet ms) {
        matchSets.remove(ms);
    }
}
