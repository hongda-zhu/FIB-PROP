package scrabble.domain.controllers.subcontrollers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scrabble.domain.models.Jugador;
import scrabble.domain.models.JugadorHumano;
import scrabble.domain.models.JugadorIA;
import scrabble.helpers.Dificultad;

/**
 * Controlador para la gestión de usuarios.
 * Implementa el patrón Singleton para garantizar una única instancia.
 */
public class ControladorJugador {
    private static ControladorJugador instance;
    private Map<String, Jugador> jugadores; // key: nombre -> value: Jugador 
    
    private static final String JUGADORES_FILE = "src/main/resources/persistencias/jugadores.dat";
    
    /**
     * Constructor privado para implementar el patrón Singleton.
     * Inicializa la gestión de jugadores y carga los datos si existen.
     */
    private ControladorJugador() {
        this.jugadores = new HashMap<>();
        cargarDatos();
    }
    
    /**
     * Obtiene la instancia única del controlador (Singleton).
     * 
     * @return Instancia de ControladorJugador
     */
    public static synchronized ControladorJugador getInstance() {
        if (instance == null) {
            instance = new ControladorJugador();
        }
        return instance;
    }
    


    /**
     * Obtiene el contador de turnos que un jugador ha omitido.
     * 
     * @param nombre Nombre del jugador
     * @return El número de turnos omitidos por el jugador
     */
    public int getSkipTrack(String nombre) {
        Jugador j = getJugador(nombre);
        return j.getSkipTrack();
    }

    /**
     * Zero el contador de turnos que un jugador ha omitido.
     * 
     * @param nombre Nombre del jugador
     * @return El número de turnos omitidos por el jugador
     */
    public void clearSkipTrack(String nombre) {
        Jugador j = getJugador(nombre);
        j.clearSkipTrack();
    }


    /**
     * Agrega una ficha al jugador.
     * 
     * @param nombre Nombre del jugador
     * @param letra Ficha que se va a agregar
     */
    public void agregarFicha(String nombre, String letra) {
        Jugador j = getJugador(nombre);
        j.agregarFicha(letra);
    }

    /**
     * Obtiene la cantidad de fichas que tiene un jugador.
     * 
     * @param nombre Nombre del jugador
     * @return La cantidad de fichas del jugador
     */
    public int getCantidadFichas(String nombre) {
        Jugador j = getJugador(nombre);
        return j.getCantidadFichas();
    }

    /**
     * Añade puntuación a un jugador en el ranking.
     * Nota: La puntuación se gestiona completamente a través del ControladorRanking.
     * 
     * @param nombre Nombre del jugador
     * @param puntuacion La puntuación a añadir
     */
    public void addPuntuacion(String nombre, int puntuacion) {
        // Actualizamos la información en el sistema de ranking
        ControladorRanking controladorRanking = ControladorRanking.getInstance();
        controladorRanking.agregarPuntuacion(nombre, puntuacion);
    }

    /**
     * Obtiene el rack de un jugador.
     * 
     * @param nombre Nombre del jugador
     * @return El rack del jugador 
     */
    public Map<String,Integer> getRack(String nombre) {
        Jugador j = getJugador(nombre);
        return j.getRack();
    }

    /**
     * Obtiene el nivel de dificultad de un jugador, si es un jugador IA.
     * 
     * @param nombre Nombre del jugador
     * @return El nivel de dificultad del jugador si es una IA, o null si no lo es
     */    
    public Dificultad getNivelDificultad(String nombre) {
        if (!existeJugador(nombre)) {
            return null;
        }

        Jugador jugador = jugadores.get(nombre);

        if (jugador instanceof JugadorIA jugadorIA) {
            return jugadorIA.getNivelDificultad();
        }

        return null;
    }

    /**
     * Inicializa el rack de un jugador con un conjunto de fichas.
     * 
     * @param nombre Nombre del jugador
     * @param rack El conjunto de fichas a asignar al jugador
     */
    public void inicializarRack(String nombre, Map<String, Integer> rack) {
        Jugador j = getJugador(nombre);
        j.inicializarRack(rack);
    }

    /**
     * Obtiene el nombre de un jugador.
     * 
     * @param nombre Nombre del jugador
     * @return El nombre del jugador
     */
    public String getNombre(String nombre) {
        Jugador j = getJugador(nombre);
        return j.getNombre();
    }

    /**
     * Aumenta el contador de turnos omitidos de un jugador.
     * 
     * @param nombre Nombre del jugador
     */
    public void addSkipTrack(String nombre) {
        Jugador j = getJugador(nombre);
        j.addSkipTrack();
    }
    
