package scrabble.excepciones;

/**
 * Excepción lanzada cuando se intenta acceder a un diccionario que no existe en el sistema.
 * 
 * Esta excepción se produce cuando se intenta realizar operaciones sobre un diccionario
 * que no ha sido registrado o que ha sido eliminado del sistema. Forma parte del
 * sistema de validación de existencia de recursos.
 * 
 * Casos de uso típicos:
 * - Carga de diccionarios con nombres inexistentes
 * - Operaciones sobre diccionarios eliminados
 * - Referencias a diccionarios no inicializados
 * - Validación de palabras con diccionarios no disponibles
 * 
 * @version 2.0
 * @since 1.0
 */
public class ExceptionDiccionarioNotExist extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructor por defecto que crea la excepción con un mensaje estándar.
     * 
     * @post Se crea una instancia de la excepción con el mensaje "El diccionario no existe".
     */
    public ExceptionDiccionarioNotExist() {
        super("El diccionario no existe");
    }
    
    /**
     * Constructor que permite especificar un mensaje personalizado para la excepción.
     * 
     * @param mensaje Mensaje descriptivo específico sobre el error ocurrido.
     * @post Se crea una instancia de la excepción con el mensaje proporcionado.
     */
    public ExceptionDiccionarioNotExist(String mensaje) {
        super(mensaje);
    }
} 