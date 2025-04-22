# Directorio de Controladores de Dominio (`controllers`)

## Descripción General

Este directorio (`scrabble.domain.controllers`) alberga la lógica de control principal dentro de la capa de dominio de la aplicación Scrabble. Los controladores aquí definidos actúan como intermediarios entre la capa de presentación (o los drivers de prueba) y la lógica de negocio encapsulada en los modelos (`scrabble.domain.models`).

Su responsabilidad principal es recibir solicitudes, orquestar las operaciones necesarias invocando a los modelos adecuados, y preparar los datos para ser devueltos o presentados.

## Estructura del Directorio

-   **`ControladorDomain.java`**:
    Actúa como la **fachada principal** (Facade Pattern) para la capa de controladores del dominio. Es el punto de entrada único para que la capa de presentación interactúe con las funcionalidades del dominio (gestión de jugadores, inicio de partidas, configuración, etc.). Delega las llamadas específicas a los subcontroladores correspondientes.

-   **`subcontrollers/`**:
    Este subdirectorio contiene controladores especializados, cada uno enfocado en un área específica de la lógica de negocio (como la gestión del juego en sí, el manejo de jugadores, el ranking, los diccionarios y las configuraciones). Esta subdivisión promueve una mayor cohesión y un menor acoplamiento. Consulta el `readme.md` dentro de `subcontrollers/` para una descripción detallada de cada subcontrolador.

## Arquitectura y Flujo

1.  La capa de presentación (drivers) realiza llamadas únicamente a métodos públicos de `ControladorDomain`.
2.  `ControladorDomain` identifica la operación solicitada y la delega al subcontrolador apropiado (ej. `ControladorJuego`, `ControladorJugador`, etc.).
3.  El subcontrolador ejecuta la lógica necesaria, interactuando con las clases del paquete `models` (`Jugador`, `Tablero`, `Ranking`, `Diccionario`).
4.  Los resultados o excepciones se devuelven hacia arriba, a través de `ControladorDomain`, hasta la capa que originó la llamada.
