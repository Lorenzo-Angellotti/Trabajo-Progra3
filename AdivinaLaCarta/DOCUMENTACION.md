# Documentación del proyecto Adivina la Carta

## 1. Qué hace el proyecto

Adivina la Carta es un juego de deducción con consola e interfaz gráfica Java Swing sobre un tablero de 23
superhéroes. Cada jugador tiene un personaje secreto, y gana el primero que
descubre el del rival.

En cada turno un jugador puede hacer una sola de estas dos cosas:

- preguntar por un atributo, y descartar a todos los personajes cuya
  respuesta no coincida;
- arriesgar un nombre, y ganar si acierta.

El turno se gasta en una o en la otra. Se puede jugar Humano contra Máquina o
Máquina contra Máquina.

El proyecto aplica dos técnicas algorítmicas:

- Técnica: Divide y Conquista | Dónde se usa: Ordenar los personajes y buscarlos por ID | Clase: Ordenador, Buscador
- Técnica: Greedy | Dónde se usa: Elegir qué preguntar y decidir cuándo arriesgar | Clase: Comodin, Personalidad

Main es el punto de entrada: sin argumentos abre la consola mediante
Funcionalidad, y con --swing abre VentanaPrincipal. Funcionalidad prepara el
catálogo y administra el menú de consola; Partida coordina Humano contra Máquina
tanto en consola como en Swing. Los algoritmos y el modelo viven en el paquete
clases.

## 2. Personajes y atributos

Los IDs van del 1 al 23: Marvel del 1 al 13 y DC del 14 al 23.

Cada Personaje contiene:

- id y nombre;
- generoMasculino;
- poderes, capa, mascara, arma, vuela, lentes y calvicie;
- ColorPelo: COLORADO, NEGRO o AMARILLO;
- universoMarvel (false representa DC);
- elegido, que marca al secreto en el método adivinar(int id).

La matriz completa de los 23 personajes, con el valor de cada atributo y los
criterios con que se asignaron, está en PERSONAJES.md.

## 3. Inicialización: la máquina ordena los personajes

Los personajes no arrancan ordenados. Salen desordenados, como al volcar la caja
de un juego de mesa, y es la máquina la que tiene que armar el tablero: los
agrupa por género y después los deja en una lista autoincremental por ID.

La inicialización ocurre en tres etapas, y ninguna depende de que los datos
vengan pre-ordenados.

## Etapa 0: La caja volcada

crearCatalogoCrudo() construye los 23 personajes con sus IDs fijos y
Collections.shuffle(caja, random) los desordena.

El barajado usa el mismo Random del juego, así que cada partida arranca con un
desorden distinto. Las pruebas automáticas, en cambio, construyen el juego con
una semilla fija, es decir un valor inicial conocido que hace que Random
produzca siempre la misma secuencia: así el mismo desorden se puede reproducir
las veces que haga falta y un fallo se puede investigar.

## Etapa 1: Agrupar por género

La máquina inserta cada personaje buscando su posición con búsqueda binaria:

```
buscadorPersonajes.agregarOrdenadoPorGenero(agrupados, personaje);
```

Las mujeres (false) quedan primero y los hombres (true) después. Este es el
estado intermedio: la lista queda ordenada únicamente por género, sin ningún
orden interno de IDs.

Encontrar la posición cuesta Θ(log n) comparaciones. Insertar físicamente en
un ArrayList puede desplazar elementos y cuesta O(n). Se documentan los dos
costos por separado para no confundir la búsqueda de la posición con la
inserción completa.

## Etapa 2: Ordenar por ID con MergeSort

```
ordenador.ordenarPorId(agrupados);
```

La lista queda autoincremental de 1 a 23. Recién ahí empieza la partida.

La opción 4 del menú muestra la traza real de las tres etapas:

```
ETAPA 0 - La caja volcada (orden aleatorio):
  [22, 4, 19, 7, 16, 5, 1, 14, 10, 23, 15, 21, 3, 6, 8, 17, 9, 11, 2, 20, 12, 18, 13]
ETAPA 1 - Agrupados por genero (insercion binaria, femenino primero):
  [22, 19, 7, 16, 23, 6, 9, 11, 20, 13, 4, 5, 1, 14, 10, 15, 21, 3, 8, 17, 2, 12, 18]
ETAPA 2 - Ordenados por ID con MergeSort (lista autoincremental):
  [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23]
```

Esa traza se guarda durante prepararJuego() y no se vuelve a calcular: lo que
se muestra es el desorden real con el que arrancó esta ejecución.

