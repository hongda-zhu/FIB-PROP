package scrabble.tests;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import scrabble.domain.models.JugadorIA;
import scrabble.helpers.Dificultad;

/**
 * Test unitario para la clase JugadorIA.
 * Prueba la funcionalidad específica relacionada con jugadores controlados por IA.
 */
public class JugadorIATest {
    
    private JugadorIA jugadorIA;
    private JugadorIA jugadorIAConNombre;
    
    /**
     * Método que se ejecuta antes de cada test.
     * Prepara el entorno para la prueba.
     */
    @Before
    public void setUp() {
        // Pre:  Se necesitan instancias de JugadorIA para las pruebas.
        // - Una con nombre generado automáticamente y dificultad FACIL
        // - Otra con nombre explícito y dificultad DIFICIL
        jugadorIA = new JugadorIA(Dificultad.FACIL);
        jugadorIAConNombre = new JugadorIA("IA_TEST", Dificultad.DIFICIL);
    }
    
    /**
     * Método que se ejecuta después de cada test.
     * Limpia el entorno después de la prueba.
     */
    @After
    public void tearDown() {
        // Post:  Las referencias a objetos creados durante la prueba deben ser 
        // liberadas para permitir la recolección de basura y evitar interferencias entre tests.
        jugadorIA = null;
        jugadorIAConNombre = null;
    }
    
    /**
     * Prueba el constructor que genera un nombre automático basado en la dificultad.
     */
    @Test
    public void testConstructorConDificultad() {
        // Pre:  No existe una instancia de JugadorIA específica para esta prueba.
        // Se espera que al crear un nuevo JugadorIA solo con dificultad, genere un nombre automático
        // con formato "IA_DIFICULTAD_X" donde X es un número incremental.
        
        JugadorIA ia = new JugadorIA(Dificultad.FACIL);
        
        assertTrue("El nombre debe incluir la dificultad FACIL", ia.getNombre().contains("FACIL"));
        assertTrue("El nombre debe comenzar con IA_", ia.getNombre().startsWith("IA_"));
        assertEquals("La dificultad debe ser FACIL", Dificultad.FACIL, ia.getNivelDificultad());
        assertNotNull("El rack no debe ser null", ia.getRack());
        assertTrue("El rack debe estar vacío inicialmente", ia.getRack().isEmpty());
        
        // Post:  Se ha creado correctamente una instancia de JugadorIA con:
        // - Nombre generado automáticamente con formato "IA_FACIL_X"
        // - Nivel de dificultad FACIL
        // - Rack inicializado como HashMap vacío (no null)
    }
    
    /**
     * Prueba el constructor con nombre explícito y dificultad.
     */
    @Test
    public void testConstructorConNombreYDificultad() {
        // Pre:  No existe una instancia de JugadorIA con el nombre "IA_PRUEBA".
        // Se espera que al crear un JugadorIA con nombre explícito, use ese nombre exacto
        // en lugar de generar uno.
        
        JugadorIA ia = new JugadorIA("IA_PRUEBA", Dificultad.DIFICIL);
        
        assertEquals("El nombre debe ser el proporcionado", "IA_PRUEBA", ia.getNombre());
        assertEquals("La dificultad debe ser DIFICIL", Dificultad.DIFICIL, ia.getNivelDificultad());
        assertNotNull("El rack no debe ser null", ia.getRack());
        assertTrue("El rack debe estar vacío inicialmente", ia.getRack().isEmpty());
        
        // Post:  Se ha creado correctamente una instancia de JugadorIA con:
        // - Nombre "IA_PRUEBA" (exactamente el proporcionado)
        // - Nivel de dificultad DIFICIL
        // - Rack inicializado como HashMap vacío (no null)
    }
    
    /**
     * Prueba la generación de nombres únicos para las IAs.
     */
    @Test
    public void testGeneracionNombresUnicos() {
        // Pre:  El contador estático contadorIAs de JugadorIA determina
        // el número asignado a cada nueva IA creada con nombre automático. Este
        // test verifica que los nombres generados son únicos.
        
        JugadorIA ia1 = new JugadorIA(Dificultad.FACIL);
        JugadorIA ia2 = new JugadorIA(Dificultad.FACIL);
        JugadorIA ia3 = new JugadorIA(Dificultad.FACIL);
        
        assertNotEquals("Las IAs deben tener nombres diferentes", ia1.getNombre(), ia2.getNombre());
        assertNotEquals("Las IAs deben tener nombres diferentes", ia2.getNombre(), ia3.getNombre());
        assertNotEquals("Las IAs deben tener nombres diferentes", ia1.getNombre(), ia3.getNombre());
        
        // Post:  Se han creado tres instancias de JugadorIA con la misma dificultad
        // pero con nombres diferentes. El mecanismo de generación de nombres garantiza
        // la unicidad mediante un contador incremental.
    }
    
    /**
     * Prueba el método setNivelDificultad().
     */
    @Test
    public void testSetNivelDificultad() {
        // Pre:  Existe una instancia de JugadorIA con dificultad FACIL,
        // inicializada en setUp(). El método setNivelDificultad debe cambiar
        // la dificultad de la IA.
        
        assertEquals("La dificultad inicial debe ser FACIL", Dificultad.FACIL, jugadorIA.getNivelDificultad());
        
        jugadorIA.setNivelDificultad(Dificultad.DIFICIL);
        assertEquals("La dificultad debe ser DIFICIL después de cambiarla", 
                    Dificultad.DIFICIL, jugadorIA.getNivelDificultad());
        
        // Post:  La dificultad de la IA ha sido cambiada exitosamente de FACIL a DIFICIL.
        // Este cambio afecta el comportamiento de la IA en el juego (estrategias más avanzadas).
    }
    
