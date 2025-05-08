package scrabble.domain.controllers.subcontrollers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import scrabble.domain.models.Ranking;
import scrabble.domain.models.rankingStrategy.RankingDataProvider;
import scrabble.excepciones.ExceptionLoggingOperacion;
import scrabble.domain.persistences.interfaces.RepositorioRanking;
import scrabble.domain.persistences.implementaciones.RepositorioRankingImpl;

/**
 * Controlador para la gestión del ranking de jugadores.
 * Implementa el patrón Singleton para garantizar una única instancia.
 * Delega toda la lógica de negocio al modelo Ranking.
 */
public class ControladorRanking implements RankingDataProvider {
    private static final long serialVersionUID = 1L;
    private static transient ControladorRanking instance;
    
    // Referencia al modelo de Ranking
    private Ranking ranking;
    private RepositorioRanking repositorio;
    
    /**
     * Constructor privado para implementar el patrón Singleton.
     * Inicializa el modelo de Ranking y carga los datos existentes.
     * 
     * @pre No hay precondiciones específicas.
     * @post Se inicializa una nueva instancia con un modelo de Ranking
     *       y se cargan los datos persistentes si están disponibles.
     */
    private ControladorRanking() {
        this(new RepositorioRankingImpl());
    }
    
    /**
     * Constructor privado con inyección de repositorio para pruebas.
     * 
     * @param repositorio El repositorio a utilizar para la persistencia
     * @pre El repositorio no debe ser null.
     * @post Se inicializa una nueva instancia con un modelo de Ranking cargado desde el repositorio.
     * @throws NullPointerException si el repositorio es null
     */
    private ControladorRanking(RepositorioRanking repositorio) {
        if (repositorio == null) {
            throw new NullPointerException("El repositorio no puede ser null");
        }
        this.repositorio = repositorio;
        this.ranking = repositorio.cargar();
    }
    
    /**
     * Obtiene la instancia única del controlador (Singleton).
     * 
     * @pre No hay precondiciones específicas.
     * @return Instancia de ControladorRanking
     * @post Se devuelve la única instancia de ControladorRanking que existe en la aplicación.
     */
    public static synchronized ControladorRanking getInstance() {
        if (instance == null) {
            instance = new ControladorRanking();
        }
        return instance;
    }
    
    /**
     * Obtiene la instancia única del controlador con repositorio personalizado (para pruebas).
     * 
     * @param repositorio El repositorio a utilizar
     * @return Instancia de ControladorRanking
     * @pre El repositorio no debe ser null.
     * @post Se devuelve una instancia de ControladorRanking configurada con el repositorio especificado.
     * @throws NullPointerException Si el repositorio es null
     */
    public static synchronized ControladorRanking getInstance(RepositorioRanking repositorio) {
        instance = new ControladorRanking(repositorio);
        return instance;
    }
    
    /**
     * Agrega una puntuación para un usuario específico a la lista de puntuaciones individuales.
     * Esta puntuación afectará las estadísticas (máxima, media) y también la puntuación total.
     * 
     * @pre El nombre debe ser no nulo y no vacío, y la puntuación debe ser no negativa.
     * @param nombre Nombre del usuario
     * @param puntuacion Puntuación a agregar
     * @return true si se agregó correctamente, false en caso contrario
     * @post Si el nombre es válido y la puntuación no es negativa, se agrega la puntuación
     *       al usuario y se devuelve true. En caso contrario, se devuelve false.
     * @throws NullPointerException Si el nombre es null.
     */
    public boolean agregarPuntuacion(String nombre, int puntuacion) {
        // Verificar que se proporciona un usuario válido
        if (nombre == null || nombre.isEmpty()) {
            return false;
        }
        
        if (puntuacion < 0) {
            return false; // No se permiten puntuaciones negativas
        }
        
        // Delegar al modelo de Ranking
        boolean resultado = ranking.agregarPuntuacion(nombre, puntuacion);
        
        // Guardar cambios si la operación fue exitosa
        if (resultado) {
            repositorio.guardar(ranking);
        }
        
        return resultado;
    }
    
