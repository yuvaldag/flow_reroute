package reroute_network;

import java.util.HashMap;

import org.jgrapht.GraphPath;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

class Flow {
	GraphPath<Vertex, Edge> path;
	final int demand;

	Flow(GraphPath<Vertex,Edge> flowPath, int flowDemand) {
		path = flowPath;
		demand = flowDemand;
	}
}

// TODO: Allow access only via graph creator
public class RerouteNet {
	final SimpleDirectedWeightedGraph<Vertex, Edge> graph;
	final HashMap<Integer, Flow> flows;
	final DefaultPathRouter defaultPathRouter;
	final PathRerouter pathRerouter;

	@SuppressWarnings("unchecked")
	public RerouteNet(
			SimpleDirectedWeightedGraph<Vertex,Edge> graph,
			PathRerouter pathRerouter) {
		this.graph = (SimpleDirectedWeightedGraph<Vertex,Edge>) graph.clone();
		flows = new HashMap<Integer,Flow>();
		defaultPathRouter = new ShortestPathDefaultRouter(graph);
		this.pathRerouter = pathRerouter;
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
	public boolean addFlow(int id, Vertex source, Vertex target, int demand)
			throws FlowExistsException, NegativeDemandException {
		if(flows.containsKey(id))
			throw new FlowExistsException();

		if(demand < 0)
			throw new NegativeDemandException();

		GraphPath<Vertex,Edge> path;
		path = defaultPathRouter.findDefaultPath(graph, demand, source, target);
		if(path == null)
			return false;

		Flow flow = new Flow(path, demand);
		flows.put(id, flow);
		addFlowToNet(flow);
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

		removeFlowFromNet(flows.get(id));

		flows.remove(id);
	}

	/*
	 * Calls a stage of re-routing.
	 */
	public void rerouteFlows(){
		pathRerouter.reroute(graph, flows);
	}

	static void addFlowToNet(Flow flow) {
		for(Edge edge : flow.path.getEdgeList()) {
			edge.usedCapacity += flow.demand;
		}
	}

	static void removeFlowFromNet(Flow flow) {
		for(Edge edge : flow.path.getEdgeList()) {
			edge.usedCapacity -= flow.demand;
		}
	}
}