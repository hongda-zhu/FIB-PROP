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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import scrabble.MainApplication;
import scrabble.excepciones.ExceptionPersistenciaFallida;
import scrabble.presentation.viewControllers.ControladorPartidaView;




/**
 * Vista para la configuración inicial de una partida de Scrabble.
 * Permite seleccionar jugadores, configurar el tamaño del tablero,
 * elegir el diccionario y establecer otros parámetros antes de
 * iniciar una nueva partida.
 * 
 * La vista gestiona la selección de jugadores disponibles mediante
 * una interfaz visual interactiva, valida los parámetros de
 * configuración en tiempo real y se comunica con el controlador
 * para inicializar la partida con la configuración especificada.
 * 
 * Características principales:
 * - Selección visual de jugadores disponibles con interfaz drag-and-drop
 * - Validación en tiempo real del tamaño de tablero
 * - Configuración de diccionario desde lista disponible
 * - Reseteo de configuración a valores por defecto
 * - Integración completa con el sistema de gestión de jugadores
 * - Interfaz responsiva que se adapta al tamaño de ventana
 * - Aplicación automática de estilos CSS para coherencia visual
 * 
 * La vista utiliza el patrón MVC, comunicándose con ControladorPartidaView
 * para todas las operaciones de configuración y validación de datos.
 * Implementa validación de entrada en tiempo real para mejorar la
 * experiencia del usuario y prevenir errores de configuración.
 * 
 * @version 1.0
 * @since 1.0
 */
public class ConfigPartidaView {
    private final ControladorPartidaView ctrlPartida;
    private Parent view;
    
    // Componentes FXML
    private ComboBox<String> cbDiccionario;
    private TextField txtTamanoTablero;
    private Label lblErrorTablero;
    private Button btnIniciarPartida;
    private Button btnVolver;
    private FlowPane jugadoresDisponibles;
    private FlowPane jugadoresSeleccionados;
    private Button btnAgregarJugador;
    private Button btnQuitarJugador;
    private VBox root;
    private List<String> jugadoresReady;
    private BorderPane main;
    private VBox contenedorContenido;

    // Player button on focus 
    private Button jugadorSeleccionado = null;



    /**
     * Constructor que inicializa la vista de configuración de partida.
     * Carga el archivo FXML y configura todos los componentes necesarios
     * para la configuración de una nueva partida.
     * 
     * @pre ctrlPartida no debe ser null y debe estar correctamente inicializado.
     * @param ctrlPartida Controlador que maneja la lógica de configuración
     * @post Se crea una nueva instancia con todos los componentes configurados
     *       y listos para la configuración de partida.
     * @throws NullPointerException si ctrlPartida es null
     */    
    public ConfigPartidaView(ControladorPartidaView ctrlPartida) {
        this.ctrlPartida = ctrlPartida;
        this.jugadoresReady = new ArrayList<>();
        cargarVista();
    }
    


