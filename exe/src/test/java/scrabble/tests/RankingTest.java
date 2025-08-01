package scrabble.tests;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import scrabble.domain.models.Ranking;

/**
 * Test unitario para la clase Ranking.
 * Prueba las funcionalidades de gestión de estadísticas y ordenación del ranking.
 */
public class RankingTest {
    
    private Ranking ranking;
    
    /**
     * Método que se ejecuta antes de cada test.
     * Prepara el entorno para la prueba.
     * Pre: No existe una instancia de Ranking.
     * Post: Se ha creado una instancia de Ranking vacía.
     */
    @Before
    public void setUp() {
        ranking = new Ranking();
    }
    
    /**
     * Método que se ejecuta después de cada test.
     * Limpia el entorno después de la prueba.
     * Post: Las referencias a objetos creados durante la prueba son 
     * liberadas para permitir la recolección de basura.
     */
    @After
    public void tearDown() {
        ranking = null;
    }
    
    /**
     * Prueba el constructor y el estado inicial del Ranking.
     * Pre: No existe una instancia de Ranking.
     * Post: Se ha verificado que el Ranking se inicializa correctamente,
     * con la estrategia por defecto (Puntuación Total) y sin usuarios.
     */
    @Test
    public void testConstructor() {
        assertEquals("La estrategia por defecto debe ser Puntuación Total", 
                    "Puntuación Total", ranking.getEstrategiaActual());
        assertTrue("El ranking debe estar vacío inicialmente", 
                  ranking.getUsuarios().isEmpty());
    }
    
    /**
     * Prueba el método setEstrategia.
     * Pre: Existe una instancia de Ranking vacía.
     * Post: La estrategia de ordenación se ha cambiado correctamente.
     */
    @Test
    public void testSetEstrategia() {
        ranking.setEstrategia("maxima");
        assertEquals("La estrategia debe ser Puntuación Máxima", 
                    "Puntuación Máxima", ranking.getEstrategiaActual());
        
        ranking.setEstrategia("media");
        assertEquals("La estrategia debe ser Puntuación Media", 
                    "Puntuación Media", ranking.getEstrategiaActual());
        
        ranking.setEstrategia("partidas");
        assertEquals("La estrategia debe ser Partidas Jugadas", 
                    "Partidas Jugadas", ranking.getEstrategiaActual());
        
        ranking.setEstrategia("victorias");
        assertEquals("La estrategia debe ser Victorias", 
                    "Victorias", ranking.getEstrategiaActual());
        
        ranking.setEstrategia("total");
        assertEquals("La estrategia debe ser Puntuación Total", 
                    "Puntuación Total", ranking.getEstrategiaActual());
        
        // Estrategia inválida, debe usar el default (Puntuación Total)
        ranking.setEstrategia("invalidStrategy");
        assertEquals("La estrategia para un criterio inválido debe ser Puntuación Total", 
                    "Puntuación Total", ranking.getEstrategiaActual());
    }
    
    /**
     * Prueba el método agregarPuntuacion.
     * Pre: Existe una instancia de Ranking vacía.
     * Post: Se han agregado correctamente las puntuaciones a los usuarios
     * y estas se reflejan en sus estadísticas.
     */
    @Test
    public void testAgregarPuntuacion() {
        // Agregar puntuaciones para usuarios
        assertTrue("Debe retornar true al agregar puntuación válida", 
                  ranking.agregarPuntuacion("Jugador1", 100));
        assertTrue("Debe retornar true al agregar puntuación válida", 
                  ranking.agregarPuntuacion("Jugador1", 50));
        assertTrue("Debe retornar true al agregar puntuación válida", 
                  ranking.agregarPuntuacion("Jugador2", 75));
        
        // Verificar puntuaciones agregadas
        assertEquals("La puntuación máxima de Jugador1 debe ser 100", 
                    100, ranking.getPuntuacionMaxima("Jugador1"));
        assertEquals("La puntuación total de Jugador1 debe ser 150", 
                    150, ranking.getPuntuacionTotal("Jugador1"));
        assertEquals("La puntuación media de Jugador1 debe ser 75.0", 
                    75.0, ranking.getPuntuacionMedia("Jugador1"), 0.01);
        
        assertEquals("La puntuación máxima de Jugador2 debe ser 75", 
                    75, ranking.getPuntuacionMaxima("Jugador2"));
        
        // Verificar que se agrega al conjunto de usuarios
        Set<String> usuarios = ranking.getUsuarios();
        assertEquals("Debe haber 2 usuarios en el ranking", 2, usuarios.size());
        assertTrue("Jugador1 debe estar en el ranking", usuarios.contains("Jugador1"));
        assertTrue("Jugador2 debe estar en el ranking", usuarios.contains("Jugador2"));
        
        // Verificar que no se aceptan puntuaciones negativas
        assertFalse("Debe retornar false al agregar puntuación negativa", 
                   ranking.agregarPuntuacion("Jugador1", -10));
    }
    
