package scrabble.tests;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import scrabble.domain.models.Jugador;
import scrabble.helpers.Tuple;

/**
 * Test unitario para la clase abstracta Jugador.
 * Utiliza una implementación concreta para probar los métodos.
 */
public class JugadorTest {
    
    private Jugador jugador;
    private Map<String, Integer> fichasIniciales;
    
    /**
     * Implementación concreta de Jugador para pruebas
     */
    class JugadorConcretoTest extends Jugador {
        public JugadorConcretoTest(String nombre) {
            super(nombre);
        }
        
        @Override
        public boolean esIA() {
            return false;
        }
    }
    
    /**
     * Método que se ejecuta antes de cada test.
     * Prepara el entorno para la prueba.
     */
    @Before
    public void setUp() {
        // Pre:  Se necesita crear una instancia concreta de la clase abstracta Jugador 
        // para poder probar sus métodos, y un mapa de fichas inicial para las pruebas.
        jugador = new JugadorConcretoTest("JugadorTest");
        
        fichasIniciales = new HashMap<>();
        fichasIniciales.put("A", 3);
        fichasIniciales.put("B", 2);
        fichasIniciales.put("C", 1);
        
        jugador.inicializarRack(fichasIniciales);
    }
    
    /**
     * Método que se ejecuta después de cada test.
     * Limpia el entorno después de la prueba.
     */
    @After
    public void tearDown() {
        // Post:  Las referencias a objetos creados durante la prueba deben ser 
        // liberadas para permitir la recolección de basura y evitar interferencias entre tests.
        jugador = null;
        fichasIniciales = null;
    }
    
    /**
     * Prueba el constructor de Jugador.
     */
    @Test
    public void testConstructor() {
        // Pre:  No existe ninguna instancia de Jugador con el nombre "NuevoJugador".
        // Se espera que al crear un nuevo Jugador, sus atributos se inicialicen con valores predeterminados:
        // - nombre: el nombre proporcionado
        // - skipTrack: 0
        // - rack: null
        
        Jugador j = new JugadorConcretoTest("NuevoJugador");
        
        assertEquals("El nombre debe coincidir con el proporcionado", "NuevoJugador", j.getNombre());
        assertEquals("El skipTrack debe inicializarse a 0", 0, j.getSkipTrack());
        assertNull("El rack debe inicializarse a null", j.getRack());
        
        // Post:  Se ha creado correctamente una instancia de Jugador con 
        // nombre="NuevoJugador", skipTrack=0 y rack=null. El objeto está listo para ser 
        // utilizado en el juego tras inicializar su rack.
    }
    
    /**
     * Prueba el método getNombre.
     */
    @Test
    public void testGetNombre() {
        // Pre:  Existe una instancia de Jugador con nombre="JugadorTest", 
        // inicializada en el método setUp().
        
        assertEquals("El nombre debe ser 'JugadorTest'", "JugadorTest", jugador.getNombre());
        
        // Post:  El método getNombre() devuelve correctamente el nombre del jugador 
        // sin alterar ningún estado del objeto.
    }
    
    /**
     * Prueba el método inicializarRack.
     */
    @Test
    public void testInicializarRack() {
        // Pre:  Existe una instancia de Jugador con un rack ya inicializado 
        // con fichas A(3), B(2), C(1). Se va a cambiar el rack completo por uno nuevo.
        
        Map<String, Integer> nuevoRack = new HashMap<>();
        nuevoRack.put("X", 5);
        nuevoRack.put("Y", 1);
        
        jugador.inicializarRack(nuevoRack);
        
        assertEquals("El rack debe ser el mismo que se pasó al método", nuevoRack, jugador.getRack());
        assertEquals("La cantidad de fichas 'X' debe ser 5", Integer.valueOf(5), jugador.getRack().get("X"));
        assertEquals("La cantidad de fichas 'Y' debe ser 1", Integer.valueOf(1), jugador.getRack().get("Y"));
        
        // Post:  El rack del jugador ha sido reemplazado completamente por el nuevo rack
        // con fichas X(5) e Y(1). Las fichas anteriores (A, B, C) ya no están disponibles.
    }
    
    /**
     * Prueba el método getRack.
     */
    @Test
    public void testGetRack() {
        // Pre:  Existe una instancia de Jugador con un rack inicializado 
        // con fichas A(3), B(2), C(1) en el método setUp().
        
        Map<String, Integer> rack = jugador.getRack();
        
        assertNotNull("El rack no debe ser null", rack);
        assertEquals("La cantidad de fichas 'A' debe ser 3", Integer.valueOf(3), rack.get("A"));
        assertEquals("La cantidad de fichas 'B' debe ser 2", Integer.valueOf(2), rack.get("B"));
        assertEquals("La cantidad de fichas 'C' debe ser 1", Integer.valueOf(1), rack.get("C"));
        
        // Post:  Se ha obtenido correctamente una referencia al rack del jugador.
        // Esta referencia permite modificar el rack original, no es una copia defensiva.
    }
    
