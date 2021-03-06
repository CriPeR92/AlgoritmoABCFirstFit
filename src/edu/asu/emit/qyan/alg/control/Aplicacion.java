package edu.asu.emit.qyan.alg.control;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import edu.asu.emit.qyan.alg.model.Path;
import edu.asu.emit.qyan.alg.model.VariableGraph;
import edu.asu.emit.qyan.alg.model.abstracts.BaseVertex;

public class Aplicacion {

	public static ArrayList<FuentesComida> fuentes = new ArrayList<>();
	public static VariableGraph graph = new VariableGraph("data/test_16");
	public static ArrayList<Float> pi = new ArrayList<>();
	public static ArrayList<String[]> caminos = new ArrayList<>();


	public static void main(String[] args) throws InterruptedException, IOException {

		crearArchivoCaminos();
		leerArchivoCaminos();

		crearFuenteDeComida(5);

		for (int i=0; i< 10; i++) {
			primerPaso(5);
			segundoPaso(5);
			tercerPaso(5);
		}

	}

	/**
	 * funcion para leer el archivo y guardar en memoria
	 * @throws IOException
	 */
	private static void leerArchivoCaminos() throws IOException {
		FileReader input = new FileReader("data/Kcaminos");
		BufferedReader bufRead = new BufferedReader(input);
		String linea = bufRead.readLine();

		while (linea != null) {
			String[] variables = linea.split("-");
			variables[2] = variables[2].replace(", [", ";[");
			variables[2] = variables[2].replace("[", "");
			variables[2] = variables[2].replace("]", "");
			variables[2] = variables[2].replace(", ", "");
			caminos.add(variables);
			linea = bufRead.readLine();
		}
	}

	/**
	 * Funcion para crear el archivo de los posibles caminos
	 * @throws IOException
	 */
	private static void crearArchivoCaminos() throws IOException {
		YenTopKShortestPathsAlg yenAlg = new YenTopKShortestPathsAlg(graph);
		PrintWriter writer = new PrintWriter("data/Kcaminos", "UTF-8");

		// en este for hay que poner la cantidad de vertices que tenemos
		for (int i = 0; i <= 5; i++) {
			for (int k = 0; k <= 5; k++) {
				if (i != k) {
					List<Path> shortest_paths_list = yenAlg.get_shortest_paths(graph.get_vertex(i), graph.get_vertex(k), 4);
					writer.println(i + "-" + k + "-" + shortest_paths_list.toString());
				}
			}
		}
		writer.close();
	}

	/**
	 * Funcion para crear una fuente de comida
	 */
	public static void crearFuenteDeComida(int cantFuente) throws IOException {

		//crear matriz inicial para todas las fuentes de comida
		// Matriz que representa la red igual al archivo test_16 que se va a utilar al tener los caminos.
		for (int i = 0; i<cantFuente ; i++) {
			int[] vertices = {0, 1, 2, 3, 4, 5};
			GrafoMatriz g = new GrafoMatriz(vertices);
			g.InicializarGrafo(g.grafo);
			g.agregarRuta(0, 1, 1, 3, 5);
			g.agregarRuta(1, 5, 1, 3, 5);
			g.agregarRuta(1, 3, 1, 3, 5);
			g.agregarRuta(1, 2, 1, 3, 5);
			g.agregarRuta(2, 3, 1, 3, 5);
			g.agregarRuta(2, 4, 1, 3, 5);
			g.agregarRuta(3, 5, 1, 3, 5);
			g.agregarRuta(4, 5, 1, 3, 5);

			fuentes.add(new FuentesComida(g));
		}

		FileReader input = new FileReader("data/conexiones");
		BufferedReader bufRead = new BufferedReader(input);

		String linea = bufRead.readLine();

		while (linea != null ) {

			if (linea.trim().equals("")) {
				linea = bufRead.readLine();
				continue;
			}
			String[] str_list = linea.trim().split("\\s*,\\s*");

			int origen = Integer.parseInt(str_list[0]);
			int destino = Integer.parseInt(str_list[1]);
			int fs = Integer.parseInt(str_list[2]);
			int tiempo = Integer.parseInt(str_list[3]);
			int id = Integer.parseInt(str_list[4]);

			int inicio = origen;
			int fin = destino;
			String listaCaminos = "";


			for (int k = 0; k < caminos.size(); k++) {
				if (caminos.get(k)[0].equals(str_list[0]) && caminos.get(k)[1].equals(str_list[1])) {
					listaCaminos = caminos.get(k)[2];
					break;
				}
			}

			for (int j = 0; j < cantFuente; j++) {
				BuscarSlot r = new BuscarSlot(fuentes.get(j).grafo, listaCaminos);
				resultadoSlot res = r.concatenarCaminos(fs,0);


				if (res !=null) {
					//guardar caminos utilizados
					fuentes.get(j).caminos.add(res.camino);
					fuentes.get(j).ids.add(id);

					System.out.println(res.toString());
					Asignacion asignar = new Asignacion(fuentes.get(j).grafo, res);
					asignar.marcarSlotUtilizados(id);
				}
				else {
					System.out.println("No se encontró camino posible.");
				}
			}
			linea = bufRead.readLine();
		}
		bufRead.close();

	}

