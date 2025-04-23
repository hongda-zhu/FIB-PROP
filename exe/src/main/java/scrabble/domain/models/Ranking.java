package scrabble.domain.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import scrabble.domain.models.rankingStrategy.PlayerRankingStats;
import scrabble.domain.models.rankingStrategy.RankingDataProvider;
import scrabble.domain.models.rankingStrategy.RankingOrderStrategy;
import scrabble.domain.models.rankingStrategy.RankingOrderStrategyFactory;

/**
 * Clase que representa el ranking del juego Scrabble.
 * Almacena y gestiona las puntuaciones de los usuarios utilizando el patrón Strategy.
 */
public class Ranking implements RankingDataProvider {
    private static final long serialVersionUID = 1L;
    
    // Estructura simplificada para almacenar las estadísticas de los usuarios
    private Map<String, PlayerRankingStats> estadisticasUsuarios;
    private RankingOrderStrategy estrategiaActual;
    
    /**
     * Constructor de la clase Ranking.
     * Inicializa las estructuras de datos para almacenar puntuaciones.
     * 
     * @pre No hay precondiciones específicas.
     * @post Se crea una nueva instancia de Ranking con una estructura vacía para 
     *       almacenar estadísticas y una estrategia de ordenación por puntuación total.
     */
    public Ranking() {
        this.estadisticasUsuarios = new HashMap<>();
        
        // Por defecto usamos la estrategia de puntuación total
        this.estrategiaActual = RankingOrderStrategyFactory.createStrategy("total", this);
    }
    
    /**
     * Establece la estrategia de ordenación del ranking.
     * 
     * @pre criterio debe ser uno de los valores aceptados por RankingOrderStrategyFactory.
     * @param criterio Criterio de ordenación
     * @post La estrategia de ordenación del ranking se actualiza según el criterio especificado.
     * @throws IllegalArgumentException si el criterio no es válido
     * @throws NullPointerException si criterio es null
     */
    public void setEstrategia(String criterio) {
        if (criterio == null) {
            throw new NullPointerException("El criterio no puede ser null");
        }
        this.estrategiaActual = RankingOrderStrategyFactory.createStrategy(criterio, this);
    }
    
    /**
     * Obtiene el nombre de la estrategia actual.
     * 
     * @pre No hay precondiciones específicas.
     * @return Nombre de la estrategia
     * @post Se devuelve el nombre de la estrategia actual de ordenación del ranking.
     */
    public String getEstrategiaActual() {
        return estrategiaActual.getNombre();
    }
    
    /**
     * Agrega una nueva puntuación para un usuario.
     * 
     * @pre nombre no debe ser null. puntuacion debe ser no negativa.
     * @param nombre Nombre del usuario
     * @param puntuacion Puntuación a agregar
     * @return true si se agregó correctamente, false en caso contrario
     * @post Si la puntuación es no negativa, se agrega a las estadísticas del usuario
     *       y se devuelve true. Si ya existe un usuario con ese nombre, se actualiza.
     *       Si la puntuación es negativa, se devuelve false.
     * @throws NullPointerException si nombre es null
     */
    public boolean agregarPuntuacion(String nombre, int puntuacion) {
        if (nombre == null) {
            throw new NullPointerException("El nombre del usuario no puede ser null");
        }
        
        if (puntuacion < 0) {
            return false; // No se permiten puntuaciones negativas
        }
        
        // Obtener o crear estadísticas para este usuario
        PlayerRankingStats stats = estadisticasUsuarios.computeIfAbsent(nombre, 
                                                                 PlayerRankingStats::new);
        // Agregar puntuación sin incrementar partidas
        stats.addPuntuacionSinIncrementarPartidas(puntuacion);
        
        return true;
    }
    
    /**
     * Actualiza los contadores de partidas jugadas y victorias para un usuario.
     * 
     * @pre nombre no debe ser null.
     * @param nombre Nombre del usuario
     * @param esVictoria Indica si es una victoria
     * @post Se actualiza el contador de partidas jugadas del usuario y, si esVictoria es true,
     *       también se incrementa el contador de victorias. Si el usuario no existía, se crea.
     * @throws NullPointerException si nombre es null
     */
    public void actualizarEstadisticasUsuario(String nombre, boolean esVictoria) {
        if (nombre == null) {
            throw new NullPointerException("El nombre del usuario no puede ser null");
        }
        
        // Obtener o crear estadísticas para este usuario
        PlayerRankingStats stats = estadisticasUsuarios.computeIfAbsent(nombre, 
                                                                 PlayerRankingStats::new);
        // Actualizar estadísticas
        stats.actualizarEstadisticas(esVictoria);
    }
    
