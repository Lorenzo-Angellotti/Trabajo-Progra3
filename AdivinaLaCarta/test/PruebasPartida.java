import clases.Personaje;
import clases.Personalidad;
import clases.Pregunta;
import clases.Respondedor;
import clases.SecretoMaquina;
import funcionalidad.Funcionalidad;
import funcionalidad.Partida;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class PruebasPartida {
    private final Funcionalidad datos = new Funcionalidad(new Scanner(""), new Random(1));
    private final PrintStream salida = new PrintStream(new ByteArrayOutputStream());
    private int pruebas;

    public static void main(String[] args) {
        new PruebasPartida().ejecutar();
    }

    private void ejecutar() {
        datos.prepararJuego();
        probarVictoriaHumana();
        probarPreguntaYValidaciones();
        probarVictoriaMaquina();
        probarCancelacion();
        probarRespuestasInconsistentes();
        probarCopiasDelEstado();
        probarSecretosDistintosYReinicio();
        probarConsola();
        System.out.println("OK - " + pruebas + " pruebas de Partida superadas.");
    }

    private Partida nueva() {
        ArrayList<Personaje> personajes = datos.getPersonajes();
        return new Partida(personajes, datos.getPreguntas(), personajes.get(0),
                new SecretoMaquina(personajes.get(1)), Personalidad.NORMAL, false, salida);
    }

    private void probarVictoriaHumana() {
        Partida partida = nueva();
        partida.adivinar(1);
        verificar(partida.getEstado() == Partida.Estado.GANO_HUMANO, "No gano el humano");
        verificar(partida.getRonda() == 1, "Se jugo un turno extra tras ganar");
        verificar(partida.getCantidadCandidatosMaquina() == 23, "La maquina jugo tras perder");
        esperarError(IllegalStateException.class, () -> partida.adivinar(1));
        pruebas++;
    }

    private void probarPreguntaYValidaciones() {
        Partida partida = nueva();
        Pregunta pregunta = datos.getPreguntas()[0];
        Personaje secreto = datos.getPersonajes().get(0);
        esperarError(IllegalArgumentException.class, () -> partida.adivinar(999));
        verificar(partida.getRonda() == 1, "Un ID invalido consumio un turno");
        partida.preguntar(pregunta);
        verificar(partida.getRonda() == 2, "No avanzo la ronda");
        verificar(partida.getPreguntasDisponibles().size() == datos.getPreguntas().length - 1,
                "No se marco la pregunta usada");
        for (Personaje personaje : datos.getPersonajes()) {
            verificar(partida.esCandidatoHumano(personaje)
                            == (pregunta.evaluar(personaje) == pregunta.evaluar(secreto)),
                    "Filtrado incorrecto: " + personaje.getNombre());
        }
        esperarError(IllegalArgumentException.class, () -> partida.preguntar(pregunta));
        verificar(partida.getRonda() == 2, "Una pregunta repetida consumio un turno");
        Partida fallida = nueva();
        fallida.adivinar(2);
        verificar(!fallida.esCandidatoHumano(datos.getPersonajes().get(1)),
                "No descarto una adivinanza incorrecta");
        verificar(fallida.getRonda() == 2, "No jugo la maquina tras una adivinanza incorrecta");
        pruebas++;
    }

    private void probarVictoriaMaquina() {
        ArrayList<Personaje> personajes = datos.getPersonajes();
        for (Personalidad personalidad : Personalidad.values()) {
            for (Personaje humano : personajes) {
                Partida partida = new Partida(personajes, datos.getPreguntas(), personajes.get(0),
                        new SecretoMaquina(humano), personalidad, false, salida);
                for (int i = 0; i < 40 && partida.isActiva(); i++) {
                    partida.adivinar(2);
                }
                verificar(partida.getEstado() == Partida.Estado.GANO_MAQUINA,
                        "La maquina no encontro a " + humano.getNombre());
            }
        }
        pruebas++;
    }

    private void probarCancelacion() {
        Partida partida = nueva();
        partida.cancelar();
        verificar(partida.getEstado() == Partida.Estado.CANCELADA, "No se cancelo");
        esperarError(IllegalStateException.class, () -> partida.preguntar(datos.getPreguntas()[0]));
        esperarError(IllegalStateException.class, () -> partida.adivinar(1));
        verificar(partida.getRonda() == 1, "Cancelar avanzo la ronda");
        pruebas++;
    }

    private void probarRespuestasInconsistentes() {
        Respondedor inconsistente = new Respondedor() {
            public boolean responderPregunta(Pregunta pregunta) { return false; }
            public boolean confirmarPersonaje(Personaje personaje) { return false; }
        };
        Partida partida = new Partida(datos.getPersonajes(), datos.getPreguntas(),
                datos.getPersonajes().get(0), inconsistente, Personalidad.NORMAL, false, salida);
        for (int i = 0; i < 40 && partida.isActiva(); i++) {
            partida.adivinar(2);
        }
        verificar(partida.getEstado() == Partida.Estado.CANCELADA,
                "Las respuestas inconsistentes no cancelaron la partida");
        pruebas++;
    }

    private void probarCopiasDelEstado() {
        Partida partida = nueva();
        partida.getCandidatosHumano().clear();
        partida.getPreguntasDisponibles().clear();
        partida.getRegistro().clear();
        verificar(partida.getCandidatosHumano().size() == 23, "Se alteraron los candidatos desde fuera");
        verificar(partida.getPreguntasDisponibles().size() == 12, "Se alteraron las preguntas desde fuera");
        verificar(!partida.getRegistro().isEmpty(), "Se altero el registro desde fuera");
        pruebas++;
    }

    private void probarSecretosDistintosYReinicio() {
        for (Personaje humano : datos.getPersonajes()) {
            Partida partida = Partida.conSecretoHumano(datos.getPersonajes(), datos.getPreguntas(),
                    humano.getId(), Personalidad.NORMAL, new Random(humano.getId()), salida);
            partida.adivinar(humano.getId());
            verificar(partida.getEstado() != Partida.Estado.GANO_HUMANO,
                    "Ambos secretos son iguales");
            partida.cancelar();
            Partida nueva = Partida.conSecretoHumano(datos.getPersonajes(), datos.getPreguntas(),
                    humano.getId(), Personalidad.NORMAL, new Random(1), salida);
            verificar(nueva.isActiva() && nueva.getRonda() == 1
                    && nueva.getCandidatosHumano().size() == 23
                    && nueva.getPreguntasDisponibles().size() == 12, "Reinicio incompleto");
        }
        pruebas++;
    }

    private void probarConsola() {
        Random random = new Random(7);
        Funcionalidad referencia = new Funcionalidad(new Scanner(""), random);
        referencia.prepararJuego();
        int id = referencia.getPersonajes().get(random.nextInt(23)).getId();
        String entrada = "1\n1\n\n2\n" + id + "\n0\n";
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream anterior = System.out;
        try {
            System.setOut(new PrintStream(bytes));
            new Funcionalidad(new Scanner(entrada), new Random(7)).iniciarJuego();
        } finally {
            System.setOut(anterior);
        }
        verificar(bytes.toString().contains("Ganaste."), "La consola no completo la partida compartida");
        verificar(bytes.toString().contains("Fin del juego."), "No regreso al menu de consola");
        pruebas++;
    }

    private void esperarError(Class<? extends RuntimeException> tipo, Runnable accion) {
        try {
            accion.run();
        } catch (RuntimeException error) {
            verificar(tipo.isInstance(error), "Error inesperado: " + error);
            return;
        }
        throw new AssertionError("Se esperaba " + tipo.getSimpleName());
    }

    private void verificar(boolean condicion, String mensaje) {
        if (!condicion) { throw new AssertionError(mensaje); }
    }
}
