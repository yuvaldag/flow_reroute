package flow_generator;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class FlowGenerator {
	final Random randomGen;
	final TreeMap<Double, Integer> thresholds;
	final double flowFrequencyExpParam;
	final double flowDurationExpParam;
	final double demandExpParam;

	public FlowGenerator(
			int numNodes,
			double flowFrequencyExpParam,
			double flowDurationExpParam,
			double demandExpParam,
			long seed) {
		randomGen = new Random(seed);
		thresholds = new TreeMap<Double, Integer>();
		setNodeActivity(numNodes);
		this.flowFrequencyExpParam = flowFrequencyExpParam;
		this.flowDurationExpParam = flowDurationExpParam;
		this.demandExpParam = demandExpParam;
	}

	private double getRandomExp(final double expParam) {
		return -Math.log(1 - randomGen.nextDouble()) / expParam;
	}

	private void setNodeActivity(final int numNodes) {
		final double[] weights = new double[numNodes];
		
		// since we scale the results at the end, the value of 
		// exp-param dosn't matter
		final double expParam = 1;
		
		double totalSum = 0;
		for (int i = 0; i < weights.length; i++) {
			weights[i] = getRandomExp(expParam);
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
		
		final double delayTime = getRandomExp(flowFrequencyExpParam);
		final double duration = getRandomExp(flowDurationExpParam);
		final int demand = (int) getRandomExp(demandExpParam) + 1;
		return new FlowInfo(source, target, demand, delayTime, duration);
	}
}