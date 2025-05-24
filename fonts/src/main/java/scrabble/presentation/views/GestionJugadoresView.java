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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import scrabble.MainApplication;
import scrabble.presentation.viewControllers.ControladorJugadoresView;

/**
 * Vista para la gestión de jugadores
 */
public class GestionJugadoresView {
    private final ControladorJugadoresView controlador;
    private Parent view;
    
    // Componentes FXML
    private Button btnCrearJugador;
    private Button btnVerHistorial;
    private Button btnEliminarJugador;
    private Button btnVolver;
    private TextField txtBuscar;
    private Button btnBuscar;
    private Button btnRestablecer;    
    private TableView<JugadorRow> tablaJugadores;
    
    public GestionJugadoresView(ControladorJugadoresView controlador) {
        this.controlador = controlador;
        cargarVista();
    }
    
    private void cargarVista() {
        try {
            // Cargar FXML SIN controlador
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("/views/gestion-jugadores-view.fxml"));
            view = loader.load();
            
            // Cargar CSS
            try {
                String cssResource = "/styles/button.css";
                URL cssUrl = getClass().getResource(cssResource);
                if (cssUrl != null) {
                    view.getStylesheets().add(cssUrl.toExternalForm());
                    System.out.println("CSS cargado en GestionJugadoresView");
                }
                else {
                    System.err.println("No se pudo encontrar el recurso CSS: " + cssResource);
                }
            } catch (Exception e) {
                System.err.println("Error al cargar CSS en GestionJugadoresView: " + e.getMessage());
            }
            
            // Obtener referencias a los componentes
            btnCrearJugador = (Button) view.lookup("#btnCrearJugador");
            btnVerHistorial = (Button) view.lookup("#btnVerHistorial");
            btnEliminarJugador = (Button) view.lookup("#btnEliminarJugador");
            btnVolver = (Button) view.lookup("#btnVolver");
            txtBuscar = (TextField) view.lookup("#txtBuscar");
            btnBuscar = (Button) view.lookup("#btnBuscar");
            btnRestablecer = (Button) view.lookup("#btnRestablecer");            
            tablaJugadores = (TableView<JugadorRow>) view.lookup("#tablaJugadores");
            
            aplicarEstiloBotones();
            configurarTabla();
            configurarEventos();
            cargarJugadores();
            
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo cargar la vista de gestión de jugadores");
        }
    }
    
    /**
     * Aplicar clases CSS a los botones
     */
    private void aplicarEstiloBotones() {
        if (btnCrearJugador != null) {
            btnCrearJugador.getStyleClass().addAll("btn-effect", "btn-primary");
        }
        
        if (btnVerHistorial != null) {
            btnVerHistorial.getStyleClass().addAll("btn-effect", "btn-primary");
        }
        
        if (btnEliminarJugador != null) {
            btnEliminarJugador.getStyleClass().addAll("btn-effect", "btn-danger");
        }
        
        if (btnVolver != null) {
            btnVolver.getStyleClass().add("btn-effect");
        }
        
        if (btnBuscar != null) {
            btnBuscar.getStyleClass().addAll("btn-effect", "btn-primary");
        }

        if (btnRestablecer != null) {
            btnRestablecer.getStyleClass().add("btn-effect");
        }        
    }
    
    /**
    * Configura las columnas de la tabla
    */
    private void configurarTabla() {
        if (tablaJugadores != null) {
            tablaJugadores.getColumns().clear();
            
            tablaJugadores.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
            
            tablaJugadores.setMinWidth(100); 
            tablaJugadores.setPrefWidth(1000);
            tablaJugadores.setMaxWidth(Double.MAX_VALUE);

            TableColumn<JugadorRow, String> colNombre = new TableColumn<>("Nombre");
            colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            
            TableColumn<JugadorRow, String> colTipo = new TableColumn<>("Tipo");
            colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
            
            TableColumn<JugadorRow, String> colEnPartida = new TableColumn<>("En partida");
            colEnPartida.setCellValueFactory(new PropertyValueFactory<>("enPartida"));
            
            TableColumn<JugadorRow, String> colNombrePartida = new TableColumn<>("Nombre partida");
            colNombrePartida.setCellValueFactory(new PropertyValueFactory<>("nombrePartida"));
            
            TableColumn<JugadorRow, Integer> colPuntuacion = new TableColumn<>("Puntuación");
            colPuntuacion.setCellValueFactory(new PropertyValueFactory<>("puntuacion"));
            
            tablaJugadores.getColumns().addAll(colNombre, colTipo, colEnPartida, colNombrePartida, colPuntuacion);
            tablaJugadores.getStyleClass().add("modern-table");
            
            colNombre.setMaxWidth(1f * Integer.MAX_VALUE * 20); 
            colTipo.setMaxWidth(1f * Integer.MAX_VALUE * 15);   
            colEnPartida.setMaxWidth(1f * Integer.MAX_VALUE * 15); 
            colNombrePartida.setMaxWidth(1f * Integer.MAX_VALUE * 30);
            colPuntuacion.setMaxWidth(1f * Integer.MAX_VALUE * 20); 
            
            tablaJugadores.setPlaceholder(new javafx.scene.control.Label("No hay jugadores disponibles"));
            
            cargarCSSTablas();
            
            tablaJugadores.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                boolean haySeleccion = newSelection != null;
                if (btnVerHistorial != null) btnVerHistorial.setDisable(!haySeleccion);
                if (btnEliminarJugador != null) btnEliminarJugador.setDisable(!haySeleccion);
            });
        }
    }

    /**
    * Método para cargar el CSS específico para tablas
    */
    private void cargarCSSTablas() {
        try {
            String tableCssResource = "/styles/table.css";
            URL tableCssUrl = getClass().getResource(tableCssResource);
            if (tableCssUrl != null) {
                // Verificar si ya está cargado
                boolean yaEstaCargado = false;
                for (String stylesheet : view.getStylesheets()) {
                    if (stylesheet.contains("table.css")) {
                        yaEstaCargado = true;
                        break;
                    }
                }
                
                if (!yaEstaCargado) {
                    view.getStylesheets().add(tableCssUrl.toExternalForm());
                    System.out.println("CSS de tablas cargado correctamente");
                }
            } else {
                System.err.println("No se pudo encontrar el recurso CSS: " + tableCssResource);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar CSS para tablas: " + e.getMessage());
        }
    }
    /**
     * Configura los eventos de los componentes
     */
    private void configurarEventos() {
        if (btnCrearJugador != null) {
            btnCrearJugador.setOnAction(e -> controlador.mostrarVistaCrearJugador());
        }
        
        if (btnVerHistorial != null) {
            btnVerHistorial.setOnAction(e -> {
                JugadorRow seleccionado = tablaJugadores.getSelectionModel().getSelectedItem();
                if (seleccionado != null) {
                    controlador.mostrarVistaHistorialJugador(seleccionado.getNombre());
                } else {
                    mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Por favor, seleccione un jugador");
                }
            });
        }
        
        if (btnEliminarJugador != null) {
            btnEliminarJugador.setOnAction(e -> {
                JugadorRow seleccionado = tablaJugadores.getSelectionModel().getSelectedItem();
                if (seleccionado != null) {
                    controlador.eliminarJugador(seleccionado.getNombre());
                } else {
                    mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Por favor, seleccione un jugador");
                }
            });
        }
        
        if (btnVolver != null) {
            btnVolver.setOnAction(e -> controlador.volverAMenuPrincipal());
        }
        
        if (btnBuscar != null) {
            btnBuscar.setOnAction(e -> {
                String patron = txtBuscar.getText();
                buscarJugadores(patron);
            });
        }

        if (btnRestablecer != null) {
            btnRestablecer.setOnAction(e -> {
                txtBuscar.clear(); 
                cargarJugadores(); 
            });
        }        

        if (txtBuscar != null) {
            txtBuscar.setOnAction(e -> {
                String patron = txtBuscar.getText();
                buscarJugadores(patron);
            });
        }        
    }
    
    /**
     * Carga todos los jugadores en la tabla
     */
    private void cargarJugadores() {
        if (tablaJugadores != null) {
            List<String> jugadores = controlador.getJugadores();            
            ObservableList<JugadorRow> datos = FXCollections.observableArrayList();
            
            // Generar filas para cada jugador
            for (String jugador : jugadores) {
                String tipo = controlador.esIA(jugador) ? "Bot" : "Humano";
                boolean isPlaying = controlador.isEnPartida(jugador);
                String playing = isPlaying ? "Sí" : "No";
                String nombrePartida = isPlaying ? (controlador.getNombrePartidaActual(jugador)) : "-";
                int total = controlador.getPuntuacionTotal(jugador); 
                datos.add(new JugadorRow(jugador, tipo, playing, nombrePartida, total));
            }
            
            // Asignar datos a la tabla
            tablaJugadores.setItems(datos);
            
            // Seleccionar primer elemento si hay datos
            if (!datos.isEmpty()) {
                tablaJugadores.getSelectionModel().select(0);
            }
        }
    }
    
    /**
     * Busca jugadores según el patrón especificado
     */
    private void buscarJugadores(String patron) {
        if (tablaJugadores != null) {
            List<String> jugadoresFiltrados = controlador.buscarJugadores(patron);
            
            ObservableList<JugadorRow> datos = FXCollections.observableArrayList();
            
            // Generar filas para cada jugador
            for (String jugador : jugadoresFiltrados) {
                String tipo = jugador.equals("jiahao") ? "Sb" : "Humano";
                datos.add(new JugadorRow(jugador, tipo, "No", "-", 0));
            }
            
            tablaJugadores.setItems(datos);
        }
    }
    
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    public Parent getView() {
        return view;
    }
    
    /**
     * Clase para representar una fila en la tabla de jugadores
     */
    public static class JugadorRow {
        private String nombre;
        private String tipo;
        private String enPartida;
        private String nombrePartida;
        private int puntuacion;
        
        public JugadorRow(String nombre, String tipo, String enPartida, String nombrePartida, int puntuacion) {
            this.nombre = nombre;
            this.tipo = tipo;
            this.enPartida = enPartida;
            this.nombrePartida = nombrePartida;
            this.puntuacion = puntuacion;
        }
        
        // Getters y setters
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        
        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }
        
        public String getEnPartida() { return enPartida; }
        public void setEnPartida(String enPartida) { this.enPartida = enPartida; }
        
        public String getNombrePartida() { return nombrePartida; }
        public void setNombrePartida(String nombrePartida) { this.nombrePartida = nombrePartida; }
        
        public int getPuntuacion() { return puntuacion; }
        public void setPuntuacion(int puntuacion) { this.puntuacion = puntuacion; }
    }
}