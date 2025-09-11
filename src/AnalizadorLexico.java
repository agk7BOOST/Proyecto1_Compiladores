//Esta clase va contener el Analizador Lexico
//Basicamente va "limpiar" el codigo fuente de un archivo .txt
//Esta es la fase 0 conocida como "Preprocesamiento", en esta le quitamos espacios en blanco
// y los comentarios al codigo fuente, eso significa limpiar en este contexto

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AnalizadorLexico {
    //El metodo devuelve una cadena de texto, la que vamos a limpiar
    //Y recibe como parametro la ruta del archivo, el cual guarda como cadena :)
    //El throws es muy recomendable ya que al intentar abrir un archivo
    //necesitamos "blindarnos" en caso de que ocurra un error
    //Basicamente, si algo falla en este metodo, este va devolver la excepcion correspondiente
    //(cosas de java jejejejj)
    public String limpiarCodigo(String codigoFuente) { //fase 0
        //Expresion regular que elimina comentarios de bloque "/*micomentario*/
        //Basicamente ayuda para limpiar comentarios multilinea
        String sinComentariosDeBloque = codigoFuente.replaceAll("(?s)/\\*.*?\\*/", "");
        //(?s) permite que el "." en la expresion regular incluya saltos de linea
        //asi no se escapa ni un comentario con saltos de linea
        // "/\*.*?\*/" busca /*, luego cualquier caracter que le siga( con ".*?")
        // hasta encontrar el */ que cierra el comentario
        // (recordar que funciona pq incluye saltos de linea luego del */)

        //Este elimina los comentarios de linea (//...) del resultado anterior
        //Lo pones por separado para que no te borre el total del puto codigo luego del primer "//comentario";
        //por culpa de la bandera (?s)
        String sinComentarios = sinComentariosDeBloque.replaceAll("//.*", "");


        //Eliminamos espacios y tabulaciones al inicio y final de cada linea
        //Tambien elimina lineas vacias
        // Dividimos la cadena por saltos de línea y la convertimos en un Stream
        return Arrays.stream(sinComentarios.split("\\r?\\n"))
                .map(String::trim)
                .filter(linea -> !linea.isEmpty())
                .collect(Collectors.joining("\n"));
        //Este bloque de codigo basicamente procesa el codigo linea por linea
        //para quitar espacios con trim()
        //De paso eliminamos tambien todas las lineas que quedaron completamente vacias luego de
        //la limpieza
    }


    //Conjunto de palabras clave para una verificacion rapidita
    private static final Set<String> PALABRAS_CLAVE = new HashSet<>(Arrays.asList(
            "if", "else", "while", "return", "int", "float", "void", "for"
    ));
    //Este es nuestra lista de palabras clave

    //Funcion para verificar si un lexema es palabra clave REQUISITO DE PROYECTO
    private boolean esPalabraClave(String lexema) {
        return PALABRAS_CLAVE.contains(lexema.toLowerCase());
        //Basicamente, si lo que le metes a la funcion se encuentra en el set
        //de palabras clave, devuelve verdadero
    }

    //Funcion principal de analisis lexico (Nuestra fase 1 de cualquier compilador)
    //Clasifica cada lexema en un tipo de token
    //Devuelve una lista de objetos Token con su valor y tipo
    //Este es REQUISITO ADICIONAL de la tarea
    public List<Token> analizar(String codigoLimpio) {
        List<Token> tokens = new ArrayList<>();

        //PatronTokens es una expresion regular para identificar todos los posibles tokens
        //Sin tantas vueltas que no es calecita:
        //RegEx gigante que une las reglas para cada tipo de token usando sentencias de OR
        //que es RegEx? notacion rara de java para buscar cosas en textos
        //Se usan "grupos nombrados" (?<NOMBRE>...) para identificar que tipo de token se encontro
        String patronTokens =
                "(?<COMBINACION>[a-zA-Z_][a-zA-Z0-9_]*)"
                        + "|(?<NUMERO>\\d+)"
                        + "|(?<OPERADOR>[+\\-*/=])"
                        + "|(?<DELIMITADOR>[;(){}\\[\\]])"
                        + "|(?<ESPACIO>\\s+)"   //Esto es para que reconozca los espacios
                        + "|(?<ERROR>.)"; //Al final captura cualquier otro caracter como error
        Pattern pattern = Pattern.compile(patronTokens);
        Matcher matcher = pattern.matcher(codigoLimpio);
        //el matcher reccorre la cadena de codigo limpio buscando coincidencias con nuestro patron en patronTokens


        //Dentro de este while, se verifica que grupo tuvo una coincidencia
        //(matcher.group("NOMBRE")!=null) y se crea el token correspondiente
        while (matcher.find()) {
            if (matcher.group("ESPACIO") != null) {
                continue;
                //esto ignora los espacios
            }


            if (matcher.group("COMBINACION") != null) {
                //COMBINACIONES tiene combinaciones alfanumericas
                //por lo que puede tratarse de identificadores (variables, parametros, etc)
                //o alguna palabra clave (void, if, else, while, int, etc...)
                String lexema = matcher.group("COMBINACION");
                if (esPalabraClave(lexema)) {
                    tokens.add(new Token.Builder()
                            .conTipo(TipoToken.PALABRA_CLAVE)
                            .conValor(lexema)
                            .construir());
                    //Si detecta que tiene alguna palabra clave entonces instancia un token tipo PALABRA_CLAVE
                } else {
                    tokens.add(new Token.Builder()
                            .conTipo(TipoToken.IDENTIFICADOR)
                            .conValor(lexema)
                            .construir());
                    //Si detecta cualquier otra combinacion alfanumerica instancia un token IDENTIFICADOR
                }
            } else if (matcher.group("NUMERO") != null) {
                tokens.add(new Token.Builder()
                        .conTipo(TipoToken.NUMERO)
                        .conValor(matcher.group("NUMERO"))
                        .construir());
            } else if (matcher.group("OPERADOR") != null) {
                tokens.add(new Token.Builder()
                        .conTipo(TipoToken.OPERADOR)
                        .conValor(matcher.group("OPERADOR"))
                        .construir());
            } else if (matcher.group("DELIMITADOR") != null) {
                tokens.add(new Token.Builder()
                        .conTipo(TipoToken.DELIMITADOR)
                        .conValor(matcher.group("DELIMITADOR"))
                        .construir());
            } else if (matcher.group("ERROR") != null && !matcher.group("ERROR").trim().isEmpty()) {
                //Cualquier caracter que no coincida pues es clasificado como error
                tokens.add(new Token.Builder()
                        .conTipo(TipoToken.ERROR)
                        .conValor(matcher.group("ERROR"))
                        .construir());
            }
        }
        return tokens;
    }
    }
