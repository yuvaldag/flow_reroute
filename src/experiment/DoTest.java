package experiment;

import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;

import flow_generator.DistributionException;
import flow_generator.ExpDistribution;
import flow_generator.FlowGenerator;
import flow_generator.UniformDistribution;
import flow_generator.ZipfDistribution;
import parsing.parse_brite.ParseBrite;
import reroute_network.DefaultPathRouterException;
import reroute_network.GraphCreator;
import reroute_network.RerouteNet;
import reroute_network.RerouteNetException;
import reroute_network.KeepDefaultPath;
import rerouter.PathRerouter;
import rerouter.PathRerouterException;
import rerouter.Policy;
import rerouter.SimpleExpWeightsRerouter;

public class DoTest {

	public static void main(String[] args)
			throws DistributionException, RerouteNetException, IOException,
					PathRerouterException, DefaultPathRouterException {

		int reroutePeriod = Integer.parseInt(args[0]);
		int durationFactor = Integer.parseInt(args[1]);

		KeepDefaultPath keepDefaultPath;
		
		if ("KEEP_DEFAULT".equals(args[2])) {
			keepDefaultPath = KeepDefaultPath.KEEP_DEFAULT;
		} else if ("DONT_KEEP_DEFAULT".equals(args[2])) {
			keepDefaultPath = KeepDefaultPath.DONT_KEEP_DEFAULT;
		} else if ("KEEP_DEFAULT_MOVE_ONCE".equals(args[2])) {
			keepDefaultPath = KeepDefaultPath.KEEP_DEFAULT_MOVE_ONCE;
		} else {
			throw new RuntimeException();
		}

		Policy policy;
		
		if ("MAKE_BEFORE_BREAK".equals(args[3])) {
			policy = Policy.MakeBeforeBreak;
		} else if ("BREAK_BEFORE_MAKE".equals(args[3])) {
			policy = Policy.BreakBeforeMake;
		} else {
			throw new RuntimeException();
		}
		
		int numGraph = Integer.parseInt(args[4]);
		double expParam = Double.parseDouble(args[5]);
		int reroutingsPerInvocation = Integer.parseInt(args[6]);
		
		oneTask(
				reroutePeriod,
				durationFactor,
				keepDefaultPath,
				policy,
				expParam,
				getFilename(numGraph),
				numGraph,
				reroutingsPerInvocation,
				false);
	}
	
	static String getFilename(int numGraph) {
		return "config_files/test/parsing/parse_brite/net" + numGraph + 
				".brite";
	}

	static void oneTask( 
			final int reroutePeriod,
			final double durationFactor,
			final KeepDefaultPath keepDefaultPath,
			final Policy policy,
			final double expParam,
			final String filename,
			final long seed,
			final int reroutingsPerInvocation,
			final boolean printDetails)
			
			throws DistributionException, RerouteNetException, IOException,
					PathRerouterException, DefaultPathRouterException {
		GraphCreator creator = ParseBrite.parse(
				filename, 5);
		PathRerouter rerouter =	new SimpleExpWeightsRerouter(expParam, policy);
		RerouteNet rerouteNet = new RerouteNet(
				creator,
				rerouter,
				reroutingsPerInvocation, 
				keepDefaultPath);
		
		FlowGenerator flowGen = new FlowGenerator(
				creator.getNumGenVertices(),
				new ExpDistribution(1),
				
				// duration
				new ZipfDistribution(2, 10., 100., durationFactor),

				// demand
				new UniformDistribution(1000.0, 3000.0),
				seed);

		RunOneConfiguration oneConfig = new RunOneConfiguration(
				rerouteNet,
				flowGen,
				reroutePeriod, 
				10000,
				7000, 
				false);
		
		double result = oneConfig.run();
		String caption = 
				"reroutePeriod: " +
				"durationFactor " +
				"filename " +
				"keepDefaultPath " +
				"policy " +
				"result " +
				"avgChannels " +
				"avgReroutingsPerFlow " +
				"expParam " +
				"reroutingsPerInvocation ";
		
		String output = 
				"" + reroutePeriod + " " + 
				durationFactor + " " + 
				filename + " " + 
				keepDefaultPath + " " +
				policy + " " +
				result + " " +
				oneConfig.getAvgChannels() + " " +
				rerouteNet.avgReroutingsPerFlow() + " " +
				expParam + " " +
				reroutingsPerInvocation;
		
		System.out.println(caption);
		System.out.println(output);
		
		if (printDetails) {
			System.out.println("Num rerouted");
			
			SortedMap<Integer, Integer> rerouteHist = 
					rerouteNet.getChannelHistogram();
			
			for (Map.Entry<Integer, Integer> entry : rerouteHist.entrySet()) {
				String histOutput = "" + entry.getKey() + " " + entry.getValue();
				System.out.println(histOutput);
			}
		}
	}
}