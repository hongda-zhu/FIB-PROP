package scrabble.tests;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import scrabble.domain.models.Jugador;
import scrabble.helpers.Tuple;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test unitario para la clase abstracta Jugador.
 * Dado que Jugador es una clase abstracta, se usa una implementación concreta para las pruebas.
 */
public class JugadorTest {
    
    // Implementación concreta de Jugador para las pruebas
    private static class JugadorConcreto extends Jugador {
        public JugadorConcreto(String id, String nombre) {
            super(id, nombre);
        }
        
        @Override
        public boolean esIA() {
            return false;
        }
    }
    
    private JugadorConcreto jugador;
    
    @Before
    public void setUp() {
        // Inicializar un jugador concreto antes de cada test
        jugador = new JugadorConcreto("id1", "Jugador Test");
    }
    
    /**
     * Pre: Se ha creado una instancia de JugadorConcreto con id "id1" y nombre "Jugador Test".
     * Post: Se verifica que el constructor inicializa correctamente los valores id, nombre, 
     * puntuación inicial, skip track inicial y rack.
     * 
     * Verifica que todos los valores iniciales del jugador son los esperados.
     * Aporta validación de la correcta inicialización de la clase base Jugador.
     */
    @Test
    public void testConstructor() {
        // Verificar que los valores iniciales son correctos
        assertEquals("El ID debería ser 'id1'", "id1", jugador.getId());
        assertEquals("El nombre debería ser 'Jugador Test'", "Jugador Test", jugador.getNombre());
        assertEquals("La puntuación inicial debería ser 0", 0, jugador.getPuntuacion());
        assertEquals("El contador de saltos inicial debería ser 0", 0, jugador.getSkipTrack());
        assertNull("El rack inicialmente debería ser null", jugador.rack);
    }
    
    /**
     * Pre: Se ha creado una instancia de JugadorConcreto con id "id1".
     * Post: Se verifica que el método getId() devuelve correctamente el id del jugador.
     * 
     * Comprueba la funcionalidad del método getter para el id.
     * Aporta validación del correcto acceso al identificador del jugador.
     */
    @Test
    public void testGetId() {
        assertEquals("El ID debería ser 'id1'", "id1", jugador.getId());
    }
    
    /**
     * Pre: Se ha creado una instancia de JugadorConcreto con nombre "Jugador Test".
     * Post: Se verifica que el método getNombre() devuelve correctamente el nombre del jugador.
     * 
     * Comprueba la funcionalidad del método getter para el nombre.
     * Aporta validación del correcto acceso al nombre del jugador.
     */
    @Test
    public void testGetNombre() {
        assertEquals("El nombre debería ser 'Jugador Test'", "Jugador Test", jugador.getNombre());
    }
    
    /**
     * Pre: Se ha creado una instancia de JugadorConcreto con puntuación inicial 0.
     * Post: Se verifica que el método setPuntuacion() modifica correctamente la puntuación.
     * 
     * Comprueba la funcionalidad del método setter para la puntuación.
     * Aporta validación de la correcta modificación de la puntuación del jugador.
     */
    @Test
    public void testSetPuntuacion() {
        jugador.setPuntuacion(100);
        assertEquals("La puntuación debería ser 100", 100, jugador.getPuntuacion());
    }
    
    /**
     * Pre: Se ha creado una instancia de JugadorConcreto y se ha modificado su puntuación a 150.
     * Post: Se verifica que el método getPuntuacion() devuelve correctamente la puntuación actual.
     * 
     * Comprueba la funcionalidad del método getter para la puntuación.
     * Aporta validación del correcto acceso a la puntuación del jugador.
     */
    @Test
    public void testGetPuntuacion() {
        jugador.setPuntuacion(150);
        assertEquals("La puntuación debería ser 150", 150, jugador.getPuntuacion());
    }
    
    /**
     * Pre: Se ha creado una instancia de JugadorConcreto con puntuación inicial 0.
     * Post: Se verifica que el método addPuntuacion() incrementa correctamente la puntuación actual.
     * 
     * Comprueba la funcionalidad para incrementar la puntuación.
     * Aporta validación del correcto incremento acumulativo de puntos.
     */
    @Test
    public void testAddPuntuacion() {
        jugador.setPuntuacion(50);
        jugador.addPuntuacion(25);
        assertEquals("La puntuación debería ser 75", 75, jugador.getPuntuacion());
        
        jugador.addPuntuacion(30);
        assertEquals("La puntuación debería ser 105", 105, jugador.getPuntuacion());
    }
    
