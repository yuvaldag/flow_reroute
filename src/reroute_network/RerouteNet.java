package reroute_network;

import java.util.HashMap;
import java.util.Vector;

import org.jgrapht.GraphPath;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import rerouter.PathRerouter;
import rerouter.RerouteData;

public class RerouteNet {
	final SimpleDirectedWeightedGraph<Vertex, Edge> graph;
	final Vector<Vertex> vertices;
	final HashMap<Integer, Flow> flows;
	final DefaultPathRouter defaultPathRouter;
	final PathRerouter pathRerouter;
	final int numAllowedReroutings;
	
	RerouteNet(
			final GraphData graphData,
			final PathRerouter pathRerouter,
			final int numAllowedReroutings) {
		this.graph = graphData.graph;
		this.vertices = graphData.vertices;
		this.pathRerouter = pathRerouter;
		this.numAllowedReroutings = numAllowedReroutings;
		this.flows = new HashMap<Integer,Flow>();
		this.defaultPathRouter = new ShortestPathDefaultRouter(graph);
	}
	
	public RerouteNet(
			final GraphCreator graphCreator,
			final PathRerouter pathRerouter,
			final int numAllowedReroutings) {
		this(graphCreator.createGraph(), pathRerouter, numAllowedReroutings);
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
				source >= vertices.size() || target >= vertices.size())
			throw new IllegalNodeException();
		
		GraphPath<Vertex,Edge> path;
		path = defaultPathRouter.findDefaultPath(
				graph, demand, vertices.get(source), vertices.get(target));
		
		boolean retVal = true;
		
		for(Edge edge : path.getEdgeList()) {
			if(edge.capacity - edge.usedCapacity < demand) {
				retVal = false;
				break;
			}
		}

		if (retVal) {
			Flow flow = new Flow(path, demand);
			flows.put(id, flow);
			addFlowToNet(flow);
		}
		
		pathRerouter.newFlow(path, demand, retVal);
		
		return retVal;
	}

	/*
	 * Removes a flow.
	 * 
	 *  @param id		the identifier of the flow
	 */
	public void removeFlow(int id) throws FlowNotExistsException {
		if(! flows.containsKey(id))
			throw new FlowNotExistsException();

		removeFlowFromNet(flows.get(id));

		flows.remove(id);
	}

	/*
	 * Validates that:
	 * 		- The path is a legal path
	 * 		- The path is contained in the graph
	 * 		- The start vertex and end vertex are as expected
	 */
	private void validatePath(
			GraphPath<Vertex,Edge> path, Vertex source, Vertex target)
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
	private Vector<Flow> getElephantFlows() {
		Vector<Flow> ret = new Vector<Flow>();
		for (Flow flow : flows.values()) {
			ret.add(flow);
		}
		
		return ret;
	}
	
	/*
	 * Calls a stage of re-routing.
	 */
	@SuppressWarnings("unchecked")
	public void rerouteFlows()
			throws tooManyReroutingsException, IllegalPathException,
					NotEnoughCapacityException {
		Vector<RerouteData> dataOfFlows = pathRerouter.reroute(
				(SimpleDirectedWeightedGraph<Vertex, Edge>)graph.clone(),
				getElephantFlows(),
				numAllowedReroutings);
		
		if (dataOfFlows.size() > numAllowedReroutings)
			throw new tooManyReroutingsException();
		
		for (RerouteData dataOfFlow : dataOfFlows) {
			GraphPath<Vertex,Edge> oldPath = dataOfFlow.flow.path;
			validatePath(dataOfFlow.newPath, oldPath.getStartVertex(), 
							oldPath.getEndVertex());

			removeFlowFromNet(dataOfFlow.flow);
			if (!flowInsertable(dataOfFlow.newPath, dataOfFlow.flow.demand)) {
				addFlowToNet(dataOfFlow.flow);
				throw new NotEnoughCapacityException();
			}
			
			dataOfFlow.flow.path = dataOfFlow.newPath;
			addFlowToNet(dataOfFlow.flow);
		}
	}

	private void addFlowToNet(Flow flow) {
		for(Edge edge : flow.path.getEdgeList()) {
			edge.usedCapacity += flow.demand;
		}
	}

	private void removeFlowFromNet(Flow flow) {
		for(Edge edge : flow.path.getEdgeList()) {
			edge.usedCapacity -= flow.demand;
		}
	}
	
	private boolean flowInsertable(GraphPath<Vertex,Edge> path, int demand) {
		for(Edge edge : path.getEdgeList()) {
			if(edge.capacity - edge.usedCapacity < demand)
				return false;
		}
		
		return true;
	}
	
	Edge getEdge(int source, int target) {
		return graph.getEdge(vertices.get(source), vertices.get(target));
	}
}