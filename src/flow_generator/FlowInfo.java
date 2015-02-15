package flow_generator;

public class FlowInfo {
	public final int source;
	public final int target;
	public final int demand;
	public final double delayTime;
	public final double duration;
	
	FlowInfo(final int source, final int target, final int demand,
			final double delayTime, final double duration) {
		this.source = source;
		this.target = target;
		this.demand = demand;
		this.delayTime = delayTime;
		this.duration = duration;
	}
}
