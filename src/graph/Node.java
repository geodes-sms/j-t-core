package graph;

import java.util.ArrayList;
import java.util.HashMap;

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
	 * Subclasses names associated to an EObject
	 */
	public HashMap<String, ArrayList<String>> subClasses;
		
	
	public HashMap<String, HashMap<String, Object>> attributes;
	
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
	public Node(Graph g, int id, String label, String className, HashMap<String, ArrayList<String>> subClasses, HashMap<String, HashMap<String, Object>> attributes) {
		this.graph = g;
		this.id = id;
		this.label = label;
		this.className = className;
		this.subClasses = subClasses;
		this.attributes = attributes;
	}
}
