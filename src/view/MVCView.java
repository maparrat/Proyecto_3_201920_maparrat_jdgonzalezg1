package view;

public class MVCView 
{
	/**
	 * Metodo constructor
	 */
	public MVCView()
	{}

	/**
	 * Método que imprime el menú por consola
	 */
	public void printMenu()
	{
		System.out.println("1. Cargar grafo de los archivos de texto");
		System.out.println("2. Persistir grafo en esquema JSON");
		System.out.println("3. Cargar grafo del archivo JSON generado");
		System.out.println("4. Consultar la cantidad de componentes conectados en el grafo");
		System.out.println("5. Graficar el grafo");
		System.out.println("6. Exit");
		System.out.println("Dar el numero de opcion a resolver, luego oprimir tecla Return: (e.g., 1):");
	}
}