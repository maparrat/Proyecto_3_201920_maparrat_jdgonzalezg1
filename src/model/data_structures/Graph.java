package model.data_structures;

import java.util.Iterator;
import java.util.NoSuchElementException;

import model.logic.Arco;
import model.logic.Vertice;

/** 
 * Implementación tomada de Algorithms 4th edition by Robert Sedgewick and Kevin Wayne (2011)
 * Consultado el 04/11/19
 * Disponible en https://algs4.cs.princeton.edu/code/
 */
public class Graph<K,V> implements IGraph<K, V>
{
	private static final String NEWLINE = System.getProperty("line.separator");

	private int V;
	private int size;
	private int E;
	public boolean[] Marked;
	private Bag<V>[] adj;
	public  ArregloDinamico<Arco> arcos;
	private Haversine harve;

	/**
	 * Initializes an empty graph with {@code V} vertices and 0 edges.
	 * param V the number of vertices
	 *
	 * @param  V number of vertices
	 * @throws IllegalArgumentException if {@code V < 0}
	 */
	public Graph(int V) {
		if (V < 0) throw new IllegalArgumentException("Number of vertices must be nonnegative");
		arcos = new ArregloDinamico<>(1);
		this.size = V;
		this.E = 0;
		adj = new Bag[V];
		for (int v = 0; v < V; v++) {
			adj[v] = new Bag<V>();
		}
		harve = new Haversine();
	}

	/**
	 * Returns the number of vertices in this graph.
	 * @return the number of vertices in this graph
	 */
	public int V() {
		return V;
	}

	/**
	 * Returns the number of edges in this graph.
	 * @return the number of edges in this graph
	 */
	public int E() {
		return E;
	}

	public int size() {
		return size;
	}

	// throw an IllegalArgumentException unless {@code 0 <= v < V}
	private void validateVertex(int v) {
		if (v < 0 || v > size)
			throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (size-1));
	}

	/**
	 * Returns the vertices adjacent to vertex {@code v}.
	 * @param  v the vertex
	 * @return the vertices adjacent to vertex {@code v}, as an iterable
	 * @throws IllegalArgumentException unless {@code 0 <= v < V}
	 */
	public Iterable<V> adj(int v) {
		validateVertex(v);
		return adj[v];
	}

	/**
	 * Returns the degree of vertex {@code v}.
	 *
	 * @param  v the vertex
	 * @return the degree of vertex {@code v}
	 * @throws IllegalArgumentException unless {@code 0 <= v < V}
	 */
	public int degree(int v)
	{
		validateVertex(v);
		return adj[v].size();
	}

	@Override
	public void addEdge(K idVertexIni, K idVertexFin, double cost)
	{

		validateVertex((int) idVertexIni);
		validateVertex((int) idVertexFin);
		E++;

		if(adj[(int) idVertexIni].size() != 0 && adj[(int) idVertexFin].size() != 0)
		{
			Vertice ini = (Vertice) adj[(int) idVertexIni].iterator().next();
			Vertice fin = (Vertice) adj[(int) idVertexFin].iterator().next();

			double costo = harve.distance(ini.darLatitud(), ini.darLongitud(), fin.darLatitud(), fin.darLongitud());

			Vertice nuevo1 = (Vertice) adj[(int) idVertexFin].iterator().next();
			Vertice nuevo2 = (Vertice) adj[(int) idVertexIni].iterator().next();
			adj[(int) idVertexIni].add((V) new Vertice((int)idVertexFin, nuevo1.darLongitud(), nuevo1.darLongitud(), nuevo1.darMID()));
			adj[(int) idVertexFin].add((V) new Vertice((int)idVertexIni, nuevo2.darLongitud(), nuevo2.darLongitud(), nuevo2.darMID()));

			arcos.agregar(new Arco((Integer)idVertexIni, (Integer)idVertexFin, costo));
		}
	}

	public V getInfoVertex(K idVertex)
	{
		if(adj[(int) idVertex].size() != 0)
			return (V) adj[(int) idVertex].iterator().next();
		else
			return null;
	}

	public void setInfoVertex(K idVertex, V infoVertex)
	{
		adj[(int) idVertex].cambiarPrimero(infoVertex);

		Iterator<V> x = adj[(int)idVertex].iterator();
		x.next();
		while(x.hasNext())
		{
			Vertice actual1 = (Vertice) x.next();

			adj[(int) actual1.darId()].cambiarItem((Vertice) infoVertex);			
		}		
	}

	double a ; 
	@Override
	public double getCostArc(K idVertexIni, K idVertexFin) {

		return arcos.buscar(new Arco((int)idVertexIni, (int)idVertexFin, 0)).darCosto();
	}

	@Override
	public void setCostArc(K idVertexIni, K idVertexFin, double cost) {
		arcos.buscar(new Arco((int)idVertexIni, (int)idVertexFin, 0)).cambiarcosto(cost);
	}

	@Override
	public void addVertex(K idVertex, V infoVertex) {
		// TODO Auto-generated method stub
		adj[(int) idVertex].add(infoVertex);
		V++;

		if((int) idVertex > size)
		{
			size = (int) idVertex;
		}
	}

	@Override
	public Iterable<K> adj(K idVertex) {
		// TODO Auto-generated method stub

		Stack<Integer> respuesta = new Stack<>();

		Iterator<V> x = adj[(int)idVertex].iterator();
		x.next();

		while(x.hasNext())
		{
			Vertice actual = (Vertice) x.next();
			respuesta.push(actual.darId());
		}
		return (Iterable<K>) respuesta;		
	}

	@Override
	public void uncheck() {
		// TODO Auto-generated method stub
		for(int i = 0; i < Marked.length; i++)
		{
			Marked[i] = false;
		}
	}

	public void dfs(int s)
	{
		NonrecursiveDFS x = new NonrecursiveDFS(this, (int) s);
		Marked = x.marked;
	}

	public int cc()
	{
		int count = 0; 
		for (int v = 0; v < V(); v++) {
			if (!Marked[v]) {
				dfs( v);
				count++;
			}
		}
		return count; 
	}

	public Iterable<K> getCC(K idVertex) {

		dfs((int)idVertex);

		Stack<Integer> respuesta = new Stack<>();

		for (int i = 0; i < adj.length; i++) {
			if(Marked[i] == true)
			{
				Vertice actual = (Vertice) adj[i].iterator().next();
				respuesta.push(actual.darId());
			}			
		}

		return (Iterable<K>) respuesta;	
	}
}