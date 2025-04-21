package scrabble.excepciones;

/**
 * Excepción lanzada cuando se intenta crear una partida que ya existe.
 */
public class ExceptionLanguageNotExist extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public ExceptionLanguageNotExist() {
        super("El idioma no existe");
    }
    
    public ExceptionLanguageNotExist(String mensaje) {
        super(mensaje);
    }
} 