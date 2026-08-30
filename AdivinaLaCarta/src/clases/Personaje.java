package clases;

public class Personaje {

    private final String nombre;
    private final int id;
    private final boolean generoMasculino;
    private final boolean poderes;
    private final boolean capa;
    private final boolean mascara;
    private final boolean arma;
    private final boolean vuela;
    private final boolean lentes;
    private final boolean calvicie;
    private final ColorPelo colorPelo;
    private final boolean universoMarvel;
    private boolean elegido;

    public Personaje(String nombre, int id) {
        this(nombre, id, true, false, false, false, false, false,
                false, false, ColorPelo.NEGRO, true);
    }

    public Personaje(String nombre, int id, boolean generoMasculino,
                     boolean poderes, boolean capa, boolean mascara,
                     boolean arma, boolean vuela, boolean lentes,
                     boolean calvicie, ColorPelo colorPelo,
                     boolean universoMarvel) {
        this.nombre = nombre;
        this.id = id;
        this.generoMasculino = generoMasculino;
        this.poderes = poderes;
        this.capa = capa;
        this.mascara = mascara;
        this.arma = arma;
        this.vuela = vuela;
        this.lentes = lentes;
        this.calvicie = calvicie;
        this.colorPelo = colorPelo;
        this.universoMarvel = universoMarvel;
        this.elegido = false;
    }

    public String getNombre() {
        return nombre;
    }

    public int getId() {
        return id;
    }

    public boolean isGeneroMasculino() {
        return generoMasculino;
    }

    public boolean isPoderes() {
        return poderes;
    }

    public boolean isCapa() {
        return capa;
    }

    public boolean isMascara() {
        return mascara;
    }

    public boolean isArma() {
        return arma;
    }

    public boolean isVuela() {
        return vuela;
    }

    public boolean isLentes() {
        return lentes;
    }

    public boolean isCalvicie() {
        return calvicie;
    }

    public ColorPelo getColorPelo() {
        return colorPelo;
    }

    public boolean isUniversoMarvel() {
        return universoMarvel;
    }

    public boolean isElegido() {
        return elegido;
    }

    public void setElegido(boolean elegido) {
        this.elegido = elegido;
    }

    public String getGenero() {
        return generoMasculino ? "Masculino" : "Femenino";
    }

    public String getUniverso() {
        return universoMarvel ? "Marvel" : "DC";
    }

    public String getColorPeloVisible() {
        return calvicie ? "No aplicable" : colorPelo.getDescripcion();
    }

    public String mostrarResumen() {
        return "ID: " + id + " - " + nombre + " | " + getGenero()
                + " | " + getUniverso();
    }

    public String mostrarDetalle() {
        return mostrarResumen()
                + " | poderes=" + siNo(poderes)
                + ", capa=" + siNo(capa)
                + ", mascara=" + siNo(mascara)
                + ", arma=" + siNo(arma)
                + ", vuela=" + siNo(vuela)
                + ", lentes=" + siNo(lentes)
                + ", calvicie=" + siNo(calvicie)
                + ", pelo=" + getColorPeloVisible();
    }

    private String siNo(boolean valor) {
        return valor ? "si" : "no";
    }
}
