package graph;

/**
 * Class initializing the edges
 * 
 * @author sebastien.ehouan
 *
 * VF2 REFERENCE -- YET TO FINALIZE -- DO NOT USE
 *
 */
public class Edge {
	/**
	 *  Graph to which the edge belongs
	 */
	public Graph graph;
	
	/**
	 *  Source/origin of the edge
	 */
	public Node source; 	
	
	/**
	 *  Target/destination of the edge 
	 */
	public Node target;
	
	/**
	 *  Label of the edge
	 */
	public String label;
	
	/**
	 * 
	 * Creates a new edge.
	 * 
	 * @param g
	 * @param source
	 * @param target
	 * @param label
	 */
	public Edge(Graph g, Node source, Node target, String label) {
		this.graph = g;
		this.source = source; //store source
		source.outEdges.add(this); //update edge list at source
		this.target = target; //store target
		target.inEdges.add(this); //update edge list at target
		this.label = label;
	}
}
