package scrabble.tests;

import org.junit.Before;
import org.junit.Test;

import scrabble.domain.controllers.subcontrollers.ControladorJuego.Direction;
import scrabble.domain.models.Tablero;
import scrabble.domain.models.Tablero.Bonus;
import scrabble.helpers.Tuple;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test unitario para la clase Tablero
 */
public class TableroTest {
    
    private Tablero tablero;
    
    @Before
    public void setUp() {
        // Inicializar un nuevo tablero de tamaño estándar (15x15) antes de cada test
        tablero = new Tablero();
    }
    
    /**
     * Pre: Se ha creado una instancia de Tablero con el constructor por defecto.
     * Post: Se verifica que el tablero se crea con el tamaño correcto (15x15), que la casilla central
     * tiene las coordenadas correctas y que todas las casillas están inicialmente vacías.
     * 
     * Comprueba la correcta inicialización del tablero con valores por defecto.
     * Aporta validación de la creación básica del tablero de juego.
     */
    @Test
    public void testConstructorPorDefecto() {
        // Verificar que el tablero se crea con el tamaño correcto
        assertEquals("El tamaño del tablero por defecto debería ser 15", 15, tablero.getSize());
        
        // Verificar que la casilla central es especial
        Tuple<Integer, Integer> centro = tablero.getCenter();
        assertEquals("La coordenada X del centro debería ser 7", 7, centro.x.intValue());
        assertEquals("La coordenada Y del centro debería ser 7", 7, centro.y.intValue());
        
        // Verificar que el tablero está vacío (todas las casillas tienen " ")
        for (int i = 0; i < tablero.getSize(); i++) {
            for (int j = 0; j < tablero.getSize(); j++) {
                assertEquals("La casilla (" + i + "," + j + ") debería estar vacía", 
                           " ", tablero.getTile(new Tuple<>(i, j)));
            }
        }
    }
    
    /**
     * Pre: Se ha creado una instancia de Tablero con un tamaño específico (10x10).
     * Post: Se verifica que el tablero se crea con el tamaño correcto, que la casilla central
     * tiene las coordenadas correctas y que todas las casillas están inicialmente vacías.
     * 
     * Comprueba la correcta inicialización del tablero con un tamaño personalizado.
     * Aporta validación de la capacidad de crear tableros de diferentes dimensiones.
     */
    @Test
    public void testConstructorConTamaño() {
        // Crear un tablero de 10x10
        Tablero tablero10 = new Tablero(10);
        assertEquals("El tamaño del tablero debería ser 10", 10, tablero10.getSize());
        
        // Verificar que la casilla central es (5,5)
        Tuple<Integer, Integer> centro = tablero10.getCenter();
        assertEquals("La coordenada X del centro debería ser 5", 5, centro.x.intValue());
        assertEquals("La coordenada Y del centro debería ser 5", 5, centro.y.intValue());
        
        // Verificar que el tablero está vacío
        for (int i = 0; i < tablero10.getSize(); i++) {
            for (int j = 0; j < tablero10.getSize(); j++) {
                assertEquals("La casilla (" + i + "," + j + ") debería estar vacía", 
                           " ", tablero10.getTile(new Tuple<>(i, j)));
            }
        }
    }
    
    /**
     * Pre: Se ha creado una instancia de Tablero y se han colocado algunas letras en él.
     * Post: Se verifica que el constructor de copia crea un nuevo tablero con el mismo tamaño
     * y contenido que el original, y que las modificaciones en la copia no afectan al original.
     * 
     * Comprueba la correcta creación de una copia independiente del tablero.
     * Aporta validación de la separación de estado entre el original y la copia.
     */
    @Test
    public void testConstructorDeCopia() {
        // Colocar algunas letras en el tablero original
        tablero.setTile(new Tuple<>(0, 0), "A");
        tablero.setTile(new Tuple<>(1, 1), "B");
        tablero.setTile(new Tuple<>(2, 2), "C");
        
        // Crear una copia
        Tablero copia = new Tablero(tablero);
        
        // Verificar que la copia tiene el mismo tamaño
        assertEquals("El tamaño de la copia debería ser igual al original", 
                   tablero.getSize(), copia.getSize());
        
        // Verificar que las letras se copiaron correctamente
        assertEquals("La casilla (0,0) de la copia debería tener 'A'", 
                   "A", copia.getTile(new Tuple<>(0, 0)));
        assertEquals("La casilla (1,1) de la copia debería tener 'B'", 
                   "B", copia.getTile(new Tuple<>(1, 1)));
        assertEquals("La casilla (2,2) de la copia debería tener 'C'", 
                   "C", copia.getTile(new Tuple<>(2, 2)));
        
        // Verificar que modificar la copia no afecta al original
        copia.setTile(new Tuple<>(0, 0), "X");
        assertEquals("La casilla (0,0) de la copia debería tener 'X'", 
                   "X", copia.getTile(new Tuple<>(0, 0)));
        assertEquals("La casilla (0,0) del original debería seguir teniendo 'A'", 
                   "A", tablero.getTile(new Tuple<>(0, 0)));
    }
    
