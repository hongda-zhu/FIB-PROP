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
import scrabble.excepciones.ExceptionPersistenciaFallida;
import scrabble.helpers.Direction;
import scrabble.helpers.Triple;
import scrabble.helpers.Tuple;
import scrabble.presentation.PresentationController;
import scrabble.presentation.views.ConfigPartidaView;
import scrabble.presentation.views.GestionPartidaView;
import scrabble.presentation.views.VistaTablero;


/**
 * Controlador central para operaciones de gestión de partidas de Scrabble.
 * Coordina el flujo completo de partidas desde configuración hasta finalización,
 * manejando navegación entre vistas, validación de jugadas y persistencia de
 * estados de juego mediante un layout BorderPane centralizado.
 * 
 * Características principales:
 * - Gestión completa del ciclo de vida de partidas (crear, cargar, guardar)
 * - Navegación fluida entre vistas de gestión, configuración y tablero
 * - Validación de configuración de partida y movimientos de juego
 * - Integración con sistema de persistencia para partidas guardadas
 * - Manejo de jugadores humanos e IA con turnos automáticos
 * - Control de estado de partida y detección de finalización
 * - Aplicación automática de estilos CSS y efectos visuales
 * 
 * @version 1.0
 * @since 1.0
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
     * Constructor que inicializa el controlador con configuración por defecto.
     * Establece valores iniciales para diccionario y tamaño de tablero,
     * configura la vista de configuración y prepara el layout base.
     * 
     * @pre stage no debe ser null y debe ser el stage principal de la aplicación.
     * @param stage Stage principal donde mostrar las vistas de partida
     * @post El controlador queda inicializado con valores por defecto,
     *       vista de configuración lista y layout preparado para navegación.
     */
    public ControladorPartidaView(Stage stage) {
        this.presentationController = PresentationController.getInstance();
        vistaConfig = new ConfigPartidaView(this);
        this.stage = stage;
        this.diccionario = presentationController.getDiccionarioDefault();
        this.size = presentationController.getTamanoDefault();
        this.jugadores = new ArrayList<String>();
        this.cargado = false;
        inicializar();
    }
    

   /**
     * Inicializa el layout y muestra la vista principal de gestión de partidas.
     * Configura el BorderPane base, establece la escena inicial y muestra
     * la vista de gestión como contenido principal por defecto.
     * 
     * @pre stage debe estar disponible y configurado correctamente.
     * @post La vista de gestión se muestra como contenido principal,
     *       escena establecida en el stage y ventana visible al usuario.
     */
    private void inicializar() {
        layout = new BorderPane();
        mostrarVistaGestion();
        
        Scene scene = new Scene(layout, stage.getScene().getWidth(), stage.getScene().getHeight());
        stage.setScene(scene);
        stage.setTitle("Scrabble");
        stage.show();
    }
    

    /**
     * Muestra la vista principal de gestión de partidas en el layout.
     * Cambia el contenido central para mostrar opciones de crear nueva partida,
     * cargar partidas existentes y acceder a configuración avanzada.
     * 
     * @pre layout debe estar inicializado correctamente.
     * @post La vista de gestión se muestra en el centro del layout.
     */
    public void mostrarVistaGestion() {
        GestionPartidaView vistaGestion = new GestionPartidaView(this);
 
        layout.setCenter(vistaGestion.getView());
        stage.setTitle("Scrabble");
    }
    

    /**
     * Indica si la partida actual fue cargada desde archivo guardado.
     * Proporciona información sobre el origen de la partida actual para
     * determinar comportamientos específicos en el flujo de juego.
     * 
     * @pre No hay precondiciones específicas para consultar el estado.
     * @return true si la partida fue cargada, false si es nueva
     * @post Se devuelve el estado sin modificar la configuración interna.
     */
    public boolean getCargado() {
        return this.cargado;
    }


    /**
     * Establece el estado de carga de la partida actual.
     * Actualiza la bandera que indica si la partida proviene de un
     * archivo guardado o es una partida completamente nueva.
     * 
     * @pre cargado debe ser un valor boolean válido.
     * @param cargado Estado de carga a establecer
     * @post El estado de carga queda actualizado con el valor especificado.
     */
    public void setCargado(boolean cargado){
        this.cargado = cargado;
    }


    /**
     * Muestra la vista de configuración de partida con jugadores disponibles.
     * Carga la lista de jugadores disponibles y cambia la interfaz para
     * mostrar controles de configuración de nueva partida.
     * 
     * @pre vistaConfig debe estar inicializada correctamente.
     * @post Se muestra la vista de configuración con jugadores disponibles.
     */
    public void mostrarVistaConfiguracion() {
        List<String> jugadores = presentationController.getAllJugadoresDisponibles();
        vistaConfig.setJugadoresReady(jugadores);        
        layout.setCenter(vistaConfig.getView());
        stage.setTitle("Scrabble");
    }
    

    /**
     * Muestra la vista del tablero de juego con configuración aplicada.
     * Configura la vista del tablero con el tamaño especificado, establece
     * el jugador actual para partidas cargadas y aplica estilos CSS a botones.
     * 
     * @pre size debe estar configurado, layout debe estar inicializado.
     * @post La vista del tablero se muestra con configuración correcta
     *       y estilos CSS aplicados a todos los botones de acción.
     */
    public void mostrarVistaTablero() {
        
        VistaTablero vistaTablero = new VistaTablero(this);
        
        // Tengo que hacer un -1 aquí porque más adelante en la función para calcular el índice del siguiente jugador hago un + 1, me da pereza cambiarlo
        int idx = presentationController.getJugadorActualIdx() - 1;
        if (cargado) {
            vistaTablero.setJugadorActualIndex(idx);
        }

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
                }
            } catch (Exception e) {
                mostrarAlerta("error", "Error inesperado", "No se ha podido cargar la vista");
                volver();
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
            }
            if (btnCancelar != null) {
                btnCancelar.getStyleClass().addAll("btn-effect", "btn-danger");
            }
            if (btnPasarTurno != null) {
                btnPasarTurno.getStyleClass().addAll("btn-effect", "btn-primary");
            }
            if (btnCambiarFichas != null) {
                btnCambiarFichas.getStyleClass().add("btn-effect");
            }
            if (btnPausarPartida != null) {
                btnPausarPartida.getStyleClass().addAll("btn-effect", "btn-warning");
            }
            if (btnVolver != null) {
                btnVolver.getStyleClass().add("btn-effect");
            }
        });

    }
       
    /**
     * Inicia el proceso de creación de nueva partida.
     * Navega a la vista de configuración para que el usuario pueda
     * seleccionar jugadores, diccionario y tamaño de tablero.
     * 
     * @pre La vista de configuración debe estar disponible.
     * @post Se muestra la vista de configuración para nueva partida.
     */
    public void crearNuevaPartida() {
        // presentationController.importDiccionaryDEBUG();
        mostrarVistaConfiguracion();
    }
    
    /**
     * Inicia una nueva partida con la configuración actual validada.
     * Valida diccionario, jugadores y tamaño de tablero, inicia la partida
     * si todo es correcto y navega al tablero de juego.
     * 
     * @pre diccionario, jugadores y size deben estar configurados correctamente.
     * @post Si la validación es exitosa, se inicia la partida y se muestra el tablero.
     *       Si hay errores, se muestran alertas informativas al usuario.
     * @throws ExceptionPersistenciaFallida si hay errores de persistencia
     */    
    public void iniciarPartida() throws ExceptionPersistenciaFallida {

        if (diccionario == null) {
            mostrarAlerta("error", "Diccionacio null", "¡Por favor, selecciona un diccionario antes de continuar!");
        } else if (jugadores.size() < 1) {
            mostrarAlerta("error","Jugadores insuficientes", "¡Por favor, selecciona al menos 1 jugador antes de continuar!");
        } else if (size < 15) {
            mostrarAlerta("error","Tamaño incorrecto", "¡Por favor, configura un tamaño de tablero correcto antes de continuar!");        
        }
        else {
            boolean inicioExitoso = presentationController.iniciarPartida(jugadores, diccionario, size);
            if (inicioExitoso) {
                
                for (String nombre : jugadores) {
                    presentationController.clearSkipTrack(nombre);
                }

                vistaConfig.reset();
                mostrarVistaTablero();
            } else {
                mostrarAlerta("warning","Error al iniciar partida", "No se pudo iniciar la partida");
            }
        }
    }


    /**
     * Carga una partida previamente guardada desde archivo.
     * Restaura el estado completo de la partida especificada y navega
     * al tablero de juego para continuar desde el punto guardado.
     * 
     * @pre idPartida no debe ser null y debe corresponder a partida válida.
     * @param idPartida Identificador de la partida guardada a cargar
     * @post Si la carga es exitosa, se restaura el estado y se muestra el tablero.
     *       Si hay errores, se muestra alerta y se mantiene la vista actual.
     */
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
     * Obtiene el estado actual completo del tablero de juego.
     * Proporciona mapa con posiciones y fichas colocadas para
     * visualización en la interfaz del tablero.
     * 
     * @pre Debe haber una partida activa con tablero inicializado.
     * @return Map con posiciones del tablero y letras colocadas
     * @post Se devuelve el estado actual sin modificar el tablero.
     */
    public Map<Tuple<Integer, Integer>, String> getEstadoTablero() {
        return presentationController.getEstadoTablero();
    }  


    /**
     * Obtiene la cantidad de fichas restantes en la bolsa de la partida.
     * Consulta el número de fichas disponibles para repartición
     * durante el transcurso del juego activo.
     * 
     * @pre Debe haber una partida activa con bolsa inicializada.
     * @return int con el número de fichas restantes
     * @post Se devuelve la cantidad sin modificar la bolsa.
     */
    public int getCantidadFichasRestantes() {
        return presentationController.getCantidadFichasRestantes();
    }


    /**
     * Obtiene el estado detallado de la partida para un jugador específico.
     * Proporciona información completa sobre puntuaciones, turno actual
     * y estado general del juego desde la perspectiva del jugador.
     * 
     * @pre nombreJugador no debe ser null y debe participar en la partida.
     * @param nombreJugador Nombre del jugador para obtener estado
     * @return String con representación textual del estado de partida
     * @post Se devuelve información completa sin modificar el estado.
     */
    public String getEstadoPartida(String nombreJugador) {
        return presentationController.getEstadoPartida(nombreJugador);
    }


    /**
     * Obtiene el rack de fichas disponibles de un jugador específico.
     * Proporciona mapa con letras y cantidades disponibles para
     * realizar jugadas en el turno del jugador.
     * 
     * @pre nombreJugador no debe ser null y debe participar en la partida.
     * @param nombreJugador Nombre del jugador cuyo rack consultar
     * @return Map que asocia letras con cantidades disponibles
     * @post Se devuelve el rack actual sin modificar las fichas.
     */
    public Map<String, Integer> getRackJugador(String nombreJugador) {
        return presentationController.getRackJugador(nombreJugador);
    }


    /**
     * Verifica si un movimiento propuesto es válido según las reglas.
     * Valida la jugada considerando fichas disponibles, reglas de colocación
     * y disponibilidad de posiciones en el tablero actual.
     * 
     * @pre jugada y rack no deben ser null, deben corresponder al jugador actual.
     * @param jugada Triple con palabra, posición y dirección del movimiento
     * @param rack Mapa con fichas disponibles del jugador
     * @return true si el movimiento es válido, false en caso contrario
     * @post Se devuelve validez sin modificar tablero ni rack.
     */
    public boolean esMovimientoValido(Triple<String, Tuple<Integer, Integer>, Direction> jugada, Map<String, Integer> rack) {
        return presentationController.esMovimientoValido(jugada, rack);
    }


    /**
     * Ejecuta un turno de juego con la jugada especificada.
     * Procesa la jugada, actualiza el tablero, calcula puntuación
     * y avanza al siguiente turno en la secuencia de jugadores.
     * 
     * @pre nombreJugador y jugada no deben ser null, debe ser turno del jugador.
     * @param nombreJugador Nombre del jugador que realiza la jugada
     * @param jugada Triple con palabra, posición y dirección
     * @return int con puntos obtenidos por la jugada
     * @post La jugada se ejecuta, se actualizan puntuaciones y se avanza turno.
     */
    public int realizarTurnoPartida(String nombreJugador, Triple<String, Tuple<Integer, Integer>, Direction> jugada) {
        return presentationController.realizarTurnoPartida(nombreJugador, jugada);
    }


    /**
     * Actualiza la puntuación acumulada de un jugador específico.
     * Añade los puntos especificados al total del jugador manteniendo
     * el registro actualizado durante la partida.
     * 
     * @pre nombreJugador no debe ser null, puntos debe ser valor válido.
     * @param nombreJugador Nombre del jugador a actualizar
     * @param puntos Cantidad de puntos a añadir
     * @post La puntuación del jugador se incrementa con los puntos.
     */
    public void actualizarJugador(String nombreJugador, int puntos) {
        presentationController.actualizarJugadores(nombreJugador, puntos);
    }


    /**
     * Comprueba si se han cumplido las condiciones de finalización.
     * Evalúa criterios como fichas agotadas, racks vacíos o saltos
     * consecutivos para determinar si la partida debe terminar.
     * 
     * @pre jugadoresSeleccionados no debe ser null.
     * @param jugadoresSeleccionados Mapa con jugadores participantes
     * @post Se evalúan condiciones y se actualiza estado si debe terminar.
     */
    public void comprobarFinPartida(Map<String, Integer> jugadoresSeleccionados) {
        presentationController.comprobarFinPartida(jugadoresSeleccionados);
    }


    /**
     * Verifica si el juego actual ha terminado según criterios establecidos.
     * Consulta el estado interno para determinar si la partida
     * ha finalizado por cualquier condición de terminación válida.
     * 
     * @pre Debe haber una partida activa para consultar estado.
     * @return true si el juego terminó, false si continúa
     * @post Se devuelve estado sin modificar condiciones del juego.
     */
    public boolean isJuegoTerminado() {
        return presentationController.isJuegoTerminado();
    }


    /**
     * Finaliza la partida actual y calcula resultados finales.
     * Procesa puntuaciones finales, determina ganador, actualiza estadísticas
     * y limpia el estado de saltos de turno de todos los jugadores.
     * 
     * @pre Debe haber partida activa, jugadoresSeleccionados no debe ser null.
     * @param jugadoresSeleccionados Mapa con jugadores participantes
     * @return String con resumen de resultados y ganador
     * @post Partida finalizada, estadísticas actualizadas y estado limpio.
     */
    public String finalizarJuego(Map<String, Integer> jugadoresSeleccionados) {
        
        for (String nombre : jugadores) {
            presentationController.clearSkipTrack(nombre);
        }

        return presentationController.finalizarJuego(jugadoresSeleccionados);
    }


    /**
     * Permite intercambio de fichas del rack con la bolsa.
     * Intercambia fichas especificadas del jugador con fichas aleatorias
     * de la bolsa, consumiendo el turno en el proceso.
     * 
     * @pre nombreJugador y fichas no deben ser null, fichas deben estar en rack.
     * @param nombreJugador Nombre del jugador que intercambia
     * @param fichas Lista de fichas a intercambiar
     * @return true si el intercambio fue exitoso, false si falló
     * @post Si exitoso, fichas intercambiadas y turno consumido.
     */
    public boolean intercambiarFichas(String nombreJugador, List<String> fichas) {
        return presentationController.intercambiarFichas(nombreJugador, fichas);
    }

    /**
     * Verifica si un jugador específico es controlado por IA.
     * Determina si el jugador es artificial o humano para
     * aplicar lógica de turnos automáticos cuando corresponda.
     * 
     * @pre nombreJugador no debe ser null y debe estar en la partida.
     * @param nombreJugador Nombre del jugador a verificar
     * @return true si es IA, false si es jugador humano
     * @post Se devuelve tipo sin modificar configuración.
     */
    public boolean esIA(String nombreJugador) {
        return presentationController.esIA(nombreJugador);
    }


    /**
     * Obtiene el mapa de jugadores actualmente participantes.
     * Proporciona información sobre todos los jugadores de la partida
     * activa con sus identificadores correspondientes.
     * 
     * @pre Debe haber partida activa con jugadores inicializados.
     * @return Map que asocia nombres con identificadores
     * @post Se devuelve información sin modificar estado de partida.
     */
    public Map<String, Integer> getJugadoresActuales() {
        return presentationController.getJugadoresActuales();
    }


    /**
     * Libera todos los jugadores de la partida actual.
     * Marca jugadores como no participantes para permitir
     * unión a nuevas partidas posteriormente.
     * 
     * @pre Deben haber jugadores asignados a partida activa.
     * @post Todos los jugadores quedan liberados y disponibles.
     */
    public void liberarJugadores() {
        presentationController.liberarJugadoresActuales();
    }


    /**
     * Guarda el estado actual de la partida para continuación posterior.
     * Persiste tablero, racks, puntuaciones y turno actual para
     * permitir carga exacta desde el punto de guardado.
     * 
     * @pre Debe haber partida activa, jugadoresSeleccionados no debe ser null.
     * @param jugadoresSeleccionados Lista de jugadores participantes
     * @param turnoActual Índice del jugador con turno actual
     * @return true si guardado exitoso, false si hubo errores
     * @post Si exitoso, estado completo persiste para carga posterior.
     */
    public boolean guardarPartida(List<String> jugadoresSeleccionados, int turnoActual) {
        return presentationController.guardarPartida(jugadoresSeleccionados, turnoActual);
    }


    /**
     * Obtiene lista de identificadores de partidas guardadas disponibles.
     * Proporciona IDs de todas las partidas que pueden ser cargadas
     * para continuar desde puntos previamente guardados.
     * 
     * @pre No hay precondiciones específicas para consultar partidas.
     * @return Lista de identificadores de partidas guardadas
     * @post Se devuelve lista sin modificar estado de partidas guardadas.
     */
    public List<Integer> getPartidasGuardadasID() {
        return presentationController.getPartidasGuardadas();
    }


    /**
     * Obtiene el diccionario utilizado en una partida guardada específica.
     * Consulta información de configuración de partida guardada
     * para mostrar detalles antes de la carga.
     * 
     * @pre id no debe ser null y debe corresponder a partida válida.
     * @param id Identificador de partida guardada
     * @return String con nombre del diccionario utilizado
     * @post Se devuelve información sin cargar la partida.
     */
    public String getDiccionarioPartida(Integer id) {
        return presentationController.getDiccionarioPartida(id);
    }


    /**
     * Obtiene los puntos correspondientes a una letra específica.
     * Consulta el valor de puntuación de la letra en el diccionario
     * actual de la partida para cálculos de puntaje.
     * 
     * @pre letra no debe ser null y debe existir en el alfabeto.
     * @param letra Letra cuyo valor de puntos consultar
     * @return Integer con puntos de la letra o null si no existe
     * @post Se devuelve valor sin modificar configuración del diccionario.
     */
    public Integer obtenerPuntosPorLetra(String letra) {
        Map<String, Integer> alpha = presentationController.getAlphabet(this.diccionario);
        return alpha.get(letra);
    }


    /**
     * Obtiene el alfabeto completo con valores del diccionario actual.
     * Proporciona mapa completo de letras y sus puntuaciones
     * para uso en validaciones y cálculos de la partida.
     * 
     * @pre diccionario debe estar configurado correctamente.
     * @return Map que asocia letras con sus valores de puntuación
     * @post Se devuelve alfabeto completo sin modificar diccionario.
     */
    public Map<String, Integer> getAllAlphabet() {
        return presentationController.getAlphabet(this.diccionario);
    }


    /**
     * Obtiene el número de jugadores de una partida guardada específica.
     * Consulta cuántos jugadores participaban en la partida
     * guardada para mostrar información antes de cargar.
     * 
     * @pre id no debe ser null y debe corresponder a partida válida.
     * @param id Identificador de partida guardada
     * @return int con número de jugadores de la partida
     * @post Se devuelve información sin cargar la partida.
     */
    public int getNumJugadoresPartida (Integer id) {
        return presentationController.getNumJugadoresPartida(id);
    }


    /**
     * Elimina permanentemente una partida guardada del sistema.
     * Borra todos los datos de la partida especificada liberando
     * espacio y removiéndola de la lista de partidas disponibles.
     * 
     * @pre idPartida no debe ser null y debe corresponder a partida válida.
     * @param idPartida Identificador de partida a eliminar
     * @return true si eliminación exitosa, false si hubo errores
     * @post Si exitoso, partida eliminada permanentemente del sistema.
     */
    public boolean eliminarPartidaGuardada(Integer idPartida) {
        return presentationController.eliminarPartidaGuardada(idPartida);
    }
    

    /**
     * Retorna al menú principal preservando estado de ventana.
     * Navega de vuelta a la vista principal manteniendo dimensiones
     * y estado de maximización de la ventana actual.
     * 
     * @pre stage debe estar inicializado con escena válida.
     * @post Se navega al menú principal con estado preservado.
     */
    public void volverAMenuPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("/views/main-view.fxml"));
            Parent mainView = loader.load();
            
            Scene currentScene = stage.getScene();
            Scene newScene = new Scene(mainView, currentScene.getWidth(), currentScene.getHeight());
            stage.setScene(newScene);
            stage.setTitle("Scrabble");
            
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
     * Muestra alertas al usuario con formato consistente.
     * Proporciona método centralizado para mostrar mensajes con
     * diferentes tipos de severidad y formato uniforme.
     * 
     * @pre type, title y message no deben ser null.
     * @param type Tipo de alerta a mostrar
     * @param title Título de la ventana de alerta
     * @param message Contenido del mensaje
     * @post Se muestra alerta con formato apropiado al tipo.
     */    
    public void mostrarAlerta(String type, String title, String message) {
        presentationController.mostrarAlerta(type, title, message);
    }


    /**
     * Establece vista anterior para navegación de retorno.
     * Permite configurar a qué vista retornar cuando se active
     * funcionalidad de navegación hacia atrás.
     * 
     * @pre vista puede ser null si no hay vista anterior válida.
     * @param vista Vista anterior a la que retornar
     * @post Vista anterior registrada para navegación de retorno.
     */
    public void setVistaAnterior(Parent vista) {
        this.vistaAnterior = vista;
    }
    


    /**
     * Retorna a la vista anterior establecida previamente.
     * Muestra la vista anterior configurada o la vista de gestión
     * por defecto si no hay vista anterior definida.
     * 
     * @pre layout debe estar inicializado correctamente.
     * @post Se muestra vista anterior o vista de gestión por defecto.
     */
    public void volverAVistaAnterior() {
        if (vistaAnterior != null) {
            layout.setCenter(vistaAnterior);
        } else {
            mostrarVistaGestion();
        }
    }
    

    /**
     * Retorna a la vista de gestión de partidas reseteando configuración.
     * Limpia la configuración actual de la vista de configuración
     * y muestra la vista principal de gestión de partidas.
     * 
     * @pre vistaConfig debe estar inicializada correctamente.
     * @post Se muestra vista de gestión con configuración reseteada.
     */
    public void volver() {
        vistaConfig.reset();
        mostrarVistaGestion();
    }
    

    /**
     * Obtiene el tamaño actual configurado para el tablero.
     * Proporciona las dimensiones establecidas para la partida
     * actual o configuración temporal.
     * 
     * @pre size debe haber sido inicializado previamente.
     * @return Integer con el tamaño del tablero configurado
     * @post Se devuelve tamaño sin modificar configuración.
     */ 
    public Integer getSize() {
        return this.size;    
    }


    /**
     * Obtiene el nombre del diccionario actualmente configurado.
     * Proporciona el diccionario establecido para la partida
     * actual o configuración temporal.
     * 
     * @pre diccionario debe haber sido inicializado previamente.
     * @return String con nombre del diccionario configurado
     * @post Se devuelve diccionario sin modificar configuración.
     */
    public String getDiccionario() {
        return this.diccionario;
    }



    /**
     * Obtiene la lista de jugadores configurados para la partida.
     * Proporciona los jugadores que participarán en la partida
     * actual según la configuración establecida.
     * 
     * @pre jugadores debe haber sido inicializada previamente.
     * @return Lista de nombres de jugadores configurados
     * @post Se devuelve lista sin modificar configuración.
     */ 
    public List<String> getJugadoresDisponibles() {
        return this.jugadores;
    }


    /**
     * Obtiene lista completa de todos los jugadores registrados.
     * Proporciona acceso a todos los jugadores del sistema
     * independientemente de su estado o participación.
     * 
     * @pre No hay precondiciones específicas.
     * @return Lista de nombres de todos los jugadores registrados
     * @post Se devuelve lista completa sin modificar registro.
     */
    public List<String> getAllJugadores() {
       return presentationController.getAllJugadores(); 
    }


    /**
     * Establece el tamaño del tablero para la configuración actual.
     * Actualiza las dimensiones que se utilizarán para la partida
     * que se está configurando o va a iniciar.
     * 
     * @pre size no debe ser null y debe ser valor positivo válido.
     * @param size Nuevo tamaño de tablero a establecer
     * @post El tamaño queda actualizado en la configuración temporal.
     */
    public void setSize(Integer size) {
        this.size = size;
    }
    

    /**
     * Establece el diccionario para la configuración actual.
     * Actualiza el diccionario que se utilizará para la partida
     * que se está configurando o va a iniciar.
     * 
     * @pre diccName no debe ser null y debe corresponder a diccionario existente.
     * @param diccName Nombre del diccionario a establecer
     * @post El diccionario queda actualizado en la configuración temporal.
     */
    public void setDiccionario(String diccName) {
        this.diccionario = diccName;
    }


    /**
     * Obtiene lista de todos los diccionarios disponibles en el sistema.
     * Proporciona acceso a todos los diccionarios que pueden ser
     * utilizados para configurar y crear partidas.
     * 
     * @pre No hay precondiciones específicas.
     * @return Lista de nombres de diccionarios disponibles
     * @post Se devuelve lista completa sin modificar disponibilidad.
     */
    public List<String> getAllDiccionarios() {
        return presentationController.getAllDiccionariosDisponibles();    
    }


    /**
     * Añade un jugador a la configuración de partida actual.
     * Agrega el jugador especificado a la lista de participantes
     * para la partida que se está configurando.
     * 
     * @pre jugador no debe ser null ni estar vacío.
     * @param jugador Nombre del jugador a añadir
     * @post El jugador se añade a la lista de participantes configurados.
     */
    public void addJugador(String jugador) {
        this.jugadores.add(jugador);
    }


    /**
     * Resetea la lista de jugadores de la configuración actual.
     * Limpia todos los jugadores previamente configurados para
     * permitir una nueva selección desde cero.
     * 
     * @pre No hay precondiciones específicas.
     * @post La lista de jugadores queda vacía y lista para nueva configuración.
     */
    public void resetJugadores() {
        this.jugadores = new ArrayList<>();
    }

    /**
     * Obtiene lista de identificadores de partidas guardadas.
     * Proporciona acceso a todas las partidas que han sido guardadas
     * y están disponibles para cargar y continuar.
     * 
     * @pre No hay precondiciones específicas.
     * @return Lista de identificadores de partidas guardadas
     * @post Se devuelve lista sin modificar estado de partidas.
     */
    public List<Integer> getPartidasGuardadas() {
        return presentationController.getPartidasGuardadas();
    }
    
    /**
     * Obtiene el stage principal para acceso directo desde las vistas.
     * Proporciona referencia al stage de la aplicación para operaciones
     * que requieren manipulación directa de la ventana.
     * 
     * @pre stage debe haber sido inicializado en el constructor.
     * @return Stage principal de la aplicación
     * @post Se devuelve referencia sin modificar estado del stage.
     */
    public Stage getStage() {
        return stage;
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
     * Obtiene el diccionario configurado por defecto en el sistema.
     * Consulta el diccionario establecido como opción por defecto
     * para nuevas partidas cuando no se especifica otro.
     * 
     * @pre Sistema debe tener diccionario por defecto configurado.
     * @return String con nombre del diccionario por defecto
     * @post Se devuelve diccionario por defecto sin modificar configuración.
     */    
    public String getDiccionarioDefault() {
        return presentationController.getDiccionarioDefault();
    }


    /**
     * Obtiene el tamaño de tablero configurado por defecto en el sistema.
     * Consulta las dimensiones establecidas como opción por defecto
     * para nuevas partidas cuando no se especifica otro tamaño.
     * 
     * @pre Sistema debe tener tamaño por defecto configurado.
     * @return int con tamaño de tablero por defecto
     * @post Se devuelve tamaño por defecto sin modificar configuración.
     */
    public int getTamanoDefault() {
        return presentationController.getTamanoDefault();
    }


}