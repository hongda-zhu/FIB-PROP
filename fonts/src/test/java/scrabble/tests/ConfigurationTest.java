package scrabble.tests;

import scrabble.helpers.Idioma;
import scrabble.helpers.Tema;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

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
                    Idioma.ESPANOL, configuracion.obteneridioma());
        assertEquals("El tema por defecto debería ser CLARO", 
                    Tema.CLARO, configuracion.obtenerTema());
        assertEquals("El volumen por defecto debería ser 50", 
                    50, configuracion.obtenerVolumen());
    }
    
    @Test
    public void testCambiarIdioma() {
        // Cambiar el idioma y verificar
        configuracion.setIdioma(Idioma.INGLES);
        assertEquals("El idioma debería haber cambiado a INGLES", 
                    Idioma.INGLES, configuracion.obteneridioma());
    }
    
    @Test
    public void testCambiarTema() {
        // Cambiar el tema y verificar
        configuracion.setTema(Tema.OSCURO);
        assertEquals("El tema debería haber cambiado a OSCURO", 
                    Tema.OSCURO, configuracion.obtenerTema());
    }
    
    @Test
    public void testCambiarVolumen() {
        // Cambiar el volumen y verificar
        configuracion.setVolumen(75);
        assertEquals("El volumen debería haber cambiado a 75", 
                    75, configuracion.obtenerVolumen());
    }
    
    @Test
    public void testIntegracionMockito() {
        // Crear un mock de Configuracion
        Configuracion mockConfig = Mockito.mock(Configuracion.class);
        
        // Configurar el comportamiento del mock
        when(mockConfig.obteneridioma()).thenReturn(Idioma.CATALAN);
        when(mockConfig.obtenerTema()).thenReturn(Tema.OSCURO);
        when(mockConfig.obtenerVolumen()).thenReturn(100);
        
        // Verificar el comportamiento configurado
        assertEquals("El mock debería devolver CATALAN", 
                    Idioma.CATALAN, mockConfig.obteneridioma());
        assertEquals("El mock debería devolver OSCURO", 
                    Tema.OSCURO, mockConfig.obtenerTema());
        assertEquals("El mock debería devolver 100", 
                    100, mockConfig.obtenerVolumen());
        
        // Verificar que se llamaron los métodos
        verify(mockConfig).obteneridioma();
        verify(mockConfig).obtenerTema();
        verify(mockConfig).obtenerVolumen();
    }
}