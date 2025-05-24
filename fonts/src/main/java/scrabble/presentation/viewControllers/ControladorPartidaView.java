package scrabble.presentation.viewControllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import scrabble.MainApplication;
import scrabble.excepciones.ExceptionDiccionarioExist;
import scrabble.excepciones.ExceptionPalabraInvalida;
import scrabble.helpers.Direction;
import scrabble.helpers.Triple;
import scrabble.helpers.Tuple;
import scrabble.presentation.PresentationController;
import scrabble.presentation.views.ConfigPartidaView;
import scrabble.presentation.views.GestionPartidaView;
import scrabble.presentation.views.VistaTablero;
/**
 * Controlador central para todas las operaciones relacionadas con partidas
 * Usa el enfoque refactorizado con BorderPane central
 */
public class ControladorPartidaView {
    
    private final Stage stage;
    private Parent vistaAnterior;
    private BorderPane layout;
    ConfigPartidaView vistaConfig;    
    private PresentationController presentationController;
    boolean cargado;
    // Datos temporales para la partida
    private String diccionario;
    private Integer size;
    private ArrayList<String> jugadores;
    /**
     * Constructor que recibe el Stage principal
     */
    public ControladorPartidaView(Stage stage) {
        this.presentationController = PresentationController.getInstance();
        vistaConfig = new ConfigPartidaView(this);
        this.stage = stage;
        this.diccionario = "-- No se ha seleccionado ningún diccionario --";
        this.size = 15;
        this.jugadores = new ArrayList<String>();
        this.cargado = false;
        inicializar();
    }
    
    /**
     * Inicializa el controlador mostrando la vista principal de gestión
     */
    private void inicializar() {
        layout = new BorderPane();
        mostrarVistaGestion();
        
        Scene scene = new Scene(layout, stage.getScene().getWidth(), stage.getScene().getHeight());
        stage.setScene(scene);
        stage.setTitle("Gestión de Partidas");
        stage.show();
    }
    
    /**
     * Muestra la vista de gestión de partidas
     */
    public void mostrarVistaGestion() {
        GestionPartidaView vistaGestion = new GestionPartidaView(this);
 
        layout.setCenter(vistaGestion.getView());
        stage.setTitle("Gestión de Partidas");
    }
    
    /**
     * Indica si la partida actual ha sido una partida cargada o no.
     * @return Retorna booleano que es true si la partida ha sido cargada, false en caso contrario.
     */
    public boolean getCargado() {
        return this.cargado;
    }

    public void setCargado(boolean cargado){
        this.cargado = cargado;
    }

    /**
     * Muestra la vista de configuración de partida
     */
    public void mostrarVistaConfiguracion() {
        List<String> jugadores = presentationController.getAllJugadoresDisponibles();
        for (String nombre : jugadores) {
                System.err.println("DESDE PARTIDA VIEW: " + nombre);
        }
        vistaConfig.setJugadoresReady(jugadores);        
        layout.setCenter(vistaConfig.getView());
        stage.setTitle("SCRABBLE");
    }
    
    /**
    * Muestra la vista del tablero de juego
    */
    public void mostrarVistaTablero() {
        
        VistaTablero vistaTablero = new VistaTablero(this);
        Parent viewTablero = vistaTablero.getView();
        vistaTablero.setTableroSize(size);
        layout.setCenter(viewTablero);
        stage.setTitle("Scrabble");
        

        Scene escena = stage.getScene();
        if (escena != null) {
            try {
                String cssResource = "/styles/button.css";
                URL cssUrl = getClass().getResource(cssResource);
                if (cssUrl != null && !escena.getStylesheets().contains(cssUrl.toExternalForm())) {
                    escena.getStylesheets().add(cssUrl.toExternalForm());
                    System.out.println("CSS aplicado directamente a la escena desde ControladorPartidaView");
                }
            } catch (Exception e) {
                System.err.println("Error al aplicar CSS a la escena: " + e.getMessage());
            }
        }
        
        
        Platform.runLater(() -> {
            Button btnConfirmar = (Button) viewTablero.lookup("#btnConfirmar");
            Button btnCancelar = (Button) viewTablero.lookup("#btnCancelar");
            Button btnPasarTurno = (Button) viewTablero.lookup("#btnPasarTurno");
            Button btnCambiarFichas = (Button) viewTablero.lookup("#btnCambiarFichas");
            Button btnPausarPartida = (Button) viewTablero.lookup("#btnPausarPartida");
            Button btnVolver = (Button) viewTablero.lookup("#btnVolver");
            
            if (btnConfirmar != null) {
                btnConfirmar.getStyleClass().addAll("btn-effect", "btn-success");
                System.out.println("Aplicado estilo a btnConfirmar");
            }
            if (btnCancelar != null) {
                btnCancelar.getStyleClass().addAll("btn-effect", "btn-danger");
                System.out.println("Aplicado estilo a btnCancelar");
            }
            if (btnPasarTurno != null) {
                btnPasarTurno.getStyleClass().addAll("btn-effect", "btn-primary");
                System.out.println("Aplicado estilo a btnPasarTurno");
            }
            if (btnCambiarFichas != null) {
                btnCambiarFichas.getStyleClass().add("btn-effect");
                System.out.println("Aplicado estilo a btnCambiarFichas");
            }
            if (btnPausarPartida != null) {
                btnPausarPartida.getStyleClass().addAll("btn-effect", "btn-warning");
                System.out.println("Aplicado estilo a btnPausarPartida");
            }
            if (btnVolver != null) {
                btnVolver.getStyleClass().add("btn-effect");
                System.out.println("Aplicado estilo a btnVolver");
            }
        });

    }
       
