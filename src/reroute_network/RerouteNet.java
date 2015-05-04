package reroute_network;

import java.util.HashMap;
import java.util.HashSet;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.FloydWarshallShortestPaths;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.util.VertexPair;

import rerouter.PathRerouter;
import rerouter.RerouteData;

public class RerouteNet {
	final SimpleDirectedWeightedGraph<Vertex, Edge> graph;
	final Vector<Vertex> generatingVertices;
	final HashMap<Integer, Flow> flows;
	final PathRerouter pathRerouter;
	final int numAllowedReroutings;
	private final HashMap<VertexPair<Vertex>, Channel> defaultChannels;
	private final HashSet<Channel> otherChannels;
	private final HashSet<Channel> allHistoryChannels;
	private int totalNumEndedFlows;
	private int totalNumFlowsReroutings;
	private final KeepDefaultPath keepDefaultPath;
	
	RerouteNet(
			final GraphData graphData,
			final PathRerouter pathRerouter,
			final int numAllowedReroutings,
			final KeepDefaultPath keepDefaultPath) {
		this.graph = graphData.graph;
		this.generatingVertices = graphData.generatingVertices;
		this.pathRerouter = pathRerouter;
		this.numAllowedReroutings = numAllowedReroutings;
		this.flows = new HashMap<Integer,Flow>();
		this.defaultChannels = getDefaultChannels();
		this.otherChannels = new HashSet<Channel>();
		this.keepDefaultPath = keepDefaultPath;
		this.allHistoryChannels = new HashSet<Channel>(
				this.defaultChannels.values());
		this.totalNumEndedFlows = 0;
		this.totalNumFlowsReroutings = 0;
	}
	
	public RerouteNet(
			final GraphCreator graphCreator,
			final PathRerouter pathRerouter,
			final int numAllowedReroutings,
			final KeepDefaultPath keepDefaultPath) {
		this(graphCreator.createGraph(),
				pathRerouter,
				numAllowedReroutings,
				keepDefaultPath);
	}

	/*
	 * 	Adds a flow to the network, if possible.
	 * 
	 *  @param id		the identifier of the flow
	 *  @param source	the source node
	 *  @param target	the target node
	 *  @param demand	the total demand
	 *  @return			true if the flow was added and false otherwise
	 */
	public boolean addFlow(int id, int source, int target, int demand)
			throws FlowExistsException, NegativeDemandException,
					IllegalNodeException, DefaultPathRouterException {
		if (flows.containsKey(id))
			throw new FlowExistsException();

		if (demand < 0)
			throw new NegativeDemandException();

		if (source < 0 || target < 0 ||
				source >= generatingVertices.size() || 
				target >= generatingVertices.size())
			throw new IllegalNodeException();
		
		VertexPair<Vertex> pathVertexPair = new VertexPair<Vertex>(
					generatingVertices.get(source), 
					generatingVertices.get(target));
		Channel channel = defaultChannels.get(pathVertexPair);
		
		if (! flowInsertable(channel.getPath(), demand)) {
			pathRerouter.newFlow(channel.getPath(), demand, false);
			return false;
		}

		Flow flow = new Flow(channel, demand, channel.getNumRerouted());
		flows.put(id, flow);
		channel.addDemand(demand);
		addFlowToNet(channel.getPath(), demand);
		
		pathRerouter.newFlow(channel.getPath(), demand, true);
		
		return true;
	}

	/*
	 * Removes a flow.
	 * 
	 *  @param id		the identifier of the flow
	 */
	public void removeFlow(int id) throws FlowNotExistsException {
		if(! flows.containsKey(id))
			throw new FlowNotExistsException();

		Flow flow = flows.get(id);
		Channel channel = flow.channel;
		channel.reduceDemand(flow.demand);
		
		totalNumEndedFlows++;
		totalNumFlowsReroutings += 
				channel.getNumRerouted() - flow.numReroutingChannelHad;
		
		if (channel.getDemand() == 0) {
			VertexPair<Vertex> vPair = new VertexPair<Vertex>(
					channel.getPath().getStartVertex(),
					channel.getPath().getEndVertex());
			
			if (defaultChannels.get(vPair) != channel) {
				otherChannels.remove(channel);
			}
		}
		
		removeFlowFromNet(channel.getPath(), flow.demand);
		flows.remove(id);
	}

	/*
	 * Validates that:
	 * 		- The path is a legal path
	 * 		- The path is contained in the graph
	 * 		- The start vertex and end vertex are as expected
	 */
	private void validatePath(
			GraphPath<Vertex, Edge> path, Vertex source, Vertex target)
					throws IllegalPathException {
		Vertex prevVertex = source;
		for (Edge e : path.getEdgeList()) {
			Vertex nextVertex = graph.getEdgeTarget(e);
			if (graph.getEdge(prevVertex, nextVertex) != e)
				throw new IllegalPathException();
			
			prevVertex = nextVertex;
		}
		
		if (prevVertex != target)
			throw new IllegalPathException();
	}
	
