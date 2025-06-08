package scrabble.presentation.popups;

import java.util.List;
import java.util.function.Consumer;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Componente popup genérico para mostrar diálogos personalizables
 */
public class PausaPopup {
    
    public enum ButtonStyle {
        SUCCESS("btn-success"),
        INFO("btn-primary"),
        WARNING("btn-warning"),
        DANGER("btn-danger"),
        SECONDARY("btn-primary"); // Cambiado de gris a estilo primario
        
        private final String styleClass;
        
        ButtonStyle(String styleClass) {
            this.styleClass = styleClass;
        }
        
        public String getStyleClass() {
            return styleClass;
        }
    }
    
    public static class PopupButton {
        private final String text;
        private final ButtonStyle style;
        private final Consumer<Stage> action;
        
        public PopupButton(String text, ButtonStyle style, Consumer<Stage> action) {
            this.text = text;
            this.style = style;
            this.action = action;
        }
        
        public String getText() { return text; }
        public ButtonStyle getStyle() { return style; }
        public Consumer<Stage> getAction() { return action; }
    }
    
    /**
     * Muestra un popup con título, mensaje y botones personalizables
     */
    public static void show(String title, String message, List<PopupButton> buttons) {
        Stage popupStage = new Stage();
        popupStage.initStyle(StageStyle.TRANSPARENT);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        
        // Contenedor principal
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: white; " +
                     "-fx-background-radius: 20; " +
                     "-fx-border-radius: 20; " +
                     "-fx-padding: 40px; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 20, 0, 0, 5);");
        
        // Título
        if (title != null && !title.isEmpty()) {
            Label titleLabel = new Label(title);
            titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #ff9800;"); // Cambiado a naranja para coincidir con tu tema
            root.getChildren().add(titleLabel);
        }
        
        // Mensaje
        if (message != null && !message.isEmpty()) {
            Label messageLabel = new Label(message);
            messageLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333; -fx-text-alignment: center;");
            messageLabel.setWrapText(true);
            messageLabel.setMaxWidth(350);
            messageLabel.setAlignment(Pos.CENTER);            
            root.getChildren().add(messageLabel);
        }
        
        // Contenedor de botones
        VBox buttonContainer = new VBox(15);
        buttonContainer.setAlignment(Pos.CENTER);
        
        // Cargar CSS desde archivo
        String cssPath = PausaPopup.class.getResource("/styles/button.css").toExternalForm();
        
        // Crear botones
        for (PopupButton popupButton : buttons) {
            Button button = createStyledButton(popupButton.getText(), popupButton.getStyle());
            button.setOnAction(e -> {
                popupButton.getAction().accept(popupStage);
                if (!popupStage.isShowing()) {
                    // Solo cerramos si la acción no ya cerró el popup
                    popupStage.close();
                }                
            });
            buttonContainer.getChildren().add(button);
        }
        
        root.getChildren().add(buttonContainer);
        
        // Configurar y mostrar
        Scene scene = new Scene(root);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        
        // Cargar estilos CSS
        scene.getStylesheets().add(cssPath);
        
        popupStage.setScene(scene);
        popupStage.centerOnScreen();
        popupStage.showAndWait();
    }
    
    /**
     * Crea un botón con los estilos de las clases CSS
     */
    private static Button createStyledButton(String text, ButtonStyle style) {
        Button button = new Button(text);
        
        // Aplicar clases CSS en lugar de estilos inline
        button.getStyleClass().add("btn-effect");
        button.getStyleClass().add(style.getStyleClass());
        
        // Configuración básica del botón
        button.setMinWidth(250);
        button.setMinHeight(50);
        button.setPrefHeight(50);
        button.setWrapText(true);
        button.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        return button;
    }
    
    /**
     * Método helper para mostrar confirmación simple
     */
    public static void showConfirmation(String title, String message, 
                                       Consumer<Stage> onConfirm, Consumer<Stage> onCancel) {
        List<PopupButton> buttons = List.of(
            new PopupButton("Confirmar", ButtonStyle.SUCCESS, onConfirm),
            new PopupButton("Cancelar", ButtonStyle.DANGER, onCancel)
        );
        show(title, message, buttons);
    }
    
    /**
     * Método helper para mostrar información
     */
    public static void showInfo(String title, String message, Consumer<Stage> onOk) {
        List<PopupButton> buttons = List.of(
            new PopupButton("OK", ButtonStyle.INFO, onOk)
        );
        show(title, message, buttons);
    }
    
    /**
     * Método helper para mostrar error
     */
    public static void showError(String title, String message, Consumer<Stage> onOk) {
        List<PopupButton> buttons = List.of(
            new PopupButton("OK", ButtonStyle.DANGER, onOk)
        );
        show(title, message, buttons);
    }
}