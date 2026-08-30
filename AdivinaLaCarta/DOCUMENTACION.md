# Documentación del proyecto Adivina la Carta

## 1. Punto de partida

La guía inicial ya tenía una estructura clara: `Main`, una lista de personajes,
selección aleatoria, ingreso por `Scanner`, un método `adivinar` y dos clases
auxiliares vacías llamadas `buscador` y `comodin`.

La solución terminada conserva esa forma. `Main` sigue siendo pequeño y delega
en `funcionalidad`. La clase `Personaje` continúa siendo una clase común con
campos privados, constructor, getters y el atributo `elegido` de la primera
versión. La lógica nueva se separó en clases simples dentro del paquete
`clases`.

## 2. Personajes y atributos

Los IDs respetan la lista entregada: Marvel del 1 al 13 y DC del 14 al 23.

Cada `Personaje` contiene:

- `id` y `nombre`;
- `generoMasculino`;
- `poderes`, `capa`, `mascara`, `arma`, `vuela`, `lentes` y `calvicie`;
- `ColorPelo`: `COLORADO`, `NEGRO` o `AMARILLO`;
- `universoMarvel` (`false` representa DC);
- `elegido`, conservado de la guía original.

Cuando un personaje tiene calvicie, las tres preguntas de color responden `NO`
y el color se muestra como “No aplicable”.

La matriz exacta de los 23 personajes está en [`PERSONAJES.md`](PERSONAJES.md).

## 3. ArrayList ordenado por género

Los personajes se agregan en el orden de IDs indicado por la consigna, pero el
`ArrayList` debe quedar agrupado por género. Las mujeres (`false`) aparecen
primero y los hombres (`true`) después. Dentro de cada género se conservan los
IDs crecientes.

Cada alta pasa por:

```java
buscadorPersonajes.agregarOrdenadoPorGenero(personajes, nuevo);
```

El arreglo no se ordena al final: la máquina encuentra la posición adecuada a
medida que se agrega cada personaje.

## 4. Divide y Conquista

Se implementa en `clases/buscador.java`.

### Posición de inserción

`buscarPosicionPorGenero` compara con el elemento central y continúa solamente
por una mitad del intervalo. Su recurrencia es:

```text
T(n) = T(n/2) + Θ(1) = Θ(log n)
```

Encontrar la posición requiere `Θ(log n)` comparaciones. Insertar físicamente
en un `ArrayList` puede desplazar elementos y cuesta `O(n)`. Se documentan ambos
costos para no confundir búsqueda con inserción completa.

### Búsqueda por ID

Como la lista tiene dos grupos ordenados internamente por ID, primero se busca
recursivamente la frontera de género. Luego se hace búsqueda binaria recursiva
en cada grupo. Dos búsquedas logarítmicas siguen siendo `Θ(log n)`.

Esta búsqueda se usa al lanzar una suposición directa y reemplaza el recorrido
lineal que tenía el método `adivinar` inicial.

## 5. Greedy

Se implementa en `clases/comodin.java`. El viejo comodín que indicaba un rango
se convirtió en una ayuda más fuerte: ahora decide cuál pregunta conviene hacer.

Elementos del algoritmo:

- **Candidatos:** las preguntas hardcodeadas todavía no utilizadas.
- **Selección:** elegir la pregunta que minimice el peor grupo restante.
- **Factibilidad:** la pregunta no fue usada y separa candidatos en un grupo
  `SI` y otro `NO`, ambos no vacíos.
- **Solución:** cuando queda un candidato, la máquina lo propone directamente.
- **Objetivo:** reducir al máximo la lista en la decisión actual.

Para cada pregunta:

```text
peorCaso = max(cantidadSi, cantidadNo)
desequilibrio = |cantidadSi - cantidadNo|
```

La pregunta con menor `peorCaso` es la mejor decisión local. Si varias empatan,
se elige una al azar. Así se mantiene el requisito de aleatoriedad sin abandonar
la optimización greedy.

Evaluar `q` preguntas sobre `n` candidatos cuesta `Θ(q·n)`. Como el proyecto
tiene siempre 12 preguntas, `q` es constante y un turno cuesta `Θ(n)`.

El criterio es óptimo para el paso actual, pero no se afirma que produzca el
árbol de preguntas globalmente mínimo para cualquier catálogo imaginable. Esa
es la limitación habitual de una heurística greedy.

## 6. Preguntas hardcodeadas

`funcionalidad.crearPreguntas()` crea un array fijo con preguntas sobre género,
poderes, capa, máscara, arma, vuelo, lentes, calvicie, los tres colores y
universo. `Pregunta.evaluar` centraliza cómo se responde cada una.

## 7. Protección del secreto humano

El personaje del humano se elige mentalmente y nunca se guarda en una variable.
`JugadorMaquina` sólo recibe la interfaz `Respondedor`, que permite preguntar o
confirmar una suposición. En Humano vs Máquina, el `Respondedor` solicita por
consola cada `sí/no`.

En Máquina vs Máquina, `SecretoMaquina` encapsula el personaje. El rival nunca
recibe una referencia directa al secreto. La coordinación lo revela únicamente
al terminar la partida.

## 8. Modos de juego

### Humano vs Máquina

El humano y la máquina alternan turnos. El humano puede elegir una pregunta del
array o intentar adivinar por ID. La máquina usa `comodin` para seleccionar su
pregunta greedy y supone cuando queda un único candidato.

### Máquina vs Máquina

Se eligen dos secretos distintos. En cada turno se muestran:

- todos los candidatos;
- evaluación `SI/NO` de cada pregunta;
- peor caso y desequilibrio;
- desempate aleatorio;
- pregunta elegida y respuesta;
- personajes descartados y restantes;
- suposición final.

## 9. Clases

- `Main`: punto de entrada.
- `funcionalidad`: menú, catálogo y coordinación de partidas.
- `Personaje`: modelo con los atributos pedidos.
- `Pregunta`: pregunta hardcodeada y evaluación de un personaje.
- `buscador`: Divide y Conquista.
- `comodin`: selección Greedy.
- `JugadorMaquina`: candidatos y turnos de una máquina.
- `Respondedor`: acceso limitado al secreto.
- `SecretoMaquina`: secreto encapsulado.

## 10. Pruebas

`test/Pruebas.java` comprueba:

1. existencia de 23 personajes;
2. agrupamiento por género e IDs crecientes;
3. búsqueda Divide y Conquista de cada ID;
4. firmas de características diferentes;
5. mejor decisión greedy local;
6. resolución de los 23 secretos posibles;
7. presencia del proceso completo en la traza.
