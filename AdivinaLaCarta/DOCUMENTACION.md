# Documentación del proyecto Adivina la Carta

## 1. Qué hace el proyecto

`Adivina la Carta` es un juego de deducción por consola sobre un tablero de 23
superhéroes. Cada jugador tiene un personaje secreto, y gana el primero que
descubre el del rival.

En cada turno un jugador puede hacer **una sola** de estas dos cosas:

- **preguntar** por un atributo, y descartar a todos los personajes cuya
  respuesta no coincida;
- **arriesgar** un nombre, y ganar si acierta.

El turno se gasta en una o en la otra. Se puede jugar Humano contra Máquina o
Máquina contra Máquina.

El proyecto aplica dos técnicas algorítmicas:

| Técnica | Dónde se usa | Clase |
|---|---|---|
| Divide y Conquista | Ordenar los personajes y buscarlos por ID | `Ordenador`, `Buscador` |
| Greedy | Elegir qué preguntar y decidir cuándo arriesgar | `Comodin`, `Personalidad` |

`Main` es el punto de entrada y delega en `Funcionalidad`, que coordina el menú
y las partidas. Los algoritmos y el modelo viven en el paquete `clases`.

## 2. Personajes y atributos

Los IDs van del 1 al 23: Marvel del 1 al 13 y DC del 14 al 23.

Cada `Personaje` contiene:

- `id` y `nombre`;
- `generoMasculino`;
- `poderes`, `capa`, `mascara`, `arma`, `vuela`, `lentes` y `calvicie`;
- `ColorPelo`: `COLORADO`, `NEGRO` o `AMARILLO`;
- `universoMarvel` (`false` representa DC);
- `elegido`, que marca al secreto en el método `adivinar(int id)`.

La matriz completa de los 23 personajes, con el valor de cada atributo y los
criterios con que se asignaron, está en [`PERSONAJES.md`](PERSONAJES.md).

## 3. Inicialización: la máquina ordena los personajes

Los personajes no arrancan ordenados. Salen desordenados, como al volcar la caja
de un juego de mesa, y **es la máquina la que tiene que armar el tablero**: los
agrupa por género y después los deja en una lista autoincremental por ID.

La inicialización ocurre en tres etapas, y ninguna depende de que los datos
vengan pre-ordenados.

### Etapa 0 — La caja volcada

`crearCatalogoCrudo()` construye los 23 personajes con sus IDs fijos y
`Collections.shuffle(caja, random)` los desordena.

El barajado usa el mismo `Random` del juego, así que cada partida arranca con un
desorden distinto. Las pruebas automáticas, en cambio, construyen el juego con
una **semilla fija**, es decir un valor inicial conocido que hace que `Random`
produzca siempre la misma secuencia: así el mismo desorden se puede reproducir
las veces que haga falta y un fallo se puede investigar.

### Etapa 1 — Agrupar por género

La máquina inserta cada personaje buscando su posición con búsqueda binaria:

```java
buscadorPersonajes.agregarOrdenadoPorGenero(agrupados, personaje);
```

Las mujeres (`false`) quedan primero y los hombres (`true`) después. Éste es el
estado intermedio: la lista queda ordenada únicamente por género, sin ningún
orden interno de IDs.

Encontrar la posición cuesta `Θ(log n)` comparaciones. Insertar físicamente en
un `ArrayList` puede desplazar elementos y cuesta `O(n)`. Se documentan los dos
costos por separado para no confundir la búsqueda de la posición con la
inserción completa.

### Etapa 2 — Ordenar por ID con MergeSort

```java
ordenador.ordenarPorId(agrupados);
```

La lista queda autoincremental de 1 a 23. Recién ahí empieza la partida.

La opción 4 del menú muestra la traza real de las tres etapas:

```text
ETAPA 0 - La caja volcada (orden aleatorio):
  [22, 4, 19, 7, 16, 5, 1, 14, 10, 23, 15, 21, 3, 6, 8, 17, 9, 11, 2, 20, 12, 18, 13]
ETAPA 1 - Agrupados por genero (insercion binaria, femenino primero):
  [22, 19, 7, 16, 23, 6, 9, 11, 20, 13, 4, 5, 1, 14, 10, 15, 21, 3, 8, 17, 2, 12, 18]
ETAPA 2 - Ordenados por ID con MergeSort (lista autoincremental):
  [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23]
```

