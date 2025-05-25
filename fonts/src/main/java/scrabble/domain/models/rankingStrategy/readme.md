# Directorio de Estrategias de Ranking

## Descripción General

Este directorio implementa el patrón de diseño Strategy para proporcionar diferentes algoritmos de ordenación del ranking de jugadores. Estas estrategias permiten clasificar a los jugadores según diversos criterios como puntuación máxima, media, puntuación total acumulada, número de partidas o victorias. El diseño facilita la extensibilidad y mantenimiento del sistema de ranking, permitiendo agregar nuevos criterios de ordenación sin modificar el código existente.

El sistema utiliza una arquitectura basada en el patrón Strategy combinado con Factory Method, proporcionando flexibilidad para cambiar dinámicamente los criterios de ordenación del ranking. Cada estrategia encapsula un algoritmo específico de comparación, permitiendo que el sistema de ranking sea extensible y mantenible.

## Estructura del Directorio

### Clase de Estadísticas

- **PlayerRankingStats.java**  
  Encapsula todas las estadísticas de un jugador en una única clase, incluyendo puntuaciones individuales, máximas, medias, totales acumuladas, partidas jugadas y victorias. Centraliza la lógica de actualización de estadísticas y proporciona una interfaz limpia para acceder a los datos. Se utiliza internamente por la clase Ranking para almacenar las estadísticas de cada jugador de forma organizada y eficiente. Incluye métodos para agregar/eliminar puntuaciones, actualizar contadores de partidas y victorias, y recalcular automáticamente estadísticas derivadas como medias y máximos.

### Interfaces y Factory

- **RankingOrderStrategy.java**  
  Define la interfaz común que deben implementar todas las estrategias de ordenación. Extiende `Comparator<String>` para permitir la comparación directa de nombres de usuario según diferentes criterios. Las implementaciones reciben directamente un objeto Ranking para acceder a las estadísticas necesarias. Forma parte del patrón Strategy, proporcionando el contrato que deben cumplir todas las estrategias concretas de ordenación.

- **RankingOrderStrategyFactory.java**  
  Implementa el patrón Factory para crear instancias de estrategias concretas según el criterio solicitado. Inyecta un objeto `Ranking` en las estrategias para que puedan acceder a los datos necesarios directamente. Centraliza la lógica de creación de estrategias y proporciona un punto único de acceso para obtener la estrategia apropiada según el criterio especificado. Soporta criterios como "maxima", "media", "partidas", "victorias" y "total".

### Estrategias Concretas

- **MaximaScoreStrategy.java**  
  Implementa la ordenación de jugadores según su puntuación máxima obtenida en cualquier partida individual. Ordena de mayor a menor puntuación máxima, con ordenación alfabética en caso de empate. Recibe un objeto `Ranking` en su constructor para obtener las puntuaciones necesarias para la comparación. Útil para identificar a los jugadores que han logrado las mejores puntuaciones individuales.

- **MediaScoreStrategy.java**  
  Ordena a los jugadores según su puntuación media calculada sobre todas sus partidas jugadas. La media se calcula dividiendo la puntuación total acumulada entre el número de partidas. Ordena de mayor a menor puntuación media, con ordenación alfabética en caso de empate. Proporciona una medida de consistencia en el rendimiento de los jugadores.

- **PartidasJugadasStrategy.java**  
  Clasifica a los jugadores según el número total de partidas jugadas, de mayor a menor. Útil para identificar a los jugadores más activos del sistema. Recibe un objeto `Ranking` en su constructor para obtener el contador de partidas necesario para la comparación. En caso de empate en número de partidas, se ordena alfabéticamente.

- **VictoriasStrategy.java**  
  Ordena a los jugadores según sus victorias totales, de mayor a menor. Permite identificar a los jugadores más exitosos del sistema basándose en el número de partidas ganadas. Recibe un objeto `Ranking` en su constructor para obtener el contador de victorias necesario para la comparación. En caso de empate, se ordena alfabéticamente.

- **PuntuacionTotalStrategy.java**  
  Clasifica a los jugadores según la puntuación total acumulada a lo largo de todas sus partidas. Suma todas las puntuaciones individuales de cada jugador y ordena de mayor a menor puntuación total. Es la estrategia por defecto del sistema. Recibe un objeto `Ranking` en su constructor para obtener la suma de todas las puntuaciones de cada jugador. Útil para identificar el rendimiento acumulativo de los jugadores.

## Arquitectura Simplificada

La implementación utiliza una arquitectura simplificada donde:

1. Las **estrategias** reciben directamente un objeto `Ranking` en lugar de depender de una interfaz abstracta, reduciendo la complejidad del diseño.
2. El objeto `Ranking` proporciona todos los métodos necesarios para acceder a las estadísticas de los jugadores (puntuaciones, partidas, victorias, etc.).
3. La inyección del objeto ranking se realiza a través del constructor de cada estrategia, estableciendo la dependencia de forma clara y explícita.
4. Cada estrategia implementa la interfaz `Comparator<String>` a través de `RankingOrderStrategy`, permitiendo su uso directo con métodos de ordenación estándar de Java.

## Criterios de Ordenación Disponibles

- **maxima**: Clasifica según la puntuación más alta conseguida en una partida individual
- **media**: Clasifica según la puntuación media de todas las partidas jugadas
- **partidas**: Clasifica según el número total de partidas jugadas (actividad del jugador)
- **victorias**: Clasifica según el número total de victorias (éxito del jugador)
- **total**: Clasifica según la puntuación total acumulada en todas las partidas (rendimiento acumulativo)

## Ventajas de la Nueva Arquitectura

1. **Acoplamiento directo**: Las estrategias trabajan directamente con el objeto `Ranking`, eliminando capas de abstracción innecesarias y simplificando el diseño.
2. **Mantenibilidad**: Menos archivos y dependencias hacen el código más fácil de mantener y entender.
3. **Claridad**: La relación entre estrategias y datos es más directa y comprensible, facilitando el debugging y las modificaciones.
4. **Extensibilidad**: Agregar nuevas estrategias requiere únicamente implementar la interfaz y actualizar el factory.
5. **Rendimiento**: El acceso directo a los datos elimina overhead de capas intermedias.
6. **Consistencia**: Todas las estrategias siguen el mismo patrón de construcción e implementación.
