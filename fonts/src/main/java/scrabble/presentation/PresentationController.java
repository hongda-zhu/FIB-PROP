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
////////
public class PresentationController {
    private static ControladorDomain ctrlDomain;
    private static PresentationController presentationController;

    public static PresentationController getInstance() {
        if (presentationController == null) {
            presentationController = new PresentationController();
            ctrlDomain = ControladorDomain.getInstance();
        }
        return presentationController;
    }

    public void initializeDefaultSettings() {
        // Registrar algunos usuarios por defecto para testing solo si no existen
        if (!ctrlDomain.existeJugador("xuanyi")) ctrlDomain.registrarUsuario("xuanyi");
        if (!ctrlDomain.existeJugador("jiahao")) ctrlDomain.registrarUsuario("jiahao");
        if (!ctrlDomain.existeJugador("songhe")) ctrlDomain.registrarUsuario("songhe");
        if (!ctrlDomain.existeJugador("hongda")) ctrlDomain.registrarUsuario("hongda");
        ctrlDomain.crearJugadorIA(Dificultad.FACIL, "DummyEZ");
        ctrlDomain.crearJugadorIA(Dificultad.DIFICIL, "DummyHardCore");
    }

    /**
    * Método para iniciar una partida con los jugadores seleccionados
     * @throws ExceptionPersistenciaFallida 
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
            System.err.println("Error al iniciar partida: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene el nombre del diccionario de la partida a cargar
     * @return El nombre del diccionario
     */
    public String getNombreDiccionario() {
        return ctrlDomain.getNombreDiccionario();
    }

    /**
    * Carga una partida guardada
    */
    public int getTableroSize() {
        return ctrlDomain.getSize();
    }

    /**
    * Obtiene la cantidad de fichas restantes en la bolsa
    */
    public int getCantidadFichasRestantes() {
        return ctrlDomain.getCantidadFichasRestantes();
    }
    
    /**
    * Obtiene el estado actual de la partida para un jugador
    */
    public String getEstadoPartida(String nombreJugador) {
        return ctrlDomain.mostrarStatusPartida(nombreJugador);
    }

    /**
    * Método para obtener el estado del tablero guardado
    */
    public Map<Tuple<Integer, Integer>, String> getEstadoTablero() {
        return ctrlDomain.getEstadoTablero();
    }    

    /**
    * Obtiene el rack de un jugador
    */
    public Map<String, Integer> getRackJugador(String nombreJugador) {
        return ctrlDomain.getRack(nombreJugador);
    }

    /**
    *  Resetea el skiptrack de un jugador
    */
    public void clearSkipTrack(String nombreJugador) {
        ctrlDomain.clearSkipTrack(nombreJugador);
    }

    /**
    * Obtiene la representación en texto del rack del jugador
    */
    public String mostrarRackJugador(String nombreJugador) {
        return ctrlDomain.mostrarRack(nombreJugador);
    }

    /**
    * Verifica si un movimiento es válido
    */
    public boolean esMovimientoValido(Triple<String, Tuple<Integer, Integer>, Direction> move, Map<String, Integer> rack) {
        return ctrlDomain.isValidMove(move, rack);
    }

    /**
    * Realiza un turno de juego
    */
    public int realizarTurnoPartida(String nombreJugador, Triple<String, Tuple<Integer, Integer>, Direction> jugada) {
        return ctrlDomain.realizarTurnoPartida(nombreJugador, jugada);
    }

    /**
    * Actualiza la puntuación de un jugador
    */
    public void actualizarJugadores(String nombreJugador, int puntos) {
        ctrlDomain.actualizarJugadores(nombreJugador, puntos);
    }

    /**
    * Comprueba si la partida ha finalizado
    */
    public void comprobarFinPartida(Map<String, Integer> jugadoresSeleccionados) {
        ctrlDomain.comprobarFinPartida(jugadoresSeleccionados);
    }

    /**
    * Verifica si el juego ha terminado
    */
    public boolean isJuegoTerminado() {
        return ctrlDomain.isJuegoTerminado();
    }