Esa traza se guarda durante `prepararJuego()` y no se vuelve a calcular: lo que
se muestra es el desorden real con el que arrancó esta ejecución.

## 4. Divide y Conquista

Divide y Conquista se aplica en tres lugares, repartidos en dos clases:

| Dónde | Qué hace | Clase | Complejidad |
|---|---|---|---|
| Etapa 1 | Inserción por búsqueda binaria | `Buscador` | `Θ(log n)` por personaje |
| Etapa 2 | Ordenamiento MergeSort | `Ordenador` | `Θ(n log n)` |
| Adivinar por ID | Búsqueda binaria | `Buscador` | `Θ(log n)` |

### MergeSort (`clases/Ordenador.java`)

El vector se parte en dos mitades, cada una se ordena recursivamente y después
se combinan con `Merge`, que recorre las dos mitades ya ordenadas una sola vez.

```text
T(n) = 2·T(n/2) + Θ(n)
```

Se resuelve con el Teorema Maestro: hay `a = 2` llamadas recursivas, cada una
sobre un subproblema `b = 2` veces más chico, y combinar cuesta `Θ(n)`, o sea
`k = 1`. Como `a = b^k` (`2 = 2¹`), la recurrencia da **`Θ(n log n)`**.

### Búsqueda binaria (`clases/Buscador.java`)

Se compara con el elemento del medio y se descarta media lista en cada paso.

```text
T(n) = T(n/2) + Θ(1)
```

Acá `a = 1`, `b = 2` y `k = 0`. Otra vez `a = b^k` (`1 = 2⁰`), y la recurrencia
da **`Θ(log n)`**.

Como la Etapa 2 dejó la lista ordenada por ID, adivinar un personaje concreto no
requiere recorrerla entera: alcanza con una búsqueda binaria.

### Una precisión sobre el Merge

El pseudocódigo habitual del `Merge` evalúa `u[i] ≤ u[j]` antes de verificar que
todavía queden elementos en la mitad izquierda. En esta implementación el orden
de las condiciones está invertido: primero se comprueba que cada mitad tenga
elementos disponibles y recién después se comparan los IDs. Es más claro de leer
y no depende de que los índices se comporten bien por casualidad.

### Por qué MergeSort y no QuickSort

**MergeSort garantiza `Θ(n log n)` en todos los casos. QuickSort no.**

QuickSort parte el vector alrededor de un pivot. Cuando el pivot es el primer
elemento, cae en un extremo justo si la entrada ya viene ordenada o casi
ordenada, y la partición deja un lado vacío. La recurrencia degenera:

```text
T(n) = T(n-1) + Θ(n) = Θ(n²)
```

Es decir, el mismo orden que un método de ordenamiento simple, perdiendo toda la
ventaja de Divide y Conquista.

En este proyecto la entrada es **impredecible**: se baraja en cada partida, así
que por azar puede llegar casi ordenada. MergeSort no tiene caso degenerado. El
precio es el vector auxiliar del `Merge`, que agrega `Θ(n)` de memoria extra; con
`n = 23` es irrelevante frente a la garantía de complejidad temporal.

### Por qué no se usaron otras técnicas

**Ordenamiento por burbujeo o por inserción.** Los dos ordenan correctamente,
pero comparan de a pares recorriendo el vector una y otra vez, lo que da `Θ(n²)`.
No dividen el problema: cada paso vuelve a mirar el total. Se descartaron porque
MergeSort resuelve lo mismo en `Θ(n log n)` y porque la consigna pide aplicar
Divide y Conquista, que es precisamente lo que estos métodos no hacen.

**Búsqueda lineal.** Recorrer los 23 personajes hasta encontrar un ID cuesta
`O(n)`. Se usa búsqueda binaria `Θ(log n)` en su lugar, y es posible únicamente
porque la Etapa 2 dejó la lista ordenada. El ordenamiento no es decorativo: es
lo que habilita la búsqueda eficiente.

