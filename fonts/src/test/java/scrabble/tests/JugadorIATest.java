package scrabble.tests;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import scrabble.domain.models.JugadorIA;
import scrabble.helpers.Dificultad;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test unitario para la clase JugadorIA
 */
public class JugadorIATest {
    
    private JugadorIA jugadorIA;
    
    @Before
    public void setUp() {
        // Inicializar un nuevo jugador IA antes de cada test
        jugadorIA = new JugadorIA(Dificultad.FACIL);
    }
    
    /**
     * Pre: Se ha creado una instancia de JugadorIA con dificultad FACIL.
     * Post: Se verifica que el constructor inicializa correctamente los valores nombre,
     * puntuación, dificultad y puntuación de última partida.
     * 
     * Verifica que todos los valores iniciales del jugador IA son los esperados.
     * Aporta validación de la correcta inicialización específica para jugadores controlados por la IA.
     */
    @Test
    public void testConstructor() {
        // Verificar que los valores iniciales son correctos
        assertTrue("El nombre debería empezar con 'IA_'", jugadorIA.getNombre().startsWith("IA_"));
        assertEquals("La puntuación inicial debería ser 0", 0, jugadorIA.getPuntuacion());
        assertEquals("La dificultad debería ser FACIL", Dificultad.FACIL, jugadorIA.getNivelDificultad());
        assertEquals("La puntuación de última partida debería ser 0", 0, jugadorIA.getPuntuacionUltimaPartida());
    }
    
    /**
     * Pre: Se ha creado una instancia de JugadorIA con dificultad FACIL.
     * Post: Se verifica que el método setNivelDificultad() modifica correctamente la dificultad.
     * 
     * Comprueba la funcionalidad del método setter para el nivel de dificultad.
     * Aporta validación de la correcta modificación del nivel de dificultad del jugador IA.
     */
    @Test
    public void testSetNivelDificultad() {
        jugadorIA.setNivelDificultad(Dificultad.DIFICIL);
        assertEquals("La dificultad debería cambiarse a DIFICIL", Dificultad.DIFICIL, jugadorIA.getNivelDificultad());
    }
    
    /**
     * Pre: Se ha creado una instancia de JugadorIA con puntuación inicial 0.
     * Post: Se verifica que el método getPartidasGanadas() devuelve 0 cuando la puntuación es 0
     * y 1 cuando la puntuación es positiva.
     * 
     * Comprueba la lógica especial de conteo de partidas ganadas para IA basada en puntuación.
     * Aporta validación del correcto cálculo de estadísticas para jugadores IA.
     */
    @Test
    public void testGetPartidasGanadas() {
        // Sin puntuación, no ha ganado
        assertEquals("Sin puntuación, partidas ganadas debería ser 0", 0, jugadorIA.getPartidasGanadas());
        
        // Con puntuación positiva, ha ganado
        jugadorIA.setPuntuacion(10);
        assertEquals("Con puntuación positiva, partidas ganadas debería ser 1", 1, jugadorIA.getPartidasGanadas());
    }
    
    /**
     * Pre: Se ha creado una instancia de JugadorIA.
     * Post: Se verifica que el método getPartidasJugadas() siempre devuelve 1.
     * 
     * Comprueba el comportamiento constante del conteo de partidas jugadas para IA.
     * Aporta validación de las reglas específicas para estadísticas de jugadores IA.
     */
    @Test
    public void testGetPartidasJugadas() {
        // La IA siempre considera que ha jugado 1 partida para estadísticas
        assertEquals("Partidas jugadas debería ser siempre 1", 1, jugadorIA.getPartidasJugadas());
    }
    
    /**
     * Pre: Se ha creado una instancia de JugadorIA con puntuación de última partida inicial 0.
     * Post: Se verifica que el método getPuntuacionUltimaPartida() devuelve el valor correcto
     * después de modificarlo con setPuntuacionUltimaPartida().
     * 
     * Comprueba la funcionalidad para gestionar el puntaje de la última partida jugada.
     * Aporta validación del correcto acceso y modificación de la puntuación específica de la última partida.
     */
    @Test
    public void testGetPuntuacionUltimaPartida() {
        assertEquals("La puntuación de última partida inicial debería ser 0", 0, jugadorIA.getPuntuacionUltimaPartida());
        
        jugadorIA.setPuntuacionUltimaPartida(50);
        assertEquals("La puntuación de última partida debería ser 50", 50, jugadorIA.getPuntuacionUltimaPartida());
    }
    
