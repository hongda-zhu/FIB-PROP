package scrabble.domain.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import scrabble.domain.controllers.subcontrollers.ControladorConfiguracion;
import scrabble.domain.controllers.subcontrollers.ControladorDiccionario;
import scrabble.domain.controllers.subcontrollers.ControladorJuego;
import scrabble.domain.controllers.subcontrollers.ControladorJugador;
import scrabble.domain.controllers.subcontrollers.ControladorRanking;
import scrabble.excepciones.ExceptionDiccionarioExist;
import scrabble.excepciones.ExceptionDiccionarioNotExist;
import scrabble.excepciones.ExceptionDiccionarioOperacionFallida;
import scrabble.excepciones.ExceptionLoggingOperacion;
import scrabble.excepciones.ExceptionPalabraExist;
import scrabble.excepciones.ExceptionPalabraInvalida;
import scrabble.excepciones.ExceptionPalabraNotExist;
import scrabble.excepciones.ExceptionPalabraVacia;
import scrabble.excepciones.ExceptionPersistenciaFallida;
import scrabble.excepciones.ExceptionRankingOperationFailed;
import scrabble.excepciones.ExceptionUserEsIA;
import scrabble.excepciones.ExceptionUserExist;
import scrabble.excepciones.ExceptionUserInGame;
import scrabble.excepciones.ExceptionUserNotExist;
import scrabble.helpers.Dificultad;
import scrabble.helpers.Direction;
import scrabble.helpers.Triple;
import scrabble.helpers.Tuple;

/**
 * Controlador principal del dominio que actúa como fachada para los subcontroladores.
 * Coordina la interacción entre los diferentes componentes del sistema de Scrabble.
 * Implementa el patrón Singleton para garantizar una única instancia del controlador.
 * 
 * Este controlador centraliza el acceso a todas las funcionalidades del sistema,
 * incluyendo gestión de usuarios, partidas, diccionarios, configuración y ranking.
 * Actúa como punto de entrada único para la capa de presentación, proporcionando
 * una interfaz unificada y simplificada para todas las operaciones del dominio.
 * Coordina las interacciones entre los diferentes subcontroladores y maneja
 * las excepciones específicas del dominio.
 * 
 * 
 * @version 2.0
 * @since 1.0
 */
public class ControladorDomain {
    private ControladorConfiguracion controladorConfiguracion;
    private ControladorJuego controladorJuego;
    private ControladorRanking controladorRanking;
    private ControladorJugador controladorJugador;
    private ControladorDiccionario controladorDiccionario;
    
    private static ControladorDomain instance;

