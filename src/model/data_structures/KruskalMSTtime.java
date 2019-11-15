package model.data_structures;

import model.logic.Arco;

/** 
 * Implementación tomada de Algorithms 4th edition by Robert Sedgewick and Kevin Wayne (2011)
 * Consultado el 15/11/19
 * Disponible en https://algs4.cs.princeton.edu/code/
 */
public class KruskalMSTtime {
	private static final double FLOATING_POINT_EPSILON = 1E-12;

	private double weight;                        // weight of MST
	private Queue<Arco> mst = new Queue<Arco>();  // edges in MST

	/**
	 * Compute a minimum spanning tree (or forest) of an edge-weighted graph.
	 * @param G the edge-weighted graph
	 */
	public KruskalMSTtime(Graph G) {
		// more efficient to build heap by passing array of edges
		MinPQ<Arco> pq = new MinPQ<Arco>();
		for (Arco e : (ArregloDinamico<Arco>)G.arcos) {
			pq.insert(e);
		}

		// run greedy algorithm
		UF uf = new UF(G.size());
		while (!pq.isEmpty() && mst.size() < G.size() - 1) {
			Arco e = pq.delMin();
			int v = e.either();
			int w = e.other(v);
			if (!uf.connected(v, w)) { // v-w does not create a cycle
				uf.union(v, w);  // merge v and w components
				mst.enqueue(e);  // add edge e to mst
				weight += e.darCostoTiempo();
			}
		}

		// check optimality conditions
		assert check(G);
	}

	/**
	 * Returns the edges in a minimum spanning tree (or forest).
	 * @return the edges in a minimum spanning tree (or forest) as
	 *    an iterable of edges
	 */
	public Iterable<Arco> edges() {
		return mst;
	}

	/**
	 * Returns the sum of the edge weights in a minimum spanning tree (or forest).
	 * @return the sum of the edge weights in a minimum spanning tree (or forest)
	 */
	public double weight() {
		return weight;
	}

	// check optimality conditions (takes time proportional to E V lg* V)
	private boolean check(Graph G) {

		// check total weight
		double total = 0.0;
		for (Arco e : edges()) {
			total += e.darCostoTiempo();
		}
		if (Math.abs(total - weight()) > FLOATING_POINT_EPSILON) {
			System.err.printf("Weight of edges does not equal weight(): %f vs. %f\n", total, weight());
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
		for (Arco e : (Iterable<Arco>)G.edges()) {
			int v = e.either(), w = e.other(v);
			if (!uf.connected(v, w)) {
				System.err.println("Not a spanning forest");
				return false;
			}
		}

		// check that it is a minimal spanning forest (cut optimality conditions)
		for (Arco e : edges()) {

			// all edges in MST except e
			uf = new UF(G.V());
			for (Arco f : mst) {
				int x = f.either(), y = f.other(x);
				if (f != e) uf.union(x, y);
			}

			// check that e is min weight edge in crossing cut
			for (Arco f : (Iterable<Arco>)G.edges()) {
				int x = f.either(), y = f.other(x);
				if (!uf.connected(x, y)) {
					if (f.darCostoTiempo() < e.darCostoTiempo()) {
						System.err.println("Edge " + f + " violates cut optimality conditions");
						return false;
					}
				}
			}

		}
		return true;
	}
}