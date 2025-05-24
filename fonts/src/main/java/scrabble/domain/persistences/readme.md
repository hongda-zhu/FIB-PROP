# Directori de Persistències (`persistences`)

## Descripció General

Aquest directori (`scrabble.domain.persistences`) és el responsable de gestionar l'emmagatzematge i la recuperació de dades de l'aplicació Scrabble. Conté les abstraccions (interfícies) i les implementacions concretes per a la persistència de diferents entitats del domini, com la configuració, els diccionaris, els jugadors, el rànquing i les partides guardades.

L'objectiu principal és desacoblar la lògica de negoci de la forma específica en què les dades són emmagatzemades, permetent potencialment canviar el mecanisme de persistència (p.ex., de fitxers serialitzats a una base de dades) amb un impacte mínim en la resta de l'aplicació.

## Estructura del Directori

El directori `persistences` s'organitza en les següents subcarpetes principals:

-   **`interfaces/`**
    Conté les interfícies Java que defineixen els contractes per a les operacions de persistència. Cada interfície declara els mètodes que una implementació de repositori ha de proporcionar per a una entitat específica del domini. Això promou una programació orientada a interfícies i facilita la substitució d'implementacions.
    -   `RepositorioConfiguracion.java`: Defineix les operacions per guardar i carregar la configuració de l'aplicació.
    -   `RepositorioDiccionario.java`: Defineix les operacions per guardar, carregar, eliminar i gestionar diccionaris de paraules.
    -   `RepositorioJugador.java`: Defineix les operacions per guardar, carregar i buscar informació sobre els jugadors.
    -   `RepositorioRanking.java`: Defineix les operacions per guardar, carregar i consultar les dades del rànquing de jugadors.
    -   `RepositorioPartida.java`: Defineix les operacions per guardar, carregar, eliminar i llistar partides en curs o finalitzades.

-   **`implementaciones/`**
    Conté les implementacions concretes de les interfícies de repositori definides a la carpeta `interfaces/`. Actualment, la majoria de les implementacions utilitzen la serialització d'objectes Java per persistir les dades en fitxers locals.
    -   `RepositorioConfiguracionImpl.java`: Implementació per a la persistència de la configuració.
    -   `RepositorioDiccionarioImpl.java`: Implementació per a la persistència dels diccionaris i el seu índex.
    -   `RepositorioJugadorImpl.java`: Implementació per a la persistència de les dades dels jugadors.
    -   `RepositorioRankingImpl.java`: Implementació per a la persistència del rànquing.
    -   `RepositorioPartidaImpl.java`: Implementació per a la persistència de les partides guardades.

## Ús

Els controladors del domini (`scrabble.domain.controllers`) utilitzen aquests repositoris (a través de les seves interfícies) per interactuar amb la capa de persistència, sense necessitat de conèixer els detalls de com s'emmagatzemen les dades. 