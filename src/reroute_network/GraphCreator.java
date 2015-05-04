package reroute_network;

import java.util.Vector;

import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import reroute_network.Vertex;
import reroute_network.Edge;

class GraphData {
	final SimpleDirectedWeightedGraph<Vertex, Edge> graph;
	final Vector<Vertex> generatingVertices;
	GraphData(
			SimpleDirectedWeightedGraph<Vertex, Edge> graph,
			Vector<Vertex> vertices) {
		this.graph = graph;
		this.generatingVertices = vertices;
	}
}

/*
 * Used to gather data to create a graph.
 */
public class GraphCreator extends Object {
	private final int numVertices;
	private final Vector<EdgeData> edgeData;
	private final int genVerticesVsOther;
	
	public GraphCreator(
			final int numVertices, 
			final Vector<EdgeData> edgeData,
			final int genVerticesVsOther) {
		this.numVertices = numVertices;
		this.edgeData = new Vector<EdgeData>(edgeData);
		this.genVerticesVsOther = genVerticesVsOther;
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
		
		Vector<Vertex> generatingVertices = new Vector<Vertex>();
		
		for (int i = 0; i < vertices.size(); i += genVerticesVsOther) {
			generatingVertices.addElement(vertices.get(i));
		}

		return new GraphData(graph, generatingVertices);
	}
	
	public int getNumGenVertices() {
		int numGenVertices = numVertices / genVerticesVsOther;
		
		if (numVertices % genVerticesVsOther > 0) {
			numGenVertices += 1;
		}
		
		return numGenVertices;
	}
	
	public int getNumEdges() {
		return edgeData.size();
	}
	
	public String toString() {
		GraphData data = createGraph();
		return data.graph.toString();
	}
}