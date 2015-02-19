package flow_generator;

import java.util.Random;

public class ZipfDistribution implements Distribution {
	private final double power;
	private final double minVal;
	private final double maxVal;
	
	public ZipfDistribution(final double power)
			throws ZipfException {
		this(power, 1.0, Double.POSITIVE_INFINITY);
	}
	
	public ZipfDistribution(
			final double power, final double minVal,
			final double maxVal)
					throws ZipfException {
		if (power <= 1.0)
			throw new ZipfException(
					"Illegal power. " +
					"The power received is " + power);

		if (minVal <= 0) {
			throw new ZipfException(
					"Minimal value is " + minVal +
					" but it has to be positive");
		}
		
		if (maxVal <= minVal) {
			throw new ZipfException(
					"Maximal value is " +
					maxVal +
					" minimal value is " +
					minVal +
					" but the maximal value has to be greater than the " +
					"minimal");
		}
		
		this.power = power;
		this.maxVal = maxVal;
		this.minVal = minVal;
	}

	@Override
	public double getRandomSample(final Random randomGen) {
		final double minPowered = Math.pow(minVal, -power + 1);
		final double maxPowered = Math.pow(maxVal, -power + 1);
		final double sample = randomGen.nextDouble();
		final double formula = minPowered - sample * (minPowered - maxPowered);
		
		return Math.pow(formula, 1 / (-power + 1));
	}
}