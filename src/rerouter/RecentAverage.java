package rerouter;

public class RecentAverage {
	private final double[] numbers;
	private int numElements;
	private double sum;
	private int nextElement;
	
	RecentAverage(final int numToAvg) {
		numbers = new double[numToAvg];
		for (int i = 0; i < numbers.length; i++) {
			numbers[i] = 0;
		}
		
		numElements = 0;
		sum = 0;
		nextElement = 0;
	}
	
	void addNum(final double num) {
		sum -= numbers[nextElement];
		sum += num;
		
		numbers[nextElement] = num;
		
		if (numElements < numbers.length) {
			numElements += 1;
		}
		
		nextElement = (nextElement + 1) % numbers.length;
	}
	
	double getAvg() {
		if (numElements == 0) {
			return 0;
		}

		return sum / numElements;
	}
}