    /**
     * Crea una nueva partida (navega a configuración)
     */
    public void crearNuevaPartida() throws ExceptionDiccionarioExist, IOException, ExceptionPalabraInvalida {
        // presentationController.importDiccionaryDEBUG();
        mostrarVistaConfiguracion();
    }
    
public void iniciarPartida() {
    System.err.println(size);
    if (diccionario == null) {
        mostrarAlerta("error", "Diccionacio null", "¡Por favor, selecciona un diccionario antes de continuar!");
    } else if (jugadores.size() < 2) {
        System.err.println("Error: Debe seleccionar al menos 2 jugadores");
        System.err.println("Jugadores actuales: " + jugadores.size());
        mostrarAlerta("error","Jugadores insuficientes", "¡Por favor, selecciona al menos 2 jugadores antes de continuar!");
    } else if (size < 15) {
        mostrarAlerta("error","Tamaño incorrecto", "¡Por favor, configura un tamaño de tablero correcto antes de continuar!");        
    }
    else {
        boolean inicioExitoso = presentationController.iniciarPartida(jugadores, diccionario, size);
        if (inicioExitoso) {
            vistaConfig.reset();
            mostrarVistaTablero();
        } else {
            mostrarAlerta("warning","Error al iniciar partida", "No se pudo iniciar la partida");
        }
    }
}

    // Método para cargar una partida existente
    public void cargarPartida(Integer idPartida) {
        try {
            presentationController.cargarPartida(idPartida);
            this.diccionario = presentationController.getNombreDiccionario();
            this.size = presentationController.getTableroSize();
            vistaConfig.reset();
            cargado = true;
            mostrarVistaTablero();
        } catch (Exception e) {
            mostrarAlerta("warning","Error al cargar partida", "No se pudo cargar la partida: " + e.getMessage());
        }
    }

    /**
    * Obtiene la cantidad de fichas restantes en la bolsa
    */
    public int getCantidadFichasRestantes() {
        return presentationController.getCantidadFichasRestantes();
    }
        // Método utilizado por VistaTablero para obtener el estado de la partida
    public String getEstadoPartida(String nombreJugador) {
        return presentationController.getEstadoPartida(nombreJugador);
    }

    // Método utilizado por VistaTablero para obtener el rack del jugador
    public Map<String, Integer> getRackJugador(String nombreJugador) {
        return presentationController.getRackJugador(nombreJugador);
    }

    // Método utilizado por VistaTablero para verificar si un movimiento es válido
    public boolean esMovimientoValido(Triple<String, Tuple<Integer, Integer>, Direction> jugada, Map<String, Integer> rack) {
        return presentationController.esMovimientoValido(jugada, rack);
    }

    // Método utilizado por VistaTablero para realizar un turno
    public int realizarTurnoPartida(String nombreJugador, Triple<String, Tuple<Integer, Integer>, Direction> jugada) {
        return presentationController.realizarTurnoPartida(nombreJugador, jugada);
    }

    // Método utilizado por VistaTablero para actualizar la puntuación de un jugador
    public void actualizarJugador(String nombreJugador, int puntos) {
        presentationController.actualizarJugadores(nombreJugador, puntos);
    }