    /**
     * Elimina una puntuación específica de un usuario.
     * 
     * @pre nombre no debe ser null.
     * @param nombre Nombre del usuario
     * @param puntuacion Puntuación a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     * @post Si el usuario existe y tiene la puntuación especificada, se elimina y se devuelve true.
     *       Si el usuario no existe o no tiene esa puntuación, se devuelve false.
     * @throws NullPointerException si nombre es null
     */
    public boolean eliminarPuntuacion(String nombre, int puntuacion) {
        if (nombre == null) {
            throw new NullPointerException("El nombre del usuario no puede ser null");
        }
        
        if (!perteneceRanking(nombre)) {
            return false;
        }
        
        // Eliminar puntuación
        return estadisticasUsuarios.get(nombre).removePuntuacion(puntuacion);
    }
    
    /**
     * Verifica si existe una puntuación específica para un usuario.
     * 
     * @pre nombre no debe ser null.
     * @param nombre Nombre del usuario
     * @param puntuacion Puntuación a verificar
     * @return true si existe la puntuación, false en caso contrario
     * @post Se devuelve true si el usuario existe y tiene la puntuación especificada,
     *       false en caso contrario.
     * @throws NullPointerException si nombre es null
     */
    public boolean existePuntuacion(String nombre, int puntuacion) {
        if (nombre == null) {
            throw new NullPointerException("El nombre del usuario no puede ser null");
        }
        
        return perteneceRanking(nombre) && 
               estadisticasUsuarios.get(nombre).contienePuntuacion(puntuacion);
    }
    
    /**
     * Obtiene todas las puntuaciones de un usuario específico.
     * 
     * @pre nombre no debe ser null.
     * @param nombre Nombre del usuario
     * @return Lista de puntuaciones del usuario
     * @post Se devuelve una lista con todas las puntuaciones del usuario si existe,
     *       o una lista vacía si no existe.
     * @throws NullPointerException si nombre es null
     */
    public List<Integer> getPuntuacionesUsuario(String nombre) {
        if (nombre == null) {
            throw new NullPointerException("El nombre del usuario no puede ser null");
        }
        
        return perteneceRanking(nombre) ? 
               estadisticasUsuarios.get(nombre).getPuntuaciones() : 
               Collections.emptyList();
    }
    
    /**
     * Obtiene la puntuación máxima de un usuario específico.
     * 
     * @pre nombre no debe ser null.
     * @param nombre Nombre del usuario
     * @return Puntuación máxima del usuario
     * @post Se devuelve la puntuación máxima del usuario si existe, o 0 si no existe.
     * @throws NullPointerException si nombre es null
     */
    @Override
    public int getPuntuacionMaxima(String nombre) {
        if (nombre == null) {
            throw new NullPointerException("El nombre del usuario no puede ser null");
        }
        
        return perteneceRanking(nombre) ? 
               estadisticasUsuarios.get(nombre).getPuntuacionMaxima() : 0;
    }
    
    /**
     * Obtiene la puntuación media de un usuario específico.
     * 
     * @pre nombre no debe ser null.
     * @param nombre Nombre del usuario
     * @return Puntuación media del usuario
     * @post Se devuelve la puntuación media del usuario si existe, o 0.0 si no existe.
     * @throws NullPointerException si nombre es null
     */
    @Override
    public double getPuntuacionMedia(String nombre) {
        if (nombre == null) {
            throw new NullPointerException("El nombre del usuario no puede ser null");
        }
        
        return perteneceRanking(nombre) ? 
               estadisticasUsuarios.get(nombre).getPuntuacionMedia() : 0.0;
    }
    
    /**
     * Obtiene el número de partidas jugadas por un usuario.
     * 
     * @pre nombre no debe ser null.
     * @param nombre Nombre del usuario
     * @return Número de partidas jugadas
     * @post Se devuelve el número de partidas jugadas por el usuario si existe, o 0 si no existe.
     * @throws NullPointerException si nombre es null
     */
    @Override
    public int getPartidasJugadas(String nombre) {
        if (nombre == null) {
            throw new NullPointerException("El nombre del usuario no puede ser null");
        }
        
        return perteneceRanking(nombre) ? 
               estadisticasUsuarios.get(nombre).getPartidasJugadas() : 0;
    }
    
    /**
     * Obtiene el número de victorias de un usuario.
     * 
     * @pre nombre no debe ser null.
     * @param nombre Nombre del usuario
     * @return Número de victorias
     * @post Se devuelve el número de victorias del usuario si existe, o 0 si no existe.
     * @throws NullPointerException si nombre es null
     */
    @Override
    public int getVictorias(String nombre) {
        if (nombre == null) {
            throw new NullPointerException("El nombre del usuario no puede ser null");
        }
        
        return perteneceRanking(nombre) ? 
               estadisticasUsuarios.get(nombre).getVictorias() : 0;
    }
    
