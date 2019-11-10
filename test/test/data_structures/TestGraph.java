package test.data_structures;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import model.data_structures.Graph;
import model.data_structures.Stack;
import model.logic.Vertice;

public class TestGraph 
{
	private Graph<Integer,Vertice> grafo ;
	private Vertice a;
	private Vertice b;
	private Vertice c;
	private Vertice d;
	@Before
	public void setUp()
	{
		grafo = new Graph<>(5);
		
		 a = new  Vertice(1, 1, 1, 1);
		 b = new  Vertice(2,2, 2, 2);
		 c = new  Vertice(3, 3, 3, 3);
		 d = new  Vertice(4,4, 4,4);
	}
	//  1----------2 
	//  |          |
	//  |          |
    //  3----------4
	// 1 = a 
	// 2 = b
	// 3 = c
	// 4 = d 
	// ------  costo 10
	// |       costo 5
	public void setUp1()
	{
		
		grafo.addVertex(1,a);
		grafo.addVertex(2, b);
		grafo.addVertex(3, c);
		grafo.addVertex(4, d);
		grafo.addEdge(1, 2, 10.0);
		grafo.addEdge(3, 4, 10.0);
		grafo.addEdge(1, 3, 5.0);
		grafo.addEdge(2, 4,5.0);
		grafo.arcos.darElemento(0).cambiarcosto(10);
		grafo.arcos.darElemento(1).cambiarcosto(10);
		grafo.arcos.darElemento(2).cambiarcosto(5);
		grafo.arcos.darElemento(3).cambiarcosto(5);
	
	}
	@Test
	public void testConstructor()
	{
		setUp1();
		assertNotNull(grafo.V());
		assertNotNull(grafo.E());
		assertEquals(4, grafo.V());
		assertEquals(4, grafo.E());
	}
	@Test
	public void testGetInfoVertex()
	{
		setUp1();

		assertEquals(grafo.getInfoVertex(1), a);
		assertEquals(grafo.getInfoVertex(2), b);
		assertEquals(grafo.getInfoVertex(3), c);
		assertEquals(grafo.getInfoVertex(4), d);
	}
	@Test
	public void testGetcost()
	{
		setUp1();

		assertEquals(grafo.getCostArc(1, 2), 10.0, 0);
		assertEquals(grafo.getCostArc(3, 4), 10.0, 0);
		assertEquals(grafo.getCostArc(1, 3), 5.0, 0);
		assertEquals(grafo.getCostArc(2, 4), 5.0, 0);
	}
	@Test
	public void testAdj()
	{
		setUp1();

		assertNotNull(grafo.adj(1));
		Stack  x = new  Stack<>();
		x = (Stack) grafo.adj(1);
		assertEquals(x.size(), 4);		
	}
	@Test
	public void testUncheck()
	{		
		setUp1();

		grafo.dfs(1);
		
		grafo.uncheck();
		
		for(int i = 1; i <5; i++)
		{
			assertFalse(grafo.Marked[i]);
		}
	}
	
	@Test
	public void testCc()
	{	
		setUp1();

		assertEquals(grafo.cc(), 4);
	}
}
