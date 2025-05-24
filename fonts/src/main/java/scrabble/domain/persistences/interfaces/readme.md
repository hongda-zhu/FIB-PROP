# Directori d'Interfícies de Persistència (`interfaces`)

## Descripció General

Aquest directori (`scrabble.domain.persistences.interfaces`) conté les interfícies Java que defineixen els **contractes** per a les operacions de persistència de dades dins de l'aplicació Scrabble. Aquestes interfícies estableixen un nivell d'abstracció entre la lògica de negoci (controladors i serveis del domini) i els mecanismes concrets d'emmagatzematge de dades.

L'ús d'interfícies per a la persistència és una pràctica clau en el disseny d'aplicacions robustes i mantenibles, ja que:
-   **Desacobla** la lògica de domini de les implementacions específiques de persistència.
-   Facilita la ** intercanviabilitat** de les implementacions de persistència (p.ex., canviar de serialització de fitxers a una base de dades SQL o NoSQL) sense afectar la resta del codi que depèn d'aquestes interfícies.
-   Promou la **testeabilitat**, permetent l'ús d'implementacions "mock" o "fake" dels repositoris durant les proves unitàries o d'integració.

## Interfícies Clau i el Seu Propòsit

Cada interfície en aquest directori correspon generalment a una entitat o agregat principal del domini, definint les operacions CRUD (Crear, Llegir, Actualitzar, Esborrar) i altres consultes rellevants per a aquesta entitat.

-   **`RepositorioConfiguracion.java`**
    -   **Propòsit:** Defineix el contracte per a la persistència de l'objecte `Configuracion` de l'aplicació.
    -   **Mètodes clau:** `guardar(Configuracion configuracion)`, `cargar()`.
    -   **Descripció:** Permet guardar l'estat actual de la configuració de l'aplicació i carregar-la en iniciar o quan sigui necessari.

-   **`RepositorioDiccionario.java`**
    -   **Propòsit:** Defineix el contracte per a la gestió i persistència dels diccionaris de paraules utilitzats en el joc.
    -   **Mètodes clau:** `guardar(String nombre, Diccionario diccionario, String path)`, `guardarIndice(Map<String, String> diccionariosPaths)`, `cargar(String nombre)`, `cargarIndice()`, `eliminar(String nombre)`, `existe(String nombre)`, `listarDiccionarios()`, `verificarDiccionarioValido(String nombre)`.
    -   **Descripció:** Especifica com s'han de guardar, carregar, llistar, eliminar i verificar els diccionaris, incloent la gestió d'un índex de diccionaris.

-   **`RepositorioJugador.java`**
    -   **Propòsit:** Defineix el contracte per a la persistència de la informació dels jugadors.
    -   **Mètodes clau:** `guardarTodos(Map<String, Jugador> jugadores)`, `cargarTodos()`, `buscarPorNombre(String nombre)`, `obtenerNombresJugadoresHumanos()`, `obtenerNombresJugadoresIA()`, `obtenerNombresTodosJugadores()`.
    -   **Descripció:** Permet emmagatzemar i recuperar informació sobre els jugadors registrats, buscar-los pel nom i obtenir llistes de noms segons el tipus de jugador.

-   **`RepositorioPartida.java`**
    -   **Propòsit:** Defineix el contracte per a la persistència dels estats de les partides de Scrabble (objectes `ControladorJuego`).
    -   **Mètodes clau:** `guardar(int id, ControladorJuego partida)`, `cargar(int id)`, `eliminar(int id)`, `listarTodas()`, `generarNuevoId()`.
    -   **Descripció:** Especifica com guardar, carregar, eliminar i llistar les partides. També defineix un mètode per generar identificadors únics per a noves partides.

-   **`RepositorioRanking.java`**
    -   **Propòsit:** Defineix el contracte per a la persistència de l'objecte `Ranking`, que emmagatzema les puntuacions i estadístiques dels jugadors.
    -   **Mètodes clau:** `guardar(Ranking ranking)`, `cargar()`, `actualizarEstadisticasJugador(String nombre, PlayerRankingStats stats)`, `eliminarJugador(String nombre)`, `obtenerRankingOrdenado(String criterio)`, i diversos mètodes per obtenir estadístiques específiques d'un jugador.
    -   **Descripció:** Especifica com guardar i carregar l'estat complet del rànquing, així com actualitzar i consultar les dades del rànquing.

## Ús

Les classes que necessiten interactuar amb la capa de persistència (principalment els controladors del domini) haurien de dependre d'aquestes interfícies, no de les seves implementacions concretes. Això s'aconsegueix normalment mitjançant la injecció de dependències, on es proporciona una instància d'una implementació del repositori a la classe client. 