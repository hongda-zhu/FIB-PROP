package scrabble.domain.models;

import java.io.Serializable;

/**
 * Clase que representa la configuración de la aplicación.
 * Encapsula los datos de configuración como idioma, tema, volumen, diccionario y tamaño de tablero.
 */
public class Configuracion implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // Valores por defecto
    private static final String IDIOMA_DEFAULT = "ESPAÑOL";
    private static final String DICCIONARIO_DEFAULT = "ESP";
    private static final String TEMA_DEFAULT = "CLARO";
    private static final int VOLUMEN_DEFAULT = 50;
    private static final int TAMANO_DEFAULT = 15;
    private static final boolean MUSICA_DEFAULT = true;
    private static final boolean SONIDO_DEFAULT = true;
    // Opciones válidas
    public static final String[] IDIOMAS_VALIDOS = {"ESPAÑOL", "INGLÉS"};
    public static final String[] TEMAS_VALIDOS = {"CLARO", "OSCURO"};
    public static final int VOLUMEN_MIN = 0;
    public static final int VOLUMEN_MAX = 100;
    public static final int TAMANO_MIN = 15;
    // Atributos
    private String idioma;
    private String tema;
    private String diccionario;
    private boolean musica;
    private int volumenMusica;
    private boolean sonido;
    private int volumenSonido;
    private int tamanoTablero;


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
        this.tamanoTablero = TAMANO_DEFAULT;
        this.diccionario = DICCIONARIO_DEFAULT;
        this.musica = MUSICA_DEFAULT;
        this.sonido = SONIDO_DEFAULT;
        this.volumenMusica = VOLUMEN_DEFAULT;
        this.volumenSonido = VOLUMEN_DEFAULT;
    }
    
    /**
     * Constructor con parámetros.
     * Inicializa la configuración con los valores especificados.
     * 
     * @param idioma Idioma inicial
     * @param tema Tema visual inicial
     * @param volumen Nivel de volumen inicial
     * @param tamano Tamaño del tablero inicial
     * @param diccionario Tamaño del tablero inicial
     * @pre Los valores deben ser válidos según las constantes definidas.
     * @post Se crea una nueva configuración con los valores especificados.
     * @throws IllegalArgumentException si alguno de los valores no es válido
     */
    public Configuracion(String idioma, String tema, int volumen, int tamano, String diccionario) {
        setIdioma(idioma);
        setTema(tema);
        setTamano(tamano);
        setDiccionario(diccionario);
    }
    
    /**
     * Establece un nuevo tamaño de tablero por defecto.
     * 
     * @param tamano Tamaño a establecer
     * @throws IllegalArgumentException si el tamano está fuera del rango permitido
     */    
    public void setTamano(int tamano) {
        if (tamano < TAMANO_MIN) {
            throw new IllegalArgumentException("El tamaño del tablero no puede ser menor que 15");
        }
        this.tamanoTablero = tamano;
    }

    /**
     * Obtiene el tamaño por defecto actual configurado.
     * 
     * @return Tamaño tablero por defecto actual
     */
    public int getTamano() {
        return tamanoTablero;
    }

    /**
     * Obtiene el diccionario actual configurado.
     * 
     * @return Diccionario actual
     */
    public String getDiccionario() {
        return diccionario;
    }
    
    /**
     * Establece un nuevo diccionario.
     * 
     * @param diccionario Diccionario a establecer
     */
    public void setDiccionario(String diccionario) {
        // Se permite que el diccionario sea null para el que caso donde el sistema se haya quedado sin diccionarios
        this.diccionario = diccionario;
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
        this.tema = tema;
    }
    
    /**
     * Obtiene el volumen actual configurado.
     * 
     * @return Volumen actual
     */
    // public int getVolumen() {
    //     return volumen;
    // }
    
    /**
     * Establece un nuevo nivel de volumen.
     * 
     * @param volumen Volumen a establecer
     * @throws IllegalArgumentException si el volumen está fuera del rango permitido
     */
    // public void setVolumen(int volumen) {
    //     if (volumen < VOLUMEN_MIN || volumen > VOLUMEN_MAX) {
    //         throw new IllegalArgumentException("El volumen debe estar entre " + 
    //                                            VOLUMEN_MIN + " y " + VOLUMEN_MAX);
    //     }
    //     this.volumen = volumen;
    // }

    /**
     * Obtiene si la música está activada.
     * 
     * @return true si la música está activada, false en caso contrario
     */
    public boolean isMusica() {
        return musica;
    }

    /**
     * Activa o desactiva la música.
     * 
     * @param musica true para activar, false para desactivar
     */
    public void setMusica(boolean musica) {
        this.musica = musica;
    }

    /**
     * Obtiene el volumen de la música.
     * 
     * @return Volumen de la música
     */
    public int getVolumenMusica() {
        return volumenMusica;
    }

    /**
     * Establece el volumen de la música.
     * 
     * @param volumenMusica Volumen a establecer
     * @throws IllegalArgumentException si el volumen está fuera del rango permitido
     */
    public void setVolumenMusica(int volumenMusica) {
        if (volumenMusica < VOLUMEN_MIN || volumenMusica > VOLUMEN_MAX) {
            throw new IllegalArgumentException("El volumen de la música debe estar entre " + 
                                               VOLUMEN_MIN + " y " + VOLUMEN_MAX);
        }
        this.volumenMusica = volumenMusica;
    }

    /**
     * Obtiene si el sonido está activado.
     * 
     * @return true si el sonido está activado, false en caso contrario
     */
    public boolean isSonido() {
        return sonido;
    }

    /**
     * Activa o desactiva el sonido.
     * 
     * @param sonido true para activar, false para desactivar
     */
    public void setSonido(boolean sonido) {
        this.sonido = sonido;
    }

    /**
     * Obtiene el volumen del sonido.
     * 
     * @return Volumen del sonido
     */
    public int getVolumenSonido() {
        return volumenSonido;
    }

    /**
     * Establece el volumen del sonido.
     * 
     * @param volumenSonido Volumen a establecer
     * @throws IllegalArgumentException si el volumen está fuera del rango permitido
     */
    public void setVolumenSonido(int volumenSonido) {
        if (volumenSonido < VOLUMEN_MIN || volumenSonido > VOLUMEN_MAX) {
            throw new IllegalArgumentException("El volumen del sonido debe estar entre " + 
                                               VOLUMEN_MIN + " y " + VOLUMEN_MAX);
        }
        this.volumenSonido = volumenSonido;
    }
}