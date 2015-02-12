package reroute_network;

import java.util.HashMap;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.FloydWarshallShortestPaths;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.util.VertexPair;

abstract class DefaultPathRouter {
	/*
	 * Find the default path for a new flow.
	 * 
	 * @param graph		the network graph
	 * @param startVertex	the start vertex
	 * @param endVertex		the end vertex
	 * @param demand		the demand
	 * @return			the path, if the flow could be added, null otherwise
	 */
	abstract GraphPath<Vertex,Edge> findDefaultPath(
			SimpleDirectedWeightedGraph<Vertex, Edge> graph, int demand,
			Vertex startVertex, Vertex endVertex);
}

class ShortestPathDefaultRouter extends DefaultPathRouter {
	// TODO: use a more compact way to save the shortest paths
	HashMap<VertexPair<Vertex>,GraphPath<Vertex,Edge>> allShortestPaths;

	GraphPath<Vertex,Edge> findDefaultPath(
			SimpleDirectedWeightedGraph<Vertex,Edge> graph,
			int demand, Vertex startVertex, Vertex endVertex){
		VertexPair<Vertex> vPair = new VertexPair<Vertex>(
				startVertex, endVertex);

		GraphPath<Vertex,Edge> candidatePath = allShortestPaths.get(vPair);
		if(candidatePath == null)
			return null;
		for(Edge edge : candidatePath.getEdgeList()) {
			if(edge.capacity - edge.usedCapacity < demand)
				return null;
		}

		return candidatePath;
	}

	ShortestPathDefaultRouter(SimpleDirectedWeightedGraph<Vertex,Edge> graph) {
		for(Edge edge : graph.edgeSet()) {
			double weight = 1.0 / edge.capacity;
			graph.setEdgeWeight(edge, weight);
		}
			
		FloydWarshallShortestPaths<Vertex,Edge> shortestPaths =
				new FloydWarshallShortestPaths<Vertex,Edge>(graph);
		allShortestPaths = new 
				HashMap<VertexPair<Vertex>,GraphPath<Vertex,Edge>>();
		
		for(GraphPath<Vertex,Edge> path : shortestPaths.getShortestPaths()) {
			VertexPair<Vertex> vPair = new VertexPair<Vertex>(
					path.getStartVertex(), path.getEndVertex());
			allShortestPaths.put(vPair, path);
		}
	}
}