package scrabble.presentation.popups;
import java.util.HashMap;
import java.util.Map;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Clase para mostrar diálogos de alerta modernos y personalizados
 */
public class customDialogo {

    /**
     * Tipos de alerta soportados
     */
    public enum AlertType {
        INFO("#2196F3", "INFORMACIÓN", "info-circle"),
        WARNING("#FF9800", "ADVERTENCIA", "exclamation-triangle"),
        ERROR("#F44336", "ERROR", "times-circle"),
        SUCCESS("#4CAF50", "ÉXITO", "check-circle");
        
        private final String color;
        private final String titulo;
        private final String iconName;
        
        AlertType(String color, String titulo, String iconName) {
            this.color = color;
            this.titulo = titulo;
            this.iconName = iconName;
        }
        
        public String getColor() {
            return color;
        }
        
        public String getTitulo() {
            return titulo;
        }
        
        public String getIconName() {
            return iconName;
        }
    }

    private static final Map<AlertType, String> ICON_MAP = new HashMap<>();
    static {
        ICON_MAP.put(AlertType.INFO, "ℹ️");
        ICON_MAP.put(AlertType.WARNING, "⚠️");
        ICON_MAP.put(AlertType.ERROR, "❌");
        ICON_MAP.put(AlertType.SUCCESS, "✅");
    }

    /**
     * Muestra un diálogo de alerta moderno y personalizado
     * @param tipo Tipo de alerta (INFO, WARNING, ERROR, SUCCESS)
     * @param titulo Título personalizado de la alerta (opcional, null para usar el predeterminado)
     * @param mensaje Mensaje a mostrar
     */
    public static void show(AlertType tipo, String titulo, String mensaje) {
        // Crear el stage
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setResizable(false);

        // Color principal según el tipo de alerta
        String color = tipo.getColor();
        String colorHover = adjustColor(color, -20); 
        String tituloAlerta = titulo != null ? titulo : tipo.getTitulo();
        String icono = ICON_MAP.get(tipo);

        // Dropshadow
        VBox root = new VBox();
        root.setStyle("-fx-background-color: #fdfcfa; " +  
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 4);"
                    );

        // Título con icono
        HBox headerBox = new HBox(10);
        headerBox.setPadding(new Insets(15, 20, 15, 20));
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setStyle("-fx-background-color: " + color + ";");

        // Icono
        Label iconLabel = new Label(icono);
        iconLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");

        // Título
        Label titleLabel = new Label(tituloAlerta);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        titleLabel.setStyle("-fx-text-fill: white;");

        headerBox.getChildren().addAll(iconLabel, titleLabel);

   
        Label messageLabel = new Label(mensaje);
        messageLabel.setWrapText(true); 
        messageLabel.setTextAlignment(TextAlignment.CENTER);
        messageLabel.setAlignment(Pos.CENTER); // Añadir esta línea
        messageLabel.setPrefWidth(380); 
        messageLabel.setMaxWidth(380); 
        messageLabel.setPadding(new Insets(20, 20, 20, 20)); // Cambiar padding para que sea simétrico
        messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333; -fx-text-alignment: center;"); // Añadir text-alignment en CSS

        // Contenedor para el mensaje
        VBox messageContainer = new VBox(messageLabel);
        messageContainer.setMinHeight(80); 
        messageContainer.setPrefHeight(100); 
        messageContainer.setAlignment(Pos.CENTER);        
        VBox.setVgrow(messageContainer, Priority.ALWAYS);

        // Botón de aceptar
        Button acceptButton = new Button("OK");
        acceptButton.setPrefWidth(120);
        acceptButton.setPrefHeight(35);
        acceptButton.setStyle(
            "-fx-background-color: " + color + "; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 4px; " +
            "-fx-cursor: hand;"
        );

        // Efectos de hover
        acceptButton.setOnMouseEntered(e -> 
            acceptButton.setStyle(
                "-fx-background-color: " + colorHover + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 4px; " +
                "-fx-cursor: hand;"
            )
        );

        acceptButton.setOnMouseExited(e -> 
            acceptButton.setStyle(
                "-fx-background-color: " + color + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 4px; " +
                "-fx-cursor: hand;"
            )
        );

        acceptButton.setOnAction(e -> stage.close());

        // Contenedor para botón
        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 20, 20, 20));
        buttonBox.getChildren().add(acceptButton);

        root.getChildren().addAll(headerBox, messageContainer, buttonBox);
        double messageHeight = Math.min(Math.max(80, (mensaje.length() / 40) * 20), 200);
        
        Scene scene = new Scene(root, 420, 150 + messageHeight);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.showAndWait();
    }

    /**
     * Ajusta un color hexadecimal haciéndolo más claro u oscuro
     * @param color Color en formato hexadecimal (#RRGGBB)
     * @param amount Cantidad a ajustar (-255 a 255)
     * @return Color ajustado en formato hexadecimal
     */
    private static String adjustColor(String color, int amount) {
        Color c = Color.web(color);
        double red = clamp(c.getRed() * 255 + amount);
        double green = clamp(c.getGreen() * 255 + amount);
        double blue = clamp(c.getBlue() * 255 + amount);
        
        return String.format("#%02X%02X%02X", (int)red, (int)green, (int)blue);
    }

    private static double clamp(double value) {
        return Math.max(0, Math.min(255, value));
    }

    /**
     * Muestra un diálogo de información
     */
    public static void showInfo(String titulo, String mensaje) {
        show(AlertType.INFO, titulo, mensaje);
    }

    /**
     * Muestra un diálogo de advertencia
     */
    public static void showWarning(String titulo, String mensaje) {
        show(AlertType.WARNING, titulo, mensaje);
    }

    /**
     * Muestra un diálogo de error
     */
    public static void showError(String titulo, String mensaje) {
        show(AlertType.ERROR, titulo, mensaje);
    }

    /**
     * Muestra un diálogo de éxito
     */
    public static void showSuccess(String titulo, String mensaje) {
        show(AlertType.SUCCESS, titulo, mensaje);
    }
}