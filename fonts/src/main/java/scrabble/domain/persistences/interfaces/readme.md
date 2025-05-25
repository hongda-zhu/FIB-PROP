# Directorio de Interfaces de Persistencia (`interfaces`)

## Descripción General

Este directorio (`scrabble.domain.persistences.interfaces`) contiene las interfaces Java que definen los **contratos** para las operaciones de persistencia de datos dentro de la aplicación Scrabble. Estas interfaces establecen un nivel de abstracción entre la lógica de negocio (controladores y servicios del dominio) y los mecanismos concretos de almacenamiento de datos.

El uso de interfaces para la persistencia es una práctica clave en el diseño de aplicaciones robustas y mantenibles, ya que:
-   **Desacopla** la lógica de dominio de las implementaciones específicas de persistencia.
-   Facilita la **intercambiabilidad** de las implementaciones de persistencia (cambiar de serialización de archivos a una base de datos SQL o NoSQL) sin afectar el resto del código que depende de estas interfaces.
-   Promueve la **testabilidad**, permitiendo el uso de implementaciones "mock" o "fake" de los repositorios durante las pruebas unitarias o de integración.
-   Implementa el **patrón Repository**, proporcionando una interfaz uniforme para el acceso a datos independientemente del mecanismo de almacenamiento subyacente.
-   **Abstrae detalles de persistencia**, permitiendo que los controladores se enfoquen en la lógica de negocio sin preocuparse por los mecanismos de almacenamiento.
-   **Facilita el mantenimiento** al centralizar los contratos de persistencia en interfaces bien definidas.

## Interfaces Clave y su Propósito

Cada interfaz en este directorio corresponde generalmente a una entidad o agregado principal del dominio, definiendo las operaciones CRUD (Crear, Leer, Actualizar, Eliminar) y otras consultas relevantes para esa entidad. Todas las interfaces siguen principios de diseño sólidos y proporcionan contratos claros para la persistencia de datos.

-   **`RepositorioConfiguracion.java`**
    -   **Propósito:** Define el contrato para la persistencia del objeto `Configuracion` de la aplicación.
    -   **Métodos clave:** `guardar(Configuracion configuracion)`, `cargar()`.
    -   **Descripción:** Permite guardar el estado actual de la configuración de la aplicación y cargarla al iniciar o cuando sea necesario. Implementa el patrón Repository para abstraer los detalles de persistencia.

-   **`RepositorioDiccionario.java`**
    -   **Propósito:** Define el contrato para la gestión y persistencia completa de los diccionarios de palabras utilizados en el juego.
    -   **Métodos clave:** `guardar(String nombre, Diccionario diccionario, String path)`, `guardarIndice(Map<String, String> diccionariosPaths)`, `cargar(String nombre)`, `cargarIndice()`, `eliminar(String nombre)`, `existe(String nombre)`, `listarDiccionarios()`, `verificarDiccionarioValido(String nombre)`.
    -   **Descripción:** Especifica cómo se deben guardar, cargar, listar, eliminar y verificar los diccionarios, incluyendo la gestión de un índice centralizado de diccionarios y validación de integridad de estructuras DAWG.

-   **`RepositorioJugador.java`**
    -   **Propósito:** Define el contrato para la persistencia de la información completa de los jugadores.
    -   **Métodos clave:** `guardarTodos(Map<String, Jugador> jugadores)`, `cargarTodos()`, `buscarPorNombre(String nombre)`, `obtenerNombresJugadoresHumanos()`, `obtenerNombresJugadoresIA()`, `obtenerNombresTodosJugadores()`.
    -   **Descripción:** Permite almacenar y recuperar información sobre los jugadores registrados, buscarlos por nombre y obtener listas de nombres según el tipo de jugador (humano/IA). Proporciona operaciones especializadas para diferentes categorías de usuarios.

-   **`RepositorioPartida.java`**
    -   **Propósito:** Define el contrato para la persistencia de los estados completos de las partidas de Scrabble (objetos `ControladorJuego`).
    -   **Métodos clave:** `guardar(int id, ControladorJuego partida)`, `cargar(int id)`, `eliminar(int id)`, `listarTodas()`, `generarNuevoId()`.
    -   **Descripción:** Especifica cómo guardar, cargar, eliminar y listar las partidas con todo su estado. También define un método para generar identificadores únicos para nuevas partidas y manejo de excepciones específicas de persistencia.

-   **`RepositorioRanking.java`**
    -   **Propósito:** Define el contrato para la persistencia del objeto `Ranking`, que almacena las puntuaciones y estadísticas completas de los jugadores.
    -   **Métodos clave:** `guardar(Ranking ranking)`, `cargar()`, `actualizarEstadisticasJugador(String nombre, PlayerRankingStats stats)`, `eliminarJugador(String nombre)`, `obtenerRankingOrdenado(String criterio)`, y diversos métodos para obtener estadísticas específicas de un jugador.
    -   **Descripción:** Especifica cómo guardar y cargar el estado completo del ranking, así como actualizar y consultar las datos del ranking con diferentes criterios de ordenación y estadísticas detalladas por jugador.

## Uso

Las clases que necesitan interactuar con la capa de persistencia (principalmente los controladores del dominio) deberían depender de estas interfaces, no de sus implementaciones concretas. Esto se logra normalmente mediante la inyección de dependencias, donde se proporciona una instancia de una implementación del repositorio a la clase cliente. Este enfoque facilita el testing, el mantenimiento y la flexibilidad del sistema, permitiendo cambiar implementaciones sin afectar el código cliente. 