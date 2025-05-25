package scrabble.excepciones;

/**
 * Excepción lanzada cuando se intenta crear un diccionario que ya existe en el sistema.
 * 
 * Esta excepción se produce cuando se intenta registrar o crear un diccionario
 * con un nombre que ya está siendo utilizado por otro diccionario existente.
 * Forma parte del sistema de validación de integridad de diccionarios.
 * 
 * Casos de uso típicos:
 * - Creación de diccionarios con nombres duplicados
 * - Importación de diccionarios con nombres conflictivos
 * - Operaciones de registro que violan la unicidad de nombres
 * 
 * @version 2.0
 * @since 1.0
 */
public class ExceptionDiccionarioExist extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructor por defecto que crea la excepción con un mensaje estándar.
     * 
     * @post Se crea una instancia de la excepción con el mensaje "El diccionario ya existe".
     */
    public ExceptionDiccionarioExist() {
        super("El diccionario ya existe");
    }
    
    /**
     * Constructor que permite especificar un mensaje personalizado para la excepción.
     * 
     * @param mensaje Mensaje descriptivo específico sobre el error ocurrido.
     * @post Se crea una instancia de la excepción con el mensaje proporcionado.
     */
    public ExceptionDiccionarioExist(String mensaje) {
        super(mensaje);
    }
} 