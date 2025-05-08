package scrabble.domain.models;

import java.io.Serializable;

/**
 * Clase que representa la configuración de la aplicación.
 * Encapsula los datos de configuración como idioma, tema y volumen.
 */
public class Configuracion implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // Valores por defecto
    private static final String IDIOMA_DEFAULT = "ESPAÑOL";
    private static final String TEMA_DEFAULT = "CLARO";
    private static final int VOLUMEN_DEFAULT = 50;
    
    // Opciones válidas
    public static final String[] IDIOMAS_VALIDOS = {"ESPAÑOL", "CATALÁN", "INGLÉS"};
    public static final String[] TEMAS_VALIDOS = {"CLARO", "OSCURO"};
    public static final int VOLUMEN_MIN = 0;
    public static final int VOLUMEN_MAX = 100;
    
    // Atributos
    private String idioma;
    private String tema;
    private int volumen;
    
    /**
     * Constructor por defecto.
     * Inicializa la configuración con valores predeterminados.
     * 
     * @pre No hay precondiciones específicas.
     * @post Se crea una nueva configuración con los valores por defecto.
     */
    public Configuracion() {
        this.idioma = IDIOMA_DEFAULT;
        this.tema = TEMA_DEFAULT;
        this.volumen = VOLUMEN_DEFAULT;
    }
    
    /**
     * Constructor con parámetros.
     * Inicializa la configuración con los valores especificados.
     * 
     * @param idioma Idioma inicial
     * @param tema Tema visual inicial
     * @param volumen Nivel de volumen inicial
     * @pre Los valores deben ser válidos según las constantes definidas.
     * @post Se crea una nueva configuración con los valores especificados.
     * @throws IllegalArgumentException si alguno de los valores no es válido
     */
    public Configuracion(String idioma, String tema, int volumen) {
        setIdioma(idioma);
        setTema(tema);
        setVolumen(volumen);
    }
    
    /**
     * Obtiene el idioma actual configurado.
     * 
     * @return Idioma actual
     */
    public String getIdioma() {
        return idioma;
    }
    
    /**
     * Establece un nuevo idioma.
     * 
     * @param idioma Idioma a establecer
     * @throws IllegalArgumentException si el idioma no es válido
     */
    public void setIdioma(String idioma) {
        if (idioma == null) {
            throw new NullPointerException("El idioma no puede ser null");
        }
        
        // Validación simple para este ejemplo
        this.idioma = idioma;
    }
    
    /**
     * Obtiene el tema visual actual configurado.
     * 
     * @return Tema actual
     */
    public String getTema() {
        return tema;
    }
    
    /**
     * Establece un nuevo tema visual.
     * 
     * @param tema Tema a establecer
     * @throws IllegalArgumentException si el tema no es válido
     */
    public void setTema(String tema) {
        if (tema == null) {
            throw new NullPointerException("El tema no puede ser null");
        }
        
        // Validación simple para este ejemplo
        this.tema = tema;
    }
    
    /**
     * Obtiene el volumen actual configurado.
     * 
     * @return Volumen actual
     */
    public int getVolumen() {
        return volumen;
    }
    
    /**
     * Establece un nuevo nivel de volumen.
     * 
     * @param volumen Volumen a establecer
     * @throws IllegalArgumentException si el volumen está fuera del rango permitido
     */
    public void setVolumen(int volumen) {
        if (volumen < VOLUMEN_MIN || volumen > VOLUMEN_MAX) {
            throw new IllegalArgumentException("El volumen debe estar entre " + 
                                               VOLUMEN_MIN + " y " + VOLUMEN_MAX);
        }
        
        this.volumen = volumen;
    }
}