    /**
     * Carga la vista FXML de configuración de partida.
     * Inicializa el archivo FXML, aplica estilos CSS, configura componentes
     * y establece los manejadores de eventos necesarios.
     * 
     * @pre El archivo FXML debe existir en la ruta especificada.
     * @post La vista FXML se carga correctamente con todos los estilos aplicados,
     *       componentes configurados y eventos establecidos.
     * @throws IOException si hay errores al cargar el archivo FXML
     */    
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
                }
            } catch (Exception e) {
                ctrlPartida.mostrarAlerta("error", "Error inesperado", "No se ha podido cargar la vista");
                e.printStackTrace();
            }           
            
            // Obtener referencias a los componentes
            root = (VBox) view.lookup("#root");
            cbDiccionario = (ComboBox<String>) view.lookup("#cbDiccionario");
            txtTamanoTablero = (TextField) view.lookup("#txtTamanoTablero");
            lblErrorTablero = (Label) view.lookup("#lblErrorTablero");
            btnIniciarPartida = (Button) view.lookup("#btnIniciarPartida");
            btnVolver = (Button) view.lookup("#btnVolver");
            btnAgregarJugador = (Button) view.lookup("#btnAgregarJugador");
            btnQuitarJugador = (Button) view.lookup("#btnQuitarJugador");
            main = (BorderPane) view.lookup("#main");
            contenedorContenido = (VBox) view.lookup("#contenedorContenido");
            
            // Los FlowPane están dentro de ScrollPane, necesitamos buscarlos diferente
            jugadoresDisponibles = buscarFlowPaneEnScrollPane(view, "jugadoresDisponibles");
            jugadoresSeleccionados = buscarFlowPaneEnScrollPane(view, "jugadoresSeleccionados");
            
            configurarFlowPane(jugadoresDisponibles);
            configurarFlowPane(jugadoresSeleccionados);            
            
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
                    }
                } 
            } catch (Exception e) {
                ctrlPartida.mostrarAlerta("error", "Error inesperado", "No se ha podido cargar la vista");
            }

            btnIniciarPartida.getStyleClass().addAll("btn-effect", "btn-success");
            btnVolver.getStyleClass().addAll("btn-effect", "btn-secondary");       
            btnAgregarJugador.getStyleClass().addAll("btn-effect", "btn-primary");  
            btnQuitarJugador.getStyleClass().addAll("btn-effect", "btn-danger");              
              

            // Configurar eventos y datos
            configurarComponentes();
            configurarEventos();
            cargarDatosActuales();
            aplicarTema();
            cambiarColorTextoPorTema(main, ctrlPartida.getTema());

            Platform.runLater(() -> {
                root.widthProperty().addListener((obs, oldVal, newVal) -> {
                    root.setPrefWidth(newVal.doubleValue()*0.8);
                });

                root.heightProperty().addListener((obs, oldVal, newVal) -> {
                    root.setPrefHeight(newVal.doubleValue()*0.75);
                });              
            });
          
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudo cargar la vista de configuración");
        }
    }
    
    /**
     * Aplica el tema visual actual al contenedor principal de la vista.
     * Este método obtiene el tema seleccionado desde el controlador y ajusta
     * el color de fondo del contenedor principal en función de si el tema es
     * "Claro" u "Oscuro".
     * @pre: El controlador debe tener un tema válido ("Claro" u "Oscuro").
     * @post: El color de fondo del contenedor principal se actualiza según el tema seleccionado.
     */

    private void aplicarTema() {
        String tema = ctrlPartida.getTema();
        if (tema.equals("Claro")) {
            main.setStyle("-fx-background-color: #f5f5f5;");
        } else {
            main.setStyle("-fx-background-color: #0b0a2e;");
        }
        aplicarFondoPorTema(contenedorContenido, ctrlPartida.getTema(), "#ffffff", "#1c1747");
    }

    /**
     * Aplica el color de fondo a un contenedor según el tema actual.
     *
     * @param nodo Nodo al que aplicar el estilo.
     * @param tema Tema actual ("Claro" o "Oscuro").
     * @param claroColor Color hexadecimal para el tema claro.
     * @param oscuroColor Color hexadecimal para el tema oscuro.
     */
    private void aplicarFondoPorTema(Parent nodo, String tema, String claroColor, String oscuroColor) {
        String fondo = tema.equals("Oscuro") ? oscuroColor : claroColor;
        // Extrae el estilo actual
        String estiloActual = nodo.getStyle();

        // Quita cualquier fondo previo para evitar duplicación
        estiloActual = estiloActual.replaceAll("-fx-background-color:\\s*[^;]+;", "");

        // Añade el nuevo color conservando el resto
        nodo.setStyle(estiloActual + "-fx-background-color: " + fondo + ";");
    }

    
    /**
     * Cambia el color del texto de todos los nodos Label dentro del nodo raíz dado,
     * según el tema especificado.
     *
     * @param root El nodo raíz (Parent) en el que buscar los Labels.
     * @param tema El tema actual, puede ser "Oscuro" o cualquier otro valor para claro.
     *
     * @pre root no es nulo y contiene nodos Label como descendientes.
     * @pre tema no es nulo.
     * @post El color del texto de todos los Labels dentro de root se establece en blanco si tema es "Oscuro",
     *       o en negro en caso contrario.
     */
    private void cambiarColorTextoPorTema(Parent root, String tema) {
        String colorTexto = tema.equals("Oscuro") ? "white" : "black";

        root.lookupAll(".label").forEach(n -> {
            if (n instanceof Label label) {
                String estiloAnterior = label.getStyle();
                if (estiloAnterior == null) estiloAnterior = "";
                estiloAnterior = estiloAnterior.replaceAll("-fx-text-fill:\\s*[^;]+;", "");
                label.setStyle(estiloAnterior + "-fx-text-fill: " + colorTexto + ";");
            }
        });
    }

    /**
     * Resetea todos los valores de configuración a sus valores por defecto.
     * Limpia las selecciones de jugadores, restaura el tamaño de tablero
     * por defecto, resetea el diccionario seleccionado y oculta mensajes de error.
     * 
     * @pre No hay precondiciones específicas.
     * @post Todos los campos de configuración se establecen a sus valores
     *       por defecto, se limpian las selecciones de jugadores y la interfaz
     *       se actualiza correspondiente. Los jugadores vuelven al estado no seleccionado.
     */
    public void reset() {
        if (cbDiccionario != null) {
            List<String> allDiccionarios = ctrlPartida.getAllDiccionarios();
            if (allDiccionarios.isEmpty()) {
                cbDiccionario.setValue("-- No hay diccionarios disponibles --");
            
            }
            else {
                cbDiccionario.setValue(ctrlPartida.getDiccionarioDefault());
            }
        }
        
        if (txtTamanoTablero != null) {
            txtTamanoTablero.setText(String.valueOf(ctrlPartida.getTamanoDefault()));
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
    }



    /**
     * Establece la lista de jugadores disponibles para la partida.
     * Actualiza la interfaz con los jugadores que pueden ser seleccionados
     * para participar en la nueva partida.
     * 
     * @pre jugadores no debe ser null.
     * @param jugadores Lista de nombres de jugadores disponibles
     * @post La lista interna se actualiza y la interfaz se refresca mostrando
     *       todos los jugadores disponibles en el panel correspondiente.
     * @throws NullPointerException si jugadores es null
     */
    public void setJugadoresReady(List<String> jugadores) {
        this.jugadoresReady = jugadores;    
        actualizarJugadoresDisponibles();
    }




    /**
     * Actualiza la interfaz de usuario con los jugadores disponibles.
     * Limpia la visualización anterior y crea botones interactivos
     * para cada jugador disponible en el sistema.
     * 
     * @pre jugadoresReady debe estar inicializada.
     * @post El panel de jugadores disponibles se actualiza con botones
     *       interactivos para cada jugador. Se fuerza la actualización
     *       del layout para reflejar los cambios.
     */
    private void actualizarJugadoresDisponibles() {
        if (jugadoresDisponibles != null && jugadoresReady != null) {
            // Primero limpiar los jugadores actuales
            jugadoresDisponibles.getChildren().clear();
            
            // Luego crear botones para cada jugador
            for (String nombre : jugadoresReady) {
                crearBotónJugador(jugadoresDisponibles, nombre, "#dbecf2");
            }
            
            // Forzar actualización del layout
            jugadoresDisponibles.layout();
        }
    }



    /**
     * Configura las propiedades visuales y de comportamiento de un FlowPane.
     * Establece espaciado, alineación, padding y otras propiedades
     * para mantener consistencia visual en los paneles de jugadores.
     * 
     * @pre flowPane puede ser null (se maneja internamente).
     * @param flowPane Panel a configurar
     * @post Si flowPane no es null, se configuran todas sus propiedades
     *       visuales y se solicita actualización del layout.
     */    
    private void configurarFlowPane(FlowPane flowPane) {
        if (flowPane != null) {
            flowPane.setPrefWrapLength(400);
            flowPane.setHgap(12);
            flowPane.setVgap(12);
            // flowPane.setAlignment(javafx.geometry.Pos.CENTER);
            flowPane.setPadding(new javafx.geometry.Insets(15, 15, 15, 30));
            // flowPane.setColumnHalignment(javafx.geometry.HPos.CENTER);   
            flowPane.requestLayout();
        }
    }



    /**
     * Busca un FlowPane específico dentro de la jerarquía de un ScrollPane.
     * Utiliza búsqueda recursiva para localizar FlowPanes anidados
     * dentro de ScrollPanes por su identificador.
     * 
     * @pre node no debe ser null, id no debe ser null.
     * @param node Nodo raíz donde iniciar la búsqueda
     * @param id Identificador del FlowPane a buscar
     * @return FlowPane encontrado o null si no se encuentra
     * @post Se devuelve la referencia al FlowPane si se encuentra,
     *       o null si no existe en la jerarquía especificada.
     */
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
    


    /**
     * Configura los componentes principales de la interfaz.
     * Inicializa el ComboBox de diccionarios, configura validación
     * del campo de tamaño de tablero y establece listeners de eventos.
     * 
     * @pre Los componentes FXML deben haber sido cargados correctamente.
     * @post El ComboBox se llena con diccionarios disponibles, el campo
     *       de tamaño tiene validación numérica y todos los listeners
     *       de eventos están configurados correctamente.
     */    
    private void configurarComponentes() {
     
        if (cbDiccionario != null && txtTamanoTablero != null) {
            Platform.runLater(() -> {
                List<String> diccionarios = ctrlPartida.getAllDiccionarios();
                for (String d : diccionarios) {
                    cbDiccionario.getItems().add(d);
                }
                cbDiccionario.setValue(ctrlPartida.getDiccionarioDefault());

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
    


    /**
     * Valida el valor introducido para el tamaño del tablero.
     * Verifica que el valor sea un número válido y cumpla con
     * los requisitos mínimos del juego.
     * 
     * @pre valor no debe ser null.
     * @param valor Cadena de texto a validar como tamaño de tablero
     * @return true si el valor es válido, false en caso contrario
     * @post Si el valor es válido, se oculta el mensaje de error.
     *       Si es inválido, se muestra el mensaje de error correspondiente
     *       y se hace visible el label de error.
     */    
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
    


    /**
     * Confirma y guarda el tamaño del tablero en el controlador.
     * Valida el valor actual del campo y lo almacena en el controlador
     * para su uso posterior, independientemente de si es válido.
     * 
     * @pre txtTamanoTablero debe estar inicializado.
     * @post El valor del campo se valida y se guarda en el controlador.
     *       Se transfiere el foco fuera del campo de texto.
     * @throws NumberFormatException si el valor no es un número válido
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
            } catch (NumberFormatException e) {
                ctrlPartida.mostrarAlerta("error", "Error inesperado", "Formato de número inesperado");
            }
        }
    }



    /**
     * Configura todos los manejadores de eventos de la interfaz.
     * Establece las acciones para botones de navegación, selección
     * de jugadores y control de la partida.
     * 
     * @pre Todos los botones deben haber sido inicializados.
     * @post Todos los botones tienen configurados sus manejadores
     *       de eventos correspondientes y clases CSS aplicadas.
     */
    private void configurarEventos() {
        if (btnIniciarPartida != null) {
            btnIniciarPartida.setOnAction(e -> {
                confirmarTamanoTablero();
                guardarConfiguracion();
                try {
                    ctrlPartida.iniciarPartida();
                } catch (ExceptionPersistenciaFallida e1) {
                    e1.printStackTrace();
                }
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
    


    /**
     * Carga los datos actuales de configuración en la interfaz.
     * Inicializa los campos con los valores actuales del controlador
     * o con valores por defecto si no hay configuración previa.
     * 
     * @pre El controlador debe estar inicializado.
     * @post El campo de tamaño de tablero muestra el valor actual
     *       o el valor por defecto si no hay configuración previa.
     */    
    private void cargarDatosActuales() {
        if (txtTamanoTablero != null) {
  
            Integer tamanoActual = ctrlPartida.getSize();
            if (tamanoActual != null) {
                    try {
                        txtTamanoTablero.setText(String.valueOf(tamanoActual));
                    } catch (NumberFormatException e) {
                        txtTamanoTablero.setText(String.valueOf(ctrlPartida.getTamanoDefault())); 
                    }

            } else {
                txtTamanoTablero.setText(String.valueOf(ctrlPartida.getTamanoDefault())); 
            }
        }
    }
    


    /**
     * Guarda toda la configuración actual en el controlador.
     * Recopila los valores de todos los campos de configuración
     * y los transfiere al controlador para su procesamiento.
     * 
     * @pre Todos los componentes de configuración deben estar inicializados.
     * @post La configuración de diccionario, tamaño de tablero y jugadores
     *       seleccionados se guarda en el controlador. Se limpia la lista
     *       de jugadores disponibles y se añaden los seleccionados.
     * @throws NumberFormatException si el tamaño de tablero no es válido
     */    
    private void guardarConfiguracion() {
        if (cbDiccionario != null && cbDiccionario.getValue() != null) {
            ctrlPartida.setDiccionario(cbDiccionario.getValue());
        }
        
        if (txtTamanoTablero != null && !txtTamanoTablero.getText().isEmpty()) {
            try {
                Integer size = Integer.parseInt(txtTamanoTablero.getText());
                if (size == null) {
                    System.exit(0);
                }
                ctrlPartida.setSize(size);

            } catch (NumberFormatException e) {
                ctrlPartida.mostrarAlerta("error", "Error inesperado", "Formato de número inesperado");
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
    


    /**
     * Crea un botón interactivo para representar un jugador.
     * Genera un botón con estilo apropiado y configuración de eventos
     * para la selección de jugadores.
     * 
     * @pre contenedor y nombre no deben ser null.
     * @param contenedor FlowPane donde se añadirá el botón
     * @param nombre Nombre del jugador a representar
     * @param color Color de fondo para el botón (no utilizado actualmente)
     * @post Se crea un botón con el nombre del jugador, se aplican
     *       las clases CSS correspondientes y se añade al contenedor.
     */    
    private void crearBotónJugador(FlowPane contenedor, String nombre, String color) {
        Button btnJugador = new Button(nombre);
        btnJugador.getStyleClass().add("jugador-button");
        btnJugador.setOnAction(e -> seleccionarJugador(btnJugador));
        contenedor.getChildren().add(btnJugador);
    }



    /**
     * Maneja la selección visual de un botón de jugador.
     * Gestiona el estado de selección, permitiendo seleccionar/deseleccionar
     * jugadores y manteniendo solo una selección activa a la vez.
     * 
     * @pre boton no debe ser null.
     * @param boton Botón de jugador que se ha clickeado
     * @post Se actualiza el estado de selección: si había otro botón
     *       seleccionado se deselecciona, y se selecciona el nuevo botón.
     *       Si se clickea el mismo botón, se deselecciona.
     */
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



    /**
     * Agrega el jugador seleccionado a la lista de participantes.
     * Mueve el botón del jugador seleccionado desde el panel de
     * disponibles al panel de seleccionados.
     * 
     * @pre Debe haber un jugador seleccionado y los paneles deben estar inicializados.
     * @post Si hay un jugador seleccionado en el panel de disponibles,
     *       se mueve al panel de seleccionados, se actualiza el layout
     *       y se limpia la selección actual.
     */
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



    /**
     * Quita el jugador seleccionado de la lista de participantes.
     * Mueve el botón del jugador seleccionado desde el panel de
     * seleccionados al panel de disponibles.
     * 
     * @pre Debe haber un jugador seleccionado y los paneles deben estar inicializados.
     * @post Si hay un jugador seleccionado en el panel de seleccionados,
     *       se mueve al panel de disponibles, se actualiza el layout
     *       y se limpia la selección actual.
     */
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
    


    /**
     * Muestra una alerta de sistema con el tipo y mensaje especificados.
     * Utiliza el sistema de alertas nativo de JavaFX para mostrar
     * mensajes informativos, de advertencia o de error al usuario.
     * 
     * @pre type, header y content no deben ser null.
     * @param type Tipo de alerta (ERROR, WARNING, INFORMATION, etc.)
     * @param header Texto del encabezado de la alerta
     * @param content Contenido detallado del mensaje
     * @post Se muestra una ventana modal con la alerta especificada
     *       y se espera a que el usuario la cierre.
     */
    private void showAlert(Alert.AlertType type, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(type.toString());
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    


    /**
     * Obtiene la vista cargada de configuración de partida.
     * Proporciona acceso a la jerarquía de componentes JavaFX
     * para su integración en la aplicación principal.
     * 
     * @pre La vista debe haber sido cargada previamente.
     * @return Parent que contiene la vista completa de configuración
     * @post Se devuelve la referencia a la vista cargada sin modificar su estado.
     */    
    public Parent getView() {
        return view;
    }
}