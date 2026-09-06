package funcionalidad;

import clases.ColorPelo;
import clases.JugadorMaquina;
import clases.Personaje;
import clases.Personalidad;
import clases.Pregunta;
import clases.Ordenador;
import clases.Respondedor;
import clases.SecretoMaquina;
import clases.Buscador;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

public class Funcionalidad {
    private ArrayList<Personaje> personajes;
    private Personaje elegido;
    private Pregunta[] preguntas;
    private final Scanner teclado;
    private final Random random;
    private final Buscador buscadorPersonajes;
    private final Ordenador ordenador;
    private int siguienteId;
    private ArrayList<Personaje> catalogoEnCarga;
    private String trazaInicializacion;
    private boolean mostrarProcesoMaquina = true;

    public Funcionalidad() {
        this(new Scanner(System.in), new Random());
    }

    public Funcionalidad(Scanner teclado, Random random) {
        this.teclado = teclado;
        this.random = random;
        this.buscadorPersonajes = new Buscador();
        this.ordenador = new Ordenador();
    }

    public void iniciarJuego() {
        prepararJuego();
        int opcion;

        do {
            System.out.println("\n====================================");
            System.out.println("       ADIVINA EL PERSONAJE");
            System.out.println("====================================");
            System.out.println("1. Humano vs Maquina");
            System.out.println("2. Maquina vs Maquina");
            System.out.println("3. Ver los 23 personajes");
            System.out.println("4. Ver como la maquina inicializo y ordeno");
            System.out.println("5. Mostrar el razonamiento de la maquina: "
                    + (mostrarProcesoMaquina ? "SI" : "NO"));
            System.out.println("0. Salir");

            opcion = leerEntero("Elija una opcion: ", 0, 5);

            switch (opcion) {
                case 1:
                    jugarHumanoVsMaquina();
                    break;
                case 2:
                    jugarMaquinaVsMaquina();
                    break;
                case 3:
                    mostrarCatalogoCompleto();
                    break;
                case 4:
                    mostrarInicializacion();
                    break;
                case 5:
                    alternarProceso();
                    break;
                default:
                    System.out.println("Fin del juego.");
                    break;
            }
        } while (opcion != 0);
    }

    /* Permite probar el catalogo sin iniciar toda la consola. */
    public void prepararJuego() {
        preguntas = crearPreguntas();
        siguienteId = 1;
        elegido = null;
        personajes = inicializarPersonajes();
    }

    /*
     * Inicializacion en tres etapas, como pide la consigna:
     *
     *   Etapa 0  La caja volcada. Los 23 personajes salen en cualquier orden.
     *   Etapa 1  La maquina los agrupa por genero. Cada alta usa insercion por
     *            busqueda binaria: Theta(log n) comparaciones por personaje.
     *            Este es el estado "ordenados unicamente segun su genero".
     *   Etapa 2  La maquina ordena por ID con MergeSort y deja la lista
     *            autoincremental: Theta(n log n).
     *
     * La traza queda guardada para poder mostrarla desde el menu.
     */
    private ArrayList<Personaje> inicializarPersonajes() {
        StringBuilder traza = new StringBuilder();

        ArrayList<Personaje> caja = crearCatalogoCrudo();
        Collections.shuffle(caja, random);
        traza.append("ETAPA 0 - La caja volcada (orden aleatorio):\n")
                .append("  ").append(mostrarIds(caja)).append("\n\n");

        ArrayList<Personaje> agrupados = new ArrayList<>();
        for (Personaje personaje : caja) {
            // Divide y Conquista: insercion por busqueda binaria.
            buscadorPersonajes.agregarOrdenadoPorGenero(agrupados, personaje);
        }
        traza.append("ETAPA 1 - Agrupados por genero (insercion binaria, ")
                .append("femenino primero):\n")
                .append("  ").append(mostrarIds(agrupados)).append("\n\n");

        // Divide y Conquista: MergeSort deja la lista autoincremental.
        ordenador.ordenarPorId(agrupados);
        traza.append("ETAPA 2 - Ordenados por ID con MergeSort ")
                .append("(lista autoincremental):\n")
                .append("  ").append(mostrarIds(agrupados)).append("\n");

        trazaInicializacion = traza.toString();
        return agrupados;
    }

