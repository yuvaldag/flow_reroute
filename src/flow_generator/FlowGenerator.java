package flow_generator;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class FlowGenerator {
	final Random randomGen;
	final TreeMap<Double, Integer> thresholds;
	final Distribution flowFrequencyGen;
	final Distribution flowDurationGen;
	final Distribution demandGen;

	public FlowGenerator(
			int numNodes,
			Distribution flowFrequencyGen,
			Distribution flowDurationGen,
			Distribution demandGen,
			long seed) {
		randomGen = new Random(seed);
		thresholds = new TreeMap<Double, Integer>();
		setNodeActivity(numNodes);
		this.flowFrequencyGen = flowFrequencyGen;
		this.flowDurationGen = flowDurationGen;
		this.demandGen = demandGen;
	}

	private void setNodeActivity(final int numNodes) {
		final double[] weights = new double[numNodes];
		
		// since we scale the results at the end, the value of 
		// lambda dosn't matter
		Distribution weightGen;
		try {
			weightGen = new ExpDistribution(1);
		} catch (ExpException e) {
			e.printStackTrace();
			throw new RuntimeException("FlowGenerator.setNodeActivity: "
					+ "internal error");
		}
		
		double totalSum = 0;
		for (int i = 0; i < weights.length; i++) {
			weights[i] = weightGen.getRandomSample(randomGen);
			totalSum += weights[i];
		}
		
		double partialSum = 0;
		for (int i = 0; i < weights.length; i++) {
			thresholds.put(partialSum / totalSum, i);
			partialSum += weights[i];
		}
	}
	
	private int getRandNode() {
		// keeping 1 - random is necessary because can return 0.
		// If we didn't do so then we would get an error
		final double randNum = 1 - randomGen.nextDouble();
		final Map.Entry<Double, Integer> entry =
				thresholds.lowerEntry(randNum);
		
		return entry.getValue();
	}
	
	public FlowInfo generate() {
		final int source = getRandNode();
		int target = getRandNode();
		
		while (target == source) {
			target = getRandNode();
		}
		
		final double delayTime = flowFrequencyGen.getRandomSample(randomGen);
		final double duration = flowDurationGen.getRandomSample(randomGen);
		final int demand = 
				(int) Math.round(demandGen.getRandomSample(randomGen));
		return new FlowInfo(source, target, demand, delayTime, duration);
	}
}