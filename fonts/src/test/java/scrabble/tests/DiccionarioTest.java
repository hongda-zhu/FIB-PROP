package scrabble.tests;

import org.junit.Before;
import org.junit.Test;
import scrabble.domain.models.Dawg;
import scrabble.domain.models.Diccionario;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import static org.junit.Assert.*;

/**
 * Test unitario para la clase Diccionario 
 * Verifica el correcto funcionamiento de la gestión del alfabeto,
 * la bolsa de fichas, los comodines y la interacción con la estructura Dawg.
 */
public class DiccionarioTest {

    private Diccionario diccionario;
    private List<String> lineasAlphabetValido;
    private List<String> palabrasValidas;

    @Before
    public void setUp() {
        // Inicializar un nuevo diccionario antes de cada test
        diccionario = new Diccionario();

        // Datos de prueba válidos para el alfabeto
        lineasAlphabetValido = new ArrayList<>();
        lineasAlphabetValido.add("A 9 1");
        lineasAlphabetValido.add("E 12 1");
        lineasAlphabetValido.add("O 8 1");
        lineasAlphabetValido.add("S 6 1");
        lineasAlphabetValido.add("X 1 8");
        lineasAlphabetValido.add("CH 1 5"); // Ejemplo de letra compuesta
        lineasAlphabetValido.add("# 2 0"); // Comodín

        // Datos de prueba válidos para las palabras (ordenados alfabéticamente)
        palabrasValidas = new ArrayList<>();
        palabrasValidas.add("CASA");
        palabrasValidas.add("CHAOS"); // Palabra con letra compuesta
        palabrasValidas.add("ESO");
        palabrasValidas.add("SAXO");
        palabrasValidas.add("SOL");

        // Ordenar explícitamente por si el orden de inserción cambia en el futuro
        palabrasValidas.sort(String::compareTo);
    }

    /**
     * Pre: Se ha creado una instancia de Diccionario.
     * Post: Se verifica que el constructor inicializa correctamente el diccionario.
     *
     * Comprueba la correcta inicialización del diccionario.
     * Aporta validación del estado inicial del objeto para gestión de diccionarios.
     */
    @Test
    public void testConstructor() {
        assertNotNull("El diccionario no debería ser null", diccionario);
        assertNotNull("El Dawg interno no debería ser null", diccionario.getDawg());
        assertTrue("El mapa de alfabeto debería estar vacío inicialmente", diccionario.getAlphabet().isEmpty());
        assertTrue("El mapa de bolsa debería estar vacío inicialmente", diccionario.getBag().isEmpty());
        assertTrue("El conjunto de comodines debería estar vacío inicialmente", diccionario.getComodines().isEmpty());
    }

    /**
     * Pre: Se ha creado una instancia de Diccionario y existe una lista de palabras válidas.
     * Post: Se verifica que las palabras se añaden correctamente al Dawg y se pueden recuperar.
     *
     * Comprueba la funcionalidad para añadir y recuperar palabras en el Dawg.
     * Aporta validación de las operaciones fundamentales de gestión de estructura de palabras.
     */
    @Test
    public void testSetDawgYGetDawg() {

        diccionario.setDawg(palabrasValidas);
        Dawg dawg = diccionario.getDawg();
        assertNotNull("El Dawg recuperado no debería ser null", dawg);

        assertTrue("El Dawg debería contener la palabra 'CASA'", dawg.search("CASA"));
        assertTrue("El Dawg debería contener la palabra 'ESO'", dawg.search("ESO"));
        assertTrue("El Dawg debería contener la palabra 'SAXO'", dawg.search("SAXO"));
        assertTrue("El Dawg debería contener la palabra 'SOL'", dawg.search("SOL"));
        assertTrue("El Dawg debería contener la palabra 'CHAOS'", dawg.search("CHAOS"));

        assertFalse("El Dawg no debería contener la palabra 'ÁRBOL'", dawg.search("ÁRBOL"));
        assertFalse("El Dawg no debería contener la palabra 'LUNA'", dawg.search("LUNA"));
    }

    /**
     * Pre: Se ha creado una instancia de Diccionario.
     * Post: Se verifica que pasar null a setDawg causa NullPointerException.
     *
     * Comprueba el manejo de entrada nula en setDawg.
     */
    @Test(expected = NullPointerException.class)
    public void testSetDawgConNull() {
        diccionario.setDawg(null);
    }

