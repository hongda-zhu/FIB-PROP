# Directorio de Estrategias de Ranking

## Descripción General

Este directorio implementa el patrón de diseño Strategy para proporcionar diferentes algoritmos de ordenación del ranking de jugadores. Estas estrategias permiten clasificar a los jugadores según diversos criterios como puntuación máxima, media, puntuación total acumulada, número de partidas o victorias.

## Estructura del Directorio

### Clase de Estadísticas

- **PlayerRankingStats.java**  
  Encapsula todas las estadísticas de un jugador en una única clase, incluyendo puntuaciones, máximos, medias, partidas y victorias. Centraliza la lógica de actualización de estadísticas y proporciona una interfaz limpia para acceder a los datos.

### Interfaces, Provider y Factory

- **RankingDataProvider.java**  
  Define la interfaz para los proveedores de datos de ranking, abstrayendo el acceso a las estadísticas de jugadores. Desacopla las estrategias de ordenación de las implementaciones concretas de almacenamiento de datos.

- **RankingOrderStrategy.java**  
  Define la interfaz común que deben implementar todas las estrategias de ordenación. Extiende `Comparator<String>` para permitir la comparación directa de nombres de usuario según diferentes criterios.

- **RankingOrderStrategyFactory.java**  
  Implementa el patrón Factory para crear instancias de estrategias concretas según el criterio solicitado. Inyecta un `RankingDataProvider` en las estrategias para que puedan acceder a los datos necesarios.

### Estrategias Concretas

- **MaximaScoreStrategy.java**  
  Implementa la ordenación de jugadores según su puntuación máxima obtenida. Usa un `RankingDataProvider` para obtener las puntuaciones necesarias para la comparación.

- **MediaScoreStrategy.java**  
  Ordena a los jugadores según su puntuación media. Usa un `RankingDataProvider` para obtener las puntuaciones necesarias para la comparación.

- **PartidasJugadasStrategy.java**  
  Clasifica a los jugadores según el número total de partidas jugadas. Usa un `RankingDataProvider` para obtener las partidas jugadas necesarias para la comparación.

- **VictoriasStrategy.java**  
  Ordena a los jugadores según sus victorias totales. Usa un `RankingDataProvider` para obtener las victorias necesarias para la comparación.

- **PuntuacionTotalStrategy.java**  
  Clasifica a los jugadores según la puntuación total acumulada a lo largo de todas sus partidas. Usa un `RankingDataProvider` para obtener la suma de todas las puntuaciones de cada jugador.

## Inversión de Dependencias

La implementación sigue el principio de inversión de dependencias:

1. Las **estrategias** (clases de alto nivel) dependen de una **abstracción** (`RankingDataProvider`), no de detalles concretos.
2. Tanto `ControladorRanking` como `Ranking` (clases de bajo nivel) implementan la interfaz `RankingDataProvider`.
3. La inyección del proveedor de datos se realiza a través del constructor de cada estrategia.

## Criterios de Ordenación Disponibles

- **maxima**: Clasifica según la puntuación más alta conseguida en una partida
- **media**: Clasifica según la puntuación media de todas las partidas
- **partidas**: Clasifica según el número total de partidas jugadas 
- **victorias**: Clasifica según el número total de victorias
- **total**: Clasifica según la puntuación total acumulada en todas las partidas
