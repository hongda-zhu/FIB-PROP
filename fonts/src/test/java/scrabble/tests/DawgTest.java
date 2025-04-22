package scrabble.tests;

import org.junit.Before;
import org.junit.Test;
import scrabble.domain.models.Dawg;
import scrabble.domain.models.DawgNode;

import java.util.Set;
import static org.junit.Assert.*;

/**
 * Test unitario para la clase Dawg 
 */
public class DawgTest {

    private Dawg dawg;

    @Before
    public void setUp() {
        // Inicializar un nuevo DAWG antes de cada test
        dawg = new Dawg();
    }

    /**
     * Pre: Se ha creado una instancia de Dawg (en setUp).
     * Post: Se verifica que el constructor inicializa correctamente un nodo raíz válido
     * que no es final y no tiene aristas.
     *
     * Comprueba la correcta inicialización del grafo DAWG.
     */
    @Test
    public void testConstructor() {
        assertNotNull("El nodo raíz no debería ser null", dawg.getRoot());
        assertFalse("El nodo raíz no debería ser final inicialmente", dawg.getRoot().isFinal());
        assertTrue("El nodo raíz no debería tener aristas inicialmente", dawg.getRoot().getAllEdges().isEmpty());
    }

    /**
     * Pre: Se ha creado una instancia de Dawg y se insertan "casa", "caso", "cama".
     * Post: Se verifica que el método insert() añade correctamente las palabras al grafo
     * y que search() encuentra o rechaza palabras según corresponda.
     *
     * Comprueba la funcionalidad básica de inserción y búsqueda de palabras.
     */
    @Test
    public void testInsertYSearch() {
        dawg.insert("casa");
        dawg.insert("caso");
        dawg.insert("cama");

        assertTrue("'casa' debería encontrarse en el DAWG", dawg.search("casa"));
        assertTrue("'caso' debería encontrarse en el DAWG", dawg.search("caso"));
        assertTrue("'cama' debería encontrarse en el DAWG", dawg.search("cama"));

        assertFalse("'cosa' no debería encontrarse en el DAWG", dawg.search("cosa"));
        assertFalse("'cara' no debería encontrarse en el DAWG", dawg.search("cara"));
        assertFalse("'c' (prefijo no insertado como palabra) no debería encontrarse en el DAWG", dawg.search("c"));
        assertFalse("La cadena vacía no debería encontrarse en el DAWG", dawg.search(""));
    }

    /**
     * Pre: Se ha creado una instancia de Dawg y se insertan "a", "ab", "abc".
     * Post: Se verifica que se pueden insertar y encontrar prefijos como palabras válidas
     * y que prefijos no marcados como finales no se encuentran con search().
     *
     * Comprueba el comportamiento con palabras que son prefijos de otras.
     */
    @Test
    public void testInsertPrefijos() {
        dawg.insert("a");
        dawg.insert("ab");
        dawg.insert("abc");

        assertTrue("'a' debería encontrarse en el DAWG", dawg.search("a"));
        assertTrue("'ab' debería encontrarse en el DAWG", dawg.search("ab"));
        assertTrue("'abc' debería encontrarse en el DAWG", dawg.search("abc"));

        assertFalse("'abcd' no debería encontrarse en el DAWG", dawg.search("abcd"));

        // Reset para probar el caso donde solo se inserta la palabra larga
        dawg = new Dawg();
        dawg.insert("abc");
        assertFalse("'ab' (prefijo no insertado como palabra) no debería encontrarse", dawg.search("ab"));
        assertFalse("'a' (prefijo no insertado como palabra) no debería encontrarse", dawg.search("a"));
    }

