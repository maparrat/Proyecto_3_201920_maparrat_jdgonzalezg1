package controller;

import java.util.InputMismatchException;
import java.util.Scanner;

import model.data_structures.Stack;
import model.logic.Arco;
import model.logic.MVCModelo;
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
	 * Hilo de ejecución del programa
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
					System.out.println("---------\nEl grafo se guardó en formato JSON\n---------");
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
					System.out.println("Debe ingresar un valor numérico\n---------");
					break;
				}

				int id = modelo.idMasCercano(latitud, longitud);

				if(id > 0)
				{
					System.out.println("Id del Vértice más cercano: " + id + "\n---------");
				}
				else
				{
					System.out.println("No hay vértices cargados.\n---------");
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
					System.out.println("Debe ingresar un valor numérico\n---------");
					break;
				}

				Stack<Arco> camino = modelo.caminodeCostoMinimoUber(alatitudD, alongitudD, alatitudO, alongitudO);
				
				if(camino == null)
				{
					System.out.println("No hay un camino entre estos vertices");
					break;
				}
				else
				{
					System.out.println("El total de vertices a recorrer es " + camino.size());
					
					Arco actual = camino.pop();
					Vertice origenPrimero = modelo.darVericeSegunId(actual.darOrigen());
					System.out.println("La información del vértice #"+ 1 + " es: ID" + origenPrimero.darId() +" , Latitud:"+ origenPrimero.darLatitud() +" , Longitud:"+origenPrimero.darLongitud());
					Vertice destinoPrimero = modelo.darVericeSegunId(actual.darDest());
					System.out.println("La información del vértice #"+ 2 + " es: ID" + destinoPrimero.darId() +" , Latitud:"+ destinoPrimero.darLatitud() +" , Longitud:"+destinoPrimero.darLongitud());
					
					int i = 3;
					while(camino.size() > 0)
					{
						actual = camino.pop();
						Vertice destino = modelo.darVericeSegunId(actual.darDest());
						System.out.println("La información del vértice #"+ i + " es: ID" + destino.darId() +" , Latitud:"+ destino.darLatitud() +" , Longitud:"+destino.darLongitud());
						i++;
					}
					
					System.out.println("El tiempo de viaje entre los vértices es: "+ modelo.darTiempoDIJK(alatitudD, alongitudD));
					System.out.println("La distancia de viaje entre los vértices es: "+ modelo.darDistanciaDIJK(alatitudD, alongitudD));
					
					break;
				}


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