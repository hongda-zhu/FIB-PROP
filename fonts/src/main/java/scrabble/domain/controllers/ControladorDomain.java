package scrabble.domain.controllers;

import java.io.IOException;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import scrabble.domain.controllers.subcontrollers.ControladorConfiguracion;
import scrabble.domain.controllers.subcontrollers.ControladorDiccionario;
import scrabble.domain.controllers.subcontrollers.ControladorJuego;
import scrabble.domain.controllers.subcontrollers.ControladorJuego.Direction;
import scrabble.domain.controllers.subcontrollers.ControladorJugador;
import scrabble.domain.controllers.subcontrollers.ControladorRanking;
import scrabble.excepciones.*;
import scrabble.helpers.Dificultad;
import scrabble.helpers.Triple;
import scrabble.helpers.Tuple;

/**
* Constructor del controlador del dominio.
* Inicializa todos los subcontroladores necesarios.
*/
public class ControladorDomain {
    private ControladorConfiguracion controladorConfiguracion;
    private ControladorJuego controladorJuego;
    private ControladorRanking controladorRanking;
    private ControladorJugador controladorJugador;
    private ControladorDiccionario controladorDiccionario;

    public ControladorDomain() {
        this.controladorConfiguracion = new ControladorConfiguracion();
        this.controladorJuego = new ControladorJuego();
        this.controladorRanking = ControladorRanking.getInstance();
        this.controladorJugador = ControladorJugador.getInstance();
        this.controladorDiccionario = ControladorDiccionario.getInstance();
    }

