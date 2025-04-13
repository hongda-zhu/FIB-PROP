package scrabble.tests;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import scrabble.domain.models.Ranking;
import scrabble.domain.models.rankingStrategy.MaximaScoreStrategy;
import scrabble.domain.models.rankingStrategy.MediaScoreStrategy;
import scrabble.domain.models.rankingStrategy.PartidasJugadasStrategy;
import scrabble.domain.models.rankingStrategy.RatioVictoriasStrategy;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test unitario para la clase Ranking
 */
public class RankingTest {
    
    private Ranking ranking;
    
    @Before
    public void setUp() {
        // Inicializar un nuevo ranking antes de cada test
        ranking = new Ranking();
    }
    
    /**
     * Pre: Se ha creado una instancia de Ranking.
     * Post: Se verifica que el constructor inicializa correctamente el ranking
     * y establece MaximaScoreStrategy como estrategia por defecto.
     * 
     * Comprueba la correcta inicialización del ranking.
     * Aporta validación del estado inicial del objeto de ranking.
     */
    @Test
    public void testConstructor() {
        // Verificar que el ranking se crea correctamente
        assertNotNull("El ranking no debería ser null", ranking);
        
        // Verificar que la estrategia por defecto es MaximaScoreStrategy
        assertEquals("La estrategia por defecto debería ser 'Puntuación Máxima'", 
                   "Puntuación Máxima", ranking.getEstrategiaActual());
    }
    
    /**
     * Pre: Se ha creado una instancia de Ranking vacía.
     * Post: Se verifica que se pueden añadir puntuaciones, se almacenan correctamente,
     * y se actualizan las estadísticas correspondientes.
     * 
     * Comprueba la funcionalidad para añadir puntuaciones.
     * Aporta validación de la correcta gestión de puntuaciones de usuario.
     */
    @Test
    public void testAgregarPuntuacion() {
        // Añadir algunas puntuaciones
        assertTrue("Debería poderse añadir una puntuación", ranking.agregarPuntuacion("usuario1", 100));
        assertTrue("Debería poderse añadir otra puntuación para el mismo usuario", 
                 ranking.agregarPuntuacion("usuario1", 150));
        assertTrue("Debería poderse añadir una puntuación para otro usuario", 
                 ranking.agregarPuntuacion("usuario2", 200));
        
        // Verificar que las puntuaciones se han guardado correctamente
        assertEquals("El usuario1 debería tener 2 puntuaciones", 
                   2, ranking.getPuntuacionesUsuario("usuario1").size());
        assertEquals("El usuario2 debería tener 1 puntuación", 
                   1, ranking.getPuntuacionesUsuario("usuario2").size());
        
        // Verificar que se calculan correctamente las estadísticas
        assertEquals("La puntuación máxima del usuario1 debería ser 150", 
                   150, ranking.getPuntuacionMaxima("usuario1"));
        assertEquals("La puntuación media del usuario1 debería ser 125", 
                   125.0, ranking.getPuntuacionMedia("usuario1"), 0.001);
        
        // Verificar comportamiento con puntuaciones negativas
        assertFalse("No debería poderse añadir una puntuación negativa", 
                  ranking.agregarPuntuacion("usuario1", -50));
    }
    
    /**
     * Pre: Se ha creado una instancia de Ranking vacía.
     * Post: Se verifica que se actualizan correctamente los contadores de partidas
     * jugadas y victorias para distintos usuarios.
     * 
     * Comprueba la funcionalidad para actualizar estadísticas de partidas.
     * Aporta validación de la correcta gestión de contadores de actividad.
     */
    @Test
    public void testActualizarEstadisticasUsuario() {
        // Actualizar estadísticas de partidas jugadas y victorias
        ranking.actualizarEstadisticasUsuario("usuario1", true); // Victoria
        ranking.actualizarEstadisticasUsuario("usuario1", false); // Derrota
        ranking.actualizarEstadisticasUsuario("usuario1", true); // Victoria
        
        ranking.actualizarEstadisticasUsuario("usuario2", false); // Derrota
        
        // Verificar estadísticas del usuario1
        assertEquals("El usuario1 debería tener 3 partidas jugadas", 
                   3, ranking.getPartidasJugadas("usuario1"));
        assertEquals("El usuario1 debería tener 2 victorias", 
                   2, ranking.getVictorias("usuario1"));
        
        // Verificar estadísticas del usuario2
        assertEquals("El usuario2 debería tener 1 partida jugada", 
                   1, ranking.getPartidasJugadas("usuario2"));
        assertEquals("El usuario2 debería tener 0 victorias", 
                   0, ranking.getVictorias("usuario2"));
    }
    
