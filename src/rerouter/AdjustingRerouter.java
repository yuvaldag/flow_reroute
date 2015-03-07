package rerouter;

import java.util.Random;

import org.jgrapht.GraphPath;

import reroute_network.Edge;
import reroute_network.Vertex;

public class AdjustingRerouter extends ChangingExpRerouter {
	private final RecentAverage avgBlocking;
	private final double constExplorationFactor;
	private final double expParamExplorationFactor;
	private final double stepSize;
	private final double[] constUpdates;
	private final double[] expParamUpdates;
	private final StatisticsCalculator statsCalc;
	private final Random random;
	
	public AdjustingRerouter(
			final int numToAvg,
			final double constExplorationFactor,
			final double expParamExplorationFactor,
			final double stepSize,
			final int numEdges)
					throws PathRerouterException {
		super(numEdges);
		this.avgBlocking = new RecentAverage(numToAvg);
		this.stepSize = stepSize;
		this.constExplorationFactor = constExplorationFactor;
		this.expParamExplorationFactor = expParamExplorationFactor;
		
		this.constUpdates = new double[numEdges];
		this.expParamUpdates = new double[numEdges];
		
		for (int i = 0; i < numEdges; i++) {
			this.constUpdates[i] = 0;
			this.expParamUpdates[i] = 0;
		}
		
		statsCalc = new StatisticsCalculator();
		random = new Random();
	}
	
	@Override
	public void newFlow(
			GraphPath<Vertex, Edge> path, 
			int demand,
			boolean addedSuccessfully) {
		statsCalc.addFlow(addedSuccessfully);
	}
	
	@Override
	public void prepareReroute() {
		final double avgBlockRatio = avgBlocking.getAvg();
		final double currBlockRatio = statsCalc.fractionOfFailure();
		final double step = (avgBlockRatio - currBlockRatio) * stepSize;
		
		for (int i = 0; i < constants.length; i++) {
			constants[i] += (-1 + step) * constUpdates[i];
			expParams[i] += (-1 + step) * expParamUpdates[i];
			
			constUpdates[i] = (1 - 2 * random.nextInt(2)) * 
					constExplorationFactor;
			expParamUpdates[i] = (1 - 2 * random.nextInt(2)) * 
					expParamExplorationFactor;
			
			if (constants[i] < constExplorationFactor * 2) {
				constants[i] = constExplorationFactor * 2;
			}
			
			if (expParams[i] < 10) {
				expParams[i] = 10;
			}
			
			constants[i] += constUpdates[i];
			expParams[i] += expParamUpdates[i];
		}
		
		avgBlocking.addNum(currBlockRatio);
		statsCalc.newRound();
	}
}