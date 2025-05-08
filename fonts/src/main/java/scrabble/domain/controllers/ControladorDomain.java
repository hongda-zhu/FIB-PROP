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
import scrabble.domain.persistences.interfaces.RepositorioConfiguracion;
import scrabble.domain.persistences.implementaciones.RepositorioConfiguracionImpl;
import scrabble.excepciones.*;
import scrabble.helpers.Dificultad;
import scrabble.helpers.Triple;
import scrabble.helpers.Tuple;

/**
* Controlador principal del dominio que actúa como fachada para los subcontroladores.
* Coordina la interacción entre los diferentes componentes del sistema.
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
    * Inicializa todos los subcontroladores necesarios.
    * 
    * @pre No hay precondiciones específicas.
    * @post Se inicializan todos los subcontroladores y el sistema queda listo para su uso.
    */
    public ControladorDomain() {
        try {
            // Creamos la implementación del repositorio y se la pasamos al controlador
            RepositorioConfiguracion repoConfiguracion = new RepositorioConfiguracionImpl();
            this.controladorConfiguracion = new ControladorConfiguracion(repoConfiguracion);
            this.controladorJuego = new ControladorJuego();
            this.controladorRanking = ControladorRanking.getInstance();
            this.controladorJugador = ControladorJugador.getInstance();
            this.controladorDiccionario = ControladorDiccionario.getInstance();
        } catch (Exception e) {
            System.err.println("Error al inicializar el controlador de dominio: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
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
     * @param nombre nombre del jugador
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
     * Registra un nuevo usuario humano.
     * 
     * @pre El nombre no debe ser null y debe ser único (excepto para "admin").
     * @param nombre nombre de usuario
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
     * @param nombre nombre de usuario
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
     * @return String con información detallada sobre todos los usuarios registrados.
     * @post Se devuelve una cadena de texto con información detallada sobre todos los usuarios registrados.
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
     * Establece el idioma de la aplicación.
     * 
     * @param idioma El idioma a establecer
     * @throws ExceptionPersistenciaFallida si ocurre un error durante la persistencia
     */
    public void setIdioma(String idioma) throws ExceptionPersistenciaFallida {
        controladorConfiguracion.setIdioma(idioma);
    }

    /**
     * Establece el tema visual de la aplicación.
     * 
     * @param tema El tema a establecer
     * @throws ExceptionPersistenciaFallida si ocurre un error durante la persistencia
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
    public void setVolumen(int volumen) throws ExceptionPersistenciaFallida {
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
        try {
            controladorDiccionario.crearDiccionario(nombre, rutaArchivoAlpha, rutaArchivoWords);
        } catch (ExceptionDiccionarioOperacionFallida e) {
            throw new ExceptionLoggingOperacion("Error al añadir el lenguaje: " + e.getMessage(), "creación", true);
        }
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
        return (move.x == "P" || move.x == "CF")? null: controladorJuego.realizarTurno(move, nombreJugador, rack, esIA, dificultad);
    }

    public void managePartidaIniciar(String idiomaSeleccionado, Map<String, Integer> jugadoresSeleccionados, Integer N) throws IOException{
        
        iniciarPartida(jugadoresSeleccionados, idiomaSeleccionado, N);
        // Inicializar a los jugadores para la partida (marcando que están en partida)
        List<String> listaJugadores = new ArrayList<>(jugadoresSeleccionados.keySet());
        inicializarJugadoresPartida(listaJugadores);
    }

    public int realizarTurnoPartida (String nombreJugador, Triple<String, Tuple<Integer, Integer>, Direction> jugada) {
        
        Tuple<Map<String, Integer>, Integer> result = realizarTurno(jugada, nombreJugador);
        if (result == null) {
            addSkipTrack(nombreJugador);
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

    public void comprobarFinPartida(Map<String, Integer> jugadoresSeleccionados) {
        boolean allskiped = true;

        for (String entry : jugadoresSeleccionados.keySet()) {
            String nombreJugador = entry;
            if (getSkipTrack(nombreJugador) < 3) {
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
     * Intercambia fichas del rack del jugador por nuevas fichas de la bolsa.
     * @param nombre nombre del jugador
     * @param letras lista de letras a intercambiar
     * @return true si el intercambio es exitoso, false si no hay suficientes fichas en la bolsa
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
    public void iniciarPartida(Map<String, Integer> jugadoresSeleccionados, String nombreDiccionario, int N) {
    
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
     * Devuelve la cantidad de fichas restantes en el pool del juego.
     * @return cantidad de fichas disponibles
     */
    public int getCantidadFichasRestantes() {
        return controladorJuego.getCantidadFichas();
    }

    /**
     * Finaliza el juego actual y realiza tareas de limpieza si es necesario.
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
     * Actualiza las puntuaciones de los jugadores seleccionados en el ranking.
     * @param jugadoresSeleccionados Mapa de jugadores y sus puntuaciones
     */
    public void actualizarJugadores(String nombre, int puntuacion) {
        controladorJuego.actualizarPuntuaciones(nombre, puntuacion);
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
    public List<String> getUsuariosRanking() {
        return controladorRanking.getUsuarios();
    }

    public List<String> getUsuariosHumanos() {
        return controladorJugador.getJugadoresHumanos();
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
                
            }
        }
    }

    /**
     * Método para actualizar estadísticas de jugadores al finalizar una partida con posibilidad de múltiples ganadores.
     * Actualiza la puntuación de la partida actual y marca a los jugadores como "fuera de partida".
     * Las estadísticas históricas se actualizan en el ControladorRanking.
     * También limpia el nombre de la partida actual para cada jugador.
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
     * Obtiene la puntuación total acumulada de un jugador humano.
     * Ahora obtiene este dato directamente del ranking.
     * 
     * @param nombre Nombre del jugador
     * @return La puntuación total acumulada, o 0 si el jugador no es humano o no existe
     */
    public int getPuntuacionTotalDirecta(String nombre) {
        // Obtener puntuación directamente del ranking
        return controladorRanking.getPuntuacionTotal(nombre);
    }
    
    /**
     * Establece la puntuación total de un jugador, eliminando todas sus puntuaciones anteriores.
     * 
     * @param nombreUsuario Nombre del jugador
     * @param puntuacionTotal Nueva puntuación total a establecer
     * @return true si se estableció correctamente, false en caso contrario
     * @pre El usuario debe existir en el sistema.
     * @post Si el usuario existe, se eliminan todas sus puntuaciones anteriores del ranking, se establece
     *       la nueva puntuación total y se guardan los datos. Si el usuario no existe o hay algún error,
     *       se devuelve false.
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
     * @param nombre Nombre del jugador
     * @param puntos Los puntos a añadir
     * @return true si se añadieron correctamente, false en caso contrario
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
     * @param nombre Nombre del diccionario
     * @param path Ruta al directorio del diccionario
     * @throws ExceptionDiccionarioExist Si ya existe un diccionario con ese nombre
     * @throws IOException Si hay problemas con los archivos
     * @throws ExceptionPalabraInvalida Si las palabras contienen caracteres no válidos.
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
     * @param nombre Nombre del diccionario a eliminar
     * @throws ExceptionDiccionarioNotExist Si el diccionario no existe
     * @throws IOException Si hay problemas eliminando los archivos
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
     * Valida que la palabra pueda formarse usando únicamente los tokens existentes en el alfabeto. Por ejemplo,
     * si el alfabeto solo contiene "CC", solo podrán añadirse palabras compuestas de múltiplos de "CC" como "CC", "CCCC", etc.
     * 
     * @param nombre Nombre del diccionario
     * @param palabra Palabra a modificar
     * @param anadir true para añadir, false para eliminar
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
     * Modifica una palabra existente en un diccionario.
     * Valida que la nueva palabra pueda formarse usando únicamente los tokens existentes en el alfabeto.
     * 
     * @param nombre Nombre del diccionario
     * @param palabraOriginal Palabra original a modificar
     * @param palabraNueva Nueva palabra que reemplazará a la original
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
     * @param nombreDiccionario Nombre del diccionario
     * @return Conjunto de caracteres válidos
     * @throws ExceptionDiccionarioNotExist Si el diccionario no existe
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
     * @param nombreDiccionario Nombre del diccionario
     * @return Conjunto de tokens del alfabeto (ejemplo: A, B, CH, RR)
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
     * @param nombreDiccionario Nombre del diccionario
     * @param palabra Palabra a verificar
     * @return true si la palabra existe en el diccionario, false en caso contrario
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
            throw new ExceptionLoggingOperacion("Error al verificar directorio: " + e.getMessage(), "validación", true);
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
            throw new ExceptionLoggingOperacion("Error al verificar comodín: " + e.getMessage(), "consulta", true);
        }
    }

    public boolean isValidMove (Triple<String,Tuple<Integer, Integer>, Direction> move, Map<String, Integer> rack) {
        return controladorJuego.isJuegoIniciado()?controladorJuego.isValidMove(move, rack):controladorJuego.isValidFirstMove(move, rack);
    }


    /**
     * Muestra el rack del jugador de forma ordenada y legible.
     * 
     * @param jugador Nombre del jugador
     * @return String con la información del rack
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

    public boolean guardarPartida() {
        return controladorJuego.guardar();
    }
    public void cargarPartida(Integer nombrePartida) {
        controladorJuego.cargarDesdeArchivo(nombrePartida);
    }
    public List<Integer> getPartidasGuardadas() {
        return ControladorJuego.listarArchivosGuardados();
    }
    public boolean eliminarPartidaGuardada(Integer nombrePartida) {
        Map<String, Integer> jugadores = ControladorJuego.getJugadoresPorId(nombrePartida);
        for (String jugador : jugadores.keySet()) {
            if (!controladorJugador.esIA(jugador)) {
                controladorJugador.setEnPartida(jugador, false);
                controladorJugador.setNombrePartidaActual(jugador, "");
            }
        }
        return ControladorJuego.eliminarArchivoGuardado(nombrePartida);
    }

    public void aliberarJugadoresActuales() {
        Map<String, Integer> jugadoresSeleccionados = controladorJuego.getJugadoresActuales();
        for (String jugador :jugadoresSeleccionados.keySet()) {
            controladorJugador.setEnPartida(jugador, false);
            controladorJugador.setNombrePartidaActual(jugador, "");
        }
    }

    public Map<String, Integer> getJugadoresActuales() {
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

    /**
     * Obtiene el número de partidas jugadas por un jugador humano directamente del ranking.
     * 
     * @param nombre Nombre del jugador
     * @return Número de partidas jugadas o 0 si el jugador no existe o es IA
     */
    public int getPartidasJugadasDirecta(String nombre) {
        return controladorRanking.getPartidasJugadas(nombre);
    }
    
    /**
     * Obtiene el número de partidas ganadas por un jugador humano directamente del ranking.
     * 
     * @param nombre Nombre del jugador
     * @return Número de partidas ganadas o 0 si el jugador no existe o es IA
     */
    public int getPartidasGanadasDirecta(String nombre) {
        return controladorRanking.getVictorias(nombre);
    }

    /**
     * Método para actualizar estadísticas de jugadores al finalizar una partida.
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
     * Obtiene el nombre de la partida actual en la que está participando un jugador.
     * 
     * @param nombre Nombre del jugador
     * @return Nombre de la partida actual o cadena vacía si no está en partida o es una IA
     */
    public String getNombrePartidaActual(String nombre) {
        return controladorJugador.getNombrePartidaActual(nombre);
    }
    
    /**
     * Establece el nombre de la partida actual en la que está participando un jugador.
     * 
     * @param nombre Nombre del jugador
     * @param nombrePartida Nombre de la partida
     * @return true si se estableció correctamente, false si el jugador no existe o es una IA
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
     *       y media, y se incrementa la puntuación total. Se devuelve true si la operación tuvo éxito,
     *       false en caso contrario.
     * @throws NullPointerException Si el nombre es null.
     */
    public boolean agregarPuntuacionIndividual(String nombre, int puntuacion) {
        return controladorRanking.agregarPuntuacionIndividual(nombre, puntuacion);
    }

    /**
     * Elimina todos los archivos de persistencia del sistema.
     * Este método se utiliza principalmente al cerrar la aplicación para limpiar los datos temporales.
     * 
     * @return true si todos los archivos fueron eliminados correctamente, false en caso contrario
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
}