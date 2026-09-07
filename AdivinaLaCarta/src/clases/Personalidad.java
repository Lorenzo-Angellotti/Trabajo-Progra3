package clases;


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

    
    public int getPreguntasEntreApuestas() {
        return preguntasEntreApuestas;
    }

    public double getUmbralRiesgo() {
        return umbralRiesgo;
    }

    
    public boolean valeLaPena(int cantidadCandidatos) {
        return (1.0 / cantidadCandidatos) >= umbralRiesgo;
    }

    public int candidatosMaximosParaApostar() {
        return (int) Math.floor(1.0 / umbralRiesgo);
    }
}
