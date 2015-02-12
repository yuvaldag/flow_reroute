package reroute_network;

import java.util.Vector;

import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import reroute_network.Vertex;
import reroute_network.Edge;

class EdgeData {
	final int source;
	final int target;
	final int capacity;

	EdgeData(final int source, final int target, final int capacity) {
		this.source = source;
		this.target = target;
		this.capacity = capacity;
	}
}

/*
 * Used to gather data to create a graph.
 */
public class GraphCreator extends Object {
	final int numVertices;
	final Vector<EdgeData> edgeData;

	public GraphCreator(final int numVertices) {
		this.numVertices = numVertices;
		edgeData = new Vector<EdgeData>();
	}
	
	public void addEdge(
			final int source, final int target, final int capacity) {
		edgeData.add(new EdgeData(source, target, capacity));
	}
	
	/*
	 * Creates a graph.
	 * 
	 * @param vertices		An output parameter. It will contain all vertices,
	 * 							while vertex number i is in the i'th index,
	 * 							for all i.
	 * @return				The created graph
	 */
	public SimpleDirectedWeightedGraph<Vertex,Edge> getGraph(
			Vector<Vertex> vertices) {
		SimpleDirectedWeightedGraph<Vertex,Edge> graph =
				new SimpleDirectedWeightedGraph<Vertex,Edge>(Edge.class);
		
		vertices.clear();
		for(int i = 0; i < numVertices; i++) {
			Vertex vertex = new Vertex(i);
			vertices.add(vertex);
			if (!graph.addVertex(vertex))
				throw new UnexpectedInternalException("Vertex alread exsists");
		}
		
		for(EdgeData edata : edgeData) {
			Edge edge = new Edge(edata.capacity);
			graph.addEdge(
					vertices.get(edata.source),
					vertices.get(edata.target),
					edge);
		}
		
		return graph;
	}
}