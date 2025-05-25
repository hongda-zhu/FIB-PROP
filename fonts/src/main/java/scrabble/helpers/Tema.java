package scrabble.helpers;

/**
 * Enumeración que define los temas visuales disponibles para la aplicación.
 * 
 * Esta enumeración especifica los esquemas de colores y estilos visuales
 * que pueden aplicarse a la interfaz de usuario, permitiendo a los usuarios
 * personalizar la apariencia de la aplicación según sus preferencias.
 * 
 * Temas disponibles:
 * - CLARO: Tema con colores claros y fondo blanco
 * - OSCURO: Tema con colores oscuros y fondo negro
 * 
 * Se utiliza en la configuración del sistema para determinar qué esquema
 * de colores aplicar a todos los elementos de la interfaz de usuario.
 * 
 * @version 2.0
 * @since 1.0
 */
public enum Tema {
    /** 
     * Tema claro con fondo blanco y colores claros.
     * Proporciona una interfaz brillante y tradicional.
     */
    CLARO,
    
    /** 
     * Tema oscuro con fondo negro y colores oscuros.
     * Reduce la fatiga visual en condiciones de poca luz.
     */
    OSCURO
}