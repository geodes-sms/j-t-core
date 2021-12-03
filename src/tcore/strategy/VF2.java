package tcore.strategy;

import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import graph.Edge;
import graph.Graph;
import graph.Node;
import tcore.*;
import tcore.messages.Match;
import utils.Utils;

/**
 * VF2 Algorithm implementation based on (sub)graph isomorphism
 * 
 * YET TO BE FINALIZED -- DO NOT USE
 *
 * @author Sebastien EHouan
 * @author An Li
 * @since 2020-04-25
 */
public class VF2 implements IMatchAlgo {
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

	/**
	 * EMF label attribute.
	 */
	private EStructuralFeature label;

	/**
	 * @param lhs
	 * @param max
	 * @param model
	 */
	public VF2(LHS lhs, int max, Model model) {
		super();
		if (max <= 0) {
			throw new IllegalArgumentException("Matcher's maximum number of iterations must be greater than 0.");
		}
		this.max = max;
		this.lhs = lhs;
		this.model = model;
	}

	/**
	 * Find matches given a query graph and a set of target graphs
	 * 
	 * @param graphSet   Target graph set
	 * @param queryGraph Query graph
	 * @return The state set containing the mappings
	 */
	public ArrayList<State> matchGraphSetWithQuery(ArrayList<Graph> graphSet, Graph queryGraph) {
		ArrayList<State> stateSet = new ArrayList<State>();
		for (Graph targetGraph : graphSet) {
			State resState = matchGraphPair(targetGraph, queryGraph);
			if (resState.matched) {
				stateSet.add(resState);
			}
		}

		return stateSet;
	}

	/**
	 * Figure out if the target graph contains query graph
	 * 
	 * Equivalent of the has_match method in Python
	 * 
	 * @param targetGraph Big Graph
	 * @param queryGraph  Small Graph
	 * @return Match or not
	 */
	public State matchGraphPair(Graph targetGraph, Graph queryGraph) {
		State state = new State(targetGraph, queryGraph); // The state to store the result mapping

		matchRecursive(state, targetGraph, queryGraph);

		return state;
	}

	/**
	 * Recursively figure out if the target graph contains query graph
	 * 
	 * Equivalent of match_iter using recursion. Defines match_iter's pair
	 * 
	 * @param state       VF2 State
	 * @param targetGraph Big Graph
	 * @param queryGraph  Small Graph
	 * @return Match or not
	 */
	private boolean matchRecursive(State state, Graph targetGraph, Graph queryGraph) {

		if (state.depth == queryGraph.nodes.size()) { // Found a match
			state.matched = true;
			return true;
		} else { // Extend the state
			ArrayList<Pair<Integer, Integer>> candidatePairs = genCandidatePairs(state, targetGraph, queryGraph);
			for (Pair<Integer, Integer> entry : candidatePairs) {
				if (checkFeasibility(state, entry.getKey(), entry.getValue())) {
					state.extendMatch(entry.getKey(), entry.getValue()); // extend mapping
					if (matchRecursive(state, targetGraph, queryGraph)) { // Found a match
						return true;
					}
					state.backtrack(entry.getKey(), entry.getValue()); // remove the match added before
				}
			}
		}
		return false;
	}

