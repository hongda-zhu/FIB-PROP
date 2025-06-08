# Directorio de Excepciones (`excepciones`)

## Descripción General

Este directorio (`scrabble.excepciones`) contiene las clases de excepciones personalizadas que se utilizan en toda la aplicación para manejar situaciones de error específicas del dominio. Estas excepciones heredan generalmente de `Exception` o `RuntimeException` y proporcionan información detallada sobre los problemas encontrados, facilitando un manejo de errores más robusto y específico que el uso de excepciones genéricas.

## Estructura del Directorio

### Excepciones de Usuario y Autenticación

-   **`ExceptionUserExist.java`**: Lanzada cuando se intenta crear/registrar un usuario que ya existe en el sistema.
-   **`ExceptionUserNotExist.java`**: Lanzada cuando se intenta operar con un usuario que no existe.
-   **`ExceptionUserInGame.java`**: Lanzada cuando se intenta realizar una operación con un usuario que ya está participando en una partida activa.
-   **`ExceptionUserEsIA.java`**: Lanzada al intentar realizar una operación no permitida para un jugador de tipo IA (eliminación, login, etc.).
-   **`ExceptionUserLoggedIn.java`**: Lanzada cuando se detecta un conflicto de estado de sesión de usuario (ej. login cuando ya está autenticado).

### Excepciones de Partida y Jugabilidad

-   **`ExceptionNotEnoughTiles.java`**: Lanzada cuando no hay suficientes fichas disponibles para completar una operación (robar, cambiar, validar movimientos).
-   **`ExceptionPalabraInvalida.java`**: Lanzada cuando una palabra contiene caracteres no válidos para el diccionario o no cumple las reglas de validación.
-   **`ExceptionPalabraExist.java`**: Lanzada al intentar añadir una palabra que ya existe en el diccionario durante operaciones de gestión.
-   **`ExceptionPalabraNotExist.java`**: Lanzada al intentar operar con una palabra que no existe en la estructura DAWG del diccionario.
-   **`ExceptionPersistenciaFallida.java`**: Lanzada cuando ocurren errores durante operaciones de persistencia de datos (E/O, serialización, acceso a archivos).

### Excepciones de Gestión (Diccionarios, Config.)

-   **`ExceptionDiccionarioExist.java`**: Lanzada al intentar crear/importar un diccionario con un nombre que ya existe.
-   **`ExceptionDiccionarioNotExist.java`**: Lanzada al intentar operar (eliminar, usar) con un diccionario que no existe.
-   **`ExceptionDiccionarioOperacionFallida.java`**: Lanzada cuando falla una operación específica sobre un diccionario (creación, modificación, eliminación, importación, validación).
-   **`ExceptionLoggingOperacion.java`**: Excepción especial para transmitir mensajes informativos y de logging entre capas del sistema.

### Excepciones de Ranking y Puntuaciones

-   **`ExceptionRankingOperationFailed.java`**: Lanzada cuando una operación en el sistema de ranking no se puede completar (actualización de estadísticas, cálculo de posiciones, persistencia).