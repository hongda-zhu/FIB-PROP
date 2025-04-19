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
import scrabble.excepciones.ExceptionUserEsIA;
import scrabble.excepciones.ExceptionUserExist;
import scrabble.excepciones.ExceptionUserInGame;
import scrabble.excepciones.ExceptionUserNotExist;
import scrabble.excepciones.ExceptionPuntuacionNotExist;
import scrabble.excepciones.ExceptionRankingOperationFailed;
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
     * Verifica si un jugador (humano) está listo para jugar.
     * @param nombre nombre del jugador
     * @return true si está listo para jugar
     * @throws ExceptionUserEsIA si el usuario es una IA
     * @throws ExceptionUserInGame si el usuario ya está en partida
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
     * Registra un nuevo usuario humano.
     * @param nombre nombre de usuario
     * @return true si se registra exitosamente
     * @throws ExceptionUserExist si ya existe un usuario con ese nombre
     */
    public boolean registrarUsuario(String nombre) {
        if (controladorJugador.existeJugador(nombre) && !nombre.equals("admin")) {
            throw new ExceptionUserExist();
        }

        return controladorJugador.registrarUsuario(nombre);
    }

    /**
     * Elimina el usuario del sistema y su puntuación en el ranking (si existe).
     * @param nombre nombre de usuario
     * @return true si la eliminación es exitosa
     * @throws ExceptionUserNotExist si el usuario no existe
     * @throws ExceptionRankingOperationFailed si falla la operación sobre el ranking
     */
    public boolean eliminarUsuario(String nombre) {
        if (!controladorJugador.existeJugador(nombre)) {
            throw new ExceptionUserNotExist();
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
     * @param dificultadStr dificultad en formato String (FACIL, DIFICIL)
     * @return el nombre generado para el jugador IA
     */
    public String crearJugadorIA(String dificultadStr) {
        Dificultad dificultad = Dificultad.valueOf(dificultadStr);
        return controladorJugador.registrarJugadorIA(dificultad) ? 
            controladorJugador.getJugadoresIA().get(controladorJugador.getJugadoresIA().size() - 1) : 
            null;
    }

    /**
     * Devuelve la lista de nombres de jugadores IA registrados.
     * @return lista de nombres de jugadores IA
     */
    public List<String> getJugadoresIA() {
        return controladorJugador.getJugadoresIA();
    }

    /**
     * Obtiene el nombre de un jugador.
     * @param nombre nombre del jugador
     * @return nombre del jugador (mismo que parámetro)
     */
    public String getNombre(String nombre) {
        return controladorJugador.getNombre(nombre);
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
    * @param nombreJugador nombre del jugador que realiza el turno.
    * @param dificultadStr nivel de dificultad en formato texto (solo aplicable si el jugador es IA).
    * @return una tupla que contiene:
    *         
    *           ·Un mapa con el rack actualizado del jugador tras el turno.
    *           ·El puntaje obtenido en el turno.
    *         
    * 
    */    
    public Tuple<Map<String, Integer>, Integer> realizarTurno(String nombreJugador, String dificultadStr, BooleanWrapper pausado) {
        Map<String, Integer> rack = controladorJugador.getRack(nombreJugador);
        boolean esIA = controladorJugador.esIA(nombreJugador);
        Dificultad dificultad = dificultadStr.equals("") ? null : Dificultad.valueOf(dificultadStr);
        return controladorJuego.realizarTurno(nombreJugador, rack, esIA, dificultad, pausado);
    }

    /**
     * Obtiene el número de turnos consecutivos que un jugador ha pasado.
     * @param nombre nombre del jugador
     * @return número de turnos pasados
     */
    public int getSkipTrack(String nombre) {
        return controladorJugador.getSkipTrack(nombre);
    }

    /**
     * Incrementa el contador de turnos pasados por el jugador.
     * @param nombre nombre del jugador
     */
    public void addSkipTrack(String nombre) {
        controladorJugador.addSkipTrack(nombre);
    }

    /**
     * Inicializa el rack del jugador con las fichas proporcionadas.
     * @param nombre nombre del jugador
     * @param rack mapa de letras a cantidades
     */
    public void inicializarRack(String nombre, Map<String,Integer> rack) {
        controladorJugador.inicializarRack(nombre, rack);
    }

    /**
     * Añade puntuación al jugador.
     * @param nombre nombre del jugador
     * @param puntuacion cantidad a sumar
     */
    public void addPuntuacion(String nombre, int puntuacion) {
        controladorJugador.addPuntuacion(nombre, puntuacion);
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
     * @param nombre nombre del jugador
     * @return cantidad total de fichas
     */
    public int getCantidadFichas(String nombre) {
        return controladorJugador.getCantidadFichas(nombre);
    }

   /**
     * Agrega una ficha al rack del jugador.
     * @param nombre nombre del jugador
     * @param letra letra a agregar
     */
    public void agregarFicha(String nombre, String letra) {
        controladorJugador.agregarFicha(nombre, letra);
    }

    /**
     * Devuelve la puntuación actual del jugador.
     * @param nombre nombre del jugador
     * @return puntuación del jugador
     */
    public int getPuntuacion(String nombre) {
        return controladorJugador.getPuntuacion(nombre);
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
            String nombreJugador = entry.getValue(); // Ahora entry.getValue() es directamente el nombre
            Map<String, Integer> rack = controladorJuego.cogerFichas(7);
            inicializarRack(nombreJugador, rack);
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

    // METODOS DE RANKING

    public void verRanking() {
        controladorRanking.verRanking();
    }

    public boolean actualizarEstadisticasUsuario(String nombre, boolean esVictoria) {
        if (!controladorJugador.existeJugador(nombre)) {
            throw new ExceptionUserNotExist();
        }
        return controladorRanking.actualizarEstadisticasUsuario(nombre, esVictoria);
    }

    public boolean eliminarPuntuacion(String nombre, int puntuacion) {
        if (!controladorRanking.existePuntuacion(nombre, puntuacion)) {
            throw new ExceptionPuntuacionNotExist();
        }
        return controladorRanking.eliminarPuntuacion(nombre, puntuacion);
    }

    public void cambiarEstrategia(String criterio) {
        controladorRanking.cambiarEstrategia(criterio);
    }

    public void listarPuntuaciones() {
        controladorRanking.listarPuntuaciones();
    }

    /**
     * Verifica si un jugador existe en el sistema.
     * 
     * @param nombre Nombre del jugador
     * @return true si el jugador existe, false en caso contrario
     */
    public boolean existeJugador(String nombre) {
        return controladorJugador.existeJugador(nombre);
    }
    
    /**
     * Verifica si un jugador es una IA.
     * 
     * @param nombre Nombre del jugador
     * @return true si el jugador es una IA, false si es un jugador humano
     */
    public boolean esIA(String nombre) {
        return controladorJugador.esIA(nombre);
    }
    
    /**
     * Verifica si un jugador humano está en una partida.
     * 
     * @param nombre Nombre del jugador
     * @return true si está en partida, false en caso contrario
     */
    public boolean isEnPartida(String nombre) {
        return controladorJugador.isEnPartida(nombre);
    }
}


