package scrabble.tests;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import scrabble.domain.models.Bolsa;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test unitario para la clase Bolsa
 */
public class BolsaTest {
    
    private Bolsa bolsa;
    private Map<String, Integer> letrasFrecuencias;
    
    @Before
    public void setUp() {
        // Inicializar una nueva bolsa y un mapa de letras con frecuencias
        bolsa = new Bolsa();
        letrasFrecuencias = new HashMap<>();
        letrasFrecuencias.put("A", 9);
        letrasFrecuencias.put("E", 12);
        letrasFrecuencias.put("O", 8);
        letrasFrecuencias.put("S", 6);
        letrasFrecuencias.put("X", 1);
    }
    
    /**
     * Pre: Se ha creado una instancia de Bolsa vacía y un mapa con frecuencias de letras.
     * Post: Se verifica que el método llenarBolsa() inicializa correctamente la bolsa con
     * la cantidad total de fichas esperada.
     * 
     * Comprueba la funcionalidad para inicializar la bolsa con un conjunto de letras.
     * Aporta validación de la correcta carga de fichas según las frecuencias definidas.
     */
    @Test
    public void testLlenarBolsa() {
        // Llenar la bolsa con las letras y frecuencias definidas
        bolsa.llenarBolsa(letrasFrecuencias);
        
        // Verificar que la cantidad de fichas es correcta
        int totalEsperado = 9 + 12 + 8 + 6 + 1; // Suma de todas las frecuencias
        assertEquals("La cantidad de fichas debería ser la suma de las frecuencias", 
                   totalEsperado, bolsa.getCantidadFichas());
    }
    
    /**
     * Pre: Se ha creado una instancia de Bolsa y se ha llenado con un conjunto de fichas.
     * Post: Se verifica que el método sacarFicha() extrae correctamente fichas de la bolsa,
     * reduciendo la cantidad total y devolviendo null cuando está vacía.
     * 
     * Comprueba la funcionalidad para extraer fichas aleatoriamente de la bolsa.
     * Aporta validación del correcto funcionamiento del mecanismo de extracción y actualización de estado.
     */
    @Test
    public void testSacarFicha() {
        // Llenar la bolsa con pocas fichas para facilitar las pruebas
        Map<String, Integer> pocasFichas = new HashMap<>();
        pocasFichas.put("A", 3);
        pocasFichas.put("B", 2);
        
        bolsa.llenarBolsa(pocasFichas);
        
        // Verificar que la cantidad inicial es correcta
        assertEquals("La cantidad inicial de fichas debería ser 5", 5, bolsa.getCantidadFichas());
        
        // Sacar una ficha
        String ficha = bolsa.sacarFicha();
        
        // Verificar que se ha sacado una ficha válida
        assertNotNull("La ficha sacada no debería ser null", ficha);
        assertTrue("La ficha sacada debería ser 'A' o 'B'", ficha.equals("A") || ficha.equals("B"));
        
        // Verificar que la cantidad se ha reducido
        assertEquals("La cantidad de fichas debería reducirse a 4", 4, bolsa.getCantidadFichas());
        
        // Sacar el resto de fichas
        Set<String> fichasSacadas = new HashSet<>();
        fichasSacadas.add(ficha);
        
        for (int i = 0; i < 4; i++) {
            ficha = bolsa.sacarFicha();
            assertNotNull("La ficha sacada no debería ser null", ficha);
            fichasSacadas.add(ficha);
        }
        
        // Verificar que se han sacado todas las fichas
        assertEquals("La cantidad de fichas debería ser 0", 0, bolsa.getCantidadFichas());
        
        // Verificar que se han sacado las fichas correctas
        assertTrue("Se deberían haber sacado fichas 'A'", fichasSacadas.contains("A"));
        assertTrue("Se deberían haber sacado fichas 'B'", fichasSacadas.contains("B"));
        
        // Intentar sacar otra ficha cuando la bolsa está vacía
        ficha = bolsa.sacarFicha();
        assertNull("Sacar una ficha de una bolsa vacía debería devolver null", ficha);
    }
    
    /**
     * Pre: Se ha creado una instancia de Bolsa y se ha llenado con un conjunto de fichas.
     * Post: Se verifica que las frecuencias de las letras sacadas coinciden con las frecuencias
     * inicialmente definidas.
     * 
     * Comprueba la distribución de frecuencias de las fichas extraídas.
     * Aporta validación de que la extracción preserva las proporciones correctas de cada letra.
     */
    @Test
    public void testFrecuenciasDeLetrasSacadas() {
        // Llenar la bolsa con letras y frecuencias conocidas
        bolsa.llenarBolsa(letrasFrecuencias);
        
        // Sacar todas las fichas y contar sus frecuencias
        Map<String, Integer> frecuenciasSacadas = new HashMap<>();
        String ficha;
        while ((ficha = bolsa.sacarFicha()) != null) {
            frecuenciasSacadas.put(ficha, frecuenciasSacadas.getOrDefault(ficha, 0) + 1);
        }
        
        // Verificar que las frecuencias coinciden con las originales
        for (Map.Entry<String, Integer> entry : letrasFrecuencias.entrySet()) {
            String letra = entry.getKey();
            int frecuenciaEsperada = entry.getValue();
            int frecuenciaReal = frecuenciasSacadas.getOrDefault(letra, 0);
            
            assertEquals("La frecuencia de la letra '" + letra + "' debería ser " + frecuenciaEsperada,
                       frecuenciaEsperada, frecuenciaReal);
        }
    }
    
