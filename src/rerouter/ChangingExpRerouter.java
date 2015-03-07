package rerouter;

import reroute_network.Edge;

public class ChangingExpRerouter extends ExpWeightsRerouter {
	private final double STARTING_EXP_PARAM = 8.0;
	private final double STARTING_CONST = 1.0;
	
	protected final double[] constants;
	protected final double[] expParams;
	
	public ChangingExpRerouter(int numEdges) 
			throws PathRerouterException {
		super();
		
		constants = new double[numEdges];
		expParams = new double[numEdges];
		
		for (int i = 0; i < numEdges; i++) {
			constants[i] = STARTING_CONST;
			expParams[i] = STARTING_EXP_PARAM;
		}
	}
	
	@Override
	double edgeCostMultiplier(Edge edge) {
		if (constants[edge.getId()] >= 0) {
			return constants[edge.getId()];
		} else {
			return 0;
		}
	}
	
	@Override
	double edgeExpParam(Edge edge) {
		if (expParams[edge.getId()] > 0) {
			return expParams[edge.getId()];
		} else {
			return 0;
		}
	}
	
	@Override
	public void printParams() {
		for (int i = 0; i < constants.length; i++) {
			System.out.println(
					"edge: " + i + ", constant: " + constants[i] + 
					", expParam: " + expParams[i]);
		}
	}
}