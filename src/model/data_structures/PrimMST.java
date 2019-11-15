package model.data_structures;

import model.logic.Arco;
import model.logic.Vertice;

/** 
 * Implementación tomada de Algorithms 4th edition by Robert Sedgewick and Kevin Wayne (2011)
 * Consultado el 15/11/19
 * Disponible en https://algs4.cs.princeton.edu/code/
 */
public class PrimMST
{
	private static final double FLOATING_POINT_EPSILON = 1E-12;

	private Arco[] edgeTo;        // edgeTo[v] = shortest edge from tree vertex to non-tree vertex
	private double[] distTo;      // distTo[v] = weight of shortest such edge
	private boolean[] marked;     // marked[v] = true if v on tree, false otherwise
	private IndexMinPQ<Double> pq;

	/**
	 * Compute a minimum spanning tree (or forest) of an edge-weighted graph.
	 * @param G the edge-weighted graph
	 */
	public PrimMST(Graph G) {
		edgeTo = new Arco[G.size()];
		distTo = new double[G.size()];
		marked = new boolean[G.size()];
		pq = new IndexMinPQ<Double>(G.size());
		for (int v = 0; v < G.size(); v++)
			distTo[v] = Double.POSITIVE_INFINITY;

		for (int v = 0; v < G.size(); v++)      // run from each vertex to find
			if (!marked[v]) prim(G, v);      // minimum spanning forest

		// check optimality conditions
		assert check(G);
	}

	// run Prim's algorithm in graph G, starting from vertex s
	private void prim(Graph G, int s) {
		distTo[s] = 0.0;
		pq.insert(s, distTo[s]);
		while (!pq.isEmpty()) {
			int v = pq.delMin();
			scan(G, v);
		}
	}

	// scan vertex v
	private void scan(Graph G, int v) {
		marked[v] = true;
		if(G.getInfoVertex(v) != null)
		{
			for (Arco e : ((Vertice)G.getInfoVertex(v)).darArcos())
			{
				int w = e.other(v);
				if (marked[w]) continue;         // v-w is obsolete edge
				if (e.darCostoDistancia() < distTo[w]) {
					distTo[w] = e.darCostoDistancia();
					edgeTo[w] = e;
					if (pq.contains(w)) pq.decreaseKey(w, distTo[w]);
					else                pq.insert(w, distTo[w]);
				}
			}
		}
	}

	/**
	 * Returns the edges in a minimum spanning tree (or forest).
	 * @return the edges in a minimum spanning tree (or forest) as
	 *    an iterable of edges
	 */
	public Iterable<Arco> edges() {
		Queue<Arco> mst = new Queue<Arco>();
		for (int v = 0; v < edgeTo.length; v++) {
			Arco e = edgeTo[v];
			if (e != null) {
				mst.enqueue(e);
			}
		}
		return mst;
	}

	/**
	 * Returns the sum of the edge weights in a minimum spanning tree (or forest).
	 * @return the sum of the edge weights in a minimum spanning tree (or forest)
	 */
	public double weight() {
		double weight = 0.0;
		for (Arco e : edges())
			weight += e.darCostoDistancia();
		return weight;
	}

	// check optimality conditions (takes time proportional to E V lg* V)
	private boolean check(Graph G) {

		// check weight
		double totalWeight = 0.0;
		for (Arco e : edges()) {
			totalWeight += e.darCostoDistancia();
		}
		if (Math.abs(totalWeight - weight()) > FLOATING_POINT_EPSILON) {
			System.err.printf("Weight of edges does not equal weight(): %f vs. %f\n", totalWeight, weight());
			return false;
		}

		// check that it is acyclic
		UF uf = new UF(G.size());
		for (Arco e : edges()) {
			int v = e.either(), w = e.other(v);
			if (uf.connected(v, w)) {
				System.err.println("Not a forest");
				return false;
			}
			uf.union(v, w);
		}

		// check that it is a spanning forest
		for (Arco e : (Iterable<Arco>)G.edges()) 
		{
			int v = e.either(), w = e.other(v);
			if (!uf.connected(v, w)) {
				System.err.println("Not a spanning forest");
				return false;
			}
		}

		// check that it is a minimal spanning forest (cut optimality conditions)
		for (Arco e : edges()) {

			// all edges in MST except e
			uf = new UF(G.size());
			for (Arco f : edges()) {
				int x = f.either(), y = f.other(x);
				if (f != e) uf.union(x, y);
			}

			// check that e is min weight edge in crossing cut
			for (Arco f : (Iterable<Arco>)G.edges()) {
				int x = f.either(), y = f.other(x);
				if (!uf.connected(x, y)) {
					if (f.darCostoDistancia() < e.darCostoDistancia()) {
						System.err.println("Edge " + f + " violates cut optimality conditions");
						return false;
					}
				}
			}
		}

		return true;
	}
}