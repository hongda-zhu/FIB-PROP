package scrabble.tests;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import scrabble.domain.models.JugadorHumano;
import scrabble.helpers.Tuple;

/**
 * Test unitario para la clase JugadorHumano.
 * Prueba la funcionalidad específica relacionada con jugadores humanos.
 */
public class JugadorHumanoTest {
    
    private JugadorHumano jugador;
    
    /**
     * Método que se ejecuta antes de cada test.
     * Prepara el entorno para la prueba.
     */
    @Before
    public void setUp() {
        // Pre:  Se necesita una instancia de JugadorHumano para las pruebas.
        // El jugador humano se inicializa con nombre="JugadorTest", enPartida=false,
        // nombrePartidaActual="" y rack vacío.
        jugador = new JugadorHumano("JugadorTest");
    }
    
    /**
     * Método que se ejecuta después de cada test.
     * Limpia el entorno después de la prueba.
     */
    @After
    public void tearDown() {
        // Post: Las referencias a objetos creados durante la prueba deben ser 
        // liberadas para permitir la recolección de basura y evitar interferencias entre tests.
        jugador = null;
    }
    
    /**
     * Prueba el constructor de JugadorHumano.
     */
    @Test
    public void testConstructor() {
        // Pre:  No existe ninguna instancia de JugadorHumano con el nombre "NuevoJugador".
        // Se espera que al crear un nuevo JugadorHumano, sus atributos se inicialicen con valores predeterminados:
        // - nombre: el nombre proporcionado
        // - enPartida: false
        // - nombrePartidaActual: ""
        // - rack: HashMap vacío (no null)
        
        JugadorHumano j = new JugadorHumano("NuevoJugador");
        
        assertEquals("El nombre debe ser el proporcionado", "NuevoJugador", j.getNombre());
        assertFalse("El jugador no debe estar en partida inicialmente", j.isEnPartida());
        assertEquals("El nombre de partida debe estar vacío inicialmente", "", j.getNombrePartidaActual());
        assertNotNull("El rack no debe ser null", j.getRack());
        assertTrue("El rack debe estar vacío inicialmente", j.getRack().isEmpty());
        
        // Post:  Se ha creado correctamente una instancia de JugadorHumano con
        // nombre="NuevoJugador", enPartida=false, nombrePartidaActual="" y rack vacío.
        // El objeto está listo para ser utilizado en el juego.
    }
    
    /**
     * Prueba el método setEnPartida() cuando se establece a true.
     */
    @Test
    public void testSetEnPartidaTrue() {
        // Pre:  Existe una instancia de JugadorHumano inicializada en setUp()
        // con enPartida=false. El método setEnPartida(true) debe cambiar el estado
        // del jugador a "en partida".
        
        assertFalse("Inicialmente el jugador no debe estar en partida", jugador.isEnPartida());
        
        jugador.setEnPartida(true);
        
        assertTrue("El jugador debe estar en partida después de setEnPartida(true)", jugador.isEnPartida());
        
        // Post:  El estado del jugador ha cambiado a enPartida=true.
        // Esto permite que el jugador participe en una partida y controla que
        // no pueda unirse a múltiples partidas simultáneamente.
    }
    
    /**
     * Prueba el método setEnPartida() cuando se establece a false.
     */
    @Test
    public void testSetEnPartidaFalse() {
        // Pre:  Se configura una instancia de JugadorHumano como si estuviera
        // en una partida (enPartida=true, nombrePartidaActual="PartidaPrueba").
        // El método setEnPartida(false) debe cambiar el estado y limpiar el nombre de partida.
        
        jugador.setEnPartida(true);
        jugador.setNombrePartidaActual("PartidaPrueba");
        
        assertTrue("El jugador debe estar en partida", jugador.isEnPartida());
        assertEquals("El nombre de partida debe ser 'PartidaPrueba'", "PartidaPrueba", jugador.getNombrePartidaActual());
        
        jugador.setEnPartida(false);
        
        assertFalse("El jugador no debe estar en partida después de setEnPartida(false)", jugador.isEnPartida());
        assertEquals("El nombre de partida debe estar vacío", "", jugador.getNombrePartidaActual());
        
        // Post:  El estado del jugador ha cambiado a enPartida=false y
        // nombrePartidaActual="". Esto indica que el jugador ha salido de la partida
        // y está disponible para unirse a otra partida.
    }
    
