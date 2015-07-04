package rerouter;

public class LimitedChannelsPolicy {
	public final int numEdges;
	
	public LimitedChannelsPolicy() {
		this(0);
	}
	
	public LimitedChannelsPolicy(int numEdges) {
		this.numEdges = numEdges;
	}
}