    /**
     * Pre: Se ha creado una instancia de Ranking con puntuaciones añadidas.
     * Post: Se verifica que se pueden eliminar puntuaciones específicas y
     * se actualizan correctamente las estadísticas.
     * 
     * Comprueba la funcionalidad para eliminar puntuaciones.
     * Aporta validación de la correcta gestión de eliminación y actualización de estadísticas.
     */
    @Test
    public void testEliminarPuntuacion() {
        // Añadir puntuaciones
        ranking.agregarPuntuacion("usuario1", 100);
        ranking.agregarPuntuacion("usuario1", 150);
        ranking.agregarPuntuacion("usuario1", 200);
        
        // Verificar que se han guardado correctamente
        assertEquals("Inicialmente, el usuario1 debería tener 3 puntuaciones", 
                   3, ranking.getPuntuacionesUsuario("usuario1").size());
        
        // Eliminar una puntuación
        assertTrue("Debería poder eliminarse una puntuación existente", 
                 ranking.eliminarPuntuacion("usuario1", 150));
        
        // Verificar que se ha eliminado
        assertEquals("Después de eliminar, el usuario1 debería tener 2 puntuaciones", 
                   2, ranking.getPuntuacionesUsuario("usuario1").size());
        assertFalse("La puntuación eliminada no debería existir", 
                  ranking.existePuntuacion("usuario1", 150));
        
        // Verificar que se actualizan las estadísticas
        assertEquals("La puntuación máxima debería actualizarse", 
                   200, ranking.getPuntuacionMaxima("usuario1"));
        assertEquals("La puntuación media debería actualizarse", 
                   150.0, ranking.getPuntuacionMedia("usuario1"), 0.001);
        
        // Eliminar una puntuación inexistente
        assertFalse("No debería poder eliminarse una puntuación inexistente", 
                  ranking.eliminarPuntuacion("usuario1", 300));
        assertFalse("No debería poder eliminarse una puntuación de un usuario inexistente", 
                  ranking.eliminarPuntuacion("usuarioInexistente", 100));
    }
    
    /**
     * Pre: Se ha creado una instancia de Ranking con una puntuación añadida.
     * Post: Se verifica que el método existePuntuacion detecta correctamente
     * puntuaciones existentes y no existentes.
     * 
     * Comprueba la funcionalidad para verificar la existencia de puntuaciones.
     * Aporta validación de la correcta detección de puntuaciones registradas.
     */
    @Test
    public void testExistePuntuacion() {
        // Añadir puntuaciones
        ranking.agregarPuntuacion("usuario1", 100);
        
        // Verificar que existen
        assertTrue("Debería existir la puntuación añadida", 
                 ranking.existePuntuacion("usuario1", 100));
        
        // Verificar que no existen puntuaciones no añadidas
        assertFalse("No debería existir una puntuación no añadida", 
                  ranking.existePuntuacion("usuario1", 200));
        assertFalse("No debería existir una puntuación de un usuario inexistente", 
                  ranking.existePuntuacion("usuarioInexistente", 100));
    }
    
