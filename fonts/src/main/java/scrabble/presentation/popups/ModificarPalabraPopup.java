package scrabble.presentation.popups;

import java.util.Set;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import scrabble.presentation.viewControllers.ControladorDiccionarioView;

public class ModificarPalabraPopup {

    private final ObservableList<String> palabras;
    private final Set<String> alfabeto;
    private final String palabraOriginal;
    private ControladorDiccionarioView controlador = ControladorDiccionarioView.getInstance();

    public ModificarPalabraPopup(ObservableList<String> palabras, Set<String> alfabeto, String palabraOriginal) {
        this.palabras = palabras;
        this.alfabeto = alfabeto;
        this.palabraOriginal = palabraOriginal;
    }

    public void mostrar(Stage owner) {
        Stage popup = new Stage();
        popup.initOwner(owner);
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Modificar palabra del diccionario");

        TextField campoPalabra = new TextField(palabraOriginal);
        campoPalabra.setPromptText("Ej: PALABRA");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        campoPalabra.textProperty().addListener((obs, oldVal, newVal) -> {
            validar(campoPalabra, errorLabel, newVal.trim().toUpperCase());
        });

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setOnAction(e -> popup.close());

        Button btnAceptar = new Button("Aceptar");
        btnAceptar.setDefaultButton(true);
        btnAceptar.setOnAction(e -> {
            String texto = campoPalabra.getText().trim().toUpperCase();
            if (validar(campoPalabra, errorLabel, texto)) {
                int index = palabras.indexOf(palabraOriginal);
                if (index >= 0) {
                    palabras.set(index, texto);
                }
                controlador.mostrarAlerta("success", "Palabra modificada", "La palabra \"" + texto + "\" ha sido modificado correctamente");
                popup.close();
            }
        });

        HBox buttonBox = new HBox(20, btnCancelar, btnAceptar);
        buttonBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(8,
                new Label("Modificar palabra:"),
                campoPalabra,
                errorLabel,
                buttonBox
        );
        layout.setPadding(new Insets(15));
        layout.setAlignment(Pos.CENTER_LEFT);

        Scene scene = new Scene(layout);

        try {
            String cssResource = "/styles/button.css";
            scene.getStylesheets().add(getClass().getResource(cssResource).toExternalForm());
        } catch (Exception e) {
            System.err.println("Error al cargar CSS: " + e.getMessage());
        }

        btnAceptar.getStyleClass().addAll("btn-effect", "btn-success");
        btnCancelar.getStyleClass().addAll("btn-effect", "btn-danger");

        popup.setScene(scene);
        popup.showAndWait();
    }

    private boolean validar(TextField campo, Label errorLabel, String texto) {
        if (texto.isEmpty()) {
            campo.setStyle("-fx-border-color: red;");
            errorLabel.setText("Campo obligatorio.");
            return false;
        } else if (!texto.matches("[\\p{L}·]+")) {
            campo.setStyle("-fx-border-color: red;");
            errorLabel.setText("Solo letras sin espacios ni símbolos.");
            return false;
        } else if (!texto.equalsIgnoreCase(palabraOriginal) &&
                   palabras.stream().anyMatch(p -> p.equalsIgnoreCase(texto))) {
            campo.setStyle("-fx-border-color: red;");
            errorLabel.setText("Ya existe esa palabra.");
            return false;
        } else if (!puedeConstruirseDesdeAlfabeto(texto)) {
            campo.setStyle("-fx-border-color: red;");
            errorLabel.setText("Contiene letras no presentes en el alfabeto.");
            return false;
        }

        campo.setStyle("");
        errorLabel.setText("");
        return true;
    }

    private boolean puedeConstruirseDesdeAlfabeto(String palabra) {
        return puedeFormarseDesde(palabra, 0);
    }

    private boolean puedeFormarseDesde(String palabra, int idx) {
        if (idx == palabra.length()) return true;
        for (String letra : alfabeto) {
            if (palabra.startsWith(letra, idx)) {
                if (puedeFormarseDesde(palabra, idx + letra.length())) return true;
            }
        }
        return false;
    }
}
