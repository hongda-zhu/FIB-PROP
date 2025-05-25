# Directorio de Estrategias de Ranking

## Descripción General

Este directorio implementa el patrón de diseño Strategy para proporcionar diferentes algoritmos de ordenación del ranking de jugadores. Estas estrategias permiten clasificar a los jugadores según diversos criterios como puntuación máxima, media, puntuación total acumulada, número de partidas o victorias.

## Estructura del Directorio

### Clase de Estadísticas

- **PlayerRankingStats.java**  
  Encapsula todas las estadísticas de un jugador en una única clase, incluyendo puntuaciones, máximos, medias, partidas y victorias. Centraliza la lógica de actualización de estadísticas y proporciona una interfaz limpia para acceder a los datos. Se utiliza internamente por la clase Ranking para almacenar las estadísticas de cada jugador de forma organizada.

### Interfaces y Factory

- **RankingOrderStrategy.java**  
  Define la interfaz común que deben implementar todas las estrategias de ordenación. Extiende `Comparator<String>` para permitir la comparación directa de nombres de usuario según diferentes criterios. Las implementaciones reciben directamente un objeto Ranking para acceder a las estadísticas necesarias.

- **RankingOrderStrategyFactory.java**  
  Implementa el patrón Factory para crear instancias de estrategias concretas según el criterio solicitado. Inyecta un objeto `Ranking` en las estrategias para que puedan acceder a los datos necesarios directamente.

### Estrategias Concretas

- **MaximaScoreStrategy.java**  
  Implementa la ordenación de jugadores según su puntuación máxima obtenida. Recibe un objeto `Ranking` en su constructor para obtener las puntuaciones necesarias para la comparación.

- **MediaScoreStrategy.java**  
  Ordena a los jugadores según su puntuación media. Recibe un objeto `Ranking` en su constructor para obtener las puntuaciones necesarias para la comparación.

- **PartidasJugadasStrategy.java**  
  Clasifica a los jugadores según el número total de partidas jugadas. Recibe un objeto `Ranking` en su constructor para obtener las partidas jugadas necesarias para la comparación.

- **VictoriasStrategy.java**  
  Ordena a los jugadores según sus victorias totales. Recibe un objeto `Ranking` en su constructor para obtener las victorias necesarias para la comparación.

- **PuntuacionTotalStrategy.java**  
  Clasifica a los jugadores según la puntuación total acumulada a lo largo de todas sus partidas. Recibe un objeto `Ranking` en su constructor para obtener la suma de todas las puntuaciones de cada jugador.

## Arquitectura Simplificada

La implementación utiliza una arquitectura simplificada donde:

1. Las **estrategias** reciben directamente un objeto `Ranking` en lugar de depender de una interfaz abstracta.
2. El objeto `Ranking` proporciona todos los métodos necesarios para acceder a las estadísticas de los jugadores.
3. La inyección del objeto ranking se realiza a través del constructor de cada estrategia.
4. Se elimina la complejidad del acoplamiento circular que existía anteriormente con `RankingDataProvider`.

## Criterios de Ordenación Disponibles

- **maxima**: Clasifica según la puntuación más alta conseguida en una partida
- **media**: Clasifica según la puntuación media de todas las partidas
- **partidas**: Clasifica según el número total de partidas jugadas 
- **victorias**: Clasifica según el número total de victorias
- **total**: Clasifica según la puntuación total acumulada en todas las partidas

## Ventajas de la Nueva Arquitectura

1. **Simplicidad**: Eliminación de la interfaz `RankingDataProvider` reduce la complejidad del código.
2. **Acoplamiento directo**: Las estrategias trabajan directamente con el objeto `Ranking`, eliminando capas de abstracción innecesarias.
3. **Mantenibilidad**: Menos archivos y dependencias hacen el código más fácil de mantener.
4. **Claridad**: La relación entre estrategias y datos es más directa y comprensible.
