package rerouter;

import java.util.Vector;

import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import reroute_network.Edge;
import reroute_network.Flow;
import reroute_network.RerouteData;
import reroute_network.Vertex;

public class PathRerouter {
	public Vector<RerouteData> reroute(
			SimpleDirectedWeightedGraph<Vertex,Edge> graph,
			Vector<Flow> consideredFlows) {
		
		return new Vector<RerouteData>();
	}
	
	void setDraftUsedCapacities(
			SimpleDirectedWeightedGraph<Vertex,Edge> graph) {
		for (Edge edge : graph.edgeSet()) {
			edge.draftUsedCapacity = edge.getUsedCapacity();
		}
	}
	
	void addFlowDraft(Flow flow) {
		for (Edge e : flow.getPath().getEdgeList()) {
			e.draftUsedCapacity += flow.getDemand();
		}
	}
	
	void removeFlowDraft(Flow flow) {
		for (Edge e : flow.getPath().getEdgeList()) {
			e.draftUsedCapacity -= flow.getDemand();
		}
	}
}
