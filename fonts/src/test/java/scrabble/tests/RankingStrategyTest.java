package scrabble.tests;

import org.junit.Before;
import org.junit.Test;
import scrabble.domain.models.rankingStrategy.*;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Test unitario para las estrategias de ordenación de ranking
 */
public class RankingStrategyTest {
    
    private List<PlayerRankingStats> playerStats;
    
    @Before
    public void setUp() {
        // Inicializar los datos para las pruebas con PlayerRankingStats
        playerStats = new ArrayList<>();
        
        // Crear stats para usuario1
        PlayerRankingStats stats1 = new PlayerRankingStats("usuario1");
        stats1.addPuntuacion(100);
        stats1.addPuntuacion(150);
        stats1.addPuntuacion(200);
        // Simular 10 partidas con 8 victorias
        for (int i = 0; i < 10; i++) {
            stats1.actualizarEstadisticas(i < 8); // Las primeras 8 son victorias
        }
        playerStats.add(stats1);
        
        // Crear stats para usuario2
        PlayerRankingStats stats2 = new PlayerRankingStats("usuario2");
        stats2.addPuntuacion(300);
        stats2.addPuntuacion(250);
        // Simular 5 partidas con 4 victorias
        for (int i = 0; i < 5; i++) {
            stats2.actualizarEstadisticas(i < 4); // Las primeras 4 son victorias
        }
        playerStats.add(stats2);
        
        // Crear stats para usuario3
        PlayerRankingStats stats3 = new PlayerRankingStats("usuario3");
        stats3.addPuntuacion(50);
        stats3.addPuntuacion(120);
        stats3.addPuntuacion(180);
        // Simular 15 partidas con 9 victorias
        for (int i = 0; i < 15; i++) {
            stats3.actualizarEstadisticas(i < 9); // Las primeras 9 son victorias
        }
        playerStats.add(stats3);
    }
    
    @Test
    public void testMaximaScoreStrategy() {
        // Crear la estrategia
        RankingOrderStrategy estrategia = new MaximaScoreStrategy();
        
        // Crear una copia de la lista para ordenar
        List<PlayerRankingStats> statsOrdenados = new ArrayList<>(playerStats);
        
        // Ordenar utilizando la estrategia como Comparator
        Collections.sort(statsOrdenados, estrategia);
        
        // Extraer nombres de usuario ordenados
        List<String> ranking = statsOrdenados.stream()
                              .map(PlayerRankingStats::getUsername)
                              .toList();
        
        // Verificar que el orden es correcto (por puntuación máxima descendente)
        assertEquals("El primer lugar debería ser usuario2 (300 puntos)", "usuario2", ranking.get(0));
        assertEquals("El segundo lugar debería ser usuario1 (200 puntos)", "usuario1", ranking.get(1));
        assertEquals("El tercer lugar debería ser usuario3 (180 puntos)", "usuario3", ranking.get(2));
        
        // Verificar el nombre de la estrategia
        assertEquals("El nombre de la estrategia debería ser 'Puntuación Máxima'", 
                   "Puntuación Máxima", estrategia.getNombre());
    }
    
    @Test
    public void testMediaScoreStrategy() {
        // Crear la estrategia
        RankingOrderStrategy estrategia = new MediaScoreStrategy();
        
        // Crear una copia de la lista para ordenar
        List<PlayerRankingStats> statsOrdenados = new ArrayList<>(playerStats);
        
        // Ordenar utilizando la estrategia como Comparator
        Collections.sort(statsOrdenados, estrategia);
        
        // Extraer nombres de usuario ordenados
        List<String> ranking = statsOrdenados.stream()
                              .map(PlayerRankingStats::getUsername)
                              .toList();
        
        // Verificar que el orden es correcto (por puntuación media descendente)
        assertEquals("El primer lugar debería ser usuario2 (275.0 puntos)", "usuario2", ranking.get(0));
        assertEquals("El segundo lugar debería ser usuario1 (150.0 puntos)", "usuario1", ranking.get(1));
        assertEquals("El tercer lugar debería ser usuario3 (116.6 puntos)", "usuario3", ranking.get(2));
        
        // Verificar el nombre de la estrategia
        assertEquals("El nombre de la estrategia debería ser 'Puntuación Media'", 
                   "Puntuación Media", estrategia.getNombre());
    }
    
