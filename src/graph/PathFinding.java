/*
 * All of the given code may be used on free will when referenced to the source.
 * Initial version created at 14:13:39
 */
package graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Stack;

/**
 * This class provides Path finding techniques which can be utilised in the
 * graph.
 *
 * 
 * @author Joshua Scheidt
 */
public class PathFinding {

	public static class DijkstraInfo {
		public int dist;
		public Vertex parent = null;

		public DijkstraInfo(int dist) {
			this.dist = dist;
		}
	}

	/**
	 * Performs Dijkstra's path finding algorithm and returns the new edge between
	 * the vertices.
	 *
	 * @param G
	 *            The graph in which Dijkstra has to be performed
	 * @param start
	 *            The starting vertex
	 * @param end
	 *            The endpoint vertex
	 * @return The new edge with the lowest weight
	 *
	 * @author Joshua Scheidt
	 */
	public static Edge DijkstraSingleEdge(UndirectedGraph G, Vertex start, Vertex end) {
		ArrayList<Vertex> Q = new ArrayList<>();
		HashMap<Integer, DijkstraInfo> datamap = new HashMap<>();
		for (Vertex i : G.getVertices().values()) {
			datamap.put(i.getKey(), new DijkstraInfo(Integer.MAX_VALUE));
			Q.add(i);
		}
		// System.out
		// .println("Start:" + start.getKey() + " End:" + end.getKey() + " G(V):" +
		// G.getVertices().size() + " G(E):" + G.getEdges().size());
		datamap.get(start.getKey()).dist = 0;

		boolean reachedEnd = false;

		// System.out.println("In here");

		while (!Q.isEmpty()) {
			int smallestDist = Integer.MAX_VALUE;
			Vertex current = null;
			for (Vertex i : Q) {
				if (datamap.get(i.getKey()).dist < smallestDist) {
					current = i;
					smallestDist = datamap.get(i.getKey()).dist;
				}
			}
			if (reachedEnd && smallestDist > datamap.get(end.getKey()).dist)
				break;
			if (current == null)
				System.out.println("ERROR: No shortest distance vertex found with distance < INTEGER.MAX_VALUE");
			if (current == end)
				reachedEnd = true;
			Q.remove(current);
			int distToCur = datamap.get(current.getKey()).dist;
			int totDistToNb = 0;
			for (Vertex nb : current.getNeighbors()) {
				totDistToNb = distToCur + current.getConnectingEdge(nb).getCost().get();
				DijkstraInfo nbInfo = datamap.get(nb.getKey());
				if (nbInfo == null)
					System.out.println(nb.getKey() + " ???");
				if (totDistToNb < nbInfo.dist) {
					nbInfo.dist = totDistToNb;
					nbInfo.parent = current;
				}
			}

		}

		ArrayList<Vertex> path = new ArrayList<>();
		Vertex current = end;
		while (datamap.get(current.getKey()).parent != null) {
			path.add(current);
			current = datamap.get(current.getKey()).parent;
		}
		path.add(current);
		for (Vertex i : path)
			System.out.print(i.getKey() + " - ");
		System.out.println();

		Edge newEdge = new Edge(start, end, datamap.get(end.getKey()).dist, true);
		for (int i = 0; i < path.size() - 1; i++) {
			newEdge.pushSubsumed(
					new int[] { path.get(i).getKey(), path.get(i).getKey(), path.get(i).getConnectingEdge(path.get(i + 1)).getCost().get() });
		}

		return newEdge;
	}

