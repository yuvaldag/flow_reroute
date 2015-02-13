package reroute_network;

import org.jgrapht.GraphPath;

public class RerouteData {
	public final Flow flow;
	public final GraphPath<Vertex,Edge> newPath;
	
	public RerouteData(Flow flow, GraphPath<Vertex,Edge> newPath) {
		this.flow = flow;
		this.newPath = newPath;
	}
}