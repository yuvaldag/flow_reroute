package rerouter;

import org.jgrapht.GraphPath;

import reroute_network.Edge;
import reroute_network.Flow;
import reroute_network.Vertex;

public class RerouteData {
	public final Flow flow;
	public final GraphPath<Vertex,Edge> newPath;
	
	RerouteData(Flow flow, GraphPath<Vertex,Edge> newPath) {
		this.flow = flow;
		this.newPath = newPath;
	}
}