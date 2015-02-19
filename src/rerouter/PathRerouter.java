package rerouter;

import java.util.Vector;

import org.jgrapht.GraphPath;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import reroute_network.Edge;
import reroute_network.Flow;
import reroute_network.Vertex;

public class PathRerouter {
	public Vector<RerouteData> reroute(
			SimpleDirectedWeightedGraph<Vertex,Edge> graph,
			Vector<Flow> consideredFlows,
			int numReroutes) {
		return new Vector<RerouteData>();
	}
	
	void setDraftUsedCapacities(
			SimpleDirectedWeightedGraph<Vertex,Edge> graph) {
		for (Edge edge : graph.edgeSet()) {
			edge.draftUsedCapacity = edge.getUsedCapacity();
		}
	}
	
	void changeUsedCapacityDraft(GraphPath<Vertex,Edge> path, int change) {
		for (Edge e : path.getEdgeList()) {
			e.draftUsedCapacity += change;
		}
	}
}
