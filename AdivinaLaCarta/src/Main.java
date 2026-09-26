import funcionalidad.Funcionalidad;
import interfaz.VentanaPrincipal;

public class Main {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("--swing")) {
            VentanaPrincipal.abrir();
            return;
        }

        Main juego = new Main();
        juego.iniciarJuego();
    }

    public void iniciarJuego() {
        Funcionalidad juego = new Funcionalidad();
        juego.iniciarJuego();
    }
}
