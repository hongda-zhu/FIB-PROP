package scrabble.presentation.popups;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


/**
 * Diálogo personalizado para mostrar alertas modernas con iconos y estilos personalizados.
 * Proporciona diferentes tipos de alertas: información, advertencia, error y éxito.
 */
public class customDialogo {

    /**
     * Tipos de alerta soportados con sus respectivos colores e iconos.
     */
    public enum AlertType {
        INFO("#2196F3", "INFORMACIÓN", "information.png"),
        WARNING("#FF9800", "ADVERTENCIA", "alert.png"),
        ERROR("#F44336", "ERROR", "close.png"),
        SUCCESS("#4CAF50", "ÉXITO", "check.png");
        
        private final String color;
        private final String titulo;
        private final String iconFile;

        /**
         * Constructor del tipo de alerta.
         * @param color Color hexadecimal del tipo de alerta
         * @param titulo Título predeterminado del tipo
         * @param iconFile Nombre del archivo de icono
         */        
        AlertType(String color, String titulo, String iconFile) {
            this.color = color;
            this.titulo = titulo;
            this.iconFile = iconFile;
        }
        
        public String getColor() {
            return color;
        }
        
        public String getTitulo() {
            return titulo;
        }
        
        public String getIconFile() {
            return iconFile;
        }
    }


    /**
     * Crea un ImageView para el icono del tipo de alerta especificado.
     * @param tipo Tipo de alerta
     * @return ImageView con el icono o null si no se puede cargar
     */
    private static ImageView createIcon(AlertType tipo) {
        try {
            String iconPath = "/imgs/" + tipo.getIconFile();
            Image image = new Image(customDialogo.class.getResourceAsStream(iconPath));
            
            if (image.isError()) {
                System.err.println("❌ Error al cargar imagen: " + iconPath);
                return null;
            }
            
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(20);
            imageView.setFitHeight(20);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            
            return imageView;
        } catch (Exception e) {
            System.err.println("❌ Excepción al cargar icono: " + e.getMessage());
            return null;
        }
    }


    /**
     * Muestra un diálogo de alerta moderno y personalizado.
     * @param tipo Tipo de alerta
     * @param titulo Título personalizado (null para usar el predeterminado)
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

        // Contenedor principal con sombra
        VBox root = new VBox();
        root.setStyle("-fx-background-color: #fdfcfa; " +  
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 4);"
                    );

        // Header con icono y título
        HBox headerBox = new HBox(10);
        headerBox.setPadding(new Insets(15, 20, 15, 20));
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setStyle("-fx-background-color: " + color + ";");

        // Crear el icono
        ImageView iconView = createIcon(tipo);
        if (iconView != null) {
            // Aplicar filtro blanco al icono para que se vea bien en el header colorido
            iconView.setStyle("-fx-effect: dropshadow(gaussian, white, 0, 1.0, 0, 0);");
        }

        // Título
        Label titleLabel = new Label(tituloAlerta);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        titleLabel.setStyle("-fx-text-fill: white;");

        // Agregar elementos al header
        if (iconView != null) {
            headerBox.getChildren().addAll(iconView, titleLabel);
        } else {
            // Fallback: usar texto si no se puede cargar la imagen
            Label fallbackIcon = new Label(getFallbackIcon(tipo));
            fallbackIcon.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");
            headerBox.getChildren().addAll(fallbackIcon, titleLabel);
        }

        // Usar Text en lugar de Label para mejor cálculo de altura
        Text messageText = new Text(mensaje);
        messageText.setWrappingWidth(360); // Ancho ligeramente menor para márgenes
        messageText.setTextAlignment(TextAlignment.CENTER);
        messageText.setStyle("-fx-font-size: 14px; -fx-fill: #333333;");
        messageText.setFont(Font.font("System", 14));
        
        // Calcular la altura real del texto
        double textHeight = messageText.getBoundsInLocal().getHeight();
        
        // Contenedor para el texto con padding
        VBox messageContainer = new VBox();
        messageContainer.setPadding(new Insets(20));
        messageContainer.setAlignment(Pos.CENTER);
        messageContainer.getChildren().add(messageText);
        
        // Calcular altura mínima necesaria
        double minContentHeight = Math.max(80, textHeight + 40); // 40 para padding vertical
        messageContainer.setMinHeight(minContentHeight);
        messageContainer.setPrefHeight(minContentHeight);
        
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
        
        // Calcular altura total del diálogo
        double headerHeight = 50; // altura aproximada del header
        double buttonBoxHeight = 65; // altura del área del botón con padding
        double totalHeight = headerHeight + minContentHeight + buttonBoxHeight;
        
        // Asegurar una altura mínima
        totalHeight = Math.max(200, totalHeight);
        
        // Limitar altura máxima para evitar diálogos demasiado grandes
        totalHeight = Math.min(600, totalHeight);
        
        Scene scene = new Scene(root, 420, totalHeight);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        
        // Centrar en pantalla
        stage.centerOnScreen();
        
        stage.showAndWait();
    }


    /**
     * Obtiene un icono emoji de fallback cuando no se puede cargar la imagen.
     * @param tipo Tipo de alerta
     * @return String con emoji representativo del tipo de alerta
     */
    private static String getFallbackIcon(AlertType tipo) {
        switch (tipo) {
            case INFO: return "ℹ️";
            case WARNING: return "⚠️";
            case ERROR: return "❌";
            case SUCCESS: return "✅";
            default: return "•";
        }
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


    /**
     * Limita un valor entre 0 y 255.
     * @param value Valor a limitar
     * @return Valor limitado entre 0 y 255
     */
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
     * Muestra un diálogo de información.
     * @param titulo Título del diálogo
     * @param mensaje Mensaje a mostrar
     */
    public static void showWarning(String titulo, String mensaje) {
        show(AlertType.WARNING, titulo, mensaje);
    }


    /**
     * Muestra un diálogo de advertencia.
     * @param titulo Título del diálogo
     * @param mensaje Mensaje a mostrar
     */
    public static void showError(String titulo, String mensaje) {
        show(AlertType.ERROR, titulo, mensaje);
    }

    /**
     * Muestra un diálogo de éxito.
     * @param titulo Título del diálogo
     * @param mensaje Mensaje a mostrar
     */
    public static void showSuccess(String titulo, String mensaje) {
        show(AlertType.SUCCESS, titulo, mensaje);
    }
}