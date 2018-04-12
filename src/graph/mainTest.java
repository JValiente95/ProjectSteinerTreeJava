package graph;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class mainTest {

	public static void main(String[] args) {
		File[] files = readFiles(new File("data\\heuristics\\instance043.gr"));
		UndirectedGraph graph = new UndirectedGraphReader().read(files[0]);
		// PreProcess pp = new PreProcess(graph);
		// boolean[] preProcessable;
		// do{
		// preProcessable = pp.graph.preProcessable();
		// if(preProcessable[0]){
		// pp.removeLeafNodes();
		// }
		// if(preProcessable[1]){
		// pp.removeNonTerminalDegreeTwo();
		// }
		// } while (preProcessable[0] || preProcessable[1]);
		PreProcess processed = new PreProcess(graph);
		long starts = System.currentTimeMillis();

		// SteinerTreeSolver solver = new InvertedKruskal();
		// printSolution(solver.solve(pp.graph));
		// SteinerTreeSolver solver = new MobiusDynamics();
		// solver.solve(graph);
		processed.removeBridgesAndSections(graph.getVertices().size());

		// Below is used to create a file with same name in lp format
		// fileName = fileName.substring(fileName.indexOf("\\") + 1);
		// fileName = fileName.substring(fileName.indexOf("\\") + 1);
		// fileName = fileName.substring(0, fileName.indexOf("."));
		// CutILP fp = new CutILP(graph, fileName);
		// fp.initiateCutSearch();

		System.out.println("Took " + (System.currentTimeMillis() - starts) + " ms");
		// for (Vertex v : processed.graph.getVertices().values()) {
		// System.out.println("Vertex: " + v.getKey());
		// }
		// for (Edge v : processed.graph.getEdges()) {
		// System.out.println("Edge: " + v.getVertices()[0].getKey() + " " +
		// v.getVertices()[1].getKey() + " cost: " + v.getCost().get());
		// }
		System.out.println("Vertices from " + graph.getVertices().size() + " to " + processed.graph.getVertices().size());
		System.out.println("Edges from " + graph.getEdges().size() + " to " + processed.graph.getEdges().size());
		// SteinerTreeSolver solver = new MobiusDynamics();
		// solver.solve(graph);
		// ArrayList<Vertex[]> articulationBridges =
		// processed.articulationBridgeFinding(graph.getVertices().get(1),
		// graph.getVertices().size());
		//
		// System.out.println("Took " + (System.currentTimeMillis() - starts) + " ms");
		// for (Vertex[] v : articulationBridges) {
		// if (v.length == 2) {
		// System.out.println(v[0].getKey() + " " + v[1].getKey());
		// } else
		// System.out.println(v[0].getKey());
		// }
		// doAnalysis(files);
	}

	/**
	 * Prints solution to standard out. Checks each edge and vertex to see if it
	 * contains other hidden edges and or vertices that need to be included
	 * 
	 * @param solution
	 *            Solution including all the edges in the solution
	 */
	private static void printSolution(List<Edge> solution) {
		String temp = "";
		int sum = 0;
		int[] subsumed;
		for (int i = 0; i < solution.size(); i++) {
			if (!(solution.get(i).getVertices()[0].getSubsumed() == null)) {
				while (!solution.get(i).getVertices()[0].getSubsumed().isEmpty()) {
					subsumed = solution.get(i).getVertices()[0].getSubsumed().pop();
					temp = temp.concat(subsumed[0] + " " + subsumed[1]);
					sum += subsumed[2];
				}
			}
			if (!(solution.get(i).getVertices()[1].getSubsumed() == null)) {
				while (!solution.get(i).getVertices()[1].getSubsumed().isEmpty()) {
					subsumed = solution.get(i).getVertices()[1].getSubsumed().pop();
					temp = temp.concat(subsumed[0] + " " + subsumed[1]);
					sum += subsumed[2];
				}
			}
			if (!(solution.get(i).getStack() == null)) {
				while (!solution.get(i).getStack().isEmpty()) {
					subsumed = solution.get(i).getStack().pop();
					temp = temp.concat(subsumed[0] + " " + subsumed[1]);
					sum += subsumed[2];
				}
			}
			temp = temp.concat(solution.get(i).getVertices()[0].getKey() + " " + solution.get(i).getVertices()[1].getKey() + "\n");
			sum += solution.get(i).getCost().get();
		}
		System.out.println("VALUE " + sum);
		System.out.println(temp);
	}

	/**
	 * Perform analysis
	 *
	 * @param files
	 *
	 * @author Marciano Geijselaers
	 * @author Joshua Scheidt
	 */
	private static void doAnalysis(File[] files) {

		Integer[][][] results = new Integer[files.length][5][4]; // Per file, save all different graphs' Nodes, Terminals and Edges. The second
																	// index has to be changed depending on which comparisons we want. The first
																	// index will always be the base graph without preprocess changes.

		for (int fileIndex = 0; fileIndex < files.length; fileIndex++) {
			// Read the standard graph and set its values to the first index of the results.
			Long start, end;
			start = System.currentTimeMillis();
			UndirectedGraph graph = new UndirectedGraphReader().read(files[fileIndex]);
			end = System.currentTimeMillis();
			results[fileIndex][0][0] = graph.getVertices().size();
			results[fileIndex][0][1] = graph.getNumberOfTerminals();
			results[fileIndex][0][2] = graph.getEdges().size();
			results[fileIndex][0][3] = (int) (end - start);

			// Create a cloned graph which will use preprocessing.
			PreProcess improved = new PreProcess(graph);
			printCurrentSize(improved);

			// Prints the degrees of the vertices from the original graph up to a maximum of
			// degree 9.
			System.out.println("Original Degree Scale: ");
			int[] degrees = graph.countDegree();
			for (int i = 0; i < degrees.length; i++) {
				System.out.print(degrees[i] + ", ");
			}
			System.out.println("");

			// Leaf Node Removal
			start = System.currentTimeMillis();
			improved.removeLeafNodes();
			end = System.currentTimeMillis();
			results[fileIndex][1][0] = improved.graph.getVertices().size();
			results[fileIndex][1][1] = improved.graph.getNumberOfTerminals();
			results[fileIndex][1][2] = improved.graph.getEdges().size();
			results[fileIndex][1][3] = (int) (end - start);

			// Remove non-degree terminal
			start = System.currentTimeMillis();
			improved.removeNonTerminalDegreeTwo();
			end = System.currentTimeMillis();
			results[fileIndex][2][0] = improved.graph.getVertices().size();
			results[fileIndex][2][1] = improved.graph.getNumberOfTerminals();
			results[fileIndex][2][2] = improved.graph.getEdges().size();
			results[fileIndex][2][3] = (int) (end - start);

			// Iterative part here
			start = System.currentTimeMillis();
			boolean[] keepPreProcessing = improved.graph.preProcessable();
			while (keepPreProcessing[0] || keepPreProcessing[1]) {
				if (keepPreProcessing[0]) {
					improved.removeLeafNodes();
				}
				if (keepPreProcessing[1]) {
					improved.removeNonTerminalDegreeTwo();
				}
				keepPreProcessing = improved.graph.preProcessable();
			}
			end = System.currentTimeMillis();
			results[fileIndex][3][0] = improved.graph.getVertices().size();
			results[fileIndex][3][1] = improved.graph.getNumberOfTerminals();
			results[fileIndex][3][2] = improved.graph.getEdges().size();
			results[fileIndex][3][3] = (int) (end - start);

			// Bridge Finding
			start = System.currentTimeMillis();
			// improved.removeBridgesAndSections(graph.getVertices().size());
			end = System.currentTimeMillis();
			results[fileIndex][4][0] = graph.getVertices().size();
			results[fileIndex][4][1] = graph.getNumberOfTerminals();
			results[fileIndex][4][2] = graph.getEdges().size();
			results[fileIndex][4][3] = (int) (end - start);

			System.out.println("done");

			// Leaf Node Removal
			// improved.removeLeafNodes();
			//
			// // Non-Terminal Degree Two removal
			// long start = System.nanoTime();
			// improved.removeNonTerminalDegreeTwo();
			// long stop = System.nanoTime();
			// System.out.println("Time Taken: " + (stop - start) / 1000000000.0);
			//
			// printCurrentSize(improved);
			// printDegreeScale(improved);
			//
			// improved.removeLeafNodes();
			//
			printCurrentSize(improved);
			printDegreeScale(improved);
			//
			// improved.removeNonTerminalDegreeTwo();
		}
		System.out.println("\n\nTotal results:");
		double[] percentileReductionVertices = new double[results.length];
		double[] percentileReductionEdges = new double[results.length];
		double[] percentileReductionTerminals = new double[results.length];
		double[] averageTimeTaken = new double[results.length];
		int counter = 0;
		for (Integer[][] singleFileResults : results) {
			System.out.println(Arrays.toString(singleFileResults[0]));
			percentileReductionVertices[counter] = ((double) (singleFileResults[0][0] - singleFileResults[3][0])) / (double) singleFileResults[0][0];
			percentileReductionTerminals[counter] = ((double) (singleFileResults[0][1] - singleFileResults[3][1])) / (double) singleFileResults[0][1];
			percentileReductionEdges[counter] = ((double) (singleFileResults[0][2] - singleFileResults[3][2])) / (double) singleFileResults[0][2];
			averageTimeTaken[counter] = singleFileResults[1][3] + singleFileResults[2][3] + singleFileResults[3][3];
			counter++;
		}
		System.out.println(Arrays.toString(percentileReductionVertices));
		System.out.println(Arrays.toString(percentileReductionEdges));
		System.out.println(Arrays.toString(percentileReductionTerminals));
		System.out.println(Arrays.toString(averageTimeTaken));
	}

	/**
	 * Reads all files from a given directory. The directory is allowed to be both a
	 * folder or a file.
	 *
	 * @param directory
	 *            The directory for which file(s) have to be read.
	 * @return An array of found files.
	 *
	 * @author Joshua Scheidt
	 */
	private static File[] readFiles(File directory) {
		if (directory.exists()) {
			if (directory.isFile() && directory.getName().contains(".gr")) {
				return new File[] { directory };
			} else if (directory.isDirectory()) {
				File[] files = directory.listFiles();
				ArrayList<File> filesList = new ArrayList<>();
				for (int i = 0; i < files.length; i++) {
					if (files[i].isFile() && files[i].getName().contains(".gr")) {
						filesList.add(files[i]);
					}
				}
				return filesList.toArray(files);
			}
		}
		return new File[] {};
	}

	public static void printCurrentSize(PreProcess improved) {
		System.out.println("Current number of (Vertices, Terminals): (" + improved.graph.getVertices().size() + ", "
				+ improved.graph.getNumberOfTerminals() + ") Current number of Edges: " + improved.graph.getEdges().size());
	}

	public static void printDegreeScale(PreProcess improved) {
		System.out.println("Current Degree Scale: ");
		int[] clonedDegrees = improved.graph.countDegree();
		for (int i = 0; i < clonedDegrees.length; i++) {
			System.out.print(clonedDegrees[i] + ", ");
		}
		System.out.println("");
	}

}
