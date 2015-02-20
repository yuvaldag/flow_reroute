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
import rerouter.ExpWeightsRerouter;
import rerouter.NonumiformMultiplierCostRerouter;
import rerouter.PathRerouter;
import rerouter.PathRerouterException;

public class DoTest {
	public static void main(String[] args) 
			throws DistributionException, RerouteNetException, IOException,
					PathRerouterException, DefaultPathRouterException {
		GraphCreator creator = ParseBrite.parse(
				"config_files/test/parsing/parse_brite/tmp_net.brite");
		NonumiformMultiplierCostRerouter rerouter = 
				new NonumiformMultiplierCostRerouter(
						5.0, 0., 0.0, creator.getNumEdges());
				//new ExpWeightsRerouter(8.0);
		RerouteNet rerouteNet = new RerouteNet(creator, rerouter, 0);
		FlowGenerator flowGen = new FlowGenerator(
				creator.getNumVertices(),
				new ExpDistribution(20),
				
				// duration
				new ZipfDistribution(2, 8.0, 150),
				
				// demand
				new ZipfDistribution(2, 20., 500.1),
				45325903);

		RunOneConfiguration oneConfig = new RunOneConfiguration(
				rerouteNet, flowGen, 5.0, 50000, true);
		double result = oneConfig.run();
		//oneConfig.printTrace();
		//rerouter.printMultipliers();
		oneConfig.printErrs(2000);
		System.out.println(result);
	}
}