    /**
     * Prueba el método actualizarEstadisticasUsuario.
     * Pre: Existe una instancia de Ranking vacía.
     * Post: Se han actualizado correctamente las estadísticas de partidas 
     * y victorias de los usuarios.
     */
    @Test
    public void testActualizarEstadisticasUsuario() {
        // Actualizar estadísticas para un usuario nuevo (debe crearlo)
        ranking.actualizarEstadisticasUsuario("Jugador1", true);  // Victoria
        ranking.actualizarEstadisticasUsuario("Jugador1", false); // Derrota
        ranking.actualizarEstadisticasUsuario("Jugador1", true);  // Victoria
        
        // Verificar estadísticas
        assertEquals("Jugador1 debe tener 3 partidas jugadas", 
                    3, ranking.getPartidasJugadas("Jugador1"));
        assertEquals("Jugador1 debe tener 2 victorias", 
                    2, ranking.getVictorias("Jugador1"));
        
        // Verificar que se ha agregado al conjunto de usuarios
        assertTrue("Jugador1 debe estar en el ranking", 
                  ranking.perteneceRanking("Jugador1"));
    }
    
    /**
     * Prueba los métodos eliminarPuntuacion y existePuntuacion.
     * Pre: Existe una instancia de Ranking con puntuaciones agregadas.
     * Post: Se han eliminado correctamente las puntuaciones específicas
     * y estas ya no existen en las estadísticas del usuario.
     */
    @Test
    public void testEliminarYExistePuntuacion() {
        // Agregar puntuaciones
        ranking.agregarPuntuacion("Jugador1", 100);
        ranking.agregarPuntuacion("Jugador1", 50);
        ranking.agregarPuntuacion("Jugador1", 75);
        
        // Verificar existencia de puntuaciones
        assertTrue("Debe existir la puntuación 100 para Jugador1", 
                  ranking.existePuntuacion("Jugador1", 100));
        assertTrue("Debe existir la puntuación 50 para Jugador1", 
                  ranking.existePuntuacion("Jugador1", 50));
        assertFalse("No debe existir la puntuación 200 para Jugador1", 
                   ranking.existePuntuacion("Jugador1", 200));
        
        // Eliminar puntuación
        assertTrue("Debe retornar true al eliminar puntuación existente", 
                  ranking.eliminarPuntuacion("Jugador1", 100));
        
        // Verificar que se ha eliminado
        assertFalse("No debe existir la puntuación 100 para Jugador1 después de eliminarla", 
                   ranking.existePuntuacion("Jugador1", 100));
        assertEquals("La puntuación máxima de Jugador1 ahora debe ser 75", 
                    75, ranking.getPuntuacionMaxima("Jugador1"));
        
        // Intentar eliminar puntuación inexistente
        assertFalse("Debe retornar false al intentar eliminar puntuación inexistente", 
                   ranking.eliminarPuntuacion("Jugador1", 200));
        
        // Intentar eliminar puntuación de usuario inexistente
        assertFalse("Debe retornar false al intentar eliminar puntuación de usuario inexistente", 
                   ranking.eliminarPuntuacion("JugadorInexistente", 100));
    }
    
