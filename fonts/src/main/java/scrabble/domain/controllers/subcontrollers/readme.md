# Directorio de Subcontroladores

## Descripción General

Este directorio contiene los controladores específicos que implementan la lógica de negocio para las diferentes funcionalidades del juego Scrabble. Estos subcontroladores gestionan aspectos particulares del sistema y coordinan las operaciones entre los modelos y la capa de presentación.

## Estructura del Directorio

### Controladores Principales

- **ControladorJugador.java**  
  Gestiona las operaciones relacionadas con los usuarios, incluyendo registro, autenticación, actualización de perfiles y gestión de sesiones. Implementa el patrón Singleton para garantizar una única instancia de gestión de usuarios.

- **ControladorRanking.java**  
  Administra el sistema de clasificación de jugadores, permitiendo agregar puntuaciones, actualizar estadísticas y consultar rankings según diferentes criterios. También implementa Singleton.

- **ControladorJuego.java**  
  Coordina el desarrollo de las partidas, incluyendo la inicialización del tablero, la gestión de turnos, la validación de jugadas y el cálculo de puntuaciones. Constituye el núcleo de la lógica del juego.

- **ControladorConfiguracion.java**  
  Gestiona las configuraciones generales de la aplicación, como opciones de juego, preferencias de usuario y configuraciones del sistema.

### Subcarpeta de Managers

- **managers/**  
  Contiene componentes especializados que implementan funcionalidades específicas:
  
  - **GestorAutenticacion**: Maneja la verificación de credenciales y seguridad
  - **GestorJugada**: Implementa la lógica de validación y ejecución de jugadas en el tablero

