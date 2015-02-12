package parsing.parse_brite;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Vector;

import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.junit.Test;

import reroute_network.Vertex;
import reroute_network.Edge;

public class ParseBriteTest {
	
	@Test
	public void test() {
		Vector<Vertex> vertices = new Vector<Vertex>();
		SimpleDirectedWeightedGraph<Vertex,Edge> graph = null;
		final String filename =
				"config_files/test/parsing/parse_brite/tmp_net.brite";
		ParseBrite briteParser = null;
		try {
			 briteParser = new ParseBrite(filename);
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		graph = briteParser.getGraph(vertices);

		System.out.println(graph);
	}

}