    /**
    * Obtiene listado completo de usuarios (SOLO DEBUG o podemos dejarlo)
    * @return Lista con todos los datos
    */
    public List<String> getTodosUsuariosDebug() {
        List<String> usuarios = new ArrayList<>();
        
        // Jugadores humanos
        for (String nombre : getJugadoresHumanos()) {
            JugadorHumano jugador = (JugadorHumano) getJugador(nombre);

            usuarios.add(String.format(
                "Nombre: %-15s | Tipo: Humano | En partida: %s",
                jugador.getNombre(),
                jugador.isEnPartida() ? "Sí" : "No"
            ));
        }
        
        // Jugadores IA
        for (String nombre : getJugadoresIA()) {
            JugadorIA ia = (JugadorIA) getJugador(nombre);
            usuarios.add(String.format(
                "Nombre: %-15s | Tipo: IA | Dificultad: %s",
                ia.getNombre(),
                ia.getNivelDificultad()
            ));
        }
        
        return usuarios;
    }

    /**
    * Verifica si un determinado jugador es IA.
    *
    * @pre El jugador con el nombre dado debe existir en el sistema  
    * @param nombre Nombre del jugador
    * @return true si el jugador es IA, false en caso contrario
    */    
    public boolean esIA(String nombre) {
        Jugador jugador = getJugador(nombre);
        return jugador.esIA();
    }
    
    /**
     * Verifica si un jugador existe en el sistema.
     * 
     * @param nombre Nombre del jugador
     * @return true si el jugador existe, false en caso contrario
     */
    public boolean existeJugador(String nombre) {
        return jugadores.containsKey(nombre);
    }
    
    /**
     * Verifica si un jugador humano está en una partida.
     * 
     * @param nombre Nombre del jugador
     * @return true si está en partida, false en caso contrario
     */
    public boolean isEnPartida(String nombre) {
        if (!existeJugador(nombre)) {
            return false;
        }
        
        Jugador jugador = jugadores.get(nombre);
        if (jugador.esIA()) {
            return false; // Las IAs no están "en partida" del mismo modo que los humanos
        }
        
        return ((JugadorHumano) jugador).isEnPartida();
    }
    
    /**
     * Registra un nuevo jugador humano en el sistema.
     * 
     * @param nombre Nombre del jugador
     * @return true si se registró correctamente, false en caso contrario
     */
    public boolean registrarUsuario(String nombre) {
        if (existeJugador(nombre)) {
            return false;
        }
        
        JugadorHumano nuevoJugador = new JugadorHumano(nombre);
        jugadores.put(nombre, nuevoJugador);
        
        guardarDatos();
        return true;
    }
    
    /**
     * Registra un nuevo jugador IA en el sistema.
     * 
     * @param dificultad Nivel de dificultad
     * @return true si se registró correctamente, false en caso contrario
     */
    public boolean registrarJugadorIA(Dificultad dificultad, String nombre) {
        // Para JugadorIA, ahora usamos su constructor que genera un nombre automático
        JugadorIA nuevoJugadorIA = new JugadorIA(nombre, dificultad);
        
        // El nombre real viene del jugador generado
        String nombreReal = nuevoJugadorIA.getNombre();
        
        // Verificamos que no exista
        if (existeJugador(nombreReal)) {
            return false;
        }
        
        jugadores.put(nombreReal, nuevoJugadorIA);
        guardarDatos();
        return true;
    }
    
    /**
     * Alternativa para registrar un jugador IA con un nombre específico.
     * Este método se mantiene por compatibilidad con código existente.
     * 
     * @param nombre Nombre para referenciar (no se usa)
     * @param dificultad Nivel de dificultad
     * @return true si se registró correctamente, false en caso contrario
     */
    public boolean registrarJugadorIA(String nombre, Dificultad dificultad) {
        return registrarJugadorIA(dificultad, nombre);
    }
    
    /**
     * Elimina un jugador del sistema.
     * 
     * @param nombre Nombre del jugador a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     */
    public boolean eliminarUsuario(String nombre) {
        if (!existeJugador(nombre)) {
            return false;
        }
        
        jugadores.remove(nombre);
        guardarDatos();
        return true;
    }
    