    /**
     * Pre: Se ha creado una instancia de Diccionario y existe una lista de líneas de alfabeto válidas.
     * Post: Se verifica que las letras y puntos se añaden correctamente al alfabeto y se pueden recuperar.
     *
     * Comprueba la funcionalidad para añadir y recuperar alfabetos.
     * Aporta validación de las operaciones de gestión de alfabetos y puntuaciones.
     */
    @Test
    public void testSetAlphabetYGetAlphabet() {
        diccionario.setAlphabet(lineasAlphabetValido);
        Map<String, Integer> alphabet = diccionario.getAlphabet();
        assertNotNull("El alfabeto recuperado no debería ser null", alphabet);

        assertEquals("La letra 'A' debería valer 1 punto", Integer.valueOf(1), alphabet.get("A"));
        assertEquals("La letra 'E' debería valer 1 punto", Integer.valueOf(1), alphabet.get("E"));
        assertEquals("La letra 'O' debería valer 1 punto", Integer.valueOf(1), alphabet.get("O"));
        assertEquals("La letra 'S' debería valer 1 punto", Integer.valueOf(1), alphabet.get("S"));
        assertEquals("La letra 'X' debería valer 8 puntos", Integer.valueOf(8), alphabet.get("X"));
        assertEquals("La letra 'CH' debería valer 5 puntos", Integer.valueOf(5), alphabet.get("CH"));
        assertEquals("El comodín '#' debería valer 0 puntos", Integer.valueOf(0), alphabet.get("#"));

        assertNull("No debería existir la letra 'Z' en el alfabeto", alphabet.get("Z"));
        assertEquals("El tamaño del alfabeto debería ser 7", 7, alphabet.size());
    }

    /**
     * Pre: Se ha creado una instancia de Diccionario.
     * Post: Se verifica que pasar null a setAlphabet causa NullPointerException.
     *
     * Comprueba el manejo de entrada nula en setAlphabet.
     */
    @Test(expected = NullPointerException.class)
    public void testSetAlphabetConNull() {
        diccionario.setAlphabet(null);
    }

     /**
     * Pre: Se ha creado una instancia de Diccionario y se pasa una lista con letras duplicadas.
     * Post: Se verifica que la última definición de la letra prevalece (sobrescritura).
     *
     * Comprueba el comportamiento de setAlphabet con definiciones duplicadas.
     */
    @Test
    public void testSetAlphabetConDuplicados() {
        List<String> lineasConDuplicado = new ArrayList<>();
        lineasConDuplicado.add("A 9 1");
        lineasConDuplicado.add("B 2 3");
        lineasConDuplicado.add("A 1 10"); // Redefinición de A

        diccionario.setAlphabet(lineasConDuplicado);
        Map<String, Integer> alphabet = diccionario.getAlphabet();
        Map<String, Integer> bag = diccionario.getBag();

        assertEquals("El alfabeto debería tener 2 entradas", 2, alphabet.size());
        assertEquals("La letra 'A' debería valer 10 puntos (última definición)", Integer.valueOf(10), alphabet.get("A"));
        assertEquals("La frecuencia de 'A' debería ser 1 (última definición)", Integer.valueOf(1), bag.get("A"));
        assertEquals("La letra 'B' debería valer 3 puntos", Integer.valueOf(3), alphabet.get("B"));
        assertEquals("La frecuencia de 'B' debería ser 2", Integer.valueOf(2), bag.get("B"));
    }

    /**
     * Pre: Se ha creado una instancia de Diccionario y se ha añadido un alfabeto.
     * Post: Se verifica que la bolsa se genera correctamente con las frecuencias de cada letra.
     *
     * Comprueba la funcionalidad para obtener la bolsa de fichas.
     * Aporta validación de la correcta gestión de frecuencias de letras.
     */
    @Test
    public void testGetBag() {
        diccionario.setAlphabet(lineasAlphabetValido);
        Map<String, Integer> bag = diccionario.getBag();
        assertNotNull("La bolsa recuperada no debería ser null", bag);

        assertEquals("La frecuencia de 'A' debería ser 9", Integer.valueOf(9), bag.get("A"));
        assertEquals("La frecuencia de 'E' debería ser 12", Integer.valueOf(12), bag.get("E"));
        assertEquals("La frecuencia de 'O' debería ser 8", Integer.valueOf(8), bag.get("O"));
        assertEquals("La frecuencia de 'S' debería ser 6", Integer.valueOf(6), bag.get("S"));
        assertEquals("La frecuencia de 'X' debería ser 1", Integer.valueOf(1), bag.get("X"));
        assertEquals("La frecuencia de 'CH' debería ser 1", Integer.valueOf(1), bag.get("CH"));
        assertEquals("La frecuencia del comodín '#' debería ser 2", Integer.valueOf(2), bag.get("#"));

        assertNull("No debería existir la letra 'Z' en la bolsa", bag.get("Z"));
        assertEquals("El tamaño de la bolsa debería ser 7", 7, bag.size());
    }

