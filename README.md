-----

# ⚙️ Preprocesador y Analizador Léxico de Código Fuente

Proyecto universitario grupal enfocado en los fundamentos de la compilación y el procesamiento de lenguajes de programación.

-----

## 🎯 Objetivo del Proyecto

El objetivo principal es desarrollar una herramienta en consola capaz de realizar dos tareas fundamentales sobre un archivo de código fuente:

1.  **Preprocesamiento:** Limpiar el código eliminando comentarios y espacios innecesarios.
2.  **Análisis Léxico:** Analizar el código limpio para identificar, clasificar y reportar sus componentes léxicos (tokens).

-----

## ✨ Características Principales

### Fase 1: Limpieza de Código

  - **Lectura de archivos:** El programa debe poder leer un archivo de código fuente como entrada.
  - **Eliminación de comentarios:**
      - Soporte para comentarios de una sola línea (ej. `// esto es un comentario`).
      - Soporte para comentarios de múltiples líneas (ej. `/* ... */`).
  - **Normalización de espacios:** Elimina espacios y tabulaciones innecesarias al inicio y al final de cada línea.
  - **Generación de salida:** Crea un nuevo archivo que contiene el código fuente ya limpio y listo para ser analizado.

### Fase 2: Análisis Léxico (Tokenización)

  - Una vez limpio el código, el programa lo procesa secuencialmente para identificar y clasificar cada "palabra" o símbolo (token).

-----

## 🔖 Clasificación de Tokens

El analizador clasifica los tokens encontrados en las siguientes categorías:

| Tipo de Token | Descripción | Ejemplos |
| :--- | :--- | :--- |
| `PALABRA_CLAVE` | Palabras reservadas del lenguaje. | `if`, `else`, `while`, `return` |
| `IDENTIFICADOR` | Nombres de variables o funciones. | `miVariable`, `_contador`, `calcular` |
| `NUMERO` | Secuencias de dígitos numéricos. | `123`, `42`, `9` |
| `OPERADOR` | Símbolos para operaciones matemáticas o lógicas. | `+`, `-`, `*`, `/`, `=` |
| `DELIMITADOR` | Símbolos de puntuación y agrupación. | `;`, `(`, `)`, `{`, `}` |
| `ERROR` | Cualquier carácter o secuencia no reconocida. | `$`, `#`, `?` |

-----

## 💻 Formato de Salida

Por cada token identificado, el programa debe imprimir en la consola un reporte con el siguiente formato, mostrando su clasificación y su valor:

```
<TIPO_DE_TOKEN> -> <VALOR_DEL_TOKEN>
```

**Ejemplo de salida:**

```
PALABRA_CLAVE -> if
DELIMITADOR -> (
IDENTIFICADOR -> x
OPERADOR -> >
NUMERO -> 10
DELIMITADOR -> )
...
```

-----

## 🛠️ Requisitos de Implementación

  - **Modularidad:** El código debe estar bien organizado, separando la lógica en funciones claras para la lectura, limpieza y el análisis léxico.
  - **Funciones Clave:**
      - Implementar una función específica (`esPalabraClave`) que verifique si un lexema corresponde a una palabra clave.
      - Crear una función principal de análisis que clasifique cada lexema en su tipo de token correspondiente.
  - **Manejo de Estructura:** El programa debe ser capaz de procesar el código fuente de manera secuencial, manejando correctamente los espacios, tabulaciones y saltos de línea.

-----

## 🚀 Entrega Final

  - Presentación y exposición del funcionamiento del programa.
  - Revisión y explicación del código fuente desarrollado.
