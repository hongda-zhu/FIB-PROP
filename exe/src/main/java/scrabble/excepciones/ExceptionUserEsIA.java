package scrabble.excepciones;

/**
 * Excepción lanzada cuando se intenta realizar una operación no permitida con un usuario IA.
 * 
 * Esta excepción se produce cuando se intenta ejecutar operaciones que están
 * restringidas para jugadores de tipo IA, como eliminación, modificación de
 * configuraciones específicas o acciones que solo son válidas para usuarios humanos.
 * Forma parte del sistema de validación de tipos de usuario.
 * 
 * Casos de uso típicos:
 * - Intento de eliminar jugadores IA del sistema
 * - Operaciones de login con usuarios IA
 * - Modificación de configuraciones restringidas para IA
 * - Acciones de gestión no aplicables a jugadores artificiales
 * 
 * @version 2.0
 * @since 1.0
 */
public class ExceptionUserEsIA extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructor por defecto que crea la excepción con un mensaje estándar.
     * 
     * @post Se crea una instancia de la excepción con el mensaje "No se puede realizar esta operación porque el usuario es una IA".
     */
    public ExceptionUserEsIA() {
        super("No se puede realizar esta operación porque el usuario es una IA");
    }
    
    /**
     * Constructor que permite especificar un mensaje personalizado para la excepción.
     * 
     * @param mensaje Mensaje descriptivo específico sobre el error ocurrido.
     * @post Se crea una instancia de la excepción con el mensaje proporcionado.
     */
    public ExceptionUserEsIA(String mensaje) {
        super(mensaje);
    }
} 