## 4. Divide y Conquista

Divide y Conquista se aplica en tres lugares, repartidos en dos clases:

- Dónde: Etapa 1 | Qué hace: Inserción por búsqueda binaria | Clase: Buscador | Complejidad: Θ(log n) por personaje
- Dónde: Etapa 2 | Qué hace: Ordenamiento MergeSort | Clase: Ordenador | Complejidad: Θ(n log n)
- Dónde: Adivinar por ID | Qué hace: Búsqueda binaria | Clase: Buscador | Complejidad: Θ(log n)

## MergeSort (clases/Ordenador.java)

El vector se parte en dos mitades, cada una se ordena recursivamente y después
se combinan con Merge, que recorre las dos mitades ya ordenadas una sola vez.

```
T(n) = 2·T(n/2) + Θ(n)
```

Se resuelve con el Teorema Maestro: hay a = 2 llamadas recursivas, cada una
sobre un subproblema b = 2 veces más chico, y combinar cuesta Θ(n), o sea
k = 1. Como a = b^k (2 = 2¹), la recurrencia da Θ(n log n).

## Búsqueda binaria (clases/Buscador.java)

Se compara con el elemento del medio y se descarta media lista en cada paso.

```
T(n) = T(n/2) + Θ(1)
```

Acá a = 1, b = 2 y k = 0. Otra vez a = b^k (1 = 2⁰), y la recurrencia
da Θ(log n).

Como la Etapa 2 dejó la lista ordenada por ID, adivinar un personaje concreto no
requiere recorrerla entera: alcanza con una búsqueda binaria.

## Una precisión sobre el Merge

El pseudocódigo habitual del Merge evalúa u[i] ≤ u[j] antes de verificar que
todavía queden elementos en la mitad izquierda. En esta implementación el orden
de las condiciones está invertido: primero se comprueba que cada mitad tenga
elementos disponibles y recién después se comparan los IDs. Es más claro de leer
y no depende de que los índices se comporten bien por casualidad.

## Por qué MergeSort y no QuickSort

MergeSort garantiza Θ(n log n) en todos los casos. QuickSort no.

QuickSort parte el vector alrededor de un pivot. Cuando el pivot es el primer
elemento, cae en un extremo justo si la entrada ya viene ordenada o casi
ordenada, y la partición deja un lado vacío. La recurrencia degenera:

```
T(n) = T(n-1) + Θ(n) = Θ(n²)
```

Es decir, el mismo orden que un método de ordenamiento simple, perdiendo toda la
ventaja de Divide y Conquista.

En este proyecto la entrada es impredecible: se baraja en cada partida, así
que por azar puede llegar casi ordenada. MergeSort no tiene caso degenerado. El
precio es el vector auxiliar del Merge, que agrega Θ(n) de memoria extra; con
n = 23 es irrelevante frente a la garantía de complejidad temporal.

## Por qué no se usaron otras técnicas

Ordenamiento por burbujeo o por inserción. Los dos ordenan correctamente,
pero comparan de a pares recorriendo el vector una y otra vez, lo que da Θ(n²).
No dividen el problema: cada paso vuelve a mirar el total. Se descartaron porque
MergeSort resuelve lo mismo en Θ(n log n) y porque la consigna pide aplicar
Divide y Conquista, que es precisamente lo que estos métodos no hacen.

Búsqueda lineal. Recorrer los 23 personajes hasta encontrar un ID cuesta
O(n). Se usa búsqueda binaria Θ(log n) en su lugar, y es posible únicamente
porque la Etapa 2 dejó la lista ordenada. El ordenamiento no es decorativo: es
lo que habilita la búsqueda eficiente.

Programación dinámica. Es la técnica indicada cuando un problema se rompe en
subproblemas que se repiten, y conviene guardar cada resultado para no
recalcularlo. El caso típico es Fibonacci: la versión recursiva ingenua vuelve a
calcular fib(3) decenas de veces, y memorizarlo lo baja de exponencial a
lineal. Acá no aplica: cada pregunta parte el conjunto de candidatos en dos
grupos disjuntos que no se vuelven a visitar. No hay ningún subproblema
repetido que valga la pena guardar.

Fuerza bruta. Se podría evaluar todas las secuencias posibles de preguntas
para encontrar la que resuelve en menos turnos garantizados. Con 12 preguntas
las secuencias posibles son 12!, más de 479 millones, y habría que evaluar cada
una contra los 23 secretos posibles. Greedy encuentra una solución muy buena
mirando sólo el turno actual, y se explica en la sección siguiente.

