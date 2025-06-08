# SCRABBLE - Entrega 2: Documentación de Diseño Arquitectónico
## Grupo 41 - Arquitectura de Software en 3 Capas

**Autores:**
- Hongda Zhu (hongda.zhu@estudiantat.upc.edu)
- Shenghao Ye (shenghao.ye@estudiantat.upc.edu)  
- Songhe Wang (songhe.wang@estudiantat.upc.edu)
- Xuanyi Qiu (xuanyi.qiu@estudiantat.upc.edu)

---

## 📋 Índice de Contenidos

### [1. Introducción General](#1-introducción-general)
### [2. Navegación por Componentes de Evaluación](#2-navegación-por-componentes-de-evaluación)
### [3. Documentación Técnica Complementaria](#3-documentación-técnica-complementaria)
### [4. Estructura de Archivos](#4-estructura-de-archivos)
### [5. Guía de Revisión por Criterios](#5-guía-de-revisión-por-criterios)

---

## 1. Introducción General

Presentamos en esta entrega la evolución arquitectónica completa del sistema Scrabble, documentando nuestra transformación desde una arquitectura monolítica hacia un diseño modular fundamentado en el patrón Repository y los principios SOLID. Hemos organizado la documentación conforme a los criterios de evaluación especificados, facilitando así la revisión sistemática de cada componente arquitectónico.

### Evolución Arquitectónica Implementada

En esta segunda entrega, hemos introducido mejoras arquitectónicas significativas:
- **Patrón Repository**: Hemos implementado una separación completa entre la lógica de dominio y los mecanismos de persistencia
- **Principios SOLID**: Hemos aplicado rigurosamente estos principios en toda la arquitectura del sistema
- **Mejoras significadas en el algorismo**: Hemos optimizado las estructuras de datos mediante la implementación de Tuple, Triple y enumeraciones tipadas
- **Gestión de Excepciones**: Hemos desarrollado un sistema robusto de manejo de errores específicos del dominio

---

## 2. Navegación por Componentes de Evaluación

### 🏗️ **Capa de Dominio (25%)**
**Inicio:** [`Descripciones de las clases de domino+repositorio.md`](./Descripciones%20de%20las%20clases%20de%20domino+repositorio.md)

**Diagramas Arquitectónicos:**
- [`Diagrama dominio con contenido.mmd`](./Diagrama%20dominio%20con%20contenido.mmd) - Diagrama completo con atributos y métodos
- [`diagrama dominio con contenidos.svg`](./diagrama%20dominio%20con%20contenidos.svg) - Visualización SVG del diagrama completo
- [`Diagrama dominio sin contenido.mmd`](./Diagrama%20dominio%20sin%20contenido.mmd) - Diagrama estructural simplificado
- [`diagrama dominio sin contenidos.svg`](./diagrama%20dominio%20sin%20contenidos.svg) - Visualización SVG del diagrama estructural
- [`diagrama dominio sin contenidos marcados cambios.png`](./diagrama%20dominio%20sin%20contenidos%20marcados%20cambios.png) - Diagrama con cambios marcados

**Contenido Evaluable que Hemos Desarrollado:**
- ✅ Descripción detallada de 6 controladores principales con responsabilidades específicas
- ✅ Documentación exhaustiva de 10 modelos de dominio con jerarquías bien definidas
- ✅ Especificación completa de 24 clases de excepciones específicas del dominio
- ✅ Análisis detallado de relaciones y cardinalidades entre componentes
- ✅ Aplicación correcta de patrones: Singleton, Strategy, Factory, Template Method

### 🖥️ **Capa de Presentación (25%)**
**Archivo Principal:** [`Descripciones_de_clase_capa_presentación.pdf`](./Descripciones_de_clase_capa_presentación.pdf)

**Diagramas Arquitectónicos:**
- [`Diagrama_capa_presentacion_sin_contenido.svg`](./Diagrama_capa_presentacion_sin_contenido.svg) - Diagrama estructural
- [`Diagrama_capa_presentacion_con_contenido.svg`](./Diagrama_capa_presentacion_con_contenido.svg) - Diagrama completo con detalles

