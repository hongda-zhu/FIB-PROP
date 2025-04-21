package scrabble.tests;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import scrabble.domain.models.JugadorHumano;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test unitario para la clase JugadorHumano
 */
public class JugadorHumanoTest {
    
    private JugadorHumano jugador;
    
    @Before
    public void setUp() {
        // Inicializar un nuevo jugador antes de cada test
        jugador = new JugadorHumano("Usuario Test");
    }
    
    /**
     * Pre: Se ha creado una instancia de JugadorHumano con nombre "Usuario Test".
     * Post: Se verifica que el constructor inicializa correctamente los valores nombre, puntuación,
     * estado en partida y contadores de partidas.
     * 
     * Comprueba la correcta inicialización de un jugador humano.
     * Aporta validación de los valores iniciales específicos para jugadores controlados por usuario.
     */
    @Test
    public void testConstructor() {
        // Verificar que los valores iniciales son correctos
        assertEquals("El nombre debería ser 'Usuario Test'", "Usuario Test", jugador.getNombre());
        assertEquals("La puntuación inicial debería ser 0", 0, jugador.getPuntuacion());
        assertFalse("El jugador no debería estar en partida inicialmente", jugador.isEnPartida());
        assertEquals("El contador de partidas jugadas debería ser 0", 0, jugador.getPartidasJugadas());
        assertEquals("El contador de partidas ganadas debería ser 0", 0, jugador.getPartidasGanadas());
    }
    
    /**
     * Pre: Se ha creado una instancia de JugadorHumano con estado inicial no en partida.
     * Post: Se verifica que el método setEnPartida() modifica correctamente el estado en partida.
     * 
     * Comprueba la funcionalidad para cambiar el estado de participación en partida.
     * Aporta validación de la correcta gestión del estado de juego del jugador.
     */
    @Test
    public void testSetEnPartida() {
        jugador.setEnPartida(true);
        assertTrue("El jugador debería estar en partida", jugador.isEnPartida());
        
        jugador.setEnPartida(false);
        assertFalse("El jugador no debería estar en partida", jugador.isEnPartida());
    }
    
    /**
     * Pre: Se ha creado una instancia de JugadorHumano con puntuación inicial 0.
     * Post: Se verifica que el método setPuntuacion() modifica correctamente la puntuación.
     * 
     * Comprueba la funcionalidad para actualizar la puntuación del jugador.
     * Aporta validación de la correcta gestión del puntaje acumulado.
     */
    @Test
    public void testSetPuntuacion() {
        jugador.setPuntuacion(100);
        assertEquals("La puntuación debería ser 100", 100, jugador.getPuntuacion());
    }
    
    /**
     * Pre: Se ha creado una instancia de JugadorHumano con contador de partidas jugadas inicial 0.
     * Post: Se verifica que el método incrementarPartidasJugadas() aumenta correctamente el contador.
     * 
     * Comprueba la funcionalidad para incrementar el registro de partidas jugadas.
     * Aporta validación de la correcta actualización de estadísticas de juego.
     */
    @Test
    public void testIncrementarPartidasJugadas() {
        jugador.incrementarPartidasJugadas();
        assertEquals("El contador de partidas jugadas debería ser 1", 1, jugador.getPartidasJugadas());
        
        jugador.incrementarPartidasJugadas();
        assertEquals("El contador de partidas jugadas debería ser 2", 2, jugador.getPartidasJugadas());
    }
    
    /**
     * Pre: Se ha creado una instancia de JugadorHumano con contador de partidas ganadas inicial 0.
     * Post: Se verifica que el método incrementarPartidasGanadas() aumenta correctamente el contador.
     * 
     * Comprueba la funcionalidad para incrementar el registro de victorias.
     * Aporta validación de la correcta actualización de estadísticas de rendimiento.
     */
    @Test
    public void testIncrementarPartidasGanadas() {
        jugador.incrementarPartidasGanadas();
        assertEquals("El contador de partidas ganadas debería ser 1", 1, jugador.getPartidasGanadas());
        
        jugador.incrementarPartidasGanadas();
        assertEquals("El contador de partidas ganadas debería ser 2", 2, jugador.getPartidasGanadas());
    }
    
