package tcore.messages;

import tcore.LHS;

import java.util.ArrayList;

/**
 * A container for matches between a {@link tcore.Model} and a {@link tcore.LHS}.<br>
 * Each MatchSet is associated with a single {@link LHS}.
 *
 * @author Pierre-Olivier Talbot
 * @version 0.5
 * @since 2017-12-08
 */
public class MatchSet {
    /**
     * The left hand side associated with this MatchSet.
     */
    private LHS lhs;

    /**
     * The next Match scheduled to be rewritten.
     */
    private Match matchToRewrite;

    /**
     * The matches contained in this MatchSet.
     */
    private ArrayList<Match> matches;

    /**
     * Constructor.
     *
     * @param matches The matches to be contained inside this MatchSet.
     * @param lhs     The left hand side associated with this MatchSet.
     */
    public MatchSet(ArrayList<Match> matches, LHS lhs) {
        this.lhs = lhs;
        this.matches = matches;
    }

    /**
     * Overriden equal function.
     */
    @Override
	public boolean equals(Object o) {
    	if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        MatchSet matchSet = (MatchSet) o;
    	
    	//foreach i,m in this.match : isequal &= m.equals(others.match[i]);
    	
    	for(int i=0; i<this.matches.size(); i++) {
    		if (!this.matches.get(i).equals(matchSet.matches.get(i))) {
    			return false;
    		}
    	}
    	
    	return true;
    }

    /**
     * Copy constructor.
     *
     * @param ms The MatchSet to copy.
     */
    public MatchSet(MatchSet ms) {
        lhs = ms.lhs;
        matches = new ArrayList<>();
        for (Match m : ms.matches) {
            matches.add(new Match(m));
        }
        matchToRewrite = ms.matchToRewrite;
    }


    public boolean hasDirtyMatches() {
        for (Match m : matches) {
            if (m.hasDirtyNodes()) return true;
        }
        return false;
    }

    public LHS getLhs() {
        return lhs;
    }

    public Match getMatchToRewrite() {
        return matchToRewrite;
    }

    public ArrayList<Match> getMatches() {
        return matches;
    }

    public void setMatchToRewrite(Match matchToRewrite) {
        this.matchToRewrite = matchToRewrite;
    }
}
