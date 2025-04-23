# Directorio DriverInputs 

### Descripción general
Contiene ficheros de entrada que permiten testear las funcionalidades principales del driver.

### Indicaciones de uso
Para ejecutar las pruebas que sean tipo "run_X_test.bat (para windows)" o "run_X_test.sh (para Linux)". Sitúate en /driverInputs y ejecuta ".\run_X_test.bat" o "./run_X_test.sh".

### Estructura del directorio
- UsuarioInput.txt: input de datos para probar funcionalidades principales sobre la gestión de usuarios.

- DiccionarioInput.txt: input de datos para probar funcionalidades principales sobre la gestión de diccionarios.

- PartidaInput.txt: input de datos para probar funcionalidades principales sobre la gestión de partidas.

- RankingInput.txt: input de datos para probar funcionalidades principales sobre la gestión de rankings.


Ficheros necesarios para el testeo por la naturaleza aleatoria del juego:
- partidas.dat: archivo con partidas predefinidas para probar la carga y eliminación de partidas.

- puntuaciones_ranking_test.dat: archivo con datos en el ranking predefinidos para testear el correcto funcionamiento del ranking. 

### Casos de uso y clases involucrados
- UsuarioInput.txt:
  Casos de uso: crear usuario, eliminar usuario, mostrar usuarios
  Clases involucradas: Jugador, JugadorHumano, JugadorIA, ControladorJugador, ControladorDomain

- DiccionarioInput.txt:
  Casos de uso: crear diccionario, eliminar diccionario, importar diccionario, modificar diccionario, mostrar diccionarios
  Clases involucradas: Diccionario, Dawg, DawgNode, ControladorDiccionario, ControladorDomain

- PartidaInput.txt:
  Casos de uso: cargar partida, eliminar partida, pausar partida, abandonar partida
  Clases involucradas: Tablero, Bolsa, ControladorJuego

- RankingInput.txt:
  Casos de uso: ver ranking, consultar historial de un jugador, filtrar ranking
  Clases involucradas: Ranking, RankingOrderStrategyFactory, RankingOrderStrategy, PuntuacionTotalStrategy, MaximaScoreStrategy, MediaScoreStrategy, VictoriasStrategy, RankingDataProvider, PlayerRankingStats, ControladorRanking, ControladorDomain.