    // Método utilizado por VistaTablero para comprobar si la partida ha finalizado
    public void comprobarFinPartida(Map<String, Integer> jugadoresSeleccionados) {
        presentationController.comprobarFinPartida(jugadoresSeleccionados);
    }

    // Método utilizado por VistaTablero para verificar si el juego ha terminado
    public boolean isJuegoTerminado() {
        return presentationController.isJuegoTerminado();
    }

    // Método utilizado por VistaTablero para finalizar el juego
    public String finalizarJuego(Map<String, Integer> jugadoresSeleccionados) {
        return presentationController.finalizarJuego(jugadoresSeleccionados);
    }

    // Método utilizado por VistaTablero para intercambiar fichas
    public boolean intercambiarFichas(String nombreJugador, List<String> fichas) {
        return presentationController.intercambiarFichas(nombreJugador, fichas);
    }

    // Método utilizado por VistaTablero para verificar si un jugador es IA
    public boolean esIA(String nombreJugador) {
        return presentationController.esIA(nombreJugador);
    }

    // Método utilizado por VistaTablero para obtener los jugadores actuales
    public Map<String, Integer> getJugadoresActuales() {
        return presentationController.getJugadoresActuales();
    }

    // Método utilizado por VistaTablero para liberar jugadores al salir
    public void liberarJugadores() {
        presentationController.liberarJugadoresActuales();
    }

    // Método utilizado por VistaTablero para guardar la partida
    public boolean guardarPartida() {
        return presentationController.guardarPartida();
    }

    // Método para obtener las partidas guardadas
    public List<Integer> getPartidasGuardadasID() {
        return presentationController.getPartidasGuardadas();
    }

    // Método para eliminar una partida guardada
    public boolean eliminarPartidaGuardada(Integer idPartida) {
        return presentationController.eliminarPartidaGuardada(idPartida);
    }
    
    /**
     * Vuelve al menú principal
     */
    public void volverAMenuPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("/views/main-view.fxml"));
            Parent mainView = loader.load();
            
            Scene currentScene = stage.getScene();
            Scene newScene = new Scene(mainView, currentScene.getWidth(), currentScene.getHeight());
            stage.setScene(newScene);
            stage.setTitle("SCRABBLE");
            
            // Mantener maximizado si estaba maximizado
            if (stage.isMaximized()) {
                stage.setMaximized(false);
                stage.setMaximized(true);
            }
            
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void mostrarAlerta(String type, String title, String message) {
        presentationController.mostrarAlerta(type, title, message);
    }

    /**
     * Establece la vista anterior para poder volver
     */
    public void setVistaAnterior(Parent vista) {
        this.vistaAnterior = vista;
    }
    
    /**
     * Vuelve a la vista anterior
     */
    public void volverAVistaAnterior() {
        if (vistaAnterior != null) {
            layout.setCenter(vistaAnterior);
        } else {
            mostrarVistaGestion();
        }
    }
    
    /**
     * Vuelve a la vista de gestión de partidas
     */
    public void volver() {
        vistaConfig.reset();
        mostrarVistaGestion();
    }
    
    /**
     * Devuelve el tamaño del tablero actual
     */    
    public Integer getSize() {
        return this.size;    
    }

    /**
     * Devuelve el tamaño del diccionario actual
     */ 
    public String getDiccionario() {
        return this.diccionario;
    }


    /**
     * Devuelve el listado de jugadores actuales en la partida
     */ 
    public List<String> getJugadoresDisponibles() {
        return this.jugadores;
    }

    public List<String> getAllJugadores() {
       return presentationController.getAllJugadores(); 
    }
    /**
     * Configura el tamaño del tablero
     */
    public void setSize(Integer size) {
        this.size = size;
    }
    
    /**
     * Configura el diccionario de la partida
     */    
    public void setDiccionario(String diccName) {
        this.diccionario = diccName;
    }

    public List<String> getAllDiccionarios() {
        return presentationController.getAllDiccionariosDisponibles();    
    }

    /**
     * Añade un jugador que participará en la partida
     */ 
    public void addJugador(String jugador) {
        this.jugadores.add(jugador);
    }

    public void resetJugadores() {
        this.jugadores = new ArrayList<>();
    }

    public List<Integer> getPartidasGuardadas() {
        return presentationController.getPartidasGuardadas();
    }
    

    public Stage getStage() {
        return stage;
    }
    
    /**
     * Método usado por VistaTablero para la funcionalidad existente
     */
    public void mostrarTurnoUsuario() {
        // TODO: Implementar si es necesario
    }
}