**Contenido Evaluable que Hemos Implementado:**
- ✅ Diseño de interfaz de usuario siguiendo criterios estrictos de usabilidad
- ✅ Controladores de presentación con gestión eficiente de eventos
- ✅ Arquitectura MVC correctamente implementada en la capa de presentación
- ✅ Ergonomía y estética del diseño cuidadosamente planificadas

### 💾 **Capa de Persistencia (10%)**
**Archivo Principal:** [`diagrama repositorios.mmd`](./diagrama%20repositorios.mmd)

**Diagramas Arquitectónicos:**
- [`diagrama repositorio.svg`](./diagrama%20repositorio.svg) - Visualización SVG del patrón Repository

**Documentación Integrada:** [`Descripciones de las clases de domino+repositorio.md`](./Descripciones%20de%20las%20clases%20de%20domino+repositorio.md) - Sección 2.3

**Contenido Evaluable que Hemos Desarrollado:**
- ✅ Patrón Repository implementado con 5 interfaces especializadas
- ✅ Implementaciones concretas utilizando serialización Java optimizada
- ✅ Separación clara y efectiva entre abstracciones e implementaciones
- ✅ Gestión robusta y completa de errores de persistencia

### 🔧 **Estructuras de Datos y Algoritmos (40%)**
**Archivo Principal:** [`Estructura_de_datos_y_algorismos.pdf`](./Estructura_de_datos_y_algorismos.pdf)

**Análisis Complementario:** [`Analisis sobre cambios de dominio y repositorio.md`](./Analisis%20sobre%20cambios%20de%20dominio%20y%20repositorio.md)

**Contenido Evaluable que Hemos Implementado:**
- ✅ Implementación completa de DAWG (Directed Acyclic Word Graph) para diccionarios
- ✅ Estructuras auxiliares optimizadas: Tuple<X,Y> y Triple<A,B,C>
- ✅ Optimizaciones significativas de rendimiento y gestión eficiente de memoria
- ✅ Análisis comparativo detallado de mejoras respecto a la Entrega 1

---

## 3. Documentación Técnica Complementaria

### 📚 **Documentación Javadoc**
**Ubicación:** [`javadoc/index.html`](./javadoc/index.html)

Hemos generado documentación técnica completa utilizando Javadoc que complementa las descripciones de clases del dominio y repositorios. Esta documentación incluye:

**Características de Nuestra Documentación Javadoc:**
- **Cobertura Completa**: Documentación de todas las clases del dominio, persistencia y presentación
- **Etiquetas Personalizadas**: Hemos implementado soporte para `@pre` (precondiciones) y `@post` (postcondiciones)
- **Navegación Estructurada**: Índices por paquetes, clases y miembros para facilitar la consulta
- **Codificación UTF-8**: Soporte completo para caracteres especiales y acentos

**Estructura de la Documentación Javadoc:**
```
javadoc/
├── index.html                    # Página principal de la documentación
├── allclasses-index.html         # Índice de todas las clases
├── allpackages-index.html        # Índice de todos los paquetes
├── overview-tree.html            # Árbol jerárquico de clases
├── scrabble/                     # Documentación por paquetes
│   ├── domain/                   # Clases del dominio
│   ├── presentation/             # Clases de presentación
│   └── helpers/                  # Clases auxiliares
└── search.html                   # Funcionalidad de búsqueda
```

**Acceso Directo a Secciones Clave:**
- **Controladores de Dominio**: `javadoc/scrabble/domain/controllers/`
- **Modelos de Dominio**: `javadoc/scrabble/domain/models/`
- **Interfaces Repository**: `javadoc/scrabble/domain/persistences/interfaces/`
- **Implementaciones Repository**: `javadoc/scrabble/domain/persistences/implementaciones/`
- **Excepciones del Sistema**: `javadoc/scrabble/domain/exceptions/`

