package flow_generator;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class FlowGenerator {
	final Random randomGen;
	final TreeMap<Double, Integer> nodeActivities;
	final double demandFrequencyExpParam;
	final double flowDurationExpParam;

	FlowGenerator(
			int numNodes,
			double demandFrequencyExpParam,
			double flowDurationExpParam,
			long seed) {
		randomGen = new Random(seed);
		nodeActivities = new TreeMap<Double, Integer>();
		setNodeActivity(numNodes);
		this.demandFrequencyExpParam = demandFrequencyExpParam;
		this.flowDurationExpParam = flowDurationExpParam;
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
			nodeActivities.put(partialSum / totalSum, i);
			partialSum += weights[i];
		}
	}
	
	private int getRandNode() {
		// keeping 1 - random is necessary because can return 0.
		// If we didn't do so then we would get an error
		final double randNum = 1 - randomGen.nextDouble();
		final Map.Entry<Double, Integer> entry =
				nodeActivities.lowerEntry(randNum);
		
		return entry.getValue();
	}
	
	public FlowInfo generate() {
		final int source = getRandNode();
		int target = getRandNode();
		
		while (target == source) {
			target = getRandNode();
		}
		
		final double delayTime = getRandomExp(demandFrequencyExpParam);
		final double duration = getRandomExp(flowDurationExpParam);
		
		return new FlowInfo(source, target, delayTime, duration);
	}
}