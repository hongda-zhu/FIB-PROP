# Directori d'Implementacions de Persistència (`implementaciones`)

## Descripció General

Aquest directori (`scrabble.domain.persistences.implementaciones`) conté les classes que proporcionen les implementacions concretes per a les interfícies de repositori definides a la carpeta `interfaces/`. Cada classe aquí és responsable de la lògica específica per guardar i carregar dades per a una entitat particular del domini, utilitzant mecanismes de persistència específics.

En la implementació actual, la majoria d'aquestes classes utilitzen la **serialització d'objectes Java** per emmagatzemar dades en fitxers binaris (normalment amb extensió `.dat`) i, en el cas dels diccionaris, també gestionen fitxers de text directament.

## Fitxers Clau i les Seves Responsabilitats

-   **`RepositorioConfiguracionImpl.java`**
    -   **Descripció:** Implementa la interfície `RepositorioConfiguracion`.
    -   **Responsabilitat:** Gestiona la persistència de l'objecte `Configuracion` de l'aplicació.
    -   **Mecanisme:** Serialitza i deserialitza l'objecte `Configuracion` cap a/des d'un fitxer anomenat `configuracion.dat`.
    -   **Fitxer de dades:** `src/main/resources/persistencias/configuracion.dat`

-   **`RepositorioDiccionarioImpl.java`**
    -   **Descripció:** Implementa la interfície `RepositorioDiccionario`.
    -   **Responsabilitat:** Gestiona la persistència dels diccionaris de paraules. Emmagatzema un índex de diccionaris (nom a ruta) i gestiona els fitxers individuals de cada diccionari (`alpha.txt`, `words.txt`).
    -   **Mecanisme:** L'índex de diccionaris es serialitza (`diccionarios_index.dat`). Els fitxers de cada diccionari es gestionen directament al sistema de fitxers.
    -   **Fitxer d'índex:** `src/main/resources/persistencias/diccionarios_index.dat`
    -   **Fitxers de diccionari:** Emmagatzemats en subdirectoris dins de `src/main/resources/diccionarios/` (la ruta específica es desa a l'índex).

-   **`RepositorioJugadorImpl.java`**
    -   **Descripció:** Implementa la interfície `RepositorioJugador`.
    -   **Responsabilitat:** Gestiona la persistència d'un mapa de jugadors (nom del jugador a objecte `Jugador`).
    -   **Mecanisme:** Serialitza i deserialitza el mapa complet de jugadors cap a/des d'un fitxer anomenat `jugadores.dat`.
    -   **Fitxer de dades:** `src/main/resources/persistencias/jugadores.dat`

-   **`RepositorioPartidaImpl.java`**
    -   **Descripció:** Implementa la interfície `RepositorioPartida`.
    -   **Responsabilitat:** Gestiona la persistència dels estats de les partides de Scrabble (objectes `ControladorJuego`). Permet guardar, carregar, eliminar i llistar partides.
    -   **Mecanisme:** Serialitza i deserialitza un mapa d'identificadors de partida a objectes `ControladorJuego` cap a/des d'un fitxer anomenat `partidas.dat`.
    -   **Fitxer de dades:** `src/main/resources/persistencias/partidas.dat`

-   **`RepositorioRankingImpl.java`**
    -   **Descripció:** Implementa la interfície `RepositorioRanking`.
    -   **Responsabilitat:** Gestiona la persistència de l'objecte `Ranking` que conté les estadístiques i classificacions dels jugadors.
    -   **Mecanisme:** Serialitza i deserialitza l'objecte `Ranking` complet cap a/des d'un fitxer anomenat `ranking.dat`.
    -   **Fitxer de dades:** `src/main/resources/persistencias/ranking.dat`

## Consideracions

-   **Gestió de Directoris:** Cada implementació s'encarrega d'assegurar que els directoris necessaris per a la persistència existeixin abans d'intentar operacions d'escriptura.
-   **Gestió d'Errors:** Els mètodes generalment retornen un booleà indicant l'èxit o fracàs de l'operació i imprimeixen missatges d'error a `System.err` o llancen excepcions específiques (com `ExceptionPersistenciaFallida`) en cas de problemes.
-   **Dependències:** Aquestes classes depenen dels models definits a `scrabble.domain.models`, dels controladors (com `ControladorJuego`) i de les interfícies a `scrabble.domain.persistences.interfaces`. 