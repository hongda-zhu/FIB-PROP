# Directorio de Recursos para Pruebas

## Descripción General

Este directorio contiene los recursos estáticos necesarios para ejecutar las pruebas de la aplicación Scrabble. Incluye archivos de datos que simulan diccionarios, configuraciones de juego y otros elementos necesarios para crear un entorno de pruebas consistente y controlado.

## Contenido del Directorio

### Archivos de Diccionario

- **words.txt**  
  Lista completa de palabras válidas utilizadas para las pruebas del diccionario y la validación de jugadas. Este archivo simula un diccionario real del juego y contiene:
  
  - Palabras ordenadas alfabéticamente
  - Una palabra por línea
  - Sin caracteres especiales o acentos
  - Utilizadas para construir y verificar la estructura DAWG

### Archivos de Configuración de Letras

- **alpha.txt**  
  Configuración del alfabeto para las pruebas, definiendo las letras disponibles, su frecuencia y valor en puntos. Cada línea sigue el formato:
  
  ```
  [letra] [frecuencia] [puntos]
  ```
  
  Por ejemplo:
  ```
  A 9 1
  B 2 3
  C 2 3
  ```
  
  Este archivo es crucial para probar:
  - La inicialización de la bolsa de fichas
  - El cálculo de puntuaciones
  - La validación de jugadas