    /**
     * Prueba el método eliminarUsuario.
     * Pre: Existe una instancia de Ranking con usuarios agregados.
     * Post: Se ha eliminado correctamente el usuario especificado
     * y todas sus estadísticas.
     */
    @Test
    public void testEliminarUsuario() {
        // Agregar puntuaciones a dos usuarios
        ranking.agregarPuntuacion("Jugador1", 100);
        ranking.agregarPuntuacion("Jugador2", 75);
        
        // Eliminar un usuario
        assertTrue("Debe retornar true al eliminar usuario existente", 
                  ranking.eliminarUsuario("Jugador1"));
        
        // Verificar que se ha eliminado
        assertFalse("Jugador1 no debe estar en el ranking", 
                   ranking.perteneceRanking("Jugador1"));
        assertTrue("Jugador2 debe seguir en el ranking", 
                  ranking.perteneceRanking("Jugador2"));
        
        // Verificar que se han eliminado todas sus estadísticas
        assertEquals("La puntuación máxima de Jugador1 debe ser 0", 
                    0, ranking.getPuntuacionMaxima("Jugador1"));
        
        // Intentar eliminar usuario inexistente
        assertFalse("Debe retornar false al intentar eliminar usuario inexistente", 
                   ranking.eliminarUsuario("JugadorInexistente"));
    }
    
    /**
     * Prueba el método getRanking con diferentes estrategias.
     * Pre: Existe una instancia de Ranking con varios usuarios y estadísticas.
     * Post: El ranking se ordena correctamente según las diferentes estrategias.
     */
    @Test
    public void testGetRankingConDiferentesEstrategias() {
        // Preparar datos para varios usuarios
        // Jugador1: Alta puntuación máxima, pocas partidas
        ranking.agregarPuntuacion("Jugador1", 100);
        ranking.agregarPuntuacion("Jugador1", 50);
        ranking.actualizarEstadisticasUsuario("Jugador1", true);  // 1 victoria
        ranking.actualizarEstadisticasUsuario("Jugador1", false); // 1 derrota
        
        // Jugador2: Puntuación máxima media, muchas partidas y victorias
        ranking.agregarPuntuacion("Jugador2", 80);
        ranking.agregarPuntuacion("Jugador2", 70);
        ranking.agregarPuntuacion("Jugador2", 60);
        for (int i = 0; i < 8; i++) {
            ranking.actualizarEstadisticasUsuario("Jugador2", i < 6); // 6 victorias, 2 derrotas
        }
        
        // Jugador3: Baja puntuación máxima, puntuación total alta
        ranking.agregarPuntuacion("Jugador3", 30);
        ranking.agregarPuntuacion("Jugador3", 30);
        ranking.agregarPuntuacion("Jugador3", 30);
        ranking.agregarPuntuacion("Jugador3", 30);
        ranking.agregarPuntuacion("Jugador3", 30);
        ranking.actualizarEstadisticasUsuario("Jugador3", false); // 0 victorias
        
        // Probar ordenación por puntuación máxima
        List<String> rankingMaxima = ranking.getRanking("maxima");
        assertEquals("Jugador1 debe ser primero por puntuación máxima", 
                    "Jugador1", rankingMaxima.get(0));
        assertEquals("Jugador2 debe ser segundo por puntuación máxima", 
                    "Jugador2", rankingMaxima.get(1));
        assertEquals("Jugador3 debe ser tercero por puntuación máxima", 
                    "Jugador3", rankingMaxima.get(2));
        
        // Probar ordenación por partidas jugadas
        List<String> rankingPartidas = ranking.getRanking("partidas");
        assertEquals("Jugador2 debe ser primero por partidas jugadas", 
                    "Jugador2", rankingPartidas.get(0));
        
        // Probar ordenación por victorias
        List<String> rankingVictorias = ranking.getRanking("victorias");
        assertEquals("Jugador2 debe ser primero por victorias", 
                    "Jugador2", rankingVictorias.get(0));
        assertEquals("Jugador1 debe ser segundo por victorias", 
                    "Jugador1", rankingVictorias.get(1));
        
        // Probar ordenación por puntuación total
        List<String> rankingTotal = ranking.getRanking("total");
        assertEquals("El orden por puntuación total debe ser correcto", 
                    Arrays.asList("Jugador2", "Jugador1", "Jugador3"), rankingTotal);
        
        // Probar obtener ranking con la estrategia actual
        ranking.setEstrategia("victorias");
        List<String> rankingActual = ranking.getRanking();
        assertEquals("El orden con la estrategia actual debe ser correcto", 
                    rankingVictorias, rankingActual);
    }
    