    private String mostrarIds(ArrayList<Personaje> lista) {
        StringBuilder texto = new StringBuilder("[");

        for (int i = 0; i < lista.size(); i++) {
            texto.append(lista.get(i).getId());

            if (i < lista.size() - 1) {
                texto.append(", ");
            }
        }

        return texto.append("]").toString();
    }

    public String getTrazaInicializacion() {
        return trazaInicializacion;
    }

    private ArrayList<Personaje> crearCatalogoCrudo() {
        ArrayList<Personaje> crudos = new ArrayList<>();
        cargarPersonajes(crudos);
        return crudos;
    }

    private void cargarPersonajes(ArrayList<Personaje> destino) {
        catalogoEnCarga = destino;

        // Marvel: IDs 1 a 13.
        agregarPersonaje("Spider-Man (Peter Parker)", true, true, false,
                true, false, false, true, false, ColorPelo.NEGRO, true);
        agregarPersonaje("Iron Man (Tony Stark)", true, true, false,
                true, true, true, false, false, ColorPelo.NEGRO, true);
        agregarPersonaje("Capitán América (Steve Rogers)", true, true, false,
                true, true, false, false, false, ColorPelo.AMARILLO, true);
        agregarPersonaje("Thor", true, true, true,
                false, true, true, false, false, ColorPelo.AMARILLO, true);
        agregarPersonaje("Hulk (Bruce Banner)", true, true, false,
                false, false, false, true, false, ColorPelo.NEGRO, true);
        agregarPersonaje("Jean Grey", false, true, false,
                false, false, true, false, false, ColorPelo.COLORADO, true);
        agregarPersonaje("Viuda Negra (Natasha Romanoff)", false, false, false,
                false, true, false, false, false, ColorPelo.COLORADO, true);
        agregarPersonaje("Doctor Strange", true, true, true,
                false, false, true, false, false, ColorPelo.NEGRO, true);
        agregarPersonaje("Pantera Negra (Shuri)", false, true, false,
                true, true, false, false, false, ColorPelo.NEGRO, true);
        agregarPersonaje("Deadpool (Wade Wilson)", true, true, false,
                true, true, false, false, true, ColorPelo.NEGRO, true);
        agregarPersonaje("Avispa (Janet van Dyne)", false, true, false,
                true, false, true, false, false, ColorPelo.NEGRO, true);
        agregarPersonaje("Profesor X (Charles Xavier)", true, true, false,
                false, false, false, false, true, ColorPelo.NEGRO, true);
        agregarPersonaje("Gamora", false, true, false,
                false, true, false, false, false, ColorPelo.NEGRO, true);

        // DC: IDs 14 a 23.
        agregarPersonaje("Superman (Clark Kent)", true, true, true,
                false, false, true, true, false, ColorPelo.NEGRO, false);
        agregarPersonaje("Batman (Bruce Wayne)", true, false, true,
                true, true, false, false, false, ColorPelo.NEGRO, false);
        agregarPersonaje("Mujer Maravilla (Diana Prince)", false, true, false,
                false, true, true, false, false, ColorPelo.NEGRO, false);
        agregarPersonaje("Flash (Barry Allen)", true, true, false,
                true, false, false, false, false, ColorPelo.AMARILLO, false);
        agregarPersonaje("Aquaman (Arthur Curry)", true, true, false,
                false, true, false, false, false, ColorPelo.AMARILLO, false);
        agregarPersonaje("Chica Halcón (Shayera Hol)", false, true, false,
                true, true, true, false, false, ColorPelo.COLORADO, false);
        agregarPersonaje("Supergirl (Kara Zor-El)", false, true, true,
                false, false, true, false, false, ColorPelo.AMARILLO, false);
        agregarPersonaje("Cyborg (Victor Stone)", true, true, false,
                false, true, true, false, true, ColorPelo.NEGRO, false);
        agregarPersonaje("Canario Negro (Dinah Lance)", false, true, false,
                true, false, false, false, false, ColorPelo.AMARILLO, false);
        agregarPersonaje("Batgirl (Barbara Gordon)", false, false, true,
                true, true, false, true, false, ColorPelo.COLORADO, false);
    }

