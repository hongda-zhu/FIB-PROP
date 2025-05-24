package scrabble.presentation.viewControllers;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import scrabble.MainApplication;
import scrabble.presentation.PresentationController;
import scrabble.presentation.popups.CrearJugadorPopup;
import scrabble.presentation.views.GestionJugadoresView;
import scrabble.presentation.views.HistorialJugadorView;

/**
 * Controlador central para todas las operaciones relacionadas con jugadores
 */
public class ControladorJugadoresView {
    
    private final Stage stage;
    private BorderPane layout;
    private PresentationController presentationController;
    private Parent vistaAnterior;
    
    // Jugador seleccionado actualmente para ver historial
    private String jugadorSeleccionado;
    
    /**
     * Constructor que recibe el Stage principal
     */
    public ControladorJugadoresView(Stage stage) {
        this.presentationController = PresentationController.getInstance();
        this.stage = stage;
        inicializar();
    }
    
    /**
     * Inicializa el controlador mostrando la vista principal de gestión
     */
    private void inicializar() {
        layout = new BorderPane();
        mostrarVistaGestionJugadores();
        
        Scene scene = new Scene(layout, stage.getScene().getWidth(), stage.getScene().getHeight());
        
        // Aplicar CSS
        try {
            String cssResource = "/styles/button.css";
            URL cssUrl = getClass().getResource(cssResource);
            if (cssUrl != null && !scene.getStylesheets().contains(cssUrl.toExternalForm())) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
                System.out.println("CSS aplicado a la escena desde ControladorJugadoresView");
            }
        } catch (Exception e) {
            System.err.println("Error al aplicar CSS a la escena: " + e.getMessage());
        }
        
