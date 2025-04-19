package scrabble.excepciones;


/**
 * Excepción lanzada cuando se intenta cambiar parámetros (nombre/contraseña) de un jugador tipo IA,
 * o cuando se intenta iniciar sesión como IA.
 */
public class ExceptionUserEsIA extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public ExceptionUserEsIA() {
        super("El usuario es IA");
    }
    
    public ExceptionUserEsIA(String mensaje) {
        super(mensaje);
    }
} 