# Directorio Raíz de Paquetes (scrabble)

## Descripción General

Este directorio es el paquete raíz de la aplicación Scrabble, que contiene todos los subpaquetes y clases que componen el sistema. La aplicación implementa una versión del juego de mesa Scrabble con funcionalidades para juego individual, multijugador, y contra la IA.

## Estructura del Directorio

### Paquetes Principales

- **domain/**  
  Contiene los componentes centrales de la lógica de negocio, siguiendo una arquitectura en capas:
  
  - **models/**: Entidades que representan los conceptos del juego
  - **controllers/**: Lógica que coordina las operaciones entre modelos
    - **subcontrollers/**: Controladores específicos para diferentes funcionalidades
      - **managers/**: Componentes especializados para operaciones específicas

- **excepciones/**  
  Implementa las excepciones personalizadas que la aplicación utiliza para manejar situaciones de error específicas del dominio del juego.

- **helpers/**  
  Proporciona clases auxiliares y estructuras de datos genéricas utilizadas en todo el proyecto.