    /**
     * Prueba el método sacarFicha en diferentes escenarios.
     */
    @Test
    public void testSacarFicha() {
        // Pre:  Existe una instancia de Jugador con un rack inicializado 
        // con fichas A(3), B(2), C(1). El método sacarFicha debe disminuir la cantidad 
        // de la ficha extraída o eliminarla si es la última, y devolver una tupla con 
        // la ficha y la cantidad restante.
        
        // Caso 1: Sacar una ficha que existe y hay varias
        Tuple<String, Integer> resultado = jugador.sacarFicha("A");
        assertEquals("La ficha extraída debe ser 'A'", "A", resultado.x);
        assertEquals("Deben quedar 2 fichas 'A'", Integer.valueOf(2), resultado.y);
        assertEquals("El rack debe actualizarse a 2 fichas 'A'", Integer.valueOf(2), jugador.getRack().get("A"));
        
        // Caso 2: Sacar una ficha que existe y es la última
        resultado = jugador.sacarFicha("C");
        assertEquals("La ficha extraída debe ser 'C'", "C", resultado.x);
        assertEquals("No deben quedar fichas 'C'", Integer.valueOf(0), resultado.y);
        assertFalse("La ficha 'C' debe eliminarse del rack", jugador.getRack().containsKey("C"));
        
        // Caso 3: Sacar una ficha que no existe
        resultado = jugador.sacarFicha("Z");
        assertNull("El resultado debe ser null al sacar una ficha inexistente", resultado);
        
        // Post:  El rack ha sido modificado correctamente. Ahora contiene A(2), B(2)
        // y la ficha C ha sido eliminada. Si se intenta sacar una ficha inexistente, se 
        // devuelve null y el rack queda sin cambios.
    }
    
    /**
     * Prueba el método agregarFicha en diferentes escenarios.
     */
    @Test
    public void testAgregarFicha() {
        // Pre:  Existe una instancia de Jugador con un rack inicializado 
        // con fichas A(3), B(2), C(1). El método agregarFicha debe incrementar la cantidad 
        // de ficha si ya existe, o añadirla con cantidad 1 si no existe.
        
        // Caso 1: Agregar una ficha que ya existe
        jugador.agregarFicha("A");
        assertEquals("Deben haber 4 fichas 'A' tras agregar una", Integer.valueOf(4), jugador.getRack().get("A"));
        
        // Caso 2: Agregar una ficha que no existe
        jugador.agregarFicha("Z");
        assertEquals("Debe haber 1 ficha 'Z' tras agregar una", Integer.valueOf(1), jugador.getRack().get("Z"));
        
        // Post:  El rack ha sido modificado correctamente. Ahora contiene A(4), B(2), C(1)
        // y se ha añadido Z(1). La cantidad total de fichas ha aumentado en 2.
    }
    
    /**
     * Prueba el método getCantidadFichas en diferentes escenarios.
     */
    @Test
    public void testGetCantidadFichas() {
        // Pre:  Existe una instancia de Jugador con un rack inicializado 
        // con fichas A(3), B(2), C(1), para un total de 6 fichas. El método getCantidadFichas
        // debe sumar todas las cantidades de fichas en el rack.
        
        // Caso inicial: La cantidad inicial debe ser 3 + 2 + 1 = 6
        assertEquals("La cantidad inicial de fichas debe ser 6", 6, jugador.getCantidadFichas());
        
        // Tras agregar una ficha
        jugador.agregarFicha("D");
        assertEquals("La cantidad de fichas debe ser 7 tras agregar una", 7, jugador.getCantidadFichas());
        
        // Tras sacar una ficha
        jugador.sacarFicha("A");
        assertEquals("La cantidad de fichas debe ser 6 tras sacar una", 6, jugador.getCantidadFichas());
        
        // Post:  El método ha calculado correctamente el total de fichas en 
        // diferentes estados del rack. El rack ahora contiene A(2), B(2), C(1), D(1) 
        // para un total de 6 fichas.
    }
    
    /**
     * Prueba las operaciones relacionadas con skipTrack.
     */
    @Test
    public void testSkipTrackOperations() {
        // Pre:  Existe una instancia de Jugador con skipTrack=0, inicializado en setUp().
        // skipTrack es un contador que registra cuántos turnos consecutivos ha pasado un jugador.
        
        // Verificamos valor inicial
        assertEquals("El skipTrack inicial debe ser 0", 0, jugador.getSkipTrack());
        
        // Incrementamos y verificamos
        jugador.addSkipTrack();
        assertEquals("El skipTrack debe ser 1 tras incrementar", 1, jugador.getSkipTrack());
        
        // Incrementamos otra vez
        jugador.addSkipTrack();
        assertEquals("El skipTrack debe ser 2 tras incrementar nuevamente", 2, jugador.getSkipTrack());
        
        // Establecemos un valor específico
        jugador.setSkipTrack(5);
        assertEquals("El skipTrack debe ser 5 tras asignar directamente", 5, jugador.getSkipTrack());
        
        // Limpiamos
        jugador.clearSkipTrack();
        assertEquals("El skipTrack debe ser 0 tras limpiar", 0, jugador.getSkipTrack());
        
        // Post:  Los métodos relacionados con skipTrack funcionan correctamente:
        // - addSkipTrack() incrementa el contador en 1
        // - setSkipTrack(int) establece el contador al valor especificado
        // - clearSkipTrack() reinicia el contador a 0
        // - getSkipTrack() devuelve el valor actual
    }
    
    /**
     * Prueba el método esIA con diferentes implementaciones.
     */
    @Test
    public void testEsIA() {
        // Pre:  Existe una instancia de JugadorConcretoTest que implementa
        // esIA() para devolver false. El método esIA() es abstracto en Jugador y debe
        // ser implementado por las clases derivadas.
        
        // Verificamos la implementación de prueba (no IA)
        assertFalse("El jugador de prueba no debe ser IA", jugador.esIA());
        
        // Creamos una implementación alternativa (IA)
        Jugador jugadorIA = new Jugador("JugadorIA") {
            @Override
            public boolean esIA() {
                return true;
            }
        };
        
        // Verificamos la implementación alternativa
        assertTrue("El jugador alternativo debe ser IA", jugadorIA.esIA());
        
        // Post:  Se ha verificado que el método esIA() puede ser implementado
        // por clases derivadas para devolver tanto true como false, permitiendo distinguir
        // entre jugadores humanos e IA.
    }
}
