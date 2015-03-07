package experiment;

import java.io.IOException;

import flow_generator.DistributionException;
import flow_generator.ExpDistribution;
import flow_generator.FlowGenerator;
import flow_generator.ZipfDistribution;
import parsing.parse_brite.ParseBrite;
import reroute_network.DefaultPathRouterException;
import reroute_network.GraphCreator;
import reroute_network.RerouteNet;
import reroute_network.RerouteNetException;
import rerouter.AdjustingRerouter;
import rerouter.NonumiformMultiplierCostRerouter;
import rerouter.PathRerouter;
import rerouter.PathRerouterException;
import rerouter.RLRerouter;
import rerouter.SimpleExpWeightsRerouter;

public class DoTest {
	public static void main(String[] args) 
			throws DistributionException, RerouteNetException, IOException,
					PathRerouterException, DefaultPathRouterException {
		GraphCreator creator = ParseBrite.parse(
				"config_files/test/parsing/parse_brite/tmp_net.brite");
		PathRerouter rerouter = 
				//new RLRerouter(
				//		.0, 0.95, 0., creator.getNumEdges());
				//new SimpleExpWeightsRerouter(8.0);
				new AdjustingRerouter(10, 0.05, 0.1, 10, creator.getNumEdges());
		RerouteNet rerouteNet = new RerouteNet(creator, rerouter, 3);
		FlowGenerator flowGen = new FlowGenerator(
				creator.getNumVertices(),
				new ExpDistribution(20),
				
				// duration
				new ZipfDistribution(2, 8.0, 150),
				
				// demand
				new ZipfDistribution(2, 20., 500.1),
				453259503);

		RunOneConfiguration oneConfig = new RunOneConfiguration(
				rerouteNet, flowGen, 5.0, 15000, true);
		double result = oneConfig.run();
		//oneConfig.printTrace();
		//rerouter.printMultipliers();
		oneConfig.printErrs(200);
		rerouter.printParams();
		System.out.println(result);
	}
}