    private void agregarPersonaje(String nombre,
                                  boolean generoMasculino,
                                  boolean poderes,
                                  boolean capa,
                                  boolean mascara,
                                  boolean arma,
                                  boolean vuela,
                                  boolean lentes,
                                  boolean calvicie,
                                  ColorPelo colorPelo,
                                  boolean universoMarvel) {
        Personaje nuevo = new Personaje(nombre, siguienteId,
                generoMasculino, poderes, capa, mascara, arma, vuela,
                lentes, calvicie, colorPelo, universoMarvel);

        siguienteId++;

        // Solo carga el dato crudo. Ordenar es tarea de la maquina (etapas 1 y 2).
        catalogoEnCarga.add(nuevo);
    }

    private Pregunta[] crearPreguntas() {
        return new Pregunta[]{
                new Pregunta(Pregunta.GENERO_MASCULINO, "Es de genero masculino?"),
                new Pregunta(Pregunta.PODERES, "Tiene poderes?"),
                new Pregunta(Pregunta.CAPA, "Usa capa?"),
                new Pregunta(Pregunta.MASCARA, "Usa mascara?"),
                new Pregunta(Pregunta.ARMA, "Usa un arma?"),
                new Pregunta(Pregunta.VUELA, "Puede volar?"),
                new Pregunta(Pregunta.LENTES,
                        "Usa lentes en su identidad civil?"),
                new Pregunta(Pregunta.CALVICIE, "Tiene calvicie?"),
                new Pregunta(Pregunta.PELO_COLORADO, "Tiene el pelo colorado?"),
                new Pregunta(Pregunta.PELO_NEGRO, "Tiene el pelo negro?"),
                new Pregunta(Pregunta.PELO_AMARILLO, "Tiene el pelo amarillo?"),
                new Pregunta(Pregunta.UNIVERSO_MARVEL, "Pertenece a Marvel?")
        };
    }

    private void jugarHumanoVsMaquina() {
        reiniciarElegidos();
        elegido = elegirAleatoriamente();
        elegido.setElegido(true);

        SecretoMaquina secretoDeLaMaquina = new SecretoMaquina(elegido);
        Personalidad personalidad = elegirRival();
        JugadorMaquina maquina = new JugadorMaquina(
                "Maquina", personajes, preguntas, personalidad);
        ArrayList<Personaje> candidatosDelHumano = new ArrayList<>(personajes);
        boolean[] preguntasUsadasPorHumano = new boolean[preguntas.length];
        Respondedor secretoDelHumano = crearRespondedorHumano();

        System.out.println("\n=== HUMANO VS MAQUINA ===");
        System.out.println("Rival: maquina " + personalidad.getNombre()
                + ". Usa Greedy para elegir la pregunta (minimiza el peor caso) "
                + "y se la juega cada "
                + personalidad.getPreguntasEntreApuestas()
                + " preguntas si quedan "
                + personalidad.candidatosMaximosParaApostar()
                + " candidatos o menos.");
        mostrarCatalogoResumido();
        System.out.println("\nElija mentalmente un personaje de la lista.");
        System.out.println("No debe escribir su ID: la maquina no guardara su secreto.");
        esperarEnter("Cuando lo haya elegido, presione Enter...");

        int ronda = 1;

        while (true) {
            System.out.println("\n========== RONDA " + ronda + " ==========");

            if (jugarTurnoHumano(candidatosDelHumano,
                    preguntasUsadasPorHumano, secretoDeLaMaquina)) {
                System.out.println("Gano el jugador humano.");
                System.out.println("El personaje era: " + elegido.getNombre());
                return;
            }

            System.out.println("\n--- Turno de la maquina ---");
            boolean ganoMaquina = maquina.jugarTurno(
                    secretoDelHumano, mostrarProcesoMaquina, System.out);

            if (ganoMaquina) {
                System.out.println("Gano la maquina.");
                System.out.println("El secreto de la maquina era: "
                        + elegido.getNombre());
                return;
            }

            if (maquina.isSinCandidatos()) {
                System.out.println("Las respuestas dadas se contradicen. Se cancela la partida.");
                return;
            }

            ronda++;
        }
    }

