package scrabble.tests;

import org.junit.Before;
import org.junit.Test;
// No se necesita Mockito
import scrabble.domain.controllers.subcontrollers.ControladorJuego.Direction;
import scrabble.domain.models.Tablero;
import scrabble.domain.models.Tablero.Bonus;
import scrabble.helpers.Tuple;

import static org.junit.Assert.*;

/**
 * Test unitario para la clase Tablero (JUnit 4).
 */
public class TableroTest {

    private Tablero tablero; // Tablero estándar 15x15

    @Before
    public void setUp() {
        // Pre: Ninguna acción específica antes de cada test.
        // Post: Se inicializa un tablero estándar de 15x15.
        tablero = new Tablero();
    }

    /**
     * Pre: Se ha creado una instancia de Tablero con el constructor por defecto (en setUp).
     * Post: Se verifica que el tablero se crea con tamaño 15x15, centro (7,7) y casillas vacías.
     *
     * Comprueba la correcta inicialización del tablero con valores por defecto.
     */
    @Test
    public void testConstructorPorDefecto() {
        assertEquals("El tamaño del tablero por defecto debería ser 15", 15, tablero.getSize());

        Tuple<Integer, Integer> centro = tablero.getCenter();
        assertEquals("La coordenada X del centro debería ser 7", 7, centro.x.intValue());
        assertEquals("La coordenada Y del centro debería ser 7", 7, centro.y.intValue());

        for (int i = 0; i < tablero.getSize(); i++) {
            for (int j = 0; j < tablero.getSize(); j++) {
                assertEquals("La casilla (" + i + "," + j + ") debería estar vacía",
                             " ", tablero.getTile(new Tuple<>(i, j)));
            }
        }
    }

    /**
     * Pre: Ninguna acción específica.
     * Post: Se crea una instancia de Tablero con tamaño 10x10 y se verifica su tamaño,
     * centro (5,5) y que las casillas están vacías.
     *
     * Comprueba la correcta inicialización del tablero con un tamaño personalizado.
     */
    @Test
    public void testConstructorConTamaño() {
        Tablero tablero10 = new Tablero(10);
        assertEquals("El tamaño del tablero debería ser 10", 10, tablero10.getSize());

        Tuple<Integer, Integer> centro = tablero10.getCenter();
        assertEquals("La coordenada X del centro debería ser 5", 5, centro.x.intValue());
        assertEquals("La coordenada Y del centro debería ser 5", 5, centro.y.intValue());

        for (int i = 0; i < tablero10.getSize(); i++) {
            for (int j = 0; j < tablero10.getSize(); j++) {
                assertEquals("La casilla (" + i + "," + j + ") debería estar vacía",
                             " ", tablero10.getTile(new Tuple<>(i, j)));
            }
        }
    }

    /**
     * Pre: Se ha creado una instancia de Tablero (en setUp) y se colocan letras.
     * Post: Se crea una copia y se verifica que tiene el mismo contenido inicial,
     * pero que modificar la copia no afecta al original.
     *
     * Comprueba la correcta creación de una copia independiente del tablero.
     */
    @Test
    public void testConstructorDeCopia() {
        tablero.setTile(new Tuple<>(0, 0), "A");
        tablero.setTile(new Tuple<>(1, 1), "B");
        tablero.setTile(new Tuple<>(2, 2), "C");

        Tablero copia = new Tablero(tablero);

        assertEquals("El tamaño de la copia debería ser igual al original", tablero.getSize(), copia.getSize());
        assertEquals("La casilla (0,0) de la copia debería tener 'A'", "A", copia.getTile(new Tuple<>(0, 0)));
        assertEquals("La casilla (1,1) de la copia debería tener 'B'", "B", copia.getTile(new Tuple<>(1, 1)));
        assertEquals("La casilla (2,2) de la copia debería tener 'C'", "C", copia.getTile(new Tuple<>(2, 2)));

        // Modificar copia y verificar original
        copia.setTile(new Tuple<>(0, 0), "X");
        assertEquals("La casilla (0,0) de la copia debería tener 'X'", "X", copia.getTile(new Tuple<>(0, 0)));
        assertEquals("La casilla (0,0) del original debería seguir teniendo 'A'", "A", tablero.getTile(new Tuple<>(0, 0))); // Verifica independencia
    }

