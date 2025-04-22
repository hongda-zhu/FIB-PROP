package scrabble.domain.controllers.subcontrollers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import scrabble.domain.models.rankingStrategy.PlayerRankingStats;
import scrabble.domain.models.rankingStrategy.RankingDataProvider;
import scrabble.domain.models.rankingStrategy.RankingOrderStrategy;
import scrabble.domain.models.rankingStrategy.RankingOrderStrategyFactory;

/**
 * Controlador para la gestión del ranking de jugadores.
 * Implementa el patrón Singleton para garantizar una única instancia.
 */
public class ControladorRanking implements RankingDataProvider {
    private static final long serialVersionUID = 1L;
    private static transient ControladorRanking instance;
    
    // Estructura simplificada para almacenar las estadísticas de los usuarios
    private Map<String, PlayerRankingStats> estadisticasUsuarios;
    
    // Estrategia actual de ordenación
    private String estrategiaActual;
    
    private static final String RANKING_FILE = "src/main/resources/persistencias/ranking.dat";
    
    /**
     * Constructor privado para implementar el patrón Singleton.
     * Inicializa las estructuras de datos y carga los datos existentes.
     * 
     * @pre No hay precondiciones específicas.
     * @post Se inicializa una nueva instancia con un mapa de estadísticas vacío
     *       y se cargan los datos persistentes si están disponibles.
     *       La estrategia por defecto se establece a "total".
     */
    private ControladorRanking() {
        this.estadisticasUsuarios = new HashMap<>();
        this.estrategiaActual = "total"; // Estrategia por defecto cambiada a puntuación total
        
        cargarDatos();
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
     * Agrega una puntuación para un usuario específico.
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
        
        // Obtener o crear las estadísticas del usuario
        PlayerRankingStats stats = estadisticasUsuarios.computeIfAbsent(nombre, 
                                                                      PlayerRankingStats::new);
        // Agregar puntuación (la clase PlayerRankingStats actualiza internamente máximo y media)
        stats.addPuntuacion(puntuacion);
        
        return true;
    }
    
    /**
     * Actualiza las estadísticas de un usuario (partidas jugadas y victoria).
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
        // Verificar que se proporciona un usuario válido
        if (nombre == null || nombre.isEmpty()) {
            return false;
        }
        
        // Obtener o crear las estadísticas del usuario
        PlayerRankingStats stats = estadisticasUsuarios.computeIfAbsent(nombre, 
                                                                      PlayerRankingStats::new);
        // Actualizar estadísticas
        stats.actualizarEstadisticas(esVictoria);
        
        return true;
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
        return estadisticasUsuarios.containsKey(nombre);
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
        if (!perteneceRanking(nombre)) {
            return false;
        }
        
        // Eliminar puntuación y actualizar estadísticas automáticamente
        return estadisticasUsuarios.get(nombre).removePuntuacion(puntuacion);
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
        return perteneceRanking(nombre) && 
               estadisticasUsuarios.get(nombre).contienePuntuacion(puntuacion);
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
        if (!perteneceRanking(nombre)) {
            return false;
        }
        
        // Eliminar usuario del mapa de estadísticas
        estadisticasUsuarios.remove(nombre);
        
        // Guardar cambios inmediatamente
        guardarDatos();
        
        return !perteneceRanking(nombre);
    }
    
    /**
     * Cambia la estrategia de ordenación del ranking.
     * 
     * @pre No hay precondiciones específicas.
     * @param criterio Criterio de ordenación: "maxima", "media", "partidas" o "ratio" (victorias totales)
     * @post La estrategia de ordenación del ranking se actualiza al criterio especificado.
     * @throws NullPointerException Si el criterio es null.
     * @throws IllegalArgumentException Si el criterio no es uno de los valores permitidos.
     */
    public void setEstrategia(String criterio) {
        this.estrategiaActual = criterio;
    }
    
