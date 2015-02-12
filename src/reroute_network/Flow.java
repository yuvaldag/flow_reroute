package reroute_network;

import org.jgrapht.GraphPath;

public class Flow {
	GraphPath<Vertex, Edge> path;
	final int demand;

	Flow(GraphPath<Vertex,Edge> flowPath, int flowDemand) {
		path = flowPath;
		demand = flowDemand;
	}
	
	public int getDemand() {
		return demand;
	}
	
	public GraphPath<Vertex,Edge> getPath() {
		return path;
	}
}