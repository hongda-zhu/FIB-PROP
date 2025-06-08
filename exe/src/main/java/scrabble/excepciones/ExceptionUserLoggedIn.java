package scrabble.excepciones;

/**
 * Excepción lanzada cuando se detecta un conflicto de estado de sesión de usuario.
 * 
 * Esta excepción se produce cuando se intenta realizar una operación que entra
 * en conflicto con el estado actual de la sesión del usuario, como intentar
 * hacer login cuando ya está logueado o realizar operaciones que requieren
 * un estado específico de autenticación.
 * 
 * Casos de uso típicos:
 * - Intento de login cuando el usuario ya está autenticado
 * - Operaciones que requieren logout previo
 * - Conflictos de estado en la gestión de sesiones
 * - Validación de estados de autenticación
 * 
 * @version 2.0
 * @since 1.0
 */
public class ExceptionUserLoggedIn extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructor por defecto que crea la excepción con un mensaje estándar.
     * 
     * @post Se crea una instancia de la excepción con el mensaje "El usuario ha iniciado sesión".
     */
    public ExceptionUserLoggedIn() {
        super("El usuario ha iniciado sesión");
    }
    
    /**
     * Constructor que permite especificar un mensaje personalizado para la excepción.
     * 
     * @param mensaje Mensaje descriptivo específico sobre el error ocurrido.
     * @post Se crea una instancia de la excepción con el mensaje proporcionado.
     */
    public ExceptionUserLoggedIn(String mensaje) {
        super(mensaje);
    }
} 