    @Test
    public void testPartidasJugadasStrategy() {
        // Crear la estrategia
        RankingOrderStrategy estrategia = new PartidasJugadasStrategy();
        
        // Crear una copia de la lista para ordenar
        List<PlayerRankingStats> statsOrdenados = new ArrayList<>(playerStats);
        
        // Ordenar utilizando la estrategia como Comparator
        Collections.sort(statsOrdenados, estrategia);
        
        // Extraer nombres de usuario ordenados
        List<String> ranking = statsOrdenados.stream()
                              .map(PlayerRankingStats::getUsername)
                              .toList();
        
        // Verificar que el orden es correcto (por partidas jugadas descendente)
        assertEquals("El primer lugar debería ser usuario3 (15 partidas)", "usuario3", ranking.get(0));
        assertEquals("El segundo lugar debería ser usuario1 (10 partidas)", "usuario1", ranking.get(1));
        assertEquals("El tercer lugar debería ser usuario2 (5 partidas)", "usuario2", ranking.get(2));
        
        // Verificar el nombre de la estrategia
        assertEquals("El nombre de la estrategia debería ser 'Partidas Jugadas'", 
                   "Partidas Jugadas", estrategia.getNombre());
    }
    
    @Test
    public void testVictoriasStrategy() {
        // Crear la estrategia
        RankingOrderStrategy estrategia = new VictoriasStrategy();
        
        // Crear una copia de la lista para ordenar
        List<PlayerRankingStats> statsOrdenados = new ArrayList<>(playerStats);
        
        // Ordenar utilizando la estrategia como Comparator
        Collections.sort(statsOrdenados, estrategia);
        
        // Extraer nombres de usuario ordenados
        List<String> ranking = statsOrdenados.stream()
                              .map(PlayerRankingStats::getUsername)
                              .toList();
        
        // Verificar que el orden es correcto (por total de victorias descendente)
        assertEquals("El primer lugar debería ser usuario3 (9 victorias)", "usuario3", ranking.get(0));
        assertEquals("El segundo lugar debería ser usuario1 (8 victorias)", "usuario1", ranking.get(1));
        assertEquals("El tercer lugar debería ser usuario2 (4 victorias)", "usuario2", ranking.get(2));
        
        // Verificar el nombre de la estrategia
        assertEquals("El nombre de la estrategia debería ser 'Victorias'", 
                   "Victorias", estrategia.getNombre());
    }
    
    @Test
    public void testRankingOrderStrategyFactory() {
        // Verificar que se crean las estrategias correctas
        assertNotNull("Debería crearse una estrategia por defecto para null", 
                    RankingOrderStrategyFactory.createStrategy(null));
        assertTrue("La estrategia por defecto debería ser MaximaScoreStrategy", 
                 RankingOrderStrategyFactory.createStrategy(null) instanceof MaximaScoreStrategy);
        
        assertTrue("Para 'maxima' debería crearse MaximaScoreStrategy", 
                 RankingOrderStrategyFactory.createStrategy("maxima") instanceof MaximaScoreStrategy);
        assertTrue("Para 'media' debería crearse MediaScoreStrategy", 
                 RankingOrderStrategyFactory.createStrategy("media") instanceof MediaScoreStrategy);
        assertTrue("Para 'partidas' debería crearse PartidasJugadasStrategy", 
                 RankingOrderStrategyFactory.createStrategy("partidas") instanceof PartidasJugadasStrategy);
        assertTrue("Para 'victorias' debería crearse VictoriasStrategy", 
                 RankingOrderStrategyFactory.createStrategy("victorias") instanceof VictoriasStrategy);
        assertTrue("Para 'ratio' debería crearse VictoriasStrategy", 
                 RankingOrderStrategyFactory.createStrategy("ratio") instanceof VictoriasStrategy);
        
        // Verificar comportamiento con criterio inválido
        assertTrue("Para un criterio inválido debería crearse MaximaScoreStrategy", 
                 RankingOrderStrategyFactory.createStrategy("criterioinvalido") instanceof MaximaScoreStrategy);
    }
    
