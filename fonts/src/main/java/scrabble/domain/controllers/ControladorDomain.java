package scrabble.domain.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scrabble.domain.controllers.subcontrollers.ControladorConfiguracion;
import scrabble.domain.controllers.subcontrollers.ControladorJuego;
import scrabble.domain.controllers.subcontrollers.ControladorJugador;
import scrabble.domain.controllers.subcontrollers.ControladorRanking;
import scrabble.excepciones.ExceptionDiccionarioExist;
import scrabble.excepciones.ExceptionLanguageNotExist;
import scrabble.excepciones.ExceptionPasswordMismatch;
import scrabble.excepciones.ExceptionPuntuacionNotExist;
import scrabble.excepciones.ExceptionRankingOperationFailed;
import scrabble.excepciones.ExceptionUserEsIA;
import scrabble.excepciones.ExceptionUserExist;
import scrabble.excepciones.ExceptionUserInGame;
import scrabble.excepciones.ExceptionUserLoggedIn;
import scrabble.excepciones.ExceptionUserNotExist;
import scrabble.excepciones.ExceptionUserNotLoggedIn;
import scrabble.helpers.Dificultad;
import scrabble.helpers.Tuple;
import scrabble.helpers.BooleanWrapper;


    /**
    * Constructor del controlador del dominio.
    * Inicializa todos los subcontroladores necesarios.
    */
public class ControladorDomain {
    private ControladorConfiguracion controladorConfiguracion;
    private ControladorJuego controladorJuego;
    private ControladorRanking controladorRanking;
    private ControladorJugador controladorJugador;


    public ControladorDomain() {
        this.controladorConfiguracion = new ControladorConfiguracion();
        this.controladorJuego = new ControladorJuego();
        this.controladorRanking = ControladorRanking.getInstance();
        this.controladorJugador = ControladorJugador.getInstance();
    }

    /**
     * Retorna el ID asociado al nombre de usuario proporcionado.
     * @param username nombre de usuario
     * @return ID del usuario
     * @throws ExceptionUserNotExist si el usuario no existe
     */

    public String getIdPorNombre(String username) {
        if (!controladorJugador.existeJugador(username)) {
            throw new ExceptionUserNotExist();
        }
        return controladorJugador.getIdPorNombre(username);
    }

    /**
     * Verifica si un jugador (humano) está listo para jugar.
     * @param id identificador del jugador
     * @return true si está listo para jugar
     * @throws ExceptionUserEsIA si el usuario es una IA
     * @throws ExceptionUserNotLoggedIn si el usuario no ha iniciado sesión
     * @throws ExceptionUserInGame si el usuario ya está en partida
     */
    public boolean playerReadyToPlay(String id) {

        if (controladorJugador.esIA(id)) {
            throw new ExceptionUserEsIA();
        }

        if (!controladorJugador.isLoggedIn(id)) {
            throw new ExceptionUserNotLoggedIn();
        }

        if (controladorJugador.isEnPartida(id)) {
            throw new ExceptionUserInGame();
        }    
        
        return true;
    }

    /**
     * Inicia sesión con nombre de usuario y contraseña.
     * @param username nombre de usuario
     * @param password contraseña
     * @return true si la autenticación fue exitosa
     * @throws ExceptionUserNotExist si el usuario no existe
     * @throws ExceptionUserEsIA si el usuario es una IA
     * @throws ExceptionPasswordMismatch si la contraseña es incorrecta
     * @throws ExceptionUserLoggedIn si el usuario ya está autenticado
     */
    public boolean  iniciarSesion(String username, String password) {

        String id = getIdPorNombre(username);

        if (!controladorJugador.existeJugador(id)) {
            throw new ExceptionUserNotExist();
        }

        if (controladorJugador.esIA(id)) {
            throw new ExceptionUserEsIA();
        }

        if (!controladorJugador.verificarPassword(id, password)) {
            throw new ExceptionPasswordMismatch();
        }
        
        if (controladorJugador.isLoggedIn(id)) {
            throw new ExceptionUserLoggedIn();
        } 

        return controladorJugador.autenticar(id, password);
    }

    /**
     * Registra un nuevo usuario humano.
     * @param username nombre de usuario
     * @param password contraseña del usuario
     * @return true si se registra exitosamente
     * @throws ExceptionUserExist si ya existe un usuario con ese nombre
     */
    public boolean  registrarUsuario(String username, String password) {
        // lo pongo así porque me es comodo para debugar, recordar quitar

        if (controladorJugador.existeJugador(username) && !username.equals("admin")) {
            throw new ExceptionUserExist();
        }


        return controladorJugador.registrarUsuario(username, password);
    }

