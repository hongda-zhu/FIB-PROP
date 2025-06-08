package scrabble.presentation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import scrabble.domain.controllers.ControladorDomain;
import scrabble.excepciones.ExceptionDiccionarioNotExist;
import scrabble.excepciones.ExceptionPersistenciaFallida;
import scrabble.helpers.Dificultad;
import scrabble.helpers.Direction;
import scrabble.helpers.Triple;
import scrabble.helpers.Tuple;
import scrabble.presentation.popups.customDialogo;


/**
 * Controlador principal de la capa de presentación del juego Scrabble.
 * Esta clase actúa como intermediario central entre las vistas de la interfaz
 * de usuario y la lógica de dominio, proporcionando una API unificada para
 * todas las operaciones del juego, gestión de usuarios y configuración.
 * 
 * Implementa el patrón Singleton para garantizar una única instancia durante
 * la ejecución de la aplicación y maneja la comunicación bidireccional entre
 * la capa de presentación y el dominio del negocio, encapsulando la complejidad
 * de las operaciones del dominio y proporcionando métodos específicos y seguros.
 * 
 * Características principales:
 * - Gestión completa de partidas (creación, carga, guardado, eliminación)
 * - Administración de jugadores humanos e IA con diferentes dificultades
 * - Control integral de diccionarios personalizados y predefinidos
 * - Manejo de configuración de la aplicación con persistencia automática
 * - Sistema de ranking con estadísticas detalladas de jugadores
 * - Validación de movimientos y gestión de turnos de juego
 * - Intercambio de fichas y gestión del estado del tablero
 * - Búsqueda y filtrado de jugadores con criterios personalizables
 * - Inicialización automática de datos por defecto para testing
 * - Sistema centralizado de alertas y notificaciones para la UI
 * - Gestión de temas visuales con persistencia de preferencias
 * - Control de audio (música y efectos de sonido) con configuración
 * - Manejo robusto de excepciones con mensajes informativos
 * 
 * El controlador utiliza el patrón Singleton para mantener consistencia en
 * el estado de la aplicación y coordina las operaciones complejas que requieren
 * múltiples interacciones con el dominio. Proporciona métodos especializados
 * para cada contexto de uso (partida, configuración, ranking, etc.) y
 * garantiza la integridad de los datos mediante validaciones apropiadas.
 * 
 * Todas las operaciones que pueden fallar están protegidas con manejo de
 * excepciones y proporcionan retroalimentación adecuada al usuario a través
 * del sistema de alertas integrado, manteniendo la estabilidad de la aplicación
 * incluso en casos de error o condiciones excepcionales.
 * 
 * @version 1.0
 * @since 1.0
 */
public class PresentationController {
    private static ControladorDomain ctrlDomain;
    private static PresentationController presentationController;

    /**
     * Obtiene la instancia única del controlador de presentación.
     * Implementa el patrón Singleton garantizando una única instancia durante
     * toda la ejecución de la aplicación e inicializa automáticamente el
     * controlador de dominio asociado en la primera invocación.
     * 
     * @pre No hay precondiciones específicas para la creación de la instancia.
     * @return La instancia única de PresentationController
     * @post Se devuelve la instancia única del controlador, creándola si es
     *       la primera invocación junto con su controlador de dominio asociado.
     *       Las invocaciones posteriores devuelven la misma instancia.
     */
    public static PresentationController getInstance() {
        if (presentationController == null) {
            presentationController = new PresentationController();
            ctrlDomain = ControladorDomain.getInstance();
        }
        return presentationController;
    }


    /**
     * Inicializa la configuración por defecto de la aplicación.
     * Configura diccionarios predefinidos, crea usuarios de prueba para testing
     * y establece jugadores IA con diferentes niveles de dificultad para
     * facilitar el desarrollo y testing de la aplicación.
     * 
     * @pre El controlador de dominio debe estar inicializado correctamente.
     * @post Se inicializan diccionarios predefinidos, se crean usuarios de prueba
     *       (xuanyi, jiahao, songhe, hongda) solo si no existen previamente,
     *       y se establecen jugadores IA con dificultades fácil y difícil.
     */
    public void initializeDefaultSettings() {
        inicializarDiccionarios();
        // Registrar algunos usuarios por defecto para testing solo si no existen
        if (!ctrlDomain.existeJugador("xuanyi")) ctrlDomain.registrarUsuario("xuanyi");
        if (!ctrlDomain.existeJugador("jiahao")) ctrlDomain.registrarUsuario("jiahao");
        if (!ctrlDomain.existeJugador("songhe")) ctrlDomain.registrarUsuario("songhe");
        if (!ctrlDomain.existeJugador("hongda")) ctrlDomain.registrarUsuario("hongda");
        ctrlDomain.crearJugadorIA(Dificultad.FACIL, "DummyEZ");
        ctrlDomain.crearJugadorIA(Dificultad.DIFICIL, "DummyHardCore");
    }



    /**
     * Inicia una nueva partida con los jugadores y configuración especificados.
     * Crea un mapa de jugadores con sus identificadores, valida la configuración
     * y delega al controlador de dominio para inicializar la partida con el
     * diccionario y tamaño de tablero especificados.
     * 
     * @pre jugadores no debe ser null ni estar vacía, diccionario debe existir,
     *      tamanioTablero debe ser un valor válido positivo.
     * @param jugadores Lista de nombres de jugadores participantes
     * @param diccionario Nombre del diccionario a utilizar en la partida
     * @param tamanioTablero Tamaño del tablero para la partida
     * @return true si la partida se inició correctamente, false si hubo errores
     * @post Si es exitoso, se inicia una nueva partida con la configuración
     *       especificada. Si falla, se retorna false y la partida no se inicia.
     * @throws ExceptionPersistenciaFallida si hay errores de persistencia durante la inicialización
     */
    public boolean iniciarPartida(List<String> jugadores, String diccionario, Integer tamanioTablero) throws ExceptionPersistenciaFallida {
        try {
            // Crear un mapa con los jugadores y sus IDs
            Map<String, Integer> jugadoresMap = new HashMap<>();
            for (int i = 0; i < jugadores.size(); i++) {
                jugadoresMap.put(jugadores.get(i), i);
            }

            ctrlDomain.managePartidaIniciar(diccionario, jugadoresMap, tamanioTablero);
            return true;
        } catch (IOException e) {
            return false;
        }
    }