    /**
     * Obtiene la estrategia actual de ordenación.
     * 
     * @pre No hay precondiciones específicas.
     * @return Nombre de la estrategia actual
     * @post Se devuelve el nombre de la estrategia actual de ordenación.
     */
    public String getEstrategiaActual() {
        RankingOrderStrategy estrategia = RankingOrderStrategyFactory.createStrategy(estrategiaActual, this);
        return estrategia.getNombre();
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
        // Verificar si estadisticasUsuarios es null y manejarlo
        if (estadisticasUsuarios == null) {
            estadisticasUsuarios = new HashMap<>();
            return new ArrayList<>();
        }
        
        // Obtenemos una instancia del controlador de jugadores para verificar si son IA
        ControladorJugador controladorJugador = ControladorJugador.getInstance();
        
        // Filtramos los jugadores IA y obtenemos solo los nombres de usuarios humanos
        List<String> usuariosHumanos = estadisticasUsuarios.keySet().stream()
            .filter(nombre -> !controladorJugador.existeJugador(nombre) || 
                    !controladorJugador.esIA(nombre))
            .collect(Collectors.toList());
        
        // Obtenemos la estrategia de ordenación, pasando this como proveedor de datos
        RankingOrderStrategy estrategia = RankingOrderStrategyFactory.createStrategy(estrategiaActual, this);
        
        // Ordenamos los nombres de usuario directamente usando la estrategia como Comparator
        Collections.sort(usuariosHumanos, estrategia);
        
        // Devolvemos la lista ordenada
        return usuariosHumanos;
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
        // Verificar si estadisticasUsuarios es null y manejarlo
        if (estadisticasUsuarios == null) {
            estadisticasUsuarios = new HashMap<>();
            return new ArrayList<>();
        }
        
        // Obtenemos una instancia del controlador de jugadores para verificar si son IA
        ControladorJugador controladorJugador = ControladorJugador.getInstance();
        
        // Filtramos los jugadores IA y obtenemos solo los nombres de usuarios humanos
        List<String> usuariosHumanos = estadisticasUsuarios.keySet().stream()
            .filter(nombre -> !controladorJugador.existeJugador(nombre) || 
                    !controladorJugador.esIA(nombre))
            .collect(Collectors.toList());
        
        // Obtenemos la estrategia de ordenación específica, pasando this como proveedor de datos
        RankingOrderStrategy estrategia = RankingOrderStrategyFactory.createStrategy(criterio, this);
        
        // Ordenamos los nombres de usuario directamente usando la estrategia como Comparator
        Collections.sort(usuariosHumanos, estrategia);
        
        // Devolvemos la lista ordenada
        return usuariosHumanos;
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
        return perteneceRanking(nombre) ? 
               new ArrayList<>(estadisticasUsuarios.get(nombre).getPuntuaciones()) : 
               new ArrayList<>();
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
        return perteneceRanking(nombre) ? 
               estadisticasUsuarios.get(nombre).getPuntuacionMaxima() : 0;
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
        return perteneceRanking(nombre) ? 
               estadisticasUsuarios.get(nombre).getPuntuacionMedia() : 0.0;
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
        return perteneceRanking(nombre) ? 
               estadisticasUsuarios.get(nombre).getPartidasJugadas() : 0;
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
        return perteneceRanking(nombre) ? 
               estadisticasUsuarios.get(nombre).getVictorias() : 0;
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
    public int getPuntuacionTotal(String nombre) {
        return perteneceRanking(nombre) ? 
               estadisticasUsuarios.get(nombre).getPuntuacionTotal() : 0;
    }
    
    /**
     * Establece la puntuación total de un jugador
     * 
     * @pre El jugador debe existir en el ranking y la puntuación debe ser no negativa.
     * @param nombre Nombre del jugador
     * @param puntuacionAgregada Puntuación total a establecer
     * @return true si se ha establecido correctamente, false en caso contrario
     * @post Si el jugador existe y la puntuación es no negativa, se establece la puntuación total
     *       y se devuelve true. En caso contrario, se devuelve false.
     * @throws NullPointerException Si el nombre es null.
     */
    public boolean setPuntuacionTotal(String nombre, int puntuacionAgregada) {
        //  verifica si existe el jugador en el ranking
        if(!perteneceRanking(nombre)) {
            return false;
        }
        
        // Obtener o crear las estadísticas del usuario
        PlayerRankingStats stats = estadisticasUsuarios.get(nombre);
        if (puntuacionAgregada < 0) {
            return false; // No se permiten puntuaciones negativas
        }
        
        // Actualizar estadísticas
        stats.setPuntuacionTotal(puntuacionAgregada);
        
        // Guardar cambios inmediatamente
        guardarDatos();
        
        return true;
    }
    
    /**
     * Incrementa la puntuación total de un jugador
     * 
     * @pre El jugador debe existir en el ranking y los puntos a añadir deben ser positivos.
     * @param nombre Nombre del jugador
     * @param puntosPartidas Puntos a añadir a la puntuación total existente
     * @return true si se ha incrementado correctamente, false en caso contrario
     * @post Si el jugador existe y los puntos son positivos, se incrementa la puntuación total
     *       y se devuelve true. En caso contrario, se devuelve false.
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
        int nuevaPuntuacionTotal = puntuacionActual + puntosPartidas;
        
        // Establecer la nueva puntuación total
        return setPuntuacionTotal(nombre, nuevaPuntuacionTotal);
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
        // Garantizar que estadisticasUsuarios nunca sea null
        if (estadisticasUsuarios == null) {
            estadisticasUsuarios = new HashMap<>();
            return new ArrayList<>();
        }
        
        // Obtenemos una instancia del controlador de jugadores para verificar si son IA
        ControladorJugador controladorJugador = ControladorJugador.getInstance();
        
        return estadisticasUsuarios.keySet().stream()
               .filter(nombre -> !controladorJugador.existeJugador(nombre) || !controladorJugador.esIA(nombre))
               .collect(Collectors.toList());
    }
    
    /**
     * Guarda los datos del ranking en un archivo serializado.
     * 
     * @pre No hay precondiciones específicas.
     * @post Los datos del ranking se guardan en el archivo especificado por RANKING_FILE.
     *       En caso de error, se registra el mensaje en la consola de error.
     */
    public void guardarDatos() {
        try {
            FileOutputStream fos = new FileOutputStream(RANKING_FILE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            oos.close();
            fos.close();
        } catch (IOException e) {
            System.err.println("Error al guardar el ranking: " + e.getMessage());
        }
    }
    
    /**
     * Carga los datos del ranking desde un archivo serializado.
     * 
     * @pre No hay precondiciones específicas.
     * @post Si el archivo existe y se puede leer correctamente, se cargan los datos del ranking.
     *       En caso de error, se inicializan estructuras vacías y se registra el mensaje en la consola de error.
     */
    private void cargarDatos() {
        File rankingFile = new File(RANKING_FILE);
        if (rankingFile.exists()) {
            try {
                FileInputStream fis = new FileInputStream(RANKING_FILE);
                ObjectInputStream ois = new ObjectInputStream(fis);
                ControladorRanking loaded = (ControladorRanking) ois.readObject();
                ois.close();
                fis.close();
                
                // Copiar datos del ranking cargado
                // Asegurarse de que estadisticasUsuarios nunca sea null
                if (loaded.estadisticasUsuarios != null) {
                    this.estadisticasUsuarios = loaded.estadisticasUsuarios;
                } else {
                    this.estadisticasUsuarios = new HashMap<>();
                }
                
                this.estrategiaActual = loaded.estrategiaActual;
                
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error al cargar el ranking: " + e.getMessage());
                // Inicializar con datos vacíos
                this.estadisticasUsuarios = new HashMap<>();
            }
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
        Map<String, Integer> resultado = new HashMap<>();
        // Verificación de nulidad para prevenir NPE
        if (estadisticasUsuarios == null) {
            estadisticasUsuarios = new HashMap<>();
            return resultado;
        }
        
        for (Map.Entry<String, PlayerRankingStats> entry : estadisticasUsuarios.entrySet()) {
            resultado.put(entry.getKey(), entry.getValue().getPuntuacionMaxima());
        }
        return resultado;
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
        Map<String, Double> resultado = new HashMap<>();
        // Verificación de nulidad para prevenir NPE
        if (estadisticasUsuarios == null) {
            estadisticasUsuarios = new HashMap<>();
            return resultado;
        }
        
        for (Map.Entry<String, PlayerRankingStats> entry : estadisticasUsuarios.entrySet()) {
            resultado.put(entry.getKey(), entry.getValue().getPuntuacionMedia());
        }
        return resultado;
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
        Map<String, Integer> resultado = new HashMap<>();
        // Verificación de nulidad para prevenir NPE
        if (estadisticasUsuarios == null) {
            estadisticasUsuarios = new HashMap<>();
            return resultado;
        }
        
        for (Map.Entry<String, PlayerRankingStats> entry : estadisticasUsuarios.entrySet()) {
            resultado.put(entry.getKey(), entry.getValue().getPartidasJugadas());
        }
        return resultado;
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
        Map<String, Integer> resultado = new HashMap<>();
        // Verificación de nulidad para prevenir NPE
        if (estadisticasUsuarios == null) {
            estadisticasUsuarios = new HashMap<>();
            return resultado;
        }
        
        for (Map.Entry<String, PlayerRankingStats> entry : estadisticasUsuarios.entrySet()) {
            resultado.put(entry.getKey(), entry.getValue().getVictorias());
        }
        return resultado;
    }

    /**
     * Ordena una lista de usuarios según el criterio de ranking especificado.
     * 
     * @pre La lista de usuarios puede ser null o vacía.
     * @param usuarios Lista de usuarios a ordenar
     * @param criterio Criterio de ordenación ("maxima", "media", "partidas", "victorias")
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
        
        // Verificar si estadisticasUsuarios es null y manejarlo
        if (estadisticasUsuarios == null) {
            estadisticasUsuarios = new HashMap<>();
            return new ArrayList<>();
        }
        
        // Filtrar los usuarios que pertenecen al ranking
        List<String> usuariosDelRanking = usuarios.stream()
                .filter(this::perteneceRanking)
                .collect(Collectors.toList());
        
        if (usuariosDelRanking.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Usar la estrategia adecuada según el criterio, pasando this como proveedor de datos
        RankingOrderStrategy estrategia = RankingOrderStrategyFactory.createStrategy(criterio, this);
        
        // Ordenar directamente los nombres de usuario usando la estrategia como Comparator
        Collections.sort(usuariosDelRanking, estrategia);
        
        // Devolver la lista ordenada
        return usuariosDelRanking;
    }
}