# Análisis Arquitectónico Evolutivo: Sistema Scrabble

## Transformación Estructural entre Entrega 1 y Entrega 2

---

## Resumen Ejecutivo

El presente documento analiza la evolución arquitectónica del sistema Scrabble a través de dos entregas principales, examinando las transformaciones estructurales, la introducción de nuevos patrones de diseño y las mejoras en la organización del dominio. En esta revisión, profundizamos aún más, desde la perspectiva de un equipo de desarrollo, en los matices de estos cambios, especialmente en cómo las decisiones de diseño impactaron la cohesión de los componentes, el acoplamiento entre ellos y el rendimiento general del sistema. La investigación revela una transición significativa desde una arquitectura monolítica hacia un diseño más modular y mantenible, con especial énfasis en la separación de responsabilidades y la aplicación de principios de ingeniería de software.

---

## 1. Análisis Arquitectónico de la Entrega 1

### 1.1 Fundamentos Estructurales

La primera iteración del sistema estableció una arquitectura de tres capas con características específicas que definieron el comportamiento inicial del software:

#### 1.1.1 Capa de Controladores

La arquitectura inicial implementó el patrón **Facade** a través de `ControladorDomain`, estableciendo un punto único de acceso al sistema. Esta decisión arquitectónica centralizó las operaciones y proporcionó una interfaz unificada para las interacciones del usuario.

```java
class ControladorDomain {
    -ControladorConfiguracion controladorConfiguracion
    -ControladorJuego controladorJuego
    -ControladorRanking controladorRanking
    -ControladorJugador controladorJugador
    -ControladorDiccionario controladorDiccionario
}
```

Los controladores específicos implementaron el patrón **Singleton**, garantizando una única instancia por dominio funcional. Esta aproximación aseguró la consistencia de estado pero introdujo acoplamiento implícito entre componentes.

#### 1.1.2 Persistencia Integrada en Controladores

Los controladores de la primera entrega manejaban directamente la persistencia de datos:

```java
class ControladorJugador {
    private static final String JUGADORES_FILE = "jugadores.dat";
    private Map<String, Jugador> jugadores;

    private void guardarDatos() {
        // Serialización directa en el controlador
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(JUGADORES_FILE))) {
            oos.writeObject(jugadores);
        } catch (IOException e) {
            // Manejo de errores de persistencia en lógica de dominio
        }
    }
}
```

**Problemas identificados:**

- **Violación del SRP**: Los controladores mezclaban lógica de dominio con persistencia
- **Acoplamiento fuerte**: Cambios en el formato de almacenamiento requerían modificar controladores
- **Testing complejo**: Las pruebas unitarias dependían del sistema de archivos
- **Flexibilidad limitada**: Imposible cambiar mecanismos de persistencia sin refactorización

### 1.2 Modelo de Dominio y Jerarquías de Objetos

#### 1.2.1 Jerarquía de Jugadores

La implementación de la jerarquía `Jugador` demostró un diseño orientado a objetos sólido, aplicando correctamente los principios de herencia y polimorfismo:

```java
abstract class Jugador {
    #rack : Map<String,Integer>
    #skipTrack : int
    #nombre : String
    +esIA()* : boolean  // Método abstracto definiendo comportamiento polimórfico
}
```

Las especializaciones `JugadorHumano` y `JugadorIA` extendieron la funcionalidad base con características específicas:

- **JugadorHumano**: Gestión de estado de partida y contexto de sesión
- **JugadorIA**: Implementación de niveles de dificultad y generación automática de nombres

#### 1.2.2 Estructura de Datos Avanzada: DAWG

La implementación del **Directed Acyclic Word Graph** representó una decisión arquitectónica sofisticada para la gestión del diccionario:

```java
class Dawg {
    -root: DawgNode
    -minimizedNodes: Map<DawgNode, DawgNode>
    -uncheckedNodes: Stack<Triple<DawgNode, String, DawgNode>>
    -previousWord: String
}
```

