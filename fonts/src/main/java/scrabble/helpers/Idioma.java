package scrabble.helpers;

/**
 * Enumeración que define los idiomas disponibles para la interfaz de la aplicación.
 * 
 * Esta enumeración especifica los idiomas soportados para la localización
 * de la interfaz de usuario, permitiendo a los usuarios seleccionar su
 * idioma preferido para los menús, mensajes y elementos de la interfaz.
 * 
 * Idiomas disponibles:
 * - ESPANOL: Interfaz en español
 * - CATALAN: Interfaz en catalán  
 * - INGLES: Interfaz en inglés
 * 
 * Se utiliza en la configuración del sistema para determinar qué textos
 * mostrar en la interfaz de usuario y en los mensajes del sistema.
 * 
 * @version 2.0
 * @since 1.0
 */
public enum Idioma {
    /** 
     * Idioma español para la interfaz de usuario.
     * Textos y mensajes se muestran en español.
     */
    ESPANOL,
    
    /** 
     * Idioma catalán para la interfaz de usuario.
     * Textos y mensajes se muestran en catalán.
     */
    CATALAN,
    
    /** 
     * Idioma inglés para la interfaz de usuario.
     * Textos y mensajes se muestran en inglés.
     */
    INGLES
}