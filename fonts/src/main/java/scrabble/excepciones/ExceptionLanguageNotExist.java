package scrabble.excepciones;

/**
 * Excepción lanzada cuando se intenta utilizar un idioma que no existe en el sistema.
 * 
 * Esta excepción se produce cuando se hace referencia a un idioma no soportado
 * por la aplicación o cuando se intenta configurar un idioma que no está disponible
 * en las opciones del sistema. Forma parte del sistema de validación de configuración.
 * 
 * Casos de uso típicos:
 * - Configuración de idioma con valores no válidos
 * - Referencias a idiomas no implementados
 * - Carga de configuraciones con idiomas obsoletos
 * - Validación de parámetros de localización
 * 
 * @version 2.0
 * @since 1.0
 */
public class ExceptionLanguageNotExist extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructor por defecto que crea la excepción con un mensaje estándar.
     * 
     * @post Se crea una instancia de la excepción con el mensaje "El idioma no existe".
     */
    public ExceptionLanguageNotExist() {
        super("El idioma no existe");
    }
    
    /**
     * Constructor que permite especificar un mensaje personalizado para la excepción.
     * 
     * @param mensaje Mensaje descriptivo específico sobre el error ocurrido.
     * @post Se crea una instancia de la excepción con el mensaje proporcionado.
     */
    public ExceptionLanguageNotExist(String mensaje) {
        super(mensaje);
    }
} 