    /**
     * Pre: Se ha creado una instancia de Diccionario y se ha añadido un alfabeto.
     * Post: Se verifica que se detectan correctamente los comodines.
     *
     * Comprueba la funcionalidad para identificar comodines.
     * Aporta validación de la correcta gestión de fichas especiales.
     */
    @Test
    public void testComodines() {
        diccionario.setAlphabet(lineasAlphabetValido);

        Set<String> comodines = diccionario.getComodines();
        assertNotNull("El conjunto de comodines no debería ser null", comodines);
        assertEquals("Debería haber 1 tipo de comodín", 1, comodines.size());
        assertTrue("El comodín '#' debería estar en el conjunto", comodines.contains("#"));
        assertFalse("La letra 'A' no debería ser un comodín", comodines.contains("A"));
        assertFalse("La letra 'CH' no debería ser un comodín", comodines.contains("CH"));

        assertTrue("El método esComodin debería reconocer '#' como comodín", diccionario.esComodin("#"));
        assertFalse("El método esComodin no debería reconocer 'A' como comodín", diccionario.esComodin("A"));
        assertFalse("El método esComodin no debería reconocer 'CH' como comodín", diccionario.esComodin("CH"));
    }

    /**
     * Pre: Se ha creado un Diccionario y se define un alfabeto donde un símbolo tiene 0 puntos pero no es '#'.
     * Post: Se verifica que dicho símbolo no es considerado un comodín.
     *
     * Comprueba la especificidad de la identificación de comodines.
     */
    @Test
    public void testSetAlphabetSimboloConCeroPuntosNoEsComodin() {
        List<String> lineas = new ArrayList<>();
        lineas.add("A 9 1");
        lineas.add("? 2 0"); // Símbolo '?' con 0 puntos
        lineas.add("# 1 0"); // Comodín real

        diccionario.setAlphabet(lineas);

        Set<String> comodines = diccionario.getComodines();
        assertEquals("Debería haber solo 1 tipo de comodín", 1, comodines.size());
        assertTrue("El conjunto de comodines debería contener '#'", comodines.contains("#"));
        assertFalse("El conjunto de comodines NO debería contener '?'", comodines.contains("?"));

        assertTrue("esComodin('#') debería ser true", diccionario.esComodin("#"));
        assertFalse("esComodin('?') debería ser false", diccionario.esComodin("?"));
        assertFalse("esComodin('A') debería ser false", diccionario.esComodin("A"));
        assertEquals("El alfabeto debería tener 3 entradas", 3, diccionario.getAlphabet().size());
        assertEquals("La puntuación de '?' debería ser 0", Integer.valueOf(0), diccionario.getAlphabet().get("?"));
    }

    /**
     * Pre: Se ha creado una instancia de Diccionario y se ha añadido un Dawg.
     * Post: Se verifica que se puede determinar si una palabra existe en el diccionario.
     *
     * Comprueba la funcionalidad de búsqueda de palabras, incluyendo case-insensitivity.
     * Aporta validación de la correcta integración entre Dawg y operaciones de consulta.
     */
    @Test
    public void testContienePalabra() {
        diccionario.setDawg(palabrasValidas);

        assertTrue("La palabra 'CASA' debería existir en el diccionario", diccionario.contienePalabra("CASA"));
        assertTrue("La palabra 'ESO' debería existir en el diccionario", diccionario.contienePalabra("ESO"));
        assertTrue("La palabra 'casa' (en minúsculas) debería existir (case-insensitive)", diccionario.contienePalabra("casa"));
        assertTrue("La palabra 'CHAOS' debería existir", diccionario.contienePalabra("CHAOS"));

        assertFalse("La palabra 'ÁRBOL' no debería existir en el diccionario", diccionario.contienePalabra("ÁRBOL"));
        assertFalse("La palabra vacía no debería existir en el diccionario", diccionario.contienePalabra(""));
        assertFalse("La palabra 'CAS' (prefijo) no debería existir como palabra completa", diccionario.contienePalabra("CAS"));
        assertFalse("La palabra 'LUNA' (no añadida) no debería existir", diccionario.contienePalabra("LUNA"));
    }

