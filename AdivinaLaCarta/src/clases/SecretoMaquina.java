package clases;

public class SecretoMaquina implements Respondedor {
    private final Personaje secreto;

    public SecretoMaquina(Personaje secreto) {
        this.secreto = secreto;
    }

    @Override
    public boolean responderPregunta(Pregunta pregunta) {
        return pregunta.evaluar(secreto);
    }

    @Override
    public boolean confirmarPersonaje(Personaje personaje) {
        return secreto.getId() == personaje.getId();
    }

    public Personaje revelarAlFinal() {
        return secreto;
    }
}
