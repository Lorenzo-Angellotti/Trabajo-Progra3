package clases;

import java.util.ArrayList;

/*
 * Divide y Conquista aplicado al ordenamiento.
 *
 * La maquina recibe los personajes tal como salen de la caja (agrupados
 * unicamente por genero) y debe dejarlos en una lista autoincremental por ID,
 * como pide la consigna.
 *
 * Se eligio MergeSort y no QuickSort porque MergeSort garantiza Theta(n log n)
 * en todos los casos. QuickSort con pivot en u[ini] (la version de la Clase 02)
 * degrada a Theta(n^2) cuando la entrada llega ordenada o casi ordenada, y aca
 * la entrada se baraja en cada partida: puede llegar casi ordenada por azar.
 */
public class Ordenador {

    /*
     * MergeSort: divide el intervalo en dos mitades, ordena cada una
     * recursivamente y luego las mezcla.
     *
     * Recurrencia: T(n) = 2 T(n/2) + Theta(n)
     * Con a = 2, b = 2, k = 1 estamos en el caso a = b^k, o sea Theta(n log n).
     */
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