	/**
	 * funcion para calcular los FS de todas las fuentes de comida
	 */

	public static void calcularFS(int fuentesComida) {

		int suma = 0;

		// for para recorrer todas las fuentes de comida
		for (int i = 0; i < fuentesComida; i++) {

			// for para recorrer las filas de un grafo
			for (int k = 0; k < fuentes.get(i).grafo.grafo.length; k++) {
				// for para recorrer las columnas de un grafo
				for (int j = 0; j < fuentes.get(i).grafo.grafo.length; j++) {
					// for para recorrer el array de listafs (cada enlace del grafo)
					for (int p = 0; p < fuentes.get(i).grafo.grafo[k][j].listafs.length; p++){
						if (fuentes.get(i).grafo.grafo[k][j].listafs[p].libreOcupado == 1) {
							suma = suma + 1;
						}
					}
				}
			}
			fuentes.get(i).fsUtilizados = (suma/2);
			suma = 0;

		}

		System.out.println("asd");

	}

	/**
	 * funcion para calcular los FS para una fuente de comida
	 */

	public static int calcularFsUno(int nroGrafo) {

		int suma = 0;

		// for para recorrer las filas de un grafo
		for (int k = 0; k < fuentes.get(nroGrafo).grafo.grafo.length; k++) {
			// for para recorrer las columnas de un grafo
			for (int j = 0; j < fuentes.get(nroGrafo).grafo.grafo.length; j++) {
				// for para recorrer el array de listafs (cada enlace del grafo)
				for (int p = 0; p < fuentes.get(nroGrafo).grafo.grafo[k][j].listafs.length; p++){
					if (fuentes.get(nroGrafo).grafo.grafo[k][j].listafs[p].libreOcupado == 1) {
						suma = suma + 1;
					}
				}
			}
		}
		//fuentes.get(nroGrafo).fsUtilizados = (suma/2);

		return suma/2;
	}

	/**
	 En el primer paso vamos a utilizar a las abejas empleadas para cambiar soluciones de las fuentes de comida si es que tienen
	 mejor resultado
	 **/
	public static void primerPaso(int cantFuentes) {
		calcularFS(cantFuentes);

		//calcular Vij para cada fuente de comida
		for (int i = 0; i < cantFuentes; i++) {
			Random rand = new Random();
			double alpha = (double)(Math.random() * 2 - 1);
			int j = rand.nextInt(fuentes.get(i).caminos.size()-1);
			int k = rand.nextInt(fuentes.get(i).grafo.nodos);
			borrarConexion(j, i);
		}

	}

	/**
	 * Primero vamos a eliminar la conexion actual para volver a buscar un lugar para la misma
	 * @param nroCamino
	 * @param nroGrafo
	 */

