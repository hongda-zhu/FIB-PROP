package scrabble.presentation.views;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import scrabble.MainApplication;
import scrabble.helpers.Direction;
import scrabble.helpers.TipoCasilla;
import scrabble.helpers.Triple;
import scrabble.helpers.Tuple;
import scrabble.presentation.PresentationController;
import scrabble.presentation.componentes.CasillaDisplay;
import scrabble.presentation.componentes.Ficha;
import scrabble.presentation.popups.CambiarFichasPopup;
import scrabble.presentation.popups.ComodinPopup;
import scrabble.presentation.popups.PausaPopup;
import scrabble.presentation.viewControllers.ControladorPartidaView;


/**
 * Vista principal del tablero de juego de Scrabble.
 * Maneja la interfaz de usuario durante una partida activa, incluyendo el tablero,
 * las fichas del jugador, las puntuaciones y el historial de jugadas. Gestiona
 * tanto las interacciones del jugador humano como la visualización de los
 * movimientos de la IA.
 * 
 * La clase coordina múltiples componentes de la interfaz: el tablero con casillas
 * especiales, el rack de fichas del jugador actual, el panel de puntuaciones,
 * el historial de jugadas y los controles de partida. Implementa el flujo
 * completo de una partida desde la configuración inicial hasta la finalización.
 * 
 * Características principales:
 * - Gestión de turnos entre jugadores humanos e IA
 * - Validación de jugadas en tiempo real
 * - Soporte para comodines con selección de letra
 * - Sistema de guardado y carga de partidas
 * - Interfaz adaptable a diferentes tamaños de tablero
 * - Gestión de sonidos para mejorar la experiencia de usuario
 * 
 * La vista utiliza el patrón MVC, comunicándose con ControladorPartidaView
 * para todas las operaciones de lógica de negocio y persistencia.
 * 
 * @version 1.0
 * @since 1.0
 */
public class VistaTablero {
    
    @FXML private BorderPane panelPrincipal;
    @FXML private GridPane tablero;
    @FXML private HBox fichasJugador;
    @FXML private Label jugadorActual;
    @FXML private Button btnConfirmar;
    @FXML private Button btnCancelar;
    @FXML private Button btnPasarTurno;  
    @FXML private VBox puntuacionesContainer; 
    @FXML private VBox historialContainer;
    @FXML private Label lblFichasRestantes;
    @FXML private ScrollPane scrollPane;
    @FXML private VBox filasNumeracion;
    @FXML private HBox columnasNumeracion;
    @FXML private Button btnCambiarFichas;    
    @FXML private Label lblDiccionarioSeleccionado;
    @FXML private VBox panelIzquierdo;
    @FXML private VBox panelCentro;  
    @FXML private VBox panelDerecho;
    @FXML private StackPane tableroContainer;
    @FXML private BorderPane root;    
    @FXML private ScrollPane historialScrollPane;    
    private ControladorPartidaView controlador;
    private Ficha fichaSeleccionada;
    private Parent view;
    private Integer N;
    private boolean saved;
    private List<String> historialJugadas = new ArrayList<>();
    private final int MAX_HISTORIAL_ENTRIES = 20; // Límite de entradas en el historial
    private boolean firstMove;
    // == PARÁMETROS NECESARIOS PARA JUGAR LA PARTIDA == //
    private List<String> jugadores;
    private int jugadorActualIndex = 0;
    private String jugadorActualNombre;
    private Map<String, Integer> jugadoresPuntuaciones;
    private Map<String, Integer> rackActual;
    private  Direction direccionActual;

    // Lista para rastrear las fichas colocadas en el turno actual 
    private List<FichaColocada> fichasColocadasEnTurnoActual = new ArrayList<>();
    
    private Map<Tuple<Integer, Integer>, String> posiciones; 
    // Tamaño fijo para cada casilla
    private final int CASILLA_SIZE = 40;
    private Set<String> alfabeto;

    /**
    * Clase interna que representa una ficha colocada temporalmente.
    * Encapsula la información de una ficha que ha sido colocada
    * en el tablero pero aún no ha sido confirmada.
    * 
    * Mantiene tanto la información original de la ficha como
    * cualquier transformación aplicada (especialmente para comodines).
    * Permite el seguimiento completo del estado de las fichas
    * durante el turno actual.
    * 
    * @version 1.0
    * @since 1.0
    */
    private class FichaColocada {
        private String letra;
        private String letraOriginal; 
        private int puntos;
        private int fila;
        private int columna;
        private boolean esComodin;




        /**
        * Constructor para fichas normales.
        * Crea una representación de ficha colocada con
        * información básica de letra y posición.
        * 
        * @pre letra no debe ser null, fila y columna deben ser válidas.
        * @param letra Letra de la ficha
        * @param puntos Puntos de la ficha
        * @param fila Coordenada de fila donde se colocó
        * @param columna Coordenada de columna donde se colocó
        * @post Se crea una instancia con la información de la ficha colocada.
        */
        public FichaColocada(String letra, int puntos, int fila, int columna) {
            this.letra = letra;
            this.puntos = puntos;
            this.fila = fila;
            this.columna = columna;
            this.esComodin = "#".equals(letra);            
        }


        /**
        * Constructor para fichas con transformaciones (comodines).
        * Crea una representación que mantiene tanto la letra original
        * como la letra convertida para comodines.
        * 
        * @pre Ningún parámetro debe ser null.
        * @param letraOriginal Letra original de la ficha (# para comodines)
        * @param letraConvertida Letra a la que se convirtió el comodín
        * @param puntos Puntos de la ficha
        * @param fila Coordenada de fila donde se colocó
        * @param columna Coordenada de columna donde se colocó
        * @param esComodin Indica si la ficha es un comodín
        * @post Se crea una instancia con información completa de transformación.
        */
        public FichaColocada(String letraOriginal, String letraConvertida, int puntos, int fila, int columna, boolean esComodin) {
            this.letraOriginal = letraOriginal;
            this.letra = letraConvertida;
            this.puntos = puntos;
            this.fila = fila;
            this.columna = columna;
            this.esComodin = esComodin;
        }    
    


        /**
        * Obtiene la letra original de la ficha.
        * Para comodines, devuelve "#"; para fichas normales, devuelve la letra.
        * 
        * @pre La ficha debe estar correctamente inicializada.
        * @return Letra original de la ficha
        * @post Se devuelve la letra original sin modificar el estado de la ficha.
        */
        public String getLetraOriginal() {
            return this.letraOriginal;
        }



        /**
        * Obtiene la letra actual de la ficha (puede ser convertida).
        * Para comodines convertidos, devuelve la letra seleccionada;
        * para fichas normales, devuelve la letra de la ficha.
        * 
        * @pre La ficha debe estar correctamente inicializada.
        * @return Letra actual de la ficha
        * @post Se devuelve la letra actual sin modificar el estado.
        */
        public String getLetra() {
            return this.letra;
        }



        /**
        * Determina si la ficha es un comodín.
        * Verifica si la ficha original era un comodín que
        * ha sido convertido a otra letra.
        * 
        * @pre La ficha debe estar correctamente inicializada.
        * @return true si la ficha es un comodín, false en caso contrario
        * @post Se devuelve el estado de comodín sin modificar la ficha.
        */
        public boolean esComodin() {
            return this.esComodin;
        }



        /**
        * Establece la letra convertida para un comodín.
        * Permite cambiar la letra a la que se ha convertido
        * un comodín, solo si la ficha es efectivamente un comodín.
        * 
        * @pre La ficha debe ser un comodín, nuevaLetra no debe ser null.
        * @param nuevaLetra Nueva letra para el comodín
        * @post Si la ficha es un comodín, se actualiza la letra convertida
        *       con el valor especificado.
        */
        public void setLetraConvertida(String nuevaLetra) {
            if (esComodin) {
                this.letra = nuevaLetra;
            }
        }        
    }


    /**
     * Constructor que inicializa la vista del tablero de juego.
     * Carga el archivo FXML correspondiente y configura todos los componentes
     * necesarios para una partida de Scrabble.
     * 
     * @pre controlador no debe ser null y debe estar correctamente inicializado.
     * @param controlador Controlador que maneja la lógica de la partida
     * @post Se crea una nueva instancia de VistaTablero con todos los componentes
     *       inicializados y listos para comenzar una partida.
     * @throws NullPointerException si controlador es null
     */
    public VistaTablero(ControladorPartidaView controlador) {
        this.controlador = controlador;
        this.N = controlador.getSize();
        this.saved = false;
        this.posiciones = new HashMap<>();
        this.firstMove = true;
        this.alfabeto = controlador.getAllAlphabet().keySet();
        cargarVista();
    }
    

    /**
    * Establece el índice del jugador actual en la partida.
    * Utilizado para mantener la sincronización del turno actual
    * entre diferentes componentes del sistema.
    * 
    * @pre idx debe ser un índice válido dentro del rango de jugadores.
    * @param idx Índice del jugador actual
    * @post Se actualiza el índice del jugador actual con el valor especificado.
    */    
    public void setJugadorActualIndex(int idx) {
        this.jugadorActualIndex = idx;
    }


    /**
    * Establece el tamaño del tablero de juego.
    * Permite configurar tableros de diferentes dimensiones
    * para adaptar la experiencia de juego.
    * 
    * @pre n debe ser un valor positivo mayor o igual a 15.
    * @param n Tamaño del tablero (NxN)
    * @post Se actualiza el tamaño del tablero con el valor especificado.
    */
    public void setTableroSize(Integer n) {
        this.N = n;
    }


