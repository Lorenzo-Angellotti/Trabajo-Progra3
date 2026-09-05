# Documentación del proyecto Adivina la Carta

## 1. Punto de partida

La guía inicial ya tenía una estructura clara: `Main`, una lista de personajes,
selección aleatoria, ingreso por `Scanner`, un método `adivinar` y dos clases
auxiliares vacías llamadas `Buscador` y `Comodin`.

La solución terminada conserva esa forma. `Main` sigue siendo pequeño y delega
en `Funcionalidad`. La clase `Personaje` continúa siendo una clase común con
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

## 3. Inicialización: la máquina ordena los personajes

La consigna pide que los personajes **empiecen ordenados únicamente según su
género** y que sea **la máquina** quien los disponga en una lista ordenada de
forma autoincremental. El profesor lo comparó con volcar la caja de un juego de
mesa: las piezas salen tiradas y alguien tiene que armarlas.

Por eso la inicialización ocurre en tres etapas y **ninguna depende de que los
datos vengan pre-ordenados**:

### Etapa 0 — La caja volcada

`crearCatalogoCrudo()` construye los 23 personajes con sus IDs fijos y
`Collections.shuffle(caja, random)` los desordena. El barajado usa el mismo
`Random` del juego, así que cada partida arranca con un desorden distinto y las
pruebas pueden reproducirlo con una semilla fija.

### Etapa 1 — Agrupar por género

La máquina inserta cada personaje usando búsqueda binaria de la posición:

```java
buscadorPersonajes.agregarOrdenadoPorGenero(agrupados, personaje);
```

Las mujeres (`false`) quedan primero y los hombres (`true`) después. Éste es
exactamente el estado *"ordenados únicamente según su género"* que describe la
consigna. Encontrar la posición cuesta `Θ(log n)` comparaciones; insertar
físicamente en un `ArrayList` puede desplazar elementos y cuesta `O(n)`. Se
documentan ambos costos para no confundir búsqueda con inserción completa.

### Etapa 2 — Ordenar por ID con MergeSort

```java
ordenador.ordenarPorId(agrupados);
```

La lista queda autoincremental de 1 a 23. Recién ahí empieza la partida.

La opción 4 del menú muestra la traza real de las tres etapas:

```text
ETAPA 0 - La caja volcada (orden aleatorio):
  [22, 10, 2, 14, 11, 23, 1, 18, 16, 3, 7, ...]
ETAPA 1 - Agrupados por genero (insercion binaria, femenino primero):
  [16, 7, 22, 10, 2, 14, 11, 23, 1, 18, 3, ...]
ETAPA 2 - Ordenados por ID con MergeSort (lista autoincremental):
  [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, ...]
```

## 4. Divide y Conquista

Se aplica en tres lugares distintos, en dos clases:

| Dónde | Qué hace | Clase | Complejidad |
|---|---|---|---|
| Etapa 1 | Inserción por búsqueda binaria | `Buscador` | `Θ(log n)` por personaje |
| Etapa 2 | Ordenamiento MergeSort | `Ordenador` | `Θ(n log n)` |
| Adivinar por ID | Búsqueda binaria | `Buscador` | `Θ(log n)` |

### MergeSort (`clases/Ordenador.java`)

Sigue el esquema de la Clase 02: dividir el intervalo en dos mitades, ordenar
cada una recursivamente y combinarlas con `Merge`.

```text
T(n) = 2·T(n/2) + Θ(n)
```

Con `a = 2`, `b = 2` y `k = 1` estamos en el caso `a = b^k`, que da `Θ(n log n)`.
El `Merge` recorre las dos mitades una sola vez, y ése es el `k = 1`.

**Detalle de implementación.** En el pseudocódigo de la teoría la condición del
`Merge` evalúa `u[i] ≤ u[j]` antes de verificar `i < mid + 1`. En la
implementación se invirtió el orden de las condiciones para comprobar primero
que queden elementos en cada mitad y recién después comparar los IDs.

