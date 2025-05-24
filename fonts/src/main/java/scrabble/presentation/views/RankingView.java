package scrabble.presentation.views;

import java.net.URL;
import java.util.Comparator;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import scrabble.presentation.componentes.JugadorRanking;
import scrabble.presentation.viewControllers.ControladorRankingView;

/**
 * Clase que representa la vista de gestión de ranking.
 * Permite visualizar y gestionar el ranking de jugadores.
 */

public class RankingView {

    private final Parent view;

    private ControladorRankingView controlador;

    private Button volverBtn;
    private Button eliminarBtn;
    private Button searchBtn;
    private Button restablecerBtn;
    private TableView<JugadorRanking> table;
    private ComboBox<String> orderCombo;
    private ComboBox<String> filterCombo;
    private TextField quantityField;
    private ObservableList<JugadorRanking> datosOriginales;
    private ObservableList<JugadorRanking> datosVisibles;
    private Label errorLabel;
    

    /**
     * Constructor de la vista de gestión de ranking.
     *
     * @param controlador Controlador de la vista.
     */

    public RankingView(ControladorRankingView controlador) {
        this.controlador = controlador;
        crearBotones();
        // Barra superior con título
        Label title = new Label("Gestión de Ranking");
        title.setFont(new Font("Arial", 24));
        title.setTextFill(Color.WHITE);
        StackPane topBar = new StackPane(title);
        topBar.setPadding(new Insets(20));
        topBar.setStyle("-fx-background-color: #2E3A59;");

        // Filtros (no funcionales, solo estéticos como en la imagen)
        this.orderCombo = new ComboBox<>();
        orderCombo.setPromptText("Ordenar por:");
        orderCombo.setDisable(false);
        orderCombo.setItems(FXCollections.observableArrayList(
        "P. Total", "P. Máxima", "P. Media", "Partidas", "Victorias"
        ));
        
        orderCombo.setOnAction(e -> aplicarOrden(orderCombo.getValue()));

        this.filterCombo = new ComboBox<>();
        filterCombo.setPromptText("Filtrar por:");
        filterCombo.setDisable(false);
        filterCombo.setItems(FXCollections.observableArrayList(
        "P. Total", "P. Máxima", "P. Media", "Partidas", "Victorias"
        ));

        this.quantityField = new TextField();
        quantityField.setPromptText("Cantidad a filtrar:");
        quantityField.setDisable(true);

        filterCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            quantityField.setDisable(newVal == null || newVal.isBlank());
            errorLabel.setText("");
        });
        
        quantityField.setPrefWidth(150);
        quantityField.setMaxWidth(150);

        this.errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setStyle("-fx-font-size: 12;");
        errorLabel.setVisible(false);

        errorLabel.setMaxWidth(150);

        quantityField.textProperty().addListener((obs, oldVal, newVal) -> {
            validarCantidadEnTiempoReal(newVal);
        });

        VBox quantityBox = new VBox(2, quantityField, errorLabel);
        quantityBox.setPadding(new Insets(16, 0, 0, 0));


        HBox filterBox = new HBox(10, orderCombo, new Region(), filterCombo, quantityBox, searchBtn, restablecerBtn);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        filterBox.setPadding(new Insets(10, 100, 10, 100));
        HBox.setHgrow(filterBox.getChildren().get(1), Priority.ALWAYS);

        // Tabla
        this.table = new TableView<>();
        
        inicializarTabla();

        aplicarOrden("P.Total");

        Button page1 = new Button("1");
        page1.setStyle("-fx-background-color: #2E3A59; -fx-text-fill: white;");
        Button page2 = new Button("2");
        Button prev = new Button("<<");
        Button next = new Button(">>");

        HBox pagination = new HBox(5, prev, page1, page2, next);
        pagination.setAlignment(Pos.CENTER);

        HBox controlBox = new HBox(20, volverBtn, eliminarBtn);
        controlBox.setAlignment(Pos.CENTER);
        controlBox.setPadding(new Insets(20));

        table.setPrefWidth(1000);

        HBox centeredBox = new HBox(table);
        centeredBox.setAlignment(Pos.CENTER);

        // Layout principal
        VBox layout = new VBox();
        layout.setSpacing(10);
        layout.setStyle("-fx-background-color: #f8f8f8;");
        layout.setPadding(new Insets(10));

        layout.getChildren().addAll(topBar, filterBox, centeredBox, controlBox);
        layout.setMaxWidth(1200);

        this.view = layout;

        try {
            List<String> cssResources = List.of(
                "/styles/table.css",
                "/styles/button.css"
            );

            for (String cssResource : cssResources) {
                URL cssUrl = getClass().getResource(cssResource);
                if (cssUrl != null) {
                    view.getStylesheets().add(cssUrl.toExternalForm());
                    System.out.println("CSS cargado en CrearDiccionarioView: " + cssResource);
                } else {
                    System.err.println("No se pudo encontrar el recurso CSS: " + cssResource);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar CSS en CrearDiccionarioView: " + e.getMessage());
        }
        aplicarCssBotones();
        eliminarBtn.setDisable(true);
    }


    /**
    * Aplica estilos y configuraciones adicionales a la TableView
    */
    private void estilizarTabla() {
        if (table != null) {
            // Aplicar clase CSS
            table.getStyleClass().add("modern-table");
            
            // Configurar dimensiones
            table.setMinWidth(600);
            table.setPrefWidth(800);
            table.setMaxWidth(Double.MAX_VALUE);
            
            // Desactivar reordenamiento de columnas
            for (TableColumn<JugadorRanking, ?> column : table.getColumns()) {
                column.setReorderable(false);
            }
            
            // Configurar selección
            table.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.SINGLE);
            
            // Placeholder personalizado con mejor estilo
            Label placeholderLabel = new Label("No hay jugadores en el ranking");
            placeholderLabel.setStyle("-fx-text-fill: #757575; -fx-font-style: italic; -fx-font-size: 14px;");
            table.setPlaceholder(placeholderLabel);
            
            // Event listener para habilitar/deshabilitar botón según selección
            table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                boolean haySeleccion = newSelection != null;
                if (eliminarBtn != null) eliminarBtn.setDisable(!haySeleccion);
            });
            
            // Configurar altura de filas
            table.setRowFactory(tv -> {
                javafx.scene.control.TableRow<JugadorRanking> row = new javafx.scene.control.TableRow<>();
                row.setPrefHeight(45); 
                return row;
            });
            
            table.setPadding(new Insets(0));
        }
    }
    private void aplicarCssBotones() {
        eliminarBtn.getStyleClass().addAll("btn-effect", "btn-danger");
        volverBtn.getStyleClass().addAll("btn-effect", "btn-primary");
        searchBtn.getStyleClass().addAll("btn-effect", "btn-primary");
        restablecerBtn.getStyleClass().addAll("btn-effect", "btn-primary");
    }

    /**
     * Método para aplicar el orden seleccionado en la tabla.
     *
     * @param criterio Criterio de ordenación.
     */

    private void aplicarOrden(String criterio) {
        Comparator<JugadorRanking> comparador;
        if (criterio == null) return;
        switch (criterio) {
            case "P. Total":
                comparador = Comparator.comparingInt(JugadorRanking::getPuntosTotales).reversed();
                break;
            case "P. Máxima":
                comparador = Comparator.comparingInt(JugadorRanking::getPuntosMaximos).reversed();
                break;
            case "P. Media":
                comparador = Comparator.comparingDouble(JugadorRanking::getPuntosMedias).reversed();
                break;
            case "Partidas":
                comparador = Comparator.comparingInt(JugadorRanking::getPartidas).reversed();
                break;    
            case "Victorias":
                comparador = Comparator.comparingInt(JugadorRanking::getVictorias).reversed();
                break;
            default:
                comparador = Comparator.comparingInt(JugadorRanking::getPuntosTotales).reversed();
                break;
        }
        FXCollections.sort(table.getItems(), comparador);
    }

    /**
     * Método para filtrar la tabla
     */

    private void filtrarTabla() {
        // Implementar lógica de filtrado aquí
        // Por ejemplo, filtrar los jugadores que cumplen con el criterio y el límite
        String criterio = filterCombo.getValue();
        String limitText = quantityField.getText().trim();
        if (criterio == null) {
            controlador.mostrarAlerta("error", "Filtrado de tabla", "Debe seleccionar un criterio de filtrado.");
            return;
        }
        try {
            Double limit;
            if (limitText.isBlank()) limit = 0.0;
            else limit = Double.parseDouble(limitText);
            if (limit < 0) {
                controlador.mostrarAlerta("error", "Filtrado de tabla", "La cantidad de filtro debe ser un número positivo");
                return;
            }
            aplicarFiltro(criterio, limit);
        } catch (NumberFormatException e) {
            controlador.mostrarAlerta("error", "Filtrado de tabla", "La cantidad de filtro no és un número válido");
        }
    }

    private void validarCantidadEnTiempoReal(String valor) {
        if (valor == null || valor.isBlank()) {
            errorLabel.setText("Ingrese una cantidad.");
            errorLabel.setVisible(true);
        } else {
            try {
                double cantidad = Double.parseDouble(valor);
                if (cantidad < 0) {
                    errorLabel.setText("Debe ser positivo.");
                    errorLabel.setVisible(true);
                } else {
                    errorLabel.setVisible(false);
                }
            } catch (NumberFormatException e) {
                errorLabel.setText("Debe ser un número.");
                errorLabel.setVisible(true);
            }
        }
    }


    /**
     * Método para aplicar el filtro a la tabla.
     *
     * @param criterio Criterio de filtrado.
     * @param limit Límite de filtrado.
     */

    private void aplicarFiltro(String criterio, Double limit) {
        datosVisibles.setAll(
            datosOriginales.filtered(jugador -> {
                switch (criterio) {
                    case "P. Total":
                        return jugador.getPuntosTotales() >= limit;
                    case "P. Máxima":
                        return jugador.getPuntosMaximos() >= limit;
                    case "P. Media":
                        return jugador.getPuntosMedias() >= limit;
                    case "Partidas":
                        return jugador.getPartidas() >= limit;
                    case "Victorias":
                        return jugador.getVictorias() >= limit;
                    default:
                        return true;
                }
            })
        );
    }

    /**
     * Método para inicializar la tabla con los datos de los jugadores.
     */

    private void inicializarTabla() {
        table.getStyleClass().add("excel-table");
        TableColumn<JugadorRanking, Number> colPos = new TableColumn<>("Posición");
        colPos.setCellValueFactory(cellData -> {
            int index = table.getItems().indexOf(cellData.getValue()) + 1;
            return new javafx.beans.property.SimpleIntegerProperty(index);
        });

        TableColumn<JugadorRanking, String> colNombre = new TableColumn<>("Jugador");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<JugadorRanking, Number> colTotal = new TableColumn<>("P. Total");
        colTotal.setCellValueFactory(new PropertyValueFactory<>("puntosTotales"));

        TableColumn<JugadorRanking, Number> colMax = new TableColumn<>("P. Máxima");
        colMax.setCellValueFactory(new PropertyValueFactory<>("puntosMaximos"));

        TableColumn<JugadorRanking, Number> colMedia = new TableColumn<>("P. Media");
        colMedia.setCellValueFactory(new PropertyValueFactory<>("puntosMedias"));

        TableColumn<JugadorRanking, Number> colPartidas = new TableColumn<>("Partidas");
        colPartidas.setCellValueFactory(new PropertyValueFactory<>("partidas"));

        TableColumn<JugadorRanking, Number> colVictorias = new TableColumn<>("Victorias");
        colVictorias.setCellValueFactory(new PropertyValueFactory<>("victorias"));

        table.getColumns().addAll(colPos, colNombre, colTotal, colMax, colMedia, colPartidas, colVictorias);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        datosOriginales = FXCollections.observableArrayList();
        List<String> usuarios = controlador.getUsuariosRanking();
        for (String usuario: usuarios) {
            int pt = controlador.getPuntuacionTotal(usuario);
            int pm = controlador.getPuntuacionMaxima(usuario);
            double pmed = Math.round(controlador.getPuntuacionMedia(usuario) * 100.0) / 100.0;
            int partidas = controlador.getPartidasJugadas(usuario);
            int victorias = controlador.getvictorias(usuario);

            JugadorRanking jugador = new JugadorRanking(usuario, pt, pm, pmed, partidas, victorias);

            datosOriginales.add(jugador);
        }

        // datosOriginales = FXCollections.observableArrayList(
        //     new JugadorRanking("Alice", 1230, 300, 205.0, 10, 4),
        //     new JugadorRanking("Bob", 950, 250, 158.3, 9, 2),
        //     new JugadorRanking("Charlie", 1430, 350, 238.3, 10, 5),
        //     new JugadorRanking("Diana", 800, 200, 160.0, 5, 1),
        //     new JugadorRanking("Eve", 1760, 400, 293.3, 6, 6)
        // );

        // table.setPadding(new Insets(200));
        datosVisibles = FXCollections.observableArrayList(datosOriginales);
        table.setItems(datosVisibles);

        estilizarTabla();
    }

    /**
     * Método para crear los botones de la vista.
     */

    private void crearBotones() {
        volverBtn = crearBoton("Volver", "#2E3A59");
        volverBtn.setOnAction(e -> controlador.volverAMenuPrincipal());

        eliminarBtn = crearBoton("Eliminar Jugador del Ranking", "#e74c3c");
        eliminarBtn.setOnAction(e -> {
            JugadorRanking seleccionado = table.getSelectionModel().getSelectedItem();
            if (seleccionado == null) {
                controlador.mostrarAlerta("info", "Eliminar jugador", "Seleccione un jugador del ranking para eliminar.");
                return;
            }
            if (controlador.eliminarUsuarioRanking(seleccionado.getNombre())) {
                datosOriginales.remove(seleccionado);
                datosVisibles.remove(seleccionado);
                controlador.mostrarAlerta("success", "Jugador eliminado", "El jugador " + seleccionado.getNombre() + " ha sido eliminado del ranking.");
            } else {
                controlador.mostrarAlerta("error", "Eliminar jugador", "No se pudo eliminar el jugador " + seleccionado.getNombre() + "del ranking.");
            }
        });

        searchBtn = crearBoton("Buscar", "#3498db");
        searchBtn.setOnAction(e -> filtrarTabla());

        restablecerBtn = crearBoton("Restablecer", "#3498db");
        restablecerBtn.setOnAction(e -> {
            orderCombo.getSelectionModel().clearSelection();
            orderCombo.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? orderCombo.getPromptText() : item);
                }
            });
            filterCombo.getSelectionModel().clearSelection();
            filterCombo.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? filterCombo.getPromptText() : item);
                }
            });
            quantityField.clear();
            quantityField.setPromptText("Cantidad a filtrar:");
            datosVisibles.setAll(datosOriginales);
            errorLabel.setText("");
            aplicarOrden("P. Total");
        });
    }

    /**
     * Método para crear un botón con un texto y un color específico.
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
     * Método para obtener la vista.
     *
     * @return La vista de gestión de ranking.
     */

    public Parent getView() {
        return view;
    }
    
}
