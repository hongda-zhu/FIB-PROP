package scrabble.tests;

import org.junit.Before;
import org.junit.Test;
import scrabble.domain.models.DawgNode;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Test unitario para la clase DawgNode 
 */
public class DawgNodeTest {

    private DawgNode node;

    @Before
    public void setUp() {
        // Inicializar un nuevo nodo antes de cada test
        node = new DawgNode();
    }

    /**
     * Pre: Se ha creado una instancia de DawgNode.
     * Post: Se verifica que un nodo recién creado no es final y no tiene aristas.
     *
     * Comprueba la correcta inicialización de un nodo con valores por defecto.
     * Aporta validación del estado inicial de los nodos en la estructura DAWG.
     */
    @Test
    public void testConstructor() {
        // Verificar que el nodo se crea con los valores por defecto
        assertFalse("Un nodo nuevo no debería ser final", node.isFinal());
        assertTrue("Un nodo nuevo no debería tener aristas", node.getAllEdges().isEmpty());
    }

    /**
     * Pre: Se ha creado una instancia de DawgNode con estado no final.
     * Post: Se verifica que los métodos setFinal() e isFinal() modifican y devuelven
     * correctamente el estado de finalidad del nodo.
     *
     * Comprueba la funcionalidad para marcar un nodo como final o no final.
     * Aporta validación de la correcta gestión del estado de finalidad en la estructura DAWG.
     */
    @Test
    public void testSetFinalYIsFinal() {
        // Inicialmente no es final
        assertFalse("Un nodo nuevo no debería ser final", node.isFinal());

        // Establecer como final
        node.setFinal(true);
        assertTrue("El nodo debería ser final después de setFinal(true)", node.isFinal());

        // Cambiar de nuevo
        node.setFinal(false);
        assertFalse("El nodo no debería ser final después de setFinal(false)", node.isFinal());
    }

     /**
     * Pre: Se ha creado una instancia de DawgNode y se ha establecido como final.
     * Post: Se verifica que añadir una arista no cambia el estado 'isFinal'.
     *
     * Comprueba que las operaciones de aristas no interfieren con el estado de finalidad.
     */
    @Test
    public void testAddEdgeNoAlteraIsFinal() {
        node.setFinal(true);
        assertTrue("El nodo debería ser final inicialmente en este test", node.isFinal());

        DawgNode destino = new DawgNode();
        node.addEdge("f", destino);

        assertTrue("Añadir una arista no debería cambiar el estado isFinal", node.isFinal());
        assertSame("La arista 'f' debería apuntar al destino correcto", destino, node.getEdge("f"));
    }

    /**
     * Pre: Se ha creado una instancia de DawgNode sin aristas.
     * Post: Se verifica que los métodos addEdge() y getEdge() añaden y devuelven
     * correctamente las aristas del nodo.
     *
     * Comprueba la funcionalidad para crear y acceder a conexiones entre nodos.
     * Aporta validación de la correcta gestión de aristas en la estructura de grafo.
     */
    @Test
    public void testAddEdgeYGetEdge() {
        // Inicialmente no hay aristas
        assertNull("No debería haber una arista para 'a' inicialmente", node.getEdge("a"));

        // Crear un nodo destino
        DawgNode destino = new DawgNode();

        // Añadir una arista
        node.addEdge("a", destino);

        // Verificar que se ha añadido correctamente
        assertSame("getEdge('a') debería devolver el nodo destino", destino, node.getEdge("a"));

        // Añadir otra arista
        DawgNode destino2 = new DawgNode();
        node.addEdge("b", destino2);

        // Verificar que ambas aristas existen
        assertSame("getEdge('a') debería devolver el primer nodo destino",
                   destino, node.getEdge("a"));
        assertSame("getEdge('b') debería devolver el segundo nodo destino",
                   destino2, node.getEdge("b"));
    }

    /**
     * Pre: Se ha creado una instancia de DawgNode y se han añadido varias aristas.
     * Post: Se verifica que el método getAllEdges() devuelve correctamente el conjunto
     * de todas las etiquetas de aristas del nodo.
     *
     * Comprueba la funcionalidad para obtener todas las conexiones salientes de un nodo.
     * Aporta validación de la correcta recuperación de las etiquetas de todas las aristas.
     */
    @Test
    public void testGetAllEdges() {
        // Inicialmente no hay aristas
        Set<String> aristas = node.getAllEdges();
        assertTrue("Un nodo nuevo no debería tener aristas", aristas.isEmpty());

        // Añadir algunas aristas
        node.addEdge("a", new DawgNode());
        node.addEdge("b", new DawgNode());
        node.addEdge("c", new DawgNode());

        // Verificar que getAllEdges devuelve todas las aristas
        aristas = node.getAllEdges();
        assertEquals("Debería haber 3 aristas", 3, aristas.size());
        // Crear un set esperado para comparar (mejor que contains individuales)
        Set<String> expectedEdges = new HashSet<>();
        expectedEdges.add("a");
        expectedEdges.add("b");
        expectedEdges.add("c");
        assertEquals("El conjunto de aristas obtenido debe ser igual al esperado", expectedEdges, aristas);
    }