	/**
	 * Generate all candidate pairs given current state
	 * 
	 * @param state       VF2 State
	 * @param targetGraph Big Graph
	 * @param queryGraph  Small Graph
	 * @return Candidate Pairs
	 */
	private ArrayList<Pair<Integer, Integer>> genCandidatePairs(State state, Graph targetGraph, Graph queryGraph) {
		ArrayList<Pair<Integer, Integer>> pairList = new ArrayList<Pair<Integer, Integer>>();

		if (!state.T1out.isEmpty() && !state.T2out.isEmpty()){
			// Generate candidates from T1out and T2out if they are not empty
			
			// Faster Version
			// Since every node should be matched in query graph
			// Therefore we can only extend one node of query graph (with biggest id)
			// instead of generate the whole Cartesian product of the target and query 
			int queryNodeIndex = -1;
			for (int i : state.T2out) {
				queryNodeIndex = Math.max(i, queryNodeIndex);
			}
			for (int i : state.T1out) {
				pairList.add(new Pair<Integer,Integer>(i, queryNodeIndex));
			}
			
			return pairList;
		} else if (!state.T1in.isEmpty() && !state.T2in.isEmpty()){
			// Generate candidates from T1in and T2in if they are not empty
			
			// Faster Version
			// Since every node should be matched in query graph
			// Therefore we can only extend one node of query graph (with biggest id)
			// instead of generate the whole Cartesian product of the target and query 
			int queryNodeIndex = -1;
			for (int i : state.T2in) {
				queryNodeIndex = Math.max(i, queryNodeIndex);
			}
			for (int i : state.T1in) {
				pairList.add(new Pair<Integer,Integer>(i, queryNodeIndex));
			}
			
			return pairList;
		} else {
			// Generate from all unmapped nodes
			
			// Faster Version
			// Since every node should be matched in query graph
			// Therefore we can only extend one node of query graph (with biggest id)
			// instead of generate the whole Cartesian product of the target and query 
			int queryNodeIndex = -1;
			for (int i : state.unmapped2) {
				queryNodeIndex = Math.max(i, queryNodeIndex);
			}
			for (int i : state.unmapped1) {
				pairList.add(new Pair<Integer,Integer>(i, queryNodeIndex));
			}
			
			return pairList;
		}
	}

	/**
	 * Verify the (syntactic and semantic) feasibility of adding this match based on
	 * the formulas below
	 * 
	 * F(s,n,m) = F_sync(s,n,m) ∧ F_sem(s,n,m)
	 * 
	 * F_sync(s,n,m) = R_pred ∧ R_succ ∧ R_in ∧ R_out ∧ R_new F_sem(s,n,m)
	 * ⇔ n ≈ m
	 * 
	 * Node Label Rule ≈ F_sem
	 * 
	 * @param state           VF2 State
	 * @param targetNodeIndex Target Graph Node Index
	 * @param queryNodeIndex  Query Graph Node Index
	 * @return Feasible or not
	 */
	private Boolean checkFeasibility(State state, int targetNodeIndex, int queryNodeIndex) {
		// The two nodes must have the same label
		if (!state.targetGraph.nodes.get(targetNodeIndex).label
				.equals(state.queryGraph.nodes.get(queryNodeIndex).label)) {
			return false;
		}

		// Predecessor Rule and Successor Rule
		if (!checkPredAndSucc(state, targetNodeIndex, queryNodeIndex)) {
			return false;
		}

		// In Rule and Out Rule
		if (!checkInAndOut(state, targetNodeIndex, queryNodeIndex)) {
			return false;
		}

		// New Rule
		if (!checkNew(state, targetNodeIndex, queryNodeIndex)) {
			return false;
		}

		// TODO: Adding support for checking constraints on attributes
//		try {
//			ScriptEngine js = Utils.js; 
//			Object value, constraint;
//			String script = constraint.toString().replaceAll("getAttr\\(\\)", "\"" + (value == null ? "" : value).toString() + "\"");
//			js.eval(script);
//			if (js.get("result").toString().equals("false")) {
//				return false;
//            }
//			
//		} catch (ScriptException e) {
//            e.printStackTrace();
//            return false;
//		}

		return true;
	}

