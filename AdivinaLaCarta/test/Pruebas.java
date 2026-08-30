import clases.JugadorMaquina;
import clases.Personaje;
import clases.Personalidad;
import clases.Pregunta;
import clases.SecretoMaquina;
import clases.Buscador;
import funcionalidad.Funcionalidad;
import clases.Comodin;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class Pruebas {
    private int cantidadPruebas;

    public static void main(String[] args) {
        new Pruebas().ejecutar();
    }

    private void ejecutar() {
        Funcionalidad juego = new Funcionalidad(new Scanner(""), new Random(1));
        juego.prepararJuego();

        probarCantidadOrdenIdsYBusqueda(juego);
        probarOrdenamientoConVariasSemillas();
        probarTrazaDeInicializacion();
        probarFirmasDiferentes(juego);
        probarEleccionGreedy(juego);
        probarTodosLosSecretos(juego);
        probarPersonalidades(juego);
        probarTrazaCompleta(juego);

        System.out.println("OK - " + cantidadPruebas + " pruebas superadas.");
    }

    private void probarCantidadOrdenIdsYBusqueda(Funcionalidad juego) {
        ArrayList<Personaje> personajes = juego.getPersonajes();
        Buscador buscadorPersonajes = new Buscador();

        verificar(personajes.size() == 23, "Deben existir 23 personajes");

        // Tras la Etapa 2 (MergeSort) la lista debe quedar autoincremental.
        for (int i = 0; i < personajes.size(); i++) {
            Personaje personaje = personajes.get(i);

            verificar(personaje.getId() == i + 1,
                    "MergeSort no dejo la lista autoincremental: en la posicion "
                            + i + " hay el ID " + personaje.getId());

            Personaje encontrado = buscadorPersonajes.buscarPorId(
                    personajes, personaje.getId());
            verificar(encontrado == personaje,
                    "La busqueda DyC no encontro el ID " + personaje.getId());
        }

        verificar(buscadorPersonajes.buscarPorId(personajes, 99) == null,
                "Se encontro un ID inexistente");
        aprobada();
    }

    /*
     * La caja se baraja distinto en cada partida. MergeSort tiene que dejar
     * la lista autoincremental sin importar como haya salido el desorden.
     */
    private void probarOrdenamientoConVariasSemillas() {
        for (long semilla = 0; semilla < 200; semilla++) {
            Funcionalidad juego = new Funcionalidad(
                    new Scanner(""), new Random(semilla));
            juego.prepararJuego();
            ArrayList<Personaje> personajes = juego.getPersonajes();

            verificar(personajes.size() == 23,
                    "Faltan personajes con la semilla " + semilla);

            for (int i = 0; i < personajes.size(); i++) {
                verificar(personajes.get(i).getId() == i + 1,
                        "MergeSort fallo con la semilla " + semilla
                                + " en la posicion " + i);
            }
        }

        aprobada();
    }

    /* La consigna pide poder ver los procesos que hace la maquina. */
    private void probarTrazaDeInicializacion() {
        Funcionalidad juego = new Funcionalidad(new Scanner(""), new Random(7));
        juego.prepararJuego();
        String traza = juego.getTrazaInicializacion();

        verificar(traza != null, "No se guardo la traza de inicializacion");
        verificar(traza.contains("ETAPA 0"), "Falta la etapa de la caja volcada");
        verificar(traza.contains("ETAPA 1"), "Falta la etapa de agrupar por genero");
        verificar(traza.contains("ETAPA 2"), "Falta la etapa de MergeSort");
        aprobada();
    }

    private void probarFirmasDiferentes(Funcionalidad juego) {
        Set<String> firmas = new HashSet<>();

        for (Personaje p : juego.getPersonajes()) {
            String firma = p.isGeneroMasculino() + ":" + p.isPoderes()
                    + ":" + p.isCapa() + ":" + p.isMascara()
                    + ":" + p.isArma() + ":" + p.isVuela()
                    + ":" + p.isLentes() + ":" + p.isCalvicie()
                    + ":" + p.getColorPelo() + ":" + p.isUniversoMarvel();

            verificar(firmas.add(firma),
                    "Firma repetida para " + p.getNombre());
        }

        aprobada();
    }

    private void probarEleccionGreedy(Funcionalidad juego) {
        ArrayList<Personaje> personajes = juego.getPersonajes();
        Pregunta[] preguntas = juego.getPreguntas();
        Comodin selector = new Comodin();
        boolean[] usadas = new boolean[preguntas.length];
        PrintStream salidaVacia = new PrintStream(new ByteArrayOutputStream());

        Pregunta elegida = selector.elegirMejorPregunta(
                personajes, preguntas, usadas, new Random(50),
                false, salidaVacia);

        int mejorPeorCaso = Integer.MAX_VALUE;

        for (Pregunta pregunta : preguntas) {
            int cantidadSi = selector.contarRespuestasSi(personajes, pregunta);
            int cantidadNo = personajes.size() - cantidadSi;

            if (cantidadSi > 0 && cantidadNo > 0) {
                mejorPeorCaso = Math.min(
                        mejorPeorCaso, Math.max(cantidadSi, cantidadNo));
            }
        }

        int siElegida = selector.contarRespuestasSi(personajes, elegida);
        int peorCasoElegido = Math.max(
                siElegida, personajes.size() - siElegida);

        verificar(peorCasoElegido == mejorPeorCaso,
                "El Comodin greedy no eligio el optimo local");
        aprobada();
    }

    private void probarTodosLosSecretos(Funcionalidad juego) {
        ArrayList<Personaje> personajes = juego.getPersonajes();
        Pregunta[] preguntas = juego.getPreguntas();
        PrintStream salidaVacia = new PrintStream(new ByteArrayOutputStream());

        for (Personaje secreto : personajes) {
            JugadorMaquina maquina = new JugadorMaquina(
                    "Prueba", personajes, preguntas,
                    new Random(1000L + secreto.getId()));
            SecretoMaquina respondedor = new SecretoMaquina(secreto);
            boolean gano = false;

            for (int turno = 0; turno < 40 && !gano; turno++) {
                gano = maquina.jugarTurno(respondedor, false, salidaVacia);
                verificar(!maquina.isSinCandidatos(),
                        "Se elimino el secreto " + secreto.getNombre());
            }

            verificar(gano, "No se encontro a " + secreto.getNombre());
        }

        aprobada();
    }

    /*
     * Arriesgar no puede romper el juego: con cualquier personalidad la
     * maquina tiene que seguir resolviendo los 23 secretos posibles.
     */
    private void probarPersonalidades(Funcionalidad juego) {
        ArrayList<Personaje> personajes = juego.getPersonajes();
        Pregunta[] preguntas = juego.getPreguntas();
        PrintStream salidaVacia = new PrintStream(new ByteArrayOutputStream());

        for (Personalidad personalidad : Personalidad.values()) {
            verificar(personalidad.candidatosMaximosParaApostar() >= 2,
                    "La personalidad " + personalidad.getNombre()
                            + " nunca arriesgaria antes de tener certeza");

            for (Personaje secreto : personajes) {
                JugadorMaquina maquina = new JugadorMaquina(
                        "Prueba", personajes, preguntas,
                        new Random(500L + secreto.getId()), personalidad);
                SecretoMaquina respondedor = new SecretoMaquina(secreto);
                boolean gano = false;

                for (int turno = 0; turno < 40 && !gano; turno++) {
                    gano = maquina.jugarTurno(respondedor, false, salidaVacia);
                    verificar(!maquina.isSinCandidatos(),
                            "Se elimino el secreto " + secreto.getNombre()
                                    + " con personalidad " + personalidad.getNombre());
                }

                verificar(gano, "La personalidad " + personalidad.getNombre()
                        + " no encontro a " + secreto.getNombre());
            }
        }

        aprobada();
    }

    private void probarTrazaCompleta(Funcionalidad juego) {
        ArrayList<Personaje> personajes = juego.getPersonajes();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream salida = new PrintStream(bytes, true, StandardCharsets.UTF_8);
        JugadorMaquina maquina = new JugadorMaquina(
                "Maquina de prueba", personajes, juego.getPreguntas(),
                new Random(2026));
        SecretoMaquina secreto = new SecretoMaquina(personajes.get(10));
        boolean gano = false;

        for (int turno = 0; turno < 40 && !gano; turno++) {
            gano = maquina.jugarTurno(secreto, true, salida);
        }

        String traza = bytes.toString(StandardCharsets.UTF_8);
        verificar(gano, "La partida de traza no termino");
        verificar(traza.contains("Evaluacion GREEDY"),
                "No se muestra la evaluacion greedy");
        verificar(traza.contains("DESCARTADOS"),
                "No se muestran los descartes");
        verificar(traza.contains("SUPOSICION DIRECTA"),
                "No se muestra la suposicion final");
        aprobada();
    }

    private void verificar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
    }

    private void aprobada() {
        cantidadPruebas++;
    }
}
