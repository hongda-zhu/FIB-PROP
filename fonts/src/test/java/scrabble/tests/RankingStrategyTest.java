package scrabble.tests;

import org.junit.Before;
import org.junit.Test;
import scrabble.domain.models.rankingStrategy.*;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test unitario para las estrategias de ordenación de ranking
 */
public class RankingStrategyTest {
    
    private Map<String, List<Integer>> puntuacionesPorUsuario;
    private Map<String, Integer> puntuacionMaximaPorUsuario;
    private Map<String, Double> puntuacionMediaPorUsuario;
    private Map<String, Integer> partidasJugadasPorUsuario;
    private Map<String, Integer> victoriasUsuario;
    
    @Before
    public void setUp() {
        // Inicializar los datos para las pruebas
        puntuacionesPorUsuario = new HashMap<>();
        puntuacionMaximaPorUsuario = new HashMap<>();
        puntuacionMediaPorUsuario = new HashMap<>();
        partidasJugadasPorUsuario = new HashMap<>();
        victoriasUsuario = new HashMap<>();
        
        // Datos de prueba para tres usuarios
        puntuacionesPorUsuario.put("usuario1", Arrays.asList(100, 150, 200));
        puntuacionesPorUsuario.put("usuario2", Arrays.asList(300, 250));
        puntuacionesPorUsuario.put("usuario3", Arrays.asList(50, 120, 180));
        
        puntuacionMaximaPorUsuario.put("usuario1", 200);
        puntuacionMaximaPorUsuario.put("usuario2", 300);
        puntuacionMaximaPorUsuario.put("usuario3", 180);
        
        puntuacionMediaPorUsuario.put("usuario1", 150.0);
        puntuacionMediaPorUsuario.put("usuario2", 275.0);
        puntuacionMediaPorUsuario.put("usuario3", 116.6);
        
        partidasJugadasPorUsuario.put("usuario1", 10);
        partidasJugadasPorUsuario.put("usuario2", 5);
        partidasJugadasPorUsuario.put("usuario3", 15);
        
        victoriasUsuario.put("usuario1", 8); // 80% victorias
        victoriasUsuario.put("usuario2", 4); // 80% victorias
        victoriasUsuario.put("usuario3", 9); // 60% victorias
    }
    
    @Test
    public void testMaximaScoreStrategy() {
        // Crear la estrategia
        RankingOrderStrategy estrategia = new MaximaScoreStrategy();
        
        // Ejecutar la ordenación
        List<String> ranking = estrategia.ordenarRanking(
            puntuacionesPorUsuario,
            puntuacionMaximaPorUsuario,
            puntuacionMediaPorUsuario,
            partidasJugadasPorUsuario,
            victoriasUsuario
        );
        
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
        
        // Ejecutar la ordenación
        List<String> ranking = estrategia.ordenarRanking(
            puntuacionesPorUsuario,
            puntuacionMaximaPorUsuario,
            puntuacionMediaPorUsuario,
            partidasJugadasPorUsuario,
            victoriasUsuario
        );
        
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
        
        // Ejecutar la ordenación
        List<String> ranking = estrategia.ordenarRanking(
            puntuacionesPorUsuario,
            puntuacionMaximaPorUsuario,
            puntuacionMediaPorUsuario,
            partidasJugadasPorUsuario,
            victoriasUsuario
        );
        
        // Verificar que el orden es correcto (por partidas jugadas descendente)
        assertEquals("El primer lugar debería ser usuario3 (15 partidas)", "usuario3", ranking.get(0));
        assertEquals("El segundo lugar debería ser usuario1 (10 partidas)", "usuario1", ranking.get(1));
        assertEquals("El tercer lugar debería ser usuario2 (5 partidas)", "usuario2", ranking.get(2));
        
        // Verificar el nombre de la estrategia
        assertEquals("El nombre de la estrategia debería ser 'Partidas Jugadas'", 
                   "Partidas Jugadas", estrategia.getNombre());
    }
    
