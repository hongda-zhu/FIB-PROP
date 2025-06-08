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


/**
 * Popup modal para añadir nuevas palabras válidas a un diccionario.
 * Proporciona interfaz especializada con validación de consistencia alfabética
 * y prevención de duplicados mediante algoritmo de backtracking para verificación.
 * 
 * Características principales:
 * - Validación de consistencia con alfabeto del diccionario actual
 * - Algoritmo de backtracking para verificar construcción desde alfabeto
 * - Prevención de duplicados con verificación en tiempo real
 * - Validación de formato de entrada para solo letras válidas
 * - Retroalimentación inmediata con confirmación de éxito
 * 
 * @version 1.0
 * @since 1.0
 */
public class AñadirPalabraPopup {

    private final ObservableList<String> palabras;
    private final Set<String> alfabeto;
    private ControladorDiccionarioView controlador = ControladorDiccionarioView.getInstance();


    /**
     * Constructor que inicializa el popup con referencias a palabras y alfabeto.
     * Establece conexiones con la lista de palabras existentes y el conjunto
     * de letras del alfabeto para validaciones de duplicados y consistencia.
     * 
     * @pre palabras y alfabeto no deben ser null, deben corresponder al diccionario actual.
     * @param palabras Lista observable de palabras donde se añadirá la nueva palabra
     * @param alfabeto Conjunto de letras válidas del alfabeto del diccionario
     * @post El popup queda inicializado con validaciones configuradas para consistencia.
     */
    public AñadirPalabraPopup(ObservableList<String> palabras, Set<String> alfabeto) {
        this.palabras = palabras;
        this.alfabeto = alfabeto;
    }

    /**
     * Muestra el popup modal para añadir una nueva palabra al diccionario.
     * Crea interfaz con campo de entrada, validación en tiempo real de consistencia
     * alfabética, prevención de duplicados y confirmación de éxito al completar.
     * 
     * @pre owner no debe ser null y debe ser una ventana válida del sistema.
     * @param owner Ventana padre que será bloqueada por este popup modal
     * @post Se muestra popup modal que valida consistencia alfabética y duplicados.
     *       Si se completa exitosamente, la palabra se añade y se muestra confirmación.
     */
    public void mostrar(Stage owner) {
        Stage popup = new Stage();
        popup.initOwner(owner);
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Añadir palabra al diccionario");

        TextField campoPalabra = new TextField();
        campoPalabra.setPromptText("Ej: PALABRA");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        campoPalabra.textProperty().addListener((obs, oldVal, newVal) -> {
            String texto = newVal.trim().toUpperCase();

            if (texto.isEmpty()) {
                campoPalabra.setStyle("-fx-border-color: red;");
                errorLabel.setText("Campo obligatorio.");
            } else if (!texto.matches("[\\p{L}·]+")) {
                campoPalabra.setStyle("-fx-border-color: red;");
                errorLabel.setText("Solo letras sin espacios ni símbolos.");
            } else if (palabras.stream().anyMatch(p -> p.equalsIgnoreCase(texto))) {
                campoPalabra.setStyle("-fx-border-color: red;");
                errorLabel.setText("La palabra ya existe.");
            } else if (!puedeConstruirseDesdeAlfabeto(texto)) {
                campoPalabra.setStyle("-fx-border-color: red;");
                errorLabel.setText("Contiene letras no presentes en el alfabeto.");
            } else {
                campoPalabra.setStyle("");
                errorLabel.setText("");
            }
        });

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setOnAction(e -> popup.close());

        Button btnAceptar = new Button("Aceptar");
        btnAceptar.setDefaultButton(true);
        btnAceptar.setOnAction(e -> {
            String texto = campoPalabra.getText().trim().toUpperCase();
            boolean valido = true;
            if (texto.isEmpty()) {
                campoPalabra.setStyle("-fx-border-color: red;");
                errorLabel.setText("Campo obligatorio.");
                valido = false;
            } else if (!texto.matches("[\\p{L}·]+")) {
                campoPalabra.setStyle("-fx-border-color: red;");
                errorLabel.setText("Solo letras sin espacios ni símbolos.");
                valido = false;
            } else if (palabras.stream().anyMatch(p -> p.equalsIgnoreCase(texto))) {
                campoPalabra.setStyle("-fx-border-color: red;");
                errorLabel.setText("La palabra ya existe.");
                valido = false;
            } else if (!puedeConstruirseDesdeAlfabeto(texto)) {
                campoPalabra.setStyle("-fx-border-color: red;");
                errorLabel.setText("Contiene letras no presentes en el alfabeto.");
                valido = false;
            } else {
                campoPalabra.setStyle("");
                errorLabel.setText("");
            }
            if (valido) {
                palabras.add(texto);
                controlador.mostrarAlerta("success", "Palabra añadida", "La palabra \"" + texto + "\" se ha añadido correctamente al diccionario.");
                popup.close();
            }
        });

        HBox buttonBox = new HBox(20, btnCancelar, btnAceptar);
        buttonBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(8,
                new Label("Nueva palabra:"),
                campoPalabra,
                errorLabel,
                buttonBox
        );
        layout.setPadding(new Insets(15));
        layout.setAlignment(Pos.CENTER_LEFT);

        Scene scene = new Scene(layout);

         // Aplicar CSS
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


    /**
     * Verifica si una palabra puede construirse desde el alfabeto disponible.
     * Utiliza algoritmo de backtracking para determinar si la palabra puede
     * formarse utilizando únicamente las letras presentes en el alfabeto.
     * 
     * @pre palabra no debe ser null y debe ser una cadena válida.
     * @param palabra Palabra a verificar contra el alfabeto disponible
     * @return true si la palabra puede construirse, false en caso contrario
     * @post Se devuelve resultado de verificación sin modificar palabra o alfabeto.
     */
    private boolean puedeConstruirseDesdeAlfabeto(String palabra) {
        return puedeFormarseDesde(palabra, 0);
    }


    /**
     * Método recursivo de backtracking para verificación de construcción de palabra.
     * Implementa lógica recursiva que intenta formar la palabra desde el índice
     * especificado utilizando las letras disponibles en el alfabeto.
     * 
     * @pre palabra no debe ser null, idx debe ser un índice válido (0 <= idx <= palabra.length()).
     * @param palabra Palabra que se está verificando
     * @param idx Índice actual en la palabra desde donde continuar verificación
     * @return true si se puede formar la palabra desde este índice, false si no
     * @post Se devuelve resultado de verificación recursiva sin efectos secundarios.
     */
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
