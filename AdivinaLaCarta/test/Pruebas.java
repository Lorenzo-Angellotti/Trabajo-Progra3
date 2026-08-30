import clases.JugadorMaquina;
import clases.Personaje;
import clases.Pregunta;
import clases.SecretoMaquina;
import clases.buscador;
import clases.comodin;

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
        funcionalidad juego = new funcionalidad(new Scanner(""), new Random(1));
        juego.prepararJuego();

        probarCantidadOrdenIdsYBusqueda(juego);
        probarFirmasDiferentes(juego);
        probarEleccionGreedy(juego);
        probarTodosLosSecretos(juego);
        probarTrazaCompleta(juego);

        System.out.println("OK - " + cantidadPruebas + " pruebas superadas.");
    }

    private void probarCantidadOrdenIdsYBusqueda(funcionalidad juego) {
        ArrayList<Personaje> personajes = juego.getPersonajes();
        buscador buscadorPersonajes = new buscador();

        verificar(personajes.size() == 23, "Deben existir 23 personajes");

        boolean aparecioMasculino = false;
        int ultimoIdFemenino = 0;
        int ultimoIdMasculino = 0;

        for (Personaje personaje : personajes) {
            if (personaje.isGeneroMasculino()) {
                aparecioMasculino = true;
                verificar(personaje.getId() > ultimoIdMasculino,
                        "Los IDs masculinos no estan ordenados");
                ultimoIdMasculino = personaje.getId();
            } else {
                verificar(!aparecioMasculino,
                        "El ArrayList no esta agrupado por genero");
                verificar(personaje.getId() > ultimoIdFemenino,
                        "Los IDs femeninos no estan ordenados");
                ultimoIdFemenino = personaje.getId();
            }

            Personaje encontrado = buscadorPersonajes.buscarPorId(
                    personajes, personaje.getId());
            verificar(encontrado == personaje,
                    "La busqueda DyC no encontro el ID " + personaje.getId());
        }

        verificar(buscadorPersonajes.buscarPorId(personajes, 99) == null,
                "Se encontro un ID inexistente");
        aprobada();
    }

    private void probarFirmasDiferentes(funcionalidad juego) {
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

    private void probarEleccionGreedy(funcionalidad juego) {
        ArrayList<Personaje> personajes = juego.getPersonajes();
        Pregunta[] preguntas = juego.getPreguntas();
        comodin selector = new comodin();
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
                "El comodin greedy no eligio el optimo local");
        aprobada();
    }

    private void probarTodosLosSecretos(funcionalidad juego) {
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

    private void probarTrazaCompleta(funcionalidad juego) {
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