    /**
     * Pre: Se ha creado una instancia de Dawg y se han insertado "casa", "caso".
     * Post: Se verifica que getNode() devuelve el nodo correcto para un prefijo o palabra,
     * con el estado `isFinal` correcto, y null para palabras/prefijos no insertados.
     *
     * Comprueba la funcionalidad para acceder a nodos específicos dentro del grafo.
     */
    @Test
    public void testGetNode() {
        dawg.insert("casa");
        dawg.insert("caso");

        DawgNode nodoCas = dawg.getNode("cas");
        assertNotNull("Debería haber un nodo para el prefijo 'cas'", nodoCas);
        assertFalse("El nodo para 'cas' (prefijo) no debería ser final", nodoCas.isFinal());
        assertTrue("El nodo para 'cas' debería tener arista para 'a'", nodoCas.getAllEdges().contains("a"));
        assertTrue("El nodo para 'cas' debería tener arista para 'o'", nodoCas.getAllEdges().contains("o"));
        assertEquals("El nodo para 'cas' debería tener 2 aristas", 2, nodoCas.getAllEdges().size());

        DawgNode nodoCasa = dawg.getNode("casa");
        assertNotNull("Debería haber un nodo para 'casa'", nodoCasa);
        assertTrue("El nodo para 'casa' (palabra completa) debería ser final", nodoCasa.isFinal());

        DawgNode nodoCaso = dawg.getNode("caso");
        assertNotNull("Debería haber un nodo para 'caso'", nodoCaso);
        assertTrue("El nodo para 'caso' (palabra completa) debería ser final", nodoCaso.isFinal());

        assertNotSame("El nodo para 'cas' y 'casa' deben ser diferentes", nodoCas, nodoCasa);

        assertNull("No debería haber un nodo para 'cama' (no insertada)", dawg.getNode("cama"));
        assertNull("No debería haber un nodo para 'caos' (prefijo inválido)", dawg.getNode("caos"));
        assertNull("No debería haber un nodo para 'casas' (extensión inválida)", dawg.getNode("casas"));

        DawgNode nodoC = dawg.getNode("c");
         assertNotNull("Debería haber un nodo para el prefijo 'c'", nodoC);
         assertFalse("El nodo para 'c' (prefijo) no debería ser final", nodoC.isFinal());
         assertTrue("El nodo para 'c' debería tener arista para 'a'", nodoC.getAllEdges().contains("a"));
    }

    /**
     * Pre: Se ha creado una instancia de Dawg y se intenta insertar "".
     * Post: Se verifica que search("") es false, getNode("") devuelve el nodo raíz,
     * y la raíz no está marcada como final.
     *
     * Comprueba el comportamiento con la palabra vacía.
     */
    @Test
    public void testPalabraVacia() {
        dawg.insert(""); // La implementación debería ignorar esto

        assertFalse("La cadena vacía no debería encontrarse después de intentar insertarla", dawg.search(""));
        assertSame("getNode(\"\") debería devolver el nodo raíz", dawg.getRoot(), dawg.getNode(""));
        assertFalse("El nodo raíz no debería marcarse como final por insertar \"\"", dawg.getRoot().isFinal());
    }

    /**
     * Pre: Se ha creado una instancia de Dawg y se insertan "casa", "CASA".
     * Post: Se verifica que la estructura distingue correctamente entre mayúsculas y minúsculas.
     *
     * Comprueba la sensibilidad a mayúsculas/minúsculas en la validación de palabras.
     */
    @Test
    public void testDiferentesCasos() {
        dawg.insert("casa");
        dawg.insert("CASA");

        assertTrue("'casa' (minúsculas) debería encontrarse", dawg.search("casa"));
        assertTrue("'CASA' (mayúsculas) debería encontrarse", dawg.search("CASA"));
        assertFalse("'Casa' (mixto) no debería encontrarse", dawg.search("Casa"));
        assertFalse("'cAsa' (mixto) no debería encontrarse", dawg.search("cAsa"));

        DawgNode nodo_casa = dawg.getNode("casa");
        DawgNode nodo_CASA = dawg.getNode("CASA");
        assertNotNull("Debería existir nodo para 'casa'", nodo_casa);
        assertNotNull("Debería existir nodo para 'CASA'", nodo_CASA);
        assertNotSame("Los nodos finales para 'casa' y 'CASA' deberían ser diferentes si la estructura es case-sensitive",
                      nodo_casa, nodo_CASA);
    }

    /**
     * Pre: Se ha creado una instancia de Dawg y se insertan palabras con caracteres especiales.
     * Post: Se verifica que se pueden insertar y encontrar palabras con caracteres como acentos y ñ.
     *
     * Comprueba el soporte para caracteres no ASCII.
     */
    @Test
    public void testCaracteresEspeciales() {
        dawg.insert("café");
        dawg.insert("año");
        dawg.insert("niño");

        assertTrue("'café' debería encontrarse en el DAWG", dawg.search("café"));
        assertTrue("'año' debería encontrarse en el DAWG", dawg.search("año"));
        assertTrue("'niño' debería encontrarse en el DAWG", dawg.search("niño"));

        assertFalse("'cafe' (sin acento) no debería encontrarse", dawg.search("cafe"));
        assertFalse("'ano' (sin ñ) no debería encontrarse", dawg.search("ano"));
        assertFalse("'niña' (diferente) no debería encontrarse", dawg.search("niña"));
    }