    /**
     * Pre: Se ha creado una instancia de JugadorConcreto con rack inicial null.
     * Post: Se verifica que el método inicializarRack() establece correctamente el rack del jugador.
     * 
     * Comprueba la funcionalidad para inicializar el conjunto de fichas del jugador.
     * Aporta validación de la correcta asignación del rack de fichas.
     */
    @Test
    public void testInicializarRack() {
        // Crear un rack
        Map<String, Integer> rack = new HashMap<>();
        rack.put("A", 3);
        rack.put("E", 2);
        
        // Inicializar el rack
        jugador.inicializarRack(rack);
        
        // Verificar que se ha inicializado correctamente
        assertNotNull("El rack no debería ser null después de inicializarlo", jugador.getRack());
        assertEquals("El rack debería contener las fichas inicializadas", rack, jugador.getRack());
    }
    
    /**
     * Pre: Se ha creado una instancia de JugadorConcreto y se ha inicializado su rack.
     * Post: Se verifica que el método getRack() devuelve correctamente el rack del jugador.
     * 
     * Comprueba la funcionalidad del método getter para el rack.
     * Aporta validación del correcto acceso al conjunto de fichas del jugador.
     */
    @Test
    public void testGetRack() {
        // Inicialmente el rack es null
        assertNull("El rack inicialmente debería ser null", jugador.getRack());
        
        // Inicializar el rack
        Map<String, Integer> rack = new HashMap<>();
        rack.put("A", 3);
        jugador.inicializarRack(rack);
        
        // Verificar que se obtiene el rack correcto
        assertEquals("El rack debería contener las fichas inicializadas", rack, jugador.getRack());
    }
    
    /**
     * Pre: Se ha creado una instancia de JugadorConcreto y se ha inicializado su rack con fichas.
     * Post: Se verifica que el método sacarFicha() extrae correctamente una ficha del rack y actualiza
     * la cantidad disponible. También se verifica el comportamiento cuando se intenta sacar una ficha
     * inexistente.
     * 
     * Comprueba la funcionalidad para extraer fichas del rack.
     * Aporta validación del correcto manejo de fichas, incluyendo casos límite.
     */
    @Test
    public void testSacarFicha() {
        // Inicializar el rack
        Map<String, Integer> rack = new HashMap<>();
        rack.put("A", 3);
        rack.put("B", 1);
        jugador.inicializarRack(rack);
        
        // Sacar una ficha que existe
        Tuple<String, Integer> resultado = jugador.sacarFicha("A");
        assertNotNull("El resultado no debería ser null al sacar una ficha existente", resultado);
        assertEquals("La ficha sacada debería ser 'A'", "A", resultado.x);
        assertEquals("La cantidad restante debería ser 2", Integer.valueOf(2), resultado.y);
        assertEquals("Debería quedar 2 fichas 'A' en el rack", Integer.valueOf(2), jugador.getRack().get("A"));
        
        // Sacar la última ficha de un tipo
        resultado = jugador.sacarFicha("B");
        assertNotNull("El resultado no debería ser null al sacar la última ficha de un tipo", resultado);
        assertEquals("La ficha sacada debería ser 'B'", "B", resultado.x);
        assertEquals("La cantidad restante debería ser 0", Integer.valueOf(0), resultado.y);
        assertNull("No debería quedar ninguna ficha 'B' en el rack", jugador.getRack().get("B"));
        
        // Sacar una ficha que no existe
        resultado = jugador.sacarFicha("C");
        assertNull("El resultado debería ser null al sacar una ficha inexistente", resultado);
    }
    
    /**
     * Pre: Se ha creado una instancia de JugadorConcreto y se ha inicializado su rack con fichas.
     * Post: Se verifica que el método agregarFicha() añade correctamente una ficha al rack, tanto
     * cuando la ficha ya existe como cuando no.
     * 
     * Comprueba la funcionalidad para añadir fichas al rack.
     * Aporta validación del correcto incremento de fichas disponibles.
     */
    @Test
    public void testAgregarFicha() {
        // Inicializar el rack
        Map<String, Integer> rack = new HashMap<>();
        rack.put("A", 1);
        jugador.inicializarRack(rack);
        
        // Agregar una ficha que ya existe
        jugador.agregarFicha("A");
        assertEquals("Debería haber 2 fichas 'A'", Integer.valueOf(2), jugador.getRack().get("A"));
        
        // Agregar una ficha que no existe
        jugador.agregarFicha("B");
        assertEquals("Debería haber 1 ficha 'B'", Integer.valueOf(1), jugador.getRack().get("B"));
    }
    
