package reroute_network;

import java.util.Vector;

import org.jgrapht.graph.SimpleDirectedWeightedGraph;

class NetworkExampleException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6785283603715945230L;
}

abstract class NetworkExample {
	final SimpleDirectedWeightedGraph<Vertex,Edge> graph;
	final Vector<Edge> edges;
	NetworkExample() {
		graph = new SimpleDirectedWeightedGraph<Vertex,Edge>(Edge.class);
		edges = new Vector<Edge>();
	}
	
	static boolean edgeCompare(Edge e1, Edge e2) {
		return e1.usedCapacity == e2.usedCapacity;
	}
	
	void addEdge(Vertex source, Vertex target, Edge edge) {
		graph.addEdge(source, target, edge);
		edges.add(edge);
	}
	
	boolean equals(NetworkExample net) {
		for(int i = 0; i < edges.size(); i++) {
			if(!edgeCompare(edges.get(i), net.edges.get(i))) {
				return false;
			}
		}
		return true;
	}

	static String networkNonEqualsMsg(
			NetworkExample actual, NetworkExample expected) {
		return String.format(
				"Actual edges: %s, Expected edges: %s",
				actual.edges,
				expected.edges);
	}
}

class NetworkExample1 extends NetworkExample {
	// Creates the indirected graph:
	//
	// v1---5--v2
	//   \    /|
	//   11  7 |
	//     \/  |
	//     /\  3
	//    /  \ |
	//   /    \|
	// v4--2---v3
	
	final Vertex v1;
	final Vertex v2;
	final Vertex v3;
	final Vertex v4;

	final Edge e12;
	final Edge e21;
	final Edge e23;
	final Edge e32;
	final Edge e34;
	final Edge e43;
	final Edge e13;
	final Edge e31;
	final Edge e24;
	final Edge e42;

	NetworkExample1() {
		v1 = new Vertex(1);
		graph.addVertex(v1);
		v2 = new Vertex(2);
		graph.addVertex(v2);
		v3 = new Vertex(3);
		graph.addVertex(v3);
		v4 = new Vertex(4);
		graph.addVertex(v4);
		
		e12 = new Edge(5);
		addEdge(v1, v2, e12);
		e21 = new Edge(5);
		addEdge(v2, v1, e21);
		e23 = new Edge(3);
		addEdge(v2, v3, e23);
		e32 = new Edge(3);
		addEdge(v3, v2, e32);
		e34 = new Edge(2);
		addEdge(v3, v4, e34);
		e43 = new Edge(2);
		addEdge(v4, v3, e43);
		e13 = new Edge(11);
		addEdge(v1, v3, e13);
		e31 = new Edge(11);
		addEdge(v3, v1, e31);
		e24 = new Edge(7);
		addEdge(v2, v4, e24);
		e42 = new Edge(7);
		addEdge(v4, v2, e42);
	}
}