    /**
     * Pre: Se ha creado una instancia de Diccionario.
     * Post: Se verifica que llamar a contienePalabra con null lanza NullPointerException.
     *
     * Comprueba el manejo de entrada null en contienePalabra.
     */
    @Test(expected = NullPointerException.class)
    public void testContienePalabraConNull() {
        diccionario.contienePalabra(null);
    }

    /**
     * Pre: Se ha creado una instancia de Diccionario y se ha añadido un alfabeto.
     * Post: Se verifica que se obtienen correctamente los caracteres individuales del alfabeto, incluyendo letras compuestas.
     *
     * Comprueba la funcionalidad para obtener caracteres válidos del alfabeto, incluyendo letras compuestas.
     * Aporta validación de la correcta gestión de caracteres disponibles.
     */
    @Test
    public void testGetAlphabetChars() {
        diccionario.setAlphabet(lineasAlphabetValido);
        Set<Character> chars = diccionario.getAlphabetChars();
        assertNotNull("El conjunto de caracteres no debería ser null", chars);

        Set<Character> expectedChars = new HashSet<>();
        expectedChars.add('A'); expectedChars.add('E'); expectedChars.add('O');
        expectedChars.add('S'); expectedChars.add('X'); expectedChars.add('C');
        expectedChars.add('H'); expectedChars.add('#');

        assertEquals("El conjunto de caracteres obtenido debe ser igual al esperado", expectedChars, chars);
        assertEquals("El tamaño del conjunto de caracteres debería ser 8", 8, chars.size());
        assertFalse("No debería contener el carácter 'Z'", chars.contains('Z'));
    }

    /**
     * Pre: Se ha creado una instancia de Diccionario y se ha añadido un alfabeto.
     * Post: Se verifica que la validación de sintaxis de palabras funciona correctamente.
     *
     * Comprueba la funcionalidad para validar si palabras usan solo caracteres definidos.
     * Aporta validación de la correcta aplicación de reglas de sintaxis.
     */
    @Test
    public void testIsValidWordSyntax() {
        diccionario.setAlphabet(lineasAlphabetValido);
        Set<Character> chars = diccionario.getAlphabetChars();

        assertTrue("'CASA' debería tener sintaxis válida", diccionario.isValidWordSyntax("CASA", chars));
        assertTrue("'ESO' debería tener sintaxis válida", diccionario.isValidWordSyntax("ESO", chars));
        assertTrue("'CHAOS' debería tener sintaxis válida", diccionario.isValidWordSyntax("CHAOS", chars));

        assertFalse("'EXAMEN' no debería tener sintaxis válida (contiene M, N no definidos)",
                    diccionario.isValidWordSyntax("EXAMEN", chars));
        assertFalse("'ÁRBOL' no debería tener sintaxis válida (contiene Á, R, B, L no definidos)",
                    diccionario.isValidWordSyntax("ÁRBOL", chars));
        assertFalse("'123' no debería tener sintaxis válida (contiene números)",
                    diccionario.isValidWordSyntax("123", chars));
        assertFalse("Una palabra vacía no debería tener sintaxis válida",
                   diccionario.isValidWordSyntax("", chars));
        assertFalse("Una palabra null no debería tener sintaxis válida",
                    diccionario.isValidWordSyntax(null, chars));
    }

    /**
     * Pre: Se ha creado una instancia de Diccionario y se ha añadido un alfabeto.
     * Post: Se verifica que se obtiene correctamente el puntaje de una letra/símbolo.
     *
     * Comprueba la funcionalidad para obtener puntuaciones.
     * Aporta validación de la correcta gestión de valores de puntuación.
     */
    @Test
    public void testGetPuntaje() {
        diccionario.setAlphabet(lineasAlphabetValido);

        assertEquals("La 'A' debería valer 1 punto", 1, diccionario.getPuntaje("A"));
        assertEquals("La 'X' debería valer 8 puntos", 8, diccionario.getPuntaje("X"));
        assertEquals("La 'CH' debería valer 5 puntos", 5, diccionario.getPuntaje("CH"));
        assertEquals("El '#' (comodín) debería valer 0 puntos", 0, diccionario.getPuntaje("#"));

        assertEquals("Una letra inexistente como 'Z' debería valer 0 puntos", 0, diccionario.getPuntaje("Z"));
        assertEquals("Un string vacío debería valer 0 puntos", 0, diccionario.getPuntaje(""));
        assertEquals("Un string null debería valer 0 puntos", 0, diccionario.getPuntaje(null));
    }