---

## 4. Estructura de Archivos

```
docs/entrega 2/
├── readme.md                                          # Este archivo - Navegación principal
├── Analisis sobre cambios de dominio y repositorio.md # Análisis evolutivo arquitectónico
├── Descripciones de las clases de domino+repositorio.md # Documentación completa capa dominio
├── Descripciones_de_clase_capa_presentación.pdf      # Documentación capa presentación
├── Diagrama dominio con contenido.mmd                # Diagrama dominio completo
├── diagrama dominio con contenidos.svg               # Visualización SVG dominio completo
├── Diagrama dominio sin contenido.mmd                # Diagrama dominio estructural
├── diagrama dominio sin contenidos.svg               # Visualización SVG dominio estructural
├── diagrama dominio sin contenidos marcados cambios.png # Diagrama con cambios marcados
├── diagrama repositorios.mmd                         # Diagrama capa persistencia
├── diagrama repositorio.svg                          # Visualización SVG repositorios
├── Diagrama_capa_presentacion_sin_contenido.svg      # Diagrama presentación estructural
├── Diagrama_capa_presentacion_con_contenido.svg      # Diagrama presentación completo
├── Estructura_de_datos_y_algorismos.pdf              # ED&ALG principales
└── javadoc/                                          # Documentación técnica Javadoc
    ├── index.html                                    # Entrada principal Javadoc
    ├── allclasses-index.html                         # Índice completo de clases
    └── scrabble/                                     # Documentación por paquetes
```

---

## 5. Guía de Revisión por Criterios

### 📊 **Para Evaluación de Capa de Dominio (25%)**

**Inicio:** [`Descripciones de las clases de domino+repositorio.md`](./Descripciones%20de%20las%20clases%20de%20domino+repositorio.md)

**Documentación Técnica Complementaria:** [`javadoc/scrabble/domain/`](./javadoc/scrabble/domain/)

**Puntos Clave que Hemos Desarrollado:**
1. **Controladores (Sección 2.1)**: Hemos implementado 6 controladores con patrón Singleton y Facade
2. **Modelos (Sección 2.2)**: Hemos diseñado jerarquías completas: Jugador, Tablero, Bolsa, Ranking, Diccionario
3. **Excepciones (Sección 2.4)**: Hemos desarrollado 24 excepciones específicas organizadas por categorías funcionales
4. **Helpers (Sección 2.5)**: Hemos creado enumeraciones y estructuras auxiliares optimizadas

**Diagramas de Apoyo que Proporcionamos:**
- [`diagrama dominio con contenidos.svg`](./diagrama%20dominio%20con%20contenidos.svg): Visualización completa de atributos y métodos implementados
- [`diagrama dominio sin contenidos.svg`](./diagrama%20dominio%20sin%20contenidos.svg): Relaciones estructurales claras entre componentes
- [`diagrama dominio sin contenidos marcados cambios.png`](./diagrama%20dominio%20sin%20contenidos%20marcados%20cambios.png): Evolución y cambios arquitectónicos

### 📊 **Para Evaluación de Capa de Presentación (25%)**

**Archivo Principal:** [`Descripciones_de_clase_capa_presentación.pdf`](./Descripciones_de_clase_capa_presentación.pdf)

**Documentación Técnica Complementaria:** [`javadoc/scrabble/presentation/`](./javadoc/scrabble/presentation/)

**Diagramas Visuales:**
- [`Diagrama_capa_presentacion_con_contenido.svg`](./Diagrama_capa_presentacion_con_contenido.svg): Diagrama completo con detalles de implementación
- [`Diagrama_capa_presentacion_sin_contenido.svg`](./Diagrama_capa_presentacion_sin_contenido.svg): Estructura arquitectónica clara

