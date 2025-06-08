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
 * Controlador para la gestión de rankings y estadísticas de jugadores.
 * Maneja la interacción entre la vista de ranking y el modelo de datos,
 * coordinando operaciones de consulta, filtrado y eliminación de jugadores
 * del sistema de ranking mediante el patrón Singleton.
 * 
 * Características principales:
 * - Gestión centralizada de visualización de rankings y estadísticas
 * - Operaciones de eliminación de jugadores del ranking con confirmación
 * - Integración con sistema de temas para consistencia visual
 * - Navegación fluida con preservación de estado de ventana
 * - Acceso a métricas detalladas de rendimiento de jugadores
 * 
 * @version 1.0
 * @since 1.0
 */
public class ControladorRankingView {

    // Singleton instance
    private static ControladorRankingView instance;

    private final PresentationController presentationController;

    private Stage stage;
    private Parent vistaAnterior;
    private BorderPane layout;

    /**
     * Constructor privado para implementación del patrón Singleton.
     * Inicializa la conexión con el controlador de presentación para
     * acceso a funcionalidades del sistema de ranking.
     * 
     * @pre No hay precondiciones específicas para la construcción.
     * @post Se crea instancia con controlador de presentación inicializado.
     */
    private ControladorRankingView() {
        this.presentationController = PresentationController.getInstance();
    }


    /**
     * Obtiene la instancia única del controlador de ranking.
     * Implementa el patrón Singleton para garantizar una sola instancia
     * durante toda la ejecución de la aplicación.
     * 
     * @pre No hay precondiciones específicas para obtener la instancia.
     * @return La instancia única de ControladorRankingView
     * @post Se devuelve instancia única, creándola si es primera invocación.
     */
    public static ControladorRankingView getInstance() {
        if (instance == null) {
            instance = new ControladorRankingView();
        }
        return instance;
    }


    /**
     * Inicializa la vista de gestión de rankings con configuración completa.
     * Configura el layout, crea la vista de ranking, establece la escena
     * y muestra la ventana maximizada con el título apropiado.
     * 
     * @pre stage no debe ser null y debe ser el stage principal.
     * @param stage Stage principal donde mostrar la vista de ranking
     * @post La vista de ranking se muestra completamente configurada,
     *       ventana maximizada y título establecido correctamente.
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
     * Muestra la vista de gestión de rankings en el layout central.
     * Crea una nueva instancia de la vista de ranking y la establece
     * como contenido principal del layout BorderPane.
     * 
     * @pre layout debe estar inicializado correctamente.
     * @post La vista de ranking se muestra en el centro del layout.
     */
    public void mostrarVistaRanking() {
        layout.setCenter(new RankingView(this).getView());
    }


    /**
     * Muestra el popup modal para eliminar jugador del ranking.
     * Abre una ventana de diálogo especializada para confirmar
     * la eliminación de un jugador del sistema de ranking.
     * 
     * @pre stage debe estar disponible como ventana padre.
     * @post Se muestra popup de eliminación sobre la ventana principal.
     */
    public void mostrarPopupEliminarJugador() {
        EliminarJugadorPopup popup = new EliminarJugadorPopup();
        popup.mostrar(stage);
    }


    /**
     * Establece vista anterior para navegación de retorno.
     * Permite configurar a qué vista retornar cuando se active
     * la funcionalidad de navegación hacia atrás.
     * 
     * @pre vista puede ser null si no hay vista anterior válida.
     * @param vista Vista anterior a la que se puede retornar
     * @post Vista anterior registrada para posible navegación de retorno.
     */
    public void setVistaAnterior(Parent vista) {
        this.vistaAnterior = vista;
    }


    /**
     * Retorna a la vista anterior establecida previamente.
     * Muestra la vista anterior configurada en el centro del layout
     * si existe una vista anterior válida registrada.
     * 
     * @pre layout debe estar inicializado correctamente.
     * @post Se muestra vista anterior si existe, sino no hay cambios.
     */
    public void volverAVistaAnterior() {
        if (vistaAnterior != null) {
            layout.setCenter(vistaAnterior);
        }
    }



    /**
     * Retorna al menú principal preservando estado de ventana.
     * Navega de vuelta a la vista principal manteniendo dimensiones,
     * estilos CSS y estado de maximización de la ventana actual.
     * 
     * @pre stage debe estar inicializado con escena válida.
     * @post Se navega al menú principal con estado de ventana preservado,
     *       o se muestra alerta de error si la navegación falla.
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
     * Obtiene el tema visual actualmente configurado en la aplicación.
     * Consulta la configuración actual para determinar qué tema
     * está activo para mantener consistencia visual.
     * 
     * @pre presentationController debe estar inicializado correctamente.
     * @return String con el nombre del tema actual
     * @post Se devuelve tema actual sin modificar configuración visual.
     */
    public String getTema() {
        return presentationController.getTema();
    }

