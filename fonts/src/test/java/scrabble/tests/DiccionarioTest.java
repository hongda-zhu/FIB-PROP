package scrabble.tests;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import scrabble.domain.models.Dawg;
import scrabble.domain.models.Diccionario;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test unitario para la clase Diccionario
 */
public class DiccionarioTest {
    
    private Diccionario diccionario;
    private Path archivoAlphabetTemp;
    private Path archivoPalabrasTemp;
    
    @Before
    public void setUp() throws IOException {
        // Inicializar un nuevo diccionario antes de cada test
        diccionario = new Diccionario();
        
        // Crear archivos temporales para las pruebas
        archivoAlphabetTemp = Files.createTempFile("alphabet", ".txt");
        archivoPalabrasTemp = Files.createTempFile("palabras", ".txt");
        
        // Escribir datos en el archivo del alfabeto
        try (FileWriter writer = new FileWriter(archivoAlphabetTemp.toFile())) {
            writer.write("A 9 1\n");
            writer.write("E 12 1\n");
            writer.write("O 8 1\n");
            writer.write("S 6 1\n");
            writer.write("X 1 8\n");
        }
        
        // Escribir datos en el archivo de palabras
        try (FileWriter writer = new FileWriter(archivoPalabrasTemp.toFile())) {
            writer.write("CASA\n");
            writer.write("PERRO\n");
            writer.write("GATO\n");
            writer.write("SOL\n");
            writer.write("LUNA\n");
        }
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
        // Verificar que el diccionario se crea correctamente
        assertNotNull("El diccionario no debería ser null", diccionario);
    }
    
    /**
     * Pre: Se ha creado una instancia de Diccionario y existen archivos de palabras.
     * Post: Se verifica que las palabras se añaden correctamente al Dawg y se pueden recuperar.
     * 
     * Comprueba la funcionalidad para añadir y recuperar Dawgs.
     * Aporta validación de las operaciones fundamentales de gestión de estructura de palabras.
     */
    @Test
    public void testAddDawgYGetDawg() {
        // Añadir un Dawg y verificar que se puede recuperar
        diccionario.addDawg("español", archivoPalabrasTemp.toString());
        
        Dawg dawg = diccionario.getDawg("español");
        assertNotNull("El Dawg recuperado no debería ser null", dawg);
        
        // Verificar que el Dawg contiene las palabras del archivo
        assertTrue("El Dawg debería contener la palabra 'CASA'", dawg.search("CASA"));
        assertTrue("El Dawg debería contener la palabra 'PERRO'", dawg.search("PERRO"));
        assertTrue("El Dawg debería contener la palabra 'GATO'", dawg.search("GATO"));
        assertTrue("El Dawg debería contener la palabra 'SOL'", dawg.search("SOL"));
        assertTrue("El Dawg debería contener la palabra 'LUNA'", dawg.search("LUNA"));
        
        // Verificar que el Dawg no contiene palabras que no están en el archivo
        assertFalse("El Dawg no debería contener la palabra 'ÁRBOL'", dawg.search("ÁRBOL"));
    }
    