    /**
     * Cierra la sesión del usuario proporcionado.
     * @param username nombre de usuario
     * @return true si se cierra la sesión correctamente
     * @throws ExceptionUserNotExist si el usuario no existe
     * @throws ExceptionUserNotLoggedIn si el usuario no ha iniciado sesión
     * @throws ExceptionUserEsIA si el usuario es una IA
     */
    public boolean cerrarSesion(String username) {
        String id = getIdPorNombre(username);   

        if (!controladorJugador.existeJugador(id)) {
            throw new ExceptionUserNotExist();
        }
        if (!controladorJugador.isLoggedIn(id)) {
            throw new ExceptionUserNotLoggedIn();
        }
        if (controladorJugador.esIA(id)) {
            throw new ExceptionUserEsIA();
        }

        return controladorJugador.cerrarSesion(id);
    }

    /**
     * Elimina el usuario si no está logueado y su puntuación en el ranking (si existe).
     * @param username nombre de usuario
     * @return true si la eliminación es exitosa
     * @throws ExceptionUserNotExist si el usuario no existe
     * @throws ExceptionUserLoggedIn si el usuario está autenticado
     * @throws ExceptionRankingOperationFailed si falla la operación sobre el ranking
     */
    public boolean eliminarUsuario(String username) {
        String id = getIdPorNombre(username);

        if (!controladorJugador.existeJugador(id)) {
            throw new ExceptionUserNotExist();
        }
        if (controladorJugador.isLoggedIn(id)) {
            throw new ExceptionUserLoggedIn();
        }

        // asumo que se puede eliminar una IA
        boolean eliminacionExitosa = true;
        if (controladorRanking.perteneceRanking(id)) eliminacionExitosa = controladorRanking.eliminarUsuario(id);

        if (!eliminacionExitosa) {
            throw new ExceptionRankingOperationFailed();
        }

        return controladorJugador.eliminarUsuario(id) && eliminacionExitosa;
    }

    /**
     * Cambia la contraseña de un usuario dado si la anterior coincide.
     * @param username nombre de usuario
     * @param oldPass contraseña actual
     * @param nuevaContrasena nueva contraseña
     * @return true si se realiza el cambio correctamente
     * @throws ExceptionUserNotExist si el usuario no existe
     * @throws ExceptionUserEsIA si el usuario es una IA
     * @throws ExceptionPasswordMismatch si la contraseña actual no coincide
     */
    public boolean cambiarContrasena(String username, String oldPass, String nuevaContrasena) {
        String id = getIdPorNombre(username);

        if (!controladorJugador.existeJugador(id)) {
            throw new ExceptionUserNotExist();
        }

        if (controladorJugador.esIA(id)) {
            throw new ExceptionUserEsIA();
        }

        // jugador existe y no es IA, queda verificar la contraseña
        if (!controladorJugador.verificarPassword(id, oldPass)) {
            throw new ExceptionPasswordMismatch();
        }

        // Realizar el cambio de contraseña
        boolean resultado = controladorJugador.cambiarContrasena(id, oldPass, nuevaContrasena);


        return resultado;
    }

    /**
     * Cambia el nombre de un usuario humano.
     * @param antiguoNombre nombre actual del usuario
     * @param nuevoNombre nuevo nombre a asignar
     * @return true si se cambia correctamente
     * @throws ExceptionUserNotExist si el usuario no existe
     * @throws ExceptionUserEsIA si el usuario es una IA
     */
    public boolean cambiarNombre(String antiguoNombre, String nuevoNombre) {

        String id = getIdPorNombre(antiguoNombre);

        if (!controladorJugador.existeJugador(id)) {
            throw new ExceptionUserNotExist();
        }

        if (controladorJugador.esIA(id)) {
            throw new ExceptionUserEsIA();
        }

        return controladorJugador.cambiarNombre(id, nuevoNombre);

    }

    /**
     * Crea un jugador IA con dificultad especificada.
     * @param id identificador único del jugador IA
     * @param dificultadStr dificultad en formato String (EASY, MEDIUM, HARD...)
     * @throws ExceptionUserExist si el ID ya está registrado
     */
    public void crearJugadorIA(String id, String dificultadStr) {
        // lo pongo así porque me es comodo para debugar
        Dificultad dificultad = Dificultad.valueOf(dificultadStr);
        if (!controladorJugador.existeJugador(id)) controladorJugador.registrarJugadorIA(id, dificultad);
        else throw new ExceptionUserExist();
    }

    /**
     * Devuelve la lista de identificadores de jugadores IA registrados.
     * @return lista de IDs de jugadores IA
     */
    public List<String> getJugadoresIA() {
        return controladorJugador.getJugadoresIA();
    }

    /**
     * Obtiene el nombre de usuario asociado a un ID.
     * @param id identificador del jugador
     * @return nombre del jugador
     */
    public String getNombrePorId(String id) {
        return controladorJugador.getNombre(id);
    }

