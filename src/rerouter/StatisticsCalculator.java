package rerouter;

class StatisticsCalculator {
	private int numSucceed;
	private int numFailed;
	
	StatisticsCalculator() {
		newRound();
	}
	
	void addFlow(final boolean addedSuccessfully) {
		if (addedSuccessfully) {
			numSucceed += 1;
		} else {
			numFailed += 1;
		}
	}
	
	void newRound() {
		numSucceed = 0;
		numFailed = 0;
	}
	
	double fractionOfSuccess() {
		int totalFlows = numSucceed + numFailed;
		
		if (totalFlows > 0) {
			return ((double)numSucceed) / (totalFlows);
		} else {
			return 0;
		}
	}
	
	double fractionOfFailure() {
		return 1.0 - fractionOfSuccess();
	}
}