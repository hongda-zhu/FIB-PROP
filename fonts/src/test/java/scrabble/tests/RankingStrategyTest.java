package scrabble.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import scrabble.domain.models.rankingStrategy.*;

/**
 * Test unitario para las estrategias de ordenación del ranking.
 * Prueba la implementación y comportamiento de las diferentes estrategias.
 */
public class RankingStrategyTest {

    private MockRankingDataProvider dataProvider;
    private List<String> jugadores;
    
    /**
     * Implementación simple de un RankingDataProvider para pruebas
     */
    private static class MockRankingDataProvider implements RankingDataProvider {
        private static final long serialVersionUID = 1L;
        
        // Datos de prueba
        private final java.util.Map<String, PlayerRankingStats> stats = new java.util.HashMap<>();
        
        public void addPlayerStats(String username, int[] puntuaciones, int partidasJugadas, int victorias) {
            PlayerRankingStats playerStats = new PlayerRankingStats(username);
            
            // Añadir puntuaciones
            for (int puntuacion : puntuaciones) {
                playerStats.addPuntuacion(puntuacion);
            }
            
            // Simular partidas jugadas y victorias
            for (int i = 0; i < partidasJugadas; i++) {
                playerStats.actualizarEstadisticas(i < victorias);
            }
            
            stats.put(username, playerStats);
        }
        
        @Override
        public int getPuntuacionMaxima(String username) {
            return stats.containsKey(username) ? stats.get(username).getPuntuacionMaxima() : 0;
        }
        
        @Override
        public double getPuntuacionMedia(String username) {
            return stats.containsKey(username) ? stats.get(username).getPuntuacionMedia() : 0.0;
        }
        
        @Override
        public int getPartidasJugadas(String username) {
            return stats.containsKey(username) ? stats.get(username).getPartidasJugadas() : 0;
        }
        
        @Override
        public int getVictorias(String username) {
            return stats.containsKey(username) ? stats.get(username).getVictorias() : 0;
        }
        
        @Override
        public int getPuntuacionTotal(String username) {
            return stats.containsKey(username) ? stats.get(username).getPuntuacionTotal() : 0;
        }
    }
    
    /**
     * Método que se ejecuta antes de cada test.
     * Prepara el entorno para la prueba.
     * Pre:  Se necesita crear un proveedor de datos de prueba y una lista de jugadores
     * para verificar el comportamiento de las estrategias de ordenación.
     */
    @Before
    public void setUp() {
        dataProvider = new MockRankingDataProvider();
        jugadores = new ArrayList<>(Arrays.asList("Jugador1", "Jugador2", "Jugador3", "Jugador4"));
        
        // Jugador1: Puntuación máxima alta, media baja, pocas partidas, pocas victorias
        dataProvider.addPlayerStats("Jugador1", new int[]{100, 20, 30}, 5, 1);
        
        // Jugador2: Puntuación máxima media, media alta, muchas partidas, muchas victorias
        dataProvider.addPlayerStats("Jugador2", new int[]{60, 70, 80, 90}, 20, 15);
        
        // Jugador3: Puntuación máxima baja, media media, partidas medias, victorias medias
        dataProvider.addPlayerStats("Jugador3", new int[]{50, 50, 50, 50}, 10, 5);
        
        // Jugador4: Sin puntuaciones, muchas partidas, pocas victorias
        dataProvider.addPlayerStats("Jugador4", new int[]{}, 15, 2);
    }
    
    /**
     * Método que se ejecuta después de cada test.
     * Limpia el entorno después de la prueba.
     * Post:  Las referencias a objetos creados durante la prueba deben ser 
     * liberadas para permitir la recolección de basura y evitar interferencias entre tests.
     */
    @After
    public void tearDown() {
        dataProvider = null;
        jugadores = null;
    }
    