    /**
     * Pre: Se ha creado una instancia de JugadorIA con puntuación inicial 0.
     * Post: Se verifica que el método getRatioVictorias() devuelve 0 cuando la puntuación es 0
     * y 1 cuando la puntuación es positiva.
     * 
     * Comprueba la lógica de cálculo del ratio de victorias basado en la puntuación.
     * Aporta validación del correcto cálculo de estadísticas específicas para jugadores IA.
     */
    @Test
    public void testGetRatioVictorias() {
        // Sin puntuación, ratio es 0
        assertEquals("Sin puntuación, ratio debería ser 0", 0.0, jugadorIA.getRatioVictorias(), 0.001);
        
        // Con puntuación positiva, ratio es 1
        jugadorIA.setPuntuacion(10);
        assertEquals("Con puntuación positiva, ratio debería ser 1", 1.0, jugadorIA.getRatioVictorias(), 0.001);
    }
    
    /**
     * Pre: Se ha creado una instancia de JugadorIA.
     * Post: Se verifica que el método esIA() devuelve true.
     * 
     * Comprueba la correcta identificación del tipo de jugador.
     * Aporta validación de la diferenciación entre tipos de jugadores.
     */
    @Test
    public void testEsIA() {
        assertTrue("JugadorIA debería ser una IA", jugadorIA.esIA());
    }
    
    /**
     * Pre: Se ha creado una instancia de JugadorIA y se ha inicializado su rack con fichas.
     * Post: Se verifica que el método getRack() devuelve correctamente el rack del jugador.
     * 
     * Comprueba la funcionalidad para acceder a las fichas disponibles.
     * Aporta validación del correcto acceso al conjunto de fichas.
     */
    @Test
    public void testGetRack() {
        // Inicialmente el rack está vacío
        assertTrue("El rack inicialmente debería estar vacío", jugadorIA.getRack().isEmpty());
        
        // Inicializar el rack usando método público
        Map<String, Integer> rack = new HashMap<>();
        rack.put("A", 3);
        rack.put("E", 2);
        jugadorIA.inicializarRack(rack);
        
        // Verificar contenido del rack
        Map<String, Integer> fichasActuales = jugadorIA.getRack();
        assertNotNull("El rack no debería ser null", fichasActuales);
        assertEquals("Debería haber 3 fichas 'A'", Integer.valueOf(3), fichasActuales.get("A"));
        assertEquals("Debería haber 2 fichas 'E'", Integer.valueOf(2), fichasActuales.get("E"));
    }
    
    /**
     * Pre: Se ha creado una instancia de JugadorIA con puntuación de última partida inicial 0.
     * Post: Se verifica que el método getPuntaje() devuelve correctamente la puntuación de la última partida.
     * 
     * Comprueba la funcionalidad del método alternativo para obtener la puntuación de la última partida.
     * Aporta validación del correcto acceso al puntaje específico.
     */
    @Test
    public void testGetPuntaje() {
        assertEquals("El puntaje inicial debería ser 0", 0, jugadorIA.getPuntaje());
        
        jugadorIA.setPuntuacionUltimaPartida(75);
        assertEquals("El puntaje debería ser 75", 75, jugadorIA.getPuntaje());
    }
    
    /**
     * Pre: Se ha creado una instancia de JugadorIA con puntaje inicial 0.
     * Post: Se verifica que el método addPuntaje() incrementa correctamente el puntaje.
     * 
     * Comprueba la funcionalidad para incrementar el puntaje de la partida actual.
     * Aporta validación del correcto incremento acumulativo de puntos en la partida.
     */
    @Test
    public void testAddPuntaje() {
        jugadorIA.addPuntaje(25);
        assertEquals("El puntaje debería ser 25", 25, jugadorIA.getPuntaje());
        
        jugadorIA.addPuntaje(25);
        assertEquals("El puntaje debería ser 50", 50, jugadorIA.getPuntaje());
    }
    
