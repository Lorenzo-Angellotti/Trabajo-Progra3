# Adivina la Carta

Segunda implementación del trabajo de Programación III, construida a partir de
la guía inicial del alumno.

Se conservaron las ideas originales:

- `Main` inicia el juego;
- `Funcionalidad` contiene el flujo principal;
- `Personaje` es una clase tradicional con atributos, constructor y getters;
- los personajes se guardan en un `ArrayList`;
- `Buscador` y `Comodin`, que estaban preparados como clases auxiliares, ahora
  implementan Divide y Conquista y Greedy respectivamente;
- se mantiene el método `adivinar(int id)` de la primera versión.

## Algoritmos aplicados

| Dónde | Técnica | Clase | Complejidad |
|---|---|---|---|
| Agrupar por género | Inserción por búsqueda binaria | `Buscador` | `Θ(log n)` por alta |
| Ordenar por ID | MergeSort | `Ordenador` | `Θ(n log n)` |
| Adivinar por ID | Búsqueda binaria | `Buscador` | `Θ(log n)` |
| Elegir la pregunta | Greedy | `Comodin` | `Θ(n)` por turno |
| Preguntar o jugársela | Greedy | `Personalidad` | `Θ(1)` por turno |

Los personajes **arrancan desordenados** y es la máquina la que los agrupa por
género y los deja en una lista autoincremental, como pide la consigna. La opción
4 del menú muestra ese proceso paso a paso.

## Ejecutar

Desde PowerShell (Windows):

```powershell
.\ejecutar.ps1
```

Desde bash (Linux o macOS):

```bash
./ejecutar.sh
```

También se puede abrir `AdivinaLaCarta.iml` directamente con IntelliJ IDEA y
ejecutar `Main.java`.

## Probar

```powershell
.\probar.ps1
```

```bash
./probar.sh
```

Las pruebas no necesitan Maven, Gradle ni librerías externas. Deben mostrar
`OK - 8 pruebas superadas.`

## Documentación

La explicación completa de los algoritmos, las clases, los atributos y las
decisiones se encuentra en [`DOCUMENTACION.md`](DOCUMENTACION.md). La matriz
exacta está en [`PERSONAJES.md`](PERSONAJES.md).