## 5. Greedy

Se implementa en clases/Comodin.java. Un algoritmo Greedy toma en cada paso la
mejor decisión disponible en ese momento, sin simular el resto de la partida
y sin volver atrás sobre lo ya decidido.

La máquina toma dos decisiones de este tipo por turno: primero evalúa si conviene
arriesgar, y si no, elige qué preguntar.

## Los cinco elementos del esquema Greedy

- Elemento: Conjunto de candidatos | En este juego: Los personajes que todavía no fueron descartados
- Elemento: Función de selección | En este juego: La pregunta que minimiza el peor caso
- Elemento: Función de factibilidad | En este juego: Que la pregunta no se haya usado y que divida el conjunto
- Elemento: Función de solución | En este juego: Queda un único candidato posible
- Elemento: Función objetivo | En este juego: Usar la menor cantidad de turnos

## La función de selección

Para cada pregunta disponible se cuenta cuántos candidatos responderían SÍ y
cuántos NO. La máquina elige la pregunta cuyo peor caso sea más chico:

```
puntaje(pregunta) = max(cantidadSi, cantidadNo)
```

Se descartan las preguntas que no dividen, es decir aquellas donde todos los
candidatos responden lo mismo, porque no aportan información.

Minimizar el peor caso equivale a buscar la partición más balanceada posible. Es
el mismo principio que sostiene la búsqueda binaria: si cada pregunta parte el
conjunto casi por la mitad, la cantidad de turnos crece como log₂ de la
cantidad de personajes. Con 23 candidatos, log₂(23) ≈ 4,52.

## El desempate no es al azar

Varias preguntas pueden empatar en el mismo peor caso. En ese caso no se
sortea: se desempata con dos reglas fijas, aplicadas en orden.

1. Se prefiere un filtro de los que lista la consigna (género, calvicie, lentes,
   colores de pelo) sobre uno de los atributos declarados adicionalmente. Ante
   igual puntaje las dos preguntas sirven exactamente lo mismo, así que se
   privilegia la consigna.
2. Si el empate persiste, se toma la primera en el orden en que están declaradas.

Gracias a esto cada partida es reproducible y toda elección se puede auditar
leyendo la consola.

## Costo por turno

Evaluar una pregunta requiere recorrer los candidatos una vez. Con q preguntas
y n candidatos, evaluarlas todas cuesta Θ(q·n).

En este proyecto q vale siempre 12, porque las preguntas están fijas y no
dependen del tamaño del tablero. Una constante no afecta el orden de
complejidad, así que Θ(12·n) se escribe Θ(n): el costo de un turno crece
de forma proporcional a la cantidad de candidatos que quedan, y como esa
cantidad baja turno a turno, los turnos se van abaratando.

## Segunda decisión Greedy: preguntar o jugársela

La máquina no espera a tener certeza absoluta para tirar un nombre. Antes de
elegir pregunta evalúa si le conviene arriesgar, y esa comparación también es
voraz, porque mira sólo el turno actual:

- Preguntar no gana la partida, pero garantiza reducir el peor caso.
- Arriesgar gana con probabilidad 1/k, donde k es la cantidad de
  candidatos que quedan. Si falla, ese candidato se descarta, así que la apuesta
  perdida igual aporta información.

Cada máquina tiene un umbral de riesgo, es decir, la probabilidad mínima de
acierto que le exige a una apuesta para animarse a hacerla. La máquina arriesga
cuando se cumplen las dos condiciones de su personalidad: que hayan pasado
suficientes preguntas desde la última apuesta y que 1/k alcance ese umbral.

- Personalidad: CAUTELOSA | Preguntas entre apuestas: 4 | Umbral: 50 % | Arriesga con: 2 candidatos o menos
- Personalidad: NORMAL | Preguntas entre apuestas: 3 | Umbral: 33 % | Arriesga con: 3 candidatos o menos
- Personalidad: AUDAZ | Preguntas entre apuestas: 2 | Umbral: 20 % | Arriesga con: 5 candidatos o menos

Un ejemplo: con 8 candidatos la probabilidad de acertar es 1/8 = 12,5 %, así
que ninguna de las tres arriesga y todas preguntan. Con 4 candidatos es 25 %, y
sólo la AUDAZ se la juega.

Esto es distinto del caso en que queda un único candidato. Ahí no hay
apuesta ni intervienen las personalidades: la máquina ya sabe la respuesta y
simplemente la dice.

## Cuánto cuesta cada umbral

Se midieron los 23 secretos posibles con cada personalidad:

- Personalidad: CAUTELOSA | Turnos promedio: 5,30
- Personalidad: NORMAL | Turnos promedio: 5,26
- Personalidad: AUDAZ | Turnos promedio: 5,35

Las tres resuelven siempre y ninguna necesita más de 6 turnos. Las tres son
mejores que no arriesgar nunca y esperar a tener un único candidato, que da 5,61
turnos de promedio.

El dato interesante es que existe un punto intermedio óptimo. Arriesgar de
menos desperdicia turnos preguntando cuando ya casi no queda información por
ganar. Arriesgar de más los desperdicia en apuestas con poca probabilidad de
acertar. La NORMAL está en el medio y es la que menos turnos necesita.

## Las dos máquinas virtuales

Las dos máquinas aplican el mismo criterio Greedy para elegir la pregunta,
minimizar el peor caso, y se diferencian en el umbral de riesgo:

- Máquina: A | Personalidad: CAUTELOSA | Arriesga cuando quedan: 2 candidatos o menos
- Máquina: B | Personalidad: AUDAZ | Arriesga cuando quedan: 5 candidatos o menos

Se eligieron los dos extremos del rango a propósito, para que el contraste se
note en la traza. En Humano vs Máquina el jugador elige contra cuál de las tres
personalidades quiere jugar: no se sortea, así queda explícito a qué se enfrenta.

## Alcance del criterio

Greedy elige lo mejor para el turno actual, no la mejor secuencia completa de
preguntas. Puede existir un orden distinto que resuelva algún secreto en menos
turnos, pero encontrarlo requeriría explorar todas las secuencias posibles. El
resultado medido, entre 5,26 y 5,35 turnos contra un mínimo teórico de 4,52,
muestra que la decisión local queda muy cerca del óptimo a un costo mucho menor.

## 6. Ninguna decisión de la máquina es al azar

JugadorMaquina y Comodin no reciben ni usan Random. Las tres decisiones
que toma una máquina se derivan de un criterio explícito, y ese criterio se
imprime en la consola:

- Decisión: Qué pregunta hacer | Criterio: El menor peor caso; ante empate, primero un filtro de la consigna y después el orden declarado
- Decisión: Cuándo arriesgar | Criterio: Que 1/k alcance el umbral de la personalidad y que hayan pasado las preguntas mínimas
- Decisión: A quién apostar | Criterio: El candidato de menor ID, es decir el primero de la lista que dejó ordenada el MergeSort

Sobre la última hay algo que aclarar. Cuando la máquina decide apostar, todos los
candidatos que sobreviven tienen la misma probabilidad de ser el secreto, 1/k.
Ninguna elección acierta más seguido que otra, así que no existe un criterio
que mejore el resultado. Lo que sí se puede elegir es que la decisión sea
determinista y auditable, y por eso se toma el de menor ID: sortear daría el
mismo resultado estadístico pero no se podría justificar frente a la pregunta
"¿qué criterio usaste?".

La consecuencia es que dos partidas contra el mismo secreto producen
exactamente la misma traza, jugada por jugada. Hay una prueba automática que lo
verifica.

## Dónde sí se usa azar, y por qué

El único Random del proyecto está en Funcionalidad, y se usa para dos cosas
que no son decisiones de la máquina sino condiciones iniciales de la partida:

- barajar los 23 personajes en la Etapa 0 (por ejemplo, es lo que ocurre al
  volcar la caja de un juego de mesa y encontrar las piezas tiradas);
- elegir los secretos de cada partida, es decir qué personaje le toca a cada
  jugador.

La distinción es la que importa: el azar puede modelar el mundo, pero no
puede reemplazar un criterio. Si el orden inicial fuera fijo, el MergeSort no
tendría nada que ordenar y la Etapa 2 sería decorativa.

## 7. Preguntas: por qué son doce y no seis

Funcionalidad.crearPreguntas() crea un array fijo de doce preguntas, y
Pregunta.evaluar centraliza cómo se responde cada una. Seis son los filtros que
lista la consigna y seis son atributos adicionales declarados por el grupo:

- Filtros de la consigna: género, calvicie, lentes, pelo colorado / negro / amarillo | Atributos declarados adicionales: poderes, capa, máscara, arma, vuela, universo

## Demostración: los seis filtros de la consigna no alcanzan

La consigna pide en su primera regla 23 personajes con características
distinguibles, y más abajo lista seis filtros aplicables. Las dos cosas juntas
son imposibles, y se puede demostrar.