    /**
     * Pre: Se ha creado una instancia de Tablero (en setUp).
     * Post: Se verifica que setTile() modifica el contenido de una casilla
     * y getTile() lo recupera correctamente.
     *
     * Comprueba la funcionalidad para modificar y acceder al contenido de casillas.
     */
    @Test
    public void testSetTileYGetTile() {
        Tuple<Integer, Integer> pos = new Tuple<>(3, 4);
        assertEquals("La casilla inicialmente debería estar vacía", " ", tablero.getTile(pos));

        tablero.setTile(pos, "D");
        assertEquals("La casilla debería tener la letra 'D'", "D", tablero.getTile(pos));

        tablero.setTile(pos, "E");
        assertEquals("La casilla debería tener la letra 'E'", "E", tablero.getTile(pos));
    }

    /**
     * Pre: Se ha creado una instancia de Tablero (en setUp).
     * Post: Se verifica que validPosition() devuelve true para coordenadas dentro
     * del tablero y false para coordenadas fuera.
     *
     * Comprueba la validación de coordenadas dentro de los límites del tablero.
     */
    @Test
    public void testValidPosition() {
        // Válidas
        assertTrue("La posición (0,0) debería ser válida", tablero.validPosition(new Tuple<>(0, 0)));
        assertTrue("La posición (7,7) debería ser válida", tablero.validPosition(new Tuple<>(7, 7)));
        assertTrue("La posición (14,14) debería ser válida", tablero.validPosition(new Tuple<>(14, 14))); // Límite superior

        // Inválidas
        assertFalse("La posición (-1,0) no debería ser válida", tablero.validPosition(new Tuple<>(-1, 0)));
        assertFalse("La posición (0,-1) no debería ser válida", tablero.validPosition(new Tuple<>(0, -1)));
        assertFalse("La posición (15,0) no debería ser válida (fuera por x)", tablero.validPosition(new Tuple<>(15, 0)));
        assertFalse("La posición (0,15) no debería ser válida (fuera por y)", tablero.validPosition(new Tuple<>(0, 15)));
        assertFalse("La posición (15,15) no debería ser válida", tablero.validPosition(new Tuple<>(15, 15)));
    }

    /**
     * Pre: Se ha creado una instancia de Tablero (en setUp).
     * Post: Se verifica que isEmpty() e isFilled() devuelven el estado correcto
     * de una casilla, antes y después de colocar una ficha, y para posiciones inválidas.
     *
     * Comprueba la funcionalidad para determinar si una casilla está vacía o no.
     */
    @Test
    public void testIsEmptyYIsFilled() {
        Tuple<Integer, Integer> pos = new Tuple<>(5, 5);
        assertTrue("La casilla debería estar vacía inicialmente", tablero.isEmpty(pos));
        assertFalse("La casilla no debería estar llena inicialmente", tablero.isFilled(pos));

        tablero.setTile(pos, "F");
        assertFalse("La casilla no debería estar vacía después de colocar una letra", tablero.isEmpty(pos));
        assertTrue("La casilla debería estar llena después de colocar una letra", tablero.isFilled(pos));

        // Posiciones inválidas
        Tuple<Integer, Integer> posInvalida = new Tuple<>(-1, -1);
        assertFalse("Una posición inválida no debería considerarse vacía", tablero.isEmpty(posInvalida));
        assertFalse("Una posición inválida no debería considerarse llena", tablero.isFilled(posInvalida));
    }