    /**
    * Finaliza el juego y obtiene el resultado
    */
    public String finalizarJuego(Map<String, Integer> jugadoresSeleccionados) {
        return ctrlDomain.finalizarJuego(jugadoresSeleccionados);
    }

    /**
    * Intercambia fichas del rack del jugador
    */
    public boolean intercambiarFichas(String nombreJugador, List<String> fichas) {
        return ctrlDomain.intercambiarFichas(nombreJugador, fichas);
    }

    /**
    * Obtiene los jugadores actuales de la partida
    */
    public Map<String, Integer> getJugadoresActuales() {
        return ctrlDomain.getJugadoresActuales();
    }

    /**
    * Libera los jugadores al finalizar/salir de una partida
    */
    public void liberarJugadoresActuales() {
        ctrlDomain.aliberarJugadoresActuales();
    }

    /**
    * Guarda la partida actual
    */
    public boolean guardarPartida() {
        return ctrlDomain.guardarPartida();
    }

    /**
    * Obtiene la lista de partidas guardadas
    */
    public List<Integer> getPartidasGuardadas() {
        return ctrlDomain.getPartidasGuardadas();
    }

    public String getDiccionarioPartida(Integer id) {
        return ctrlDomain.obtenerDiccionarioPartida(id);
    }

    public int getNumJugadoresPartida (Integer id) {
        return ctrlDomain.getNumJugadoresPartida(id);
    }

    /**
    * Carga una partida guardada
    */
    public void cargarPartida(Integer idPartida) {
        ctrlDomain.cargarPartida(idPartida);
    }



    /**
    * Elimina una partida guardada
    */
    public boolean eliminarPartidaGuardada(Integer idPartida) {
        return ctrlDomain.eliminarPartidaGuardada(idPartida);
    }

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

    public List<String> getAllJugadores() {
        return ctrlDomain.getUsuariosHumanos();
    }

    public int getVictorias(String nombre) {
        return ctrlDomain.getVictorias(nombre);
    }

    public boolean isEnPartida(String nombre) {
        return ctrlDomain.isEnPartida(nombre);
    }

    public boolean esIA(String nombre) {
        return ctrlDomain.esIA(nombre);
    }

    public String getNombrePartidaActual(String nombre) {
        return ctrlDomain.getNombrePartidaActual(nombre);
    }

    public List<String> getAllDiccionariosDisponibles() {
        return ctrlDomain.getDiccionariosDisponibles();
    }

    public void iniciarPartida(ArrayList<String> jugadores) {
        ctrlDomain.inicializarJugadoresPartida(jugadores);
    }

  
    public boolean crearJugador(String nombre) {
        // Implementar llamada al modelo para crear un jugador
        return ctrlDomain.registrarUsuario(nombre);
    }

    public boolean eliminarJugador(String nombre) {
        // Implementar llamada al modelo para eliminar un jugador
        return ctrlDomain.eliminarUsuario(nombre);
    }

    public List<String> buscarJugadoresPorNombre(String patron) {
        List<String> resultados = new ArrayList<>();
        List<String> todos = getAllJugadoresDisponibles();
        
        if (patron == null || patron.trim().isEmpty()) {
            resultados.addAll(todos);
        } else {
            String patronLower = patron.toLowerCase();
            for (String jugador : todos) {
                if (jugador.toLowerCase().contains(patronLower)) {
                    resultados.add(jugador);
                }
            }
        }
        
        return resultados;
    }

    /**
     * Devuelve una lista de diccionarios disponibles
     * @return Lista de nombres de diccionarios disponibles
     */

    public List<String> getDiccionariosDisponibles() {
        // Implementar llamada al modelo para obtener diccionarios disponibles
        return ctrlDomain.getDiccionariosDisponibles();
    }

    /**
     * Crea un nuevo diccionario
     * @param nombre Nombre del diccionario
     * @param path Ruta del diccionario
     */

