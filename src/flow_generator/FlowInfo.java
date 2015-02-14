package flow_generator;

public class FlowInfo {
	final int source;
	final int target;
	final double delayTime;
	final double duration;
	
	FlowInfo(final int source, final int target, final double delayTime,
				final double duration) {
		this.source = source;
		this.target = target;
		this.delayTime = delayTime;
		this.duration = duration;
	}
}