Los seis filtros no son seis atributos independientes. La calvicie y los tres
colores de pelo son cuatro estados mutuamente excluyentes de un mismo
atributo: un personaje calvo no tiene color de pelo, y uno con pelo tiene
exactamente uno de los tres colores. Quedan entonces tres dimensiones reales:

```
género (2) × lentes (2) × estado del pelo (3 colores + calvo = 4) = 16
```

Dieciséis combinaciones posibles para veintitrés personajes. Por el principio
del palomar, si hay más objetos que casilleros al menos un casillero recibe más
de un objeto: sin importar cómo se repartan los atributos, quedan como mínimo
siete personajes indistinguibles de otro.

Se verificó sobre el elenco real usando únicamente los seis filtros de la
consigna. Los 23 personajes colapsan en 8 clases de equivalencia, y en siete
de ellas hay más de un personaje:

- Personajes con la misma firma: Capitán América, Thor, Flash, Aquaman | Cuántos: 4
- Personajes con la misma firma: Pantera Negra, Avispa, Gamora, Mujer Maravilla | Cuántos: 4
- Personajes con la misma firma: Spider-Man, Hulk, Superman | Cuántos: 3
- Personajes con la misma firma: Iron Man, Doctor Strange, Batman | Cuántos: 3
- Personajes con la misma firma: Jean Grey, Viuda Negra, Chica Halcón | Cuántos: 3
- Personajes con la misma firma: Deadpool, Profesor X, Cyborg | Cuántos: 3
- Personajes con la misma firma: Supergirl, Canario Negro | Cuántos: 2

Con esos seis filtros el juego sería imposible de ganar por deducción: ninguna
combinación de preguntas separa a Thor de Aquaman.

Ampliar el conjunto de atributos no es una licencia que se tomó el grupo, es la
única forma de cumplir la primera regla. La expresión *"características
distinguibles a declarar"* es justamente lo que habilita a declarar atributos
además de los listados.

## Cuántos atributos adicionales hacen falta

Se recorrieron todas las combinaciones posibles de los seis atributos extra para
encontrar el conjunto mínimo que distingue a los 23 personajes:

- Atributos extra: 1 | ¿Alcanza?: No, ninguna de las 6 combinaciones
- Atributos extra: 2 | ¿Alcanza?: No, ninguna de las 15 combinaciones
- Atributos extra: 3 | ¿Alcanza?: Sí, 3 de las 20 combinaciones

Las tres que funcionan son máscara + arma + vuela, máscara + arma + universo y
máscara + vuela + universo. El mínimo es entonces tres.

El proyecto usa los seis. Los tres restantes se conservan porque aportan cortes
distintos que le dan más opciones al Greedy, sin agrandar el problema: el costo
por turno sigue siendo Θ(n).

## Balance de los filtros

El Greedy elige la pregunta que minimiza el peor grupo restante, así que un
filtro donde casi todos responden lo mismo es un filtro que casi nunca se va a
elegir. El elenco se diseñó para que los filtros de la consigna sean útiles:

- Filtro: género masculino *(consigna)* | Sí: 13 | No: 10 | Peor caso: 13
- Filtro: pelo negro *(consigna)* | Sí: 10 | No: 13 | Peor caso: 13
- Filtro: pelo amarillo *(consigna)* | Sí: 6 | No: 17 | Peor caso: 17
- Filtro: lentes *(consigna)* | Sí: 4 | No: 19 | Peor caso: 19
- Filtro: pelo colorado *(consigna)* | Sí: 4 | No: 19 | Peor caso: 19
- Filtro: calvicie *(consigna)* | Sí: 3 | No: 20 | Peor caso: 20
- Filtro: máscara | Sí: 11 | No: 12 | Peor caso: 12
- Filtro: arma | Sí: 13 | No: 10 | Peor caso: 13
- Filtro: vuela | Sí: 10 | No: 13 | Peor caso: 13
- Filtro: universo Marvel | Sí: 13 | No: 10 | Peor caso: 13
- Filtro: capa | Sí: 6 | No: 17 | Peor caso: 17
- Filtro: poderes | Sí: 20 | No: 3 | Peor caso: 20

## Por qué la calvicie sigue estando aunque esté desbalanceada

La calvicie queda en 3 contra 20 y se evaluó sacarla del juego. Se decidió
mantenerla por dos razones.

Primera: sin ella el juego se rompe. Al quitar la calvicie, Capitán América y
Deadpool quedan con firma idéntica en los once filtros restantes, y ninguna
combinación de los atributos disponibles los separa. Un filtro poco frecuente
puede ser irremplazable: no aporta en el caso promedio, pero es el único que
separa un par concreto.