**Ventajas técnicas del DAWG:**

- Complejidad temporal O(n) para búsqueda de palabras
- Compresión eficiente del espacio de almacenamiento
- Soporte para validación incremental de palabras parciales
- Minimización automática para optimización de memoria

### 1.3 Subsistema de Ranking y Patrón Strategy

#### 1.3.1 Implementación del Patrón Strategy

El sistema de ranking implementó correctamente el patrón **Strategy** para gestionar diferentes criterios de ordenación:

```java
interface RankingOrderStrategy {
    +compare(String username1, String username2) int
    +getNombre() String
}
```

Las estrategias concretas (`MaximaScoreStrategy`, `MediaScoreStrategy`, `PartidasJugadasStrategy`, `VictoriasStrategy`, `PuntuacionTotalStrategy`) proporcionaron flexibilidad en los algoritmos de ordenación.

#### 1.3.2 Patrón Factory para Creación de Estrategias

La implementación del `RankingOrderStrategyFactory` centralizó la lógica de creación de estrategias:

```java
class RankingOrderStrategyFactory {
    +createStrategy(String criterio, RankingDataProvider dataProvider) RankingOrderStrategy
}
```

#### 1.3.3 Interfaz RankingDataProvider

La primera entrega introdujo `RankingDataProvider` como abstracción para acceso a datos de ranking:

```java
interface RankingDataProvider {
    +getPuntuacionMaxima(String username) : int
    +getPuntuacionMedia(String username) : double
    +getPartidasJugadas(String username) : int
    +getVictorias(String username) : int
    +getPuntuacionTotal(String username) : int
}
```

Esta interfaz fue implementada por `ControladorRanking`, creando una dependencia circular que posteriormente sería optimizada.

### 1.4 Limitaciones Identificadas en la Entrega 1

Las limitaciones arquitectónicas identificadas en la primera entrega:

1.  **Violación del Principio de Responsabilidad Única (SRP)**: Los controladores, al asumir responsabilidades de lógica de negocio y de persistencia, se convertían en clases con menor cohesión que son difíciles de mantener y entender. Cada cambio, ya fuera en la lógica o en el formato de almacenamiento, convergía en el mismo punto, aumentando la complejidad y el riesgo.
2.  **Acoplamiento Fuerte**: La dependencia directa entre controladores y persistencia creaba un acoplamiento rígido. Modificar el almacenamiento requería cambios en múltiples controladores.
3.  **Testing Complejo**: Las pruebas unitarias requerían acceso al sistema de archivos, convirtiéndolas en pruebas de integración más lentas y complejas.
4.  **Flexibilidad Limitada**: Cambiar el mecanismo de persistencia requería refactorización en múltiples controladores.

---

## 2. Evolución Arquitectónica en la Entrega 2

### 2.1 Introducción del Patrón Repository

La transformación más significativa en la segunda entrega fue la implementación del **patrón Repository**, estableciendo una abstracción clara entre la lógica de dominio y los mecanismos de persistencia. Esta decisión transformó el sistema de una arquitectura monolítica hacia un diseño más modular y extensible.

#### 2.1.1 Definición de Interfaces Repository

