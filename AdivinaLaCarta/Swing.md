**`VentanaPrincipal` — Interfaz gráfica**

La clase  ventana principal, ubicada en el paquete Interfaz, administra la interfaz gráfica desarrollada con Swing. Trabaja junto con `VentanaPrincipal.form`, donde se define el diseño de la ventana.

Sus responsabilidades son:

- Configurar los componentes y manejar la navegación entre pantallas.
- Mostrar personajes, imágenes, preguntas y opciones de juego.
- Detectar los clics y leer las selecciones del usuario.
- Solicitar a `Partida` que ejecute las acciones del juego.
- Actualizar las tarjetas, los mensajes y los indicadores de la partida.
- Mostrar diálogos de confirmación y resultados.

Esta clase no aplica las reglas del juego: presenta la información y conecta las acciones del usuario con la funcionalidad correspondiente.

Partida — Estado y coordinación del juego**

La clase partida, ubicada en el paquete funcionalidad, representa una partida de humano contra máquina. Mantiene su estado y coordina su desarrollo, sin depender de componentes de Swing.

Sus responsabilidades son:

- Mantener los candidatos del humano y las preguntas disponibles.
- Gestionar el secreto de la máquina y las respuestas del humano mediante Respnondedor
- Procesar preguntas y descartar los personajes que no coinciden con la respuesta.
- Comprobar las adivinanzas del humano.
- Dar paso al turno de JugadorMaquina, que conserva su lógica de decisión.
- Controlar las rondas, las victorias y la cancelación.
- Registrar los acontecimientos de la partida para que puedan mostrarse.

La misma clase se utiliza desde Swing y desde el modo humano contra máquina de consola, evitando duplicar las reglas.



Como trabajan juntas

Cuando el usuario presiona **Preguntar**, `VentanaPrincipal` obtiene la pregunta seleccionada y llama a `partida.preguntar(...)`. `Partida` procesa la respuesta, actualiza los candidatos y ejecuta el turno de la máquina. Luego, la ventana consulta el estado actualizado y lo muestra en pantalla.

Esta separación permite modificar la presentación sin alterar las reglas, y probar el funcionamiento de una partida sin abrir una ventana.