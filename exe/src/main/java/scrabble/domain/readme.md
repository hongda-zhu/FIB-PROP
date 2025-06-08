# Directorio de Domain (`domain`)

## Descripción General

Este directorio (`scrabble.domain`) contiene la implementación del núcleo lógico de la aplicación Scrabble, siguiendo los principios de una **arquitectura en capas**. Aquí se encuentran los modelos de datos (entidades) y los controladores que implementan la lógica de negocio de la aplicación, manteniendo una clara separación de responsabilidades con otras capas como la presentación o la persistencia (aunque la persistencia basada en serialización se maneja actualmente desde algunos controladores/modelos).

## Estructura del Directorio

### Subcarpetas Principales

-   **`models/`**
    Contiene las clases que representan las entidades del dominio, como `Jugador`, `Tablero`, `Ranking`, `Diccionario`, `Configuracion`, etc. Estas clases encapsulan la estructura de datos fundamental y los comportamientos intrínsecos de cada entidad. Consulta el `readme.md` dentro de esta carpeta para más detalles.

-   **`controllers/`**
    Contiene los controladores que coordinan las operaciones de la aplicación, implementan la lógica de negocio de alto nivel y orquestan las interacciones entre los diferentes modelos y subcontroladores. Actúa como la capa de coordinación entre la presentación y los modelos del dominio.
    -   **`subcontrollers/`**: Alberga controladores especializados para áreas funcionales específicas, como la gestión de jugadores (`ControladorJugador`), el ranking (`ControladorRanking`), el flujo de una partida (`ControladorJuego`), la gestión de diccionarios (`ControladorDiccionario`) y configuraciones (`ControladorConfiguracion`). Cada subcontrolador implementa el patrón Singleton y utiliza repositorios para la persistencia. Consulta el `readme.md` dentro de esta carpeta para más detalles.

-   **`persistences/`**
    Gestiona toda la persistencia de datos del sistema utilizando el patrón Repository. Se divide en interfaces que definen contratos y implementaciones concretas que manejan la serialización y almacenamiento de datos.
    -   **`interfaces/`**: Define las interfaces Repository para cada entidad del dominio.
    -   **`implementaciones/`**: Proporciona implementaciones concretas utilizando serialización Java y gestión de archivos.

## Patrones de Diseño Implementados (Identificados)

-   **Arquitectura en Capas:** Clara separación entre Modelos y Controladores dentro del dominio.
-   **Singleton:** Utilizado extensamente en los controladores (`ControladorDomain` y subcontroladores) para asegurar una única instancia de gestión para cada subsistema.
-   **Façade:** Aplicado principalmente en `ControladorDomain` como punto de entrada único a la lógica del dominio, y potencialmente en los subcontroladores para simplificar la interacción con sus respectivos subsistemas.
-   **Strategy:** Utilizado en el subsistema de Ranking (`rankingStrategy/`) para permitir diferentes criterios de ordenación de jugadores.
-   **Factory:** Empleado en `ControladorJugador` para crear diferentes tipos de `Jugador` (Humano/IA) y en `RankingOrderStrategyFactory` para instanciar estrategias de ranking.
-   **(Herencia y Polimorfismo):** Utilizado en la jerarquía de `Jugador` para definir comportamiento común y especializado.