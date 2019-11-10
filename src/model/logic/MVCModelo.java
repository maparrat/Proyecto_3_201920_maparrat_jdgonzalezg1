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

import model.data_structures.ArregloDinamico;
import model.data_structures.Graph;
import model.data_structures.Haversine;
import model.data_structures.Stack;

/**
 * Definicion del modelo del mundo
 */
public class MVCModelo{

	/**
	 * Atributos del modelo del mundo
	 */
	private Graph<Integer, Vertice> grafo;

	/**
	 * Constructor del modelo del mundo
	 */
	public MVCModelo()
	{
		grafo = new Graph<>(250000);
	}

	public void cargarGrafo() throws Exception
	{
		BufferedReader br = new BufferedReader(new FileReader(new File("data/bogota_vertices.txt")));
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

		br = new BufferedReader(new FileReader(new File("data/bogota_arcos.txt")));
		linea = br.readLine();

		while(linea != null)
		{
			String[] datos = linea.split(" ");

			for (int i = 1; i < datos.length; i++)
			{
				grafo.addEdge(Integer.parseInt(datos[0]), Integer.parseInt(datos[i]), 0);
			}

			linea = br.readLine();
		}
		br.close();
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
				System.out.println(actual.darId());
			}
		}

		try (FileWriter file1 = new FileWriter(new File("data/vertices.json"))) {

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
			datosArco.put("costo", actual.darCosto());

			JSONObject arco = new JSONObject(); 
			arco.put("arco", datosArco);

			listaArcos.add(arco);
			System.out.println(i);
		}

		try (FileWriter file2 = new FileWriter(new File("data/arcos.json"))) {

			file2.write(listaArcos.toJSONString());
			file2.flush();

		} catch (IOException e)
		{e.printStackTrace();}		
	}

	public void leerJSON()
	{
		grafo = new Graph<>(250000);

		JSONParser jsonParser = new JSONParser();

		try (FileReader reader = new FileReader("data/vertices.json"))
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

		try (FileReader reader = new FileReader("data/arcos.json"))
		{
			Object obj = jsonParser.parse(reader);

			JSONArray array = (JSONArray) obj;

			for(int i = 0; i < array.size(); i++)
			{
				JSONObject actual = (JSONObject) array.get(i);
				JSONObject arcoActual = (JSONObject) actual.get("arco");

				Arco nuevo = new Arco(((Long)arcoActual.get("origen")).intValue(), ((Long)arcoActual.get("destino")).intValue(), (double)arcoActual.get("costo"));

				grafo.addEdge(((Long)arcoActual.get("origen")).intValue(), ((Long)arcoActual.get("destino")).intValue(), (double)arcoActual.get("costo"));
				arcosCargados.agregar(nuevo);
			}
		}
		catch (Exception e)
		{e.printStackTrace();}		

		grafo.arcos = arcosCargados;
	}

	/**
	 * line = [{lat: 0, lng: 16},{lat: 2, lng: 16}];
      path = new google.maps.Polyline({path: line, strokeColor: '#FF0000', strokeWeight: 2});
      path.setMap(map);
	 * @throws Exception 
	 */	

	public void crearMapa() throws Exception 
	{
		FileReader reader = new FileReader(new File("data/index.txt"));
		BufferedReader bf = new BufferedReader(reader);

		BufferedWriter pf = new BufferedWriter(new PrintWriter(new File("data/mapa.txt")));

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
}