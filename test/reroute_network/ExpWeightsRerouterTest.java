package reroute_network;

import static org.junit.Assert.*;

import org.junit.Test;

import rerouter.ExpWeightsRerouter;
import rerouter.PathRerouterException;
import rerouter.SimpleExpWeightsRerouter;

public class ExpWeightsRerouterTest {
	
	@Test
	/*
	 * It should test:
	 * 		- What happens when we can reroute 2 flows, and request that
	 * 		- That re-routing when we should goes fine
	 * 		- That trying to reroute when we shouldn't goes fine
	 * 		- Check what happens when there are 2 reroute possibilities
	 */
	public void testReroute() throws PathRerouterException {
		ExpWeightsRerouter rerouter = new SimpleExpWeightsRerouter(10.0);
		NetworkExample1 expected = new NetworkExample1();
		NetworkExample1 actual = new NetworkExample1();
		RerouteNet actualNet = new RerouteNet(
				new GraphData(actual.graph, actual.vertices),
				rerouter,
				1,
				true);
		try {
			expected.e12.usedCapacity += 1;
			actualNet.addFlow(0, 1, 2, 1);
			actualNet.rerouteFlows();
			assertTrue(
					NetworkExample.networkNonEqualsMsg(actual, expected),
					expected.equals(actual));
			
			expected.e13.usedCapacity += 1;
			expected.e32.usedCapacity += 1;
			actualNet.addFlow(1, 1, 2, 1);
			actualNet.rerouteFlows();
			assertTrue(
					NetworkExample.networkNonEqualsMsg(actual, expected),
					expected.equals(actual));
			
			expected.e21.usedCapacity += 1;
			expected.e12.usedCapacity += 1;
			actualNet.addFlow(2, 2, 1, 1);
			actualNet.addFlow(3, 1, 2, 1);
			actualNet.rerouteFlows();
			assertTrue(
					NetworkExample.networkNonEqualsMsg(actual, expected),
					expected.equals(actual));
			
			expected.e12.usedCapacity += 1;
			actualNet.addFlow(4, 1, 2, 1);
			expected.e13.usedCapacity += 1;
			expected.e34.usedCapacity += 1;
			expected.e42.usedCapacity += 1;
			actualNet.addFlow(5, 1, 2, 1);
			expected.e21.usedCapacity += 1;
			actualNet.addFlow(6, 2, 1, 1);
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
		} catch (RerouteNetException e) {
			e.printStackTrace();
			fail();
		} catch (DefaultPathRouterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}	
	}

	@Test
	public void testRerouteMultipleFlows() throws PathRerouterException {
		// TODO: add a test for re-routing multiple flows at once
		// TODO: see what happens when the demands are greater than one 
		ExpWeightsRerouter rerouter = new SimpleExpWeightsRerouter(10.0);
		NetworkExample1 expected = new NetworkExample1();
		NetworkExample1 actual = new NetworkExample1();
		RerouteNet actualNet = new RerouteNet(
				new GraphData(actual.graph, actual.vertices),
				rerouter,
				2,
				true);
		try {
			actualNet.addFlow(0, 1, 2, 2);
			actualNet.addFlow(1, 1, 2, 1);
			actualNet.addFlow(2, 1, 2, 1);
			actualNet.addFlow(3, 1, 2, 1);
			actualNet.rerouteFlows();
		} catch (RerouteNetException e) {
			e.printStackTrace();
			fail();
		} catch (DefaultPathRouterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}		
		expected.e12.usedCapacity += 2;
		
		expected.e13.usedCapacity += 2;
		expected.e32.usedCapacity += 2;
		
		expected.e13.usedCapacity += 1;
		expected.e34.usedCapacity += 1;
		expected.e42.usedCapacity += 1;
		
		assertTrue(
				NetworkExample.networkNonEqualsMsg(actual, expected),
				expected.equals(actual));
		
		try {
			actualNet.rerouteFlows();
		} catch (RerouteNetException e) {
			e.printStackTrace();
			fail();
		}		
		assertTrue(
				NetworkExample.networkNonEqualsMsg(actual, expected),
				expected.equals(actual));
	}
}