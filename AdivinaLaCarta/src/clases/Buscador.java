package clases;

import java.util.ArrayList;

public class Buscador {

    
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

    
    public Personaje buscarPorId(ArrayList<Personaje> personajes, int id) {
        return buscarIdBinario(personajes, id, 0, personajes.size() - 1);
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
