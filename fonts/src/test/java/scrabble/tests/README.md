# Directorio de Pruebas

## Descripción General

Este directorio contiene pruebas unitarias para todos los componentes de la aplicación del juego Scrabble. Estas pruebas validan el comportamiento de cada clase del modelo, asegurando que funcionen correctamente según las especificaciones.

## Estructura del Directorio

### Pruebas de Modelos
- **ConfiguracionTest.java**  
  Pruebas unitarias para la clase `Configuracion`. Se prueba las gestiones de las configuraciones de la app como el volumen, idioma y tema.

- **JugadorTest.java**  
  Pruebas unitarias para la clase abstracta `Jugador`. Se prueba la funcionalidad común que comparten todos los tipos de jugador mediante una implementación concreta.

- **JugadorHumanoTest.java**  
  Pruebas unitarias para la clase `JugadorHumano`. Se prueba funcionalidad específica relacionada con jugadores humanos como la verificación de contraseña, el estado de inicio de sesión y las estadísticas de juego.

- **JugadorIATest.java**  
  Pruebas unitarias para la clase `JugadorIA`. Se prueba funcionalidad específica de la IA como la configuración de dificultad y estadísticas particulares de la IA en el juego.

- **TableroTest.java**  
  Pruebas unitarias para la clase `Tablero`. Se prueba la creación del tablero, la colocación de fichas, el cálculo de bonificaciones y la funcionalidad de puntuación de jugadas.

- **BolsaTest.java**  
  Pruebas unitarias para la clase `Bolsa`. Se prueba la funcionalidad de la bolsa de letras, incluyendo el llenado de la bolsa, la extracción de fichas y el seguimiento de las fichas restantes.

- **RankingTest.java**  
  Pruebas unitarias para la clase `Ranking`. Se prueba el sistema de clasificación de jugadores, incluyendo la adición de puntuaciones, actualización de estadísticas y recuperación de clasificaciones con diferentes estrategias.

- **RankingStrategyTest.java**  
  Pruebas unitarias para las distintas estrategias de ordenación del ranking. Se prueba la ordenación de jugadores por puntuación máxima, puntuación media, partidas jugadas y ratio de victorias.

- **DiccionarioTest.java**  
  Pruebas unitarias para la clase `Diccionario`. Se prueba la funcionalidad del diccionario, incluyendo la carga de listas de palabras, la validación de palabras y la gestión de múltiples idiomas.

- **DawgNodeTest.java**  
  Pruebas unitarias para la clase `DawgNode`. Se prueba el funcionamiento de los nodos que componen el grafo acíclico dirigido de palabras (DAWG) utilizado para validar palabras de manera eficiente.


## Relación con los Modelos

Cada archivo de prueba corresponde a una clase del modelo en el paquete `scrabble.domain.models`:

- `ConfiguracionTest.java` → `Configuracion.java` 
- `JugadorTest.java` → `Jugador.java`  
- `JugadorHumanoTest.java` → `JugadorHumano.java`  
- `JugadorIATest.java` → `JugadorIA.java`  
- `TableroTest.java` → `Tablero.java`  
- `BolsaTest.java` → `Bolsa.java`  
- `RankingTest.java` → `Ranking.java`  
- `RankingStrategyTest.java` → `rankingStrategy/*.java`  
- `DiccionarioTest.java` → `Diccionario.java`  
- `DawgNodeTest.java` → `DawgNode.java`  

## Cobertura de Pruebas

Las pruebas utilizan los frameworks **JUnit** y **Mockito** para proporcionar una validación completa de:

- Constructores e inicialización de clases  
- Métodos getters y setters  
- Lógica central del negocio  
- Casos extremos y manejo de errores  
- Integración con otros componentes  

Cada prueba incluye condiciones **pre** y **post** que explican qué se está probando y qué validación aporta al proyecto.