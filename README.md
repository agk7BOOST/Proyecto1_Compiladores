# ⚙️ Compilador Didáctico con Interfaz Gráfica

Proyecto universitario para la materia de Compiladores, enfocado en las seis fases fundamentales del procesamiento de lenguajes de programación: análisis léxico, sintáctico, semántico, optimización de código, generación de código y simulación.

**Integrantes:**

  - Ivan Alvarenga
  - Juan Isidro Garcia
  - Maria Jazmin Caceres
  - Ciro Sosa
  - Victor Duarte

-----

## 📸 Vista Previa de la Aplicación

<img width="1916" height="984" alt="image" src="https://github.com/user-attachments/assets/6dd1de41-6132-4103-af2f-f4d85b0046af" />

-----

## 🚀 Cómo Usar el Programa

Tienes dos formas de ejecutar este compilador:

### Opción 1: Ejecutable `.jar` (Recomendado)

1.  Ve a la sección de **[Releases](https://github.com/agk7BOOST/Proyecto1_Compiladores/releases)** en este repositorio.
2.  Descarga el archivo `AnalizadorLexico.jar` de la última versión.
3.  Asegúrate de tener Java 8 o una versión superior instalada en tu sistema.
4.  Haz doble clic en el archivo `.jar` para ejecutar la aplicación.

### Opción 2: Desde el Código Fuente

1.  Clona este repositorio en tu máquina local.
2.  Ábrelo con un IDE como IntelliJ IDEA.
3.  Ejecuta el método `main` en la clase `Principal.java`.

-----

## 🎯 Fases del Compilador

Esta herraminenta fue desarrollada para visualizar y comprender las diferentes estapas de la compilación de un lenguaje simple.

1.  **Análisis Léxico:** El código fuente es procesado para eliminar comentarios y espacios innecesarios. Luego, se identifican y clasifican los componentes léxicos (tokens).
2.  **Análisis Sintáctico:** Los tokens son analizados para verificar que la estructura del código sea gramaticalmente correcta. Como resultado, se genera un Árbol de Sintaxis Abstracta (AST).
3.  **Análisis Semántico:** Se revisa el AST en busca de errores semánticos, como el uso de variables no declaradas o la asignación de tipos de datos incompatibles.
4.  **Optimización de Código:** Se aplican técnicas de optimización al AST para mejorar la eficiencia del código sin alterar su funcionalidad.
5.  **Generación de Código:** A partir del AST optimizado, se genera código equivalente en otro lenguaje (en este caso C++).
6.  **Simulación:** El código original es ejecutado en un simulador para mostrar el resultado en una consola.

-----

## 🔖 Clasificación de Tokens

El analizador clasifica los tokens encontrados en las siguientes categorías:

| Tipo de Token | Descripción | Ejemplos |
| :--- | :--- | :--- |
| `PALABRA_CLAVE` | Palabras reservadas del lenguaje. | `if`, `else`, `while`, `for`, `imprimir` |
| `IDENTIFICADOR` | Nombres de variables o funciones. | `miVariable`, `_contador`, `calcular` |
| `NUMERO` | Secuencias de dígitos numéricos. | `123`, `42`, `9` |
| `STRING` | Secuencias de caracteres entre comillas. | `"Hola, mundo!"` |
| `OPERADOR` | Símbolos para operaciones matemáticas o lógicas. | `+`, `-`, `*`, `/`, `=`, `==`, `!=` |
| `DELIMITADOR` | Símbolos de puntuación y agrupación. | `;`, `(`, `)`, `{`, `}` |
| `ERROR` | Cualquier carácter o secuencia no reconocida. | `$`, `#`, `?` |

-----

## 💻 Formato de Salida

La interfaz gráfica permite visualizar el resultado de cada una de las fases de compilación en tiempo real.

**Ejemplo de salida en la pestaña de Análisis Léxico:**

```
--- CÓDIGO LIMPIO ---
imprimir("--- Bucle For ---");
for(int i=0; i<5; i=i+1){
if(i==3){
imprimir("i es tres!");
}else{
imprimir(i);
}
}

--- TOKENS ENCONTRADOS ---
<PALABRA_CLAVE  > imprimir
<DELIMITADOR    > (
<STRING         > "--- Bucle For ---"
<DELIMITADOR    > )
<DELIMITADOR    > ;
...
```
