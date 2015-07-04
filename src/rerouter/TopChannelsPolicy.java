package rerouter;

public class TopChannelsPolicy {
	final boolean isNumber;
	public final int numTopChannels;
	final double fracTopChannels;
	
	private TopChannelsPolicy(
			final boolean isNumber,
			final int numTopChannels,
			final double fracTopChannels) {
		this.isNumber = isNumber;
		this.numTopChannels = numTopChannels;
		this.fracTopChannels = fracTopChannels;
	}

	public static TopChannelsPolicy createNum(final int num) {
		return new TopChannelsPolicy(true, num, 0);
	}
	
	public static TopChannelsPolicy createFrac(final double frac) {
		return new TopChannelsPolicy(false, 0, frac);
	}
	
	int getNumChannels(final int totalChannels) {
		if (isNumber) {
			return numTopChannels <= totalChannels ? 
					numTopChannels : totalChannels;
		} else {
			return (int)(fracTopChannels * totalChannels);
		}
	}
}
