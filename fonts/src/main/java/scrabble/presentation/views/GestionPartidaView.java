package scrabble.presentation.views;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import scrabble.MainApplication;
import scrabble.excepciones.ExceptionDiccionarioExist;
import scrabble.excepciones.ExceptionPalabraInvalida;
import scrabble.presentation.viewControllers.ControladorPartidaView;
/**
 * Vista para gestión de partidas
 */
public class GestionPartidaView {
    private final ControladorPartidaView controlador;
    private Parent view;
    
    // Componentes FXML
    private Button btnJugarNuevaPartida;
    private Button btnEliminarPartida;
    private Button btnCargarPartida;
    private Button btnVolver;
    private TableView<PartidaRow> tablaPartidas;
    
    public GestionPartidaView(ControladorPartidaView controlador) {
        this.controlador = controlador;
        cargarVista();
    }
    
    /**
     * Clase para representar una fila en la tabla de partidas
     */
    public static class PartidaRow {
        private Integer id;
        private String diccionario;
        private Integer numJugadores;
        
        public PartidaRow(Integer id, String diccionario, Integer numJugadores) {
            this.id = id;
            this.diccionario = diccionario;
            this.numJugadores = numJugadores;
        }
        
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
        
        public String getDiccionario() { return diccionario; }
        public void setDiccionario(String diccionario) { this.diccionario = diccionario; }
        
        public Integer getNumJugadores() { return numJugadores; }
        public void setNumJugadores(Integer numJugadores) { this.numJugadores = numJugadores; }
    } 

