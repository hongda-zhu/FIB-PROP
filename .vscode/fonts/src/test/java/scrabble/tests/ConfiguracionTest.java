package scrabble.tests;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import scrabble.domain.models.Configuracion;

/**
 * Test unitario para la clase Configuracion
 */
public class ConfiguracionTest {
    
    private Configuracion configuracion;
    
    @Before
    public void setUp() {
        // Inicializar una nueva configuración antes de cada test
        configuracion = new Configuracion();
    }
    
    @Test
    public void testValoresPorDefecto() {
        // Verificar que los valores por defecto son correctos
        assertEquals("El idioma por defecto debería ser ESPANOL", 
                    "ESPANOL", configuracion.obteneridioma());
        assertEquals("El tema por defecto debería ser CLARO", 
                    "CLARO", configuracion.obtenerTema());
        assertEquals("El volumen por defecto debería ser 50", 
                    50, configuracion.obtenerVolumen());
    }
    
    @Test
    public void testCambiarIdioma() {
        // Cambiar el idioma y verificar
        configuracion.setIdioma("INGLES");
        assertEquals("El idioma debería haber cambiado a INGLES", 
                    "INGLES", configuracion.obteneridioma());
    }
    
    @Test
    public void testCambiarTema() {
        // Cambiar el tema y verificar
        configuracion.setTema("OSCURO");
        assertEquals("El tema debería haber cambiado a OSCURO", 
                    "OSCURO", configuracion.obtenerTema());
    }
    
    @Test
    public void testCambiarVolumen() {
        // Cambiar el volumen y verificar
        configuracion.setVolumen(75);
        assertEquals("El volumen debería haber cambiado a 75", 
                    75, configuracion.obtenerVolumen());
    }
    
    @Test
    public void testCambiarVolumenNegativo() {
        // Cambiar el volumen y verificar

        try {
            configuracion.setVolumen(-1);
            fail("Se espera IllegalArgumentException  para volumen negativo"); 
        } catch (IllegalArgumentException  e) {
            assertEquals("El volumen no puede ser negativo", "El volumen debe ser un valor entre 0 y 100", e.getMessage());
        }
    }

    @Test
    public void testCambiarVolumenMuyAlto() {
        // Cambiar el volumen y verificar

        try {
            configuracion.setVolumen(101);
            fail("Se espera IllegalArgumentException  para volumen mayor de 100");
        } catch (IllegalArgumentException  e) {
            assertEquals("El volumen no puede ser negativo", "El volumen debe ser un valor entre 0 y 100", e.getMessage());
        }
    }

    @Test
    public void testCambiarIdiomaInexistente() {

        try {
            configuracion.setIdioma("ASDWD");
            fail("Se espera IllegalArgumentException  para idioma inexistente");
        } catch (IllegalArgumentException  e) {
            assertEquals("El idioma debe existir", "El idioma debe ser: ESPANOL, CATALAN o INGLES", e.getMessage());
        }
    }

    @Test
    public void testCambiarTemaInexistente() {

        try {
            configuracion.setTema("ASDWD");
            fail("Se espera IllegalArgumentException  para tema inexistente");
        } catch (IllegalArgumentException  e) {
            assertEquals("El tema debe existir", "El tema deber ser CLARO o OSCURO", e.getMessage());
        }
    }   

    @Test
    public void testIntegracionMockito() {
        // Crear un mock de Configuracion
        Configuracion mockConfig = Mockito.mock(Configuracion.class);
        
        // Configurar el comportamiento del mock
        when(mockConfig.obteneridioma()).thenReturn("CATALAN");
        when(mockConfig.obtenerTema()).thenReturn("OSCURO");
        when(mockConfig.obtenerVolumen()).thenReturn(100);
        
        // Verificar el comportamiento configurado
        assertEquals("El mock debería devolver CATALAN", 
                    "CATALAN", mockConfig.obteneridioma());
        assertEquals("El mock debería devolver OSCURO", 
                    "OSCURO", mockConfig.obtenerTema());
        assertEquals("El mock debería devolver 100", 
                    100, mockConfig.obtenerVolumen());
        
        // Verificar que se llamaron los métodos
        verify(mockConfig).obteneridioma();
        verify(mockConfig).obtenerTema();
        verify(mockConfig).obtenerVolumen();
    }
}