    /**
     * Pre: Se ha creado una instancia de Tablero.
     * Post: Se verifica que los métodos setTile() y getTile() funcionan correctamente para
     * establecer y obtener el contenido de una casilla.
     * 
     * Comprueba la funcionalidad para modificar y acceder al contenido de casillas.
     * Aporta validación de las operaciones básicas de manipulación del tablero.
     */
    @Test
    public void testSetTileYGetTile() {
        Tuple<Integer, Integer> pos = new Tuple<>(3, 4);
        
        // Verificar que inicialmente está vacía
        assertEquals("La casilla inicialmente debería estar vacía", " ", tablero.getTile(pos));
        
        // Establecer una letra
        tablero.setTile(pos, "D");
        
        // Verificar que se ha establecido correctamente
        assertEquals("La casilla debería tener la letra 'D'", "D", tablero.getTile(pos));
        
        // Cambiar la letra
        tablero.setTile(pos, "E");
        
        // Verificar que se ha cambiado correctamente
        assertEquals("La casilla debería tener la letra 'E'", "E", tablero.getTile(pos));
    }
    
    /**
     * Pre: Se ha creado una instancia de Tablero.
     * Post: Se verifica que el método validPosition() devuelve true para coordenadas dentro
     * del tablero y false para coordenadas fuera de él.
     * 
     * Comprueba la validación de coordenadas dentro de los límites del tablero.
     * Aporta validación de la detección de posiciones válidas e inválidas.
     */
    @Test
    public void testValidPosition() {
        // Posiciones válidas
        assertTrue("La posición (0,0) debería ser válida", 
                 tablero.validPosition(new Tuple<>(0, 0)));
        assertTrue("La posición (7,7) debería ser válida", 
                 tablero.validPosition(new Tuple<>(7, 7)));
        assertTrue("La posición (14,14) debería ser válida", 
                 tablero.validPosition(new Tuple<>(14, 14)));
        
        // Posiciones inválidas (fuera del tablero)
        assertFalse("La posición (-1,0) no debería ser válida", 
                  tablero.validPosition(new Tuple<>(-1, 0)));
        assertFalse("La posición (0,-1) no debería ser válida", 
                  tablero.validPosition(new Tuple<>(0, -1)));
        assertFalse("La posición (15,0) no debería ser válida", 
                  tablero.validPosition(new Tuple<>(15, 0)));
        assertFalse("La posición (0,15) no debería ser válida", 
                  tablero.validPosition(new Tuple<>(0, 15)));
    }
    
    /**
     * Pre: Se ha creado una instancia de Tablero.
     * Post: Se verifica que los métodos isEmpty() e isFilled() devuelven correctamente
     * el estado de una casilla, incluso para posiciones inválidas.
     * 
     * Comprueba la funcionalidad para determinar si una casilla está vacía o no.
     * Aporta validación de la correcta detección del estado de las casillas.
     */
    @Test
    public void testIsEmptyYIsFilled() {
        Tuple<Integer, Integer> pos = new Tuple<>(5, 5);
        
        // Inicialmente la casilla está vacía
        assertTrue("La casilla debería estar vacía inicialmente", tablero.isEmpty(pos));
        assertFalse("La casilla no debería estar llena inicialmente", tablero.isFilled(pos));
        
        // Después de colocar una letra, la casilla no está vacía
        tablero.setTile(pos, "F");
        assertFalse("La casilla no debería estar vacía después de colocar una letra", 
                  tablero.isEmpty(pos));
        assertTrue("La casilla debería estar llena después de colocar una letra", 
                 tablero.isFilled(pos));
        
        // Verificar comportamiento con posiciones inválidas
        Tuple<Integer, Integer> posInvalida = new Tuple<>(-1, -1);
        assertFalse("Una posición inválida no debería considerarse vacía", 
                  tablero.isEmpty(posInvalida));
        assertFalse("Una posición inválida no debería considerarse llena", 
                  tablero.isFilled(posInvalida));
    }
    
