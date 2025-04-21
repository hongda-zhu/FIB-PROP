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
    
    private static final String RANKING_FILE = "ranking.dat";
    
    /**
     * Constructor privado para implementar el patrón Singleton.
     * Inicializa las estructuras de datos y carga los datos existentes.
     */
    private ControladorRanking() {
        this.estadisticasUsuarios = new HashMap<>();
        this.estrategiaActual = "maxima"; // Estrategia por defecto
        
        cargarDatos();
    }
    
    /**
     * Obtiene la instancia única del controlador (Singleton).
     * 
     * @return Instancia de ControladorRanking
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
     * @param nombre Nombre del usuario
     * @param puntuacion Puntuación a agregar
     * @return true si se agregó correctamente, false en caso contrario
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
     * @param nombre Nombre del usuario
     * @param esVictoria true si el usuario ha ganado la partida
     * @return true si se actualizaron correctamente las estadísticas
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
     * @param nombre Nombre del usuario
     * @return true si el usuario existe en el ranking, false en caso contrario
     */
    public boolean perteneceRanking(String nombre) {
        return estadisticasUsuarios.containsKey(nombre);
    }
    
    /**
     * Elimina una puntuación específica de un usuario.
     * 
     * @param nombre Nombre del usuario
     * @param puntuacion Puntuación a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
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
     * @param nombre Nombre del usuario
     * @param puntuacion Puntuación a verificar
     * @return true si existe la puntuación, false en caso contrario
     */
    public boolean existePuntuacion(String nombre, int puntuacion) {
        return perteneceRanking(nombre) && 
               estadisticasUsuarios.get(nombre).contienePuntuacion(puntuacion);
    }
    
    /**
     * Elimina un usuario del ranking.
     * 
     * @param nombre Nombre del usuario a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
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
     * @param criterio Criterio de ordenación: "maxima", "media", "partidas" o "ratio" (victorias totales)
     */
    public void setEstrategia(String criterio) {
        this.estrategiaActual = criterio;
    }
    
    /**
     * Obtiene la estrategia actual de ordenación.
     * 
     * @return Nombre de la estrategia actual
     */
    public String getEstrategiaActual() {
        RankingOrderStrategy estrategia = RankingOrderStrategyFactory.createStrategy(estrategiaActual, this);
        return estrategia.getNombre();
    }
    
    /**
     * Obtiene el ranking de usuarios según la estrategia actual.
     * Filtra automáticamente los jugadores IA para no incluirlos en el ranking.
     * 
     * @return Lista ordenada de nombres de usuario humanos
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
     * @param criterio Criterio de ordenación
     * @return Lista ordenada de nombres de usuario humanos
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
     * Obtiene todas las puntuaciones de un usuario específico.
     * 
     * @param nombre Nombre del usuario
     * @return Lista de puntuaciones del usuario
     */
    public List<Integer> getPuntuacionesUsuario(String nombre) {
        return perteneceRanking(nombre) ? 
               estadisticasUsuarios.get(nombre).getPuntuaciones() : 
               new ArrayList<>();
    }
    
    /**
     * Obtiene la puntuación máxima de un usuario específico.
     * 
     * @param nombre Nombre del usuario
     * @return Puntuación máxima del usuario
     */
    @Override
    public int getPuntuacionMaxima(String nombre) {
        return perteneceRanking(nombre) ? 
               estadisticasUsuarios.get(nombre).getPuntuacionMaxima() : 0;
    }
    
    /**
     * Obtiene la puntuación media de un usuario específico.
     * 
     * @param nombre Nombre del usuario
     * @return Puntuación media del usuario
     */
    @Override
    public double getPuntuacionMedia(String nombre) {
        return perteneceRanking(nombre) ? 
               estadisticasUsuarios.get(nombre).getPuntuacionMedia() : 0.0;
    }
    
    /**
     * Obtiene el número de partidas jugadas por un usuario específico.
     * 
     * @param nombre Nombre del usuario
     * @return Número de partidas jugadas
     */
    @Override
    public int getPartidasJugadas(String nombre) {
        return perteneceRanking(nombre) ? 
               estadisticasUsuarios.get(nombre).getPartidasJugadas() : 0;
    }
    
    /**
     * Obtiene el número de victorias de un usuario específico.
     * 
     * @param nombre Nombre del usuario
     * @return Número de victorias
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
     * @param nombre Nombre del usuario
     * @return Puntuación total acumulada
     */
    public int getPuntuacionTotal(String nombre) {
        return perteneceRanking(nombre) ? 
               estadisticasUsuarios.get(nombre).getPuntuacionTotal() : 0;
    }
    
    /**
     * Obtiene todos los nombres de usuario registrados en el ranking.
     * Filtra automáticamente los jugadores IA para no incluirlos en el ranking.
     * 
     * @return Lista de nombres de usuario humanos en el ranking
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
     * @return Mapa de usuarios y sus puntuaciones máximas
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
     * @return Mapa de usuarios y sus puntuaciones medias
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
     * @return Mapa de usuarios y su número de partidas jugadas
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
     * @return Mapa de usuarios y su número de victorias
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
     * @param usuarios Lista de usuarios a ordenar
     * @param criterio Criterio de ordenación ("maxima", "media", "partidas", "victorias")
     * @return Nueva lista ordenada de usuarios
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