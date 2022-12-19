package tcore.strategy;

import tcore.LHS;
import tcore.Model;
import tcore.RulePrimitive;
import tcore.constant.JTCoreConstant;
import tcore.messages.Match;
import tcore.messages.MatchSet;
import tcore.messages.Packet;
import java.util.ArrayList;

import org.eclipse.xsd.XSDSimpleTypeDefinition;

import graph.Graph;

/**
 * T-Core primitive meant for matching a {@link LHS} with a given {@link Model}.
 *
 * @author Pierre-Olivier Talbot
 * @author Sebastien Ehouan
 * @since 2017-12-01
 */

public class Matcher extends RulePrimitive {

	private IMatchAlgo iMatchAlgo;

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

	private String nameAlgo;

	/**
	 * @param lhs
	 * @param max
	 * @param useVF2 True if VF2 is used, False otherwise
	 */
	public Matcher(LHS lhs, int max, String nameAlgo) {
		super();
		if (max <= 0) {
			throw new IllegalArgumentException("Matcher's maximum number of iterations must be greater than 0.");
		}
		this.max = max;
		this.lhs = lhs;
		this.nameAlgo = nameAlgo;
		switch (nameAlgo) {
		case JTCoreConstant.SIMPLEMATCH_ALGORITHM:
			SimpleMatchFactory simpleMatchFactory = new SimpleMatchFactory();
			this.iMatchAlgo = simpleMatchFactory.createMatchAlgo(lhs, max, model);
			break;
		case JTCoreConstant.VF2_ALGORITHM:
			VF2MatchAlgoFactory VF2Factory = new VF2MatchAlgoFactory();
			this.iMatchAlgo = VF2Factory.createMatchAlgo(lhs, max, model);
			break;
		default:
			throw new IllegalArgumentException("Unexpected value: " + nameAlgo);
		}
		// this.iMatchAlgo = SimpleMatchFactory.createMatchAlgo(lhs, max, model,
		// nameAlgo);
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
		Graph mg = model.getGraph();
		Graph precGraph = lhs.getPreconditionPattern().getGraph();

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
	 */
	public ArrayList<Match> match() {

		IMatchAlgo lhsMatcher;

		switch (nameAlgo) {
		case JTCoreConstant.SIMPLEMATCH_ALGORITHM:
			SimpleMatchFactory simpleMatchFactory = new SimpleMatchFactory();
			lhsMatcher = simpleMatchFactory.createMatchAlgo(lhs, max, model);
			break;
		case JTCoreConstant.VF2_ALGORITHM:
			VF2MatchAlgoFactory VF2Factory = new VF2MatchAlgoFactory();
			lhsMatcher = VF2Factory.createMatchAlgo(lhs, max, model);
			break;
		default:
			throw new IllegalArgumentException("Unexpected value: " + nameAlgo);
		}

		ArrayList<Match> results = lhsMatcher.match();

		/**
		 * Code implementation from the Python version for reference
		 */

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
		/** END */

		// Using lhsMatcher to implement the VF2 matching algorithm
		/** YET TO FINALIZE -- DO NOT USE */
//      IMatchAlgo lhsMatcher = matchAlgoFactory.createMatchAlgo(lhs, max, model);
//		ArrayList<Match> results = lhsMatcher.match();

		return results;

	}
}