    /**
     * Pre: Se ha creado una instancia de Diccionario y existe un archivo de alfabeto.
     * Post: Se verifica que las letras y puntos se añaden correctamente al alfabeto y se pueden recuperar.
     * 
     * Comprueba la funcionalidad para añadir y recuperar alfabetos.
     * Aporta validación de las operaciones de gestión de alfabetos y puntuaciones.
     */
    @Test
    public void testAddAlphabetYGetAlphabet() {
        // Añadir un alfabeto y verificar que se puede recuperar
        diccionario.addAlphabet("español", archivoAlphabetTemp.toString());
        
        Map<String, Integer> alphabet = diccionario.getAlphabet("español");
        assertNotNull("El alfabeto recuperado no debería ser null", alphabet);
        
        // Verificar que el alfabeto contiene las letras y puntos correctos
        assertEquals("La letra 'A' debería valer 1 punto", Integer.valueOf(1), alphabet.get("A"));
        assertEquals("La letra 'E' debería valer 1 punto", Integer.valueOf(1), alphabet.get("E"));
        assertEquals("La letra 'O' debería valer 1 punto", Integer.valueOf(1), alphabet.get("O"));
        assertEquals("La letra 'S' debería valer 1 punto", Integer.valueOf(1), alphabet.get("S"));
        assertEquals("La letra 'X' debería valer 8 puntos", Integer.valueOf(8), alphabet.get("X"));
        
        // Verificar que no hay letras que no estén en el archivo
        assertNull("No debería existir la letra 'Z' en el alfabeto", alphabet.get("Z"));
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
        // Añadir un alfabeto y verificar que se puede recuperar la bolsa
        diccionario.addAlphabet("español", archivoAlphabetTemp.toString());
        
        Map<String, Integer> bag = diccionario.getBag("español");
        assertNotNull("La bolsa recuperada no debería ser null", bag);
        
        // Verificar que la bolsa contiene las letras y frecuencias correctas
        assertEquals("La frecuencia de 'A' debería ser 9", Integer.valueOf(9), bag.get("A"));
        assertEquals("La frecuencia de 'E' debería ser 12", Integer.valueOf(12), bag.get("E"));
        assertEquals("La frecuencia de 'O' debería ser 8", Integer.valueOf(8), bag.get("O"));
        assertEquals("La frecuencia de 'S' debería ser 6", Integer.valueOf(6), bag.get("S"));
        assertEquals("La frecuencia de 'X' debería ser 1", Integer.valueOf(1), bag.get("X"));
        
        // Verificar que no hay letras que no estén en el archivo
        assertNull("No debería existir la letra 'Z' en la bolsa", bag.get("Z"));
    }
    
    /**
     * Pre: Se ha creado una instancia de Diccionario y se ha añadido un Dawg.
     * Post: Se verifica que el Dawg se elimina correctamente y ya no es accesible.
     * 
     * Comprueba la funcionalidad para eliminar un Dawg.
     * Aporta validación de la correcta gestión de eliminación de diccionarios.
     */
    @Test
    public void testDeleteDawg() {
        // Añadir un Dawg
        diccionario.addDawg("español", archivoPalabrasTemp.toString());
        
        // Verificar que se puede recuperar
        assertNotNull("El Dawg debería existir antes de eliminarlo", diccionario.getDawg("español"));
        
        // Eliminar el Dawg
        diccionario.deleteDawg("español");
        
        // Verificar que ya no se puede recuperar
        assertNull("El Dawg no debería existir después de eliminarlo", diccionario.getDawg("español"));
    }
    
    /**
     * Pre: Se ha creado una instancia de Diccionario y se ha añadido un alfabeto.
     * Post: Se verifica que el alfabeto se elimina correctamente y ya no es accesible.
     * 
     * Comprueba la funcionalidad para eliminar un alfabeto.
     * Aporta validación de la correcta gestión de eliminación de alfabetos.
     */
    @Test
    public void testDeleteAlphabet() {
        // Añadir un alfabeto
        diccionario.addAlphabet("español", archivoAlphabetTemp.toString());
        
        // Verificar que se puede recuperar
        assertNotNull("El alfabeto debería existir antes de eliminarlo", diccionario.getAlphabet("español"));
        
        // Eliminar el alfabeto
        diccionario.deleteAlphabet("español");
        
        // Verificar que ya no se puede recuperar
        assertNull("El alfabeto no debería existir después de eliminarlo", diccionario.getAlphabet("español"));
    }
    