    /**
     * Pre: Se ha creado una instancia de Tablero.
     * Post: Se verifica que el método getBonus() devuelve correctamente el tipo de bonus
     * para diferentes casillas del tablero, incluyendo posiciones inválidas.
     * 
     * Comprueba la funcionalidad para obtener el tipo de bonus de una casilla.
     * Aporta validación de la correcta asignación de bonificaciones en el tablero.
     */
    @Test
    public void testGetBonus() {
        // Verificar algunos bonus conocidos en el tablero 15x15
        assertEquals("La casilla (0,0) debería tener bonus TW", 
                   Bonus.TW, tablero.getBonus(new Tuple<>(0, 0)));
        assertEquals("La casilla (7,7) debería tener bonus N", 
                   Bonus.N, tablero.getBonus(new Tuple<>(7, 7)));
        assertEquals("La casilla (5,1) debería tener bonus TL", 
                   Bonus.TL, tablero.getBonus(new Tuple<>(5, 1)));
        assertEquals("La casilla (1,1) debería tener bonus DW", 
                   Bonus.DW, tablero.getBonus(new Tuple<>(1, 1)));
        assertEquals("La casilla (3,0) debería tener bonus DL", 
                   Bonus.DL, tablero.getBonus(new Tuple<>(3, 0)));
        
        // Verificar comportamiento con posiciones inválidas
        assertNull("Una posición inválida debería devolver null para getBonus", 
                 tablero.getBonus(new Tuple<>(-1, -1)));
    }
    
    /**
     * Pre: Se ha creado una instancia de Tablero.
     * Post: Se verifica que el método getPointValue() devuelve correctamente el valor en
     * puntos de distintas letras, sin distinguir mayúsculas y minúsculas.
     * 
     * Comprueba la funcionalidad para obtener el valor en puntos de las letras.
     * Aporta validación del correcto sistema de puntuación de las fichas.
     */
    @Test
    public void testGetPointValue() {
        // Verificar que el valor de algunas letras es correcto
        assertNotEquals("El valor de 'a' no debería ser 0", 0, tablero.getPointValue('a'));
        assertNotEquals("El valor de 'z' no debería ser 0", 0, tablero.getPointValue('z'));
        
        // El valor de una letra debería ser igual independientemente de si es mayúscula o minúscula
        assertEquals("El valor de 'a' y 'A' debería ser el mismo", 
                   tablero.getPointValue('a'), tablero.getPointValue('A'));
        
        // Verificar comportamiento con caracteres que no son letras
        assertEquals("Un carácter que no es letra debería tener valor 0", 
                   0, tablero.getPointValue('1'));
    }
    
    /**
     * Pre: Se ha creado una instancia de Tablero.
     * Post: Se verifica que el método makeMove() coloca correctamente las letras de una palabra
     * en el tablero en la dirección especificada y devuelve una puntuación positiva.
     * 
     * Comprueba la funcionalidad para realizar jugadas en el tablero.
     * Aporta validación de la correcta colocación de palabras y cálculo de puntuación.
     */
    @Test
    public void testMakeMove() {
        // Crear un movimiento simple: colocar "CASA" horizontalmente
        String palabra = "CASA";
        Tuple<Integer, Integer> posFinal = new Tuple<>(7, 10);
        Direction direccion = Direction.HORIZONTAL;
        
        // Realizar el movimiento
        int puntos = tablero.makeMove(posFinal, palabra, direccion);
        
        // Verificar que se han colocado las letras correctamente
        assertEquals("La letra 'A' debería estar en (7,10)", "A", tablero.getTile(new Tuple<>(7, 10)));
        assertEquals("La letra 'S' debería estar en (7,9)", "S", tablero.getTile(new Tuple<>(7, 9)));
        assertEquals("La letra 'A' debería estar en (7,8)", "A", tablero.getTile(new Tuple<>(7, 8)));
        assertEquals("La letra 'C' debería estar en (7,7)", "C", tablero.getTile(new Tuple<>(7, 7)));
        
        // Verificar que el método devuelve una puntuación (no es necesario verificar el valor exacto,
        // ya que depende de la implementación específica de los valores de las letras)
        assertTrue("La puntuación debería ser positiva", puntos > 0);
        
        // Verificar comportamiento con posiciones inválidas
        try {
            tablero.makeMove(new Tuple<>(-1, -1), "TEST", direccion);
            fail("Debería lanzar una excepción al intentar colocar una palabra en una posición inválida");
        } catch (IllegalArgumentException e) {
            // Comportamiento esperado
        }
    }
    