    /**
     * Pre: Se ha creado una instancia de Bolsa.
     * Post: Se verifica que el comportamiento con una bolsa vacía es correcto: cero fichas
     * y null al intentar sacar una ficha.
     * 
     * Comprueba el manejo de casos límite con bolsas vacías.
     * Aporta validación del correcto comportamiento ante ausencia de fichas.
     */
    @Test
    public void testBolsaVacia() {
        // Verificar comportamiento con una bolsa vacía
        bolsa.llenarBolsa(new HashMap<>());
        
        assertEquals("Una bolsa recién creada debería tener 0 fichas", 0, bolsa.getCantidadFichas());
        assertNull("Sacar una ficha de una bolsa vacía debería devolver null", bolsa.sacarFicha());
        
        bolsa.llenarBolsa(new HashMap<>());
        assertEquals("Una bolsa llenada con un mapa vacío debería tener 0 fichas", 
                   0, bolsa.getCantidadFichas());
    }
    
    /**
     * Pre: Se ha creado una instancia de Bolsa y se ha llenado con un conjunto de fichas.
     * Post: Se verifica que llenar la bolsa nuevamente reemplaza el contenido anterior
     * en lugar de añadirlo.
     * 
     * Comprueba el comportamiento al llenar una bolsa que ya contiene fichas.
     * Aporta validación de la correcta gestión del reinicio de contenido.
     */
    @Test
    public void testLlenarBolsaRepetido() {
        // Llenar la bolsa una primera vez
        bolsa.llenarBolsa(letrasFrecuencias);
        int cantidadInicial = bolsa.getCantidadFichas();
        
        // Llenar la bolsa una segunda vez
        Map<String, Integer> otrasFichas = new HashMap<>();
        otrasFichas.put("C", 4);
        otrasFichas.put("D", 5);
        
        bolsa.llenarBolsa(otrasFichas);
        int cantidadFinal = bolsa.getCantidadFichas();
        
        // Verificar que la segunda llamada reemplaza el contenido, no lo añade
        assertEquals("La segunda llamada a llenarBolsa debería reemplazar el contenido", 
                   4 + 5, cantidadFinal);
        assertNotEquals("La cantidad final no debería ser la suma de las dos cargas", 
                      cantidadInicial + 4 + 5, cantidadFinal);
        
        // Verificar que ahora sólo salen las nuevas letras
        Set<String> letrasEsperadas = new HashSet<>(otrasFichas.keySet());
        Set<String> letrasSacadas = new HashSet<>();
        
        String ficha;
        while ((ficha = bolsa.sacarFicha()) != null) {
            letrasSacadas.add(ficha);
        }
        
        assertEquals("Se deberían haber sacado sólo las letras de la segunda carga",
                   letrasEsperadas, letrasSacadas);
    }
    
    /**
     * Pre: Se ha creado una instancia de Bolsa con diferentes cantidades de fichas.
     * Post: Se verifica que el método getCantidadFichas() devuelve correctamente la
     * cantidad total de fichas en la bolsa en diferentes estados.
     * 
     * Comprueba la funcionalidad para obtener la cantidad actual de fichas.
     * Aporta validación del correcto conteo de fichas en diferentes situaciones.
     */
    @Test
    public void testGetCantidadFichas() {
        bolsa.llenarBolsa(new HashMap<>());
        
        // Verificar con bolsa vacía
        assertEquals("Una bolsa vacía debería tener 0 fichas", 0, bolsa.getCantidadFichas());
        
        // Llenar la bolsa
        bolsa.llenarBolsa(letrasFrecuencias);
        
        // Calcular el total esperado
        int totalEsperado = 0;
        for (int frecuencia : letrasFrecuencias.values()) {
            totalEsperado += frecuencia;
        }
        
        // Verificar con bolsa llena
        assertEquals("La cantidad de fichas debería ser correcta", 
                   totalEsperado, bolsa.getCantidadFichas());
        
        // Sacar algunas fichas
        for (int i = 0; i < 10; i++) {
            bolsa.sacarFicha();
        }
        
        // Verificar con bolsa parcialmente vacía
        assertEquals("La cantidad de fichas debería reducirse correctamente", 
                   totalEsperado - 10, bolsa.getCantidadFichas());
    }
    
    /**
     * Pre: Se ha creado un mock de Bolsa con comportamiento configurado.
     * Post: Se verifica que los métodos del mock devuelven los valores esperados y que
     * se han realizado las llamadas esperadas.
     * 
     * Comprueba la integración con la biblioteca Mockito para pruebas con objetos simulados.
     * Aporta validación del correcto uso de mocks para simular comportamientos.
     */
    @Test
    public void testIntegracionMockito() {
        Bolsa mockBolsa = Mockito.mock(Bolsa.class);
        
        // Configurar el comportamiento del mock
        when(mockBolsa.getCantidadFichas()).thenReturn(42);
        when(mockBolsa.sacarFicha()).thenReturn("A").thenReturn("B").thenReturn(null);
        
        // Verificar el comportamiento
        assertEquals("El mock debería devolver 42 para getCantidadFichas", 
                   42, mockBolsa.getCantidadFichas());
        assertEquals("El mock debería devolver 'A' para la primera llamada a sacarFicha", 
                   "A", mockBolsa.sacarFicha());
        assertEquals("El mock debería devolver 'B' para la segunda llamada a sacarFicha", 
                   "B", mockBolsa.sacarFicha());
        assertNull("El mock debería devolver null para la tercera llamada a sacarFicha", 
                 mockBolsa.sacarFicha());
        
        // Verificar las llamadas
        verify(mockBolsa, times(1)).getCantidadFichas();
        verify(mockBolsa, times(3)).sacarFicha();
    }
}