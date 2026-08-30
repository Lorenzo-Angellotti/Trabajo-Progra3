package clases;

/*
 * Esta interfaz evita que el jugador que adivina pueda leer directamente
 * la variable del personaje secreto.
 */
public interface Respondedor {
    boolean responderPregunta(Pregunta pregunta);

    boolean confirmarPersonaje(Personaje personaje);
}