    /**
     * Prueba la estrategia de ordenación por puntuación máxima.
     * Pre:  Existe un proveedor de datos con estadísticas de jugadores y una lista de nombres
     * de jugadores. La estrategia MaximaScoreStrategy debe ordenar los jugadores por su
     * puntuación máxima en orden descendente.
     * Post:  La lista ha sido ordenada correctamente según la puntuación máxima de cada jugador
     * en orden descendente. Los jugadores con la misma puntuación máxima se ordenarían alfabéticamente.
     */
    @Test
    public void testMaximaScoreStrategy() {
        RankingOrderStrategy strategy = new MaximaScoreStrategy(dataProvider);
        
        // Ordenamos la lista según la estrategia
        Collections.sort(jugadores, strategy);
        
        // Verificamos que el orden es el esperado según las puntuaciones máximas:
        // Jugador1 (100) > Jugador2 (90) > Jugador3 (50) > Jugador4 (0)
        assertEquals("El primer jugador debe ser el de mayor puntuación máxima", "Jugador1", jugadores.get(0));
        assertEquals("El segundo jugador debe ser el siguiente en puntuación máxima", "Jugador2", jugadores.get(1));
        assertEquals("El tercer jugador debe ser el siguiente en puntuación máxima", "Jugador3", jugadores.get(2));
        assertEquals("El último jugador debe ser el de menor puntuación máxima", "Jugador4", jugadores.get(3));
        
        // Verificamos el nombre de la estrategia
        assertEquals("El nombre debe ser 'Puntuación Máxima'", "Puntuación Máxima", strategy.getNombre());
    }
    
    /**
     * Prueba la estrategia de ordenación por puntuación media.
     * Pre:  Existe un proveedor de datos con estadísticas de jugadores y una lista de nombres
     * de jugadores. La estrategia MediaScoreStrategy debe ordenar los jugadores por su
     * puntuación media en orden descendente.
     * Post:  La lista ha sido ordenada correctamente según la puntuación media de cada jugador
     * en orden descendente. Los jugadores con la misma puntuación media se ordenan alfabéticamente.
     */
    @Test
    public void testMediaScoreStrategy() {
        RankingOrderStrategy strategy = new MediaScoreStrategy(dataProvider);
        
        // Ordenamos la lista según la estrategia
        Collections.sort(jugadores, strategy);
        
        // Verificamos que el orden es el esperado según las puntuaciones medias:
        // Jugador2 (75) > Jugador3 (50) > Jugador1 (50) > Jugador4 (0)
        // Nota: Jugador3 y Jugador1 tienen la misma media, pero se ordenan alfabéticamente
        assertEquals("El primer jugador debe ser el de mayor puntuación media", "Jugador2", jugadores.get(0));
        assertEquals("El segundo jugador debe ser el siguiente en puntuación media", "Jugador1", jugadores.get(1));
        assertEquals("El tercer jugador debe ser el siguiente en puntuación media", "Jugador3", jugadores.get(2));
        assertEquals("El último jugador debe ser el de menor puntuación media", "Jugador4", jugadores.get(3));
        
        // Verificamos el nombre de la estrategia
        assertEquals("El nombre debe ser 'Puntuación Media'", "Puntuación Media", strategy.getNombre());
    }
    
    /**
     * Prueba la estrategia de ordenación por partidas jugadas.
     * Pre:  Existe un proveedor de datos con estadísticas de jugadores y una lista de nombres
     * de jugadores. La estrategia PartidasJugadasStrategy debe ordenar los jugadores por el
     * número de partidas jugadas en orden descendente.
     * Post:  La lista ha sido ordenada correctamente según el número de partidas jugadas por
     * cada jugador en orden descendente. Los jugadores con el mismo número de partidas jugadas
     * se ordenarían alfabéticamente.
     */
    @Test
    public void testPartidasJugadasStrategy() {
        RankingOrderStrategy strategy = new PartidasJugadasStrategy(dataProvider);
        
        // Ordenamos la lista según la estrategia
        Collections.sort(jugadores, strategy);
        
        // Verificamos que el orden es el esperado según las partidas jugadas:
        // Jugador2 (20) > Jugador4 (15) > Jugador3 (10) > Jugador1 (5)
        assertEquals("El primer jugador debe ser el de más partidas jugadas", "Jugador2", jugadores.get(0));
        assertEquals("El segundo jugador debe ser el siguiente en partidas jugadas", "Jugador4", jugadores.get(1));
        assertEquals("El tercer jugador debe ser el siguiente en partidas jugadas", "Jugador3", jugadores.get(2));
        assertEquals("El último jugador debe ser el de menos partidas jugadas", "Jugador1", jugadores.get(3));
        
        // Verificamos el nombre de la estrategia
        assertEquals("El nombre debe ser 'Partidas Jugadas'", "Partidas Jugadas", strategy.getNombre());
    }
    
