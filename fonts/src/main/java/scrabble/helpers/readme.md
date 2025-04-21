# Directorio de Helpers

## Descripción General

Este directorio contiene clases auxiliares que proporcionan funcionalidades de soporte utilizadas en todo el proyecto. Estas clases implementan estructuras de datos genéricas y definiciones de constantes que mejoran la modularidad y mantenibilidad del código.

## Estructura del Directorio

### Clases de Datos

- **Tuple.java**  
  Implementa una estructura de datos genérica para almacenar pares de valores. Se utiliza principalmente para representar coordenadas en el tablero y otras asociaciones de pares de valores.

- **Triple.java**  
  Extiende el concepto de Tuple para almacenar tres valores relacionados. Se usa principalmente en el gestor de jugadas para representar movimientos (palabra, posición, dirección).
  
### Enumeraciones

- **Idioma.java**  
  Define constantes para los diferentes idiomas soportados en el juego, facilitando la gestión de diccionarios y configuraciones lingüísticas.

- **Tema.java**  
  Define constantes para los diferentes temas o categorías de palabras que pueden utilizarse en el juego.

- **Dificultad.java**  
  Enumera los niveles de dificultad disponibles para los jugadores IA.

