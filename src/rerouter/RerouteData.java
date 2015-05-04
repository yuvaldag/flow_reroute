package rerouter;

import org.jgrapht.GraphPath;

import reroute_network.Channel;
import reroute_network.Edge;
import reroute_network.Vertex;

public class RerouteData {
	public final Channel channel;
	public final GraphPath<Vertex,Edge> newPath;
	
	RerouteData(Channel channel, GraphPath<Vertex,Edge> newPath) {
		this.channel = channel;
		this.newPath = newPath;
	}
}