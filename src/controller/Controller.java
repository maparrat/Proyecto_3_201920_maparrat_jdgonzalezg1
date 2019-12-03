package controller;

import java.util.InputMismatchException;
import java.util.Scanner;

import model.data_structures.ArregloDinamico;
import model.data_structures.DijkstraUndirectedSPdist;
import model.data_structures.Graph;
import model.data_structures.Queue;
import model.data_structures.Stack;
import model.logic.Arco;
import model.logic.MVCModelo;
import model.logic.UBERTrip;
import model.logic.Vertice;
import view.MVCView;

public class Controller {

	/* Instancia del Modelo*/
	private MVCModelo modelo;

	/* Instancia de la Vista*/
	private MVCView view;

	/**
	 * Crear la vista y el modelo del proyecto
	 * @param capacidad tamaNo inicial del arreglo
	 */
	public Controller()
	{
		view = new MVCView();
		modelo = new MVCModelo();
	}

	/**
	 * Hilo de ejecuci�n del programa
	 */
	public void run() 
	{
		Scanner lector = new Scanner(System.in);
		boolean fin = false;

		while(!fin)
		{
			view.printMenu();

			String in;
			in = lector.next();

			int option;
			try
			{
				option = Integer.parseInt(in);
			}
			catch(NumberFormatException e)
			{
				option = 0;
			}

			switch(option){
			case 1:

				try
				{
					modelo.cargarArchivoCSVWeekly();
					modelo.cargarGrafo();
					imprimirInformacionGrafoCargado();
				}
				catch(Exception e)
				{					
					System.out.println("Se produjo un error al cargar el grafo.");
					e.printStackTrace();
				}

				break;

			case 2:

				try
				{
					modelo.escribirJSON();
					System.out.println("---------\nEl grafo se guard� en formato JSON\n---------");
				}
				catch (Exception e)
				{
					System.out.println("No se pudo persistir el grafo.\n---------");
					e.printStackTrace();
				}

				break;

			case 3:

				try
				{
					modelo.leerJSON();
					imprimirInformacionGrafoCargado();
				}
				catch(Exception e)
				{					
					System.out.println("Se produjo un error al cargar el grafo.");
					e.printStackTrace();
				}

				break;

			case 4:

				double latitud;
				double longitud;
				try
				{
					System.out.println("--------- \nDar latitud a buscar: ");
					latitud = lector.nextDouble();
					System.out.println("--------- \nDar longitud a buscar: ");
					longitud = lector.nextDouble();
				}
				catch(InputMismatchException e)
				{
					System.out.println("Debe ingresar un valor num�rico\n---------");
					break;
				}

				int id = modelo.idMasCercano(latitud, longitud);

				if(id >= 0)
				{
					System.out.println("Id del V�rtice m�s cercano: " + id + "\n---------");
				}
				else
				{
					System.out.println("No hay v�rtices cargados.\n---------");
				}

				break;

			case 5:

				double alatitudO;
				double alongitudO;
				double alatitudD;
				double alongitudD;
				try
				{
					System.out.println("--------- \nDar latitud de origen a buscar: ");
					alatitudO = lector.nextDouble();
					System.out.println("--------- \nDar longitud de origen a buscar: ");
					alongitudO = lector.nextDouble();
					System.out.println("--------- \nDar latitud de destino a buscar: ");
					alatitudD = lector.nextDouble();
					System.out.println("--------- \nDar longitud de destino a buscar: ");
					alongitudD = lector.nextDouble();
				}
				catch(InputMismatchException e)
				{
					System.out.println("Debe ingresar un valor num�rico\n---------");
					break;
				}

				Stack<Arco> camino = modelo.caminodeCostoMinimoUber(alatitudO, alongitudO, alatitudD, alongitudD);

				if(camino == null)
				{
					System.out.println("---------\nNo hay uningun camino entre estos vertices\n---------");
					break;
				}
				else
				{
					System.out.println("---------\nEl total de vertices a recorrer es: " + (camino.size() + 1) + "\n");

					Arco actual = camino.pop();
					Vertice origenPrimero = modelo.darVericeSegunId(actual.darOrigen());
					System.out.println("La informaci�n del v�rtice #" + 1 + " es:\nID: " + origenPrimero.darId() + "\nLatitud: " + origenPrimero.darLatitud() + "\nLongitud: " + origenPrimero.darLongitud() + "\n---------");
					Vertice destinoPrimero = modelo.darVericeSegunId(actual.darDest());
					System.out.println("La informaci�n del v�rtice #" + 2 + " es:\nID: " + destinoPrimero.darId() + "\nLatitud: " + destinoPrimero.darLatitud() + "\nLongitud: " + destinoPrimero.darLongitud() + "\n---------");

					int i = 3;
					while(camino.size() > 0)
					{
						actual = camino.pop();
						Vertice destino = modelo.darVericeSegunId(actual.darDest());
						System.out.println("La informaci�n del v�rtice #" + i + " es:\nID:" + destino.darId() + "\nLatitud: " + destino.darLatitud() + "\nLongitud: " + destino.darLongitud() + "\n---------");
						i++;
					}

					System.out.println("El tiempo de viaje entre los v�rtices es: " + modelo.darTiempoDIJK(alatitudO, alongitudO, alatitudD, alongitudD) + " segundos");
					System.out.println("La distancia de viaje entre los v�rtices es: " + modelo.darDistanciaDIJK(alatitudO, alongitudO, alatitudD, alongitudD) + " kil�metros\n---------");

					break;
				}

			case 6:

				int numero ; 
				try
				{
					System.out.println("--------- \nDar cantidad de vertices a retornar: ");
					numero = (int) lector.nextDouble();
				}
				catch(InputMismatchException e)
				{
					System.out.println("Debe ingresar un valor num�rico\n---------");
					break;
				}

				ArregloDinamico<Vertice> menores = modelo.verticesMenorVel(numero);

				for(int i = 0 ; i < menores.darTamano(); i++)
				{
					Vertice actual = menores.darElemento(i);
					System.out.println("La informaci�n del v�rtice #" + (i+1) + " es:");
					System.out.println("ID: " + actual.darId());
					System.out.println("Latitud: " + actual.darLatitud());
					System.out.println("Longitud: " + actual.darLongitud());
					System.out.println("Velocidad promedio: " + actual.velProm + "\n---------");
				}

				break;

			case 7:

				long startTime = System.currentTimeMillis(); // Medici�n tiempo actual

				Graph<Integer, Vertice> prim = modelo.prim();

				long endTime = System.currentTimeMillis(); // Medici�n tiempo actual
				long duration = endTime - startTime; // Duracion de ejecucion del algoritmo

				double costoTotal = 0;
				for(int i = 1; i <= prim.arcos.darTamano(); i++)
				{
					Arco actual = prim.arcos.darElemento(i);

					if(actual != null)
					{
						System.out.println("El arco #" + i + " va entre los v�rtices " + actual.darOrigen() + " y " + actual.darDest());
						costoTotal += actual.darCostoDistancia();
					}
				}

				System.out.println("---------\nLa duraci�n del algoritmo fue: " + duration + " milisegundos");

				System.out.println("El n�mero de v�rtices del grafo es: " + prim.V());

				System.out.println("El costo total del grafo es: " + costoTotal + " kil�metros\n---------");

				break;

			case 8:

				double blatitudO;
				double blongitudO;
				double blatitudD;
				double blongitudD;
				try
				{
					System.out.println("--------- \nDar latitud de origen a buscar: ");
					blatitudO = lector.nextDouble();
					System.out.println("--------- \nDar longitud de origen a buscar: ");
					blongitudO = lector.nextDouble();
					System.out.println("--------- \nDar latitud de destino a buscar: ");
					blatitudD = lector.nextDouble();
					System.out.println("--------- \nDar longitud de destino a buscar: ");
					blongitudD = lector.nextDouble();
				}
				catch(InputMismatchException e)
				{
					System.out.println("Debe ingresar un valor num�rico\n---------");
					break;
				}

				Stack<Arco> caminob = modelo.costoMinimoHarversine(blatitudO, blongitudO, blatitudD, blongitudD);

				if(caminob == null)
				{
					System.out.println("---------\nNo hay uningun camino entre estos vertices\n---------");
					break;
				}
				else
				{
					System.out.println("---------\nEl total de vertices a recorrer es: " + (caminob.size() + 1) + "\n");

					Arco actual = caminob.pop();
					Vertice origenPrimero = modelo.darVericeSegunId(actual.darOrigen());
					System.out.println("La informaci�n del v�rtice #" + 1 + " es:\nID: " + origenPrimero.darId() + "\nLatitud: " + origenPrimero.darLatitud() + "\nLongitud: " + origenPrimero.darLongitud() + "\n---------");
					Vertice destinoPrimero = modelo.darVericeSegunId(actual.darDest());
					System.out.println("La informaci�n del v�rtice #" + 2 + " es:\nID: " + destinoPrimero.darId() + "\nLatitud: " + destinoPrimero.darLatitud() + "\nLongitud: " + destinoPrimero.darLongitud() + "\n---------");

					int i = 3;
					while(caminob.size() > 0)
					{
						actual = caminob.pop();
						Vertice destino = modelo.darVericeSegunId(actual.darDest());
						System.out.println("La informaci�n del v�rtice #" + i + " es:\nID:" + destino.darId() + "\nLatitud: " + destino.darLatitud() + "\nLongitud: " + destino.darLongitud() + "\n---------");
						i++;
					}

					System.out.println("El tiempo de viaje entre los v�rtices es: " + modelo.darTiempoDIJK(blatitudO, blongitudO, blatitudD, blongitudD) + " segundos");
					System.out.println("La distancia de viaje entre los v�rtices es: " + modelo.darDistanciaDIJK(blatitudO, blongitudO, blatitudD, blongitudD) + " kil�metros\n---------");

				}

				break;

			case 9:

				double latitudO;
				double longitudO;
				double Tiempo;
				try
				{
					System.out.println("--------- \nDar latitud de origen a buscar: ");
					latitudO = lector.nextDouble();
					System.out.println("--------- \nDar longitud de origen a buscar: ");
					longitudO = lector.nextDouble();
					System.out.println("--------- \nDar tiempo m�ximo: ");
					Tiempo = lector.nextDouble();

				}
				catch(InputMismatchException e)
				{
					System.out.println("Debe ingresar un valor num�rico\n---------");
					break;
				}

				Queue<Vertice> vertices = modelo.verticesAlcanzables(Tiempo, latitudO, longitudO);

				if(vertices.size() == 0)
				{
					System.out.println("---------\nNo hay ningun v�rtice alcanzable desde las cordenadas ingresadas\n---------");
					break;
				}
				else
				{
					System.out.println("---------\nLos vertices son \n");
					int contador = 1;
					while(vertices.isEmpty() == false)
					{
						Vertice veactual = vertices.dequeue();
						System.out.println("---------\nEl vertice numero"+ contador);
						contador ++;
						System.out.println("---------\nID: "+veactual.darId());
						System.out.println("---------\nLatitud: "+veactual.darLatitud());
						System.out.println("---------\nLongitud: "+veactual.darLongitud());
					}


					break;
				}



			case 10:

				long startTime2 = System.currentTimeMillis(); // Medici�n tiempo actual

				Graph<Integer, Vertice> kruskal = modelo.kruskal();

				long endTime2 = System.currentTimeMillis(); // Medici�n tiempo actual
				long duration2 = endTime2 - startTime2; // Duracion de ejecucion del algoritmo

				double costoTotal2 = 0;
				for(int i = 1; i <= kruskal.arcos.darTamano(); i++)
				{
					Arco actual = kruskal.arcos.darElemento(i);

					if(actual != null)
					{
						System.out.println("El arco #" + i + " va entre los v�rtices " + actual.darOrigen() + " y " + actual.darDest());
						costoTotal2 += actual.darCostoDistancia();
					}
				}

				System.out.println("---------\nLa duraci�n del algoritmo fue: " + duration2 + " milisegundos");

				System.out.println("El n�mero de v�rtices del grafo es: " + kruskal.V());

				System.out.println("El costo total del grafo es: " + costoTotal2 + " kil�metros\n---------");
				System.out.println("---------\nLa cantidad de vertices fue: " + duration2 + " milisegundos");

				break;
			case 11:
				Graph<Integer,Vertice> grafo = modelo.grafoSimplificado();
				System.out.println("---------\nLa cantidad de vertices fue: " + grafo.V());
				System.out.println("---------\nLa cantidad de arcos fue: " + grafo.arcos.darTamano());
				break;
			case 12:


				int destino;
				int origen;

				try
				{
					System.out.println("--------- \nDar Id de origen a buscar: ");
					origen = (int) lector.nextDouble();
					System.out.println("--------- \nDar Id de destino a buscar: ");
					destino= (int) lector.nextDouble();
				}
				catch(InputMismatchException e)
				{
					System.out.println("Debe ingresar un valor num�rico\n---------");
					break;
				}

				long startTime4 = System.currentTimeMillis(); // Medici�n tiempo actual

				DijkstraUndirectedSPdist grafoC = modelo.dijkstraC(destino, origen);
				Stack<Arco> caminoc = (Stack<Arco>) grafoC.pathTo(destino);

				long endTime4 = System.currentTimeMillis(); // Medici�n tiempo actual
				long duration4 = endTime4 - startTime4; // Duracion de ejecucion del algoritmo

				System.out.println("---------\nLa duraci�n del algoritmo fue: " + duration4 + " milisegundos");

				System.out.println("El vertice 1 es: " + origen);

				int contador = 2;
				double tiempo = 0;
				while(caminoc.isEmpty() == false)
				{
					Arco actual=  caminoc.pop();
					System.out.println("El vertice " + contador + " es: " + actual.darDest());
					contador++;
					tiempo += actual.darCostoTiempo();
				}
				System.out.println("Su costo total en tiempo es: " + tiempo);
				System.out.println("El tiempo promedio entre las zonas dadas es: " + modelo.tiempoEntreDosZonas(origen, destino));

				break;
				
			case 13:

				int idC; 
				try
				{
					System.out.println("--------- \nDar id de origen: ");
					idC = (int) lector.nextDouble();
				}
				catch(InputMismatchException e)
				{
					System.out.println("Debe ingresar un valor num�rico\n---------");
					break;
				}
				long startTime3 = System.currentTimeMillis(); // Medici�n tiempo actual

				Queue<Arco> arcos = modelo.camninosMenorLong(idC);

				long endTime3 = System.currentTimeMillis(); // Medici�n tiempo actual
				long duration3 = endTime3 - startTime3; // Duracion de ejecucion del algoritmo

				System.out.println("El tiempo que toma el metodo es "+duration3 + " milisegundos");
				System.out.println("La cantidad de arcos fue "+arcos.size() );
				System.out.println("El vertice 1 es: " + idC);

				int contadorx = 2;

				while(arcos.isEmpty() == false)
				{
					Arco actual=  arcos.dequeue();
					System.out.println("El vertice " + contadorx + " es: " + actual.darDest());
					contadorx++;
				}


				break;

			case 14: 
				System.out.println("--------- \n Hasta pronto !! \n---------"); 
				lector.close();
				fin = true;
				break;

			default: 
				System.out.println("--------- \n Opcion Invalida !! \n---------");
				break;
			}
		}
	}

	public void imprimirInformacionGrafoCargado()
	{
		try
		{
			modelo.crearMapa();
			int numCC = modelo.darCantidadCC();

			System.out.println("---------\nGrafo cargado");
			System.out.println("Cantidad de vertices cargados: " + modelo.darNumeroVertices());
			System.out.println("Cantidad de arcos cargados: " + modelo.darNumeroArcos());
			System.out.println("Cantidad de componentes conectadas: " + numCC);
			System.out.println("Se creo el mapa correctamente en la carpeta /datai. Cambiar el tipo de archivo a .html para visualizarlo\n---------");	
		}
		catch (Exception e) {e.printStackTrace();}
	}	
}