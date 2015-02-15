package experiment;

abstract class Event implements Comparable<Event> {
	final double timestamp;
	
	Event(double timestamp) {
		this.timestamp = timestamp;
	}
	
	public int compareTo(Event event) {
		Double thisTime = timestamp;
		Double eventTime = event.timestamp;
		
		return thisTime.compareTo(eventTime);
	}
}

class FlowStartEvent extends Event {
	final int source;
	final int target;
	final int demand;
	final double duration;
	final int id;
	
	FlowStartEvent(
			final double timestamp,
			final int source,
			final int target,
			final int demand,
			final double duration,
			int id) {
		super(timestamp);
		this.source = source;
		this.target = target;
		this.demand = demand;
		this.duration = duration;
		this.id = id;
	}
}

class FlowEndEvent extends Event {
	final int id;
	
	FlowEndEvent(final double timestamp, final int id) {
		super(timestamp);
		this.id = id;
	}
}

class RerouteEvent extends Event {
	RerouteEvent(final double timestamp) {
		super(timestamp);
	}
}

class ExperimentEndEvent extends Event {
	ExperimentEndEvent(final double timestamp) {
		super(timestamp);
	}
}