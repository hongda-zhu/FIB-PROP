# Directorio de Estrategias de Ranking

## Descripción General

Este directorio implementa el patrón de diseño Strategy para proporcionar diferentes algoritmos de ordenación del ranking de jugadores. Estas estrategias permiten clasificar a los jugadores según diversos criterios como puntuación máxima, media, número de partidas o ratio de victorias.

## Estructura del Directorio

### Interfaz y Factory

- **RankingOrderStrategy.java**  
  Define la interfaz común que deben implementar todas las estrategias de ordenación. Establece el contrato para comparar jugadores según diferentes criterios.

- **RankingOrderStrategyFactory.java**  
  Implementa el patrón Factory para crear instancias de estrategias concretas según el criterio solicitado. Centraliza la creación de estrategias y facilita su extensibilidad.

### Estrategias Concretas

- **MaximaScoreStrategy.java**  
  Implementa la ordenación de jugadores según su puntuación máxima obtenida. Prioriza a jugadores que han logrado puntajes altos en partidas individuales.

- **MediaScoreStrategy.java**  
  Ordena a los jugadores según su puntuación media. Favorece a jugadores con rendimiento consistentemente alto a lo largo de múltiples partidas.

- **PartidasJugadasStrategy.java**  
  Clasifica a los jugadores según el número total de partidas jugadas. Destaca a los jugadores más activos en el sistema.

- **RatioVictoriasStrategy.java**  
  Ordena a los jugadores según su ratio de victorias (victorias/partidas jugadas). Premia la efectividad de los jugadores independientemente del número de partidas.

## Relación con el Sistema de Ranking

Estas estrategias son utilizadas por la clase `Ranking` para proporcionar diferentes vistas de clasificación:

- La clase `Ranking` mantiene una estrategia activa que determina el orden predeterminado
- El método `setEstrategia()` permite cambiar dinámicamente el criterio de ordenación
- El método `getRanking()` utiliza la estrategia actual para obtener la lista ordenada
