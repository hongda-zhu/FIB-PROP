package scrabble.tests;

import org.junit.Before;
import org.junit.Test;
// No se necesita Mockito aquí ya que probamos la clase Diccionario directamente
// import org.mockito.Mockito;
import scrabble.domain.models.Dawg;
import scrabble.domain.models.Diccionario;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;
// import static org.mockito.Mockito.*; // No necesario

/**
 * Test unitario para la clase Diccionario.
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

        // Crear datos de prueba válidos para el alfabeto
        lineasAlphabetValido = new ArrayList<>();
        lineasAlphabetValido.add("A 9 1");
        lineasAlphabetValido.add("E 12 1");
        lineasAlphabetValido.add("O 8 1");
        lineasAlphabetValido.add("S 6 1");
        lineasAlphabetValido.add("X 1 8");
        lineasAlphabetValido.add("CH 1 5"); // Ejemplo de letra compuesta
        lineasAlphabetValido.add("# 2 0"); // Comodín

        // Crear datos de prueba válidos para las palabras
        palabrasValidas = new ArrayList<>();
        palabrasValidas.add("CASA");
        palabrasValidas.add("ESO");
        palabrasValidas.add("SAXO");
        palabrasValidas.add("SOL");
        palabrasValidas.add("CHAOS"); // Palabra con letra compuesta
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

        // Verificar que el Dawg contiene las palabras añadidas
        assertTrue("El Dawg debería contener la palabra 'CASA'", dawg.search("CASA"));
        assertTrue("El Dawg debería contener la palabra 'ESO'", dawg.search("ESO"));
        assertTrue("El Dawg debería contener la palabra 'SAXO'", dawg.search("SAXO"));
        assertTrue("El Dawg debería contener la palabra 'SOL'", dawg.search("SOL"));
        assertTrue("El Dawg debería contener la palabra 'CHAOS'", dawg.search("CHAOS"));

        // Verificar que el Dawg no contiene palabras que no están en la lista
        assertFalse("El Dawg no debería contener la palabra 'ÁRBOL'", dawg.search("ÁRBOL"));
        assertFalse("El Dawg no debería contener la palabra 'LUNA'", dawg.search("LUNA")); // Palabra no añadida
    }

    /**
     * Pre: Se ha creado una instancia de Diccionario.
     * Post: Se verifica que pasar null a setDawg causa NullPointerException.
     *
     * Comprueba el manejo de entrada nula en setDawg.
     */
    @Test(expected = NullPointerException.class)
    public void testSetDawgConNull() {
        diccionario.setDawg(null); // Debería lanzar NullPointerException al intentar iterar
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

        // Verificar que el alfabeto contiene las letras y puntos correctos
        assertEquals("La letra 'A' debería valer 1 punto", Integer.valueOf(1), alphabet.get("A"));
        assertEquals("La letra 'E' debería valer 1 punto", Integer.valueOf(1), alphabet.get("E"));
        assertEquals("La letra 'O' debería valer 1 punto", Integer.valueOf(1), alphabet.get("O"));
        assertEquals("La letra 'S' debería valer 1 punto", Integer.valueOf(1), alphabet.get("S"));
        assertEquals("La letra 'X' debería valer 8 puntos", Integer.valueOf(8), alphabet.get("X"));
        assertEquals("La letra 'CH' debería valer 5 puntos", Integer.valueOf(5), alphabet.get("CH"));
        assertEquals("El comodín '#' debería valer 0 puntos", Integer.valueOf(0), alphabet.get("#"));

        // Verificar que no hay letras que no estén en la lista
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
        diccionario.setAlphabet(null); // Debería lanzar NullPointerException al intentar iterar
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

        // Verificar que la bolsa contiene las letras y frecuencias correctas
        assertEquals("La frecuencia de 'A' debería ser 9", Integer.valueOf(9), bag.get("A"));
        assertEquals("La frecuencia de 'E' debería ser 12", Integer.valueOf(12), bag.get("E"));
        assertEquals("La frecuencia de 'O' debería ser 8", Integer.valueOf(8), bag.get("O"));
        assertEquals("La frecuencia de 'S' debería ser 6", Integer.valueOf(6), bag.get("S"));
        assertEquals("La frecuencia de 'X' debería ser 1", Integer.valueOf(1), bag.get("X"));
        assertEquals("La frecuencia de 'CH' debería ser 1", Integer.valueOf(1), bag.get("CH"));
        assertEquals("La frecuencia del comodín '#' debería ser 2", Integer.valueOf(2), bag.get("#"));

        // Verificar que no hay letras que no estén en la lista
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

        // Verificar que se reconoce el comodín
        Set<String> comodines = diccionario.getComodines();
        assertNotNull("El conjunto de comodines no debería ser null", comodines);
        assertEquals("Debería haber 1 tipo de comodín", 1, comodines.size());
        assertTrue("El comodín '#' debería estar en el conjunto", comodines.contains("#"));
        assertFalse("La letra 'A' no debería ser un comodín", comodines.contains("A"));
        assertFalse("La letra 'CH' no debería ser un comodín", comodines.contains("CH"));

        // Verificar el método esComodin
        assertTrue("El método esComodin debería reconocer '#' como comodín", diccionario.esComodin("#"));
        assertFalse("El método esComodin no debería reconocer 'A' como comodín", diccionario.esComodin("A"));
        assertFalse("El método esComodin no debería reconocer 'CH' como comodín", diccionario.esComodin("CH"));
    }

    /**
     * Pre: Se ha creado una instancia de Diccionario y se ha añadido un Dawg.
     * Post: Se verifica que se puede determinar si una palabra existe en el diccionario.
     *
     * Comprueba la funcionalidad de búsqueda de palabras.
     * Aporta validación de la correcta integración entre Dawg y operaciones de consulta.
     */
    @Test
    public void testContienePalabra() {
        diccionario.setDawg(palabrasValidas);

        // Verificar palabras que existen
        assertTrue("La palabra 'CASA' debería existir en el diccionario", diccionario.contienePalabra("CASA"));
        assertTrue("La palabra 'ESO' debería existir en el diccionario", diccionario.contienePalabra("ESO"));
        assertTrue("La palabra 'casa' (en minúsculas) debería existir (case-insensitive)", diccionario.contienePalabra("casa"));
        assertTrue("La palabra 'CHAOS' debería existir", diccionario.contienePalabra("CHAOS"));

        // Verificar palabras que no existen
        assertFalse("La palabra 'ÁRBOL' no debería existir en el diccionario", diccionario.contienePalabra("ÁRBOL"));
        assertFalse("La palabra vacía no debería existir en el diccionario", diccionario.contienePalabra(""));
        assertFalse("La palabra 'CAS' (prefijo) no debería existir como palabra completa", diccionario.contienePalabra("CAS"));
        assertFalse("La palabra 'LUNA' (no añadida) no debería existir", diccionario.contienePalabra("LUNA"));
    }

    /**
     * Pre: Se ha creado una instancia de Diccionario y se ha añadido un alfabeto.
     * Post: Se verifica que se obtienen correctamente los caracteres del alfabeto.
     *
     * Comprueba la funcionalidad para obtener caracteres válidos.
     * Aporta validación de la correcta gestión de caracteres disponibles.
     */
    @Test
    public void testGetAlphabetChars() {
        diccionario.setAlphabet(lineasAlphabetValido);

        // Obtener los caracteres del alfabeto
        Set<Character> chars = diccionario.getAlphabetChars();
        assertNotNull("El conjunto de caracteres no debería ser null", chars);

        // Verificar que contiene los caracteres esperados (incluyendo los de letras compuestas)
        assertTrue("Debería contener el carácter 'A'", chars.contains('A'));
        assertTrue("Debería contener el carácter 'E'", chars.contains('E'));
        assertTrue("Debería contener el carácter 'O'", chars.contains('O'));
        assertTrue("Debería contener el carácter 'S'", chars.contains('S'));
        assertTrue("Debería contener el carácter 'X'", chars.contains('X'));
        assertTrue("Debería contener el carácter 'C' (de CH)", chars.contains('C'));
        assertTrue("Debería contener el carácter 'H' (de CH)", chars.contains('H'));
        assertTrue("Debería contener el carácter '#'", chars.contains('#'));

        assertEquals("El tamaño del conjunto de caracteres debería ser 8", 8, chars.size());

        // Verificar que no contiene caracteres que no están en el alfabeto
        assertFalse("No debería contener el carácter 'Z'", chars.contains('Z'));
    }

    /**
     * Pre: Se ha creado una instancia de Diccionario y se ha añadido un alfabeto.
     * Post: Se verifica que la validación de sintaxis de palabras funciona correctamente.
     *
     * Comprueba la funcionalidad para validar palabras.
     * Aporta validación de la correcta aplicación de reglas de sintaxis.
     */
    @Test
    public void testIsValidWordSyntax() {
        diccionario.setAlphabet(lineasAlphabetValido);

        // Obtener los caracteres del alfabeto
        Set<Character> chars = diccionario.getAlphabetChars(); // {A, E, O, S, X, C, H, #}

        // Verificar palabras válidas (usando solo chars del alfabeto)
        assertTrue("'CASA' debería tener sintaxis válida", diccionario.isValidWordSyntax("CASA", chars));
        assertTrue("'ESO' debería tener sintaxis válida", diccionario.isValidWordSyntax("ESO", chars));
        assertTrue("'CHAOS' debería tener sintaxis válida", diccionario.isValidWordSyntax("CHAOS", chars)); // Usa C, H, A, O, S

        // Verificar palabras inválidas
        assertFalse("'EXAMEN' no debería tener sintaxis válida (contiene M, N no definidos)",
                diccionario.isValidWordSyntax("EXAMEN", chars));
        assertFalse("'ÁRBOL' no debería tener sintaxis válida (contiene Á, R, B, L no definidos)",
                diccionario.isValidWordSyntax("ÁRBOL", chars));
        assertFalse("'123' no debería tener sintaxis válida (contiene números)",
                diccionario.isValidWordSyntax("123", chars));
         assertFalse("Una palabra vacía no debería tener sintaxis válida",
                 diccionario.isValidWordSyntax("", chars)); // Asumiendo que palabras vacías no son válidas
    }

    /**
     * Pre: Se ha creado una instancia de Diccionario y se ha añadido un alfabeto.
     * Post: Se verifica que se obtiene correctamente el puntaje de una letra.
     *
     * Comprueba la funcionalidad para obtener puntuaciones.
     * Aporta validación de la correcta gestión de valores de puntuación.
     */
    @Test
    public void testGetPuntaje() {
        diccionario.setAlphabet(lineasAlphabetValido);

        // Verificar puntajes correctos
        assertEquals("La 'A' debería valer 1 punto", 1, diccionario.getPuntaje("A"));
        assertEquals("La 'X' debería valer 8 puntos", 8, diccionario.getPuntaje("X"));
        assertEquals("La 'CH' debería valer 5 puntos", 5, diccionario.getPuntaje("CH"));
        assertEquals("El '#' (comodín) debería valer 0 puntos", 0, diccionario.getPuntaje("#"));

        // Verificar puntaje de letra inexistente
        assertEquals("Una letra inexistente como 'Z' debería valer 0 puntos", 0, diccionario.getPuntaje("Z"));
        assertEquals("Un string vacío debería valer 0 puntos", 0, diccionario.getPuntaje(""));
    }

    /**
     * Pre: Se ha creado una instancia de Diccionario y se ha añadido un Dawg.
     * Post: Se verifica que se obtienen correctamente los bordes disponibles para una palabra parcial.
     *
     * Comprueba la funcionalidad para explorar posibles continuaciones de palabras.
     * Aporta validación de la correcta implementación de navegación en el Dawg.
     */
    @Test
    public void testGetAvailableEdges() {
        diccionario.setDawg(palabrasValidas); // CASA, ESO, SAXO, SOL, CHAOS

        // Verificar bordes disponibles para prefijos
        Set<String> edgesCA = diccionario.getAvailableEdges("CA"); // Debería ser solo 'S' (de CASA)
        assertNotNull("El conjunto de bordes para 'CA' no debería ser null", edgesCA);
        assertEquals("Desde 'CA' solo debería haber 1 borde", 1, edgesCA.size());
        assertTrue("Desde 'CA' debería poder ir a 'S'", edgesCA.contains("S"));

        Set<String> edgesS = diccionario.getAvailableEdges("S"); // Debería ser 'A' (de SAXO) y 'O' (de SOL)
        assertNotNull("El conjunto de bordes para 'S' no debería ser null", edgesS);
        assertEquals("Desde 'S' debería haber 2 bordes", 2, edgesS.size());
        assertTrue("Desde 'S' debería poder ir a 'A'", edgesS.contains("A"));
        assertTrue("Desde 'S' debería poder ir a 'O'", edgesS.contains("O"));

        // Verificar que no hay bordes para palabras completas que no son prefijos de otras
        Set<String> edgesSOL = diccionario.getAvailableEdges("SOL");
        assertNotNull("El conjunto de bordes para 'SOL' no debería ser null", edgesSOL);
        assertTrue("Desde 'SOL' no debería haber bordes disponibles", edgesSOL.isEmpty());

        // Verificar comportamiento con prefijo inexistente
        Set<String> edgesZZ = diccionario.getAvailableEdges("ZZ");
        assertNull("El conjunto de bordes para un prefijo inexistente ('ZZ') debería ser null", edgesZZ);
    }

    /**
     * Pre: Se ha creado una instancia de Diccionario y se ha añadido un Dawg.
     * Post: Se verifica que se detecta correctamente si una palabra parcial es final.
     *
     * Comprueba la funcionalidad para identificar finales de palabras.
     * Aporta validación de la correcta implementación de estados finales en el Dawg.
     */
    @Test
    public void testIsFinal() {
        diccionario.setDawg(palabrasValidas); // CASA, ESO, SAXO, SOL, CHAOS

        // Verificar palabras completas
        assertTrue("'CASA' debería ser final", diccionario.isFinal("CASA"));
        assertTrue("'SOL' debería ser final", diccionario.isFinal("SOL"));
        assertTrue("'CHAOS' debería ser final", diccionario.isFinal("CHAOS"));

        // Verificar prefijos que no son palabras completas
        assertFalse("'CA' no debería ser final", diccionario.isFinal("CA"));
        assertFalse("'SAX' no debería ser final", diccionario.isFinal("SAX"));
        assertFalse("'CHAO' no debería ser final", diccionario.isFinal("CHAO"));

        // Verificar palabras inexistentes
        assertFalse("'ÁRBOL' no debería ser final", diccionario.isFinal("ÁRBOL"));
        assertFalse("'LUNA' no debería ser final", diccionario.isFinal("LUNA"));
    }

    /**
     * Pre: Se ha creado una instancia de Diccionario y se ha añadido un Dawg.
     * Post: Se verifica que se detecta correctamente si existe un nodo para una palabra parcial.
     *
     * Comprueba la funcionalidad para verificar existencia de nodos.
     * Aporta validación de la correcta implementación de estructura del Dawg.
     */
    @Test
    public void testNodeExists() {
        diccionario.setDawg(palabrasValidas); // CASA, ESO, SAXO, SOL, CHAOS

        // Verificar nodos existentes (prefijos y palabras completas)
        assertTrue("Debería existir un nodo para 'CASA'", diccionario.nodeExists("CASA"));
        assertTrue("Debería existir un nodo para 'CA'", diccionario.nodeExists("CA"));
        assertTrue("Debería existir un nodo para 'S'", diccionario.nodeExists("S"));
        assertTrue("Debería existir un nodo para 'SOL'", diccionario.nodeExists("SOL"));
        assertTrue("Debería existir un nodo para 'CHAOS'", diccionario.nodeExists("CHAOS"));
        assertTrue("Debería existir un nodo para 'CH'", diccionario.nodeExists("CH"));

        // Verificar nodos inexistentes
        assertFalse("No debería existir un nodo para 'ÁRBOL'", diccionario.nodeExists("ÁRBOL"));
        assertFalse("No debería existir un nodo para 'ZZ'", diccionario.nodeExists("ZZ"));
        assertFalse("No debería existir un nodo para 'CASX'", diccionario.nodeExists("CASX"));
    }

    // testGetFichas() eliminado porque es idéntico a testGetBag()

    /**
     * Pre: Se ha creado una instancia de Diccionario y se ha intentado establecer un alfabeto inválido.
     * Post: Se verifica que se lanzan las excepciones apropiadas.
     *
     * Comprueba el comportamiento con datos inválidos en setAlphabet.
     * Aporta validación de la robustez ante entradas incorrectas.
     */
    @Test
    public void testSetAlphabetConDatosInvalidos() {
        // Probar con frecuencia negativa
        List<String> lineasInvalidas1 = new ArrayList<>();
        lineasInvalidas1.add("A -1 1");

        try {
            diccionario.setAlphabet(lineasInvalidas1);
            fail("Debería lanzar IllegalArgumentException con frecuencia negativa");
        } catch (IllegalArgumentException e) {
            // Comportamiento esperado
            assertTrue("El mensaje debería mencionar la frecuencia: " + e.getMessage(),
                    e.getMessage().contains("frecuencia"));
        }

        // Probar con puntos negativos
        List<String> lineasInvalidas2 = new ArrayList<>();
        lineasInvalidas2.add("A 1 -1");

        try {
            diccionario.setAlphabet(lineasInvalidas2);
            fail("Debería lanzar IllegalArgumentException con puntos negativos");
        } catch (IllegalArgumentException e) {
            // Comportamiento esperado
            assertTrue("El mensaje debería mencionar los puntos: " + e.getMessage(),
                    e.getMessage().contains("puntos"));
        }

        // Probar con formato incorrecto (menos de 3 partes)
        List<String> lineasInvalidas3 = new ArrayList<>();
        lineasInvalidas3.add("A 1");

        try {
            diccionario.setAlphabet(lineasInvalidas3);
            fail("Debería lanzar IllegalArgumentException con formato incorrecto");
        } catch (IllegalArgumentException e) {
            // Comportamiento esperado
            assertTrue("El mensaje debería mencionar formato incorrecto: " + e.getMessage(),
                    e.getMessage().contains("formato incorrecto"));
        }

        // Probar con valores no numéricos para frecuencia/puntos
        List<String> lineasInvalidas4 = new ArrayList<>();
        lineasInvalidas4.add("A uno 1");

        try {
            diccionario.setAlphabet(lineasInvalidas4);
            fail("Debería lanzar IllegalArgumentException con valores no numéricos");
        } catch (IllegalArgumentException e) {
            // Comportamiento esperado (esperamos NumberFormatException dentro, que se relanza como IAE)
             assertTrue("El mensaje debería mencionar formato incorrecto o números: " + e.getMessage(),
                     e.getMessage().contains("números") || e.getMessage().contains("Formato incorrecto"));
        }
        
        List<String> lineasInvalidas5 = new ArrayList<>();
        lineasInvalidas5.add("A 1 uno");

        try {
            diccionario.setAlphabet(lineasInvalidas5);
            fail("Debería lanzar IllegalArgumentException con valores no numéricos");
        } catch (IllegalArgumentException e) {
            // Comportamiento esperado
             assertTrue("El mensaje debería mencionar formato incorrecto o números: " + e.getMessage(),
                     e.getMessage().contains("números") || e.getMessage().contains("Formato incorrecto"));
        }

        // Probar con lista vacía
        List<String> lineasVacias = new ArrayList<>();

        try {
            diccionario.setAlphabet(lineasVacias);
            fail("Debería lanzar IllegalArgumentException con lista vacía");
        } catch (IllegalArgumentException e) {
            // Comportamiento esperado
            assertTrue("El mensaje debería mencionar que el alfabeto está vacío: " + e.getMessage(),
                    e.getMessage().contains("alfabeto no puede estar vacío"));
        }
    }
}