    private boolean jugarTurnoHumano(ArrayList<Personaje> candidatos,
                                     boolean[] preguntasUsadas,
                                     Respondedor rival) {
        System.out.println("\n--- Su turno ---");
        System.out.println("Candidatos: " + mostrarLista(candidatos));
        System.out.println("1. Hacer una pregunta");
        System.out.println("2. Adivinar directamente");

        int opcion = leerEntero("Elija: ", 1, 2);

        if (opcion == 2) {
            int id = leerEntero("ID del personaje: ", 1, personajes.size());
            Personaje supuesto = buscadorPersonajes.buscarPorId(personajes, id);

            if (supuesto == null) {
                System.out.println("No existe un personaje con ese ID.");
                return false;
            }

            boolean acierto = rival.confirmarPersonaje(supuesto);

            if (acierto) {
                System.out.println("Adivinacion correcta: " + supuesto.getNombre());
                return true;
            }

            System.out.println(supuesto.getNombre() + " no es el elegido.");
            candidatos.remove(supuesto);
            return false;
        }

        ArrayList<Integer> posicionesDisponibles = new ArrayList<>();

        for (int i = 0; i < preguntas.length; i++) {
            if (!preguntasUsadas[i]) {
                posicionesDisponibles.add(i);
                System.out.println(posicionesDisponibles.size() + ". "
                        + preguntas[i].getTexto());
            }
        }

        if (posicionesDisponibles.isEmpty()) {
            System.out.println("Ya uso todas las preguntas. Debera adivinar.");
            return false;
        }

        int numero = leerEntero(
                "Numero de pregunta: ", 1, posicionesDisponibles.size());
        int posicionReal = posicionesDisponibles.get(numero - 1);
        Pregunta pregunta = preguntas[posicionReal];
        preguntasUsadas[posicionReal] = true;

        boolean respuesta = rival.responderPregunta(pregunta);
        filtrarCandidatos(candidatos, pregunta, respuesta);

        System.out.println("Respuesta: " + (respuesta ? "SI" : "NO"));
        System.out.println("Quedan " + candidatos.size() + " candidatos: "
                + mostrarLista(candidatos));

        return false;
    }

