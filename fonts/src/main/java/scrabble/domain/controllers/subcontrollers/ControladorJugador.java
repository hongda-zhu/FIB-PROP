package scrabble.domain.controllers.subcontrollers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import scrabble.domain.models.Jugador;
import scrabble.domain.models.JugadorHumano;
import scrabble.domain.models.JugadorIA;
import scrabble.domain.persistences.interfaces.RepositorioJugador;
import scrabble.helpers.Dificultad;

/**
 * Controlador para la gestión de usuarios.
 * Implementa el patrón Singleton para garantizar una única instancia.
 */
public class ControladorJugador {
    private static ControladorJugador instance;
    private Map<String, Jugador> jugadores; // key: nombre -> value: Jugador 
    private RepositorioJugador repositorioJugador;
    
    /**
     * Constructor privado para implementar el patrón Singleton.
     * Inicializa la gestión de jugadores y carga los datos si existen.
     * 
     * @pre repositorioJugador no debe ser null.
     * @post Se inicializa una nueva instancia con un mapa de jugadores vacío
     *       y se cargan los datos persistentes si están disponibles.
     */
    private ControladorJugador(RepositorioJugador repositorioJugador) {
        if (repositorioJugador == null) {
            throw new NullPointerException("El repositorio de jugadores no puede ser null");
        }
        this.jugadores = new HashMap<>();
        this.repositorioJugador = repositorioJugador;
        cargarDatos();
    }
    
    /**
     * Obtiene la instancia única del controlador (Singleton).
     * Utiliza la implementación por defecto del repositorio.
     * 
     * @pre No hay precondiciones específicas.
     * @return Instancia de ControladorJugador
     * @post Se devuelve la única instancia de ControladorJugador que existe en la aplicación.
     */
    public static synchronized ControladorJugador getInstance() {
        if (instance == null) {
            // Usar un método de fábrica para crear la implementación del repositorio
            RepositorioJugador repo = createDefaultRepository();
            instance = new ControladorJugador(repo);
        }
        return instance;
    }
    
    /**
     * Obtiene la instancia única del controlador (Singleton) con un repositorio específico.
     * Útil para pruebas o para usar una implementación alternativa de repositorio.
     * 
     * @param repositorioJugador Implementación de RepositorioJugador a utilizar
     * @pre repositorioJugador no debe ser null.
     * @return Instancia de ControladorJugador
     * @post Se devuelve la única instancia de ControladorJugador que existe en la aplicación.
     * @throws NullPointerException Si repositorioJugador es null.
     */
    public static synchronized ControladorJugador getInstance(RepositorioJugador repositorioJugador) {
        if (repositorioJugador == null) {
            throw new NullPointerException("El repositorio de jugadores no puede ser null");
        }
        
        if (instance == null) {
            instance = new ControladorJugador(repositorioJugador);
        } else {
            // Si la instancia ya existe, actualizamos su repositorio
            instance.repositorioJugador = repositorioJugador;
        }
        return instance;
    }
    
    /**
     * Método privado para crear la implementación por defecto del repositorio.
     * Encapsula la creación de la implementación concreta para evitar dependencias directas.
     * 
     * @return Una implementación de RepositorioJugador
     */
    private static RepositorioJugador createDefaultRepository() {
        try {
            // Cambiamos la ruta para que apunte al package correcto
            return (RepositorioJugador) Class.forName("scrabble.domain.persistences.implementaciones.RepositorioJugadorImpl").getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("No se pudo crear la implementación por defecto del repositorio: " + e.getMessage(), e);
        }
    }
    
    /**
     * Método privado para añadir un jugador al mapa interno.
     * 
     * @pre nombre y jugador no deben ser null.
     * @param nombre Nombre del jugador (clave)
     * @param jugador Objeto Jugador a añadir (valor)
     * @post El jugador se añade al mapa interno.
     * @throws NullPointerException Si alguno de los parámetros es null.
     */
    private void addJugadorToMap(String nombre, Jugador jugador) {
        if (nombre == null || jugador == null) {
            throw new NullPointerException("El nombre y el jugador no pueden ser null");
        }
        jugadores.put(nombre, jugador);
    }
    
    /**
     * Método privado para eliminar un jugador del mapa interno.
     * 
     * @pre nombre no debe ser null.
     * @param nombre Nombre del jugador a eliminar
     * @return true si el jugador existía y fue eliminado, false si no existía
     * @post Si el jugador existía, se elimina del mapa interno y se devuelve true.
     *       Si no existía, se devuelve false.
     * @throws NullPointerException Si el nombre es null.
     */
    private boolean removeJugadorFromMap(String nombre) {
        if (nombre == null) {
            throw new NullPointerException("El nombre no puede ser null");
        }
        return jugadores.remove(nombre) != null;
    }
    