**Criterios de Evaluación que Cumplimos:**
- Diseño de interfaz implementado con principios rigurosos de usabilidad
- Controladores de presentación con gestión eficiente y robusta de eventos
- Ergonomía y estética del diseño cuidadosamente planificadas y ejecutadas
- Cumplimiento estricto de la arquitectura de 3 capas

### 📊 **Para Evaluación de Capa de Persistencia (10%)**

**Inicio:** [`diagrama repositorios.mmd`](./diagrama%20repositorios.mmd)

**Diagrama Visual:** [`diagrama repositorio.svg`](./diagrama%20repositorio.svg)

**Documentación Detallada:** Sección 2.3 en [`Descripciones de las clases de domino+repositorio.md`](./Descripciones%20de%20las%20clases%20de%20domino+repositorio.md)

**Documentación Técnica Complementaria:** [`javadoc/scrabble/domain/persistences/`](./javadoc/scrabble/domain/persistences/)

**Puntos Clave que Hemos Implementado:**
- 5 interfaces Repository especializadas con responsabilidades claramente definidas
- Implementaciones concretas con gestión robusta y completa de errores
- Separación clara y efectiva entre abstracciones e implementaciones concretas

### 📊 **Para Evaluación de ED&ALG (40%)**

**Archivo Principal:** [`Estructura_de_datos_y_algorismos.pdf`](./Estructura_de_datos_y_algorismos.pdf)

**Análisis Evolutivo:** [`Analisis sobre cambios de dominio y repositorio.md`](./Analisis%20sobre%20cambios%20de%20dominio%20y%20repositorio.md)

**Documentación Técnica Complementaria:** [`javadoc/scrabble/helpers/`](./javadoc/scrabble/helpers/)

**Estructuras Principales que Hemos Desarrollado:**
1. **DAWG**: Implementación completa y optimizada para validación eficiente de palabras
2. **Tuple/Triple**: Estructuras auxiliares tipadas para mejorar la seguridad de tipos
3. **Optimizaciones**: Mejoras significativas de rendimiento y gestión eficiente de memoria
4. **Comparativa**: Análisis detallado de la evolución desde la Entrega 1

---

## 🎯 Acceso Rápido por Criterio de Evaluación

| Criterio            | Peso | Archivo Principal                                        | Diagramas Visuales                                                                                                 | Documentación Técnica                          | Sección Específica   |
|---------------------|------|-----------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------|------------------------------------------------|----------------------|
| **Capa Domini**     | 25%  | [Descripciones dominio+repositorio](./Descripciones%20de%20las%20clases%20de%20domino+repositorio.md) | [diagrama dominio con contenidos.svg](./diagrama%20dominio%20con%20contenidos.svg), [diagrama dominio sin contenidos.svg](./diagrama%20dominio%20sin%20contenidos.svg), [diagrama dominio sin contenidos marcados cambios.png](./diagrama%20dominio%20sin%20contenidos%20marcados%20cambios.png) | [Javadoc Domain](./javadoc/scrabble/domain/)     | Secciones 2.1-2.2    |
| **Capa Presentació** | 25%  | [Descripciones presentación](./Descripciones_de_clase_capa_presentación.pdf) | [Diagrama_capa_presentacion_con_contenido.svg](./Diagrama_capa_presentacion_con_contenido.svg), [Diagrama_capa_presentacion_sin_contenido.svg](./Diagrama_capa_presentacion_sin_contenido.svg) | [Javadoc Presentation](./javadoc/scrabble/presentation/) | Documento completo   |
| **Capa Persistència** | 10%  | [Diagrama repositorios](./diagrama%20repositorios.mmd)    | [diagrama repositorio.svg](./diagrama%20repositorio.svg)                                                            | [Javadoc Persistences](./javadoc/scrabble/domain/persistences/)          | Sección 2.3          |
| **ED&ALG**          | 40%  | [Estructura datos algoritmos](./Estructura_de_datos_y_algorismos.pdf) | -                                                                                                                  | [Javadoc Helpers](./javadoc/scrabble/helpers/)  | Documento completo   |