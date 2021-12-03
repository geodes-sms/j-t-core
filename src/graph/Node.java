package graph;

import java.util.ArrayList;

/**
 * Class defining nodes
 * 
 * @author sebastien.ehouan
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
	public Node(Graph g, int id, String label) {
		this.graph = g;
		this.id = id;
		this.label = label;
	}
	
	/**
	 * Overridden equals method
	 * Returns true if id and label matches, false otherwise
	 */
	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
        if (other == null || this.getClass() != other.getClass()) return false;
        
        Node otherNode = (Node) other;
		
		if (this.id != otherNode.id) {
			return false;
		}
		if (this.label.equals(otherNode.label)) {
			return false;
		}
		
		return true;
	}
}