    @Test
    public void testRatioVictoriasStrategy() {
        // Crear la estrategia
        RankingOrderStrategy estrategia = new RatioVictoriasStrategy();
        
        // Ejecutar la ordenación
        List<String> ranking = estrategia.ordenarRanking(
            puntuacionesPorUsuario,
            puntuacionMaximaPorUsuario,
            puntuacionMediaPorUsuario,
            partidasJugadasPorUsuario,
            victoriasUsuario
        );
        
        // Verificar que el orden es correcto (por ratio de victorias descendente)
        // usuario1 y usuario2 tienen el mismo ratio (80%), pero usuario1 debería ir primero por orden alfabético
        assertEquals("El primer lugar debería ser usuario1 (80% victorias)", "usuario1", ranking.get(0));
        assertEquals("El segundo lugar debería ser usuario2 (80% victorias)", "usuario2", ranking.get(1));
        assertEquals("El tercer lugar debería ser usuario3 (60% victorias)", "usuario3", ranking.get(2));
        
        // Verificar el nombre de la estrategia
        assertEquals("El nombre de la estrategia debería ser 'Ratio de Victorias'", 
                   "Ratio de Victorias", estrategia.getNombre());
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
        assertTrue("Para 'victorias' debería crearse RatioVictoriasStrategy", 
                 RankingOrderStrategyFactory.createStrategy("victorias") instanceof RatioVictoriasStrategy);
        
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
        RankingOrderStrategy estrategiaVictorias = new RatioVictoriasStrategy();
        
        // Crear mapas vacíos
        Map<String, List<Integer>> puntuacionesVacias = new HashMap<>();
        Map<String, Integer> maximasVacias = new HashMap<>();
        Map<String, Double> mediasVacias = new HashMap<>();
        Map<String, Integer> partidasVacias = new HashMap<>();
        Map<String, Integer> victoriasVacias = new HashMap<>();
        
        // Ejecutar las ordenaciones
        List<String> rankingMaxima = estrategiaMaxima.ordenarRanking(
            puntuacionesVacias, maximasVacias, mediasVacias, partidasVacias, victoriasVacias);
        List<String> rankingMedia = estrategiaMedia.ordenarRanking(
            puntuacionesVacias, maximasVacias, mediasVacias, partidasVacias, victoriasVacias);
        List<String> rankingPartidas = estrategiaPartidas.ordenarRanking(
            puntuacionesVacias, maximasVacias, mediasVacias, partidasVacias, victoriasVacias);
        List<String> rankingVictorias = estrategiaVictorias.ordenarRanking(
            puntuacionesVacias, maximasVacias, mediasVacias, partidasVacias, victoriasVacias);
        
        // Verificar que todas devuelven listas vacías
        assertTrue("Con mapas vacíos, MaximaScoreStrategy debería devolver una lista vacía", 
                 rankingMaxima.isEmpty());
        assertTrue("Con mapas vacíos, MediaScoreStrategy debería devolver una lista vacía", 
                 rankingMedia.isEmpty());
        assertTrue("Con mapas vacíos, PartidasJugadasStrategy debería devolver una lista vacía", 
                 rankingPartidas.isEmpty());
        assertTrue("Con mapas vacíos, RatioVictoriasStrategy debería devolver una lista vacía", 
                 rankingVictorias.isEmpty());
    }
    