    /**
     * Establece si un jugador humano está en una partida.
     * 
     * @param nombre Nombre del jugador
     * @param enPartida true si está en partida, false en caso contrario
     * @return true si se estableció correctamente, false en caso contrario
     */
    public boolean setEnPartida(String nombre, boolean enPartida) {
        if (!existeJugador(nombre)) {
            return false;
        }
        
        Jugador jugador = jugadores.get(nombre);
        if (jugador.esIA()) {
            return false; // Las IAs no pueden estar en múltiples partidas
        }
        
        ((JugadorHumano) jugador).setEnPartida(enPartida);
        guardarDatos();
        return true;
    }
    
    /**
     * Establece la puntuación de un jugador en el ranking.
     * Nota: La puntuación se gestiona completamente a través del ControladorRanking.
     * 
     * @param nombre Nombre del jugador
     * @param puntuacion Nueva puntuación
     * @return true si se estableció correctamente, false en caso contrario
     */
    public boolean setPuntuacion(String nombre, int puntuacion) {
        if (!existeJugador(nombre)) {
            return false;
        }
        
        // Obtenemos la puntuación actual desde el ranking
        ControladorRanking controladorRanking = ControladorRanking.getInstance();
        int puntuacionAnterior = controladorRanking.getPuntuacionTotal(nombre);
        
        // Si la puntuación es mayor, añadimos la diferencia
        if (puntuacion > puntuacionAnterior) {
            controladorRanking.agregarPuntuacion(nombre, puntuacion - puntuacionAnterior);
        }
        // Si necesitamos reflejar una reducción de puntuación, se podría implementar aquí
        
        return true;
    }
    
    /**
     * Obtiene una lista de nombres de todos los jugadores registrados.
     * 
     * @return Lista de nombres de jugadores
     */
    public List<String> getJugadoresRegistrados() {
        return new ArrayList<>(jugadores.keySet());
    }
    
    /**
     * Obtiene una lista de nombres de todos los jugadores humanos.
     * 
     * @return Lista de nombres de jugadores humanos
     */
    public List<String> getJugadoresHumanos() {
        List<String> jugadoresHumanos = new ArrayList<>();
        
        for (Map.Entry<String, Jugador> entry : jugadores.entrySet()) {
            if (!entry.getValue().esIA()) {
                jugadoresHumanos.add(entry.getKey());
            }
        }
        
        return jugadoresHumanos;
    }
    
    /**
     * Obtiene una lista de nombres de todos los jugadores IA.
     * 
     * @return Lista de nombres de jugadores IA
     */
    public List<String> getJugadoresIA() {
        List<String> jugadoresIA = new ArrayList<>();
        
        for (Map.Entry<String, Jugador> entry : jugadores.entrySet()) {
            if (entry.getValue().esIA()) {
                jugadoresIA.add(entry.getKey());
            }
        }
        
        return jugadoresIA;
    }
    
    /**
     * Obtiene un jugador por su nombre.
     * 
     * @param nombre Nombre del jugador
     * @return Jugador encontrado o null si no existe
     */
    private Jugador getJugador(String nombre) {
        return jugadores.get(nombre);
    }
    
    /**
     * Incrementa el contador de partidas jugadas para un jugador en el ranking.
     * 
     * @param nombre Nombre del jugador
     * @return true si se incrementó correctamente, false en caso contrario
     */
    public boolean incrementarPartidasJugadas(String nombre) {
        if (!existeJugador(nombre)) {
            return false;
        }
        
        if (esIA(nombre)) {
            return false; // Las IAs no tienen contador de partidas
        }
        
        // Delegar al ControladorRanking
        ControladorRanking controladorRanking = ControladorRanking.getInstance();
        controladorRanking.actualizarEstadisticasUsuario(nombre, false);
        
        return true;
    }
    
    /**
     * Incrementa el contador de partidas ganadas para un jugador en el ranking.
     * 
     * @param nombre Nombre del jugador
     * @return true si se incrementó correctamente, false en caso contrario
     */
    public boolean incrementarPartidasGanadas(String nombre) {
        if (!existeJugador(nombre)) {
            return false;
        }
        
        if (esIA(nombre)) {
            return false; // Las IAs no tienen contador de partidas
        }
        
        // Delegar al ControladorRanking
        ControladorRanking controladorRanking = ControladorRanking.getInstance();
        controladorRanking.actualizarEstadisticasUsuario(nombre, true);
        
        return true;
    }
    