    /**
     * Pre: Se ha creado una instancia de JugadorIA con skipTrack inicial 0.
     * Post: Se verifica que los métodos para gestionar el contador de saltos funcionan correctamente.
     * 
     * Comprueba la funcionalidad para gestionar el contador de turnos saltados.
     * Aporta validación del correcto incremento y modificación del contador de saltos.
     */
    @Test
    public void testSkipTrack() {
        assertEquals("El skip track inicial debería ser 0", 0, jugadorIA.getSkipTrack());
        
        jugadorIA.addSkipTrack();
        assertEquals("El skip track debería ser 1", 1, jugadorIA.getSkipTrack());
        
        jugadorIA.setSkipTrack(3);
        assertEquals("El skip track debería ser 3", 3, jugadorIA.getSkipTrack());
    }
    
    /**
     * Pre: Se ha creado una instancia de JugadorIA con puntuación y dificultad modificadas.
     * Post: Se verifica que el método toString() incluye la información relevante.
     * 
     * Comprueba la representación textual del jugador IA.
     * Aporta validación de la correcta generación de cadenas informativas.
     */
    @Test
    public void testToString() {
        jugadorIA.setPuntuacion(100);
        jugadorIA.setNivelDificultad(Dificultad.DIFICIL);
        
        String resultado = jugadorIA.toString();
        
        assertTrue("toString() debería contener el nombre", resultado.contains(jugadorIA.getNombre()));
        assertTrue("toString() debería contener la puntuación", resultado.contains("100"));
        assertTrue("toString() debería contener la dificultad", resultado.contains("DIFICIL"));
    }
    
    /**
     * Pre: Se ha creado un mock de JugadorIA con comportamiento configurado.
     * Post: Se verifica que los métodos del mock devuelven los valores esperados y que se han
     * realizado las llamadas esperadas.
     * 
     * Comprueba la integración con la biblioteca Mockito para pruebas con objetos simulados.
     * Aporta validación del correcto uso de mocks para simular comportamientos.
     */
    @Test
    public void testIntegracionMockito() {
        JugadorIA mockJugadorIA = Mockito.mock(JugadorIA.class);
        
        when(mockJugadorIA.getNombre()).thenReturn("IA_Mock");
        when(mockJugadorIA.getNivelDificultad()).thenReturn(Dificultad.DIFICIL);
        when(mockJugadorIA.getPuntuacion()).thenReturn(100);
        when(mockJugadorIA.esIA()).thenReturn(true);
        
        assertEquals("El mock debería devolver 'IA_Mock'", "IA_Mock", mockJugadorIA.getNombre());
        assertEquals("El mock debería devolver DIFICIL", Dificultad.DIFICIL, mockJugadorIA.getNivelDificultad());
        assertEquals("El mock debería devolver 100", 100, mockJugadorIA.getPuntuacion());
        assertTrue("El mock debería devolver true para esIA()", mockJugadorIA.esIA());
        
        verify(mockJugadorIA).getNombre();
        verify(mockJugadorIA).getNivelDificultad();
        verify(mockJugadorIA).getPuntuacion();
        verify(mockJugadorIA).esIA();
    }
    
    /**
     * Pre: Se ha creado una instancia de JugadorIA con rack inicial vacío.
     * Post: Se verifica que el método inicializarRack() establece correctamente el rack.
     * 
     * Comprueba la funcionalidad para inicializar el conjunto de fichas.
     * Aporta validación de la correcta asignación del rack de fichas al jugador IA.
     */
    @Test
    public void testInicializarRack() {
        Map<String, Integer> rack = new HashMap<>();
        rack.put("A", 3);
        rack.put("B", 2);
        
        jugadorIA.inicializarRack(rack);
        assertEquals("El rack debería contener las fichas inicializadas", rack, jugadorIA.getRack());
    }
    
