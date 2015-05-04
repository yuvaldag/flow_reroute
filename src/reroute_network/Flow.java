package reroute_network;

public class Flow {
	final int demand;
	final Channel channel;
	final int numReroutingChannelHad;
	
	Flow(Channel channel, int demand, final int numReroutingChannelHad) {
		this.channel = channel;
		this.demand = demand;
		this.numReroutingChannelHad = numReroutingChannelHad;
	}
	
	public int getDemand() {
		return demand;
	}
}