    /**
     * Obtiene el nombre del diccionario de la partida actualmente cargada.
     * Consulta el controlador de dominio para obtener información sobre
     * el diccionario que se está utilizando en la partida actual.
     * 
     * @pre Debe haber una partida activa o cargada en el sistema.
     * @return String con el nombre del diccionario de la partida actual
     * @post Se devuelve el nombre del diccionario sin modificar el estado
     *       de la partida ni realizar validaciones adicionales.
     */
    public String getNombreDiccionario() {
        return ctrlDomain.getNombreDiccionario();
    }


    /**
     * Obtiene el tamaño del tablero de la partida actual.
     * Consulta las dimensiones del tablero configurado para la partida
     * que está actualmente en curso o cargada en el sistema.
     * 
     * @pre Debe haber una partida activa o cargada con tablero configurado.
     * @return int con el tamaño del tablero (número de casillas por lado)
     * @post Se devuelve el tamaño del tablero sin modificar la configuración
     *       ni el estado actual de la partida.
     */
    public int getTableroSize() {
        return ctrlDomain.getSize();
    }


    /**
     * Obtiene la cantidad de fichas restantes en la bolsa de la partida actual.
     * Consulta el número de fichas disponibles que quedan por repartir
     * durante el transcurso de la partida activa.
     * 
     * @pre Debe haber una partida activa con bolsa de fichas inicializada.
     * @return int con el número de fichas restantes en la bolsa
     * @post Se devuelve la cantidad actual de fichas sin modificar el estado
     *       de la bolsa ni realizar extracciones de fichas.
     */
    public int getCantidadFichasRestantes() {
        return ctrlDomain.getCantidadFichasRestantes();
    }
    

    /**
     * Obtiene el estado detallado de la partida para un jugador específico.
     * Proporciona información completa sobre el estado actual de la partida
     * desde la perspectiva del jugador especificado, incluyendo puntuaciones,
     * turno actual y otra información relevante del juego.
     * 
     * @pre nombreJugador no debe ser null y debe ser un participante de la partida activa.
     * @param nombreJugador Nombre del jugador para el cual obtener el estado
     * @return String con la representación textual del estado de la partida
     * @post Se devuelve información completa del estado sin modificar el juego
     *       ni revelar información privada de otros jugadores.
     */
    public String getEstadoPartida(String nombreJugador) {
        return ctrlDomain.mostrarStatusPartida(nombreJugador);
    }


    /**
     * Obtiene el estado actual completo del tablero de juego.
     * Proporciona un mapa con todas las posiciones del tablero y las fichas
     * colocadas en cada posición, representando el estado visual completo
     * del tablero en el momento de la consulta.
     * 
     * @pre Debe haber una partida activa con tablero inicializado.
     * @return Map que asocia posiciones del tablero con las letras colocadas
     * @post Se devuelve una representación completa del estado del tablero
     *       sin modificar las posiciones ni el estado de la partida.
     */
    public Map<Tuple<Integer, Integer>, String> getEstadoTablero() {
        return ctrlDomain.getEstadoTablero();
    }    



    /**
     * Obtiene el rack de fichas de un jugador específico.
     * Proporciona un mapa con las letras disponibles en el rack del jugador
     * y la cantidad de cada letra que posee para realizar jugadas.
     * 
     * @pre nombreJugador no debe ser null y debe participar en la partida activa.
     * @param nombreJugador Nombre del jugador cuyo rack se desea consultar
     * @return Map que asocia letras con sus cantidades en el rack del jugador
     * @post Se devuelve el contenido actual del rack sin modificar las fichas
     *       ni realizar validaciones de jugadas posibles.
     */
    public Map<String, Integer> getRackJugador(String nombreJugador) {
        return ctrlDomain.getRack(nombreJugador);
    }


    /**
     * Resetea el contador de saltos de turno de un jugador específico.
     * Limpia el historial de turnos saltados para el jugador especificado,
     * permitiendo un reinicio limpio del seguimiento de saltos de turno.
     * 
     * @pre nombreJugador no debe ser null y debe participar en la partida activa.
     * @param nombreJugador Nombre del jugador cuyo contador resetear
     * @post El contador de saltos de turno del jugador se restablece a cero,
     *       permitiendo un seguimiento limpio de futuros saltos de turno.
     */
    public void clearSkipTrack(String nombreJugador) {
        ctrlDomain.clearSkipTrack(nombreJugador);
    }


    /**
     * Obtiene una representación textual del rack de un jugador.
     * Proporciona una cadena formateada que muestra las fichas disponibles
     * del jugador de manera legible para visualización en la interfaz.
     * 
     * @pre nombreJugador no debe ser null y debe participar en la partida activa.
     * @param nombreJugador Nombre del jugador cuyo rack mostrar
     * @return String con representación textual formateada del rack
     * @post Se devuelve una representación legible del rack sin modificar
     *       el contenido real de las fichas del jugador.
     */
    public String mostrarRackJugador(String nombreJugador) {
        return ctrlDomain.mostrarRack(nombreJugador);
    }


    /**
     * Verifica si un movimiento propuesto es válido según las reglas del juego.
     * Valida la jugada considerando las fichas disponibles en el rack del jugador,
     * las reglas de colocación de palabras y la disponibilidad de posiciones
     * en el tablero para el movimiento especificado.
     * 
     * @pre move no debe ser null y rack debe contener las fichas del jugador.
     * @param move Triple que especifica palabra, posición inicial y dirección
     * @param rack Mapa con las fichas disponibles del jugador
     * @return true si el movimiento es válido según las reglas, false en caso contrario
     * @post Se devuelve la validez del movimiento sin modificar el estado del
     *       tablero ni consumir fichas del rack del jugador.
     */
    public boolean esMovimientoValido(Triple<String, Tuple<Integer, Integer>, Direction> move, Map<String, Integer> rack) {
        return ctrlDomain.isValidMove(move, rack);
    }