    /**
     * Obtiene la lista de jugadores que aparecen en el ranking.
     * Proporciona nombres de todos los jugadores que tienen estadísticas
     * registradas y están incluidos en el sistema de ranking.
     * 
     * @pre No hay precondiciones específicas para consultar ranking.
     * @return Lista de nombres de jugadores en el ranking
     * @post Se devuelve lista sin modificar estadísticas de jugadores.
     */
    public List<String> getUsuariosRanking() {
        return presentationController.getUsuariosRanking();
    }

    /**
     * Elimina un usuario del ranking y resetea su puntuación total.
     * Remueve al jugador especificado del sistema de ranking y
     * reinicia todas sus estadísticas acumuladas a valores iniciales.
     * 
     * @pre nombre no debe ser null y debe corresponder a jugador registrado.
     * @param nombre Nombre del usuario a eliminar del ranking
     * @return true si eliminación exitosa, false en caso contrario
     * @post Si exitoso, jugador eliminado del ranking con estadísticas reseteadas.
     */
    public boolean eliminarUsuarioRanking(String nombre) {
        // Primero, capturar la información antes de eliminar
        return presentationController.eliminarUsuarioRanking(nombre);
    }


    /**
     * Obtiene la puntuación máxima alcanzada por un jugador específico.
     * Consulta las estadísticas históricas para encontrar la puntuación
     * más alta obtenida en cualquier partida individual del jugador.
     * 
     * @pre nombre no debe ser null y debe corresponder a jugador registrado.
     * @param nombre Nombre del jugador cuya puntuación máxima consultar
     * @return int con la puntuación máxima alcanzada
     * @post Se devuelve puntuación máxima sin modificar estadísticas.
     */
    public int getPuntuacionMaxima(String nombre) {
        return presentationController.getPuntuacionMaxima(nombre);
    }

    /**
     * Obtiene la puntuación media de todas las partidas de un jugador.
     * Calcula el promedio de puntuaciones obtenidas por el jugador
     * a lo largo de todas sus partidas registradas en el sistema.
     * 
     * @pre nombre no debe ser null y debe corresponder a jugador registrado.
     * @param nombre Nombre del jugador cuya puntuación media consultar
     * @return double con la puntuación media calculada
     * @post Se devuelve puntuación media sin modificar estadísticas.
     */
    public double getPuntuacionMedia(String nombre) {
        return presentationController.getPuntuacionMedia(nombre);
    }

 
     /**
     * Obtiene el número total de partidas jugadas por un jugador.
     * Consulta las estadísticas para obtener el recuento total de
     * partidas en las que el jugador ha participado.
     * 
     * @pre nombre no debe ser null y debe corresponder a jugador registrado.
     * @param nombre Nombre del jugador cuyas partidas consultar
     * @return int con número total de partidas jugadas
     * @post Se devuelve número de partidas sin modificar estadísticas.
     */
    public int getPartidasJugadas(String nombre) {
        return presentationController.getPartidasJugadas(nombre);
    }

    /**
     * Obtiene el número total de victorias conseguidas por un jugador.
     * Consulta las estadísticas para obtener el recuento de partidas
     * ganadas por el jugador a lo largo de su historial.
     * 
     * @pre nombre no debe ser null y debe corresponder a jugador registrado.
     * @param nombre Nombre del jugador cuyas victorias consultar
     * @return int con número total de victorias conseguidas
     * @post Se devuelve número de victorias sin modificar estadísticas.
     */
    public int getvictorias(String nombre) {
        return presentationController.getvictorias(nombre);
    }


    /**
     * Obtiene la puntuación total acumulada de un jugador específico.
     * Consulta la suma total de puntos obtenidos por el jugador
     * en todas las partidas completadas registradas en el sistema.
     * 
     * @pre nombre no debe ser null y debe corresponder a jugador registrado.
     * @param nombre Nombre del jugador cuya puntuación total consultar
     * @return int con puntuación total acumulada del jugador
     * @post Se devuelve puntuación total sin modificar estadísticas.
     */
    public int getPuntuacionTotal(String nombre) {
        return presentationController.getPuntuacionTotal(nombre);
    }

    /**
     * Muestra alertas y notificaciones al usuario con formato consistente.
     * Proporciona método centralizado para mostrar mensajes con diferentes
     * tipos de severidad y formato uniforme en la interfaz de ranking.
     * 
     * @pre tipo, titulo y mensaje no deben ser null.
     * @param tipo Tipo de alerta ("info", "warning", "error", "success")
     * @param titulo Título de la ventana de alerta
     * @param mensaje Contenido del mensaje a mostrar
     * @post Se muestra alerta con formato apropiado al tipo especificado.
     */
    public void mostrarAlerta(String tipo, String titulo, String mensaje) {
        presentationController.mostrarAlerta(tipo, titulo, mensaje);
    }
}
