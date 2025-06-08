# Directorio de Implementaciones de Persistencia (`implementaciones`)

## Descripción General

Este directorio (`scrabble.domain.persistences.implementaciones`) contiene las clases que proporcionan las implementaciones concretas para las interfaces de repositorio definidas en la carpeta `interfaces/`. Cada clase aquí es responsable de la lógica específica para guardar y cargar datos para una entidad particular del dominio, utilizando mecanismos de persistencia específicos.

En la implementación actual, la mayoría de estas clases utilizan la **serialización de objetos Java** para almacenar datos en archivos binarios (normalmente con extensión `.dat`) y, en el caso de los diccionarios, también gestionan archivos de texto directamente. Todas las implementaciones siguen el patrón Repository y proporcionan manejo automático de directorios, gestión de errores robusta y operaciones CRUD completas.

## Archivos Clave y sus Responsabilidades

-   **`RepositorioConfiguracionImpl.java`**
    -   **Descripción:** Implementa la interfaz `RepositorioConfiguracion` para la gestión de configuraciones del sistema.
    -   **Responsabilidad:** Gestiona la persistencia completa del objeto `Configuracion` de la aplicación, incluyendo tema visual, configuración de audio, diccionario por defecto y tamaño del tablero.
    -   **Mecanismo:** Serializa y deserializa el objeto `Configuracion` hacia/desde un archivo llamado `configuracion.dat`. Proporciona valores por defecto en caso de errores.
    -   **Archivo de datos:** `src/main/resources/persistencias/configuracion.dat`

-   **`RepositorioDiccionarioImpl.java`**
    -   **Descripción:** Implementa la interfaz `RepositorioDiccionario` para la gestión completa de diccionarios.
    -   **Responsabilidad:** Gestiona la persistencia de diccionarios de palabras con estructura DAWG. Mantiene un índice de diccionarios (nombre a ruta) y gestiona los archivos individuales de cada diccionario (`alpha.txt`, `words.txt`). Incluye validación de integridad y operaciones CRUD completas.
    -   **Mecanismo:** El índice de diccionarios se serializa (`diccionarios_index.dat`). Los archivos de cada diccionario se gestionan directamente en el sistema de archivos con verificación de validez.
    -   **Archivo de índice:** `src/main/resources/persistencias/diccionarios_index.dat`
    -   **Archivos de diccionario:** Almacenados en subdirectorios dentro de `src/main/resources/diccionarios/` (la ruta específica se guarda en el índice).

-   **`RepositorioJugadorImpl.java`**
    -   **Descripción:** Implementa la interfaz `RepositorioJugador` para la gestión de jugadores humanos e IA.
    -   **Responsabilidad:** Gestiona la persistencia de un mapa completo de jugadores (nombre del jugador a objeto `Jugador`). Proporciona operaciones de búsqueda y filtrado por tipo de jugador (humano/IA).
    -   **Mecanismo:** Serializa y deserializa el mapa completo de jugadores hacia/desde un archivo llamado `jugadores.dat`. Incluye métodos de consulta especializados.
    -   **Archivo de datos:** `src/main/resources/persistencias/jugadores.dat`

-   **`RepositorioPartidaImpl.java`**
    -   **Descripción:** Implementa la interfaz `RepositorioPartida` para la gestión completa de partidas guardadas.
    -   **Responsabilidad:** Gestiona la persistencia de los estados completos de las partidas de Scrabble (objetos `ControladorJuego`). Permite guardar, cargar, eliminar y listar partidas con generación automática de IDs únicos.
    -   **Mecanismo:** Serializa y deserializa un mapa de identificadores de partida a objetos `ControladorJuego` hacia/desde un archivo llamado `partidas.dat`. Incluye gestión robusta de errores.
    -   **Archivo de datos:** `src/main/resources/persistencias/partidas.dat`

-   **`RepositorioRankingImpl.java`**
    -   **Descripción:** Implementa la interfaz `RepositorioRanking` para la gestión del sistema de clasificación.
    -   **Responsabilidad:** Gestiona la persistencia del objeto `Ranking` que contiene las estadísticas completas y clasificaciones de los jugadores. Proporciona operaciones especializadas para consultas de estadísticas individuales.
    -   **Mecanismo:** Serializa y deserializa el objeto `Ranking` completo hacia/desde un archivo llamado `ranking.dat`. Incluye métodos de consulta optimizados para diferentes criterios de ranking.
    -   **Archivo de datos:** `src/main/resources/persistencias/ranking.dat`

## Consideraciones

-   **Gestión de Directorios:** Cada implementación se encarga de asegurar que los directorios necesarios para la persistencia existan antes de intentar operaciones de escritura. Se crean automáticamente si no existen.
-   **Gestión de Errores:** Los métodos generalmente retornan un booleano indicando el éxito o fracaso de la operación e imprimen mensajes de error a `System.err` o lanzan excepciones específicas (como `ExceptionPersistenciaFallida`) en caso de problemas. Incluyen manejo robusto de excepciones de E/S.
-   **Dependencias:** Estas clases dependen de los modelos definidos en `scrabble.domain.models`, de los controladores (como `ControladorJuego`) y de las interfaces en `scrabble.domain.persistences.interfaces`. Siguen el principio de inversión de dependencias.
-   **Rendimiento:** Las implementaciones están optimizadas para operaciones frecuentes de lectura/escritura y manejan eficientemente la serialización de objetos complejos.
-   **Integridad de Datos:** Incluyen verificaciones de integridad y validación de datos para asegurar la consistencia del estado persistido. 