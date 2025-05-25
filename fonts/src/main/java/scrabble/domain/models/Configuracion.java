package scrabble.domain.models;

import java.io.Serializable;

/**
 * Clase que representa la configuración de la aplicación.
 * Encapsula los datos de configuración como idioma, tema, volumen, diccionario y tamaño de tablero.
 * Proporciona una gestión centralizada de todas las preferencias del usuario y configuraciones
 * del sistema, incluyendo configuraciones de audio, visuales y de juego. Implementa Serializable
 * para permitir la persistencia de las configuraciones entre sesiones.
 * 
 * 
 * @version 1.0
 * @since 1.0
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
     * Inicializa la configuración con valores predeterminados que proporcionan
     * una experiencia de usuario estándar. Establece el idioma en español,
     * tema claro, volúmenes al 50%, y tablero de tamaño estándar (15x15).
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
     * Inicializa la configuración con los valores especificados, permitiendo
     * establecer configuraciones personalizadas desde el momento de la creación.
     * Los valores deben cumplir con las restricciones definidas por las constantes
     * de validación de la clase.
     * 
     * @param idioma Idioma inicial de la interfaz
     * @param tema Tema visual inicial (claro/oscuro)
     * @param volumen Nivel de volumen inicial (0-100)
     * @param tamano Tamaño del tablero inicial (mínimo 15)
     * @param diccionario Diccionario por defecto a utilizar
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
     * Valida que el tamaño sea al menos el mínimo permitido para garantizar
     * que el juego sea jugable. El tamaño mínimo está definido por la constante
     * TAMANO_MIN para mantener la compatibilidad con las reglas del juego.
     * 
     * @param tamano Tamaño a establecer (debe ser >= TAMANO_MIN)
     * @pre El tamaño debe ser mayor o igual a TAMANO_MIN.
     * @post El tamaño del tablero se actualiza al valor especificado.
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
     * Proporciona el tamaño de tablero que se utilizará por defecto
     * al crear nuevas partidas, a menos que se especifique otro tamaño.
     * 
     * @return Tamaño tablero por defecto actual
     * @pre No hay precondiciones específicas.
     * @post Se devuelve el tamaño de tablero configurado actualmente.
     */
    public int getTamano() {
        return tamanoTablero;
    }

    /**
     * Obtiene el diccionario actual configurado.
     * Devuelve el identificador del diccionario que se utilizará por defecto
     * en las partidas. Puede ser null si no hay diccionarios disponibles.
     * 
     * @return Diccionario actual o null si no hay diccionario configurado
     * @pre No hay precondiciones específicas.
     * @post Se devuelve el identificador del diccionario configurado o null.
     */
    public String getDiccionario() {
        return diccionario;
    }
    
    /**
     * Establece un nuevo diccionario.
     * Permite cambiar el diccionario por defecto que se utilizará en las partidas.
     * Se permite que sea null para casos donde el sistema se haya quedado sin
     * diccionarios disponibles.
     * 
     * @param diccionario Diccionario a establecer (puede ser null)
     * @pre No hay precondiciones específicas.
     * @post El diccionario configurado se actualiza al valor especificado.
     */
    public void setDiccionario(String diccionario) {
        // Se permite que el diccionario sea null para el que caso donde el sistema se haya quedado sin diccionarios
        this.diccionario = diccionario;
    }

    /**
     * Obtiene el idioma actual configurado.
     * Devuelve el idioma de la interfaz de usuario que está actualmente
     * configurado en el sistema.
     * 
     * @return Idioma actual de la interfaz
     * @pre No hay precondiciones específicas.
     * @post Se devuelve el idioma configurado actualmente.
     */
    public String getIdioma() {
        return idioma;
    }
    
    /**
     * Establece un nuevo idioma.
     * Cambia el idioma de la interfaz de usuario. El idioma debe ser uno
     * de los valores válidos definidos en IDIOMAS_VALIDOS para garantizar
     * que existe soporte de localización.
     * 
     * @param idioma Idioma a establecer (debe estar en IDIOMAS_VALIDOS)
     * @pre El idioma no debe ser null.
     * @post El idioma de la interfaz se actualiza al valor especificado.
     * @throws IllegalArgumentException si el idioma no es válido
     * @throws NullPointerException si idioma es null
     */
    public void setIdioma(String idioma) {
        if (idioma == null) {
            throw new NullPointerException("El idioma no puede ser null");
        }
        this.idioma = idioma;
    }
    
    /**
     * Obtiene el tema visual actual configurado.
     * Devuelve el tema de la interfaz (claro/oscuro) que está actualmente
     * configurado para la aplicación.
     * 
     * @return Tema visual actual
     * @pre No hay precondiciones específicas.
     * @post Se devuelve el tema configurado actualmente.
     */
    public String getTema() {
        return tema;
    }
    
    /**
     * Establece un nuevo tema visual.
     * Cambia el tema de la interfaz de usuario. El tema debe ser uno
     * de los valores válidos definidos en TEMAS_VALIDOS.
     * 
     * @param tema Tema a establecer (debe estar en TEMAS_VALIDOS)
     * @pre El tema no debe ser null.
     * @post El tema de la interfaz se actualiza al valor especificado.
     * @throws IllegalArgumentException si el tema no es válido
     * @throws NullPointerException si tema es null
     */
    public void setTema(String tema) {
        if (tema == null) {
            throw new NullPointerException("El tema no puede ser null");
        }
        this.tema = tema;
    }
    
    /**
     * Obtiene si la música está activada.
     * Indica si la reproducción de música de fondo está habilitada
     * en la configuración actual del usuario.
     * 
     * @return true si la música está activada, false en caso contrario
     * @pre No hay precondiciones específicas.
     * @post Se devuelve el estado actual de activación de la música.
     */
    public boolean isMusica() {
        return musica;
    }

    /**
     * Activa o desactiva la música.
     * Permite al usuario habilitar o deshabilitar la reproducción
     * de música de fondo en la aplicación.
     * 
     * @param musica true para activar, false para desactivar
     * @pre No hay precondiciones específicas.
     * @post El estado de activación de la música se actualiza al valor especificado.
     */
    public void setMusica(boolean musica) {
        this.musica = musica;
    }

    /**
     * Obtiene el volumen de la música.
     * Devuelve el nivel de volumen configurado para la música de fondo,
     * en una escala de VOLUMEN_MIN a VOLUMEN_MAX.
     * 
     * @return Volumen de la música (0-100)
     * @pre No hay precondiciones específicas.
     * @post Se devuelve el volumen de música configurado actualmente.
     */
    public int getVolumenMusica() {
        return volumenMusica;
    }

    /**
     * Establece el volumen de la música.
     * Configura el nivel de volumen para la música de fondo.
     * El valor debe estar dentro del rango válido definido por
     * VOLUMEN_MIN y VOLUMEN_MAX.
     * 
     * @param volumenMusica Volumen a establecer (0-100)
     * @pre El volumen debe estar entre VOLUMEN_MIN y VOLUMEN_MAX.
     * @post El volumen de la música se actualiza al valor especificado.
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
     * Indica si la reproducción de efectos de sonido está habilitada
     * en la configuración actual del usuario.
     * 
     * @return true si el sonido está activado, false en caso contrario
     * @pre No hay precondiciones específicas.
     * @post Se devuelve el estado actual de activación del sonido.
     */
    public boolean isSonido() {
        return sonido;
    }

    /**
     * Activa o desactiva el sonido.
     * Permite al usuario habilitar o deshabilitar la reproducción
     * de efectos de sonido en la aplicación.
     * 
     * @param sonido true para activar, false para desactivar
     * @pre No hay precondiciones específicas.
     * @post El estado de activación del sonido se actualiza al valor especificado.
     */
    public void setSonido(boolean sonido) {
        this.sonido = sonido;
    }

    /**
     * Obtiene el volumen del sonido.
     * Devuelve el nivel de volumen configurado para los efectos de sonido,
     * en una escala de VOLUMEN_MIN a VOLUMEN_MAX.
     * 
     * @return Volumen del sonido (0-100)
     * @pre No hay precondiciones específicas.
     * @post Se devuelve el volumen de sonido configurado actualmente.
     */
    public int getVolumenSonido() {
        return volumenSonido;
    }

    /**
     * Establece el volumen del sonido.
     * Configura el nivel de volumen para los efectos de sonido.
     * El valor debe estar dentro del rango válido definido por
     * VOLUMEN_MIN y VOLUMEN_MAX.
     * 
     * @param volumenSonido Volumen a establecer (0-100)
     * @pre El volumen debe estar entre VOLUMEN_MIN y VOLUMEN_MAX.
     * @post El volumen del sonido se actualiza al valor especificado.
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