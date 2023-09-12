package tcore;

import graph.Edge;
import graph.Graph;
import graph.Node;

import java.io.PrintWriter;
import java.util.HashSet;

/**
 * Class implementing the concept of states for the VF2 algorithm
 * 
 * VF2 REFERENCE -- YET TO FINALIZE -- DO NOT USE
 *
 * @author Sebastien Ehouan
 * @since 2020-04-25
 */

public class State {
	@SuppressWarnings("javadoc")
	public int[] core_1;
	@SuppressWarnings("javadoc")
	public int[] core_2;

	@SuppressWarnings("javadoc")
	public int[] in_1;
	@SuppressWarnings("javadoc")
	public int[] in_2;
	@SuppressWarnings("javadoc")
	public int[] out_1;
	@SuppressWarnings("javadoc")
	public int[] out_2;

	@SuppressWarnings("javadoc")
	public HashSet<Integer> T1in;
	@SuppressWarnings("javadoc")
	public HashSet<Integer> T1out;
	@SuppressWarnings("javadoc")
	public HashSet<Integer> T2in;
	@SuppressWarnings("javadoc")
	public HashSet<Integer> T2out;

	/**
	 * Unmapped nodes in target graph.
	 */
	public HashSet<Integer> unmapped1;

	/**
	 * Unmapped nodes in query graph.
	 */
	public HashSet<Integer> unmapped2;

	/**
	 * Current depth of the search tree.
	 */
	public int depth = 0;

	@SuppressWarnings("javadoc")
	public boolean matched = false;

	/**
	 * 
	 */
	public Graph targetGraph;
	/**
	 * 
	 */
	public Graph queryGraph;

	/**
	 * Initialize a State
	 * 
	 * @param targetGraph The big graph
	 * @param queryGraph  The small graph
	 */
	public State(Graph targetGraph, Graph queryGraph) {

		this.targetGraph = targetGraph;
		this.queryGraph = queryGraph;

		int targetSize = targetGraph.nodes.size();
		int querySize = queryGraph.nodes.size();

		T1in = new HashSet<Integer>(targetSize * 2);
		T1out = new HashSet<Integer>(targetSize * 2);
		T2in = new HashSet<Integer>(querySize * 2);
		T2out = new HashSet<Integer>(querySize * 2);

		unmapped1 = new HashSet<Integer>(targetSize * 2);
		unmapped2 = new HashSet<Integer>(querySize * 2);

		core_1 = new int[targetSize];
		core_2 = new int[querySize];

		in_1 = new int[targetSize];
		in_2 = new int[querySize];
		out_1 = new int[targetSize];
		out_2 = new int[querySize];

		// initialize values ("-1" means no mapping / not contained in the set)
		// initially, all sets are empty and no nodes are mapped
		for (int i = 0; i < targetSize; i++) {
			core_1[i] = -1;
			in_1[i] = -1;
			out_1[i] = -1;
			unmapped1.add(i);
		}
		for (int i = 0; i < querySize; i++) {
			core_2[i] = -1;
			in_2[i] = -1;
			out_2[i] = -1;
			unmapped2.add(i);
		}
	}

	/**
	 * @param nodeId
	 * @return
	 */
	public Boolean inM1(int nodeId) {
		return (core_1[nodeId] > -1);
	}

	/**
	 * @param nodeId
	 * @return
	 */
	public Boolean inM2(int nodeId) {
		return (core_2[nodeId] > -1);
	}

	/**
	 * @param nodeId
	 * @return
	 */
	public Boolean inT1in(int nodeId) {
		return ((core_1[nodeId] == -1) && (in_1[nodeId] > -1));
	}

	/**
	 * @param nodeId
	 * @return
	 */
	public Boolean inT2in(int nodeId) {
		return ((core_2[nodeId] == -1) && (in_2[nodeId] > -1));
	}

	/**
	 * @param nodeId
	 * @return
	 */
	public Boolean inT1out(int nodeId) {
		return ((core_1[nodeId] == -1) && (out_1[nodeId] > -1));
	}

	/**
	 * @param nodeId
	 * @return
	 */
	public Boolean inT2out(int nodeId) {
		return ((core_2[nodeId] == -1) && (out_2[nodeId] > -1));
	}

	/**
	 * @param nodeId
	 * @return
	 */
	public Boolean inT1(int nodeId) {
		return (this.inT1in(nodeId) || this.inT1out(nodeId));
	}

	/**
	 * @param nodeId
	 * @return
	 */
	public Boolean inT2(int nodeId) {
		return (this.inT2in(nodeId) || this.inT2out(nodeId));
	}

