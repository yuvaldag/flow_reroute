package flow_generator;

import java.util.Random;

public class ExpDistribution implements Distribution {
	private double lambda;
	
	public ExpDistribution() throws ExpException {
		this(1);
	}
	
	public ExpDistribution(double lambda) throws ExpException {
		if (lambda <= 0.0) {
			throw new ExpException(
					"Illegal value of lambda. It has to be positive. " +
					"Value received is " + lambda);
		}
		
		this.lambda = lambda;
	}
	
	@Override
	public double getRandomSample(Random randomGen) {
		return -Math.log(1 - randomGen.nextDouble()) / lambda;
	}

}