	public static void borrarConexion(int nroCamino, int nroGrafo) {

		String camino = String.valueOf(fuentes.get(nroGrafo).caminos.get(nroCamino));
//		String[] caminos = camino.split(":");

//		caminos[0] = caminos[0].replace(",", "");
//		caminos[0] = caminos[0].replace("[", "");
//		caminos[0] = caminos[0].replace("]", "");
//		caminos[0] = caminos[0].replace(" ", "");

		String caminoFinal = camino;
		int inicioSolicitud = (int)caminoFinal.charAt(0) - 48;
		int finSolicitud = (int)caminoFinal.charAt(caminoFinal.length()-1) - 48;

		int inicio = 0;
		int longitud = 0;
		Boolean bandera = true;

		for (int p = 0; p < caminoFinal.length()-1; p++) {

			int primer = (int)caminoFinal.charAt(p) - 48;
			int segundo = (int)caminoFinal.charAt(p+1) - 48;

			for (int k=0; k < fuentes.get(nroGrafo).grafo.grafo[0][0].listafs.length; k++) {
				if (fuentes.get(nroGrafo).grafo.grafo[primer][segundo].listafs[k].id == fuentes.get(nroGrafo).ids.get(nroCamino)) {
					if (p==0) {
						if (bandera) {
							inicio = k;
							bandera = false;
						}

						longitud = longitud + 1;
					}

					fuentes.get(nroGrafo).grafo.grafo[primer][segundo].listafs[k].id = 0;
					fuentes.get(nroGrafo).grafo.grafo[primer][segundo].listafs[k].libreOcupado = 0;
					fuentes.get(nroGrafo).grafo.grafo[segundo][primer].listafs[k].id = 0;
					fuentes.get(nroGrafo).grafo.grafo[segundo][primer].listafs[k].libreOcupado = 0;
				}
			}
		}


		Boolean reasignar = asginar(inicioSolicitud, finSolicitud, nroGrafo, longitud, fuentes.get(nroGrafo).ids.get(nroCamino));

		if (reasignar) {
			fuentes.get(nroGrafo).modificado++;
			fuentes.get(nroGrafo).caminos.remove(fuentes.get(nroGrafo).caminos.size()-1);
			fuentes.get(nroGrafo).ids.remove(fuentes.get(nroGrafo).ids.size()-1);
			// volver a como estaba
			for (int p = 0; p < caminoFinal.length()-1; p++) {

				int primer = (int)caminoFinal.charAt(p) - 48;
				int segundo = (int)caminoFinal.charAt(p+1) - 48;

				for (int k=0; k < fuentes.get(nroGrafo).grafo.grafo[0][0].listafs.length; k++) {
					if (k == inicio)
						for (int j=0; j < longitud; j++ ) {
							fuentes.get(nroGrafo).grafo.grafo[primer][segundo].listafs[k].id = fuentes.get(nroGrafo).ids.get(nroCamino);
							fuentes.get(nroGrafo).grafo.grafo[primer][segundo].listafs[k].libreOcupado = 1;
							fuentes.get(nroGrafo).grafo.grafo[segundo][primer].listafs[k].id = fuentes.get(nroGrafo).ids.get(nroCamino);
							fuentes.get(nroGrafo).grafo.grafo[segundo][primer].listafs[k].libreOcupado = 1;
						}
				}
			}

		} else {
			fuentes.get(nroGrafo).caminos.remove(nroCamino);
			fuentes.get(nroGrafo).ids.remove(nroCamino);
			fuentes.get(nroGrafo).fsUtilizados = calcularFsUno(nroGrafo);
			fuentes.get(nroGrafo).modificado = 0;
		}


	}

	/**
	 * Funcion para asignar una conexion nueva
	 * @param inicio
	 * @param fin
	 * @param nroGrafo
	 * @param cantFs
	 * @param id
	 * @return
	 */

	public static Boolean asginar(int inicio, int fin, int nroGrafo, int cantFs, int id) {


		YenTopKShortestPathsAlg yenAlg = new YenTopKShortestPathsAlg(graph);

		String listaCaminos = "";


		for (int k = 0; k < caminos.size(); k++) {
			if (caminos.get(k)[0].equals(inicio) && caminos.get(k)[1].equals(fin)) {
				listaCaminos = caminos.get(k)[2];
				break;
			}
		}

		BuscarSlot r = new BuscarSlot(fuentes.get(nroGrafo).grafo, listaCaminos);
		resultadoSlot res = r.concatenarCaminos(cantFs,1);


		if (res !=null) {
			//guardar caminos utilizados
				fuentes.get(nroGrafo).caminos.add(res.camino);
				fuentes.get(nroGrafo).ids.add(id);
			Asignacion asignar = new Asignacion(fuentes.get(nroGrafo).grafo, res);
			asignar.marcarSlotUtilizados(id);
		}
		else {
			System.out.println("No se encontró camino posible.");
		}

		int fsNuevo = calcularFsUno(nroGrafo);

		if (fsNuevo > fuentes.get(nroGrafo).fsUtilizados) {
			return true;
		}
		return false;

	}