    /**
     * Pre: Se ha creado una instancia de Ranking con puntuaciones añadidas.
     * Post: Se verifica que getPuntuacionesUsuario devuelve todas las puntuaciones 
     * de un usuario y una lista vacía para usuarios sin puntuaciones.
     * 
     * Comprueba la funcionalidad para obtener todas las puntuaciones de un usuario.
     * Aporta validación de la correcta recuperación de históricos de puntuación.
     */
    @Test
    public void testGetPuntuacionesUsuario() {
        // Para un usuario sin puntuaciones
        assertTrue("Para un usuario sin puntuaciones, debería devolverse una lista vacía", 
                 ranking.getPuntuacionesUsuario("usuarioInexistente").isEmpty());
        
        // Añadir puntuaciones
        ranking.agregarPuntuacion("usuario1", 100);
        ranking.agregarPuntuacion("usuario1", 200);
        
        // Verificar que se obtienen todas las puntuaciones
        List<Integer> puntuaciones = ranking.getPuntuacionesUsuario("usuario1");
        assertEquals("Deberían obtenerse 2 puntuaciones", 2, puntuaciones.size());
        assertTrue("La lista debería contener 100", puntuaciones.contains(100));
        assertTrue("La lista debería contener 200", puntuaciones.contains(200));
    }
    
    /**
     * Pre: Se ha creado una instancia de Ranking con puntuaciones añadidas.
     * Post: Se verifica que getPuntuacionMaxima devuelve la puntuación más alta
     * de un usuario y 0 para usuarios sin puntuaciones.
     * 
     * Comprueba la funcionalidad para obtener la puntuación máxima de un usuario.
     * Aporta validación de la correcta identificación de puntuaciones máximas.
     */
    @Test
    public void testGetPuntuacionMaxima() {
        // Para un usuario sin puntuaciones
        assertEquals("Para un usuario sin puntuaciones, la puntuación máxima debería ser 0", 
                   0, ranking.getPuntuacionMaxima("usuarioInexistente"));
        
        // Añadir puntuaciones
        ranking.agregarPuntuacion("usuario1", 100);
        ranking.agregarPuntuacion("usuario1", 300);
        ranking.agregarPuntuacion("usuario1", 200);
        
        // Verificar que se obtiene la puntuación máxima
        assertEquals("La puntuación máxima debería ser 300", 
                   300, ranking.getPuntuacionMaxima("usuario1"));
    }
    
    /**
     * Pre: Se ha creado una instancia de Ranking con puntuaciones añadidas.
     * Post: Se verifica que getPuntuacionMedia calcula correctamente la media
     * de puntuaciones y devuelve 0 para usuarios sin puntuaciones.
     * 
     * Comprueba la funcionalidad para calcular la puntuación media de un usuario.
     * Aporta validación del correcto cálculo de promedios de puntuación.
     */
    @Test
    public void testGetPuntuacionMedia() {
        // Para un usuario sin puntuaciones
        assertEquals("Para un usuario sin puntuaciones, la puntuación media debería ser 0", 
                   0.0, ranking.getPuntuacionMedia("usuarioInexistente"), 0.001);
        
        // Añadir puntuaciones
        ranking.agregarPuntuacion("usuario1", 100);
        ranking.agregarPuntuacion("usuario1", 200);
        ranking.agregarPuntuacion("usuario1", 300);
        
        // Verificar que se calcula correctamente la media
        assertEquals("La puntuación media debería ser 200", 
                   200.0, ranking.getPuntuacionMedia("usuario1"), 0.001);
    }
    
    /**
     * Pre: Se ha creado una instancia de Ranking y se han actualizado estadísticas.
     * Post: Se verifica que getPartidasJugadas devuelve el número correcto
     * de partidas jugadas y 0 para usuarios sin partidas.
     * 
     * Comprueba la funcionalidad para obtener el número de partidas jugadas.
     * Aporta validación del correcto seguimiento de actividad de juego.
     */
    @Test
    public void testGetPartidasJugadas() {
        // Para un usuario que no ha jugado
        assertEquals("Para un usuario sin partidas, las partidas jugadas deberían ser 0", 
                   0, ranking.getPartidasJugadas("usuarioInexistente"));
        
        // Actualizar estadísticas
        ranking.actualizarEstadisticasUsuario("usuario1", true);
        ranking.actualizarEstadisticasUsuario("usuario1", false);
        
        // Verificar que se cuenta correctamente
        assertEquals("Las partidas jugadas deberían ser 2", 
                   2, ranking.getPartidasJugadas("usuario1"));
    }
    
