package scrabble.excepciones;

/**
 * Excepción lanzada cuando se intenta crear una partida que ya existe.
 */
public class ExceptionLanguageExist extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public ExceptionLanguageExist() {
        super("El idioma ya existe");
    }
    
    public ExceptionLanguageExist(String mensaje) {
        super(mensaje);
    }
} 