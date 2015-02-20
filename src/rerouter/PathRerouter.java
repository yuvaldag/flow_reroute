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
	
	protected int[] getDraftUsedCapacities(
			SimpleDirectedWeightedGraph<Vertex,Edge> graph) {
		int numEdges = graph.edgeSet().size();
		int[] draftUsedCapacities = new int[numEdges];
		for (Edge edge : graph.edgeSet()) {
			draftUsedCapacities[edge.getId()] = edge.getUsedCapacity();
		}
		
		return draftUsedCapacities;
	}
	
	protected void changeDraftUsedCapacities(
			GraphPath<Vertex,Edge> path,
			int change,
			int[] draftUsedCapacities) {
		for (Edge e : path.getEdgeList()) {
			draftUsedCapacities[e.getId()] += change;
		}
	}
	
	public void newFlow( 
			GraphPath<Vertex, Edge> path, 
			int demand,
			boolean addedSuccessfully) {
	}
}
