package scrabble.tests;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import scrabble.domain.models.Jugador;

/**
 * Test unitario para la clase Jugador
 */
public class JugadorTest {
    
    private Jugador jugador;
    private Map<String, Integer> fichasIniciales;
    
    /**
     * Método que se ejecuta antes de cada test.
     * Pre: Se necesita crear una instancia concreta de la clase abstracta Jugador 
     * y un mapa de fichas inicial para las pruebas.
     */
    @Before
    public void setUp() {
        // Pre: Se necesita crear una instancia concreta de la clase abstracta Jugador 
        // y un mapa de fichas inicial para las pruebas.
        
        // Creamos una clase anónima que extiende de Jugador (para poder instanciarla)
        jugador = new Jugador("TestJugador") {
            @Override
            public boolean esIA() {
                return false;
            }
        };
        
        // Creamos un mapa de fichas inicial para las pruebas
        fichasIniciales = new HashMap<>();
        fichasIniciales.put("A", 3);
        fichasIniciales.put("B", 2);
        fichasIniciales.put("C", 1);
    }
    
    /**
     * Método que se ejecuta después de cada test.
     * Post: Las referencias a objetos creados durante la prueba deben ser liberadas 
     * para permitir la recolección de basura.
     */
    @After
    public void tearDown() {
        // Post: Las referencias a objetos creados durante la prueba deben ser liberadas 
        // para permitir la recolección de basura.
        jugador = null;
        fichasIniciales = null;
    }
    
    /**
     * Prueba el constructor de Jugador.
     * Pre: No existe ninguna instancia con el nombre "NuevoJugador".
     * Post: Se ha creado correctamente una instancia con el nombre especificado.
     */
    @Test
    public void testConstructor() {
        // Pre: No existe ninguna instancia con el nombre "NuevoJugador".
        Jugador nuevoJugador = new Jugador("NuevoJugador") {
            @Override
            public boolean esIA() {
                return false;
            }
        };
        
        assertEquals("NuevoJugador", nuevoJugador.getNombre());
        assertEquals(0, nuevoJugador.getSkipTrack());
        assertNull(nuevoJugador.getRack());
        
        // Post: Se ha creado correctamente una instancia con el nombre especificado.
    }
    
    /**
     * Prueba el método getNombre.
     * Pre: Existe una instancia de Jugador, creada en setUp().
     * Post: El método getNombre devuelve el nombre correcto.
     */
    @Test
    public void testGetNombre() {
        // Pre: Existe una instancia de Jugador, creada en setUp().
        
        assertEquals("TestJugador", jugador.getNombre());
        
        // Post: El método getNombre devuelve el nombre correcto.
    }
    
    /**
     * Prueba el método inicializarRack.
     * Pre: Existe una instancia de Jugador, creada en setUp(), 
     * y un mapa de fichas inicial.
     * Post: El rack del jugador se ha inicializado correctamente con las fichas especificadas.
     */
    @Test
    public void testInicializarRack() {
        // Pre: Existe una instancia de Jugador, creada en setUp(), 
        // y un mapa de fichas inicial.
        
        jugador.inicializarRack(fichasIniciales);
        
        Map<String, Integer> rack = jugador.getRack();
        assertNotNull("El rack no debería ser null después de inicializarlo", rack);
        assertEquals("El rack debería tener 3 tipos de fichas", 3, rack.size());
        assertEquals("Debería haber 3 fichas 'A'", Integer.valueOf(3), rack.get("A"));
        assertEquals("Debería haber 2 fichas 'B'", Integer.valueOf(2), rack.get("B"));
        assertEquals("Debería haber 1 ficha 'C'", Integer.valueOf(1), rack.get("C"));
        
        // Post: El rack del jugador se ha inicializado correctamente con las fichas especificadas.
    }
    
    /**
     * Prueba el método getRack.
     * Pre: Existe una instancia de Jugador sin rack inicializado.
     * Post: El rack devuelto es null antes de inicializarlo, 
     * y contiene las fichas correctas después de inicializarlo.
     */
    @Test
    public void testGetRack() {
        // Pre: Existe una instancia de Jugador sin rack inicializado.
        
        assertNull("El rack debería ser null antes de inicializarlo", jugador.getRack());
        
        jugador.inicializarRack(fichasIniciales);
        Map<String, Integer> rack = jugador.getRack();
        
        assertNotNull("El rack no debería ser null después de inicializarlo", rack);
        assertEquals("El rack debería tener 3 tipos de fichas", 3, rack.size());
        
        // Post: El rack devuelto es null antes de inicializarlo, 
        // y contiene las fichas correctas después de inicializarlo.
    }
    
