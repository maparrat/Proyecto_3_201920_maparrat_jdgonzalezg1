package model.logic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.opencsv.CSVReader;
import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;

import model.data_structures.ArregloDinamico;
import model.data_structures.Bag;
import model.data_structures.Graph;
import model.data_structures.Haversine;
import model.data_structures.PrimMST;
import model.data_structures.PrimMSTtime;
import model.data_structures.PrimMSTvel;
import model.data_structures.Stack;

/**
 * Definicion del modelo del mundo
 */
public class MVCModelo{

	/**
	 * Atributos del modelo del mundo
	 */
	private Graph<Integer, Vertice> grafo;

	private ArregloDinamico<UBERTrip>[] viajes;

	private Haversine haver;
	
	private PrimMST primDistancia;
	
	private PrimMSTtime primTiempo;
	
	private PrimMSTvel primVelocidad;
	
	/**
	 * Constructor del modelo del mundo
	 */
	public MVCModelo()
	{
		grafo = new Graph<>(250000);
		viajes = new ArregloDinamico[1160];
		haver = new Haversine();

		for (int v = 0; v < 1160; v++) {
			viajes[v] = new ArregloDinamico(1);
		}
	}

	public void cargarGrafo() throws Exception
	{
		BufferedReader br = new BufferedReader(new FileReader(new File("datai/bogota_vertices.txt")));
		String linea = br.readLine();
		linea = br.readLine();

		while(linea != null)
		{
			String[] datos = linea.split(";");

			if(!datos.equals(""))
			{
				Vertice nuevo = new Vertice(Integer.parseInt(datos[0]), Double.parseDouble(datos[1]), Double.parseDouble(datos[2]), Integer.parseInt(datos[3]));

				grafo.addVertex(Integer.parseInt(datos[0]), nuevo);
			}

			linea = br.readLine();
		}
		br.close();

		br = new BufferedReader(new FileReader(new File("datai/bogota_arcos.txt")));
		linea = br.readLine();

		while(linea != null)
		{
			String[] datos = linea.split(" ");

			for (int i = 1; i < datos.length; i++)
			{
				double costoTiempo = 0;
				if(grafo.getInfoVertex(Integer.parseInt(datos[0])) != null)
				{
					ArregloDinamico<UBERTrip> x = viajes[grafo.getInfoVertex(Integer.parseInt(datos[0])).darMID()-1];
					int denominador = 0;
					for (int j = 0; j < x.darCapacidad(); j++)
					{
						if(x.darElemento(j) != null && x.darElemento(j).darDatosViaje()[1] == Integer.parseInt(datos[i]))
						{
							costoTiempo += x.darElemento(j).darDatosViaje()[3];
							denominador ++;
						}
					}

					if(denominador == 0)
					{						
						if(grafo.getInfoVertex(Integer.parseInt(datos[0])) != null && grafo.getInfoVertex(Integer.parseInt(datos[i])) != null && grafo.getInfoVertex(Integer.parseInt(datos[0])).darMID() == grafo.getInfoVertex(Integer.parseInt(datos[i])).darMID())
						{
							costoTiempo = 10;
						}
						else
						{
							costoTiempo = 100;
						}
					}
					else
					{
						costoTiempo = (costoTiempo / denominador);
					}
					grafo.addEdge(Integer.parseInt(datos[0]), Integer.parseInt(datos[i]), costoTiempo);
				}
			}

			linea = br.readLine();
			System.out.println(Integer.parseInt(datos[0]));
		}
		br.close();
	}

	public void cargarArchivoCSVWeekly() throws Exception
	{
		if(viajes[0].darTamano() == 0 && viajes[1160-1].darTamano() == 0)
		{
			boolean primeraLectura = true;

			CSVReader reader = new CSVReader(new FileReader("data/bogota-cadastral-2018-1-WeeklyAggregate.csv"));

			for(String[] line: reader)
			{
				if(!primeraLectura)
				{
					UBERTrip dato = new UBERTrip(Short.parseShort(line[0]), Short.parseShort(line[1]), Short.parseShort(line[2]), Float.parseFloat(line[3]), Float.parseFloat(line[4]), Float.parseFloat(line[5]), Float.parseFloat(line[6]));
					int x = Short.parseShort(line[0]);
					viajes[x-1].agregar(dato);
				}
				primeraLectura = false;
			}
			reader.close();
		}
	}	

