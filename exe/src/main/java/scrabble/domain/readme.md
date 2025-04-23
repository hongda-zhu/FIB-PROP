# Directorio de Domain (`domain`)

## Descripción General

Este directorio (`scrabble.domain`) contiene la implementación del núcleo lógico de la aplicación Scrabble, siguiendo los principios de una **arquitectura en capas**. Aquí se encuentran los modelos de datos (entidades) y los controladores que implementan la lógica de negocio de la aplicación, manteniendo una clara separación de responsabilidades con otras capas como la presentación o la persistencia (aunque la persistencia basada en serialización se maneja actualmente desde algunos controladores/modelos).

## Estructura del Directorio

### Subcarpetas Principales

-   **`models/`**
    Contiene las clases que representan las entidades del dominio, como `Jugador`, `Tablero`, `Ranking`, `Diccionario`, `Configuracion`, etc. Estas clases encapsulan la estructura de datos fundamental y los comportamientos intrínsecos de cada entidad. Consulta el `readme.md` dentro de esta carpeta para más detalles.

-   **`controllers/`**
    Contiene los controladores que coordinan las operaciones de la aplicación, implementan la lógica de negocio de alto nivel y orquestan las interacciones entre los diferentes modelos y subcontroladores.
    -   **`subcontrollers/`**: Alberga controladores especializados para áreas funcionales específicas, como la gestión de jugadores (`ControladorJugador`), el ranking (`ControladorRanking`), el flujo de una partida (`ControladorJuego`), la gestión de diccionarios (`ControladorDiccionario`) y configuraciones (`ControladorConfiguracion`). Consulta el `readme.md` dentro de esta carpeta para más detalles.

## Patrones de Diseño Implementados (Identificados)

-   **Arquitectura en Capas:** Clara separación entre Modelos y Controladores dentro del dominio.
-   **Singleton:** Utilizado extensamente en los controladores (`ControladorDomain` y subcontroladores) para asegurar una única instancia de gestión para cada subsistema.
-   **Façade:** Aplicado principalmente en `ControladorDomain` como punto de entrada único a la lógica del dominio, y potencialmente en los subcontroladores para simplificar la interacción con sus respectivos subsistemas.
-   **Strategy:** Utilizado en el subsistema de Ranking (`rankingStrategy/`) para permitir diferentes criterios de ordenación de jugadores.
-   **Factory:** Empleado en `ControladorJugador` para crear diferentes tipos de `Jugador` (Humano/IA) y en `RankingOrderStrategyFactory` para instanciar estrategias de ranking.
-   **(Herencia y Polimorfismo):** Utilizado en la jerarquía de `Jugador` para definir comportamiento común y especializado.