    /**
    * Muestra por consola todos los usuarios y otras infos (DEBUG)
    */
    public void mostrarTodosUsuariosDebug() {
        System.out.println("\n=== LISTADO DEBUG DE USUARIOS ===");
        System.out.println("-----------------------------------------");
        
        controladorJugador.getTodosUsuariosDebug().forEach(System.out::println);
        
        System.out.println("\nTotal registrados: " + controladorJugador.getJugadoresRegistrados().size());
        System.out.println("- Humanos: " + controladorJugador.getJugadoresHumanos().size());
        System.out.println("- IA: " + controladorJugador.getJugadoresIA().size());
    }

    public void setIdioma(String idioma) {
        controladorConfiguracion.setIdioma(idioma);
    }

    public void setTema(String tema) {
        controladorConfiguracion.setTema(tema);
    }

    public void setVolumen(int volumen) {
        controladorConfiguracion.setVolumen(volumen);
    }

    /**
    * Devuelve la lista de nombres de los diccionarios disponibles en el sistema.
    *
    * @return lista de nombres de diccionarios disponibles.
    */
    public List<String> getDiccionariosDisponibles () {
        return controladorJuego.getDiccionariosDisponibles();
    }

    /**
    * Añade un nuevo lenguaje al sistema a partir de los archivos en las rutas proporcionadas.
    *
    * @param nombre nombre del nuevo lenguaje.
    * @param rutaArchivoAlpha ruta del archivo con el alfabeto del lenguaje.
    * @param rutaArchivoWords ruta del archivo con las palabras válidas del lenguaje.
    * @throws ExceptionDiccionarioExist si el lenguaje ya existe en el sistema.
    */
    public void anadirLenguaje(String nombre, String rutaArchivoAlpha, String rutaArchivoWords) {
        if (controladorJuego.existeLenguaje(nombre)) {
            throw new ExceptionDiccionarioExist();
        }

        controladorJuego.anadirLenguaje(nombre, rutaArchivoAlpha, rutaArchivoWords);
    }

    /**
    * Verifica si existe un lenguaje en el sistema con el nombre dado.
    *
    * @param nombre nombre del lenguaje a comprobar.
    * @return true si el lenguaje ya existe; false en caso contrario.
    */
    public boolean existeLenguaje(String nombre) {
        return controladorJuego.existeLenguaje(nombre); 
    }

    /**
    * Establece el lenguaje a utilizar en la partida actual.
    *
    * @param nombre nombre del lenguaje a configurar.
    * @throws ExceptionLanguageNotExist si el lenguaje no existe en el sistema.
    */
    public void setLenguaje(String nombre) {
        if (!controladorJuego.existeLenguaje(nombre)) {
            throw new ExceptionLanguageNotExist();
        }
        controladorJuego.setLenguaje(nombre);
    }

    // MÉTODOS PARA JUGAR 


        /**
        * Realiza el turno de un jugador (independientemente si es humano o no) y devuelve el resultado del turno.
        *
        * @param username nombre del usuario que realiza el turno.
        * @param id identificador único del jugador.
        * @param dificultadStr nivel de dificultad en formato texto (solo aplicable si el jugador es IA).
        * @return una tupla que contiene:
        *         
        *           ·Un mapa con el rack actualizado del jugador tras el turno.
        *           ·El puntaje obtenido en el turno.
        *         
        * 
        */    
    public Tuple<Map<String, Integer>, Integer> realizarTurno(String username, String id, String dificultadStr, BooleanWrapper pausado) {

        Map<String, Integer> rack = controladorJugador.getRack(id);
        boolean esIA = controladorJugador.esIA(id);
        Dificultad dificultad = dificultadStr.equals("") ? null : Dificultad.valueOf(dificultadStr);
        return controladorJuego.realizarTurno(username, rack, esIA, dificultad, pausado);

    }

    /**
     * Obtiene el número de turnos consecutivos que un jugador ha pasado.
     * @param id identificador del jugador
     * @return número de turnos pasados
     */
    public int getSkipTrack (String id) {
        return controladorJugador.getSkipTrack(id);
    }

    /**
     * Incrementa el contador de turnos pasados por el jugador.
     * @param id identificador del jugador
     */
    public void addSkipTrack(String id) {
        controladorJugador.addSkipTrack(id);
    }

    /**
     * Inicializa el rack del jugador con las fichas proporcionadas.
     * @param id identificador del jugador
     * @param rack mapa de letras a cantidades
     */
    public void inicializarRack(String id, Map<String,Integer> rack) {
        controladorJugador.inicializarRack(id, rack);
    }

    /**
     * Añade puntuación al jugador.
     * @param id identificador del jugador
     * @param puntuacion cantidad a sumar
     */
    public void addPuntuacion(String id, int puntuacion) {
        controladorJugador.addPuntuacion(id, puntuacion);
    }  