    /**
     * Pre: Se ha creado una instancia de Diccionario y existen archivos para dos idiomas diferentes.
     * Post: Se verifica que los datos de ambos idiomas se almacenan de forma independiente.
     * 
     * Comprueba la funcionalidad para gestionar múltiples idiomas simultáneamente.
     * Aporta validación del aislamiento entre diferentes configuraciones lingüísticas.
     */
    @Test
    public void testMultiplesIdiomasIndependientes() {
        // Añadir múltiples idiomas
        diccionario.addDawg("español", archivoPalabrasTemp.toString());
        diccionario.addAlphabet("español", archivoAlphabetTemp.toString());
        
        // Crear archivos temporales para otro idioma
        try {
            Path archivoAlphabetTemp2 = Files.createTempFile("alphabet2", ".txt");
            Path archivoPalabrasTemp2 = Files.createTempFile("palabras2", ".txt");
            
            // Escribir datos en el archivo del alfabeto
            try (FileWriter writer = new FileWriter(archivoAlphabetTemp2.toFile())) {
                writer.write("A 1 2\n");
                writer.write("B 2 3\n");
                writer.write("C 3 4\n");
            }
            
            // Escribir datos en el archivo de palabras
            try (FileWriter writer = new FileWriter(archivoPalabrasTemp2.toFile())) {
                writer.write("APPLE\n");
                writer.write("BANANA\n");
                writer.write("CHERRY\n");
            }
            
            // Añadir el segundo idioma
            diccionario.addDawg("inglés", archivoPalabrasTemp2.toString());
            diccionario.addAlphabet("inglés", archivoAlphabetTemp2.toString());
            
            // Verificar que ambos idiomas existen y son independientes
            Dawg dawgEspanol = diccionario.getDawg("español");
            Dawg dawgIngles = diccionario.getDawg("inglés");
            
            assertNotNull("El Dawg español no debería ser null", dawgEspanol);
            assertNotNull("El Dawg inglés no debería ser null", dawgIngles);
            
            // Verificar que cada Dawg contiene sus propias palabras
            assertTrue("El Dawg español debería contener 'CASA'", dawgEspanol.search("CASA"));
            assertTrue("El Dawg inglés debería contener 'APPLE'", dawgIngles.search("APPLE"));
            
            assertFalse("El Dawg español no debería contener 'APPLE'", dawgEspanol.search("APPLE"));
            assertFalse("El Dawg inglés no debería contener 'CASA'", dawgIngles.search("CASA"));
            
            // Verificar que cada alfabeto contiene sus propias letras
            Map<String, Integer> alphabetEspanol = diccionario.getAlphabet("español");
            Map<String, Integer> alphabetIngles = diccionario.getAlphabet("inglés");
            
            assertEquals("La 'A' en español debería valer 1", Integer.valueOf(1), alphabetEspanol.get("A"));
            assertEquals("La 'A' en inglés debería valer 2", Integer.valueOf(2), alphabetIngles.get("A"));
            
            assertNull("El alfabeto español no debería contener 'B'", alphabetEspanol.get("B"));
            assertNotNull("El alfabeto inglés debería contener 'B'", alphabetIngles.get("B"));
            
            // Limpiar
            Files.delete(archivoAlphabetTemp2);
            Files.delete(archivoPalabrasTemp2);
            
        } catch (IOException e) {
            fail("Error al crear o manipular archivos temporales: " + e.getMessage());
        }
    }
    
