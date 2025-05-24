# Directorio Raíz de Paquetes (`scrabble.presentation`)

## Descripción General
Este directorio contiene los componentes de la interfaz de usuario de la aplicación Scrabble, implementada usando JavaFX. Sigue una arquitectura MVC (Model-View-Controller) y proporciona una interfaz gráfica completa para todas las funcionalidades del juego. 

## Estructura del Directorio

### Paquetes Principales
-   **`componentes/`**
    Contiene los componentes gráficos reutilizables y personalizados de la aplicación:

-   **`popups/`**
    Implementa ventanas emergentes modales para interacciones específicas  

-   **`viewControllers/`**
    Contiene los controladores que manejan la lógica de las diferentes vistas 

-   **`views/`**
    Clases que definen las vistas principales de la aplicación definidas en FXML. 

-   **`PresentationController.java/`**
    Controlador principal que actúa como fachada para la capa de presentación. Gestiona la comunicación entre los controladores de vistas y la capa de dominio.

## Características Principales

### Patrón de Diseño

-   **`MVC`**
    Separación clara entre controladores, vistas y modelos

-   **`Patrón Singleton`**
    Los controladores implementan el patrón Singleton.

  -   **`Componentes reutilizables`**
    Se han implementado componentes que pueden ser reutilizados en todo el sistema, mejorando así la resusabilidad y modularidad del código.
