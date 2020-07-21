package tcore.strategy;

import tcore.LHS;
import tcore.Model;
import tcore.RulePrimitive;
import tcore.messages.Match;
import tcore.messages.MatchSet;
import tcore.messages.Packet;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * T-Core primitive meant for matching a {@link LHS} with a given {@link Model}.
 *
 * @author Pierre-Olivier Talbot
 * @since 2017-12-01
 */

public class Matcher extends RulePrimitive {
	private IMatchAlgoFactory matchAlgoFactory;
	
    /**
     * Maximum number of matches authorized.
     */
    private int max;

    /**
     * The left hand side to match.
     */
    private LHS lhs;

    /**
     * The model in which to find a match.
     */
    private Model model;

    public Matcher(LHS lhs, int max) {
        super();
        if (max <= 0) {
            throw new IllegalArgumentException("Matcher's maximum number of iterations must be greater than 0.");
        }
        this.max = max;
        this.lhs = lhs;
        this.matchAlgoFactory = new SimpleMatchFactory();
    }

    /**
     * Receives and processes a packet for matching procedure.
     *
     * @param p The packet.
     * @return The resulting packet.
     */
    @Override
    public Packet packetIn(Packet p) {
        isSuccess = false;
        if (p.getModel() == null) {
            throw new IllegalArgumentException("The packet sent to the Matcher must contain a valid model.");
        }
        
        model = p.getModel();

        ArrayList<Match> allMatches = match();
        ArrayList<Match> chosenMatches = new ArrayList<>();
        for (int i = 0; i < allMatches.size() && i < max; i++) {
            chosenMatches.add(allMatches.get(i));
        }
        MatchSet ms = new MatchSet(chosenMatches, lhs);
        p.setCurrentMatchSet(ms);
        p.setLhs(lhs);
        isSuccess = true;
        exception = null;

        return p;
    }

    /**
     * Tries to match the {@link LHS} to the {@link Model}.
     * 
     * @return A list of matches.
     * @throws FileNotFoundException 
     */
    public ArrayList<Match> match() { //call matchingalogrithm.match()

        IMatchAlgo lhsMatcher = matchAlgoFactory.createMatchAlgo(lhs, max, model);
		
        ArrayList<Match> results = lhsMatcher.match();
//        
//        # Either there are no NACs, or there were only unbound NACs that do not match, so match the LHS now
//        bound_NACs.sort(key=lambda nac: nac.bridge.vcount(), reverse=True)
//        if not bound_NACs:
//            IMatchAlgo lhsMatcher = matchAlgoFactory.createMatchAlgo(lhs, max);
//            # Convert the pivots
//            lhs_pivots = pivots.to_mapping(graph, self.condition)
//            try:
//                for mapping in lhsMatcher.match_iter(context=lhs_pivots):
//                    if self.condition.constraint(lambda i: getSourceNodeFromLabel(i, mapping, self.condition), graph):
//                        yield mapping
//            except: raise
//            finally: lhsMatcher.reset_recursion_limit()
//            
//            # The matching is complete
//            return
//        
//        # Continue the matching looking for the LHS now
//        IMatchAlgo lhsMatcher = matchAlgoFactory.createMatchAlgo(lhs, max);
//        # Augment the bridge mapping with the pivot mappings
//        lhs_pivots = pivots.to_mapping(graph, self.condition)
//        
//        try:
//            for mapping in lhsMatcher.match_iter(context=lhs_pivots):
//                if self.condition.constraint(lambda i: getSourceNodeFromLabel(i, mapping, self.condition), graph):
//                    # A match of the LHS is found: ensure that no remaining NAC do match
//                    invalid = False
//                    for NAC in bound_NACs:
//                        # This mapping represents the mapping of the bridge of this NAC with the LHS
//                        match = Match()
//                        match.from_mapping(mapping, graph, self.condition)
//                        bridgeMapping = match.to_mapping(graph, NAC)
//                        
//                        # Now continue the matching looking for a match of the corresponding NAC
//                        nacMatcher = HimesisMatcher(source_graph=graph, pattern_graph=NAC, pred1=pred1, succ1=succ1)
//                        for nac_mapping in nacMatcher.match_iter(context=bridgeMapping):
//                            if NAC.constraint(lambda i: getSourceNodeFromLabel(i, nac_mapping, NAC), graph):
//                                # An occurrence of the NAC is found: current mapping is not valid
//                                invalid = True
//                                break
//                        if invalid:
//                            # An occurrence of the NAC was found: current mapping is not valid
//                            break
//                    else:
//                        # Either there are no bound NACs or no occurrence of any bound NAC was found: current mapping is valid
//                        yield mapping
//        except: raise
//        finally: lhsMatcher.reset_recursion_limit()

//		@NotNull IMatchAlgo lhsMatcher = matchAlgoFactory.createIMatchAlgo(lhs, max, "VF2");
//		
//		//Using lhsMatcher to implement the VF2 matching algorithm
//		lhsMatcher.match();
        
        return results;
		
    }
}