        stage.setScene(scene);
        stage.setTitle("Gestión de Jugadores");
        stage.show();
    }
    
    /**
     * Muestra la vista principal de gestión de jugadores
     */
    public void mostrarVistaGestionJugadores() {
        GestionJugadoresView vistaGestion = new GestionJugadoresView(this);
        layout.setCenter(vistaGestion.getView());
        stage.setTitle("Gestión de Jugadores");
    }
    
    /**
     * Muestra la vista de historial de un jugador
     */
    public void mostrarVistaHistorialJugador(String nombreJugador) {
        this.jugadorSeleccionado = nombreJugador;
        HistorialJugadorView vistaHistorial = new HistorialJugadorView(this, nombreJugador);
        layout.setCenter(vistaHistorial.getView());
        stage.setTitle("Gestión de Jugadores - Ver historial");
    }
    
    /**
     * Muestra el popup para crear un nuevo jugador
     */
    public void mostrarVistaCrearJugador() {
        CrearJugadorPopup popup = new CrearJugadorPopup(this);
        popup.mostrar(stage);
    }
    
    /**
     * Crea un nuevo jugador con el nombre especificado
     * Este método ya no se usa directamente desde la vista, sino que se maneja en el popup
     */
    public boolean crearJugador(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            mostrarAlerta("error", "Error", "El nombre del jugador no puede estar vacío");
            return false;
        }
        
        boolean creado = presentationController.crearJugador(nombre);
        return creado;
    }
    
    public void eliminarJugador(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            presentationController.mostrarAlerta("warning", "Advertencia", "Por favor, seleccione un jugador para eliminar");
            return;
        }
        
        // Eliminar jugador en el modelo
        try {
            presentationController.eliminarJugador(nombre);
            // Si no lanza excepción, significa que se eliminó correctamente
            presentationController.mostrarAlerta("success", "Información", "Jugador eliminado exitosamente");
            // Actualizar la vista
            mostrarVistaGestionJugadores();
        } catch (scrabble.excepciones.ExceptionUserInGame e) {
            // Capturar específicamente la excepción de usuario en juego
            presentationController.mostrarAlerta("error", "Error", 
                "No se pudo eliminar el jugador: " + e.getMessage());
        } catch (Exception e) {
            // Capturar cualquier otra excepción que pueda ocurrir
            presentationController.mostrarAlerta("error", "Error", 
                "Error inesperado al eliminar el jugador: " + e.getMessage());
        }
    }
    
    /**
     * Busca jugadores por nombre (filtrado)
     */
    public List<String> buscarJugadores(String patron) {
        return presentationController.buscarJugadoresPorNombre(patron);
    }
    
    /**
     * Obtiene todos los jugadores
     */
    public List<String> getJugadores() {
        return presentationController.getAllJugadores();
    }
    
    /**
     * Obtiene las estadísticas de un jugador
     */
    public EstadisticasJugador getEstadisticasJugador(String nombre) {

        EstadisticasJugador stats = new EstadisticasJugador();
    
        int total = presentationController.getPuntuacionTotal(nombre);
        int maximo = presentationController.getPuntuacionMaxima(nombre);
        double media = presentationController.getPuntuacionMedia(nombre);
        int partidasJugadas = presentationController.getPartidasJugadas(nombre);
        int victorias = presentationController.getVictorias(nombre);
        String ratio;
        if (partidasJugadas == 0) {
            ratio = "0%";
        } else {
            double r = (double) victorias / partidasJugadas;
            ratio = String.format("%.2f%%", r * 100); 
        }
        stats.setPuntuacionTotal(total);
        stats.setPuntuacionMaxima(maximo);
        stats.setPuntuacionMedia(media);
        stats.setPartidasJugadas(partidasJugadas);
        stats.setVictorias(victorias);
        stats.setRatioVictorias(ratio);
        
        List<Integer> historial = presentationController.getPuntuacionesUsuario(nombre);
        stats.setHistorialPuntuaciones(historial);
        
        return stats;
    }
    
    public void mostrarAlerta(String type, String title, String message) {
        presentationController.mostrarAlerta(type, title, message);
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
            
            // Mantener CSS
            if (!currentScene.getStylesheets().isEmpty()) {
                newScene.getStylesheets().addAll(currentScene.getStylesheets());
            }
            
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
    
    /**
     * Vuelve a la vista anterior
     */
    public void volver() {
        if (vistaAnterior != null) {
            layout.setCenter(vistaAnterior);
        } else {
            mostrarVistaGestionJugadores();
        }
    }
    
    /**
     * Establece la vista anterior para poder volver
     */
    public void setVistaAnterior(Parent vista) {
        this.vistaAnterior = vista;
    }
    
    /**
     * Muestra una alerta
     */
    private void showAlert(Alert.AlertType type, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(type.toString());
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    /**
     * Verifica si un jugador es una IA.
     * @param nombre
     * @return true si el jugador es una IA, false si es un jugador humano
     */
    public boolean isEnPartida (String nombre) {
        return presentationController.isEnPartida(nombre);
    }

    /**
     * Verifica si un jugador humano está en una partida.
     * 
     * @param nombre Nombre del jugador
     * @return true si está en partida, false en caso contrario
     */
    public boolean esIA(String nombre) {
        return presentationController.esIA(nombre);
    }

    public String getNombrePartidaActual(String nombre) {
        return presentationController.getNombrePartidaActual(nombre);
    }

    public int getPuntuacionTotal(String nombre) {
        return presentationController.getPuntuacionTotal(nombre);
    }

    public List<Integer> getPuntuacionesUsuario(String nombre) {
        return presentationController.getPuntuacionesUsuario(nombre);
    }
    /**
     * Clase interna para manejar estadísticas de jugador
     */
    public class EstadisticasJugador {
        private int puntuacionTotal;
        private int puntuacionMaxima;
        private double puntuacionMedia;
        private int partidasJugadas;
        private int victorias;
        private String ratioVictorias;
        private List<Integer> historialPuntuaciones;
        
        // Getters y setters
        public int getPuntuacionTotal() { return puntuacionTotal; }
        public void setPuntuacionTotal(int puntuacionTotal) { this.puntuacionTotal = puntuacionTotal; }
        
        public int getPuntuacionMaxima() { return puntuacionMaxima; }
        public void setPuntuacionMaxima(int puntuacionMaxima) { this.puntuacionMaxima = puntuacionMaxima; }
        
        public String getPuntuacionMedia() { return String.format("%.2f", puntuacionMedia); }
        public void setPuntuacionMedia(double puntuacionMedia) { this.puntuacionMedia = puntuacionMedia; }
        
        public int getPartidasJugadas() { return partidasJugadas; }
        public void setPartidasJugadas(int partidasJugadas) { this.partidasJugadas = partidasJugadas; }
        
        public int getVictorias() { return victorias; }
        public void setVictorias(int victorias) { this.victorias = victorias; }
        
        public String getRatioVictorias() { return ratioVictorias; }
        public void setRatioVictorias(String ratioVictorias) { this.ratioVictorias = ratioVictorias; }
        
        public List<Integer> getHistorialPuntuaciones() { return historialPuntuaciones; }
        public void setHistorialPuntuaciones(List<Integer> historialPuntuaciones) { this.historialPuntuaciones = historialPuntuaciones; }
    }
}