	/**
	 * Performs Dijkstra's path finding algorithm and returns the new edges between
	 * the vertices.
	 *
	 * @param G
	 *            The graph in which Dijkstra has to be performed
	 * @param start
	 *            The starting vertex
	 * @param end
	 *            The endpoint vertices as an array
	 * @param edges
	 *            The allowed edges to traverse over
	 * @return The new edges with the lowest weights
	 *
	 * @author Joshua Scheidt
	 */
	public static ArrayList<Edge> DijkstraMultiPath(UndirectedGraph G, Vertex start, ArrayList<Vertex> end, ArrayList<Edge> edges) {
		ArrayList<Vertex> Q = new ArrayList<>();
		HashMap<Vertex, DijkstraInfo> datamap = new HashMap<>();
		for (Edge e : edges) {
			if (!datamap.containsKey(e.getVertices()[0])) {
				datamap.put(e.getVertices()[0], new DijkstraInfo(Integer.MAX_VALUE));
				Q.add(e.getVertices()[0]);
			}
			if (!datamap.containsKey(e.getVertices()[1])) {
				datamap.put(e.getVertices()[1], new DijkstraInfo(Integer.MAX_VALUE));
				Q.add(e.getVertices()[1]);
			}
		}
		datamap.get(start).dist = 0;

		int numReachedEnd = 0;
		// System.out.println(G.getVertices().get(5).getNeighbors().size());
		while (!Q.isEmpty()) {
			int smallestDist = Integer.MAX_VALUE;
			Vertex current = null;
			for (Vertex i : Q) {
				if (datamap.get(i).dist < smallestDist) {
					current = i;
					smallestDist = datamap.get(i).dist;
				}
			}
			if (numReachedEnd == end.size())
				break;
			if (current == null)
				System.out.println("ERROR: No shortest distance vertex found with distance < INTEGER.MAX_VALUE");
			for (Vertex i : end)
				if (i.getKey() == current.getKey())
					numReachedEnd++;
			Q.remove(current);
			int distToCur = datamap.get(current).dist;
			int totDistToNb = 0;
			for (Vertex nb : current.getNeighbors()) {
				if (edges.contains(current.getConnectingEdge(nb))) {
					totDistToNb = distToCur + current.getConnectingEdge(nb).getCost().get();
					DijkstraInfo nbInfo = datamap.get(nb);
					if (nbInfo == null)
						System.out.println(nb.getKey() + " ???");
					if (totDistToNb < nbInfo.dist) {
						nbInfo.dist = totDistToNb;
						nbInfo.parent = current;
					}
				}
			}

		}

		ArrayList<Edge> result = new ArrayList<>();
		for (Vertex v : end) {
			ArrayList<Vertex> path = new ArrayList<>();
			Vertex current = v;
			while (datamap.get(current).parent != null) {
				path.add(current);
				current = datamap.get(current).parent;
			}
			path.add(current);
			if (start.isNeighbor(v) && start.getConnectingEdge(v).getCost().get() <= datamap.get(v).dist)
				continue;
			else {
				Stack<int[]> pathStack = new Stack<>();
				for (int i = 0; i < path.size() - 1; i++) {
					pathStack.push(new int[] { path.get(i).getKey(), path.get(i + 1).getKey(), G.getVertices().get(path.get(i).getKey())
							.getConnectingEdge(G.getVertices().get(path.get(i + 1).getKey())).getCost().get() });
				}
				if (start.isNeighbor(v)) {
					start.getConnectingEdge(v).setCost(datamap.get(v).dist);
					start.getConnectingEdge(v).replaceStack(pathStack);
				} else {
					Edge newEdge = new Edge(start, v, datamap.get(v).dist);
					newEdge.pushStack(pathStack);
					result.add(newEdge);
				}
			}
		}
		return result;
	}

