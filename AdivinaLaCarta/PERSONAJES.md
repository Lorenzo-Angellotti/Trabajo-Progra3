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
| 12 | Groot | M | Sí | No | No | No | No | No | Sí | — | Marvel |
| 13 | Gamora | F | Sí | No | No | Sí | No | No | No | Negro | Marvel |
| 14 | Superman (Clark Kent) | M | Sí | Sí | No | No | Sí | Sí | No | Negro | DC |
| 15 | Batman (Bruce Wayne) | M | No | Sí | Sí | Sí | No | No | No | Negro | DC |
| 16 | Mujer Maravilla (Diana Prince) | F | Sí | No | No | Sí | Sí | Sí | No | Negro | DC |
| 17 | Flash (Barry Allen) | M | Sí | No | Sí | No | No | No | No | Amarillo | DC |
| 18 | Aquaman (Arthur Curry) | M | Sí | No | No | Sí | No | No | No | Amarillo | DC |
| 19 | Chica Halcón (Shayera Hol) | F | Sí | No | Sí | Sí | Sí | No | No | Colorado | DC |
| 20 | Supergirl (Kara Zor-El) | F | Sí | Sí | No | No | Sí | No | No | Amarillo | DC |
| 21 | Cyborg (Victor Stone) | M | Sí | No | No | Sí | Sí | No | Sí | — | DC |
| 22 | Canario Negro (Dinah Lance) | F | Sí | No | Sí | No | No | No | No | Amarillo | DC |
| 23 | Batgirl (Barbara Gordon) | F | No | No | Sí | Sí | No | Sí | No | Colorado | DC |

Quedan **10 personajes femeninos y 13 masculinos**. El equilibrio es
intencional: con un elenco muy desbalanceado el filtro de género nunca sería
elegido por el Greedy, porque descartaría casi nada. Ver la sección 6 de
[`DOCUMENTACION.md`](DOCUMENTACION.md).

Las decisiones potencialmente discutibles se fijaron de esta manera:

- la tecnología cuenta como poderes para Iron Man, Avispa, Shuri y Cyborg;
- Batgirl y Batman no tienen poderes: dependen de entrenamiento y equipamiento;
- Jean Grey, Mujer Maravilla y Avispa cuentan como capaces de volar;
- **los lentes se evalúan por la identidad civil del personaje**, de forma
  consistente: Clark Kent, Bruce Banner, Peter Parker, Diana Prince y Barbara
  Gordon usan lentes fuera del traje;
- los visores de máscaras y cascos no cuentan como lentes;
- el color de pelo no se evalúa cuando el personaje tiene calvicie: en ese caso
  las tres preguntas de color responden NO y el color se muestra como
  "No aplicable";
- Groot y Cyborg no tienen pelo por su naturaleza (madera y placas metálicas),
  igual que Deadpool por sus cicatrices.

Los datos están concentrados en `funcionalidad.cargarPersonajes()`, por lo que
pueden modificarse sin cambiar Divide y Conquista, Greedy ni los turnos.
