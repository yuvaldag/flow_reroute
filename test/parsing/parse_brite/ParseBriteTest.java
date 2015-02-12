package parsing.parse_brite;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Vector;

import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.junit.Test;

import parsing.graph_creator.GraphCreatorException;
import reroute_network.Vertex;
import reroute_network.Edge;

public class ParseBriteTest {

	//@Test
	public void justTest() {
		String str = "1   2   3";
		String splitted[] = str.split("[ ]+");
		System.out.println(splitted.length);
	}
	
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
		
		try {
			graph = briteParser.getGraph(vertices);
		} catch (GraphCreatorException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		System.out.println(graph);
	}

}