    /**
     * Prueba el método sacarFicha.
     * Pre: Existe una instancia de Jugador con rack inicializado con A:3, B:2, C:1.
     * Post: La ficha se extrae correctamente y la cantidad se reduce en el rack.
     * Sacar una ficha inexistente devuelve null, y sacar todas las fichas de un tipo 
     * lo elimina del rack.
     */
    @Test
    public void testSacarFicha() {
        // Pre: Existe una instancia de Jugador con rack inicializado con A:3, B:2, C:1.
        jugador.inicializarRack(fichasIniciales);
        
        // Test sacar una ficha que existe (A) 
        scrabble.helpers.Tuple<String, Integer> resultado = jugador.sacarFicha("A");
        assertNotNull("El resultado no debe ser null", resultado);
        assertEquals("La ficha extraída debe ser A", "A", resultado.x);
        assertEquals("La cantidad restante debe ser 2", Integer.valueOf(2), resultado.y);
        assertEquals("Debería quedar 2 fichas 'A'", Integer.valueOf(2), jugador.getRack().get("A"));
        
        // Test sacar una ficha que no existe (X)
        assertNull("Sacar una ficha que no existe debe devolver null", jugador.sacarFicha("X"));
        
        // Test sacar todas las fichas de un tipo
        resultado = jugador.sacarFicha("C");
        assertNotNull("El resultado no debe ser null", resultado);
        assertEquals("La ficha extraída debe ser C", "C", resultado.x);
        assertEquals("La cantidad restante debe ser 0", Integer.valueOf(0), resultado.y);
        assertNull("La clave 'C' debe desaparecer del rack", jugador.getRack().get("C"));
        assertEquals("El rack debe tener ahora solo 2 tipos de fichas", 2, jugador.getRack().size());
        
        // Post: La ficha se extrae correctamente y la cantidad se reduce en el rack.
        // Sacar una ficha inexistente devuelve null, y sacar todas las fichas de un tipo 
        // lo elimina del rack.
    }
    
    /**
     * Prueba el método agregarFicha.
     * Pre: Existe una instancia de Jugador con rack inicializado con A:3, B:2, C:1.
     * Post: Las fichas se agregan correctamente al rack, 
     * aumentando la cantidad si ya existe el tipo de ficha, 
     * o creando una nueva entrada si es un tipo nuevo.
     */
    @Test
    public void testAgregarFicha() {
        // Pre: Existe una instancia de Jugador con rack inicializado con A:3, B:2, C:1.
        jugador.inicializarRack(fichasIniciales);
        
        // Test agregar una ficha de un tipo existente (A)
        jugador.agregarFicha("A");
        assertEquals("Debería haber 4 fichas 'A'", Integer.valueOf(4), jugador.getRack().get("A"));
        
        // Test agregar una ficha de un nuevo tipo (X)
        jugador.agregarFicha("X");
        assertEquals("Debería haber 1 ficha 'X'", Integer.valueOf(1), jugador.getRack().get("X"));
        assertEquals("El rack debe tener ahora 4 tipos de fichas", 4, jugador.getRack().size());
        
        // Post: Las fichas se agregan correctamente al rack, 
        // aumentando la cantidad si ya existe el tipo de ficha, 
        // o creando una nueva entrada si es un tipo nuevo.
    }
    
    /**
     * Prueba el método getCantidadFichas.
     * Pre: Existe una instancia de Jugador con rack inicializado con A:3, B:2, C:1.
     * Post: El método devuelve la cantidad correcta de fichas en el rack.
     */
    @Test
    public void testGetCantidadFichas() {
        // Pre: Existe una instancia de Jugador con rack inicializado con A:3, B:2, C:1.
        jugador.inicializarRack(fichasIniciales);
        
        // Verificamos que la suma total de fichas es 3 + 2 + 1 = 6
        assertEquals("Debería haber 6 fichas en total", 6, jugador.getCantidadFichas());
        
        // Agregamos una ficha más
        jugador.agregarFicha("D");
        assertEquals("Debería haber 7 fichas en total", 7, jugador.getCantidadFichas());
        
        // Sacamos una ficha
        jugador.sacarFicha("A");
        assertEquals("Debería haber 6 fichas en total", 6, jugador.getCantidadFichas());
        
        // Post: El método devuelve la cantidad correcta de fichas en el rack.
    }
    
    /**
     * Prueba los métodos relacionados con el skipTrack (addSkipTrack, clearSkipTrack, getSkipTrack).
     * Pre: Existe una instancia de Jugador con skipTrack inicializado a 0.
     * Post: El skipTrack se incrementa correctamente con addSkipTrack, 
     * se reinicia a 0 con clearSkipTrack, y getSkipTrack devuelve el valor actual.
     */
    @Test
    public void testSkipTrackOperations() {
        // Pre: Existe una instancia de Jugador con skipTrack inicializado a 0.
        
        assertEquals("El skipTrack inicial debe ser 0", 0, jugador.getSkipTrack());
        
        jugador.addSkipTrack();
        assertEquals("El skipTrack debe ser 1 después de addSkipTrack", 1, jugador.getSkipTrack());
        
        jugador.addSkipTrack();
        assertEquals("El skipTrack debe ser 2 después de otro addSkipTrack", 2, jugador.getSkipTrack());
        
        jugador.clearSkipTrack();
        assertEquals("El skipTrack debe ser 0 después de clearSkipTrack", 0, jugador.getSkipTrack());
        
        // Post: El skipTrack se incrementa correctamente con addSkipTrack, 
        // se reinicia a 0 con clearSkipTrack, y getSkipTrack devuelve el valor actual.
    }
    
    /**
     * Prueba el método esIA, que debe ser implementado por las subclases.
     * Pre: Existe una instancia de Jugador con la implementación específica 
     * de esIA() que devuelve false.
     * Post: El método esIA devuelve false para esta implementación concreta.
     */
    @Test
    public void testEsIA() {
        // Pre: Existe una instancia de Jugador con la implementación específica 
        // de esIA() que devuelve false.
        
        assertFalse("Esta implementación de Jugador debe devolver false en esIA()", jugador.esIA());
        
        // Post: El método esIA devuelve false para esta implementación concreta.
    }
}