**Programación dinámica.** Es la técnica indicada cuando un problema se rompe en
subproblemas que **se repiten**, y conviene guardar cada resultado para no
recalcularlo. El caso típico es Fibonacci: la versión recursiva ingenua vuelve a
calcular `fib(3)` decenas de veces, y memorizarlo lo baja de exponencial a
lineal. Acá no aplica: cada pregunta parte el conjunto de candidatos en dos
grupos **disjuntos** que no se vuelven a visitar. No hay ningún subproblema
repetido que valga la pena guardar.

**Fuerza bruta.** Se podría evaluar todas las secuencias posibles de preguntas
para encontrar la que resuelve en menos turnos garantizados. Con 12 preguntas
las secuencias posibles son `12!`, más de 479 millones, y habría que evaluar cada
una contra los 23 secretos posibles. Greedy encuentra una solución muy buena
mirando sólo el turno actual, y se explica en la sección siguiente.

## 5. Greedy

Se implementa en `clases/Comodin.java`. Un algoritmo Greedy toma en cada paso la
mejor decisión disponible **en ese momento**, sin simular el resto de la partida
y sin volver atrás sobre lo ya decidido.

La máquina toma dos decisiones de este tipo por turno: primero evalúa si conviene
arriesgar, y si no, elige qué preguntar.

### Los cinco elementos del esquema Greedy

| Elemento | En este juego |
|---|---|
| Conjunto de candidatos | Los personajes que todavía no fueron descartados |
| Función de selección | La pregunta que minimiza el peor caso |
| Función de factibilidad | Que la pregunta no se haya usado y que divida el conjunto |
| Función de solución | Queda un único candidato posible |
| Función objetivo | Usar la menor cantidad de turnos |

### La función de selección

Para cada pregunta disponible se cuenta cuántos candidatos responderían `SÍ` y
cuántos `NO`. La máquina elige la pregunta cuyo **peor caso** sea más chico:

```text
puntaje(pregunta) = max(cantidadSi, cantidadNo)
```

Se descartan las preguntas que no dividen, es decir aquellas donde todos los
candidatos responden lo mismo, porque no aportan información.

Minimizar el peor caso equivale a buscar la partición más balanceada posible. Es
el mismo principio que sostiene la búsqueda binaria: si cada pregunta parte el
conjunto casi por la mitad, la cantidad de turnos crece como `log₂` de la
cantidad de personajes. Con 23 candidatos, `log₂(23) ≈ 4,52`.

### El desempate no es al azar

Varias preguntas pueden empatar en el mismo peor caso. En ese caso **no se
sortea**: se desempata con dos reglas fijas, aplicadas en orden.

1. Se prefiere un filtro de los que lista la consigna (género, calvicie, lentes,
   colores de pelo) sobre uno de los atributos declarados adicionalmente. Ante
   igual puntaje las dos preguntas sirven exactamente lo mismo, así que se
   privilegia la consigna.
2. Si el empate persiste, se toma la primera en el orden en que están declaradas.

Gracias a esto cada partida es reproducible y toda elección se puede auditar
leyendo la consola.

### Costo por turno

Evaluar una pregunta requiere recorrer los candidatos una vez. Con `q` preguntas
y `n` candidatos, evaluarlas todas cuesta `Θ(q·n)`.

En este proyecto `q` vale siempre 12, porque las preguntas están fijas y no
dependen del tamaño del tablero. Una constante no afecta el orden de
complejidad, así que `Θ(12·n)` se escribe **`Θ(n)`**: el costo de un turno crece
de forma proporcional a la cantidad de candidatos que quedan, y como esa
cantidad baja turno a turno, los turnos se van abaratando.

### Segunda decisión Greedy: preguntar o jugársela

La máquina no espera a tener certeza absoluta para tirar un nombre. Antes de
elegir pregunta evalúa si le conviene arriesgar, y esa comparación también es
voraz, porque mira sólo el turno actual:

- **Preguntar** no gana la partida, pero garantiza reducir el peor caso.
- **Arriesgar** gana con probabilidad `1/k`, donde `k` es la cantidad de
  candidatos que quedan. Si falla, ese candidato se descarta, así que la apuesta
  perdida igual aporta información.

Cada máquina tiene un **umbral de riesgo**, es decir, la probabilidad mínima de
acierto que le exige a una apuesta para animarse a hacerla. La máquina arriesga
cuando se cumplen las dos condiciones de su personalidad: que hayan pasado
suficientes preguntas desde la última apuesta y que `1/k` alcance ese umbral.