    /**
     * Pre: Se ha creado una instancia de Diccionario y se ha añadido un Dawg.
     * Post: Se verifica que se detecta correctamente si existe un nodo para una palabra parcial o completa.
     *
     * Comprueba la funcionalidad para verificar existencia de nodos en el Dawg subyacente.
     * Aporta validación de la correcta implementación de estructura del Dawg.
     */
    @Test
    public void testNodeExists() {
        diccionario.setDawg(palabrasValidas); // CASA, ESO, SAXO, SOL, CHAOS

        assertTrue("Debería existir un nodo para 'CASA'", diccionario.nodeExists("CASA"));
        assertTrue("Debería existir un nodo para 'CA'", diccionario.nodeExists("CA"));
        assertTrue("Debería existir un nodo para 'S'", diccionario.nodeExists("S"));
        assertTrue("Debería existir un nodo para 'SOL'", diccionario.nodeExists("SOL"));
        assertTrue("Debería existir un nodo para 'CHAOS'", diccionario.nodeExists("CHAOS"));
        assertTrue("Debería existir un nodo para 'CH'", diccionario.nodeExists("CH"));
        assertTrue("Debería existir un nodo para \"\" (raíz)", diccionario.nodeExists("")); // Nodo raíz existe

        assertFalse("No debería existir un nodo para 'ÁRBOL'", diccionario.nodeExists("ÁRBOL"));
        assertFalse("No debería existir un nodo para 'ZZ'", diccionario.nodeExists("ZZ"));
        assertFalse("No debería existir un nodo para 'CASX'", diccionario.nodeExists("CASX"));
    }

    /**
     * Pre: Se ha creado una instancia de Diccionario y se ha intentado establecer un alfabeto inválido.
     * Post: Se verifica que se lanzan las excepciones apropiadas con mensajes descriptivos.
     *
     * Comprueba el comportamiento robusto con datos inválidos en setAlphabet.
     * Aporta validación de la robustez ante entradas incorrectas.
     */
    @Test
    public void testSetAlphabetConDatosInvalidos() {
        // Frecuencia negativa
        List<String> invalidFreq = List.of("A -1 1");
        try {
            diccionario.setAlphabet(invalidFreq);
            fail("Debería lanzar IllegalArgumentException con frecuencia negativa");
        } catch (IllegalArgumentException e) {
            assertTrue("Mensaje de error debería mencionar frecuencia: " + e.getMessage(), e.getMessage().contains("frecuencia"));
        }

        // Puntos negativos
        List<String> invalidPoints = List.of("A 1 -1");
        try {
            diccionario.setAlphabet(invalidPoints);
            fail("Debería lanzar IllegalArgumentException con puntos negativos");
        } catch (IllegalArgumentException e) {
            assertTrue("Mensaje de error debería mencionar puntos: " + e.getMessage(), e.getMessage().contains("puntos"));
        }

        // Formato incorrecto (menos partes)
        List<String> invalidFormatLess = List.of("A 1");
        try {
            diccionario.setAlphabet(invalidFormatLess);
            fail("Debería lanzar IllegalArgumentException con formato incorrecto (menos partes)");
        } catch (IllegalArgumentException e) {
            assertTrue("Mensaje de error debería mencionar formato incorrecto: " + e.getMessage(), e.getMessage().contains("formato incorrecto"));
        }

         // Formato incorrecto (más partes) - Asumiendo que causa error
        List<String> invalidFormatMore = List.of("A 1 1 extra");
        try {
            diccionario.setAlphabet(invalidFormatMore);
            fail("Debería lanzar IllegalArgumentException con formato incorrecto (más partes)");
        } catch (IllegalArgumentException e) {
            assertTrue("Mensaje de error debería mencionar formato incorrecto: " + e.getMessage(), e.getMessage().contains("formato incorrecto"));
        }


        // Valor no numérico para frecuencia
        List<String> nonNumericFreq = List.of("A uno 1");
        try {
            diccionario.setAlphabet(nonNumericFreq);
            fail("Debería lanzar IllegalArgumentException con frecuencia no numérica");
        } catch (IllegalArgumentException e) {
             assertTrue("Mensaje debería mencionar números o formato: " + e.getMessage(), e.getMessage().contains("números") || e.getMessage().contains("Formato incorrecto"));
        }

        // Valor no numérico para puntos
        List<String> nonNumericPoints = List.of("A 1 uno");
        try {
            diccionario.setAlphabet(nonNumericPoints);
            fail("Debería lanzar IllegalArgumentException con puntos no numéricos");
        } catch (IllegalArgumentException e) {
             assertTrue("Mensaje debería mencionar números o formato: " + e.getMessage(), e.getMessage().contains("números") || e.getMessage().contains("Formato incorrecto"));
        }

        // Lista vacía
        List<String> emptyList = new ArrayList<>();
        try {
            diccionario.setAlphabet(emptyList);
            fail("Debería lanzar IllegalArgumentException con lista vacía");
        } catch (IllegalArgumentException e) {
            assertTrue("Mensaje debería mencionar que el alfabeto está vacío: " + e.getMessage(), e.getMessage().contains("alfabeto no puede estar vacío"));
        }
    }