    /**
     * Pre: Se ha creado una instancia de JugadorHumano y se han modificado sus contadores de partidas.
     * Post: Se verifica que el método getRatioVictorias() calcula correctamente la proporción de
     * victorias respecto a partidas jugadas.
     * 
     * Comprueba el cálculo del ratio de victorias para estadísticas.
     * Aporta validación del correcto cálculo de métricas de rendimiento del jugador.
     */
    @Test
    public void testGetRatioVictorias() {
        assertEquals("El ratio de victorias inicial debería ser 0", 0.0, jugador.getRatioVictorias(), 0.001);
        
        jugador.incrementarPartidasJugadas();
        jugador.incrementarPartidasJugadas();
        jugador.incrementarPartidasGanadas();
        
        assertEquals("El ratio de victorias debería ser 0.5 (1/2)", 0.5, jugador.getRatioVictorias(), 0.001);
        
        jugador.incrementarPartidasJugadas();
        jugador.incrementarPartidasJugadas();
        jugador.incrementarPartidasGanadas();
        jugador.incrementarPartidasGanadas();
        
        assertEquals("El ratio de victorias debería ser 0.75 (3/4)", 0.75, jugador.getRatioVictorias(), 0.001);
    }
    
    /**
     * Pre: Se ha creado una instancia de JugadorHumano.
     * Post: Se verifica que el método esIA() devuelve false, identificando correctamente
     * que no es un jugador controlado por la IA.
     * 
     * Comprueba la correcta identificación del tipo de jugador.
     * Aporta validación de la diferenciación entre jugadores humanos y IA.
     */
    @Test
    public void testEsIA() {
        assertFalse("JugadorHumano no es una IA", jugador.esIA());
    }
    
    /**
     * Pre: Se ha creado una instancia de JugadorHumano y se ha inicializado su rack.
     * Post: Se verifica que el método getRack() devuelve correctamente el rack del jugador.
     * 
     * Comprueba la funcionalidad para acceder a las fichas disponibles del jugador.
     * Aporta validación del correcto acceso al conjunto de fichas.
     */
    @Test
    public void testGetRack() {
        // Inicialmente hay un rack vacío
        Map<String, Integer> fichasIniciales = jugador.getRack();
        assertNotNull("El rack inicialmente no debería ser null", fichasIniciales);
        assertTrue("El rack inicialmente debería estar vacío", fichasIniciales.isEmpty());
        
        // Inicializar el rack
        Map<String, Integer> rack = new HashMap<>();
        rack.put("A", 3);
        rack.put("E", 2);
        jugador.inicializarRack(rack);
        
        Map<String, Integer> fichas = jugador.getRack();
        assertNotNull("El rack no debería ser null después de inicializarlo", fichas);
        assertEquals("Debería haber 3 fichas 'A'", Integer.valueOf(3), fichas.get("A"));
        assertEquals("Debería haber 2 fichas 'E'", Integer.valueOf(2), fichas.get("E"));
    }
    
    /**
     * Pre: Se ha creado una instancia de JugadorHumano con puntaje inicial 0.
     * Post: Se verifica que el método addPuntaje() incrementa correctamente el puntaje.
     * 
     * Comprueba la funcionalidad para incrementar el puntaje de la partida actual.
     * Aporta validación del correcto incremento acumulativo de puntos.
     */
    @Test
    public void testAddPuntaje() {
        jugador.addPuntaje(50);
        assertEquals("El puntaje debería ser 50", 50, jugador.getPuntaje());
        
        jugador.addPuntaje(25);
        assertEquals("El puntaje debería ser 75", 75, jugador.getPuntaje());
    }
    