    /**
     * Prueba la estrategia de ordenación por victorias.
     * Pre:  Existe un proveedor de datos con estadísticas de jugadores y una lista de nombres
     * de jugadores. La estrategia VictoriasStrategy debe ordenar los jugadores por el
     * número de victorias en orden descendente.
     * Post:  La lista ha sido ordenada correctamente según el número de victorias de cada
     * jugador en orden descendente. Los jugadores con el mismo número de victorias se
     * ordenarían alfabéticamente.
     */
    @Test
    public void testVictoriasStrategy() {
        RankingOrderStrategy strategy = new VictoriasStrategy(dataProvider);
        
        // Ordenamos la lista según la estrategia
        Collections.sort(jugadores, strategy);
        
        // Verificamos que el orden es el esperado según las victorias:
        // Jugador2 (15) > Jugador3 (5) > Jugador4 (2) > Jugador1 (1)
        assertEquals("El primer jugador debe ser el de más victorias", "Jugador2", jugadores.get(0));
        assertEquals("El segundo jugador debe ser el siguiente en victorias", "Jugador3", jugadores.get(1));
        assertEquals("El tercer jugador debe ser el siguiente en victorias", "Jugador4", jugadores.get(2));
        assertEquals("El último jugador debe ser el de menos victorias", "Jugador1", jugadores.get(3));
        
        // Verificamos el nombre de la estrategia
        assertEquals("El nombre debe ser 'Victorias'", "Victorias", strategy.getNombre());
    }
    
    /**
     * Prueba la estrategia de ordenación por puntuación total.
     * Pre:  Existe un proveedor de datos con estadísticas de jugadores y una lista de nombres
     * de jugadores. La estrategia PuntuacionTotalStrategy debe ordenar los jugadores por su
     * puntuación total acumulada en orden descendente.
     * Post:  La lista ha sido ordenada correctamente según la puntuación total acumulada de
     * cada jugador en orden descendente. Los jugadores con la misma puntuación total se
     * ordenarían alfabéticamente.
     */
    @Test
    public void testPuntuacionTotalStrategy() {
        RankingOrderStrategy strategy = new PuntuacionTotalStrategy(dataProvider);
        
        // Ordenamos la lista según la estrategia
        Collections.sort(jugadores, strategy);
        
        // Verificamos que el orden es el esperado según las puntuaciones totales:
        // Jugador2 (300) > Jugador3 (200) > Jugador1 (150) > Jugador4 (0)
        assertEquals("El primer jugador debe ser el de mayor puntuación total", "Jugador2", jugadores.get(0));
        assertEquals("El segundo jugador debe ser el siguiente en puntuación total", "Jugador3", jugadores.get(1));
        assertEquals("El tercer jugador debe ser el siguiente en puntuación total", "Jugador1", jugadores.get(2));
        assertEquals("El último jugador debe ser el de menor puntuación total", "Jugador4", jugadores.get(3));
        
        // Verificamos el nombre de la estrategia
        assertEquals("El nombre debe ser 'Puntuación Total'", "Puntuación Total", strategy.getNombre());
    }
    
