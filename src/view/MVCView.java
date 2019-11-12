package view;

public class MVCView 
{
	/**
	 * Metodo constructor
	 */
	public MVCView()
	{}

	/**
	 * M�todo que imprime el men� por consola
	 */
	public void printMenu()
	{
		System.out.println("1. Cargar grafo de los archivos de texto");
		System.out.println("2. Persistir grafo en esquema JSON");
		System.out.println("3. Cargar grafo del archivo JSON generado");
		System.out.println("4. Consultar la cantidad de componentes conectados en el grafo");
		System.out.println("5. 4A � Encontrar el	camino de costo m�nimo Uber time");
		System.out.println("6. 5A � Determinar	los	n v�rtices	con	menor velocidad promedio	en	la	ciudad	de	Bogot�.");
		System.out.println("7. 6A �Calcular	 un	 �rbol	 de	 expansi�n	 m�nima PRIM	 (MST)");
		System.out.println("8. 7B � Encontrar el	camino de costo m�nimo Haversine");
		System.out.println("9. 8B � Encontrar v�rtices	son	alcanzables	para	un	tiempo	T");
		System.out.println("10.9B � Calcular	 un	 �rbol	 de	 expansi�n	 m�nima	Kruskal  (MST)	");
		System.out.println("11.10C � Construir un grafo simplificado");
		System.out.println("12.11C � Encontrar el	camino de costo m�nimo (algoritmo	 de	 Dijkstra)");
		System.out.println("5.12C � A	partir	de	una	zona	origen calcular	los	caminos	de	menor	longitud");
		System.out.println("6. Exit");
		System.out.println("Dar el numero de opcion a resolver, luego oprimir tecla Return: (e.g., 1):");
	}
}