| Personalidad | Preguntas entre apuestas | Umbral | Arriesga con |
|---|---:|---:|---:|
| `CAUTELOSA` | 4 | 50 % | 2 candidatos o menos |
| `NORMAL` | 3 | 33 % | 3 candidatos o menos |
| `AUDAZ` | 2 | 20 % | 5 candidatos o menos |

Un ejemplo: con 8 candidatos la probabilidad de acertar es `1/8 = 12,5 %`, así
que ninguna de las tres arriesga y todas preguntan. Con 4 candidatos es `25 %`, y
sólo la `AUDAZ` se la juega.

Esto es distinto del caso en que **queda un único candidato**. Ahí no hay
apuesta ni intervienen las personalidades: la máquina ya sabe la respuesta y
simplemente la dice.

### Cuánto cuesta cada umbral

Se midieron los 23 secretos posibles con cada personalidad:

| Personalidad | Turnos promedio |
|---|---:|
| `CAUTELOSA` | 5,30 |
| `NORMAL` | **5,26** |
| `AUDAZ` | 5,35 |

Las tres resuelven siempre y ninguna necesita más de 6 turnos. Las tres son
mejores que no arriesgar nunca y esperar a tener un único candidato, que da 5,61
turnos de promedio.

El dato interesante es que existe un **punto intermedio óptimo**. Arriesgar de
menos desperdicia turnos preguntando cuando ya casi no queda información por
ganar. Arriesgar de más los desperdicia en apuestas con poca probabilidad de
acertar. La `NORMAL` está en el medio y es la que menos turnos necesita.

### Las dos máquinas virtuales

Las dos máquinas aplican **el mismo criterio Greedy** para elegir la pregunta,
minimizar el peor caso, y se diferencian en el umbral de riesgo:

| Máquina | Personalidad | Arriesga cuando quedan |
|---|---|---|
| A | `CAUTELOSA` | 2 candidatos o menos |
| B | `AUDAZ` | 5 candidatos o menos |

Se eligieron los dos extremos del rango a propósito, para que el contraste se
note en la traza. En Humano vs Máquina el jugador elige contra cuál de las tres
personalidades quiere jugar: no se sortea, así queda explícito a qué se enfrenta.

### Alcance del criterio

Greedy elige lo mejor para el turno actual, no la mejor secuencia completa de
preguntas. Puede existir un orden distinto que resuelva algún secreto en menos
turnos, pero encontrarlo requeriría explorar todas las secuencias posibles. El
resultado medido, entre 5,26 y 5,35 turnos contra un mínimo teórico de 4,52,
muestra que la decisión local queda muy cerca del óptimo a un costo mucho menor.

## 6. Ninguna decisión de la máquina es al azar

`JugadorMaquina` y `Comodin` **no reciben ni usan `Random`**. Las tres decisiones
que toma una máquina se derivan de un criterio explícito, y ese criterio se
imprime en la consola:

| Decisión | Criterio |
|---|---|
| Qué pregunta hacer | El menor peor caso; ante empate, primero un filtro de la consigna y después el orden declarado |
| Cuándo arriesgar | Que `1/k` alcance el umbral de la personalidad y que hayan pasado las preguntas mínimas |
| A quién apostar | El candidato de menor ID, es decir el primero de la lista que dejó ordenada el MergeSort |

Sobre la última hay algo que aclarar. Cuando la máquina decide apostar, todos los
candidatos que sobreviven tienen la misma probabilidad de ser el secreto, `1/k`.
Ninguna elección acierta más seguido que otra, así que **no existe un criterio
que mejore el resultado**. Lo que sí se puede elegir es que la decisión sea
determinista y auditable, y por eso se toma el de menor ID: sortear daría el
mismo resultado estadístico pero no se podría justificar frente a la pregunta
"¿qué criterio usaste?".

La consecuencia es que **dos partidas contra el mismo secreto producen
exactamente la misma traza, jugada por jugada**. Hay una prueba automática que lo
verifica.

### Dónde sí se usa azar, y por qué

El único `Random` del proyecto está en `Funcionalidad`, y se usa para dos cosas
que no son decisiones de la máquina sino **condiciones iniciales de la partida**:

- barajar los 23 personajes en la Etapa 0 (por ejemplo, es lo que ocurre al
  volcar la caja de un juego de mesa y encontrar las piezas tiradas);
- elegir los secretos de cada partida, es decir qué personaje le toca a cada
  jugador.

La distinción es la que importa: el azar puede **modelar el mundo**, pero no
puede **reemplazar un criterio**. Si el orden inicial fuera fijo, el MergeSort no
tendría nada que ordenar y la Etapa 2 sería decorativa.

## 7. Preguntas: por qué son doce y no seis

`Funcionalidad.crearPreguntas()` crea un array fijo de doce preguntas, y
`Pregunta.evaluar` centraliza cómo se responde cada una. Seis son los filtros que
lista la consigna y seis son atributos adicionales declarados por el grupo:

| Filtros de la consigna | Atributos declarados adicionales |
|---|---|
| género, calvicie, lentes, pelo colorado / negro / amarillo | poderes, capa, máscara, arma, vuela, universo |

### Demostración: los seis filtros de la consigna no alcanzan

La consigna pide en su primera regla **23 personajes con características
distinguibles**, y más abajo lista seis filtros aplicables. Las dos cosas juntas
son imposibles, y se puede demostrar.

Los seis filtros no son seis atributos independientes. La calvicie y los tres
colores de pelo son **cuatro estados mutuamente excluyentes de un mismo
atributo**: un personaje calvo no tiene color de pelo, y uno con pelo tiene
exactamente uno de los tres colores. Quedan entonces tres dimensiones reales:

```text
género (2) × lentes (2) × estado del pelo (3 colores + calvo = 4) = 16
```

Dieciséis combinaciones posibles para veintitrés personajes. Por el **principio
del palomar**, si hay más objetos que casilleros al menos un casillero recibe más
de un objeto: sin importar cómo se repartan los atributos, quedan como mínimo
siete personajes indistinguibles de otro.

Se verificó sobre el elenco real usando únicamente los seis filtros de la
consigna. Los 23 personajes colapsan en **8 clases de equivalencia**, y en siete
de ellas hay más de un personaje:

| Personajes con la misma firma | Cuántos |
|---|---:|
| Capitán América, Thor, Flash, Aquaman | 4 |
| Pantera Negra, Avispa, Gamora, Mujer Maravilla | 4 |
| Spider-Man, Hulk, Superman | 3 |
| Iron Man, Doctor Strange, Batman | 3 |
| Jean Grey, Viuda Negra, Chica Halcón | 3 |
| Deadpool, Profesor X, Cyborg | 3 |
| Supergirl, Canario Negro | 2 |

Con esos seis filtros el juego sería imposible de ganar por deducción: ninguna
combinación de preguntas separa a Thor de Aquaman.

Ampliar el conjunto de atributos no es una licencia que se tomó el grupo, es la
única forma de cumplir la primera regla. La expresión *"características
distinguibles **a declarar**"* es justamente lo que habilita a declarar atributos
además de los listados.

### Cuántos atributos adicionales hacen falta

Se recorrieron todas las combinaciones posibles de los seis atributos extra para
encontrar el conjunto mínimo que distingue a los 23 personajes:

| Atributos extra | ¿Alcanza? |
|---|---|
| 1 | No, ninguna de las 6 combinaciones |
| 2 | No, ninguna de las 15 combinaciones |
| 3 | Sí, 3 de las 20 combinaciones |

Las tres que funcionan son máscara + arma + vuela, máscara + arma + universo y
máscara + vuela + universo. El mínimo es entonces **tres**.

El proyecto usa los seis. Los tres restantes se conservan porque aportan cortes
distintos que le dan más opciones al Greedy, sin agrandar el problema: el costo
por turno sigue siendo `Θ(n)`.

### Balance de los filtros

El Greedy elige la pregunta que minimiza el peor grupo restante, así que un
filtro donde casi todos responden lo mismo es un filtro que casi nunca se va a
elegir. El elenco se diseñó para que los filtros de la consigna sean útiles:

