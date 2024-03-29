package model.data_structures;

import java.util.Iterator;
import java.util.NoSuchElementException;

import model.logic.Vertice;

/** 
 * Implementación tomada de Algorithms 4th edition by Robert Sedgewick and Kevin Wayne (2011)
 * Consultado el 04/11/19
 * Disponible en https://algs4.cs.princeton.edu/code/
 */
public class Bag<Item> implements Iterable<Item> {
    private Node<Item> first;    // beginning of bag
    private int n;               // number of elements in bag

    // helper linked list class
    private static class Node<Item> {
        private Item item;
        private Node<Item> next;
    }

    /**
     * Initializes an empty bag.
     */
    public Bag() {
        first = null;
        n = 0;
    }

    /**
     * Returns true if this bag is empty.
     *
     * @return {@code true} if this bag is empty;
     *         {@code false} otherwise
     */
    public boolean isEmpty() {
        return first == null;
    }

    /**
     * Returns the number of items in this bag.
     *
     * @return the number of items in this bag
     */
    public int size() {
        return n;
    }

    /**
     * Adds the item to this bag.
     * @param  item the item to add to this bag
     */
    public void add(Item item) {
    	
    	if(first == null)
    	{
    		first = new Node<Item>();
    		first.item = item;
    	}
    	else
    	{
    		Node<Item> current = first;
    		
    		while(current.next != null)
    		{
    			current = current.next;
    		}
    		current.next = new Node<Item>();
    		current.next.item = item;
    	}
    	
        n++;
    }
    
    public void cambiarPrimero(Item item) {
        first.item = item;
    }
    
    public void cambiarItem(Vertice item) {
        if(first == null)
        {
        	System.out.println("Lista vacia");
        }
        else
        {
        	Node<Item> current = first;
        	Vertice actual = (Vertice) current.item;
        	while(actual.compareTo(item) != 0)
        	{
        		current = current.next;
        		actual= (Vertice) current.item;
        	}
        	current.item = (Item) item;
        }
    }

    /**
     * Returns an iterator that iterates over the items in this bag in arbitrary order.
     * @return an iterator that iterates over the items in this bag in arbitrary order
     */
    public Iterator<Item> iterator()  {
        return new ListIterator(first);  
    }

    // an iterator, doesn't implement remove() since it's optional
    private class ListIterator implements Iterator<Item> {
        private Node<Item> current;

        public ListIterator(Node<Item> first) {
            current = first;
        }

        public boolean hasNext()  { return current != null;                     }
        public void remove()      { throw new UnsupportedOperationException();  }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            Item item = current.item;
            current = current.next; 
            return item;
        }
    }
}