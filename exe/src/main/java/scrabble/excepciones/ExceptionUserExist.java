package scrabble.excepciones;

/**
 * Excepción lanzada cuando se intenta crear un usuario que ya existe en el sistema.
 * 
 * Esta excepción se produce cuando se intenta registrar un nuevo usuario
 * con un nombre que ya está siendo utilizado por otro usuario existente.
 * Forma parte del sistema de validación de unicidad de usuarios.
 * 
 * Casos de uso típicos:
 * - Registro de usuarios con nombres duplicados
 * - Creación de jugadores con identificadores conflictivos
 * - Validación de unicidad durante operaciones de alta
 * - Control de integridad en la gestión de usuarios
 * 
 * @version 2.0
 * @since 1.0
 */
public class ExceptionUserExist extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructor por defecto que crea la excepción con un mensaje estándar.
     * 
     * @post Se crea una instancia de la excepción con el mensaje "El usuario ya existe".
     */
    public ExceptionUserExist() {
        super("El usuario ya existe");
    }
    
    /**
     * Constructor que permite especificar un mensaje personalizado para la excepción.
     * 
     * @param mensaje Mensaje descriptivo específico sobre el error ocurrido.
     * @post Se crea una instancia de la excepción con el mensaje proporcionado.
     */
    public ExceptionUserExist(String mensaje) {
        super(mensaje);
    }
} 