Segunda: está entre los filtros que lista la consigna. El proyecto ya agrega
seis atributos que la consigna no menciona, y esa decisión está justificada con
la demostración de arriba. Quitar además uno de los que sí menciona sería
incoherente.

El desbalance tampoco es un defecto de diseño: en el juego de mesa original la
calvicie también es un rasgo minoritario. Es un filtro de baja frecuencia pero
alta información: casi siempre responde NO y descarta poco, pero cuando
responde SÍ deja apenas tres candidatos. El Greedy lo detecta solo y lo pospone
(es decir, lo deja para el final en lugar de elegirlo primero), que es
exactamente el comportamiento esperado del criterio de minimizar el peor caso.

## 8. Separación entre la consola y la lógica

El razonamiento de la máquina se ve en la consola, pero si se apaga esa salida
el juego funciona igual. Eso se resuelve por diseño y no con condicionales
repartidos por el código.

Ningún método del paquete clases escribe en System.out. Todos reciben un
PrintStream salida y un boolean mostrarProceso. La lógica no depende en
ningún punto de que se haya impreso algo: la salida es un observador del proceso
y no parte de él, es decir, imprimir o no imprimir no cambia ninguna decisión ni
ningún resultado.

La opción 5 del menú alterna el razonamiento en tiempo real y sirve para
demostrarlo: con la traza activada o silenciada, la partida avanza y termina
exactamente igual. Las pruebas automáticas se apoyan en lo mismo, ya que corren
cientos de partidas contra un PrintStream vacío.

La interfaz gráfica ya está incorporada con Java Swing. Consola y Swing usan
Partida para compartir el estado y las reglas de Humano contra Máquina, mientras
JugadorMaquina conserva sus decisiones y algoritmos. La ventana muestra el
registro de la partida; el razonamiento detallado de la máquina sigue saliendo
por el PrintStream recibido, que al jugar desde Swing es System.out.

## 9. Protección del secreto

En consola, el personaje del humano se elige mentalmente y no se guarda en una
variable. En Swing, el humano lo selecciona al iniciar y queda encapsulado en un
SecretoMaquina que responde automáticamente según sus atributos.

En ambos casos, JugadorMaquina sólo recibe la interfaz Respondedor, que permite
preguntar o confirmar una suposición, pero no leer el secreto. La diferencia es
que el Respondedor de consola solicita cada sí/no al usuario, mientras que el
de Swing calcula la respuesta sobre el personaje seleccionado.

En Máquina vs Máquina, SecretoMaquina encapsula el personaje (es decir, lo
guarda en un campo privado y sólo expone los métodos de Respondedor). El rival
nunca recibe una referencia directa al secreto, y la coordinación lo revela
únicamente al terminar la partida.

## 10. Modos de juego

## Humano vs Máquina en consola

El jugador elige primero contra cuál de las tres personalidades quiere jugar, y
después piensa su personaje sin escribirlo. La máquina elige el suyo al azar
entre los 23.

Los dos alternan turnos, y cada turno se gasta en una sola acción:

- El humano puede elegir una pregunta del listado, y el programa filtra
  automáticamente sus candidatos con la respuesta; o puede arriesgar un ID.
- La máquina evalúa primero si le conviene arriesgar según su umbral. Si no,
  elige la pregunta con Comodin y la muestra por consola para que el humano
  responda s o n.

Si queda un único candidato posible, la máquina lo dice directamente: ahí no hay
apuesta, ya tiene la respuesta.

## Humano vs Máquina en Swing

El usuario selecciona su personaje secreto y la personalidad del rival en la
ventana. La máquina elige al azar un secreto distinto del humano. Las preguntas
y adivinanzas se seleccionan mediante listas y botones; las respuestas del
humano se calculan automáticamente sobre su personaje.

El tablero muestra las imágenes y los datos de los personajes, y atenúa las
cartas descartadas. También se muestran la ronda, los candidatos restantes y el
registro de las acciones. Se puede iniciar una nueva partida o cancelar la
actual con confirmación.

Ambas interfaces usan Partida para Humano contra Máquina. En consola la entrada
se lee con Scanner; en Swing se obtiene de los componentes gráficos.

## Máquina vs Máquina

Se eligen dos secretos distintos y las dos máquinas juegan solas. En cada turno
se muestra:

**Comparación de Humano vs Máquina en consola y Swing**

Ambas interfaces comparten las reglas y la clase `Partida`, pero cambia la interacción del humano.

