package scrabble.tests;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import scrabble.domain.models.rankingStrategy.*;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test unitario para las estrategias de ordenación de ranking
 */
public class RankingStrategyTest {
    
    private RankingDataProvider dataProvider;
    
    @Before
    public void setUp() {
        // Crear un mock de RankingDataProvider
        dataProvider = Mockito.mock(RankingDataProvider.class);
        
        // Configurar el comportamiento del mock para usuario1
        when(dataProvider.getPuntuacionMaxima("usuario1")).thenReturn(200);  // Máxima puntuación = 200
        when(dataProvider.getPuntuacionMedia("usuario1")).thenReturn(150.0); // Media = 150.0
        when(dataProvider.getPartidasJugadas("usuario1")).thenReturn(10);    // 10 partidas jugadas
        when(dataProvider.getVictorias("usuario1")).thenReturn(8);           // 8 victorias
        
        // Configurar el comportamiento del mock para usuario2
        when(dataProvider.getPuntuacionMaxima("usuario2")).thenReturn(300);  // Máxima puntuación = 300
        when(dataProvider.getPuntuacionMedia("usuario2")).thenReturn(275.0); // Media = 275.0
        when(dataProvider.getPartidasJugadas("usuario2")).thenReturn(5);     // 5 partidas jugadas
        when(dataProvider.getVictorias("usuario2")).thenReturn(4);           // 4 victorias
        
        // Configurar el comportamiento del mock para usuario3
        when(dataProvider.getPuntuacionMaxima("usuario3")).thenReturn(180);  // Máxima puntuación = 180
        when(dataProvider.getPuntuacionMedia("usuario3")).thenReturn(116.6); // Media = 116.6
        when(dataProvider.getPartidasJugadas("usuario3")).thenReturn(15);    // 15 partidas jugadas
        when(dataProvider.getVictorias("usuario3")).thenReturn(9);           // 9 victorias
    }
    
    @Test
    public void testMaximaScoreStrategy() {
        // Crear la estrategia con el dataProvider mock
        RankingOrderStrategy estrategia = new MaximaScoreStrategy(dataProvider);
        
        // Lista de usuarios a ordenar
        List<String> usuarios = Arrays.asList("usuario1", "usuario2", "usuario3");
        
        // Ordenar utilizando la estrategia como Comparator
        usuarios.sort(estrategia);
        
        // Verificar que el orden es correcto (por puntuación máxima descendente)
        assertEquals("El primer lugar debería ser usuario2 (300 puntos)", "usuario2", usuarios.get(0));
        assertEquals("El segundo lugar debería ser usuario1 (200 puntos)", "usuario1", usuarios.get(1));
        assertEquals("El tercer lugar debería ser usuario3 (180 puntos)", "usuario3", usuarios.get(2));
        
        // Verificar el nombre de la estrategia
        assertEquals("El nombre de la estrategia debería ser 'Puntuación Máxima'", 
                   "Puntuación Máxima", estrategia.getNombre());
    }
    
    @Test
    public void testMediaScoreStrategy() {
        // Crear la estrategia con el dataProvider mock
        RankingOrderStrategy estrategia = new MediaScoreStrategy(dataProvider);
        
        // Lista de usuarios a ordenar
        List<String> usuarios = Arrays.asList("usuario1", "usuario2", "usuario3");
        
        // Ordenar utilizando la estrategia como Comparator
        usuarios.sort(estrategia);
        
        // Verificar que el orden es correcto (por puntuación media descendente)
        assertEquals("El primer lugar debería ser usuario2 (275.0 puntos)", "usuario2", usuarios.get(0));
        assertEquals("El segundo lugar debería ser usuario1 (150.0 puntos)", "usuario1", usuarios.get(1));
        assertEquals("El tercer lugar debería ser usuario3 (116.6 puntos)", "usuario3", usuarios.get(2));
        
        // Verificar el nombre de la estrategia
        assertEquals("El nombre de la estrategia debería ser 'Puntuación Media'", 
                   "Puntuación Media", estrategia.getNombre());
    }
    