    /**
     * Constructor del controlador del dominio.
     * Inicializa todos los subcontroladores necesarios para el funcionamiento del sistema.
     * Crea las instancias de todos los controladores especializados y establece las conexiones necesarias.
     * 
     * @pre No hay precondiciones específicas.
     * @post Se inicializan todos los subcontroladores y el sistema queda listo para su uso.
     *       En caso de error durante la inicialización, se imprime el error en la consola.
     */
    public ControladorDomain() {
        try {
            // ControladorConfiguracion ahora maneja su propio repositorio internamente
            this.controladorConfiguracion = new ControladorConfiguracion();
            this.controladorJuego = new ControladorJuego();
            this.controladorRanking = ControladorRanking.getInstance();
            this.controladorJugador = ControladorJugador.getInstance();
            this.controladorDiccionario = ControladorDiccionario.getInstance();
        } catch (Exception e) {
            System.err.println("Error al inicializar el controlador de dominio: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Obtiene la instancia única del controlador de dominio (patrón Singleton).
     * 
     * @pre No hay precondiciones específicas.
     * @return La instancia única del ControladorDomain
     * @post Si no existe una instancia previa, se crea una nueva. En caso contrario, se devuelve la existente.
     */
    public static ControladorDomain getInstance() {
        if (instance == null) {
            try {
                instance = new ControladorDomain();
            } catch (Exception e) {
                System.err.println("Error initializing ControladorDomain: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return instance;
    }

    // METODOS DE USUARIOS
    /**
     * Verifica si un jugador (humano) está listo para jugar.
     * 
     * @pre El nombre no debe ser null.
     * @param nombre nombre del jugador a verificar
     * @return true si está listo para jugar
     * @post Se verifica que el jugador existe, no es IA y no está en partida.
     * @throws ExceptionUserNotExist si el usuario no existe
     * @throws ExceptionUserEsIA si el usuario es una IA
     * @throws ExceptionUserInGame si el usuario ya está en partida
     * @throws NullPointerException si el nombre es null
     */
    public boolean playerReadyToPlay(String nombre) {
        if (!controladorJugador.existeJugador(nombre)) {
            throw new ExceptionUserNotExist();
        }

        if (controladorJugador.esIA(nombre)) {
            throw new ExceptionUserEsIA();
        }

        if (controladorJugador.isEnPartida(nombre)) {
            throw new ExceptionUserInGame();
        }    
        
        return true;
    }

    /**
     * Registra un nuevo usuario humano en el sistema.
     * 
     * @pre El nombre no debe ser null y debe ser único (excepto para "admin").
     * @param nombre nombre de usuario a registrar
     * @return true si se registra exitosamente
     * @post Si el nombre es válido y no existe, se crea un nuevo usuario humano en el sistema.
     * @throws ExceptionUserExist si ya existe un usuario con ese nombre
     * @throws NullPointerException si el nombre es null
     */
    public boolean registrarUsuario(String nombre) {
        if (controladorJugador.existeJugador(nombre) && !nombre.equals("admin")) {
            throw new ExceptionUserExist();
        }

        return controladorJugador.registrarUsuario(nombre);
    }

    /**
     * Elimina el usuario del sistema y su puntuación en el ranking (si existe).
     * 
     * @pre El nombre no debe ser null y debe corresponder a un usuario humano existente que no esté en partida.
     * @param nombre nombre de usuario a eliminar
     * @return true si la eliminación es exitosa
     * @post Si las verificaciones son correctas, se elimina al usuario y sus datos del ranking.
     * @throws ExceptionUserNotExist si el usuario no existe
     * @throws ExceptionUserEsIA si el usuario es una IA
     * @throws ExceptionUserInGame si el usuario está actualmente en una partida
     * @throws ExceptionRankingOperationFailed si falla la operación sobre el ranking
     * @throws NullPointerException si el nombre es null
     */
    public boolean eliminarUsuario(String nombre) {
        if (!controladorJugador.existeJugador(nombre)) {
            throw new ExceptionUserNotExist();
        }

        // Verificar si el jugador es una IA
        if (controladorJugador.esIA(nombre)) {
            throw new ExceptionUserEsIA("No se puede eliminar un jugador IA");
        }

        // Verificar si el jugador está en partida
        if (controladorJugador.isEnPartida(nombre)) {
            throw new ExceptionUserInGame();
        }

        // asumo que se puede eliminar una IA
        boolean eliminacionExitosa = true;
        if (controladorRanking.perteneceRanking(nombre)) {
            eliminacionExitosa = controladorRanking.eliminarUsuario(nombre);
        }

        if (!eliminacionExitosa) {
            throw new ExceptionRankingOperationFailed();
        }

        return controladorJugador.eliminarUsuario(nombre) && eliminacionExitosa;
    }

    /**
     * Crea un jugador IA con dificultad especificada.
     * 
     * @pre La dificultad y el nombre no deben ser null.
     * @param dificultad nivel de dificultad para la IA
     * @param nombre nombre para el jugador IA
     * @post Se crea un nuevo jugador IA con el nombre y dificultad especificados.
     * @throws NullPointerException si la dificultad o el nombre son null
     */
    public void crearJugadorIA(Dificultad dificultad, String nombre) {
        controladorJugador.registrarJugadorIA(nombre, dificultad);
    }

    /**
     * Devuelve la lista de nombres de jugadores IA registrados.
     * 
     * @pre No hay precondiciones específicas.
     * @return lista de nombres de jugadores IA
     * @post Se devuelve una lista (posiblemente vacía) con los nombres de todos los jugadores IA registrados.
     */
    public List<String> getJugadoresIA() {
        return controladorJugador.getJugadoresIA();
    }

    /**
     * Obtiene el nombre de un jugador.
     * 
     * @pre El nombre no debe ser null.
     * @param nombre nombre del jugador
     * @return nombre del jugador (mismo que parámetro)
     * @post Se devuelve el mismo nombre recibido como parámetro.
     * @throws NullPointerException si el nombre es null
     */
    public String getNombre(String nombre) {
        return controladorJugador.getNombre(nombre);
    }

    /**
     * Obtiene información detallada sobre todos los usuarios para depuración.
     * 
     * @pre No hay precondiciones específicas.
     * @return String con información detallada sobre todos los usuarios registrados
     * @post Se devuelve una cadena de texto con información detallada sobre todos los usuarios registrados,
     *       incluyendo estadísticas de humanos e IAs.
     */
    public String obtenerTodosUsuariosDebug() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n=== LISTADO DEBUG DE USUARIOS ===\n");
        sb.append("-----------------------------------------\n");
        
        controladorJugador.getTodosUsuariosDebug().forEach(line -> sb.append(line).append("\n"));
        
        sb.append("\nTotal registrados: ").append(controladorJugador.getJugadoresRegistrados().size()).append("\n");
        sb.append("- Humanos: ").append(controladorJugador.getJugadoresHumanos().size()).append("\n");
        sb.append("- IA: ").append(controladorJugador.getJugadoresIA().size()).append("\n");
        
        return sb.toString();
    }

   /**
     * Obtiene el conjunto de nombres de todos los jugadores registrados.
     * 
     * @pre No hay precondiciones específicas.
     * @return conjunto de nombres de todos los jugadores (humanos e IAs)
     * @post Se devuelve un conjunto con los nombres de todos los jugadores registrados en el sistema.
     */
    public Set<String> getAllJugadores() {
        return controladorJugador.getNombresJugadoresFromMap();
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
    public void setTema(String tema) throws ExceptionPersistenciaFallida {
        controladorConfiguracion.setTema(tema);
    }

    /**
     * Establece el nivel de volumen de la aplicación.
     * 
     * @param volumen El volumen a establecer
     * @throws ExceptionPersistenciaFallida si ocurre un error durante la persistencia
     */
    // public void setVolumen(int volumen) throws ExceptionPersistenciaFallida {
    //     controladorConfiguracion.setVolumen(volumen);
    // }


    /**
    * Añade un nuevo lenguaje al sistema a partir de los archivos en las rutas proporcionadas.
    *
    * @pre El nombre no debe ser null y las rutas deben apuntar a archivos válidos.
    * @param nombre nombre del nuevo lenguaje
    * @param rutaArchivoAlpha ruta del archivo con el alfabeto del lenguaje
    * @param rutaArchivoWords ruta del archivo con las palabras válidas del lenguaje
    * @post Si el lenguaje no existe previamente, se crea un nuevo diccionario con el nombre y archivos especificados.
    * @throws ExceptionDiccionarioExist si el lenguaje ya existe en el sistema
    * @throws IOException Si hay problemas al leer los archivos
    * @throws ExceptionPalabraInvalida Si las palabras contienen caracteres no válidos
    * @throws ExceptionLoggingOperacion si falla la operación de creación del diccionario
    */
    public void anadirLenguaje(String nombre, String rutaArchivoAlpha, String rutaArchivoWords) throws IOException, ExceptionDiccionarioExist, ExceptionPalabraInvalida {
        if (existeLenguaje(nombre)) {
            throw new ExceptionDiccionarioExist();
        }
        // Delegar al controlador de diccionarios
        try {
            controladorDiccionario.crearDiccionario(nombre, rutaArchivoAlpha, rutaArchivoWords);
        } catch (ExceptionDiccionarioOperacionFallida e) {
            throw new ExceptionLoggingOperacion("Error al añadir el lenguaje: " + e.getMessage(), "creación", true);
        }
    }

    /**
    * Verifica si existe un lenguaje en el sistema con el nombre dado.
    *
    * @pre El nombre no debe ser null.
    * @param nombre nombre del lenguaje a comprobar
    * @return true si el lenguaje ya existe; false en caso contrario
    * @post Se verifica la existencia sin modificar el estado del sistema.
    */
    public boolean existeLenguaje(String nombre) {
        return controladorDiccionario.existeDiccionario(nombre);
    }

    // MÉTODOS PARA JUGAR 
    /**
    * Realiza el turno de un jugador y devuelve el resultado del turno.
    *
    * @pre El move y nombreJugador no deben ser null.
    * @param move movimiento a realizar, conteniendo la palabra, posición y dirección
    * @param nombreJugador nombre del jugador que realiza el turno
    * @return una tupla que contiene el rack actualizado del jugador y el puntaje obtenido,
    *         o null si el movimiento es pasar turno ("P") o cambiar fichas ("CF")
    * @post Si el movimiento es válido, se actualiza el estado del juego y se devuelve el resultado.
    *       Si es pasar turno o cambiar fichas, se devuelve null.
    */    
    public Tuple<Map<String, Integer>, Integer> realizarTurno(Triple<String,Tuple<Integer, Integer>, Direction> move, String nombreJugador) {
        Map<String, Integer> rack = controladorJugador.getRack(nombreJugador);
        boolean esIA = controladorJugador.esIA(nombreJugador);
        Dificultad dificultad = getNivelDificultad(nombreJugador);
        return (move.x == "P" || move.x == "CF")? null: controladorJuego.realizarTurno(move, nombreJugador, rack, esIA, dificultad);
    }

    /**
     * Gestiona el inicio de una partida completa, incluyendo la inicialización del juego y los jugadores.
     * 
     * @pre Los parámetros no deben ser null y N debe ser un tamaño válido de tablero.
     * @param idiomaSeleccionado idioma/diccionario a utilizar en la partida
     * @param jugadoresSeleccionados mapa de jugadores participantes con sus IDs
     * @param N tamaño del tablero de la partida
     * @post Se inicia la partida y se inicializan todos los jugadores para participar.
     * @throws IOException si hay problemas con los archivos del diccionario
     * @throws ExceptionPersistenciaFallida si falla la persistencia de la partida
     */
    public void managePartidaIniciar(String idiomaSeleccionado, Map<String, Integer> jugadoresSeleccionados, Integer N) throws IOException, ExceptionPersistenciaFallida{
        
        iniciarPartida(jugadoresSeleccionados, idiomaSeleccionado, N);
        // Inicializar a los jugadores para la partida (marcando que están en partida)
        List<String> listaJugadores = new ArrayList<>(jugadoresSeleccionados.keySet());        
        inicializarJugadoresPartida(listaJugadores);
    }

    /**
     * Realiza un turno completo en la partida actual, incluyendo la gestión de fichas y verificación de fin de partida.
     * 
     * @pre El nombreJugador y jugada no deben ser null.
     * @param nombreJugador nombre del jugador que realiza el turno
     * @param jugada movimiento a realizar
     * @return puntuación obtenida en el turno, o 0 si se pasa turno
     * @post Se actualiza el estado de la partida, se gestionan las fichas del jugador y se verifica si la partida ha terminado.
     */
    public int realizarTurnoPartida (String nombreJugador, Triple<String, Tuple<Integer, Integer>, Direction> jugada) {
        
        Tuple<Map<String, Integer>, Integer> result = realizarTurno(jugada, nombreJugador);
        if (result == null) {
            addSkipTrack(nombreJugador);
            comprobarFinPartida(controladorJuego.getJugadoresActuales());
        } else {
            inicializarRack(nombreJugador, result.x);

            Map<String, Integer> nuevasFicha = cogerFichas(7 - getCantidadFichas(nombreJugador));
            
            if (nuevasFicha == null) {
                controladorJuego.finalizarJuego();
            } else {
                for (Map.Entry<String, Integer> fichas : nuevasFicha.entrySet()) {
                    String letra = fichas.getKey();
                    int cantidad = fichas.getValue();
                    for (int i = 0; i < cantidad; i++) {
                        agregarFicha(nombreJugador, letra);
                    }   
                }
            }            
                controladorJugador.clearSkipTrack(nombreJugador);        

            return result.y;
        }
        return 0;               
    }

    /**
     * Comprueba si la partida ha terminado por turnos pasados consecutivos.
     * 
     * @pre El mapa de jugadores no debe ser null.
     * @param jugadoresSeleccionados mapa de jugadores en la partida
     * @post Si todos los jugadores han pasado 2 turnos consecutivos, se finaliza el juego.
     */
    public void comprobarFinPartida(Map<String, Integer> jugadoresSeleccionados) {
        boolean allskiped = true;
        
        for (String entry : jugadoresSeleccionados.keySet()) {
            String nombreJugador = entry;
            if (getSkipTrack(nombreJugador) < 2) {
                allskiped = false;
                break;
            }
        }
        if (allskiped) {
            controladorJuego.finalizarJuego();
        }
    }

    /**
     * Obtiene el número de turnos consecutivos que un jugador ha pasado.
     * 
     * @pre El nombre no debe ser null.
     * @param nombre nombre del jugador
     * @return número de turnos pasados consecutivamente
     * @post Se devuelve el contador de turnos pasados sin modificar el estado.
     */
    public int getSkipTrack(String nombre) {
        return controladorJugador.getSkipTrack(nombre);
    }

    /**
     * Incrementa el contador de turnos pasados por el jugador.
     * 
     * @pre El nombre no debe ser null.
     * @param nombre nombre del jugador
     * @post Se incrementa en 1 el contador de turnos pasados del jugador.
     */
    public void addSkipTrack(String nombre) {
        controladorJugador.addSkipTrack(nombre);
    }

    /**
    * Resetea el contador de turnos pasados de un jugador a cero.
    * 
    * @pre El nombreJugador no debe ser null.
    * @param nombreJugador nombre del jugador
    * @post El contador de turnos pasados del jugador se establece en 0.
    */
    public void clearSkipTrack(String nombreJugador) {
        controladorJugador.clearSkipTrack(nombreJugador);
    }

    /**
     * Inicializa el rack del jugador con las fichas proporcionadas.
     * 
     * @pre El nombre y rack no deben ser null.
     * @param nombre nombre del jugador
     * @param rack mapa de letras a cantidades
     * @post El rack del jugador se actualiza con las fichas especificadas.
     */
    public void inicializarRack(String nombre, Map<String,Integer> rack) {
        controladorJugador.inicializarRack(nombre, rack);
    }

    /**
     * Añade puntuación al jugador.
     * 
     * @pre El nombre no debe ser null y la puntuación debe ser no negativa.
     * @param nombre nombre del jugador
     * @param puntuacion cantidad a sumar
     * @post Se incrementa la puntuación del jugador en la cantidad especificada.
     */
    public void addPuntuacion(String nombre, int puntuacion) {
        controladorJugador.addPuntuacion(nombre, puntuacion);
    }  

    /**
     * Devuelve una cantidad específica de fichas aleatorias de la bolsa del juego.
     * 
     * @pre La cantidad debe ser no negativa.
     * @param cantidad número de fichas a coger
     * @return mapa de letras a cantidades, o null si no hay suficientes fichas
     * @post Se extraen fichas de la bolsa del juego. Si no hay suficientes, se devuelve null.
     */
    public Map<String, Integer> cogerFichas(int cantidad) {
        return controladorJuego.cogerFichas(cantidad);
    }

    /**
     * Devuelve la cantidad total de fichas actuales en el rack del jugador.
     * 
     * @pre El nombre no debe ser null.
     * @param nombre nombre del jugador
     * @return cantidad total de fichas en el rack
     * @post Se devuelve la suma de todas las fichas en el rack del jugador.
     */
    public int getCantidadFichas(String nombre) {
        return controladorJugador.getCantidadFichas(nombre);
    }

   /**
     * Agrega una ficha específica al rack del jugador.
     * 
     * @pre El nombre y letra no deben ser null.
     * @param nombre nombre del jugador
     * @param letra letra a agregar
     * @post Se incrementa en 1 la cantidad de la letra especificada en el rack del jugador.
     */
    public void agregarFicha(String nombre, String letra) {
        controladorJugador.agregarFicha(nombre, letra);
    }



    /**
     * Intercambia fichas del rack del jugador por nuevas fichas de la bolsa.
     * 
     * @pre El nombre y letras no deben ser null.
     * @param nombre nombre del jugador
     * @param letras lista de letras a intercambiar
     * @return true si el intercambio es exitoso, false si no hay suficientes fichas en la bolsa
     * @post Las fichas especificadas se devuelven a la bolsa y se toman nuevas fichas aleatorias.
     *       Si no hay suficientes fichas en la bolsa, se finaliza el juego.
     */
    public boolean intercambiarFichas (String nombre, List<String> letras) {
        Map<String, Integer> rack = controladorJugador.getRack(nombre);

        for (String letra : letras) {
            if (rack.containsKey(letra)) {
                int cantidad = rack.get(letra);
                if (cantidad > 1) {
                    rack.put(letra, cantidad - 1);
                } else {
                    rack.remove(letra);
                }
            }
        }

        Map<String, Integer> fichasDevueltas = new HashMap<>();

        for (String letra : letras) {
            if (fichasDevueltas.containsKey(letra)) {
                fichasDevueltas.put(letra, fichasDevueltas.get(letra) + 1);
            } else {
                fichasDevueltas.put(letra, 1);
            }
        }

        Map<String, Integer> fichasBolsa = controladorJuego.cogerFichas(letras.size());

        if (fichasBolsa == null) {
            controladorJuego.finalizarJuego();
            return false; // No hay suficientes fichas en la bolsa
        }

        controladorJuego.meterFichas(fichasDevueltas);

        for (Map.Entry<String, Integer> entry : fichasBolsa.entrySet()) {
            String letra = entry.getKey();
            int cantidad = entry.getValue();
            for (int i = 0; i < cantidad; i++) {
                rack.put(letra, rack.getOrDefault(letra, 0) + 1);
            }
        }
        controladorJugador.inicializarRack(nombre, rack);

        return true;
    }

    /**
     * Devuelve la puntuación actual del jugador en la partida.
     * 
     * @pre El nombre no debe ser null.
     * @param nombre nombre del jugador
     * @return puntuación actual del jugador
     * @post Se devuelve la puntuación sin modificar el estado del jugador.
     */
    public int getPuntuacion(String nombre) {
        return controladorJugador.getPuntuacion(nombre);
    }
    
    /**
    * Inicia una nueva partida de Scrabble con los parámetros proporcionados.
    * 
    * @pre Los parámetros no deben ser null y N debe ser un tamaño válido.
    * @param jugadoresSeleccionados mapa que asocia nombres de jugadores a sus puntuaciones iniciales
    * @param nombreDiccionario nombre del diccionario/lenguaje a utilizar en la partida
    * @param N tamaño del tablero; si es distinto de 15, se crea un tablero personalizado de NxN
    * @post Se inicializa el juego con el tablero y diccionario especificados, y se reparten 7 fichas a cada jugador.
    * @throws ExceptionPersistenciaFallida si falla la persistencia de la partida
    */  
    public void iniciarPartida(Map<String, Integer> jugadoresSeleccionados, String nombreDiccionario, int N) throws ExceptionPersistenciaFallida {
    
        Map<String, Integer> jugadoresPuntuaciones = new HashMap<>();
        for (String jugador : jugadoresSeleccionados.keySet()) {
            jugadoresPuntuaciones.put(jugador, 0);
        }
        controladorJuego.inicializarJuego(N, jugadoresPuntuaciones, nombreDiccionario);
        
        // Inicializar racks para todos los jugadores
        for (String jugador : jugadoresSeleccionados.keySet()) {
            Map<String, Integer> rack = controladorJuego.cogerFichas(7);
            if (rack == null) {
                controladorJuego.finalizarJuego();
            }
            inicializarRack(jugador, rack);
        }
    }

    /**
     * Devuelve la cantidad de fichas restantes en la bolsa del juego.
     * 
     * @pre Debe haber un juego iniciado.
     * @return cantidad de fichas disponibles en la bolsa
     * @post Se devuelve el número de fichas sin modificar el estado del juego.
     */
    public int getCantidadFichasRestantes() {
        return controladorJuego.getCantidadFichas();
    }

    /**
     * Finaliza el juego actual, determina ganadores y realiza tareas de limpieza.
     * 
     * @pre El mapa de jugadores no debe ser null.
     * @param jugadoresSeleccionados mapa de jugadores con sus puntuaciones finales
     * @return mensaje con los resultados de la partida y ganadores
     * @post Se determinan los ganadores, se actualizan las estadísticas, se reinicia el juego
     *       y se liberan los jugadores humanos de la partida.
     */
    public String finalizarJuego(Map<String, Integer> jugadoresSeleccionados) {
        
        for (String jugador : jugadoresSeleccionados.keySet()) {
            controladorJugador.clearSkipTrack(jugador);
        }
        
        // Determinar el ganador o ganadores (en caso de empate)
        int maxPuntuacion = -1;
        List<String> ganadores = new ArrayList<>();
        
        // Primero encontramos la puntuación máxima
        for (Map.Entry<String, Integer> entry : jugadoresSeleccionados.entrySet()) {
            if (entry.getValue() > maxPuntuacion) {
                maxPuntuacion = entry.getValue();
            }
        }
        
        // Luego identificamos todos los jugadores con esa puntuación máxima (pueden ser varios en caso de empate)
        for (Map.Entry<String, Integer> entry : jugadoresSeleccionados.entrySet()) {
            if (entry.getValue() == maxPuntuacion) {
                ganadores.add(entry.getKey());
            }
        }
        
        // Ahora actualizamos las estadísticas para todos los jugadores, marcando múltiples ganadores si es necesario
        finalizarPartidaJugadoresMultiple(jugadoresSeleccionados, ganadores);
        
        // Mensaje de resultado
        StringBuilder mensajeGanadores = new StringBuilder();
        if (ganadores.size() > 1) {
            mensajeGanadores.append("""
            +--------------------------------------+
            | ¡EMPATE!                             |
            +--------------------------------------+
            """);
            mensajeGanadores.append(String.format("Los jugadores empatados son: %s con una puntuación de: %d puntos.%n",
            String.join(", ", ganadores), maxPuntuacion));
        } else if (ganadores.size() == 1) {
            mensajeGanadores.append("""
            +--------------------------------------+
            | ¡FELICIDADES!                        |
            +--------------------------------------+
            """);
            mensajeGanadores.append(String.format("El ganador es: %s con una puntuación de: %d puntos.%n",
            ganadores.get(0), maxPuntuacion));
        } else {
            mensajeGanadores.append("""
            +--------------------------------------+
            | SIN GANADORES                        |
            +--------------------------------------+
            """);
            mensajeGanadores.append("No hubo ganadores en esta partida.\n");
        }

        // Mostrar resultados finales
        mensajeGanadores.append("+--------------------------------------+\n");
        mensajeGanadores.append("  RESULTADOS FINALES                    \n");
        for (Map.Entry<String, Integer> entry : jugadoresSeleccionados.entrySet()) {
            mensajeGanadores.append(String.format("  %-20s : %4d puntos\n", entry.getKey(), entry.getValue()));
        }
        mensajeGanadores.append("+--------------------------------------+\n");

        
        controladorJuego.reiniciarJuego();
        
        for (String nombreJugador : jugadoresSeleccionados.keySet()) {
            controladorJugador.clearSkipTrack(nombreJugador);
            
            // Actualizar el estado de los jugadores humanos (desvincularlos de la partida)
            if (!controladorJugador.esIA(nombreJugador)) {
                controladorJugador.setEnPartida(nombreJugador, false);
                controladorJugador.setNombrePartidaActual(nombreJugador, "");
            }
        }
        
        return mensajeGanadores.toString();
    }

    /**
     * Verifica si el juego actual ha terminado.
     * 
     * @pre No hay precondiciones específicas.
     * @return true si el juego ha finalizado, false en caso contrario
     * @post Se devuelve el estado de finalización sin modificar el juego.
     */
    public boolean isJuegoTerminado() {
        return controladorJuego.isJuegoTerminado();
    }

    /**
     * Obtiene el tamaño del tablero de la partida actual.
     * 
     * @pre Debe haber un juego iniciado.
     * @return El tamaño del tablero (NxN)
     * @post Se devuelve el tamaño sin modificar el estado del juego.
     */
    public int getSize() {
        return controladorJuego.getSize();
    }


    // METODOS DE RANKING

    /**
     * Obtiene la lista de usuarios ordenados según la estrategia actual de ranking.
     * 
     * @pre No hay precondiciones específicas.
     * @return Lista de usuarios ordenados según el criterio actual
     * @post Se devuelve la lista ordenada sin modificar el estado del ranking.
     */
    public List<String> getRanking() {
        return controladorRanking.getRanking();
    }

    /**
     * Obtiene la lista de usuarios ordenados según un criterio específico.
     * 
     * @pre El criterio debe ser válido ("maxima", "media", "partidas", "victorias").
     * @param criterio Criterio de ordenación
     * @return Lista de usuarios ordenados según el criterio especificado
     * @post Se devuelve la lista ordenada sin modificar la estrategia actual del ranking.
     */
    public List<String> getRanking(String criterio) {
        return controladorRanking.getRanking(criterio);
    }

    /**
     * Obtiene el nombre de la estrategia actual de ordenación.
     * 
     * @pre No hay precondiciones específicas.
     * @return Nombre descriptivo de la estrategia actual
     * @post Se devuelve el nombre sin modificar el estado del ranking.
     */
    public String getEstrategiaRanking() {
        return controladorRanking.getEstrategiaActual();
    }

    /**
     * Obtiene el identificador del criterio actual de ordenación.
     * 
     * @pre No hay precondiciones específicas.
     * @return Criterio actual ("maxima", "media", "partidas", "victorias")
     * @post Se convierte el nombre de la estrategia a su identificador correspondiente.
     */
    public String getEstrategiaActual() {
        // Convertir el nombre de la estrategia a su identificador
        String nombreEstrategia = getEstrategiaRanking();
        
        if (nombreEstrategia.contains("Máxima")) return "maxima";
        if (nombreEstrategia.contains("Media")) return "media";
        if (nombreEstrategia.contains("Partidas")) return "partidas";
        if (nombreEstrategia.contains("Victorias")) return "victorias";
        
        return "maxima"; // Por defecto
    }

    /**
     * Cambia la estrategia de ordenación del ranking.
     * 
     * @pre El criterio debe ser válido ("maxima", "media", "partidas", "victorias").
     * @param criterio Criterio de ordenación a establecer
     * @post La estrategia de ranking se actualiza al criterio especificado.
     */
    public void cambiarEstrategiaRanking(String criterio) {
        controladorRanking.setEstrategia(criterio);
    }

    /**
     * Obtiene la puntuación máxima de un usuario específico.
     * 
     * @pre El nombre no debe ser null.
     * @param nombre Nombre del usuario
     * @return Puntuación máxima obtenida por el usuario
     * @post Se devuelve la puntuación máxima sin modificar el estado del ranking.
     */
    public int getPuntuacionMaxima(String nombre) {
        return controladorRanking.getPuntuacionMaxima(nombre);
    }

    /**
     * Obtiene la puntuación media de un usuario específico.
     * 
     * @pre El nombre no debe ser null.
     * @param nombre Nombre del usuario
     * @return Puntuación media del usuario
     * @post Se devuelve la puntuación media calculada sin modificar el estado del ranking.
     */
    public double getPuntuacionMedia(String nombre) {
        return controladorRanking.getPuntuacionMedia(nombre);
    }

    /**
     * Obtiene el número de partidas jugadas por un usuario específico.
     * 
     * @pre El nombre no debe ser null.
     * @param nombre Nombre del usuario
     * @return Número de partidas jugadas
     * @post Se devuelve el contador de partidas sin modificar el estado del ranking.
     */
    public int getPartidasJugadas(String nombre) {
        return controladorRanking.getPartidasJugadas(nombre);
    }

    /**
     * Obtiene el número de victorias de un usuario específico.
     * 
     * @pre El nombre no debe ser null.
     * @param nombre Nombre del usuario
     * @return Número de victorias del usuario
     * @post Se devuelve el contador de victorias sin modificar el estado del ranking.
     */
    public int getVictorias(String nombre) {
        return controladorRanking.getVictorias(nombre);
    }

    /**
     * Obtiene la puntuación total acumulada de un usuario específico.
     * 
     * @pre El nombre no debe ser null.
     * @param nombre Nombre del usuario
     * @return Puntuación total acumulada
     * @post Se devuelve la suma total de puntuaciones sin modificar el estado del ranking.
     */
    public int getPuntuacionTotal(String nombre) {
        return controladorRanking.getPuntuacionTotal(nombre);
    }

    /**
     * Obtiene todas las puntuaciones individuales de un usuario específico.
     * 
     * @pre El nombre no debe ser null.
     * @param nombre Nombre del usuario
     * @return Lista de todas las puntuaciones del usuario
     * @post Se devuelve la lista de puntuaciones sin modificar el estado del ranking.
     */
    public List<Integer> getPuntuacionesUsuario(String nombre) {
        return controladorRanking.getPuntuacionesUsuario(nombre);
    }

    /**
     * Verifica si un usuario está registrado en el ranking.
     * 
     * @pre El nombre no debe ser null.
     * @param nombre Nombre del usuario
     * @return true si el usuario está en el ranking, false en caso contrario
     * @post Se verifica la pertenencia sin modificar el estado del ranking.
     */
    public boolean perteneceRanking(String nombre) {
        return controladorRanking.perteneceRanking(nombre);
    }

    /**
     * Actualiza las puntuaciones de un jugador en el sistema de juego.
     * 
     * @pre El nombre no debe ser null.
     * @param nombre nombre del jugador
     * @param puntuacion nueva puntuación a establecer
     * @post Se actualiza la puntuación del jugador en el controlador de juego.
     */
    public void actualizarJugadores(String nombre, int puntuacion) {
        controladorJuego.actualizarPuntuaciones(nombre, puntuacion);
    }


    /**
     * Obtiene la lista de todos los usuarios registrados en el ranking.
     * 
     * @pre No hay precondiciones específicas.
     * @return Lista de nombres de usuarios en el ranking
     * @post Se devuelve la lista sin modificar el estado del ranking.
     */
    public List<String> getUsuariosRanking() {
        return controladorRanking.getUsuarios();
    }

    /**
     * Obtiene la lista de todos los jugadores humanos registrados.
     * 
     * @pre No hay precondiciones específicas.
     * @return Lista de nombres de jugadores humanos
     * @post Se devuelve la lista sin modificar el estado del sistema.
     */
    public List<String> getUsuariosHumanos() {
        return controladorJugador.getJugadoresHumanos();
    }

    /**
     * Agrega una puntuación a un usuario en el ranking.
     * 
     * @pre El nombre no debe ser null y la puntuación debe ser no negativa.
     * @param nombre Nombre del usuario
     * @param puntuacion Puntuación a agregar
     * @return true si se agregó correctamente, false en caso contrario
     * @post Se añade la puntuación al historial del usuario y se actualizan sus estadísticas.
     */
    public boolean agregarPuntuacion(String nombre, int puntuacion) {
        return controladorRanking.agregarPuntuacion(nombre, puntuacion);
    }

    /**
     * Elimina una puntuación específica de un usuario.
     * 
     * @pre El nombre no debe ser null.
     * @param nombre Nombre del usuario
     * @param puntuacion Puntuación a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     * @post Se elimina la puntuación del historial del usuario y se recalculan sus estadísticas.
     */
    public boolean eliminarPuntuacion(String nombre, int puntuacion) {
        return controladorRanking.eliminarPuntuacion(nombre, puntuacion);
    }

    /**
     * Actualiza las estadísticas de victoria de un usuario.
     * 
     * @pre El nombre no debe ser null.
     * @param nombre Nombre del usuario
     * @param esVictoria true si el usuario ganó la partida, false en caso contrario
     * @return true si se actualizó correctamente, false en caso contrario
     * @post Se incrementan los contadores de partidas jugadas y victorias (si corresponde).
     */
    public boolean actualizarEstadisticasUsuario(String nombre, boolean esVictoria) {
        return controladorRanking.actualizarEstadisticasUsuario(nombre, esVictoria);
    }

    /**
     * Elimina a un usuario del ranking y resetea su puntuación total.
     * 
     * @pre El nombre no debe ser null.
     * @param nombre Nombre del usuario
     * @return true si se eliminó correctamente, false en caso contrario
     * @post El usuario se elimina del ranking y su puntuación total se establece en cero.
     */
    public boolean eliminarUsuarioRanking(String nombre) {
        // Primero, capturar la información antes de eliminar
        boolean eliminado = controladorRanking.eliminarUsuario(nombre);
        return eliminado;
    }

    // Metodos de Jugadores

    /**
     * Verifica si un jugador existe en el sistema.
     * 
     * @pre El nombre no debe ser null.
     * @param nombre Nombre del jugador
     * @return true si el jugador existe, false en caso contrario
     * @post Se verifica la existencia sin modificar el estado del sistema.
     */
    public boolean existeJugador(String nombre) {
        return controladorJugador.existeJugador(nombre);
    }
    
    /**
     * Verifica si un jugador es una IA.
     * 
     * @pre El nombre no debe ser null.
     * @param nombre Nombre del jugador
     * @return true si el jugador es una IA, false si es un jugador humano
     * @post Se verifica el tipo de jugador sin modificar el estado del sistema.
     */
    public boolean esIA(String nombre) {
        return controladorJugador.esIA(nombre);
    }
    
    /**
     * Obtiene el nivel de dificultad de un jugador IA.
     * 
     * @pre El nombre no debe ser null.
     * @param nombre Nombre del jugador
     * @return Dificultad del jugador IA o null si no es una IA
     * @post Se devuelve la dificultad sin modificar el estado del jugador.
     */
    public Dificultad getNivelDificultad(String nombre) {
        return controladorJugador.getNivelDificultad(nombre);
    }
    
    /**
     * Verifica si un jugador humano está participando en una partida.
     * 
     * @pre El nombre no debe ser null.
     * @param nombre Nombre del jugador
     * @return true si está en partida, false en caso contrario
     * @post Se verifica el estado sin modificar la información del jugador.
     */
    public boolean isEnPartida(String nombre) {
        return controladorJugador.isEnPartida(nombre);
    }

    /**
     * Inicializa a los jugadores al comenzar una partida.
     * Marca a los jugadores humanos como "en partida" y establece el nombre de la partida.
     *
     * @pre La lista de jugadores no debe ser null.
     * @param jugadores Lista de nombres de jugadores en la partida
     * @post Los jugadores humanos se marcan como en partida y se les asigna el ID de la partida actual.
     */
    public void inicializarJugadoresPartida(List<String> jugadores) {
        ControladorJugador controladorJugador = ControladorJugador.getInstance();
        String nombrePartida = String.valueOf(controladorJuego.getIdPartida());
        for (String nombre : jugadores) {
            // Solo aplicamos esto a jugadores humanos
            if (!controladorJugador.esIA(nombre)) {
                controladorJugador.setEnPartida(nombre, true);
                controladorJugador.setNombrePartidaActual(nombre, nombrePartida);
            }
        }
    }

    /**
     * Actualiza estadísticas de jugadores al finalizar una partida con posibilidad de múltiples ganadores.
     * Actualiza la puntuación de la partida actual y marca a los jugadores como "fuera de partida".
     *
     * @pre Los mapas no deben ser null.
     * @param puntuacionesFinales Mapa con nombres de jugadores y sus puntuaciones finales
     * @param ganadores Lista de nombres de los jugadores ganadores (para empates)
     * @post Se actualizan las estadísticas de todos los jugadores humanos y se liberan de la partida.
     */
    public void finalizarPartidaJugadoresMultiple(Map<String, Integer> puntuacionesFinales, List<String> ganadores) {
        ControladorJugador controladorJugador = ControladorJugador.getInstance();
        
        for (Map.Entry<String, Integer> entry : puntuacionesFinales.entrySet()) {
            String nombre = entry.getKey();
            int puntuacion = entry.getValue();
            
            // Solo procesamos jugadores humanos
            if (!controladorJugador.esIA(nombre)) {
                // Actualizar estado del jugador
                controladorJugador.setEnPartida(nombre, false);
                
                // Limpiar el nombre de la partida actual (aunque setEnPartida debería hacerlo,
                // lo hacemos explícitamente para mayor claridad)
                controladorJugador.setNombrePartidaActual(nombre, "");
                
                // Actualizar estadísticas en el ranking
                boolean esGanador = ganadores.contains(nombre);
                controladorRanking.actualizarEstadisticasUsuario(nombre, esGanador);
                
                // Agregar puntuación al ranking SIN incrementar el contador de partidas
                // ya que ya lo hicimos en actualizarEstadisticasUsuario
                controladorRanking.agregarPuntuacionSinIncrementarPartidas(nombre, puntuacion);
            }
        }
    }

    /**
     * Obtiene la puntuación total acumulada de un jugador humano directamente del ranking.
     * 
     * @pre El nombre no debe ser null.
     * @param nombre Nombre del jugador
     * @return La puntuación total acumulada, o 0 si el jugador no existe o es IA
     * @post Se devuelve la puntuación total sin modificar el estado del ranking.
     */
    public int getPuntuacionTotalDirecta(String nombre) {
        // Obtener puntuación directamente del ranking
        return controladorRanking.getPuntuacionTotal(nombre);
    }
    
    /**
     * Establece la puntuación total de un jugador, eliminando todas sus puntuaciones anteriores.
     * 
     * @pre El nombreUsuario no debe ser null y debe existir en el sistema.
     * @param nombreUsuario Nombre del jugador
     * @param puntuacionTotal Nueva puntuación total a establecer
     * @return true si se estableció correctamente, false en caso contrario
     * @post Si el usuario existe, se eliminan todas sus puntuaciones anteriores del ranking,
     *       se establece la nueva puntuación total y se guardan los datos.
     */
    public boolean setPuntuacionTotal(String nombreUsuario, int puntuacionTotal) {        
        // Verificar que el usuario exista
        if (!controladorJugador.existeJugador(nombreUsuario)) {
            return false;
        }
        
        // Utilizar el método del ControladorRanking para establecer la puntuación total
        return controladorRanking.setPuntuacionTotal(nombreUsuario, puntuacionTotal);
    }
    
    /**
     * Añade puntos a la puntuación total acumulada de un jugador humano.
     * 
     * @pre El nombre no debe ser null y debe existir en el sistema.
     * @param nombre Nombre del jugador
     * @param puntos Los puntos a añadir
     * @return true si se añadieron correctamente, false en caso contrario
     * @post Se incrementa la puntuación total del jugador en la cantidad especificada.
     */
    public boolean addPuntuacionTotal(String nombre, int puntos) {
        // Verificar que el usuario exista
        if (!controladorJugador.existeJugador(nombre)) {
            return false;
        }
        
        // Utilizar el método del ControladorRanking para incrementar la puntuación total
        return controladorRanking.addPuntuacionTotal(nombre, puntos);
    }

    // --- MÉTODOS DE DICCIONARIO ---
    
    /**
     * Delega la creación de un diccionario al ControladorDiccionario.
     * 
     * @pre El nombre y path no deben ser null.
     * @param nombre Nombre del diccionario
     * @param path Ruta al directorio del diccionario
     * @post Se crea un nuevo diccionario con los archivos del directorio especificado.
     * @throws ExceptionDiccionarioExist Si ya existe un diccionario con ese nombre
     * @throws IOException Si hay problemas con los archivos
     * @throws ExceptionPalabraInvalida Si las palabras contienen caracteres no válidos
     * @throws ExceptionLoggingOperacion Si falla la operación de creación
     */
    public void crearDiccionario(String nombre, String path) throws ExceptionDiccionarioExist, IOException, ExceptionPalabraInvalida {
        try {
            controladorDiccionario.crearDiccionario(nombre, path);
        } catch (ExceptionDiccionarioOperacionFallida e) {
            throw new ExceptionLoggingOperacion("Error al crear el diccionario: " + e.getMessage(), "creación", true);
        }
    }
    
    /**
     * Delega la eliminación de un diccionario al ControladorDiccionario.
     * 
     * @pre El nombre no debe ser null.
     * @param nombre Nombre del diccionario a eliminar
     * @post El diccionario y sus archivos asociados se eliminan del sistema.
     * @throws ExceptionDiccionarioNotExist Si el diccionario no existe
     * @throws IOException Si hay problemas eliminando los archivos
     * @throws ExceptionLoggingOperacion Si falla la operación de eliminación
     */
    public void eliminarDiccionario(String nombre) throws ExceptionDiccionarioNotExist, IOException {
        try {
            controladorDiccionario.eliminarDiccionario(nombre);
        } catch (ExceptionDiccionarioOperacionFallida e) {
            throw new ExceptionLoggingOperacion("Error al eliminar el diccionario: " + e.getMessage(), "eliminación", true);
        }
    }
    
    /**
     * Verifica si existe un diccionario con el nombre especificado.
     * 
     * @pre El nombre no debe ser null.
     * @param nombre Nombre del diccionario
     * @return true si existe, false en caso contrario
     * @post Se verifica la existencia sin modificar el estado del sistema.
     */
    public boolean existeDiccionario(String nombre) {
        return controladorDiccionario.existeDiccionario(nombre);
    }
    
    /**
     * Obtiene la lista de nombres de diccionarios disponibles.
     * 
     * @pre No hay precondiciones específicas.
     * @return Lista de nombres de diccionarios
     * @post Se devuelve la lista sin modificar el estado del sistema.
     */
    public List<String> getDiccionariosDisponibles() {
        return controladorDiccionario.getDiccionariosDisponibles();
    }

    /**
     * Obtiene el alfabeto de un diccionario con los valores de puntuación de cada carácter.
     * 
     * @pre El nombre no debe ser null.
     * @param nombre Nombre del diccionario
     * @return Mapa de caracteres a sus valores de puntuación
     * @post Se devuelve el alfabeto sin modificar el estado del diccionario.
     * @throws ExceptionDiccionarioNotExist Si el diccionario no existe
     * @throws IOException Si hay problemas leyendo el archivo del alfabeto
     */
    public Map<String, Integer> getAlphabet(String nombre) throws ExceptionDiccionarioNotExist, IOException {
        return controladorDiccionario.getAlphabet(nombre);
    }

    /**
     * Devuelve una lista con todas las palabras del diccionario.
     * 
     * @pre El nombre del diccionario no debe ser null.
     * @param dic Nombre de diccionario
     * @return Lista de palabras del diccionario
     * @post Se devuelve la lista de palabras sin modificar el estado del diccionario.
     */    
    public List<String> getListaPalabras(String dic) {
        return controladorDiccionario.getListaPalabras(dic);
    }

    /**
     * Devuelve una lista de letras con su puntuación y frecuencia en formato: "letra puntuacion frecuencia".
     * 
     * @pre El nombre del diccionario no debe ser null.
     * @param dic Nombre de diccionario
     * @return Lista de letras con formato "letra puntuacion frecuencia"
     * @post Se devuelve la lista del alfabeto sin modificar el estado del diccionario.
     */
    public List<String> getListaAlfabeto(String dic) {
        return controladorDiccionario.getListaAlfabeto(dic);
    }
    
    /**
     * Modifica un diccionario añadiendo o eliminando una palabra.
     * Valida que la palabra pueda formarse usando únicamente los tokens existentes en el alfabeto.
     * 
     * @pre El nombre y palabra no deben ser null.
     * @param nombre Nombre del diccionario
     * @param palabra Palabra a modificar
     * @param anadir true para añadir, false para eliminar
     * @post La palabra se añade o elimina del diccionario según el parámetro anadir.
     * @throws ExceptionDiccionarioNotExist Si el diccionario no existe
     * @throws ExceptionPalabraVacia Si la palabra está vacía
     * @throws ExceptionPalabraInvalida Si la palabra no puede formarse con los tokens del alfabeto
     * @throws ExceptionPalabraExist Si al añadir, la palabra ya existe
     * @throws ExceptionPalabraNotExist Si al eliminar, la palabra no existe
     * @throws IOException Si hay problemas con los archivos
     */
    public void modificarPalabraDiccionario(String nombre, String palabra, boolean anadir) 
            throws ExceptionDiccionarioNotExist, ExceptionPalabraVacia, ExceptionPalabraInvalida, 
                   ExceptionPalabraExist, ExceptionPalabraNotExist, IOException {
        controladorDiccionario.modificarPalabraDiccionario(nombre, palabra, anadir);
    }

    /**
     * Modifica una palabra existente en un diccionario reemplazándola por una nueva.
     * Valida que la nueva palabra pueda formarse usando únicamente los tokens existentes en el alfabeto.
     * 
     * @pre Los nombres de palabras no deben ser null.
     * @param nombre Nombre del diccionario
     * @param palabraOriginal Palabra original a modificar
     * @param palabraNueva Nueva palabra que reemplazará a la original
     * @post La palabra original se reemplaza por la nueva en el diccionario.
     * @throws ExceptionDiccionarioNotExist Si el diccionario no existe
     * @throws ExceptionPalabraVacia Si alguna palabra está vacía
     * @throws ExceptionPalabraInvalida Si la nueva palabra no puede formarse con los tokens del alfabeto
     * @throws ExceptionPalabraNotExist Si la palabra original no existe
     * @throws ExceptionPalabraExist Si la nueva palabra ya existe
     * @throws IOException Si hay problemas con los archivos
     */
    public void modificarPalabraEnDiccionario(String nombre, String palabraOriginal, String palabraNueva) 
            throws ExceptionDiccionarioNotExist, ExceptionPalabraVacia, ExceptionPalabraInvalida, 
                   ExceptionPalabraNotExist, ExceptionPalabraExist, IOException {
        controladorDiccionario.modificarPalabra(nombre, palabraOriginal, palabraNueva);
    }

    /**
     * Obtiene el conjunto de caracteres válidos del alfabeto de un diccionario.
     * 
     * @pre El nombreDiccionario no debe ser null.
     * @param nombreDiccionario Nombre del diccionario
     * @return Conjunto de caracteres válidos
     * @post Se devuelve el conjunto de caracteres sin modificar el estado del diccionario.
     * @throws ExceptionLoggingOperacion Si el diccionario no existe o hay problemas de E/S
     */
    public Set<Character> getCaracteresAlfabeto(String nombreDiccionario) {
        try {
            return controladorDiccionario.getCaracteresAlfabeto(nombreDiccionario);
        } catch (ExceptionDiccionarioNotExist | IOException e) {
            throw new ExceptionLoggingOperacion("Error al obtener caracteres del alfabeto: " + e.getMessage(), "consulta", true);
        }
    }

    /**
     * Obtiene el conjunto de tokens (letras, incluyendo multicarácter como CH, RR) del alfabeto.
     * 
     * @pre El nombreDiccionario no debe ser null.
     * @param nombreDiccionario Nombre del diccionario
     * @return Conjunto de tokens del alfabeto (ejemplo: A, B, CH, RR)
     * @post Se devuelve el conjunto de tokens sin modificar el estado del diccionario.
     * @throws ExceptionLoggingOperacion Si el diccionario no existe
     */
    public Set<String> getTokensAlfabeto(String nombreDiccionario) {
        try {
            return controladorDiccionario.getTokensAlfabeto(nombreDiccionario);
        } catch (ExceptionDiccionarioNotExist e) {
            throw new ExceptionLoggingOperacion("Error al obtener tokens del alfabeto: " + e.getMessage(), "consulta", true);
        }
    }

    /**
     * Verifica si una palabra existe en el diccionario.
     * 
     * @pre Los parámetros no deben ser null.
     * @param nombreDiccionario Nombre del diccionario
     * @param palabra Palabra a verificar
     * @return true si la palabra existe en el diccionario, false en caso contrario
     * @post Se verifica la existencia sin modificar el estado del diccionario.
     * @throws ExceptionLoggingOperacion Si hay errores durante la verificación
     */
    public boolean existePalabra(String nombreDiccionario, String palabra) {
        try {
            return controladorDiccionario.existePalabra(nombreDiccionario, palabra);
        } catch (Exception e) {
            throw new ExceptionLoggingOperacion("Error al verificar palabra: " + e.getMessage(), "consulta", true);
        }
    }
    
    /**
     * Verifica si un diccionario configurado sigue siendo válido (sus archivos existen).
     * 
     * @pre El nombreDiccionario no debe ser null.
     * @param nombreDiccionario Nombre del diccionario a verificar
     * @return true si el diccionario es válido, false si falta algún archivo necesario
     * @post Se verifica la validez sin modificar el estado del diccionario.
     */
    public boolean verificarDiccionarioValido(String nombreDiccionario) {
        if (!existeDiccionario(nombreDiccionario)) {
            return false;
        }
        return controladorDiccionario.verificarDiccionarioValido(nombreDiccionario);
    }
    
    /**
     * Verifica si un directorio contiene un diccionario válido (alpha.txt y words.txt).
     * 
     * @pre La rutaDirectorio no debe ser null.
     * @param rutaDirectorio Ruta del directorio a verificar
     * @return true si el directorio contiene un diccionario válido
     * @post Se verifica la validez del directorio sin modificar el sistema de archivos.
     * @throws ExceptionLoggingOperacion Si hay errores durante la verificación del directorio
     */
    public boolean esDiccionarioValido(String rutaDirectorio) {
        try {
            Path dirPath = Paths.get(rutaDirectorio);
            if (!Files.exists(dirPath) || !Files.isDirectory(dirPath)) {
                return false;
            }
            
            Path alphaPath = dirPath.resolve("alpha.txt");
            Path wordsPath = dirPath.resolve("words.txt");
            
            return Files.exists(alphaPath) && Files.exists(wordsPath);
        } catch (Exception e) {
            throw new ExceptionLoggingOperacion("Error al verificar directorio: " + e.getMessage(), "validación", true);
        }
    }

    /**
     * Verifica si un carácter es un comodín en el diccionario especificado.
     * 
     * @pre Los parámetros no deben ser null.
     * @param nombreDiccionario Nombre del diccionario
     * @param caracter Carácter a verificar
     * @return true si es un comodín, false en caso contrario
     * @post Se verifica si el carácter es comodín sin modificar el estado del diccionario.
     * @throws ExceptionLoggingOperacion Si hay errores durante la verificación
     */
    public boolean esComodin(String nombreDiccionario, String caracter) {
        try {
            if (!existeDiccionario(nombreDiccionario)) {
                return false;
            }
            return controladorDiccionario.esComodin(nombreDiccionario, caracter);
        } catch (Exception e) {
            throw new ExceptionLoggingOperacion("Error al verificar comodín: " + e.getMessage(), "consulta", true);
        }
    }

    /**
     * Valida si un movimiento es válido en el contexto actual del juego.
     * 
     * @pre Los parámetros no deben ser null.
     * @param move movimiento a validar (palabra, posición, dirección)
     * @param rack fichas disponibles del jugador
     * @return true si el movimiento es válido, false en caso contrario
     * @post Se valida el movimiento según las reglas del juego y el estado actual del tablero.
     */
    public boolean isValidMove (Triple<String,Tuple<Integer, Integer>, Direction> move, Map<String, Integer> rack) {
        return controladorJuego.isJuegoIniciado()?controladorJuego.isValidMove(move, rack):controladorJuego.isValidFirstMove(move, rack);
    }

    /**
     * Muestra el rack del jugador de forma ordenada y legible.
     * 
     * @pre El jugador no debe ser null.
     * @param jugador Nombre del jugador
     * @return String con la información formateada del rack
     * @post Se devuelve una representación textual del rack sin modificar el estado del jugador.
     */
    public String mostrarRack(String jugador) {
        StringBuilder rackInfo = new StringBuilder();
        rackInfo.append("\n=== Rack del jugador: ").append(jugador).append(" ===\n");
        rackInfo.append("-----------------------------------------\n");
        this.controladorJugador.getRack(jugador).forEach((letra, cantidad) -> {
            rackInfo.append(String.format("Letra: %s | Cantidad: %d%n", letra, cantidad));
        });
        rackInfo.append("-----------------------------------------\n");
        return rackInfo.toString();
    }

    /**
     * Obtiene el rack actual de un jugador.
     * 
     * @pre El nombre no debe ser null.
     * @param nombre nombre del jugador
     * @return mapa con las fichas del jugador (letra -> cantidad)
     * @post Se devuelve el rack sin modificar el estado del jugador.
     */
    public Map<String, Integer> getRack(String nombre) {
        return controladorJugador.getRack(nombre);
    }

    /**
     * Muestra el estado actual de la partida para un jugador específico.
     * 
     * @pre El nombreJugador no debe ser null.
     * @param nombreJugador nombre del jugador
     * @return String con información del estado de la partida
     * @post Se devuelve información del estado sin modificar la partida.
     */
    public String mostrarStatusPartida(String nombreJugador) {
        return controladorJuego.mostrarStatusPartida(nombreJugador);
    }

    /**
     * Obtiene el nombre del diccionario utilizado en la partida actual.
     * 
     * @pre Debe haber una partida iniciada.
     * @return nombre del diccionario actual
     * @post Se devuelve el nombre sin modificar el estado de la partida.
     */
    public String getNombreDiccionario() {
        return controladorJuego.getNombreDiccionario();
    }
    
    /**
     * Guarda la partida actual en el sistema de persistencia.
     * 
     * @pre Debe haber una partida iniciada.
     * @return true si se guardó correctamente, false en caso de error
     * @post La partida se persiste en el almacenamiento si no hay errores.
     */
    public boolean guardarPartida() {
        try {
            return controladorJuego.guardar();
        } catch (ExceptionPersistenciaFallida e) {
            return false;
        }
    }

    /**
     * Carga una partida previamente guardada.
     * 
     * @pre El nombrePartida no debe ser null.
     * @param nombrePartida identificador de la partida a cargar
     * @post Se carga el estado de la partida especificada. Si hay errores, se imprime la traza.
     */
    public void cargarPartida(Integer nombrePartida) {
        try {
            controladorJuego.cargarDesdeArchivo(nombrePartida);
        } catch (ExceptionPersistenciaFallida e) {
            e.printStackTrace();
        }
    }

    /**
    * Obtiene el estado actual del tablero de juego.
    * 
    * @pre Debe haber una partida iniciada.
    * @return mapa con las posiciones del tablero y las letras colocadas
    * @post Se devuelve el estado del tablero sin modificar la partida.
    */
    public Map<Tuple<Integer, Integer>, String> getEstadoTablero() {
        return controladorJuego.getEstadoTablero();
    }  

    /**
     * Obtiene el nombre del diccionario asociado a una partida específica.
     *
     * @pre El idPartida debe ser válido.
     * @param idPartida el identificador único de la partida
     * @return el nombre del diccionario utilizado en la partida, o null si no se encuentra la partida
     * @post Se devuelve el nombre del diccionario sin modificar el estado de la partida.
     */
    public String obtenerDiccionarioPartida(int idPartida) {
        try {
            return ControladorJuego.obtenerDiccionarioPartida(idPartida);
        }
        catch (ExceptionPersistenciaFallida e) {
            return null;
        }
    }

    /**
     * Obtiene el número de jugadores asociado a una partida específica.
     *
     * @pre El idPartida debe ser válido.
     * @param idPartida el identificador único de la partida
     * @return la cantidad de jugadores en la partida, o -1 si no se encuentra la partida
     * @post Se devuelve el número de jugadores sin modificar el estado de la partida.
     */
    public int getNumJugadoresPartida(int idPartida){
        try {
            return ControladorJuego.getNumJugadoresPartida(idPartida);   
        } catch (
         ExceptionPersistenciaFallida e) {
          return -1;
        }
    }

    /**
     * Obtiene la lista de partidas guardadas disponibles.
     * 
     * @pre No hay precondiciones específicas.
     * @return lista de identificadores de partidas guardadas
     * @post Se devuelve la lista de partidas guardadas, o una lista vacía si hay errores.
     */
    public List<Integer> getPartidasGuardadas() {
        try {
            return ControladorJuego.listarArchivosGuardados();
        } catch (ExceptionPersistenciaFallida e) {
            return new ArrayList<>(); // Devuelve lista vacía en caso de error
        }
    }

    /**
     * Elimina una partida guardada del sistema de persistencia.
     * 
     * @pre El nombrePartida no debe ser null.
     * @param nombrePartida identificador de la partida a eliminar
     * @return true si se eliminó correctamente, false en caso de error
     * @post La partida se elimina del almacenamiento y los jugadores humanos se liberan de la partida.
     */
    public boolean eliminarPartidaGuardada(Integer nombrePartida) {
        try {
            Map<String, Integer> jugadores = ControladorJuego.getJugadoresPorId(nombrePartida);
            for (String jugador : jugadores.keySet()) {
                if (!controladorJugador.esIA(jugador)) {
                    controladorJugador.setEnPartida(jugador, false);
                    controladorJugador.setNombrePartidaActual(jugador, "");
                }
            }
            return ControladorJuego.eliminarArchivoGuardado(nombrePartida);
        } catch (ExceptionPersistenciaFallida e) {
            return false;
        }
    }

    /**
     * Libera a todos los jugadores de la partida actual.
     * 
     * @pre Debe haber una partida con jugadores.
     * @post Todos los jugadores humanos se marcan como fuera de partida y se limpia su partida actual.
     */
    public void aliberarJugadoresActuales() {
        Map<String, Integer> jugadoresSeleccionados = controladorJuego.getJugadoresActuales();
        for (String jugador :jugadoresSeleccionados.keySet()) {
            controladorJugador.setEnPartida(jugador, false);
            controladorJugador.setNombrePartidaActual(jugador, "");
        }
    }

    /**
     * Obtiene el mapa de jugadores participantes en la partida actual.
     * 
     * @pre Debe haber una partida iniciada.
     * @return mapa de jugadores con sus puntuaciones actuales
     * @post Se devuelve el mapa sin modificar el estado de la partida.
     */
    public Map<String, Integer> getJugadoresActuales() {
        return controladorJuego.getJugadoresActuales();
    }

    /**
     * Ordena una lista de usuarios según el criterio de ranking especificado.
     * Este método encapsula la lógica de ordenación para evitar tenerla en la capa de presentación.
     * 
     * @pre Los parámetros no deben ser null y el criterio debe ser válido.
     * @param usuarios Lista de usuarios a ordenar
     * @param criterio Criterio de ordenación ("maxima", "media", "partidas", "victorias")
     * @return Nueva lista ordenada de usuarios
     * @post Se devuelve una nueva lista ordenada sin modificar la lista original.
     */
    public List<String> ordenarUsuariosPorCriterio(List<String> usuarios, String criterio) {
        // Delegamos en el ControladorRanking para mantener la separación de responsabilidades
        return controladorRanking.ordenarUsuariosPorCriterio(usuarios, criterio);
    }

    /**
     * Obtiene el número de partidas jugadas por un jugador humano directamente del ranking.
     * 
     * @pre El nombre no debe ser null.
     * @param nombre Nombre del jugador
     * @return Número de partidas jugadas o 0 si el jugador no existe o es IA
     * @post Se devuelve el contador sin modificar el estado del ranking.
     */
    public int getPartidasJugadasDirecta(String nombre) {
        return controladorRanking.getPartidasJugadas(nombre);
    }
    
    /**
     * Obtiene el número de partidas ganadas por un jugador humano directamente del ranking.
     * 
     * @pre El nombre no debe ser null.
     * @param nombre Nombre del jugador
     * @return Número de partidas ganadas o 0 si el jugador no existe o es IA
     * @post Se devuelve el contador de victorias sin modificar el estado del ranking.
     */
    public int getPartidasGanadasDirecta(String nombre) {
        return controladorRanking.getVictorias(nombre);
    }

    /**
     * Actualiza estadísticas de jugadores al finalizar una partida con un único ganador.
     * 
     * @pre Los parámetros no deben ser null.
     * @param puntuacionesFinales Mapa con nombres de jugadores y sus puntuaciones finales
     * @param ganador Nombre del jugador ganador
     * @post Se actualizan las estadísticas llamando al método para múltiples ganadores con una lista de un elemento.
     */
    public void finalizarPartidaJugadores(Map<String, Integer> puntuacionesFinales, String ganador) {
        List<String> ganadores = new ArrayList<>();
        ganadores.add(ganador);
        finalizarPartidaJugadoresMultiple(puntuacionesFinales, ganadores);
    }

    /**
     * Obtiene el nombre de la partida actual en la que está participando un jugador.
     * 
     * @pre El nombre no debe ser null.
     * @param nombre Nombre del jugador
     * @return Nombre de la partida actual o cadena vacía si no está en partida o es una IA
     * @post Se devuelve el nombre de la partida sin modificar el estado del jugador.
     */
    public String getNombrePartidaActual(String nombre) {
        return controladorJugador.getNombrePartidaActual(nombre);
    }
    
    /**
     * Establece el nombre de la partida actual en la que está participando un jugador.
     * 
     * @pre Los parámetros no deben ser null.
     * @param nombre Nombre del jugador
     * @param nombrePartida Nombre de la partida
     * @return true si se estableció correctamente, false si el jugador no existe o es una IA
     * @post Se actualiza el nombre de la partida actual del jugador.
     */
    public boolean setNombrePartidaActual(String nombre, String nombrePartida) {
        return controladorJugador.setNombrePartidaActual(nombre, nombrePartida);
    }

    /**
     * Agrega una puntuación individual para un usuario específico.
     * Este método es idéntico a agregarPuntuacion y se proporciona para claridad semántica.
     * 
     * @pre El nombre debe ser no nulo y no vacío, y la puntuación debe ser no negativa.
     * @param nombre Nombre del usuario
     * @param puntuacion Puntuación individual a agregar
     * @return true si se agregó correctamente, false en caso contrario
     * @post Si el nombre es válido y la puntuación no es negativa, se agrega la puntuación
     *       a la lista de puntuaciones individuales del usuario, se actualiza la puntuación máxima
     *       y media, y se incrementa la puntuación total.
     * @throws NullPointerException Si el nombre es null
     */
    public boolean agregarPuntuacionIndividual(String nombre, int puntuacion) {
        return controladorRanking.agregarPuntuacionIndividual(nombre, puntuacion);
    }

    /**
     * Elimina todos los archivos de persistencia del sistema.
     * Este método se utiliza principalmente al cerrar la aplicación para limpiar los datos temporales.
     * 
     * @pre No hay precondiciones específicas.
     * @return true si todos los archivos fueron eliminados correctamente, false en caso contrario
     * @post Se intentan eliminar todos los archivos de persistencia del sistema.
     */
    public boolean limpiarPersistencias() {
        boolean todoOk = true;
        try {
            // Lista de rutas posibles para los archivos de persistencia
            String[][] posiblesRutas = {

                // Ruta relativa desde directorio fonts
                {"src/main/resources/persistencias/jugadores.dat", "src/main/resources/persistencias/partidas.dat", "src/main/resources/persistencias/ranking.dat"},
               
            };

            
            // Intentar cada conjunto de rutas posibles
            for (String[] rutasActuales : posiblesRutas) {
                for (String ruta : rutasActuales) {
                    Path path = Paths.get(ruta);
                    if (Files.exists(path)) {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            todoOk = false;
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            todoOk = false;
        }
        return todoOk;
    }

    /* == Getters y setters de la configuración == */

    /**
    * Obtiene el tema visual actual configurado en la aplicación.
    * 
    * @pre No hay precondiciones específicas.
    * @return String que representa el tema visual actual configurado
    * @post Se devuelve el tema actual sin modificar el estado de la configuración.
    */
    public String obtenerTema() {
        return controladorConfiguracion.obtenerTema();
    }

    /**
    * Obtiene el nivel de volumen actual configurado en la aplicación.
    * 
    * @pre No hay precondiciones específicas.
    * @return int que representa el nivel de volumen actual (0-100)
    * @post Se devuelve el volumen actual sin modificar el estado de la configuración.
    */
    // public int obtenerVolumen() {
    //     return controladorConfiguracion.obtenerVolumen();
    // }

    /**
    * Obtiene el diccionario actual configurado en la aplicación.
    * 
    * @pre No hay precondiciones específicas.
    * @return String que representa el diccionario actual configurado
    * @post Se devuelve el diccionario actual sin modificar el estado de la configuración.
    */
    public String obtenerDiccionario() {
        return controladorConfiguracion.obtenerDiccionario();
    }

    /**
    * Obtiene el tamaño actual configurado en la aplicación.
    * 
    * @pre No hay precondiciones específicas.
    * @return int que representa el tamaño actual configurado
    * @post Se devuelve el tamaño actual sin modificar el estado de la configuración.
    */
    public int obtenerTamano() {
        return controladorConfiguracion.obtenerTamano();
    }

    /**
    * Establece un nuevo tema visual para la configuración de la aplicación.
    * 
    * @pre El tema debe ser una de las opciones válidas soportadas por la aplicación.
    * @param tema String que representa el nuevo tema visual a configurar
    * @post El tema de la configuración se actualiza al valor especificado y se persiste.
    * @throws ExceptionPersistenciaFallida si ocurre un error al guardar la configuración
    * @throws IllegalArgumentException si el tema no es uno de los valores permitidos
    * @throws NullPointerException si el parámetro tema es null
    */
    public void establecerTema(String tema) throws ExceptionPersistenciaFallida {
        controladorConfiguracion.setTema(tema);
    }

    /**
    * Establece un nuevo nivel de volumen para la configuración de la aplicación.
    * 
    * @pre El volumen debe estar dentro del rango permitido (generalmente 0-100).
    * @param volumen int que representa el nuevo nivel de volumen a configurar
    * @post El nivel de volumen de la configuración se actualiza al valor especificado y se persiste.
    * @throws ExceptionPersistenciaFallida si ocurre un error al guardar la configuración
    * @throws IllegalArgumentException si el volumen está fuera del rango permitido
    */
    // public void establecerVolumen(int volumen) throws ExceptionPersistenciaFallida {
    //     controladorConfiguracion.setVolumen(volumen);
    // }

    /**
    * Establece un nuevo diccionario para la configuración de la aplicación.
    * 
    * @pre El diccionario debe ser una de las opciones válidas soportadas por la aplicación.
    * @param diccionario String que representa el nuevo diccionario a configurar
    * @post El diccionario de la configuración se actualiza al valor especificado y se persiste.
    * @throws ExceptionPersistenciaFallida si ocurre un error al guardar la configuración
    * @throws IllegalArgumentException si el diccionario no es uno de los valores permitidos
    * @throws NullPointerException si el parámetro diccionario es null
    */
    public void establecerDiccionario(String diccionario) throws ExceptionPersistenciaFallida {
        controladorConfiguracion.setDiccionario(diccionario);
    }

    /**
    * Establece un nuevo tamaño para la configuración de la aplicación.
    * 
    * @pre El tamaño debe estar dentro del rango permitido por la aplicación.
    * @param tamano int que representa el nuevo tamaño a configurar
    * @post El tamaño de la configuración se actualiza al valor especificado y se persiste.
    * @throws ExceptionPersistenciaFallida si ocurre un error al guardar la configuración
    * @throws IllegalArgumentException si el tamaño está fuera del rango permitido
    */
    public void establecerTamano(int tamano) throws ExceptionPersistenciaFallida {
        controladorConfiguracion.setTamano(tamano);
    }   

    /**
     * Guarda la configuración general de la aplicación en un archivo de propiedades.
     * 
     * @pre Los parámetros de volumen deben estar en rangos válidos.
     * @param tema El tema visual seleccionado por el usuario
     * @param musicaActivada Indica si la música está activada (true) o desactivada (false)
     * @param sonidoActivado Indica si los efectos de sonido están activados (true) o desactivados (false)
     * @param volumenMusica El volumen establecido de la música
     * @param volumenSonido El volumen establecido del sonido
     * @post Los valores se almacenan en un archivo de configuración, sobrescribiendo cualquier configuración previa.
     *       Si ocurre un error de E/S durante el guardado, se imprime la traza de la excepción.
     */
    public void guardarConfiguracionGeneral(String tema, boolean musicaActivada, boolean sonidoActivado, int volumenMusica, int volumenSonido) {
        controladorConfiguracion.guardarConfiguracionGeneral(tema, musicaActivada, sonidoActivado, volumenMusica, volumenSonido);
    }


    /**
     * Carga la configuración desde un archivo de propiedades.
     * Si el archivo no se puede cargar, se utilizarán valores por defecto.
     *
     * @pre No hay precondiciones específicas.
     * @return Un mapa que contiene las claves y valores de configuración cargados
     * @post Se devuelve la configuración cargada sin modificar el estado del sistema.
     */
    public Map<String, String>  cargarConfiguracion() {
        return controladorConfiguracion.getConfMap();
    }
}