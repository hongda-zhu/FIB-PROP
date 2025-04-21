package scrabble.domain.controllers.subcontrollers;

import scrabble.domain.models.Configuracion;

/**
 * Controlador para la gestión de la configuración de la aplicación.
 * Encapsula el acceso a los parámetros configurables como idioma, tema y volumen.
 */

public class ControladorConfiguracion {
    /**
     * Instancia de configuración que contiene los valores actuales de la aplicación.
     */
    private Configuracion configuracion;

    /**
     * Constructor para la clase ControladorConfiguracion.
     * Inicializa una nueva instancia de Configuracion.
     */
    public ControladorConfiguracion() {
        this.configuracion = new Configuracion();
    }

    /**
     * Obtiene el idioma actual configurado en la aplicación.
     * 
     * @return Idioma actual
     */
    public String obteneridioma() {
        return configuracion.obteneridioma();
    }

    /**
     * Obtiene el tema visual actual configurado en la aplicación.
     * 
     * @return Tema actual
     */
    public String obtenerTema() {
        return configuracion.obtenerTema();
    }

    /**
     * Obtiene el volumen actual configurado en la aplicación.
     * 
     * @return Volumen actual
     */
    public int obtenerVolumen() {
        return configuracion.obtenerVolumen();
    }

    /**
     * Establece un nuevo idioma para la configuración.
     * 
     * @param i Idioma a establecer
     */
    public void setIdioma(String i) {
        configuracion.setIdioma(i);
    }

    /**
     * Establece un nuevo tema visual para la configuración.
     * 
     * @param t Tema a establecer
     */
    public void setTema(String t) {
        configuracion.setTema(t);
    }

    /**
     * Establece un nuevo nivel de volumen para la configuración.
     * 
     * @param v Volumen a establecer
     */
    public void setVolumen(int v) {
        configuracion.setVolumen(v);
    }
}
