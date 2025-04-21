# Directorio de Subcontroladores

## Descripción General

Este directorio contiene los controladores específicos que implementan la lógica de negocio para las diferentes funcionalidades del juego Scrabble. Estos subcontroladores gestionan aspectos particulares del dominio del juego y coordinan las operaciones con los modelos correspondientes, actuando bajo la dirección del `ControladorDomain` (fachada). Siguen una arquitectura de capas bien definida para mantener una clara separación de responsabilidades.

## Estructura del Directorio

### Controladores Principales

-   **ControladorJugador.java**
    Gestiona las operaciones relacionadas con los jugadores (usuarios), incluyendo la creación de jugadores humanos y IA, autenticación (login/logout) y recuperación de información y estadísticas de los jugadores. Implementa el patrón Singleton para garantizar una única instancia. Se encarga de la persistencia de los datos de los jugadores mediante serialización Java (`.dat`).

-   **ControladorRanking.java**
    Administra el sistema de clasificación (ranking) de jugadores. Carga y guarda el estado del ranking. Implementa el patrón Singleton y también la interfaz `RankingDataProvider`, delegando la obtención de datos de jugadores a `ControladorJugador`. Utiliza diferentes `RankingOrderStrategy` para consultar el ranking ordenado según distintos criterios (victorias, puntuación máxima, etc.). Actualiza el ranking basándose en los resultados de las partidas finalizadas.

-   **ControladorJuego.java**
    Coordina el desarrollo y la lógica de una partida de Scrabble en curso. Implementa el patrón Singleton. Se encarga de iniciar la partida (tablero, bolsa, jugadores), gestionar los turnos, validar las jugadas propuestas por los jugadores (humanos o IA) utilizando el diccionario y el tablero, calcular las puntuaciones, y detectar y finalizar la partida. Orquesta las interacciones entre el `Tablero`, la `Bolsa`, los `Jugador`es activos, la `Configuracion` de la partida y el `Diccionario`, además de notificar a `ControladorJugador` y `ControladorRanking` al finalizar. *No implementa directamente la estrategia de juego de la IA*, sino que facilita el flujo para cualquier tipo de jugador.

-   **ControladorDiccionario.java**
    Gestiona la colección de diccionarios disponibles para el juego. Implementa el patrón Singleton. Permite crear, importar (desde archivos de texto), listar y eliminar diccionarios. Proporciona acceso a un diccionario específico para la validación de palabras durante una partida (`ControladorJuego` lo utiliza). Persiste la lista de diccionarios disponibles (el índice) mediante serialización, mientras que los datos de palabras residen en archivos externos.


Cada uno de estos controladores sigue principios sólidos de diseño orientado a objetos y aplica patrones de diseño (como Singleton) para mejorar la organización y mantenibilidad del código. La comunicación entre ellos se realiza a menudo a través del `ControladorDomain` o mediante referencias directas cuando es necesario (ej. `ControladorJuego` usando `ControladorDiccionario`).

Los controladores reciben las peticiones desde el `ControladorDomain` (que actúa como fachada) y coordinan la lógica específica de su dominio, trabajando con los modelos correspondientes (`Tablero`, `Bolsa`, `Jugador`, `Ranking`, `Diccionario` etc.) para implementar las reglas de negocio y mantener la consistencia del sistema. No contienen lógica de presentación ni acceso directo a la interfaz de usuario, manteniendo así una clara separación de responsabilidades.