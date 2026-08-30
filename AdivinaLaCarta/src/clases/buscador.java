package clases;

import java.util.ArrayList;

public class buscador {

    /*
     * Divide y Conquista: encuentra recursivamente la posicion donde debe
     * insertarse el personaje. Las mujeres (false) quedan primero y los
     * hombres (true) despues. Dentro de cada genero se conservan los IDs.
     */
    public void agregarOrdenadoPorGenero(ArrayList<Personaje> personajes,
                                         Personaje nuevo) {
        int posicion = buscarPosicionPorGenero(
                personajes, nuevo.isGeneroMasculino(), 0, personajes.size());
        personajes.add(posicion, nuevo);
    }

    public int buscarPosicionPorGenero(ArrayList<Personaje> personajes,
                                       boolean generoMasculino,
                                       int inicio,
                                       int finExclusivo) {
        if (inicio >= finExclusivo) {
            return inicio;
        }

        int medio = inicio + (finExclusivo - inicio) / 2;
        boolean generoDelMedio = personajes.get(medio).isGeneroMasculino();

        if (Boolean.compare(generoDelMedio, generoMasculino) <= 0) {
            return buscarPosicionPorGenero(
                    personajes, generoMasculino, medio + 1, finExclusivo);
        }

        return buscarPosicionPorGenero(
                personajes, generoMasculino, inicio, medio);
    }

    /*
     * Los IDs estan ordenados dentro de cada bloque de genero. Primero se
     * busca la frontera y luego se aplica busqueda binaria recursiva en cada
     * bloque. El costo total es O(log n).
     */
    public Personaje buscarPorId(ArrayList<Personaje> personajes, int id) {
        int primerMasculino = buscarPrimerMasculino(
                personajes, 0, personajes.size());

        Personaje encontrado = buscarIdBinario(
                personajes, id, 0, primerMasculino - 1);

        if (encontrado == null) {
            encontrado = buscarIdBinario(
                    personajes, id, primerMasculino, personajes.size() - 1);
        }

        return encontrado;
    }

    private int buscarPrimerMasculino(ArrayList<Personaje> personajes,
                                      int inicio,
                                      int finExclusivo) {
        if (inicio >= finExclusivo) {
            return inicio;
        }

        int medio = inicio + (finExclusivo - inicio) / 2;

        if (personajes.get(medio).isGeneroMasculino()) {
            return buscarPrimerMasculino(personajes, inicio, medio);
        }

        return buscarPrimerMasculino(personajes, medio + 1, finExclusivo);
    }

    private Personaje buscarIdBinario(ArrayList<Personaje> personajes,
                                      int id,
                                      int inicio,
                                      int fin) {
        if (inicio > fin) {
            return null;
        }

        int medio = inicio + (fin - inicio) / 2;
        Personaje actual = personajes.get(medio);

        if (actual.getId() == id) {
            return actual;
        }

        if (id < actual.getId()) {
            return buscarIdBinario(personajes, id, inicio, medio - 1);
        }

        return buscarIdBinario(personajes, id, medio + 1, fin);
    }
}
