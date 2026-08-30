package clases;

public enum ColorPelo {
    COLORADO("Colorado"),
    NEGRO("Negro"),
    AMARILLO("Amarillo");

    private final String descripcion;

    ColorPelo(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