	/**
	En el segundo paso vamos a seleccionar una fuente de comida utilizando la ruleta para cambiar su solucion y verificar si es mejor
	 **/
	public static void segundoPaso(int cantFuentes) {

		Random rand = new Random();
		float sumatoria = 0;
		float prueba;
		float suma = 0;

		//primero se calcula todos los pi de todas las fuentes de comida
		for (int i = 0; i<cantFuentes; i++) {
			sumatoria = sumatoria + fuentes.get(i).fsUtilizados;
		}

		// se agregan los valores de pi
		for (int j = 0; j<cantFuentes; j++) {
			prueba = fuentes.get(j).fsUtilizados / sumatoria;
			pi.add(prueba);
		}

		// se va cambiar un resultado dependiendo de la ruleta
		for (int p = 0; p < cantFuentes; p++) {

			float nectar = rand.nextFloat();

			for (int i=0; i < cantFuentes; i++) {
				suma = suma + pi.get(i);

				if (suma >= nectar) {
					int j = rand.nextInt(fuentes.get(i).caminos.size()-1);
					borrarConexion(j, i);
				}
			}

		}


	}

	/**
	 * En el tercer paso vamos a verificar si existen fuentes de comida abandonadas y vamos a guardar la mejor fuente de comida o solucion hasta el momento
	 */

	public static void tercerPaso(int cantFuentes) throws IOException {

		for (int i=0; i < cantFuentes; i++) {
			if (fuentes.get(i).modificado > 3) {

				fuentes.remove(i);

				int[] vertices = {0, 1, 2, 3, 4, 5};
				GrafoMatriz g = new GrafoMatriz(vertices);
				g.InicializarGrafo(g.grafo);
				g.agregarRuta(0, 1, 1, 3, 5);
				g.agregarRuta(1, 5, 1, 3, 5);
				g.agregarRuta(1, 3, 1, 3, 5);
				g.agregarRuta(1, 2, 1, 3, 5);
				g.agregarRuta(2, 3, 1, 3, 5);
				g.agregarRuta(2, 4, 1, 3, 5);
				g.agregarRuta(3, 5, 1, 3, 5);
				g.agregarRuta(4, 5, 1, 3, 5);

				fuentes.add(new FuentesComida(g));

				YenTopKShortestPathsAlg yenAlg = new YenTopKShortestPathsAlg(graph);
				FileReader input = new FileReader("data/conexiones");
				BufferedReader bufRead = new BufferedReader(input);

				String linea = bufRead.readLine();

				while (linea != null ) {

					if (linea.trim().equals("")) {
						linea = bufRead.readLine();
						continue;
					}
					String[] str_list = linea.trim().split("\\s*,\\s*");

					int origen = Integer.parseInt(str_list[0]);
					int destino = Integer.parseInt(str_list[1]);
					int fs = Integer.parseInt(str_list[2]);
					int tiempo = Integer.parseInt(str_list[3]);
					int id = Integer.parseInt(str_list[4]);

					int inicio = origen;
					int fin = destino;

					String listaCaminos = "";


					for (int k = 0; k < caminos.size(); k++) {
						if (caminos.get(k)[0].equals(inicio) && caminos.get(k)[1].equals(fin)) {
							listaCaminos = caminos.get(k)[2];
							break;
						}
					}


						BuscarSlot r = new BuscarSlot(fuentes.get(fuentes.size()-1).grafo, listaCaminos);
						resultadoSlot res = r.concatenarCaminos(fs,0);


						if (res !=null) {
							//guardar caminos utilizados
							fuentes.get(fuentes.size()-1).caminos.add(res.camino);
							fuentes.get(fuentes.size()-1).ids.add(id);

							System.out.println(res.toString());
							Asignacion asignar = new Asignacion(fuentes.get(fuentes.size()-1).grafo, res);
							asignar.marcarSlotUtilizados(id);
						}
						else {
							System.out.println("No se encontró camino posible.");
						}
					}

			}
		}


	}

	//con los caminos que tenemos en shortest_paths_list se va armar el vector de slot que tenemos en el grafoMatriz g.
	
	
	
}
