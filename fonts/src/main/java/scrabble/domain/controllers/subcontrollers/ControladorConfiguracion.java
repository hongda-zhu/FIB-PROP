package scrabble.domain.controllers.subcontrollers;

import scrabble.domain.models.Configuracion;
import scrabble.domain.persistences.interfaces.RepositorioConfiguracion;
import scrabble.domain.persistences.implementaciones.RepositorioConfiguracionImpl;
import scrabble.excepciones.ExceptionPersistenciaFallida;

/**
 * Controlador para la gestión de la configuración de la aplicación.
 * Encapsula el acceso a los parámetros configurables como idioma, tema y volumen.
 */
public class ControladorConfiguracion {
    /**
     * Repositorio para manejar la persistencia de la configuración.
     */
    private RepositorioConfiguracion repositorio;

    /**
     * Constructor para la clase ControladorConfiguracion.
     * Inicializa una nueva instancia de Configuracion y carga los valores guardados si existen.
     * 
     * @pre No hay precondiciones específicas.
     * @post Se crea una nueva instancia de ControladorConfiguracion con una 
     *       configuración inicializada con valores por defecto o cargada desde persistencia.
     */
    public ControladorConfiguracion() {
        this(new RepositorioConfiguracionImpl());
    }
    
    /**
     * Constructor para la clase ControladorConfiguracion con inyección de repositorio.
     * Permite especificar el repositorio a utilizar para pruebas o diferentes implementaciones.
     * 
     * @param repositorio El repositorio a utilizar para la persistencia
     * @pre El repositorio no debe ser null.
     * @post Se crea una nueva instancia de ControladorConfiguracion con una 
     *       configuración inicializada con valores cargados desde el repositorio especificado.
     * @throws NullPointerException si el repositorio es null
     */
    public ControladorConfiguracion(RepositorioConfiguracion repositorio) {
        if (repositorio == null) {
            throw new NullPointerException("El repositorio no puede ser null");
        }
        this.repositorio = repositorio;
    }

    /**
     * Obtiene el idioma actual configurado en la aplicación.
     * 
     * @pre No hay precondiciones específicas.
     * @return Idioma actual
     * @post Se devuelve un String que representa el idioma actual configurado en la aplicación.
     */
    public String obteneridioma() {
        return this.repositorio.cargar().getIdioma();
    }

    /**
     * Obtiene el tema visual actual configurado en la aplicación.
     * 
     * @pre No hay precondiciones específicas.
     * @return Tema actual
     * @post Se devuelve un String que representa el tema visual actual configurado en la aplicación.
     */
    public String obtenerTema() {
        return this.repositorio.cargar().getTema();
    }

    /**
     * Obtiene el volumen actual configurado en la aplicación.
     * 
     * @pre No hay precondiciones específicas.
     * @return Volumen actual
     * @post Se devuelve un entero que representa el nivel de volumen actual configurado en la aplicación.
     */
    public int obtenerVolumen() {
        return this.repositorio.cargar().getVolumen();
    }

    /**
     * Establece un nuevo idioma para la configuración.
     * 
     * @pre El idioma debe ser una de las opciones válidas.
     * @param i Idioma a establecer
     * @post El idioma de la configuración se actualiza al valor especificado y se guarda la configuración.
     * @throws IllegalArgumentException si el idioma no es uno de los valores permitidos
     * @throws NullPointerException si el parámetro i es null
     */
    public void setIdioma(String i) throws ExceptionPersistenciaFallida {
        Configuracion configuracion = repositorio.cargar();
        configuracion.setIdioma(i);
        repositorio.guardar(configuracion);
    }

    /**
     * Establece un nuevo tema visual para la configuración.
     * 
     * @pre El tema debe ser una de las opciones válidas.
     * @param t Tema a establecer
     * @post El tema de la configuración se actualiza al valor especificado y se guarda la configuración.
     * @throws IllegalArgumentException si el tema no es uno de los valores permitidos
     * @throws NullPointerException si el parámetro t es null
     */
    public void setTema(String t) throws ExceptionPersistenciaFallida {
        Configuracion configuracion = repositorio.cargar();
        configuracion.setTema(t);
        repositorio.guardar(configuracion);
    }

    /**
     * Establece un nuevo nivel de volumen para la configuración.
     * 
     * @pre El volumen debe estar en el rango permitido.
     * @param v Volumen a establecer
     * @post El nivel de volumen de la configuración se actualiza al valor especificado y se guarda la configuración.
     * @throws IllegalArgumentException si el volumen está fuera del rango permitido
     */
    public void setVolumen(int v) throws ExceptionPersistenciaFallida {
        Configuracion configuracion = repositorio.cargar();
        configuracion.setVolumen(v);
        repositorio.guardar(configuracion);
    }
    
    /**
     * Carga la configuración desde el repositorio.
     */
    public boolean cargarConfiguracion() throws ExceptionPersistenciaFallida {
        return repositorio.cargar() != null;
    }
}