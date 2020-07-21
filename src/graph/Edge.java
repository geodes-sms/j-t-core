package graph;

public class Edge {
	public Graph graph; 	//graph to which the edge belongs
		
		public Node source; 	//source/origin of the edge
		public Node target; 	//target/destination of the edge 
		public int label; 	//label of the edge
		
		// creates new edge
		public Edge(Graph g, Node source, Node target, int label) {
			this.graph = g;
			this.source = source; //store source
			source.outEdges.add(this); //update edge list at source
			this.target = target; //store target
			target.inEdges.add(this); //update edge list at target
			this.label = label;
		}
}