    private void jugarMaquinaVsMaquina() {
        Personaje secretoA = personajes.get(random.nextInt(personajes.size()));
        Personaje secretoB;

        do {
            secretoB = personajes.get(random.nextInt(personajes.size()));
        } while (secretoA.getId() == secretoB.getId());

        SecretoMaquina respondedorA = new SecretoMaquina(secretoA);
        SecretoMaquina respondedorB = new SecretoMaquina(secretoB);

        /*
         * Las dos maquinas aplican el mismo criterio Greedy para elegir la
         * pregunta y se diferencian en el umbral de riesgo con el que deciden
         * dejar de preguntar y jugarsela.
         */
        JugadorMaquina maquinaA = new JugadorMaquina(
                "Maquina A", personajes, preguntas, Personalidad.CAUTELOSA);
        JugadorMaquina maquinaB = new JugadorMaquina(
                "Maquina B", personajes, preguntas, Personalidad.AUDAZ);

        System.out.println("\n=== MAQUINA VS MAQUINA ===");
        System.out.println("Los secretos son distintos y se revelaran al finalizar.");
        System.out.println("Se mostrara todo el proceso GREEDY de cada turno.");
        System.out.println("Ambas eligen la pregunta con el mismo criterio "
                + "Greedy: minimizar el peor caso.");
        System.out.println("Maquina A es " + maquinaA.getPersonalidad().getNombre()
                + " y Maquina B es " + maquinaB.getPersonalidad().getNombre()
                + ": se diferencian en el umbral de riesgo con el que deciden "
                + "jugarsela.");

        for (int turno = 1; turno <= 100; turno++) {
            boolean juegaA = turno % 2 != 0;
            System.out.println("\n---------- TURNO " + turno + " ----------");
            boolean gano;

            if (juegaA) {
                gano = maquinaA.jugarTurno(respondedorB, true, System.out);
            } else {
                gano = maquinaB.jugarTurno(respondedorA, true, System.out);
            }

            if (gano) {
                String ganador = juegaA ? "Maquina A" : "Maquina B";
                mostrarFinalMaquinas(ganador, respondedorA, respondedorB);
                return;
            }

            if (maquinaA.isSinCandidatos() || maquinaB.isSinCandidatos()) {
                mostrarFinalMaquinas("Sin ganador", respondedorA, respondedorB);
                return;
            }
        }

        mostrarFinalMaquinas("Sin ganador por limite de turnos",
                respondedorA, respondedorB);
    }

    private Respondedor crearRespondedorHumano() {
        return new Respondedor() {
            @Override
            public boolean responderPregunta(Pregunta pregunta) {
                return leerSiNo("La maquina pregunta: " + pregunta.getTexto());
            }

            @Override
            public boolean confirmarPersonaje(Personaje personaje) {
                return leerSiNo("La maquina supone que eligio a "
                        + personaje.getNombre() + ". Acerto?");
            }
        };
    }

    private void filtrarCandidatos(ArrayList<Personaje> candidatos,
                                   Pregunta pregunta,
                                   boolean respuesta) {
        for (int i = candidatos.size() - 1; i >= 0; i--) {
            if (pregunta.evaluar(candidatos.get(i)) != respuesta) {
                candidatos.remove(i);
            }
        }
    }

    /*
     * El rival no se sortea: lo elige el jugador. Asi queda explicito contra
     * que criterio esta jugando y se puede comparar una maquina con la otra.
     */
    private Personalidad elegirRival() {
        Personalidad[] opciones = Personalidad.values();
        System.out.println("\nContra que maquina queres jugar?");

        for (int i = 0; i < opciones.length; i++) {
            System.out.println((i + 1) + ". " + opciones[i].getNombre()
                    + " - arriesga cada " + opciones[i].getPreguntasEntreApuestas()
                    + " preguntas si quedan "
                    + opciones[i].candidatosMaximosParaApostar()
                    + " candidatos o menos");
        }

        return opciones[leerEntero("Elija rival: ", 1, opciones.length) - 1];
    }

    private Personaje elegirAleatoriamente() {
        int posicion = random.nextInt(personajes.size());
        return personajes.get(posicion);
    }

    private void reiniciarElegidos() {
        for (Personaje personaje : personajes) {
            personaje.setElegido(false);
        }
    }

    /* Conserva el metodo de adivinacion de la primera version. */
    public boolean adivinar(int id) {
        Personaje supuesto = buscadorPersonajes.buscarPorId(personajes, id);

        if (supuesto == null) {
            System.out.println("No existe un personaje con ese ID");
            return false;
        }

        if (supuesto.isElegido()) {
            System.out.println("Acertaste: " + supuesto.getNombre());
            return true;
        }

        System.out.println(supuesto.getNombre() + " no es el elegido");
        return false;
    }