    /**
     * Prueba el método isEnPartida().
     */
    @Test
    public void testIsEnPartida() {
        // Pre:  Existe una instancia de JugadorHumano inicializada en setUp()
        // con enPartida=false. El método isEnPartida() debe devolver el estado actual
        // del jugador respecto a si está participando en una partida.
        
        assertFalse("El jugador no debe estar en partida inicialmente", jugador.isEnPartida());
        
        jugador.setEnPartida(true);
        assertTrue("El jugador debe estar en partida después de setEnPartida(true)", jugador.isEnPartida());
        
        jugador.setEnPartida(false);
        assertFalse("El jugador no debe estar en partida después de setEnPartida(false)", jugador.isEnPartida());
        
        // Post:  El método isEnPartida() devuelve correctamente el estado actual
        // del jugador (true cuando está en partida, false cuando no lo está).
        // El estado final del jugador es enPartida=false.
    }
    
    /**
     * Prueba el método getNombrePartidaActual().
     */
    @Test
    public void testGetNombrePartidaActual() {
        // Pre:  Existe una instancia de JugadorHumano inicializada en setUp()
        // con nombrePartidaActual="". El método getNombrePartidaActual() debe devolver
        // el nombre de la partida en la que está participando el jugador.
        
        assertEquals("El nombre de partida debe estar vacío inicialmente", "", jugador.getNombrePartidaActual());
        
        jugador.setNombrePartidaActual("PartidaTest");
        
        assertEquals("Debe devolver el nombre de partida establecido", "PartidaTest", jugador.getNombrePartidaActual());
        
        // Post:  El método getNombrePartidaActual() devuelve correctamente
        // el nombre de la partida (antes vacío, ahora "PartidaTest").
        // Esto permite identificar en qué partida está participando el jugador.
    }
    
    /**
     * Prueba el método setNombrePartidaActual().
     */
    @Test
    public void testSetNombrePartidaActual() {
        // Pre:  Existe una instancia de JugadorHumano inicializada en setUp()
        // con nombrePartidaActual="". El método setNombrePartidaActual() debe cambiar
        // el nombre de la partida en la que está participando el jugador.
        
        jugador.setNombrePartidaActual("PartidaTest");
        
        assertEquals("El nombre de partida debe ser 'PartidaTest'", "PartidaTest", jugador.getNombrePartidaActual());
        
        jugador.setNombrePartidaActual("OtraPartida");
        assertEquals("El nombre de partida debe ser 'OtraPartida'", "OtraPartida", jugador.getNombrePartidaActual());
        
        // Post:  El método setNombrePartidaActual() cambia correctamente
        // el nombre de la partida (primero a "PartidaTest", luego a "OtraPartida").
        // Nótese que cambiar el nombre de partida no cambia automáticamente
        // el estado enPartida del jugador.
    }
    
    /**
     * Prueba el método esIA().
     */
    @Test
    public void testEsIA() {
        // Pre:  Existe una instancia de JugadorHumano inicializada en setUp().
        // El método esIA() implementa el método abstracto de la clase Jugador y debe
        // devolver false para jugadores humanos.
        
        assertFalse("Un jugador humano nunca debe ser IA", jugador.esIA());
        
        // Post:  El método esIA() devuelve false, permitiendo que otras
        // partes del sistema identifiquen y traten de manera diferente a los
        // jugadores humanos (por ejemplo, para pedirles interacción).
    }
    
