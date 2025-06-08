# Documentación de clases Java del Dominio

# **SCRABBLE** **Grupo 41** **Hongda Zhu (hongda.zhu@estudiantat.upc.edu)** **Shenghao Ye (shenghao.ye@estudiantat.upc.edu)** **Songhe Wang (songhe.wang@estudiantat.upc.edu)** **Xuanyi Qiu (xuanyi.qiu@estudiantat.upc.edu)**

## Índice

[**1. Introducción**](#1-introducción)

[**2. Capa de Dominio**](#2-capa-de-dominio)

[**2.1 Controladores**](#21-controladores)

[2.1.1. ControladorConfiguracion](#211-controladorconfiguración)

[2.1.2. ControladorDiccionario](#212-controladordiccionario)

[2.1.3. ControladorJuego](#213-controladorjuego)

[2.1.4. ControladorJugador](#214-controladorjugador)

[2.1.5. ControladorRanking](#215-controladorranking)

[2.1.6. ControladorDomain](#216-controladordomain)

[**2.2 Modelos**](#22-modelos)

[2.2.1. Jugador](#221-jugador)

[2.2.2. JugadorHumano](#222-jugadorhumano)

[2.2.3. JugadorIA](#223-jugadoria)

[2.2.4. Tablero](#224-tablero)

[2.2.5. Bolsa](#225-bolsa)

[2.2.6. Ranking](#226-ranking)

[2.2.7. Diccionario](#227-diccionario)

[2.2.8. Dawg](#228-dawg)

[2.2.9. DawgNode](#229-dawgnode)

[2.2.10. Configuracion](#2210-configuracion)

[**2.3 Persistencias**](#23-persistencias)

[2.3.1. Interfaces](#231-interfaces)

[2.3.2. Implementaciones](#232-implementaciones)

[**2.4 Excepciones**](#24-excepciones)

[2.4.1. Excepciones de Usuario](#241-excepciones-de-usuario)

[2.4.2. Excepciones de Partida](#242-excepciones-de-partida)

[2.4.3. Excepciones de Gestión](#243-excepciones-de-gestión)

[**2.5 Helpers**](#25-helpers)

[2.5.1. Estructuras de Datos](#251-estructuras-de-datos)

[2.5.2. Enumeraciones](#252-enumeraciones)

---

# **1. Introducción**

En este documento se encuentran las descripciones detalladas de todas las clases del dominio de la aplicación Scrabble, incluyendo controladores, modelos, persistencias, excepciones y clases auxiliares. Cada clase está documentada con su cardinalidad, descripción de atributos y relaciones que ayudan a la comprensión de la arquitectura del sistema.

La capa de dominio implementa el patrón de arquitectura por capas, donde los controladores gestionan la lógica de negocio, los modelos representan las entidades del juego, las persistencias manejan el almacenamiento de datos, las excepciones proporcionan manejo de errores específico del dominio, y las clases helper ofrecen utilidades reutilizables.

El diseño sigue principios SOLID y patrones de diseño como Singleton, Repository, Strategy y Facade para garantizar un código mantenible, extensible y bien estructurado.

---

# **2. Capa de Dominio**

## **2.1 Controladores**

### **2.1.1. ControladorConfiguracion**

• **Nom de la classe**: ControladorConfiguracion

• **Breu descripció de la classe**: Controlador para la gestión de la configuración de la aplicación. Encapsula el acceso a los parámetros
configurables como tema visual, configuración de audio, diccionario por defecto y tamaño del tablero.

• **Cardinalitat**: Una instancia por aplicación (no singleton, pero típicamente una sola instancia activa)

• **Descripció dels atributs**:

- repositorio: Repositorio para manejar la persistencia de la configuración (no static)

• **Descripció de les relacions**:

- Relació d'agregació amb la classe "RepositorioConfiguracion": utiliza el repositorio para persistir y cargar configuraciones
- Relació d'associació amb la classe "Configuracion": gestiona objetos de configuración del sistema

### **2.1.2. ControladorDiccionario**

• **Nom de la classe**: ControladorDiccionario

• **Breu descripció de la classe**: Controlador para la gestión de diccionarios en el juego de Scrabble. Implementa el patrón Singleton y es responsable de la lectura/escritura de archivos relacionados con diccionarios, validación de palabras y gestión de estructuras DAWG.

• **Cardinalitat**: Una única instancia por aplicación (patrón Singleton)

• **Descripció dels atributs**:

- instance: Instancia única del controlador (static)
- diccionarios: Mapa de diccionarios cargados en memoria (no static)
- diccionarioPaths: Mapa de rutas de diccionarios (no static)
- repositorio: Repositorio para persistencia de diccionarios (no static)

• **Descripció de les relacions**:

- Relació d'agregació amb la classe "Diccionario": gestiona múltiples diccionarios del sistema
- Relació d'agregació amb la classe "RepositorioDiccionario": utiliza el repositorio para persistencia
- Relació d'associació amb la classe "ControladorJuego": proporciona servicios de diccionario para el juego

### **2.1.3. ControladorJuego**

• **Nom de la classe**: ControladorJuego

• **Breu descripció de la classe**: Controlador principal para la gestión completa de partidas de Scrabble. Centraliza toda la lógica de juego, incluyendo gestión del tablero, validación de movimientos, cálculo de puntuaciones, manejo de turnos y persistencia del estado de la partida.

• **Cardinalitat**: Una instancia por partida activa

• **Descripció dels atributs**:

- serialVersionUID: Identificador de versión para serialización (static final)
- idPartida: Identificador de la partida actual (no static)
- controladorDiccionario: Controlador de diccionarios (no static, transient)
- tablero: Tablero de juego actual (no static)
- bolsa: Bolsa de fichas de la partida (no static)
- direction: Dirección actual del juego (no static)
- juegoTerminado: Estado de finalización del juego (no static)
- juegoIniciado: Estado de inicio del juego (no static)
- lastCrossCheck: Última verificación cruzada realizada (no static)
- nombreDiccionario: Nombre del diccionario utilizado (no static)
- jugadores: Mapa de jugadores y sus puntuaciones (no static)
- repositorioPartida: Repositorio para persistencia de partidas (static)
- alfabeto: Conjunto de letras del alfabeto (no static)
- jugaoresOrdenados: Conjunto de jugadores ordenados segun el turno actual y progreso de la partida (para cargar partidas)
- turnoActual: Guarda el índice del jugador que le toca jugadar en la lista del atributo anterior.

• **Descripció de les relacions**:

- Relació d'agregació amb la classe "Tablero": gestiona el tablero de juego
- Relació d'agregació amb la classe "Bolsa": gestiona la bolsa de fichas
- Relació d'associació amb la classe "ControladorDiccionario": utiliza servicios de diccionario
- Relació d'agregació amb la classe "RepositorioPartida": utiliza el repositorio para persistencia
- Relació d'associació amb múltiples clases helper (Triple, Tuple, Direction, Dificultad): utiliza estructuras auxiliares

### **2.1.4. ControladorJugador**

• **Nom de la classe**: ControladorJugador

• **Breu descripció de la classe**: Controlador para la gestión de jugadores en el sistema. Implementa el patrón Singleton y centraliza toda la gestión de jugadores humanos e IA, incluyendo registro, eliminación, gestión de racks de fichas y seguimiento de puntuaciones.

• **Cardinalitat**: Una única instancia por aplicación (patrón Singleton)

• **Descripció dels atributs**:

- instance: Instancia única del controlador (static)
- jugadores: Mapa de jugadores registrados (no static)
- repositorioJugador: Repositorio para persistencia de jugadores (no static)

• **Descripció de les relacions**:

- Relació d'agregació amb la classe "Jugador": gestiona múltiples jugadores del sistema
- Relació d'agregació amb la classe "JugadorHumano": gestiona jugadores humanos específicamente
- Relació d'agregació amb la classe "JugadorIA": gestiona jugadores IA específicamente
- Relació d'agregació amb la classe "RepositorioJugador": utiliza el repositorio para persistencia
- Relació d'associació amb la classe "Dificultad": gestiona niveles de dificultad para IA

### **2.1.5. ControladorRanking**

• **Nom de la classe**: ControladorRanking

• **Breu descripció de la classe**: Controlador para la gestión del ranking de jugadores. Implementa el patrón Singleton y delega toda la lógica de negocio al modelo Ranking. Gestiona operaciones relacionadas con el sistema de ranking, incluyendo puntuaciones, estadísticas y diferentes estrategias de ordenación.

• **Cardinalitat**: Una única instancia por aplicación (patrón Singleton)

• **Descripció dels atributs**:

- instance: Instancia única del controlador (static, transient)
- ranking: Modelo de ranking que contiene la lógica de negocio (no static)
- repositorioRanking: Repositorio para persistencia del ranking (no static)

• **Descripció de les relacions**:

- Relació d'agregació amb la classe "Ranking": delega la lógica de negocio al modelo
- Relació d'agregació amb la classe "RepositorioRanking": utiliza el repositorio para persistencia
- Relació d'associació amb la classe "ControladorJugador": coordina con la gestión de jugadores

### **2.1.6. ControladorDomain**

• **Nom de la classe**: ControladorDomain

• **Breu descripció de la classe**: Controlador principal que implementa el patrón Facade proporcionando un punto de entrada único al dominio. Coordina todos los subcontroladores especializados y gestiona el flujo de datos entre las diferentes capas del sistema.

• **Cardinalitat**: Una única instancia por aplicación (patrón Singleton)

• **Descripció dels atributs**:

- controladorConfiguracion: Controlador de configuración del sistema (no static)
- controladorJuego: Controlador de lógica de juego (no static)
- controladorRanking: Controlador de ranking de jugadores (no static)
- controladorJugador: Controlador de gestión de jugadores (no static)
- controladorDiccionario: Controlador de gestión de diccionarios (no static)
- instance: Instancia única del controlador (static)

• **Descripció de les relacions**:

- Relació d'agregació amb la classe "ControladorConfiguracion": coordina la gestión de configuración
- Relació d'agregació amb la classe "ControladorJuego": coordina la lógica de juego
- Relació d'agregació amb la classe "ControladorRanking": coordina el sistema de ranking
- Relació d'agregació amb la classe "ControladorJugador": coordina la gestión de jugadores
- Relació d'agregació amb la classe "ControladorDiccionario": coordina la gestión de diccionarios
- Relació d'associació amb múltiples clases helper: utiliza estructuras auxiliares para operaciones

---

## **2.2 Modelos**

### **2.2.1. Jugador**

• **Nom de la classe**: Jugador
• **Breu descripció de la classe**: Clase abstracta que define el comportamiento común de todos los jugadores, incluyendo la gestión de puntuaciones, atril de fichas (rack) y estadísticas base. Actúa como clase base para JugadorHumano y JugadorIA.

• **Cardinalitat**: Clase abstracta, no se instancia directamente

• **Descripció dels atributs**:

- serialVersionUID: Identificador de versión para serialización (static final)
- rack: Mapa de fichas disponibles del jugador (no static)
- skipTrack: Contador de turnos pasados consecutivos (no static)
- nombre: Nombre único del jugador (no static)

• **Descripció de les relacions**:

- Relació d'herència amb les classes "JugadorHumano" i "JugadorIA": clase padre que define comportamiento común
- Relació d'associació amb la classe "ControladorJugador": es gestionado por el controlador

### **2.2.2. JugadorHumano**

• **Nom de la classe**: JugadorHumano

• **Breu descripció de la classe**: Implementación concreta para jugadores humanos. Extiende Jugador añadiendo funcionalidad específica como el estado de participación en partidas y el nombre de la partida actual.

• **Cardinalitat**: Una instancia por cada jugador humano registrado en el sistema

• **Descripció dels atributs**:

- enPartida: Indica si el jugador está actualmente en una partida (no static)
- nombrePartidaActual: Nombre de la partida actual si está jugando (no static)

• **Descripció de les relacions**:

- Relació d'herència amb la classe "Jugador": extiende la funcionalidad base
- Relació d'associació amb la classe "ControladorJugador": es gestionado específicamente como jugador humano

### **2.2.3. JugadorIA**

• **Nom de la classe**: JugadorIA

• **Breu descripció de la classe**: Implementación para jugadores controlados por la inteligencia artificial, con diferentes niveles de dificultad. Incluye generación automática de nombres únicos para IAs y gestión del nivel de dificultad.

• **Cardinalitat**: Una instancia por cada jugador IA creado para partidas específicas

• **Descripció dels atributs**:

- serialVersionUID: Identificador de versión para serialización (static final)
- contadorIAs: Contador estático para generar nombres únicos (static)
- nivelDificultad: Nivel de dificultad de la IA (no static)

• **Descripció de les relacions**:

- Relació d'herència amb la classe "Jugador": extiende la funcionalidad base
- Relació d'associació amb la classe "Dificultad": utiliza enum para definir nivel
- Relació d'associació amb la classe "ControladorJugador": es gestionado específicamente como jugador IA

### **2.2.4. Tablero**

• **Nom de la classe**: Tablero

• **Breu descripció de la classe**: Representa el tablero de juego, incluyendo la gestión de casillas, multiplicadores de bonificación y la validación de la colocación de palabras. Mantiene dos matrices: una para las fichas colocadas y otra para las bonificaciones.

• **Cardinalitat**: Una instancia por partida activa

• **Descripció dels atributs**:

- serialVersionUID: Identificador de versión para serialización (static final)
- tablero: Matriz de fichas colocadas en el tablero (no static)
- bonus: Matriz de bonificaciones de las casillas (no static)
- alphabetPoint: Mapa de puntos por carácter del alfabeto (no static)
- N: Tamaño del tablero (NxN) (no static)

• **Descripció de les relacions**:

- Relació d'associació amb la classe "TipoCasilla": utiliza enum para tipos de bonificación
- Relació d'associació amb la classe "Tuple": utiliza para representar coordenadas
- Relació d'agregació amb la classe "ControladorJuego": es gestionado por el controlador de juego

### **2.2.5. Bolsa**

• **Nom de la classe**: Bolsa

• **Breu descripció de la classe**: Implementa la bolsa de fichas del juego, controla la distribución y extracción aleatoria de letras. Se inicializa con una distribución específica de fichas según el idioma del diccionario.

• **Cardinalitat**: Una instancia por partida activa

• **Descripció dels atributs**:

- serialVersionUID: Identificador de versión para serialización (static final)
- fichas: Lista de fichas disponibles en la bolsa (no static)

• **Descripció de les relacions**:

- Relació d'agregació amb la classe "ControladorJuego": es gestionada por el controlador de juego
- Relació d'associació amb la classe "Diccionario": se inicializa según la distribución del diccionario

### **2.2.6. Ranking**

• **Nom de la classe**: Ranking

• **Breu descripció de la classe**: Gestiona el almacenamiento de las estadísticas de los jugadores utilizando objetos PlayerRankingStats y aplica diferentes estrategias de ordenación mediante RankingOrderStrategy. Implementa el patrón Strategy para múltiples criterios de ordenación.

• **Cardinalitat**: Una instancia única por aplicación (gestionada por ControladorRanking)

• **Descripció dels atributs**:

- serialVersionUID: Identificador de versión para serialización (static final)
- estadisticasUsuarios: Mapa de estadísticas de jugadores (no static)
- estrategiaActual: Estrategia actual de ordenación (no static, transient)
- nombreEstrategiaActual: Nombre de la estrategia actual para persistencia (no static)

• **Descripció de les relacions**:

- Relació d'agregació amb la classe "PlayerRankingStats": contiene estadísticas de jugadores
- Relació d'associació amb la classe "RankingOrderStrategy": utiliza patrón Strategy para ordenación
- Relació d'agregació amb la classe "ControladorRanking": es gestionado por el controlador

### **2.2.7. Diccionario**

• **Nom de la classe**: Diccionario

• **Breu descripció de la classe**: Representa un diccionario de palabras válidas, utilizando una estructura DAWG para búsquedas y validaciones eficientes. Gestiona el alfabeto del idioma, la distribución de fichas y los caracteres comodín.

• **Cardinalitat**: Una instancia por cada diccionario cargado en el sistema

• **Descripció dels atributs**:

- serialVersionUID: Identificador de versión para serialización (static final)
- dawg: Estructura DAWG para validación de palabras (no static)
- alphabet: Mapa del alfabeto con puntuaciones (no static)
- bag: Distribución de fichas del diccionario (no static)
- comodines: Conjunto de caracteres comodín (no static)

• **Descripció de les relacions**:

- Relació d'agregació amb la classe "Dawg": utiliza estructura DAWG para validaciones
- Relació d'agregació amb la classe "ControladorDiccionario": es gestionado por el controlador
- Relació d'associació amb la classe "ControladorJuego": proporciona servicios de validación

### **2.2.8. Dawg**

• **Nom de la classe**: Dawg

• **Breu descripció de la classe**: Implementa un Grafo Acíclico Dirigido de Palabras para validar palabras de manera eficiente con uso óptimo de memoria. Permite compartir sufijos comunes entre palabras, reduciendo el espacio requerido.

• **Cardinalitat**: Una instancia por cada diccionario que requiere validación eficiente

• **Descripció dels atributs**:

- root: Nodo raíz del DAWG (final, no static)
- minimizedNodes: Mapa de nodos minimizados para optimización (no static)
- uncheckedNodes: Pila de nodos no verificados durante construcción (no static)
- previousWord: Palabra anterior procesada para minimización incremental (no static)

• **Descripció de les relacions**:

- Relació d'agregació amb la classe "DawgNode": compuesto por múltiples nodos
- Relació d'agregació amb la classe "Diccionario": es utilizado por diccionarios para validación

### **2.2.9. DawgNode**

• **Nom de la classe**: DawgNode

• **Breu descripció de la classe**: Representa un nodo individual en la estructura DAWG. Cada nodo puede tener múltiples aristas hacia otros nodos y puede ser marcado como final de palabra.

• **Cardinalitat**: Múltiples instancias por cada DAWG, una por cada estado único

• **Descripció dels atributs**:

- edges: Mapa de aristas hacia otros nodos (no static)
- isFinal: Indica si el nodo representa el final de una palabra válida (no static)

• **Descripció de les relacions**:

- Relació d'agregació amb la classe "Dawg": forma parte de la estructura DAWG
- Relació d'associació amb si mateix: los nodos se conectan entre sí mediante aristas

### **2.2.10. Configuracion**

• **Nom de la classe**: Configuracion

• **Breu descripció de la classe**: Encapsula todas las configuraciones del sistema incluyendo tema visual, configuraciones de audio, diccionario por defecto y tamaño de tablero. Proporciona validación de valores y configuraciones por defecto.

• **Cardinalitat**: Una instancia por aplicación (gestionada por ControladorConfiguracion)

• **Descripció dels atributs**:

- serialVersionUID: Identificador de versión para serialización (static final)
- tema: Tema visual seleccionado (no static)
- diccionario: Diccionario por defecto (no static)
- musica: Estado de activación de música (no static)
- volumenMusica: Nivel de volumen de música (no static)
- sonido: Estado de activación de sonido (no static)
- volumenSonido: Nivel de volumen de sonido (no static)
- tamanoTablero: Tamaño de tablero por defecto (no static)

• **Descripció de les relacions**:

- Relació d'agregació amb la classe "ControladorConfiguracion": es gestionada por el controlador
- Relació d'associació amb la classe "Tema": utiliza enums para configuraciones

---

## **2.3 Persistencias**

### **2.3.1. Interfaces**

#### **2.3.1.1. RepositorioConfiguracion**

• **Nom de la classe**: RepositorioConfiguracion

• **Breu descripció de la classe**: Interfaz que define las operaciones de persistencia para la configuración del sistema. Establece el contrato para guardar y cargar objetos de configuración, implementando el patrón Repository para abstraer los detalles de persistencia.

• **Cardinalitat**: Una interfaz implementada por una clase concreta por aplicación

• **Descripció dels atributs**: No tiene atributos (es una interfaz)

• **Descripció de les relacions**:

- Relació d'implementació amb la classe "RepositorioConfiguracionImpl": define el contrato implementado por la clase concreta
- Relació d'associació amb la classe "Configuracion": define operaciones para persistir objetos de configuración

#### **2.3.1.2. RepositorioDiccionario**

• **Nom de la classe**: RepositorioDiccionario

• **Breu descripció de la classe**: Interfaz que define las operaciones de persistencia para diccionarios. Establece el contrato para gestión CRUD completa, gestión de índices, validación de integridad y verificación de estructuras DAWG.

• **Cardinalitat**: Una interfaz implementada por una clase concreta por aplicación

• **Descripció dels atributs**: No tiene atributos (es una interfaz)

• **Descripció de les relacions**:

- Relació d'implementació amb la classe "RepositorioDiccionarioImpl": define el contrato implementado por la clase concreta
- Relació d'associació amb la classe "Diccionario": define operaciones para persistir diccionarios

#### **2.3.1.3. RepositorioJugador**

• **Nom de la classe**: RepositorioJugador

• **Breu descripció de la classe**: Interfaz que define las operaciones de persistencia para jugadores. Establece el contrato para gestión de jugadores humanos e IA, incluyendo operaciones de búsqueda, filtrado por tipo y consultas especializadas.

• **Cardinalitat**: Una interfaz implementada por una clase concreta por aplicación

• **Descripció dels atributs**: No tiene atributos (es una interfaz)

• **Descripció de les relacions**:

- Relació d'implementació amb la classe "RepositorioJugadorImpl": define el contrato implementado por la clase concreta
- Relació d'associació amb la classe "Jugador": define operaciones para persistir jugadores

#### **2.3.1.4. RepositorioPartida**

• **Nom de la classe**: RepositorioPartida

• **Breu descripció de la classe**: Interfaz que define las operaciones de persistencia para partidas. Establece el contrato para operaciones CRUD completas, generación automática de IDs únicos, serialización de estados complejos y gestión de errores específicos.

• **Cardinalitat**: Una interfaz implementada por una clase concreta por aplicación
• **Descripció dels atributs**: No tiene atributos (es una interfaz)
• **Descripció de les relacions**:

- Relació d'implementació amb la classe "RepositorioPartidaImpl": define el contrato implementado por la clase concreta
- Relació d'associació amb la classe "ControladorJuego": define operaciones para persistir estados de partida

#### **2.3.1.5. RepositorioRanking**

• **Nom de la classe**: RepositorioRanking

• **Breu descripció de la classe**: Interfaz que define las operaciones de persistencia para el ranking. Establece el contrato para gestión de estadísticas de jugadores, consultas especializadas, generación de rankings ordenados y actualización incremental.

• **Cardinalitat**: Una interfaz implementada por una clase concreta por aplicación

• **Descripció dels atributs**: No tiene atributos (es una interfaz)

• **Descripció de les relacions**:

- Relació d'implementació amb la classe "RepositorioRankingImpl": define el contrato implementado por la clase concreta
- Relació d'associació amb la classe "Ranking": define operaciones para persistir rankings
- Relació d'associació amb la classe "PlayerRankingStats": define operaciones para estadísticas de jugadores

### **2.3.2. Implementaciones**

#### **2.3.2.1. RepositorioConfiguracionImpl**

• **Nom de la classe**: RepositorioConfiguracionImpl

• **Breu descripció de la classe**: Implementación concreta del repositorio de configuración que gestiona la persistencia completa utilizando serialización Java. Maneja configuraciones de tema visual, audio, diccionario por defecto y tamaño del tablero en archivo binario.

• **Cardinalitat**: Una instancia por aplicación

• **Descripció dels atributs**:

- CONFIG_FILE: Ruta del archivo de configuración (static final)

• **Descripció de les relacions**:

- Relació d'implementació amb la interfície "RepositorioConfiguracion": implementa el contrato definido
- Relació d'associació amb la classe "Configuracion": persiste y carga objetos de configuración

#### **2.3.2.2. RepositorioDiccionarioImpl**

• **Nom de la classe**: RepositorioDiccionarioImpl

• **Breu descripció de la classe**: Implementación del repositorio de diccionarios con gestión completa de estructuras DAWG. Utiliza enfoque híbrido con índice centralizado y almacenamiento directo de archivos de diccionario, validación de integridad y gestión robusta de errores.

• **Cardinalitat**: Una instancia por aplicación

• **Descripció dels atributs**:

- DICCIONARIOS_INDEX_FILE: Ruta del archivo índice de diccionarios (static final)

• **Descripció de les relacions**:

- Relació d'implementació amb la interfície "RepositorioDiccionario": implementa el contrato definido
- Relació d'associació amb la classe "Diccionario": persiste y carga diccionarios
- Relació d'associació amb classes del sistema de archivos: gestiona archivos alpha.txt y words.txt

#### **2.3.2.3. RepositorioJugadorImpl**

• **Nom de la classe**: RepositorioJugadorImpl

• **Breu descripció de la classe**: Implementación del repositorio de jugadores con gestión completa de usuarios humanos e IA. Utiliza serialización Java para persistir mapas de jugadores, proporciona operaciones especializadas de filtrado y consultas optimizadas.

• **Cardinalitat**: Una instancia por aplicación

• **Descripció dels atributs**:

- JUGADORES_FILE: Ruta del archivo de jugadores (static final)

• **Descripció de les relacions**:

- Relació d'implementació amb la interfície "RepositorioJugador": implementa el contrato definido
- Relació d'associació amb la classe "Jugador": persiste y carga jugadores
- Relació d'associació amb les classes "JugadorHumano" i "JugadorIA": gestiona ambos tipos de jugadores

#### **2.3.2.4. RepositorioPartidaImpl**

• **Nom de la classe**: RepositorioPartidaImpl

• **Breu descripció de la classe**: Implementación del repositorio de partidas con gestión completa de estados de juego. Utiliza serialización Java para persistir objetos ControladorJuego, genera IDs únicos automáticamente y maneja errores específicos de persistencia.

• **Cardinalitat**: Una instancia por aplicación

• **Descripció dels atributs**:

- DIRECTORIO_PERSISTENCIA: Directorio base de persistencia (static final)
- ARCHIVO_PARTIDAS: Ruta del archivo de partidas (static final)

• **Descripció de les relacions**:

- Relació d'implementació amb la interfície "RepositorioPartida": implementa el contrato definido
- Relació d'associació amb la classe "ControladorJuego": persiste y carga estados completos de partida
- Relació d'associació amb la classe "ExceptionPersistenciaFallida": lanza excepciones específicas

#### **2.3.2.5. RepositorioRankingImpl**

• **Nom de la classe**: RepositorioRankingImpl

• **Breu descripció de la classe**: Implementación del repositorio de ranking que gestiona la persistencia del objeto Ranking completo utilizando serialización Java. Proporciona operaciones específicas para consultar estadísticas individuales y delega la lógica de negocio al modelo.

• **Cardinalitat**: Una instancia por aplicación

• **Descripció dels atributs**:

- RANKING_FILE: Ruta del archivo de ranking (static final)

• **Descripció de les relacions**:

- Relació d'implementació amb la interfície "RepositorioRanking": implementa el contrato definido
- Relació d'associació amb la classe "Ranking": persiste y carga objetos de ranking
- Relació d'associació amb la classe "PlayerRankingStats": gestiona estadísticas de jugadores

---

## **2.4 Excepciones**

### **2.4.1. Excepciones de Usuario**

#### **2.4.1.1. ExceptionUserExist**

• **Nom de la classe**: ExceptionUserExist

• **Breu descripció de la classe**: Excepción lanzada cuando se intenta crear un usuario que ya existe en el sistema. Se produce durante el registro de usuarios con nombres duplicados y forma parte del sistema de validación de unicidad.

• **Cardinalitat**: Se instancia cada vez que ocurre el error específico

• **Descripció dels atributs**:

- serialVersionUID: Identificador de versión para serialización (static final)

• **Descripció de les relacions**:

- Relació d'herència amb la classe "RuntimeException": extiende la funcionalidad de excepciones en tiempo de ejecución
- Relació d'associació amb controladores de usuario: es lanzada por operaciones de gestión de usuarios

#### **2.4.1.2. ExceptionUserNotExist**

• **Nom de la classe**: ExceptionUserNotExist

• **Breu descripció de la classe**: Excepción lanzada cuando se intenta acceder a un usuario que no existe en el sistema. Se produce durante operaciones sobre jugadores no registrados o eliminados, formando parte del sistema de validación de existencia.

• **Cardinalitat**: Se instancia cada vez que ocurre el error específico

• **Descripció dels atributs**:

- serialVersionUID: Identificador de versión para serialización (static final)

• **Descripció de les relacions**:

- Relació d'herència amb la classe "RuntimeException": extiende la funcionalidad de excepciones en tiempo de ejecución
- Relació d'associació amb controladores de usuario: es lanzada por operaciones de búsqueda y acceso

#### **2.4.1.3. ExceptionUserInGame**

• **Nom de la classe**: ExceptionUserInGame

• **Breu descripció de la classe**: Excepción lanzada cuando se intenta realizar una operación con un usuario que ya está participando en una partida. Forma parte del sistema de control de estado de usuarios y gestión de partidas concurrentes.

• **Cardinalitat**: Se instancia cada vez que ocurre el error específico

• **Descripció dels atributs**:

- serialVersionUID: Identificador de versión para serialización (static final)

• **Descripció de les relacions**:

- Relació d'herència amb la clase "RuntimeException": extiende la funcionalidad de excepciones en tiempo de ejecución
- Relació d'associació amb controladores de juego: es lanzada por operaciones de gestión de partidas

#### **2.4.1.4. ExceptionUserEsIA**

• **Nom de la classe**: ExceptionUserEsIA

• **Breu descripció de la classe**: Excepción lanzada cuando se intenta realizar una operación no permitida con un usuario IA. Se produce cuando se ejecutan operaciones restringidas para jugadores artificiales, como eliminación o modificación de configuraciones específicas.

• **Cardinalitat**: Se instancia cada vez que ocurre el error específico

• **Descripció dels atributs**:

- serialVersionUID: Identificador de versión para serialización (static final)

• **Descripció de les relacions**:

- Relació d'herència amb la clase "RuntimeException": extiende la funcionalidad de excepciones en tiempo de ejecución
- Relació d'associació amb la clase "JugadorIA": es lanzada por operaciones específicas sobre jugadores IA

#### **2.4.1.5. ExceptionUserLoggedIn**

• **Nom de la classe**: ExceptionUserLoggedIn

• **Breu descripció de la classe**: Excepción lanzada cuando se detecta un conflicto de estado de sesión de usuario. Se produce cuando se intenta realizar operaciones que entran en conflicto con el estado actual de autenticación del usuario.

• **Cardinalitat**: Se instancia cada vez que ocurre el error específico

• **Descripció dels atributs**:

- serialVersionUID: Identificador de versión para serialización (static final)

• **Descripció de les relacions**:

- Relació d'herència amb la clase "RuntimeException": extiende la funcionalidad de excepciones en tiempo de ejecución
- Relació d'associació amb sistemas de autenticación: es lanzada por operaciones de gestión de sesiones

### **2.4.2. Excepciones de Partida y Palabras**

#### **2.4.2.1. ExceptionNotEnoughTiles**

• **Nom de la classe**: ExceptionNotEnoughTiles

• **Breu descripció de la classe**: Excepción lanzada cuando no hay suficientes fichas disponibles para completar una operación. Es fundamental para el control del flujo del juego y la validación de movimientos cuando se agotan las fichas.

• **Cardinalitat**: Se instancia cada vez que ocurre el error específico

• **Descripció dels atributs**:

- serialVersionUID: Identificador de versión para serialización (static final)

• **Descripció de les relacions**:

- Relació d'herència amb la clase "RuntimeException": extiende la funcionalidad de excepciones en tiempo de ejecución
- Relació d'associació amb la clase "Bolsa": es lanzada cuando la bolsa no tiene fichas suficientes
- Relació d'associació amb la clase "ControladorJuego": es lanzada durante operaciones de juego

#### **2.4.2.2. ExceptionPalabraInvalida**

• **Nom de la classe**: ExceptionPalabraInvalida

• **Breu descripció de la classe**: Excepción lanzada cuando una palabra contiene caracteres no válidos para el diccionario. Se produce cuando se procesan palabras con caracteres fuera del alfabeto del diccionario actual o que no cumplen las reglas de validación.

• **Cardinalitat**: Se instancia cada vez que ocurre el error específico

• **Descripció dels atributs**:

- serialVersionUID: Identificador de versión para serialización (static final)

• **Descripció de les relacions**:

- Relació d'herència amb la clase "RuntimeException": extiende la funcionalidad de excepciones en tiempo de ejecución
- Relació d'associació amb la clase "Diccionario": es lanzada durante validaciones de palabras

#### **2.4.2.3. ExceptionPalabraExist**

• **Nom de la classe**: ExceptionPalabraExist

• **Breu descripció de la classe**: Excepción lanzada cuando se intenta añadir una palabra que ya existe en el diccionario. Se produce durante operaciones de gestión de diccionarios cuando se intenta agregar palabras duplicadas a la estructura DAWG.

• **Cardinalitat**: Se instancia cada vez que ocurre el error específico

• **Descripció dels atributs**:

- serialVersionUID: Identificador de versión para serialización (static final)

• **Descripció de les relacions**:

- Relació d'herència amb la clase "RuntimeException": extiende la funcionalidad de excepciones en tiempo de ejecución
- Relació d'associació amb la clase "Diccionario": es lanzada durante operaciones de modificación de diccionarios

#### **2.4.2.4. ExceptionPalabraNotExist**

• **Nom de la classe**: ExceptionPalabraNotExist

• **Breu descripció de la classe**: Excepción lanzada cuando se intenta eliminar una palabra que no existe en el diccionario. Se produce durante operaciones sobre palabras no presentes en la estructura DAWG del diccionario.

• **Cardinalitat**: Se instancia cada vez que ocurre el error específico

• **Descripció dels atributs**:

- serialVersionUID: Identificador de versión para serialización (static final)

• **Descripció de les relacions**:

- Relació d'herència amb la clase "RuntimeException": extiende la funcionalidad de excepciones en tiempo de ejecución
- Relació d'associació amb la clase "Diccionario": es lanzada durante operaciones de consulta y eliminación

#### **2.4.2.5. ExceptionPalabraVacia**

• **Nom de la classe**: ExceptionPalabraVacia

• **Breu descripció de la classe**: Excepción lanzada cuando se intenta procesar una palabra vacía. Se produce cuando se realizan operaciones que requieren palabras válidas pero se proporciona una cadena vacía o nula.

• **Cardinalitat**: Se instancia cada vez que ocurre el error específico

• **Descripció dels atributs**:

- serialVersionUID: Identificador de versión para serialización (static final)

• **Descripció de les relacions**:

- Relació d'herència amb la clase "RuntimeException": extiende la funcionalidad de excepciones en tiempo de ejecución
- Relació d'associació amb operaciones de validación: es lanzada durante verificaciones de entrada

### **2.4.3. Excepciones de Gestión del Sistema**

#### **2.4.3.1. ExceptionDiccionarioExist**

• **Nom de la classe**: ExceptionDiccionarioExist

• **Breu descripció de la classe**: Excepción lanzada cuando se intenta crear un diccionario que ya existe en el sistema. Se produce durante el registro de diccionarios con nombres duplicados, formando parte del sistema de validación de integridad.

• **Cardinalitat**: Se instancia cada vez que ocurre el error específico

• **Descripció dels atributs**:

- serialVersionUID: Identificador de versión para serialización (static final)

• **Descripció de les relacions**:

- Relació d'herència amb la clase "RuntimeException": extiende la funcionalidad de excepciones en tiempo de ejecución
- Relació d'associació amb la clase "ControladorDiccionario": es lanzada durante operaciones de creación de diccionarios

#### **2.4.3.2. ExceptionDiccionarioNotExist**

• **Nom de la classe**: ExceptionDiccionarioNotExist

• **Breu descripció de la classe**: Excepción lanzada cuando se intenta acceder a un diccionario que no existe en el sistema. Se produce durante operaciones sobre diccionarios no registrados o eliminados, formando parte del sistema de validación de existencia.

• **Cardinalitat**: Se instancia cada vez que ocurre el error específico

• **Descripció dels atributs**:

- serialVersionUID: Identificador de versión para serialización (static final)

• **Descripció de les relacions**:

- Relació d'herència amb la clase "RuntimeException": extiende la funcionalidad de excepciones en tiempo de ejecución
- Relació d'associació amb la clase "ControladorDiccionario": es lanzada durante operaciones de acceso a diccionarios

#### **2.4.3.3. ExceptionDiccionarioOperacionFallida**

• **Nom de la classe**: ExceptionDiccionarioOperacionFallida

• **Breu descripció de la classe**: Excepción lanzada cuando falla una operación específica sobre un diccionario. Encapsula errores durante operaciones complejas como creación, modificación, eliminación, importación o validación de diccionarios.

• **Cardinalitat**: Se instancia cada vez que ocurre el error específico

• **Descripció dels atributs**:

- serialVersionUID: Identificador de versión para serialización (static final)
- tipoOperacion: Tipo de operación que falló (no static)

• **Descripció de les relacions**:

- Relació d'herència amb la clase "Exception": extiende la funcionalidad de excepciones verificadas
- Relació d'associació amb operaciones de diccionario: es lanzada durante operaciones complejas de gestión

#### **2.4.3.4. ExceptionRankingOperationFailed**

• **Nom de la classe**: ExceptionRankingOperationFailed

• **Breu descripció de la classe**: Excepción lanzada cuando una operación en el sistema de ranking no se puede completar. Se produce cuando fallan operaciones relacionadas con gestión de estadísticas, cálculo de posiciones o persistencia de datos de ranking.

• **Cardinalitat**: Se instancia cada vez que ocurre el error específico

• **Descripció dels atributs**:

- serialVersionUID: Identificador de versión para serialización (static final)

• **Descripció de les relacions**:

- Relació d'herència amb la clase "RuntimeException": extiende la funcionalidad de excepciones en tiempo de ejecución
- Relació d'associació amb la clase "ControladorRanking": es lanzada durante operaciones de gestión de ranking

#### **2.4.3.5. ExceptionPersistenciaFallida**

• **Nom de la classe**: ExceptionPersistenciaFallida

• **Breu descripció de la classe**: Excepción lanzada cuando ocurre un error durante operaciones de persistencia de datos. Encapsula errores relacionados con almacenamiento y recuperación de datos, incluyendo problemas de E/O, serialización y acceso a archivos.

• **Cardinalitat**: Se instancia cada vez que ocurre el error específico

• **Descripció dels atributs**:

- serialVersionUID: Identificador de versión para serialización (static final)

• **Descripció de les relacions**:

- Relació d'herència amb la clase "Exception": extiende la funcionalidad de excepciones verificadas
- Relació d'associació amb todas las implementaciones de repositorio: es lanzada durante operaciones de persistencia

#### **2.4.3.6. ExceptionLoggingOperacion**

• **Nom de la classe**: ExceptionLoggingOperacion

• **Breu descripció de la classe**: Excepción que encapsula mensajes informativos y de logging del sistema. Transmite información desde capas internas hacia capas de presentación, actuando como mecanismo de comunicación entre capas para eventos que requieren notificación.

• **Cardinalitat**: Se instancia cada vez que se necesita transmitir información entre capas

• **Descripció dels atributs**:

- serialVersionUID: Identificador de versión para serialización (static final)
- tipoOperacion: Categoría o tipo de operación (no static)
- esError: Indica si es un mensaje de error o informativo (no static)

• **Descripció de les relacions**:

- Relació d'herència amb la clase "RuntimeException": extiende la funcionalidad de excepciones en tiempo de ejecución
- Relació d'associació amb sistemas de logging: es utilizada para transmitir mensajes informativos

---

## **2.4.4 Estrategias de Ranking**

#### **2.4.4.1. RankingOrderStrategy**

• **Nom de la classe**: RankingOrderStrategy

• **Breu descripció de la classe**: Interfaz que define la estrategia para ordenar el ranking de jugadores. Actúa como un Comparator especializado para nombres de usuario y forma parte del patrón Strategy, permitiendo cambiar dinámicamente el algoritmo de ordenación del ranking.

• **Cardinalitat**: Una interfaz implementada por múltiples estrategias concretas

• **Descripció dels atributs**: No tiene atributos (es una interfaz)

• **Descripció de les relacions**:

- Relació d'implementació amb múltiples classes de estrategia: define el contrato para estrategias de ordenación
- Relació d'associació amb la classe "Ranking": es utilizada por el ranking para ordenar jugadores

#### **2.4.4.2. RankingOrderStrategyFactory**

• **Nom de la classe**: RankingOrderStrategyFactory

• **Breu descripció de la classe**: Factory para crear estrategias de ordenación del ranking. Implementa el patrón Factory Method para centralizar la creación de estrategias según criterios específicos, facilitando el mantenimiento y la extensibilidad del sistema.

• **Cardinalitat**: Clase utilitaria con métodos estáticos

• **Descripció dels atributs**: No tiene atributos (solo métodos estáticos)

• **Descripció de les relacions**:

- Relació d'associació amb todas las estrategias concretas: crea instancias de estrategias específicas
- Relació d'associació amb la classe "Ranking": recibe el ranking para inyectarlo en las estrategias

#### **2.4.4.3. MaximaScoreStrategy**

• **Nom de la classe**: MaximaScoreStrategy

• **Breu descripció de la classe**: Implementación de RankingOrderStrategy que ordena por puntuación máxima. Esta estrategia ordena a los jugadores según la puntuación más alta que han conseguido en una sola partida, de mayor a menor.

• **Cardinalitat**: Una instancia por cada uso de este criterio de ordenación

• **Descripció dels atributs**:

- serialVersionUID: Identificador de versión para serialización (static final)
- ranking: Objeto ranking para acceder a las estadísticas (final, no static)

• **Descripció de les relacions**:

- Relació d'implementació amb la interfície "RankingOrderStrategy": implementa el contrato de estrategia
- Relació d'associació amb la classe "Ranking": utiliza el ranking para obtener puntuaciones máximas

#### **2.4.4.4. MediaScoreStrategy**

• **Nom de la classe**: MediaScoreStrategy

• **Breu descripció de la classe**: Implementación de RankingOrderStrategy que ordena por puntuación media. Esta estrategia calcula la puntuación promedio de todas las partidas jugadas por cada jugador y los ordena de mayor a menor puntuación media.

• **Cardinalitat**: Una instancia por cada uso de este criterio de ordenación

• **Descripció dels atributs**:

- serialVersionUID: Identificador de versión para serialización (static final)
- ranking: Objeto ranking para acceder a las estadísticas (final, no static)

• **Descripció de les relacions**:

- Relació d'implementació amb la interfície "RankingOrderStrategy": implementa el contrato de estrategia
- Relació d'associació amb la classe "Ranking": utiliza el ranking para calcular puntuaciones medias

#### **2.4.4.5. PartidasJugadasStrategy**

• **Nom de la classe**: PartidasJugadasStrategy

• **Breu descripció de la classe**: Implementación de RankingOrderStrategy que ordena por número de partidas jugadas. Esta estrategia ordena a los jugadores según el número total de partidas que han jugado, de mayor a menor, útil para identificar a los jugadores más activos.

• **Cardinalitat**: Una instancia por cada uso de este criterio de ordenación

• **Descripció dels atributs**:

- serialVersionUID: Identificador de versión para serialización (static final)
- ranking: Objeto ranking para acceder a las estadísticas (final, no static)

• **Descripció de les relacions**:

- Relació d'implementació amb la interfície "RankingOrderStrategy": implementa el contrato de estrategia
- Relació d'associació amb la classe "Ranking": utiliza el ranking para obtener número de partidas jugadas

#### **2.4.4.6. VictoriasStrategy**

• **Nom de la classe**: VictoriasStrategy

• **Breu descripció de la classe**: Implementación de RankingOrderStrategy que ordena por total de victorias. Esta estrategia ordena a los jugadores según el número total de partidas que han ganado, de mayor a menor, útil para identificar a los jugadores más exitosos.

• **Cardinalitat**: Una instancia por cada uso de este criterio de ordenación

• **Descripció dels atributs**:

- serialVersionUID: Identificador de versión para serialización (static final)
- ranking: Objeto ranking para acceder a las estadísticas (final, no static)

• **Descripció de les relacions**:

- Relació d'implementació amb la interfície "RankingOrderStrategy": implementa el contrato de estrategia
- Relació d'associació amb la classe "Ranking": utiliza el ranking para obtener número de victorias

#### **2.4.4.7. PuntuacionTotalStrategy**

• **Nom de la classe**: PuntuacionTotalStrategy

• **Breu descripció de la classe**: Estrategia para ordenar el ranking por puntuación total acumulada. Esta estrategia suma todas las puntuaciones obtenidas por cada jugador a lo largo de todas sus partidas y los ordena de mayor a menor puntuación total.

• **Cardinalitat**: Una instancia por cada uso de este criterio de ordenación

• **Descripció dels atributs**:

- serialVersionUID: Identificador de versión para serialización (static final)
- ranking: Objeto ranking para acceder a las estadísticas (no static)

• **Descripció de les relacions**:

- Relació d'implementació amb la interfície "RankingOrderStrategy": implementa el contrato de estrategia
- Relació d'associació amb la classe "Ranking": utiliza el ranking para obtener puntuaciones totales acumuladas

#### **2.4.4.8. PlayerRankingStats**

• **Nom de la classe**: PlayerRankingStats

• **Breu descripció de la classe**: Clase que encapsula todas las estadísticas de un jugador para el ranking. Esta clase centraliza la gestión de puntuaciones, partidas y victorias, proporcionando una interfaz limpia para el acceso y actualización de datos.

• **Cardinalitat**: Una instancia por cada jugador registrado en el sistema de ranking

• **Descripció dels atributs**:

- serialVersionUID: Identificador de versión para serialización (static final)
- username: Nombre del jugador (no static)
- puntuaciones: Lista de puntuaciones del jugador (no static)
- puntuacionMaxima: Puntuación máxima registrada (no static)
- puntuacionMedia: Puntuación media calculada (no static)
- partidasJugadas: Número de partidas jugadas (no static)
- victorias: Número de victorias (no static)
- puntuacionTotalAcumulada: Suma total de todas las puntuaciones (no static)

• **Descripció de les relacions**:

- Relació d'agregació amb la classe "Ranking": es utilizada por el ranking para almacenar estadísticas
- Relació d'associació amb todas las estrategias de ranking: proporciona datos para las comparaciones

---

## **2.5 Helpers**

### **2.5.1. Estructuras de Datos**

Las estructuras de datos auxiliares se describen en el archivo helpers/readme.md:

• **Tuple**: Tupla genérica inmutable para pares de valores (coordenadas, asociaciones)

• **Triple**: Tupla genérica mutable para tres valores (movimientos de juego)

### **2.5.2. Enumeraciones**

#### **2.5.2.1. Tema**

• **Nom de la classe**: Tema

• **Breu descripció de la classe**: Enumeración que define los temas visuales disponibles para la aplicación. Especifica los esquemas de colores y estilos visuales que pueden aplicarse a la interfaz de usuario para personalización.

• **Cardinalitat**: Enumeración con valores constantes (CLARO, OSCURO)

• **Descripció dels atributs**:

- CLARO: Tema claro con fondo blanco y colores claros (static final)
- OSCURO: Tema oscuro con fondo negro y colores oscuros (static final)

• **Descripció de les relacions**:

- Relació d'associació amb la classe "Configuracion": es utilizada para configurar el tema visual del sistema
- Relació d'associació amb la interfaz de usuario: determina el esquema de colores aplicado

#### **2.5.2.2. Dificultad**

• **Nom de la classe**: Dificultad

• **Breu descripció de la classe**: Enumeración que define los niveles de dificultad disponibles para jugadores IA. Establece los grados de complejidad que puede tener la inteligencia artificial, afectando la estrategia de selección de movimientos y calidad de jugadas.

• **Cardinalitat**: Enumeración con valores constantes (FACIL, DIFICIL)

• **Descripció dels atributs**:

- FACIL: Nivel fácil donde la IA realiza movimientos básicos sin optimización (static final)
- DIFICIL: Nivel difícil donde la IA utiliza estrategias avanzadas para maximizar puntuación (static final)

• **Descripció de les relacions**:

- Relació d'associació amb la clase "JugadorIA": define el nivel de dificultad de jugadores artificiales
- Relació d'associació amb algoritmos de IA: determina la estrategia de juego utilizada

#### **2.5.2.3. Direction**

• **Nom de la classe**: Direction

• **Breu descripció de la classe**: Enumeración que define las direcciones posibles para colocar palabras en el tablero de Scrabble. Especifica las orientaciones válidas en las que se pueden formar palabras, determinando cómo se extienden las letras desde una posición inicial.

• **Cardinalitat**: Enumeración con valores constantes (HORIZONTAL, VERTICAL)

• **Descripció dels atributs**:

- HORIZONTAL: Dirección horizontal donde las palabras se forman de izquierda a derecha (static final)
- VERTICAL: Dirección vertical donde las palabras se forman de arriba hacia abajo (static final)

• **Descripció de les relacions**:

- Relació d'associació amb la clase "ControladorJuego": es utilizada en validación de movimientos y búsqueda de jugadas
- Relació d'associació amb la clase "Triple": forma parte de la información de movimientos de juego

#### **2.5.2.4. TipoCasilla**

• **Nom de la classe**: TipoCasilla

• **Breu descripció de la classe**: Enumeración que define los tipos de casillas especiales en el tablero de Scrabble. Especifica los diferentes tipos de casillas con efectos específicos sobre la puntuación de las palabras formadas según las reglas oficiales del Scrabble.

• **Cardinalitat**: Enumeración con valores constantes (NORMAL, CENTRO, LETRA_DOBLE, LETRA_TRIPLE, PALABRA_DOBLE, PALABRA_TRIPLE)

• **Descripció dels atributs**:

- NORMAL: Casilla estándar sin multiplicadores (static final)
- CENTRO: Casilla central del tablero, punto de inicio obligatorio (static final)
- LETRA_DOBLE: Casilla que duplica el valor de la letra colocada (static final)
- LETRA_TRIPLE: Casilla que triplica el valor de la letra colocada (static final)
- PALABRA_DOBLE: Casilla que duplica el valor total de la palabra (static final)
- PALABRA_TRIPLE: Casilla que triplica el valor total de la palabra (static final)

• **Descripció de les relacions**:

- Relació d'associació amb la clase "Tablero": define los tipos de casillas en la matriz de bonificaciones
- Relació d'associació amb algoritmos de puntuación: determina los multiplicadores aplicados