    /**
     * Método privado para obtener un jugador del mapa interno por su nombre.
     * 
     * @pre nombre no debe ser null.
     * @param nombre Nombre del jugador a obtener
     * @return El objeto Jugador asociado al nombre, o null si no existe
     * @post Se devuelve el objeto Jugador asociado al nombre, o null si no existe.
     * @throws NullPointerException Si el nombre es null.
     */
    private Jugador getJugadorFromMap(String nombre) {
        if (nombre == null) {
            throw new NullPointerException("El nombre no puede ser null");
        }
        return jugadores.get(nombre);
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
        if (nombre == null) {
            throw new NullPointerException("El nombre no puede ser null");
        }
        return getNombresJugadoresFromMap().contains(nombre);
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
        if (nombre == null) {
            throw new NullPointerException("El nombre no puede ser null");
        }
        return getJugadorFromMap(nombre);
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

        Jugador jugador = getJugador(nombre);

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
     * Método privado para añadir información de usuario a una lista.
     *
     * @pre lista no debe ser null, infoFormateada no debe ser null.
     * @param lista Lista donde se añadirá la información
     * @param infoFormateada String con la información formateada del usuario
     * @post La información formateada se añade a la lista.
     * @throws NullPointerException Si alguno de los parámetros es null.
     */
    private void addUsuarioToList(List<String> lista, String infoFormateada) {
        if (lista == null || infoFormateada == null) {
            throw new NullPointerException("La lista y la información formateada no pueden ser null");
        }
        lista.add(infoFormateada);
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

            String infoJugador = String.format(
                "Nombre: %-15s | Tipo: Humano | En partida: %s",
                jugador.getNombre(),
                jugador.isEnPartida() ? "Sí" : "No"
            );
            addUsuarioToList(usuarios, infoJugador);
        }
        
        // Jugadores IA
        for (String nombre : getJugadoresIA()) {
            JugadorIA ia = (JugadorIA) getJugador(nombre);
            String infoIA = String.format(
                "Nombre: %-15s | Tipo: IA | Dificultad: %s",
                ia.getNombre(),
                ia.getNivelDificultad()
            );
            addUsuarioToList(usuarios, infoIA);
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
        
        Jugador jugador = getJugador(nombre);
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
        
        Jugador jugador = getJugador(nombre);
        if (jugador.esIA()) {
            return false; // Las IAs no pueden estar en múltiples partidas
        }
        
        ((JugadorHumano) jugador).setEnPartida(enPartida);
        guardarDatos();
        return true;
    }
    
    /**
     * Método privado para obtener los nombres de todos los jugadores registrados.
     * 
     * @pre No hay precondiciones específicas.
     * @return Conjunto con los nombres de todos los jugadores
     * @post Se devuelve un conjunto (posiblemente vacío) con los nombres de todos los jugadores.
     */
    private Set<String> getNombresJugadoresFromMap() {
        return new HashSet<>(jugadores.keySet());
    }

    /**
     * Obtiene una lista de nombres de todos los jugadores registrados.
     * 
     * @pre No hay precondiciones específicas.
     * @return Lista de nombres de jugadores
     * @post Se devuelve una lista (posiblemente vacía) con los nombres de todos los jugadores.
     */
    public List<String> getJugadoresRegistrados() {
        return new ArrayList<>(getNombresJugadoresFromMap());
    }
    
    /**
     * Método privado para añadir un nombre a una lista.
     *
     * @pre lista no debe ser null, nombre no debe ser null.
     * @param lista Lista donde se añadirá el nombre
     * @param nombre Nombre a añadir a la lista
     * @post El nombre se añade a la lista.
     * @throws NullPointerException Si alguno de los parámetros es null.
     */
    private void addNombreToList(List<String> lista, String nombre) {
        if (lista == null || nombre == null) {
            throw new NullPointerException("La lista y el nombre no pueden ser null");
        }
        lista.add(nombre);
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
        
        for (String nombre : getJugadoresRegistrados()) {
            Jugador jugador = getJugador(nombre);
            if (!jugador.esIA()) {
                addNombreToList(jugadoresHumanos, nombre);
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
        
        for (String nombre : getJugadoresRegistrados()) {
            Jugador jugador = getJugador(nombre);
            if (jugador.esIA()) {
                addNombreToList(jugadoresIA, nombre);
            }
        }
        
        return jugadoresIA;
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
        
        JugadorHumano jugador = (JugadorHumano) getJugador(nombre);
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
        
        JugadorHumano jugador = (JugadorHumano) getJugador(nombre);
        jugador.setNombrePartidaActual(nombrePartida);
        guardarDatos();
        return true;
    }
    
    /**
     * Método privado para añadir información a un mapa.
     *
     * @pre mapa no debe ser null, clave no debe ser null.
     * @param mapa Mapa donde se añadirá la información
     * @param clave Clave bajo la cual se almacenará el valor
     * @param valor Valor a almacenar (puede ser null)
     * @post La clave y valor se añaden al mapa.
     * @throws NullPointerException Si mapa o clave son null.
     */
    private void addInfoToMap(Map<String, Object> mapa, String clave, Object valor) {
        if (mapa == null || clave == null) {
            throw new NullPointerException("El mapa y la clave no pueden ser null");
        }
        mapa.put(clave, valor);
    }

    /**
     * Obtiene la información de un jugador como un mapa de datos.
     * Este método proporciona encapsulación al devolver solo la información
     * necesaria sin exponer directamente el objeto Jugador.
     *
     * @pre El jugador con el nombre especificado debe existir.
     * @param nombre Nombre del jugador
     * @return Mapa con datos del jugador (nombre, tipo, puntuación, etc.)
     * @post Se devuelve un mapa con la información relevante del jugador.
     * @throws NullPointerException Si el nombre es null.
     * @throws IllegalArgumentException Si el jugador no existe.
     */
    public Map<String, Object> getInfoJugador(String nombre) {
        if (nombre == null) {
            throw new NullPointerException("El nombre no puede ser null");
        }
        
        if (!existeJugador(nombre)) {
            throw new IllegalArgumentException("No existe un jugador con el nombre: " + nombre);
        }
        
        Jugador jugador = getJugador(nombre);
        Map<String, Object> info = new HashMap<>();
        
        // Datos comunes
        addInfoToMap(info, "nombre", jugador.getNombre());
        addInfoToMap(info, "esIA", jugador.esIA());
        addInfoToMap(info, "cantidadFichas", jugador.getCantidadFichas());
        addInfoToMap(info, "skipTrack", jugador.getSkipTrack());
        
        // Datos específicos según tipo
        if (jugador.esIA()) {
            JugadorIA jugadorIA = (JugadorIA) jugador;
            addInfoToMap(info, "nivelDificultad", jugadorIA.getNivelDificultad());
        } else {
            JugadorHumano jugadorHumano = (JugadorHumano) jugador;
            addInfoToMap(info, "enPartida", jugadorHumano.isEnPartida());
            addInfoToMap(info, "nombrePartidaActual", jugadorHumano.getNombrePartidaActual());
            
            // Obtener puntuación desde el controlador de ranking
            ControladorRanking controladorRanking = ControladorRanking.getInstance();
            addInfoToMap(info, "puntuacion", controladorRanking.getPuntuacionTotal(nombre));
        }
        
        return info;
    }

    /**
     * Guarda los datos de los jugadores a través del repositorio.
     * 
     * @pre No hay precondiciones específicas.
     * @post Los datos de los jugadores se guardan usando el repositorio.
     * @throws RuntimeException si ocurre un error al guardar los jugadores
     */
    private void guardarDatos() {
        boolean success = repositorioJugador.guardarTodos(jugadores);
        if (!success) {
            throw new RuntimeException("Error al guardar los jugadores a través del repositorio");
        }
    }
    
    /**
     * Carga los datos de los jugadores desde el repositorio.
     * 
     * @pre No hay precondiciones específicas.
     * @post Si el repositorio tiene datos, se cargan los jugadores.
     *       En caso de error, se inicializa un mapa vacío.
     */
    private void cargarDatos() {
        Map<String, Jugador> jugadoresCargados = repositorioJugador.cargarTodos();
        if (jugadoresCargados != null && !jugadoresCargados.isEmpty()) {
            this.jugadores = jugadoresCargados;
        } else {
            // Si no hay datos, inicializamos un mapa vacío
            this.jugadores = new HashMap<>();
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
        addJugadorToMap(nombre, nuevoJugador);
        
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
        
        addJugadorToMap(nombreReal, nuevoJugadorIA);
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
        
        boolean removed = removeJugadorFromMap(nombre);
        if (removed) {
            guardarDatos();
        }
        return removed;
    }
}