```java
interface RepositorioJugador {
    +guardarTodos(Map<String, Jugador> jugadores): boolean
    +cargarTodos(): Map<String, Jugador>
    +buscarPorNombre(String nombre): Jugador
    +obtenerNombresJugadoresHumanos(): List<String>
    +obtenerNombresJugadoresIA(): List<String>
    +obtenerNombresTodosJugadores(): List<String>
}

interface RepositorioPartida {
    +guardar(int id, ControladorJuego partida): boolean
    +cargar(int id): ControladorJuego
    +eliminar(int id): boolean
    +listarTodas(): List<Integer>
    +generarNuevoId(): int
}

interface RepositorioDiccionario {
    +guardar(String nombre, Diccionario diccionario, String path): boolean
    +guardarIndice(Map<String, String> diccionariosPaths): boolean
    +cargar(String nombre): Diccionario
    +cargarIndice(): Map<String, String>
    +eliminar(String nombre): boolean
    +existe(String nombre): boolean
    +listarDiccionarios(): List<String>
    +verificarDiccionarioValido(String nombre): boolean
}

interface RepositorioRanking {
    +guardar(Ranking ranking): boolean
    +cargar(): Ranking
    +actualizarEstadisticasJugador(String nombre, PlayerRankingStats stats): boolean
    +eliminarJugador(String nombre): boolean
    +obtenerRankingOrdenado(String criterio): List<String>
    +obtenerTodosJugadores(): Set<String>
    +obtenerPuntuacionMaxima(String nombre): int
    +obtenerPuntuacionMedia(String nombre): double
    +obtenerPartidasJugadas(String nombre): int
    +obtenerVictorias(String nombre): int
    +obtenerPuntuacionTotal(String nombre): int
    +existeJugador(String nombre): boolean
}

interface RepositorioConfiguracion {
    +guardar(Configuracion configuracion): boolean
    +cargar(): Configuracion
}
```

**Análisis de Diseño de Interfaces:**

Las interfaces siguen el **Principio de Segregación de Interfaces (ISP)**, donde cada repositorio expone únicamente los métodos relevantes para su dominio específico:

1. **Granularidad por Dominio**: Cada interfaz encapsula operaciones relacionadas con una única entidad de negocio.

2. **Flexibilidad en Implementaciones**: Las interfaces definen contratos sin imponer restricciones sobre la tecnología de persistencia. Podemos cambiar de serialización Java a JSON o bases de datos sin afectar el código cliente.

3. **Consultas Específicas**: Métodos como `obtenerNombresJugadoresHumanos()` permiten implementaciones que evitan cargar objetos completos cuando solo necesitamos identificadores.

#### 2.1.2 Implementaciones Concretas

```java
class RepositorioJugadorImpl implements RepositorioJugador {
    -JUGADORES_FILE: String
    +guardarTodos(Map<String, Jugador> jugadores): boolean
    +cargarTodos(): Map<String, Jugador>
    +buscarPorNombre(String nombre): Jugador
    +obtenerNombresJugadoresHumanos(): List<String>
    +obtenerNombresJugadoresIA(): List<String>
    +obtenerNombresTodosJugadores(): List<String>
    // Métodos privados especializados
    -validarJugador(Jugador jugador): boolean
    -serializarJugadores(Map<String, Jugador> jugadores, String archivo): boolean
    -deserializarJugadores(String archivo): Map<String, Jugador>
}

class RepositorioPartidaImpl implements RepositorioPartida {
    -DIRECTORIO_PERSISTENCIA: String
    -ARCHIVO_PARTIDAS: String
    +guardar(int id, ControladorJuego partida): boolean
    +cargar(int id): ControladorJuego
    +eliminar(int id): boolean
    +listarTodas(): List<Integer>
    +generarNuevoId(): int
    -cargarTodasLasPartidas(): Map<Integer, ControladorJuego>
    -guardarTodasLasPartidas(Map<Integer, ControladorJuego> mapaPartidas): boolean
    -validarDirectorio(): boolean
    -limpiarPartidaObsoleta(int id): void
}

class RepositorioDiccionarioImpl implements RepositorioDiccionario {
    -DICCIONARIOS_INDEX_FILE: String
    -DIRECTORIO_DICCIONARIOS: String
    +guardar(String nombre, Diccionario diccionario, String path): boolean
    +guardarIndice(Map<String, String> diccionariosPaths): boolean
    +cargar(String nombre): Diccionario
    +cargarIndice(): Map<String, String>
    +eliminar(String nombre): boolean
    +existe(String nombre): boolean
    +listarDiccionarios(): List<String>
    +verificarDiccionarioValido(String nombre): boolean
    -validarNombreDiccionario(String nombre): boolean
    -construirRutaDiccionario(String nombre): String
    -serializarDiccionario(Diccionario diccionario, String ruta): boolean
    -deserializarDiccionario(String ruta): Diccionario
}

class RepositorioRankingImpl implements RepositorioRanking {
    -RANKING_FILE: String
    -DIRECTORIO_DATOS: String
    +guardar(Ranking ranking): boolean
    +cargar(): Ranking
    +actualizarEstadisticasJugador(String nombre, PlayerRankingStats stats): boolean
    +eliminarJugador(String nombre): boolean
    +obtenerRankingOrdenado(String criterio): List<String>
    +obtenerTodosJugadores(): Set<String>
    +obtenerPuntuacionMaxima(String nombre): int
    +obtenerPuntuacionMedia(String nombre): double
    +obtenerPartidasJugadas(String nombre): int
    +obtenerVictorias(String nombre): int
    +obtenerPuntuacionTotal(String nombre): int
    +existeJugador(String nombre): boolean
    -crearRankingVacio(): Ranking
    -validarEstructuraRanking(Ranking ranking): boolean
    -serializarRanking(Ranking ranking, String archivo): boolean
    -deserializarRanking(String archivo): Ranking
}

class RepositorioConfiguracionImpl implements RepositorioConfiguracion {
    -CONFIG_FILE: String
    -CONFIGURACION_POR_DEFECTO: Map<String, Object>
    +guardar(Configuracion configuracion): boolean
    +cargar(): Configuracion
    -crearConfiguracionPorDefecto(): Configuracion
    -validarConfiguracion(Configuracion config): boolean
    -serializarConfiguracion(Configuracion config, String archivo): boolean
    -deserializarConfiguracion(String archivo): Configuracion
    -aplicarValoresPorDefecto(Configuracion config): void
}
```

