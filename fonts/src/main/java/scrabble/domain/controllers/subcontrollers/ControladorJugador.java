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
     * 
     * @pre No hay precondiciones específicas.
     * @post Se inicializa una nueva instancia con un mapa de jugadores vacío
     *       y se cargan los datos persistentes si están disponibles.
     */
    private ControladorJugador() {
        this.jugadores = new HashMap<>();
        cargarDatos();
    }
    
    /**
     * Obtiene la instancia única del controlador (Singleton).
     * 
     * @pre No hay precondiciones específicas.
     * @return Instancia de ControladorJugador
     * @post Se devuelve la única instancia de ControladorJugador que existe en la aplicación.
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
     * @pre El jugador con el nombre especificado debe existir.
     * @param nombre Nombre del jugador
     * @return El número de turnos omitidos por el jugador
     * @post Se devuelve un entero no negativo que representa los turnos omitidos.
     * @throws NullPointerException Si el nombre es null o si el jugador no existe.
     */
    public int getSkipTrack(String nombre) {
        Jugador j = getJugador(nombre);
        return j.getSkipTrack();
    }

    /**
     * Reinicia el contador de turnos que un jugador ha omitido.
     * 
     * @pre El jugador con el nombre especificado debe existir.
     * @param nombre Nombre del jugador
     * @post El contador de turnos omitidos del jugador se establece a cero.
     * @throws NullPointerException Si el nombre es null o si el jugador no existe.
     */
    public void clearSkipTrack(String nombre) {
        Jugador j = getJugador(nombre);
        j.clearSkipTrack();
    }


    /**
     * Agrega una ficha al jugador.
     * 
     * @pre El jugador con el nombre especificado debe existir.
     * @param nombre Nombre del jugador
     * @param letra Ficha que se va a agregar
     * @post La ficha se añade al rack del jugador.
     * @throws NullPointerException Si alguno de los parámetros es null o si el jugador no existe.
     */
    public void agregarFicha(String nombre, String letra) {
        Jugador j = getJugador(nombre);
        j.agregarFicha(letra);
    }
    

    /**
     * Obtiene la cantidad de fichas que tiene un jugador.
     * 
     * @pre El jugador con el nombre especificado debe existir.
     * @param nombre Nombre del jugador
     * @return La cantidad de fichas del jugador
     * @post Se devuelve un entero no negativo que representa la cantidad de fichas.
     * @throws NullPointerException Si el nombre es null o si el jugador no existe.
     */
    public int getCantidadFichas(String nombre) {
        Jugador j = getJugador(nombre);
        return j.getCantidadFichas();
    }

    /**
     * Añade puntuación a un jugador en el ranking.
     * Nota: La puntuación se gestiona completamente a través del ControladorRanking.
     * 
     * @pre El jugador con el nombre especificado debe existir.
     * @param nombre Nombre del jugador
     * @param puntuacion La puntuación a añadir
     * @post La puntuación se añade a la puntuación total del jugador en el ranking.
     * @throws NullPointerException Si el nombre es null.
     */
    public void addPuntuacion(String nombre, int puntuacion) {
        // Actualizamos la información en el sistema de ranking
        ControladorRanking controladorRanking = ControladorRanking.getInstance();
        controladorRanking.agregarPuntuacion(nombre, puntuacion);
    }

    /**
     * Obtiene el rack de un jugador.
     * 
     * @pre El jugador con el nombre especificado debe existir.
     * @param nombre Nombre del jugador
     * @return El rack del jugador 
     * @post Se devuelve un mapa con las fichas del jugador y sus cantidades.
     * @throws NullPointerException Si el nombre es null o si el jugador no existe.
     */
    public Map<String,Integer> getRack(String nombre) {
        Jugador j = getJugador(nombre);
        return j.getRack();
    }

    /**
     * Obtiene el nivel de dificultad de un jugador, si es un jugador IA.
     * 
     * @pre No hay precondiciones específicas.
     * @param nombre Nombre del jugador
     * @return El nivel de dificultad del jugador si es una IA, o null si no lo es
     * @post Si el jugador existe y es una IA, se devuelve su nivel de dificultad.
     *       En caso contrario, se devuelve null.
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
     * @pre El jugador con el nombre especificado debe existir.
     * @param nombre Nombre del jugador
     * @param rack El conjunto de fichas a asignar al jugador
     * @post El rack del jugador se inicializa con las fichas especificadas.
     * @throws NullPointerException Si alguno de los parámetros es null o si el jugador no existe.
     */
    public void inicializarRack(String nombre, Map<String, Integer> rack) {
        Jugador j = getJugador(nombre);
        j.inicializarRack(rack);
    }

    /**
     * Obtiene el nombre de un jugador.
     * 
     * @pre El jugador con el nombre especificado debe existir.
     * @param nombre Nombre del jugador
     * @return El nombre del jugador
     * @post Se devuelve el nombre del jugador.
     * @throws NullPointerException Si el nombre es null o si el jugador no existe.
     */
    public String getNombre(String nombre) {
        Jugador j = getJugador(nombre);
        return j.getNombre();
    }

    /**
     * Aumenta el contador de turnos omitidos de un jugador.
     * 
     * @pre El jugador con el nombre especificado debe existir.
     * @param nombre Nombre del jugador
     * @post El contador de turnos omitidos del jugador se incrementa en uno.
     * @throws NullPointerException Si el nombre es null o si el jugador no existe.
     */
    public void addSkipTrack(String nombre) {
        Jugador j = getJugador(nombre);
        j.addSkipTrack();
    }
    
    /**
    * Obtiene listado completo de usuarios (SOLO DEBUG o podemos dejarlo)
    * 
    * @pre No hay precondiciones específicas.
    * @return Lista con todos los datos
    * @post Se devuelve una lista con información formateada de todos los jugadores registrados.
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
    * @post Se devuelve un valor booleano indicando si el jugador es IA.
    * @throws NullPointerException Si el nombre es null o si el jugador no existe.
    */    
    public boolean esIA(String nombre) {
        Jugador jugador = getJugador(nombre);
        return jugador.esIA();
    }
    
    /**
     * Verifica si un jugador existe en el sistema.
     * 
     * @pre No hay precondiciones específicas.
     * @param nombre Nombre del jugador
     * @return true si el jugador existe, false en caso contrario
     * @post Se devuelve un valor booleano indicando si el jugador existe.
     * @throws NullPointerException Si el nombre es null.
     */
    public boolean existeJugador(String nombre) {
        return jugadores.containsKey(nombre);
    }
    
    /**
     * Verifica si un jugador humano está en una partida.
     * 
     * @pre No hay precondiciones específicas fuertes.
     * @param nombre Nombre del jugador
     * @return true si está en partida, false en caso contrario
     * @post Se devuelve un valor booleano indicando si el jugador humano está en partida.
     *       Si el jugador no existe o es IA, devuelve false.
     * @throws NullPointerException Si el nombre es null.
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
     * Establece si un jugador humano está en una partida.
     * 
     * @pre No hay precondiciones específicas fuertes.
     * @param nombre Nombre del jugador
     * @param enPartida true si está en partida, false en caso contrario
     * @return true si se estableció correctamente, false en caso contrario
     * @post Si el jugador existe y es humano, se actualiza su estado de partida y se devuelve true.
     *       Si el jugador no existe o es IA, se devuelve false.
     * @throws NullPointerException Si el nombre es null.
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
     * @pre No hay precondiciones específicas fuertes.
     * @param nombre Nombre del jugador
     * @param puntuacion Nueva puntuación
     * @return true si se estableció correctamente, false en caso contrario
     * @post Si el jugador existe, se actualiza su puntuación en el ranking si es mayor que la actual.
     *       Si el jugador no existe, se devuelve false.
     * @throws NullPointerException Si el nombre es null.
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
     * @pre No hay precondiciones específicas.
     * @return Lista de nombres de jugadores
     * @post Se devuelve una lista (posiblemente vacía) con los nombres de todos los jugadores.
     */
    public List<String> getJugadoresRegistrados() {
        return new ArrayList<>(jugadores.keySet());
    }
    
    /**
     * Obtiene una lista de nombres de todos los jugadores humanos.
     * 
     * @pre No hay precondiciones específicas.
     * @return Lista de nombres de jugadores humanos
     * @post Se devuelve una lista (posiblemente vacía) con los nombres de los jugadores humanos.
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
     * @pre No hay precondiciones específicas.
     * @return Lista de nombres de jugadores IA
     * @post Se devuelve una lista (posiblemente vacía) con los nombres de los jugadores IA.
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
     * @pre El nombre debe corresponder a un jugador existente.
     * @param nombre Nombre del jugador
     * @return Jugador encontrado o null si no existe
     * @post Se devuelve el objeto Jugador asociado al nombre, o null si no existe.
     * @throws NullPointerException Si el nombre es null.
     */
    private Jugador getJugador(String nombre) {
        return jugadores.get(nombre);
    }
    
    /**
     * Incrementa el contador de partidas jugadas para un jugador en el ranking.
     * 
     * @pre No hay precondiciones específicas fuertes.
     * @param nombre Nombre del jugador
     * @return true si se incrementó correctamente, false en caso contrario
     * @post Si el jugador existe y no es IA, se incrementa su contador de partidas jugadas y se devuelve true.
     *       Si el jugador no existe o es IA, se devuelve false.
     * @throws NullPointerException Si el nombre es null.
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
     * @pre No hay precondiciones específicas fuertes.
     * @param nombre Nombre del jugador
     * @return true si se incrementó correctamente, false en caso contrario
     * @post Si el jugador existe y no es IA, se incrementa su contador de partidas ganadas y se devuelve true.
     *       Si el jugador no existe o es IA, se devuelve false.
     * @throws NullPointerException Si el nombre es null.
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
     * @pre No hay precondiciones específicas fuertes.
     * @param nombre Nombre del jugador
     * @return La puntuación total acumulada, o 0 si el jugador no existe o es una IA
     * @post Se devuelve un entero no negativo que representa la puntuación total del jugador.
     * @throws NullPointerException Si el nombre es null.
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
     * @pre No hay precondiciones específicas fuertes.
     * @param nombre Nombre del jugador
     * @return La puntuación actual, o 0 si el jugador no existe o es una IA
     * @post Se devuelve un entero no negativo que representa la puntuación actual del jugador.
     * @throws NullPointerException Si el nombre es null.
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
     * @pre No hay precondiciones específicas fuertes.
     * @param nombre Nombre del jugador
     * @return Nombre de la partida actual o cadena vacía si no está en partida o es una IA
     * @post Se devuelve el nombre de la partida actual del jugador, o cadena vacía si no aplica.
     * @throws NullPointerException Si el nombre es null.
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
     * @pre No hay precondiciones específicas fuertes.
     * @param nombre Nombre del jugador
     * @param nombrePartida Nombre de la partida
     * @return true si se estableció correctamente, false si el jugador no existe o es una IA
     * @post Si el jugador existe y no es IA, se actualiza el nombre de su partida actual y se devuelve true.
     *       Si el jugador no existe o es IA, se devuelve false.
     * @throws NullPointerException Si alguno de los parámetros es null.
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
     * @pre No hay precondiciones específicas fuertes.
     * @param nombre Nombre del jugador
     * @return Map con la información básica del jugador, o null si no existe
     * @post Si el jugador existe, se devuelve un mapa con su información básica.
     *       Si el jugador no existe, se devuelve null.
     * @throws NullPointerException Si el nombre es null.
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
     * 
     * @pre No hay precondiciones específicas.
     * @post Los datos de los jugadores se guardan en el archivo especificado por JUGADORES_FILE.
     *       En caso de error, se registra el mensaje en la consola de error.
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
     * 
     * @pre No hay precondiciones específicas.
     * @post Si el archivo existe y se puede leer correctamente, se cargan los datos de los jugadores.
     *       En caso de error, se inicializa un mapa vacío y se registra el mensaje en la consola de error.
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

    /**
     * Registra un nuevo jugador humano en el sistema.
     * 
     * @pre El nombre no debe corresponder a un jugador ya existente y no debe ser null.
     * @param nombre Nombre del jugador
     * @return true si se registró correctamente, false en caso contrario
     * @post Si el nombre no existe, se crea un nuevo jugador humano y se devuelve true.
     *       Si el nombre ya existe, se devuelve false.
     * @throws NullPointerException Si el nombre es null.
     */
    public boolean registrarUsuario(String nombre) {
        if (nombre == null) {
            throw new NullPointerException("El nombre no puede ser null");
        }
        
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
     * @pre No hay precondiciones específicas, se generará un nombre único.
     * @param dificultad Nivel de dificultad
     * @param nombre Nombre base para la IA
     * @return true si se registró correctamente, false en caso contrario
     * @post Si el nombre generado no existe, se crea un nuevo jugador IA y se devuelve true.
     *       Si el nombre generado ya existe, se devuelve false.
     * @throws NullPointerException Si dificultad es null.
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
     * @pre No hay precondiciones específicas, se generará un nombre único.
     * @param nombre Nombre base para la IA (no se usa directamente)
     * @param dificultad Nivel de dificultad
     * @return true si se registró correctamente, false en caso contrario
     * @post Si el nombre generado no existe, se crea un nuevo jugador IA y se devuelve true.
     *       Si el nombre generado ya existe, se devuelve false.
     * @throws NullPointerException Si alguno de los parámetros es null.
     */
    public boolean registrarJugadorIA(String nombre, Dificultad dificultad) {
        return registrarJugadorIA(dificultad, nombre);
    }
    
    /**
     * Elimina un jugador del sistema.
     * 
     * @pre No hay precondiciones específicas fuertes.
     * @param nombre Nombre del jugador a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     * @post Si el jugador existía, se elimina del sistema y se devuelve true.
     *       Si el jugador no existía, se devuelve false.
     * @throws NullPointerException Si el nombre es null.
     */
    public boolean eliminarUsuario(String nombre) {
        if (!existeJugador(nombre)) {
            return false;
        }
        
        jugadores.remove(nombre);
        guardarDatos();
        return true;
    }
}