	// TODO: Return only elephant flows
	private Vector<Channel> getElephantChannels() {
		Vector<Channel> allChannels =
				new Vector<Channel>(defaultChannels.values());
		
		if (keepDefaultPath != KeepDefaultPath.KEEP_DEFAULT_MOVE_ONCE) {
			for (Channel channel : otherChannels) {
				allChannels.add(channel);
			}
		}
		
		return allChannels;
	}
	
	/*
	 * Calls a stage of re-routing.
	 */
	public void rerouteFlows()
			throws IllegalPathException, NotEnoughCapacityException {
		pathRerouter.prepareReroute();
		
		for (int i = 0; i < numAllowedReroutings; i++) { 
			RerouteData rerouteData = pathRerouter.reroute(
					graph,
					getElephantChannels());
		
			if (rerouteData == null) {
				return;
			}
			
			Channel channel = rerouteData.channel;
			GraphPath<Vertex,Edge> oldPath = channel.getPath();
			validatePath(rerouteData.newPath, oldPath.getStartVertex(), 
							oldPath.getEndVertex());

			removeFlowFromNet(oldPath, channel.getDemand());
			
			if (!flowInsertable(rerouteData.newPath, channel.getDemand())) {
				addFlowToNet(channel.getPath(), channel.getDemand());
				throw new NotEnoughCapacityException();
			}
			
			channel.reroute(rerouteData.newPath);
			addFlowToNet(rerouteData.newPath, channel.getDemand());
			
			if (keepDefaultPath == KeepDefaultPath.KEEP_DEFAULT ||
					keepDefaultPath == 
									KeepDefaultPath.KEEP_DEFAULT_MOVE_ONCE) {
				VertexPair<Vertex> vPair = new VertexPair<Vertex>( 
						oldPath.getStartVertex(), oldPath.getEndVertex());
				
				if (defaultChannels.get(vPair) == channel) {
					otherChannels.add(channel);
					
					Channel newChannel = new Channel(oldPath);
					defaultChannels.put(vPair, newChannel);
					allHistoryChannels.add(newChannel);
				}
			}
		}

		pathRerouter.endReroute(graph);
	}

	private void addFlowToNet(GraphPath<Vertex, Edge> path, int demand) {
		for(Edge edge : path.getEdgeList()) {
			edge.usedCapacity += demand;
		}
	}

	private void removeFlowFromNet(GraphPath<Vertex, Edge> path, int demand) {
		for(Edge edge : path.getEdgeList()) {
			edge.usedCapacity -= demand;
		}
	}

	private boolean flowInsertable(GraphPath<Vertex,Edge> path, int demand) {
		for(Edge edge : path.getEdgeList()) {
			if(edge.capacity - edge.usedCapacity < demand)
				return false;
		}

		return true;
	}

	HashMap<VertexPair<Vertex>, Channel> getDefaultChannels() {
		for(Edge edge : graph.edgeSet()) {
			double weight = 1.0 / edge.capacity;
			graph.setEdgeWeight(edge, weight);
		}
	
		FloydWarshallShortestPaths<Vertex,Edge> shortestPaths =
				new FloydWarshallShortestPaths<Vertex,Edge>(graph);
		HashMap<VertexPair<Vertex>, Channel> defChannels = new 
				HashMap<VertexPair<Vertex>, Channel>();

		for(GraphPath<Vertex,Edge> path : shortestPaths.getShortestPaths()) {
			VertexPair<Vertex> vPair = new VertexPair<Vertex>(
					path.getStartVertex(), path.getEndVertex());

			if (generatingVertices.contains(vPair.getFirst()) &&
					generatingVertices.contains(vPair.getSecond())) {
				Channel channel = new Channel(path);
				defChannels.put(vPair, channel);
			}
		}

		return defChannels;
	}

	public int getNumChannels() {
		return defaultChannels.size() + otherChannels.size();
	}

	public SortedMap<Integer, Integer> getChannelHistogram() {
		SortedMap<Integer, Integer> histogram = 
				new TreeMap<Integer, Integer>();

		for (Channel channel : allHistoryChannels) {
			int numRerouted = channel.getNumRerouted();

			if (histogram.containsKey(numRerouted)) {
				int count = histogram.get(numRerouted) + 1;
				histogram.put(numRerouted, count);
			} else {
				histogram.put(numRerouted, 1);
			}
		}

		return histogram;
	}

	public double avgReroutingsPerFlow() {
		return ((double)totalNumFlowsReroutings) / totalNumEndedFlows;
	}
	
	public double getAvgDefaultChanngelLength() {
		final int num = defaultChannels.values().size();
		int sum = 0;
		
		for (Channel channel : defaultChannels.values()) {
			sum += channel.getPath().getEdgeList().size();
		}

		return ((double)sum) / num;
	}
}