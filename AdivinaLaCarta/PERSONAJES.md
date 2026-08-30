# Personajes y características utilizadas

Los IDs conservan exactamente el orden entregado. El `ArrayList` los muestra
agrupados por género, pero no modifica esos IDs.

| ID | Personaje | Género | Poderes | Capa | Máscara | Arma | Vuela | Lentes | Calvicie | Pelo | Universo |
|---:|---|---|---|---|---|---|---|---|---|---|---|
| 1 | Spider-Man (Peter Parker) | M | Sí | No | Sí | No | No | No | No | Negro | Marvel |
| 2 | Iron Man (Tony Stark) | M | Sí | No | Sí | Sí | Sí | No | No | Negro | Marvel |
| 3 | Capitán América (Steve Rogers) | M | Sí | No | Sí | Sí | No | No | No | Amarillo | Marvel |
| 4 | Thor | M | Sí | Sí | No | Sí | Sí | No | No | Amarillo | Marvel |
| 5 | Hulk (Bruce Banner) | M | Sí | No | No | No | No | No | No | Negro | Marvel |
| 6 | Wolverine (Logan) | M | Sí | No | No | Sí | No | No | No | Negro | Marvel |
| 7 | Viuda Negra (Natasha Romanoff) | F | No | No | No | Sí | No | No | No | Colorado | Marvel |
| 8 | Doctor Strange | M | Sí | Sí | No | No | Sí | No | No | Negro | Marvel |
| 9 | Pantera Negra (T'Challa) | M | Sí | No | Sí | Sí | No | No | No | Negro | Marvel |
| 10 | Deadpool (Wade Wilson) | M | Sí | No | Sí | Sí | No | No | Sí | — | Marvel |
| 11 | Ant-Man (Scott Lang) | M | Sí | No | Sí | No | Sí | No | No | Negro | Marvel |
| 12 | Groot | M | Sí | No | No | No | No | No | Sí | — | Marvel |
| 13 | Star-Lord (Peter Quill) | M | No | No | Sí | Sí | Sí | No | No | Amarillo | Marvel |
| 14 | Superman (Clark Kent) | M | Sí | Sí | No | No | Sí | Sí | No | Negro | DC |
| 15 | Batman (Bruce Wayne) | M | No | Sí | Sí | Sí | No | No | No | Negro | DC |
| 16 | Mujer Maravilla (Diana Prince) | F | Sí | No | No | Sí | Sí | No | No | Negro | DC |
| 17 | Flash (Barry Allen) | M | Sí | No | Sí | No | No | No | No | Amarillo | DC |
| 18 | Aquaman (Arthur Curry) | M | Sí | No | No | Sí | No | No | No | Amarillo | DC |
| 19 | Linterna Verde (Hal Jordan) | M | Sí | No | Sí | No | Sí | No | No | Negro | DC |
| 20 | Shazam (Billy Batson) | M | Sí | Sí | No | No | Sí | No | No | Negro | DC |
| 21 | Cyborg (Victor Stone) | M | Sí | No | No | Sí | Sí | No | Sí | — | DC |
| 22 | Green Arrow (Oliver Queen) | M | No | No | Sí | Sí | No | No | No | Amarillo | DC |
| 23 | Atom (Ray Palmer) | M | Sí | No | Sí | No | No | No | No | Negro | DC |

Las decisiones potencialmente discutibles se fijaron de esta manera:

- la tecnología cuenta como poderes para Iron Man, Ant-Man, Cyborg y Atom;
- Ant-Man cuenta como capaz de volar mediante hormigas voladoras;
- los lentes de Clark Kent cuentan para Superman;
- los visores de máscaras y cascos no cuentan como lentes;
- el color de pelo no se evalúa cuando el personaje tiene calvicie.

Los datos están concentrados en `funcionalidad.cargarPersonajes()`, por lo que
pueden modificarse sin cambiar Divide y Conquista, Greedy ni los turnos.
