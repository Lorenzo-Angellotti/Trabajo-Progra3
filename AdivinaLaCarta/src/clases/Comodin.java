package clases;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;

public class Comodin {

    /*
     * Algoritmo Greedy: en cada turno elige la pregunta que minimiza el
     * mayor grupo que podria quedar. No reconsidera preguntas anteriores.
     * Si varias preguntas son igualmente buenas, elige una al azar.
     */
    public Pregunta elegirMejorPregunta(ArrayList<Personaje> candidatos,
                                        Pregunta[] preguntas,
                                        boolean[] preguntasUsadas,
                                        Random random,
                                        boolean mostrarProceso,
                                        PrintStream salida) {

        ArrayList<Pregunta> mejoresPreguntas = new ArrayList<>();
        int mejorPeorCaso = Integer.MAX_VALUE;

        if (mostrarProceso) {
            salida.println("Evaluacion GREEDY de las preguntas:");
        }

        for (int i = 0; i < preguntas.length; i++) {
            if (preguntasUsadas[i]) {
                continue;
            }

            Pregunta pregunta = preguntas[i];
            int cantidadSi = contarRespuestasSi(candidatos, pregunta);
            int cantidadNo = candidatos.size() - cantidadSi;
            int peorCaso = Math.max(cantidadSi, cantidadNo);
            int desequilibrio = Math.abs(cantidadSi - cantidadNo);
            boolean divide = cantidadSi > 0 && cantidadNo > 0;

            if (mostrarProceso) {
                salida.println("  " + pregunta.getTexto()
                        + " | si=" + cantidadSi
                        + ", no=" + cantidadNo
                        + ", peor caso=" + peorCaso
                        + ", desequilibrio=" + desequilibrio
                        + (divide ? "" : " | no divide candidatos"));
            }

            if (!divide) {
                continue;
            }

            if (peorCaso < mejorPeorCaso) {
                mejorPeorCaso = peorCaso;
                mejoresPreguntas.clear();
                mejoresPreguntas.add(pregunta);
            } else if (peorCaso == mejorPeorCaso) {
                mejoresPreguntas.add(pregunta);
            }
        }

        if (mejoresPreguntas.isEmpty()) {
            return null;
        }

        Pregunta elegida = mejoresPreguntas.get(
                random.nextInt(mejoresPreguntas.size()));

        if (mostrarProceso && mejoresPreguntas.size() > 1) {
            salida.println("Hay " + mejoresPreguntas.size()
                    + " preguntas optimas empatadas. Se elige una al azar.");
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