    /**
     * Pre: Se ha creado una instancia de Diccionario y se ha añadido un Dawg.
     * Post: Se verifica que se obtienen correctamente las aristas disponibles desde un nodo.
     *
     * Comprueba la funcionalidad para obtener las posibles letras que pueden seguir a una palabra parcial.
     * Aporta validación de la correcta implementación de navegación por el Dawg.
     */
    @Test
    public void testGetAvailableEdges() {
        diccionario.setDawg(palabrasValidas); // CASA, ESO, SAXO, SOL, CHAOS

        Set<String> edgesFromC = diccionario.getAvailableEdges("C");
        assertNotNull("El conjunto de aristas no debería ser null", edgesFromC);
        assertTrue("Debería existir la arista 'A' desde 'C'", edgesFromC.contains("A"));
        assertTrue("Debería existir la arista 'H' desde 'C'", edgesFromC.contains("H"));
        assertEquals("Deberían existir 2 aristas desde 'C'", 2, edgesFromC.size());

        Set<String> edgesFromS = diccionario.getAvailableEdges("S");
        assertNotNull("El conjunto de aristas no debería ser null", edgesFromS);
        assertTrue("Debería existir la arista 'A' desde 'S'", edgesFromS.contains("A"));
        assertTrue("Debería existir la arista 'O' desde 'S'", edgesFromS.contains("O"));
        assertEquals("Deberían existir 2 aristas desde 'S'", 2, edgesFromS.size());

        Set<String> edgesFromEmpty = diccionario.getAvailableEdges("");
        assertNotNull("El conjunto de aristas desde la raíz no debería ser null", edgesFromEmpty);
        assertTrue("Debería existir la arista 'C' desde la raíz", edgesFromEmpty.contains("C"));
        assertTrue("Debería existir la arista 'E' desde la raíz", edgesFromEmpty.contains("E"));
        assertTrue("Debería existir la arista 'S' desde la raíz", edgesFromEmpty.contains("S"));
        assertEquals("Deberían existir 3 aristas desde la raíz", 3, edgesFromEmpty.size());

        Set<String> edgesFromNonExistent = diccionario.getAvailableEdges("Z");
        assertNull("Debería devolver null para un nodo inexistente", edgesFromNonExistent);
    }

    /**
     * Pre: Se ha creado una instancia de Diccionario y se ha añadido un Dawg.
     * Post: Se verifica que se determina correctamente si una palabra parcial es una palabra completa en el diccionario.
     *
     * Comprueba la funcionalidad para determinar si un nodo representa una palabra completa.
     * Aporta validación de la correcta implementación de la finalidad de nodos en el Dawg.
     */
    @Test
    public void testIsFinal() {
        diccionario.setDawg(palabrasValidas); // CASA, ESO, SAXO, SOL, CHAOS

        assertTrue("'CASA' debería ser una palabra completa", diccionario.isFinal("CASA"));
        assertTrue("'ESO' debería ser una palabra completa", diccionario.isFinal("ESO"));
        assertTrue("'SAXO' debería ser una palabra completa", diccionario.isFinal("SAXO"));
        assertTrue("'SOL' debería ser una palabra completa", diccionario.isFinal("SOL"));
        assertTrue("'CHAOS' debería ser una palabra completa", diccionario.isFinal("CHAOS"));

        assertFalse("'CAS' no debería ser una palabra completa", diccionario.isFinal("CAS"));
        assertFalse("'CA' no debería ser una palabra completa", diccionario.isFinal("CA"));
        assertFalse("'S' no debería ser una palabra completa", diccionario.isFinal("S"));
        assertFalse("'' (cadena vacía) no debería ser una palabra completa", diccionario.isFinal(""));
        assertFalse("'ARBOL' no debería ser una palabra completa", diccionario.isFinal("ARBOL"));
    }

