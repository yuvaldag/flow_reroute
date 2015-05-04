package experiment;

import java.io.IOException;

import reroute_network.DefaultPathRouterException;
import reroute_network.RerouteNetException;
import rerouter.PathRerouterException;
import flow_generator.DistributionException;

public class AdHocTest {
	public static void main(String[] args) 
			throws DistributionException, RerouteNetException, IOException, 
			PathRerouterException, DefaultPathRouterException {
		String a[] = {"200", "30", "KEEP_DEFAULT", "MAKE_BEFORE_BREAK", "2", "10", "1"};
		DoTest.main(a);
		a[2] = "DONT_KEEP_DEFAULT";
		DoTest.main(a);
		a[2] = "KEEP_DEFAULT_MOVE_ONCE";
		DoTest.main(a);
		a[3] = "BREAK_BEFORE_MAKE";
		DoTest.main(a);
	}
}