**Errata detectada en el material de clase.** En el ejemplo de funcionamiento de
MergeSort de la Clase 02, al hacer el merge de `[5]` y `[3]` en la rama
izquierda, el diagrama muestra `5 3` cuando debería mostrar `3 5`. Se confirma
con el nivel siguiente del mismo diagrama, que da `1 3 5 7 8`: el 3 ya estaba
antes que el 5. La implementación de este proyecto produce el orden correcto.

### Por qué MergeSort y no QuickSort

**MergeSort garantiza `Θ(n log n)` en todos los casos. QuickSort no.**

El `Pivot` de la Clase 02 toma `u[ini]`, el primer elemento, como pivot. Ese
pivot cae en un extremo del subarreglo justo cuando la entrada ya viene ordenada
o casi ordenada, y en ese caso la partición deja un lado vacío. La recurrencia
degenera:

```text
T(n) = T(n-1) + Θ(n) = Θ(n²)
```

Es decir, el mismo orden que un método de ordenamiento simple, perdiendo toda la
ventaja de Divide y Conquista.

En este proyecto la entrada es **impredecible**: se baraja en cada partida, así
que por azar puede llegar casi ordenada. MergeSort no tiene caso degenerado. El
costo es el vector auxiliar de la etapa `Merge`, que agrega `Θ(n)` de espacio
extra; con `n = 23` es irrelevante frente a la garantía de complejidad temporal.

### Por qué no otras técnicas

- **Programación dinámica** (por ejemplo el Fibonacci con memoización que se vio
  en clase) no aplica: no existen subproblemas superpuestos que convenga
  guardar. Cada pregunta parte el conjunto de candidatos en dos grupos disjuntos
  que no se vuelven a visitar.
- **Búsqueda lineal** era lo que hacía el método `adivinar` de la primera
  versión: `O(n)`. Se reemplazó por búsqueda binaria `Θ(log n)`, que es posible
  precisamente porque la Etapa 2 dejó la lista ordenada.

## 5. Greedy

Se implementa en `clases/Comodin.java`. El viejo comodín que indicaba un rango
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

### Segunda decisión Greedy: preguntar o jugársela

El profesor pidió que la máquina no espere a tener certeza absoluta, sino que
cada ciertas preguntas *"se la juegue y tire un nombre"*. Esa también es una
decisión voraz, y se implementa en `clases/Personalidad.java`.

En cada turno la máquina compara dos opciones, sabiendo que **el turno se gasta
en una o en la otra**:

- **Preguntar** no gana la partida, pero garantiza reducir el peor caso.
- **Arriesgar** gana con probabilidad `1/k`, donde `k` es la cantidad de
  candidatos que quedan. Si falla, ese candidato se descarta, así que la apuesta
  perdida igual aporta información.

La máquina se la juega cuando se cumplen las dos condiciones de su personalidad:
que hayan pasado suficientes preguntas desde la última apuesta y que `1/k`
alcance su umbral de riesgo.

| Personalidad | Preguntas entre apuestas | Umbral | Arriesga con |
|---|---:|---:|---:|
| `CAUTELOSA` | 4 | 50 % | 2 candidatos o menos |
| `NORMAL` | 3 | 33 % | 3 candidatos o menos |
| `AUDAZ` | 2 | 20 % | 5 candidatos o menos |

Es voraz en el mismo sentido que la elección de pregunta: mira sólo el turno
actual, no simula el resto de la partida y no revisa decisiones anteriores.

**Resultado medido** sobre los 23 secretos posibles y 40 semillas distintas
(920 partidas por personalidad):

| Personalidad | Turnos promedio | Peor caso | Partidas sin resolver |
|---|---:|---:|---:|
| `CAUTELOSA` | 5,30 | 6 | 0 |
| `NORMAL` | **5,23** | 6 | 0 |
| `AUDAZ` | 5,37 | 7 | 0 |

Las tres resuelven siempre, y las tres mejoran el promedio de 5,52 turnos que
daba la versión que sólo suponía con un único candidato. Existe un **óptimo
intermedio**: arriesgar de menos desperdicia turnos preguntando cuando ya casi
no queda información por ganar, y arriesgar de más los desperdicia en apuestas
con poca probabilidad de acertar.