    /**
     * Ejecuta un turno de juego para un jugador con la jugada especificada.
     * Procesa la jugada del jugador, actualiza el estado del tablero, calcula
     * puntuaciones obtenidas y avanza el estado de la partida al siguiente turno.
     * 
     * @pre nombreJugador y jugada no deben ser null, debe ser el turno del jugador.
     * @param nombreJugador Nombre del jugador que realiza la jugada
     * @param jugada Triple con palabra, posición y dirección de la jugada
     * @return int con los puntos obtenidos por la jugada realizada
     * @post La jugada se ejecuta en el tablero, se actualizan las puntuaciones,
     *       se consumen las fichas utilizadas y se avanza al siguiente turno.
     */
    public int realizarTurnoPartida(String nombreJugador, Triple<String, Tuple<Integer, Integer>, Direction> jugada) {
        return ctrlDomain.realizarTurnoPartida(nombreJugador, jugada);
    }


    /**
     * Actualiza la puntuación acumulada de un jugador específico.
     * Añade los puntos especificados al total acumulado del jugador,
     * manteniendo el registro actualizado de puntuaciones durante la partida.
     * 
     * @pre nombreJugador no debe ser null y puntos debe ser un valor válido.
     * @param nombreJugador Nombre del jugador cuya puntuación actualizar
     * @param puntos Cantidad de puntos a añadir al total del jugador
     * @post La puntuación del jugador se incrementa con los puntos especificados
     *       y se actualiza el registro de puntuaciones de la partida.
     */
    public void actualizarJugadores(String nombreJugador, int puntos) {
        ctrlDomain.actualizarJugadores(nombreJugador, puntos);
    }


    /**
     * Comprueba si las condiciones de finalización de la partida se han cumplido.
     * Evalúa criterios como fichas agotadas, racks vacíos, o múltiples saltos
     * consecutivos para determinar si la partida debe finalizar automáticamente.
     * 
     * @pre jugadoresSeleccionados no debe ser null y debe contener participantes válidos.
     * @param jugadoresSeleccionados Mapa con jugadores y sus identificadores
     * @post Se evalúan las condiciones de fin de partida y se actualiza el
     *       estado interno si se determina que el juego debe terminar.
     */
    public void comprobarFinPartida(Map<String, Integer> jugadoresSeleccionados) {
        ctrlDomain.comprobarFinPartida(jugadoresSeleccionados);
    }


    /**
     * Verifica si el juego actual ha terminado según las condiciones establecidas.
     * Consulta el estado interno del juego para determinar si la partida
     * ha finalizado por cualquiera de los criterios de terminación válidos.
     * 
     * @pre Debe haber una partida activa para consultar su estado.
     * @return true si el juego ha terminado, false si continúa en curso
     * @post Se devuelve el estado de finalización sin modificar las condiciones
     *       del juego ni forzar la terminación de la partida.
     */
    public boolean isJuegoTerminado() {
        return ctrlDomain.isJuegoTerminado();
    }


    /**
     * Finaliza la partida actual y calcula los resultados finales.
     * Procesa las puntuaciones finales, determina el ganador, actualiza
     * estadísticas de jugadores y proporciona un resumen completo de la partida.
     * 
     * @pre Debe haber una partida activa y jugadoresSeleccionados no debe ser null.
     * @param jugadoresSeleccionados Mapa con jugadores participantes
     * @return String con el resumen de resultados y ganador de la partida
     * @post La partida se finaliza oficialmente, se actualizan estadísticas de
     *       jugadores, se registran victorias y se libera el estado de la partida.
     */
    public String finalizarJuego(Map<String, Integer> jugadoresSeleccionados) {
        return ctrlDomain.finalizarJuego(jugadoresSeleccionados);
    }


    /**
     * Permite a un jugador intercambiar fichas de su rack con la bolsa.
     * Intercambia las fichas especificadas del rack del jugador con fichas
     * aleatorias de la bolsa, consumiendo un turno en el proceso.
     * 
     * @pre nombreJugador y fichas no deben ser null, fichas deben estar en el rack.
     * @param nombreJugador Nombre del jugador que realiza el intercambio
     * @param fichas Lista de fichas que el jugador desea intercambiar
     * @return true si el intercambio fue exitoso, false si no se pudo realizar
     * @post Si es exitoso, las fichas especificadas se intercambian con la bolsa
     *       y se consume el turno del jugador. Si falla, no se realizan cambios.
     */
    public boolean intercambiarFichas(String nombreJugador, List<String> fichas) {
        return ctrlDomain.intercambiarFichas(nombreJugador, fichas);
    }


    /**
     * Obtiene el mapa de jugadores actualmente participantes en la partida.
     * Proporciona información sobre todos los jugadores que están participando
     * en la partida activa con sus identificadores correspondientes.
     * 
     * @pre Debe haber una partida activa con jugadores inicializados.
     * @return Map que asocia nombres de jugadores con sus identificadores
     * @post Se devuelve información de jugadores actuales sin modificar el
     *       estado de la partida ni la configuración de participantes.
     */
    public Map<String, Integer> getJugadoresActuales() {
        return ctrlDomain.getJugadoresActuales();
    }


    /**
     * Libera los jugadores de la partida actual al finalizar o abandonar.
     * Marca a todos los jugadores como no participantes de ninguna partida,
     * permitiendo que puedan unirse a nuevas partidas posteriormente.
     * 
     * @pre Debe haber jugadores asignados a una partida activa.
     * @post Todos los jugadores quedan liberados de la partida actual y
     *       disponibles para participar en nuevas partidas futuras.
     */
    public void liberarJugadoresActuales() {
        ctrlDomain.aliberarJugadoresActuales();
    }