	//----------------------------
	//METODOS
	//----------------------------

	public int darNumeroVertices()
	{
		return grafo.V();
	}

	public int darNumeroArcos()
	{
		return grafo.E();
	}

	public void escribirJSON()
	{
		JSONArray listaVertices = new JSONArray();

		for (int i = 0; i < grafo.size(); i++)
		{
			Vertice actual = grafo.getInfoVertex(i);

			if(actual != null)
			{
				JSONObject datosVertice = new JSONObject();
				datosVertice.put("id", actual.darId());
				datosVertice.put("longitud", actual.darLongitud());
				datosVertice.put("latitud", actual.darLatitud());
				datosVertice.put("MOVEMENT_ID", actual.darMID());

				JSONObject vertice = new JSONObject(); 
				vertice.put("vertice", datosVertice);

				listaVertices.add(vertice);
			}
		}

		try (FileWriter file1 = new FileWriter(new File("datai/vertices.json"))) {

			file1.write(listaVertices.toJSONString());
			file1.flush();

		} catch (IOException e)
		{e.printStackTrace();}

		JSONArray listaArcos = new JSONArray();

		for (int i = 0; i < grafo.arcos.darTamano(); i++)
		{
			Arco actual = grafo.arcos.darElemento(i);

			JSONObject datosArco = new JSONObject();
			datosArco.put("origen", actual.darOrigen());
			datosArco.put("destino", actual.darDest());
			datosArco.put("costoDistancia", actual.darCostoDistancia());
			datosArco.put("costoTiempo", actual.darCostoTiempo());
			datosArco.put("costoVelocidad", actual.darCostoVelocidad());

			JSONObject arco = new JSONObject(); 
			arco.put("arco", datosArco);

			listaArcos.add(arco);
			System.out.println(i);
		}

		try (FileWriter file2 = new FileWriter(new File("datai/arcos.json"))) {

			file2.write(listaArcos.toJSONString());
			file2.flush();

		} catch (IOException e)
		{e.printStackTrace();}		
	}

	public void leerJSON()
	{
		grafo = new Graph<>(250000);

		JSONParser jsonParser = new JSONParser();

		try (FileReader reader = new FileReader("datai/vertices.json"))
		{
			Object obj = jsonParser.parse(reader);

			JSONArray array = (JSONArray) obj;

			for(int i = 0; i < array.size(); i++)
			{
				JSONObject actual = (JSONObject) array.get(i);
				JSONObject verticeActual = (JSONObject) actual.get("vertice");

				Vertice nuevo = new Vertice(((Long)verticeActual.get("id")).intValue(), (double)verticeActual.get("longitud"), (double)verticeActual.get("latitud"), ((Long)verticeActual.get("MOVEMENT_ID")).intValue());

				grafo.addVertex(((Long)verticeActual.get("id")).intValue(), nuevo);
			}
		}
		catch (Exception e)
		{e.printStackTrace();}

		ArregloDinamico<Arco> arcosCargados = new ArregloDinamico<>(1);

		jsonParser = new JSONParser();

		try (FileReader reader = new FileReader("datai/arcos.json"))
		{
			Object obj = jsonParser.parse(reader);

			JSONArray array = (JSONArray) obj;

			for(int i = 0; i < array.size(); i++)
			{
				JSONObject actual = (JSONObject) array.get(i);
				JSONObject arcoActual = (JSONObject) actual.get("arco");

				Arco nuevo = new Arco(((Long)arcoActual.get("origen")).intValue(), ((Long)arcoActual.get("destino")).intValue(), (double)arcoActual.get("costoDistancia"), (double)arcoActual.get("costoTiempo"), (double)arcoActual.get("costoVelocidad"));

				grafo.addEdge(((Long)arcoActual.get("origen")).intValue(), ((Long)arcoActual.get("destino")).intValue(), (double)arcoActual.get("costoTiempo"));
				arcosCargados.agregar(nuevo);
			}
		}
		catch (Exception e)
		{e.printStackTrace();}		

		grafo.arcos = arcosCargados;
	}