	/**
	 * Performs Dijkstra's path finding algorithm and returns the new edges between
	 * the vertices.
	 *
	 * @param G
	 *            The graph in which Dijkstra has to be performed
	 * @param start
	 *            The starting vertex
	 * @param end
	 *            The endpoint vertices as an array
	 * @param edges
	 *            The allowed edges to traverse over
	 * @return The new edges with the lowest weights
	 *
	 * @author Joshua Scheidt
	 */
	public static ArrayList<EdgeFake> DijkstraMultiPathFakeEdges(UndirectedGraph G, Vertex start, ArrayList<Vertex> end, ArrayList<Edge> edges) {
		ArrayList<Vertex> Q = new ArrayList<>();
		HashMap<Vertex, DijkstraInfo> datamap = new HashMap<>();
		if (edges == null) {
			edges = new ArrayList<>();
			for (Edge e : G.getEdges())
				edges.add(e);
		}
		for (Edge e : edges) {
			if (!datamap.containsKey(e.getVertices()[0])) {
				datamap.put(e.getVertices()[0], new DijkstraInfo(Integer.MAX_VALUE));
				Q.add(e.getVertices()[0]);
			}
			if (!datamap.containsKey(e.getVertices()[1])) {
				datamap.put(e.getVertices()[1], new DijkstraInfo(Integer.MAX_VALUE));
				Q.add(e.getVertices()[1]);
			}
		}
		datamap.get(start).dist = 0;

		int numReachedEnd = 0;
		// System.out.println(G.getVertices().get(5).getNeighbors().size());
		while (!Q.isEmpty()) {
			int smallestDist = Integer.MAX_VALUE;
			Vertex current = null;
			for (Vertex i : Q) {
				if (datamap.get(i).dist < smallestDist) {
					current = i;
					smallestDist = datamap.get(i).dist;
				}
			}
			if (numReachedEnd == end.size())
				break;
			if (current == null)
				System.out.println("ERROR: No shortest distance vertex found with distance < INTEGER.MAX_VALUE");
			for (Vertex i : end)
				if (i.getKey() == current.getKey())
					numReachedEnd++;
			Q.remove(current);
			int distToCur = datamap.get(current).dist;
			int totDistToNb = 0;
			for (Vertex nb : current.getNeighbors()) {
				if (edges.contains(current.getConnectingEdge(nb))) {
					totDistToNb = distToCur + current.getConnectingEdge(nb).getCost().get();
					DijkstraInfo nbInfo = datamap.get(nb);
					if (nbInfo == null)
						System.out.println(nb.getKey() + " ???");
					if (totDistToNb < nbInfo.dist) {
						nbInfo.dist = totDistToNb;
						nbInfo.parent = current;
					}
				}
			}

		}

		ArrayList<EdgeFake> result = new ArrayList<>();
		for (Vertex v : end) {
			ArrayList<Vertex> path = new ArrayList<>();
			Vertex current = v;
			while (datamap.get(current).parent != null) {
				path.add(current);
				current = datamap.get(current).parent;
			}
			path.add(current);
			if (start.isNeighbor(v) && start.getConnectingEdge(v).getCost().get() <= datamap.get(v).dist)
				result.add(new EdgeFake(start, v, start.getConnectingEdge(v).getCost().get(), start.getConnectingEdge(v).getStack()));
			else {
				Stack<int[]> pathStack = new Stack<>();
				for (int i = 0; i < path.size() - 1; i++) {
					pathStack.push(new int[] { path.get(i).getKey(), path.get(i + 1).getKey(), G.getVertices().get(path.get(i).getKey())
							.getConnectingEdge(G.getVertices().get(path.get(i + 1).getKey())).getCost().get() });
				}
				EdgeFake newEdge = new EdgeFake(start, v, datamap.get(v).dist, pathStack);
				result.add(newEdge);
			}
		}
		return result;
	}

	public static class DijkstraInfo2 {
		public int dist;
		public ArrayList<Vertex> parents = null;

		public DijkstraInfo2(int dist) {
			this.dist = dist;
		}
	}

	/**
	 * Performs Dijkstra's path finding algorithm and returns costs for all the
	 * paths between the start vertex and every end vertex in the list (very similar
	 * to DijkstraMultiPath)
	 *
	 * @param G
	 *            The graph in which Dijkstra has to be performed
	 * @param start
	 *            The starting vertex
	 * @param end
	 *            The endpoint vertices as an array
	 * @param edges
	 *            The allowed edges to traverse over
	 * @return The new edges with the lowest weights
	 *
	 * @author Joshua Scheidt
	 */
	public static ArrayList<EdgeFake> DijkstraMultiPathFakeEdgesMultiSolution(UndirectedGraph G, Vertex start, ArrayList<Vertex> end,
			ArrayList<Edge> edges) {
		ArrayList<Vertex> Q = new ArrayList<>();
		HashMap<Vertex, DijkstraInfo2> datamap = new HashMap<>();
		int solFoundForVal = Integer.MAX_VALUE;
		if (edges == null) {
			edges = new ArrayList<>();
			for (Edge e : G.getEdges())
				edges.add(e);
		}
		for (Edge e : edges) {
			if (!datamap.containsKey(e.getVertices()[0])) {
				datamap.put(e.getVertices()[0], new DijkstraInfo2(Integer.MAX_VALUE));
				Q.add(e.getVertices()[0]);
			}
			if (!datamap.containsKey(e.getVertices()[1])) {
				datamap.put(e.getVertices()[1], new DijkstraInfo2(Integer.MAX_VALUE));
				Q.add(e.getVertices()[1]);
			}
		}
		datamap.get(start).dist = 0;

		int numReachedEnd = 0;
		// System.out.println(G.getVertices().get(5).getNeighbors().size());
		while (!Q.isEmpty()) {
			// System.out.println("in while");
			int smallestDist = Integer.MAX_VALUE;
			Vertex current = null;
			for (Vertex i : Q) {
				if (datamap.get(i).dist < smallestDist) {
					current = i;
					smallestDist = datamap.get(i).dist;
				}
			}
			if (smallestDist > solFoundForVal) {
				// System.out.println("breaking");
				break;
			}
			if (current == null)
				System.out.println("ERROR: No shortest distance vertex found with distance < INTEGER.MAX_VALUE");
			for (Vertex i : end)
				if (i.getKey() == current.getKey()) {
					solFoundForVal = smallestDist;
				}
			Q.remove(current);
			int distToCur = datamap.get(current).dist;
			int totDistToNb = 0;
			for (Vertex nb : current.getNeighbors()) {
				if (edges.contains(current.getConnectingEdge(nb))) {
					totDistToNb = distToCur + current.getConnectingEdge(nb).getCost().get();
					// System.out.println("old info: " + current.getKey() + " " + nb.getKey() + " "
					// + totDistToNb + " " + datamap.get(nb).dist);
					DijkstraInfo2 nbInfo = datamap.get(nb);
					if (nbInfo == null)
						System.out.println(nb.getKey() + " ???");
					if (totDistToNb == nbInfo.dist) {
						nbInfo.parents.add(current);
					} else if (totDistToNb < nbInfo.dist) {
						nbInfo.dist = totDistToNb;
						nbInfo.parents = new ArrayList<>();
						nbInfo.parents.add(current);
					}
					// System.out.println("new info: " + current.getKey() + " " + nb.getKey() + " "
					// + totDistToNb + " " + datamap.get(nb).dist);
				}
			}

		}

		ArrayList<EdgeFake> result = getResult(G, start, end.get(0), datamap, new ArrayList<Vertex>(), end.get(0), new ArrayList<EdgeFake>());
		// for (EdgeFake e : result) {
		// System.out.println("edge: " + e.getVertices()[0].getKey() + " " +
		// e.getVertices()[1].getKey() + " " + e.getCost());
		// for (int[] i : e.stack) {
		// System.out.println("stack: " + i[0] + " " + i[1] + " " + i[2]);
		// }
		// }
		return result;
	}

