package parsing.parse_brite;

import parsing.graph_creator.GraphCreator;
import parsing.graph_creator.GraphCreatorException;

import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import reroute_network.Vertex;
import reroute_network.Edge;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;


class BadFormatException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5038378988507176096L;
	
	BadFormatException() {
	}
	
	BadFormatException(String errorMsg) {
		super(errorMsg);
	}
}

public class ParseBrite {
	final GraphCreator graphCreator;
	
	private int getNumNodesOrEdges(Iterator<String> iter, String keyword)
			throws BadFormatException {
		while(iter.hasNext()) {
			String line = iter.next();
			String[] tokens = line.split("[ ]+");
			
			if(tokens.length == 0)
				continue;
			
			if(tokens[0].equals(keyword)) {
				if (tokens.length < 3)
					throw new BadFormatException("Not enough tokens in line");
				
				return Integer.parseInt(tokens[2]);
			}
		}
		
		throw new BadFormatException(
				"The line containing " + keyword + " does not appear");
	}
	
	private void addEdge(Iterator<String> iter) throws BadFormatException {
		if (!iter.hasNext())
			throw new BadFormatException("Not enough lines to read all edges");
		
		String line = iter.next();
		String[] tokens = line.split("[\t ]+");
		
		if (tokens.length < 6)
			throw new BadFormatException(
					"Not enough tokens in line containing edge. " +
					"Their number is: " + tokens.length + ". " +
					"The line equals: " + line);
		
		try {
			int source = Integer.parseInt(tokens[1]);
			int target = Integer.parseInt(tokens[2]);
			int capacity = Double.valueOf(tokens[5]).intValue();
			graphCreator.addEdge(source, target, capacity);
		} catch (NumberFormatException e) {
			throw new BadFormatException(e.getMessage());
		}
	}
	
	public ParseBrite(String filename) throws IOException {
		List<String> lines = Files.readAllLines(Paths.get(filename));
		Iterator<String> iter = lines.iterator();
		int numNodes = 0;
		int numEdges = 0;
		
		try {
			numNodes = getNumNodesOrEdges(iter, "Nodes:");
			graphCreator = new GraphCreator(numNodes);
			
			numEdges = getNumNodesOrEdges(iter, "Edges:");
			
			for(int i = 0; i < numEdges; i++) {
				addEdge(iter);
			}

		} catch (BadFormatException e) {
			throw new IOException(
					"Bad file format: " +
					filename +
					". " +
					e.getMessage());
		}
	}
	
	/*
	 * Gets the graph.
	 * 
	 * @param vertices		Output param to store all vertices
	 * @return				The parsed graph
	 */
	public SimpleDirectedWeightedGraph<Vertex,Edge> getGraph(
			Vector<Vertex> vertices) throws GraphCreatorException {
		return graphCreator.getGraph(vertices);
	}
}