    /**
     * Guarda el estado actual de la partida para cargar posteriormente.
     * Persiste todos los datos necesarios de la partida incluyendo estado del
     * tablero, racks de jugadores, puntuaciones y turno actual para permitir
     * continuación posterior del juego desde el punto exacto de guardado.
     * 
     * @pre Debe haber una partida activa y jugadoresSeleccionados no debe ser null.
     * @param jugadoresSeleccionados Lista de jugadores participantes
     * @param turnoActual Índice del jugador que tiene el turno actual
     * @return true si el guardado fue exitoso, false si hubo errores
     * @post Si es exitoso, el estado completo de la partida se persiste para
     *       carga posterior. Si falla, la partida continúa sin cambios.
     */
    public boolean guardarPartida(List<String> jugadoresSeleccionados, int turnoActual) {
        return ctrlDomain.guardarPartida(jugadoresSeleccionados, turnoActual);
    }


    /**
     * Obtiene la lista de identificadores de todas las partidas guardadas.
     * Proporciona los IDs de todas las partidas que han sido guardadas
     * previamente y están disponibles para cargar y continuar.
     * 
     * @pre No hay precondiciones específicas para consultar partidas guardadas.
     * @return Lista de enteros con los identificadores de partidas guardadas
     * @post Se devuelve la lista de IDs disponibles sin modificar el estado
     *       de las partidas guardadas ni realizar operaciones de carga.
     */
    public List<Integer> getPartidasGuardadas() {
        return ctrlDomain.getPartidasGuardadas();
    }


    /**
     * Obtiene el nombre del diccionario utilizado en una partida guardada específica.
     * Consulta la información de configuración de una partida guardada para
     * obtener el diccionario que se estaba utilizando en esa partida.
     * 
     * @pre id no debe ser null y debe corresponder a una partida guardada válida.
     * @param id Identificador de la partida guardada a consultar
     * @return String con el nombre del diccionario de la partida especificada
     * @post Se devuelve el nombre del diccionario sin cargar la partida
     *       ni modificar su estado guardado.
     */
    public String getDiccionarioPartida(Integer id) {
        return ctrlDomain.obtenerDiccionarioPartida(id);
    }


    /**
     * Obtiene el número de jugadores de una partida guardada específica.
     * Consulta cuántos jugadores estaban participando en la partida
     * guardada identificada por el ID especificado.
     * 
     * @pre id no debe ser null y debe corresponder a una partida guardada válida.
     * @param id Identificador de la partida guardada a consultar
     * @return int con el número de jugadores de la partida especificada
     * @post Se devuelve el número de participantes sin cargar la partida
     *       ni modificar su estado guardado.
     */
    public int getNumJugadoresPartida (Integer id) {
        return ctrlDomain.getNumJugadoresPartida(id);
    }


    /**
     * Carga una partida previamente guardada para continuar jugando.
     * Restaura el estado completo de la partida especificada incluyendo
     * tablero, racks, puntuaciones, turno actual y configuración para
     * permitir continuación exacta desde el punto de guardado.
     * 
     * @pre idPartida no debe ser null y debe corresponder a una partida guardada válida.
     * @param idPartida Identificador de la partida guardada a cargar
     * @post La partida especificada se carga completamente, restaurando todos
     *       los datos y estado para continuar el juego desde el punto exacto
     *       donde se guardó previamente.
     */
    public void cargarPartida(Integer idPartida) {
        ctrlDomain.cargarPartida(idPartida);
    }


    /**
     * Obtiene el índice del jugador que tiene el turno actual.
     * Proporciona información sobre qué jugador debe realizar la siguiente
     * jugada en la partida activa según el orden de turnos establecido.
     * 
     * @pre Debe haber una partida activa con turnos inicializados.
     * @return int con el índice del jugador que tiene el turno actual
     * @post Se devuelve el índice del turno actual sin modificar el orden
     *       de jugadores ni avanzar el turno de la partida.
     */
    public int getJugadorActualIdx() {
        return ctrlDomain.getTurnoActual();
    }


    /**
     * Elimina permanentemente una partida guardada del sistema.
     * Borra todos los datos asociados con la partida guardada especificada,
     * liberando espacio de almacenamiento y removiendo la partida de la
     * lista de partidas disponibles para cargar.
     * 
     * @pre idPartida no debe ser null y debe corresponder a una partida guardada válida.
     * @param idPartida Identificador de la partida guardada a eliminar
     * @return true si la eliminación fue exitosa, false si hubo errores
     * @post Si es exitoso, la partida especificada se elimina permanentemente
     *       del sistema. Si falla, la partida permanece sin cambios.
     */
    public boolean eliminarPartidaGuardada(Integer idPartida) {
        return ctrlDomain.eliminarPartidaGuardada(idPartida);
    }


    /**
     * Obtiene la lista de todos los jugadores disponibles para nuevas partidas.
     * Proporciona una lista de jugadores que no están actualmente participando
     * en ninguna partida y están disponibles para unirse a nuevas partidas.
     * 
     * @pre No hay precondiciones específicas para consultar jugadores disponibles.
     * @return Lista de nombres de jugadores disponibles para partidas
     * @post Se devuelve la lista de jugadores libres sin modificar su estado
     *       ni asignarlos a ninguna partida específica.
     */
    public List<String> getAllJugadoresDisponibles() {
        Set<String> jugadores = ctrlDomain.getAllJugadores();
        List<String> disponibles  = new ArrayList<>();
        for (String nombre : jugadores) {
            if (!ctrlDomain.isEnPartida(nombre)) {
                disponibles.add(nombre);
            }
        }

        return disponibles;         
    }