	private static ArrayList<EdgeFake> getResult(UndirectedGraph G, Vertex start, Vertex v, HashMap<Vertex, DijkstraInfo2> datamap,
			ArrayList<Vertex> path, Vertex current, ArrayList<EdgeFake> result) {
		// System.out.println("\n\n");
		// System.out.println("current path");
		// for (Vertex u : path)
		// System.out.print(u.getKey());
		// System.out.println("\ncurrent:" + current.getKey());
		// for (EdgeFake e : result) {
		// System.out.println("result: " + e.getVertices()[0].getKey() + " " +
		// e.getVertices()[1].getKey() + " " + e.getCost());
		// }
		while (datamap.get(current).parents != null) {
			path.add(current);
			if (datamap.get(current).parents.size() > 1) {
				ArrayList<Vertex> tmpPath = (ArrayList<Vertex>) path.clone();
				ArrayList<EdgeFake> tmpResult = (ArrayList<EdgeFake>) result.clone();
				for (Vertex c : datamap.get(current).parents) {
					ArrayList<Vertex> tmpTmpPath = (ArrayList<Vertex>) tmpPath.clone();
					ArrayList<EdgeFake> tmpTmpResult = (ArrayList<EdgeFake>) tmpResult.clone();
					result.addAll(getResult(G, start, v, datamap, tmpTmpPath, c, tmpTmpResult));
				}
				return result;
			} else
				current = datamap.get(current).parents.get(0);
		}

		path.add(current);
		if (start.isNeighbor(v) && start.getConnectingEdge(v).getCost().get() <= datamap.get(v).dist)
			result.add(new EdgeFake(start, v, start.getConnectingEdge(v).getCost().get(), start.getConnectingEdge(v).getStack()));
		else {
			Stack<int[]> pathStack = new Stack<>();
			for (int i = 0; i < path.size() - 1; i++) {
				pathStack.push(new int[] { path.get(i).getKey(), path.get(i + 1).getKey(),
						G.getVertices().get(path.get(i).getKey()).getConnectingEdge(G.getVertices().get(path.get(i + 1).getKey())).getCost().get() });
			}
			EdgeFake newEdge = new EdgeFake(start, v, datamap.get(v).dist, pathStack);
			// System.out.println(newEdge.getVertices()[0].getKey() + " " +
			// newEdge.getVertices()[1].getKey());
			result.add(newEdge);
		}
		return result;
	}