    /**
     * Actualiza las estadísticas de un usuario (partidas jugadas y victoria),
     * y opcionalmente registra una puntuación de partida.
     * 
     * @pre El nombre debe ser no nulo y no vacío.
     * @param nombre Nombre del usuario
     * @param esVictoria true si el usuario ha ganado la partida
     * @param puntuacion Puntuación obtenida en la partida (-1 si no se debe registrar puntuación)
     * @return true si se actualizaron correctamente las estadísticas
     * @post Si el nombre es válido, se actualizan las estadísticas del usuario
     *       (partidas jugadas y, si corresponde, victorias). Si puntuacion >= 0,
     *       también se registra como puntuación individual sin duplicar el contador de partidas.
     *       Se devuelve true si todo se ejecuta correctamente.
     * @throws NullPointerException Si el nombre es null.
     */
    public boolean actualizarEstadisticasUsuario(String nombre, boolean esVictoria, int puntuacion) {
        // Verificar que se proporciona un usuario válido
        if (nombre == null || nombre.isEmpty()) {
            return false;
        }
        
        try {
            // Primero actualizamos las estadísticas básicas (partidas y victorias)
            ranking.actualizarEstadisticasUsuario(nombre, esVictoria);
            
            // Si se proporciona una puntuación válida, la añadimos sin incrementar partidas
            if (puntuacion >= 0) {
                ranking.agregarPuntuacionSinIncrementarPartidas(nombre, puntuacion);
            }
            
            // Guardar cambios
            repositorio.guardar(ranking);
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Actualiza las estadísticas de un usuario (partidas jugadas y victoria).
     * Método de compatibilidad que no registra puntuación.
     * 
     * @pre El nombre debe ser no nulo y no vacío.
     * @param nombre Nombre del usuario
     * @param esVictoria true si el usuario ha ganado la partida
     * @return true si se actualizaron correctamente las estadísticas
     * @post Si el nombre es válido, se actualizan las estadísticas del usuario
     *       (partidas jugadas y, si corresponde, victorias) y se devuelve true.
     *       En caso contrario, se devuelve false.
     * @throws NullPointerException Si el nombre es null.
     */
    public boolean actualizarEstadisticasUsuario(String nombre, boolean esVictoria) {
        return actualizarEstadisticasUsuario(nombre, esVictoria, -1);
    }
    
    /**
     * Verifica si un usuario está en el ranking.
     * 
     * @pre No hay precondiciones específicas.
     * @param nombre Nombre del usuario
     * @return true si el usuario existe en el ranking, false en caso contrario
     * @post Se devuelve un valor booleano indicando si el usuario existe en el ranking.
     * @throws NullPointerException Si el nombre es null.
     */
    public boolean perteneceRanking(String nombre) {
        return ranking.perteneceRanking(nombre);
    }
    
    /**
     * Elimina una puntuación específica de un usuario.
     * 
     * @pre Para funcionar correctamente, el usuario debe existir en el ranking.
     * @param nombre Nombre del usuario
     * @param puntuacion Puntuación a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     * @post Si el usuario existe y tenía la puntuación especificada, 
     *       se elimina esa puntuación y se devuelve true.
     *       En caso contrario, se devuelve false.
     * @throws NullPointerException Si el nombre es null.
     */
    public boolean eliminarPuntuacion(String nombre, int puntuacion) {
        boolean resultado = ranking.eliminarPuntuacion(nombre, puntuacion);
        
        // Guardar cambios si la operación fue exitosa
        if (resultado) {
            repositorio.guardar(ranking);
        }
        
        return resultado;
    }
    
    /**
     * Verifica si existe una puntuación específica para un usuario.
     * 
     * @pre No hay precondiciones específicas fuertes.
     * @param nombre Nombre del usuario
     * @param puntuacion Puntuación a verificar
     * @return true si existe la puntuación, false en caso contrario
     * @post Se devuelve un valor booleano indicando si el usuario 
     *       tiene registrada la puntuación especificada.
     * @throws NullPointerException Si el nombre es null.
     */
    public boolean existePuntuacion(String nombre, int puntuacion) {
        return ranking.existePuntuacion(nombre, puntuacion);
    }
    
    /**
     * Elimina un usuario del ranking.
     * 
     * @pre Para funcionar correctamente, el usuario debe existir en el ranking.
     * @param nombre Nombre del usuario a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     * @post Si el usuario existía en el ranking, se elimina y se devuelve true.
     *       En caso contrario, se devuelve false.
     * @throws NullPointerException Si el nombre es null.
     */
    public boolean eliminarUsuario(String nombre) {
        boolean resultado = ranking.eliminarUsuario(nombre);
        
        // Guardar cambios si la operación fue exitosa
        if (resultado) {
            repositorio.guardar(ranking);
        }
        
        return resultado;
    }
    
    /**
     * Cambia la estrategia de ordenación del ranking.
     * 
     * @pre No hay precondiciones específicas.
     * @param criterio Criterio de ordenación: "maxima", "media", "partidas", "victorias" o "total"
     * @post La estrategia de ordenación del ranking se actualiza al criterio especificado.
     * @throws NullPointerException Si el criterio es null.
     * @throws IllegalArgumentException Si el criterio no es uno de los valores permitidos.
     */
    public void setEstrategia(String criterio) {
        ranking.setEstrategia(criterio);
        repositorio.guardar(ranking);
    }
    
    /**
     * Obtiene la estrategia actual de ordenación.
     * 
     * @pre No hay precondiciones específicas.
     * @return Nombre de la estrategia actual
     * @post Se devuelve el nombre de la estrategia actual de ordenación.
     */
    public String getEstrategiaActual() {
        return ranking.getEstrategiaActual();
    }
    
    /**
     * Obtiene el ranking de usuarios según la estrategia actual.
     * Filtra automáticamente los jugadores IA para no incluirlos en el ranking.
     * 
     * @pre No hay precondiciones específicas.
     * @return Lista ordenada de nombres de usuario humanos
     * @post Se devuelve una lista ordenada (según la estrategia actual) con los 
     *       nombres de usuarios humanos. Los jugadores IA se excluyen.
     */
    public List<String> getRanking() {
        // Obtener todos los usuarios del ranking
        List<String> todosUsuarios = new ArrayList<>(ranking.getUsuarios());
        
        // Obtenemos una instancia del controlador de jugadores para verificar si son IA
        ControladorJugador controladorJugador = ControladorJugador.getInstance();
        
        // Filtramos los jugadores IA y obtenemos solo los nombres de usuarios humanos
        List<String> usuariosHumanos = todosUsuarios.stream()
            .filter(nombre -> !controladorJugador.existeJugador(nombre) || 
                    !controladorJugador.esIA(nombre))
            .collect(Collectors.toList());
        
        // Obtenemos el ranking ordenado de estos usuarios
        return ordenarUsuariosPorCriterio(usuariosHumanos, ranking.getEstrategiaActual());
    }
    
    /**
     * Obtiene el ranking de usuarios con un criterio específico.
     * Filtra automáticamente los jugadores IA para no incluirlos en el ranking.
     * 
     * @pre No hay precondiciones específicas.
     * @param criterio Criterio de ordenación
     * @return Lista ordenada de nombres de usuario humanos
     * @post Se devuelve una lista ordenada (según el criterio especificado) con los 
     *       nombres de usuarios humanos. Los jugadores IA se excluyen.
     * @throws NullPointerException Si el criterio es null.
     * @throws IllegalArgumentException Si el criterio no es uno de los valores permitidos.
     */
    public List<String> getRanking(String criterio) {
        // Obtener todos los usuarios del ranking
        List<String> todosUsuarios = new ArrayList<>(ranking.getUsuarios());
        
        // Obtenemos una instancia del controlador de jugadores para verificar si son IA
        ControladorJugador controladorJugador = ControladorJugador.getInstance();
        
        // Filtramos los jugadores IA y obtenemos solo los nombres de usuarios humanos
        List<String> usuariosHumanos = todosUsuarios.stream()
            .filter(nombre -> !controladorJugador.existeJugador(nombre) || 
                    !controladorJugador.esIA(nombre))
            .collect(Collectors.toList());
        
        // Obtener el ranking ordenado con el criterio específico
        return ordenarUsuariosPorCriterio(usuariosHumanos, criterio);
    }
    
    /**
     * Obtiene la lista de puntuaciones de un usuario específico.
     * 
     * @pre No hay precondiciones específicas fuertes.
     * @param nombre Nombre del usuario
     * @return Lista de puntuaciones o lista vacía si el usuario no existe
     * @post Se devuelve una lista con las puntuaciones del usuario.
     *       Si el usuario no existe, se devuelve una lista vacía.
     * @throws NullPointerException Si el nombre es null.
     */
    public List<Integer> getPuntuacionesUsuario(String nombre) {
        return ranking.getPuntuacionesUsuario(nombre);
    }
    
    /**
     * Implementación de RankingDataProvider. Obtiene la puntuación máxima de un usuario.
     * 
     * @pre No hay precondiciones específicas fuertes.
     * @param nombre Nombre del usuario
     * @return La puntuación máxima del usuario, o 0 si no existe
     * @post Se devuelve un entero no negativo que representa la puntuación máxima del usuario.
     * @throws NullPointerException Si el nombre es null.
     */
    @Override
    public int getPuntuacionMaxima(String nombre) {
        return ranking.getPuntuacionMaxima(nombre);
    }
    
    /**
     * Implementación de RankingDataProvider. Obtiene la puntuación media de un usuario.
     * 
     * @pre No hay precondiciones específicas fuertes.
     * @param nombre Nombre del usuario
     * @return La puntuación media del usuario, o 0.0 si no existe
     * @post Se devuelve un valor de punto flotante no negativo que representa la puntuación media del usuario.
     * @throws NullPointerException Si el nombre es null.
     */
    @Override
    public double getPuntuacionMedia(String nombre) {
        return ranking.getPuntuacionMedia(nombre);
    }
    
    /**
     * Implementación de RankingDataProvider. Obtiene el número de partidas jugadas por un usuario.
     * 
     * @pre No hay precondiciones específicas fuertes.
     * @param nombre Nombre del usuario
     * @return El número de partidas jugadas por el usuario, o 0 si no existe
     * @post Se devuelve un entero no negativo que representa el número de partidas jugadas por el usuario.
     * @throws NullPointerException Si el nombre es null.
     */
    @Override
    public int getPartidasJugadas(String nombre) {
        return ranking.getPartidasJugadas(nombre);
    }
    
    /**
     * Implementación de RankingDataProvider. Obtiene el número de victorias de un usuario.
     * 
     * @pre No hay precondiciones específicas fuertes.
     * @param nombre Nombre del usuario
     * @return El número de victorias del usuario, o 0 si no existe
     * @post Se devuelve un entero no negativo que representa el número de victorias del usuario.
     * @throws NullPointerException Si el nombre es null.
     */
    @Override
    public int getVictorias(String nombre) {
        return ranking.getVictorias(nombre);
    }
    
    /**
     * Obtiene la puntuación total acumulada de un usuario específico.
     * Suma todas las puntuaciones individuales registradas para el usuario.
     * 
     * @pre No hay precondiciones específicas fuertes.
     * @param nombre Nombre del usuario
     * @return Puntuación total acumulada
     * @post Se devuelve un entero no negativo que representa la puntuación total acumulada del usuario.
     * @throws NullPointerException Si el nombre es null.
     */
    @Override
    public int getPuntuacionTotal(String nombre) {
        return ranking.getPuntuacionTotal(nombre);
    }
    
    /**
     * Establece la puntuación total de un jugador.
     * 
     * @param username Nombre del jugador.
     * @param nuevaPuntuacion Nueva puntuación total.
     * @pre El nombre de usuario no es null y el jugador existe en el ranking.
     * @return true si se pudo establecer la puntuación, false en caso contrario.
     * @post La puntuación total del usuario queda establecida al valor indicado.
     *       Además, se registra como una puntuación individual para asegurar
     *       que las estadísticas como puntuación máxima y media se calculen correctamente.
     */
    public boolean setPuntuacionTotal(String username, int nuevaPuntuacion) {
        if (username == null) {
            return false;
        }
        
        try {
            // Obtener la puntuación total actual
            int puntuacionActual = ranking.getPuntuacionTotal(username);
            
            // Si no existe el usuario en el ranking, crear sus estadísticas
            // registrando la puntuación como una puntuación individual
            if (!ranking.perteneceRanking(username)) {
                // En este caso sí queremos incrementar el contador de partidas
                // ya que es la primera vez que se registra al usuario
                ranking.agregarPuntuacion(username, nuevaPuntuacion);
                repositorio.guardar(ranking);
                return true;
            }
            
            // Si hay cambio en la puntuación total, registramos el valor como puntuación individual
            // esto asegura que las estadísticas (máxima, media) se calculen correctamente
            if (puntuacionActual != nuevaPuntuacion) {
                // No incrementamos el contador de partidas, ya que simplemente estamos
                // actualizando la puntuación total
                ranking.agregarPuntuacionSinIncrementarPartidas(username, nuevaPuntuacion);
            }
            
            // Establecer la puntuación total
            boolean resultado = ranking.setPuntuacionTotal(username, nuevaPuntuacion);
            
            // Guardar cambios si la operación fue exitosa
            if (resultado) {
                repositorio.guardar(ranking);
            }
            
            return resultado;
        } catch (Exception e) {
            // Si ocurre algún error durante la operación, devolver false
            return false;
        }
    }
    
    /**
     * Incrementa la puntuación total de un jugador
     * 
     * @pre El jugador debe existir en el ranking y los puntos a añadir deben ser positivos.
     * @param nombre Nombre del jugador
     * @param puntosPartidas Puntos a añadir a la puntuación total existente
     * @return true si se ha incrementado correctamente, false en caso contrario
     * @post Si el jugador existe y los puntos son positivos, se incrementa la puntuación total
     *       y se registra también como una puntuación individual para asegurar que las estadísticas
     *       como puntuación máxima y media se calculen correctamente.
     * @throws NullPointerException Si el nombre es null.
     */
    public boolean addPuntuacionTotal(String nombre, int puntosPartidas) {
        // Verificar si existe el jugador en el ranking
        if (!perteneceRanking(nombre)) {
            return false;
        }
        
        // Verificar que los puntos a añadir sean positivos
        if (puntosPartidas <= 0) {
            return false;
        }
        
        // Obtener la puntuación total actual
        int puntuacionActual = getPuntuacionTotal(nombre);
        
        // Calcular la nueva puntuación total
        int nuevaPuntuacion = puntuacionActual + puntosPartidas;
        
        // Registrar la nueva puntuación como puntuación individual
        // Esto es crucial para que la puntuación máxima y media se calculen correctamente
        // Usamos agregarPuntuacionSinIncrementarPartidas para evitar el incremento del contador de partidas
        // ya que este podría haber sido incrementado por actualizarEstadisticasUsuario
        ranking.agregarPuntuacionSinIncrementarPartidas(nombre, puntosPartidas);
        
        // Establecer la nueva puntuación total
        boolean resultado = setPuntuacionTotal(nombre, nuevaPuntuacion);
        
        return resultado;
    }
    
    /**
     * Obtiene todos los nombres de usuario registrados en el ranking.
     * Filtra automáticamente los jugadores IA para no incluirlos en el ranking.
     * 
     * @pre No hay precondiciones específicas.
     * @return Lista de nombres de usuario humanos en el ranking
     * @post Se devuelve una lista (posiblemente vacía) con los nombres de usuarios humanos en el ranking.
     */
    public List<String> getUsuarios() {
        // Obtener todos los usuarios del ranking
        Set<String> todosUsuarios = ranking.getUsuarios();
        
        // Obtenemos una instancia del controlador de jugadores para verificar si son IA
        ControladorJugador controladorJugador = ControladorJugador.getInstance();
        
        return todosUsuarios.stream()
               .filter(nombre -> !controladorJugador.existeJugador(nombre) || !controladorJugador.esIA(nombre))
               .collect(Collectors.toList());
    }
    
    /**
     * Guarda los datos del ranking utilizando el repositorio.
     * 
     * @pre No hay precondiciones específicas.
     * @post Los datos del ranking se guardan a través del repositorio.
     *       En caso de error, se registra el mensaje en la consola de error.
     */
    public void guardarDatos() {
        try {
            repositorio.guardar(ranking);
        } catch (Exception e) {
            throw new ExceptionLoggingOperacion("Error al guardar el ranking: " + e.getMessage(), "persistencia", true);
        }
    }
    
    /**
     * Para compatibilidad con código existente.
     * Devuelve un mapa con las puntuaciones máximas por usuario.
     * 
     * @pre No hay precondiciones específicas.
     * @return Mapa de usuarios y sus puntuaciones máximas
     * @post Se devuelve un mapa donde las claves son los nombres de usuario
     *       y los valores son sus puntuaciones máximas.
     */
    public Map<String, Integer> getMapaPuntuacionesMaximas() {
        return ranking.getMapaPuntuacionesMaximas();
    }
    
    /**
     * Para compatibilidad con código existente.
     * Devuelve un mapa con las puntuaciones medias por usuario.
     * 
     * @pre No hay precondiciones específicas.
     * @return Mapa de usuarios y sus puntuaciones medias
     * @post Se devuelve un mapa donde las claves son los nombres de usuario
     *       y los valores son sus puntuaciones medias.
     */
    public Map<String, Double> getMapaPuntuacionesMedias() {
        return ranking.getMapaPuntuacionesMedias();
    }
    
    /**
     * Para compatibilidad con código existente.
     * Devuelve un mapa con las partidas jugadas por usuario.
     * 
     * @pre No hay precondiciones específicas.
     * @return Mapa de usuarios y su número de partidas jugadas
     * @post Se devuelve un mapa donde las claves son los nombres de usuario
     *       y los valores son sus números de partidas jugadas.
     */
    public Map<String, Integer> getMapaPartidasJugadas() {
        return ranking.getMapaPartidasJugadas();
    }
    
    /**
     * Para compatibilidad con código existente.
     * Devuelve un mapa con las victorias por usuario.
     * 
     * @pre No hay precondiciones específicas.
     * @return Mapa de usuarios y su número de victorias
     * @post Se devuelve un mapa donde las claves son los nombres de usuario
     *       y los valores son sus números de victorias.
     */
    public Map<String, Integer> getMapaVictorias() {
        return ranking.getMapaVictorias();
    }

    /**
     * Ordena una lista de usuarios según el criterio de ranking especificado.
     * 
     * @pre La lista de usuarios puede ser null o vacía.
     * @param usuarios Lista de usuarios a ordenar
     * @param criterio Criterio de ordenación ("maxima", "media", "partidas", "victorias", "total")
     * @return Nueva lista ordenada de usuarios
     * @post Se devuelve una nueva lista ordenada según el criterio especificado.
     *       Si la lista de entrada es null o vacía, se devuelve una lista vacía.
     * @throws NullPointerException Si el criterio es null.
     * @throws IllegalArgumentException Si el criterio no es uno de los valores permitidos.
     */
    public List<String> ordenarUsuariosPorCriterio(List<String> usuarios, String criterio) {
        if (usuarios == null || usuarios.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Guardar la estrategia actual
        String estrategiaOriginal = ranking.getEstrategiaActual();
        
        // Cambiar temporalmente a la estrategia solicitada
        ranking.setEstrategia(criterio);
        
        // Filtrar los usuarios que pertenecen al ranking
        List<String> usuariosDelRanking = usuarios.stream()
                .filter(ranking::perteneceRanking)
                .collect(Collectors.toList());
        
        if (usuariosDelRanking.isEmpty()) {
            // Restaurar la estrategia original
            ranking.setEstrategia(estrategiaOriginal);
            return new ArrayList<>();
        }
        
        // Obtener una copia del ranking con el criterio especificado
        List<String> rankingOrdenado = new ArrayList<>(ranking.getRanking());
        
        // Filtrar para mantener solo los usuarios solicitados y en el orden correcto
        List<String> resultado = rankingOrdenado.stream()
                .filter(usuariosDelRanking::contains)
                .collect(Collectors.toList());
        
        // Restaurar la estrategia original
        ranking.setEstrategia(estrategiaOriginal);
        
        return resultado;
    }

    /**
     * Agrega una puntuación individual para un usuario específico sin incrementar el contador de partidas.
     * Útil cuando ya se ha incrementado el contador mediante actualizarEstadisticasUsuario.
     * 
     * @pre El nombre debe ser no nulo y no vacío, y la puntuación debe ser no negativa.
     * @param nombre Nombre del usuario
     * @param puntuacion Puntuación a agregar
     * @return true si se agregó correctamente, false en caso contrario
     * @post Si el nombre es válido y la puntuación no es negativa, se agrega la puntuación
     *       sin incrementar el contador de partidas y se devuelve true.
     *       En caso contrario, se devuelve false.
     * @throws NullPointerException Si el nombre es null.
     */
    public boolean agregarPuntuacionSinIncrementarPartidas(String nombre, int puntuacion) {
        // Verificar que se proporciona un usuario válido
        if (nombre == null || nombre.isEmpty()) {
            return false;
        }
        
        if (puntuacion < 0) {
            return false; // No se permiten puntuaciones negativas
        }
        
        // Delegar al modelo de Ranking
        boolean resultado = ranking.agregarPuntuacionSinIncrementarPartidas(nombre, puntuacion);
        
        // Guardar cambios si la operación fue exitosa
        if (resultado) {
            repositorio.guardar(ranking);
        }
        
        return resultado;
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
        // Verificar que se proporciona un usuario válido
        if (nombre == null || nombre.isEmpty()) {
            return false;
        }
        
        if (puntuacion < 0) {
            return false; // No se permiten puntuaciones negativas
        }
        
        // Delegar al modelo de Ranking para añadir la puntuación individual
        boolean resultado = ranking.agregarPuntuacion(nombre, puntuacion);
        
        // Guardar cambios si la operación fue exitosa
        if (resultado) {
            repositorio.guardar(ranking);
        }
        
        return resultado;
    }
}