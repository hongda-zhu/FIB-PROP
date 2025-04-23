# Directorio Raíz del Proyecto (fonts)

## Descripción General

Este directorio contiene el proyecto completo de Scrabble, implementado como una aplicación Java con Gradle. El proyecto sigue una estructura estándar de aplicación Java moderna, con separación clara entre código fuente, pruebas y recursos.

## Estructura del Directorio

### Archivos de Configuración

- **build.gradle**  
  Define la configuración del proyecto, incluyendo dependencias, plugins y tareas de construcción.

- **gradlew** y **gradlew.bat**  
  Scripts wrapper de Gradle que permiten ejecutar tareas de construcción sin necesidad de tener Gradle instalado en el sistema.

- **.gitignore**  
  Especifica archivos y directorios que deben ser ignorados por el sistema de control de versiones Git.

### Directorios Principales

- **src/**  
  Contiene todo el código fuente de la aplicación, pruebas y recursos.

  - **main/**: Código fuente principal de la aplicación
  - **test/**: Pruebas unitarias y de integración

- **build/**  
  Directorio generado por Gradle que contiene los archivos compilados y otros artefactos de construcción.

- **bin/**  
  Contiene archivos binarios y ejecutables generados por el proceso de compilación.

- **.gradle/**  
  Directorio de trabajo interno de Gradle, contiene archivos de caché y estado.

- **gradle/**  
  Contiene archivos de configuración específicos de Gradle, incluyendo el wrapper.

## Flujo de Construcción y Ejecución

1. **Compilación**:

   ```
   ./gradlew build
   ```

   Compila el código fuente, ejecuta las pruebas y genera los artefactos.

2. **Ejecución de pruebas**:

   ```
   ./gradlew test
   ```

   Ejecuta todas las pruebas unitarias y de integración.

3. **Ejecución de la aplicación**:

   ```
   ./gradlew run
   ```

   Inicia la aplicación principal.

4. **Ejecución de la aplicación 2**:

.\gradlew.bat runTemp "-PmainClass=scrabble.domain.DomainDriver" --stacktrace

Ejecutar la propia tasca

5. **Ejecución de la aplicación a partir de exe**:

Situados en la carpeta exe:

java -jar .\scrabble.jar