    /**
     * Pre: Se ha creado una instancia de Diccionario con datos para un idioma.
     * Post: Se verifica que al añadir nuevos datos con el mismo nombre se sobrescriben los anteriores.
     * 
     * Comprueba el comportamiento al reutilizar nombres de idioma.
     * Aporta validación de la correcta gestión de actualización de configuraciones lingüísticas.
     */
    @Test
    public void testSobreescribirDawgYAlphabet() {
        // Añadir un Dawg y un alfabeto
        diccionario.addDawg("español", archivoPalabrasTemp.toString());
        diccionario.addAlphabet("español", archivoAlphabetTemp.toString());
        
        // Crear nuevos archivos temporales con diferentes contenidos
        try {
            Path archivoAlphabetTemp2 = Files.createTempFile("alphabet2", ".txt");
            Path archivoPalabrasTemp2 = Files.createTempFile("palabras2", ".txt");
            
            // Escribir datos en el archivo del alfabeto
            try (FileWriter writer = new FileWriter(archivoAlphabetTemp2.toFile())) {
                writer.write("Z 1 10\n");
            }
            
            // Escribir datos en el archivo de palabras
            try (FileWriter writer = new FileWriter(archivoPalabrasTemp2.toFile())) {
                writer.write("ZEBRA\n");
            }
            
            // Sobreescribir el mismo idioma
            diccionario.addDawg("español", archivoPalabrasTemp2.toString());
            diccionario.addAlphabet("español", archivoAlphabetTemp2.toString());
            
            // Verificar que se han sobreescrito los datos
            Dawg dawg = diccionario.getDawg("español");
            Map<String, Integer> alphabet = diccionario.getAlphabet("español");
            
            // Verificar que el Dawg contiene solo las nuevas palabras
            assertTrue("El Dawg debería contener 'ZEBRA'", dawg.search("ZEBRA"));
            assertFalse("El Dawg no debería contener 'CASA'", dawg.search("CASA"));
            
            // Verificar que el alfabeto contiene solo las nuevas letras
            assertEquals("La letra 'Z' debería valer 10 puntos", Integer.valueOf(10), alphabet.get("Z"));
            assertNull("El alfabeto no debería contener 'A'", alphabet.get("A"));
            
            // Limpiar
            Files.delete(archivoAlphabetTemp2);
            Files.delete(archivoPalabrasTemp2);
            
        } catch (IOException e) {
            fail("Error al crear o manipular archivos temporales: " + e.getMessage());
        }
    }
    
    /**
     * Pre: Se ha creado una instancia de Diccionario.
     * Post: Se verifica que al añadir datos con archivos inexistentes se crean estructuras vacías.
     * 
     * Comprueba el comportamiento con archivos inexistentes.
     * Aporta validación de la robustez ante errores de acceso a archivos.
     */
    @Test
    public void testComportamientoConArchivosInexistentes() {
        // Intentar añadir un Dawg con un archivo que no existe
        String rutaInexistente = "ruta/que/no/existe.txt";
        diccionario.addDawg("español", rutaInexistente);
        
        // El Dawg debería existir, pero estar vacío
        Dawg dawg = diccionario.getDawg("español");
        assertNotNull("El Dawg no debería ser null incluso con archivo inexistente", dawg);
        assertFalse("El Dawg no debería contener ninguna palabra", dawg.search("CASA"));
        
        // Intentar añadir un alfabeto con un archivo que no existe
        diccionario.addAlphabet("español", rutaInexistente);
        
        // El alfabeto debería existir, pero estar vacío
        Map<String, Integer> alphabet = diccionario.getAlphabet("español");
        assertNotNull("El alfabeto no debería ser null incluso con archivo inexistente", alphabet);
        assertTrue("El alfabeto debería estar vacío", alphabet.isEmpty());
    }
    
    /**
     * Pre: Se ha creado una instancia de Diccionario y archivos temporales vacíos.
     * Post: Se verifica que al añadir datos con archivos vacíos se crean estructuras vacías.
     * 
     * Comprueba el comportamiento con archivos vacíos.
     * Aporta validación de la correcta gestión ante la ausencia de datos.
     */
    @Test
    public void testComportamientoConArchivosVacios() {
        try {
            // Crear archivos temporales vacíos
            Path archivoVacio1 = Files.createTempFile("vacio1", ".txt");
            Path archivoVacio2 = Files.createTempFile("vacio2", ".txt");
            
            // Añadir un Dawg y un alfabeto con archivos vacíos
            diccionario.addDawg("español", archivoVacio1.toString());
            diccionario.addAlphabet("español", archivoVacio2.toString());
            
            // Verificar que el Dawg y el alfabeto existen, pero están vacíos
            Dawg dawg = diccionario.getDawg("español");
            Map<String, Integer> alphabet = diccionario.getAlphabet("español");
            
            assertNotNull("El Dawg no debería ser null con archivo vacío", dawg);
            assertNotNull("El alfabeto no debería ser null con archivo vacío", alphabet);
            
            assertTrue("El alfabeto debería estar vacío", alphabet.isEmpty());
            assertFalse("El Dawg no debería contener ninguna palabra", dawg.search("CASA"));
            
            // Limpiar
            Files.delete(archivoVacio1);
            Files.delete(archivoVacio2);
            
        } catch (IOException e) {
            fail("Error al crear o manipular archivos temporales: " + e.getMessage());
        }
    }
    
