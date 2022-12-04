package graph;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class defining graphs
 * 
 * @author sebastien.ehouan
 *
 * VF2 REFERENCE -- YET TO FINALIZE -- DO NOT USE
 */
public class Graph {
	/**
	 *  Name of the graph
	 */
	public String name;
	
	@SuppressWarnings("javadoc")
	public ArrayList<Node> nodes = new ArrayList<Node>();
	
	@SuppressWarnings("javadoc")
	public ArrayList<Edge> edges = new ArrayList<Edge>();
	
	private String[][] adjacencyMatrix; //stores graph structure as adjacency matrix (-1: not adjacent, >=0: the edge label)
	private boolean adjacencyMatrixUpdateNeeded = true; //indicates if the adjacency matrix needs an update
	
	/**
	 * @param name
	 */
	public Graph(String name) {
		this.name = name;
	}
	
	/**
	 * @param id
	 * @param label
	 */
	public void addNode(int id, String label, String className, HashMap<String, ArrayList<String>> subClasses) {
		nodes.add(new Node(this, id, label, className, subClasses));
		this.adjacencyMatrixUpdateNeeded = true;
	}
	
	/**
	 * @param node
	 */
	public void addNode(Node node) {
		nodes.add(node);
		this.adjacencyMatrixUpdateNeeded = true;
	}
	
	/**
	 * @param source
	 * @param target
	 * @param label
	 */
	public void addEdge(Node source, Node target, String label) {
		edges.add(new Edge(this, source, target, label));
		this.adjacencyMatrixUpdateNeeded = true;
	}
	
	/**
	 * @param sourceId
	 * @param targetId
	 * @param label
	 */
	public void addEdge(int sourceId, int targetId, String label) {
		this.addEdge(this.nodes.get(sourceId), this.nodes.get(targetId), label);
	}
	
	
	/**
	 * Get the adjacency matrix
	 * Reconstruct it if it needs an update
	 * @return Adjacency Matrix
	 */
	public String[][] getAdjacencyMatrix() {
		
		if (this.adjacencyMatrixUpdateNeeded) {
			
			int k = this.nodes.size();
			this.adjacencyMatrix = new String[k][k];	// node size may have changed
						
			for (Edge e : this.edges) {
				this.adjacencyMatrix[e.source.id][e.target.id] = e.label; // label must not be null
			}
			this.adjacencyMatrixUpdateNeeded = false;
		}
		return this.adjacencyMatrix;
	}
	
	/**
	 *  Prints adjacency matrix to console
	 */
	public void printGraph() {
		String[][] a = this.getAdjacencyMatrix();
		int k = a.length;
		
		System.out.print(this.name + " - Nodes: ");
		for (Node n : nodes) System.out.print(n.id + " ");
		System.out.println();
		for (int i = 0 ; i < k ; i++) {
			for (int j = 0 ; j < k ; j++) {
				System.out.print(a[i][j] + " ");
			}
			System.out.println();
		}
	}
}
