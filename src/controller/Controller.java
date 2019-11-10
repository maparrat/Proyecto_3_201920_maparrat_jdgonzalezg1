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
					modelo.cargarGrafo();
					System.out.println("Grafo cargado");
					System.out.println("Cantidad de vertices cargados: " + modelo.darNumeroVertices());
					System.out.println("Cantidad de arcos cargados: " + modelo.darNumeroArcos() + "\n---------");
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
					System.out.println("El grafo se guardó en formato JSON\n---------");
				}
				catch (Exception e)
				{
					System.out.println("No se pudo persistir el grafo.\n---------");
					e.printStackTrace();
				}

			case 3:

				try
				{
					modelo.leerJSON();
					System.out.println("Grafo cargado");
					System.out.println("Cantidad de vertices cargados: " + modelo.darNumeroVertices());
					System.out.println("Cantidad de arcos cargados: " + modelo.darNumeroArcos() + "\n---------");
				}
				catch(Exception e)
				{					
					System.out.println("Se produjo un error al cargar el grafo.");
					e.printStackTrace();
				}
				
				break;

			case 4:

				//ZonaUBER zona = modelo.consultarZonaPorID(MID);

				//	System.out.println("Nombre: " + zona.darScanombre() + "\nPerímetro: " + (zona.darShape_leng()*100) + " kilómetros\nÁrea: " + (zona.darShape_area()*10000) + " kilómetros cuadrados\nNúmero de puntos: " + zona.darCoordinates().darNumeroElementos() + "\n---------");

				break;

			case 5:

				try
				{
					modelo.crearMapa();
					System.out.println("Se creo el mapa correctamente, favor cambiar el tipo de  archivo  a .html para visualizarlo");
				}
				catch (Exception e)
				{
					e.printStackTrace();
					System.out.println("Hubo un error creando el mapa");
				}
				break;

			case 6: 
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
}