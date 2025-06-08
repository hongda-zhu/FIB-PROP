package scrabble.excepciones;

/**
 * Excepción que se lanza cuando falla una operación específica sobre un diccionario.
 * 
 * Esta excepción encapsula errores que pueden ocurrir durante operaciones complejas
 * de gestión de diccionarios, como creación, modificación, eliminación, importación
 * o validación. Proporciona información detallada sobre el tipo de operación que falló
 * para facilitar el diagnóstico y manejo de errores.
 * 
 * Casos de uso típicos:
 * - Errores durante la creación de diccionarios
 * - Fallos en la importación de archivos de diccionario
 * - Problemas en la modificación de palabras
 * - Errores de validación de estructura DAWG
 * - Fallos en operaciones de eliminación
 * 
 * @version 2.0
 * @since 1.0
 */
public class ExceptionDiccionarioOperacionFallida extends Exception {
    private static final long serialVersionUID = 1L;
    private final String tipoOperacion;

    /**
     * Constructor por defecto que establece un mensaje predeterminado.
     * 
     * @post Se crea una instancia de la excepción con un mensaje genérico y tipo de operación "desconocida".
     */
    public ExceptionDiccionarioOperacionFallida() {
        super("Falló una operación en el diccionario");
        this.tipoOperacion = "desconocida";
    }

    /**
     * Constructor que permite establecer un mensaje personalizado.
     * 
     * @param message Mensaje de error personalizado que describe el problema específico.
     * @post Se crea una instancia de la excepción con el mensaje proporcionado y tipo de operación "desconocida".
     */
    public ExceptionDiccionarioOperacionFallida(String message) {
        super(message);
        this.tipoOperacion = "desconocida";
    }

    /**
     * Constructor completo que permite establecer un mensaje personalizado y el tipo de operación.
     * 
     * @param message Mensaje de error personalizado que describe el problema específico.
     * @param tipoOperacion Tipo de operación que falló (crear, eliminar, modificar, importar, validar, etc.).
     * @post Se crea una instancia de la excepción con el mensaje y tipo de operación proporcionados.
     */
    public ExceptionDiccionarioOperacionFallida(String message, String tipoOperacion) {
        super(message);
        this.tipoOperacion = tipoOperacion;
    }
    
    /**
     * Obtiene el tipo de operación que falló.
     * 
     * @return El tipo de operación que causó la excepción, o "desconocida" si no se especificó.
     * @post Se devuelve una cadena que identifica el tipo de operación fallida.
     */
    public String getTipoOperacion() {
        return tipoOperacion;
    }
} 