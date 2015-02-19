package reroute_network;

import org.jgrapht.graph.DefaultWeightedEdge;

public class Edge extends DefaultWeightedEdge {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7897313466827461675L;
	final int capacity;
	int usedCapacity;
	private final int id;
	//public int draftUsedCapacity;
	
	
	Edge(int capacity, int id) {
		this.capacity = capacity;
		this.id = id;
		usedCapacity = 0;
	}
	
	public String toString() {
		return String.format("(%d/%d)", usedCapacity, capacity);
	}
	
	public int getCapacity() {
		return capacity;
	}
	
	public int getUsedCapacity() {
		return usedCapacity;
	}
	
	public int getId() {
		return id;
	}
}