    /**
     * Prueba el patrón Factory con la creación de diferentes estrategias.
     * Pre:  Existe un proveedor de datos con estadísticas de jugadores. La factory
     * RankingOrderStrategyFactory debe crear instancias de las diferentes estrategias
     * según el criterio especificado.
     * Post:  La factory ha creado correctamente instancias de las diferentes estrategias
     * según el criterio especificado, y la estrategia por defecto es PuntuacionTotalStrategy.
     */
    @Test
    public void testRankingOrderStrategyFactory() {
        // Creamos diferentes estrategias usando la factory
        RankingOrderStrategy maximaStrategy = RankingOrderStrategyFactory.createStrategy("maxima", dataProvider);
        RankingOrderStrategy mediaStrategy = RankingOrderStrategyFactory.createStrategy("media", dataProvider);
        RankingOrderStrategy partidasStrategy = RankingOrderStrategyFactory.createStrategy("partidas", dataProvider);
        RankingOrderStrategy victoriasStrategy = RankingOrderStrategyFactory.createStrategy("victorias", dataProvider);
        RankingOrderStrategy totalStrategy = RankingOrderStrategyFactory.createStrategy("total", dataProvider);
        RankingOrderStrategy defaultStrategy = RankingOrderStrategyFactory.createStrategy(null, dataProvider);
        
        // Verificamos que se han creado las estrategias correctas
        assertTrue("Debe ser instancia de MaximaScoreStrategy", maximaStrategy instanceof MaximaScoreStrategy);
        assertTrue("Debe ser instancia de MediaScoreStrategy", mediaStrategy instanceof MediaScoreStrategy);
        assertTrue("Debe ser instancia de PartidasJugadasStrategy", partidasStrategy instanceof PartidasJugadasStrategy);
        assertTrue("Debe ser instancia de VictoriasStrategy", victoriasStrategy instanceof VictoriasStrategy);
        assertTrue("Debe ser instancia de PuntuacionTotalStrategy", totalStrategy instanceof PuntuacionTotalStrategy);
        assertTrue("La estrategia por defecto debe ser PuntuacionTotalStrategy", defaultStrategy instanceof PuntuacionTotalStrategy);
        
        // Verificamos los nombres de las estrategias
        assertEquals("Puntuación Máxima", maximaStrategy.getNombre());
        assertEquals("Puntuación Media", mediaStrategy.getNombre());
        assertEquals("Partidas Jugadas", partidasStrategy.getNombre());
        assertEquals("Victorias", victoriasStrategy.getNombre());
        assertEquals("Puntuación Total", totalStrategy.getNombre());
        assertEquals("Puntuación Total", defaultStrategy.getNombre());
    }
    
    /**
     * Prueba el comportamiento de las estrategias cuando no hay datos para algunos jugadores.
     * Pre:  Existe un proveedor de datos y una lista de jugadores donde uno no tiene
     * estadísticas registradas. Las estrategias deben manejar correctamente esta situación.
     * Post:  Todas las estrategias han manejado correctamente la situación de un jugador
     * sin datos, colocándolo en último lugar en el ranking.
     */
    @Test
    public void testEstrategiasConDatosFaltantes() {
        // Añadimos un jugador sin estadísticas a la lista
        jugadores.add("JugadorSinDatos");
        
        // Probamos cada estrategia con el nuevo jugador
        RankingOrderStrategy maximaStrategy = new MaximaScoreStrategy(dataProvider);
        Collections.sort(jugadores, maximaStrategy);
        assertEquals("El jugador sin datos debe quedar en último lugar por puntuación máxima", "JugadorSinDatos", jugadores.get(4));
        
        RankingOrderStrategy mediaStrategy = new MediaScoreStrategy(dataProvider);
        Collections.sort(jugadores, mediaStrategy);
        assertEquals("El jugador sin datos debe quedar en último lugar por puntuación media", "JugadorSinDatos", jugadores.get(4));
        
        RankingOrderStrategy partidasStrategy = new PartidasJugadasStrategy(dataProvider);
        Collections.sort(jugadores, partidasStrategy);
        assertEquals("El jugador sin datos debe quedar en último lugar por partidas jugadas", "JugadorSinDatos", jugadores.get(4));
        
        RankingOrderStrategy victoriasStrategy = new VictoriasStrategy(dataProvider);
        Collections.sort(jugadores, victoriasStrategy);
        assertEquals("El jugador sin datos debe quedar en último lugar por victorias", "JugadorSinDatos", jugadores.get(4));
        
        RankingOrderStrategy totalStrategy = new PuntuacionTotalStrategy(dataProvider);
        Collections.sort(jugadores, totalStrategy);
        assertEquals("El jugador sin datos debe quedar en último lugar por puntuación total", "JugadorSinDatos", jugadores.get(4));
    }
    