| Filtro | Sí | No | Peor caso |
|---|---:|---:|---:|
| género masculino *(consigna)* | 13 | 10 | 13 |
| pelo negro *(consigna)* | 10 | 13 | 13 |
| pelo amarillo *(consigna)* | 6 | 17 | 17 |
| lentes *(consigna)* | 4 | 19 | 19 |
| pelo colorado *(consigna)* | 4 | 19 | 19 |
| calvicie *(consigna)* | 3 | 20 | 20 |
| máscara | 11 | 12 | 12 |
| arma | 13 | 10 | 13 |
| vuela | 10 | 13 | 13 |
| universo Marvel | 13 | 10 | 13 |
| capa | 6 | 17 | 17 |
| poderes | 20 | 3 | 20 |

### Por qué la calvicie sigue estando aunque esté desbalanceada

La calvicie queda en 3 contra 20 y se evaluó sacarla del juego. Se decidió
mantenerla por dos razones.

**Primera: sin ella el juego se rompe.** Al quitar la calvicie, Capitán América y
Deadpool quedan con firma idéntica en los once filtros restantes, y ninguna
combinación de los atributos disponibles los separa. Un filtro poco frecuente
puede ser irremplazable: no aporta en el caso promedio, pero es el único que
separa un par concreto.

**Segunda: está entre los filtros que lista la consigna.** El proyecto ya agrega
seis atributos que la consigna no menciona, y esa decisión está justificada con
la demostración de arriba. Quitar además uno de los que sí menciona sería
incoherente.

El desbalance tampoco es un defecto de diseño: en el juego de mesa original la
calvicie también es un rasgo minoritario. Es un filtro de **baja frecuencia pero
alta información**: casi siempre responde `NO` y descarta poco, pero cuando
responde `SÍ` deja apenas tres candidatos. El Greedy lo detecta solo y lo pospone
(es decir, lo deja para el final en lugar de elegirlo primero), que es
exactamente el comportamiento esperado del criterio de minimizar el peor caso.

## 8. Separación entre la consola y la lógica

El razonamiento de la máquina se ve en la consola, pero **si se apaga esa salida
el juego funciona igual**. Eso se resuelve por diseño y no con condicionales
repartidos por el código.

Ningún método del paquete `clases` escribe en `System.out`. Todos reciben un
`PrintStream salida` y un `boolean mostrarProceso`. La lógica no depende en
ningún punto de que se haya impreso algo: la salida es un observador del proceso
y no parte de él, es decir, imprimir o no imprimir no cambia ninguna decisión ni
ningún resultado.

La opción 5 del menú alterna el razonamiento en tiempo real y sirve para
demostrarlo: con la traza activada o silenciada, la partida avanza y termina
exactamente igual. Las pruebas automáticas se apoyan en lo mismo, ya que corren
cientos de partidas contra un `PrintStream` vacío.

Ésta es la separación de responsabilidades que va a permitir agregar la interfaz
gráfica más adelante sin tocar los algoritmos: la vista de consola y la vista
gráfica van a ser dos observadores del mismo proceso.

## 9. Protección del secreto

El personaje del humano se elige mentalmente y **nunca se guarda en una
variable**. `JugadorMaquina` sólo recibe la interfaz `Respondedor`, que permite
preguntar o confirmar una suposición, pero no leer el secreto. En Humano vs
Máquina el `Respondedor` solicita por consola cada `sí/no`.

En Máquina vs Máquina, `SecretoMaquina` encapsula el personaje (es decir, lo
guarda en un campo privado y sólo expone los métodos de `Respondedor`). El rival
nunca recibe una referencia directa al secreto, y la coordinación lo revela
únicamente al terminar la partida.

## 10. Modos de juego

### Humano vs Máquina

El jugador elige primero contra cuál de las tres personalidades quiere jugar, y
después piensa su personaje sin escribirlo. La máquina elige el suyo al azar
entre los 23.

Los dos alternan turnos, y cada turno se gasta en **una sola** acción:

- El humano puede elegir una pregunta del listado, y el programa filtra
  automáticamente sus candidatos con la respuesta; o puede arriesgar un ID.
- La máquina evalúa primero si le conviene arriesgar según su umbral. Si no,
  elige la pregunta con `Comodin` y la muestra por consola para que el humano
  responda `s` o `n`.

Si queda un único candidato posible, la máquina lo dice directamente: ahí no hay
apuesta, ya tiene la respuesta.

### Máquina vs Máquina

