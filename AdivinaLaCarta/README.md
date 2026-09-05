# Adivina la Carta

Juego de deducción por consola sobre un tablero de 23 superhéroes. Cada jugador
tiene un personaje secreto y gana el primero que descubre el del rival, ya sea
haciendo preguntas por atributo o arriesgando un nombre. Se puede jugar Humano
contra Máquina o Máquina contra Máquina.

Estructura del proyecto:

- `Main` inicia el juego;
- `Funcionalidad` contiene el flujo principal;
- `Personaje` es una clase tradicional con atributos, constructor y getters;
- los personajes se guardan en un `ArrayList`;
- `Ordenador` y `Buscador` implementan Divide y Conquista;
- `Comodin` y `Personalidad` implementan las dos decisiones Greedy;
- `adivinar(int id)` resuelve una suposición directa por ID.

## Algoritmos aplicados

| Dónde | Técnica | Clase | Complejidad |
|---|---|---|---|
| Agrupar por género | Inserción por búsqueda binaria | `Buscador` | `Θ(log n)` por alta |
| Ordenar por ID | MergeSort | `Ordenador` | `Θ(n log n)` |
| Adivinar por ID | Búsqueda binaria | `Buscador` | `Θ(log n)` |
| Elegir la pregunta | Greedy | `Comodin` | `Θ(n)` por turno |
| Preguntar o jugársela | Greedy | `Personalidad` | `Θ(1)` por turno |

Los personajes **arrancan desordenados** y es la máquina la que los agrupa por
género y los deja en una lista autoincremental. La opción 4 del menú muestra ese
proceso paso a paso.

## Ejecutar

Desde PowerShell (Windows):

```powershell
.\ejecutar.ps1
```

Desde bash (Linux o macOS):

```bash
./ejecutar.sh
```

También se puede abrir la carpeta `AdivinaLaCarta` como proyecto en IntelliJ
IDEA y ejecutar `Main.java`.

## Probar

```powershell
.\probar.ps1
```

```bash
./probar.sh
```

Las pruebas no necesitan Maven, Gradle ni librerías externas. Deben mostrar
`OK - 9 pruebas superadas.`

## Documentación

La explicación completa de los algoritmos, las clases, los atributos y las
decisiones se encuentra en [`DOCUMENTACION.md`](DOCUMENTACION.md). La matriz
exacta está en [`PERSONAJES.md`](PERSONAJES.md).
