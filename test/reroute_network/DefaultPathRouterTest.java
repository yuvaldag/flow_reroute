package reroute_network;

import static org.junit.Assert.*;

import org.junit.Test;

public class DefaultPathRouterTest {
	@Test
	public void testFindDefaultPath() {
		NetworkExample1 actual = new NetworkExample1();
		RerouteNet actualNet = new RerouteNet(
				actual.graph, new PathRerouter());
		NetworkExample1 expected = new NetworkExample1();

		try {
			assertTrue(actualNet.addFlow(0, actual.v1, actual.v2, 1));
		} catch (FlowExistsException | NegativeDemandException e) {
			fail();
		}
		expected.e12.usedCapacity += 1;
		assertTrue(
				NetworkExample.networkNonEqualsMsg(actual, expected),
				actual.equals(expected));

		try {
			assertFalse(actualNet.addFlow(
					1, actual.v1, actual.v2, 5));
		} catch (FlowExistsException | NegativeDemandException e) {
			e.printStackTrace();
			fail();
		}
		
		try {
			assertTrue(actualNet.addFlow(
					1, actual.v2, actual.v3, 2));
		} catch (FlowExistsException | NegativeDemandException e) {
			e.printStackTrace();
			fail();
		}
		expected.e21.usedCapacity += 2;
		expected.e13.usedCapacity += 2;
		assertTrue(
				NetworkExample.networkNonEqualsMsg(actual, expected),
				actual.equals(expected));
		
		try {
			assertFalse(actualNet.addFlow(2, actual.v1, actual.v3, 10));
		} catch (FlowExistsException | NegativeDemandException e) {
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
			actualNet.addFlow(0, actual.v1, actual.v2, 5);
		} catch (FlowExistsException | NegativeDemandException e) {
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