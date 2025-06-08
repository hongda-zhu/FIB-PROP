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
 * Controlador central para operaciones de gestión de jugadores en Scrabble.
 * Coordina la navegación entre vistas de gestión de jugadores e historial,
 * maneja operaciones CRUD de jugadores y proporciona acceso a estadísticas
 * detalladas mediante integración con el controlador de presentación.
 * 
 * Características principales:
 * - Navegación fluida entre vista de gestión e historial de jugadores
 * - Operaciones completas de creación y eliminación de jugadores con validación
 * - Acceso a estadísticas detalladas incluyendo puntuaciones y ratios de victoria
 * - Búsqueda y filtrado de jugadores con criterios personalizables
 * - Manejo de popups especializados para creación de jugadores
 * - Integración con sistema de alertas para retroalimentación al usuario
 * - Preservación de estado de ventana y estilos durante navegación
 * 
 * @version 1.0
 * @since 1.0
 */
public class ControladorJugadoresView {
    
    private final Stage stage;
    private BorderPane layout;
    private PresentationController presentationController;
    private Parent vistaAnterior;
    
    // Jugador seleccionado actualmente para ver historial
    private String jugadorSeleccionado;
    

    /**
     * Constructor que inicializa el controlador con el stage principal.
     * Configura la conexión con el controlador de presentación y establece
     * el layout base para la navegación entre vistas de jugadores.
     * 
     * @pre stage no debe ser null y debe ser el stage principal de la aplicación.
     * @param stage Stage principal donde mostrar las vistas de gestión
     * @post El controlador queda inicializado con layout configurado y listo
     *       para mostrar la vista principal de gestión de jugadores.
     */
    public ControladorJugadoresView(Stage stage) {
        this.presentationController = PresentationController.getInstance();
        this.stage = stage;
        inicializar();
    }
    

    /**
     * Inicializa el controlador mostrando la vista principal de gestión.
     * Configura el layout base, aplica estilos CSS y establece la escena
     * inicial con la vista de gestión de jugadores como contenido principal.
     * 
     * @pre stage debe estar inicializado y disponible para configuración.
     * @post La vista de gestión se muestra como contenido principal, CSS aplicado,
     *       título de ventana establecido y stage visible al usuario.
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
            }
        } catch (Exception e) {
                mostrarAlerta("error", "Error inesperado", "No se ha podido cargar la vista");
        }
        
        stage.setScene(scene);
        stage.setTitle("Gestión de Jugadores");
        stage.show();
    }
    

    /**
     * Muestra la vista principal de gestión de jugadores en el layout central.
     * Cambia el contenido principal para mostrar la lista de jugadores
     * con opciones de crear, eliminar y ver historial de cada jugador.
     * 
     * @pre layout debe estar inicializado y configurado correctamente.
     * @post La vista de gestión se muestra en el centro del layout y
     *       el título de la ventana se actualiza apropiadamente.
     */
    public void mostrarVistaGestionJugadores() {
        GestionJugadoresView vistaGestion = new GestionJugadoresView(this);
        layout.setCenter(vistaGestion.getView());
        stage.setTitle("Gestión de Jugadores");
    }
    


    /**
     * Muestra la vista de historial detallado de un jugador específico.
     * Cambia el contenido principal para mostrar estadísticas completas,
     * historial de puntuaciones y datos de rendimiento del jugador seleccionado.
     * 
     * @pre nombreJugador no debe ser null y debe corresponder a un jugador registrado.
     * @param nombreJugador Nombre del jugador cuyo historial mostrar
     * @post La vista de historial se muestra con datos del jugador especificado
     *       y el título de ventana se actualiza para reflejar el contexto.
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
     * Muestra el popup modal para crear un nuevo jugador en el sistema.
     * Abre una ventana de diálogo especializada que permite al usuario
     * ingresar datos del nuevo jugador con validación en tiempo real.
     * 
     * @pre stage debe estar disponible como ventana padre para el popup.
     * @post Se muestra el popup de creación sobre la ventana principal,
     *       bloqueando interacción hasta completar o cancelar la creación.
     */
    public boolean crearJugador(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            mostrarAlerta("error", "Error", "El nombre del jugador no puede estar vacío");
            return false;
        }
        
