package scrabble.excepciones;

/**
 * Excepción lanzada cuando se intenta acceder a un usuario que no existe en el sistema.
 * 
 * Esta excepción se produce cuando se realizan operaciones sobre un jugador
 * que no ha sido registrado en el sistema o que ha sido eliminado. Forma parte
 * del sistema de validación de existencia de usuarios.
 * 
 * Casos de uso típicos:
 * - Operaciones de login con usuarios inexistentes
 * - Búsqueda de jugadores no registrados
 * - Acceso a estadísticas de usuarios eliminados
 * - Referencias a jugadores en partidas con usuarios no válidos
 * 
 * @version 2.0
 * @since 1.0
 */
public class ExceptionUserNotExist extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructor por defecto que crea la excepción con un mensaje estándar.
     * 
     * @post Se crea una instancia de la excepción con el mensaje "El usuario no existe".
     */
    public ExceptionUserNotExist() {
        super("El usuario no existe");
    }
    
    /**
     * Constructor que permite especificar un mensaje personalizado para la excepción.
     * 
     * @param mensaje Mensaje descriptivo específico sobre el error ocurrido.
     * @post Se crea una instancia de la excepción con el mensaje proporcionado.
     */
    public ExceptionUserNotExist(String mensaje) {
        super(mensaje);
    }
} 