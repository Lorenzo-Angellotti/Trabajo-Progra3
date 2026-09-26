package funcionalidad;

import clases.Buscador;
import clases.JugadorMaquina;
import clases.Personaje;
import clases.Personalidad;
import clases.Pregunta;
import clases.Respondedor;
import clases.SecretoMaquina;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;

/** Estado y reglas de una partida humano contra maquina, sin dependencias de UI. */
public class Partida {
    public enum Estado { EN_CURSO, GANO_HUMANO, GANO_MAQUINA, CANCELADA }

    private final ArrayList<Personaje> personajes;
    private final ArrayList<Personaje> candidatosHumano;
    private final ArrayList<Pregunta> preguntasDisponibles;
    private final ArrayList<String> registro = new ArrayList<>();
    private final SecretoMaquina secretoMaquina;
    private final Respondedor secretoHumano;
    private final JugadorMaquina maquina;
    private final PrintStream salida;
    private final boolean mostrarProceso;
    private Estado estado = Estado.EN_CURSO;
    private int ronda = 1;
    private String mensajeFinal = "";

    public Partida(ArrayList<Personaje> personajes, Pregunta[] preguntas,
                   Personaje secretoMaquina, Respondedor secretoHumano,
                   Personalidad personalidad, boolean mostrarProceso, PrintStream salida) {
        this.personajes = new ArrayList<>(personajes);
        this.candidatosHumano = new ArrayList<>(personajes);
        this.preguntasDisponibles = new ArrayList<>();
        for (Pregunta pregunta : preguntas) {
            preguntasDisponibles.add(pregunta);
        }
        this.secretoMaquina = new SecretoMaquina(secretoMaquina);
        this.secretoHumano = secretoHumano;
        this.maquina = new JugadorMaquina("Maquina", personajes, preguntas.clone(), personalidad);
        this.mostrarProceso = mostrarProceso;
        this.salida = salida;
        registrar("Partida nueva. Rival: maquina " + personalidad.getNombre() + ".");
        registrar("La maquina eligio su personaje secreto.");
    }

    /** Variante con respuestas automaticas sobre el personaje elegido por el humano. */
    public static Partida conSecretoHumano(ArrayList<Personaje> personajes,
                                           Pregunta[] preguntas, int idHumano,
                                           Personalidad personalidad, Random random,
                                           PrintStream salida) {
        Personaje humano = new Buscador().buscarPorId(personajes, idHumano);
        if (humano == null) {
            throw new IllegalArgumentException("No existe personaje con ID " + idHumano);
        }
        ArrayList<Personaje> posiblesSecretos = new ArrayList<>(personajes);
        posiblesSecretos.remove(humano);
        if (posiblesSecretos.isEmpty()) {
            throw new IllegalArgumentException("Se necesitan al menos dos personajes.");
        }
        Personaje secreto = posiblesSecretos.get(random.nextInt(posiblesSecretos.size()));
        Partida partida = new Partida(personajes, preguntas, secreto,
                new SecretoMaquina(humano), personalidad, true, salida);
        partida.registrar("Elegiste: " + humano.getNombre());
        return partida;
    }

    /** Una accion valida del humano incluye la respuesta y el turno de la maquina. */
    public void preguntar(Pregunta pregunta) {
        exigirActiva();
        if (!preguntasDisponibles.remove(pregunta)) {
            throw new IllegalArgumentException("La pregunta no esta disponible.");
        }
        boolean respuesta = secretoMaquina.responderPregunta(pregunta);
        for (int i = candidatosHumano.size() - 1; i >= 0; i--) {
            if (pregunta.evaluar(candidatosHumano.get(i)) != respuesta) {
                candidatosHumano.remove(i);
            }
        }
        registrar("Preguntaste: " + pregunta.getTexto());
        registrar("Respuesta de la maquina: " + siNo(respuesta));
        registrar("Te quedan " + candidatosHumano.size() + " candidatos.");
        jugarTurnoMaquina();
    }

    public void adivinar(int id) {
        exigirActiva();
        Personaje supuesto = new Buscador().buscarPorId(personajes, id);
        if (supuesto == null) {
            throw new IllegalArgumentException("No existe personaje con ID " + id);
        }
        if (secretoMaquina.confirmarPersonaje(supuesto)) {
            finalizar(Estado.GANO_HUMANO, "Ganaste. El secreto de la maquina era "
                    + supuesto.getNombre() + ".");
            return;
        }
        candidatosHumano.remove(supuesto);
        registrar(supuesto.getNombre() + " no era el secreto de la maquina.");
        jugarTurnoMaquina();
    }

    private void jugarTurnoMaquina() {
        salida.println("--- Turno de la maquina (ronda " + ronda + ") ---");
        boolean gano = maquina.jugarTurno(new Respondedor() {
            @Override
            public boolean responderPregunta(Pregunta pregunta) {
                boolean respuesta = secretoHumano.responderPregunta(pregunta);
                registrar("La maquina pregunto: " + pregunta.getTexto()
                        + " Respuesta: " + siNo(respuesta));
                return respuesta;
            }

            @Override
            public boolean confirmarPersonaje(Personaje personaje) {
                boolean acierto = secretoHumano.confirmarPersonaje(personaje);
                registrar("La maquina arriesgo: " + personaje.getNombre()
                        + ". Resultado: " + (acierto ? "acerto" : "fallo"));
                return acierto;
            }
        }, mostrarProceso, salida);
        if (gano) {
            finalizar(Estado.GANO_MAQUINA, "Gano la maquina.");
        } else if (maquina.isSinCandidatos() || maquina.getCantidadCandidatos() == 0) {
            finalizar(Estado.CANCELADA,
                    "La partida se cancelo porque las respuestas quedaron inconsistentes.");
        } else {
            ronda++;
        }
    }

    public void cancelar() {
        if (isActiva()) {
            finalizar(Estado.CANCELADA, "Partida cancelada.");
        }
    }

    private void finalizar(Estado resultado, String mensaje) {
        estado = resultado;
        mensajeFinal = mensaje;
        registrar(mensaje);
        registrar("Secreto de la maquina: " + secretoMaquina.revelarAlFinal().getNombre());
    }

    private void exigirActiva() {
        if (!isActiva()) {
            throw new IllegalStateException("La partida ya termino.");
        }
    }

    private void registrar(String mensaje) {
        registro.add(mensaje);
        salida.println(mensaje);
    }

    private String siNo(boolean respuesta) { return respuesta ? "SI" : "NO"; }
    public boolean isActiva() { return estado == Estado.EN_CURSO; }
    public Estado getEstado() { return estado; }
    public int getRonda() { return ronda; }
    public String getMensajeFinal() { return mensajeFinal; }
    public int getCantidadCandidatosMaquina() { return maquina.getCantidadCandidatos(); }
    public boolean esCandidatoHumano(Personaje personaje) { return candidatosHumano.contains(personaje); }
    public ArrayList<Personaje> getCandidatosHumano() { return new ArrayList<>(candidatosHumano); }
    public ArrayList<Pregunta> getPreguntasDisponibles() { return new ArrayList<>(preguntasDisponibles); }
    public ArrayList<String> getRegistro() { return new ArrayList<>(registro); }
}
