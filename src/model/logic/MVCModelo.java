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

import javafx.geometry.VerticalDirection;
import model.data_structures.ArregloDinamico;
import model.data_structures.Bag;
import model.data_structures.DijkstraUndirectedSP;
import model.data_structures.DijkstraUndirectedSPdist;
import model.data_structures.Graph;
import model.data_structures.Haversine;
import model.data_structures.KruskalMST;
import model.data_structures.KruskalMSTtime;
import model.data_structures.KruskalMSTvel;

import model.data_structures.PrimMST;
import model.data_structures.PrimMSTtime;
import model.data_structures.PrimMSTvel;
import model.data_structures.Queue;
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

	private KruskalMST kruskalDistancia;

	private KruskalMSTtime kruskalTiempo;

	private KruskalMSTvel kruskalVelocidad;
	private DijkstraUndirectedSP dijkstra;
	private DijkstraUndirectedSPdist dijkstraDist;

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
	public void crearMapa(Graph pgrafo, String punto) throws Exception 
	{
		FileReader reader = new FileReader(new File("datai/index.txt"));
		BufferedReader bf = new BufferedReader(reader);

		BufferedWriter pf = new BufferedWriter(new PrintWriter(new File("datai/mapa"+punto+".txt")));

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

		for (int i = 0; i < pgrafo.arcos.darTamano(); i++)
		{
			Arco actual = (Arco) pgrafo.arcos.darElemento(i);

			Vertice vertice1 = (Vertice) pgrafo.getInfoVertex(actual.darOrigen());
			Vertice vertice2 = (Vertice) pgrafo.getInfoVertex(actual.darDest());

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

	public Vertice darVericeSegunId(int id)
	{
		return grafo.getInfoVertex(id);
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
	public Stack<Arco> caminodeCostoMinimoUber(double latOrig, double longOrig, double latDest, double longDest)
	{
		int ideOrigen = idMasCercano(latOrig, longOrig);
		int ideDestino = idMasCercano(latDest, longDest);

		dijkstraDist = new DijkstraUndirectedSPdist(grafo, ideOrigen);
		Graph <Integer, Vertice>pgrafo = new  Graph<>(100000);
		//Parte de graficar
		Stack<Arco> arcos = (Stack<Arco>) dijkstraDist.pathTo(ideDestino);
		while(arcos.isEmpty() == false)
		{
			Arco actual = arcos.pop();
			Vertice vactualorig = grafo.getInfoVertex(actual.darOrigen());
			Vertice vactualdes = grafo.getInfoVertex(actual.darDest());
			pgrafo.addVertex(vactualorig.darId(), vactualorig);
			pgrafo.addVertex(vactualdes.darId(), vactualdes);
			pgrafo.addEdge(actual.darOrigen(), actual.darDest(), actual.darCostoDistancia());
		}
		try {
			crearMapa(pgrafo, "4A");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (Stack<Arco>) dijkstraDist.pathTo(ideDestino);

	}

	public double darDistanciaDIJK(double latDes, double longDest)
	{
		int ideDestino = idMasCercano(latDes, longDest);
		return dijkstraDist.distTo(ideDestino);
	}

	public double darTiempoDIJK(double latOrig, double longOrig, double latDest, double longDest)
	{
		int ideOrigen = idMasCercano(latOrig, longOrig);
		int ideDestino = idMasCercano(latDest, longDest);

		dijkstra = new DijkstraUndirectedSP(grafo, ideOrigen);

		return dijkstra.distTo(ideDestino);
	}

	//5A
	public ArregloDinamico<Vertice> verticesMenorVel(int n)
	{
		Vertice[] ordenar = new Vertice[grafo.V()];
		int actualA = 0;
		for(int i = 0; i< grafo.size(); i++)
		{
			if(grafo.getInfoVertex(i) != null)
			{
				ordenar[actualA]= grafo.getInfoVertex(i);
				actualA++;
			}
		}

		//ordenamiento
		for (int i=1; i < ordenar.length; i++) {
			Vertice aux = ordenar[i];
			int j;
			for (j=i-1; j >=0 && ordenar[j].velocidadPromedio() > aux.velocidadPromedio(); j--)
			{
				ordenar[j+1] = ordenar[j];
			}
			ordenar[j+1] = aux;
		}
		ArregloDinamico<Vertice> respuesta = new ArregloDinamico<>(1);
		Graph< Integer, Vertice> pgrafo = new Graph<>(100000);
		for(int i= 0; i < n; i++)
		{
			//graficar

			pgrafo.addVertex(ordenar[i].darId(), ordenar[i]);
			ArregloDinamico<Arco> actual  = ordenar[i].darArcos();
			for(int j = 0 ; j<actual.darTamano(); j++)
			{
				Vertice verticeActual = grafo.getInfoVertex(actual.darElemento(j).darDest());
				pgrafo.addVertex(actual.darElemento(j).darDest(), verticeActual);
				pgrafo.addEdge(ordenar[i].darId(), actual.darElemento(j).darDest(), actual.darElemento(j).darCostoDistancia());
			}
			respuesta.agregar(ordenar[i]);
		}
		try {
			crearMapa(pgrafo, "5A");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return respuesta ;
	}

	//6A
	public Graph prim()
	{
		primDistancia = new PrimMST(grafo);
		Queue<Arco> arcosNuevo = (Queue<Arco>) primDistancia.edges();

		Graph<Integer, Vertice> nuevo = new Graph<>(250000);

		for(int i= 0; i < grafo.size(); i++)
		{
			Vertice actual = grafo.getInfoVertex(i);
			if(actual != null)
			{
				nuevo.addVertex(i, actual);
			}	
		}

		while(!arcosNuevo.isEmpty())
		{
			Arco actual = arcosNuevo.dequeue();
			nuevo.addEdge(actual.darOrigen(), actual.darDest(), actual.darCostoDistancia());
		}
		try {
			crearMapa(nuevo, "6A");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return nuevo;
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
		kruskalDistancia = new KruskalMST(grafo);
		kruskalDistancia.edges();
		kruskalDistancia.weight();

		kruskalTiempo = new KruskalMSTtime(grafo);
		kruskalTiempo.edges();
		kruskalTiempo.weight();

		kruskalVelocidad = new KruskalMSTvel(grafo);
		kruskalVelocidad.edges();
		kruskalVelocidad.weight();
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