    private void cargarVista() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("/views/gestion-partida-view.fxml"));
            view = loader.load();

            try {
                String cssResource = "/styles/button.css";
                URL cssUrl = getClass().getResource(cssResource);
                if (cssUrl != null) {
                    view.getStylesheets().add(cssUrl.toExternalForm());
                    System.out.println("CSS cargado en GestionPartidaView");
                }
                else {
                    System.err.println("No se pudo encontrar el recurso CSS: " + cssResource);
                }

                String tableCssResource = "/styles/table.css";
                URL tableCssUrl = getClass().getResource(tableCssResource);
                if (tableCssUrl != null) {
                    view.getStylesheets().add(tableCssUrl.toExternalForm());
                    System.out.println("CSS de tablas cargado en GestionPartidaView");
                }
                else {
                    System.err.println("No se pudo encontrar el recurso CSS: " + tableCssResource);
                }                
            } catch (Exception e) {
                System.err.println("Error al cargar CSS en GestionPartidaView: " + e.getMessage());
            }                        
            // Obtener referencias a los componentes usando lookup
            btnJugarNuevaPartida = (Button) view.lookup("#btnJugarNuevaPartida");
            btnEliminarPartida = (Button) view.lookup("#btnEliminarPartida");
            btnCargarPartida = (Button) view.lookup("#btnCargarPartida");
            btnVolver = (Button) view.lookup("#btnVolver");
            tablaPartidas = (TableView<PartidaRow>) view.lookup("#tablaPartidas");
            
            aplicarEstiloBotones();  
            configurarTabla();
            estilizarTabla();          
            configurarEventos();
            cargarPartidas();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudo cargar la vista de gestión de partidas");
        }
    }
    

    /**
     * Configura la tabla de partidas
     */
    private void configurarTabla() {
        if (tablaPartidas != null) {
            tablaPartidas.getColumns().clear();
            
            tablaPartidas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
            tablaPartidas.getStyleClass().add("modern-table");
            
            // Configurar columnas
            TableColumn<PartidaRow, Integer> colID = new TableColumn<>("ID");
            colID.setCellValueFactory(new PropertyValueFactory<>("id"));
            
            TableColumn<PartidaRow, String> colDiccionario = new TableColumn<>("Diccionario");
            colDiccionario.setCellValueFactory(new PropertyValueFactory<>("diccionario"));
            
            TableColumn<PartidaRow, Integer> colNumJugadores = new TableColumn<>("Jugadores");
            colNumJugadores.setCellValueFactory(new PropertyValueFactory<>("numJugadores"));
            
            tablaPartidas.getColumns().addAll(colID, colDiccionario, colNumJugadores);
            
            // Configurar anchos proporcionales
            colID.setMaxWidth(1f * Integer.MAX_VALUE * 20);
            colDiccionario.setMaxWidth(1f * Integer.MAX_VALUE * 50);
            colNumJugadores.setMaxWidth(1f * Integer.MAX_VALUE * 30);
            
            // Placeholder cuando no hay partidas
            tablaPartidas.setPlaceholder(new javafx.scene.control.Label("No hay partidas guardadas"));
        }
    }    

    /**
    * Aplica estilos y configuraciones adicionales a la TableView
    */
    private void estilizarTabla() {
        if (tablaPartidas != null) {
            // Aplicar clase CSS
            tablaPartidas.getStyleClass().add("modern-table");
            
            // Configurar dimensiones
            tablaPartidas.setMinWidth(400);
            tablaPartidas.setPrefWidth(600);
            tablaPartidas.setMaxWidth(Double.MAX_VALUE);
            
            // Desactivar reordenamiento de columnas
            for (TableColumn<PartidaRow, ?> column : tablaPartidas.getColumns()) {
                column.setReorderable(false);
            }
            
            // Configurar selección
            tablaPartidas.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.SINGLE);
            
            // Placeholder personalizado
            javafx.scene.control.Label placeholderLabel = new javafx.scene.control.Label("No hay partidas guardadas");
            placeholderLabel.setStyle("-fx-text-fill: #757575; -fx-font-style: italic; -fx-font-size: 14px;");
            tablaPartidas.setPlaceholder(placeholderLabel);
            
            // Event listener para habilitar/deshabilitar botones según selección
            tablaPartidas.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                boolean haySeleccion = newSelection != null;
                if (btnCargarPartida != null) btnCargarPartida.setDisable(!haySeleccion);
                if (btnEliminarPartida != null) btnEliminarPartida.setDisable(!haySeleccion);
            });
        }
    }

    /**
     * Aplicar clases CSS a los botones
     */
    private void aplicarEstiloBotones() {
        if (btnJugarNuevaPartida != null) {
            btnJugarNuevaPartida.getStyleClass().addAll("btn-effect", "btn-primary");
            System.out.println("Clases CSS aplicadas a btnJugarNuevaPartida: " + btnJugarNuevaPartida.getStyleClass());
        }
        
        if (btnEliminarPartida != null) {
            btnEliminarPartida.getStyleClass().addAll("btn-effect", "btn-danger");
            System.out.println("Clases CSS aplicadas a btnEliminarPartida: " + btnEliminarPartida.getStyleClass());
        }
        
        if (btnCargarPartida != null) {
            btnCargarPartida.getStyleClass().addAll("btn-effect", "btn-success");
            System.out.println("Clases CSS aplicadas a btnCargarPartida: " + btnCargarPartida.getStyleClass());
        }
        
        if (btnVolver != null) {
            btnVolver.getStyleClass().add("btn-effect");
            System.out.println("Clases CSS aplicadas a btnVolver: " + btnVolver.getStyleClass());
        }
    }

    private void configurarEventos() throws ExceptionDiccionarioExist, IOException, ExceptionPalabraInvalida {
        if (btnJugarNuevaPartida != null) {
            
            btnJugarNuevaPartida.setOnAction(e -> {
                try {
                    controlador.crearNuevaPartida();
                } catch (ExceptionDiccionarioExist | IOException | ExceptionPalabraInvalida ex) {
                    // Maneja la excepción - muestra un mensaje de error
                    showAlert(Alert.AlertType.ERROR, "Error", 
                        "Error al crear una nueva partida: " + ex.getMessage());
                    ex.printStackTrace(); // Para depuración
                }
            });
        }
        
        if (btnCargarPartida != null) {
            btnCargarPartida.setOnAction(e -> {
                PartidaRow partidaSeleccionada = tablaPartidas.getSelectionModel().getSelectedItem();
                if (partidaSeleccionada != null) {
                    System.out.println("CARGANDO PARTIDA " + partidaSeleccionada.getId());
                    controlador.cargarPartida(partidaSeleccionada.getId());
                } else {
                    controlador.mostrarAlerta("warning", "Advertencia", "Por favor, seleccione una partida para cargar");
                }
            });
        }
        
        if (btnEliminarPartida != null) {
            btnEliminarPartida.setOnAction(e -> {
                PartidaRow partidaSeleccionada = tablaPartidas.getSelectionModel().getSelectedItem();
                if (partidaSeleccionada != null) {
                    controlador.eliminarPartidaGuardada(partidaSeleccionada.getId());
                    cargarPartidas();
                } else {
                    controlador.mostrarAlerta("warning", "Advertencia", "Por favor, seleccione una partida para eliminar");
                }
            });
        }
        
        if (btnVolver != null) {
            btnVolver.setOnAction(e -> controlador.volverAMenuPrincipal());
        }
    }
    
    private void cargarPartidas() {
        if (tablaPartidas != null) {
            List<Integer> partidasGuardadas = controlador.getPartidasGuardadasID();
            ObservableList<PartidaRow> datos = FXCollections.observableArrayList();
            
            for (Integer id : partidasGuardadas) {
                 String diccionario = controlador.getDiccionarioPartida(id);
                int numJugadores = controlador.getNumJugadoresPartida(id); 
                
                datos.add(new PartidaRow(id, diccionario, numJugadores));
            }
            
            tablaPartidas.setItems(datos);
            
            // Seleccionar primer elemento si hay datos
            if (!datos.isEmpty()) {
                tablaPartidas.getSelectionModel().select(0);
            }
        }
    }
    
    private void showAlert(Alert.AlertType type, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(type.toString());
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    public Parent getView() {
        return view;
    }
}