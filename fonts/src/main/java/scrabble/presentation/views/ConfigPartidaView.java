package scrabble.presentation.views;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import scrabble.MainApplication;
import scrabble.presentation.viewControllers.ControladorPartidaView;

/**
 * Vista para configuración de partida
 */
public class ConfigPartidaView {
    private final ControladorPartidaView ctrlPartida;
    private Parent view;
    
    // Componentes FXML
    private ComboBox<String> cbDiccionario;
    private TextField txtTamanoTablero;
    private Label lblErrorTablero;
    private CheckBox chkPenalizacion;
    private Button btnIniciarPartida;
    private Button btnVolver;
    private FlowPane jugadoresDisponibles;
    private FlowPane jugadoresSeleccionados;
    private Button btnAgregarJugador;
    private Button btnQuitarJugador;

    private List<String> jugadoresReady;

    // Player button on focus 
    private Button jugadorSeleccionado = null;
    public ConfigPartidaView(ControladorPartidaView ctrlPartida) {
        this.ctrlPartida = ctrlPartida;
        this.jugadoresReady = new ArrayList<>();
        cargarVista();
    }
    
    private void cargarVista() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("/views/config-partida-view.fxml"));
            view = loader.load();
            
            try {
                String cssResource = "/styles/button.css";
                URL cssUrl = getClass().getResource(cssResource);
                if (cssUrl != null) {
                    String cssExternalForm = cssUrl.toExternalForm();
                    view.getStylesheets().add(cssExternalForm);
                    System.out.println("CSS cargado con éxito desde: " + cssExternalForm);
                } else {
                    System.err.println("No se pudo encontrar el recurso CSS: " + cssResource);
                }
            } catch (Exception e) {
                System.err.println("Error al cargar el CSS: " + e.getMessage());
                e.printStackTrace();
            }           
            
            // Obtener referencias a los componentes
            cbDiccionario = (ComboBox<String>) view.lookup("#cbDiccionario");
            txtTamanoTablero = (TextField) view.lookup("#txtTamanoTablero");
            lblErrorTablero = (Label) view.lookup("#lblErrorTablero");
            chkPenalizacion = (CheckBox) view.lookup("#chkPenalizacion");
            btnIniciarPartida = (Button) view.lookup("#btnIniciarPartida");
            btnVolver = (Button) view.lookup("#btnVolver");
            btnAgregarJugador = (Button) view.lookup("#btnAgregarJugador");
            btnQuitarJugador = (Button) view.lookup("#btnQuitarJugador");
            
            // Los FlowPane están dentro de ScrollPane, necesitamos buscarlos diferente
            jugadoresDisponibles = buscarFlowPaneEnScrollPane(view, "jugadoresDisponibles");
            jugadoresSeleccionados = buscarFlowPaneEnScrollPane(view, "jugadoresSeleccionados");
            
            configurarFlowPane(jugadoresDisponibles);
            configurarFlowPane(jugadoresSeleccionados);            
            // Debug para verificar
            System.out.println("DEBUG: jugadoresDisponibles = " + (jugadoresDisponibles != null ? "OK" : "NULL"));
            System.out.println("DEBUG: jugadoresSeleccionados = " + (jugadoresSeleccionados != null ? "OK" : "NULL"));
            
            String css = """
                .jugador-button {
                    -fx-background-color: #dbecf2;
                    -fx-text-fill: #333333;
                    -fx-border-radius: 10;
                    -fx-background-radius: 10;
                    -fx-padding: 5 10;
                    -fx-min-width: 80px;
                    -fx-min-height: 35px;
                    -fx-font-size: 14px;
                    -fx-font-weight: normal;
                    -fx-cursor: hand;
                }
                
                .jugador-button:hover {
                    -fx-background-color: #c8e6ec;
                    -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 5, 0, 0, 2);
                }
                
                .jugador-button:pressed {
                    -fx-background-color: #b8dce8;
                }
                
                .jugador-button.selected {
                    -fx-background-color: #a8d2e4;
                    -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 6, 0, 0, 3);
                    -fx-border-color: #4a8fa7;
                    -fx-border-width: 2;
                }
                
                .jugador-button.selected:hover {
                    -fx-background-color: #98c8da;
                }
                
                
                .btn-agregar {
                    -fx-cursor: hand;
                }
                
                .btn-quitar {
                    -fx-cursor: hand;
                }
            """;
            
            Platform.runLater(() -> {
                if (view.getScene() != null) {
                    view.getScene().getStylesheets().add("data:text/css;charset=utf-8," + 
                        css.replace("\n", "").replace(" ", "%20"));
                }
            });      

            // Configurar eventos y datos
            configurarComponentes();
            configurarEventos();
            cargarDatosActuales();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudo cargar la vista de configuración");
        }
    }
    
    /**
    * Método para resetear todos los valores de la pantalla a sus valores por defecto
    */
    public void reset() {
        if (cbDiccionario != null) {
            List<String> allDiccionarios = ctrlPartida.getAllDiccionarios();
            if (allDiccionarios.isEmpty()) {
                cbDiccionario.setValue("-- No hay diccionarios disponibles --");
            
            }
            else cbDiccionario.setValue(allDiccionarios.get(0));
        }
        
        if (txtTamanoTablero != null) {
            txtTamanoTablero.setText("15");
        }
        
        if (jugadoresSeleccionados != null && jugadoresDisponibles != null) {
            List<Node> nodosSeleccionados = new ArrayList<>(jugadoresSeleccionados.getChildren());
            for (Node nodo : nodosSeleccionados) {
                if (nodo instanceof Button) {
                    Button btnJugador = (Button) nodo;
                    jugadoresSeleccionados.getChildren().remove(btnJugador);
                    jugadoresDisponibles.getChildren().add(btnJugador);
                }
            }
            
            jugadoresSeleccionados.layout();
            jugadoresDisponibles.layout();
        }

        jugadorSeleccionado = null;        

        if (lblErrorTablero != null) {
            lblErrorTablero.setVisible(false);
        }

        ctrlPartida.resetJugadores();
        System.out.println("Pantalla de configuración reiniciada");
    }