Se eligen dos secretos distintos y las dos máquinas juegan solas. En cada turno
se muestra:

- los candidatos que le quedan a la máquina que juega;
- la decisión de riesgo, con la probabilidad de acertar contra su umbral;
- la evaluación Greedy de las doce preguntas, con `sí`, `no` y peor caso, y una
  marca en las que son filtros de la consigna;
- la pregunta elegida, con el peor caso que la ganó y el motivo del desempate si
  lo hubo;
- la respuesta, los personajes descartados y los que quedan;
- la apuesta, cuando la hay, con la cantidad de candidatos y la probabilidad.

Los secretos se revelan al terminar.

### Ver la inicialización

La opción 4 muestra la traza de las tres etapas guardada durante
`prepararJuego()`. No vuelve a barajar: exhibe el desorden real con el que
arrancó esta ejecución y cómo lo resolvió la máquina.

### Silenciar el razonamiento

La opción 5 activa o silencia la traza de la máquina, y el estado actual se ve en
el propio menú.

## 11. Clases

| Clase | Responsabilidad |
|---|---|
| `Main` | Punto de entrada |
| `Funcionalidad` | Menú, catálogo, inicialización en tres etapas y coordinación de partidas |
| `Personaje` | Modelo con los atributos del tablero |
| `ColorPelo` | Colores posibles de pelo |
| `Pregunta` | Texto de la pregunta y evaluación sobre un personaje |
| `Ordenador` | MergeSort por ID (Divide y Conquista) |
| `Buscador` | Inserción por búsqueda binaria y búsqueda por ID (Divide y Conquista) |
| `Comodin` | Selección de la pregunta (Greedy) |
| `Personalidad` | Umbral de riesgo para decidir cuándo arriesgar (Greedy) |
| `JugadorMaquina` | Candidatos y turnos de una máquina |
| `Respondedor` | Interfaz que limita el acceso al secreto |
| `SecretoMaquina` | Secreto encapsulado de una máquina |

## 12. Pruebas

`test/Pruebas.java` comprueba:

1. que existan 23 personajes, que la lista final sea autoincremental por ID y que
   la búsqueda Divide y Conquista encuentre cada ID;
2. que MergeSort deje la lista ordenada de 1 a 23 partiendo de **200 barajados
   distintos**, o sea que el ordenamiento no dependa del desorden inicial;
3. que la traza de inicialización contenga las tres etapas;
4. que los 23 personajes tengan firmas de características diferentes;
5. que la decisión Greedy de un turno sea efectivamente la de menor peor caso;
6. que la máquina resuelva los 23 secretos posibles;
7. que las tres personalidades resuelvan los 23 secretos sin que una apuesta
   fallida elimine nunca al personaje correcto;
8. que dos partidas contra el mismo secreto produzcan trazas idénticas, es decir
   que la máquina no tenga ninguna decisión al azar;
9. que la traza de una partida contenga el proceso completo.

Las pruebas no necesitan Maven, Gradle ni librerías externas. Se ejecutan con
`.\probar.ps1` en Windows o `./probar.sh` en Linux y macOS, y deben mostrar
`OK - 9 pruebas superadas.`

## 13. Estructura del proyecto

```text
AdivinaLaCarta/
├── src/
│   ├── Main.java                  punto de entrada
│   ├── clases/                    modelo y algoritmos
│   │   ├── Personaje.java
│   │   ├── ColorPelo.java
│   │   ├── Pregunta.java
│   │   ├── Personalidad.java      umbral de riesgo (Greedy)
│   │   ├── Ordenador.java         MergeSort (Divide y Conquista)
│   │   ├── Buscador.java          búsqueda binaria (Divide y Conquista)
│   │   ├── Comodin.java           selección de pregunta (Greedy)
│   │   ├── JugadorMaquina.java
│   │   ├── Respondedor.java
│   │   └── SecretoMaquina.java
│   └── funcionalidad/
│       └── Funcionalidad.java     menú, catálogo y coordinación
└── test/
    └── Pruebas.java
```

Los nombres siguen la convención de Java: `PascalCase` para clases y minúscula
para paquetes.

La carpeta `out/` con los `.class` compilados está excluida por `.gitignore`:
son artefactos generados, no código fuente.
