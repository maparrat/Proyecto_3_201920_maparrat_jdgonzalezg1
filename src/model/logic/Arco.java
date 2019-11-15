package model.logic;

public class Arco implements Comparable<Arco>
{
	private int origen;

	private int destino;

	private double costoDistancia, costoTiempo, costoVelocidad;

	public Arco(int pOrigen, int pDestino,  double pCostoDistancia, double pCostoTiempo, double pCostoVelocidad)
	{
		origen = pOrigen;
		destino = pDestino;
		costoDistancia = pCostoDistancia;
		costoTiempo = pCostoTiempo;
		costoVelocidad = pCostoVelocidad;
	}

	public int darOrigen()
	{
		return origen;
	}

	public int darDest()
	{
		return destino;
	}

	public double darCostoDistancia()
	{
		return costoDistancia;
	}

	public void cambiarCostoDistancia(double param)
	{
		costoDistancia = param;
	}
	
	public double darCostoTiempo()
	{
		return costoTiempo;
	}

	public void cambiarCostoTiempo(double param)
	{
		costoTiempo = param;
	}
	
	public double darCostoVelocidad()
	{
		return costoVelocidad;
	}

	public void cambiarCostoVelocidad(double param)
	{
		costoVelocidad = param;
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
	
    public int either() {
        return destino;
    }

    public int other(int vertex) {
        if      (vertex == origen) return destino;
        else if (vertex == destino) return origen;
        else throw new IllegalArgumentException("Illegal endpoint");
    }
}