    /**
     * Prueba la estrategia factory con un criterio inválido.
     * Pre:  Existe un proveedor de datos con estadísticas de jugadores. La factory
     * debe usar la estrategia por defecto cuando se proporciona un criterio inválido.
     * Post:  La factory ha utilizado la estrategia por defecto (PuntuacionTotalStrategy)
     * cuando se ha proporcionado un criterio inválido.
     */
    @Test
    public void testFactoryConCriterioInvalido() {
        RankingOrderStrategy strategy = RankingOrderStrategyFactory.createStrategy("criterio_invalido", dataProvider);
        
        // Verificamos que se ha creado la estrategia por defecto
        assertTrue("La estrategia para un criterio inválido debe ser PuntuacionTotalStrategy", 
                 strategy instanceof PuntuacionTotalStrategy);
        assertEquals("Puntuación Total", strategy.getNombre());
    }
    
    /**
     * Prueba la validación de data provider null en la factory.
     * Pre:  Se intenta crear una estrategia con un dataProvider null.
     * Esto debería lanzar una IllegalArgumentException.
     * Post:  Se ha lanzado la excepción esperada al intentar crear una estrategia
     * con un dataProvider null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFactoryConDataProviderNull() {
        RankingOrderStrategyFactory.createStrategy("maxima", null);
    }
    
    /**
     * Prueba los métodos adicionales de PlayerRankingStats.
     * Pre: Se crea una instancia de PlayerRankingStats.
     * Post: Se verifican los métodos setPuntuacionTotal(), getUsername() y getPuntuaciones().
     */
    @Test
    public void testPlayerRankingStatsMethods() {
        // Crear una instancia de PlayerRankingStats
        PlayerRankingStats stats = new PlayerRankingStats("TestUser");
        
        // Prueba getUsername()
        assertEquals("El username debe ser 'TestUser'", "TestUser", stats.getUsername());
        
        // Añadir algunas puntuaciones
        stats.addPuntuacion(10);
        stats.addPuntuacion(20);
        stats.addPuntuacion(30);
        
        // Prueba getPuntuaciones()
        List<Integer> puntuaciones = stats.getPuntuaciones();
        assertEquals("Debe haber 3 puntuaciones", 3, puntuaciones.size());
        assertTrue("Debe contener la puntuación 10", puntuaciones.contains(10));
        assertTrue("Debe contener la puntuación 20", puntuaciones.contains(20));
        assertTrue("Debe contener la puntuación 30", puntuaciones.contains(30));
        
        // Verificar que getPuntuaciones() devuelve una copia defensiva
        puntuaciones.add(99); // Modificar la lista devuelta
        List<Integer> puntuacionesNuevas = stats.getPuntuaciones();
        assertEquals("Debe seguir habiendo 3 puntuaciones originales", 3, puntuacionesNuevas.size());
        assertFalse("No debe contener la puntuación 99", puntuacionesNuevas.contains(99));
        
        // Verificar puntuación total actual
        assertEquals("La puntuación total debe ser 60", 60, stats.getPuntuacionTotal());
        
        // Prueba setPuntuacionTotal()
        stats.setPuntuacionTotal(100);
        assertEquals("La puntuación total debe ser 100", 100, stats.getPuntuacionTotal());
        
        // Verificar que setPuntuacionTotal no afecta a las puntuaciones individuales
        puntuacionesNuevas = stats.getPuntuaciones();
        assertEquals("Debe seguir habiendo 3 puntuaciones", 3, puntuacionesNuevas.size());
        
        // Prueba setPuntuacionTotal con valor negativo (debe ser ignorado)
        stats.setPuntuacionTotal(-50);
        assertEquals("La puntuación total no debe cambiar con valor negativo", 100, stats.getPuntuacionTotal());
    }
}
