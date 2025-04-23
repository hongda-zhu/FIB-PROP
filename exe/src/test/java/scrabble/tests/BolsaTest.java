package scrabble.tests;

import org.junit.Before;
import org.junit.Test;
import scrabble.domain.models.Bolsa; // Asegúrate que la ruta es correcta

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;


/**
 * Test unitario para la clase Bolsa 
 */
public class BolsaTest {

    private Bolsa bolsa;
    private Map<String, Integer> letrasFrecuenciasBase;

    @Before
    public void setUp() {
        // Inicializar una nueva bolsa y un mapa de letras con frecuencias base
        // antes de cada test.
        bolsa = new Bolsa(); // Asume constructor por defecto
        letrasFrecuenciasBase = new HashMap<>();
        letrasFrecuenciasBase.put("A", 9);
        letrasFrecuenciasBase.put("E", 12);
        letrasFrecuenciasBase.put("O", 8);
        letrasFrecuenciasBase.put("S", 6);
        letrasFrecuenciasBase.put("X", 1);
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
        bolsa.llenarBolsa(letrasFrecuenciasBase);

        // Calcular total esperado (forma alternativa sin streams por si acaso)
        int totalEsperado = 0;
        for (int freq : letrasFrecuenciasBase.values()) {
            totalEsperado += freq;
        }

        // Verificar que la cantidad de fichas es correcta
        // assertEquals(message, expected, actual) en JUnit 4
        assertEquals("La cantidad de fichas debería ser la suma de las frecuencias definida.",
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
        int totalPocasFichas = 5;

        bolsa.llenarBolsa(pocasFichas);

        // Verificar que la cantidad inicial es correcta
        assertEquals("La cantidad inicial de fichas debería ser " + totalPocasFichas,
                     totalPocasFichas, bolsa.getCantidadFichas());

        // Sacar una ficha
        String ficha = bolsa.sacarFicha();

        // Verificar que se ha sacado una ficha válida
        assertNotNull("La primera ficha sacada no debería ser null.", ficha);
        assertTrue("La ficha sacada (" + ficha + ") debería ser una de las definidas en pocasFichas.",
                   pocasFichas.containsKey(ficha));

        // Verificar que la cantidad se ha reducido
        assertEquals("La cantidad de fichas debería reducirse en 1 después de sacar una.",
                     totalPocasFichas - 1, bolsa.getCantidadFichas());

        // Sacar el resto de fichas
        Set<String> fichasSacadas = new HashSet<>();
        fichasSacadas.add(ficha); // Añadir la primera que sacamos

        // Sacamos las 4 restantes
        for (int i = 0; i < totalPocasFichas - 1; i++) {
            ficha = bolsa.sacarFicha();
            assertNotNull("La ficha sacada (iteración " + i + ") no debería ser null.", ficha);
            assertTrue("La ficha sacada (" + ficha + ") en la iteración " + i + " debería ser válida.",
                       pocasFichas.containsKey(ficha));
            fichasSacadas.add(ficha);
        }

        // Verificar que se han sacado todas las fichas
        assertEquals("La cantidad de fichas debería ser 0 después de sacar todas.",
                     0, bolsa.getCantidadFichas());

        // Verificar que los tipos de fichas sacadas son los esperados (A y B)
        assertTrue("Se deberían haber sacado fichas 'A'.", fichasSacadas.contains("A"));
        assertTrue("Se deberían haber sacado fichas 'B'.", fichasSacadas.contains("B"));
        assertEquals("Solo debería haber dos tipos de fichas ('A' y 'B') sacadas.",
                     2, fichasSacadas.size());

        // Intentar sacar otra ficha cuando la bolsa está vacía
        ficha = bolsa.sacarFicha();
        assertNull("Sacar una ficha de una bolsa vacía debería devolver null.", ficha);
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
        bolsa.llenarBolsa(letrasFrecuenciasBase);

        // Sacar todas las fichas y contar sus frecuencias
        Map<String, Integer> frecuenciasSacadas = new HashMap<>();
        String ficha;
        while ((ficha = bolsa.sacarFicha()) != null) {
            frecuenciasSacadas.put(ficha, frecuenciasSacadas.getOrDefault(ficha, 0) + 1);
        }

        // Verificar que las frecuencias coinciden con las originales
        assertEquals("El número de tipos de letras sacadas debe coincidir con el inicial.",
                     letrasFrecuenciasBase.size(), frecuenciasSacadas.size());

        for (Map.Entry<String, Integer> entry : letrasFrecuenciasBase.entrySet()) {
            String letra = entry.getKey();
            int frecuenciaEsperada = entry.getValue();
            // Usar getOrDefault para evitar NullPointerException si falta una letra (aunque no debería pasar)
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
        // El setup ya crea una bolsa vacía, pero llenamos explícitamente con vacío para asegurar
        bolsa.llenarBolsa(new HashMap<String, Integer>()); // Especificar tipos genéricos es buena práctica

        assertEquals("Una bolsa llenada con un mapa vacío debería tener 0 fichas.",
                     0, bolsa.getCantidadFichas());
        assertNull("Sacar una ficha de una bolsa explícitamente vaciada debería devolver null.",
                   bolsa.sacarFicha());
        // La segunda llamada y assert redundantes han sido eliminados.
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
        bolsa.llenarBolsa(letrasFrecuenciasBase);
        int cantidadInicial = bolsa.getCantidadFichas();
        assertTrue("La cantidad inicial debería ser mayor que 0.", cantidadInicial > 0);

        // Preparar un segundo conjunto de fichas
        Map<String, Integer> otrasFichas = new HashMap<>();
        otrasFichas.put("C", 4);
        otrasFichas.put("D", 5);
        int cantidadOtrasFichas = 9;

        // Llenar la bolsa una segunda vez
        bolsa.llenarBolsa(otrasFichas);
        int cantidadFinal = bolsa.getCantidadFichas();

        // Verificar que la segunda llamada reemplaza el contenido, no lo añade
        assertEquals("La segunda llamada a llenarBolsa debería reemplazar el contenido, resultando en " + cantidadOtrasFichas + " fichas.",
                     cantidadOtrasFichas, cantidadFinal);
        assertNotEquals("La cantidad final no debería ser la suma de las dos cargas.",
                       cantidadInicial + cantidadOtrasFichas, cantidadFinal);

        // Verificar que ahora sólo salen las nuevas letras
        Set<String> letrasEsperadas = otrasFichas.keySet(); // Esperamos "C" y "D"
        Set<String> letrasSacadas = new HashSet<>();
        String ficha;
        while ((ficha = bolsa.sacarFicha()) != null) {
            letrasSacadas.add(ficha);
        }

        // Usamos assertEquals para comparar Sets directamente
        assertEquals("Tras la segunda carga, sólo se deberían haber sacado las letras de esa carga.",
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
        // Verificar con bolsa inicialmente vacía (del setup)
        // Llenamos explícitamente para ser claros en el test
         bolsa.llenarBolsa(new HashMap<String, Integer>());
         assertEquals("Una bolsa recién creada (o vaciada) debería tener 0 fichas.",
                      0, bolsa.getCantidadFichas());

        // Llenar la bolsa
        bolsa.llenarBolsa(letrasFrecuenciasBase);

        // Calcular el total esperado
        int totalEsperado = 0;
        for (int freq : letrasFrecuenciasBase.values()) {
            totalEsperado += freq;
        }

        // Verificar con bolsa llena
        assertEquals("La cantidad de fichas después de llenar debería ser " + totalEsperado,
                     totalEsperado, bolsa.getCantidadFichas());

        // Sacar algunas fichas (menos que el total)
        int fichasASacar = 10;
        assertTrue("Asegurarse que hay suficientes fichas para sacar.", totalEsperado > fichasASacar);
        for (int i = 0; i < fichasASacar; i++) {
             String ficha = bolsa.sacarFicha();
             // Podemos añadir una verificación extra si queremos ser muy estrictos
             assertNotNull("La ficha sacada no debería ser null mientras haya fichas (iteración " + i + ").", ficha);
        }

        // Verificar con bolsa parcialmente vacía
        assertEquals("La cantidad de fichas después de sacar " + fichasASacar + " debería ser " + (totalEsperado - fichasASacar),
                     totalEsperado - fichasASacar, bolsa.getCantidadFichas());

        // Vaciar completamente la bolsa
        while(bolsa.sacarFicha() != null); // Bucle para vaciar

        // Verificar con bolsa vaciada
        assertEquals("La cantidad de fichas después de vaciarla completamente debería ser 0.",
                     0, bolsa.getCantidadFichas());
    }

    // testIntegracionMockito eliminado porque no prueba la lógica de Bolsa.

}