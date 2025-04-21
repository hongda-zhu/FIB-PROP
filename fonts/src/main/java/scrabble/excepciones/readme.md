# Directorio de Excepciones

## Descripción General

Este directorio contiene las clases de excepciones personalizadas que se utilizan en toda la aplicación para manejar situaciones de error específicas del dominio. Estas excepciones proporcionan información detallada sobre los problemas y facilitan un manejo consistente de errores.

## Estructura del Directorio

### Excepciones de Usuario

- **ExceptionUserExist.java**  
  Lanzada cuando se intenta crear un usuario que ya existe en el sistema.

- **ExceptionUserNotExist.java**  
  Lanzada cuando se intenta operar con un usuario que no existe en el sistema.

- **ExceptionUserLoggedIn.java**  
  Lanzada cuando se realiza una operación que requiere que el usuario no tenga sesión iniciada.

- **ExceptionUserNotLoggedIn.java**  
  Lanzada cuando se intenta realizar una operación que requiere sesión iniciada.

- **ExceptionUserInGame.java**  
  Lanzada cuando se intenta realizar una operación incompatible con un usuario ya en partida.

- **ExceptionInvalidCredentials.java** y **ExceptionPasswordMismatch.java**  
  Lanzadas cuando hay problemas con la autenticación de usuarios.

### Excepciones de Partida

- **ExceptionPartidaExist.java**  
  Lanzada cuando se intenta crear una partida con un identificador ya existente.

- **ExceptionPartidaNotExist.java**  
  Lanzada cuando se intenta operar con una partida inexistente.

### Excepciones de Contenido

- **ExceptionDiccionarioExist.java** y **ExceptionDiccionarioNotExist.java**  
  Lanzadas al gestionar diccionarios con identificadores duplicados o inexistentes.

- **ExceptionIdiomaNotExists.java** y **ExceptionTemaNotExists.java**  
  Lanzadas cuando se intenta usar un idioma o tema no configurado en el sistema.

### Excepciones de Ranking

- **ExceptionRankingOperationFailed.java**  
  Lanzada cuando una operación sobre el ranking no puede completarse.

- **ExceptionPuntuacionNotExist.java** y **ExceptionInvalidScore.java**  
  Lanzadas cuando hay problemas con las puntuaciones de los usuarios.