    @Test
    public void testPartidasJugadasStrategy() {
        // Crear la estrategia con el dataProvider mock
        RankingOrderStrategy estrategia = new PartidasJugadasStrategy(dataProvider);
        
        // Lista de usuarios a ordenar
        List<String> usuarios = Arrays.asList("usuario1", "usuario2", "usuario3");
        
        // Ordenar utilizando la estrategia como Comparator
        usuarios.sort(estrategia);
        
        // Verificar que el orden es correcto (por partidas jugadas descendente)
        assertEquals("El primer lugar debería ser usuario3 (15 partidas)", "usuario3", usuarios.get(0));
        assertEquals("El segundo lugar debería ser usuario1 (10 partidas)", "usuario1", usuarios.get(1));
        assertEquals("El tercer lugar debería ser usuario2 (5 partidas)", "usuario2", usuarios.get(2));
        
        // Verificar el nombre de la estrategia
        assertEquals("El nombre de la estrategia debería ser 'Partidas Jugadas'", 
                   "Partidas Jugadas", estrategia.getNombre());
    }
    
    @Test
    public void testVictoriasStrategy() {
        // Crear la estrategia con el dataProvider mock
        RankingOrderStrategy estrategia = new VictoriasStrategy(dataProvider);
        
        // Lista de usuarios a ordenar
        List<String> usuarios = Arrays.asList("usuario1", "usuario2", "usuario3");
        
        // Ordenar utilizando la estrategia como Comparator
        usuarios.sort(estrategia);
        
        // Verificar que el orden es correcto (por total de victorias descendente)
        assertEquals("El primer lugar debería ser usuario3 (9 victorias)", "usuario3", usuarios.get(0));
        assertEquals("El segundo lugar debería ser usuario1 (8 victorias)", "usuario1", usuarios.get(1));
        assertEquals("El tercer lugar debería ser usuario2 (4 victorias)", "usuario2", usuarios.get(2));
        
        // Verificar el nombre de la estrategia
        assertEquals("El nombre de la estrategia debería ser 'Victorias'", 
                   "Victorias", estrategia.getNombre());
    }
    
