# Directorio de Models

## Descripción General

Este directorio contiene las clases de modelo que representan las entidades principales del juego Scrabble. Estas clases implementan la lógica de negocio fundamental y las estructuras de datos necesarias para el funcionamiento del juego. Cada clase encapsula un aspecto específico del dominio del juego, desde la representación de jugadores y tableros hasta la gestión de diccionarios y rankings.

Las clases de modelo siguen principios de diseño orientado a objetos, incluyendo encapsulación, herencia y polimorfismo. Implementan Serializable para permitir la persistencia de partidas y configuraciones entre sesiones. El diseño facilita la extensibilidad y mantenimiento del sistema, proporcionando una base sólida para la lógica de juego.

El diseño del modelo sigue una arquitectura de dominio rica (Rich Domain Model), donde las entidades no solo contienen datos sino también comportamiento relevante al negocio. Esto permite que la lógica de negocio esté cerca de los datos que manipula, mejorando la cohesión y reduciendo el acoplamiento con otras capas del sistema.

## Estructura del Directorio

### Clases de Juego

-   **Jugador.java**
    Clase abstracta que define el comportamiento común de todos los jugadores, incluyendo la gestión de puntuaciones, atril de fichas (rack) y estadísticas base. Proporciona métodos para manipular fichas, gestionar turnos pasados y mantener el estado del jugador durante la partida. Actúa como clase base para JugadorHumano y JugadorIA, implementando funcionalidad compartida como la gestión del rack y contadores de turnos.

-   **JugadorHumano.java**
    Implementación concreta para jugadores humanos. Extiende Jugador añadiendo funcionalidad específica como el estado de participación en partidas y el nombre de la partida actual. Mantiene información sobre si el jugador está actualmente en una partida y proporciona métodos para gestionar este estado. No incluye lógica de IA, delegando las decisiones de juego a la interfaz de usuario.

-   **JugadorIA.java**
    Implementación para jugadores controlados por la inteligencia artificial, con diferentes niveles de dificultad definidos por el enum `Dificultad`. Incluye generación automática de nombres únicos para IAs y gestión del nivel de dificultad. Los jugadores IA se crean para partidas específicas y no mantienen estadísticas persistentes entre sesiones, siendo eliminados al finalizar la partida.

-   **Tablero.java**
    Representa el tablero de juego, incluyendo la gestión de casillas, multiplicadores de bonificación y la validación de la colocación de palabras. Mantiene dos matrices: una para las fichas colocadas y otra para las bonificaciones. Proporciona métodos para colocar fichas, calcular puntuaciones considerando bonificaciones, y validar posiciones. Soporta tableros de diferentes tamaños, siendo 15x15 el estándar con bonificaciones predefinidas.

-   **Bolsa.java**
    Implementa la bolsa de fichas del juego, controla la distribución y extracción aleatoria de letras. Se inicializa con una distribución específica de fichas según el idioma del diccionario y proporciona métodos para sacar fichas aleatoriamente. Mantiene el estado de las fichas restantes durante la partida y se puede consultar para determinar cuándo se acerca el final del juego.

### Clases de Gestión de Datos

-   **Ranking.java**
    Gestiona el almacenamiento de las estadísticas de los jugadores utilizando objetos `PlayerRankingStats` y aplica diferentes estrategias de ordenación mediante `RankingOrderStrategy`. Implementa el patrón Strategy para permitir múltiples criterios de ordenación como puntuación máxima, media, total, partidas jugadas y victorias. Proporciona métodos para agregar puntuaciones, actualizar estadísticas de partidas y obtener rankings ordenados según diferentes criterios.

-   **Diccionario.java**
    Representa un diccionario de palabras válidas, utilizando una estructura DAWG (`Dawg.java`) para búsquedas y validaciones eficientes. Gestiona el alfabeto del idioma, la distribución de fichas y los caracteres comodín. Proporciona métodos para verificar palabras, obtener puntuaciones de letras y acceder a la estructura DAWG para validaciones avanzadas. Soporta operaciones de adición y eliminación de palabras con reconstrucción automática del DAWG.

-   **Dawg.java** y **DawgNode.java**
    Implementan un Grafo Acíclico Dirigido de Palabras (DAWG) para validar palabras de manera eficiente con un uso óptimo de memoria. El DAWG permite compartir sufijos comunes entre palabras, reduciendo significativamente el espacio requerido. Incluye algoritmos de minimización incremental para construcción eficiente y métodos para búsqueda, validación de prefijos y extracción de palabras completas.

-   **Configuracion.java**
    Encapsula todas las configuraciones del sistema incluyendo idioma, tema visual, configuraciones de audio (música y sonidos con sus respectivos volúmenes), diccionario por defecto y tamaño de tablero. Proporciona validación de valores y configuraciones por defecto. Implementa Serializable para persistir las preferencias del usuario entre sesiones.

### Subcarpetas

-   **rankingStrategy/**
    Contiene las implementaciones de diferentes estrategias (`RankingOrderStrategy`) para ordenar el ranking de jugadores y la clase `PlayerRankingStats` para almacenar estadísticas, siguiendo el patrón Strategy. Las estrategias incluyen ordenación por puntuación máxima, media, total, partidas jugadas y victorias. Cada estrategia recibe directamente un objeto `Ranking` para acceder a los datos necesarios, simplificando la arquitectura y mejorando el rendimiento.

## Relación con los Controladores

Estas clases implementan la lógica de negocio y los datos que son gestionados y utilizados por los controladores en el paquete `scrabble.domain.controllers.subcontrollers`:

-   `JugadorHumano`, `JugadorIA` → `Jugador` → `ControladorJugador` (Gestiona ciclo de vida, persistencia, autenticación, estadísticas y operaciones CRUD de jugadores)
-   `Ranking` (y clases en `rankingStrategy/`) → `ControladorRanking` (Gestiona la instancia singleton de Ranking, aplica estrategias de ordenación, persiste datos mediante repositorio) 
-   `Diccionario` (y `Dawg`/`DawgNode`) → `ControladorDiccionario` (Gestiona la colección de objetos Diccionario, importación desde archivos, eliminación y operaciones de mantenimiento)
-   `Tablero`, `Bolsa`, `Jugador` (instancias activas) → `ControladorJuego` (Utiliza estos modelos para orquestar una partida completa, gestionar turnos y aplicar reglas de juego)
-   `Configuracion` → `ControladorConfiguracion` (Gestiona las preferencias del usuario y configuraciones del sistema)

## Principios de Diseño

Las clases de modelo siguen varios principios importantes:

1. **Encapsulación**: Cada clase encapsula su estado interno y proporciona interfaces públicas bien definidas.
2. **Responsabilidad Única**: Cada clase tiene una responsabilidad específica y bien definida en el dominio del juego.
3. **Inmutabilidad Selectiva**: Los objetos mantienen consistencia interna mientras permiten modificaciones controladas.
4. **Serialización**: Todas las clases implementan Serializable para persistencia, con métodos personalizados cuando es necesario.
5. **Validación**: Los métodos incluyen validación de parámetros y manejo de casos edge.
6. **Documentación**: Cada método incluye precondiciones, postcondiciones y documentación completa de comportamiento.