### Las dos máquinas virtuales

La consigna pide modelar dos máquinas. Las dos aplican **el mismo criterio
Greedy** para elegir la pregunta, minimizar el peor caso, y se diferencian en el
**umbral de riesgo** con el que deciden dejar de preguntar y jugársela.

| Máquina | Personalidad | Arriesga cuando quedan | Turnos promedio |
|---|---|---|---:|
| A | `CAUTELOSA` | 2 candidatos o menos | 5,30 |
| B | `AUDAZ` | 5 candidatos o menos | 5,37 |

El profesor pidió expresamente que la decisión de arriesgar responda a un
criterio numérico y no al azar, y describió el tipo de criterio esperado:
*"cuando yo reduzco las posibilidades en un 60% sí o sí me la juego porque ya
reduje más de la mitad"*. El umbral de riesgo es exactamente eso, expresado como
probabilidad de acierto `1/k`.

En Humano vs Máquina el jugador **elige** contra cuál de las tres personalidades
jugar. No se sortea, así queda explícito contra qué umbral se enfrenta.

### Ninguna decisión de la máquina es al azar

`JugadorMaquina` y `Comodin` **no tienen ninguna fuente de azar**: no reciben ni
usan `Random`. Las tres decisiones que toma una máquina se derivan de un
criterio explícito que se imprime en consola:

| Decisión | Criterio |
|---|---|
| Qué pregunta hacer | El menor peor caso; ante empate se prefiere un filtro de la consigna y, si persiste, el primero en el orden declarado |
| Cuándo arriesgar | `1/k ≥ umbral` de la personalidad, con las preguntas mínimas cumplidas |
| A quién apostar | El candidato de menor ID, o sea el primero de la lista que dejó ordenada el MergeSort |

Sobre la última: al llegar a la apuesta todos los candidatos que sobreviven son
**equiprobables**, cada uno con probabilidad `1/k`. Ninguna elección domina a
otra en términos de acierto, así que no existe un criterio que mejore el
resultado. Lo que sí importa es que la decisión sea determinista y auditable:
sortear sería indefendible frente a la pregunta "¿qué criterio usaste?".

Como consecuencia, **dos partidas contra el mismo secreto producen exactamente
la misma traza, jugada por jugada**. Hay una prueba automática que lo verifica.

### Dónde sí se usa azar, y por qué

El único `Random` del proyecto está en `Funcionalidad`, y se usa para dos cosas
que **no son decisiones de la máquina** sino condiciones iniciales del juego:

- barajar los 23 personajes en la Etapa 0, que modela volcar la caja del juego
  de mesa;
- elegir los secretos de cada partida.

La distinción es la que importa: el azar puede **modelar el mundo**, pero no
puede **reemplazar un criterio**. Si el orden inicial fuera fijo, el MergeSort
no tendría nada que ordenar y la Etapa 2 sería decorativa.

## 6. Preguntas: por qué son doce y no seis

`Funcionalidad.crearPreguntas()` crea un array fijo de doce preguntas y
`Pregunta.evaluar` centraliza cómo se responde cada una. Seis son las que lista
la consigna y seis son atributos adicionales declarados por el grupo:

| Filtros de la consigna | Atributos declarados adicionales |
|---|---|
| género, calvicie, lentes, pelo colorado / negro / amarillo | poderes, capa, máscara, arma, vuela, universo |

### Demostración: los seis filtros de la consigna no alcanzan

La consigna pide en su primera regla **23 personajes con características
distinguibles**, y más abajo lista seis filtros aplicables. Las dos cosas juntas
son imposibles, y se puede demostrar.

Los seis filtros no son seis atributos independientes. La calvicie y los tres
colores de pelo son **cuatro estados mutuamente excluyentes de un mismo
atributo**: un personaje calvo no tiene color de pelo, y un personaje con pelo
tiene exactamente uno de los tres colores. Quedan entonces tres dimensiones
reales:

```text
género (2) × lentes (2) × estado del pelo (3 colores + calvo = 4) = 16
```

Dieciséis combinaciones posibles para veintitrés personajes. Por el **principio
del palomar**, si hay más objetos que casilleros al menos un casillero recibe
más de un objeto: sin importar cómo se repartan los atributos, quedan como
mínimo siete personajes indistinguibles de otro.

Se verificó sobre el elenco real usando únicamente los seis filtros de la
consigna: los 23 personajes colapsan en **6 clases de equivalencia**, con un
grupo de once personajes con exactamente la misma firma. El juego sería
imposible de ganar por deducción.

Ampliar el conjunto de atributos no es una licencia que se tomó el grupo: es la
única forma de cumplir la primera regla de la consigna. La expresión
*"características distinguibles **a declarar**"* es justamente lo que habilita a
declarar atributos además de los listados.

### Cuántos atributos adicionales hacen falta

Se recorrieron todas las combinaciones posibles de los seis atributos extra para
buscar el conjunto mínimo que distingue a los 23 personajes:

| Atributos extra | ¿Alcanza? |
|---|---|
| 1 | No, ninguna de las 6 combinaciones |
| 2 | No, ninguna de las 15 combinaciones |
| 3 | No, ninguna de las 20 combinaciones |
| 4 | Sí, exactamente una: máscara + arma + vuela + universo |

El mínimo teórico es cuatro. El proyecto usa seis, apenas dos por encima del
mínimo: los dos restantes (poderes y capa) se conservan porque aportan cortes
distintos al Greedy sin agrandar el problema.

### Balance de los filtros

El Greedy elige la pregunta que minimiza el peor grupo restante, así que un
filtro donde casi todos responden lo mismo es un filtro que nunca se va a
elegir. El elenco se diseñó para que los filtros de la consigna sean útiles:

| Filtro | Sí | No | Peor caso |
|---|---:|---:|---:|
| género masculino *(consigna)* | 13 | 10 | 13 |
| calvicie *(consigna)* | 3 | 20 | 20 |
| lentes *(consigna)* | 4 | 19 | 19 |
| pelo colorado *(consigna)* | 4 | 19 | 19 |
| pelo negro *(consigna)* | 10 | 13 | 13 |
| pelo amarillo *(consigna)* | 6 | 17 | 17 |
| poderes | 20 | 3 | 20 |
| capa | 6 | 17 | 17 |
| máscara | 11 | 12 | 12 |
| arma | 13 | 10 | 13 |
| vuela | 10 | 13 | 13 |
| universo Marvel | 13 | 10 | 13 |

### Por qué la calvicie sigue estando aunque esté desbalanceada

La calvicie queda en 3 contra 20 y se evaluó sacarla del juego. Se decidió
mantenerla por dos razones.

**Primera: sin ella el juego se rompe.** Se verificó que al quitar la calvicie,
Capitán América y Deadpool quedan con firma idéntica en los once filtros
restantes, y ninguna combinación de los atributos disponibles los separa. Esto
muestra que un filtro poco frecuente puede ser irremplazable: no aporta en el
caso promedio, pero es el único que separa un par concreto.

**Segunda: está en la lista de filtros de la consigna.** El proyecto ya agrega
seis atributos que la consigna no menciona, y esa decisión está justificada con
la demostración de arriba. Quitar además uno de los que sí menciona sería
incoherente.

El desbalance no es un defecto de diseño. En el Adivina Quién original la
calvicie también es un rasgo minoritario, alrededor de 5 de 24 personajes. Es un
filtro de **baja frecuencia pero alta información**: casi siempre responde NO y
descarta poco, pero cuando responde SÍ deja apenas tres candidatos. El Greedy lo
detecta solo y lo pospone, que es exactamente el comportamiento esperado del
criterio de minimizar el peor caso.

### El criterio de los lentes

