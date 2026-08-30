package clases;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;

public class JugadorMaquina {
    private final String nombre;
    private final ArrayList<Personaje> candidatos;
    private final Pregunta[] preguntas;
    private final boolean[] preguntasUsadas;
    private final Random random;
    private final comodin selectorGreedy;
    private boolean sinCandidatos;

    public JugadorMaquina(String nombre,
                          ArrayList<Personaje> personajes,
                          Pregunta[] preguntas,
                          Random random) {
        this.nombre = nombre;
        this.candidatos = new ArrayList<>(personajes);
        this.preguntas = preguntas;
        this.preguntasUsadas = new boolean[preguntas.length];
        this.random = random;
        this.selectorGreedy = new comodin();
        this.sinCandidatos = false;
    }

    public boolean jugarTurno(Respondedor rival,
                              boolean mostrarProcesoCompleto,
                              PrintStream salida) {
        salida.println(nombre + " tiene " + candidatos.size()
                + " candidatos: " + mostrarCandidatos());

        if (candidatos.isEmpty()) {
            sinCandidatos = true;
            salida.println("No quedan candidatos. Las respuestas son inconsistentes.");
            return false;
        }

        if (candidatos.size() == 1) {
            Personaje supuesto = candidatos.get(0);
            boolean acierto = rival.confirmarPersonaje(supuesto);

            salida.println("SUPOSICION DIRECTA: " + supuesto.getId()
                    + " - " + supuesto.getNombre()
                    + (acierto ? " -> CORRECTA" : " -> INCORRECTA"));

            if (!acierto) {
                candidatos.remove(supuesto);
            }

            return acierto;
        }

        Pregunta pregunta = selectorGreedy.elegirMejorPregunta(
                candidatos, preguntas, preguntasUsadas, random,
                mostrarProcesoCompleto, salida);

        if (pregunta == null) {
            Personaje supuesto = candidatos.get(0);
            boolean acierto = rival.confirmarPersonaje(supuesto);

            salida.println("No quedan preguntas que separen candidatos.");
            salida.println("SUPOSICION DIRECTA: " + supuesto.getId()
                    + " - " + supuesto.getNombre()
                    + (acierto ? " -> CORRECTA" : " -> INCORRECTA"));

            if (!acierto) {
                candidatos.remove(supuesto);
            }

            return acierto;
        }

        int posicionPregunta = buscarPosicionPregunta(pregunta);
        preguntasUsadas[posicionPregunta] = true;

        boolean respuesta = rival.responderPregunta(pregunta);
        ArrayList<Personaje> descartados = filtrarCandidatos(pregunta, respuesta);

        salida.println("PREGUNTA ELEGIDA: " + pregunta.getTexto());
        salida.println("RESPUESTA: " + (respuesta ? "SI" : "NO"));
        salida.println("DESCARTADOS: " + mostrarPersonajes(descartados));
        salida.println("RESTANTES: " + mostrarCandidatos());

        return false;
    }

    private ArrayList<Personaje> filtrarCandidatos(Pregunta pregunta,
                                                   boolean respuesta) {
        ArrayList<Personaje> descartados = new ArrayList<>();

        for (int i = candidatos.size() - 1; i >= 0; i--) {
            Personaje personaje = candidatos.get(i);

            if (pregunta.evaluar(personaje) != respuesta) {
                descartados.add(0, personaje);
                candidatos.remove(i);
            }
        }

        return descartados;
    }

    private int buscarPosicionPregunta(Pregunta pregunta) {
        for (int i = 0; i < preguntas.length; i++) {
            if (preguntas[i].getCodigo() == pregunta.getCodigo()) {
                return i;
            }
        }

        return -1;
    }

    private String mostrarCandidatos() {
        return mostrarPersonajes(candidatos);
    }

    private String mostrarPersonajes(ArrayList<Personaje> lista) {
        StringBuilder texto = new StringBuilder("[");

        for (int i = 0; i < lista.size(); i++) {
            Personaje personaje = lista.get(i);
            texto.append(personaje.getId())
                    .append(":")
                    .append(personaje.getNombre());

            if (i < lista.size() - 1) {
                texto.append(", ");
            }
        }

        return texto.append("]").toString();
    }

    public int getCantidadCandidatos() {
        return candidatos.size();
    }

    public boolean isSinCandidatos() {
        return sinCandidatos;
    }
}
