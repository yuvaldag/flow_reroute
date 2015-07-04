package parsing.parse_brite;

import reroute_network.EdgeData;
import reroute_network.GraphCreator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedList;
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

public final class ParseBrite {
	
	private ParseBrite() {
	}
	
	private static int getNumNodesOrEdges(Iterator<String> iter, String keyword)
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
	
	private static void addEdge(Iterator<String> iter, Vector<EdgeData> edges)
			throws BadFormatException {
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
			edges.add(new EdgeData(source, target, capacity));
			edges.add(new EdgeData(target, source, capacity));
		} catch (NumberFormatException e) {
			throw new BadFormatException(e.getMessage());
		}
	}

	public static GraphCreator parse(String filename) throws IOException {
		return parse(filename, 1);
	}
	
	public static GraphCreator parse(
			String filename,
			int generatingVerticesVsOther) 
					throws IOException {
		Vector<EdgeData> allEdgeData = new Vector<EdgeData>();
		List<String> lines = new LinkedList<String>();//
		//List<String> lines = Files.readAllLines(Paths.get(filename));
		
		BufferedReader br = new BufferedReader(new FileReader(filename));
	    String line;
	    while ((line = br.readLine()) != null) {
	       lines.add(line);
	    }
	    br.close();
		
		Iterator<String> iter = lines.iterator();
		int numNodes = 0;
		int numEdges = 0;
		
		try {
			numNodes = getNumNodesOrEdges(iter, "Nodes:");
			numEdges = getNumNodesOrEdges(iter, "Edges:");
			
			for(int i = 0; i < numEdges; i++) {
				addEdge(iter, allEdgeData);
			}

		} catch (BadFormatException e) {
			throw new IOException(
					"Bad file format: " +
					filename +
					". " +
					e.getMessage());
		}

		return new GraphCreator(
				numNodes, allEdgeData, generatingVerticesVsOther);
	}
}