    /**
     * Pre: Se ha creado una instancia de DawgNode y se ha añadido una arista.
     * Post: Se verifica que añadir una nueva arista con la misma etiqueta sobrescribe
     * la arista anterior.
     *
     * Comprueba el comportamiento al añadir aristas con etiquetas ya existentes.
     * Aporta validación de la correcta gestión de colisiones en las etiquetas de aristas.
     */
    @Test
    public void testSobreescribirArista() {
        // Añadir una arista
        DawgNode destino1 = new DawgNode();
        node.addEdge("a", destino1);

        // Verificar que se ha añadido correctamente
        assertSame("getEdge('a') debería devolver el nodo destino original", destino1, node.getEdge("a"));
        assertEquals("Debería haber 1 arista", 1, node.getAllEdges().size());


        // Sobreescribir la arista
        DawgNode destino2 = new DawgNode();
        node.addEdge("a", destino2);

        // Verificar que se ha sobreescrito correctamente
        assertSame("getEdge('a') debería devolver el nuevo nodo destino",
                   destino2, node.getEdge("a"));
        assertNotSame("getEdge('a') no debería devolver el nodo destino original después de sobrescribir",
                      destino1, node.getEdge("a"));
         assertEquals("Debería seguir habiendo 1 arista después de sobrescribir", 1, node.getAllEdges().size());
    }

    /**
     * Pre: Se ha creado una instancia de DawgNode.
     * Post: Se verifica que las aristas con letras iguales pero en diferentes casos
     * (mayúsculas/minúsculas) se tratan como aristas diferentes.
     *
     * Comprueba la sensibilidad a mayúsculas/minúsculas en las etiquetas de aristas.
     * Aporta validación del correcto tratamiento de la distinción entre caracteres.
     */
    @Test
    public void testAristasSensiblesAMayusculas() {
        // Añadir varias aristas con el mismo carácter pero en diferentes casos
        DawgNode destino1 = new DawgNode();
        DawgNode destino2 = new DawgNode();

        node.addEdge("a", destino1);
        node.addEdge("A", destino2);

        // Verificar que se consideran aristas diferentes
        assertSame("getEdge('a') debería devolver el primer nodo destino",
                   destino1, node.getEdge("a"));
        assertSame("getEdge('A') debería devolver el segundo nodo destino",
                   destino2, node.getEdge("A"));
        assertNotSame("Los nodos destino para 'a' y 'A' deberían ser diferentes",
                      node.getEdge("a"), node.getEdge("A"));
        assertEquals("Debería haber 2 aristas", 2, node.getAllEdges().size());
        assertTrue("El conjunto de aristas debería contener 'a'", node.getAllEdges().contains("a"));
        assertTrue("El conjunto de aristas debería contener 'A'", node.getAllEdges().contains("A"));
    }

    /**
     * Pre: Se ha creado una instancia de DawgNode.
     * Post: Se verifica que se pueden añadir aristas con etiquetas de longitud 1.
     *
     * Comprueba el funcionamiento con etiquetas de un solo carácter.
     * Aporta validación del correcto manejo de etiquetas simples.
     */
    @Test
    public void testAristasCadenasLongitud1() {
        // Añadir aristas con cadenas de longitud 1
        DawgNode destino = new DawgNode();

        node.addEdge("x", destino);

        // Verificar que se ha añadido correctamente
        assertSame("getEdge('x') debería devolver el nodo destino",
                   destino, node.getEdge("x"));
        assertEquals("Debería haber 1 arista", 1, node.getAllEdges().size());
        assertEquals("La única arista debería ser 'x'", Collections.singleton("x"), node.getAllEdges());
    }

    // --- Tests Adicionales para Casos Límite ---

    /**
     * Pre: Se ha creado una instancia de DawgNode.
     * Post: Se verifica que se puede añadir y recuperar una arista con etiqueta vacía "".
     */
    @Test
    public void testAddEdgeConEtiquetaVacia() {
        DawgNode destino = new DawgNode();
        node.addEdge("", destino);
        assertSame("getEdge(\"\") debería devolver el nodo destino para etiqueta vacía",
                   destino, node.getEdge(""));
        assertEquals("Debería haber 1 arista", 1, node.getAllEdges().size());
        assertTrue("El conjunto de aristas debería contener la etiqueta vacía", node.getAllEdges().contains(""));
    }

    /**
     * Pre: Se ha creado una instancia de DawgNode.
     * Post: Se verifica que se puede añadir y recuperar una arista con etiqueta multicaracter.
     */
    @Test
    public void testAddEdgeConEtiquetaMulticaracter() {
        DawgNode destino = new DawgNode();
        String etiqueta = "ab";
        node.addEdge(etiqueta, destino);
        assertSame("getEdge(\"ab\") debería devolver el nodo destino para etiqueta multicaracter",
                   destino, node.getEdge(etiqueta));
        assertEquals("Debería haber 1 arista", 1, node.getAllEdges().size());
        assertTrue("El conjunto de aristas debería contener 'ab'", node.getAllEdges().contains(etiqueta));
    }
}