public void setJugadoresReady(List<String> jugadores) {
    if (jugadores == null) {
        System.err.println("No hay jugadores disponibles!");
        return;
    }
    
    for (String nombre : jugadores) {
        System.err.println("DESDE CONFIG PARTIDA: " + nombre);
    }
    
    this.jugadoresReady = jugadores;
    
    // Actualizar la UI con los jugadores
    actualizarJugadoresDisponibles();
}

/**
 * Actualiza la UI con los jugadores disponibles
 */
private void actualizarJugadoresDisponibles() {
    if (jugadoresDisponibles != null && jugadoresReady != null) {
        // Primero limpiar los jugadores actuales
        jugadoresDisponibles.getChildren().clear();
        
        // Luego crear botones para cada jugador
        for (String nombre : jugadoresReady) {
            System.err.println("CREANDO BOTÓN PARA: " + nombre);
            crearBotónJugador(jugadoresDisponibles, nombre, "#dbecf2");
        }
        
        // Forzar actualización del layout
        jugadoresDisponibles.layout();
    }
}
    private void configurarFlowPane(FlowPane flowPane) {
        if (flowPane != null) {
            flowPane.setPrefWrapLength(240);
            flowPane.setHgap(12);
            flowPane.setVgap(12);
            flowPane.setAlignment(javafx.geometry.Pos.CENTER);
            flowPane.setPadding(new javafx.geometry.Insets(15, 15, 15, 15));
            flowPane.setColumnHalignment(javafx.geometry.HPos.CENTER);   
            flowPane.requestLayout();
        }
    }

    // Método para buscar FlowPane dentro de ScrollPane
    private FlowPane buscarFlowPaneEnScrollPane(Node node, String id) {
        if (node instanceof ScrollPane) {
            ScrollPane scrollPane = (ScrollPane) node;
            Node content = scrollPane.getContent();
            if (content != null && content.getId() != null && content.getId().equals(id)) {
                if (content instanceof FlowPane) {
                    return (FlowPane) content;
                }
            }
        }
        
        if (node instanceof Parent) {
            Parent parent = (Parent) node;
            for (Node child : parent.getChildrenUnmodifiable()) {
                FlowPane result = buscarFlowPaneEnScrollPane(child, id);
                if (result != null) {
                    return result;
                }
            }
        }
        
        return null;
    }
    
    private void configurarComponentes() {
     
        if (cbDiccionario != null && txtTamanoTablero != null) {
            Platform.runLater(() -> {
                List<String> diccionarios = ctrlPartida.getAllDiccionarios();
                for (String d : diccionarios) {
                    cbDiccionario.getItems().add(d);
                }
                cbDiccionario.setValue(diccionarios.get(0));

                txtTamanoTablero.addEventFilter(KeyEvent.KEY_TYPED, event -> {
                        if (!event.getCharacter().matches("[0-9]")) {
                            event.consume();
                        }
                });
            
                txtTamanoTablero.setText(ctrlPartida.getSize().toString());
                txtTamanoTablero.textProperty().addListener((observable, oldValue, newValue) -> {
                    validarTamanoTablero(newValue);
                });
                    
                // Event Listeners para focus y enter
                txtTamanoTablero.setOnAction(e -> confirmarTamanoTablero());            
                txtTamanoTablero.focusedProperty().addListener((observable, oldValue, newValue) -> {
                    // Si pierde el foco (newValue = false)
                    if (!newValue) {
                        confirmarTamanoTablero();
                    }
                });
                
                // Desseleccioanarr input box tamaño de tablero
                view.setOnMouseClicked(e -> {
                    if (txtTamanoTablero.isFocused() && e.getTarget() != txtTamanoTablero) {
                        confirmarTamanoTablero();
                        view.requestFocus();
                    }
                });            
            });

        }
    }
    
    private boolean validarTamanoTablero(String valor) {
        if (lblErrorTablero == null) return true;
        
        try {
            int tamano = Integer.parseInt(valor);
            if (tamano < 15) {
                lblErrorTablero.setText("El tamaño mínimo del tablero es 15");
                lblErrorTablero.setVisible(true);
                return false;
            } else {
                lblErrorTablero.setVisible(false);
                return true;
            }
        } catch (NumberFormatException e) {
            if (valor.isEmpty()) {
                lblErrorTablero.setText("Debes introducir un tamaño");
            } else {
                lblErrorTablero.setText("Por favor, introduce un número válido");
            }
            lblErrorTablero.setVisible(true);
            return false;
        }
    }
    
    /*
     * Confirma el tamaño del tablero y lo guarda en el controladorPartida
     */ 
    private void confirmarTamanoTablero() {
        if (txtTamanoTablero != null && !txtTamanoTablero.getText().isEmpty()) {
            boolean esValido = validarTamanoTablero(txtTamanoTablero.getText());
            try {
                // Siempre guardamos el valor, incluso si no es válido
                // para que el controlador tenga el valor actual
                Integer size = Integer.parseInt(txtTamanoTablero.getText());
                ctrlPartida.setSize(size);
                view.requestFocus();
                
                if (esValido) {
                    System.out.println("Tamaño de tablero confirmado y válido: " + size);
                } else {
                    System.out.println("Tamaño de tablero guardado pero NO válido: " + size);
                }
            } catch (NumberFormatException e) {
                System.err.println("Error al convertir el tamaño del tablero: " + e.getMessage());
            }
        }
    }

    private void configurarEventos() {
        if (btnIniciarPartida != null) {
            btnIniciarPartida.setOnAction(e -> {
                confirmarTamanoTablero();
                guardarConfiguracion();
                ctrlPartida.iniciarPartida();
                reset();                    
                
            });
        }
        
        if (btnVolver != null) {
            btnVolver.setOnAction(e -> ctrlPartida.volver());
        }
        
        if (btnAgregarJugador != null) {
            btnAgregarJugador.setOnAction(e -> agregarJugadorSeleccionado());
            btnAgregarJugador.getStyleClass().add("btn-agregar");
        }
        
        if (btnQuitarJugador != null) {
            btnQuitarJugador.setOnAction(e -> quitarJugadorSeleccionado());
            btnQuitarJugador.getStyleClass().add("btn-quitar");
        }
    }
    
    private void cargarDatosActuales() {
        if (txtTamanoTablero != null) {
  
            Integer tamanoActual = ctrlPartida.getSize();
            if (tamanoActual != null) {
                    try {
                        txtTamanoTablero.setText(String.valueOf(tamanoActual));
                    } catch (NumberFormatException e) {
                        txtTamanoTablero.setText("15"); 
                    }

            } else {
                txtTamanoTablero.setText("15"); 
            }
        }
    }
    
    private void guardarConfiguracion() {
        if (cbDiccionario != null && cbDiccionario.getValue() != null) {
            ctrlPartida.setDiccionario(cbDiccionario.getValue());
        }
        
        if (txtTamanoTablero != null && !txtTamanoTablero.getText().isEmpty()) {
            try {
                Integer size = Integer.parseInt(txtTamanoTablero.getText());
                if (size == null) {
                    System.err.println("size is null");                
                    System.exit(0);
                }
                ctrlPartida.setSize(size);

            } catch (NumberFormatException e) {
                System.err.println("Error al convertir el tamaño del tablero: " + e.getMessage());
            }
        }
        
        // Guardar jugadores seleccionados
        if (jugadoresSeleccionados != null) {
            ctrlPartida.getJugadoresDisponibles().clear();
            for (javafx.scene.Node node : jugadoresSeleccionados.getChildren()) {
                if (node instanceof Button) {
                    ctrlPartida.addJugador(((Button) node).getText());
                }
            }
        }
    }
    
    private void crearBotónJugador(FlowPane contenedor, String nombre, String color) {
        Button btnJugador = new Button(nombre);
        btnJugador.getStyleClass().add("jugador-button");
        btnJugador.setOnAction(e -> seleccionarJugador(btnJugador));
        contenedor.getChildren().add(btnJugador);
    }

    private void seleccionarJugador(Button boton) {
        // Si ya hay un botón seleccionado, deseleccionarlo
        if (jugadorSeleccionado != null) {
            jugadorSeleccionado.getStyleClass().remove("selected");
        }
        
        // Si se hace clic en el mismo botón, deseleccionar
        if (jugadorSeleccionado == boton) {
            jugadorSeleccionado = null;
        } else {
            // Seleccionar el nuevo botón
            jugadorSeleccionado = boton;
            boton.getStyleClass().add("selected");
        }
    }
    
    private void agregarJugadorSeleccionado() {
        if (jugadorSeleccionado != null && jugadoresDisponibles != null && jugadoresSeleccionados != null) {
            // Verificar que el jugador está en disponibles
            if (jugadoresDisponibles.getChildren().contains(jugadorSeleccionado)) {
                // Mover el jugador seleccionado
                jugadoresDisponibles.getChildren().remove(jugadorSeleccionado);
                jugadoresSeleccionados.getChildren().add(jugadorSeleccionado);
                
                // Forzar layout refresh
                jugadoresDisponibles.layout();
                jugadoresSeleccionados.layout();
                
                // Limpiar selección
                jugadorSeleccionado.getStyleClass().remove("selected");
                jugadorSeleccionado = null;
            }
        }
    }

    private void quitarJugadorSeleccionado() {
        if (jugadorSeleccionado != null && jugadoresDisponibles != null && jugadoresSeleccionados != null) {
            // Verificar que el jugador está en seleccionados
            if (jugadoresSeleccionados.getChildren().contains(jugadorSeleccionado)) {
                // Mover el jugador seleccionado
                jugadoresSeleccionados.getChildren().remove(jugadorSeleccionado);
                jugadoresDisponibles.getChildren().add(jugadorSeleccionado);
                
                // Forzar layout refresh
                jugadoresDisponibles.layout();
                jugadoresSeleccionados.layout();
                
                // Limpiar selección
                jugadorSeleccionado.getStyleClass().remove("selected");
                jugadorSeleccionado = null;
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