**Impacto del Patrón Repository:**

La introducción del patrón Repository separó la lógica de persistencia de los controladores, que era una fuente principal de rigidez. Al abstraer estas operaciones detrás de interfaces como `RepositorioJugador`, establecimos un contrato claro. Esto introduce acoplamiento entre controlador e interfaz, pero es un **acoplamiento hacia abstracciones**, no hacia implementaciones concretas.

**Transformación del Acoplamiento:**

- **Eliminado**: Controladores ↔ Detalles de serialización/archivos/formato de datos
- **Introducido**: Controladores ↔ Contratos de persistencia (interfaces)

Las interfaces de repositorio cambian menos frecuentemente que los detalles de implementación de persistencia.

**Beneficios del Desacoplamiento:**

1. **Flexibilidad en Almacenamiento**: Podemos cambiar `RepositorioJugadorImpl` para usar cualquier otro tipo de base de datos sin modificar controladores.

2. **Cohesión Mejorada**: Los controladores se enfocan en lógica de negocio, los repositorios en gestión de datos.

3. **Testabilidad**: Los tests unitarios pueden usar mocks de las interfaces sin acceso real al sistema de archivos.

4. **Mantenibilidad**: Los cambios de persistencia están contenidos en los repositorios.

5. **Optimización Aislada**: Podemos implementar caching y optimizaciones de I/O sin afectar la lógica de negocio.

### 2.2 Reestructuración de Controladores

#### 2.2.1 Separación de Responsabilidades

Los controladores en la Entrega 2 delegaron completamente las operaciones de persistencia a los repositories:

```java
class ControladorJugador {
    -jugadores: Map<String,Jugador>
    -repositorioJugador: RepositorioJugador  // Inyección de dependencia

    // Métodos enfocados únicamente en lógica de dominio
    +registrarUsuario(String nombre): boolean
    +eliminarUsuario(String nombre): boolean
    // Sin métodos de persistencia directa
}
```

**Beneficios logrados:**

- **Principio de Responsabilidad Única (SRP)**: Los controladores se enfocan únicamente en lógica de negocio, delegando la persistencia al repositorio.
- **Inversión de Dependencias (DIP)**: Los controladores dependen de interfaces (abstracciones), no de implementaciones concretas. Esto facilita la sustitución de implementaciones y las pruebas unitarias.
- **Testabilidad Mejorada**: Podemos probar `ControladorJugador` de forma aislada usando mocks de `RepositorioJugador`, sin acceso real al sistema de archivos.
- **Mejor Cohesión**: Al separar la persistencia, los controladores aumentaron su cohesión y el acoplamiento global del sistema disminuyó.

### 2.3 Expansión del Modelo de Datos

#### 2.3.1 Nuevas Enumeraciones y Tipos

La Entrega 2 introdujo tipos adicionales para mejorar la expresividad del dominio:

```java
enum Direction { HORIZONTAL, VERTICAL }
enum Idioma { ESPANOL, CATALAN, INGLES }
enum Tema { CLARO, OSCURO }
enum TipoCasilla {
    NORMAL, CENTRO, LETRA_DOBLE, LETRA_TRIPLE,
    PALABRA_DOBLE, PALABRA_TRIPLE
}
```

**Impacto de las Enumeraciones:**

Las enumeraciones mejoran la type safety y eliminan constantes mágicas:

1. **Eliminación de Magic Strings**:

   - **Antes**: `if (direccion.equals("HORIZONTAL"))`
   - **Después**: `if (direccion == Direction.HORIZONTAL)`

2. **Extensibilidad**: Las enumeraciones permiten agregar comportamiento específico y garantizan switches exhaustivos.

3. **Rendimiento**: Comparaciones por referencia (==) en lugar de `.equals()`.

#### 2.3.2 Estructuras de Datos Generalizadas

```java
class Tuple<X, Y> {
    public final X x;
    public final Y y;

    public Tuple(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) { /* implementación */ }

    @Override
    public int hashCode() { /* implementación */ }
}

class Triple<A, B, C> {
    public A x;
    public B y;
    public C z;

    public Triple(A x, B y, C z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public A getx() { return x; }
    public B gety() { return y; }
    public C getz() { return z; }

    public void setFromTriple(Triple<A, B, C> triple) {
        this.x = triple.getx();
        this.y = triple.gety();
        this.z = triple.getz();
    }
}
```

**Características principales:**

- **Tuple**: Inmutable con campos finales públicos para acceso directo
- **Triple**: Mutable con getters y capacidad de copia desde otra instancia
- **Type Safety**: Evita el uso de `Object[]` o estructuras genéricas sin tipo
- **Uso específico**: Tuple para coordenadas, Triple para movimientos complejos

### 2.4 Optimización del Sistema de Ranking

#### 2.4.1 Eliminación de RankingDataProvider

Una mejora significativa fue la eliminación de la interfaz `RankingDataProvider`, simplificando las dependencias:

**Antes (Entrega 1):**

```java
class MaximaScoreStrategy {
    -dataProvider: RankingDataProvider
    +MaximaScoreStrategy(RankingDataProvider dataProvider)
}
```

**Después (Entrega 2):**

```java
class MaximaScoreStrategy {
    -ranking: Ranking  // Referencia directa
    +MaximaScoreStrategy(Ranking ranking)
}
```

**Beneficios de la simplificación:**

- **Menos interfaces**: Una interfaz menos que mantener y documentar
- **Dependencias más directas**: Las estrategias acceden directamente al objeto `Ranking`
- **Eliminación de indirección**: No hay intermediarios innecesarios entre estrategias y datos
- **Mejor cohesión**: Las estrategias se enfocan en su algoritmo específico

Esta simplificación eliminó complejidad innecesaria manteniendo la funcionalidad del sistema de ranking.

#### 2.4.2 Mejoras en PlayerRankingStats

```java
class PlayerRankingStats {
    -username: String
    -puntuaciones: List<Integer>
    -puntuacionMaxima: int
    -puntuacionMedia: double
    -partidasJugadas: int
    -victorias: int
    -puntuacionTotalAcumulada: int

    +addPuntuacionSinIncrementarPartidas(int puntuacion): void  // Nuevo método
    +actualizarEstadisticas(boolean esVictoria): void
    +setPuntuacionTotal(int puntuacionTotal): void
}
```

### 2.5 Evolución de la Gestión de Configuración

#### 2.5.1 Expansión de la Clase Configuracion

```java
class Configuracion {
    -serialVersionUID: long
    -idioma: String
    -tema: String
    -diccionario: String
    -musica: boolean
    -volumenMusica: int
    -sonido: boolean
    -volumenSonido: int
    -tamanoTablero: int

    // Métodos setter específicos para cada propiedad
    +setIdioma(String idioma): void
    +setTema(String tema): void
    +setDiccionario(String diccionario): void
}
```

### 2.6 Mejoras en el Sistema de Tablero

#### 2.6.1 Nuevas Funcionalidades

```java
class Tablero {
    -tablero: String[][]
    -bonus: Bonus[][]
    -alphabetPoint: Map<Character, Integer>
    -N: int

    +getEstadoTablero(): Map<Tuple<Integer, Integer>, String>  // Nuevo
    +setTile(Tuple<Integer, Integer> pos, String letra): void  // Mejorado
    +getTile(Tuple<Integer, Integer> pos): String
    +validPosition(Tuple<Integer, Integer> pos): boolean
}
```

---

## 3. Análisis Comparativo de Patrones de Diseño

### 3.1 Patrones Mantenidos y Evolucionados

| Patrón              | Entrega 1                      | Entrega 2                      | Evolución                                      |
| ------------------- | ------------------------------ | ------------------------------ | ---------------------------------------------- |
| **Facade**          | ✅ ControladorDomain           | ✅ ControladorDomain           | Responsabilidades más claras, mejor delegación |
| **Singleton**       | ✅ Controllers específicos     | ✅ Controllers específicos     | Mantenido con mejor encapsulación              |
| **Strategy**        | ✅ Ranking strategies          | ✅ Ranking strategies          | Simplificado, dependencias más directas        |
| **Factory**         | ✅ RankingOrderStrategyFactory | ✅ RankingOrderStrategyFactory | Mantenido, mejor integración                   |
| **Template Method** | ✅ Jugador abstracto           | ✅ Jugador abstracto           | Expandido con nuevas especializaciones         |

### 3.2 Patrones Nuevos Introducidos

| Patrón                   | Implementación                          | Beneficio Principal              |
| ------------------------ | --------------------------------------- | -------------------------------- |
| **Repository**           | Interfaces + Implementaciones concretas | Abstracción de persistencia      |
| **Dependency Injection** | Controllers reciben repositories        | Testabilidad y flexibilidad      |
| **Data Transfer Object** | Tuple, Triple para transferencia        | Encapsulación de datos complejos |

### 3.3 Patrones Eliminados o Simplificados

- **RankingDataProvider**: Eliminado para reducir complejidad
- **Dependencias circulares**: Resueltas mediante reestructuración

---

## 4. Análisis Profundo de Estructuras de Datos: Evolución hacia Eficiencia y Type Safety

### 4.1 Transformación de Collections y Mapas: De Genérico a Específico

#### 4.1.1 Entrega 1 - Estructuras Básicas y Limitaciones

```java
// Uso directo de collections estándar - Problemas identificados
Map<String, Jugador> jugadores              // Type safety básico
Set<String> jugadoresPartida               // Sin contexto semántico
List<Integer> puntuaciones                 // Datos primitivos sin estructura
Map<String, Object> configuraciones       // Type erasure problemático
```

**Problemas Identificados en la Entrega 1:**

- **Pérdida de Información Semántica**: `Set<String>` no expresa que contiene nombres de jugadores
- **Type Safety Limitado**: `Map<String, Object>` requiere casting manual y es propenso a errores
- **Falta de Validación**: Collections sin restricciones de contenido
- **Dificultad de Debugging**: Estructuras genéricas dificultan la identificación de problemas

#### 4.1.2 Entrega 2 - Estructuras Especializadas y Optimizadas

```java
// Tipado más específico y estructurado - Mejoras implementadas
Map<Tuple<Integer, Integer>, String> estadoTablero           // Coordenadas tipadas
Map<String, PlayerRankingStats> estadisticasUsuarios        // Value objects ricos
Map<Tuple<Integer, Integer>, Set<String>> crossCheck        // Estructuras complejas tipadas
Stack<Triple<DawgNode, String, DawgNode>> uncheckedNodes    // Algoritmos con tipos específicos
Map<String, List<Integer>> puntuacionesPorJugador          // Agrupación semántica
Set<Tuple<Integer, Integer>> posicionesValidas             // Coordenadas como first-class citizens
```

**Mejoras en Type Safety:**

1. **Eliminación de Casting**: Los tipos específicos evitan conversiones manuales
2. **Verificación en Compilación**: Errores detectados durante la compilación
3. **Expresividad**: `Map<Tuple<Integer, Integer>, String>` es más claro que `Map<String, Object>`

**Optimizaciones de Acceso:**

```java
// Entrega 1: Estructuras genéricas
Map<String, Object> configuraciones;
String valor = (String) configuraciones.get("clave"); // Casting requerido

// Entrega 2: Tipos específicos
Map<String, PlayerRankingStats> estadisticas;
PlayerRankingStats stats = estadisticas.get(nombre); // Sin casting
```

### 4.2 Separación de Responsabilidades: Controladores vs Persistencia

#### 4.2.1 Entrega 1: Responsabilidades Mezcladas

En la primera entrega, los controladores manejaban tanto lógica de dominio como persistencia:

```java
class ControladorJugador {
    private static final String JUGADORES_FILE = "jugadores.dat";
    private Map<String, Jugador> jugadores;

    // Lógica de dominio mezclada con persistencia
    private void guardarDatos() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(JUGADORES_FILE))) {
            oos.writeObject(jugadores);
        } catch (IOException e) {
            // Manejo de errores en el controlador
        }
    }
}
```

**Problemas identificados:**

- Violación del Principio de Responsabilidad Única
- Acoplamiento fuerte entre lógica de negocio y persistencia
- Dificultades para testing unitario
- Imposibilidad de cambiar mecanismos de almacenamiento sin modificar controladores

#### 4.2.2 Entrega 2: Separación Clara de Responsabilidades

La segunda entrega introdujo el patrón Repository para separar estas responsabilidades:

```java
// Controlador enfocado en lógica de dominio
class ControladorJugador {
    private Map<String, Jugador> jugadores;
    private RepositorioJugador repositorioJugador;

    public ControladorJugador(RepositorioJugador repositorioJugador) {
        this.repositorioJugador = repositorioJugador;
        this.jugadores = new HashMap<>();
        cargarDatos();
    }

    public boolean registrarUsuario(String nombre) {
        if (existeJugador(nombre)) return false;

        JugadorHumano nuevoJugador = new JugadorHumano(nombre);
        jugadores.put(nombre, nuevoJugador);
        guardarDatos();
        return true;
    }

    // Persistencia delegada al repository
    private void guardarDatos() {
        repositorioJugador.guardarTodos(jugadores);
    }
}

// Repository especializado en persistencia
class RepositorioJugadorImpl implements RepositorioJugador {
    private static final String JUGADORES_FILE = "src/main/resources/persistencias/jugadores.dat";

    @Override
    public boolean guardarTodos(Map<String, Jugador> jugadores) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(JUGADORES_FILE))) {
            oos.writeObject(jugadores);
            return true;
        } catch (IOException e) {
            System.err.println("Error al guardar jugadores: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Map<String, Jugador> cargarTodos() {
        // Implementación de carga con manejo de errores
        File file = new File(JUGADORES_FILE);
        if (!file.exists()) return new HashMap<>();

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(JUGADORES_FILE))) {
            return (Map<String, Jugador>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new HashMap<>();
        }
    }
}
```

### 4.4 Beneficios de la Separación de Responsabilidades

#### 4.4.1 Mejoras en Testabilidad

La separación permite testing unitario efectivo:

```java
@Test
public void testRegistrarUsuario() {
    RepositorioJugador mockRepo = mock(RepositorioJugador.class);
    ControladorJugador controller = new ControladorJugador(mockRepo);

    when(mockRepo.cargarTodos()).thenReturn(new HashMap<>());
    boolean resultado = controller.registrarUsuario("TestUser");

    assertTrue(resultado);
    verify(mockRepo).guardarTodos(any());
}
```

#### 4.4.2 Flexibilidad en Persistencia

El patrón Repository permite cambiar implementaciones sin afectar controladores:

- **Actual**: Serialización Java a archivos
- **Futuro**: Cualquier otro tipo de base de datos
- **Testing**: Implementación en memoria

#### 4.4.3 Mejor Cohesión y Menor Acoplamiento

- **Controladores**: Enfocados únicamente en lógica de dominio
- **Repositories**: Especializados en persistencia y acceso a datos
- **Interfaces**: Contratos claros entre capas

---

## 5. Impacto en Principios de Ingeniería de Software

### 5.1 Aplicación de Principios SOLID

#### 5.1.1 Single Responsibility Principle (SRP)

**Entrega 1**: Violado - Controladores manejaban lógica + persistencia
**Entrega 2**: Cumplido - Separación clara: Controllers (lógica) + Repositories (persistencia)

#### 5.1.2 Open/Closed Principle (OCP)

**Entrega 2**: Nuevas implementaciones de Repository sin modificar Controllers existentes

#### 5.1.3 Liskov Substitution Principle (LSP)

**Ambas entregas**: Cumplido en jerarquías de Jugador y estrategias de Ranking

#### 5.1.4 Interface Segregation Principle (ISP)

**Entrega 2**: Interfaces Repository específicas por dominio

#### 5.1.5 Dependency Inversion Principle (DIP)

**Entrega 2**: Controllers dependen de abstracciones (interfaces Repository)

---

## 6. Conclusiones y Análisis de Impacto

### 6.1 Mejoras Arquitectónicas Principales

La transformación entre entregas ha resultado en mejoras significativas en varios aspectos:

### 6.2 Transformación de Acoplamiento y Cohesión

#### 6.2.1 Cambios en Acoplamiento

**Acoplamiento Eliminado:**

- Controladores ↔ Detalles de persistencia
- Lógica de negocio ↔ Formato de datos
- Testing ↔ Sistema de archivos

**Acoplamiento Introducido:**

- Controladores ↔ Interfaces Repository (hacia abstracciones)

#### 6.2.2 Mejoras en Cohesión

**Controladores:** Cada controlador maneja exclusivamente su dominio, con operaciones cohesivas agrupadas lógicamente.

**Repositorios:** Un repositorio por entidad, con operaciones CRUD cohesivas y validación centralizada.

#### 6.2.3 Aplicación de Principios SOLID

| Principio | Entrega 1                       | Entrega 2                        |
| --------- | ------------------------------- | -------------------------------- |
| **SRP**   | Violado en controladores        | Cumplido estrictamente           |
| **OCP**   | Parcialmente cumplido           | Extensible sin modificación      |
| **LSP**   | Cumplido en jerarquías          | Mantenido y mejorado             |
| **ISP**   | Interfaces monolíticas          | Interfaces específicas           |
| **DIP**   | Dependencias hacia concreciones | Dependencias hacia abstracciones |
