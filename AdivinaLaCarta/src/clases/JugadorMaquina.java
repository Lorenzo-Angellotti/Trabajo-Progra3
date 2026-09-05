package clases;

import java.io.PrintStream;
import java.util.ArrayList;

/*
 * Una maquina virtual del juego.
 *
 * IMPORTANTE: esta clase no tiene ninguna fuente de azar. Todas sus decisiones
 * (que pregunta hacer, cuando arriesgar y a quien apostar) se derivan de un
 * criterio explicito y quedan registradas en la consola. Dos partidas con el
 * mismo secreto producen exactamente la misma secuencia de jugadas.
 */
public class JugadorMaquina {
    private final String nombre;
    private final ArrayList<Personaje> candidatos;
    private final Pregunta[] preguntas;
    private final boolean[] preguntasUsadas;
    private final Comodin selectorGreedy;
    private final Personalidad personalidad;
    private boolean sinCandidatos;
    private int preguntasDesdeUltimaApuesta;

    public JugadorMaquina(String nombre,
                          ArrayList<Personaje> personajes,
                          Pregunta[] preguntas) {
        this(nombre, personajes, preguntas, Personalidad.NORMAL);
    }

    public JugadorMaquina(String nombre,
                          ArrayList<Personaje> personajes,
                          Pregunta[] preguntas,
                          Personalidad personalidad) {
        this.nombre = nombre;
        this.candidatos = new ArrayList<>(personajes);
        this.preguntas = preguntas;
        this.preguntasUsadas = new boolean[preguntas.length];
        this.selectorGreedy = new Comodin();
        this.personalidad = personalidad;
        this.sinCandidatos = false;
        this.preguntasDesdeUltimaApuesta = 0;
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

        // Certeza total: no hay nada que arriesgar.
        if (candidatos.size() == 1) {
            return apostar(rival, salida, "SUPOSICION DIRECTA (un solo candidato)");
        }

        /*
         * Decision Greedy del turno: preguntar o jugarsela.
         * El turno se gasta en una cosa o en la otra, nunca en las dos.
         */
        if (decideArriesgar(mostrarProcesoCompleto, salida)) {
            return apostar(rival, salida,
                    "SE LA JUEGA (personalidad " + personalidad.getNombre() + ")");
        }

        Pregunta pregunta = selectorGreedy.elegirMejorPregunta(
                candidatos, preguntas, preguntasUsadas,
                mostrarProcesoCompleto, salida);

        if (pregunta == null) {
            salida.println("No quedan preguntas que separen candidatos.");
            return apostar(rival, salida, "SUPOSICION FORZADA");
        }

        int posicionPregunta = buscarPosicionPregunta(pregunta);
        preguntasUsadas[posicionPregunta] = true;
        preguntasDesdeUltimaApuesta++;

        boolean respuesta = rival.responderPregunta(pregunta);
        ArrayList<Personaje> descartados = filtrarCandidatos(pregunta, respuesta);

        salida.println("PREGUNTA ELEGIDA: " + pregunta.getTexto());
        salida.println("RESPUESTA: " + (respuesta ? "SI" : "NO"));
        salida.println("DESCARTADOS: " + mostrarPersonajes(descartados));
        salida.println("RESTANTES: " + mostrarCandidatos());

        return false;
    }

    /*
     * La maquina se la juega cuando se cumplen las dos condiciones de su
     * personalidad: que hayan pasado suficientes preguntas desde la ultima
     * apuesta y que la probabilidad de acertar (1/k) llegue a su umbral.
     */
    private boolean decideArriesgar(boolean mostrarProceso, PrintStream salida) {
        boolean toco = preguntasDesdeUltimaApuesta
                >= personalidad.getPreguntasEntreApuestas();
        boolean conviene = personalidad.valeLaPena(candidatos.size());
        double probabilidad = 100.0 / candidatos.size();

        if (mostrarProceso) {
            salida.printf("Decision de riesgo (%s): %d preguntas desde la "
                            + "ultima apuesta (necesita %d), "
                            + "probabilidad de acertar %.1f%% "
                            + "(necesita %.0f%%) -> %s%n",
                    personalidad.getNombre(),
                    preguntasDesdeUltimaApuesta,
                    personalidad.getPreguntasEntreApuestas(),
                    probabilidad,
                    personalidad.getUmbralRiesgo() * 100,
                    (toco && conviene) ? "SE LA JUEGA" : "PREGUNTA");
        }

        return toco && conviene;
    }

    /*
     * Que candidato arriesgar.
     *
     * Todos los candidatos que sobreviven son equiprobables: cada uno tiene
     * probabilidad 1/k de ser el secreto, asi que NINGUNA eleccion es mejor
     * que otra en terminos de acierto. Como no hay criterio que domine, se
     * toma el de menor ID, es decir el primero de la lista que dejo ordenada
     * el MergeSort.
     *
     * No se sortea: una apuesta al azar seria indefendible frente a la
     * pregunta "que criterio usaste". Elegir el primero de la lista ordenada
     * hace que la partida sea reproducible y que la decision se pueda auditar
     * desde la consola.
     *
     * Si la apuesta falla, ese candidato se descarta: la apuesta perdida igual
     * aporta informacion.
     */
    private boolean apostar(Respondedor rival, PrintStream salida, String motivo) {
        Personaje supuesto = elegirCandidatoAApostar();
        boolean acierto = rival.confirmarPersonaje(supuesto);

        if (candidatos.size() == 1) {
            salida.println(motivo + ": queda un unico candidato posible.");
        } else {
            salida.printf("%s: quedan %d candidatos equiprobables "
                            + "(%.1f%% cada uno), se apuesta al de menor ID.%n",
                    motivo, candidatos.size(), 100.0 / candidatos.size());
        }
        salida.println("  Es el " + supuesto.getId()
                + " - " + supuesto.getNombre() + "?"
                + (acierto ? " -> CORRECTA" : " -> INCORRECTA"));

        preguntasDesdeUltimaApuesta = 0;

        if (!acierto) {
            candidatos.remove(supuesto);
        }

        return acierto;
    }

    /* Menor ID = primero de la lista que ordeno el MergeSort. */
    private Personaje elegirCandidatoAApostar() {
        Personaje menor = candidatos.get(0);

        for (Personaje candidato : candidatos) {
            if (candidato.getId() < menor.getId()) {
                menor = candidato;
            }
        }

        return menor;
    }

    public Personalidad getPersonalidad() {
        return personalidad;
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