    /**
     * Pre: Se ha creado una instancia de Ranking y se han actualizado estadísticas.
     * Post: Se verifica que getVictorias devuelve el número correcto de victorias
     * y 0 para usuarios sin victorias o sin partidas.
     * 
     * Comprueba la funcionalidad para obtener el número de victorias.
     * Aporta validación del correcto seguimiento de resultados de juego.
     */
    @Test
    public void testGetVictorias() {
        // Para un usuario que no ha jugado
        assertEquals("Para un usuario sin partidas, las victorias deberían ser 0", 
                   0, ranking.getVictorias("usuarioInexistente"));
        
        // Actualizar estadísticas
        ranking.actualizarEstadisticasUsuario("usuario1", true); // Victoria
        ranking.actualizarEstadisticasUsuario("usuario1", false); // Derrota
        ranking.actualizarEstadisticasUsuario("usuario1", true); // Victoria
        
        // Verificar que se cuentan correctamente
        assertEquals("Las victorias deberían ser 2", 
                   2, ranking.getVictorias("usuario1"));
    }
    
    /**
     * Pre: Se ha creado una instancia de Ranking con estrategia por defecto.
     * Post: Se verifica que setEstrategia cambia correctamente la estrategia
     * de ordenación y maneja adecuadamente estrategias inválidas.
     * 
     * Comprueba la funcionalidad para cambiar la estrategia de ordenación.
     * Aporta validación de la correcta aplicación del patrón Strategy.
     */
    @Test
    public void testSetEstrategia() {
        // Cambiar la estrategia
        ranking.setEstrategia("media");
        assertEquals("La estrategia debería cambiarse a 'Puntuación Media'", 
                   "Puntuación Media", ranking.getEstrategiaActual());
        
        ranking.setEstrategia("partidas");
        assertEquals("La estrategia debería cambiarse a 'Partidas Jugadas'", 
                   "Partidas Jugadas", ranking.getEstrategiaActual());
        
        ranking.setEstrategia("victorias");
        assertEquals("La estrategia debería cambiarse a 'Ratio de Victorias'", 
                   "Ratio de Victorias", ranking.getEstrategiaActual());
        
        ranking.setEstrategia("maxima");
        assertEquals("La estrategia debería cambiarse a 'Puntuación Máxima'", 
                   "Puntuación Máxima", ranking.getEstrategiaActual());
        
        // Estrategia inválida (debería usar la predeterminada, MaximaScoreStrategy)
        ranking.setEstrategia("estrategiaInvalida");
        assertEquals("Con una estrategia inválida, debería usarse 'Puntuación Máxima'", 
                   "Puntuación Máxima", ranking.getEstrategiaActual());
    }
    
    /**
     * Pre: Se ha creado una instancia de Ranking con datos para varios usuarios.
     * Post: Se verifica que getRanking devuelve los usuarios ordenados según
     * la estrategia actual y que el orden cambia al cambiar la estrategia.
     * 
     * Comprueba la funcionalidad para obtener rankings con diferentes estrategias.
     * Aporta validación del correcto ordenamiento según distintos criterios.
     */
    @Test
    public void testGetRanking() {
        // Añadir puntuaciones y estadísticas para varios usuarios
        ranking.agregarPuntuacion("usuario1", 100);
        ranking.agregarPuntuacion("usuario2", 200);
        ranking.agregarPuntuacion("usuario3", 150);
        
        ranking.actualizarEstadisticasUsuario("usuario1", true); // 1 partida, 1 victoria
        ranking.actualizarEstadisticasUsuario("usuario2", true); // 1 partida, 1 victoria
        ranking.actualizarEstadisticasUsuario("usuario2", false); // 2 partidas, 1 victoria
        ranking.actualizarEstadisticasUsuario("usuario3", false); // 1 partida, 0 victorias
        
        // Verificar el orden según la estrategia predeterminada (Puntuación Máxima)
        List<String> rankingPorPuntuacionMaxima = ranking.getRanking();
        assertEquals("El primer lugar por puntuación máxima debería ser usuario2", 
                   "usuario2", rankingPorPuntuacionMaxima.get(0));
        assertEquals("El segundo lugar por puntuación máxima debería ser usuario3", 
                   "usuario3", rankingPorPuntuacionMaxima.get(1));
        assertEquals("El tercer lugar por puntuación máxima debería ser usuario1", 
                   "usuario1", rankingPorPuntuacionMaxima.get(2));
        
        // Cambiar la estrategia a Partidas Jugadas
        ranking.setEstrategia("partidas");
        List<String> rankingPorPartidasJugadas = ranking.getRanking();
        assertEquals("El primer lugar por partidas jugadas debería ser usuario2", 
                   "usuario2", rankingPorPartidasJugadas.get(0));
        
        // Cambiar la estrategia a Ratio de Victorias
        ranking.setEstrategia("victorias");
        List<String> rankingPorRatioVictorias = ranking.getRanking();
        assertEquals("El primer lugar por ratio de victorias debería ser usuario1", 
                   "usuario1", rankingPorRatioVictorias.get(0));
    }
    
