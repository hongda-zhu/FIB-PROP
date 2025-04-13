package scrabble.tests;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import scrabble.domain.models.Dawg;
import scrabble.domain.models.DawgNode;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

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
     * Pre: Se ha creado una instancia de Dawg.
     * Post: Se verifica que el constructor inicializa correctamente un nodo raíz válido
     * que no es final y no tiene aristas.
     * 
     * Comprueba la correcta inicialización del grafo DAWG.
     * Aporta validación del estado inicial de la estructura de datos para validación de palabras.
     */
    @Test
    public void testConstructor() {
        // Verificar que el DAWG se crea con un nodo raíz válido
        assertNotNull("El nodo raíz no debería ser null", dawg.getRoot());
        assertFalse("El nodo raíz no debería ser final inicialmente", dawg.getRoot().isFinal());
        assertTrue("El nodo raíz no debería tener aristas inicialmente", dawg.getRoot().getAllEdges().isEmpty());
    }
    
    /**
     * Pre: Se ha creado una instancia de Dawg.
     * Post: Se verifica que el método insert() añade correctamente las palabras al grafo
     * y que search() encuentra o rechaza palabras según corresponda.
     * 
     * Comprueba la funcionalidad básica de inserción y búsqueda de palabras.
     * Aporta validación de las operaciones fundamentales del diccionario de palabras.
     */
    @Test
    public void testInsertYSearch() {
        // Insertar algunas palabras
        dawg.insert("casa");
        dawg.insert("caso");
        dawg.insert("cama");
        
        // Verificar que se pueden buscar correctamente
        assertTrue("'casa' debería encontrarse en el DAWG", dawg.search("casa"));
        assertTrue("'caso' debería encontrarse en el DAWG", dawg.search("caso"));
        assertTrue("'cama' debería encontrarse en el DAWG", dawg.search("cama"));
        
        // Verificar que palabras no insertadas no se encuentran
        assertFalse("'cosa' no debería encontrarse en el DAWG", dawg.search("cosa"));
        assertFalse("'cara' no debería encontrarse en el DAWG", dawg.search("cara"));
        assertFalse("'c' no debería encontrarse en el DAWG", dawg.search("c"));
        
        // Verificar comportamiento con cadena vacía
        assertFalse("La cadena vacía no debería encontrarse en el DAWG", dawg.search(""));
    }
    
    /**
     * Pre: Se ha creado una instancia de Dawg.
     * Post: Se verifica que se pueden insertar y encontrar prefijos como palabras válidas.
     * 
     * Comprueba el comportamiento con palabras que son prefijos de otras.
     * Aporta validación del manejo correcto de relaciones jerárquicas entre palabras.
     */
    @Test
    public void testInsertPrefijos() {
        // Insertar una palabra y sus prefijos
        dawg.insert("a");
        dawg.insert("ab");
        dawg.insert("abc");
        
        // Verificar que se encuentran todos
        assertTrue("'a' debería encontrarse en el DAWG", dawg.search("a"));
        assertTrue("'ab' debería encontrarse en el DAWG", dawg.search("ab"));
        assertTrue("'abc' debería encontrarse en el DAWG", dawg.search("abc"));
        
        // Verificar que no se encuentran extensiones
        assertFalse("'abcd' no debería encontrarse en el DAWG", dawg.search("abcd"));
    }
    
    /**
     * Pre: Se ha creado una instancia de Dawg y se han insertado algunas palabras.
     * Post: Se verifica que getNode() devuelve el nodo correcto para un prefijo o palabra,
     * y null para palabras no insertadas.
     * 
     * Comprueba la funcionalidad para acceder a nodos específicos dentro del grafo.
     * Aporta validación del acceso a la estructura interna del DAWG.
     */
    @Test
    public void testGetNode() {
        // Insertar algunas palabras
        dawg.insert("casa");
        dawg.insert("caso");
        
        // Obtener nodos para diferentes prefijos
        DawgNode nodoCas = dawg.getNode("cas");
        
        // Verificar que se obtienen nodos correctos
        assertNotNull("Debería haber un nodo para el prefijo 'cas'", nodoCas);
        assertFalse("El nodo para 'cas' no debería ser final", nodoCas.isFinal());
        assertTrue("El nodo para 'cas' debería tener aristas", nodoCas.getAllEdges().size() > 0);
        assertTrue("El nodo para 'cas' debería tener una arista para 'a'", 
                 nodoCas.getAllEdges().contains("a"));
        assertTrue("El nodo para 'cas' debería tener una arista para 'o'", 
                 nodoCas.getAllEdges().contains("o"));
        
        // Obtener nodos para palabras completas
        DawgNode nodoCasa = dawg.getNode("casa");
        
        // Verificar que son nodos finales
        assertNotNull("Debería haber un nodo para 'casa'", nodoCasa);
        assertTrue("El nodo para 'casa' debería ser final", nodoCasa.isFinal());
        
        // Verificar que no se obtienen nodos para palabras no insertadas
        assertNull("No debería haber un nodo para 'cama'", dawg.getNode("cama"));
    }
    
    /**
     * Pre: Se ha creado una instancia de Dawg.
     * Post: Se verifica que el comportamiento con cadenas vacías es correcto,
     * rechazándolas como palabras válidas.
     * 
     * Comprueba el manejo de casos límite con cadenas vacías.
     * Aporta validación del comportamiento ante entradas extremas.
     */
    @Test
    public void testInsertPalabrasVacias() {
        // Insertar cadena vacía
        dawg.insert("");
        
        // Verificar comportamiento
        assertFalse("La cadena vacía no debería encontrarse en el DAWG", dawg.search(""));
        assertNull("No debería haber un nodo para la cadena vacía", dawg.getNode(""));
    }
    
    /**
     * Pre: Se ha creado una instancia de Dawg.
     * Post: Se verifica que la estructura distingue correctamente entre
     * mayúsculas y minúsculas en las palabras.
     * 
     * Comprueba la sensibilidad a mayúsculas/minúsculas en la validación de palabras.
     * Aporta validación del correcto tratamiento de casos en las letras.
     */
    @Test
    public void testDiferentesCasos() {
        // Insertar palabras en minúsculas
        dawg.insert("casa");
        dawg.insert("CASA");
        
        // Verificar que se distingue entre mayúsculas y minúsculas
        assertTrue("'casa' debería encontrarse en el DAWG", dawg.search("casa"));
        assertTrue("'CASA' debería encontrarse en el DAWG", dawg.search("CASA"));
        assertFalse("'Casa' no debería encontrarse en el DAWG", dawg.search("Casa"));
    }
    
    /**
     * Pre: Se ha creado una instancia de Dawg.
     * Post: Se verifica que se pueden insertar y encontrar palabras
     * con caracteres especiales como acentos y ñ.
     * 
     * Comprueba el soporte para caracteres no ASCII como acentos y ñ.
     * Aporta validación del correcto manejo de caracteres especiales en español.
     */
    @Test
    public void testCaracteresEspeciales() {
        // Insertar palabras con caracteres especiales
        dawg.insert("café");
        dawg.insert("año");
        dawg.insert("niño");
        
        // Verificar que se pueden buscar correctamente
        assertTrue("'café' debería encontrarse en el DAWG", dawg.search("café"));
        assertTrue("'año' debería encontrarse en el DAWG", dawg.search("año"));
        assertTrue("'niño' debería encontrarse en el DAWG", dawg.search("niño"));
    }
    
    /**
     * Pre: Se ha creado una instancia de Dawg.
     * Post: Se verifica que se pueden insertar y encontrar palabras muy largas,
     * mientras que sus prefijos no se consideran palabras válidas.
     * 
     * Comprueba el comportamiento con palabras de gran longitud.
     * Aporta validación del manejo de entradas extensas y sus prefijos.
     */
    @Test
    public void testPalabrasMuyLargas() {
        // Insertar una palabra muy larga
        String palabraLarga = "supercalifragilisticoespialidoso";
        dawg.insert(palabraLarga);
        
        // Verificar que se puede buscar
        assertTrue("La palabra larga debería encontrarse en el DAWG", dawg.search(palabraLarga));
        
        // Verificar que un prefijo no se encuentra
        assertFalse("Un prefijo de la palabra larga no debería encontrarse en el DAWG", 
                  dawg.search("supercali"));
    }
    
    /**
     * Pre: Se ha creado una instancia de Dawg.
     * Post: Se verifica que getRoot() devuelve el nodo raíz esperado y que
     * las modificaciones en el grafo se reflejan correctamente a partir de este nodo.
     * 
     * Comprueba el acceso al nodo raíz y su comportamiento después de insertar palabras.
     * Aporta validación de la estructura del grafo a partir de su punto de entrada.
     */
    @Test
    public void testGetRoot() {
        // Verificar que getRoot devuelve el nodo raíz
        DawgNode raiz = dawg.getRoot();
        
        // La raíz no debería ser null
        assertNotNull("La raíz no debería ser null", raiz);
        
        // La raíz no debería ser final inicialmente
        assertFalse("La raíz no debería ser final inicialmente", raiz.isFinal());
        
        // Insertar una palabra de una letra
        dawg.insert("a");
        
        // Verificar que la raíz sigue sin ser final
        assertFalse("La raíz no debería ser final después de insertar 'a'", raiz.isFinal());
        
        // Verificar que la raíz tiene una arista para 'a'
        assertTrue("La raíz debería tener una arista para 'a'", raiz.getAllEdges().contains("a"));
        
        // Verificar que el nodo al que apunta la arista 'a' es final
        DawgNode nodoA = raiz.getEdge("a");
        assertNotNull("El nodo para 'a' no debería ser null", nodoA);
        assertTrue("El nodo para 'a' debería ser final", nodoA.isFinal());
    }
    
    /**
     * Pre: Se ha creado un mock de Dawg con comportamiento configurado.
     * Post: Se verifica que los métodos del mock devuelven los valores esperados
     * y que se han realizado las llamadas esperadas.
     * 
     * Comprueba la integración con la biblioteca Mockito para pruebas con objetos simulados.
     * Aporta validación del correcto uso de mocks para simular comportamientos.
     */
    @Test
    public void testIntegracionMockito() {
        Dawg mockDawg = Mockito.mock(Dawg.class);
        DawgNode mockRaiz = Mockito.mock(DawgNode.class);
        DawgNode mockNodo = Mockito.mock(DawgNode.class);
        
        // Configurar el comportamiento del mock
        when(mockDawg.getRoot()).thenReturn(mockRaiz);
        when(mockDawg.search("casa")).thenReturn(true);
        when(mockDawg.search("cosa")).thenReturn(false);
        when(mockDawg.getNode("cas")).thenReturn(mockNodo);
        
        // Verificar el comportamiento
        assertSame("El mock debería devolver el nodo raíz mock", mockRaiz, mockDawg.getRoot());
        assertTrue("El mock debería devolver true para search('casa')", mockDawg.search("casa"));
        assertFalse("El mock debería devolver false para search('cosa')", mockDawg.search("cosa"));
        assertSame("El mock debería devolver el nodo mock para getNode('cas')", 
                  mockNodo, mockDawg.getNode("cas"));
        
        // Verificar las llamadas
        verify(mockDawg).getRoot();
        verify(mockDawg).search("casa");
        verify(mockDawg).search("cosa");
        verify(mockDawg).getNode("cas");
    }
}