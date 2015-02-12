package reroute_network;

import java.util.HashMap;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

class ExpWeightsRerouter extends PathRerouter {
	final double expParam;
	final int numReroutes;
	
	ExpWeightsRerouter(int numReroutes, double expParam) {
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
			
			if(edge.usedCapacity + demand > edge.capacity) {
				newWeight = Double.POSITIVE_INFINITY;
			} else {
				double costBeforeChange = costFunction(
						edge.usedCapacity, edge.capacity);
				double costAfterChange = costFunction(
						edge.usedCapacity + demand, edge.capacity);
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
	
	private void rerouteOne(
			SimpleDirectedWeightedGraph<Vertex,Edge> graph,
			HashMap<Integer,Flow> consideredFlows) {
		double bestImprovement = 0.0;
		Flow bestFlow = null;
		GraphPath<Vertex,Edge> bestPath = null;

		for(Flow flow : consideredFlows.values()) {
			RerouteNet.removeFlowFromNet(flow);

			setWeights(graph, flow.demand);
			
			double oldWeight = pathWeight(graph, flow.path);
			
			Vertex source = flow.path.getStartVertex();
			Vertex target = flow.path.getEndVertex();
			DijkstraShortestPath<Vertex,Edge> shortestPathObj = 
					new DijkstraShortestPath<Vertex,Edge>(
							graph, source, target);
			GraphPath<Vertex,Edge> shortestPath = shortestPathObj.getPath();
			
			double newWeight = shortestPath.getWeight();
			double improvement = newWeight - oldWeight;
			
			if(improvement < bestImprovement) {
				bestImprovement = improvement;
				bestFlow = flow;
				bestPath = shortestPath;
			}
			
			RerouteNet.addFlowToNet(flow);
		}
		
		// TODO: Check if greater than some epsilon
		// TODO: If the condition is false then we can give up trying to
		//		 	reroute more flows
		if(bestImprovement < 0.0) {
			RerouteNet.removeFlowFromNet(bestFlow);
			bestFlow.path = bestPath;
			RerouteNet.addFlowToNet(bestFlow);
		}
	}

	void reroute(
			SimpleDirectedWeightedGraph<Vertex,Edge> graph,
			HashMap<Integer,Flow> consideredFlows) {
		for(int i = 0; i < numReroutes; i++)
			rerouteOne(graph, consideredFlows);
	}
}