    public void crearDiccionario(String nombre, String path) {
        try {
            ctrlDomain.crearDiccionario(nombre, path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void eliminarDiccionario(String nombre) {
        try {
            ctrlDomain.eliminarDiccionario(nombre);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void modificarPalabraDiccionario(String nombreDiccionario, String palabra, boolean add) {
        try {
            ctrlDomain.modificarPalabraDiccionario(nombreDiccionario, palabra, add);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Obtiene el los carácteres y sus correspondientes valores del diccionario actual
     * @param nombre Nombre del diccionario actual en partida
     * @return Un mapa que contiene los caráteres y sus valores
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
     * Devuelve una lista con todas las palabras del diccionario.
     * @param  dic Nombre de diccionario
     * @return Lista de palabras.
     */

    public List<String> getListaPalabras(String dic) {
        return ctrlDomain.getListaPalabras(dic);
    }

    /**
     * Devuelve una lista de letras con su puntuación y frecuencia en formato: "letra puntuacion frecuencia" del diccionario.
     * @param  dic Nombre de diccionario
     * @return Lista de letras
     */

    public List<String> getListaAlfabeto(String dic) {
        return ctrlDomain.getListaAlfabeto(dic);
    }

    /**
     * Devuelve una lista de jugadores en el ranking
     * @return Lista de nombres de jugadores en el ranking
     */

    public List<String> getUsuariosRanking() {
        return ctrlDomain.getUsuariosRanking();
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
        return ctrlDomain.eliminarUsuario(nombre);
    }

    /**
     * Devuelve la puntuación máxima de un jugador
     * @param nombre Nombre del jugador
     * @return Puntuación máxima del jugador
     */

    public int getPuntuacionMaxima(String nombre) {
        return ctrlDomain.getPuntuacionMaxima(nombre);
    }

    /**
     * Devuelve la puntuación media de un jugador
     * @param nombre Nombre del jugador
     * @return Puntuación media del jugador
     */

    public double getPuntuacionMedia(String nombre) {
        return ctrlDomain.getPuntuacionMedia(nombre);
    }

    /**
     * Devuelve el número de partidas jugadas por un jugador
     * @param nombre Nombre del jugador
     * @return Número de partidas jugadas por el jugador
     */

    public int getPartidasJugadas(String nombre) {
        return ctrlDomain.getPartidasJugadas(nombre);
    }

    /**
     * Devuelve el número de victorias de un jugador
     * @param nombre Nombre del jugador
     * @return Número de victorias del jugador
     */

    public int getvictorias(String nombre) {
        return ctrlDomain.getVictorias(nombre);
    }

    /**
     * Devuelve la puntuación total de un jugador
     * @param nombre Nombre del jugador
     * @return Puntuación total del jugador
     */

    public int getPuntuacionTotal(String nombre) {
        return ctrlDomain.getPuntuacionTotal(nombre);
    }

    public List<Integer> getPuntuacionesUsuario(String nombre) {
        return ctrlDomain.getPuntuacionesUsuario(nombre);
    }

    /**
     * Guarda la configuración general de la aplicación en un archivo de propiedades.
     * 
     * @param tema             El tema visual seleccionado por el usuario.
     * @param musicaActivada   Indica si la música está activada (true) o desactivada (false).
     * @param sonidoActivado   Indica si los efectos de sonido están activados (true) o desactivados (false).
     * @param volumenMusica    El volumen establecido de la música.
     * @param volumenSonido    El volumen establecido del sonido.
     * 
     * Este método almacena los valores proporcionados en un archivo de configuración,
     * sobrescribiendo cualquier configuración previa. Si ocurre un error de entrada/salida
     * durante el proceso de guardado, se imprime la traza de la excepción.
     */

    public void guardarConfiguracionGeneral( String tema, boolean musicaActivada, boolean sonidoActivado, int volumenMusica, int volumenSonido) {
        ctrlDomain.guardarConfiguracionGeneral( tema, musicaActivada, sonidoActivado, volumenMusica, volumenSonido);
    }

    /**
     * Carga la configuración desde un archivo de propiedades especificado por CONFIG_FILE.
     * Si el archivo no se puede cargar, se utilizarán valores por defecto.
     *
     * @return Un mapa que contiene las claves y valores de configuración cargados.
     */

    public Map<String, String>  cargarConfiguracion() {
        return ctrlDomain.cargarConfiguracion();
    }

    /**
    * Método centralizado para mostrar alertas en todo el controlador
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