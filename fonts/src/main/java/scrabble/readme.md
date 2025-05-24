# Directorio Raíz de Paquetes (`scrabble`)

## Descripción General

Este directorio (`scrabble`) es el paquete raíz del código fuente principal (`src/main/java`) de la aplicación Scrabble. Contiene todos los subpaquetes y clases que componen el sistema central de la aplicación. La aplicación implementa una versión del juego de mesa Scrabble con funcionalidades para juego individual (contra la IA) y potencialmente multijugador (dependiendo del estado final de la implementación).

## Estructura del Directorio

### Paquetes Principales

-   **`domain/`**
    Contiene los componentes centrales de la lógica de negocio, siguiendo una arquitectura en capas:
    -   **`models/`**: Entidades que representan los conceptos del juego (`Jugador`, `Tablero`, `Bolsa`, `Ranking`, `Diccionario`, `Configuracion`, etc.).
    -   **`controllers/`**: Lógica que coordina las operaciones entre modelos. Contiene la fachada `ControladorDomain`.
        -   **`subcontrollers/`**: Controladores específicos para diferentes funcionalidades (`ControladorJuego`, `ControladorJugador`, `ControladorRanking`, etc.).

-   **`excepciones/`**
    Implementa las excepciones personalizadas que la aplicación utiliza para manejar situaciones de error específicas del dominio del juego (ej. `ExceptionUserNotExist`, `ExceptionPalabraInvalida`, `ExceptionNotEnoughTiles`).

-   **`helpers/`**
    Proporciona clases auxiliares y estructuras de datos genéricas utilizadas en diferentes partes del proyecto (ej. `Tuple`, `Triple`, Enums como `Dificultad`).

- **`MainApplication.java`**
    Es el entry point para la aplicación.