    private void mostrarFinalMaquinas(String ganador,
                                      SecretoMaquina secretoA,
                                      SecretoMaquina secretoB) {
        System.out.println("\n========== FIN ==========");
        System.out.println("Resultado: " + ganador);
        System.out.println("Secreto de Maquina A: "
                + secretoA.revelarAlFinal().getNombre());
        System.out.println("Secreto de Maquina B: "
                + secretoB.revelarAlFinal().getNombre());
    }

    /*
     * La lista se muestra tal como la dejo el MergeSort: ordenada por ID de
     * forma autoincremental, que es el estado que pide la consigna.
     */
    private void mostrarCatalogoResumido() {
        System.out.println("\n[LISTA ORDENADA POR ID - "
                + personajes.size() + " personajes]");

        for (Personaje personaje : personajes) {
            System.out.println(personaje.mostrarResumen());
        }
    }

    /*
     * Separacion de responsabilidades: la consola de razonamiento se puede
     * apagar y el juego sigue funcionando igual. La logica no depende de que
     * se imprima nada; la salida es solo un observador del proceso.
     */
    private void alternarProceso() {
        mostrarProcesoMaquina = !mostrarProcesoMaquina;
        System.out.println("\nRazonamiento de la maquina en consola: "
                + (mostrarProcesoMaquina ? "ACTIVADO" : "SILENCIADO"));
        System.out.println("El juego funciona igual en ambos modos: la logica "
                + "no depende de la salida por consola.");
    }

    private void mostrarInicializacion() {
        System.out.println("\n=== INICIALIZACION DE LA MAQUINA ===");
        System.out.println(trazaInicializacion);
        System.out.println("Etapa 1: insercion por busqueda binaria, "
                + "Theta(log n) por personaje.");
        System.out.println("Etapa 2: MergeSort, Theta(n log n) en todos los casos.");
    }

    /*
     * La lista se muestra tal como la dejo el MergeSort de la Etapa 2:
     * ordenada por ID de forma autoincremental.
     */
    private void mostrarCatalogoCompleto() {
        System.out.println("\nLos " + personajes.size() + " personajes, "
                + "ordenados por ID tal como los dejo el MergeSort:");

        for (Personaje personaje : personajes) {
            System.out.println(personaje.mostrarDetalle());
        }
    }

    private String mostrarLista(ArrayList<Personaje> lista) {
        StringBuilder resultado = new StringBuilder("[");

        for (int i = 0; i < lista.size(); i++) {
            resultado.append(lista.get(i).getId())
                    .append(":")
                    .append(lista.get(i).getNombre());

            if (i < lista.size() - 1) {
                resultado.append(", ");
            }
        }

        return resultado.append("]").toString();
    }

    private int leerEntero(String mensaje, int minimo, int maximo) {
        while (true) {
            System.out.print(mensaje);
            String texto = teclado.nextLine();

            try {
                int numero = Integer.parseInt(texto.trim());

                if (numero >= minimo && numero <= maximo) {
                    return numero;
                }
            } catch (NumberFormatException ignored) {
                // Se muestra el mismo mensaje para ambos tipos de error.
            }

            System.out.println("Ingrese un numero entre " + minimo
                    + " y " + maximo + ".");
        }
    }

    private boolean leerSiNo(String mensaje) {
        while (true) {
            System.out.print(mensaje + " (s/n): ");
            String respuesta = teclado.nextLine().trim().toLowerCase();

            if (respuesta.equals("s") || respuesta.equals("si")) {
                return true;
            }

            if (respuesta.equals("n") || respuesta.equals("no")) {
                return false;
            }

            System.out.println("Responda s o n.");
        }
    }

    private void esperarEnter(String mensaje) {
        System.out.print(mensaje);
        teclado.nextLine();
    }

    public ArrayList<Personaje> getPersonajes() {
        return new ArrayList<>(personajes);
    }

    public Pregunta[] getPreguntas() {
        return preguntas.clone();
    }
}