    /**
     * Obtiene el ranking ordenado según la estrategia actual.
     * 
     * @pre No hay precondiciones específicas.
     * @return Lista de usuarios ordenados según la estrategia actual
     * @post Se devuelve una lista con los nombres de todos los usuarios ordenados
     *       según la estrategia de ordenación actual.
     */
    public List<String> getRanking() {
        // Obtener los nombres de usuario
        List<String> usuarios = new ArrayList<>(estadisticasUsuarios.keySet());
        
        // Ordenar la lista usando la estrategia actual como Comparator
        Collections.sort(usuarios, estrategiaActual);
        
        return usuarios;
    }
    
    /**
     * Obtiene el ranking ordenado según una estrategia específica.
     * 
     * @pre criterio debe ser uno de los valores aceptados por RankingOrderStrategyFactory.
     * @param criterio Criterio de ordenación
     * @return Lista de usuarios ordenados según el criterio especificado
     * @post Se devuelve una lista con los nombres de todos los usuarios ordenados
     *       según el criterio de ordenación especificado.
     * @throws IllegalArgumentException si el criterio no es válido
     * @throws NullPointerException si criterio es null
     */
    public List<String> getRanking(String criterio) {
        if (criterio == null) {
            throw new NullPointerException("El criterio no puede ser null");
        }
        
        // Obtener los nombres de usuario
        List<String> usuarios = new ArrayList<>(estadisticasUsuarios.keySet());
        
        // Crear la estrategia específica
        RankingOrderStrategy estrategia = RankingOrderStrategyFactory.createStrategy(criterio, this);
        
        // Ordenar la lista usando la estrategia como Comparator
        Collections.sort(usuarios, estrategia);
        
        return usuarios;
    }
    
    /**
     * Obtiene una representación en formato mapa del ranking por puntuación máxima.
     * Para compatibilidad con código existente.
     * 
     * @pre No hay precondiciones específicas.
     * @return Mapa con usuarios y sus puntuaciones máximas
     * @post Se devuelve un mapa donde las claves son los nombres de usuario y
     *       los valores son las puntuaciones máximas correspondientes.
     */
    public Map<String, Integer> getMapaPuntuacionesMaximas() {
        Map<String, Integer> resultado = new HashMap<>();
        for (Map.Entry<String, PlayerRankingStats> entry : estadisticasUsuarios.entrySet()) {
            resultado.put(entry.getKey(), entry.getValue().getPuntuacionMaxima());
        }
        return resultado;
    }
    
    /**
     * Obtiene una representación en formato mapa del ranking por puntuación media.
     * Para compatibilidad con código existente.
     * 
     * @pre No hay precondiciones específicas.
     * @return Mapa con usuarios y sus puntuaciones medias
     * @post Se devuelve un mapa donde las claves son los nombres de usuario y
     *       los valores son las puntuaciones medias correspondientes.
     */
    public Map<String, Double> getMapaPuntuacionesMedias() {
        Map<String, Double> resultado = new HashMap<>();
        for (Map.Entry<String, PlayerRankingStats> entry : estadisticasUsuarios.entrySet()) {
            resultado.put(entry.getKey(), entry.getValue().getPuntuacionMedia());
        }
        return resultado;
    }
    
    /**
     * Obtiene una representación en formato mapa del ranking por partidas jugadas.
     * Para compatibilidad con código existente.
     * 
     * @pre No hay precondiciones específicas.
     * @return Mapa con usuarios y su número de partidas jugadas
     * @post Se devuelve un mapa donde las claves son los nombres de usuario y
     *       los valores son el número de partidas jugadas correspondientes.
     */
    public Map<String, Integer> getMapaPartidasJugadas() {
        Map<String, Integer> resultado = new HashMap<>();
        for (Map.Entry<String, PlayerRankingStats> entry : estadisticasUsuarios.entrySet()) {
            resultado.put(entry.getKey(), entry.getValue().getPartidasJugadas());
        }
        return resultado;
    }
    
    /**
     * Obtiene una representación en formato mapa del ranking por victorias.
     * Para compatibilidad con código existente.
     * 
     * @pre No hay precondiciones específicas.
     * @return Mapa con usuarios y su número de victorias
     * @post Se devuelve un mapa donde las claves son los nombres de usuario y
     *       los valores son el número de victorias correspondientes.
     */
    public Map<String, Integer> getMapaVictorias() {
        Map<String, Integer> resultado = new HashMap<>();
        for (Map.Entry<String, PlayerRankingStats> entry : estadisticasUsuarios.entrySet()) {
            resultado.put(entry.getKey(), entry.getValue().getVictorias());
        }
        return resultado;
    }
    
