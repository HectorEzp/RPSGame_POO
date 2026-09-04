package valle.grande.edu.pe.rpsgames;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.Random;

public class HelloController {

    @FXML
    private VBox root;

    private int jugador = 0;
    private int pc = 0;
    private int rondaActual = 0;
    private final int MAX_RONDAS = 10;

    private int tiempoRestante = 10;
    private Timeline temporizador;

    private String dificultad = "Fácil";

    // Trofeo progresivo
    private boolean piezaBase = false;      // Se gana en Fácil
    private boolean piezaCuerpo = false;    // Se gana en Intermedio
    private boolean piezaCopa = false;      // Se gana en Difícil

    private Label marcador;
    private Label eleccionPC;
    private Label resultado;
    private Label labelTiempo;
    private Label labelRonda;

    private final Random random = new Random();

    @FXML
    public void initialize() {
        mostrarMenuPrincipal();
    }

    private void mostrarMenuPrincipal() {
        if (temporizador != null) temporizador.stop();
        root.getChildren().clear();

        Label titulo = new Label("🎮 CACHIPÚN GAMER 🏆");
        titulo.setStyle("-fx-font-size:26px; -fx-font-weight:bold; -fx-text-fill: #00f0ff;");

        Label subtitulo = new Label("Selecciona la dificultad:");
        subtitulo.setStyle("-fx-font-size:16px; -fx-text-fill: #ffffff;");

        Button btnFacil = crearBotonMenu("🟢 Fácil (Gana la Base 🧱)", "#28a745");
        Button btnIntermedio = crearBotonMenu("🟡 Intermedio (Gana el Cuerpo 🏛️)", "#ffc107");
        Button btnDificil = crearBotonMenu("🔴 Difícil (Gana la Copa 🏆)", "#dc3545");

        btnFacil.setOnAction(e -> iniciarJuego("Fácil"));
        btnIntermedio.setOnAction(e -> iniciarJuego("Intermedio"));
        btnDificil.setOnAction(e -> iniciarJuego("Difícil"));

        Label labelTrofeo = new Label("🏆 Estado de tu Trofeo Gamer:\n" + obtenerVisualTrofeo());
        labelTrofeo.setStyle("-fx-font-size:14px; -fx-text-fill: #00f0ff; -fx-alignment: center;");

        root.getChildren().addAll(titulo, subtitulo, btnFacil, btnIntermedio, btnDificil, labelTrofeo);
    }

