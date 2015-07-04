package rerouter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import reroute_network.Channel;
import reroute_network.Edge;
import reroute_network.Vertex;

public abstract class ExpWeightsRerouter extends PathRerouter {	
	private final Policy policy;
	private final TopChannelsPolicy topChannelsPolicy;
	private final LimitedChannelsPolicy limitedChannelsPolicy;
	private final Random random;
	private int numSPInvocations;
	private final boolean oldArticle;
	
	public ExpWeightsRerouter( 
			Policy policy,
			TopChannelsPolicy topChannelsPolicy,
			LimitedChannelsPolicy randomChannelsPolicy,
			long seed,
			final boolean oldArticle) 
					throws PathRerouterException {
		this.policy = policy;
		this.topChannelsPolicy = topChannelsPolicy;
		this.limitedChannelsPolicy = randomChannelsPolicy;
		this.random = new Random(seed);
		this.numSPInvocations = 0;
		this.oldArticle = oldArticle;
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

	private double edgeWeight(
			Edge edge, 
			Policy policy, 
			Set<Edge> pathEdges,
			int demand) {
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
		} else if (! oldArticle){
			double costBeforeChange = costFunction(
					edge, usedCapAfterRemoving);
			double costAfterChange = costFunction(
					edge, usedCapAfterRemoving + demand);
			newWeight = costAfterChange - costBeforeChange;
		} else {
			newWeight = Math.exp(-(double)(edge.getCapacity() - 
					usedCapAfterRemoving - demand) / edge.getCapacity());
		}
		
		return newWeight;
	}
	
	private void setWeights(
			SimpleDirectedWeightedGraph<Vertex,Edge> graph, 
			GraphPath<Vertex, Edge> oldPath,
			int demand) {

		HashSet<Edge> pathEdges = new HashSet<Edge>(oldPath.getEdgeList());

		for(Edge edge : graph.edgeSet()) {
			double newWeight = edgeWeight(edge, this.policy, pathEdges, demand);
			graph.setEdgeWeight(edge, newWeight);
		}
	}

	abstract double edgeCostMultiplier(Edge edge);

	abstract double edgeExpParam(Edge edge);

	private double pathWeight(
			SimpleDirectedWeightedGraph<Vertex,Edge> graph,
			GraphPath<Vertex,Edge> path,
			int demand) {
		double sum = 0;
		
		for(Edge edge : path.getEdgeList()) {
			sum += edgeWeight(edge, Policy.BreakBeforeMake, graph.edgeSet(), 
					demand);
		}
		
		return sum;
	}

	private boolean emptyIntersection(Set<Edge> s1, Collection<Edge> s2) {
		Set<Edge> intersection = new HashSet<Edge>(s1);
		intersection.retainAll(s2);
		
		return intersection.size() == 0;
	}
	
	RerouteData getRerouteData(
			SimpleDirectedWeightedGraph<Vertex, Edge> graph,
			Channel channel) {
		numSPInvocations += 1;
		setWeights(graph, channel.getPath(), channel.getDemand());
		
		double oldWeight = pathWeight(graph, channel.getPath(), 
				channel.getDemand());
		
		Vertex source = channel.getPath().getStartVertex();
		Vertex target = channel.getPath().getEndVertex();
		DijkstraShortestPath<Vertex,Edge> shortestPathObj = 
				new DijkstraShortestPath<Vertex,Edge>(
						graph, source, target);
		GraphPath<Vertex,Edge> shortestPath = shortestPathObj.getPath();

		double newWeight = shortestPath.getWeight();
		
		double improvement = newWeight - oldWeight;

		return new RerouteData(channel, shortestPath, improvement);
	}
	
	@Override
	public RerouteData reroute(
			SimpleDirectedWeightedGraph<Vertex,Edge> graph,
			Vector<Channel> consideredChannels) {

		double minImprovement = 0;
		RerouteData bestRerouteData = null;
		
		Set<Edge> topEdges = null;
		if (limitedChannelsPolicy.numEdges > 0) {
			List<Edge> edges = new ArrayList<Edge>(graph.edgeSet());
			Collections.sort(edges);
			edges = edges.subList(0, limitedChannelsPolicy.numEdges);
			topEdges = new HashSet<Edge>(edges);
		}
		
		for(Channel channel : consideredChannels) {
			if (topEdges == null || ! emptyIntersection(
						topEdges, channel.getPath().getEdgeList())) {
				RerouteData rerouteData = getRerouteData(graph, channel);
				double improvement =
						rerouteData.improvement;
				if (improvement < minImprovement) {
					minImprovement = improvement;
					bestRerouteData = rerouteData;
				}
			}
		}
	
		return bestRerouteData;
	}
	
	public int getNumSPInvocations() {
		return numSPInvocations;
	}
}