    /**
     * Prueba los métodos para obtener mapas de puntuaciones.
     * Pre: Existe una instancia de Ranking con varios usuarios y estadísticas.
     * Post: Los mapas devueltos contienen correctamente las estadísticas esperadas.
     */
    @Test
    public void testGetMapasPuntuaciones() {
        // Preparar datos
        ranking.agregarPuntuacion("Jugador1", 100);
        ranking.agregarPuntuacion("Jugador2", 80);
        ranking.actualizarEstadisticasUsuario("Jugador1", true);  // 1 victoria
        ranking.actualizarEstadisticasUsuario("Jugador2", false); // 0 victorias
        
        // Probar mapa de puntuaciones máximas
        Map<String, Integer> mapaPuntuacionesMaximas = ranking.getMapaPuntuacionesMaximas();
        assertEquals("La puntuación máxima de Jugador1 debe ser 100", 
                    Integer.valueOf(100), mapaPuntuacionesMaximas.get("Jugador1"));
        assertEquals("La puntuación máxima de Jugador2 debe ser 80", 
                    Integer.valueOf(80), mapaPuntuacionesMaximas.get("Jugador2"));
        
        // Probar mapa de puntuaciones medias
        Map<String, Double> mapaPuntuacionesMedias = ranking.getMapaPuntuacionesMedias();
        assertEquals("La puntuación media de Jugador1 debe ser 100.0", 
                    100.0, mapaPuntuacionesMedias.get("Jugador1"), 0.01);
        
        // Probar mapa de partidas jugadas
        Map<String, Integer> mapaPartidasJugadas = ranking.getMapaPartidasJugadas();
        assertEquals("Las partidas jugadas de Jugador1 deben ser 1", 
                    Integer.valueOf(1), mapaPartidasJugadas.get("Jugador1"));
        
        // Probar mapa de victorias
        Map<String, Integer> mapaVictorias = ranking.getMapaVictorias();
        assertEquals("Las victorias de Jugador1 deben ser 1", 
                    Integer.valueOf(1), mapaVictorias.get("Jugador1"));
        assertEquals("Las victorias de Jugador2 deben ser 0", 
                    Integer.valueOf(0), mapaVictorias.get("Jugador2"));
    }
    
    /**
     * Prueba los métodos de RankingDataProvider implementados por Ranking.
     * Pre: Existe una instancia de Ranking con usuarios y estadísticas.
     * Post: Los métodos devuelven correctamente los valores esperados.
     */
    @Test
    public void testRankingDataProviderMethods() {
        // Preparar datos
        ranking.agregarPuntuacion("Jugador1", 100);
        ranking.agregarPuntuacion("Jugador1", 50);
        ranking.actualizarEstadisticasUsuario("Jugador1", true);  // 1 victoria
        
        // Prueba getPuntuacionMaxima
        assertEquals("La puntuación máxima debe ser 100", 
                    100, ranking.getPuntuacionMaxima("Jugador1"));
        assertEquals("La puntuación máxima para usuario inexistente debe ser 0", 
                    0, ranking.getPuntuacionMaxima("JugadorInexistente"));
        
        // Prueba getPuntuacionMedia
        assertEquals("La puntuación media debe ser 75.0", 
                    75.0, ranking.getPuntuacionMedia("Jugador1"), 0.01);
        
        // Prueba getPartidasJugadas
        assertEquals("Las partidas jugadas deben ser 1", 
                    1, ranking.getPartidasJugadas("Jugador1"));
        
        // Prueba getVictorias
        assertEquals("Las victorias deben ser 1", 
                    1, ranking.getVictorias("Jugador1"));
        
        // Prueba getPuntuacionTotal
        assertEquals("La puntuación total debe ser 150", 
                    150, ranking.getPuntuacionTotal("Jugador1"));
    }
    
