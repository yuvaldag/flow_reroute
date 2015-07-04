package reroute_network;

import org.jgrapht.graph.DefaultWeightedEdge;

public class Edge extends DefaultWeightedEdge
		implements Comparable<Edge> {
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
	
	public int compareTo(Edge edge) {
		double thisAvailability = 1 - (double)usedCapacity / capacity;
		double otherAvailability = 
				1 - (double)edge.usedCapacity / edge.capacity;
		
		return (int)Math.signum(thisAvailability - otherAvailability);
	}
}