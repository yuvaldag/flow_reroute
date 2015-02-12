package reroute_network;

import org.jgrapht.graph.DefaultWeightedEdge;

// TODO: Refactor this class to provide public access to some fields
public class Edge extends DefaultWeightedEdge {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7897313466827461675L;
	final int capacity;
	int usedCapacity;
	
	public Edge(int edgeCapacity) {
		capacity = edgeCapacity;
		usedCapacity = 0;
	}
	
	public String toString() {
		return String.format("(%d/%d)", usedCapacity, capacity);
	}
}