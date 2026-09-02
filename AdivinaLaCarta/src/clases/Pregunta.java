package clases;

public class Pregunta {
    public static final int GENERO_MASCULINO = 1;
    public static final int PODERES = 2;
    public static final int CAPA = 3;
    public static final int MASCARA = 4;
    public static final int ARMA = 5;
    public static final int VUELA = 6;
    public static final int LENTES = 7;
    public static final int CALVICIE = 8;
    public static final int PELO_COLORADO = 9;
    public static final int PELO_NEGRO = 10;
    public static final int PELO_AMARILLO = 11;
    public static final int UNIVERSO_MARVEL = 12;

    private final int codigo;
    private final String texto;

    public Pregunta(int codigo, String texto) {
        this.codigo = codigo;
        this.texto = texto;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getTexto() {
        return texto;
    }

    public boolean evaluar(Personaje personaje) {
        switch (codigo) {
            case GENERO_MASCULINO:
                return personaje.isGeneroMasculino();
            case PODERES:
                return personaje.isPoderes();
            case CAPA:
                return personaje.isCapa();
            case MASCARA:
                return personaje.isMascara();
            case ARMA:
                return personaje.isArma();
            case VUELA:
                return personaje.isVuela();
            /*
             * Los lentes se evaluan sobre la identidad civil del personaje,
             * no sobre el traje: Clark Kent usa lentes aunque Superman no.
             * El criterio esta enunciado en el texto de la pregunta para que
             * el jugador humano no tenga que adivinarlo.
             */
            case LENTES:
                return personaje.isLentes();
            case CALVICIE:
                return personaje.isCalvicie();
            case PELO_COLORADO:
                return !personaje.isCalvicie()
                        && personaje.getColorPelo() == ColorPelo.COLORADO;
            case PELO_NEGRO:
                return !personaje.isCalvicie()
                        && personaje.getColorPelo() == ColorPelo.NEGRO;
            case PELO_AMARILLO:
                return !personaje.isCalvicie()
                        && personaje.getColorPelo() == ColorPelo.AMARILLO;
            case UNIVERSO_MARVEL:
                return personaje.isUniversoMarvel();
            default:
                throw new IllegalArgumentException("Pregunta desconocida: " + codigo);
        }
    }
}