    /**
     * Pre: Se ha creado una instancia de Ranking con datos para varios usuarios.
     * Post: Se verifica que getRanking(criterio) devuelve rankings ordenados
     * según diferentes criterios en una sola llamada.
     * 
     * Comprueba la funcionalidad para obtener rankings con un criterio específico.
     * Aporta validación de la flexibilidad para obtener distintas visualizaciones del ranking.
     */
    @Test
    public void testGetRankingConCriterio() {
        // Añadir puntuaciones y estadísticas
        ranking.agregarPuntuacion("usuario1", 100);
        ranking.agregarPuntuacion("usuario2", 200);
        ranking.agregarPuntuacion("usuario3", 150);
        
        ranking.actualizarEstadisticasUsuario("usuario1", true);
        ranking.actualizarEstadisticasUsuario("usuario2", true);
        ranking.actualizarEstadisticasUsuario("usuario2", false);
        ranking.actualizarEstadisticasUsuario("usuario3", false);
        
        // Obtener diferentes rankings según el criterio
        List<String> rankingMaxima = ranking.getRanking("maxima");
        List<String> rankingMedia = ranking.getRanking("media");
        List<String> rankingPartidas = ranking.getRanking("partidas");
        List<String> rankingVictorias = ranking.getRanking("victorias");
        
        // Verificar que son diferentes
        assertEquals("El primer lugar por puntuación máxima debería ser usuario2", 
                   "usuario2", rankingMaxima.get(0));
        assertEquals("El primer lugar por puntuación media debería ser usuario2", 
                   "usuario2", rankingMedia.get(0));
        assertEquals("El primer lugar por partidas jugadas debería ser usuario2", 
                   "usuario2", rankingPartidas.get(0));
        assertEquals("El primer lugar por ratio de victorias debería ser usuario1", 
                   "usuario1", rankingVictorias.get(0));
    }
    
    /**
     * Pre: Se ha creado una instancia de Ranking con un usuario con datos.
     * Post: Se verifica que eliminarUsuario elimina todas las puntuaciones y
     * estadísticas del usuario y no permite eliminar usuarios inexistentes.
     * 
     * Comprueba la funcionalidad para eliminar completamente a un usuario.
     * Aporta validación de la correcta gestión de la eliminación de usuarios.
     */
    @Test
    public void testEliminarUsuario() {
        // Añadir puntuaciones y estadísticas
        ranking.agregarPuntuacion("usuario1", 100);
        ranking.actualizarEstadisticasUsuario("usuario1", true);
        
        // Verificar que existe
        assertTrue("El usuario debería existir antes de eliminarlo", 
                 ranking.getUsuarios().contains("usuario1"));
        
        // Eliminar el usuario
        assertTrue("Debería poder eliminarse un usuario existente", 
                 ranking.eliminarUsuario("usuario1"));
        
        // Verificar que ya no existe
        assertFalse("El usuario no debería existir después de eliminarlo", 
                  ranking.getUsuarios().contains("usuario1"));
        assertTrue("Las puntuaciones del usuario deberían haberse eliminado", 
                 ranking.getPuntuacionesUsuario("usuario1").isEmpty());
        assertEquals("Las estadísticas del usuario deberían haberse eliminado", 
                   0, ranking.getPartidasJugadas("usuario1"));
        
        // Eliminar un usuario inexistente
        assertFalse("No debería poder eliminarse un usuario inexistente", 
                  ranking.eliminarUsuario("usuarioInexistente"));
    }
    
