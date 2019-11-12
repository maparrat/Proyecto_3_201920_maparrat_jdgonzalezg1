package model.data_structures;

import java.util.Iterator;

import model.logic.Vertice;

/** 
 * Implementación tomada de Algorithms 4th edition by Robert Sedgewick and Kevin Wayne (2011)
 * Consultado el 04/11/19
 * Disponible en https://algs4.cs.princeton.edu/code/
 */
public class NonrecursiveDFS {
	public boolean[] marked;  // marked[v] = is there an s-v path?
	/**
	 * Computes the vertices connected to the source vertex {@code s} in the graph {@code G}.
	 * @param G the graph
	 * @param s the source vertex
	 * @throws IllegalArgumentException unless {@code 0 <= s < V}
	 */
	public NonrecursiveDFS(Graph G, int s) {
		marked = new boolean[G.size()];

		validateVertex(s);

		// to be able to iterate over each adjacency list, keeping track of which
		// vertex in each adjacency list needs to be explored next
		Iterator<Vertice>[] adj = (Iterator<Vertice>[]) new Iterator[G.size()];
		for (int v = 0; v < G.size(); v++)
			adj[v] = G.adj(v).iterator();

		// depth-first search using an explicit stack
		Stack<Integer> stack = new Stack<Integer>();
		marked[s] = true;
		stack.push(s);
		while (!stack.isEmpty())
		{
			int v = stack.peek();

			if (adj[v].hasNext())
			{
				int w = ((Vertice)adj[v].next()).darId();

				if (!marked[w])
				{
					// discovered vertex w for the first time
					marked[w] = true;
					// edgeTo[w] = v;
					stack.push(w);
				}
			}
			else
			{
				stack.pop();
			}
		}
	}

	/**
	 * Is vertex {@code v} connected to the source vertex {@code s}?
	 * @param v the vertex
	 * @return {@code true} if vertex {@code v} is connected to the source vertex {@code s},
	 *    and {@code false} otherwise
	 * @throws IllegalArgumentException unless {@code 0 <= v < V}
	 */
	public boolean marked(int v)
	{
		validateVertex(v);
		return marked[v];
	}

	// throw an IllegalArgumentException unless {@code 0 <= v < V}
	private void validateVertex(int v)
	{
		int V = marked.length;
		if (v < 0 || v >= V)
			throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
	}
}