    /**
     * Pre: Se ha creado una instancia de JugadorHumano con rack inicial vacío.
     * Post: Se verifica que el método inicializarRack() establece correctamente el rack.
     * 
     * Comprueba la funcionalidad para inicializar el conjunto de fichas.
     * Aporta validación de la correcta asignación del rack de fichas al jugador.
     */
    @Test
    public void testInicializarRack() {
        Map<String, Integer> rack = new HashMap<>();
        rack.put("A", 3);
        rack.put("B", 2);
        
        jugador.inicializarRack(rack);
        assertEquals("El rack debería contener las fichas inicializadas", rack, jugador.getRack());
    }
    
    /**
     * Pre: Se ha creado una instancia de JugadorHumano y se ha inicializado su rack con fichas.
     * Post: Se verifica que el método sacarFicha() extrae correctamente fichas del rack
     * y actualiza las cantidades disponibles.
     * 
     * Comprueba la funcionalidad para extraer fichas del rack.
     * Aporta validación del correcto manejo de fichas, incluyendo casos límite.
     */
    @Test
    public void testSacarFicha() {
        Map<String, Integer> rack = new HashMap<>();
        rack.put("A", 3);
        rack.put("B", 1);
        jugador.inicializarRack(rack);
        
        // Sacar una ficha 'A' (hay 3)
        jugador.sacarFicha("A");
        assertEquals("Debería quedar 2 fichas 'A'", Integer.valueOf(2), jugador.getRack().get("A"));
        
        // Sacar otra ficha 'A' (quedan 2)
        jugador.sacarFicha("A");
        assertEquals("Debería quedar 1 ficha 'A'", Integer.valueOf(1), jugador.getRack().get("A"));
        
        // Sacar la última ficha 'A'
        jugador.sacarFicha("A");
        assertNull("No debería quedar ninguna ficha 'A'", jugador.getRack().get("A"));
        
        // Sacar la ficha 'B' (hay 1)
        jugador.sacarFicha("B");
        assertNull("No debería quedar ninguna ficha 'B'", jugador.getRack().get("B"));
    }
    
    /**
     * Pre: Se ha creado una instancia de JugadorHumano y se ha inicializado su rack.
     * Post: Se verifica que el método agregarFicha() añade correctamente fichas al rack.
     * 
     * Comprueba la funcionalidad para añadir fichas al rack.
     * Aporta validación del correcto incremento de fichas disponibles.
     */
    @Test
    public void testAgregarFicha() {
        Map<String, Integer> rack = new HashMap<>();
        rack.put("A", 1);
        jugador.inicializarRack(rack);
        
        // Agregar una ficha 'A' (ya hay 1)
        jugador.agregarFicha("A");
        assertEquals("Debería haber 2 fichas 'A'", Integer.valueOf(2), jugador.getRack().get("A"));
        
        // Agregar una ficha 'B' (no hay ninguna)
        jugador.agregarFicha("B");
        assertEquals("Debería haber 1 ficha 'B'", Integer.valueOf(1), jugador.getRack().get("B"));
    }
    
    /**
     * Pre: Se ha creado una instancia de JugadorHumano y se ha inicializado su rack con fichas.
     * Post: Se verifica que el método getCantidadFichas() devuelve correctamente el total
     * de fichas en el rack.
     * 
     * Comprueba la funcionalidad para contar el total de fichas disponibles.
     * Aporta validación del correcto conteo de recursos del jugador.
     */
    @Test
    public void testGetCantidadFichas() {
        Map<String, Integer> rack = new HashMap<>();
        rack.put("A", 3);
        rack.put("B", 2);
        rack.put("C", 1);
        jugador.inicializarRack(rack);
        
        assertEquals("Debería haber un total de 6 fichas", 6, jugador.getCantidadFichas());
        
        jugador.sacarFicha("A");
        assertEquals("Debería haber un total de 5 fichas después de sacar una 'A'", 5, jugador.getCantidadFichas());
    }
    
    /**
     * Pre: Se ha creado una instancia de JugadorHumano con contador de saltos inicial 0.
     * Post: Se verifica que el método setSkipTrack() modifica correctamente el contador.
     * 
     * Comprueba la funcionalidad para establecer el número de turnos saltados.
     * Aporta validación de la correcta modificación del contador de saltos.
     */
    @Test
    public void testSetSkipTrack() {
        jugador.setSkipTrack(2);
        assertEquals("El contador de saltos debería ser 2", 2, jugador.getSkipTrack());
    }
    
