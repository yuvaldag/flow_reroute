package experiment;

import java.io.IOException;

import flow_generator.ConstantDistribution;
import flow_generator.DistributionException;
import flow_generator.ExpDistribution;
import flow_generator.FlowGenerator;
import flow_generator.ZipfDistribution;
import parsing.parse_brite.ParseBrite;
import reroute_network.GraphCreator;
import reroute_network.RerouteNet;
import reroute_network.RerouteNetException;
import rerouter.ExpWeightsRerouter;
import rerouter.PathRerouter;

public class DoTest {
	public static void main(String[] args) 
			throws DistributionException, RerouteNetException, IOException {
		GraphCreator creator = ParseBrite.parse(
				"config_files/test/parsing/parse_brite/tmp_net.brite");
		PathRerouter rerouter = new ExpWeightsRerouter(10.0);
		RerouteNet rerouteNet = new RerouteNet(creator, rerouter, 20);
		FlowGenerator flowGen = new FlowGenerator(
				creator.getNumVertices(),
				new ExpDistribution(12),
				new ZipfDistribution(2, 8.0, Double.POSITIVE_INFINITY),
				//new ConstantDistribution(25),

				new ZipfDistribution(2, 40.0, 1000),
				//new ConstantDistribution(100),

				45325903);

		RunOneConfiguration oneConfig = new RunOneConfiguration(
				rerouteNet, flowGen, 10.0, 500, true);
		double result = oneConfig.run();
		oneConfig.printTrace();
		System.out.println(result);
	}
}