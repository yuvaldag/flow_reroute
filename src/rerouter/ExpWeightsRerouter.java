package rerouter;

import java.util.Vector;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import reroute_network.Edge;
import reroute_network.Flow;
import reroute_network.RerouteData;
import reroute_network.Vertex;

public class ExpWeightsRerouter extends PathRerouter {
	final double expParam;
	final int numReroutes;
	
	public ExpWeightsRerouter(int numReroutes, double expParam) {
		this.numReroutes = numReroutes;
		this.expParam = expParam;
	}

	double costFunction(int usedCapacity, int totalCapacity) {
		return Math.exp(
				-expParam * (totalCapacity - usedCapacity) / totalCapacity);
	}
	
	private void setWeights(
			SimpleDirectedWeightedGraph<Vertex,Edge> graph, int demand) {
		for(Edge edge : graph.edgeSet()) {
			double newWeight;
			if(edge.draftUsedCapacity + demand > edge.getCapacity()) {
				newWeight = Double.POSITIVE_INFINITY;
			} else {
				double costBeforeChange = costFunction(
						edge.draftUsedCapacity, edge.getCapacity());
				double costAfterChange = costFunction(
						edge.draftUsedCapacity + demand, edge.getCapacity());
				newWeight = costAfterChange - costBeforeChange;
			}
			
			graph.setEdgeWeight(edge, newWeight);
		}
	}
	
	private double pathWeight(
			SimpleDirectedWeightedGraph<Vertex,Edge> graph,
			GraphPath<Vertex,Edge> path) {
		double sum = 0;
		for(Edge edge : path.getEdgeList()) {
			sum += graph.getEdgeWeight(edge);
		}
		
		return sum;
	}
	
	private RerouteData rerouteOne(
			SimpleDirectedWeightedGraph<Vertex,Edge> graph,
			Vector<Flow> consideredFlows) {
		double bestImprovement = 0.0;
		RerouteData bestRerouteData = null;
		
		for(Flow flow : consideredFlows) {
			changeUsedCapacityDraft(flow.getPath(), -flow.getDemand());

			setWeights(graph, flow.getDemand());
			
			double oldWeight = pathWeight(graph, flow.getPath());
			
			Vertex source = flow.getPath().getStartVertex();
			Vertex target = flow.getPath().getEndVertex();
			DijkstraShortestPath<Vertex,Edge> shortestPathObj = 
					new DijkstraShortestPath<Vertex,Edge>(
							graph, source, target);
			GraphPath<Vertex,Edge> shortestPath = shortestPathObj.getPath();
			
			double newWeight = shortestPath.getWeight();
			double improvement = newWeight - oldWeight;
			
			if(improvement < bestImprovement) {
				bestImprovement = improvement;
				bestRerouteData = new RerouteData(flow, shortestPath);
			}
			
			changeUsedCapacityDraft(flow.getPath(), flow.getDemand());
		}
		
		// TODO: Return null if best improvement is smaller than some epsilon
		return bestRerouteData;
	}

	public Vector<RerouteData> reroute(
			SimpleDirectedWeightedGraph<Vertex,Edge> graph,
			Vector<Flow> consideredFlows) {
		Vector<RerouteData> ret = new Vector<RerouteData>();
		
		setDraftUsedCapacities(graph);
		
		for(int i = 0; i < numReroutes; i++) {
			RerouteData newData = rerouteOne(graph, consideredFlows);
			if (newData == null)
				return ret;

			ret.add(newData);
			changeUsedCapacityDraft(
					newData.flow.getPath(), -newData.flow.getDemand());
			changeUsedCapacityDraft(newData.newPath, newData.flow.getDemand());
		}

		return ret;
	}
}