    /**
     * Pre: Se ha creado una instancia de Dawg y se inserta una palabra muy larga.
     * Post: Se verifica que se puede encontrar la palabra larga, pero no sus prefijos (no marcados como final).
     *
     * Comprueba el comportamiento con palabras de gran longitud.
     */
    @Test
    public void testPalabrasMuyLargas() {
        String palabraLarga = "supercalifragilisticoespialidoso";
        dawg.insert(palabraLarga);

        assertTrue("La palabra larga debería encontrarse en el DAWG", dawg.search(palabraLarga));

        assertFalse("Un prefijo ('supercali') de la palabra larga no debería encontrarse como palabra",
                    dawg.search("supercali"));
        assertNotNull("Debería existir un nodo para el prefijo 'supercali'", dawg.getNode("supercali"));
        assertFalse("El nodo del prefijo 'supercali' no debería ser final", dawg.getNode("supercali").isFinal());
    }

    /**
     * Pre: Se ha creado una instancia de Dawg (y luego se inserta "a").
     * Post: Se verifica que getRoot() devuelve el nodo raíz esperado y que
     * las modificaciones en el grafo se reflejan correctamente a partir de este nodo.
     *
     * Comprueba el acceso al nodo raíz y su comportamiento después de insertar palabras.
     */
    @Test
    public void testGetRoot() {
        DawgNode raiz = dawg.getRoot();
        assertNotNull("La raíz no debería ser null", raiz);
        assertFalse("La raíz no debería ser final inicialmente", raiz.isFinal());
        assertTrue("La raíz no debería tener aristas inicialmente", raiz.getAllEdges().isEmpty());

        dawg.insert("a");

        assertFalse("La raíz no debería ser final después de insertar 'a'", raiz.isFinal());
        assertTrue("La raíz debería tener una arista para 'a'", raiz.getAllEdges().contains("a"));
        assertEquals("La raíz debería tener solo 1 arista", 1, raiz.getAllEdges().size());

        DawgNode nodoA = raiz.getEdge("a");
        assertNotNull("El nodo para 'a' no debería ser null", nodoA);
        assertTrue("El nodo para 'a' debería ser final", nodoA.isFinal());
    }

    // --- Tests Adicionales ---

    /**
     * Pre: Se ha creado una instancia de Dawg.
     * Post: Se verifica que intentar insertar null lanza NullPointerException.
     *
     * Comprueba el manejo de entrada null en insert().
     */
    @Test(expected = NullPointerException.class)
    public void testInsertNullLanzaExcepcion() {
        dawg.insert(null);
    }

     /**
     * Pre: Se ha creado una instancia de Dawg.
     * Post: Se verifica que intentar buscar null lanza NullPointerException.
     *
     * Comprueba el manejo de entrada null en search().
     */
    @Test(expected = NullPointerException.class)
    public void testSearchNullLanzaExcepcion() {
        dawg.search(null);
    }

    /**
     * Pre: Se ha creado una instancia de Dawg.
     * Post: Se verifica que intentar obtener nodo para null lanza NullPointerException.
     *
     * Comprueba el manejo de entrada null en getNode().
     */
    @Test(expected = NullPointerException.class)
    public void testGetNodeNullLanzaExcepcion() {
        dawg.getNode(null);
    }

    /**
     * Pre: Se ha creado una instancia de Dawg y se inserta una palabra dos veces.
     * Post: Se verifica que insertar la misma palabra de nuevo no causa errores
     * y el estado final (nodo final) es el mismo objeto.
     *
     * Comprueba la idempotencia de la operación de inserción.
     */
    @Test
    public void testInsertPalabraRepetida() {
        String palabra = "test";
        dawg.insert(palabra);

        assertTrue("La palabra debería encontrarse tras la primera inserción", dawg.search(palabra));
        DawgNode nodoAntes = dawg.getNode(palabra);
        assertNotNull("El nodo debería existir tras la primera inserción", nodoAntes);
        assertTrue("El nodo debería ser final tras la primera inserción", nodoAntes.isFinal());

        dawg.insert(palabra); // Insertar de nuevo

        assertTrue("La palabra debería encontrarse tras la segunda inserción", dawg.search(palabra));
        DawgNode nodoDespues = dawg.getNode(palabra);
        assertNotNull("El nodo debería existir tras la segunda inserción", nodoDespues);
        assertTrue("El nodo debería ser final tras la segunda inserción", nodoDespues.isFinal());

        assertSame("El nodo final debería ser la misma instancia después de re-insertar",
                   nodoAntes, nodoDespues);
    }

