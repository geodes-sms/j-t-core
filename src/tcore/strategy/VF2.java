package tcore.strategy;

import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import graph.Edge;
import graph.Graph;
import graph.Node;
import tcore.*;
import tcore.constant.JTCoreConstant;
import tcore.messages.Match;
import utils.Utils;

/**
 * VF2 Algorithm implementation based on (sub)graph isomorphism
 * 
 * YET TO BE FINALIZED -- DO NOT USE
 *
 * @author Sebastien Ehouan
 * @author An Li
 * @since 2021-12-19
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
	 * Evaluates if a value respects a given constraint on it
	 * 
	 * @param state           VF2 State
	 * @param targetNodeIndex Target Graph Node Index
	 * @param queryNodeIndex  Query Graph Node Index
	 * @param sign            Sign used in the constraint (<= | >= | < | > | == | !=)
	 * @param constraint 	  The whole constraint
	 * @param value			  Value of the model's attribute on which the constraint must be evaluated
	 * @return A boolean, true if the model's attribute value respect the sub-constraint set by the pattern, false otherwise
	 */
	public Boolean evaluateConstraint(State state, int targetNodeIndex, int queryNodeIndex, String sign, Object constraint, Object value){
        ScriptEngine js = Utils.js;
		String rhs = new String();
		
		//If the sign is <= | >= | == | != : 

		if (sign.length() == 2) {
			rhs = constraint.toString().substring(constraint.toString().indexOf(sign)+2);	
		}
		//If the sign is > | < : 
		else {
			rhs = constraint.toString().substring(constraint.toString().indexOf(sign)+1);	
		}
		//If the constraint involves a .length statement
		if (constraint.toString().contains(".length")) {
			value = "\"" + value+ "\"" +".length" ;
		}

		//We create the script that will be evaluated with the value of the model's attribute and the constraint.
		String script = value + sign + rhs;
		if (sign.matches(">|<|==|>=|<=|!=")) {
			try {
				return (Boolean) js.eval(script);
			}	
			catch (ScriptException e) {
				e.printStackTrace();
			}
		}
		return false;
	}


	/**
	 * Checks, for a tested target node, if the constraints from the query node on its attributes' values are respected or not
	 * 
	 * @param state           VF2 State
	 * @param targetNodeIndex Target Graph Node Index
	 * @param queryNodeIndex  Query Graph Node Index
	 * @return A boolean, true if the model's attributes values respect the pattern's constraint, false otherwise
	 */
	public Boolean checkConstraint (State state, int targetNodeIndex, int queryNodeIndex) {
        ScriptEngine js = Utils.js;

		Object constraints = state.queryGraph.nodes.get(queryNodeIndex).attributes.values().iterator().next().get("ram_constraint");
		ArrayList<String> constraintsList = new ArrayList<String>();
		ArrayList<String> operatorList = new ArrayList<String>();
		String sign = new String();
		Object valueModel = new Object();
		ArrayList<String> res = new ArrayList<String>();

		//If the ram_constraint attribute is not null or empty
		if (constraints != null && !constraints.equals("")){
			
			//We parse each constraint between the || and && operators, if there are any
			//We create two lists : operatorList with the operators, and constraintsList a list with all the constraints to be evaluated
		
			while (constraints.toString().contains("||") || constraints.toString().contains("&&")) {
				if (constraints.toString().contains("||")) {
					if (constraints.toString().contains("&&")) {
						if (constraints.toString().indexOf("||") < constraints.toString().indexOf("&&")){
							operatorList.add("||");
							constraintsList.add(constraints.toString().substring(0, constraints.toString().indexOf("||")));
							constraints = constraints.toString().substring(constraints.toString().indexOf("||")+2);
						}
						else {
							operatorList.add("&&");
							constraintsList.add(constraints.toString().substring(0, constraints.toString().indexOf("&&")));
							constraints = constraints.toString().substring(constraints.toString().indexOf("&&")+2);
						}
					}
					else {
						operatorList.add("||");
						constraintsList.add(constraints.toString().substring(0, constraints.toString().indexOf("||")));
						constraints = constraints.toString().substring(constraints.toString().indexOf("||")+2);
					}
				}
				else {
					operatorList.add("&&");
					constraintsList.add(constraints.toString().substring(0, constraints.toString().indexOf("&&")));
					constraints = constraints.toString().substring(constraints.toString().indexOf("&&")+2);
				}
			}
			
			constraintsList.add(constraints.toString());
			
			/*
			 * For each constraint we parse to get : the sign, (optional) the function used,
			 * the value of the attribute in the model that should be evaluated
			 */			
			
			System.out.println("Liste des contraintes : "+constraintsList);
			for (String cons : constraintsList) {
				cons = cons.strip();

				if (cons.contains(">=")){
					sign = cons.substring(cons.indexOf(">"), cons.indexOf("=")+1);
					if (cons.contains(".")){
							valueModel = state.targetGraph.nodes.get(targetNodeIndex).attributes.values().iterator().next().get(cons.substring(0, cons.indexOf(".")));
					}
					else {
						valueModel = state.targetGraph.nodes.get(targetNodeIndex).attributes.values().iterator().next().get(cons.substring(0, cons.indexOf(">")));
					}
				}

				else if (cons.contains("<=")){
					sign = cons.substring(cons.indexOf("<"), cons.indexOf("=")+1);
					if (cons.contains(".")){
						valueModel = state.targetGraph.nodes.get(targetNodeIndex).attributes.values().iterator().next().get(cons.substring(0, cons.indexOf(".")));
					}
					else {
					valueModel = state.targetGraph.nodes.get(targetNodeIndex).attributes.values().iterator().next().get(cons.substring(0, cons.indexOf("<")));
					}
				}

				else if (cons.contains(">") && (!cons.substring(cons.indexOf(">")+1).contains("="))){
					sign = cons.substring(cons.indexOf(">"), cons.indexOf(">")+1);
					if (cons.contains(".")){
						valueModel = state.targetGraph.nodes.get(targetNodeIndex).attributes.values().iterator().next().get(cons.substring(0, cons.indexOf(".")));
					}
					else {
						valueModel = state.targetGraph.nodes.get(targetNodeIndex).attributes.values().iterator().next().get(cons.substring(0, cons.indexOf(">")));
					}
				}

				else if (cons.contains("<") && (!cons.substring(cons.indexOf("<")+1).contains("="))){
					sign = cons.substring(cons.indexOf("<"), cons.indexOf("<")+1);
					if (cons.contains(".")){
						valueModel = state.targetGraph.nodes.get(targetNodeIndex).attributes.values().iterator().next().get(cons.substring(0, cons.indexOf(".")));
					}
					else {
						valueModel = state.targetGraph.nodes.get(targetNodeIndex).attributes.values().iterator().next().get(cons.substring(0, cons.indexOf("<")));
					}
				}

				else if (cons.contains("==")){
					sign = cons.substring(cons.indexOf("="), cons.indexOf("=")+2);
					if (cons.contains(".")){
						valueModel = state.targetGraph.nodes.get(targetNodeIndex).attributes.values().iterator().next().get(cons.substring(0, cons.indexOf(".")));
					}
					else {
						valueModel = state.targetGraph.nodes.get(targetNodeIndex).attributes.values().iterator().next().get(cons.substring(0, cons.indexOf("=")));	
					}
				}
				
				else if (cons.contains("!=")){
					sign = cons.substring(cons.indexOf("!"), cons.indexOf("=")+1);
					if (cons.contains(".")){
						valueModel = state.targetGraph.nodes.get(targetNodeIndex).attributes.values().iterator().next().get(cons.substring(0, cons.indexOf(".")));
					}
					else {
						valueModel = state.targetGraph.nodes.get(targetNodeIndex).attributes.values().iterator().next().get(cons.substring(0, cons.indexOf("!")));
					}
				}
								
				else {
					return false;
				}
				
				/*
				 * If the model's attribute value that has to be evaluated is null or empty we
				 * return false, otherwise we test the constraint on the given value and add the
				 * result to an array of booleans.
				 */		
				
				if(valueModel == null || valueModel.equals("")) {
					return false;
				}
				else {
					res.add(evaluateConstraint(state, targetNodeIndex, queryNodeIndex, sign, cons, valueModel).toString());	
				}
			}
			

			String checkingRes = res.get(0);

			for (int i = 0 ; i < res.size()-1; i++) {

				checkingRes += operatorList.get(i) + res.get(i+1) ;

			}
			
			// We evaluate the results of each constraint with the operators between each result. 
			// If the overall constraint (ram_constraint) is respected : true, false otherwise
			System.out.println("Script après évaluations séparées : " +checkingRes);
			try {
				System.out.println("Résultat évaluation : " +js.eval(checkingRes));
				return (Boolean) js.eval(checkingRes);
				}	
			catch (ScriptException e){
				e.printStackTrace();
				return false;
			}
		}
		else {
			return true;
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
		// We get the subclasses and abstract classes from the Metamodel
		HashMap<String, ArrayList<String>> subclasses = model.getMetamodel().getSubclasses();		
		ArrayList<String> abstracts = model.getMetamodel().Abstracts();
		
		// The two nodes must have the same label
		if (!state.targetGraph.nodes.get(targetNodeIndex).label
				.equals(state.queryGraph.nodes.get(queryNodeIndex).label)) {
			return false;
		}
		
		/*
		 * If a queryNode is not abstract according to the Metamodel, checks if there
		 * is a match on the className between the queryNode and the targetNode,
		 * otherwise it checks if the targetNode is a subclass of the queryNode
		 * according to the MetaModel. 
		 * If the queryNode is abstract, we only check if the targetNode is a subclass of 
		 * the queryNode according to the MetaModel.
		 */
		 		
		if (!abstracts.contains(state.queryGraph.nodes.get(queryNodeIndex).className)) {
			if (!state.targetGraph.nodes.get(targetNodeIndex).className.equals(state.queryGraph.nodes.get(queryNodeIndex).className) && 
					(!subclasses.values().iterator().next().contains(state.targetGraph.nodes.get(targetNodeIndex).className))) {
				return false;
			}
		}
		else {
			if (!subclasses.values().iterator().next().contains(state.targetGraph.nodes.get(targetNodeIndex).className)){
				return false;
			}
		}
		
		//We check the (optional) constraints applied by the queryNode on the targetNode
		if (!checkConstraint(state, targetNodeIndex, queryNodeIndex)) {
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
			Match match = new Match();
			for (int nodeIndex : baseState.core_2) {
				Node matchedNode = model.getGraph().nodes.get(nodeIndex);
				if (matchedNode != null) {
					EObject matchedObject = model.getObjectOfNode(matchedNode);
					if (matchedObject != null) {
						match.addMapping(matchedNode.label, matchedObject);
					}
				}
			}
			if (match.getLabelMappings().size() > 0) {
				results.add(match);
			}
		}

		ArrayList<Match> nacMatches = new ArrayList<>();
		for (Graph ng : nacGraphs) {
			State nacState = matchGraphPair(model.getGraph(), ng);
			if (nacState.matched) {
				Match match = new Match();
				for (int nodeIndex : nacState.core_2) {
					Node matchedNode = model.getGraph().nodes.get(nodeIndex);
					if (matchedNode != null) {
						EObject matchedObject = model.getObjectOfNode(matchedNode);
						if (matchedObject != null) {
							match.addMapping(matchedNode.label, matchedObject);
						}
					}
				}
				if (match.getLabelMappings().size() > 0) {
					nacMatches.add(match);
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