        boolean creado = presentationController.crearJugador(nombre);
        return creado;
    }
    

    /**
     * Crea un nuevo jugador con validación de nombre y manejo de errores.
     * Valida que el nombre no esté vacío y delega la creación al controlador
     * de presentación, proporcionando retroalimentación sobre el resultado.
     * 
     * @pre nombre no debe ser null.
     * @param nombre Nombre del jugador a crear en el sistema
     * @return true si el jugador se creó exitosamente, false si hubo errores
     * @post Si es exitoso, el jugador queda registrado. Si falla, se muestra
     *       mensaje de error apropiado y no se realizan cambios.
     */    
    public void eliminarJugador(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            presentationController.mostrarAlerta("warning", "Advertencia", "Por favor, seleccione un jugador para eliminar");
            return;
        }
        
        // Eliminar jugador en el modelo
        try {
            presentationController.eliminarJugador(nombre);
            // Si no lanza excepción, significa que se eliminó correctamente
            presentationController.mostrarAlerta("success", "Éxito", "Jugador eliminado exitosamente");
            // Actualizar la vista
            mostrarVistaGestionJugadores();
        } catch (scrabble.excepciones.ExceptionUserInGame e) {
            // Capturar específicamente la excepción de usuario en juego
            presentationController.mostrarAlerta("error", "Error", 
                "No se ha podido eliminar el jugador: " + "El jugador se encuentra en una partida");
        } catch (Exception e) {
            // Capturar cualquier otra excepción que pueda ocurrir
            presentationController.mostrarAlerta("error", "Error", 
                "Error inesperado al eliminar el jugador: " + e.getMessage());
        }
    }
    

    /**
     * Elimina un jugador del sistema con validación y manejo de excepciones.
     * Valida la selección, maneja casos especiales como jugadores en partida
     * y proporciona retroalimentación detallada sobre el resultado de la operación.
     * 
     * @pre nombre no debe ser null.
     * @param nombre Nombre del jugador a eliminar del sistema
     * @post Si es exitoso, el jugador se elimina y se actualiza la vista.
     *       Si hay errores (jugador en partida, etc.), se muestran mensajes específicos.
     */
    public List<String> buscarJugadores(String patron) {
        return presentationController.buscarJugadoresPorNombre(patron);
    }
    

    /**
     * Busca jugadores cuyo nombre coincida con el patrón de búsqueda.
     * Filtra la lista de jugadores registrados para encontrar coincidencias
     * parciales con el patrón especificado, facilitando la localización.
     * 
     * @pre patron puede ser null (devuelve todos los jugadores).
     * @param patron Cadena de texto a buscar en nombres de jugadores
     * @return Lista de nombres de jugadores que coinciden con el patrón
     * @post Se devuelve lista filtrada sin modificar el registro de jugadores.
     */
    public List<String> getJugadores() {
        return presentationController.getAllJugadores();
    }
    
    /**
     * Obtiene estadísticas completas y detalladas de un jugador específico.
     * Recopila y calcula todas las métricas de rendimiento incluyendo
     * puntuaciones, ratios de victoria y historial de partidas del jugador.
     * 
     * @pre nombre no debe ser null y debe corresponder a un jugador registrado.
     * @param nombre Nombre del jugador cuyas estadísticas obtener
     * @return Objeto EstadisticasJugador con todos los datos de rendimiento
     * @post Se devuelve estadísticas completas sin modificar datos del jugador.
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
    

    /**
     * Muestra alertas y notificaciones con formato consistente.
     * Proporciona método centralizado para mostrar mensajes al usuario
     * con diferentes tipos de severidad y formato uniforme.
     * 
     * @pre type, title y message no deben ser null.
     * @param type Tipo de alerta a mostrar
     * @param title Título de la ventana de alerta
     * @param message Contenido del mensaje para el usuario
     * @post Se muestra la alerta con formato apropiado al tipo especificado.
     */    
    public void mostrarAlerta(String type, String title, String message) {
        presentationController.mostrarAlerta(type, title, message);
    }


    /**
     * Retorna al menú principal preservando estado de ventana.
     * Navega de vuelta a la vista principal de la aplicación manteniendo
     * dimensiones, estilos CSS y estado de maximización de la ventana.
     * 
     * @pre stage debe estar inicializado con escena válida.
     * @post Se navega al menú principal con estado de ventana preservado.
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
     * Retorna a la vista anterior en el flujo de navegación.
     * Muestra la vista previamente establecida o la vista de gestión
     * principal si no hay vista anterior definida.
     * 
     * @pre layout debe estar inicializado correctamente.
     * @post Se muestra la vista anterior o la vista de gestión por defecto.
     */
    public void volver() {
        if (vistaAnterior != null) {
            layout.setCenter(vistaAnterior);
        } else {
            mostrarVistaGestionJugadores();
        }
    }
    

    /**
     * Establece una vista anterior para navegación de retorno.
     * Permite configurar a qué vista retornar cuando se active
     * la funcionalidad de navegación hacia atrás.
     * 
     * @pre vista puede ser null si no hay vista anterior válida.
     * @param vista Vista anterior a la que se puede retornar
     * @post La vista anterior queda registrada para navegación de retorno.
     */
    public void setVistaAnterior(Parent vista) {
        this.vistaAnterior = vista;
    }
    

    /**
     * Muestra alertas nativas de JavaFX con configuración específica.
     * Crea y muestra alertas usando el sistema nativo de JavaFX
     * con tipo, encabezado y contenido personalizables.
     * 
     * @pre type, header y content no deben ser null.
     * @param type Tipo de alerta de JavaFX a mostrar
     * @param header Texto del encabezado de la alerta
     * @param content Contenido detallado del mensaje
     * @post Se muestra alerta modal que bloquea hasta ser cerrada por el usuario.
     */
    private void showAlert(Alert.AlertType type, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(type.toString());
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    

    /**
     * Verifica si un jugador está actualmente participando en una partida.
     * Consulta el estado actual del jugador para determinar su disponibilidad
     * para nuevas partidas o operaciones que requieren que esté libre.
     * 
     * @pre nombre no debe ser null y debe corresponder a un jugador registrado.
     * @param nombre Nombre del jugador cuyo estado consultar
     * @return true si está en partida activa, false si está disponible
     * @post Se devuelve el estado actual sin modificar la participación.
     */
    public boolean isEnPartida (String nombre) {
        return presentationController.isEnPartida(nombre);
    }

    /**
     * Verifica si un jugador es controlado por inteligencia artificial.
     * Determina si el jugador especificado es un jugador IA del sistema
     * o un jugador humano controlado por usuario real.
     * 
     * @pre nombre no debe ser null y debe corresponder a un jugador registrado.
     * @param nombre Nombre del jugador a verificar
     * @return true si es jugador IA, false si es jugador humano
     * @post Se devuelve el tipo sin modificar la configuración del jugador.
     */
    public boolean esIA(String nombre) {
        return presentationController.esIA(nombre);
    }


    /**
     * Obtiene el identificador de la partida actual de un jugador.
     * Proporciona información sobre en qué partida específica está
     * participando actualmente el jugador, si está en alguna.
     * 
     * @pre nombre no debe ser null y debe corresponder a un jugador registrado.
     * @param nombre Nombre del jugador cuya partida actual consultar
     * @return String con identificador de la partida actual o null si no está en partida
     * @post Se devuelve información de partida sin modificar la participación.
     */
    public String getNombrePartidaActual(String nombre) {
        return presentationController.getNombrePartidaActual(nombre);
    }


    /**
     * Obtiene la puntuación total acumulada de un jugador.
     * Consulta la suma de todas las puntuaciones obtenidas por el jugador
     * a lo largo de todas las partidas completadas en su historial.
     * 
     * @pre nombre no debe ser null y debe corresponder a un jugador registrado.
     * @param nombre Nombre del jugador cuya puntuación total consultar
     * @return int con la puntuación total acumulada del jugador
     * @post Se devuelve la puntuación total sin modificar estadísticas.
     */
    public int getPuntuacionTotal(String nombre) {
        return presentationController.getPuntuacionTotal(nombre);
    }


    /**
     * Obtiene el historial completo de puntuaciones de un jugador.
     * Proporciona la lista de todas las puntuaciones individuales obtenidas
     * en cada partida completada por el jugador específico.
     * 
     * @pre nombre no debe ser null y debe corresponder a un jugador registrado.
     * @param nombre Nombre del jugador cuyo historial consultar
     * @return Lista de enteros con todas las puntuaciones del jugador
     * @post Se devuelve el historial completo sin modificar los datos.
     */
    public List<Integer> getPuntuacionesUsuario(String nombre) {
        return presentationController.getPuntuacionesUsuario(nombre);
    }

    /**
     * Obtiene el tema actual desde el controlador de presentación.
     *
     * @return una cadena que representa el tema actual.
     * @pre presentationController debe estar inicializado.
     * @post Se retorna el tema actualmente seleccionado en la aplicación.
     */
    public String getTema() {
        return presentationController.getTema();
    }


    /**
    * Clase interna para encapsular estadísticas completas de rendimiento de jugador.
    * Proporciona una estructura de datos cohesiva que agrupa todas las métricas
    * de rendimiento de un jugador específico, incluyendo puntuaciones, historial
    * de partidas y ratios de éxito calculados para análisis y visualización.
    * @version 1.0
    * @since 1.0
    */
    public class EstadisticasJugador {
        private int puntuacionTotal;
        private int puntuacionMaxima;
        private double puntuacionMedia;
        private int partidasJugadas;
        private int victorias;
        private String ratioVictorias;
        private List<Integer> historialPuntuaciones;
        
        /**
        * Obtiene la puntuación total acumulada del jugador.
        * Representa la suma de todas las puntuaciones obtenidas por el jugador
        * a lo largo de todas las partidas completadas en su historial.
        * 
        * @pre No hay precondiciones específicas para acceder a la puntuación total.
        * @return int con la puntuación total acumulada del jugador
        * @post Se devuelve el valor actual sin modificar el estado interno.
        */
        public int getPuntuacionTotal() { return puntuacionTotal; }

        /**
        * Establece la puntuación total acumulada del jugador.
        * Actualiza el valor de la suma total de puntuaciones obtenidas
        * por el jugador en todas sus partidas completadas.
        * 
        * @pre puntuacionTotal debe ser un valor no negativo.
        * @param puntuacionTotal Nueva puntuación total a establecer
        * @post La puntuación total queda actualizada con el valor especificado.
        */        
        public void setPuntuacionTotal(int puntuacionTotal) { this.puntuacionTotal = puntuacionTotal; }
        
        /**
        * Obtiene la puntuación máxima alcanzada por el jugador en una sola partida.
        * Representa el mejor rendimiento individual del jugador en cualquiera
        * de las partidas que ha completado en su historial.
        * 
        * @pre No hay precondiciones específicas para acceder a la puntuación máxima.
        * @return int con la puntuación máxima alcanzada en una partida individual
        * @post Se devuelve el valor actual sin modificar el estado interno.
        */        
        public int getPuntuacionMaxima() { return puntuacionMaxima; }

        /**
        * Establece la puntuación máxima alcanzada por el jugador.
        * Actualiza el valor del mejor rendimiento individual registrado
        * por el jugador en una sola partida de su historial.
        * 
        * @pre puntuacionMaxima debe ser un valor no negativo.
        * @param puntuacionMaxima Nueva puntuación máxima a establecer
        * @post La puntuación máxima queda actualizada con el valor especificado.
        */        
        public void setPuntuacionMaxima(int puntuacionMaxima) { this.puntuacionMaxima = puntuacionMaxima; }
        
        /**
        * Obtiene la puntuación media formateada del jugador con precisión decimal.
        * Calcula y formatea el promedio de puntuaciones obtenidas por el jugador
        * a lo largo de todas sus partidas, con formato de dos decimales.
        * 
        * @pre puntuacionMedia debe haber sido establecida previamente.
        * @return String con la puntuación media formateada a dos decimales
        * @post Se devuelve la media formateada sin modificar el valor interno.
        */        
        public String getPuntuacionMedia() { return String.format("%.2f", puntuacionMedia); }
        /**
        * Establece la puntuación media calculada del jugador.
        * Actualiza el valor promedio de puntuaciones obtenidas por el jugador,
        * calculado a partir de todas las partidas completadas en su historial.
        * 
        * @pre puntuacionMedia debe ser un valor no negativo.
        * @param puntuacionMedia Nueva puntuación media a establecer
        * @post La puntuación media queda actualizada con el valor especificado.
        */        
        public void setPuntuacionMedia(double puntuacionMedia) { this.puntuacionMedia = puntuacionMedia; }
        /**
        * Obtiene el número total de partidas jugadas por el jugador.
        * Representa el recuento completo de partidas en las que el jugador
        * ha participado, independientemente del resultado obtenido.
        * 
        * @pre No hay precondiciones específicas para acceder a partidas jugadas.
        * @return int con el número total de partidas jugadas
        * @post Se devuelve el valor actual sin modificar el estado interno.
        */        
        public int getPartidasJugadas() { return partidasJugadas; }
        /**
        * Establece el número total de partidas jugadas por el jugador.
        * Actualiza el recuento de partidas en las que el jugador ha participado,
        * usado para cálculos de ratios y estadísticas derivadas.
        * 
        * @pre partidasJugadas debe ser un valor no negativo.
        * @param partidasJugadas Nuevo número de partidas jugadas a establecer
        * @post El número de partidas jugadas queda actualizado con el valor especificado.
        */       
        public void setPartidasJugadas(int partidasJugadas) { this.partidasJugadas = partidasJugadas; }
        /**
        * Obtiene el número total de victorias conseguidas por el jugador.
        * Representa el recuento de partidas ganadas por el jugador a lo largo
        * de su historial de participación en el juego.
        * 
        * @pre No hay precondiciones específicas para acceder a victorias.
        * @return int con el número total de victorias conseguidas
        * @post Se devuelve el valor actual sin modificar el estado interno.
        */        
        public int getVictorias() { return victorias; }
        /**
        * Establece el número total de victorias conseguidas por el jugador.
        * Actualiza el recuento de partidas ganadas, usado para cálculos de
        * rendimiento y ratios de éxito del jugador.
        * 
        * @pre victorias debe ser un valor no negativo y no mayor que partidasJugadas.
        * @param victorias Nuevo número de victorias a establecer
        * @post El número de victorias queda actualizado con el valor especificado.
        */        
        public void setVictorias(int victorias) { this.victorias = victorias; }
        
        /**
        * Obtiene el ratio de victorias formateado como porcentaje.
        * Proporciona el porcentaje de éxito del jugador calculado como la
        * proporción de victorias respecto al total de partidas jugadas.
        * 
        * @pre ratioVictorias debe haber sido calculado y establecido previamente.
        * @return String con el ratio de victorias formateado como porcentaje
        * @post Se devuelve el ratio formateado sin modificar el valor interno.
        */        
        public String getRatioVictorias() { return ratioVictorias; }
        /**
        * Establece el ratio de victorias formateado como porcentaje.
        * Actualiza el porcentaje de éxito del jugador, generalmente calculado
        * como (victorias / partidasJugadas) * 100 y formateado apropiadamente.
        * 
        * @pre ratioVictorias no debe ser null y debe ser un formato de porcentaje válido.
        * @param ratioVictorias Nuevo ratio de victorias formateado a establecer
        * @post El ratio de victorias queda actualizado con el valor especificado.
        */        
        public void setRatioVictorias(String ratioVictorias) { this.ratioVictorias = ratioVictorias; }
        /**
        * Obtiene el historial completo de puntuaciones del jugador.
        * Proporciona la lista cronológica de todas las puntuaciones obtenidas
        * por el jugador en cada partida individual completada.
        * 
        * @pre No hay precondiciones específicas para acceder al historial.
        * @return Lista de enteros con todas las puntuaciones históricas del jugador
        * @post Se devuelve la referencia al historial sin modificar su contenido.
        */        
        public List<Integer> getHistorialPuntuaciones() { return historialPuntuaciones; }
        /**
        * Establece el historial completo de puntuaciones del jugador.
        * Actualiza la lista cronológica con todas las puntuaciones obtenidas
        * en las partidas individuales completadas por el jugador.
        * 
        * @pre historialPuntuaciones no debe ser null y debe contener valores válidos.
        * @param historialPuntuaciones Nueva lista de puntuaciones históricas
        * @post El historial de puntuaciones queda actualizado con la lista especificada.
        */        
        public void setHistorialPuntuaciones(List<Integer> historialPuntuaciones) { this.historialPuntuaciones = historialPuntuaciones; }
    }
}