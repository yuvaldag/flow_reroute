package reroute_network;

public class EdgeData {
	final int source;
	final int target;
	final int capacity;

	public EdgeData(final int source, final int target, final int capacity) {
		this.source = source;
		this.target = target;
		this.capacity = capacity;
	}
}