    /**
     * Obtiene la lista completa de todos los jugadores humanos registrados.
     * Proporciona todos los jugadores humanos del sistema, independientemente
     * de si están participando en partidas o disponibles para nuevas partidas.
     * 
     * @pre No hay precondiciones específicas para consultar todos los jugadores.
     * @return Lista de nombres de todos los jugadores humanos registrados
     * @post Se devuelve la lista completa sin modificar el estado de ningún
     *       jugador ni realizar operaciones de filtrado adicionales.
     */
    public List<String> getAllJugadores() {
        return ctrlDomain.getUsuariosHumanos();
    }


    /**
     * Obtiene el número total de victorias de un jugador específico.
     * Consulta las estadísticas históricas del jugador para obtener
     * el recuento total de partidas ganadas a lo largo del tiempo.
     * 
     * @pre nombre no debe ser null y debe corresponder a un jugador registrado.
     * @param nombre Nombre del jugador cuyas victorias consultar
     * @return int con el número total de victorias del jugador
     * @post Se devuelve el recuento de victorias sin modificar las estadísticas
     *       del jugador ni realizar actualizaciones en sus datos.
     */
    public int getVictorias(String nombre) {
        return ctrlDomain.getVictorias(nombre);
    }


    /**
     * Verifica si un jugador específico está actualmente participando en una partida.
     * Consulta el estado actual del jugador para determinar si está ocupado
     * en una partida activa o disponible para unirse a nuevas partidas.
     * 
     * @pre nombre no debe ser null y debe corresponder a un jugador registrado.
     * @param nombre Nombre del jugador cuyo estado consultar
     * @return true si el jugador está en una partida activa, false si está disponible
     * @post Se devuelve el estado actual sin modificar la participación del
     *       jugador ni afectar su disponibilidad para partidas.
     */
    public boolean isEnPartida(String nombre) {
        return ctrlDomain.isEnPartida(nombre);
    }


    /**
     * Verifica si un jugador específico es controlado por inteligencia artificial.
     * Determina si el jugador especificado es un jugador IA del sistema
     * o un jugador humano controlado por un usuario real.
     * 
     * @pre nombre no debe ser null y debe corresponder a un jugador registrado.
     * @param nombre Nombre del jugador a verificar
     * @return true si es un jugador IA, false si es un jugador humano
     * @post Se devuelve el tipo de jugador sin modificar su configuración
     *       ni afectar su comportamiento en el juego.
     */
    public boolean esIA(String nombre) {
        return ctrlDomain.esIA(nombre);
    }


    /**
     * Obtiene el nombre identificador de la partida actual de un jugador.
     * Proporciona información sobre qué partida específica está jugando
     * actualmente el jugador especificado, si está participando en alguna.
     * 
     * @pre nombre no debe ser null y debe corresponder a un jugador en partida.
     * @param nombre Nombre del jugador cuya partida actual consultar
     * @return String con el identificador de la partida actual del jugador
     * @post Se devuelve el nombre de la partida sin modificar la participación
     *       del jugador ni afectar el estado de la partida.
     */
    public String getNombrePartidaActual(String nombre) {
        return ctrlDomain.getNombrePartidaActual(nombre);
    }


    /**
     * Obtiene la lista de todos los diccionarios disponibles en el sistema.
     * Proporciona los nombres de todos los diccionarios que pueden ser
     * utilizados para crear nuevas partidas, incluyendo predefinidos y personalizados.
     * 
     * @pre No hay precondiciones específicas para consultar diccionarios disponibles.
     * @return Lista de nombres de diccionarios disponibles para usar
     * @post Se devuelve la lista completa sin modificar la configuración
     *       de diccionarios ni realizar operaciones de carga específicas.
     */
    public List<String> getAllDiccionariosDisponibles() {
        return ctrlDomain.getDiccionariosDisponibles();
    }


    /**
     * Inicializa los jugadores participantes para una nueva partida.
     * Prepara el estado interno de los jugadores especificados para
     * participar en una nueva partida, marcándolos como ocupados.
     * 
     * @pre jugadores no debe ser null ni estar vacía, jugadores deben estar disponibles.
     * @param jugadores Lista de nombres de jugadores a inicializar
     * @post Los jugadores especificados quedan marcados como participantes
     *       de la nueva partida y no disponibles para otras partidas.
     */
    public void iniciarPartida(ArrayList<String> jugadores) {
        ctrlDomain.inicializarJugadoresPartida(jugadores);
    }


     /**
     * Crea un nuevo jugador humano en el sistema.
     * Registra un nuevo jugador con el nombre especificado, inicializa
     * sus estadísticas básicas y lo marca como disponible para partidas.
     * 
     * @pre nombre no debe ser null, estar vacío, ni corresponder a un jugador existente.
     * @param nombre Nombre único del jugador a crear
     * @return true si el jugador se creó exitosamente, false si hubo errores
     * @post Si es exitoso, el jugador queda registrado con estadísticas iniciales.
     *       Si falla (nombre duplicado, etc.), no se realizan cambios.
     */ 
    public boolean crearJugador(String nombre) {
        // Implementar llamada al modelo para crear un jugador
        return ctrlDomain.registrarUsuario(nombre);
    }


    /**
     * Elimina permanentemente un jugador del sistema.
     * Remueve al jugador especificado del registro, incluyendo todas sus
     * estadísticas históricas, puntuaciones y datos asociados del sistema.
     * 
     * @pre nombre no debe ser null y debe corresponder a un jugador registrado
     *      que no esté participando actualmente en ninguna partida activa.
     * @param nombre Nombre del jugador a eliminar del sistema
     * @return true si la eliminación fue exitosa, false si hubo errores
     * @post Si es exitoso, el jugador y todos sus datos se eliminan permanentemente.
     *       Si falla (jugador en partida, etc.), no se realizan cambios.
     */
    public boolean eliminarJugador(String nombre) {
        // Implementar llamada al modelo para eliminar un jugador
        return ctrlDomain.eliminarUsuario(nombre);
    }


