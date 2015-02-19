package flow_generator;

import java.util.Random;

public class ConstantDistribution implements Distribution {
	private final double val;
	
	public ConstantDistribution(double val) {
		this.val = val;
	}
	
	@Override
	public double getRandomSample(Random randomGen) {
		return val;
	}

}
