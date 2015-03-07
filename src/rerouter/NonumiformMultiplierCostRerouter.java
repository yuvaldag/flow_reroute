package rerouter;

import java.util.stream.DoubleStream;

import org.jgrapht.GraphPath;

import reroute_network.Edge;
import reroute_network.Vertex;

public class NonumiformMultiplierCostRerouter extends SimpleExpWeightsRerouter {
	private final double[] edgeMultipliers;
	private final double[] updates;
	private final boolean[] blockedFlows;

	private final double stepSize;
	private final double smoothFactor;

	public NonumiformMultiplierCostRerouter(
			double expParam,
			double stepSize,
			double smoothFactor,
			int numEdges) throws PathRerouterException {
		super(expParam);
		
		if (stepSize < 0 || stepSize >= 1) {
			throw new PathRerouterException("stepSize = " + stepSize +
					" and it doeesn't satisfy 0 <= stepSize < 1");
		}
		
		if (smoothFactor < 0 || smoothFactor >= 1) {
			throw new PathRerouterException("smoothFactor = " + smoothFactor +
					" and it doeesn't satisfy 0 <= smoothFactor < 1");
		}
		
		if (numEdges <= 0) {
			throw new PathRerouterException("numEdges = " + numEdges +
					" but it has to be positive");
		}
			
		this.edgeMultipliers = new double[numEdges];
		this.updates = new double[numEdges];
		this.blockedFlows = new boolean[numEdges];
		for (int i = 0; i < edgeMultipliers.length; i++) {
			edgeMultipliers[i] = 1;
			updates[i] = 0;
		}
		
		resetBlockedFlows();
		this.stepSize = stepSize;
		this.smoothFactor = smoothFactor;
	}

	void resetBlockedFlows() {
		for (int i = 0; i < blockedFlows.length; i++) {
			blockedFlows[i] = false;
		}
	}

	@Override
	double edgeCostMultiplier(Edge edge) {
		return edgeMultipliers[edge.getId()];
	}
	
	@Override
	public void prepareReroute() {
		for (int i = 0; i < blockedFlows.length; i++) {
			updates[i] = smoothFactor * updates[i];
			if (blockedFlows[i]) {
				updates[i] += 1 - smoothFactor;
			}
			edgeMultipliers[i] += stepSize * updates[i];
		}

		double multiplierAvg =
				DoubleStream.of(edgeMultipliers).average().getAsDouble();
		for (int i = 0; i < edgeMultipliers.length; i++) {
			edgeMultipliers[i] /= multiplierAvg;
		}
		
		resetBlockedFlows();
	}
	
	@Override
	public void newFlow(
			GraphPath<Vertex, Edge> path,
			int demand,
			boolean addedSuccessfully) {
		
		if (addedSuccessfully) {
			return;
		}

		for (Edge e : path.getEdgeList()) {
			if (e.getCapacity() - e.getUsedCapacity() < demand) {
				blockedFlows[e.getId()] = true;
			}	
		}
	}
	
	public void printMultipliers() {
		for (int i = 0; i < edgeMultipliers.length; i++) {
			System.out.println(
					"" + i + " " + edgeMultipliers[i] + " " + updates[i]);
		}
	}
}