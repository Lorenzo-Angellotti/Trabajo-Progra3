package interfaz;

import clases.Personaje;
import clases.Personalidad;
import clases.Pregunta;
import funcionalidad.Funcionalidad;
import funcionalidad.Partida;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;

public class VentanaPrincipal {
    private static final String PANTALLA_MENU = "pantallaMenu";
    private static final String PANTALLA_PERSONAJES = "pantallaPersonajes";
    private static final String PANTALLA_JUEGO = "pantallaJuego";
    private static final int ANCHO_IMAGEN = 110;
    private static final int ALTO_IMAGEN = 110;

    private JPanel panelRaiz;
    private JPanel panelPrincipal;
    private JPanel panelTarjetas;
    private JTextArea areaRegistro;
    private JTextArea listadoPersonajes;
    private JLabel labelEstado;
    private JLabel labelSecretoHumano;
    private JComboBox<Personalidad> comboRival;
    private JComboBox<PersonajeItem> comboSecretoHumano;
    private JComboBox<PreguntaItem> comboPreguntas;
    private JComboBox<PersonajeItem> comboAdivinar;
    private JButton botonEmpezar;
    private JButton botonVerPersonajes;
    private JButton botonVolverMenu;
    private JButton botonNuevaPartida;
    private JButton botonCancelarPartida;
    private JButton botonPreguntar;
    private JButton botonAdivinar;

    private Funcionalidad funcionalidad;
    private ArrayList<Personaje> personajes;
    private Partida partida;

    public VentanaPrincipal() {
        configurarComponentesDinamicos();
        configurarModelosEstaticos();
        prepararDatosIniciales();
        configurarEventos();
    }