	public void crearMapa() throws Exception 
	{
		FileReader reader = new FileReader(new File("datai/index.txt"));
		BufferedReader bf = new BufferedReader(reader);

		BufferedWriter pf = new BufferedWriter(new PrintWriter(new File("datai/mapa.txt")));

		boolean llego = false;
		while(llego == false)
		{
			String lineaActual = bf.readLine();
			pf.write(lineaActual + "\n");

			if(lineaActual.equals("//kiwi"))
			{
				llego = true;

			}
		}		

		for (int i = 0; i < grafo.arcos.darTamano(); i++)
		{
			Arco actual = grafo.arcos.darElemento(i);

			Vertice vertice1 = grafo.getInfoVertex(actual.darOrigen());
			Vertice vertice2 = grafo.getInfoVertex(actual.darDest());

			if(vertice1.darLatitud() < 4.621360 && vertice1.darLatitud() > 4.597714 && vertice1.darLongitud() < -74.062707 && vertice1.darLongitud() > -74.094723 && vertice2.darLatitud() < 4.621360 && vertice2.darLatitud() > 4.597714 && vertice2.darLongitud() < -74.062707 && vertice2.darLongitud() > -74.094723)
			{
				pf.write("line = [{lat: " + vertice1.darLatitud() + ", lng: " + vertice1.darLongitud() + "},{lat: " + vertice2.darLatitud() + ", lng: " + vertice2.darLongitud() + "}];\n");
				pf.write("path = new google.maps.Polyline({path: line, strokeColor: '#FF0000', strokeWeight: 2});\n");
				pf.write("path.setMap(map);\n");
			}
		}

		boolean acabo = false;
		while(acabo == false)
		{
			String lineaActual = bf.readLine();
			pf.write(lineaActual + "\n");

			if(lineaActual.equals("//acabe"))
			{
				acabo = true;
			}
		}

		bf.close();
		pf.close();
	}

	public int darCantidadCC()
	{
		return grafo.cc();
	}

	//-------------------------------------
	//Parte Inicial
	//-------------------------------------
	//3
	public int idMasCercano(double pLatitud, double pLongitud)
	{
		int respuesta = -1;
		double menorDistancia = Double.POSITIVE_INFINITY;
		
		for (int i = 0; i < grafo.size(); i++)
		{
			Vertice actual = grafo.getInfoVertex(i);
			
			if(actual != null)
			{
				double distanciaActual = haver.distance(pLatitud, pLongitud, actual.darLatitud(), actual.darLongitud());
				if(distanciaActual < menorDistancia)
				{
					menorDistancia = distanciaActual;
					respuesta = i;
				}
			}
		}
		
		return respuesta;
	}
	
	//--------------------------------------
	//A
	//--------------------------------------
	//4A
	public ArregloDinamico<Double> caminodeCostoMinimoUber(double latDes,double lonDes,double latorig,double lonOrig)
	{
		return null;
	}

	//5A
	public ArregloDinamico<Vertice> verticesMenorVel(int n)
	{
		return null;
	}

	//6A
	public void prim()
	{
		primDistancia = new PrimMST(grafo);
		primDistancia.edges();
		primDistancia.weight();
		
		primTiempo = new PrimMSTtime(grafo);
		primTiempo.edges();
		primTiempo.weight();
		
		primVelocidad = new PrimMSTvel(grafo);
		primVelocidad.edges();
		primVelocidad.weight();
	}

	//-------------------------------------
	//B
	//-------------------------------------
	//7B
	public ArregloDinamico<Double> costoMinimoHarversine(double latDes,double lonDes,double latorig,double lonOrig)
	{
		return null;
	}

	//8B
	public ArregloDinamico<Vertice> verticesAlcanzables(double Tiempo,double latorig,double lonOrig)
	{
		return null;
	}

	//9B
	public void kruskal()
	{

	}

	//----------------------------------------
	//C
	//----------------------------------------
	//10C
	public void grafoSimplificado()
	{

	}

	//11C
	public ArregloDinamico<Vertice> dijkstra(int idOrigen, int idDestino)
	{
		return null;
	}

	//12C
	public ArregloDinamico<Vertice> camninosMenorLong(int idOrigen)
	{
		return null;
	}
}