    /**
     * Pre: Se ha creado una instancia de Diccionario y se ha añadido un alfabeto.
     * Post: Se verifica que getFichas() retorna correctamente el mapa de frecuencias de las fichas.
     *
     * Comprueba que el método getFichas() funciona como alias de getBag().
     * Aporta validación de la correcta gestión de información de fichas.
     */
    @Test
    public void testGetFichas() {
        diccionario.setAlphabet(lineasAlphabetValido);
        
        Map<String, Integer> fichas = diccionario.getFichas();
        assertNotNull("El mapa de fichas no debería ser null", fichas);
        
        assertEquals("La frecuencia de 'A' debería ser 9", Integer.valueOf(9), fichas.get("A"));
        assertEquals("La frecuencia de 'E' debería ser 12", Integer.valueOf(12), fichas.get("E"));
        assertEquals("La frecuencia de 'O' debería ser 8", Integer.valueOf(8), fichas.get("O"));
        assertEquals("La frecuencia de 'S' debería ser 6", Integer.valueOf(6), fichas.get("S"));
        assertEquals("La frecuencia de 'X' debería ser 1", Integer.valueOf(1), fichas.get("X"));
        assertEquals("La frecuencia de 'CH' debería ser 1", Integer.valueOf(1), fichas.get("CH"));
        assertEquals("La frecuencia del comodín '#' debería ser 2", Integer.valueOf(2), fichas.get("#"));
        
        assertNull("No debería existir la letra 'Z' en las fichas", fichas.get("Z"));
        assertEquals("El tamaño del mapa de fichas debería ser 7", 7, fichas.size());
        
        // Comparamos con getBag() para confirmar que son el mismo objeto
        assertSame("getFichas() y getBag() deberían devolver el mismo objeto", diccionario.getBag(), fichas);
    }

    /**
     * Pre: Se ha creado una instancia de Diccionario y se ha añadido un alfabeto.
     * Post: Se verifica que esComodin lanza NullPointerException cuando se pasa null.
     *
     * Comprueba el manejo de entrada nula en esComodin.
     */
    @Test(expected = NullPointerException.class)
    public void testEsComodinConNull() {
        diccionario.setAlphabet(lineasAlphabetValido);
        diccionario.esComodin(null);
    }

    /**
     * Pre: Se ha creado una instancia de Diccionario y se ha añadido un alfabeto.
     * Post: Se verifica que getAlphabetKeys devuelve correctamente todas las claves del alfabeto,
     * preservando los tokens multicarácter.
     *
     * Comprueba la funcionalidad para obtener las claves completas del alfabeto.
     * Aporta validación de la correcta gestión de tokens multicarácter.
     */
    @Test
    public void testGetAlphabetKeys() {
        diccionario.setAlphabet(lineasAlphabetValido);
        
        Set<String> keys = diccionario.getAlphabetKeys();
        assertNotNull("El conjunto de claves no debería ser null", keys);
        
        // Verificar que contiene todas las claves exactas (incluyendo tokens multicarácter)
        assertTrue("Debería contener la clave 'A'", keys.contains("A"));
        assertTrue("Debería contener la clave 'E'", keys.contains("E"));
        assertTrue("Debería contener la clave 'O'", keys.contains("O"));
        assertTrue("Debería contener la clave 'S'", keys.contains("S"));
        assertTrue("Debería contener la clave 'X'", keys.contains("X"));
        assertTrue("Debería contener la clave 'CH' como token completo", keys.contains("CH"));
        assertTrue("Debería contener la clave '#'", keys.contains("#"));
        
        // Verificar el tamaño del conjunto
        assertEquals("El conjunto debería tener 7 claves", 7, keys.size());
        
        // Verificar que CH es un token completo, no caracteres separados
        assertFalse("No debería contener 'C' como clave separada", keys.contains("C"));
        assertFalse("No debería contener 'H' como clave separada", keys.contains("H"));
        
        // Verificar comportamiento con alfabeto vacío
        Diccionario dicVacio = new Diccionario();
        Set<String> keysVacio = dicVacio.getAlphabetKeys();
        assertNotNull("Con alfabeto vacío, el conjunto no debería ser null", keysVacio);
        assertTrue("Con alfabeto vacío, el conjunto debería estar vacío", keysVacio.isEmpty());
    }