    @Test
    public void testRankingOrderStrategyFactory() {
        // Verificar que se crean las estrategias correctas
        RankingOrderStrategy defaultStrategy = RankingOrderStrategyFactory.createStrategy(null, dataProvider);
        assertNotNull("Debería crearse una estrategia por defecto para null", defaultStrategy);
        assertTrue("La estrategia por defecto debería ser MaximaScoreStrategy", 
                 defaultStrategy instanceof MaximaScoreStrategy);
        
        assertTrue("Para 'maxima' debería crearse MaximaScoreStrategy", 
                 RankingOrderStrategyFactory.createStrategy("maxima", dataProvider) instanceof MaximaScoreStrategy);
        assertTrue("Para 'media' debería crearse MediaScoreStrategy", 
                 RankingOrderStrategyFactory.createStrategy("media", dataProvider) instanceof MediaScoreStrategy);
        assertTrue("Para 'partidas' debería crearse PartidasJugadasStrategy", 
                 RankingOrderStrategyFactory.createStrategy("partidas", dataProvider) instanceof PartidasJugadasStrategy);
        assertTrue("Para 'victorias' debería crearse VictoriasStrategy", 
                 RankingOrderStrategyFactory.createStrategy("victorias", dataProvider) instanceof VictoriasStrategy);
        assertTrue("Para 'ratio' debería crearse VictoriasStrategy", 
                 RankingOrderStrategyFactory.createStrategy("ratio", dataProvider) instanceof VictoriasStrategy);
        
        // Verificar comportamiento con criterio inválido
        assertTrue("Para un criterio inválido debería crearse MaximaScoreStrategy", 
                 RankingOrderStrategyFactory.createStrategy("criterioinvalido", dataProvider) instanceof MaximaScoreStrategy);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRankingOrderStrategyFactoryNullDataProvider() {
        // Debe lanzar IllegalArgumentException si dataProvider es null
        RankingOrderStrategyFactory.createStrategy("maxima", null);
    }
    
    @Test
    public void testEmpates() {
        // Se reconfiguran los mocks para simular un empate
        RankingDataProvider mockProvider = Mockito.mock(RankingDataProvider.class);
        
        // Ambos usuarios tienen 100 puntos
        when(mockProvider.getPuntuacionMaxima("usuarioA")).thenReturn(100);
        when(mockProvider.getPuntuacionMaxima("usuarioB")).thenReturn(100);
        
        // Crear estrategia con el mock
        RankingOrderStrategy estrategia = new MaximaScoreStrategy(mockProvider);
        
        // Lista con usuarios empatados
        List<String> usuarios = Arrays.asList("usuarioB", "usuarioA"); // Orden inverso para comprobar que se reordenan
        
        // Ordenar la lista
        usuarios.sort(estrategia);
        
        // En caso de empate, debería ordenar alfabéticamente
        assertEquals("En caso de empate, el primer lugar debe ser usuarioA (orden alfabético)", 
                   "usuarioA", usuarios.get(0));
        assertEquals("En caso de empate, el segundo lugar debe ser usuarioB (orden alfabético)", 
                   "usuarioB", usuarios.get(1));
    }
    
    @Test
    public void testComportamientoConListaVacia() {
        // Crear todas las estrategias
        RankingOrderStrategy estrategiaMaxima = new MaximaScoreStrategy(dataProvider);
        RankingOrderStrategy estrategiaMedia = new MediaScoreStrategy(dataProvider);
        RankingOrderStrategy estrategiaPartidas = new PartidasJugadasStrategy(dataProvider);
        RankingOrderStrategy estrategiaVictorias = new VictoriasStrategy(dataProvider);
        
        // Lista vacía de usuarios
        List<String> usuariosVacios = new ArrayList<>();
        
        // Ordenar las listas vacías
        usuariosVacios.sort(estrategiaMaxima);
        List<String> listaMaxima = new ArrayList<>(usuariosVacios);
        
        usuariosVacios.sort(estrategiaMedia);
        List<String> listaMedia = new ArrayList<>(usuariosVacios);
        
        usuariosVacios.sort(estrategiaPartidas);
        List<String> listaPartidas = new ArrayList<>(usuariosVacios);
        
        usuariosVacios.sort(estrategiaVictorias);
        List<String> listaVictorias = new ArrayList<>(usuariosVacios);
        
        // Verificar que todas siguen vacías
        assertTrue("Con lista vacía, MaximaScoreStrategy debería mantener la lista vacía", 
                 listaMaxima.isEmpty());
        assertTrue("Con lista vacía, MediaScoreStrategy debería mantener la lista vacía", 
                 listaMedia.isEmpty());
        assertTrue("Con lista vacía, PartidasJugadasStrategy debería mantener la lista vacía", 
                 listaPartidas.isEmpty());
        assertTrue("Con lista vacía, VictoriasStrategy debería mantener la lista vacía", 
                 listaVictorias.isEmpty());
    }
    
    @Test
    public void testVerificacionInteraccionConDataProvider() {
        // Crear las estrategias con el dataProvider
        RankingOrderStrategy estrategiaMaxima = new MaximaScoreStrategy(dataProvider);
        RankingOrderStrategy estrategiaMedia = new MediaScoreStrategy(dataProvider);
        RankingOrderStrategy estrategiaPartidas = new PartidasJugadasStrategy(dataProvider);
        RankingOrderStrategy estrategiaVictorias = new VictoriasStrategy(dataProvider);
        
        // Lista de usuarios a ordenar
        List<String> usuarios = Arrays.asList("usuario1", "usuario2");
        
        // Ordenar utilizando las estrategias
        usuarios.sort(estrategiaMaxima);
        usuarios.sort(estrategiaMedia);
        usuarios.sort(estrategiaPartidas);
        usuarios.sort(estrategiaVictorias);
        
        // Verificar que se llamaron los métodos adecuados del dataProvider para cada estrategia
        verify(dataProvider, atLeastOnce()).getPuntuacionMaxima("usuario1");
        verify(dataProvider, atLeastOnce()).getPuntuacionMaxima("usuario2");
        verify(dataProvider, atLeastOnce()).getPuntuacionMedia("usuario1");
        verify(dataProvider, atLeastOnce()).getPuntuacionMedia("usuario2");
        verify(dataProvider, atLeastOnce()).getPartidasJugadas("usuario1");
        verify(dataProvider, atLeastOnce()).getPartidasJugadas("usuario2");
        verify(dataProvider, atLeastOnce()).getVictorias("usuario1");
        verify(dataProvider, atLeastOnce()).getVictorias("usuario2");
    }
}