    /**
     * Busca jugadores cuyo nombre coincida con el patrón especificado.
     * Filtra la lista de jugadores humanos registrados para encontrar
     * aquellos cuyos nombres contengan el patrón de búsqueda especificado,
     * excluyendo automáticamente jugadores IA de los resultados.
     * 
     * @pre patron puede ser null (devuelve todos los jugadores humanos).
     * @param patron Cadena de texto a buscar en los nombres de jugadores
     * @return Lista de nombres de jugadores que coinciden con el patrón
     * @post Se devuelve una lista filtrada de jugadores humanos que contienen
     *       el patrón en sus nombres, sin modificar el registro de jugadores.
     */
    public List<String> buscarJugadoresPorNombre(String patron) {
        List<String> resultados = new ArrayList<>();
        List<String> todos = getAllJugadores();
        
        if (patron == null || patron.trim().isEmpty()) {
            resultados.addAll(todos);
        } else {
            String patronLower = patron.toLowerCase();
            for (String jugador : todos) {
                if (jugador.toLowerCase().contains(patronLower) && !esIA(jugador)) {
                    resultados.add(jugador);
                }
            }
        }
        
        return resultados;
    }


    /**
     * Obtiene la lista de diccionarios disponibles para usar en partidas.
     * Proporciona todos los diccionarios que están actualmente disponibles
     * en el sistema, incluyendo diccionarios predefinidos y personalizados.
     * 
     * @pre No hay precondiciones específicas para consultar diccionarios.
     * @return Lista de nombres de diccionarios disponibles en el sistema
     * @post Se devuelve la lista completa de diccionarios sin modificar
     *       su disponibilidad ni realizar operaciones de carga específicas.
     */
    public List<String> getDiccionariosDisponibles() {
        // Implementar llamada al modelo para obtener diccionarios disponibles
        return ctrlDomain.getDiccionariosDisponibles();
    }


