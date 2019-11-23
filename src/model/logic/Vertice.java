package model.logic;

import model.data_structures.ArregloDinamico;
import model.data_structures.Graph;

public class Vertice implements Comparable<Vertice>
{
	private int id;

	private double longitud, latitud;

	private int MOVEMENT_ID;

	public ArregloDinamico<Arco> arcos;
	
	public double velProm;

	public Vertice(int pId, double pLongitud, double pLatitud, int pMOVEMENT_ID)
	{
		id = pId;
		longitud = pLongitud;
		latitud = pLatitud;
		MOVEMENT_ID = pMOVEMENT_ID;
		arcos = new ArregloDinamico<>(1);
	}

	public int compareTo(Vertice param) {
		if(id > param.id)
		{
			return 1;
		}
		else if(id < param.id)
		{
			return -1;
		}
		return 0;
	}

	public int darId()
	{
		return id;
	}

	public double darLongitud()
	{
		return longitud;
	}

	public double darLatitud()
	{
		return latitud;
	}

	public int darMID()
	{
		return MOVEMENT_ID;
	}

	public void agregarArco(Arco param)
	{
		arcos.agregar(param);
	}

	public ArregloDinamico<Arco> darArcos()
	{
		return arcos;
	}
	public double velocidadPromedio()
	{
		double x = 0;
		double suma = 0;
		if (arcos.darTamano() > 0)
		{
			for(int i = 0; i < arcos.darTamano(); i++)
			{
				suma += arcos.darElemento(i).darCostoVelocidad();
			}
			x = suma/arcos.darTamano();
			return x;

		}
		else
		{
			return 0;
		}
	}
}