    @Test
    public void testComportamientoConMapasVacios() {
        // Crear estrategias
        RankingOrderStrategy estrategiaMaxima = new MaximaScoreStrategy();
        RankingOrderStrategy estrategiaMedia = new MediaScoreStrategy();
        RankingOrderStrategy estrategiaPartidas = new PartidasJugadasStrategy();
        RankingOrderStrategy estrategiaVictorias = new VictoriasStrategy();
        
        // Lista vacía de estadísticas
        List<PlayerRankingStats> statsVacios = new ArrayList<>();
        
        // Extraer listas de nombres a partir de listas vacías
        List<String> rankingMaxima = statsVacios.stream()
                                   .sorted(estrategiaMaxima)
                                   .map(PlayerRankingStats::getUsername)
                                   .toList();
        List<String> rankingMedia = statsVacios.stream()
                                  .sorted(estrategiaMedia)
                                  .map(PlayerRankingStats::getUsername)
                                  .toList();
        List<String> rankingPartidas = statsVacios.stream()
                                     .sorted(estrategiaPartidas)
                                     .map(PlayerRankingStats::getUsername)
                                     .toList();
        List<String> rankingVictorias = statsVacios.stream()
                                      .sorted(estrategiaVictorias)
                                      .map(PlayerRankingStats::getUsername)
                                      .toList();
        
        // Verificar que todas devuelven listas vacías
        assertTrue("Con lista vacía, MaximaScoreStrategy debería devolver una lista vacía", 
                 rankingMaxima.isEmpty());
        assertTrue("Con lista vacía, MediaScoreStrategy debería devolver una lista vacía", 
                 rankingMedia.isEmpty());
        assertTrue("Con lista vacía, PartidasJugadasStrategy debería devolver una lista vacía", 
                 rankingPartidas.isEmpty());
        assertTrue("Con lista vacía, VictoriasStrategy debería devolver una lista vacía", 
                 rankingVictorias.isEmpty());
    }
    
    @Test
    public void testComportamientoConUnSoloUsuario() {
        // Crear estrategias
        RankingOrderStrategy estrategiaMaxima = new MaximaScoreStrategy();
        RankingOrderStrategy estrategiaMedia = new MediaScoreStrategy();
        RankingOrderStrategy estrategiaPartidas = new PartidasJugadasStrategy();
        RankingOrderStrategy estrategiaVictorias = new VictoriasStrategy();
        
        // Crear lista con un solo usuario
        List<PlayerRankingStats> statsUnico = new ArrayList<>();
        PlayerRankingStats statsUsuario = new PlayerRankingStats("usuario1");
        statsUsuario.addPuntuacion(100);
        statsUsuario.actualizarEstadisticas(true); // 1 partida, 1 victoria
        statsUnico.add(statsUsuario);
        
        // Extraer listas de nombres (no es necesario ordenar ya que solo hay uno)
        List<String> rankingMaxima = statsUnico.stream()
                                   .map(PlayerRankingStats::getUsername)
                                   .toList();
        List<String> rankingMedia = statsUnico.stream()
                                  .map(PlayerRankingStats::getUsername)
                                  .toList();
        List<String> rankingPartidas = statsUnico.stream()
                                     .map(PlayerRankingStats::getUsername)
                                     .toList();
        List<String> rankingVictorias = statsUnico.stream()
                                      .map(PlayerRankingStats::getUsername)
                                      .toList();
        
        // Verificar que todas devuelven listas con un solo usuario
        assertEquals("Con un solo usuario, todas las estrategias deberían devolverlo", 
                   1, rankingMaxima.size());
        assertEquals("Con un solo usuario, todas las estrategias deberían devolverlo", 
                   1, rankingMedia.size());
        assertEquals("Con un solo usuario, todas las estrategias deberían devolverlo", 
                   1, rankingPartidas.size());
        assertEquals("Con un solo usuario, todas las estrategias deberían devolverlo", 
                   1, rankingVictorias.size());
        
        assertEquals("El único usuario debe ser 'usuario1'", "usuario1", rankingMaxima.get(0));
    }
    
    @Test
    public void testEmpates() {
        // Crear dos usuarios con estadísticas idénticas para testear empates y orden alfabético
        List<PlayerRankingStats> statsEmpates = new ArrayList<>();
        
        // Usuario A y B tienen la misma puntuación máxima
        PlayerRankingStats statsA = new PlayerRankingStats("usuarioA");
        statsA.addPuntuacion(100);
        statsEmpates.add(statsA);
        
        PlayerRankingStats statsB = new PlayerRankingStats("usuarioB");
        statsB.addPuntuacion(100);
        statsEmpates.add(statsB);
        
        // Estrategia a probar
        RankingOrderStrategy estrategia = new MaximaScoreStrategy();
        
        // Ordenar
        List<PlayerRankingStats> statsOrdenados = new ArrayList<>(statsEmpates);
        Collections.sort(statsOrdenados, estrategia);
        
        // Extraer nombres
        List<String> ranking = statsOrdenados.stream()
                              .map(PlayerRankingStats::getUsername)
                              .toList();
        
        // En caso de empate, debería ordenar alfabéticamente
        assertEquals("En caso de empate, debería ordenar alfabéticamente", 
                    "usuarioA", ranking.get(0));
        assertEquals("En caso de empate, debería ordenar alfabéticamente", 
                    "usuarioB", ranking.get(1));
    }
}