    @Test
    public void testComportamientoConUnSoloUsuario() {
        // Crear estrategias
        RankingOrderStrategy estrategiaMaxima = new MaximaScoreStrategy();
        RankingOrderStrategy estrategiaMedia = new MediaScoreStrategy();
        RankingOrderStrategy estrategiaPartidas = new PartidasJugadasStrategy();
        RankingOrderStrategy estrategiaVictorias = new RatioVictoriasStrategy();
        
        // Crear mapas con un solo usuario
        Map<String, List<Integer>> puntuacionesUn = new HashMap<>();
        puntuacionesUn.put("usuario1", Arrays.asList(100));
        
        Map<String, Integer> maximasUn = new HashMap<>();
        maximasUn.put("usuario1", 100);
        
        Map<String, Double> mediasUn = new HashMap<>();
        mediasUn.put("usuario1", 100.0);
        
        Map<String, Integer> partidasUn = new HashMap<>();
        partidasUn.put("usuario1", 1);
        
        Map<String, Integer> victoriasUn = new HashMap<>();
        victoriasUn.put("usuario1", 1);
        
        // Ejecutar las ordenaciones
        List<String> rankingMaxima = estrategiaMaxima.ordenarRanking(
            puntuacionesUn, maximasUn, mediasUn, partidasUn, victoriasUn);
        List<String> rankingMedia = estrategiaMedia.ordenarRanking(
            puntuacionesUn, maximasUn, mediasUn, partidasUn, victoriasUn);
        List<String> rankingPartidas = estrategiaPartidas.ordenarRanking(
            puntuacionesUn, maximasUn, mediasUn, partidasUn, victoriasUn);
        List<String> rankingVictorias = estrategiaVictorias.ordenarRanking(
            puntuacionesUn, maximasUn, mediasUn, partidasUn, victoriasUn);
        
        // Verificar que todas devuelven listas con un solo usuario
        assertEquals("Con un solo usuario, todas las estrategias deberían devolverlo", 
                   1, rankingMaxima.size());
        assertEquals("Con un solo usuario, todas las estrategias deberían devolverlo", 
                   1, rankingMedia.size());
        assertEquals("Con un solo usuario, todas las estrategias deberían devolverlo", 
                   1, rankingPartidas.size());
        assertEquals("Con un solo usuario, todas las estrategias deberían devolverlo", 
                   1, rankingVictorias.size());
        
        assertEquals("Con un solo usuario, todas las estrategias deberían devolverlo", 
                   "usuario1", rankingMaxima.get(0));
        assertEquals("Con un solo usuario, todas las estrategias deberían devolverlo", 
                   "usuario1", rankingMedia.get(0));
        assertEquals("Con un solo usuario, todas las estrategias deberían devolverlo", 
                   "usuario1", rankingPartidas.get(0));
        assertEquals("Con un solo usuario, todas las estrategias deberían devolverlo", 
                   "usuario1", rankingVictorias.get(0));
    }
    
    @Test
    public void testEmpates() {
        // Crear puntuaciones con empates
        Map<String, Integer> maximasEmpate = new HashMap<>();
        maximasEmpate.put("usuario1", 100);
        maximasEmpate.put("usuario2", 100);
        maximasEmpate.put("usuario3", 50);
        
        Map<String, Double> mediasEmpate = new HashMap<>();
        mediasEmpate.put("usuario1", 80.0);
        mediasEmpate.put("usuario2", 80.0);
        mediasEmpate.put("usuario3", 50.0);
        
        Map<String, Integer> partidasEmpate = new HashMap<>();
        partidasEmpate.put("usuario1", 10);
        partidasEmpate.put("usuario2", 10);
        partidasEmpate.put("usuario3", 5);
        
        // Crear estrategias
        RankingOrderStrategy estrategiaMaxima = new MaximaScoreStrategy();
        RankingOrderStrategy estrategiaMedia = new MediaScoreStrategy();
        RankingOrderStrategy estrategiaPartidas = new PartidasJugadasStrategy();
        
        // Ejecutar las ordenaciones con empates
        List<String> rankingMaxima = estrategiaMaxima.ordenarRanking(
            puntuacionesPorUsuario, maximasEmpate, puntuacionMediaPorUsuario, 
            partidasJugadasPorUsuario, victoriasUsuario);
        List<String> rankingMedia = estrategiaMedia.ordenarRanking(
            puntuacionesPorUsuario, puntuacionMaximaPorUsuario, mediasEmpate, 
            partidasJugadasPorUsuario, victoriasUsuario);
        List<String> rankingPartidas = estrategiaPartidas.ordenarRanking(
            puntuacionesPorUsuario, puntuacionMaximaPorUsuario, puntuacionMediaPorUsuario, 
            partidasEmpate, victoriasUsuario);
        
        // Verificar que los empates se resuelven por orden alfabético
        assertEquals("En caso de empate de máxima, usuario1 debería ir antes que usuario2", 
                   "usuario1", rankingMaxima.get(0));
        assertEquals("En caso de empate de máxima, usuario1 debería ir antes que usuario2", 
                   "usuario2", rankingMaxima.get(1));
        
        assertEquals("En caso de empate de media, usuario1 debería ir antes que usuario2", 
                   "usuario1", rankingMedia.get(0));
        assertEquals("En caso de empate de media, usuario1 debería ir antes que usuario2", 
                   "usuario2", rankingMedia.get(1));
        
        assertEquals("En caso de empate de partidas, usuario1 debería ir antes que usuario2", 
                   "usuario1", rankingPartidas.get(0));
        assertEquals("En caso de empate de partidas, usuario1 debería ir antes que usuario2", 
                   "usuario2", rankingPartidas.get(1));
    }
}