	/**
	 * @param nodeId
	 * @return
	 */
	public Boolean inN1Tilde(int nodeId) {
		return ((core_1[nodeId] == -1) && (in_1[nodeId] == -1) && (out_1[nodeId] == -1));
	}

	/**
	 * @param nodeId
	 * @return
	 */
	public Boolean inN2Tilde(int nodeId) {
		return ((core_2[nodeId] == -1) && (in_2[nodeId] == -1) && (out_2[nodeId] == -1));
	}

	/**
	 * Add a new match (targetIndex, queryIndex) to the state
	 * 
	 * @param targetIndex Index of the node in target graph
	 * @param queryIndex  Index of the node in query graph
	 */
	public void extendMatch(int targetIndex, int queryIndex) {

		core_1[targetIndex] = queryIndex;
		core_2[queryIndex] = targetIndex;
		unmapped1.remove(targetIndex);
		unmapped2.remove(queryIndex);
		T1in.remove(targetIndex);
		T1out.remove(targetIndex);
		T2in.remove(queryIndex);
		T2out.remove(queryIndex);

		depth++; // move down one level in the search tree

		Node targetNode = targetGraph.nodes.get(targetIndex);
		Node queryNode = queryGraph.nodes.get(queryIndex);

		for (Edge e : targetNode.inEdges) {
			if (in_1[e.source.id] == -1) { // if the node is not in T1in or mapping
				in_1[e.source.id] = depth;
				if (!inM1(e.source.id)) // if not in M1, add into T1in
					T1in.add(e.source.id);
			}
		}

		for (Edge e : targetNode.outEdges) {
			if (out_1[e.target.id] == -1) { // if the node is not in T1out or mapping
				out_1[e.target.id] = depth;
				if (!inM1(e.target.id)) // if not in M1, add into T1out
					T1out.add(e.target.id);
			}
		}

		for (Edge e : queryNode.inEdges) {
			if (in_2[e.source.id] == -1) { // if the node is not in T2in or mapping
				in_2[e.source.id] = depth;
				if (!inM2(e.source.id)) // if not in M1, add into T2in
					T2in.add(e.source.id);
			}
		}

		for (Edge e : queryNode.outEdges) {
			if (out_2[e.target.id] == -1) { // if the node is not in T2out or mapping
				out_2[e.target.id] = depth;
				if (!inM2(e.target.id)) // if not in M1, add into T2out
					T2out.add(e.target.id);
			}
		}

	}

	/**
	 * Remove the match of (targetNodeIndex, queryNodeIndex) for backtrack
	 * 
	 * @param targetNodeIndex Index of the node in target graph.
	 * @param queryNodeIndex  Index of the node in query graph.
	 */
	public void backtrack(int targetNodeIndex, int queryNodeIndex) {

		core_1[targetNodeIndex] = -1;
		core_2[queryNodeIndex] = -1;
		unmapped1.add(targetNodeIndex);
		unmapped2.add(queryNodeIndex);

		for (int i = 0; i < core_1.length; i++) {
			if (in_1[i] == depth) {
				in_1[i] = -1;
				T1in.remove(i);
			}
			if (out_1[i] == depth) {
				out_1[i] = -1;
				T1out.remove(i);
			}
		}
		for (int i = 0; i < core_2.length; i++) {
			if (in_2[i] == depth) {
				in_2[i] = -1;
				T2in.remove(i);
			}
			if (out_2[i] == depth) {
				out_2[i] = -1;
				T2out.remove(i);
			}
		}

		// put targetNodeIndex and queryNodeIndex back into Tin and Tout sets if
		// necessary
		if (inT1in(targetNodeIndex))
			T1in.add(targetNodeIndex);
		if (inT1out(targetNodeIndex))
			T1out.add(targetNodeIndex);
		if (inT2in(queryNodeIndex))
			T2in.add(queryNodeIndex);
		if (inT2out(queryNodeIndex))
			T2out.add(queryNodeIndex);

		depth--;
	}

	/**
	 * Print the current mapping
	 */
	public void printMapping() {
		for (int i = 0; i < core_2.length; i++) {
			System.out.print("(" + core_2[i] + "-" + i + ") ");
		}
		System.out.println();
	}

	/**
	 * Write state to file
	 * 
	 * @param writer PrintWriter value.
	 */
	public void writeMapping(PrintWriter writer) {
		for (int i = 0; i < core_2.length; i++) {
			writer.write("(" + core_2[i] + "-" + i + ") ");
		}
		writer.write("\n");
	}
}
