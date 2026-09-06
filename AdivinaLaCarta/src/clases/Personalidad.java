package clases;

/*
 * Personalidad de una maquina: define cuando decide dejar de preguntar y
 * arriesgar un nombre.
 *
 * El profesor pidio que la maquina "se la juegue" cada ciertas preguntas en
 * lugar de esperar a tener certeza absoluta. Esa decision tambien es Greedy:
 * en el turno actual la maquina compara dos opciones y elige la que le
 * conviene ahora, sin reconsiderar turnos anteriores.
 *
 *   - Preguntar: gasta el turno pero garantiza reducir el peor caso.
 *   - Arriesgar: gasta el turno y gana con probabilidad 1/k, donde k es la
 *     cantidad de candidatos que quedan.
 *
 * Cada personalidad fija cuanto tolera esperar y desde que probabilidad
 * considera que vale la pena jugarsela.
 */
public enum Personalidad {
    CAUTELOSA("Cautelosa", 4, 0.50),
    NORMAL("Normal", 3, 0.33),
    AUDAZ("Audaz", 2, 0.20);

    private final String nombre;
    private final int preguntasEntreApuestas;
    private final double umbralRiesgo;

    Personalidad(String nombre, int preguntasEntreApuestas, double umbralRiesgo) {
        this.nombre = nombre;
        this.preguntasEntreApuestas = preguntasEntreApuestas;
        this.umbralRiesgo = umbralRiesgo;
    }

    public String getNombre() {
        return nombre;
    }

    /* Cada cuantas preguntas la maquina se plantea arriesgar. */
    public int getPreguntasEntreApuestas() {
        return preguntasEntreApuestas;
    }

    /* Probabilidad minima de acierto que la maquina exige para apostar. */
    public double getUmbralRiesgo() {
        return umbralRiesgo;
    }

    /*
     * Con k candidatos la probabilidad de acertar a ciegas es 1/k.
     * La maquina apuesta solo si esa probabilidad llega a su umbral.
     */
    public boolean valeLaPena(int cantidadCandidatos) {
        return (1.0 / cantidadCandidatos) >= umbralRiesgo;
    }

    public int candidatosMaximosParaApostar() {
        return (int) Math.floor(1.0 / umbralRiesgo);
    }
}