    /**
     * Crea un nuevo diccionario personalizado en el sistema.
     * Registra un diccionario con el nombre especificado cargando su contenido
     * desde la ruta de archivo proporcionada, validando formato y estructura.
     * 
     * @pre nombre no debe ser null ni vacío, path debe ser una ruta válida
     *      a un archivo de diccionario con formato correcto.
     * @param nombre Nombre único para el nuevo diccionario
     * @param path Ruta del archivo que contiene los datos del diccionario
     * @post Si es exitoso, el diccionario queda disponible para usar en partidas.
     * @throws RuntimeException si hay errores durante la creación o carga
     */
    public void crearDiccionario(String nombre, List<String> alfabeto, List<String> palabras) {
        try {
            ctrlDomain.crearDiccionario(nombre, alfabeto, palabras);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Elimina permanentemente un diccionario del sistema.
     * Remueve el diccionario especificado del registro, haciéndolo no disponible
     * para nuevas partidas y liberando recursos asociados.
     * 
     * @pre nombre no debe ser null y debe corresponder a un diccionario existente
     *      que no esté siendo usado en partidas activas.
     * @param nombre Nombre del diccionario a eliminar
     * @post El diccionario se elimina permanentemente del sistema.
     * @throws RuntimeException si hay errores durante la eliminación
     */
    public void eliminarDiccionario(String nombre) {
        try {
            ctrlDomain.eliminarDiccionario(nombre);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * Modifica el contenido de un diccionario añadiendo o removiendo una palabra.
     * Permite actualizar dinámicamente el contenido de diccionarios existentes
     * agregando nuevas palabras válidas o removiendo palabras no deseadas.
     * 
     * @pre nombreDiccionario y palabra no deben ser null, diccionario debe existir.
     * @param nombreDiccionario Nombre del diccionario a modificar
     * @param palabra Palabra a añadir o remover del diccionario
     * @param add true para añadir la palabra, false para removerla
     * @post La palabra se añade o remueve del diccionario según el parámetro add.
     * @throws RuntimeException si hay errores durante la modificación
     */
    public void modificarPalabraDiccionario(String nombreDiccionario, String palabra, boolean add) {
        try {
            ctrlDomain.modificarPalabraDiccionario(nombreDiccionario, palabra, add);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Obtiene el alfabeto con valores de puntuación de un diccionario específico.
     * Proporciona un mapa que asocia cada letra del alfabeto del diccionario
     * con su valor de puntuación correspondiente para cálculos de puntaje.
     * 
     * @pre nombre no debe ser null y debe corresponder a un diccionario existente.
     * @param nombre Nombre del diccionario cuyo alfabeto consultar
     * @return Map que asocia letras con sus valores de puntuación, null si hay error
     * @post Se devuelve el alfabeto con puntuaciones sin modificar el diccionario.
     */
    public Map<String, Integer> getAlphabet(String nombre) {
        try {
            return ctrlDomain.getAlphabet(nombre);        
        } catch (ExceptionDiccionarioNotExist | IOException e) {
            mostrarAlerta("error", "Diccionario no existe", e.getMessage());
            return null;
        }        
    }


    /**
     * Obtiene la lista completa de palabras válidas de un diccionario.
     * Proporciona todas las palabras que están registradas como válidas
     * en el diccionario especificado para verificación durante el juego.
     * 
     * @pre dic no debe ser null y debe corresponder a un diccionario existente.
     * @param dic Nombre del diccionario cuyas palabras consultar
     * @return Lista de todas las palabras válidas del diccionario
     * @post Se devuelve la lista completa de palabras sin modificar el diccionario.
     */
    public List<String> getListaPalabras(String dic) {
        return ctrlDomain.getListaPalabras(dic);
    }


    /**
     * Obtiene la lista del alfabeto con puntuaciones y frecuencias de un diccionario.
     * Proporciona información detallada sobre cada letra del alfabeto incluyendo
     * su puntuación y frecuencia en formato "letra puntuacion frecuencia".
     * 
     * @pre dic no debe ser null y debe corresponder a un diccionario existente.
     * @param dic Nombre del diccionario cuyo alfabeto consultar
     * @return Lista de strings con formato "letra puntuacion frecuencia"
     * @post Se devuelve información completa del alfabeto sin modificar el diccionario.
     */
    public List<String> getListaAlfabeto(String dic) {
        return ctrlDomain.getListaAlfabeto(dic);
    }


    /**
     * Obtiene la lista de todos los jugadores que aparecen en el ranking.
     * Proporciona los nombres de jugadores que tienen estadísticas registradas
     * y están incluidos en el sistema de ranking del juego.
     * 
     * @pre No hay precondiciones específicas para consultar el ranking.
     * @return Lista de nombres de jugadores que aparecen en el ranking
     * @post Se devuelve la lista de jugadores en ranking sin modificar estadísticas.
     */
    public List<String> getUsuariosRanking() {
        return ctrlDomain.getUsuariosRanking();
    }

 
     /**
     * Elimina un usuario del ranking y resetea su puntuación total a cero.
     * Remueve al jugador especificado del sistema de ranking y reinicia
     * todas sus estadísticas acumuladas a valores iniciales.
     * 
     * @pre nombre no debe ser null y debe corresponder a un jugador registrado.
     * @param nombre Nombre del usuario a eliminar del ranking
     * @return true si se eliminó correctamente, false en caso contrario
     * @post Si es exitoso, el jugador se elimina del ranking y sus estadísticas
     *       se resetean. Si falla, no se realizan cambios en el ranking.
     */
    public boolean eliminarUsuarioRanking(String nombre) {
        // Primero, capturar la información antes de eliminar
        return ctrlDomain.eliminarUsuario(nombre);
    }


    /**
     * Obtiene la puntuación máxima alcanzada por un jugador en una sola partida.
     * Consulta las estadísticas históricas del jugador para encontrar
     * la puntuación más alta obtenida en cualquier partida individual.
     * 
     * @pre nombre no debe ser null y debe corresponder a un jugador registrado.
     * @param nombre Nombre del jugador cuya puntuación máxima consultar
     * @return int con la puntuación máxima alcanzada por el jugador
     * @post Se devuelve la puntuación máxima sin modificar las estadísticas.
     */
    public int getPuntuacionMaxima(String nombre) {
        return ctrlDomain.getPuntuacionMaxima(nombre);
    }



    /**
     * Obtiene la puntuación media de todas las partidas jugadas por un jugador.
     * Calcula el promedio de puntuaciones obtenidas por el jugador a lo
     * largo de todas sus partidas registradas en el sistema.
     * 
     * @pre nombre no debe ser null y debe corresponder a un jugador registrado.
     * @param nombre Nombre del jugador cuya puntuación media consultar
     * @return double con la puntuación media del jugador
     * @post Se devuelve la puntuación media calculada sin modificar estadísticas.
     */
    public double getPuntuacionMedia(String nombre) {
        return ctrlDomain.getPuntuacionMedia(nombre);
    }


    /**
     * Obtiene el número total de partidas jugadas por un jugador específico.
     * Consulta las estadísticas del jugador para obtener el recuento total
     * de partidas en las que ha participado, completas o no.
     * 
     * @pre nombre no debe ser null y debe corresponder a un jugador registrado.
     * @param nombre Nombre del jugador cuyas partidas consultar
     * @return int con el número total de partidas jugadas por el jugador
     * @post Se devuelve el número de partidas sin modificar las estadísticas.
     */
    public int getPartidasJugadas(String nombre) {
        return ctrlDomain.getPartidasJugadas(nombre);
    }


    /**
     * Obtiene el número total de victorias de un jugador específico.
     * Consulta las estadísticas del jugador para obtener el recuento de
     * partidas ganadas a lo largo de su historial de juego.
     * 
     * @pre nombre no debe ser null y debe corresponder a un jugador registrado.
     * @param nombre Nombre del jugador cuyas victorias consultar
     * @return int con el número total de victorias del jugador
     * @post Se devuelve el número de victorias sin modificar las estadísticas.
     */

    public int getvictorias(String nombre) {
        return ctrlDomain.getVictorias(nombre);
    }


    /**
     * Obtiene la puntuación total acumulada de un jugador a lo largo de todas sus partidas.
     * Consulta la suma total de puntos obtenidos por el jugador en todas
     * las partidas completadas registradas en el sistema.
     * 
     * @pre nombre no debe ser null y debe corresponder a un jugador registrado.
     * @param nombre Nombre del jugador cuya puntuación total consultar
     * @return int con la puntuación total acumulada del jugador
     * @post Se devuelve la puntuación total sin modificar las estadísticas.
     */
    public int getPuntuacionTotal(String nombre) {
        return ctrlDomain.getPuntuacionTotal(nombre);
    }


    /**
     * Obtiene la lista de todas las puntuaciones individuales de un jugador.
     * Proporciona el historial completo de puntuaciones obtenidas por el
     * jugador en cada partida individual que ha completado.
     * 
     * @pre nombre no debe ser null y debe corresponder a un jugador registrado.
     * @param nombre Nombre del jugador cuyas puntuaciones consultar
     * @return Lista de enteros con todas las puntuaciones del jugador
     * @post Se devuelve el historial completo sin modificar las estadísticas.
     */
    public List<Integer> getPuntuacionesUsuario(String nombre) {
        return ctrlDomain.getPuntuacionesUsuario(nombre);
    }



    /**
     * Guarda la configuración general de la aplicación de forma persistente.
     * Almacena las preferencias del usuario incluyendo tema visual, configuración
     * de audio y niveles de volumen en un archivo de configuración para
     * mantener las preferencias entre sesiones de la aplicación.
     * 
     * @pre Los parámetros de configuración deben tener valores válidos.
     * @param tema Tema visual seleccionado por el usuario
     * @param musicaActivada Estado de activación de la música de fondo
     * @param sonidoActivado Estado de activación de efectos de sonido
     * @param volumenMusica Nivel de volumen para la música (0-100)
     * @param volumenSonido Nivel de volumen para efectos de sonido (0-100)
     * @post La configuración se guarda persistentemente y estará disponible
     *       en futuras sesiones de la aplicación.
     */
    public void guardarConfiguracionGeneral( String tema, boolean musicaActivada, boolean sonidoActivado, int volumenMusica, int volumenSonido) {
        ctrlDomain.guardarConfiguracionGeneral( tema, musicaActivada, sonidoActivado, volumenMusica, volumenSonido);
    }


    /**
     * Carga la configuración guardada desde el archivo de propiedades.
     * Lee las preferencias del usuario almacenadas previamente, utilizando
     * valores por defecto si el archivo no existe o hay errores de lectura.
     * 
     * @pre No hay precondiciones específicas para cargar configuración.
     * @return Mapa con las claves y valores de configuración cargados
     * @post Se devuelve la configuración disponible, usando valores por defecto
     *       para claves faltantes o en caso de errores de lectura.
     */
    public Map<String, String>  cargarConfiguracion() {
        return ctrlDomain.cargarConfiguracion();
    }


    /**
     * Establece el diccionario por defecto para nuevas partidas.
     * Configura qué diccionario se utilizará automáticamente cuando se
     * creen nuevas partidas sin especificar diccionario explícitamente.
     * 
     * @pre dicc no debe ser null y debe corresponder a un diccionario existente.
     * @param dicc Nombre del diccionario a establecer como por defecto
     * @post El diccionario especificado queda configurado como por defecto
     *       para futuras partidas nuevas.
     */
    public void setDiccionarioDefault(String dicc) {
        ctrlDomain.establecerDiccionario(dicc);
    }


    /**
     * Establece el tamaño de tablero por defecto para nuevas partidas.
     * Configura las dimensiones que tendrá automáticamente el tablero cuando
     * se creen nuevas partidas sin especificar tamaño explícitamente.
     * 
     * @pre tamano debe ser un valor positivo válido para tableros de Scrabble.
     * @param tamano Tamaño del tablero a establecer como por defecto
     * @post El tamaño especificado queda configurado como por defecto
     *       para futuras partidas nuevas.
     */
    public void setTamanoDefault(int tamano) {
        ctrlDomain.establecerTamano(tamano);
    }


    /**
     * Obtiene el nombre del diccionario configurado por defecto.
     * Consulta qué diccionario está establecido como por defecto para
     * la creación de nuevas partidas cuando no se especifica otro.
     * 
     * @pre Debe haber un diccionario por defecto configurado en el sistema.
     * @return String con el nombre del diccionario por defecto
     * @post Se devuelve el diccionario por defecto sin modificar la configuración.
     */
    public String getDiccionarioDefault() {
        return ctrlDomain.obtenerDiccionario();
    }

    /**
     * Obtiene el tamaño de tablero configurado por defecto.
     * Consulta las dimensiones establecidas como por defecto para
     * la creación de nuevas partidas cuando no se especifica otro tamaño.
     * 
     * @pre Debe haber un tamaño por defecto configurado en el sistema.
     * @return int con el tamaño de tablero por defecto
     * @post Se devuelve el tamaño por defecto sin modificar la configuración.
     */
    public int getTamanoDefault() {
        return ctrlDomain.obtenerTamano();
    }

     /**
     * Establece el tema visual de la aplicación.
     * 
     * @pre El tema debe ser una opción válida soportada por la aplicación.
     * @param tema El tema a establecer
     * @post El tema de la configuración se actualiza y se persiste.
     * @throws ExceptionPersistenciaFallida si ocurre un error durante la persistencia
     * @throws NullPointerException si el tema es null
     */
    public void setTema(String tema) {
        try {
            ctrlDomain.setTema(tema);
        } catch (ExceptionPersistenciaFallida e) {
            mostrarAlerta("error", "Error al guardar tema", e.getMessage());
        }
    }


    /**
     * Obtiene el tema visual actualmente configurado en la aplicación.
     * Consulta qué tema está activo actualmente para la interfaz de usuario.
     * 
     * @pre Debe haber un tema configurado en el sistema.
     * @return String con el nombre del tema actual ("Claro" o "Oscuro")
     * @post Se devuelve el tema actual sin modificar la configuración visual.
     */
    public String getTema() {
        return ctrlDomain.obtenerTema();
    }   


    /**
     * Inicializa los diccionarios predefinidos disponibles en la aplicación.
     * Carga y registra los diccionarios base necesarios para el funcionamiento
     * del juego, asegurando que estén disponibles para crear partidas.
     * 
     * @pre Los archivos de diccionarios predefinidos deben estar accesibles.
     * @post Los diccionarios predefinidos quedan cargados y disponibles
     *       para su uso en partidas del juego.
     */
    public void inicializarDiccionarios() {
        ctrlDomain.inicializarDiccionarios();
    }


    /**
     * Muestra alertas y notificaciones centralizadas en toda la aplicación.
     * Proporciona un sistema unificado para mostrar mensajes al usuario
     * con diferentes tipos de severidad y formato consistente.
     * 
     * @pre tipo, titulo y mensaje no deben ser null.
     * @param tipo Tipo de alerta ("info", "warning", "error", "success")
     * @param titulo Título de la ventana de alerta
     * @param mensaje Contenido del mensaje a mostrar al usuario
     * @post Se muestra la alerta correspondiente al tipo especificado con
     *       el título y mensaje proporcionados, usando formato apropiado.
     */
    public void mostrarAlerta(String tipo, String titulo, String mensaje) {
        switch (tipo.toLowerCase()) {
            case "info":
                customDialogo.showInfo(titulo, mensaje);
                break;
            case "warning":
                customDialogo.showWarning(titulo, mensaje);
                break;
            case "error":
                customDialogo.showError(titulo, mensaje);
                break;
            case "success":
                customDialogo.showSuccess(titulo, mensaje);
                break;
            default:
                customDialogo.showInfo(titulo, mensaje);
        }
    }    
}