	/**
	 * Check the predecessor rule and successor rule It ensures the consistency of
	 * the partial matching
	 * 
	 * @param state           VF2 State
	 * @param targetNodeIndex Target Graph Node Index
	 * @param queryNodeIndex  Query Graph Node Index
	 * @return Feasible or not
	 */
	private Boolean checkPredAndSucc(State state, int targetNodeIndex, int queryNodeIndex) {

		Node targetNode = state.targetGraph.nodes.get(targetNodeIndex);
		Node queryNode = state.queryGraph.nodes.get(queryNodeIndex);
		String[][] targetAdjacency = state.targetGraph.getAdjacencyMatrix();
		String[][] queryAdjacency = state.queryGraph.getAdjacencyMatrix();

		// Predecessor Rule
		// For all mapped predecessors of the query node,
		// there must exist corresponding predecessors of target node.
		// Vice Versa
		for (Edge e : targetNode.inEdges) {
			if (state.core_1[e.source.id] > -1) {
				if (queryAdjacency[state.core_1[e.source.id]][queryNodeIndex] == null) {
					return false; // not such edge in target graph
				} else if (!queryAdjacency[state.core_1[e.source.id]][queryNodeIndex].equals(e.label)) {
					return false; // label doesn't match
				}
			}
		}

		for (Edge e : queryNode.inEdges) {
			if (state.core_2[e.source.id] > -1) {
				if (targetAdjacency[state.core_2[e.source.id]][targetNodeIndex] == null) {
					return false; // not such edge in target graph
				} else if (!targetAdjacency[state.core_2[e.source.id]][targetNodeIndex].equals(e.label)) {
					return false; // label doesn't match
				}
			}
		}

		// Successor Rule
		// For all mapped successors of the query node,
		// there must exist corresponding successors of the target node
		// Vice Versa
		for (Edge e : targetNode.outEdges) {
			if (state.core_1[e.target.id] > -1) {
				if (queryAdjacency[queryNodeIndex][state.core_1[e.target.id]] == null) {
					return false; // no such edge in target graph
				} else if (!queryAdjacency[queryNodeIndex][state.core_1[e.target.id]].equals(e.label)) {
					return false; // label doesn't match
				}
			}
		}

		for (Edge e : queryNode.outEdges) {
			if (state.core_2[e.target.id] > -1) {
				if (targetAdjacency[targetNodeIndex][state.core_2[e.target.id]] == null) {
					return false; // no such edge in target graph
				} else if (!targetAdjacency[targetNodeIndex][state.core_2[e.target.id]].equals(e.label)) {
					return false; // label doesn't match
				}
			}
		}

		return true;
	}

	/**
	 * Check the in rule and out rule This prunes the search tree using 1-look-ahead
	 * 
	 * @param state           VF2 State
	 * @param targetNodeIndex Target Graph Node Index
	 * @param queryNodeIndex  Query Graph Node Index
	 * @return Feasible or not
	 */
	private boolean checkInAndOut(State state, int targetNodeIndex, int queryNodeIndex) {

		Node targetNode = state.targetGraph.nodes.get(targetNodeIndex);
		Node queryNode = state.queryGraph.nodes.get(queryNodeIndex);

		int targetPredCnt = 0, targetSucCnt = 0;
		int queryPredCnt = 0, querySucCnt = 0;

		// In Rule
		// The number predecessors/successors of the target node that are in T1in
		// must be larger than or equal to those of the query node that are in T2in
		for (Edge e : targetNode.inEdges) {
			if (state.inT1in(e.source.id)) {
				targetPredCnt++;
			}
		}
		for (Edge e : targetNode.outEdges) {
			if (state.inT1in(e.target.id)) {
				targetSucCnt++;
			}
		}
		for (Edge e : queryNode.inEdges) {
			if (state.inT2in(e.source.id)) {
				queryPredCnt++;
			}
		}
		for (Edge e : queryNode.outEdges) {
			if (state.inT2in(e.target.id)) {
				queryPredCnt++;
			}
		}
		if (targetPredCnt < queryPredCnt || targetSucCnt < querySucCnt) {
			return false;
		}

		// Out Rule
		// The number predecessors/successors of the target node that are in T1out
		// must be larger than or equal to those of the query node that are in T2out
		for (Edge e : targetNode.inEdges) {
			if (state.inT1out(e.source.id)) {
				targetPredCnt++;
			}
		}
		for (Edge e : targetNode.outEdges) {
			if (state.inT1out(e.target.id)) {
				targetSucCnt++;
			}
		}
		for (Edge e : queryNode.inEdges) {
			if (state.inT2out(e.source.id)) {
				queryPredCnt++;
			}
		}
		for (Edge e : queryNode.outEdges) {
			if (state.inT2out(e.target.id)) {
				queryPredCnt++;
			}
		}
		if (targetPredCnt < queryPredCnt || targetSucCnt < querySucCnt) {
			return false;
		}

		return true;
	}

