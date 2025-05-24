package scrabble.presentation.viewControllers;

import java.io.IOException;
import java.util.List;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import scrabble.MainApplication;
import scrabble.presentation.PresentationController;
import scrabble.presentation.popups.EliminarJugadorPopup;
import scrabble.presentation.views.RankingView;

/**
 * Controlador para la vista de gestión de rankings.
 * Maneja la interacción entre la vista y el modelo.
 */

public class ControladorRankingView {

    // Singleton instance
    private static ControladorRankingView instance;

    private final PresentationController presentationController;

    private Stage stage;
    private Parent vistaAnterior;
    private BorderPane layout;

    /**
     * Constructor de la clase ControladorRankingView.
     *
     * @param stage La ventana principal de la aplicación.
     */

    private ControladorRankingView() {
        this.presentationController = PresentationController.getInstance();
    }

    /**
     * Obtiene la instancia singleton del ControladorRanking.
     * @param stage La ventana principal de la aplicación.
     * @return La instancia única del ControladorRanking.
     */
    public static ControladorRankingView getInstance() {
        if (instance == null) {
            instance = new ControladorRankingView();
        }
        return instance;
    }

    /**
     * Inicializa la vista de gestión de rankings.
     */

    public void inicializar(Stage stage) {
        // Mostrar la vista inicial
        this.stage = stage;
        layout = new BorderPane();
        RankingView vistaRanking = new RankingView(this);
        layout.setCenter(vistaRanking.getView());
        Scene escena = new Scene(layout, stage.getWidth(), stage.getHeight());
        stage.setScene(escena);
        stage.setTitle("Gestión de Ranking");
        stage.setMaximized(true);
        stage.show();
    }

    /**
     * Muestra la vista de gestión de rankings.
     */

    public void mostrarVistaRanking() {
        layout.setCenter(new RankingView(this).getView());
    }

    public void mostrarPopupEliminarJugador() {
        EliminarJugadorPopup popup = new EliminarJugadorPopup();
        popup.mostrar(stage);
    }

    /*
     * Setea la vista anterior para poder volver a ella.
     *
     * @param vista La vista anterior a la que se quiere volver.
     */

    public void setVistaAnterior(Parent vista) {
        this.vistaAnterior = vista;
    }

    /**
     * Vuelve a la vista anterior.
     */

    public void volverAVistaAnterior() {
        if (vistaAnterior != null) {
            layout.setCenter(vistaAnterior);
        }
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
            
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("error", "Error", "Error al volver al menú principal.");
        }
    }

    /**
     * Devuelve la lista de usuarios de ranking.
     *
     * @return Lista de usuarios de ranking.
     */

    public List<String> getUsuariosRanking() {
        return presentationController.getUsuariosRanking();
    }

    /**
     * Elimina a un usuario del ranking.
     * Además, resetea su puntuación total a cero.
     * 
     * @param nombre Nombre del usuario
     * @return true si se eliminó correctamente, false en caso contrario
     */
    public boolean eliminarUsuarioRanking(String nombre) {
        // Primero, capturar la información antes de eliminar
        return presentationController.eliminarUsuarioRanking(nombre);
    }

    /**
     * Devuelve la puntuación máxima del jugador.
     *
     * @param nombre Nombre del jugador.
     * @return Puntuación máxima del jugador.
     */

    public int getPuntuacionMaxima(String nombre) {
        return presentationController.getPuntuacionMaxima(nombre);
    }

    /**
     * Devuelve la puntuación media del jugador.
     *
     * @param nombre Nombre del jugador.
     * @return Puntuación media del jugador.
     */

    public double getPuntuacionMedia(String nombre) {
        return presentationController.getPuntuacionMedia(nombre);
    }

    /**
     * Devuelve el número de partidas jugadas por el jugador.
     *
     * @param nombre Nombre del jugador.
     * @return Número de partidas jugadas por el jugador.
     */

    public int getPartidasJugadas(String nombre) {
        return presentationController.getPartidasJugadas(nombre);
    }

    /**
     * Devuelve el número de victorias del jugador.
     *
     * @param nombre Nombre del jugador.
     * @return Número de victorias del jugador.
     */

    public int getvictorias(String nombre) {
        return presentationController.getvictorias(nombre);
    }

    /**
     * Devuelve la puntuación total del jugador.
     *
     * @param nombre Nombre del jugador.
     * @return Puntuación total del jugador.
     */

    public int getPuntuacionTotal(String nombre) {
        return presentationController.getPuntuacionTotal(nombre);
    }

     /*
     * Muestra un aviso al usuario.
     * @param tipo Tipo de alerta
     * @param titulo Titulo de la alerta
     * @param mensaje Mensaje a mostrar
     */

    public void mostrarAlerta(String tipo, String titulo, String mensaje) {
        presentationController.mostrarAlerta(tipo, titulo, mensaje);
    }

    /**
     * Muestra un mensaje de error al usuario.
     *
     * @param mensaje Mensaje a mostrar.
     */

    // public void mostrarError(String mensaje) {
    //     // Implementar lógica para mostrar un mensaje de error al usuario
    //     Alert alert = new Alert(Alert.AlertType.WARNING, mensaje, ButtonType.OK);
    //     alert.setTitle("Advertencia");
    //     alert.setHeaderText(null);
    //     alert.showAndWait();
    // }
}
