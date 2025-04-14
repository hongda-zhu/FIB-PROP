# Directorio de Models

## Descripción General

Este directorio contiene las clases de modelo que representan las entidades principales del juego Scrabble. Estas clases implementan la lógica de negocio fundamental y las estructuras de datos necesarias para el funcionamiento del juego.

## Estructura del Directorio

### Clases de Juego

- **Jugador.java**  
  Clase abstracta que define el comportamiento común de todos los jugadores, incluyendo la gestión de puntuaciones y el identificador único.

- **JugadorHumano.java**  
  Implementación concreta para jugadores humanos. Gestiona credenciales, estadísticas de juego y el estado de sesión del jugador.

- **JugadorIA.java**  
  Implementación para jugadores controlados por la inteligencia artificial, con diferentes niveles de dificultad.

- **Tablero.java**  
  Representa el tablero de juego, incluyendo la gestión de casillas, bonificaciones y la validación de posiciones de juego.

- **Bolsa.java**  
  Implementa la bolsa de fichas del juego, controlando la distribución y extracción aleatoria de letras.

### Clases de Gestión de Datos

- **Ranking.java**  
  Gestiona el sistema de clasificación de jugadores, permitiendo diferentes estrategias de ordenación y almacenamiento de estadísticas.

- **Diccionario.java**  
  Administra los diccionarios de palabras válidas para diferentes idiomas, utilizando estructuras DAWG para búsquedas eficientes.

- **Dawg.java** y **DawgNode.java**  
  Implementan un Grafo Acíclico Dirigido de Palabras (DAWG) para validar palabras de manera eficiente con un uso óptimo de memoria.

- **Configuracion.java**  
  Gestiona las configuraciones generales del juego, permitiendo personalizar diversos aspectos de la partida.

### Subcarpetas

- **rankingStrategy/**  
  Contiene las implementaciones de diferentes estrategias para ordenar el ranking de jugadores, siguiendo el patrón de diseño Strategy.

## Relación con los Controladores

Estas clases implementan la lógica de negocio que es gestionada por los controladores en el paquete `scrabble.domain.controllers`:

- `Jugador`, `JugadorHumano`, `JugadorIA` → `ControladorUsuario`
- `Ranking` → `ControladorRanking`
- `Tablero`, `Bolsa`, `Diccionario` → `ControladorJuego` y `GestorJugada`
