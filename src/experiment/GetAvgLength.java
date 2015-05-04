package experiment;

import java.io.IOException;

import parsing.parse_brite.ParseBrite;
import reroute_network.GraphCreator;
import reroute_network.KeepDefaultPath;
import reroute_network.RerouteNet;
import rerouter.PathRerouter;
import rerouter.PathRerouterException;
import rerouter.Policy;
import rerouter.SimpleExpWeightsRerouter;

public class GetAvgLength {
	public static void main(String[] args) throws IOException, PathRerouterException {
		final double num = 100;
		double sum = 0;
		
		for (int i = 1; i <= num; i++) {
			GraphCreator creator = ParseBrite.parse(
					DoTest.getFilename(i), 5);
			PathRerouter rerouter =	new SimpleExpWeightsRerouter(
					10.0, Policy.MakeBeforeBreak);
			RerouteNet rerouteNet = new RerouteNet(
					creator,
					rerouter,
					1,
					KeepDefaultPath.KEEP_DEFAULT);
			sum += rerouteNet.getAvgDefaultChanngelLength();
		}

		final double result = ((double)sum) / num;
		System.out.println(result);
	}
}