    /**
     * Pre: Se ha creado una instancia de Diccionario sin añadir ningún idioma.
     * Post: Se verifica que al consultar un idioma inexistente se obtiene null.
     * 
     * Comprueba el comportamiento ante consultas de idiomas no definidos.
     * Aporta validación de la correcta gestión de casos de no existencia.
     */
    @Test
    public void testComportamientoConIdiomaInexistente() {
        // Intentar obtener un Dawg para un idioma que no existe
        Dawg dawg = diccionario.getDawg("idiomaInexistente");
        assertNull("El Dawg debería ser null para un idioma inexistente", dawg);
        
        // Intentar obtener un alfabeto para un idioma que no existe
        Map<String, Integer> alphabet = diccionario.getAlphabet("idiomaInexistente");
        assertNull("El alfabeto debería ser null para un idioma inexistente", alphabet);
        
        // Intentar obtener una bolsa para un idioma que no existe
        Map<String, Integer> bag = diccionario.getBag("idiomaInexistente");
        assertNull("La bolsa debería ser null para un idioma inexistente", bag);
    }
    
    /**
     * Pre: Se ha creado una instancia de Diccionario sin añadir ningún idioma.
     * Post: Se verifica que eliminar un idioma inexistente no causa errores.
     * 
     * Comprueba el comportamiento ante intentos de eliminación de idiomas no definidos.
     * Aporta validación de la correcta gestión de errores en operaciones de eliminación.
     */
    @Test
    public void testEliminarIdiomaInexistente() {
        // No debería ocurrir ningún error al intentar eliminar un idioma que no existe
        diccionario.deleteDawg("idiomaInexistente");
        diccionario.deleteAlphabet("idiomaInexistente");
    }
    
    /**
     * Pre: Se ha creado un mock de Diccionario con comportamiento configurado.
     * Post: Se verifica que los métodos del mock devuelven los valores esperados
     * y que se han realizado las llamadas esperadas.
     * 
     * Comprueba la integración con la biblioteca Mockito para pruebas con objetos simulados.
     * Aporta validación del correcto uso de mocks para simular comportamientos.
     */
    @Test
    public void testIntegracionMockito() {
        Diccionario mockDiccionario = Mockito.mock(Diccionario.class);
        Dawg mockDawg = Mockito.mock(Dawg.class);
        Map<String, Integer> mockAlphabet = Mockito.mock(Map.class);
        Map<String, Integer> mockBag = Mockito.mock(Map.class);
        
        // Configurar el comportamiento del mock
        when(mockDiccionario.getDawg("español")).thenReturn(mockDawg);
        when(mockDiccionario.getAlphabet("español")).thenReturn(mockAlphabet);
        when(mockDiccionario.getBag("español")).thenReturn(mockBag);
        
        // Verificar el comportamiento
        assertSame("El mock debería devolver el Dawg mock", mockDawg, mockDiccionario.getDawg("español"));
        assertSame("El mock debería devolver el alfabeto mock", mockAlphabet, mockDiccionario.getAlphabet("español"));
        assertSame("El mock debería devolver la bolsa mock", mockBag, mockDiccionario.getBag("español"));
        
        // Verificar las llamadas
        verify(mockDiccionario).getDawg("español");
        verify(mockDiccionario).getAlphabet("español");
        verify(mockDiccionario).getBag("español");
    }
}