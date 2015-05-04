package rerouter;

import java.util.HashSet;
import java.util.Vector;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import reroute_network.Channel;
import reroute_network.Edge;
import reroute_network.Vertex;

public abstract class ExpWeightsRerouter extends PathRerouter {	
	private final Policy policy;
	
	public ExpWeightsRerouter(Policy policy) throws PathRerouterException {
		this.policy = policy;
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
			SimpleDirectedWeightedGraph<Vertex,Edge> graph, 
			GraphPath<Vertex, Edge> oldPath,
			int demand) {

		HashSet<Edge> pathEdges = new HashSet<Edge>(oldPath.getEdgeList());

		for(Edge edge : graph.edgeSet()) {
			double newWeight;
			int usedCapAfterRemoving;
			
			if (pathEdges.contains(edge)) {
				usedCapAfterRemoving = edge.getUsedCapacity() - demand;
			} else {
				usedCapAfterRemoving = edge.getUsedCapacity();
			}
			
			int threshCapacity;
			
			if (policy == Policy.MakeBeforeBreak) {
				threshCapacity = edge.getUsedCapacity();
			} else if (policy == Policy.BreakBeforeMake) {
				threshCapacity = usedCapAfterRemoving;
			} else {
				throw new RuntimeException();
			}
			
			if(threshCapacity + demand > edge.getCapacity()) {
				newWeight = Double.POSITIVE_INFINITY;
			} else {
				double costBeforeChange = costFunction(
						edge, usedCapAfterRemoving);
				double costAfterChange = costFunction(
						edge, usedCapAfterRemoving + demand);
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

	@Override
	public RerouteData reroute(
			SimpleDirectedWeightedGraph<Vertex,Edge> graph,
			Vector<Channel> consideredChannels) {

		double bestImprovement = 0.0;
		RerouteData bestRerouteData = null;
		
		for(Channel channel : consideredChannels) {

			setWeights(graph, channel.getPath(), channel.getDemand());
			
			double oldWeight = pathWeight(graph, channel.getPath());
			
			Vertex source = channel.getPath().getStartVertex();
			Vertex target = channel.getPath().getEndVertex();
			DijkstraShortestPath<Vertex,Edge> shortestPathObj = 
					new DijkstraShortestPath<Vertex,Edge>(
							graph, source, target);
			GraphPath<Vertex,Edge> shortestPath = shortestPathObj.getPath();
			
			double newWeight = shortestPath.getWeight();
			double improvement = newWeight - oldWeight;
			
			if(improvement < bestImprovement) {
				bestImprovement = improvement;
				bestRerouteData = new RerouteData(channel, shortestPath);
			}
		}
		
		// TODO: Return null if best improvement is smaller than some epsilon
		return bestRerouteData;
	}
}