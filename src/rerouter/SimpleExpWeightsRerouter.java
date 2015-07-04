package rerouter;

import reroute_network.Edge;

public class SimpleExpWeightsRerouter extends ExpWeightsRerouter {
	private final double expParam;
	
	public SimpleExpWeightsRerouter(
			double expParam, 
			Policy policy,
			TopChannelsPolicy topChannelsPolicy,
			LimitedChannelsPolicy randomChannelsPolicy,
			long seed,
			final boolean oldArticle)
					throws PathRerouterException {
		super(policy, topChannelsPolicy, randomChannelsPolicy, seed, 
				oldArticle);
		
		if (expParam <= 0.0) {
			throw new PathRerouterException("Exp param has to be nonnegative" +
					" but its value was " + expParam);
		}
				
		this.expParam = expParam;
	}

	@Override
	double edgeCostMultiplier(Edge edge) {
		return 1.0;
	}

	@Override
	double edgeExpParam(Edge edge) {
		return expParam;
	}

}