    /**
     * Pre: Se ha creado una instancia de JugadorIA y se ha inicializado su rack con fichas.
     * Post: Se verifica que el método sacarFicha() extrae correctamente una ficha y actualiza
     * la cantidad disponible.
     * 
     * Comprueba la funcionalidad para extraer fichas del rack.
     * Aporta validación del correcto manejo de fichas, incluyendo casos límite.
     */
    @Test
    public void testSacarFicha() {
        Map<String, Integer> rack = new HashMap<>();
        rack.put("A", 3);
        rack.put("B", 1);
        jugadorIA.inicializarRack(rack);
        
        // Sacar una ficha 'A' (hay 3)
        jugadorIA.sacarFicha("A");
        assertEquals("Debería quedar 2 fichas 'A'", Integer.valueOf(2), jugadorIA.getRack().get("A"));
        
        // Sacar la ficha 'B' (hay 1)
        jugadorIA.sacarFicha("B");
        assertNull("No debería quedar ninguna ficha 'B'", jugadorIA.getRack().get("B"));
        
        // Sacar una ficha que no existe
        assertNull("Debería devolver null al intentar sacar una ficha que no existe", 
                 jugadorIA.sacarFicha("Z"));
    }
    
    /**
     * Pre: Se ha creado una instancia de JugadorIA y se ha inicializado su rack.
     * Post: Se verifica que el método agregarFicha() añade correctamente una ficha al rack.
     * 
     * Comprueba la funcionalidad para añadir fichas al rack.
     * Aporta validación del correcto incremento de fichas disponibles.
     */
    @Test
    public void testAgregarFicha() {
        Map<String, Integer> rack = new HashMap<>();
        jugadorIA.inicializarRack(rack);
        
        // Agregar una ficha 'A' (no hay ninguna)
        jugadorIA.agregarFicha("A");
        assertEquals("Debería haber 1 ficha 'A'", Integer.valueOf(1), jugadorIA.getRack().get("A"));
        
        // Agregar otra ficha 'A' (ya hay 1)
        jugadorIA.agregarFicha("A");
        assertEquals("Debería haber 2 fichas 'A'", Integer.valueOf(2), jugadorIA.getRack().get("A"));
    }
    
    /**
     * Pre: Se ha creado una instancia de JugadorIA y se ha inicializado su rack con fichas.
     * Post: Se verifica que el método getCantidadFichas() devuelve correctamente el número total
     * de fichas en el rack y se actualiza al sacar fichas.
     * 
     * Comprueba la funcionalidad para obtener la cantidad total de fichas.
     * Aporta validación del correcto conteo de fichas disponibles.
     */
    @Test
    public void testGetCantidadFichas() {
        Map<String, Integer> rack = new HashMap<>();
        rack.put("A", 3);
        rack.put("B", 2);
        jugadorIA.inicializarRack(rack);
        
        assertEquals("Debería haber un total de 5 fichas", 5, jugadorIA.getCantidadFichas());
        
        jugadorIA.sacarFicha("A");
        assertEquals("Debería haber un total de 4 fichas después de sacar una 'A'", 4, jugadorIA.getCantidadFichas());
    }
    
    /**
     * Pre: Se crean múltiples instancias de JugadorIA.
     * Post: Se verifica que cada instancia tiene un nombre único con formato apropiado.
     * 
     * Comprueba la generación de nombres únicos para cada instancia.
     * Aporta validación del correcto funcionamiento del contador global.
     */
    @Test
    public void testNombresUnicosGenerados() {
        JugadorIA ia1 = new JugadorIA(Dificultad.FACIL);
        JugadorIA ia2 = new JugadorIA(Dificultad.FACIL);
        JugadorIA ia3 = new JugadorIA(Dificultad.DIFICIL);
        
        String nombre1 = ia1.getNombre();
        String nombre2 = ia2.getNombre();
        String nombre3 = ia3.getNombre();
        
        assertTrue("Los nombres deberían empezar con 'IA_'", 
                 nombre1.startsWith("IA_") && nombre2.startsWith("IA_") && nombre3.startsWith("IA_"));
        
        assertTrue("Los nombres con dificultad FACIL deberían contener '_FACIL_'", 
                 nombre1.contains("_FACIL_") && nombre2.contains("_FACIL_"));
        assertTrue("Los nombres con dificultad DIFICIL deberían contener '_DIFICIL_'", 
                 nombre3.contains("_DIFICIL_"));
        
        assertNotEquals("Los nombres deberían ser diferentes", nombre1, nombre2);
        assertNotEquals("Los nombres deberían ser diferentes", nombre1, nombre3);
        assertNotEquals("Los nombres deberían ser diferentes", nombre2, nombre3);
    }
}