	/**
	 * Performs Dijkstra's path finding algorithm and returns the new edges between
	 * the vertices.
	 *
	 * @param G
	 *            The graph in which Dijkstra has to be performed
	 * @param start
	 *            The starting vertex
	 * @param end
	 *            The endpoint vertices as an array
	 * @param edges
	 *            The allowed edges to traverse over
	 * @return The new edges with the lowest weights
	 *
	 * @author Joshua Scheidt
	 */
	public static ArrayList<EdgeFake> DijkstraShortestPathHeuristic(UndirectedGraph G, ArrayList<Vertex> start,
			HashMap<Vertex, HashMap<Vertex, DijkstraInfo>> fullInfo, HashMap<Vertex, ArrayList<Vertex>> availableSearches,
			HashMap<Vertex, Integer> lowestCosts, HashMap<Vertex, ArrayList<Integer>> alreadyVisited) {
		// System.out.println(start.size());
		Vertex foundFrom = null, foundTerminal = null;
		while (true) {
			// System.out.println(Q.size());
			int smallestDist = Integer.MAX_VALUE;
			Vertex begin = null;
			Vertex chosen = null;
			Entry<Vertex, Integer> min = Collections.min(lowestCosts.entrySet(), Comparator.comparing(Entry::getValue));

			// for (Vertex v : start)
			for (Vertex i : availableSearches.get(min.getKey())) {
				// System.out.println(fullInfo.get(v).get(i).dist);
				if (fullInfo.get(min.getKey()).get(i).dist < smallestDist) {
					begin = min.getKey();
					chosen = i;
					smallestDist = fullInfo.get(min.getKey()).get(i).dist;
					// System.out.println("in here");
				}
			}
			// System.out.println(chosen.getKey());

			if (chosen == null)
				System.out.println("ERROR: No shortest distance vertex found with distance < INTEGER.MAX_VALUE");
			if (chosen.isTerminal() && !start.contains(chosen)) {
				foundFrom = begin;
				foundTerminal = chosen;
				break;
			}

			availableSearches.get(begin).remove(chosen);
			alreadyVisited.get(begin).add(chosen.getKey());

			int distToCur = fullInfo.get(begin).get(chosen).dist;
			int totDistToNb = 0;
			for (Vertex nb : chosen.getNeighbors()) {
				if (G.getEdges().contains(chosen.getConnectingEdge(nb)) && !alreadyVisited.get(begin).contains(nb.getKey())) {
					totDistToNb = distToCur + chosen.getConnectingEdge(nb).getCost().get();
					DijkstraInfo nbInfo = fullInfo.get(begin).get(nb);
					// System.out.println("before:" + nbInfo.dist);
					if (nbInfo == null)
						System.out.println(nb.getKey() + " ???");
					if (totDistToNb < nbInfo.dist) {
						nbInfo.dist = totDistToNb;
						nbInfo.parent = chosen;
						if (!availableSearches.get(begin).contains(nb)) {
							availableSearches.get(begin).add(nb);
						}
					}
					// System.out.println("after: " + nbInfo.dist);
				}
			}
			int lowest = Integer.MAX_VALUE;
			for (Vertex v : availableSearches.get(begin)) {
				if (lowest > fullInfo.get(begin).get(v).dist)
					lowest = fullInfo.get(begin).get(v).dist;
			}
			lowestCosts.put(begin, lowest);
		}

		ArrayList<EdgeFake> result = new ArrayList<>();
		ArrayList<Vertex> path = new ArrayList<>();
		Vertex current = foundTerminal;
		while (fullInfo.get(foundFrom).get(current).parent != null) {
			path.add(current);
			current = fullInfo.get(foundFrom).get(current).parent;
		}
		path.add(current);
		if (foundFrom.isNeighbor(foundTerminal))
			result.add(new EdgeFake(foundFrom, foundTerminal, foundFrom.getConnectingEdge(foundTerminal).getCost().get(),
					foundFrom.getConnectingEdge(foundTerminal).getStack()));
		else {
			Stack<int[]> pathStack = new Stack<>();
			for (int i = 0; i < path.size() - 1; i++) {
				pathStack.push(new int[] { path.get(i).getKey(), path.get(i + 1).getKey(),
						G.getVertices().get(path.get(i).getKey()).getConnectingEdge(G.getVertices().get(path.get(i + 1).getKey())).getCost().get() });
			}
			EdgeFake newEdge = new EdgeFake(foundFrom, foundTerminal, fullInfo.get(foundFrom).get(foundTerminal).dist, pathStack);
			result.add(newEdge);
		}

		return result;
	}