	/**
	 * Check the new rule
	 * This prunes the search tree using 2-look-ahead
	 * 
	 * @param state           VF2 State
	 * @param targetNodeIndex Target Graph Node Index
	 * @param queryNodeIndex  Query Graph Node Index
	 * @return Feasible or not
	 */
	private boolean checkNew(State state, int targetNodeIndex, int queryNodeIndex) {

		Node targetNode = state.targetGraph.nodes.get(targetNodeIndex);
		Node queryNode = state.queryGraph.nodes.get(queryNodeIndex);

		int targetPredCnt = 0, targetSucCnt = 0;
		int queryPredCnt = 0, querySucCnt = 0;

		// In Rule
		// The number predecessors/successors of the target node that are in T1in
		// must be larger than or equal to those of the query node that are in T2in
		for (Edge e : targetNode.inEdges) {
			if (state.inN1Tilde(e.source.id)) {
				targetPredCnt++;
			}
		}
		for (Edge e : targetNode.outEdges) {
			if (state.inN1Tilde(e.target.id)) {
				targetSucCnt++;
			}
		}
		for (Edge e : queryNode.inEdges) {
			if (state.inN2Tilde(e.source.id)) {
				queryPredCnt++;
			}
		}
		for (Edge e : queryNode.outEdges) {
			if (state.inN2Tilde(e.target.id)) {
				querySucCnt++;
			}
		}
		if (targetPredCnt < queryPredCnt || targetSucCnt < querySucCnt) {
			return false;
		}

		return true;
	}

	/**
	 * Performs matching on labels, while excluding matches on NACs 
	 * TODO: In a future iteration, implement matching on types and constraints
	 * 
	 * @return Set of label matches
	 */
	@Override
	public ArrayList<Match> match() {
		Graph patternGraph = lhs.getPreconditionPattern().getGraph();
		ArrayList<Pattern> nacs = (lhs.getNacs() == null) ? new ArrayList<>() : lhs.getNacs();
		ArrayList<Graph> nacGraphs = new ArrayList<>();
		for (Pattern nac : nacs) {
			nacGraphs.add(nac.getGraph());
		}

		ArrayList<Match> results = new ArrayList<>();

		State baseState = matchGraphPair(model.getGraph(), patternGraph);
		if (baseState.matched) {
			for (int nodeIndex : baseState.core_2) {
				Node matchedNode = model.getGraph().nodes.get(nodeIndex);
				if (matchedNode != null) {
					if (model.getObjectsByNode().containsKey(matchedNode)) {
						EObject matchedObject = model.getObjectsByNode().get(matchedNode);
						Match match = new Match();
						match.addMapping(matchedNode.label, matchedObject);
						results.add(match);
					}
				}
			}
		}

		ArrayList<Match> nacMatches = new ArrayList<>();
		for (Graph ng : nacGraphs) {
			State nacState = matchGraphPair(model.getGraph(), ng);
			if (nacState.matched) {
				for (int nodeIndex : nacState.core_2) {
					Node matchedNode = model.getGraph().nodes.get(nodeIndex);
					if (matchedNode != null) {
						if (model.getObjectsByNode().containsKey(matchedNode)) {
							EObject matchedObject = model.getObjectsByNode().get(matchedNode);
							Match match = new Match();
							match.addMapping(matchedNode.label, matchedObject);
							nacMatches.add(match);
						}
					}
				}
			}
		}

		// Filtering portion is the same as in SimpleMatch
		ArrayList<Match> toRemove = new ArrayList<>();
		for (Match m : results) {
			boolean keep = true;
			for (Match nMatch : nacMatches) {
				boolean identical = true;
				for (String label : nMatch.getLabelMappings().keySet()) {
					if (!m.getLabelMappings().keySet().contains(label))
						continue;
					identical = identical && m.getLabelMappings().get(label).equals(nMatch.getLabelMappings().get(label));
				}
				if (identical)
					keep = false;
			}
			if (!keep) {
				toRemove.add(m);
			}
		}
		results.removeAll(toRemove);

		System.out.println(results.toString());
		return results;
	}

	@Override
	public ArrayList<Match> match_iter(LHS lhs, int max, Model model) {
		// TODO: Add support for match_iter
		return null;
	}
}
