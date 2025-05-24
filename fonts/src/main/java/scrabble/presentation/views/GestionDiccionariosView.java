package scrabble.presentation.views;

import java.net.URL;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import scrabble.presentation.componentes.DiccionarioVisual;
import scrabble.presentation.viewControllers.ControladorDiccionario;

/**
 * Clase que representa la vista de gestión de diccionarios.
 */

public class GestionDiccionariosView {
    private final Parent view;

    private ControladorDiccionario controlador;

    private Button btnCrear;
    private Button btnModificar;
    private Button btnEliminar;
    private Button btnImportar;
    private Button btnVolver;
    private TableView<DiccionarioVisual> listaDiccionarios;

    /**
     * Constructor de la vista de gestión de diccionarios.
     *
     * @param controlador Controlador de la vista.
     */

    public GestionDiccionariosView(ControladorDiccionario controlador) {
        this.controlador = controlador;
        Label titulo = new Label("Gestión de Diccionarios");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        titulo.setStyle("-fx-text-fill: white;");
        HBox header = new HBox(titulo);
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: #2f3e4e; -fx-padding: 15px;");

        VBox izquierda = new VBox(15);
        izquierda.setPadding(new Insets(20));
        izquierda.setSpacing(10);
        izquierda.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-background-radius: 10; -fx-border-color: #ffffff;");
        
        // Crear botones
        CrearBotones();


        izquierda.getChildren().addAll(btnCrear, btnImportar, btnModificar, btnEliminar, new Region(), btnVolver);
        VBox.setVgrow(izquierda.getChildren().get(4), Priority.ALWAYS);



        VBox diccionariosBox = new VBox(10);
        diccionariosBox.setPadding(new Insets(20));

        cargarDiccionarios();

        diccionariosBox.getChildren().addAll(listaDiccionarios);
        diccionariosBox.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-background-radius: 10; -fx-border-color: #ffffff;");

        HBox contenido = new HBox(20, izquierda, diccionariosBox);
        contenido.setPadding(new Insets(20));
        contenido.setAlignment(Pos.CENTER);
        contenido.setSpacing(40);
        HBox.setHgrow(izquierda, Priority.ALWAYS);
        HBox.setHgrow(diccionariosBox, Priority.ALWAYS);

        VBox root = new VBox(header, contenido);
        VBox.setVgrow(contenido, Priority.ALWAYS);

        // Bind the sizes of the components to adapt to the window size
        root.setPrefSize(800, 600);
        root.setStyle("-fx-background-color: #f5f5f5;");
        root.widthProperty().addListener((obs, oldVal, newVal) -> {
            izquierda.setPrefWidth(newVal.doubleValue() * 0.3);
            diccionariosBox.setPrefWidth(newVal.doubleValue() * 0.7);
        });

        root.heightProperty().addListener((obs, oldVal, newVal) -> {
            listaDiccionarios.setPrefHeight(newVal.doubleValue() * 0.9);
        });

        this.view = root;

        root.setMaxWidth(1200);

        try {
            List<String> cssResources = List.of("/styles/button.css", "/styles/excel-table.css");
            for (String cssResource : cssResources) {
                URL cssUrl = getClass().getResource(cssResource);
                if (cssUrl != null) {
                    view.getStylesheets().add(cssUrl.toExternalForm());
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar CSS en GestionDiccionariosView: " + e.getMessage());
        }

        aplicarCssBotones();
    }

    /**
     * Método para inicializar la lista de diccionarios.
     */

    private void cargarDiccionarios() {
        // Aquí se cargarían los diccionarios desde el modelo
        listaDiccionarios = new TableView<>();
        listaDiccionarios.setPlaceholder(new Label("No hay diccionarios disponibles"));
        listaDiccionarios.setItems(FXCollections.observableArrayList(controlador.getDiccionarios()));
        if (listaDiccionarios.getItems().isEmpty()) {
           listaDiccionarios.getItems().addAll(
            new DiccionarioVisual("Diccionario 1", List.of("A", "B", "C"), List.of("AB", "BC", "CA")),
            new DiccionarioVisual("Diccionario 2", List.of("X", "Y", "Z"), List.of("XY", "YZ", "ZX")),
            new DiccionarioVisual("Diccionario 3", List.of("M", "N", "O"), List.of("MN", "NO", "OM"))
            ); 
        }
        TableColumn<DiccionarioVisual, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombre()));
        colNombre.setMinWidth(300);
        colNombre.setCellFactory(column -> {
            TableCell<DiccionarioVisual, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item);
                        setAlignment(Pos.CENTER); // Centrado desde código
                    }
                }
            };
            return cell;
        });
        listaDiccionarios.getColumns().setAll(colNombre);
        listaDiccionarios.setPrefHeight(400);
        listaDiccionarios.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        listaDiccionarios.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                DiccionarioVisual diccionarioSeleccionado = listaDiccionarios.getSelectionModel().getSelectedItem();
                if (diccionarioSeleccionado != null) {
                    controlador.mostrarPopupDiccionario(diccionarioSeleccionado);
                }
            }
        });

        // Enfocar la columna seleccionada cuando se selecciona
        DiccionarioVisual seleccionado = listaDiccionarios.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            int row = listaDiccionarios.getItems().indexOf(seleccionado);
            listaDiccionarios.getFocusModel().focus(row, listaDiccionarios.getColumns().get(0));
            listaDiccionarios.getSelectionModel().select(row);
            listaDiccionarios.scrollTo(row); // opcional, para asegurar visibilidad
        }
    }

    /**
     * Método para aplicar estilos CSS a los botones.
     */

    private void aplicarCssBotones() {
        btnCrear.getStyleClass().addAll("btn-effect", "btn-primary");
        btnModificar.getStyleClass().addAll("btn-effect", "btn-primary");
        btnEliminar.getStyleClass().addAll("btn-effect", "btn-danger");
        btnImportar.getStyleClass().addAll("btn-effect", "btn-primary");
        btnVolver.getStyleClass().add("btn-effect");
    }


    /**
     * Método para crear los botones de la vista.
     */

    private void CrearBotones() {
        btnCrear = crearBoton("Crear Diccionario", "#3498db");
        btnModificar = crearBoton("Modificar Diccionario", "#3498db");
        btnEliminar = crearBoton("Eliminar Diccionario", "#e74c3c");
        btnImportar = crearBoton("Importar Diccionario", "#3498db");
        btnVolver = crearBoton("Volver", "#95a5a6");

        btnVolver.setOnAction(e -> controlador.volverAMenuPrincipal());
        btnImportar.setOnAction(e -> controlador.abrirSelectorDeDirectorio());
        btnCrear.setOnAction(e -> controlador.mostrarVistaCrear());
        btnModificar.setOnAction(e -> {
            DiccionarioVisual diccionarioSeleccionado = listaDiccionarios.getSelectionModel().getSelectedItem();
            if (diccionarioSeleccionado != null) {
                controlador.mostrarVistaModificar(diccionarioSeleccionado);
            } else {
                controlador.mostrarAlerta("info", "Selecciona un diccionario", "Por favor, selecciona un diccionario para modificar.");
            }
        });
        btnEliminar.setOnAction(e -> eliminarDiccionario());
    }

    /**
     * Método para crear un botón con el texto y color especificados.
     *
     * @param texto Texto del botón.
     * @param color Color de fondo del botón.
     * @return El botón creado.
     */

    private Button crearBoton(String texto, String color) {
        Button btn = new Button(texto);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 6;");
        btn.setMinHeight(45);
        btn.setMaxWidth(Double.MAX_VALUE);
        return btn;
    }

    /**
     * Método para eliminar un diccionario seleccionado.
     */

    private void eliminarDiccionario() {
        DiccionarioVisual diccionarioSeleccionado = listaDiccionarios.getSelectionModel().getSelectedItem();
        if (diccionarioSeleccionado != null) {
            // Aquí se llamaría al método del controlador para eliminar el diccionario
            if (controlador.eliminarDiccionario(diccionarioSeleccionado)) {
                listaDiccionarios.getItems().remove(diccionarioSeleccionado);
                controlador.mostrarAlerta("success", "Diccionario eliminado", "El diccionario" + diccionarioSeleccionado.getNombre() + " ha sido eliminado correctamente.");
            }
            
        } else {   
            controlador.mostrarAlerta("info", "Selecciona un diccionario", "Por favor, selecciona un diccionario para eliminar.");
        }
    }

    /**
     * Método para obtener la vista.
     *
     * @return La vista de gestión de diccionarios.
     */

    public Parent getView() {
        return view;
    }
}