    /**
     * Prueba el método getFichas().
     */
    @Test
    public void testGetFichas() {
        // Pre:  Existe una instancia de JugadorHumano inicializada en setUp()
        // con rack vacío. El método getFichas() debe devolver el rack del jugador
        // para que otras clases puedan consultar las fichas disponibles.
        
        assertTrue("El rack debe estar vacío inicialmente", jugador.getFichas().isEmpty());
        
        Map<String, Integer> fichas = new HashMap<>();
        fichas.put("A", 3);
        fichas.put("B", 2);
        jugador.inicializarRack(fichas);
        
        assertEquals("Debe devolver el mismo rack", fichas, jugador.getFichas());
        assertEquals("Debe tener 3 fichas 'A'", Integer.valueOf(3), jugador.getFichas().get("A"));
        assertEquals("Debe tener 2 fichas 'B'", Integer.valueOf(2), jugador.getFichas().get("B"));
        
        // Post:  El método getFichas() devuelve correctamente una referencia
        // al rack del jugador. Después de inicializar el rack con A(3) y B(2), el método
        // muestra estas fichas correctamente.
    }
    
    /**
     * Prueba el método toString().
     */
    @Test
    public void testToString() {
        // Pre:  Existe una instancia de JugadorHumano inicializada en setUp().
        // Se configura el jugador con valores específicos (enPartida=true, nombrePartidaActual="PartidaTest")
        // para probar que toString() muestra toda la información relevante.
        
        jugador.setEnPartida(true);
        jugador.setNombrePartidaActual("PartidaTest");
        
        String representacion = jugador.toString();
        assertTrue("Debe contener el nombre", representacion.contains("JugadorTest"));
        assertTrue("Debe contener el estado enPartida", representacion.contains("enPartida=true"));
        assertTrue("Debe contener el nombre de partida", representacion.contains("PartidaTest"));
        
        // Post:  El método toString() genera una cadena que contiene toda la
        // información relevante del jugador (nombre, estado enPartida y nombre de partida).
        // Esta representación es útil para depuración y registros.
    }
    
    /**
     * Prueba la herencia de métodos de la clase Jugador.
     */
    @Test
    public void testMetodosHeredados() {
        // Pre:  Existe una instancia de JugadorHumano inicializada en setUp().
        // Como JugadorHumano hereda de Jugador, debe tener acceso a todos los métodos
        // heredados para gestionar fichas y turnos pasados (skipTrack).
        
        Map<String, Integer> fichas = new HashMap<>();
        fichas.put("A", 3);
        fichas.put("B", 2);
        jugador.inicializarRack(fichas);
        
        // Agregar ficha
        jugador.agregarFicha("C");
        assertTrue("Debe contener la ficha agregada", jugador.getRack().containsKey("C"));
        assertEquals("Debe tener 1 ficha 'C'", Integer.valueOf(1), jugador.getRack().get("C"));
        
        // Sacar ficha
        Tuple<String, Integer> resultado = jugador.sacarFicha("A");
        assertEquals("La ficha extraída debe ser 'A'", "A", resultado.x);
        assertEquals("Deben quedar 2 fichas 'A'", Integer.valueOf(2), resultado.y);
        assertEquals("El rack debe actualizarse a 2 fichas 'A'", Integer.valueOf(2), jugador.getRack().get("A"));
        
        // Contar fichas
        assertEquals("Debe tener 5 fichas en total", 5, jugador.getCantidadFichas());
        
        // Operaciones de skipTrack
        assertEquals("SkipTrack inicial debe ser 0", 0, jugador.getSkipTrack());
        jugador.addSkipTrack();
        assertEquals("SkipTrack debe ser 1 después de incrementar", 1, jugador.getSkipTrack());
        
        // Post:  Los métodos heredados de la clase Jugador funcionan correctamente
        // en JugadorHumano, permitiendo gestionar fichas y turnos pasados. El rack ahora contiene
        // A(2), B(2), C(1) y el skipTrack es 1.
    }
}
