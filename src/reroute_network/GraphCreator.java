package reroute_network;

import java.util.Vector;

import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import reroute_network.Vertex;
import reroute_network.Edge;

class GraphData {
	final SimpleDirectedWeightedGraph<Vertex, Edge> graph;
	final Vector<Vertex> vertices;
	
	GraphData(
			SimpleDirectedWeightedGraph<Vertex, Edge> graph,
			Vector<Vertex> vertices) {
		this.graph = graph;
		this.vertices = vertices;
	}
}

/*
 * Used to gather data to create a graph.
 */
public class GraphCreator extends Object {
	private final int numVertices;
	private final Vector<EdgeData> edgeData;

	public GraphCreator(
			final int numVertices, final Vector<EdgeData> edgeData) {
		this.numVertices = numVertices;
		this.edgeData = new Vector<EdgeData>(edgeData);
	}
	
	/*
	 * Creates a new graph based on the data and returns it.
	 * 
	 * @return				The created graph
	 */
	GraphData createGraph() {
		SimpleDirectedWeightedGraph<Vertex,Edge> graph =
				new SimpleDirectedWeightedGraph<Vertex,Edge>(Edge.class);
		Vector<Vertex> vertices = new Vector<Vertex>();

		for(int i = 0; i < numVertices; i++) {
			Vertex vertex = new Vertex(i);
			vertices.add(vertex);
			if (!graph.addVertex(vertex))
				throw new UnexpectedInternalException("Vertex alread exsists");
		}
		
		for(int i = 0; i < edgeData.size(); i++) {
			Edge edge = new Edge(edgeData.get(i).capacity, i);
			graph.addEdge(
					vertices.get(edgeData.get(i).source),
					vertices.get(edgeData.get(i).target),
					edge);
		}
		
		return new GraphData(graph, vertices);
	}
	
	public int getNumVertices() {
		return numVertices;
	}
	
	public String toString() {
		GraphData data = createGraph();
		return data.graph.toString();
	}
}