package graph;

import java.util.ArrayList;

/**
 * Class defining nodes
 * 
 * @author sebastien.ehouan
 * @author An Li
 *
 * VF2 REFERENCE -- YET TO FINALIZE -- DO NOT USE
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
	 * Assumed to be unique
	 */
	public String label;
	
	/**
	 * Class name associated to EObject
	 */
	public String className;
	
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
	 * @param className
	 */
	public Node(Graph g, int id, String label, String className) {
		this.graph = g;
		this.id = id;
		this.label = label;
		this.className = className;
	}
}