    /**
     * Pre: Se ha creado una instancia de Diccionario.
     * Post: Se verifica que addWord funciona correctamente para diferentes casos.
     *
     * Comprueba la funcionalidad para añadir palabras al diccionario.
     * Aporta validación de la correcta gestión de adición de palabras.
     */
    @Test
    public void testAddWord() {
        // Añadir palabra nueva
        assertTrue("Añadir 'MESA' debería retornar true", diccionario.addWord("MESA"));
        assertTrue("La palabra 'MESA' debería existir ahora", diccionario.contienePalabra("MESA"));
        
        // Añadir palabra con espacios y en minúsculas (debería normalizarse)
        assertTrue("Añadir ' silla ' debería retornar true", diccionario.addWord(" silla "));
        assertTrue("La palabra 'SILLA' debería existir ahora", diccionario.contienePalabra("SILLA"));
        
        // Añadir palabra existente
        assertFalse("Añadir 'MESA' de nuevo debería retornar false", diccionario.addWord("MESA"));
        assertFalse("Añadir 'mesa' de nuevo debería retornar false", diccionario.addWord("mesa"));
        
        // Verificar que otras palabras no existen
        assertFalse("La palabra 'LIBRO' no debería existir", diccionario.contienePalabra("LIBRO"));
    }
    
    /**
     * Pre: Se ha creado una instancia de Diccionario.
     * Post: Se verifica que addWord lanza NullPointerException cuando se pasa null.
     *
     * Comprueba el manejo de entrada nula en addWord.
     */
    @Test(expected = NullPointerException.class)
    public void testAddWordNull() {
        diccionario.addWord(null);
    }
    
    /**
     * Pre: Se ha creado una instancia de Diccionario.
     * Post: Se verifica que addWord lanza IllegalArgumentException cuando se pasa una palabra vacía.
     *
     * Comprueba el manejo de entrada vacía en addWord.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddWordEmpty() {
        diccionario.addWord("");
    }
    
    /**
     * Pre: Se ha creado una instancia de Diccionario.
     * Post: Se verifica que addWord lanza IllegalArgumentException cuando se pasa una palabra
     * que es solo espacios.
     *
     * Comprueba el manejo de entrada inválida en addWord.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddWordOnlySpaces() {
        diccionario.addWord("   ");
    }

    /**
     * Pre: Se ha creado una instancia de Diccionario con algunas palabras.
     * Post: Se verifica que removeWord funciona correctamente para diferentes casos.
     *
     * Comprueba la funcionalidad para eliminar palabras del diccionario.
     * Aporta validación de la correcta gestión de eliminación de palabras.
     */
    @Test
    public void testRemoveWord() {
        // Preparar diccionario con palabras
        diccionario.addWord("CASA");
        diccionario.addWord("MESA");
        diccionario.addWord("SILLA");
        
        // Eliminar palabra existente
        assertTrue("Eliminar 'CASA' debería retornar true", diccionario.removeWord("CASA"));
        assertFalse("La palabra 'CASA' no debería existir ahora", diccionario.contienePalabra("CASA"));
        
        // Eliminar palabra en minúsculas (debería normalizarse)
        assertTrue("Eliminar 'mesa' debería retornar true", diccionario.removeWord("mesa"));
        assertFalse("La palabra 'MESA' no debería existir ahora", diccionario.contienePalabra("MESA"));
        
        // Eliminar palabra con espacios (debería normalizarse)
        assertTrue("Eliminar ' SILLA ' debería retornar true", diccionario.removeWord(" SILLA "));
        assertFalse("La palabra 'SILLA' no debería existir ahora", diccionario.contienePalabra("SILLA"));
        
        // Eliminar palabra no existente
        assertFalse("Eliminar 'LIBRO' debería retornar false", diccionario.removeWord("LIBRO"));
        
        // Verificar que se pueden añadir palabras después de eliminar todas
        assertTrue("Añadir 'NUEVA' después de eliminar todas debería funcionar", 
                 diccionario.addWord("NUEVA"));
        assertTrue("La palabra 'NUEVA' debería existir", diccionario.contienePalabra("NUEVA"));
    }
    
    /**
     * Pre: Se ha creado una instancia de Diccionario.
     * Post: Se verifica que removeWord lanza NullPointerException cuando se pasa null.
     *
     * Comprueba el manejo de entrada nula en removeWord.
     */
    @Test(expected = NullPointerException.class)
    public void testRemoveWordNull() {
        diccionario.removeWord(null);
    }
    
    /**
     * Pre: Se ha creado una instancia de Diccionario.
     * Post: Se verifica que removeWord lanza IllegalArgumentException cuando se pasa una palabra vacía.
     *
     * Comprueba el manejo de entrada vacía en removeWord.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testRemoveWordEmpty() {
        diccionario.removeWord("");
    }
    
    /**
     * Pre: Se ha creado una instancia de Diccionario.
     * Post: Se verifica que removeWord lanza IllegalArgumentException cuando se pasa una palabra
     * que es solo espacios.
     *
     * Comprueba el manejo de entrada inválida en removeWord.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testRemoveWordOnlySpaces() {
        diccionario.removeWord("   ");
    }
}