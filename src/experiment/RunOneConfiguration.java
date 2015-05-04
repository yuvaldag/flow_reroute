package experiment;

import java.util.PriorityQueue;
import java.util.Vector;

import flow_generator.FlowGenerator;
import flow_generator.FlowInfo;
import reroute_network.DefaultPathRouterException;
import reroute_network.RerouteNet;
import reroute_network.RerouteNetException;

public class RunOneConfiguration {
	final private double reroutePeriod;
	final private RerouteNet rerouteNet;
	final private FlowGenerator flowGen;
	final private PriorityQueue<Event> events;
	final private Vector<String> trace;
	final private Vector<Boolean> errors;
	private int nextFlowId;
	final private double timeToConsiderStat;
	private int sumChannels;
	private int numConsideredRerouteRounds;
	
	RunOneConfiguration(
			final RerouteNet rerouteNet,
			final FlowGenerator flowGen,
			final double reroutePeriod,
			final double totalTime,
			final double timeToConsiderStats,
			final boolean getTrace) {
		this.reroutePeriod = reroutePeriod;
		this.rerouteNet = rerouteNet;
		this.flowGen = flowGen;
		
		this.events = new PriorityQueue<Event>();
		this.events.add(new ExperimentEndEvent(totalTime));
		
		this.timeToConsiderStat = timeToConsiderStats;
		
		if (getTrace) {
			trace = new Vector<String>();
			errors = new Vector<Boolean>();
		} else {
			trace = null;
			errors = null;
		}
		
		this.nextFlowId = 0;
		
		this.sumChannels = 0;
		this.numConsideredRerouteRounds = 0;
	}
	
	private void addFlowStartEvent(double currentTime) {
		FlowInfo info = flowGen.generate();
		Event event = new FlowStartEvent(
				currentTime + info.delayTime,
				info.source,
				info.target,
				info.demand,
				info.duration,
				nextFlowId);
		nextFlowId += 1;
		events.add(event);
	}
	
	private void addRerouteEvent(double currentTime) {
		events.add(new RerouteEvent(reroutePeriod + currentTime));
	}

	private void addTrace(String str) {
		if (trace != null) {
			trace.addElement(str);
		}
	}
	
	private void addErr(boolean err) {
		if (errors != null) {
			errors.addElement(err);
		}
	}
	
	double run() throws RerouteNetException, DefaultPathRouterException {
		int flowsAddedSuccessfully = 0;
		int flowsFailedToAdd = 0;
		
		addFlowStartEvent(0);
		addRerouteEvent(0);
		
		while (true) {
			Event event = events.poll();
			
			addTrace(event.toString());
			
			if (event instanceof FlowStartEvent) {
				FlowStartEvent flowStart = (FlowStartEvent) event;
				boolean result = rerouteNet.addFlow(
						flowStart.id, flowStart.source, flowStart.target,
						flowStart.demand);
				
				if (result) {
					if (event.timestamp >= timeToConsiderStat) {
						flowsAddedSuccessfully += 1;
						addErr(false);
					}
					
					FlowEndEvent flowEnd = new FlowEndEvent(
							flowStart.timestamp + flowStart.duration,
							flowStart.id);
					events.add(flowEnd);
				} else {
					if (event.timestamp >= timeToConsiderStat) {
						flowsFailedToAdd += 1;
						addErr(true);
						addTrace("event failed to add");
					}
				} 
	
				addFlowStartEvent(flowStart.timestamp);
			} else if (event instanceof FlowEndEvent) {
				FlowEndEvent flowEnd = (FlowEndEvent) event;
				rerouteNet.removeFlow(flowEnd.id);
			} else if (event instanceof RerouteEvent) {
				rerouteNet.rerouteFlows();
				addRerouteEvent(event.timestamp);
				
				if (event.timestamp >= timeToConsiderStat) {
					this.sumChannels += rerouteNet.getNumChannels();
					this.numConsideredRerouteRounds += 1;
				}
			} else if (event instanceof ExperimentEndEvent) {
				break;
			} else {
				throw new RuntimeException(
						"Unexpected event type: " + event.getClass());
			}
		}
		
		return flowsFailedToAdd * 1.0 /
				(flowsAddedSuccessfully + flowsFailedToAdd);
	}
	
	public void printTrace() {
		for (String line : trace) {
			System.out.println(line);
		}
	}
	
	public void printErrs(int clusterLen) {
		int failed = 0;
		int total = 0;
		for (boolean err : errors) {
			if (err) {
				failed += 1;
			}
			
			total += 1;
			
			if (total % clusterLen == 0) {
				System.out.println("round: " + total + " err: " +
						((double)failed) / total);
			}
		}
	}
	
	public double getAvgChannels() {
		return ((double)this.sumChannels) / this.numConsideredRerouteRounds;
	}
}