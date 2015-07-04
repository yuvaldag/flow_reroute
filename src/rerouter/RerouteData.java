package rerouter;

import org.jgrapht.GraphPath;

import reroute_network.Channel;
import reroute_network.Edge;
import reroute_network.Vertex;

public class RerouteData 
		implements Comparable<RerouteData>{
	public final Channel channel;
	public final GraphPath<Vertex,Edge> newPath;
	final double improvement;
	
	RerouteData(
			Channel channel, 
			GraphPath<Vertex,Edge> newPath,
			final double improvement) {
		this.channel = channel;
		this.newPath = newPath;
		this.improvement = improvement;
	}
	
	double getCongestionImprovement() {
		int newNumEdges = newPath.getEdgeList().size();
		int oldNumEdges = channel.getPath().getEdgeList().size(); 
		return (newNumEdges - oldNumEdges) * channel.getDemand();
	}
	
	public int compareTo(RerouteData rerouteData) {
		return Double.compare(improvement, rerouteData.improvement);
	}
}