     /**
     * Pre: Se ha creado una instancia de Tablero de tamaño 10x10.
     * Post: Se verifica que getBonus() devuelve Bonus.N para todas las casillas válidas
     * y null para posiciones inválidas.
     *
     * Comprueba la asignación de bonus en tableros de tamaño no estándar.
     */
    @Test
    public void testGetBonusNonStandardSize() {
        Tablero tablero10 = new Tablero(10);
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                 assertEquals("Casilla ("+i+","+j+") en tablero 10x10 debería ser Bonus.N",
                              Bonus.N, tablero10.getBonus(new Tuple<>(i,j)));
            }
        }
        // Comprobar posición inválida
         assertNull("Una posición inválida (10,0) debería devolver null para getBonus",
                    tablero10.getBonus(new Tuple<>(10, 0)));
    }


    /**
     * Pre: Se ha creado una instancia de Tablero (en setUp).
     * Post: Se verifica que getPointValue() devuelve el valor correcto para letras
     * (ignorando mayúsculas/minúsculas) y 0 para no letras, según la inicialización simple.
     *
     * Comprueba la funcionalidad para obtener el valor en puntos de las letras.
     */
    @Test
    public void testGetPointValue() {
        // Depende de inicializarPuntosAlfabeto() (a=1, b=2,...)
        assertEquals("El valor de 'a' debería ser 1", 1, tablero.getPointValue('a'));
        assertEquals("El valor de 'z' debería ser 26", 26, tablero.getPointValue('z'));
        assertEquals("El valor de 'A' debería ser 1", 1, tablero.getPointValue('A')); // Ignora caso
        assertEquals("El valor de 'Z' debería ser 26", 26, tablero.getPointValue('Z')); // Ignora caso

        assertEquals("Un carácter que no es letra ('1') debería tener valor 0", 0, tablero.getPointValue('1'));
        assertEquals("Un carácter especial ('#') debería tener valor 0", 0, tablero.getPointValue('#'));
    }

    /**
     * Pre: Se ha creado una instancia de Tablero (en setUp).
     * Post: Se verifica que makeMove() coloca una palabra horizontalmente y devuelve puntos > 0.
     * Se verifica que lanzar desde posición inválida lanza IllegalArgumentException.
     *
     * Comprueba la funcionalidad básica de realizar jugadas horizontales.
     */
    @Test
    public void testMakeMoveHorizontal() {
        String palabra = "CASA";
        Tuple<Integer, Integer> posFinal = new Tuple<>(7, 10); // Coloca C en 7,7, A en 7,8, S en 7,9, A en 7,10
        Direction direccion = Direction.HORIZONTAL;

        int puntos = tablero.makeMove(posFinal, palabra, direccion);

        assertEquals("La letra 'A' debería estar en (7,10)", "A", tablero.getTile(new Tuple<>(7, 10)));
        assertEquals("La letra 'S' debería estar en (7,9)", "S", tablero.getTile(new Tuple<>(7, 9)));
        assertEquals("La letra 'A' debería estar en (7,8)", "A", tablero.getTile(new Tuple<>(7, 8)));
        assertEquals("La letra 'C' debería estar en (7,7)", "C", tablero.getTile(new Tuple<>(7, 7)));

        assertTrue("La puntuación debería ser positiva", puntos > 0);

        // Verificar excepción con posición inválida
        try {
            tablero.makeMove(new Tuple<>(-1, -1), "TEST", direccion);
            fail("Debería lanzar IllegalArgumentException al colocar en posición inválida");
        } catch (IllegalArgumentException e) {
            // Comportamiento esperado
            assertTrue("Mensaje de error debería mencionar posición inválida", e.getMessage().contains("inválida"));
        }
    }

     /**
     * Pre: Se ha creado una instancia de Tablero (en setUp).
     * Post: Se verifica que makeMove() coloca una palabra verticalmente y devuelve puntos > 0.
     *
     * Comprueba la funcionalidad básica de realizar jugadas verticales.
     */
    @Test
    public void testMakeMoveVertical() {
        String palabra = "SOL";
        Tuple<Integer, Integer> posFinal = new Tuple<>(9, 7); // Coloca S en 7,7, O en 8,7, L en 9,7
        Direction direccion = Direction.VERTICAL;

        int puntos = tablero.makeMove(posFinal, palabra, direccion);

        assertEquals("La letra 'L' debería estar en (9,7)", "L", tablero.getTile(new Tuple<>(9, 7)));
        assertEquals("La letra 'O' debería estar en (8,7)", "O", tablero.getTile(new Tuple<>(8, 7)));
        assertEquals("La letra 'S' debería estar en (7,7)", "S", tablero.getTile(new Tuple<>(7, 7)));

        assertTrue("La puntuación debería ser positiva", puntos > 0);
    }

    /**
     * Pre: Se ha creado una instancia de Tablero (en setUp).
     * Post: Se verifica que makeMove() lanza IndexOutOfBoundsException si la palabra
     * intenta colocarse fuera de los límites del tablero.
     *
     * Comprueba el manejo de límites al colocar palabras.
     */
    @Test(expected = IndexOutOfBoundsException.class)
    public void testMakeMoveOutOfBounds() {
        String palabraLarga = "PALABRAMUYLARGAQUESESALE"; // Más de 15 letras
        Tuple<Integer, Integer> posFinal = new Tuple<>(7, 14); // Última columna
        Direction direccion = Direction.HORIZONTAL; // Intenta poner letras en columnas < 0

        // Esta llamada debería intentar acceder a índices negativos al calcular la posición
        // para las primeras letras de la palabra larga.
        tablero.makeMove(posFinal, palabraLarga, direccion);
    }

     /**
     * Pre: Se ha creado una instancia de Tablero (en setUp) y se coloca una palabra.
     * Post: Se coloca otra palabra que sobreescribe parcialmente la primera y se verifica
     * que las casillas se actualizan correctamente.
     *
     * Comprueba el comportamiento de makeMove al sobreescribir casillas.
     */
    @Test
    public void testMakeMoveOverlapping() {
        // Colocar "CASA" en el centro horizontalmente
        tablero.makeMove(new Tuple<>(7, 10), "CASA", Direction.HORIZONTAL); // C(7,7) A(7,8) S(7,9) A(7,10)
        assertEquals("Antes de sobreescribir, (7,7) debería ser 'C'", "C", tablero.getTile(new Tuple<>(7, 7)));
        assertEquals("Antes de sobreescribir, (7,8) debería ser 'A'", "A", tablero.getTile(new Tuple<>(7, 8)));
        assertEquals("Antes de sobreescribir, (7,9) debería ser 'S'", "S", tablero.getTile(new Tuple<>(7, 9)));

        // Colocar "MASA" verticalmente cruzando en la 'S' (7,9)
        // M(5,9), A(6,9), S(7,9), A(8,9) -> lastPos es (8,9)
        tablero.makeMove(new Tuple<>(8, 9), "MASA", Direction.VERTICAL);

        // Verificar las letras nuevas
        assertEquals("La casilla (5,9) debería ser 'M'", "M", tablero.getTile(new Tuple<>(5, 9)));
        assertEquals("La casilla (6,9) debería ser 'A'", "A", tablero.getTile(new Tuple<>(6, 9)));
        assertEquals("La casilla (8,9) debería ser 'A'", "A", tablero.getTile(new Tuple<>(8, 9)));

        // Verificar la casilla sobreescrita ('S' sigue siendo 'S', correcto)
        assertEquals("La casilla (7,9) debería seguir siendo 'S'", "S", tablero.getTile(new Tuple<>(7, 9)));

        // Verificar que las letras originales no sobreescritas permanecen
        assertEquals("La casilla (7,7) debería seguir siendo 'C'", "C", tablero.getTile(new Tuple<>(7, 7)));
        assertEquals("La casilla (7,8) debería seguir siendo 'A'", "A", tablero.getTile(new Tuple<>(7, 8)));
        assertEquals("La casilla (7,10) debería seguir siendo 'A'", "A", tablero.getTile(new Tuple<>(7, 10)));
    }


    /**
     * Pre: Se ha creado una instancia de Tablero (en setUp).
     * Post: Se verifica que makeMove() calcula puntos diferentes (y mayores) cuando
     * una jugada utiliza casillas de bonus comparado con casillas normales.
     *
     * Comprueba el cálculo de puntuación de jugadas teniendo en cuenta bonificaciones.
     */
    @Test
    public void testCalculateMovePointsWithBonus() {
        String palabra = "CASA"; // Puntos base: C=3, A=1, S=19, A=1 => Total=24

        // Jugada 1: Sin bonus relevantes al inicio (empieza en 7,7 que es X=DL)
        tablero = new Tablero(); // Reset
        int puntos1 = tablero.makeMove(new Tuple<>(7, 10), palabra, Direction.HORIZONTAL);
        // Score: C(7,7=X)*2 + A(7,8) + S(7,9) + A(7,10) = 3*2 + 1 + 19 + 1 = 6 + 1 + 19 + 1 = 27
        // Multiplicador Palabra: 1. Total: 27 * 1 = 27

        // Jugada 2: Empieza en TW(0,0)
        tablero = new Tablero(); // Reset
        int puntos2 = tablero.makeMove(new Tuple<>(0, 3), palabra, Direction.HORIZONTAL);
        // Score Letras: C(0,0=TW) + A(0,1) + S(0,2) + A(0,3=DL)*2 = 3 + 1 + 19 + 1*2 = 3 + 1 + 19 + 2 = 25
        // Multiplicador Palabra: 3 (por TW). Total: 25 * 3 = 75

        // Jugada 3: Una letra ('S') cae en TL(5,5)
        tablero = new Tablero(); // Reset
        // Poner "MAS" horizontalmente acabando en (5,5=TL) -> M(5,3), A(5,4), S(5,5=TL)
        int puntos3 = tablero.makeMove(new Tuple<>(5, 5), "MAS", Direction.HORIZONTAL);
        // Score: M(13) + A(1) + S(19)*3 = 13 + 1 + 57 = 71
        // Multiplicador Palabra: 1. Total: 71 * 1 = 71

        assertTrue("Puntuación con TW (" + puntos2 + ") debería ser mayor que sin TW (" + puntos1 + ")",
                   puntos2 > puntos1);
        assertTrue("Puntuación con TL (" + puntos3 + ") debería ser mayor que sin bonus relevantes (" + puntos1 + ")",
                   puntos3 > puntos1); // Compara MAS con CASA, diferente longitud/letras, pero verifica que TL > N
    }

    /**
     * Pre: Se ha creado una instancia de Tablero y se han colocado letras.
     * Post: Se verifica que toString() devuelve una cadena que contiene las letras
     * colocadas y los separadores del formato tabla.
     *
     * Comprueba la generación de una representación textual del tablero.
     */
    @Test
    public void testToString() {
        tablero.setTile(new Tuple<>(7, 7), "C");
        tablero.setTile(new Tuple<>(7, 8), "A");
        tablero.setTile(new Tuple<>(7, 9), "S");
        tablero.setTile(new Tuple<>(7, 10), "A");

        String representacion = tablero.toString();

        // Verificaciones básicas con contains
        assertTrue("La representación debería contener la letra 'C'", representacion.contains(" C |"));
        assertTrue("La representación debería contener la letra 'A'", representacion.contains(" A |"));
        assertTrue("La representación debería contener la letra 'S'", representacion.contains(" S |"));
        assertTrue("La representación debería contener el formato de tabla '----'", representacion.contains("----"));
        assertTrue("La representación debería contener el formato de tabla '|'", representacion.contains("|"));
        // Verificar número de filas (N lineas de datos + N+1 lineas separadoras + 1 linea cabecera col + 1 sep cabecera = 2N+3)
        // Para N=15 -> 33 líneas -> 32 saltos de línea
        assertEquals("La representación debería tener el número correcto de saltos de línea", 32, representacion.chars().filter(ch -> ch == '\n').count());
    }
}