    /**
     * Prueba la integración de todas las funcionalidades en un escenario completo.
     * Pre: Existe una instancia de Ranking vacía.
     * Post: El ranking se ha comportado correctamente en un escenario completo
     * con múltiples operaciones.
     */
    @Test
    public void testEscenarioCompleto() {
        // 1. Agregar usuarios y puntuaciones
        ranking.agregarPuntuacion("Jugador1", 100);
        ranking.agregarPuntuacion("Jugador2", 80);
        ranking.agregarPuntuacion("Jugador3", 120);
        
        // 2. Actualizar estadísticas de partidas
        ranking.actualizarEstadisticasUsuario("Jugador1", true);  // Victoria
        ranking.actualizarEstadisticasUsuario("Jugador2", false); // Derrota
        ranking.actualizarEstadisticasUsuario("Jugador3", true);  // Victoria
        
        // 3. Verificar ranking inicial (por defecto es puntuación total)
        List<String> rankingInicial = ranking.getRanking();
        assertEquals("El orden inicial debe ser por puntuación total", 
                    "Jugador3", rankingInicial.get(0));
        
        // 4. Cambiar estrategia y verificar nuevo orden
        ranking.setEstrategia("victorias");
        List<String> rankingPorVictorias = ranking.getRanking();
        assertEquals("Los primeros lugares deben compartir 1 victoria", 
                    2, rankingPorVictorias.indexOf("Jugador2") - rankingPorVictorias.indexOf("Jugador1"));
        
        // 5. Eliminar una puntuación y verificar cambios
        ranking.eliminarPuntuacion("Jugador3", 120);
        assertEquals("La puntuación máxima de Jugador3 debe ser 0 después de eliminar su única puntuación", 
                    0, ranking.getPuntuacionMaxima("Jugador3"));
        
        // 6. Eliminar un usuario y verificar usuarios restantes
        ranking.eliminarUsuario("Jugador2");
        Set<String> usuariosRestantes = ranking.getUsuarios();
        assertEquals("Deben quedar 2 usuarios", 2, usuariosRestantes.size());
        assertTrue("Jugador1 debe seguir en el ranking", usuariosRestantes.contains("Jugador1"));
        assertTrue("Jugador3 debe seguir en el ranking", usuariosRestantes.contains("Jugador3"));
        assertFalse("Jugador2 no debe estar en el ranking", usuariosRestantes.contains("Jugador2"));
    }
    
    /**
     * Prueba el método agregarPuntuacionSinIncrementarPartidas.
     * Pre: Existe una instancia de Ranking con un usuario que tiene estadísticas iniciales.
     * Post: Se agrega correctamente una puntuación sin incrementar el contador de partidas.
     */
    @Test
    public void testAgregarPuntuacionSinIncrementarPartidas() {
        // Añadir un usuario y establecer estadísticas iniciales
        ranking.agregarPuntuacion("Jugador1", 100);
        ranking.actualizarEstadisticasUsuario("Jugador1", true); // 1 victoria, 1 partida
        
        // Verificar estadísticas iniciales
        assertEquals("Debe tener 1 partida jugada", 1, ranking.getPartidasJugadas("Jugador1"));
        assertEquals("Debe tener 1 puntuación", 1, ranking.getPuntuacionesUsuario("Jugador1").size());
        
        // Agregar puntuación sin incrementar partidas
        assertTrue("Debe retornar true al agregar puntuación sin incrementar partidas", 
                 ranking.agregarPuntuacionSinIncrementarPartidas("Jugador1", 50));
        
        // Verificar estadísticas actualizadas
        assertEquals("Debe seguir teniendo 1 partida jugada", 1, ranking.getPartidasJugadas("Jugador1"));
        assertEquals("Debe tener 2 puntuaciones", 2, ranking.getPuntuacionesUsuario("Jugador1").size());
        assertEquals("La puntuación media debe ser 75.0", 75.0, ranking.getPuntuacionMedia("Jugador1"), 0.01);
        assertEquals("La puntuación total debe ser 150", 150, ranking.getPuntuacionTotal("Jugador1"));
        
        // Verificar que no se aceptan puntuaciones negativas
        assertFalse("Debe retornar false al agregar puntuación negativa", 
                   ranking.agregarPuntuacionSinIncrementarPartidas("Jugador1", -10));
    }
    
