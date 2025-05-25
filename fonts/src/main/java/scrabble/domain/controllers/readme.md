# Directorio de Controladores (`controllers`)

## Descripción General

Este directorio (`scrabble.domain.controllers`) contiene las clases que implementan la lógica de negocio de la aplicación Scrabble y actúan como coordinadores entre los modelos y otras capas del sistema (como la presentación o la persistencia, aunque la interacción directa con la persistencia se realiza a través de las interfaces del subpaquete `persistences.interfaces`).

El objetivo principal de los controladores es gestionar el flujo de la aplicación, aplicar las reglas de negocio y orquestar las operaciones, manteniendo el estado de la aplicación en un nivel superior al de los modelos individuales.

## Ficheros clave y su responsabilidad

-   **`ControladorDomain.java`**
    -   **Descripció:** Es el controlador principal del dominio.
    -   **Responsabilitat:** Actúa como un **Façade** para toda la lógica del dominio, proporcionando un punto de entrada unificado para las operaciones de alto nivel desde otras capas (como la capa de presentación o los drivers). Coordina las interacciones entre los diferentes subcontroladores y modelos.

## Subdirectorios

-   **`subcontrollers/`**
    Contiene controladores más especializados que gestionan subsistemas específicos dentro del dominio. Cada subcontrolador se enfoca en una parte particular de la lógica de negocio.
    -   **`ControladorConfiguracion.java`:** Gestiona la lógica relacionada con la configuración del juego.
    -   **`ControladorDiccionario.java`:** Gestiona la lógica relacionada con los diccionarios (carga, validación, modificación).
    -   **`ControladorJuego.java`:** Gestiona el flujo y la lógica de una partida individual de Scrabble.
    -   **`ControladorJugador.java`:** Gestiona la lógica relacionada con los jugadores (creación, eliminación, gestión de sesiones/partidas).
    -   **`ControladorRanking.java`:** Gestiona la lógica relacionada con el ranking y las estadísticas de los jugadores.

## Interacción con otras Capas

-   **Modelos (`scrabble.domain.models`):** Los controladores utilizan e interactúan con los objetos de modelo para acceder y modificar el estado del juego y aplicar las reglas de negocio.
-   **Persistencia (`scrabble.domain.persistences.interfaces`):** Los controladores utilizan las interfaces de persistencia para guardar y cargar el estado de los modelos (como jugadores, partidas, ranking, configuración). La implementación concreta de la persistencia se encuentra en el paquete `scrabble.domain.persistences.implementaciones`.
-   **Drivers / Presentación:** Clases externas al dominio (como `DomainDriver` o la interfaz gráfica) interactúan con la lógica del dominio exclusivamente a través del `ControladorDomain`.