	/**
	 * Performs Dijkstra's path finding algorithm and returns costs for all the
	 * paths between the start vertex and every end vertex in the list (very similar
	 * to DijkstraMultiPath)
	 *
	 * @param g
	 *            List containing all the destination vertices
	 * @return An ArrayList with the corresponding path lengths
	 *
	 * @author Joshua Scheidt
	 * @author Pit Schneider
	 */
	public static HashMap<Integer, DijkstraInfo> DijkstraForDW(UndirectedGraph g, Vertex start, ArrayList<Vertex> end) {

		ArrayList<Vertex> unvisited = new ArrayList<>();
		HashMap<Integer, DijkstraInfo> datamap = new HashMap<>();
		int numReachedEnd = 0;

		for (Vertex v : g.getVertices().values()) {
			if (!datamap.containsKey(v.getKey())) {
				datamap.put(v.getKey(), new DijkstraInfo(Integer.MAX_VALUE));
				unvisited.add(v);
			}
		}

		datamap.get(start.getKey()).dist = 0;

		while (!unvisited.isEmpty() && numReachedEnd < end.size()) {
			if (RandomMain.killed)
				return null;

			int smallestDist = Integer.MAX_VALUE;
			Vertex current = null;
			for (Vertex v : unvisited) {
				if (datamap.get(v.getKey()).dist < smallestDist) {
					current = v;
					smallestDist = datamap.get(v.getKey()).dist;
				}
			}
			for (Vertex v : end) {
				if (v.getKey() == current.getKey()) {
					numReachedEnd++;
				}
			}
			unvisited.remove(current);

			int distToCur = datamap.get(current.getKey()).dist;
			for (Vertex nb : current.getNeighbors()) {
				if (unvisited.contains(nb)) {
					int totDistToNb = distToCur + current.getConnectingEdge(nb).getCost().get();
					DijkstraInfo currentInfo = datamap.get(nb.getKey());
					if (totDistToNb < currentInfo.dist) {
						currentInfo.dist = totDistToNb;
						currentInfo.parent = current;
					}
				}
			}
		}

		HashMap<Integer, DijkstraInfo> result = new HashMap<>();
		for (Vertex v : end) {
			result.put(v.getKey(), datamap.get(v.getKey()));
		}
		return result;
	}

	/**
	 * Performs Dijkstra's path finding algorithm. This one was requested for the
	 * IDW method.
	 *
	 * @param G
	 *            The graph in which Dijkstra has to be performed
	 * @param start
	 *            The starting vertex
	 * @param end
	 *            The endpoint vertex
	 * @return The path with the lowest weight
	 *
	 * @author Joshua Scheidt
	 */
	public static ArrayList<Edge> DijkstraSinglePath(UndirectedGraph G, Vertex start, Vertex end) {
		ArrayList<Vertex> Q = new ArrayList<>();
		HashMap<Integer, DijkstraInfo> datamap = new HashMap<>();
		for (Vertex i : G.getVertices().values()) {
			datamap.put(i.getKey(), new DijkstraInfo(Integer.MAX_VALUE));
			Q.add(i);
		}
		datamap.get(start.getKey()).dist = 0;

		while (!Q.isEmpty()) {
			if (RandomMain.killed)
				return null;
			int smallestDist = Integer.MAX_VALUE;
			Vertex current = null;
			for (Vertex i : Q) {
				if (datamap.get(i.getKey()).dist < smallestDist) {
					current = i;
					smallestDist = datamap.get(i.getKey()).dist;
				}
			}
			if (current == end)
				break;
			if (current == null)
				System.out.println("ERROR: No shortest distance vertex found with distance < INTEGER.MAX_VALUE");
			Q.remove(current);
			int distToCur = datamap.get(current.getKey()).dist;
			int totDistToNb = 0;
			for (Vertex nb : current.getNeighbors()) {
				totDistToNb = distToCur + current.getConnectingEdge(nb).getCost().get();
				DijkstraInfo nbInfo = datamap.get(nb.getKey());
				if (nbInfo == null)
					System.out.println(nb.getKey() + " ???");
				if (totDistToNb < nbInfo.dist) {
					nbInfo.dist = totDistToNb;
					nbInfo.parent = current;
				}
			}

		}

		ArrayList<Edge> path = new ArrayList<>();
		Vertex current = end;
		while (datamap.get(current.getKey()).parent != null) {
			path.add(current.getConnectingEdge(datamap.get(current.getKey()).parent));
			current = datamap.get(current.getKey()).parent;
		}
		return path;
	}
}