    /**
     * Pre: Se ha creado una instancia de JugadorHumano con contador de saltos inicial 0.
     * Post: Se verifica que el método addSkipTrack() incrementa correctamente el contador.
     * 
     * Comprueba la funcionalidad para incrementar el contador de turnos saltados.
     * Aporta validación del correcto incremento del contador de saltos.
     */
    @Test
    public void testAddSkipTrack() {
        assertEquals("El contador de saltos inicial debería ser 0", 0, jugador.getSkipTrack());
        
        jugador.addSkipTrack();
        assertEquals("El contador de saltos debería ser 1", 1, jugador.getSkipTrack());
        
        jugador.addSkipTrack();
        assertEquals("El contador de saltos debería ser 2", 2, jugador.getSkipTrack());
    }
    
    /**
     * Pre: Se ha creado una instancia de JugadorHumano con valores modificados en sus atributos.
     * Post: Se verifica que el método toString() incluye todos los atributos relevantes
     * con sus valores actuales.
     * 
     * Comprueba la generación de una representación textual del jugador humano.
     * Aporta validación de la correcta visualización del estado del jugador.
     */
    @Test
    public void testToString() {
        jugador.setPuntuacion(100);
        jugador.setEnPartida(true);
        jugador.incrementarPartidasJugadas();
        jugador.incrementarPartidasGanadas();
        
        String resultado = jugador.toString();
        
        assertTrue("toString() debería contener el nombre", resultado.contains("Usuario Test"));
        assertTrue("toString() debería contener la puntuación", resultado.contains("100"));
        assertTrue("toString() debería contener el estado en partida", resultado.contains("enPartida=true"));
        assertTrue("toString() debería contener las partidas jugadas", resultado.contains("partidasJugadas=1"));
        assertTrue("toString() debería contener las partidas ganadas", resultado.contains("partidasGanadas=1"));
    }
    
    /**
     * Pre: Se ha creado un mock de JugadorHumano con comportamiento configurado.
     * Post: Se verifica que los métodos del mock devuelven los valores esperados
     * y que se han realizado las llamadas esperadas.
     * 
     * Comprueba la integración con la biblioteca Mockito para pruebas con objetos simulados.
     * Aporta validación del correcto uso de mocks para simular comportamientos.
     */
    @Test
    public void testIntegracionMockito() {
        JugadorHumano mockJugador = Mockito.mock(JugadorHumano.class);
        
        when(mockJugador.getNombre()).thenReturn("Mock Nombre");
        when(mockJugador.getPuntuacion()).thenReturn(150);
        when(mockJugador.isEnPartida()).thenReturn(true);
        
        assertEquals("El mock debería devolver 'Mock Nombre'", "Mock Nombre", mockJugador.getNombre());
        assertEquals("El mock debería devolver 150", 150, mockJugador.getPuntuacion());
        assertTrue("El mock debería devolver true para isEnPartida", mockJugador.isEnPartida());
        
        verify(mockJugador).getNombre();
        verify(mockJugador).getPuntuacion();
        verify(mockJugador).isEnPartida();
    }
    
    /**
     * Pre: Se ha creado una instancia de JugadorHumano con puntuación total inicial 0.
     * Post: Se verifica que los métodos de gestión de puntuación total funcionan correctamente.
     * 
     * Comprueba la funcionalidad para gestionar la puntuación total acumulada.
     * Aporta validación del correcto acceso y modificación de la puntuación global.
     */
    @Test
    public void testPuntuacionTotal() {
        assertEquals("La puntuación total inicial debería ser 0", 0, jugador.getPuntuacionTotal());
        
        jugador.setPuntuacionTotal(100);
        assertEquals("La puntuación total debería ser 100", 100, jugador.getPuntuacionTotal());
        
        jugador.addPuntuacionTotal(50);
        assertEquals("La puntuación total debería ser 150", 150, jugador.getPuntuacionTotal());
    }
}