| Aspecto | Consola | Swing |
|---|---|---|
| Tu personaje | Lo elegís mentalmente; el programa no lo conoce. | Lo seleccionás en una lista; el programa lo conoce. |
| Respuestas a la máquina | Respondés manualmente con sí/no. | Se calculan automáticamente según tu personaje. |
| Preguntas y adivinanzas | Ingresás opciones por teclado. | Usás listas y botones. |
| Candidatos restantes | Se muestran como texto. | Se muestran con tarjetas, atenuando los descartados. |
| Secreto de la máquina | Puede coincidir con el que pensaste. | Se elige uno distinto del tuyo. |

Los turnos, los descartes y las condiciones para ganar los maneja la misma `Partida`. Las decisiones de la máquina también usan las mismas clases y dependen de la personalidad elegida.

En consola podés responder de forma inconsistente por error; en Swing las respuestas automáticas evitan eso.

- los candidatos que le quedan a la máquina que juega;
- la decisión de riesgo, con la probabilidad de acertar contra su umbral;
- la evaluación Greedy de las doce preguntas, con sí, no y peor caso, y una
  marca en las que son filtros de la consigna;
- la pregunta elegida, con el peor caso que la ganó y el motivo del desempate si
  lo hubo;
- la respuesta, los personajes descartados y los que quedan;
- la apuesta, cuando la hay, con la cantidad de candidatos y la probabilidad.

Los secretos se revelan al terminar. Este modo sigue coordinado por
Funcionalidad y disponible en consola; no utiliza Partida.

## Ver la inicialización

La opción 4 muestra la traza de las tres etapas guardada durante
prepararJuego(). No vuelve a barajar: exhibe el desorden real con el que
arrancó esta ejecución y cómo lo resolvió la máquina.

## Silenciar el razonamiento

La opción 5 activa o silencia la traza de la máquina, y el estado actual se ve en
el propio menú.

## 11. Clases

- Clase: Main | Responsabilidad: Punto de entrada
- Clase: Funcionalidad | Responsabilidad: Menú y entrada por consola, catálogo, inicialización, creación de Partida para Swing y coordinación de Máquina vs Máquina
- Clase: Partida | Responsabilidad: Estado y reglas de Humano vs Máquina compartidos entre consola y Swing
- Clase: VentanaPrincipal | Responsabilidad: Componentes Swing, eventos del usuario y presentación del estado de Partida
- Clase: Personaje | Responsabilidad: Modelo con los atributos del tablero
- Clase: ColorPelo | Responsabilidad: Colores posibles de pelo
- Clase: Pregunta | Responsabilidad: Texto de la pregunta y evaluación sobre un personaje
- Clase: Ordenador | Responsabilidad: MergeSort por ID (Divide y Conquista)
- Clase: Buscador | Responsabilidad: Inserción por búsqueda binaria y búsqueda por ID (Divide y Conquista)
- Clase: Comodin | Responsabilidad: Selección de la pregunta (Greedy)
- Clase: Personalidad | Responsabilidad: Umbral de riesgo para decidir cuándo arriesgar (Greedy)
- Clase: JugadorMaquina | Responsabilidad: Candidatos y turnos de una máquina
- Clase: Respondedor | Responsabilidad: Interfaz que limita el acceso al secreto
- Clase: SecretoMaquina | Responsabilidad: Secreto encapsulado de una máquina

## 12. Pruebas

test/Pruebas.java comprueba:

1. que existan 23 personajes, que la lista final sea autoincremental por ID y que
   la búsqueda Divide y Conquista encuentre cada ID;
2. que MergeSort deje la lista ordenada de 1 a 23 partiendo de 200 barajados
   distintos, o sea que el ordenamiento no dependa del desorden inicial;
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
.\probar.ps1 en Windows o ./probar.sh en Linux y macOS, y deben mostrar
OK - 9 pruebas superadas.

Además, test/PruebasPartida.java contiene ocho grupos de pruebas sobre:

1. victoria humana y bloqueo de acciones después de finalizar;
2. preguntas, filtrado, adivinanzas fallidas y validación de entradas;
3. victoria de la máquina con cada personalidad y secreto humano;
4. cancelación y bloqueo de acciones posteriores;
5. respuestas inconsistentes;
6. protección de las colecciones mediante copias;
7. secretos distintos y estado inicial de una nueva partida;
8. integración con el modo consola y regreso al menú.

Los scripts probar.ps1 y probar.sh compilan ambas clases de pruebas, pero
actualmente ejecutan únicamente Pruebas. Después de ejecutarlos, desde la carpeta
AdivinaLaCarta se puede correr la suite adicional con:

