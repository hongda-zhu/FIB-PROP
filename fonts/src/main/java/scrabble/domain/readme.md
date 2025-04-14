# Directorio de Domain

## Descripción General

Este directorio contiene la implementación del dominio de la aplicación Scrabble, siguiendo los principios de la arquitectura en capas. Aquí se encuentran los modelos de datos (entidades) y los controladores que implementan la lógica de negocio de la aplicación.

## Estructura del Directorio

### Subcarpetas Principales

- **models/**  
  Contiene las clases que representan las entidades del dominio, como Jugador, Tablero, Diccionario, etc. Estas clases implementan la estructura de datos fundamental y comportamientos básicos.

- **controllers/**  
  Contiene los controladores que coordinan las operaciones de la aplicación, implementan la lógica de negocio y orquestan las interacciones entre los diferentes modelos.
  
  - **subcontrollers/**  
    Controladores más específicos que manejan aspectos concretos como la gestión de usuarios, el ranking o las partidas.
    
    - **managers/**  
      Componentes de nivel inferior que implementan operaciones especializadas como la autenticación o la gestión de jugadas.

## Patrones de Diseño Implementados

- **Modelo-Vista-Controlador (MVC)**: Separación clara entre los modelos y los controladores
- **Singleton**: Utilizado en los controladores para asegurar instancia única
- **Façade**: Los controladores actúan como fachada para las operaciones complejas
- **Strategy**: En modelos como el ranking para diferentes criterios de ordenación
- **Factory**: Para la creación de diferentes tipos de objetos
- **Patrón Template Method**: En la jerarquía de Jugador
