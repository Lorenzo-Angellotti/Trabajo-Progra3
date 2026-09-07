package clases;

import java.io.PrintStream;
import java.util.ArrayList;

public class Comodin {

    
    public Pregunta elegirMejorPregunta(ArrayList<Personaje> candidatos,
                                        Pregunta[] preguntas,
                                        boolean[] preguntasUsadas,
                                        boolean mostrarProceso,
                                        PrintStream salida) {

        Pregunta elegida = null;
        int mejorPeorCaso = Integer.MAX_VALUE;
        boolean elegidaEsDeConsigna = false;
        int empatadas = 0;

        if (mostrarProceso) {
            salida.println("Evaluacion GREEDY "
                    + "(criterio: minimizar el peor caso)");
        }

        for (int i = 0; i < preguntas.length; i++) {
            if (preguntasUsadas[i]) {
                continue;
            }

            Pregunta pregunta = preguntas[i];
            int cantidadSi = contarRespuestasSi(candidatos, pregunta);
            int cantidadNo = candidatos.size() - cantidadSi;
            boolean divide = cantidadSi > 0 && cantidadNo > 0;
            int peorCaso = Math.max(cantidadSi, cantidadNo);

            if (mostrarProceso) {
                salida.println("  " + pregunta.getTexto()
                        + " | si=" + cantidadSi
                        + ", no=" + cantidadNo
                        + ", peor caso=" + peorCaso
                        + (pregunta.isDeConsigna() ? " [consigna]" : "")
                        + (divide ? "" : " | no divide candidatos"));
            }

            if (!divide) {
                continue;
            }

            boolean esDeConsigna = pregunta.isDeConsigna();
            boolean mejora = peorCaso < mejorPeorCaso;
            boolean desempataPorConsigna = peorCaso == mejorPeorCaso
                    && esDeConsigna && !elegidaEsDeConsigna;

            if (peorCaso == mejorPeorCaso) {
                empatadas++;
            }

            if (mejora) {
                mejorPeorCaso = peorCaso;
                elegida = pregunta;
                elegidaEsDeConsigna = esDeConsigna;
                empatadas = 1;
            } else if (desempataPorConsigna) {
                elegida = pregunta;
                elegidaEsDeConsigna = true;
            }
        }

        if (elegida == null) {
            return null;
        }

        if (mostrarProceso) {
            salida.println("Elegida: " + elegida.getTexto()
                    + " (peor caso=" + mejorPeorCaso
                    + (empatadas > 1
                        ? "; hubo " + empatadas + " empatadas, se desempato por "
                          + (elegidaEsDeConsigna
                             ? "filtro de la consigna"
                             : "orden declarado")
                        : "")
                    + ")");
        }

        return elegida;
    }

    public int contarRespuestasSi(ArrayList<Personaje> candidatos,
                                  Pregunta pregunta) {
        int cantidad = 0;

        for (Personaje personaje : candidatos) {
            if (pregunta.evaluar(personaje)) {
                cantidad++;
            }
        }

        return cantidad;
    }
}
