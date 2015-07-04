package experiment;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import reroute_network.DefaultPathRouterException;
import reroute_network.RerouteNetException;
import rerouter.PathRerouterException;
import flow_generator.DistributionException;

public class AdHocTest {
	public static void main(String[] args) 
			throws DistributionException, RerouteNetException, IOException, 
			PathRerouterException, DefaultPathRouterException {
		String[] arg = new String[] {
				"50",
				"20",
				"DONT_KEEP_DEFAULT",
				"BREAK_BEFORE_MAKE",
				"2",
				"10",
				"1",
				"5",
				"10",
				"true",
		};
		DoTest.main(arg);
		/*double a = Double.POSITIVE_INFINITY;
		a = a - a;
		System.out.println(a < -1);*/
	}
}