    // METODOS DE USUARIOS
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
    public void crearJugadorIA(Dificultad dificultad, String nombre) {
        controladorJugador.registrarJugadorIA(nombre, dificultad);
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
    * Añade un nuevo lenguaje al sistema a partir de los archivos en las rutas proporcionadas.
    *
    * @param nombre nombre del nuevo lenguaje.
    * @param rutaArchivoAlpha ruta del archivo con el alfabeto del lenguaje.
    * @param rutaArchivoWords ruta del archivo con las palabras válidas del lenguaje.
    * @throws ExceptionDiccionarioExist si el lenguaje ya existe en el sistema.
    * @throws IOException Si hay problemas al leer los archivos.
    * @throws ExceptionPalabraInvalida Si las palabras contienen caracteres no válidos.
    */
    public void anadirLenguaje(String nombre, String rutaArchivoAlpha, String rutaArchivoWords) throws IOException, ExceptionDiccionarioExist, ExceptionPalabraInvalida {
        if (existeLenguaje(nombre)) {
            throw new ExceptionDiccionarioExist();
        }
        // Delegar al controlador de diccionarios
        controladorDiccionario.crearDiccionario(nombre, rutaArchivoAlpha, rutaArchivoWords);
    }

    /**
    * Verifica si existe un lenguaje en el sistema con el nombre dado.
    *
    * @param nombre nombre del lenguaje a comprobar.
    * @return true si el lenguaje ya existe; false en caso contrario.
    */
    public boolean existeLenguaje(String nombre) {
        return controladorDiccionario.existeDiccionario(nombre);
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
    public Tuple<Map<String, Integer>, Integer> realizarTurno(Triple<String,Tuple<Integer, Integer>, Direction> move, String nombreJugador) {
        Map<String, Integer> rack = controladorJugador.getRack(nombreJugador);
        boolean esIA = controladorJugador.esIA(nombreJugador);
        Dificultad dificultad = getNivelDificultad(nombreJugador);
        return (move != null && move.x == "P" && !esIA)? null: controladorJuego.realizarTurno(move, nombreJugador, rack, esIA, dificultad);
    }

    public void managePartidaIniciar(String idiomaSeleccionado, Set<String> jugadoresSeleccionados, Integer N) throws IOException{
        
        iniciarPartida(jugadoresSeleccionados, idiomaSeleccionado, N);

        // Inicializar racks para todos los jugadores
        for (String jugador : jugadoresSeleccionados) {
            Map<String, Integer> rack = controladorJuego.cogerFichas(7);
            inicializarRack(jugador, rack);
        }
        
        // Inicializar a los jugadores para la partida (marcando que están en partida)
        List<String> listaJugadores = new ArrayList<>(jugadoresSeleccionados);
        inicializarJugadoresPartida(listaJugadores);
    }

    public void realizarTurnoPartida(String nombreJugador, Triple<String, Tuple<Integer, Integer>, Direction> jugada) {
        
            Tuple<Map<String, Integer>, Integer> result = realizarTurno(jugada, nombreJugador);
            if (result == null) {
                addSkipTrack(nombreJugador);
            } else {
                inicializarRack(nombreJugador, result.x);
                addPuntuacion(nombreJugador, result.y);
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
            }               
    }

    public void comprobarFinPartida(Set<String> jugadoresSeleccionados) {
        boolean allskiped = true;

        for (String entry : jugadoresSeleccionados) {
            String nombreJugador = entry;
            if (getSkipTrack(nombreJugador) < 3) {
                allskiped = false;
                break;
            }
        }
        if (allskiped) {
            System.out.println("Los jugadores han pasado mas de 2 veces consecutivas. El juego ha terminado.");
            controladorJuego.finalizarJuego();
        }
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
    public void iniciarPartida(Set<String> jugadoresSeleccionados, String nombreDiccionario, int N) {
    
        controladorJuego.inicializarJuego(N, jugadoresSeleccionados, nombreDiccionario);
        
        // Inicializar racks para todos los jugadores
        for (String jugador : jugadoresSeleccionados) {
            Map<String, Integer> rack = controladorJuego.cogerFichas(7);
            inicializarRack(jugador, rack);
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
    public String finalizarJuego(Set<String> jugadoresSeleccionados) {

        Map<String, Integer> puntuacionesFinales = new HashMap<>();
        
        for (String jugador : jugadoresSeleccionados) {
            controladorJugador.clearSkipTrack(jugador);
        }

         // Recopilar las puntuaciones finales
         for (String nombreJugador : jugadoresSeleccionados) {
            puntuacionesFinales.put(nombreJugador, getPuntuacion(nombreJugador));
        }
        
        // Determinar el ganador o ganadores (en caso de empate)
        int maxPuntuacion = -1;
        List<String> ganadores = new ArrayList<>();
        
        // Primero encontramos la puntuación máxima
        for (Map.Entry<String, Integer> entry : puntuacionesFinales.entrySet()) {
            if (entry.getValue() > maxPuntuacion) {
                maxPuntuacion = entry.getValue();
            }
        }
        
        // Luego identificamos todos los jugadores con esa puntuación máxima (pueden ser varios en caso de empate)
        for (Map.Entry<String, Integer> entry : puntuacionesFinales.entrySet()) {
            if (entry.getValue() == maxPuntuacion) {
                ganadores.add(entry.getKey());
            }
        }
        
        // Ahora actualizamos las estadísticas para todos los jugadores, marcando múltiples ganadores si es necesario
        finalizarPartidaJugadoresMultiple(puntuacionesFinales, ganadores);
        
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
        for (Map.Entry<String, Integer> entry : puntuacionesFinales.entrySet()) {
            mensajeGanadores.append(String.format("  %-20s : %4d puntos\n", entry.getKey(), entry.getValue()));
        }
        mensajeGanadores.append("+--------------------------------------+\n");

        
        controladorJuego.reiniciarJuego();
        
        for (String nombreJugador : jugadoresSeleccionados) {
            controladorJugador.clearSkipTrack(nombreJugador);
        }
        
        return mensajeGanadores.toString();
    }

    /**
     * Verifica si el juego actual ha terminado.
     * @return true si el juego ha finalizado
     */
    public boolean isJuegoTerminado() {
        return controladorJuego.isJuegoTerminado();
    }

    // METODOS DE RANKING

    /**
     * Obtiene la lista de usuarios ordenados según la estrategia actual.
     * @return Lista de usuarios ordenados
     */
    public List<String> getRanking() {
        return controladorRanking.getRanking();
    }

    /**
     * Obtiene la lista de usuarios ordenados según un criterio específico.
     * @param criterio Criterio de ordenación
     * @return Lista de usuarios ordenados
     */
    public List<String> getRanking(String criterio) {
        return controladorRanking.getRanking(criterio);
    }

    /**
     * Obtiene el nombre de la estrategia actual de ordenación.
     * @return Nombre de la estrategia
     */
    public String getEstrategiaRanking() {
        return controladorRanking.getEstrategiaActual();
    }

    /**
     * Obtiene el criterio actual de ordenación.
     * @return Criterio actual (maxima, media, partidas, victorias)
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
     * @param criterio Criterio de ordenación
     */
    public void cambiarEstrategiaRanking(String criterio) {
        controladorRanking.setEstrategia(criterio);
    }

    /**
     * Obtiene la puntuación máxima de un usuario específico.
     * @param nombre Nombre del usuario
     * @return Puntuación máxima
     */
    public int getPuntuacionMaxima(String nombre) {
        return controladorRanking.getPuntuacionMaxima(nombre);
    }

    /**
     * Obtiene la puntuación media de un usuario específico.
     * @param nombre Nombre del usuario
     * @return Puntuación media
     */
    public double getPuntuacionMedia(String nombre) {
        return controladorRanking.getPuntuacionMedia(nombre);
    }

    /**
     * Obtiene el número de partidas jugadas por un usuario específico.
     * @param nombre Nombre del usuario
     * @return Número de partidas jugadas
     */
    public int getPartidasJugadas(String nombre) {
        return controladorRanking.getPartidasJugadas(nombre);
    }

    /**
     * Obtiene el número de victorias de un usuario específico.
     * @param nombre Nombre del usuario
     * @return Número de victorias
     */
    public int getVictorias(String nombre) {
        return controladorRanking.getVictorias(nombre);
    }

    /**
     * Obtiene la puntuación total acumulada de un usuario específico.
     * @param nombre Nombre del usuario
     * @return Puntuación total acumulada
     */
    public int getPuntuacionTotal(String nombre) {
        return controladorRanking.getPuntuacionTotal(nombre);
    }

    /**
     * Obtiene todas las puntuaciones de un usuario específico.
     * @param nombre Nombre del usuario
     * @return Lista de puntuaciones
     */
    public List<Integer> getPuntuacionesUsuario(String nombre) {
        return controladorRanking.getPuntuacionesUsuario(nombre);
    }

    /**
     * Verifica si un usuario está en el ranking.
     * @param nombre Nombre del usuario
     * @return true si el usuario está en el ranking, false en caso contrario
     */
    public boolean perteneceRanking(String nombre) {
        return controladorRanking.perteneceRanking(nombre);
    }

    /**
     * Guarda los datos del ranking.
     */
    public void guardarRanking() {
        controladorRanking.guardarDatos();
    }

    /**
     * Obtiene la lista de todos los usuarios en el ranking.
     * @return Lista de usuarios
     */
    public List<String> getUsuarios() {
        return controladorRanking.getUsuarios();
    }

    /**
     * Agrega una puntuación a un usuario.
     * @param nombre Nombre del usuario
     * @param puntuacion Puntuación a agregar
     * @return true si se agregó correctamente, false en caso contrario
     */
    public boolean agregarPuntuacion(String nombre, int puntuacion) {
        return controladorRanking.agregarPuntuacion(nombre, puntuacion);
    }

    /**
     * Elimina una puntuación de un usuario.
     * @param nombre Nombre del usuario
     * @param puntuacion Puntuación a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     */
    public boolean eliminarPuntuacion(String nombre, int puntuacion) {
        return controladorRanking.eliminarPuntuacion(nombre, puntuacion);
    }

    /**
     * Actualiza las estadísticas de victoria de un usuario.
     * @param nombre Nombre del usuario
     * @param esVictoria true si el usuario ganó la partida
     * @return true si se actualizó correctamente, false en caso contrario
     */
    public boolean actualizarEstadisticasUsuario(String nombre, boolean esVictoria) {
        return controladorRanking.actualizarEstadisticasUsuario(nombre, esVictoria);
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
        boolean eliminado = controladorRanking.eliminarUsuario(nombre);
        
        // Si se eliminó correctamente del ranking y existe como jugador humano,
        // sincronizar su puntuación total con la información del ranking (0 después de eliminar)
        if (eliminado && controladorJugador.existeJugador(nombre) && !controladorJugador.esIA(nombre)) {
            // Establecer la puntuación total del jugador a 0 o a la puntuación que tenía en el ranking
            controladorJugador.setPuntuacionTotal(nombre, 0);
            
            // También podríamos considerar establecer la puntuación actual a 0
            controladorJugador.setPuntuacion(nombre, 0);
        }
        
        return eliminado;
    }

    // Metodos de Jugadores

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
     * Obtiene el nivel de dificultad de un jugador IA.
     * 
     * @param nombre Nombre del jugador
     * @return Dificultad del jugador IA o null si no es una IA
     */
    public Dificultad getNivelDificultad(String nombre) {
        return controladorJugador.getNivelDificultad(nombre);
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

    /**
     * Método para inicializar a los jugadores al comenzar una partida.
     * Marca a los jugadores humanos como "en partida" y reinicia puntuaciones.
     *
     * @param jugadores Lista de nombres de jugadores en la partida
     */
    public void inicializarJugadoresPartida(List<String> jugadores) {
        ControladorJugador controladorJugador = ControladorJugador.getInstance();
        
        for (String nombre : jugadores) {
            // Solo aplicamos esto a jugadores humanos
            if (!controladorJugador.esIA(nombre)) {
                controladorJugador.setEnPartida(nombre, true);
                controladorJugador.setPuntuacion(nombre, 0);
            }
        }
    }

    /**
     * Método para actualizar estadísticas de jugadores al finalizar una partida.
     * Actualiza puntuaciones, incrementa contadores y marca a los jugadores como "fuera de partida".
     *
     * @param puntuacionesFinales Mapa con nombres de jugadores y sus puntuaciones finales
     * @param ganador Nombre del jugador ganador
     */
    public void finalizarPartidaJugadores(Map<String, Integer> puntuacionesFinales, String ganador) {
        List<String> ganadores = new ArrayList<>();
        ganadores.add(ganador);
        finalizarPartidaJugadoresMultiple(puntuacionesFinales, ganadores);
    }
    
    /**
     * Método para actualizar estadísticas de jugadores al finalizar una partida con posibilidad de múltiples ganadores.
     * Actualiza puntuaciones, incrementa contadores y marca a los jugadores como "fuera de partida".
     *
     * @param puntuacionesFinales Mapa con nombres de jugadores y sus puntuaciones finales
     * @param ganadores Lista de nombres de los jugadores ganadores (para empates)
     */
    public void finalizarPartidaJugadoresMultiple(Map<String, Integer> puntuacionesFinales, List<String> ganadores) {
        ControladorJugador controladorJugador = ControladorJugador.getInstance();
        
        for (Map.Entry<String, Integer> entry : puntuacionesFinales.entrySet()) {
            String nombre = entry.getKey();
            int puntuacion = entry.getValue();
            
            // Solo procesamos jugadores humanos
            if (!controladorJugador.esIA(nombre)) {
                controladorJugador.setEnPartida(nombre, false);
                controladorJugador.setPuntuacion(nombre, puntuacion);
                controladorJugador.incrementarPartidasJugadas(nombre);
                
                // Actualizamos la puntuación total acumulada
                controladorJugador.addPuntuacionTotal(nombre, puntuacion);
                
                
                // Si está en la lista de ganadores, incrementamos las partidas ganadas
                if (ganadores.contains(nombre)) {
                    controladorJugador.incrementarPartidasGanadas(nombre);
                }
            }
        }
    }

    /**
     * Obtiene la puntuación total acumulada de un jugador humano.
     * 
     * @param nombre Nombre del jugador
     * @return La puntuación total acumulada, o 0 si el jugador no es humano o no existe
     */
    public int getPuntuacionTotalDirecta(String nombre) {
        return controladorJugador.getPuntuacionTotal(nombre);
    }
    
    /**
     * Establece la puntuación total acumulada de un jugador humano.
     * 
     * @param nombre Nombre del jugador
     * @param puntuacionTotal La nueva puntuación total
     * @return true si se actualizó correctamente, false en caso contrario
     */
    public boolean setPuntuacionTotal(String nombre, int puntuacionTotal) {
        return controladorJugador.setPuntuacionTotal(nombre, puntuacionTotal);
    }
    
    /**
     * Añade puntos a la puntuación total acumulada de un jugador humano.
     * 
     * @param nombre Nombre del jugador
     * @param puntos Los puntos a añadir
     * @return true si se añadieron correctamente, false en caso contrario
     */
    public boolean addPuntuacionTotal(String nombre, int puntos) {
        return controladorJugador.addPuntuacionTotal(nombre, puntos);
    }

    // --- MÉTODOS DE DICCIONARIO ---
    
    /**
     * Delega la creación de un diccionario al ControladorDiccionario.
     * 
     * @param nombre Nombre del diccionario
     * @param path Ruta al directorio del diccionario
     * @throws ExceptionDiccionarioExist Si ya existe un diccionario con ese nombre
     * @throws IOException Si hay problemas con los archivos
     * @throws ExceptionPalabraInvalida Si las palabras contienen caracteres no válidos.
     */
    public void crearDiccionario(String nombre, String path) throws ExceptionDiccionarioExist, IOException, ExceptionPalabraInvalida {
        controladorDiccionario.crearDiccionario(nombre, path);
    }
    
    /**
     * Delega la eliminación de un diccionario al ControladorDiccionario.
     * 
     * @param nombre Nombre del diccionario a eliminar
     * @throws ExceptionDiccionarioNotExist Si el diccionario no existe
     * @throws IOException Si hay problemas eliminando los archivos
     */
    public void eliminarDiccionario(String nombre) throws ExceptionDiccionarioNotExist, IOException {
        controladorDiccionario.eliminarDiccionario(nombre);
    }
    
    /**
     * Verifica si existe un diccionario con el nombre especificado.
     * 
     * @param nombre Nombre del diccionario
     * @return true si existe, false en caso contrario
     */
    public boolean existeDiccionario(String nombre) {
        return controladorDiccionario.existeDiccionario(nombre);
    }
    
    /**
     * Obtiene la lista de nombres de diccionarios disponibles.
     * 
     * @return Lista de nombres de diccionarios
     */
    public List<String> getDiccionariosDisponibles() {
        return controladorDiccionario.getDiccionariosDisponibles();
    }
    
    /**
     * Modifica un diccionario añadiendo o eliminando una palabra.
     * 
     * @param nombre Nombre del diccionario
     * @param palabra Palabra a añadir o eliminar
     * @param anadir true para añadir, false para eliminar
     * @throws ExceptionDiccionarioNotExist Si el diccionario no existe
     * @throws ExceptionPalabraVacia Si la palabra está vacía
     * @throws ExceptionPalabraInvalida Si la palabra contiene caracteres no válidos
     * @throws ExceptionPalabraExist Si la palabra ya existe (al añadir)
     * @throws ExceptionPalabraNotExist Si la palabra no existe (al eliminar)
     * @throws IOException Si hay problemas con los archivos
     */
    public void modificarPalabraDiccionario(String nombre, String palabra, boolean anadir) 
            throws ExceptionDiccionarioNotExist, ExceptionPalabraVacia, ExceptionPalabraInvalida, 
                  ExceptionPalabraExist, ExceptionPalabraNotExist, IOException {
        controladorDiccionario.modificarPalabraDiccionario(nombre, palabra, anadir);
    }

    /**
     * Modifica una palabra existente en el diccionario por otra nueva.
     * 
     * @param nombre Nombre del diccionario
     * @param palabraOriginal Palabra a modificar
     * @param palabraNueva Nueva palabra que reemplazará a la original
     * @throws ExceptionDiccionarioNotExist Si el diccionario no existe
     * @throws ExceptionPalabraVacia Si alguna de las palabras está vacía
     * @throws ExceptionPalabraInvalida Si la nueva palabra contiene caracteres no válidos
     * @throws ExceptionPalabraNotExist Si la palabra original no existe
     * @throws ExceptionPalabraExist Si la nueva palabra ya existe en el diccionario
     * @throws IOException Si hay problemas con los archivos
     */
    public void modificarPalabraEnDiccionario(String nombre, String palabraOriginal, String palabraNueva) 
            throws ExceptionDiccionarioNotExist, ExceptionPalabraVacia, ExceptionPalabraInvalida, 
                   ExceptionPalabraNotExist, ExceptionPalabraExist, IOException {
        controladorDiccionario.modificarPalabra(nombre, palabraOriginal, palabraNueva);
    }

    /**
     * Obtiene el conjunto de caracteres válidos del alfabeto de uzn diccionario.
     * 
     * @param nombreDiccionario Nombre del diccionario
     * @return Conjunto de caracteres válidos, o conjunto vacío si hay error
     */
    public Set<Character> getCaracteresAlfabeto(String nombreDiccionario) {
        try {
            return controladorDiccionario.getCaracteresAlfabeto(nombreDiccionario);
        } catch (Exception e) {
            // Si ocurre cualquier error, devolvemos un conjunto vacío
            System.err.println("Error al obtener caracteres del alfabeto: " + e.getMessage());
            return new HashSet<>();
        }
    }

    /**
     * Verifica si una palabra existe en el diccionario.
     * 
     * @param nombreDiccionario Nombre del diccionario
     * @param palabra Palabra a verificar
     * @return true si la palabra existe en el diccionario, false en caso contrario
     */
    public boolean existePalabra(String nombreDiccionario, String palabra) {
        try {
            return controladorDiccionario.existePalabra(nombreDiccionario, palabra);
        } catch (Exception e) {
            System.err.println("Error al verificar palabra: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Verifica si un diccionario configurado sigue siendo válido (sus archivos existen).
     * 
     * @param nombreDiccionario Nombre del diccionario a verificar
     * @return true si el diccionario es válido, false si falta algún archivo necesario
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
     * @param rutaDirectorio Ruta del directorio a verificar
     * @return true si el directorio contiene un diccionario válido
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
            System.err.println("Error al verificar directorio: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verifica si un carácter es un comodín en el diccionario especificado.
     * 
     * @param nombreDiccionario Nombre del diccionario
     * @param caracter Carácter a verificar
     * @return true si es un comodín, false en caso contrario
     */
    public boolean esComodin(String nombreDiccionario, String caracter) {
        try {
            if (!existeDiccionario(nombreDiccionario)) {
                return false;
            }
            return controladorDiccionario.esComodin(nombreDiccionario, caracter);
        } catch (Exception e) {
            System.err.println("Error al verificar comodín: " + e.getMessage());
            return false;
        }
    }

    public boolean isValidMove (Triple<String,Tuple<Integer, Integer>, Direction> move, Map<String, Integer> rack) {
        return controladorJuego.isJuegoIniciado()?controladorJuego.isValidMove(move, rack):controladorJuego.isValidFirstMove(move, rack);
    }


    /**
     * Muestra el rack del jugador de forma ordenada y legible.
     * 
     * @param jugador Nombre del jugador
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

    public Map<String, Integer> getRack(String nombre) {
        return controladorJugador.getRack(nombre);
    }

    public String mostrarStatusPartida(String nombreJugador) {
        return controladorJuego.mostrarStatusPartida(nombreJugador);
    }

    public void guardarPartida(String nombrePartida) {
        controladorJuego.guardar(nombrePartida);
    }
    public void cargarPartida(String nombrePartida) {
        controladorJuego.cargarDesdeArchivo(nombrePartida);
    }
    public List<String> getPartidasGuardadas() {
        return controladorJuego.listarArchivosGuardados();
    }
    public void eliminarPartidaGuardada(String nombrePartida) {
        controladorJuego.eliminarArchivoGuardado(nombrePartida);
    }

    public Set<String> getJugadoresActuales() {
        return controladorJuego.getJugadoresActuales();
    }

    /**
     * Ordena una lista de usuarios según el criterio de ranking especificado.
     * Este método encapsula la lógica de ordenación para evitar tenerla en la capa de presentación.
     * Delega en el ControladorRanking, que utiliza las estrategias definidas.
     * 
     * @param usuarios Lista de usuarios a ordenar
     * @param criterio Criterio de ordenación ("maxima", "media", "partidas", "victorias")
     * @return Nueva lista ordenada de usuarios
     */
    public List<String> ordenarUsuariosPorCriterio(List<String> usuarios, String criterio) {
        // Delegamos en el ControladorRanking para mantener la separación de responsabilidades
        return controladorRanking.ordenarUsuariosPorCriterio(usuarios, criterio);
    }
}