    private Button crearBotonMenu(String texto, String color) {
        Button btn = new Button(texto);
        btn.setPrefWidth(260);
        btn.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-background-color: #1a1c23; " +
                "-fx-text-fill: " + color + "; -fx-border-color: " + color + "; -fx-border-radius: 8px;");
        return btn;
    }

    private void iniciarJuego(String dif) {
        this.dificultad = dif;
        jugador = 0;
        pc = 0;
        rondaActual = 1;

        construirInterfazJuego();
        iniciarTemporizador();
    }

    private void construirInterfazJuego() {
        root.getChildren().clear();

        Label titulo = new Label("🤖 MODO: " + dificultad.toUpperCase());
        titulo.setStyle("-fx-font-size:22px; -fx-font-weight:bold; -fx-text-fill: #00f0ff;");

        labelRonda = new Label("Ronda: " + rondaActual + " / " + MAX_RONDAS);
        labelRonda.setStyle("-fx-font-size:16px; -fx-text-fill: #ffffff;");

        labelTiempo = new Label("⏳ Tiempo: 10s");
        labelTiempo.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill: #ff0055;");

        Label texto = new Label("Elige tu ataque:");

        Button piedra = new Button("✊ Piedra");
        Button papel = new Button("✋ Papel");
        Button tijera = new Button("✌ Tijera");

        piedra.setPrefWidth(220);
        papel.setPrefWidth(220);
        tijera.setPrefWidth(220);

        piedra.setOnAction(e -> procesarEleccion("Piedra"));
        papel.setOnAction(e -> procesarEleccion("Papel"));
        tijera.setOnAction(e -> procesarEleccion("Tijera"));

        eleccionPC = new Label("PC: ?");
        resultado = new Label("");
        marcador = new Label("Jugador 0  |  PC 0");

        marcador.setStyle("-fx-font-size:18px; -fx-font-weight:bold;");
        resultado.setStyle("-fx-font-size:18px; -fx-font-weight:bold;");

        Button btnReiniciar = new Button("🔄 Reiniciar Ronda");
        btnReiniciar.setOnAction(e -> iniciarJuego(dificultad));

        Button btnVolver = new Button("🏠 Volver al Inicio");
        btnVolver.setOnAction(e -> mostrarMenuPrincipal());

        root.getChildren().addAll(
                titulo, labelRonda, labelTiempo, texto,
                piedra, papel, tijera, eleccionPC, resultado, marcador,
                btnReiniciar, btnVolver
        );
    }

    private void iniciarTemporizador() {
        if (temporizador != null) temporizador.stop();

        tiempoRestante = 10;
        labelTiempo.setText("⏳ Tiempo: " + tiempoRestante + "s");

        temporizador = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            tiempoRestante--;
            labelTiempo.setText("⏳ Tiempo: " + tiempoRestante + "s");

            if (tiempoRestante <= 0) {
                pc++;
                resultado.setText("⏰ ¡Tiempo agotado! Punto para PC");
                eleccionPC.setText("🤖 La PC se aprovecha");
                avanzarRonda();
            }
        }));

        temporizador.setCycleCount(Timeline.INDEFINITE);
        temporizador.play();
    }

    private void procesarEleccion(String opcionJugador) {
        temporizador.stop();

        String opcionPC = calcularEleccionPC(opcionJugador);
        eleccionPC.setText("🤖 PC eligió: " + opcionPC);

        if (opcionJugador.equals(opcionPC)) {
            resultado.setText("🤝 EMPATE");
        } else if (
                (opcionJugador.equals("Piedra") && opcionPC.equals("Tijera")) ||
                        (opcionJugador.equals("Papel") && opcionPC.equals("Piedra")) ||
                        (opcionJugador.equals("Tijera") && opcionPC.equals("Papel"))
        ) {
            jugador++;
            resultado.setText("🎉 ¡GANASTE LA RONDA!");
        } else {
            pc++;
            resultado.setText("💻 PUNTOS PARA PC");
        }

        avanzarRonda();
    }

    private String calcularEleccionPC(String opcionJugador) {
        String[] opciones = {"Piedra", "Papel", "Tijera"};

        if (dificultad.equals("Fácil")) {
            return opciones[random.nextInt(3)];
        } else if (dificultad.equals("Intermedio")) {
            // 50% de probabilidad de elegir la ganadora, 50% aleatorio
            if (random.nextBoolean()) {
                return obtenerOpcionGanadora(opcionJugador);
            }
            return opciones[random.nextInt(3)];
        } else {
            // Difícil: 80% de probabilidad de elegir la ganadora
            if (random.nextInt(100) < 80) {
                return obtenerOpcionGanadora(opcionJugador);
            }
            return opciones[random.nextInt(3)];
        }
    }

    private String obtenerOpcionGanadora(String jugada) {
        switch (jugada) {
            case "Piedra": return "Papel";
            case "Papel": return "Tijera";
            default: return "Piedra";
        }
    }

    private void avanzarRonda() {
        marcador.setText("Jugador " + jugador + "  |  PC " + pc);

        if (rondaActual >= MAX_RONDAS) {
            finalizarJuego();
        } else {
            rondaActual++;
            labelRonda.setText("Ronda: " + rondaActual + " / " + MAX_RONDAS);
            iniciarTemporizador();
        }
    }

    private void finalizarJuego() {
        if (temporizador != null) temporizador.stop();
        root.getChildren().clear();

        Label tituloFinal = new Label("🏆 FIN DE LA PARTIDA 🏆");
        tituloFinal.setStyle("-fx-font-size:26px; -fx-font-weight:bold; -fx-text-fill: #00f0ff;");

        Label marcadorFinal = new Label("Marcador Final:\nJugador: " + jugador + " | PC: " + pc);
        marcadorFinal.setStyle("-fx-font-size:20px; -fx-font-weight:bold;");

        String mensajeResultado;
        if (jugador > pc) {
            mensajeResultado = "🥇 ¡DERROTASTE A LA PC EN MODO " + dificultad.toUpperCase() + "!";
            desbloquearPiezaTrofeo();
        } else if (pc > jugador) {
            mensajeResultado = "💀 LA PC GANÓ ESTA VEZ. ¡INTÉNTALO DE NUEVO!";
        } else {
            mensajeResultado = "⚖️ ¡EMPATE TÉCNICO!";
        }

        Label labelMensaje = new Label(mensajeResultado);
        labelMensaje.setStyle("-fx-font-size:16px; -fx-font-weight:bold; -fx-text-fill: #ff0055;");

        Label labelTrofeo = new Label("Construcción del Trofeo:\n" + obtenerVisualTrofeo());
        labelTrofeo.setStyle("-fx-font-size:14px; -fx-text-fill: #00f0ff; -fx-alignment: center;");

        Button btnReintentar = new Button("🔄 Jugar otra vez (" + dificultad + ")");
        btnReintentar.setStyle("-fx-font-size:14px;");
        btnReintentar.setOnAction(e -> iniciarJuego(dificultad));

        Button btnInicio = new Button("🏠 Volver al Menú Principal");
        btnInicio.setStyle("-fx-font-size:14px; -fx-background-color: #00f0ff; -fx-text-fill: #000;");
        btnInicio.setOnAction(e -> mostrarMenuPrincipal());

        root.getChildren().addAll(tituloFinal, marcadorFinal, labelMensaje, labelTrofeo, btnReintentar, btnInicio);
    }

    private void desbloquearPiezaTrofeo() {
        if (dificultad.equals("Fácil")) piezaBase = true;
        if (dificultad.equals("Intermedio")) piezaCuerpo = true;
        if (dificultad.equals("Difícil")) piezaCopa = true;
    }

    private String obtenerVisualTrofeo() {
        String copa = piezaCopa ? "     👑 [ COPA DE ORO ]" : "     ❌ [ Copa Bloqueada ]";
        String cuerpo = piezaCuerpo ? "     🏛️ [ CUERPO DEL TROFEO ]" : "     ❌ [ Cuerpo Bloqueado ]";
        String base = piezaBase ? "     🧱 [ BASE DEL TROFEO ]" : "     ❌ [ Base Bloqueada ]";

        return copa + "\n" + cuerpo + "\n" + base;
    }
}