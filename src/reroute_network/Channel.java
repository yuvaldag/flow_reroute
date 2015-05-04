package reroute_network;

import org.jgrapht.GraphPath;

public class Channel {
	private GraphPath<Vertex, Edge> path;
	private int demand;
	private int numRerouted;
	
	Channel(GraphPath<Vertex, Edge> path) {
		this.path = path;
		this.demand = 0;
		this.numRerouted = 0;
	}

	void addDemand(int addedDemand) {
		this.demand += addedDemand;
	}

	void reduceDemand(int reducedDemand) {
		this.demand -= reducedDemand;
	}
	
	public int getDemand() {
		return demand;
	}
	
	public GraphPath<Vertex, Edge> getPath() {
		return path;
	}
	
	void reroute(GraphPath<Vertex, Edge> path) {
		this.path = path;
		this.numRerouted += 1;
	}
	
	public int getNumRerouted() {
		return numRerouted;
	}
}