package clases;

import java.util.ArrayList;


public class Ordenador {

    
    public void ordenarPorId(ArrayList<Personaje> personajes) {
        if (personajes.size() > 1) {
            mergeSort(personajes, 0, personajes.size() - 1);
        }
    }

    private void mergeSort(ArrayList<Personaje> u, int ini, int fin) {
        if (ini < fin) {                 // caso base: un solo elemento
            int mid = (ini + fin) / 2;
            mergeSort(u, ini, mid);      // dividir
            mergeSort(u, mid + 1, fin);
            merge(u, ini, fin);          // combinar
        }
    }

    /*
     * Merge recorre las dos mitades ya ordenadas una sola vez, por lo que
     * cuesta Theta(n). Ese es el k = 1 de la recurrencia.
     */
    private void merge(ArrayList<Personaje> u, int ini, int fin) {
        ArrayList<Personaje> w = new ArrayList<>(fin - ini + 1);
        int mid = (ini + fin) / 2;
        int i = ini;
        int j = mid + 1;

        for (int k = 0; k <= fin - ini; k++) {
            boolean quedaIzquierda = i <= mid;
            boolean quedaDerecha = j <= fin;

            if (quedaIzquierda
                    && (!quedaDerecha
                        || u.get(i).getId() <= u.get(j).getId())) {
                w.add(u.get(i));
                i++;
            } else {
                w.add(u.get(j));
                j++;
            }
        }

        for (int k = 0; k <= fin - ini; k++) {
            u.set(ini + k, w.get(k));
        }
    }
}