    /**
     * Pre: Se ha creado una instancia de Ranking vacía y luego con usuarios.
     * Post: Se verifica que getUsuarios devuelve el conjunto de todos los 
     * usuarios registrados en el ranking.
     * 
     * Comprueba la funcionalidad para obtener todos los usuarios.
     * Aporta validación de la correcta gestión del conjunto de usuarios.
     */
    @Test
    public void testGetUsuarios() {
        // Inicialmente no hay usuarios
        assertTrue("Inicialmente no debería haber usuarios", 
                 ranking.getUsuarios().isEmpty());
        
        // Añadir algunos usuarios
        ranking.agregarPuntuacion("usuario1", 100);
        ranking.agregarPuntuacion("usuario2", 200);
        
        // Verificar que se obtienen todos
        Set<String> usuarios = ranking.getUsuarios();
        assertEquals("Debería haber 2 usuarios", 2, usuarios.size());
        assertTrue("Debería estar 'usuario1'", usuarios.contains("usuario1"));
        assertTrue("Debería estar 'usuario2'", usuarios.contains("usuario2"));
    }
    
    /**
     * Pre: Se ha creado una instancia de Ranking con puntuaciones añadidas.
     * Post: Se verifica que getMapaPuntuacionesMaximas devuelve un mapa con
     * las puntuaciones máximas de cada usuario.
     * 
     * Comprueba la funcionalidad para obtener puntuaciones máximas como mapa.
     * Aporta validación de la correcta representación en formato clave-valor.
     */
    @Test
    public void testGetMapaPuntuacionesMaximas() {
        // Añadir puntuaciones
        ranking.agregarPuntuacion("usuario1", 100);
        ranking.agregarPuntuacion("usuario1", 150);
        ranking.agregarPuntuacion("usuario2", 200);
        
        // Obtener el mapa
        Map<String, Integer> mapa = ranking.getMapaPuntuacionesMaximas();
        
        // Verificar que contiene las puntuaciones máximas
        assertEquals("La puntuación máxima de usuario1 debería ser 150", 
                   Integer.valueOf(150), mapa.get("usuario1"));
        assertEquals("La puntuación máxima de usuario2 debería ser 200", 
                   Integer.valueOf(200), mapa.get("usuario2"));
    }
    
    /**
     * Pre: Se ha creado una instancia de Ranking con puntuaciones añadidas.
     * Post: Se verifica que getMapaPuntuacionesMedias devuelve un mapa con
     * las puntuaciones medias de cada usuario.
     * 
     * Comprueba la funcionalidad para obtener puntuaciones medias como mapa.
     * Aporta validación de la correcta representación en formato clave-valor.
     */
    @Test
    public void testGetMapaPuntuacionesMedias() {
        // Añadir puntuaciones
        ranking.agregarPuntuacion("usuario1", 100);
        ranking.agregarPuntuacion("usuario1", 200);
        
        // Obtener el mapa
        Map<String, Double> mapa = ranking.getMapaPuntuacionesMedias();
        
        // Verificar que contiene las puntuaciones medias
        assertEquals("La puntuación media de usuario1 debería ser 150", 
                   Double.valueOf(150.0), mapa.get("usuario1"));
    }
    