    /**
     * Pre: Se ha creado una instancia de Dawg y se han insertado varias palabras.
     * Post: Se verifica que getAvailableEdges() devuelve correctamente el conjunto
     * de aristas disponibles desde el nodo correspondiente a una palabra parcial.
     *
     * Comprueba la funcionalidad para obtener las aristas posibles desde un nodo.
     * Aporta validación para navegación eficiente en el grafo de palabras.
     */
    @Test
    public void testGetAvailableEdges() {
        // Insertar palabras de prueba
        dawg.insert("casa");
        dawg.insert("caso");
        dawg.insert("cama");
        dawg.insert("sol");

        // Aristas desde la raíz
        Set<String> edgesFromRoot = dawg.getAvailableEdges("");
        assertNotNull("Las aristas desde la raíz no deberían ser null", edgesFromRoot);
        assertEquals("Debería haber 2 aristas desde la raíz", 2, edgesFromRoot.size());
        assertTrue("Debería existir la arista 'c' desde la raíz", edgesFromRoot.contains("c"));
        assertTrue("Debería existir la arista 's' desde la raíz", edgesFromRoot.contains("s"));

        // Aristas desde "c"
        Set<String> edgesFromC = dawg.getAvailableEdges("c");
        assertNotNull("Las aristas desde 'c' no deberían ser null", edgesFromC);
        assertEquals("Debería haber 1 arista desde 'c'", 1, edgesFromC.size());
        assertTrue("Debería existir la arista 'a' desde 'c'", edgesFromC.contains("a"));

        // Aristas desde "ca"
        Set<String> edgesFromCa = dawg.getAvailableEdges("ca");
        assertNotNull("Las aristas desde 'ca' no deberían ser null", edgesFromCa);
        assertEquals("Debería haber 2 aristas desde 'ca'", 2, edgesFromCa.size());
        assertTrue("Debería existir la arista 's' desde 'ca'", edgesFromCa.contains("s"));
        assertTrue("Debería existir la arista 'm' desde 'ca'", edgesFromCa.contains("m"));

        // Aristas desde "cas"
        Set<String> edgesFromCas = dawg.getAvailableEdges("cas");
        assertNotNull("Las aristas desde 'cas' no deberían ser null", edgesFromCas);
        assertEquals("Debería haber 2 aristas desde 'cas'", 2, edgesFromCas.size());
        assertTrue("Debería existir la arista 'a' desde 'cas'", edgesFromCas.contains("a"));
        assertTrue("Debería existir la arista 'o' desde 'cas'", edgesFromCas.contains("o"));

        // Aristas desde una palabra completa
        Set<String> edgesFromCasa = dawg.getAvailableEdges("casa");
        assertNotNull("Las aristas desde 'casa' no deberían ser null", edgesFromCasa);
        assertTrue("No debería haber aristas desde 'casa'", edgesFromCasa.isEmpty());

        // Aristas desde un nodo que no existe
        Set<String> edgesFromInexistent = dawg.getAvailableEdges("xyz");
        assertNull("Las aristas desde un nodo inexistente deberían ser null", edgesFromInexistent);
    }

    /**
     * Pre: Se ha creado una instancia de Dawg y se han insertado varias palabras.
     * Post: Se verifica que isFinal() determina correctamente si una palabra parcial
     * representa un nodo final en el DAWG (una palabra completa).
     *
     * Comprueba la funcionalidad para determinar si un nodo es final (palabra completa).
     * Aporta validación para consultas eficientes sobre finalidad de palabras.
     */
    @Test
    public void testIsFinal() {
        // Insertar palabras de prueba
        dawg.insert("casa");
        dawg.insert("caso");
        dawg.insert("cama");
        dawg.insert("sol");
        dawg.insert("s"); // Palabra de un solo carácter

        // Palabras completas (deben ser nodos finales)
        assertTrue("'casa' debería ser un nodo final", dawg.isFinal("casa"));
        assertTrue("'caso' debería ser un nodo final", dawg.isFinal("caso"));
        assertTrue("'cama' debería ser un nodo final", dawg.isFinal("cama"));
        assertTrue("'sol' debería ser un nodo final", dawg.isFinal("sol"));
        assertTrue("'s' debería ser un nodo final", dawg.isFinal("s"));

        // Prefijos (no deben ser nodos finales)
        assertFalse("'c' no debería ser un nodo final", dawg.isFinal("c"));
        assertFalse("'ca' no debería ser un nodo final", dawg.isFinal("ca"));
        assertFalse("'cas' no debería ser un nodo final", dawg.isFinal("cas"));
        assertFalse("'so' no debería ser un nodo final", dawg.isFinal("so"));

        // Palabras que no existen (no deben ser nodos finales)
        assertFalse("'xyz' no debería ser un nodo final", dawg.isFinal("xyz"));
        assertFalse("'casas' no debería ser un nodo final", dawg.isFinal("casas"));
        assertFalse("'' (cadena vacía) no debería ser un nodo final", dawg.isFinal(""));

        // Verificar que isFinal() es consistente con search()
        assertEquals("isFinal('casa') debería ser consistente con search('casa')", 
                     dawg.search("casa"), dawg.isFinal("casa"));
        assertEquals("isFinal('xyz') debería ser consistente con search('xyz')", 
                     dawg.search("xyz"), dawg.isFinal("xyz"));
    }
}