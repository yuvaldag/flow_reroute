package rerouter;

import java.util.Vector;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import reroute_network.Edge;
import reroute_network.Flow;
import reroute_network.Vertex;

public abstract class ExpWeightsRerouter extends PathRerouter {	
	public ExpWeightsRerouter() throws PathRerouterException {
	}

	double expValue(Edge edge, int usedCapacity) {
		// TODO: make it faster. Maybe by calculating values of exp(-x)
		// 		 beforehand for different values of x.
		return Math.exp(
					-edgeExpParam(edge) * (edge.getCapacity() - usedCapacity) / 
					edge.getCapacity());
	}
	
	double edgeCostMultiplierDerrivative(Edge edge, int usedCapacity) {
		return expValue(edge, usedCapacity);
	}
	
	double costFunction(Edge edge, int usedCapacity) {		
		return edgeCostMultiplier(edge) * expValue(edge, usedCapacity);
	}
	
	private void setWeights(
			SimpleDirectedWeightedGraph<Vertex,Edge> graph, int demand,
			int[] draftUsedCapacities) {
		
		for(Edge edge : graph.edgeSet()) {
			int draftUsedCap = draftUsedCapacities[edge.getId()];
			double newWeight;
			
			if(draftUsedCap + demand > edge.getCapacity()) {
				newWeight = Double.POSITIVE_INFINITY;
			} else {
				double costBeforeChange = costFunction(
						edge, draftUsedCap);
				double costAfterChange = costFunction(
						edge, draftUsedCap + demand);
				newWeight = costAfterChange - costBeforeChange;
			}
			
			graph.setEdgeWeight(edge, newWeight);
		}
	}

	abstract double edgeCostMultiplier(Edge edge);

	abstract double edgeExpParam(Edge edge);

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
			Vector<Flow> consideredFlows,
			int[] draftUsedCapacities) {
		double bestImprovement = 0.0;
		RerouteData bestRerouteData = null;
		
		for(Flow flow : consideredFlows) {
			changeDraftUsedCapacities(
					flow.getPath(), -flow.getDemand(), draftUsedCapacities);

			setWeights(graph, flow.getDemand(), draftUsedCapacities);
			
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
			
			changeDraftUsedCapacities(
					flow.getPath(), flow.getDemand(), draftUsedCapacities);
		}
		
		// TODO: Return null if best improvement is smaller than some epsilon
		return bestRerouteData;
	}

	@Override
	public RerouteData reroute(
			SimpleDirectedWeightedGraph<Vertex,Edge> graph,
			Vector<Flow> consideredFlows) {
		
		int[] draftUsedCapacities = getDraftUsedCapacities(graph);

		return rerouteOne(graph, consideredFlows, draftUsedCapacities);
	}
}