    /**
     * Comprueba si un usuario forma parte del ranking o no.
     * 
     * @pre nombre no debe ser null.
     * @param nombre Nombre del usuario a comprobar
     * @return true si tiene alguna puntuación, false en caso contrario.
     * @post Se devuelve true si el usuario existe en el ranking, false en caso contrario.
     * @throws NullPointerException si nombre es null
     */    
    public boolean perteneceRanking(String nombre) {
        if (nombre == null) {
            throw new NullPointerException("El nombre del usuario no puede ser null");
        }
        
        return estadisticasUsuarios.containsKey(nombre);
    }

    /**
     * Elimina todas las puntuaciones de un usuario.
     * 
     * @pre nombre no debe ser null.
     * @param nombre Nombre del usuario
     * @return true si se eliminaron correctamente, false en caso contrario
     * @post Si el usuario existía en el ranking, se elimina completamente
     *       y se devuelve true. Si no existía, se devuelve false.
     * @throws NullPointerException si nombre es null
     */
    public boolean eliminarUsuario(String nombre) {
        if (nombre == null) {
            throw new NullPointerException("El nombre del usuario no puede ser null");
        }
        
        return estadisticasUsuarios.remove(nombre) != null;
    }
    
    /**
     * Obtiene todos los nombres de usuario registrados en el ranking.
     * 
     * @pre No hay precondiciones específicas.
     * @return Conjunto de nombres de usuario
     * @post Se devuelve un conjunto con todos los nombres de usuario registrados en el ranking.
     */
    public Set<String> getUsuarios() {
        return new HashSet<>(estadisticasUsuarios.keySet());
    }

    /**
     * Obtiene la puntuación total acumulada de un usuario específico.
     * Suma todas las puntuaciones individuales registradas para el usuario.
     * 
     * @pre nombre no debe ser null.
     * @param nombre Nombre del usuario
     * @return Puntuación total acumulada
     * @post Se devuelve la puntuación total acumulada del usuario si existe, o 0 si no existe.
     * @throws NullPointerException si nombre es null
     */
    @Override
    public int getPuntuacionTotal(String nombre) {
        if (nombre == null) {
            throw new NullPointerException("El nombre del usuario no puede ser null");
        }
        
        return perteneceRanking(nombre) ? 
               estadisticasUsuarios.get(nombre).getPuntuacionTotal() : 0;
    }

    /**
     * Establece la puntuación total de un jugador, eliminando todas sus puntuaciones anteriores
     * y añadiendo la nueva puntuación total como una única entrada.
     * 
     * @pre El jugador debe existir en el ranking y la puntuación debe ser no negativa.
     * @param nombre Nombre del jugador
     * @param puntuacionAgregada Puntuación total a establecer
     * @return true si se ha establecido correctamente, false en caso contrario
     * @post Si el jugador existe y la puntuación es no negativa, se establece la nueva puntuación total
     *       sin afectar a las puntuaciones individuales existentes, y se devuelve true.
     *       En caso contrario, se devuelve false.
     * @throws NullPointerException Si el nombre es null.
     */
    public boolean setPuntuacionTotal(String nombre, int puntuacionAgregada) {
        if (nombre == null) {
            throw new NullPointerException("El nombre no puede ser null");
        }
        
        if (!perteneceRanking(nombre)) {
            return false;
        }
        
        if (puntuacionAgregada < 0) {
            return false; // No se permiten puntuaciones negativas
        }
        
        // Obtener las estadísticas del jugador
        PlayerRankingStats stats = estadisticasUsuarios.get(nombre);
        
        // Establecer directamente la puntuación total sin afectar a las puntuaciones individuales
        stats.setPuntuacionTotal(puntuacionAgregada);
        
        return true;
    }

    /**
     * Agrega una nueva puntuación para un usuario sin incrementar el contador de partidas.
     * Útil cuando ya se ha incrementado el contador mediante actualizarEstadisticasUsuario.
     * 
     * @pre nombre no debe ser null. puntuacion debe ser no negativa.
     * @param nombre Nombre del usuario
     * @param puntuacion Puntuación a agregar
     * @return true si se agregó correctamente, false en caso contrario
     * @post Si la puntuación es no negativa, se agrega a las estadísticas del usuario
     *       y se devuelve true. Si ya existe un usuario con ese nombre, se actualiza.
     *       Si la puntuación es negativa, se devuelve false.
     * @throws NullPointerException si nombre es null
     */
    public boolean agregarPuntuacionSinIncrementarPartidas(String nombre, int puntuacion) {
        if (nombre == null) {
            throw new NullPointerException("El nombre del usuario no puede ser null");
        }
        
        if (puntuacion < 0) {
            return false; // No se permiten puntuaciones negativas
        }
        
        // Obtener o crear estadísticas para este usuario
        PlayerRankingStats stats = estadisticasUsuarios.computeIfAbsent(nombre, 
                                                                 PlayerRankingStats::new);
        // Agregar puntuación sin incrementar partidas
        stats.addPuntuacionSinIncrementarPartidas(puntuacion);
        
        return true;
    }
}