# Adivina la Carta

Segunda implementación del trabajo de Programación III, construida a partir de
la guía inicial del alumno.

Se conservaron las ideas originales:

- `Main` inicia el juego;
- `funcionalidad` contiene el flujo principal;
- `Personaje` es una clase tradicional con atributos, constructor y getters;
- los personajes se guardan en un `ArrayList`;
- `buscador` y `comodin`, que estaban preparados como clases auxiliares, ahora
  implementan Divide y Conquista y Greedy respectivamente;
- se mantiene el método `adivinar(int id)` de la primera versión.

## Ejecutar

Desde PowerShell:

```powershell
.\ejecutar.ps1
```

También se puede abrir `AdivinaLaCarta.iml` directamente con IntelliJ IDEA y
ejecutar `Main.java`.

## Probar

```powershell
.\probar.ps1
```

Las pruebas no necesitan Maven, Gradle ni librerías externas.

## Documentación

La explicación completa de los algoritmos, las clases, los atributos y las
decisiones se encuentra en [`DOCUMENTACION.md`](DOCUMENTACION.md). La matriz
exacta está en [`PERSONAJES.md`](PERSONAJES.md).
