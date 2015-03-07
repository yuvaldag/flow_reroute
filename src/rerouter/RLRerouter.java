package rerouter;

import org.jgrapht.GraphPath;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import reroute_network.Edge;
import reroute_network.Vertex;

public class RLRerouter extends ChangingExpRerouter {	
	private final double stepSize;
	private final double futureFactor;
	private final double pastFactor;
	private final double[] constGradientSum;
	private final double[] expParamGradientSum;
	private double prevPrediction;
	private final StatisticsCalculator statsCalc;
	
	public RLRerouter(
			double stepSize,
			double futureFactor, 
			double pastFactor,
			int numEdges)
					throws PathRerouterException {
		super(numEdges);
		
		if (stepSize < 0) {
			throw new PathRerouterException("invalid step size");
		}
		
		if (futureFactor < 0 || futureFactor >= 1) {
			throw new PathRerouterException("Invalid futureFactor");
		}
		
		if (pastFactor < 0 || pastFactor >= 1) {
			throw new PathRerouterException("Invalid pastFactor");
		}
		
		this.stepSize = stepSize;
		this.pastFactor = pastFactor;
		this.futureFactor = futureFactor;
		constGradientSum = new double[numEdges];
		expParamGradientSum = new double[numEdges];

		for (int i = 0; i < numEdges; i++) {
			constGradientSum[i] = 0;
			expParamGradientSum[i] = 0;
		}

		prevPrediction = 0;
		this.statsCalc = new StatisticsCalculator();
	}
	
	private double getPrediction(
			SimpleDirectedWeightedGraph<Vertex,Edge> graph) {
		double sum = 0;
		
		for (Edge edge : graph.edgeSet()) {
			sum += costFunction(edge, edge.getUsedCapacity()) - 
					costFunction(edge, 0);
		}

		return sum;
	}

	@Override
	public void prepareReroute() {
	}

	private double weightedSum(double d1, double d2, double factor) {
		return (1 - factor) * d1 + factor * d2;
	}
	
	private double[] getConstGradient(
			SimpleDirectedWeightedGraph<Vertex, Edge> graph) {
		final int numEdges = graph.edgeSet().size();
		
		double[] gradients = new double[numEdges];
		
		for (Edge e : graph.edgeSet()) {
			gradients[e.getId()] = expValue(e, e.getUsedCapacity())
					- expValue(e, 0);
		}
		
		return gradients;
	}

	private double[] getExpParamGradient(
			SimpleDirectedWeightedGraph<Vertex, Edge> graph) {
		final int numEdges = graph.edgeSet().size();
		double[] gradients = new double[numEdges];
		
		for (Edge e : graph.edgeSet()) {
			gradients[e.getId()] = 
					-((double)e.getUsedCapacity()) / e.getCapacity() * 
					constants[e.getId()] * 
					expValue(e, e.getUsedCapacity());
		}
		
		return gradients;
	}

	@Override
	public void endReroute(SimpleDirectedWeightedGraph<Vertex, Edge> graph) {
		double penalty = statsCalc.fractionOfSuccess();
		double nextRoundPrediction = getPrediction(graph);
		double updatedPrediction = 
				weightedSum(penalty, nextRoundPrediction, futureFactor);
		double err = updatedPrediction - prevPrediction;
		double[] constGradient = getConstGradient(graph);
		double[] expParamGradient = getExpParamGradient(graph);
		
		for (int i = 0; i < constGradientSum.length; i++) {
			constGradientSum[i] = weightedSum(
					constGradient[i], constGradientSum[i], pastFactor);
			expParamGradientSum[i] = weightedSum(
					expParamGradient[i], expParamGradientSum[i], pastFactor);
			
			constants[i] += stepSize * err * constGradientSum[i];
			if (constants[i] < 0) {
				constants[i] = 0;
			}
			
			expParams[i] += stepSize * err * expParamGradientSum[i];
			if (expParams[i] < 5.) {
				expParams[i] = 5.;
			}
		}
		
		statsCalc.newRound();
		prevPrediction = nextRoundPrediction;
	}

	@Override
	public void newFlow(
			GraphPath<Vertex, Edge> path, 
			int demand,
			boolean addedSuccessfully) {
		statsCalc.addFlow(addedSuccessfully);
	}
}