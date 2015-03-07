package rerouter;

import static org.junit.Assert.*;

import org.junit.Test;

public class RecentAverageTest {

	@Test
	public void testRecentAvg() {
		RecentAverage avg = new RecentAverage(3);
		
		assertTrue(avg.getAvg() == 0.0);
		
		avg.addNum(1.0);
		assertTrue(avg.getAvg() == 1.0);
		
		avg.addNum(2.0);
		assertTrue(avg.getAvg() == 1.5);
		
		avg.addNum(3.0);
		assertTrue(avg.getAvg() == 2.0);
		
		avg.addNum(4.0);
		assertTrue(avg.getAvg() == 3.0);
		
		avg.addNum(5.0);
		assertTrue(avg.getAvg() == 4.0);
		
		avg.addNum(6.0);
		assertTrue(avg.getAvg() == 5.0);
		
		avg.addNum(7.0);
		assertTrue(avg.getAvg() == 6.0);
	}
}