    /**
     * Prueba el método setPuntuacionTotal.
     * Pre: Existe una instancia de Ranking con un usuario que tiene estadísticas iniciales.
     * Post: Se establece correctamente la puntuación total sin afectar a las puntuaciones individuales.
     */
    @Test
    public void testSetPuntuacionTotal() {
        // Añadir un usuario con puntuaciones iniciales
        ranking.agregarPuntuacion("Jugador1", 100);
        ranking.agregarPuntuacion("Jugador1", 50);
        
        // Verificar puntuación total inicial
        assertEquals("La puntuación total inicial debe ser 150", 150, ranking.getPuntuacionTotal("Jugador1"));
        assertEquals("Debe tener 2 puntuaciones individuales", 2, ranking.getPuntuacionesUsuario("Jugador1").size());
        
        // Establecer nueva puntuación total
        assertTrue("Debe retornar true al establecer puntuación total", 
                 ranking.setPuntuacionTotal("Jugador1", 200));
        
        // Verificar que se ha actualizado la puntuación total
        assertEquals("La puntuación total debe ser 200", 200, ranking.getPuntuacionTotal("Jugador1"));
        
        // Verificar que las puntuaciones individuales no han cambiado
        assertEquals("Debe seguir teniendo 2 puntuaciones individuales", 
                    2, ranking.getPuntuacionesUsuario("Jugador1").size());
        
        // Verificar que no se aceptan puntuaciones negativas
        assertFalse("Debe retornar false al establecer puntuación negativa", 
                   ranking.setPuntuacionTotal("Jugador1", -50));
        
        // Verificar que la puntuación total no ha cambiado
        assertEquals("La puntuación total debe seguir siendo 200", 200, ranking.getPuntuacionTotal("Jugador1"));
        
        // Verificar que no se puede establecer para un usuario inexistente
        assertFalse("Debe retornar false para usuario inexistente", 
                   ranking.setPuntuacionTotal("JugadorInexistente", 300));
    }
    
    /**
     * Prueba el manejo de valores null y excepciones en los métodos del Ranking.
     * Pre: Existe una instancia de Ranking vacía.
     * Post: Se verifica que los métodos manejan correctamente los parámetros null.
     */
    @Test
    public void testExcepcionesYValoresNull() {
        // Probar setEstrategia con null
        try {
            ranking.setEstrategia(null);
            fail("Debería lanzar NullPointerException");
        } catch (NullPointerException e) {
            // Comportamiento esperado
        }
        
        // Probar agregarPuntuacion con username null
        try {
            ranking.agregarPuntuacion(null, 100);
            fail("Debería lanzar NullPointerException");
        } catch (NullPointerException e) {
            // Comportamiento esperado
        }
        
        // Probar actualizarEstadisticasUsuario con username null
        try {
            ranking.actualizarEstadisticasUsuario(null, true);
            fail("Debería lanzar NullPointerException");
        } catch (NullPointerException e) {
            // Comportamiento esperado
        }
        
        // Probar eliminarPuntuacion con username null
        try {
            ranking.eliminarPuntuacion(null, 100);
            fail("Debería lanzar NullPointerException");
        } catch (NullPointerException e) {
            // Comportamiento esperado
        }
        
        // Probar existePuntuacion con username null
        try {
            ranking.existePuntuacion(null, 100);
            fail("Debería lanzar NullPointerException");
        } catch (NullPointerException e) {
            // Comportamiento esperado
        }
        
        // Probar getPuntuacionesUsuario con username null
        try {
            ranking.getPuntuacionesUsuario(null);
            fail("Debería lanzar NullPointerException");
        } catch (NullPointerException e) {
            // Comportamiento esperado
        }
        
        // Probar otros métodos que reciben username
        try {
            ranking.getPuntuacionMaxima(null);
            fail("Debería lanzar NullPointerException");
        } catch (NullPointerException e) {
            // Comportamiento esperado
        }
        
        try {
            ranking.getPuntuacionMedia(null);
            fail("Debería lanzar NullPointerException");
        } catch (NullPointerException e) {
            // Comportamiento esperado
        }
        
        try {
            ranking.perteneceRanking(null);
            fail("Debería lanzar NullPointerException");
        } catch (NullPointerException e) {
            // Comportamiento esperado
        }
        
        try {
            ranking.eliminarUsuario(null);
            fail("Debería lanzar NullPointerException");
        } catch (NullPointerException e) {
            // Comportamiento esperado
        }
    }
    
