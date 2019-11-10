package model.logic;

public class Arco implements Comparable<Arco>
{
	private int origen;

	private int destino;

	private double costo;

	public Arco(int porigen, int pdestino,  double pcosto)
	{
		origen = porigen;
		destino = pdestino;
		costo = pcosto;
	}

	public int darOrigen()
	{
		return origen;
	}

	public int darDest()
	{
		return destino;
	}

	public double darCosto()
	{
		return costo;
	}

	public void cambiarcosto(double param)
	{
		costo = param;
	}

	@Override
	public int compareTo(Arco param) {
		if(origen == param.origen && destino == param.destino)
		{
			return 0;
		}
		else if(origen > param.origen && destino > param.destino)
		{
			return 1;
		}
		else 
			return -1;
	}
}