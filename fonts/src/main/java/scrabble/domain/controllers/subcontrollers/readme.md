# Directorio de Subcontroladores

## Descripción General

Este directorio contiene los controladores específicos que implementan la lógica de negocio para las diferentes funcionalidades del juego Scrabble. Estos subcontroladores gestionan aspectos particulares del sistema y coordinan las operaciones entre los modelos y la capa de presentación, siguiendo una arquitectura de capas bien definida.

## Estructura del Directorio

### Controladores Principales

- **ControladorJugador.java**  
  Gestiona las operaciones relacionadas con los usuarios, incluyendo registro, autenticación, actualización de perfiles y gestión de sesiones. Implementa el patrón Singleton para garantizar una única instancia de gestión de usuarios. Proporciona métodos para administrar el ciclo de vida completo de los jugadores, tanto humanos como IA, incluyendo la persistencia de datos de usuario mediante serialización.

- **ControladorRanking.java**  
  Administra el sistema de clasificación de jugadores, permitiendo agregar puntuaciones, actualizar estadísticas y consultar rankings según diferentes criterios. Implementa el patrón Singleton y la interfaz `RankingDataProvider`, lo que permite utilizarlo como fuente de datos para las estrategias de ordenación. Ofrece funcionalidades para filtrar, ordenar y manipular las estadísticas de los jugadores mediante distintos criterios configurables.

- **ControladorJuego.java**  
  Coordina el desarrollo de las partidas, incluyendo la inicialización del tablero, la gestión de turnos, la validación de jugadas y el cálculo de puntuaciones. Constituye el núcleo de la lógica del juego, implementando algoritmos para la búsqueda de movimientos posibles, verificación de palabras válidas y cálculo de puntuaciones basado en las reglas oficiales de Scrabble. Mantiene el estado global del juego y orquesta las interacciones entre tablero, jugadores, bolsa de fichas y diccionario.

- **ControladorDiccionario.java**  
  Gestiona los diccionarios disponibles en el juego, proporcionando funcionalidades para crear, importar, eliminar y modificar diccionarios. Implementa el patrón Singleton y se encarga de validar palabras, obtener valores de letras y gestionar los diferentes idiomas soportados. Interactúa con el sistema de archivos para operaciones de lectura/escritura de diccionarios.

- **ControladorConfiguracion.java**  
  Gestiona las configuraciones generales de la aplicación, como opciones de juego, preferencias de usuario y configuraciones del sistema. Permite personalizar aspectos como el idioma de la interfaz, el tema visual y el volumen. Actúa como intermediario entre la configuración del usuario y su aplicación en el sistema.

Cada uno de estos controladores sigue principios sólidos de diseño orientado a objetos y aplica patrones de diseño para mejorar la mantenibilidad y extensibilidad del código. La comunicación entre controladores se realiza a través de interfaces bien definidas, permitiendo un acoplamiento bajo y una alta cohesión.

Los controladores reciben las peticiones desde el `ControladorDomain` (que actúa como fachada) y coordinan la lógica específica de su dominio, trabajando con los modelos correspondientes para implementar las reglas de negocio y mantener la consistencia del sistema. No contienen lógica de presentación ni acceso directo a la interfaz de usuario, manteniendo así una clara separación de responsabilidades.