# Directorio de Recursos (`src/main/resources`)

## Descripción General

Este directorio (`src/main/resources`) contiene todos los archivos de recursos que no son código fuente Java, pero que son necesarios para la ejecución de la aplicación Scrabble. Estos archivos se empaquetan junto con el código compilado en el artefacto final (por ejemplo, el archivo JAR) y son accesibles desde el classpath durante la ejecución.

**Nota Importante:** Esta es la ubicación estándar para recursos en proyectos Maven/Gradle. Evita colocar recursos dentro de `src/main/java`.

## Contenido Principal

En este proyecto Scrabble, se espera que este directorio contenga principalmente:

1.  **Directorios de Diccionarios:**
    * Cada subdirectorio representa un diccionario específico (el nombre puede ser customizado) (ej., `ESP`, `castellano`, `holala`).
    * Dentro de cada directorio de configuración, se espera encontrar un archivo `alpha.txt` y un `words.txt`.
    * **`alpha.txt`**: Un archivo de texto plano que define las letras del alfabeto para esa configuración, junto con su puntuación y cantidad inicial en la bolsa. La clase `Configuracion` (posiblemente a través de `ControladorConfiguracion`) lee este archivo.
    * **`words.txt`**: Un archivo de texto plano que contiene la lista de palabras válidas para ese diccionario, generalmente una palabra por línea. La codificación esperada suele ser UTF-8. Estos archivos son leídos por la clase `Diccionario` (posiblemente a través de `ControladorDiccionario`) para construir el DAWG.

2. **Directorios de Persistencias:**
    * Guardamos los .dat de todas las persistencias que tenemos en aquí
    * **`jugadores.dat`**`: La lista de jugadores creados.
    * **`ranking.dat`**: El estado actual del ranking, que cambia después de cada partida.
    * **`partidas.dat`**: El partido de juego scrabble que existen en la aplicación

## Organización

Se recomienda usar subdirectorios para mantener los recursos organizados, por ejemplo: