package reroute_network;

import static org.junit.Assert.*;

import org.junit.Test;

public class ExpWeightsRerouterTest {
	@SuppressWarnings("unused")
	private double calculateCost(
			ExpWeightsRerouter rerouter, Iterable<Edge> edges) {
		double sum = 0;
		
		for(Edge edge : edges) {
			sum += rerouter.costFunction(edge.usedCapacity, edge.capacity);
		}
		
		return sum;
	}
	
	@Test
	/*
	 * It should test:
	 * 		- What happends when we can reroute 2 flows, and request that
	 * 		- That rerouting when we should goes fine
	 * 		- That trying to reroute when we shouldn't goes fine
	 * 		- Check what happends when there are 2 reroute possibilities
	 */
	public void testReroute() {
		ExpWeightsRerouter rerouter = new ExpWeightsRerouter(1, 10.0);
		NetworkExample1 expected = new NetworkExample1();
		NetworkExample1 actual = new NetworkExample1();
		RerouteNet actualNet = new RerouteNet(actual.graph, rerouter);
		try {
			expected.e12.usedCapacity += 1;
			actualNet.addFlow(0, actual.v1, actual.v2, 1);
			actualNet.rerouteFlows();
			assertTrue(
					NetworkExample.networkNonEqualsMsg(actual, expected),
					expected.equals(actual));
			
			expected.e13.usedCapacity += 1;
			expected.e32.usedCapacity += 1;
			actualNet.addFlow(1, actual.v1, actual.v2, 1);
			actualNet.rerouteFlows();
			assertTrue(
					NetworkExample.networkNonEqualsMsg(actual, expected),
					expected.equals(actual));
			
			expected.e21.usedCapacity += 1;
			expected.e12.usedCapacity += 1;
			actualNet.addFlow(2, actual.v2, actual.v1, 1);
			actualNet.addFlow(3, actual.v1, actual.v2, 1);
			actualNet.rerouteFlows();
			assertTrue(
					NetworkExample.networkNonEqualsMsg(actual, expected),
					expected.equals(actual));
			
			expected.e12.usedCapacity += 1;
			actualNet.addFlow(4, actual.v1, actual.v2, 1);
			expected.e13.usedCapacity += 1;
			expected.e34.usedCapacity += 1;
			expected.e42.usedCapacity += 1;
			actualNet.addFlow(5, actual.v1, actual.v2, 1);
			expected.e21.usedCapacity += 1;
			actualNet.addFlow(6, actual.v2, actual.v1, 1);
			actualNet.rerouteFlows();
			assertTrue(
					NetworkExample.networkNonEqualsMsg(actual, expected),
					expected.equals(actual));
			
			expected.e21.usedCapacity -= 1;
			expected.e23.usedCapacity += 1;
			expected.e31.usedCapacity += 1;
			actualNet.rerouteFlows();
			assertTrue(
					NetworkExample.networkNonEqualsMsg(actual, expected),
					expected.equals(actual));
		} catch (FlowExistsException | NegativeDemandException e) {
			fail();
		}
	}

}