    /**
     * Prueba las funcionalidades de ordenamiento del Ranking con diferentes criterios.
     * Pre: Existe una instancia de Ranking con un conjunto complejo de usuarios.
     * Post: Se verifica el comportamiento de ordenamiento con diferentes estrategias 
     * y conjuntos de datos, incluyendo casos especiales.
     */
    @Test
    public void testOrdenamientoAvanzado() {
        // Configurar usuarios con diferentes perfiles de puntuación
        
        // Usuario1: Pocas puntuaciones pero muy altas (máxima alta, media alta)
        ranking.agregarPuntuacion("Usuario1", 1000);
        ranking.agregarPuntuacion("Usuario1", 900);
        ranking.actualizarEstadisticasUsuario("Usuario1", true);
        ranking.actualizarEstadisticasUsuario("Usuario1", true);
        
        // Usuario2: Muchas puntuaciones bajas (máxima baja, media baja, pero muchas partidas)
        for (int i = 0; i < 20; i++) {
            ranking.agregarPuntuacion("Usuario2", 50);
            ranking.actualizarEstadisticasUsuario("Usuario2", i < 5); // 5 victorias
        }
        
        // Usuario3: Puntuaciones medias (máxima media, media media, victorias proporcionales)
        for (int i = 0; i < 10; i++) {
            ranking.agregarPuntuacion("Usuario3", 200 + i * 10); // De 200 a 290
            ranking.actualizarEstadisticasUsuario("Usuario3", i < 7); // 7 victorias
        }
        
        // Usuario4: Un usuario sin puntuaciones pero con partidas
        for (int i = 0; i < 5; i++) {
            ranking.actualizarEstadisticasUsuario("Usuario4", i < 3); // 3 victorias
        }
        
        // Probar ordenamiento por puntuación máxima
        ranking.setEstrategia("maxima");
        List<String> rankingMaxima = ranking.getRanking();
        assertEquals("Usuario1", rankingMaxima.get(0)); // Tiene 1000 como máxima
        assertEquals("Usuario3", rankingMaxima.get(1)); // Tiene 290 como máxima
        assertEquals("Usuario2", rankingMaxima.get(2)); // Tiene 50 como máxima
        assertEquals("Usuario4", rankingMaxima.get(3)); // Tiene 0 como máxima
        
        // Probar ordenamiento por victorias
        ranking.setEstrategia("victorias");
        List<String> rankingVictorias = ranking.getRanking();
        assertEquals("Usuario3", rankingVictorias.get(0)); // 7 victorias
        assertEquals("Usuario2", rankingVictorias.get(1)); // 5 victorias
        assertEquals("Usuario4", rankingVictorias.get(2)); // 3 victorias
        assertEquals("Usuario1", rankingVictorias.get(3)); // 2 victorias
        
        // Probar ordenamiento por partidas
        ranking.setEstrategia("partidas");
        List<String> rankingPartidas = ranking.getRanking();
        assertEquals("Usuario2", rankingPartidas.get(0)); // 20 partidas
        assertEquals("Usuario3", rankingPartidas.get(1)); // 10 partidas
        assertEquals("Usuario4", rankingPartidas.get(2)); // 5 partidas
        assertEquals("Usuario1", rankingPartidas.get(3)); // 2 partidas
        
        // Eliminar un usuario y verificar que ya no aparece en el ranking
        ranking.eliminarUsuario("Usuario3");
        List<String> nuevoRanking = ranking.getRanking();
        assertEquals(3, nuevoRanking.size());
        assertFalse(nuevoRanking.contains("Usuario3"));
        
        // Añadir usuarios con misma puntuación para verificar ordenamiento alfabético secundario
        ranking.setEstrategia("maxima");
        ranking.eliminarUsuario("Usuario1");
        ranking.eliminarUsuario("Usuario2");
        ranking.eliminarUsuario("Usuario4");
        
        ranking.agregarPuntuacion("UsuarioB", 100);
        ranking.agregarPuntuacion("UsuarioA", 100);
        ranking.agregarPuntuacion("UsuarioC", 100);
        
        List<String> rankingAlfabetico = ranking.getRanking();
        assertEquals("UsuarioA", rankingAlfabetico.get(0));
        assertEquals("UsuarioB", rankingAlfabetico.get(1));
        assertEquals("UsuarioC", rankingAlfabetico.get(2));
    }
}