    /**
     * Devuelve una cantidad de fichas aleatorias.
     * @param cantidad número de fichas a coger
     * @return mapa de letras a cantidades
     */
    public Map<String, Integer> cogerFichas(int cantidad) {
        return controladorJuego.cogerFichas(cantidad);
    }

    /**
     * Devuelve la cantidad de fichas actuales en el rack del jugador.
     * @param id identificador del jugador
     * @return cantidad total de fichas
     */
    public int getCantidadFichas(String id) {
        return controladorJugador.getCantidadFichas(id);
    }

   /**
     * Agrega una ficha al rack del jugador.
     * @param id identificador del jugador
     * @param letra letra a agregar
     */
    public void agregarFicha(String id, String letra) {
        controladorJugador.agregarFicha(id, letra);
    }

    /**
     * Devuelve la puntuación actual del jugador.
     * @param id identificador del jugador
     * @return puntuación del jugador
     */
    public int getPuntuacion(String id) {
        return controladorJugador.getPuntuacion(id);
    }
    
    /**
    * Inicia una nueva partida de Scrabble con los parámetros proporcionados.
    * 
    * @param nombrePartida nombre identificador de la partida.
    * @param jugadoresSeleccionados mapa que asocia un nombre de jugador a su ID.
    * @param diccionario nombre del diccionario/lenguaje a utilizar en la partida.
    * @param N tamaño del tablero; si es distinto de 15, se crea un tablero personalizado de NxN.
    *     
    */  
    public void iniciarPartida(String nombrePartida, HashMap<String, String> jugadoresSeleccionados, String diccionario, int N) {
    
        controladorJuego.setLenguaje(diccionario);
        controladorJuego.iniciarJuego(diccionario);
        
        if (N != 15) {
            controladorJuego.creaTableroNxN(N);
        }

        for (Map.Entry<String, String> entry : jugadoresSeleccionados.entrySet()) {
            String id = entry.getValue();
            Map<String, Integer> rack = controladorJuego.cogerFichas(7);
            inicializarRack(id, rack);
        }

    }

    
    /**
     * Devuelve la cantidad de fichas restantes en el pool del juego.
     * @return cantidad de fichas disponibles
     */
    public int getCantidadFichasRestantes() {
        return controladorJuego.getCantidadFichas();
    }

    /**
     * Finaliza el juego actual y realiza tareas de limpieza si es necesario.
     */
    public void finalizarJuego() {
        controladorJuego.finalizarJuego();
    }

    /**
     * Reinicia el estado del juego completamente.
     */
    public void reiniciarJuego() {
        controladorJuego.reiniciarJuego();
    }

    /**
     * Verifica si el juego actual ha terminado.
     * @return true si el juego ha finalizado
     */
    public boolean isJuegoTerminado() {
        return controladorJuego.isJuegoTerminado();
    }

    // // id (nombre de partida) -> (idjugador -> (ficha -> cantidad)) 
    // public void guardarPartida(String partidaId, Map<String, Map<String, Integer>> racks) { 
    //     controladorJuego.guardarPartida(partidaId, racks);
    // }

    // public Map<String, Map<String, Integer>> cargarPartida(String idPartida) {
    //     if (!controladorJuego.existeIdPartida(idPartida)) {
    //         throw new ExceptionPartidaNotExist();
    //     }
    //     return controladorJuego.cargarPartida(idPartida);
    // }


    // METODOS DE RANKING

    public void verRanking() {
        controladorRanking.verRanking();
    }


    public boolean actualizarEstadisticasUsuario(String username, boolean esVictoria) {
        if (!controladorJugador.existeJugador(username)) {
            throw new ExceptionUserNotExist();
        }
        return controladorRanking.actualizarEstadisticasUsuario(username, esVictoria);
    }

    public boolean eliminarPuntuacion(String username, int puntuacion) {
        if (!controladorRanking.existePuntuacion(username, puntuacion)) {
            throw new ExceptionPuntuacionNotExist();
        }
        return controladorRanking.eliminarPuntuacion(username, puntuacion);
    }

    public void cambiarEstrategia(String criterio) {
        controladorRanking.cambiarEstrategia(criterio);
    }

    public void listarPuntuaciones() {
        controladorRanking.listarPuntuaciones();
    }

    // public void verPartidasGuardadas(String username) {
    //     controladorJuego.verPartidasGuardadas(username);
    // }


    // public void eliminarPartidaGuardada(String username, int idPartida) {
    //     if (!controladorJuego.existePartidaGuardada(username, idPartida)) {
    //         throw new ExceptionPartidaNotExist();
    //     }
    //     if (controladorJuego.existePartidaEnUso(username, idPartida)) {
    //         throw new ExceptionPartidaInUse();
    //     }
    //     controladorJuego.eliminarPartidaGuardada(username, idPartida);
    // }
}


