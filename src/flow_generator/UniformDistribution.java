package flow_generator;

import java.util.Random;

public class UniformDistribution implements Distribution {
	private final double min;
	private final double max;
	
	public UniformDistribution(final double min, final double max) 
			throws DistributionException {
		if (min > max) {
			throw new DistributionException(
					"UniformDistribution constructor: minimal value has to " +
					"be less than the maximal value");
		}
		
		this.min = min;
		this.max = max;
	}
	
	@Override
	public double getRandomSample(Random randomGen) {
		return randomGen.nextDouble() * (max - min) + min;
	}
}