    /**
     * Pre: Se ha creado una instancia de Ranking con estadísticas actualizadas.
     * Post: Se verifica que getMapaPartidasJugadas devuelve un mapa con
     * el número de partidas jugadas por cada usuario.
     * 
     * Comprueba la funcionalidad para obtener partidas jugadas como mapa.
     * Aporta validación de la correcta representación en formato clave-valor.
     */
    @Test
    public void testGetMapaPartidasJugadas() {
        // Actualizar estadísticas
        ranking.actualizarEstadisticasUsuario("usuario1", true);
        ranking.actualizarEstadisticasUsuario("usuario1", false);
        
        // Obtener el mapa
        Map<String, Integer> mapa = ranking.getMapaPartidasJugadas();
        
        // Verificar que contiene las partidas jugadas
        assertEquals("Las partidas jugadas de usuario1 deberían ser 2", 
                   Integer.valueOf(2), mapa.get("usuario1"));
    }
    
    /**
     * Pre: Se ha creado una instancia de Ranking con estadísticas actualizadas.
     * Post: Se verifica que getMapaVictorias devuelve un mapa con
     * el número de victorias de cada usuario.
     * 
     * Comprueba la funcionalidad para obtener victorias como mapa.
     * Aporta validación de la correcta representación en formato clave-valor.
     */
    @Test
    public void testGetMapaVictorias() {
        // Actualizar estadísticas
        ranking.actualizarEstadisticasUsuario("usuario1", true);
        ranking.actualizarEstadisticasUsuario("usuario1", false);
        ranking.actualizarEstadisticasUsuario("usuario1", true);
        
        // Obtener el mapa
        Map<String, Integer> mapa = ranking.getMapaVictorias();
        
        // Verificar que contiene las victorias
        assertEquals("Las victorias de usuario1 deberían ser 2", 
                   Integer.valueOf(2), mapa.get("usuario1"));
    }
    
    /**
     * Pre: Se ha creado un mock de Ranking con comportamiento configurado.
     * Post: Se verifica que los métodos del mock devuelven los valores esperados
     * y que se han realizado las llamadas esperadas.
     * 
     * Comprueba la integración con la biblioteca Mockito para pruebas con objetos simulados.
     * Aporta validación del correcto uso de mocks para simular comportamientos.
     */
    @Test
    public void testIntegracionMockito() {
        Ranking mockRanking = Mockito.mock(Ranking.class);
        
        // Configurar el comportamiento del mock
        when(mockRanking.getEstrategiaActual()).thenReturn("Estrategia Mock");
        when(mockRanking.agregarPuntuacion("usuario1", 100)).thenReturn(true);
        when(mockRanking.getPuntuacionMaxima("usuario1")).thenReturn(100);
        when(mockRanking.getPartidasJugadas("usuario1")).thenReturn(5);
        when(mockRanking.getVictorias("usuario1")).thenReturn(3);
        when(mockRanking.getPuntuacionMedia("usuario1")).thenReturn(75.0);
        
        // Verificar el comportamiento
        assertEquals("El mock debería devolver 'Estrategia Mock'", 
                   "Estrategia Mock", mockRanking.getEstrategiaActual());
        assertTrue("El mock debería devolver true para agregarPuntuacion", 
                 mockRanking.agregarPuntuacion("usuario1", 100));
        assertEquals("El mock debería devolver 100 para getPuntuacionMaxima", 
                   100, mockRanking.getPuntuacionMaxima("usuario1"));
        assertEquals("El mock debería devolver 5 para getPartidasJugadas", 
                   5, mockRanking.getPartidasJugadas("usuario1"));
        assertEquals("El mock debería devolver 3 para getVictorias", 
                   3, mockRanking.getVictorias("usuario1"));
        assertEquals("El mock debería devolver 75.0 para getPuntuacionMedia", 
                   75.0, mockRanking.getPuntuacionMedia("usuario1"), 0.001);
        
        // Verificar las llamadas
        verify(mockRanking).getEstrategiaActual();
        verify(mockRanking).agregarPuntuacion("usuario1", 100);
        verify(mockRanking).getPuntuacionMaxima("usuario1");
        verify(mockRanking).getPartidasJugadas("usuario1");
        verify(mockRanking).getVictorias("usuario1");
        verify(mockRanking).getPuntuacionMedia("usuario1");
    }
}