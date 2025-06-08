# Directorio de Subcontroladores

## Descripción General

Este directorio contiene los controladores específicos que implementan la lógica de negocio para las diferentes funcionalidades del juego Scrabble. Estos subcontroladores gestionan aspectos particulares del dominio del juego y coordinan las operaciones con los modelos correspondientes, actuando bajo la dirección del `ControladorDomain` (fachada). Siguen una arquitectura de capas bien definida para mantener una clara separación de responsabilidades.

## Estructura del Directorio

### Controladores Principales

-   **ControladorJugador.java**
    Gestiona las operaciones relacionadas con los jugadores (usuarios), incluyendo la creación de jugadores humanos y IA, autenticación (login/logout) y recuperación de información y estadísticas de los jugadores. Implementa el patrón Singleton y utiliza un `RepositorioJugador` para la persistencia de los datos de los jugadores (por defecto, `RepositorioJugadorImpl` que usa serialización Java).

-   **ControladorRanking.java**
    Administra el sistema de clasificación (ranking) de jugadores. Implementa el patrón Singleton y utiliza un `RepositorioRanking` para cargar y guardar el estado del ranking (por defecto, `RepositorioRankingImpl`). Delega la obtención de datos de jugadores a `ControladorJugador`. Utiliza diferentes `RankingOrderStrategy` para consultar el ranking ordenado según distintos criterios. Actualiza el ranking basándose en los resultados de las partidas finalizadas.

-   **ControladorJuego.java**
    Controlador principal que coordina el desarrollo completo de una partida de Scrabble. Gestiona la inicialización de partidas (tablero, bolsa, jugadores), manejo de turnos, validación exhaustiva de jugadas utilizando algoritmos de búsqueda, cálculo de puntuaciones con multiplicadores, y detección automática de condiciones de fin de juego. Implementa algoritmos avanzados para la búsqueda de movimientos válidos (extendLeft/Right, crossCheck, find_anchors) y proporciona funcionalidades de IA con diferentes niveles de dificultad. Utiliza un `RepositorioPartida` para la persistencia completa del estado del juego. Orquesta las interacciones complejas entre el `Tablero`, la `Bolsa`, los `Jugador`es activos, la `Configuracion` de la partida y el `Diccionario`, manteniendo la consistencia del estado del juego en todo momento.

-   **ControladorDiccionario.java**
    Gestiona la colección de diccionarios disponibles para el juego. Implementa el patrón Singleton y utiliza un `RepositorioDiccionario` (por defecto, `RepositorioDiccionarioImpl`) para la persistencia del índice de diccionarios y la carga de los datos de estos. Permite crear, importar (desde archivos de texto), listar y eliminar diccionarios. Proporciona acceso a un diccionario específico para la validación de palabras durante una partida.

-   **ControladorConfiguracion.java**
    Gestiona la configuración de la aplicación, como idioma, tema y volumen. Utiliza un `RepositorioConfiguracion` (por defecto, `RepositorioConfiguracionImpl`) para la persistencia de los ajustes de configuración.

Cada uno de estos controladores sigue principios sólidos de diseño orientado a objetos y aplica patrones de diseño (como Singleton o el uso de interfaces de Repositorio para la persistencia) para mejorar la organización y mantenibilidad del código. La comunicación entre ellos se realiza a menudo a través del `ControladorDomain` (que actúa como fachada) o mediante referencias directas cuando es necesario.

Los controladores reciben las peticiones desde el `ControladorDomain` y coordinan la lógica específica de su dominio, trabajando con los modelos correspondientes para implementar las reglas de negocio y mantener la consistencia del sistema. No contienen lógica de presentación ni acceso directo a la interfaz de usuario, manteniendo así una clara separación de responsabilidades.