    /**
    * Carga la vista FXML del tablero de juego.
    * Inicializa el archivo FXML correspondiente y configura
    * los controladores de eventos básicos.
    * 
    * @pre El archivo FXML debe existir en la ruta especificada.
    * @post La vista FXML se carga correctamente y los componentes
    *       básicos quedan configurados para su uso.
    * @throws IOException si hay errores al cargar el archivo FXML
    */
    private void cargarVista() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("/views/tablero-view.fxml"));
            loader.setController(this); // Establecer esta clase como controlador
            view = loader.load();
            
            // Configurar botón volver manualmente si es necesario
            Button btnVolver = (Button) view.lookup("#btnVolver");
            if (btnVolver != null) {
                btnVolver.setOnAction(e -> controlador.volver());
            }

            aplicarTema();
            cambiarColorTextoPorTema(root, controlador.getTema());

        } catch (IOException e) {
            e.printStackTrace();
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
        String tema = controlador.getTema();
        if (tema.equals("Claro")) {
            root.setStyle("-fx-background-color: #f5f5f5;");
        } else {
            root.setStyle("-fx-background-color: #0b0a2e;");
        }
        aplicarFondoPorTema(panelIzquierdo, controlador.getTema(), "#ffffff", "#1c1747");
        aplicarFondoPorTema(panelDerecho, controlador.getTema(), "#ffffff", "#1c1747");
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
    * Establece el controlador de la partida.
    * Permite actualizar la referencia al controlador durante
    * el ciclo de vida de la vista.
    * 
    * @pre controlador no debe ser null.
    * @param controlador Nuevo controlador de la partida
    * @post Se actualiza la referencia al controlador con el valor especificado.
    * @throws NullPointerException si controlador es null
    */        
    public void setControlador(ControladorPartidaView controlador) {
        this.controlador = controlador;
    }


    /**
    * Obtiene la vista cargada del tablero.
    * Proporciona acceso a la jerarquía de componentes JavaFX
    * para su integración en la aplicación principal.
    * 
    * @pre La vista debe haber sido cargada previamente.
    * @return Parent que contiene la vista completa del tablero
    * @post Se devuelve la referencia a la vista cargada sin modificar su estado.
    */
    public Parent getView() {
        return view;
    }    
    
    /**
     * Inicializa todos los componentes de la vista después de cargar el FXML.
     * Configura el tablero, las fichas del jugador, los eventos de interfaz
     * y ejecuta la configuración inicial de la partida.
     * 
     * @pre La vista FXML debe haber sido cargada correctamente.
     * @post Todos los componentes están configurados y la partida está lista
     *       para comenzar. Se ejecuta la inicialización de la primera jugada.
     * @throws Exception si hay errores en la configuración de componentes
     */    
    public void initialize() {
        try {
            // Inicializar el tablero y componentes
            inicializarTablero();
            
            // Inicializar fichas
            inicializarFichasJugador();
            
            // Diccionario
            if (lblDiccionarioSeleccionado != null) {
                lblDiccionarioSeleccionado.setText(controlador.getDiccionario());
            }

            if (scrollPane != null) {
                scrollPane.setPannable(true);
                scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
                scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
                scrollPane.setFitToHeight(false);
                scrollPane.setFitToWidth(false);
                scrollPane.getStyleClass().add("tablero-scroll-pane");
            }
            
            // Configurar botones
            if (btnConfirmar != null) btnConfirmar.setOnAction(e -> confirmarJugada());
            if (btnCancelar != null) btnCancelar.setOnAction(e -> cancelarJugada());
            if (btnPasarTurno != null) btnPasarTurno.setOnAction(e -> pasarTurno());
            if (btnCambiarFichas != null) btnCambiarFichas.setOnAction(e -> cambiarFichas());
            
            if (historialContainer == null) {
                historialContainer = (VBox) view.lookup("#historialContainer");
            }            
            // No necesitamos configurar btnPausarPartida porque ya tiene un controlador en el FXML (onAction="#pausarPartida")
            
            if (jugadorActual != null) {
                jugadorActual.setText("Iniciando partida...");
            }

            root.widthProperty().addListener((obs, oldVal, newVal) -> {
                panelIzquierdo.setPrefWidth(newVal.doubleValue()*0.15);
                panelCentro.setPrefWidth(newVal.doubleValue()*0.70);
                panelDerecho.setPrefWidth(newVal.doubleValue()*0.15);

            });
            // Iniciar la partida después de que todo esté configurado
            Platform.runLater(this::iniciarPartida);
        } catch (Exception e) {
            controlador.mostrarAlerta("error", "Error inesperado", "No se ha podido cargar la vista");
            controlador.volver();
            e.printStackTrace();
        }
    }

    /**
    * Configura los listeners para el redimensionamiento de la ventana.
    * Permite que el tablero se adapte automáticamente cuando
    * cambia el tamaño de la ventana principal.
    * 
    * @pre La vista debe estar completamente inicializada.
    * @post Se configuran los listeners que recentran el tablero
    *       automáticamente cuando cambia el tamaño de la ventana.
    */
    private void configurarListenerRedimensionamiento() {
        if (root != null && root.getScene() != null) {
            root.getScene().widthProperty().addListener((obs, oldVal, newVal) -> {
                recentrarTablero();
            });
            
            root.getScene().heightProperty().addListener((obs, oldVal, newVal) -> {
                recentrarTablero();
            });
        }
    }


    /**
    * Inicia la partida obteniendo los jugadores y configurando el primer turno.
    * Coordina la inicialización de todos los componentes necesarios
    * para comenzar una nueva partida o continuar una cargada.
    * 
    * @pre Los controladores y componentes deben estar correctamente inicializados.
    * @post La partida se inicia correctamente con el primer jugador configurado.
    *       Si es una partida cargada, se restaura el estado anterior.
    * @throws Exception si hay errores durante la inicialización
    */
    private void iniciarPartida() {
        try {
            // Obtener los jugadores de la partida
            jugadoresPuntuaciones = controlador.getJugadoresActuales();
            jugadores = new ArrayList<>(jugadoresPuntuaciones.keySet());
            
            if (controlador.getCargado()) {
                restaurarEstadoTablero();
            }  

            // Iniciar con el primer jugador si la partida no ha sido cargada
            if (!controlador.getCargado()) {
                jugadorActualIndex = -1;        
            }
    
            siguienteTurno();
        } catch (Exception e) {
            e.printStackTrace();
            controlador.mostrarAlerta("error", "Error", "Error al iniciar la partida: " + e.getMessage());
        }
    } 


    /**
    * Restaura el estado del tablero de una partida previamente guardada.
    * Reconstruye la posición de todas las fichas y el estado visual
    * del tablero según los datos persistidos.
    * 
    * @pre Debe existir un estado de tablero válido en el controlador.
    * @post El tablero se restaura completamente con todas las fichas
    *       en sus posiciones correctas y el estado visual actualizado.
    * @throws Exception si hay errores al restaurar el estado
    */
    private void restaurarEstadoTablero() {
        try {
            // Obtener el estado del tablero guardado
            Map<Tuple<Integer, Integer>, String> estadoTablero = controlador.getEstadoTablero();
            
            if (estadoTablero == null || estadoTablero.isEmpty()) {
                return;
            }
            
            
            // Iterar sobre todas las posiciones con fichas guardadas
            for (Map.Entry<Tuple<Integer, Integer>, String> entry : estadoTablero.entrySet()) {
                Tuple<Integer, Integer> posicion = entry.getKey();
                String letra = entry.getValue();
                
                if (letra != null && !letra.trim().isEmpty()) {
                    int fila = posicion.x;
                    int columna = posicion.y;
                    
                    // Buscar la casilla correspondiente en el tablero
                    CasillaDisplay casilla = encontrarCasilla(fila, columna);
                    
                    if (casilla != null) {
                        int puntos = obtenerPuntosPorLetra(letra);
                        casilla.colocarFicha(letra, puntos, true);                    
                        posiciones.put(new Tuple<>(fila, columna), letra);
                        
                    } 
                }
            }
            
            Platform.runLater(() -> {
                tablero.requestLayout();
                recentrarTablero(); // Recentrar después de restaurar
            });
            
            if (!estadoTablero.isEmpty()) {
                firstMove = false;
            }
            
        } catch (Exception e) {
            controlador.mostrarAlerta("error", "Error inesperado", "No se ha podido cargar la vista");
            controlador.volver();
            e.printStackTrace();
            controlador.mostrarAlerta("error", "Error", "Error al restaurar el estado del tablero: " + e.getMessage());
        }
    }


    /**
    * Encuentra una casilla específica en el tablero por sus coordenadas.
    * Utiliza búsqueda lineal en los hijos del GridPane para localizar
    * la casilla correspondiente a las coordenadas especificadas.
    * 
    * @pre fila y columna deben estar dentro de los límites del tablero.
    * @param fila Coordenada de fila de la casilla buscada
    * @param columna Coordenada de columna de la casilla buscada
    * @return CasillaDisplay correspondiente a las coordenadas, o null si no se encuentra
    * @post Se devuelve la referencia a la casilla sin modificar su estado.
    */
    private CasillaDisplay encontrarCasilla(int fila, int columna) {
        for (Node node : tablero.getChildren()) {
            if (node instanceof CasillaDisplay) {
                CasillaDisplay casilla = (CasillaDisplay) node;
                if (casilla.getFila() == fila && casilla.getColumna() == columna) {
                    return casilla;
                }
            }
        }
        return null;
    }


    /**
    * Limpia el estado de la jugada actual.
    * Resetea todas las variables temporales relacionadas con
    * la jugada en curso para preparar el siguiente turno.
    * 
    * @pre No hay precondiciones específicas.
    * @post Todas las variables de estado de la jugada actual se resetean
    *       y la lista de fichas colocadas se limpia.
    */
    private void limpiarJugadaActual() {
        fichaSeleccionada = null;    
        direccionActual = null;    
        fichasColocadasEnTurnoActual.clear();

    }


    /**
    * Avanza al siguiente jugador en la secuencia de turnos.
    * Gestiona la transición entre jugadores, actualizando la interfaz
    * y manejando tanto jugadores humanos como IA.
    * 
    * @pre La lista de jugadores debe estar inicializada y no vacía.
    * @post Se actualiza al siguiente jugador en la secuencia y se
    *       configura la interfaz correspondiente. Si es IA, se ejecuta
    *       automáticamente su turno.
    */
    private void siguienteTurno() {

        if (controlador.isJuegoTerminado()) {
            finalizarPartida();
            return;
        }
        
        limpiarJugadaActual(); 

        jugadorActualIndex = (jugadorActualIndex + 1) % jugadores.size();
        jugadorActualNombre = jugadores.get(jugadorActualIndex);
        if (controlador.esIA(jugadorActualNombre)) {
            manejarTurnoIA();
        }
        else {
            actualizarInterfazJugador();
        }

    }


    /**
    * Maneja el turno de un jugador controlado por IA.
    * Coordina la ejecución automática del turno de la IA,
    * incluyendo la toma de decisiones y la actualización visual.
    * 
    * @pre El jugador actual debe ser de tipo IA.
    * @return true si el turno se completó exitosamente, false si hubo errores
    * @post Se ejecuta el turno de la IA, se actualizan las puntuaciones
    *       y se coloca la palabra en el tablero si es válida.
    * @throws Exception si hay errores durante la ejecución del turno de IA
    */
    private boolean manejarTurnoIA() {
        try {

            Triple<String, Tuple<Integer, Integer>, Direction> jugadaIA = new Triple<>("null", null, null);
            int puntos = controlador.realizarTurnoPartida(jugadorActualNombre, jugadaIA);

            if (jugadaIA.getx().equals("") || jugadaIA.gety() == null) {
                // La IA no encontró movimientos válidos
                agregarEntradaHistorial(jugadorActualNombre, "P", 0);
                pasarTurno();
                return true;
            }
            
            String palabra = jugadaIA.getx();
            Tuple<Integer, Integer> posFinal = jugadaIA.gety();
            Direction direccion = jugadaIA.getz();
            
            colocarFichasIA(palabra, posFinal, direccion);
            controlador.actualizarJugador(jugadorActualNombre, puntos);
            agregarEntradaHistorial(jugadorActualNombre, palabra, puntos);        
            // actualizarInfoPartida();
            controlador.comprobarFinPartida(jugadoresPuntuaciones);
            
            if (controlador.isJuegoTerminado()) {
                finalizarPartida();
            } 
            else {
                siguienteTurno();
            }

            if (firstMove) firstMove = false;

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            controlador.mostrarAlerta("error", "Error en turno IA", "Error al procesar el turno de la IA: " + e.getMessage());
            return false;
        }
    }


    /**
     * Divide una palabra en letras válidas según el alfabeto definido.
     * Prioriza coincidencias más largas en caso de ambigüedad (e.g., "CH" sobre "C").
     *
     * @param word Palabra a tokenizar.
     * @return Lista de letras (símbolos) correspondientes.
     * @throws IllegalArgumentException si algún fragmento no pertenece al alfabeto.
     */
    private List<String> tokenize(String word) {
        List<String> tokens = new ArrayList<>();
        int i = 0;
        while (i < word.length()) {
            String match = null;
            int maxLength = 0;
            for (String symbol : alfabeto) {
                if (i + symbol.length() <= word.length() && word.startsWith(symbol, i)) {
                    if (symbol.length() > maxLength) {
                        match = symbol;
                        maxLength = symbol.length();
                    }
                }
            }
            if (match == null) {
                throw new IllegalArgumentException("Símbolo no reconocido en el alfabeto: " + word.substring(i));
            }
            tokens.add(match);
            i += match.length();
        }
        return tokens;
    }

    /**
    * Coloca las fichas en el tablero para una jugada de la IA.
    * Visualiza la palabra seleccionada por la IA colocando cada
    * ficha en su posición correspondiente en el tablero.
    * 
    * @pre palabra no debe ser null o vacía, posFinal debe ser válida.
    * @param palabra Palabra a colocar en el tablero
    * @param posFinal Posición final de la palabra
    * @param direccion Dirección de colocación (HORIZONTAL o VERTICAL)
    * @post Todas las fichas de la palabra se colocan visualmente en el tablero
    *       y se actualizan las estructuras de datos correspondientes.
    * @throws Exception si hay errores al colocar las fichas
    */
    private void colocarFichasIA(String palabra, Tuple<Integer, Integer> posFinal, Direction direccion) {
    if (palabra == null || palabra.isEmpty() || posFinal == null) {
        return;
    }

    try {
        List<String> tokens = tokenize(palabra.toUpperCase());
        
        // Calculate initial position based on final position and word length
        int xInicial, yInicial;
        if (direccion == Direction.HORIZONTAL) {
            // For horizontal: start at (posFinal.x, posFinal.y - wordLength + 1)
            xInicial = posFinal.x;
            yInicial = posFinal.y - tokens.size() + 1;
        } else {
            // For vertical: start at (posFinal.x - wordLength + 1, posFinal.y)
            xInicial = posFinal.x - tokens.size() + 1;
            yInicial = posFinal.y;
        }
        
        Tuple<Integer, Integer> pos = new Tuple<>(xInicial, yInicial);
        
        // Place each letter of the word
        for (int i = 0; i < tokens.size(); i++) {
            String letra = tokens.get(i);
            
            // Only place if position is empty
            if (posiciones.get(pos) == null) {
                // Find the corresponding CasillaDisplay
                for (Node node : tablero.getChildren()) {
                    if (node instanceof CasillaDisplay) {
                        CasillaDisplay casilla = (CasillaDisplay) node;
                        if (casilla.getFila() == pos.x && casilla.getColumna() == pos.y) {
                            int puntos = obtenerPuntosPorLetra(letra);
                            casilla.colocarFicha(letra, puntos, true);
                            posiciones.put(pos, letra);
                            break;
                        }
                    }
                }
            }
            
            // Advance to next position
            if (direccion == Direction.HORIZONTAL) {
                pos = new Tuple<>(pos.x, pos.y + 1);
            } else {
                pos = new Tuple<>(pos.x + 1, pos.y);
            }
        }
        
        Platform.runLater(() -> tablero.requestLayout());
        
    } catch (Exception e) {
        controlador.mostrarAlerta("error", "Error inesperado", "No se pudo colocar una ficha de la IA");
        e.printStackTrace();
    }
}


    /**
    * Actualiza la interfaz para mostrar la información del jugador actual.
    * Coordina la actualización de todos los componentes visuales
    * relacionados con el estado del jugador activo.
    * 
    * @pre El jugador actual debe estar correctamente configurado.
    * @post La interfaz muestra la información actualizada del jugador actual,
    *       incluyendo nombre, puntuación y fichas disponibles.
    */
    private void actualizarInterfazJugador() {
        if (jugadorActual != null) {
            jugadorActual.setText("Turno de: " + jugadorActualNombre);
        }
        
        actualizarInfoPartida();
        actualizarRackJugador();
    }


    /**
    * Actualiza la información general de la partida.
    * Coordina la actualización de puntuaciones y estado global
    * de la partida en la interfaz de usuario.
    * 
    * @pre Los datos de la partida deben estar disponibles en el controlador.
    * @post Todos los componentes de información de partida se actualizan
    *       con los datos más recientes.
    */
    private void actualizarInfoPartida() {
        actualizarPuntuaciones();    
        actualizarFichasRestantes();
    }



    /**
    * Actualiza la visualización de puntuaciones de todos los jugadores.
    * Refresca el panel de puntuaciones mostrando la información
    * actualizada de todos los participantes.
    * 
    * @pre La lista de jugadores y sus puntuaciones debe estar disponible.
    * @post El panel de puntuaciones muestra la información actualizada
    *       de todos los jugadores, resaltando el jugador actual.
    */
    private void actualizarPuntuaciones() {
        if (puntuacionesContainer != null) {
            puntuacionesContainer.getChildren().clear();
            
            // Label lblTitulo = new Label("Puntuaciones");
            // lblTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #333;");
            // puntuacionesContainer.getChildren().add(lblTitulo);
            
            jugadoresPuntuaciones = controlador.getJugadoresActuales();
            
            for (Map.Entry<String, Integer> entry : jugadoresPuntuaciones.entrySet()) {
                String nombre = entry.getKey();
                int puntos = entry.getValue();
                
                HBox jugadorRow = new HBox(5);
                jugadorRow.setAlignment(Pos.CENTER_LEFT);
                
                Label lblNombre = new Label(nombre + ":");
                lblNombre.setPrefWidth(90);
                String colorText = controlador.getTema().equals("Oscuro") ? "white" : "black";
                          
                lblNombre.setStyle("-fx-font-size: 14px; -fx-text-fill:" + colorText + ";");

                
                Label lblPuntos = new Label(String.valueOf(puntos) + " pts");
                lblPuntos.setStyle("-fx-font-size: 14px; -fx-text-fill:" + colorText + ";");
                
                // Resaltar al jugador actual
                if (nombre.equals(jugadorActualNombre)) {
                    lblNombre.setStyle("-fx-font-size: 14px; -fx-text-fill: #ff9800; -fx-font-weight: bold;");
                    lblPuntos.setStyle("-fx-font-size: 14px; -fx-text-fill: #ff9800; -fx-font-weight: bold;");
                }
                
                jugadorRow.getChildren().addAll(lblNombre, lblPuntos);
                puntuacionesContainer.getChildren().add(jugadorRow);
            }
        }
    }



    /**
    * Actualiza la visualización del número de fichas restantes en la bolsa.
    * Muestra el contador actualizado de fichas disponibles para
    * intercambio y distribución.
    * 
    * @pre El controlador debe proporcionar la información de fichas restantes.
    * @post El label de fichas restantes muestra el valor actualizado.
    */
    private void actualizarFichasRestantes() {
        int fichasRestantes = controlador.getCantidadFichasRestantes();    
        if (lblFichasRestantes != null) {
            lblFichasRestantes.setText("Fichas en la bolsa: " + fichasRestantes);
        }
    }



    /**
    * Actualiza el rack de fichas del jugador actual.
    * Refresca la visualización de las fichas disponibles para
    * el jugador que tiene el turno activo.
    * 
    * @pre El jugador actual debe tener un rack válido.
    * @post El rack visual se actualiza mostrando las fichas correctas
    *       del jugador actual con sus eventos configurados.
    * @throws Exception si hay errores al obtener el rack del jugador
    */
    private void actualizarRackJugador() {
        if (fichasJugador != null) {
            fichasJugador.getChildren().clear();
            rackActual = controlador.getRackJugador(jugadorActualNombre);
            
            if (rackActual == null) {
                controlador.mostrarAlerta("error", "Error", "No se pudo obtener el rack del jugador");
                return;
            }
            
            // Crear fichas para cada letra en el rack
            for (Map.Entry<String, Integer> entry : rackActual.entrySet()) {
                String letra = entry.getKey();
                int cantidad = entry.getValue();
                
                // Puntos para la letra (esto podría obtenerse del controlador)
                int puntos = obtenerPuntosPorLetra(letra);
                
                for (int i = 0; i < cantidad; i++) {
                    Ficha ficha = new Ficha(letra, puntos);
                    ficha.setMinSize(CASILLA_SIZE, CASILLA_SIZE);
                    ficha.setPrefSize(CASILLA_SIZE, CASILLA_SIZE);
                    ficha.setMaxSize(CASILLA_SIZE, CASILLA_SIZE);
                    
                    configurarEventosFicha(ficha);
                    fichasJugador.getChildren().add(ficha);
                }
            }

            
        }
    }



    /**
    * Añade una nueva entrada al historial de jugadas.
    * Registra la acción realizada por un jugador en el historial
    * visual de la partida.
    * 
    * @pre jugadorNombre no debe ser null, palabra debe ser válida.
    * @param jugadorNombre Nombre del jugador que realizó la acción
    * @param palabra Palabra jugada o tipo de acción realizada
    * @param n Puntos obtenidos o número de fichas cambiadas
    * @post Se añade una nueva entrada al historial y se actualiza
    *       la visualización, manteniendo el límite de entradas.
    */
    private void agregarEntradaHistorial(String jugadorNombre, String palabra, int n) {
        String entrada;
        
        // Crear la entrada según el tipo de acción
        if (palabra.equals("P")) {
            entrada = jugadorNombre + " pasó turno";
        } else if (palabra.equals("CF")) {
            entrada = jugadorNombre + " cambió (" + n + ") fichas";
        } else {
            entrada = jugadorNombre + " colocó palabra " + palabra + " (" + n + " pts)";
        }
        
        // Añadir al principio (más reciente arriba)
        historialJugadas.add(0, entrada);
        
        // Limitar el tamaño del historial
        if (historialJugadas.size() > MAX_HISTORIAL_ENTRIES) {
            historialJugadas.remove(historialJugadas.size() - 1);
        }
        
        actualizarVistaHistorial();
    }



    /**
    * Actualiza la visualización del historial de jugadas en la interfaz.
    * Refresca el panel de historial mostrando las entradas más recientes
    * con formato apropiado y resaltado de jugadas importantes.
    * 
    * @pre El historial de jugadas debe estar inicializado.
    * @post El panel de historial se actualiza con todas las entradas
    *       formateadas correctamente y se posiciona en la parte superior.
    */
    private void actualizarVistaHistorial() {
        if (historialContainer != null) {
            // Limpiar el contenedor
            historialContainer.getChildren().clear();
            
            for (String entrada : historialJugadas) {
                Label lblEntrada = new Label(entrada);
                
                lblEntrada.setWrapText(true);
                lblEntrada.setMaxWidth(Region.USE_COMPUTED_SIZE);
                lblEntrada.setPrefWidth(Region.USE_COMPUTED_SIZE);            
                lblEntrada.setPrefWidth(Region.USE_COMPUTED_SIZE);
                String colorText = controlador.getTema().equals("Oscuro") ? "white" : "black";          
                lblEntrada.setStyle("-fx-font-size: 14px; -fx-text-fill:" + colorText + "-fx-padding: 5;");

                
                // Destacar jugadas con puntuación alta
                if (entrada.contains(" pts)")) {
                    int ptsIndex = entrada.lastIndexOf("(") + 1;
                    int ptsEndIndex = entrada.lastIndexOf(" pts)");
                    try {
                        int pts = Integer.parseInt(entrada.substring(ptsIndex, ptsEndIndex));
                        if (pts > 15) {
                            lblEntrada.setStyle("-fx-font-size: 14px; -fx-text-fill: #ff9800; -fx-font-weight: bold; -fx-padding: 5;");
                        }
                    } catch (NumberFormatException e) {
                        continue;
                    }
                }
                
                historialContainer.getChildren().add(lblEntrada);
            }
            
            Platform.runLater(() -> {
                if (historialScrollPane != null) {
                    historialScrollPane.setVvalue(0.0);
                }
            });
        }
    }



    /**
    * Obtiene los puntos correspondientes a una letra específica.
    * Consulta el sistema de puntuación para determinar el valor
    * de una letra en el juego actual.
    * 
    * @pre letra no debe ser null.
    * @param letra Letra para la cual se quieren obtener los puntos
    * @return Puntos correspondientes a la letra, o 0 si no se encuentra
    * @post Se devuelve el valor de puntos sin modificar el estado del sistema.
    */
    public int obtenerPuntosPorLetra(String letra) {
        Integer puntos = controlador.obtenerPuntosPorLetra(letra);
        return puntos != null ? puntos : 0; // Devuelve 0 si es null
        
    }



    /**
    * Obtiene el alfabeto completo con sus puntuaciones.
    * Proporciona acceso a la información completa del sistema
    * de puntuación del juego actual.
    * 
    * @pre El controlador debe tener un sistema de puntuación inicializado.
    * @return Mapa con todas las letras y sus puntuaciones correspondientes
    * @post Se devuelve el alfabeto completo sin modificar el estado.
    */
    public Map<String, Integer> getAllAlphabet() {
        return controlador.getAllAlphabet();
    }



    /**
    * Construye una palabra en dirección horizontal a partir de fichas colocadas.
    * Analiza las fichas colocadas y las fichas existentes en el tablero
    * para formar una palabra completa en dirección horizontal.
    * 
    * @pre d debe ser HORIZONTAL, fichas no debe ser null ni vacía.
    * @param d Dirección de construcción de la palabra
    * @param fichas Lista de fichas colocadas en el turno actual
    * @return Triple con la palabra construida y las coordenadas de la última letra
    * @post Se devuelve la palabra completa formada o un resultado vacío si no es válida.
    */
    private Triple<StringBuilder, Integer, Integer> buildPalabraHorizontal(Direction d, List<FichaColocada> fichas) {
        StringBuilder palabra = new StringBuilder();
        int start;
        int end;
        int fila = fichas.get(0).fila;

        if (d == Direction.HORIZONTAL) {
            end = fichas.get(fichas.size() -1 ).columna;
            start = fichas.get(0).columna-1;
            Tuple <Integer, Integer> posicion = new Tuple<> (fila, start);

            if (posiciones.get(posicion) == null) start = fichas.get(0).columna;
            // Posibles letras contiguas a la izquierda
            while (posiciones.get(posicion) != null && start > 0) {
                palabra.insert(0, posiciones.get(posicion));
                start--;
                posicion =  new Tuple<> (fila, start);
            }

            
            // Rellenar letras entre medias
            for (int i = 0; i < fichas.size(); i++) {
                palabra.append(fichas.get(i).letra);
                
                if (i + 1 < fichas.size()) {
                    int nextLetterDist = fichas.get(i + 1).columna - fichas.get(i).columna;
                    if (nextLetterDist > 1) {

                        for (int col = fichas.get(i).columna + 1; col < fichas.get(i+1).columna; col++) {
                            Tuple <Integer, Integer> pos = new Tuple<> (fila, col);

                            if (posiciones.get(pos) != null) {
                                palabra.append(posiciones.get(pos));
                            }
                            else {
                                // Hay un hueco entre las palabras colocadas, jugada inválida.
                                return new Triple<StringBuilder,Integer,Integer>(new StringBuilder(), -1, -1);
                            }
                        }
                    }            
                
                }
            }

            boolean finished = false;
            // Posibles letras contiguas a la derecha
            for (int col = fichas.get(fichas.size() - 1).columna + 1; col < N && !finished; col++) {
                Tuple <Integer, Integer> pos = new Tuple<> (fila, col);
                if (posiciones.get(pos) != null) {
                    palabra.append(posiciones.get(pos));
                    end = col;
                }
                else {
                    finished = true;
                }
            }        

        }
        else {
            return new Triple<StringBuilder,Integer,Integer>(new StringBuilder(), -1, -1);
        }

        return new Triple<StringBuilder,Integer,Integer>(palabra, fila, end);
    }



    /**
    * Construye una palabra en dirección vertical a partir de fichas colocadas.
    * Analiza las fichas colocadas y las fichas existentes en el tablero
    * para formar una palabra completa en dirección vertical.
    * 
    * @pre d debe ser VERTICAL, fichas no debe ser null ni vacía.
    * @param d Dirección de construcción de la palabra
    * @param fichas Lista de fichas colocadas en el turno actual
    * @return Triple con la palabra construida y las coordenadas de la última letra
    * @post Se devuelve la palabra completa formada o un resultado vacío si no es válida.
    */
    private Triple<StringBuilder, Integer, Integer> buildPalabraVertical(Direction d, List<FichaColocada> fichas) {
        StringBuilder palabra = new StringBuilder();
        int start;
        int end;
        int columna = fichas.get(0).columna;

        if (d == Direction.VERTICAL) {
            end = fichas.get(fichas.size() - 1).fila;
            start = fichas.get(0).fila - 1;
            Tuple<Integer, Integer> posicion = new Tuple<>(start, columna);

            if (posiciones.get(posicion) == null) start = fichas.get(0).fila;
            // Posibles letras contiguas arriba
            while (posiciones.get(posicion) != null && start > 0) {
                palabra.insert(0, posiciones.get(posicion));
                start--;
                posicion = new Tuple<>(start, columna);
            }

            // Rellenar letras entre medias
            for (int i = 0; i < fichas.size(); i++) {
                palabra.append(fichas.get(i).letra);
                
                if (i + 1 < fichas.size()) {
                    int nextLetterDist = fichas.get(i + 1).fila - fichas.get(i).fila;

                    if (nextLetterDist > 1) {
                        for (int fila = fichas.get(i).fila + 1; fila < fichas.get(i + 1).fila; fila++) {                    
                            Tuple<Integer, Integer> pos = new Tuple<>(fila, columna);

                            if (posiciones.get(pos) != null) {
                                palabra.append(posiciones.get(pos));

                            }
                            else {
                                // Hay un hueco entre las palabras colocadas, jugada inválida.
                                return new Triple<StringBuilder, Integer, Integer>(new StringBuilder(), -2, -2);
                            }
                        }
                    }            
                }
            }

            boolean finished = false;
            // Posibles letras contiguas abajo
            for (int fila = fichas.get(fichas.size() - 1).fila + 1; fila < N && !finished; fila++) {
                Tuple<Integer, Integer> pos = new Tuple<>(fila, columna);
                if (posiciones.get(pos) != null) {
                    palabra.append(posiciones.get(pos));
                    end = fila;
                }
                else {
                    finished = true;
                }
            }        
        }
        else {
            return new Triple<StringBuilder, Integer, Integer>(new StringBuilder(), -1, -1);
        }

        return new Triple<StringBuilder, Integer, Integer>(palabra, end, columna);
    }



    /**
    * Verifica si las fichas colocadas en el turno actual forman una jugada válida.
    * Analiza la configuración de fichas colocadas para determinar si
    * forman una palabra válida según las reglas del juego.
    * 
    * @pre Debe haber fichas colocadas en el turno actual.
    * @return Tupla con booleano indicando validez y los datos de la jugada
    * @post Se devuelve la validez de la jugada y la información completa
    *       de la palabra formada si es válida.
    */
    private Tuple<Boolean, Triple<String, Tuple <Integer, Integer>, Direction>> verificarJugadaValida() {

        Tuple<Boolean,Triple<String,Tuple<Integer,Integer>,Direction>> result = new Tuple<Boolean,Triple<String,Tuple<Integer,Integer>,Direction>>(false, new Triple<>("", new Tuple<>(-1, -1), Direction.HORIZONTAL));
        if (fichasColocadasEnTurnoActual.isEmpty()) {
            return result;
        }
        
        // Construir la palabra y obtener la última ficha colocada
        StringBuilder palabra = new StringBuilder();
        Tuple<Integer, Integer> posicionUltimaLetra = null;
        
        // Si hay más de una ficha, determinar la dirección
        if (fichasColocadasEnTurnoActual.size() > 1) {
            // Ordenar las fichas por posición
            List<FichaColocada> fichasOrdenadas = new ArrayList<>(fichasColocadasEnTurnoActual);
            int fila1 = fichasOrdenadas.get(0).fila;
            int col1 = fichasOrdenadas.get(0).columna;
            boolean mismaFila = true;
            boolean mismaColumna = true;
            
            for (int i = 1; i < fichasOrdenadas.size(); i++) {
                if (fichasOrdenadas.get(i).fila != fila1) {
                    mismaFila = false;
                }
                if (fichasOrdenadas.get(i).columna != col1) {
                    mismaColumna = false;
                }
            }
            
            if (mismaFila && !mismaColumna) {
                direccionActual = Direction.HORIZONTAL;
                // Ordenar por columna
                fichasOrdenadas.sort(Comparator.comparingInt(f -> f.columna));
            } else if (!mismaFila && mismaColumna) {
                direccionActual = Direction.VERTICAL;
                // Ordenar por fila
                fichasOrdenadas.sort(Comparator.comparingInt(f -> f.fila));
            } else {
                // No alineadas
                controlador.mostrarAlerta("warning", "Jugada inválida", "¡La palabra colocada debe tener todas las fichas en una misma dirección!");
                return result;
            }

            // Triple <StringBuilder, Integer, Integer> possibleWord = buildPalabraHorizontal(direccionActual, fichasOrdenadas);
            Triple <StringBuilder, Integer, Integer> possibleWord = direccionActual == Direction.HORIZONTAL ? buildPalabraHorizontal(direccionActual, fichasOrdenadas) : buildPalabraVertical(direccionActual, fichasOrdenadas);
            palabra = possibleWord.getx();
            // System.out.println("Se ha intentado colocar la palabra: " + palabra.toString());

            if (palabra.length() == 0|| possibleWord.y == -1 || possibleWord.z == -1) {
                // System.err.println("Palabra invalida. Longitud: " + palabra.length() + " possibleword.y = " + possibleWord.y +  " possibleword.z = " + possibleWord.z);
                return result;
            } 
        
                
            posicionUltimaLetra = new Tuple<>(possibleWord.gety(), possibleWord.getz());
            // System.err.println("Termina en: " + posicionUltimaLetra.x + posicionUltimaLetra.y);
        }
        else {
            // Cuando solo hay una ficha colocada
            if (fichasColocadasEnTurnoActual.size() == 1) {
                FichaColocada ficha = fichasColocadasEnTurnoActual.get(0);
                List<FichaColocada> fichasList = new ArrayList<>();
                fichasList.add(ficha);
                
                // Intentar construir palabra horizontal
                Triple<StringBuilder, Integer, Integer> palabraHorizontal = 
                    buildPalabraHorizontal(Direction.HORIZONTAL, fichasList);
                
                // Intentar construir palabra vertical
                Triple<StringBuilder, Integer, Integer> palabraVertical = 
                    buildPalabraVertical(Direction.VERTICAL, fichasList);
                
                // Verificar si alguna dirección produjo una palabra válida (de longitud >= 2)
                if (palabraHorizontal.getx().length() >= 2) {
                    // Usar la palabra horizontal
                    palabra = palabraHorizontal.getx();
                    direccionActual = Direction.HORIZONTAL;
                    posicionUltimaLetra = new Tuple<>(ficha.fila, palabraHorizontal.getz());
                } else if (palabraVertical.getx().length() >= 2) {
                    // Usar la palabra vertical
                    palabra = palabraVertical.getx();
                    direccionActual = Direction.VERTICAL;
                    posicionUltimaLetra = new Tuple<>(palabraVertical.gety(), ficha.columna);
                } else {
                    // No se formó ninguna palabra válida en ninguna dirección
                    return new Tuple<>(false, new Triple<>("", new Tuple<>(-1, -1), Direction.HORIZONTAL));
                }
                            
                // Crear la jugada con la posición de la última letra
                Triple<String, Tuple<Integer, Integer>, Direction> jugada = 
                    new Triple<>(
                        palabra.toString(), 
                        posicionUltimaLetra, 
                        direccionActual
                    );
                
                
                // Verificar con el controlador
                Boolean valid = jugada.getx().contains("#") ? true : controlador.esMovimientoValido(jugada, rackActual);
                return new Tuple<>(valid, jugada);
            }
        
        }
                
                
        // Crear la jugada con la posición de la última letra
        Triple<String, Tuple<Integer, Integer>, Direction> jugada = 
            new Triple<>(
                palabra.toString(), 
                posicionUltimaLetra, 
                direccionActual
            );
        
        // System.err.println("Se ha creado el movimiendo con palabra: " + palabra.toString() + "| Posicion: " + posicionUltimaLetra.x + posicionUltimaLetra.y + " | Direccion: " + direccionActual);
        Boolean valid = jugada.getx().contains("#") ? true : controlador.esMovimientoValido(jugada, rackActual);
        return new Tuple<Boolean,Triple<String,Tuple<Integer,Integer>,Direction>>(valid, jugada);
    }


    /**
     * Confirma la jugada actual validando las fichas colocadas.
     * Verifica que las fichas formen una palabra válida según las reglas
     * del juego y procesa los puntos correspondientes.
     * 
     * @pre Debe haber al menos una ficha colocada en el turno actual.
     * @post Si la jugada es válida, se confirman las fichas, se actualizan
     *       las puntuaciones y se pasa al siguiente turno. Si es inválida,
     *       se muestra un mensaje de error.
     */
    private void confirmarJugada() {
        if (fichasColocadasEnTurnoActual.isEmpty()) {
            controlador.mostrarAlerta("warning", "Jugada vacía", "No has colocado ninguna ficha en el tablero, coloca al menos una!");
            return;
        }



    // Verificar si es el primer movimiento y si pasa por el centro
        if (firstMove) {
            boolean centrado = false;
            Tuple<Integer, Integer> center = new Tuple<Integer,Integer>(N/2, N/2); 
            
            for (FichaColocada f : fichasColocadasEnTurnoActual) {
                if (f.fila == center.x && f.columna == center.y) {
                    centrado = true;
                    break;
                }
            }
            
            if (!centrado) {
                controlador.mostrarAlerta("warning", "Jugada inválida", "¡Es el primer turno, debes de colocar una ficha en el centro del tablero!");
                cancelarJugada();
                return;
            }
        } 
        // Si no es el primer movimiento, verificar contigüidad
        else {
            boolean contiguo = false;
            ArrayList<Tuple<Integer, Integer>> dirs = new ArrayList<>(Arrays.asList(
                new Tuple<>(1, 0),   
                new Tuple<>(-1, 0),  
                new Tuple<>(0, 1), 
                new Tuple<>(0, -1)   
            ));
            
            // Verificar cada ficha colocada
            for (FichaColocada f : fichasColocadasEnTurnoActual) {
                for (Tuple<Integer, Integer> d : dirs) {
                    Tuple<Integer, Integer> posAdyacente = new Tuple<>(f.fila + d.x, f.columna + d.y);
                    
                    if (posiciones.get(posAdyacente) != null) {
                        contiguo = true;
                        break;
                    }
                }
                
                if (contiguo) break; 
            }
            
            if (!contiguo) {
                controlador.mostrarAlerta("warning", "Jugada inválida", "¡Movimiento ilegal por las reglas del juego, la palabra debe estar contigua con al menos una ficha del tablero!");
                cancelarJugada();
                return;
            }
        }

        Tuple<Boolean, Triple<String, Tuple <Integer, Integer>, Direction>> result = verificarJugadaValida();        
        // Verificar si la jugada es válida
        if (!result.x) {
            // System.err.println("!result.x: " + result.x);
            controlador.mostrarAlerta("warning", "Jugada inválida", "¡No existe la palabra colocada!");
            cancelarJugada();
            return;
        }
        
        Triple<String, Tuple <Integer, Integer>, Direction> jugada = result.y;
        
        try {
            // Realizar el turno
            int puntos = controlador.realizarTurnoPartida(jugadorActualNombre, jugada);
            agregarEntradaHistorial(jugadorActualNombre, jugada.getx(), puntos);
            // Actualizar puntuación
            controlador.actualizarJugador(jugadorActualNombre, puntos);
            
            // Mostrar resultado
            for (FichaColocada ficha : fichasColocadasEnTurnoActual) {

                posiciones.put(new Tuple<>(ficha.fila, ficha.columna), ficha.getLetra());
                // Estilo ficha confirmada
                for (Node node : tablero.getChildren()) {
                    if (node instanceof CasillaDisplay) {
                        CasillaDisplay casilla = (CasillaDisplay) node;
                        if (casilla.getFila() == ficha.fila && casilla.getColumna() == ficha.columna) {
                            casilla.marcarFichaComoConfirmada();
                        }
                    }
                }
            }
            
            if (firstMove) firstMove = false;

            // Limpiar lista de fichas colocadas
            fichasColocadasEnTurnoActual.clear();
            
            // Comprobar fin de partida
            controlador.comprobarFinPartida(jugadoresPuntuaciones);
            
            if (controlador.isJuegoTerminado()) {
                actualizarInfoPartida();            
                finalizarPartida();
            } else {
                siguienteTurno();
            }
        } catch (Exception e) {
            e.printStackTrace();
            controlador.mostrarAlerta("error", "Error", "Error al confirmar la jugada: " + e.getMessage());
        }
    }




    /**
    * Inicializa el tablero con casillas especiales y numeración.
    * Crea la estructura visual completa del tablero incluyendo
    * casillas especiales, numeración y eventos de interacción.
    * 
    * @pre N debe estar inicializado con un valor válido.
    * @post El tablero se crea completamente con todas las casillas
    *       configuradas, numeración añadida y eventos establecidos.
    */
    private void inicializarTablero() {
        if (tablero != null) {
            // Reset por si se cargó un tablero anteriormente
            tablero.getChildren().clear();
            if (filasNumeracion != null) filasNumeracion.getChildren().clear();
            if (columnasNumeracion != null) columnasNumeracion.getChildren().clear();
            
            //  Numeración de filas
            if (filasNumeracion != null) {
                filasNumeracion.setStyle("-fx-background-color: transparent; -fx-padding: 0; -fx-min-width: 30;");
                filasNumeracion.setMinWidth(30);
                filasNumeracion.setPrefWidth(30);
                filasNumeracion.setAlignment(Pos.TOP_CENTER); // Centrar verticalmente
                filasNumeracion.setSpacing(0);
            }
            
            if (columnasNumeracion != null) {
                columnasNumeracion.setStyle("-fx-background-color: transparent; -fx-padding: 0; -fx-min-height: 30;");
                columnasNumeracion.setMinHeight(30);
                columnasNumeracion.setPrefHeight(30);
                columnasNumeracion.setAlignment(Pos.CENTER_LEFT);
                columnasNumeracion.setSpacing(0);
                
                // Este espacio vacio es necesario para que la enum de columnas coincida con la primero columna
                Label espacioInicial = new Label("");
                espacioInicial.setMinSize(30, 30);
                espacioInicial.setPrefSize(30, 30);
                espacioInicial.setMaxSize(30, 30);
                columnasNumeracion.getChildren().add(espacioInicial);
            }
            
            // Crear numeración de columnas
            for (int col = 0; col < N; col++) {
                Label colLabel = new Label(String.valueOf(col));
                colLabel.setMinSize(CASILLA_SIZE, 30);
                colLabel.setMaxSize(CASILLA_SIZE, 30);
                colLabel.setPrefSize(CASILLA_SIZE, 30);
                colLabel.setAlignment(Pos.CENTER);
                colLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #666666;");
                
                if (columnasNumeracion != null) {
                    columnasNumeracion.getChildren().add(colLabel);
                }
            }
            
            // Crear tablero NxN
            for (int fila = 0; fila < N; fila++) {
                // Añadir numeración de fila
                if (filasNumeracion != null) {
                    Label rowLabel = new Label(String.valueOf(fila));
                    rowLabel.setMinSize(30, CASILLA_SIZE);
                    rowLabel.setMaxSize(30, CASILLA_SIZE);
                    rowLabel.setPrefSize(30, CASILLA_SIZE);
                    rowLabel.setAlignment(Pos.CENTER);
                    rowLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #666666;");
                    filasNumeracion.getChildren().add(rowLabel);
                }
                
                for (int col = 0; col < N; col++) {
                    CasillaDisplay casilla = new CasillaDisplay(fila, col, CASILLA_SIZE);
                    
                    definirTipoCasilla(casilla, fila, col);
                    
                    // Configurar eventos
                    final int finalFila = fila;
                    final int finalCol = col;
                    
                    casilla.setOnMouseEntered(e -> {
                        if (fichaSeleccionada != null && !casilla.tieneFicha()) {
                            casilla.resaltar(true);
                        }
                    });
                    
                    casilla.setOnMouseExited(e -> {
                        if (fichaSeleccionada != null) {
                            casilla.resaltar(false);
                        }
                    });
                    
                    casilla.setOnMouseClicked(e -> {
                        if (fichaSeleccionada != null && !casilla.tieneFicha()) {
                            colocarFichaEnCasilla(casilla, finalFila, finalCol);
                        } else if (casilla.tieneFicha() && !casilla.esFichaConfirmada() && fichaSeleccionada == null) {
                            retirarFichaDeCasilla(casilla);
                        }
                    });
                    
                    tablero.add(casilla, col, fila);
                }
            }
            
            tablero.setHgap(0);
            tablero.setVgap(0);
            tablero.setPadding(new Insets(0));
            
            // Inicializar posiciones
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    posiciones.put(new Tuple<>(i, j), null);
                }   
            }
            
            centrarTableroEnScrollPane();
        }
    }



    /**
    * Ejecuta la acción de pasar turno al siguiente jugador.
    * Permite que el jugador actual ceda su turno sin realizar
    * ninguna jugada en el tablero.
    * 
    * @pre Debe ser el turno de un jugador válido.
    * @post Se registra la acción de pasar turno, se avanza al siguiente
    *       jugador y se actualiza la información de la partida.
    * @throws Exception si hay errores durante la transición de turno
    */
    private void pasarTurno() {
        try {
            cancelarJugada();
            // Crear una jugada de "pasar turno"
            Triple<String, Tuple<Integer, Integer>, Direction> pasarJugada = 
                new Triple<>("P", null, null);
            
            // Realizar el turno
            controlador.realizarTurnoPartida(jugadorActualNombre, pasarJugada);
            agregarEntradaHistorial(jugadorActualNombre, "P", 0);        

            if (controlador.isJuegoTerminado()) {
                finalizarPartida();
            } else {
                
                // Pasar al siguiente jugador
                siguienteTurno();
                
                actualizarInfoPartida();            
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
    * Permite cambiar fichas del rack por nuevas fichas de la bolsa.
    * Facilita el intercambio de fichas cuando el jugador no puede
    * o no quiere realizar una jugada con sus fichas actuales.
    * 
    * @pre El jugador debe tener fichas en su rack y debe haber fichas en la bolsa.
    * @post Se intercambian las fichas seleccionadas, se actualiza el rack
    *       y se avanza al siguiente turno.
    * @throws Exception si hay errores durante el intercambio
    */
    private void cambiarFichas() {
        try {
            // Primero, cancelar cualquier jugada en curso para devolver las fichas al rack
            cancelarJugada();
            
            // Obtener el rack actual del jugador
            rackActual = controlador.getRackJugador(jugadorActualNombre);
            if (rackActual == null || rackActual.isEmpty()) {
                controlador.mostrarAlerta("error", "Error", "No se pudo obtener el rack del jugador o está vacío");
                return;
            }
            
            // Mostrar el popup para que el usuario seleccione las fichas
            CambiarFichasPopup popup = new CambiarFichasPopup(rackActual, CASILLA_SIZE, this);
            List<String> fichasACambiar = popup.mostrarYEsperar();
            if (fichasACambiar.isEmpty()) {
                return; 
            }
            if (controlador.getCantidadFichasRestantes() < fichasACambiar.size()) {
                controlador.mostrarAlerta("error", "Error", 
        "No se pudieron cambiar las fichas, hay menos fichas disponibles de las que quieres cambiar"); 
                return;       
            }
                    
            // Realizar el intercambio de fichas
            boolean intercambioExitoso = controlador.intercambiarFichas(jugadorActualNombre, fichasACambiar);
            
            if (intercambioExitoso) {
                actualizarInfoPartida();
                
                Triple<String, Tuple<Integer, Integer>, Direction> cambiarJugada = 
                    new Triple<>("CF", null, null);
                
                controlador.realizarTurnoPartida(jugadorActualNombre, cambiarJugada);
                
                agregarEntradaHistorial(jugadorActualNombre, "CF", fichasACambiar.size());
                
                // Actualizar el rack
                actualizarRackJugador();
                
                // Comprobar fin de partida
                controlador.comprobarFinPartida(jugadoresPuntuaciones);
                
                if (controlador.isJuegoTerminado()) {
                    finalizarPartida();
                } else {
                    // Pasar al siguiente jugador
                    siguienteTurno();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            controlador.mostrarAlerta("error", "Error", "Error al cambiar fichas: " + e.getMessage());
        }
    }     



    /**
    * Finaliza la partida y muestra los resultados detallados.
    * Coordina el proceso de finalización incluyendo determinación
    * del ganador, cálculo de estadísticas y presentación de resultados.
    * 
    * @pre La partida debe haber alcanzado una condición de finalización.
    * @post Se determina el ganador, se calculan las estadísticas finales,
    *       se liberan los recursos y se muestra el resultado al usuario.
    * @throws Exception si hay errores durante la finalización
    */
    private void finalizarPartida() {
        try {
            jugadoresPuntuaciones = controlador.getJugadoresActuales();
            
            // Determinar el ganador y calcular estadísticas
            String ganador = determinarGanador();
            String estadisticasFinales = generarEstadisticasFinales();
            String mensajeCompleto = construirMensajeFinal(ganador, estadisticasFinales);
            
            // Liberar jugadores de la partida actual si no está cargada
            if (!controlador.getCargado()) {
                controlador.liberarJugadores();
            } else {
                controlador.setCargado(false);
            }
            controlador.finalizarJuego(jugadoresPuntuaciones);
            mostrarResultadoFinal(mensajeCompleto);
            
        } catch (Exception e) {
            e.printStackTrace();
            controlador.mostrarAlerta("error", "Error", "Error al finalizar la partida: " + e.getMessage());
            controlador.volver();
        }
    }



    /**
    * Determina el ganador de la partida basándose en las puntuaciones finales.
    * Analiza las puntuaciones de todos los jugadores para identificar
    * al ganador o detectar situaciones de empate.
    * 
    * @pre Las puntuaciones de todos los jugadores deben estar disponibles.
    * @return String con el nombre del ganador o mensaje de empate
    * @post Se devuelve la información del ganador sin modificar las puntuaciones.
    */
    private String determinarGanador() {
        if (jugadoresPuntuaciones == null || jugadoresPuntuaciones.isEmpty()) {
            return "No hay jugadores";
        }
        
        // Encontrar la puntuación máxima
        int puntuacionMaxima = jugadoresPuntuaciones.values().stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);
        
        // Encontrar todos los jugadores con la puntuación máxima (por si hay empate)
        List<String> ganadores = jugadoresPuntuaciones.entrySet().stream()
                .filter(entry -> entry.getValue() == puntuacionMaxima)
                .map(Map.Entry::getKey)
                .collect(java.util.stream.Collectors.toList());
        
        if (ganadores.size() == 1) {
            return ganadores.get(0);
        } else {
            return "Empate entre: " + String.join(", ", ganadores);
        }
    }



    /**
    * Genera las estadísticas finales de la partida.
    * Compila información detallada sobre el rendimiento de todos
    * los jugadores para presentar un resumen completo.
    * 
    * @pre Las puntuaciones y datos de jugadores deben estar disponibles.
    * @return String con las estadísticas formateadas de la partida
    * @post Se devuelve un resumen completo de las estadísticas finales.
    */
    private String generarEstadisticasFinales() {
        StringBuilder stats = new StringBuilder();
        
        // Puntuaciones finales ordenadas de mayor a menor
        List<Map.Entry<String, Integer>> puntuacionesOrdenadas = jugadoresPuntuaciones.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(java.util.stream.Collectors.toList());
        
        stats.append("PUNTUACIONES FINALES:\n");
        for (int i = 0; i < puntuacionesOrdenadas.size(); i++) {
            Map.Entry<String, Integer> entry = puntuacionesOrdenadas.get(i);
            String posicion = (i + 1) + "º";
            String tipoJugador = controlador.esIA(entry.getKey()) ? " (IA)" : " (Humano)";
            stats.append(String.format("%s %s%s: %d puntos\n", 
                    posicion, entry.getKey(), tipoJugador, entry.getValue()));
        }
        
        return stats.toString();
    }



    /**
    * Construye el mensaje final completo de la partida.
    * Combina la información del ganador y las estadísticas
    * en un mensaje cohesivo para mostrar al usuario.
    * 
    * @pre ganador y estadisticas no deben ser null.
    * @param ganador Información del ganador de la partida
    * @param estadisticas Estadísticas detalladas de la partida
    * @return String con el mensaje final completo
    * @post Se devuelve un mensaje formateado con toda la información final.
    */
    private String construirMensajeFinal(String ganador, String estadisticas) {
        StringBuilder mensaje = new StringBuilder();
        
        // Título principal
        mensaje.append(" ¡PARTIDA FINALIZADA! \n\n");
        
        // Ganador
        if (ganador.startsWith("Empate")) {
            mensaje.append(ganador).append("!\n\n");
        } else {
            mensaje.append("🏆 ¡Ganador: ").append(ganador).append("!\n\n");
        }
        
        // Estadísticas
        mensaje.append(estadisticas);
        
        return mensaje.toString();
    }



    /**
    * Muestra el resultado final de la partida usando un popup.
    * Presenta la información final de la partida en una ventana
    * modal con opciones para el usuario.
    * 
    * @pre mensaje no debe ser null ni vacío.
    * @param mensaje Mensaje completo con los resultados finales
    * @post Se muestra el popup con los resultados y se proporciona
    *       la opción de salir de la partida.
    */
    private void mostrarResultadoFinal(String mensaje) {

        List<PausaPopup.PopupButton> buttons = List.of(
            new PausaPopup.PopupButton("Salir", PausaPopup.ButtonStyle.INFO, 
                stage -> {
                    stage.close();
                    Platform.runLater(() -> {
                        controlador.volver();
                    });
                })
        );
        
        PausaPopup.show("Resultado Final", mensaje, buttons);
    }



    /**
    * Centra el tablero en el ScrollPane para una visualización óptima.
    * Configura el tablero dentro del ScrollPane para que se muestre
    * centrado y con barras de desplazamiento apropiadas.
    * 
    * @pre scrollPane y tablero deben estar inicializados.
    * @post El tablero se configura centrado en el ScrollPane con
    *       numeración incluida y barras de desplazamiento según sea necesario.
    */
    private void centrarTableroEnScrollPane() {
        if (scrollPane != null && tablero != null) {
            // Crear el contenedor para el tablero con numeración
            BorderPane tableroConNumeracion = new BorderPane();
            tableroConNumeracion.setCenter(tablero);
            tableroConNumeracion.setLeft(filasNumeracion);
            tableroConNumeracion.setTop(columnasNumeracion);
            tableroConNumeracion.setStyle("-fx-background-color: white;");
            
            // Calcular el tamaño total del tablero incluyendo numeración
            double anchoNumeracion = 30; 
            double altoNumeracion = 30;  
            double anchoTablero = N * CASILLA_SIZE + anchoNumeracion;
            double altoTablero = N * CASILLA_SIZE + altoNumeracion;
            
            // Configurar tamaños mínimos y preferidos
            tableroConNumeracion.setMinWidth(anchoTablero);
            tableroConNumeracion.setMinHeight(altoTablero);
            tableroConNumeracion.setPrefWidth(anchoTablero);
            tableroConNumeracion.setPrefHeight(altoTablero);
            tableroConNumeracion.setMaxWidth(anchoTablero);
            tableroConNumeracion.setMaxHeight(altoTablero);
            
            // Crear un StackPane que actúe como contenedor centrador
            StackPane contenedorCentrado = new StackPane();
            contenedorCentrado.getChildren().add(tableroConNumeracion);
            contenedorCentrado.setStyle("-fx-background-color: white;");
        
            scrollPane.setContent(contenedorCentrado);
            scrollPane.setPannable(true);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.setFitToWidth(true);  
            scrollPane.setFitToHeight(true); 
            
            
            aplicarEstiloScrollbars();
            
            scrollPane.viewportBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
                if (newBounds != null) {
                    double viewportWidth = newBounds.getWidth();
                    double viewportHeight = newBounds.getHeight();
                    
                    // Si el tablero es más pequeño que el viewport, centrarlo
                    if (anchoTablero < viewportWidth) {
                        contenedorCentrado.setMinWidth(viewportWidth);
                    } else {
                        contenedorCentrado.setMinWidth(anchoTablero);
                    }
                    
                    if (altoTablero < viewportHeight) {
                        contenedorCentrado.setMinHeight(viewportHeight);
                    } else {
                        contenedorCentrado.setMinHeight(altoTablero);
                    }
                    
                    // Actualizar políticas de scrollbar según el tamaño
                    Platform.runLater(() -> {
                        boolean needsHScroll = anchoTablero > viewportWidth;
                        boolean needsVScroll = altoTablero > viewportHeight;
                        
                        scrollPane.setHbarPolicy(needsHScroll ? 
                            ScrollPane.ScrollBarPolicy.AS_NEEDED : 
                            ScrollPane.ScrollBarPolicy.NEVER);
                        scrollPane.setVbarPolicy(needsVScroll ? 
                            ScrollPane.ScrollBarPolicy.AS_NEEDED : 
                            ScrollPane.ScrollBarPolicy.NEVER);
                    });
                }
            });
            
            // Centrar el viewport inicialmente si el tablero es más grande
            Platform.runLater(() -> {
                scrollPane.applyCss();
                scrollPane.layout();
                
                // Solo centrar el scroll si el contenido es más grande que el viewport
                double viewportWidth = scrollPane.getViewportBounds().getWidth();
                double viewportHeight = scrollPane.getViewportBounds().getHeight();
                
                if (anchoTablero > viewportWidth) {
                    scrollPane.setHvalue(0.5);
                }
                if (altoTablero > viewportHeight) {
                    scrollPane.setVvalue(0.5);
                }
            });
        }
    }



    /**
    * Método adicional para recentrar el tablero cuando sea necesario
    * Útil después de cambios en el tamaño de la ventana o al cargar una partida
    */
    public void recentrarTablero() {
        if (scrollPane != null) {
            Platform.runLater(() -> {
                double anchoTablero = N * CASILLA_SIZE + 30;
                double altoTablero = N * CASILLA_SIZE + 30;
                double viewportWidth = scrollPane.getViewportBounds().getWidth();
                double viewportHeight = scrollPane.getViewportBounds().getHeight();
                
                // Solo ajustar el scroll si el contenido es más grande que el viewport
                if (anchoTablero > viewportWidth) {
                    scrollPane.setHvalue(0.5);
                } else {
                    scrollPane.setHvalue(0);
                }
                
                if (altoTablero > viewportHeight) {
                    scrollPane.setVvalue(0.5);
                } else {
                    scrollPane.setVvalue(0);
                }
            });
        }
    }

    /**
    * Aplica un estilo a las barras de desplazamiento
    */
    private void aplicarEstiloScrollbars() {
        if (scrollPane != null) {
            scrollPane.getStyleClass().add("modern-scroll-pane");        
            String cssPath = getClass().getResource("/styles/partida.css").toExternalForm();
            scrollPane.getStylesheets().add(cssPath);
            
            Platform.runLater(() -> {
                if (scrollPane.getScene() != null && !scrollPane.getScene().getStylesheets().contains(cssPath)) {
                    scrollPane.getScene().getStylesheets().add(cssPath);
                }
            });
            
            // scrollpane listeners
            scrollPane.viewportBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
                if (newBounds != null && scrollPane.getContent() != null) {
                    double contentWidth = scrollPane.getContent().getBoundsInLocal().getWidth();
                    double contentHeight = scrollPane.getContent().getBoundsInLocal().getHeight();
                    
                    // Comprobar si necesitamos scrollbars
                    boolean needsHorizontalScroll = contentWidth > newBounds.getWidth();
                    boolean needsVerticalScroll = contentHeight > newBounds.getHeight();
                    
                    Platform.runLater(() -> {
                        scrollPane.setHbarPolicy(needsHorizontalScroll ? 
                            ScrollPane.ScrollBarPolicy.AS_NEEDED : 
                            ScrollPane.ScrollBarPolicy.NEVER);
                        
                        scrollPane.setVbarPolicy(needsVerticalScroll ? 
                            ScrollPane.ScrollBarPolicy.AS_NEEDED : 
                            ScrollPane.ScrollBarPolicy.NEVER);
                    });
                }
            });
            
            scrollPane.setFitToWidth(false);
            scrollPane.setFitToHeight(false);
        }
    }
    /**
     * Define el tipo de casilla especial según su posición
     */
    private void definirTipoCasilla(CasillaDisplay casilla, int fila, int col) {
        if (N == 15) {
            // Triple Word (TP)
            List<Tuple<Integer, Integer>> twPositions = Arrays.asList(
                new Tuple<>(0, 0), new Tuple<>(0, 7), new Tuple<>(0, 14),
                new Tuple<>(7, 0), new Tuple<>(7, 14),
                new Tuple<>(14, 0), new Tuple<>(14, 7), new Tuple<>(14, 14)
            );
            
            // Triple Letter (TL)
            List<Tuple<Integer, Integer>> tlPositions = Arrays.asList(
                new Tuple<>(1, 5), new Tuple<>(1, 9),
                new Tuple<>(5, 1), new Tuple<>(5, 5), new Tuple<>(5, 9), new Tuple<>(5, 13),
                new Tuple<>(9, 1), new Tuple<>(9, 5), new Tuple<>(9, 9), new Tuple<>(9, 13),
                new Tuple<>(13, 5), new Tuple<>(13, 9)
            );
            
            // Double Word (DP)
            List<Tuple<Integer, Integer>> dwPositions = Arrays.asList(
                new Tuple<>(1, 1), new Tuple<>(1, 13),
                new Tuple<>(2, 2), new Tuple<>(2, 12),
                new Tuple<>(3, 3), new Tuple<>(3, 11),
                new Tuple<>(4, 4), new Tuple<>(4, 10),
                new Tuple<>(10, 4), new Tuple<>(10, 10),
                new Tuple<>(11, 3), new Tuple<>(11, 11),
                new Tuple<>(12, 2), new Tuple<>(12, 12),
                new Tuple<>(13, 1), new Tuple<>(13, 13)
            );
            
            // Double Letter (DL)
            List<Tuple<Integer, Integer>> dlPositions = Arrays.asList(
                new Tuple<>(0, 3), new Tuple<>(0, 11),
                new Tuple<>(2, 6), new Tuple<>(2, 8),
                new Tuple<>(3, 0), new Tuple<>(3, 7), new Tuple<>(3, 14),
                new Tuple<>(6, 2), new Tuple<>(6, 6), new Tuple<>(6, 8), new Tuple<>(6, 12),
                new Tuple<>(7, 3), new Tuple<>(7, 11),
                new Tuple<>(8, 2), new Tuple<>(8, 6), new Tuple<>(8, 8), new Tuple<>(8, 12),
                new Tuple<>(11, 0), new Tuple<>(11, 7), new Tuple<>(11, 14),
                new Tuple<>(12, 6), new Tuple<>(12, 8),
                new Tuple<>(14, 3), new Tuple<>(14, 11)
            );
            
            Tuple<Integer, Integer> posicionActual = new Tuple<>(fila, col);
            
            if (twPositions.contains(posicionActual)) {
                casilla.setTipo(TipoCasilla.PALABRA_TRIPLE);
            } else if (tlPositions.contains(posicionActual)) {
                casilla.setTipo(TipoCasilla.LETRA_TRIPLE);
            } else if (dwPositions.contains(posicionActual)) {
                casilla.setTipo(TipoCasilla.PALABRA_DOBLE);
            } else if (dlPositions.contains(posicionActual)) {
                casilla.setTipo(TipoCasilla.LETRA_DOBLE);
            } else if (fila == 7 && col == 7) {
                casilla.setTipo(TipoCasilla.CENTRO);
            } else {
                casilla.setTipo(TipoCasilla.NORMAL);
            }        
        }
        else {
            if (fila == N/2 && col == N/2) {
                casilla.setTipo(TipoCasilla.CENTRO);
            } else {
                casilla.setTipo(TipoCasilla.NORMAL);        
            }
        }
                
    }
    
    /**
     * Inicializa las fichas del jugador actual con tamaño proporcional
     */
    private void inicializarFichasJugador() {
        if (fichasJugador != null) {
            fichasJugador.getChildren().clear();
            String[] letras = {"S", "C", "R", "A", "BB", "L", "E"};
            int[] puntos = {1, 3, 1, 1, 3, 1, 1};
            
            for (int i = 0; i < 7; i++) {
                Ficha ficha = new Ficha(letras[i], puntos[i]);
                // Establecer tamaño fijo para mantener proporciones
                ficha.setMinSize(CASILLA_SIZE, CASILLA_SIZE);
                ficha.setPrefSize(CASILLA_SIZE, CASILLA_SIZE);
                ficha.setMaxSize(CASILLA_SIZE, CASILLA_SIZE);
                
                configurarEventosFicha(ficha);
                fichasJugador.getChildren().add(ficha);
            }
        }
    }
    
    /**
     * Configura los eventos de mouse para una ficha
     */
    private void configurarEventosFicha(Ficha ficha) {
        // Configurar evento de clic para seleccionar/deseleccionar la ficha
        ficha.setOnMouseClicked(e -> {
            if (fichaSeleccionada == ficha) {
                fichaSeleccionada.deseleccionar();
                fichaSeleccionada = null;
                for (Node node : tablero.getChildren()) {
                    if (node instanceof CasillaDisplay) {
                        ((CasillaDisplay) node).resaltar(false);
                    }
                }
            } else {
                // Deseleccionar la ficha anterior si existe
                if (fichaSeleccionada != null) {
                    fichaSeleccionada.deseleccionar();
                }
                ficha.seleccionar();
                fichaSeleccionada = ficha;                                
            }
            
            e.consume();
        });
    }
 
    /**
     * Coloca una ficha seleccionada en una casilla específica del tablero.
     * Maneja tanto fichas normales como comodines, validando la posición
     * y actualizando la interfaz correspondiente.
     * 
     * @pre fichaSeleccionada no debe ser null y la casilla debe estar vacía.
     * @param casilla Casilla donde se va a colocar la ficha
     * @param fila Fila de la casilla en el tablero
     * @param col Columna de la casilla en el tablero
     * @post La ficha se coloca en la casilla especificada, se actualiza la
     *       visualización y se registra en las fichas del turno actual.
     *       Si es un comodín, se muestra el popup de selección de letra.
     */
    private void colocarFichaEnCasilla(CasillaDisplay casilla, int fila, int col) {
        if (fichaSeleccionada != null && !casilla.tieneFicha()) {
            
            String letraFicha = fichaSeleccionada.getLetra();
            int puntosFicha = fichaSeleccionada.getPuntos();
            
          
            if ("#".equals(letraFicha)) {
                ComodinPopup popup = new ComodinPopup(CASILLA_SIZE, this);
                String letraSeleccionada = popup.mostrarYEsperar();
                
                if (letraSeleccionada == null) {
                    return;
                }
                
            
                puntosFicha = obtenerPuntosPorLetra(letraSeleccionada);
                casilla.colocarFicha(letraSeleccionada, puntosFicha, false);
                
                // Registrar como comodín convertido
                fichasColocadasEnTurnoActual.add(new FichaColocada(
                    "#",
                    letraSeleccionada, 
                    puntosFicha,
                    fila,
                    col,
                    true 
                ));
            } else {
                // Ficha normal
                casilla.colocarFicha(letraFicha, puntosFicha, false);
                
                fichasColocadasEnTurnoActual.add(new FichaColocada(
                    letraFicha,
                    puntosFicha,
                    fila,
                    col
                ));
            }
            
     
            if (fichasJugador != null) {
                fichasJugador.getChildren().remove(fichaSeleccionada);
            }
            
      
            fichaSeleccionada = null;
     
            for (Node node : tablero.getChildren()) {
                if (node instanceof CasillaDisplay) {
                    ((CasillaDisplay) node).resaltar(false);
                }
            }
        }
    }
    


    /**
    * Retira una ficha de una casilla del tablero y la devuelve al rack.
    * Permite al jugador deshacer la colocación de una ficha
    * antes de confirmar la jugada.
    * 
    * @pre casilla debe contener una ficha no confirmada.
    * @param casilla Casilla de la cual retirar la ficha
    * @post La ficha se retira de la casilla y se devuelve al rack
    *       del jugador con sus propiedades originales.
    */    
    private void retirarFichaDeCasilla(CasillaDisplay casilla) {
        if (casilla.tieneFicha() && !casilla.esFichaConfirmada()) {
            
            // Obtener información de la ficha
            String letra = casilla.getLetraFicha();
            int puntos = casilla.getPuntosFicha();
            int fila = casilla.getFila();
            int columna = casilla.getColumna();
            
            // Quitar la ficha de la casilla
            casilla.quitarFicha();
            devolverFichaAJugador(letra, puntos, fila, columna);
        }
    }
    


    /**
    * Verifica si una ficha en la posición especificada ha sido confirmada.
    * Determina si una ficha es parte de una jugada ya confirmada
    * y por tanto no puede ser movida.
    * 
    * @pre fila y columna deben estar dentro de los límites del tablero.
    * @param fila Coordenada de fila a verificar
    * @param columna Coordenada de columna a verificar
    * @return true si la ficha está confirmada, false en caso contrario
    * @post Se devuelve el estado de confirmación sin modificar la ficha.
    */
    public boolean esFichaConfirmada(int fila, int columna) {
        return posiciones.get(new Tuple<>(fila, columna)) != null;
    }
    


    /**
    * Notifica que una ficha ha sido retirada del tablero.
    * Actualiza las estructuras de datos cuando una ficha
    * es removida de una posición del tablero.
    * 
    * @pre fila y columna deben corresponder a una posición válida.
    * @param fila Coordenada de fila de la ficha retirada
    * @param columna Coordenada de columna de la ficha retirada
    * @post Se actualiza la lista de fichas colocadas en el turno actual
    *       removiendo la ficha de la posición especificada.
    */
    public void fichaRetiradaDelTablero(int fila, int columna) {
        fichasColocadasEnTurnoActual.removeIf(f -> f.fila == fila && f.columna == columna);
    }
    


    /**
    * Devuelve una ficha retirada del tablero al rack del jugador.
    * Maneja la lógica de devolución de fichas, incluyendo
    * el tratamiento especial de comodines.
    * 
    * @pre La ficha debe ser retirable (no confirmada).
    * @param letra Letra de la ficha a devolver
    * @param puntos Puntos de la ficha
    * @param fila Coordenada de fila de origen
    * @param columna Coordenada de columna de origen
    * @post La ficha se devuelve al rack con sus propiedades originales,
    *       restaurando comodines a su estado inicial si es necesario.
    */
    public void devolverFichaAJugador(String letra, int puntos, int fila, int columna) {
        if (esFichaConfirmada(fila, columna)) {
                return;
            }
            
            // Buscar la ficha en fichasColocadasEnTurnoActual para ver si es comodín
            FichaColocada fichaColocada = null;
            for (FichaColocada f : fichasColocadasEnTurnoActual) {
                if (f.fila == fila && f.columna == columna) {
                    fichaColocada = f;
                    break;
                }
            }
            
            fichaRetiradaDelTablero(fila, columna);
            
            // Si es comodín, devolver el comodín original
            String letraParaRack;
            int puntosParaRack;
            
            if (fichaColocada != null && fichaColocada.esComodin()) {
                letraParaRack = "#"; // Devolver como comodín
                puntosParaRack = 0; // Los comodines valen 0
            } else {
                letraParaRack = letra;
                puntosParaRack = puntos;
            }
            
            // Crear nueva ficha para el rack
            Ficha nuevaFicha = new Ficha(letraParaRack, puntosParaRack);
            nuevaFicha.setMinSize(CASILLA_SIZE, CASILLA_SIZE);
            nuevaFicha.setPrefSize(CASILLA_SIZE, CASILLA_SIZE);
            nuevaFicha.setMaxSize(CASILLA_SIZE, CASILLA_SIZE);
            configurarEventosFicha(nuevaFicha);
            
            if (fichasJugador != null) {
                fichasJugador.getChildren().add(nuevaFicha);
            }
    }


    
    /**
     * Cancela la jugada actual retirando todas las fichas no confirmadas.
     * Devuelve las fichas al rack del jugador y limpia el estado de la jugada.
     * 
     * @pre No hay precondiciones específicas.
     * @post Todas las fichas colocadas en el turno actual se retiran del tablero
     *       y se devuelven al rack del jugador. Se limpia el estado de la jugada.
     */
    private void cancelarJugada() {
        
        // Retirar todas las fichas colocadas en este turno y devolverlas al rack
        List<FichaColocada> copiaDeFichasColocadas = new ArrayList<>(fichasColocadasEnTurnoActual);
        for (FichaColocada ficha : copiaDeFichasColocadas) {
            // Buscar la casilla correspondiente
            CasillaDisplay casilla = null;
            for (Node node : tablero.getChildren()) {
                if (node instanceof CasillaDisplay) {
                    CasillaDisplay casillaActual = (CasillaDisplay) node;
                    if (casillaActual.getFila() == ficha.fila && casillaActual.getColumna() == ficha.columna) {
                        casilla = casillaActual;
                        break;
                    }
                }
            }
            
            if (casilla != null && casilla.tieneFicha() && !casilla.esFichaConfirmada()) {
                // Quitar la ficha de la casilla
                casilla.quitarFicha();
                
                String letraParaDevolver = ficha.esComodin() ? "#" : ficha.letra;
                int puntosParaDevolver = ficha.esComodin() ? 0 : ficha.puntos;                
                // Devolver la ficha al rack
                devolverFichaAJugador(ficha.letra, ficha.puntos, ficha.fila, ficha.columna);
            }
        }
        
        // Limpiar la lista de fichas colocadas en este turno
        fichasColocadasEnTurnoActual.clear();
    }
    


    /**
    * Maneja la acción de pausar la partida.
    * Muestra un menú de opciones que permite al jugador
    * pausar, guardar o salir de la partida actual.
    * 
    * @pre La partida debe estar en curso.
    * @post Se muestra el menú de pausa con opciones para reanudar,
    *       guardar o salir de la partida.
    */    
    @FXML
    private void pausarPartida() {
        
        // Definir botones del menú de pausa
        List<PausaPopup.PopupButton> buttons = List.of(
            new PausaPopup.PopupButton("Reanudar", PausaPopup.ButtonStyle.SUCCESS, 
                stage -> {
                    stage.close();
                }),
                
            new PausaPopup.PopupButton("Guardar", PausaPopup.ButtonStyle.INFO,
                stage -> {
                    // Llamar a guardar partida
                    stage.close();
                    Platform.runLater(() -> {
                        guardarPartida(jugadores, jugadorActualIndex);
                    });                    
                }),
                
            new PausaPopup.PopupButton("Salir", PausaPopup.ButtonStyle.DANGER,
                stage -> {
                    // Mostrar confirmación de salida
                    stage.close();                    
                    Platform.runLater(() -> {
                        salirPartida();
                    }); 
                })
        );
        
        // Mostrar el popup
        PausaPopup.show("Juego pausado", null, buttons);
    }
    


    /**
    * Guarda el estado actual de la partida en el sistema de persistencia.
    * Utiliza el controlador para persistir el estado completo
    * de la partida incluyendo jugadores y turno actual.
    * 
    * @pre jugadores no debe ser null, turnoActual debe ser válido.
    * @param jugadores Lista de jugadores en la partida
    * @param turnoActual Índice del jugador que tiene el turno
    * @post La partida se guarda exitosamente en el sistema de persistencia
    *       y se muestra confirmación al usuario.
    * @throws Exception si hay errores durante el proceso de guardado
    */
    private void guardarPartida(List<String> jugadores, int turnoActual) {
        try {
            boolean guardadoExitoso = controlador.guardarPartida(jugadores, turnoActual);
            if (guardadoExitoso) {
                this.saved = true;
                PausaPopup.showInfo(
                    "Información",
                    "Partida guardada exitosamente.",
                    stage -> {
                        stage.close();
                    }
                );
            } else {
                controlador.mostrarAlerta("error", "Error al guardar", "No se pudo guardar la partida");
            }
        } catch (Exception e) {
            e.printStackTrace();
            controlador.mostrarAlerta("error", "Error", "Error al guardar la partida: " + e.getMessage());
        }
    }



    /**
    * Maneja la acción de salir de la partida.
    * Coordina el proceso de salida incluyendo confirmación
    * si hay progreso no guardado y liberación de recursos.
    * 
    * @pre La partida debe estar en curso.
    * @post Si hay progreso no guardado, se solicita confirmación.
    *       Se liberan los jugadores y se regresa al menú principal.
    */
    private void salirPartida() {
        if (!saved) {
            PausaPopup.showConfirmation(
                "Confirmar salida",
                "¿Estás seguro de que quieres salir?\nSe perderá el progreso no guardado.",
                stage -> {
                    // Confirmado
                    stage.close();
                    if (!controlador.getCargado()) controlador.liberarJugadores();
                    else controlador.setCargado(false);
                    controlador.volver();
                },
                stage -> {
                    stage.close();
                }
            );        
        }
        else {
            controlador.volver();
        }
    }
    


    /**
    * Método de utilidad para salir de la vista.
    * Proporciona una forma simple de regresar al menú principal
    * sin procesamiento adicional.
    * 
    * @pre controlador no debe ser null.
    * @post Se regresa al menú principal a través del controlador.
    */
    private void salir() {
        if (controlador != null) {
            controlador.volver();
        }
    }
}