Los lentes se evalúan sobre la **identidad civil** del personaje, no sobre el
traje: Clark Kent usa lentes aunque Superman no. Para que el jugador humano no
tenga que adivinar el criterio, está enunciado en el texto mismo de la pregunta:
*"¿Usa lentes en su identidad civil?"*. Un criterio de evaluación que el jugador
no puede ver es un criterio que vuelve el juego injusto.

## 6.b Separación entre la consola y la lógica

El profesor pidió que el razonamiento de la máquina se vea en consola, pero
también que **si se apaga esa salida el juego siga funcionando igual**. Eso se
resuelve por diseño y no con condicionales repartidos por el código.

Ningún método del paquete `clases` escribe en `System.out`. Todos reciben un
`PrintStream salida` y un `boolean mostrarProceso`. La lógica del juego no
depende en ningún punto de que se haya impreso algo: la salida es un observador
del proceso, no parte de él.

La opción 5 del menú alterna el razonamiento en consola en tiempo real, y sirve
para demostrarlo: con la traza activada o silenciada, la partida avanza y
termina exactamente igual. Las pruebas automáticas se apoyan en lo mismo, ya que
corren cientos de partidas contra un `PrintStream` vacío.

Ésta es la separación de responsabilidades que va a permitir agregar la interfaz
Swing más adelante sin tocar los algoritmos: la vista de consola y la vista
gráfica van a ser dos observadores del mismo proceso.

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
array o intentar adivinar por ID. La máquina usa `Comodin` para seleccionar su
pregunta greedy y supone cuando queda un único candidato.

### Ver la inicialización

La opción 4 del menú muestra la traza de las tres etapas guardada durante
`prepararJuego()`. No vuelve a barajar: exhibe el desorden real con el que
arrancó esta ejecución y cómo lo resolvió la máquina.

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
- `Funcionalidad`: menú, catálogo, inicialización en tres etapas y coordinación
  de partidas.
- `Personaje`: modelo con los atributos pedidos.
- `Pregunta`: pregunta hardcodeada y evaluación de un personaje.
- `Ordenador`: MergeSort por ID (Divide y Conquista).
- `Personalidad`: umbral de riesgo para decidir cuándo la máquina se la juega.
- `Buscador`: inserción por búsqueda binaria y búsqueda por ID (Divide y
  Conquista).
- `Comodin`: selección Greedy.
- `JugadorMaquina`: candidatos y turnos de una máquina.
- `Respondedor`: acceso limitado al secreto.
- `SecretoMaquina`: secreto encapsulado.

## 10. Pruebas

`test/Pruebas.java` comprueba:

1. existencia de 23 personajes, lista final autoincremental por ID y búsqueda
   Divide y Conquista de cada ID;
2. que MergeSort deje la lista ordenada de 1 a 23 partiendo de **200 barajados
   distintos**, verificando que el ordenamiento no dependa del desorden inicial;
3. que la traza de inicialización contenga las tres etapas;
4. firmas de características diferentes;
5. mejor decisión greedy local;
6. resolución de los 23 secretos posibles;
7. que las tres personalidades resuelvan los 23 secretos sin que arriesgar
   elimine nunca al personaje correcto;
8. que dos partidas contra el mismo secreto produzcan trazas idénticas, o sea
   que la máquina no tenga ninguna decisión al azar;
9. presencia del proceso completo en la traza de partida.

Las pruebas no necesitan Maven, Gradle ni librerías externas. Se ejecutan con
`.\probar.ps1` y deben mostrar `OK - 9 pruebas superadas.`

## 11. Estructura del proyecto

```text
AdivinaLaCarta/
├── src/
│   ├── Main.java                  punto de entrada
│   ├── clases/                    modelo y algoritmos
│   │   ├── Personaje.java
│   │   ├── ColorPelo.java
│   │   ├── Pregunta.java
│   │   ├── Personalidad.java
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

Los nombres de clase siguen la convención de Java: `PascalCase` para clases y
`minúscula` para paquetes. Las clases `Buscador`, `Comodin` y `Funcionalidad`
venían en minúscula de la guía inicial y se renombraron.

La carpeta `out/` con los `.class` compilados está excluida por `.gitignore`:
son artefactos generados, no código fuente.
