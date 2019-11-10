package model.data_structures;

public class ArregloDinamico<T extends Comparable<T>> 
{
	/**
	 * Capacidad maxima del arreglo
	 */
	private int tamanoMax;
	/**
	 * Numero de elementos presentes en el arreglo (de forma compacta desde la posicion 0)
	 */
	private int tamanoAct;
	/**
	 * Arreglo de elementos de tamaNo maximo
	 */
	private T elementos[ ];

	/**
	 * Construir un arreglo con la capacidad maxima inicial.
	 * @param max Capacidad maxima inicial
	 */
	public ArregloDinamico( int max )
	{
		elementos = (T[]) new Comparable[max];
		tamanoMax = max;
		tamanoAct = 0;
	}

	public void agregar( T dato )
	{
		if ( tamanoAct == tamanoMax )
		{  // caso de arreglo lleno (aumentar tamaNo)
			tamanoMax = 2 * tamanoMax;
			Object [ ] copia = elementos;
			elementos = (T[])new Comparable[tamanoMax];
			for ( int i = 0; i < tamanoAct; i++)
			{
				elementos[i] = (T)copia[i];
			} 
		}	
		elementos[tamanoAct] = dato;
		tamanoAct++;
	}

	public int darCapacidad() {
		return tamanoMax;
	}

	public int darTamano() {
		return tamanoAct;
	}

	public T darElemento(int i)
	{
		return i<tamanoMax? elementos[i]:null;
	}

	public T buscar(T dato)
	{
		for(T actual : elementos)
		{
			if(dato.compareTo(actual) == 0)
			{return actual;}
		}
		return null;
	}

	public T eliminar(T dato)
	{
		T buscado = null;

		for (int i = 0; i < elementos.length; i++)
		{
			if(buscado == null)		
			{
				T actual = elementos[i];
				if(actual.compareTo(dato) == 0)
				{
					buscado = actual;
					tamanoAct --;
					if(i+1<tamanoMax)
					{elementos[i] = elementos[i+1];}
					else
					{elementos[i] = null;}
				}
			}
			else
			{
				if(i+1<tamanoMax)
				{elementos[i] = elementos[i+1];}
				else
				{elementos[i] = null;}
			}
		}
		return buscado;
	}
	
	public void cambiarElemento (T dato, int index)
	{
		if(elementos[index] == null && dato != null)
		{
			tamanoAct++;
		}
		
		if(elementos[index] != null && dato == null)
		{
			tamanoAct--;
		}
		
		elementos[index] = dato;
	}

	public T darPrimerElemento()
	{
		return elementos[0];
	}

	public T darUltimoElemento()
	{
		return elementos[darTamano()-1];
	}

	public ArregloDinamico<T> darPrimeros10Viajes()
	{
		ArregloDinamico<T> respuesta = new ArregloDinamico<>(10);
		
		for (int i = 0; i < 10 && i < tamanoAct; i++)
		{
			respuesta.cambiarElemento(elementos[i], i);
		}
		
		return respuesta;		
	}
	
	public ArregloDinamico<T> darUltimos10Viajes()
	{
		ArregloDinamico<T> respuesta = new ArregloDinamico<>(10);
		
		for (int i = tamanoAct - 1; i > tamanoAct - 11 && i >= 0; i--)
		{
			respuesta.cambiarElemento(elementos[i], tamanoAct - i - 1);
		}
		
		return respuesta;		
	}
}