# ⚙️ Analizador Léxico con Interfaz Gráfica

Proyecto universitario para la materia de Compiladores, enfocado en los fundamentos del procesamiento de lenguajes de programación.

**Integrantes:**
- Ivan Alvarenga
- Juan Isidro Garcia
- Maria Jazmin Caceres
- Ciro Sosa
- Victor Duarte

---

## 📸 Vista Previa de la Aplicación

*La interfaz permite abrir archivos de código o pegar el texto directamente para su análisis.*

---

## 🚀 Cómo Usar el Programa

Tienes dos formas de ejecutar este analizador:

### Opción 1: Ejecutable `.jar` (Recomendado)
1.  Ve a la sección de **[Releases](https://github.com/agk7BOOST/Proyecto1_Compiladores/releases)** en este repositorio.
2.  Descarga el archivo `AnalizadorLexico.jar` de la última versión.
3.  Asegúrate de tener Java 8 o una versión superior instalada en tu sistema.
4.  Haz doble clic en el archivo `.jar` para ejecutar la aplicación.

### Opción 2: Desde el Código Fuente
1.  Clona este repositorio en tu máquina local.
2.  Ábrelo con un IDE como IntelliJ IDEA.
3.  Ejecuta el método `main` en la clase `Principal.java`.

---

## 🎯 Objetivo del Proyecto

Desarrollar una herramienta capaz de realizar dos tareas fundamentales sobre un archivo de código fuente:

1.  **Preprocesamiento:** Limpiar el código eliminando comentarios y espacios innecesarios.
2.  **Análisis Léxico:** Analizar el código limpio para identificar y clasificar sus componentes léxicos (tokens).

---

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

---

## 💻 Formato de Salida

Por cada token identificado, el programa muestra su clasificación y su valor:

**Ejemplo de salida en la interfaz:**
```
--- CÓDIGO LIMPIO ---
int a=0;
for(int i=0; i<10; i++){
a=a+1;
}

--- TOKENS ENCONTRADOS ---
<PALABRA_CLAVE> -> <int>
<IDENTIFICADOR> -> <a>
<OPERADOR> -> <=>
<NUMERO> -> <0>
<DELIMITADOR> -> <;>
...
```
