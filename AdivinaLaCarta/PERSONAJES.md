# Personajes y características utilizadas

Los IDs van del 1 al 23: Marvel del 1 al 13 y DC del 14 al 23. La tabla está
escrita por ID para poder leerla, pero **en ejecución los personajes no arrancan
en este orden**: se barajan y es la máquina la que los agrupa por género y
después los ordena por ID con MergeSort. Ver la sección 3 de
[`DOCUMENTACION.md`](DOCUMENTACION.md).

| ID | Personaje | Género | Poderes | Capa | Máscara | Arma | Vuela | Lentes | Calvicie | Pelo | Universo |
|---:|---|---|---|---|---|---|---|---|---|---|---|
| 1 | Spider-Man (Peter Parker) | M | Sí | No | Sí | No | No | Sí | No | Negro | Marvel |
| 2 | Iron Man (Tony Stark) | M | Sí | No | Sí | Sí | Sí | No | No | Negro | Marvel |
| 3 | Capitán América (Steve Rogers) | M | Sí | No | Sí | Sí | No | No | No | Amarillo | Marvel |
| 4 | Thor | M | Sí | Sí | No | Sí | Sí | No | No | Amarillo | Marvel |
| 5 | Hulk (Bruce Banner) | M | Sí | No | No | No | No | Sí | No | Negro | Marvel |
| 6 | Jean Grey | F | Sí | No | No | No | Sí | No | No | Colorado | Marvel |
| 7 | Viuda Negra (Natasha Romanoff) | F | No | No | No | Sí | No | No | No | Colorado | Marvel |
| 8 | Doctor Strange | M | Sí | Sí | No | No | Sí | No | No | Negro | Marvel |
| 9 | Shuri (Pantera Negra) | F | Sí | No | Sí | Sí | No | No | No | Negro | Marvel |
| 10 | Deadpool (Wade Wilson) | M | Sí | No | Sí | Sí | No | No | Sí | — | Marvel |
| 11 | Avispa (Janet van Dyne) | F | Sí | No | Sí | No | Sí | No | No | Negro | Marvel |
| 12 | Profesor X (Charles Xavier) | M | Sí | No | No | No | No | No | Sí | — | Marvel |
| 13 | Gamora | F | Sí | No | No | Sí | No | No | No | Negro | Marvel |
| 14 | Superman (Clark Kent) | M | Sí | Sí | No | No | Sí | Sí | No | Negro | DC |
| 15 | Batman (Bruce Wayne) | M | No | Sí | Sí | Sí | No | No | No | Negro | DC |
| 16 | Mujer Maravilla (Diana Prince) | F | Sí | No | No | Sí | Sí | No | No | Negro | DC |
| 17 | Flash (Barry Allen) | M | Sí | No | Sí | No | No | No | No | Amarillo | DC |
| 18 | Aquaman (Arthur Curry) | M | Sí | No | No | Sí | No | No | No | Amarillo | DC |
| 19 | Chica Halcón (Shayera Hol) | F | Sí | No | Sí | Sí | Sí | No | No | Colorado | DC |
| 20 | Supergirl (Kara Zor-El) | F | Sí | Sí | No | No | Sí | No | No | Amarillo | DC |
| 21 | Cyborg (Victor Stone) | M | Sí | No | No | Sí | Sí | No | Sí | — | DC |
| 22 | Canario Negro (Dinah Lance) | F | Sí | No | Sí | No | No | No | No | Amarillo | DC |
| 23 | Batgirl (Barbara Gordon) | F | No | Sí | Sí | Sí | No | Sí | No | Colorado | DC |

Quedan **10 personajes femeninos y 13 masculinos**. El equilibrio es
intencional: con un elenco muy desbalanceado el filtro de género nunca sería
elegido por el Greedy, porque descartaría casi nada. Ver la sección 6 de
[`DOCUMENTACION.md`](DOCUMENTACION.md).

## Criterios de asignación

Las decisiones potencialmente discutibles se fijaron de esta manera:

- **Los lentes se evalúan sobre la identidad civil del personaje, no sobre el
  traje.** Clark Kent usa lentes aunque Superman no. El criterio está enunciado
  en el texto mismo de la pregunta ("¿Usa lentes en su identidad civil?") para
  que el jugador humano no tenga que adivinarlo. Aplica a Peter Parker, Bruce
  Banner, Clark Kent y Barbara Gordon.
- Los visores de máscaras y cascos no cuentan como lentes.
- La tecnología cuenta como poderes para Iron Man, Avispa, Shuri y Cyborg.
- Batman y Batgirl no tienen poderes: dependen de entrenamiento y equipamiento.
  Batgirl sí usa capa, igual que el resto de la familia Bat.
- Jean Grey, Mujer Maravilla y Avispa cuentan como capaces de volar.
- El color de pelo no se evalúa cuando el personaje tiene calvicie: en ese caso
  las tres preguntas de color responden NO y el color se muestra como
  "No aplicable".
- Los tres personajes con calvicie son casos canónicos y no forzados: el
  Profesor X es calvo por definición del personaje, Deadpool por sus cicatrices
  y Cyborg por sus placas metálicas.

## Cambios respecto de la lista inicial

- **Groot fue reemplazado por el Profesor X.** Groot es un árbol: no sólo el
  color de pelo no le aplica, tampoco le aplica la calvicie. Estaba forzando dos
  atributos a la vez. El Profesor X es el ejemplo canónico de calvicie, es
  Marvel y es masculino, así que no altera el balance de universos ni de género.
- **Wolverine, Ant-Man, Star-Lord, Pantera Negra, Linterna Verde, Shazam, Green
  Arrow y Atom** fueron reemplazados por Jean Grey, Avispa, Gamora, Shuri, Chica
  Halcón, Supergirl, Canario Negro y Batgirl, para equilibrar el filtro de
  género. Shuri sucede a T'Challa como Pantera Negra, por lo que el casillero
  conserva su identidad y sus atributos.

Los datos están concentrados en `Funcionalidad.cargarPersonajes()`, por lo que
pueden modificarse sin cambiar Divide y Conquista, Greedy ni los turnos.
