package controller;

import java.util.InputMismatchException;
import java.util.Scanner;

import model.logic.MVCModelo;
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

				modelo.prim();
				
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