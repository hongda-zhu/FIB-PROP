# Directorio de Models

## Descripción General

Este directorio contiene las clases de modelo que representan las entidades principales del juego Scrabble. Estas clases implementan la lógica de negocio fundamental y las estructuras de datos necesarias para el funcionamiento del juego.

## Estructura del Directorio

### Clases de Juego

-   **Jugador.java**
    Clase abstracta que define el comportamiento común de todos los jugadores, incluyendo la gestión de puntuaciones, atril de fichas y estadísticas base.

-   **JugadorHumano.java**
    Implementación concreta para jugadores humanos. Contiene la información específica como la contraseña (credencial) y hereda la gestión de estadísticas y atril.

-   **JugadorIA.java**
    Implementación para jugadores controlados por la inteligencia artificial, con diferentes niveles de dificultad (`Dificultad`) y la lógica para decidir jugadas.

-   **Tablero.java**
    Representa el tablero de juego, incluyendo la gestión de casillas, multiplicadores de bonificación y la validación de la colocación de palabras.

-   **Bolsa.java**
    Implementa la bolsa de fichas del juego, controla la distribución y extracción aleatoria de letras.

### Clases de Gestión de Datos

-   **Ranking.java**
    Gestiona el almacenamiento de las estadísticas de los jugadores (`PlayerRankingStats`) y aplica diferentes estrategias de ordenación (`RankingOrderStrategy`).

-   **Diccionario.java**
    Representa un diccionario de palabras válidas, utilizando una estructura DAWG (`Dawg.java`) para búsquedas y validaciones eficientes. Gestiona la carga/guardado de palabras desde/hacia archivos.

-   **Dawg.java** y **DawgNode.java**
    Implementan un Grafo Acíclico Dirigido de Palabras (DAWG) para validar palabras de manera eficiente con un uso óptimo de memoria.

### Subcarpetas

-   **rankingStrategy/**
    Contiene las implementaciones de diferentes estrategias (`RankingOrderStrategy`) para ordenar el ranking de jugadores, la interfaz `RankingDataProvider` para obtener datos, y la clase `PlayerRankingStats` para almacenar estadísticas, siguiendo el patrón Strategy.

## Relación con los Controladores

Estas clases implementan la lógica de negocio y los datos que son gestionados y utilizados por los controladores en el paquete `scrabble.domain.controllers.subcontrollers`:

-   `JugadorHumano`, `JugadorIA` → `Jugador` → `ControladorJugador` (Gestiona ciclo de vida, persistencia, login, estadísticas)
-   `Ranking` (y clases en `rankingStrategy/`) → `ControladorRanking` (Gestiona la instancia de Ranking, aplica estrategias, persiste) 
-   `Diccionario` (y `Dawg`/`DawgNode`) → `ControladorDiccionario` (Gestiona la colección de objetos Diccionario, importación, eliminación)
-   `Tablero`, `Bolsa`, `Jugador` (instancias activas) → `ControladorJuego` (Utiliza estos modelos para orquestar una partida)