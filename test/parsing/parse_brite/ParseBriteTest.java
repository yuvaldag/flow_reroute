package parsing.parse_brite;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import reroute_network.GraphCreator;

public class ParseBriteTest {
	
	@Test
	public void test() {
		final String filename =
				"config_files/test/parsing/parse_brite/tmp_net.brite";
		GraphCreator creator = null;
		
		try {
			 creator = ParseBrite.parse(filename);
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		System.out.println(creator);
	}

}
