package edu.asu.emit.qyan.alg.control;

import edu.asu.emit.qyan.alg.model.*;
import edu.asu.emit.qyan.alg.model.abstracts.BaseVertex;

import java.util.List;

public class BuscarSlot {

	public GrafoMatriz g;
	public String caminos;
	public String[] listaCaminos;
	
	BuscarSlot(GrafoMatriz grafomatriz, String caminos){
		this.g = grafomatriz;
		this.caminos = caminos;
		this.listaCaminos = null;
	}

	public void procesadoCaminos() {
		this.listaCaminos = this.caminos.split(";");
	}
	
	public resultadoSlot concatenarCaminos(int fs, int dato, int caminoAUsar) {
		boolean salirdelbucle;
		resultadoSlot resultfalso = null;
		int contador = 0;
	//	int[] vectorResultado = new int[g.grafo[0][0].listafs.length];
		resultadoSlot respuesta = new resultadoSlot();
		respuesta.vectorAsignacion = new int[g.grafo[0][0].listafs.length];
		procesadoCaminos();
		int res = 0;
		
		etiquetadesalida: {
		
		for (int a = 0; a < listaCaminos.length; a++) {

			for (int i = 0; i < respuesta.vectorAsignacion.length; i++) {
				respuesta.vectorAsignacion[i] = 0;
			}
			int random = (int)(Math.random() * listaCaminos.length);

			String cam;
			if (dato == 0) {
				cam = listaCaminos[random];
				respuesta.caminoUtilizado = random;

			} else if (dato == 3){
				System.out.println("la longitud del vector es:" + listaCaminos.length + "y queremos la posicion " + caminoAUsar);
				if (caminoAUsar >= listaCaminos.length || caminoAUsar < 0) {
					caminoAUsar = listaCaminos.length - 1;
				}
			    cam = listaCaminos[caminoAUsar];
			    a = listaCaminos.length;
            }
			else {
				cam = listaCaminos[a];
				respuesta.caminoUtilizado = a;
			}

			String[] caminos;
			caminos = cam.split(":");
			cam = caminos[0];
			System.out.println(cam);
			respuesta.camino = cam;
			
		//	GrafoMatriz posicion = new GrafoMatriz(g.cadenaVertices);
			
			//se concatena los vectores de los fs de cada enlace del primer camino examinado 
				for (int i=0; i < cam.length()-1; i++) {

					int k = (int) cam.charAt(i) - 48;
					int l = (int) cam.charAt(i+1) - 48;

					int n1 = g.posicionNodo(k);
					int n2 = g.posicionNodo(l);
					
				//	g.grafo[n1][n2].listafs[0].libreOcupado = 1;
				//	g.grafo[n1][n2].listafs[1].libreOcupado = 1;
				//	g.grafo[n1][n2].listafs[2].libreOcupado = 1;
				  for (int x = 0; x < g.grafo[n1][n2].listafs.length; x++) {

					  if (g.grafo[n1][n2].listafs[x].libreOcupado == 0 && respuesta.vectorAsignacion[x] == 0)
						respuesta.vectorAsignacion[x] = 0;
					  else {
						respuesta.vectorAsignacion[x] = 1;
					  }
				  }
				}
				
				// Una vez que tenemos el vector concatenado se recorre para saber si cumple con las condiciones.
				int contadorActual=0;
				int contadorFinal=0;
				int indiceActual=0;
				int indiceFinal=0;
				
				for (int i = 0; i < respuesta.vectorAsignacion.length; i++) {
					
					boolean ban = false;
					
					if (respuesta.vectorAsignacion[i] == 0) {
						
						contadorActual++;
						indiceActual = i;
						ban = true;
					}
					
					if (contadorActual >= fs ) {
						indiceFinal = indiceActual;
						contadorFinal = contadorActual;
						respuesta.indice = indiceFinal;
					    respuesta.contador = contadorFinal;
					    respuesta.cantidadfs = fs;
					    res = contadorFinal;
						break etiquetadesalida;
					}

					if (!ban) {
						contadorActual=0;
					}
				}
				
		}
		}
		
		if (res >= fs) {
			return respuesta;
		} else {
			return resultfalso;
		}
	}
}