    /**
     * Obtiene la puntuación total acumulada de un jugador desde el ranking.
     * 
     * @param nombre Nombre del jugador
     * @return La puntuación total acumulada, o 0 si el jugador no existe o es una IA
     */
    public int getPuntuacionTotal(String nombre) {
        if (!existeJugador(nombre) || esIA(nombre)) {
            return 0;
        }
        
        // Obtener la puntuación desde el ControladorRanking
        ControladorRanking controladorRanking = ControladorRanking.getInstance();
        return controladorRanking.getPuntuacionTotal(nombre);
    }
    
    /**
     * Obtiene la puntuación actual de un jugador desde el ranking.
     * 
     * @param nombre Nombre del jugador
     * @return La puntuación actual, o 0 si el jugador no existe o es una IA
     */
    public int getPuntuacion(String nombre) {
        if (!existeJugador(nombre) || esIA(nombre)) {
            return 0;
        }
        
        // Obtener la puntuación desde el ControladorRanking
        ControladorRanking controladorRanking = ControladorRanking.getInstance();
        return controladorRanking.getPuntuacionTotal(nombre);
    }
    
    /**
     * Obtiene el nombre de la partida actual en la que está participando un jugador.
     * 
     * @param nombre Nombre del jugador
     * @return Nombre de la partida actual o cadena vacía si no está en partida o es una IA
     */
    public String getNombrePartidaActual(String nombre) {
        if (!existeJugador(nombre) || esIA(nombre)) {
            return "";
        }
        
        JugadorHumano jugador = (JugadorHumano) jugadores.get(nombre);
        return jugador.getNombrePartidaActual();
    }
    
    /**
     * Establece el nombre de la partida actual en la que está participando un jugador.
     * 
     * @param nombre Nombre del jugador
     * @param nombrePartida Nombre de la partida
     * @return true si se estableció correctamente, false si el jugador no existe o es una IA
     */
    public boolean setNombrePartidaActual(String nombre, String nombrePartida) {
        if (!existeJugador(nombre) || esIA(nombre)) {
            return false;
        }
        
        JugadorHumano jugador = (JugadorHumano) jugadores.get(nombre);
        jugador.setNombrePartidaActual(nombrePartida);
        guardarDatos();
        return true;
    }

    /**
     * Obtiene la información básica de un jugador sin exponer el objeto de dominio.
     * Actúa como un patrón de transferencia de datos (DTO) improvisado, devolviendo 
     * solo datos primitivos o inmutables.
     * 
     * @param nombre Nombre del jugador
     * @return Map con la información básica del jugador, o null si no existe
     */
    public Map<String, Object> getInfoJugador(String nombre) {
        if (!existeJugador(nombre)) {
            return null;
        }
        
        Jugador jugador = getJugador(nombre);
        Map<String, Object> infoJugador = new HashMap<>();
        
        // Información común a todos los jugadores (solo valores primitivos o inmutables)
        infoJugador.put("nombre", jugador.getNombre());
        infoJugador.put("esIA", jugador.esIA());
        infoJugador.put("skipTrack", jugador.getSkipTrack());
        
        // Obtenemos la puntuación del ranking
        ControladorRanking controladorRanking = ControladorRanking.getInstance();
        infoJugador.put("puntuacion", controladorRanking.getPuntuacionTotal(nombre));
        
        // Información específica según el tipo de jugador
        if (jugador.esIA()) {
            JugadorIA jugadorIA = (JugadorIA) jugador;
            // Convertimos la enumeración a String para no exponer el tipo Dificultad
            infoJugador.put("nivelDificultad", jugadorIA.getNivelDificultad().toString());
        } else {
            JugadorHumano jugadorHumano = (JugadorHumano) jugador;
            infoJugador.put("enPartida", jugadorHumano.isEnPartida());
            infoJugador.put("nombrePartidaActual", jugadorHumano.getNombrePartidaActual());
        }
        
        return infoJugador;
    }
    
    /**
     * Guarda los datos de los jugadores en un archivo.
     */
    private void guardarDatos() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(JUGADORES_FILE))) {
            oos.writeObject(jugadores);
        } catch (IOException e) {
            System.err.println("Error al guardar los jugadores: " + e.getMessage());
        }
    }
    
    /**
     * Carga los datos de los jugadores desde un archivo.
     */
    @SuppressWarnings("unchecked")
    private void cargarDatos() {
        File jugadoresFile = new File(JUGADORES_FILE);
        if (jugadoresFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(jugadoresFile))) {
                jugadores = (Map<String, Jugador>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error al cargar los jugadores: " + e.getMessage());
                jugadores = new HashMap<>(); // Si hay error, inicializar con uno nuevo
            }
        }
    }
}