    /**
     * Pre: Se ha creado una instancia de Tablero.
     * Post: Se verifica que el método makeMove() calcula correctamente los puntos de una jugada,
     * teniendo en cuenta los bonus de las casillas.
     * 
     * Comprueba el cálculo de puntuación de jugadas teniendo en cuenta bonificaciones.
     * Aporta validación del correcto sistema de puntuación con casillas especiales.
     */
    @Test
    public void testCalculateMovePoints() {
        // Crear un movimiento simple en una casilla con bonus TW
        String palabra = "CASA";
        Tuple<Integer, Integer> posFinal = new Tuple<>(0, 3);
        Direction direccion = Direction.HORIZONTAL;
        
        // Calcular los puntos del movimiento
        int puntos = tablero.makeMove(posFinal, palabra, direccion);
        
        // La puntuación exacta dependerá de los valores específicos de las letras y la implementación,
        // pero debería ser positiva
        assertTrue("La puntuación debería ser positiva", puntos > 0);
        
        // Verificar que los bonus afectan la puntuación
        // Reiniciar el tablero
        tablero = new Tablero();
        
        // Crear otro movimiento en una casilla sin bonus especial
        palabra = "CASA";
        posFinal = new Tuple<>(7, 10);
        direccion = Direction.HORIZONTAL;
        
        int puntosSinBonus = tablero.makeMove(posFinal, palabra, direccion);
        
        // Reiniciar el tablero de nuevo
        tablero = new Tablero();
        
        // Crear el mismo movimiento en una casilla con bonus TW
        palabra = "CASA";
        posFinal = new Tuple<>(0, 3);
        direccion = Direction.HORIZONTAL;
        
        int puntosConBonus = tablero.makeMove(posFinal, palabra, direccion);
        
        // El movimiento con bonus debería dar más puntos que el movimiento sin bonus
        assertTrue("La puntuación con bonus debería ser mayor que sin bonus", 
                 puntosConBonus > puntosSinBonus);
    }
    
    /**
     * Pre: Se ha creado una instancia de Tablero y se han colocado algunas letras en él.
     * Post: Se verifica que el método toString() devuelve una representación en cadena
     * que contiene los elementos esperados y tiene el formato adecuado.
     * 
     * Comprueba la generación de una representación textual del tablero.
     * Aporta validación de la visualización correcta del estado del tablero.
     */
    @Test
    public void testToString() {
        // Colocar algunas letras en el tablero
        tablero.setTile(new Tuple<>(7, 7), "C");
        tablero.setTile(new Tuple<>(7, 8), "A");
        tablero.setTile(new Tuple<>(7, 9), "S");
        tablero.setTile(new Tuple<>(7, 10), "A");
        
        // Obtener la representación en cadena
        String representacion = tablero.toString();
        
        // Verificar que la representación contiene los elementos básicos esperados
        assertTrue("La representación debería contener la letra 'C'", 
                 representacion.contains(" C "));
        assertTrue("La representación debería contener la letra 'A'", 
                 representacion.contains(" A "));
        assertTrue("La representación debería contener la letra 'S'", 
                 representacion.contains(" S "));
        
        // Verificar que la representación tiene el formato correcto
        assertTrue("La representación debería tener el formato de una tabla", 
                 representacion.contains("---"));
        assertTrue("La representación debería tener el formato de una tabla", 
                 representacion.contains("|"));
    }
}