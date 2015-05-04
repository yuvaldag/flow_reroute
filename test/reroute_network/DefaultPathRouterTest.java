package reroute_network;

import static org.junit.Assert.*;

import org.junit.Test;

import rerouter.PathRerouter;

public class DefaultPathRouterTest {
	@Test
	public void testFindDefaultPath() {
		NetworkExample1 actual = new NetworkExample1();
		RerouteNet actualNet = new RerouteNet(
				new GraphData(actual.graph, actual.vertices),
				new PathRerouter(),
				0,
				true);
		NetworkExample1 expected = new NetworkExample1();

		try {
			assertTrue(actualNet.addFlow(0, 1, 2, 1));
		} catch (RerouteNetException e) {
			e.printStackTrace();
			fail();
		} catch (DefaultPathRouterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
		expected.e12.usedCapacity += 1;
		assertTrue(
				NetworkExample.networkNonEqualsMsg(actual, expected),
				actual.equals(expected));

		try {
			assertFalse(actualNet.addFlow(1, 1, 2, 5));
		} catch (RerouteNetException e) {
			e.printStackTrace();
			fail();
		} catch (DefaultPathRouterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}		
		try {
			assertTrue(actualNet.addFlow(1, 2, 3, 2));
		} catch (RerouteNetException e) {
			e.printStackTrace();
			fail();
		} catch (DefaultPathRouterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}		expected.e21.usedCapacity += 2;
		expected.e13.usedCapacity += 2;
		assertTrue(
				NetworkExample.networkNonEqualsMsg(actual, expected),
				actual.equals(expected));
		
		try {
			assertFalse(actualNet.addFlow(2, 1, 3, 10));
		} catch (RerouteNetException e) {
			e.printStackTrace();
			fail();
		} catch (DefaultPathRouterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
		try {
			actualNet.removeFlow(0);
		} catch (FlowNotExistsException e) {
			e.printStackTrace();
			fail();
		}
		expected.e12.usedCapacity -= 1;
		assertTrue(
				NetworkExample.networkNonEqualsMsg(actual, expected),
				actual.equals(expected));

		try {
			actualNet.addFlow(0, 1, 2, 5);
		} catch (RerouteNetException e) {
			e.printStackTrace();
			fail();
		} catch (DefaultPathRouterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
		expected.e12.usedCapacity += 5;
		assertTrue(
				NetworkExample.networkNonEqualsMsg(actual, expected),
				actual.equals(expected));

		try {
			actualNet.removeFlow(1);
		} catch (FlowNotExistsException e) {
			e.printStackTrace();
			fail();
		}
		expected.e21.usedCapacity -= 2;
		expected.e13.usedCapacity -= 2;
		assertTrue(
				NetworkExample.networkNonEqualsMsg(actual, expected),
				actual.equals(expected));
	}
}