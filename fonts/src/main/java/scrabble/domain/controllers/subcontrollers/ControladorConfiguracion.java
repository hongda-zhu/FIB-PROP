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
     * 
     * @pre No hay precondiciones específicas.
     * @post Se crea una nueva instancia de ControladorConfiguracion con una 
     *       configuración inicializada con valores por defecto.
     */
    public ControladorConfiguracion() {
        this.configuracion = new Configuracion();
    }

    /**
     * Obtiene el idioma actual configurado en la aplicación.
     * 
     * @pre No hay precondiciones específicas.
     * @return Idioma actual
     * @post Se devuelve un String que representa el idioma actual configurado en la aplicación.
     */
    public String obteneridioma() {
        return configuracion.obteneridioma();
    }

    /**
     * Obtiene el tema visual actual configurado en la aplicación.
     * 
     * @pre No hay precondiciones específicas.
     * @return Tema actual
     * @post Se devuelve un String que representa el tema visual actual configurado en la aplicación.
     */
    public String obtenerTema() {
        return configuracion.obtenerTema();
    }

    /**
     * Obtiene el volumen actual configurado en la aplicación.
     * 
     * @pre No hay precondiciones específicas.
     * @return Volumen actual
     * @post Se devuelve un entero que representa el nivel de volumen actual configurado en la aplicación.
     */
    public int obtenerVolumen() {
        return configuracion.obtenerVolumen();
    }

    /**
     * Establece un nuevo idioma para la configuración.
     * 
     * @pre El idioma no debe ser null para evitar comportamientos inesperados.
     * @param i Idioma a establecer
     * @post El idioma de la configuración se actualiza al valor especificado.
     * @throws NullPointerException Si el parámetro i es null.
     */
    public void setIdioma(String i) {
        configuracion.setIdioma(i);
    }

    /**
     * Establece un nuevo tema visual para la configuración.
     * 
     * @pre El tema no debe ser null para evitar comportamientos inesperados.
     * @param t Tema a establecer
     * @post El tema visual de la configuración se actualiza al valor especificado.
     * @throws NullPointerException Si el parámetro t es null.
     */
    public void setTema(String t) {
        configuracion.setTema(t);
    }

    /**
     * Establece un nuevo nivel de volumen para la configuración.
     * 
     * @pre No hay precondiciones específicas, aunque algunos modelos pueden
     *      implementar restricciones sobre los valores válidos de volumen.
     * @param v Volumen a establecer
     * @post El nivel de volumen de la configuración se actualiza al valor especificado.
     */
    public void setVolumen(int v) {
        configuracion.setVolumen(v);
    }
}