    /**
     * Prueba el método getNivelDificultad().
     */
    @Test
    public void testGetNivelDificultad() {
        // Pre:  Existen dos instancias de JugadorIA con diferentes dificultades,
        // inicializadas en setUp():
        // - jugadorIA con dificultad FACIL
        // - jugadorIAConNombre con dificultad DIFICIL
        
        assertEquals("Debe devolver FACIL para jugadorIA", 
                    Dificultad.FACIL, jugadorIA.getNivelDificultad());
        assertEquals("Debe devolver DIFICIL para jugadorIAConNombre", 
                    Dificultad.DIFICIL, jugadorIAConNombre.getNivelDificultad());
        
        // Post:  El método getNivelDificultad() devuelve correctamente el nivel
        // de dificultad asociado a cada instancia de JugadorIA, sin modificar ningún estado.
    }
    
    /**
     * Prueba el método getPartidasJugadas().
     */
    @Test
    public void testGetPartidasJugadas() {
        // Pre:  Existe una instancia de JugadorIA inicializada en setUp().
        // El método getPartidasJugadas() debe devolver 1 para jugadores IA (a diferencia de
        // los jugadores humanos que tienen un contador de partidas real).
        
        assertEquals("Debe devolver 1 como número de partidas jugadas", 
                    1, jugadorIA.getPartidasJugadas());
        
        // Post:  El método getPartidasJugadas() devuelve siempre 1 para las IA,
        // ya que no mantienen estadísticas entre partidas. Este valor se utiliza para
        // mostrar las estadísticas en el ranking.
    }
    
    /**
     * Prueba el método esIA().
     */
    @Test
    public void testEsIA() {
        // Pre:  Existe una instancia de JugadorIA inicializada en setUp().
        // El método esIA() implementa el método abstracto de la clase Jugador y
        // debe devolver true para jugadores IA.
        
        assertTrue("Debe devolver true para un jugador IA", jugadorIA.esIA());
        
        // Post:  El método esIA() devuelve true, permitiendo que otras partes del
        // sistema identifiquen y traten de manera diferente a los jugadores IA
        // (por ejemplo, para no pedirles interacción).
    }
    
    /**
     * Prueba el método getFichas().
     */
    @Test
    public void testGetFichas() {
        // Pre:  Existe una instancia de JugadorIA con rack vacío,
        // inicializada en setUp(). El método getFichas() debe devolver el rack del jugador.
        
        assertTrue("El rack debe estar vacío inicialmente", jugadorIA.getFichas().isEmpty());
        
        Map<String, Integer> fichas = new HashMap<>();
        fichas.put("A", 3);
        fichas.put("B", 2);
        jugadorIA.inicializarRack(fichas);
        
        assertEquals("Debe devolver el mismo rack", fichas, jugadorIA.getFichas());
        assertEquals("Debe tener 3 fichas 'A'", Integer.valueOf(3), jugadorIA.getFichas().get("A"));
        assertEquals("Debe tener 2 fichas 'B'", Integer.valueOf(2), jugadorIA.getFichas().get("B"));
        
        // Post:  El método getFichas() devuelve correctamente una referencia al
        // rack del jugador IA. Después de inicializar el rack con A(3) y B(2), el método
        // muestra estas fichas correctamente.
    }
    
    /**
     * Prueba el método toString().
     */
    @Test
    public void testToString() {
        // Pre:  Existe una instancia de JugadorIA con nombre "IA_TEST" y 
        // dificultad DIFICIL, inicializada en setUp(). El método toString() debe generar
        // una representación textual del jugador con sus propiedades principales.
        
        String representacion = jugadorIAConNombre.toString();
        assertTrue("Debe contener el nombre", representacion.contains("IA_TEST"));
        assertTrue("Debe contener la dificultad", representacion.contains("DIFICIL"));
        
        // Post:  El método toString() genera una cadena que contiene la información
        // relevante del jugador IA (nombre y nivel de dificultad). Esta representación es útil
        // para depuración y registros.
    }
    
    /**
     * Prueba la herencia de métodos de la clase Jugador.
     */
    @Test
    public void testMetodosHeredados() {
        // Pre:  Existe una instancia de JugadorIA inicializada en setUp().
        // Como JugadorIA hereda de Jugador, debe tener acceso a todos los métodos
        // heredados para gestionar fichas y turnos pasados (skipTrack).
        
        Map<String, Integer> fichas = new HashMap<>();
        fichas.put("A", 3);
        fichas.put("B", 2);
        jugadorIA.inicializarRack(fichas);
        
        // Agregar ficha
        jugadorIA.agregarFicha("C");
        assertTrue("Debe contener la ficha agregada", jugadorIA.getRack().containsKey("C"));
        assertEquals("Debe tener 1 ficha 'C'", Integer.valueOf(1), jugadorIA.getRack().get("C"));
        
        // Sacar ficha
        jugadorIA.sacarFicha("A");
        assertEquals("Debe quedar con 2 fichas 'A'", Integer.valueOf(2), jugadorIA.getRack().get("A"));
        
        // Contar fichas
        assertEquals("Debe tener 5 fichas en total", 5, jugadorIA.getCantidadFichas());
        
        // Operaciones de skipTrack
        assertEquals("SkipTrack inicial debe ser 0", 0, jugadorIA.getSkipTrack());
        jugadorIA.addSkipTrack();
        assertEquals("SkipTrack debe ser 1 después de incrementar", 1, jugadorIA.getSkipTrack());
        
        // Post:  Los métodos heredados de la clase Jugador funcionan correctamente
        // en JugadorIA, permitiendo gestionar fichas y turnos pasados. El rack ahora contiene
        // A(2), B(2), C(1) y el skipTrack es 1.
    }
}