    public static void abrir() {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            JFrame ventana = new JFrame("Adivina La Carta");
            ventana.setContentPane(new VentanaPrincipal().getPanelRaiz());
            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ventana.pack();
            ventana.setLocationRelativeTo(null);
            ventana.setVisible(true);
        });
    }

    public JPanel getPanelRaiz() {
        return panelRaiz;
    }

    private void configurarEventos() {
        botonEmpezar.addActionListener(evento -> iniciarPartida());
        botonVerPersonajes.addActionListener(evento -> {
            actualizarListadoPersonajes();
            mostrarPantalla(PANTALLA_PERSONAJES);
        });
        botonVolverMenu.addActionListener(evento -> mostrarPantalla(PANTALLA_MENU));
        botonNuevaPartida.addActionListener(evento -> iniciarPartida());
        botonCancelarPartida.addActionListener(evento -> cancelarPartida());
        botonPreguntar.addActionListener(evento -> jugarPreguntaHumano());
        botonAdivinar.addActionListener(evento -> jugarAdivinanzaHumano());
    }

    private void configurarComponentesDinamicos() {
        panelRaiz.setPreferredSize(new Dimension(1000, 650));
        panelTarjetas.setLayout(new GridLayout(0, 3, 8, 8));
        areaRegistro.setEditable(false);
        areaRegistro.setLineWrap(true);
        areaRegistro.setWrapStyleWord(true);
    }

    private void configurarModelosEstaticos() {
        comboRival.setModel(new DefaultComboBoxModel<>(Personalidad.values()));
    }

    private void prepararDatosIniciales() {
        funcionalidad = new Funcionalidad();
        funcionalidad.prepararJuego();
        personajes = funcionalidad.getPersonajes();
        cargarCombosBase();
        actualizarListadoPersonajes();
    }

    private void iniciarPartida() {
        PersonajeItem seleccionado = (PersonajeItem) comboSecretoHumano.getSelectedItem();
        if (seleccionado == null) {
            return;
        }
        partida = funcionalidad.crearPartida(seleccionado.personaje.getId(),
                (Personalidad) comboRival.getSelectedItem());
        labelSecretoHumano.setText("Tu personaje: " + seleccionado.personaje.getNombre());
        actualizarPartida();
        mostrarPantalla(PANTALLA_JUEGO);
    }

    private void jugarPreguntaHumano() {
        PreguntaItem item = (PreguntaItem) comboPreguntas.getSelectedItem();
        if (item == null) {
            JOptionPane.showMessageDialog(panelRaiz,
                    "Ya no quedan preguntas. Tenes que adivinar.");
            return;
        }
        partida.preguntar(item.pregunta);
        actualizarPartida();
    }

    private void jugarAdivinanzaHumano() {
        PersonajeItem item = (PersonajeItem) comboAdivinar.getSelectedItem();
        if (item != null) {
            partida.adivinar(item.personaje.getId());
            actualizarPartida();
        }
    }

    private void actualizarPartida() {
        DefaultComboBoxModel<PreguntaItem> modelo = new DefaultComboBoxModel<>();
        for (Pregunta pregunta : partida.getPreguntasDisponibles()) {
            modelo.addElement(new PreguntaItem(pregunta));
        }
        comboPreguntas.setModel(modelo);
        areaRegistro.setText(String.join(System.lineSeparator() + System.lineSeparator(),
                partida.getRegistro()));
        areaRegistro.setCaretPosition(areaRegistro.getDocument().getLength());
        botonPreguntar.setEnabled(partida.isActiva());
        botonAdivinar.setEnabled(partida.isActiva());
        redibujarTarjetas();
        actualizarEstado();
        if (!partida.isActiva()) {
            JOptionPane.showMessageDialog(panelRaiz, partida.getMensajeFinal());
        }
    }

    private void cancelarPartida() {
        if (partida != null && partida.isActiva()) {
            int respuesta = JOptionPane.showConfirmDialog(panelRaiz,
                    "Cancelar la partida actual?", "Cancelar partida",
                    JOptionPane.YES_NO_OPTION);
            if (respuesta != JOptionPane.YES_OPTION) {
                return;
            }
            partida.cancelar();
        }
        mostrarPantalla(PANTALLA_MENU);
    }

    private void cargarCombosBase() {
        DefaultComboBoxModel<PersonajeItem> modeloSecretos = new DefaultComboBoxModel<>();
        DefaultComboBoxModel<PersonajeItem> modeloAdivinar = new DefaultComboBoxModel<>();
        for (Personaje personaje : personajes) {
            PersonajeItem item = new PersonajeItem(personaje);
            modeloSecretos.addElement(item);
            modeloAdivinar.addElement(new PersonajeItem(personaje));
        }
        comboSecretoHumano.setModel(modeloSecretos);
        comboAdivinar.setModel(modeloAdivinar);

    }

    private void redibujarTarjetas() {
        panelTarjetas.removeAll();
        for (Personaje personaje : personajes) {
            JPanel tarjeta = new JPanel(new BorderLayout(4, 4));
            tarjeta.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(190, 196, 206)),
                    BorderFactory.createEmptyBorder(8, 8, 8, 8)));
            boolean candidato = partida == null || partida.esCandidatoHumano(personaje);
            tarjeta.setBackground(candidato ? Color.WHITE : new Color(226, 229, 234));

            JLabel id = new JLabel("#" + personaje.getId());
            id.setFont(id.getFont().deriveFont(Font.BOLD));
            JLabel imagen = new JLabel(cargarImagenPersonaje(personaje), SwingConstants.CENTER);
            imagen.setPreferredSize(new Dimension(ANCHO_IMAGEN, ALTO_IMAGEN));
            imagen.setEnabled(candidato);
            JLabel nombre = new JLabel("<html><b>" + personaje.getNombre() + "</b></html>");
            JLabel datos = new JLabel("<html>" + personaje.getGenero()
                    + " | " + personaje.getUniverso()
                    + "<br>Pelo: " + personaje.getColorPeloVisible()
                    + "</html>");

            tarjeta.add(id, BorderLayout.NORTH);
            tarjeta.add(imagen, BorderLayout.CENTER);
            JPanel texto = new JPanel(new GridLayout(0, 1, 2, 2));
            texto.setOpaque(false);
            texto.add(nombre);
            texto.add(datos);
            tarjeta.add(texto, BorderLayout.SOUTH);
            panelTarjetas.add(tarjeta);
        }

        panelTarjetas.revalidate();
        panelTarjetas.repaint();
    }

    private ImageIcon cargarImagenPersonaje(Personaje personaje) {
        URL recurso = getClass().getResource(
                "/recursos/personajes/" + personaje.getId() + ".png");

        if (recurso != null) {
            ImageIcon icono = new ImageIcon(recurso);
            Image imagen = icono.getImage().getScaledInstance(
                    ANCHO_IMAGEN, ALTO_IMAGEN, Image.SCALE_SMOOTH);
            return new ImageIcon(imagen);
        }

        return crearImagenFaltante(personaje);
    }

    private ImageIcon crearImagenFaltante(Personaje personaje) {
        BufferedImage imagen = new BufferedImage(
                ANCHO_IMAGEN, ALTO_IMAGEN, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = imagen.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(235, 238, 242));
        g.fillRoundRect(0, 0, ANCHO_IMAGEN - 1, ALTO_IMAGEN - 1, 12, 12);
        g.setColor(new Color(160, 167, 178));
        g.drawRoundRect(0, 0, ANCHO_IMAGEN - 1, ALTO_IMAGEN - 1, 12, 12);
        g.setFont(g.getFont().deriveFont(Font.BOLD, 28f));
        String texto = "#" + personaje.getId();
        int x = (ANCHO_IMAGEN - g.getFontMetrics().stringWidth(texto)) / 2;
        int y = (ALTO_IMAGEN + g.getFontMetrics().getAscent()) / 2 - 8;
        g.drawString(texto, x, y);
        g.dispose();
        return new ImageIcon(imagen);
    }

    private void actualizarListadoPersonajes() {
        StringBuilder texto = new StringBuilder();
        for (Personaje personaje : personajes) {
            texto.append(personaje.mostrarDetalle()).append(System.lineSeparator());
        }
        listadoPersonajes.setText(texto.toString());
        listadoPersonajes.setCaretPosition(0);
    }

    private void actualizarEstado() {
        if (!partida.isActiva()) {
            labelEstado.setText("Partida finalizada");
            return;
        }
        labelEstado.setText("Ronda " + partida.getRonda() + " | Tus candidatos: "
                + partida.getCandidatosHumano().size() + " | Candidatos maquina: "
                + partida.getCantidadCandidatosMaquina());
    }

    private void mostrarPantalla(String nombrePantalla) {
        CardLayout cardLayout = (CardLayout) panelPrincipal.getLayout();
        cardLayout.show(panelPrincipal, nombrePantalla);
    }

    private static class PreguntaItem {
        private final Pregunta pregunta;

        PreguntaItem(Pregunta pregunta) {
            this.pregunta = pregunta;
        }

        @Override
        public String toString() {
            return pregunta.getTexto();
        }
    }

    private static class PersonajeItem {
        private final Personaje personaje;

        PersonajeItem(Personaje personaje) {
            this.personaje = personaje;
        }

        @Override
        public String toString() {
            return personaje.getId() + " - " + personaje.getNombre();
        }
    }
}
