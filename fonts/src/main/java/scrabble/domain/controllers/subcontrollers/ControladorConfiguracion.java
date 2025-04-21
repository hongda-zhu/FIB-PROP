package scrabble.domain.controllers.subcontrollers;

import scrabble.domain.models.Configuracion;
import java.io.File;

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
     * Directorio base para los archivos de persistencia.
     * Por defecto es el directorio actual, pero puede cambiarse.
     */
    private String directorioDataBase = "./data";

    /**
     * Constructor para la clase ControladorConfiguracion.
     * Inicializa una nueva instancia de Configuracion.
     */
    public ControladorConfiguracion() {
        this.configuracion = new Configuracion();
        inicializarDirectorioDatos();
    }
    
    /**
     * Inicializa el directorio de datos si no existe.
     */
    private void inicializarDirectorioDatos() {
        File directorioData = new File(directorioDataBase);
        if (!directorioData.exists()) {
            directorioData.mkdirs();
        }
    }
    
    /**
     * Obtiene la ruta completa para un archivo de persistencia.
     * 
     * @param nombreArchivo Nombre del archivo de persistencia (ej: "jugadores.dat")
     * @return Ruta completa al archivo
     */
    public String getRutaArchivoPersistencia(String nombreArchivo) {
        return directorioDataBase + File.separator + nombreArchivo;
    }
    
    /**
     * Establece el directorio base para los archivos de persistencia.
     * 
     * @param directorio Nuevo directorio base
     */
    public void setDirectorioDataBase(String directorio) {
        this.directorioDataBase = directorio;
        inicializarDirectorioDatos();
    }
    
    /**
     * Obtiene el directorio base actual para los archivos de persistencia.
     * 
     * @return Directorio base actual
     */
    public String getDirectorioDataBase() {
        return directorioDataBase;
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
