package graph;

import java.util.ArrayList;

/**
 * @author sebastien.ehouan
 *
 */
public class Node {
	/**
	 * Graph to which the node belongs
	 */
	public Graph graph;
		
	/**
	 * A unique id - running number
	 */
	public int id;
	
	/**
	 * For semantic feasibility checks
	 */
	public int label;
	
	/**
	 * Edges of which this node is the origin
	 */
	public ArrayList<Edge> outEdges = new ArrayList<Edge>();
	
	/**
	 * Edges of which this node is the destination
	 */
	public ArrayList<Edge> inEdges = new ArrayList<Edge>();
	
	/**
	 * @param g
	 * @param id
	 * @param label
	 */
	public Node(Graph g, int id, int label) {
		this.graph = g;
		this.id = id;
		this.label = label;
	}	
}
