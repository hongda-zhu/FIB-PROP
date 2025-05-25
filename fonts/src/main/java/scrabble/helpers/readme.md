# Directorio de Helpers (`helpers`)

## Descripción General

Este directorio (`scrabble.helpers`) contiene clases auxiliares y estructuras de datos genéricas que proporcionan funcionalidades de soporte utilizadas en todo el proyecto Scrabble. Estas clases implementan estructuras de datos reutilizables, enumeraciones de constantes del dominio y utilidades que mejoran la modularidad, legibilidad y mantenibilidad del código.

Las clases helper siguen principios de diseño orientado a objetos y proporcionan abstracciones limpias para conceptos comunes del juego, facilitando el desarrollo y mantenimiento del sistema.

## Estructura del Directorio

### Clases de Estructuras de Datos

- **`Tuple.java`**  
  Implementa una tupla genérica inmutable para almacenar pares de valores relacionados. Se utiliza extensivamente para representar coordenadas en el tablero (fila, columna), pares de resultados y asociaciones clave-valor. Proporciona implementaciones correctas de equals() y hashCode() para uso en colecciones.

- **`Triple.java`**  
  Implementa una tupla genérica mutable para almacenar tres valores relacionados. Es fundamental en el sistema de jugadas para representar movimientos completos (palabra, posición, dirección). Incluye métodos de conveniencia para copia de valores y es serializable para persistencia.
  
### Enumeraciones del Dominio

- **`Idioma.java`**  
  Define los idiomas soportados para la interfaz de usuario (ESPANOL, CATALAN, INGLES). Se utiliza en la configuración del sistema para determinar la localización de textos y mensajes.

- **`Tema.java`**  
  Define los temas visuales disponibles para la aplicación (CLARO, OSCURO). Permite a los usuarios personalizar la apariencia de la interfaz según sus preferencias.

- **`Dificultad.java`**  
  Enumera los niveles de dificultad para jugadores IA (FACIL, DIFICIL). Afecta directamente a la estrategia de selección de movimientos y la calidad de las jugadas de la inteligencia artificial.

- **`Direction.java`**  
  Define las direcciones posibles para colocar palabras en el tablero (HORIZONTAL, VERTICAL). Se utiliza en la validación de movimientos, búsqueda de jugadas válidas y cálculo de puntuaciones.

- **`TipoCasilla.java`**  
  Especifica los tipos de casillas especiales en el tablero de Scrabble (NORMAL, CENTRO, LETRA_DOBLE, LETRA_TRIPLE, PALABRA_DOBLE, PALABRA_TRIPLE). Cada tipo tiene efectos específicos sobre la puntuación según las reglas oficiales del juego.

## Características Técnicas

- **Genericidad:** Las estructuras de datos son genéricas y reutilizables para diferentes tipos
- **Inmutabilidad:** Tuple es inmutable para garantizar la integridad de los datos
- **Serialización:** Soporte completo para persistencia y transmisión de datos
- **Documentación:** JavaDocs completos con ejemplos de uso y precondiciones/postcondiciones
- **Consistencia:** Implementaciones correctas de equals(), hashCode() y toString() donde corresponde