```shell
java -ea -cp out PruebasPartida
```

Su salida esperada es `OK - 8 pruebas de Partida superadas.` Estas pruebas
verifican la lógica sin abrir una ventana; no reemplazan la revisión visual de
Swing.

## 13. Estructura del proyecto

```
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
│   ├── funcionalidad/
│   │   ├── Funcionalidad.java     menú, catálogo y entrada por consola
│   │   └── Partida.java           estado y reglas de Humano vs Máquina
│   ├── interfaz/
│   │   ├── VentanaPrincipal.java  eventos y presentación Swing
│   │   └── VentanaPrincipal.form  diseño visual de la ventana
│   └── recursos/
│       └── personajes/            imágenes de personajes por ID
└── test/
    ├── Pruebas.java
    └── PruebasPartida.java
```

Los nombres siguen la convención de Java: PascalCase para clases y minúscula
para paquetes.

La carpeta out/ con los .class compilados está excluida por .gitignore:
son artefactos generados, no código fuente.


## 14. Incorporación de Java Swing y separación de responsabilidades

La incorporación de Swing permite jugar Humano contra Máquina desde una ventana,
conservando el modo consola. 

### VentanaPrincipal y el archivo .form

`interfaz/VentanaPrincipal.form` define el diseño de la interfaz mediante el
UI Designer de IntelliJ. `interfaz/VentanaPrincipal.java` configura su
comportamiento: registra los ActionListener, carga las listas e imágenes,
cambia entre las pantallas de menú, personajes y juego, y actualiza lo visible.

Los ActionListener reciben los clics y llaman a métodos de la ventana que leen
la selección del usuario y solicitan la acción correspondiente. Swing también
necesita código para redibujar las tarjetas, actualizar etiquetas, habilitar
botones y mostrar diálogos; el archivo .form no resuelve esas tareas ni las
reglas del juego.

Las imágenes se cargan desde `src/recursos/personajes/<id>.png`. Si falta una,
la ventana genera una imagen de reemplazo con el ID. La creación de la ventana
se realiza mediante `SwingUtilities.invokeLater`.

### Responsabilidad de Partida

`funcionalidad/Partida.java` representa una partida Humano contra Máquina.
Mantiene los candidatos del humano, las preguntas disponibles, el secreto de la
máquina, el Respondedor del humano, la ronda y el registro de acontecimientos.
Su estado puede ser EN_CURSO, GANO_HUMANO, GANO_MAQUINA o CANCELADA.

Sus operaciones principales son:

- `preguntar(Pregunta)`: valida que la pregunta esté disponible, obtiene la
  respuesta, descarta candidatos y ejecuta el turno de la máquina.
- `adivinar(int)`: busca el personaje por ID y comprueba el acierto. Si acierta,
  finaliza la partida; si falla, descarta ese candidato y juega la máquina.
- `cancelar()`: termina una partida activa.
- Los métodos de consulta permiten obtener la ronda, los candidatos, las
  preguntas disponibles y los mensajes para mostrarlos en la interfaz.

Partida coordina cuándo juega la máquina, pero la elección de preguntas y
apuestas sigue en JugadorMaquina y sus clases colaboradoras. No depende de
Swing ni lee datos con Scanner: recibe respuestas mediante Respondedor y una
salida PrintStream para las trazas.

Crear esta clase fue una decisión de organización, no un requisito de Swing.
Permite mantener las reglas compartidas en un solo lugar y probarlas sin abrir
la interfaz, en lugar de duplicarlas entre la ventana y la consola.

### Cómo se conectan las clases

En la implementación actual, VentanaPrincipal utiliza Funcionalidad para
preparar el catálogo y crear una partida con `crearPartida(...)`. Guarda la
Partida recibida y la utiliza directamente para preguntar, adivinar y cancelar.
Funcionalidad no actúa como intermediario en cada clic.

El flujo del botón Preguntar es:

1. El ActionListener llama a `jugarPreguntaHumano()` en la ventana.
2. La ventana obtiene la pregunta seleccionada.
3. Llama a `partida.preguntar(item.pregunta)`.
4. Partida actualiza los candidatos, coordina el turno de la máquina y determina
   si corresponde finalizar o avanzar la ronda.
5. La ventana llama a `actualizarPartida()` para mostrar el estado resultante.

El mismo método Partida.preguntar se utiliza desde la consola. Lo que cambia es
cómo se obtiene la selección y cómo se presentan los resultados.