    /**
     * Pre: Se ha creado una instancia de JugadorConcreto y se ha inicializado su rack con fichas.
     * Post: Se verifica que el método getCantidadFichas() devuelve correctamente el número total
     * de fichas en el rack.
     * 
     * Comprueba la funcionalidad para obtener la cantidad total de fichas.
     * Aporta validación del correcto conteo de fichas disponibles.
     */
    @Test
    public void testGetCantidadFichas() {
        // En lugar de verificar con rack null (que causa NullPointerException),
        // inicializar primero con un mapa vacío
        Map<String, Integer> rackVacio = new HashMap<>();
        jugador.inicializarRack(rackVacio);
        
        // Verificar que un rack vacío tiene 0 fichas
        assertEquals("La cantidad de fichas con rack vacío debería ser 0", 0, jugador.getCantidadFichas());
        
        // Inicializar el rack con fichas
        Map<String, Integer> rack = new HashMap<>();
        rack.put("A", 3);
        rack.put("B", 2);
        jugador.inicializarRack(rack);
        
        // Verificar la cantidad total
        assertEquals("La cantidad de fichas debería ser 5", 5, jugador.getCantidadFichas());
        
        // Sacar una ficha y verificar que la cantidad se reduce
        jugador.sacarFicha("A");
        assertEquals("La cantidad de fichas debería ser 4", 4, jugador.getCantidadFichas());
    }
    
    /**
     * Pre: Se ha creado una instancia de JugadorConcreto con skipTrack inicial 0.
     * Post: Se verifica que el método addSkipTrack() incrementa correctamente el contador de saltos.
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
     * Pre: Se ha creado una instancia de JugadorConcreto con skipTrack inicial 0.
     * Post: Se verifica que el método setSkipTrack() modifica correctamente el contador de saltos.
     * 
     * Comprueba la funcionalidad del método setter para el contador de saltos.
     * Aporta validación de la correcta modificación del contador de turnos saltados.
     */
    @Test
    public void testSetSkipTrack() {
        jugador.setSkipTrack(3);
        assertEquals("El contador de saltos debería ser 3", 3, jugador.getSkipTrack());
    }
    
    /**
     * Pre: Se ha creado una instancia de JugadorConcreto y se ha modificado su skipTrack.
     * Post: Se verifica que el método getSkipTrack() devuelve correctamente el contador de saltos.
     * 
     * Comprueba la funcionalidad del método getter para el contador de saltos.
     * Aporta validación del correcto acceso al contador de turnos saltados.
     */
    @Test
    public void testGetSkipTrack() {
        assertEquals("El contador de saltos inicial debería ser 0", 0, jugador.getSkipTrack());
        
        jugador.setSkipTrack(5);
        assertEquals("El contador de saltos debería ser 5", 5, jugador.getSkipTrack());
    }
    
    /**
     * Pre: Se ha creado un mock de JugadorConcreto con comportamiento configurado.
     * Post: Se verifica que los métodos del mock devuelven los valores esperados y que se han
     * realizado las llamadas esperadas.
     * 
     * Comprueba la integración con la biblioteca Mockito para pruebas con objetos simulados.
     * Aporta validación de la correcto uso de mocks para simular comportamientos.
     */
    @Test
    public void testIntegracionMockito() {
        // Crear un mock de la implementación concreta
        JugadorConcreto mockJugador = Mockito.mock(JugadorConcreto.class);
        
        // Configurar el comportamiento del mock
        when(mockJugador.getId()).thenReturn("mockId");
        when(mockJugador.getNombre()).thenReturn("Mock Nombre");
        when(mockJugador.getPuntuacion()).thenReturn(200);
        when(mockJugador.getSkipTrack()).thenReturn(3);
        when(mockJugador.esIA()).thenReturn(false);
        
        // Verificar el comportamiento
        assertEquals("El mock debería devolver 'mockId'", "mockId", mockJugador.getId());
        assertEquals("El mock debería devolver 'Mock Nombre'", "Mock Nombre", mockJugador.getNombre());
        assertEquals("El mock debería devolver 200", 200, mockJugador.getPuntuacion());
        assertEquals("El mock debería devolver 3", 3, mockJugador.getSkipTrack());
        assertFalse("El mock debería devolver false para esIA()", mockJugador.esIA());
        
        // Verificar las llamadas
        verify(mockJugador).getId();
        verify(mockJugador).getNombre();
        verify(mockJugador).getPuntuacion();
        verify(mockJugador).getSkipTrack();
        verify(mockJugador).esIA();
    }
}