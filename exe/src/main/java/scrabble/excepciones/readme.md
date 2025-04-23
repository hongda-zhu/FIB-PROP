# Directorio de Excepciones (`excepciones`)

## Descripción General

Este directorio (`scrabble.excepciones`) contiene las clases de excepciones personalizadas que se utilizan en toda la aplicación para manejar situaciones de error específicas del dominio. Estas excepciones heredan generalmente de `Exception` o `RuntimeException` y proporcionan información detallada sobre los problemas encontrados, facilitando un manejo de errores más robusto y específico que el uso de excepciones genéricas.

## Estructura del Directorio

### Excepciones de Usuario y Autenticación

-   **`ExceptionUserExist.java`**: Lanzada cuando se intenta crear/registrar un usuario que ya existe.
-   **`ExceptionUserNotExist.java`**: Lanzada cuando se intenta operar con un usuario que no existe.
-   **`ExceptionUserInGame.java`**: Lanzada cuando se intenta realizar una operación incompatible con un usuario que ya está en una partida (potencialmente para futuras funcionalidades).
-   **`ExceptionUserEsIA.java`**: Lanzada al intentar realizar una operación no permitida para un jugador de tipo IA (ej. intentar loguear una IA).

### Excepciones de Partida y Jugabilidad

-   **`ExceptionNotEnoughTiles.java`**: Lanzada cuando la bolsa no tiene suficientes fichas para una operación (robar, cambiar).
-   **`ExceptionPalabraInvalida.java`**: Lanzada cuando una palabra no es válida según las reglas del juego (no en diccionario, mala colocación, etc.). Es una excepción clave en la jugabilidad.
-   **`ExceptionPalabraExist.java`**: Lanzada al intentar añadir una palabra que ya existe (ej. en la gestión de diccionarios).
-   **`ExceptionPalabraNotExist.java`**: Lanzada al intentar operar con una palabra que no existe (ej. eliminarla de un diccionario).
-   **`ExceptionPalabraVacia.java`**: Lanzada si se intenta validar o colocar una palabra vacía.

### Excepciones de Gestión (Diccionarios, Idiomas, Config.)

-   **`ExceptionDiccionarioExist.java`**: Lanzada al intentar crear/importar un diccionario con un nombre que ya existe.
-   **`ExceptionDiccionarioNotExist.java`**: Lanzada al intentar operar (eliminar, usar) con un diccionario que no existe.
-   **`ExceptionLanguageNotExist.java`**: Lanzada al intentar usar/referenciar un idioma no existente.

### Excepciones de Ranking y Puntuaciones

-   **`ExceptionRankingOperationFailed.java`**: Lanzada cuando una operación general sobre el ranking (ej. guardado, carga) falla.
-   **`ExceptionInvalidScore.java`**: Lanzada si se intenta registrar o usar una puntuación con un valor inválido (ej. negativo donde no debe serlo).