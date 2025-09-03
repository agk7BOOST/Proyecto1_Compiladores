//Esta clase va contener el Analizador Lexico
//Basicamente va "limpiar" el codigo fuente de un archivo .txt
//Esta es la fase 0 conocida como "Preprocesamiento", en esta le quitamos espacios en blanco
// y los comentarios al codigo fuente, eso significa limpiar en este contexto

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    public String limpiarCodigo(String rutaArchivo) throws IOException{ //fase 0
        //Se leen todas las lineas del archivo del parametro.
        //Al instanciar el String puede haber un error al abrir el archivo
        //Si las cosas salen mal, el metodo lanza la excepcion aca mismo jejejej
        String codigoFuente = new String(Files.readAllBytes(Paths.get(rutaArchivo)));

        //Expresion regular que elimina comentarios de bloque "/*micomentario*/
        //Basicamente ayuda para limpiar comentarios multilinea
        String sinComentariosDeBloque = codigoFuente.replaceAll("(?s)/\\*.*?\\*/", "");
        //(?s) permite que el "." en la expresion regular incluya saltos de linea
        //asi no se escapa ni un comentario con saltos de linea
        // "/\*.*?\*/" busca /*, luego cualquier caracter que le siga( con ".*?")
        // hasta encontrar el */ que cierra el comentario
        // (recordar que funciona pq incluye saltos de linea luego del */)

        //Este elimina los comentarios de linea (//...) del resultado anterior
        //Lo pones por separado para que no te borre todo el puto codigo luego del primer "//comentario";
        //por culpa de la bandera (?s)
        String sinComentarios = sinComentariosDeBloque.replaceAll("//.*", "");


        //Eliminamos espacios y tabulaciones al inicio y final de cada linea
        //Tambien elimina lineas vacias
        return sinComentarios
                //Este bloque de codigo basicamente procesa el codigo linea por linea
                //para quitar espacios con trim()
                //De paso eliminamos tambien todas las lineas que quedaron completamente vacias luego de
                //la limpieza
                .lines()
                .map(String::trim)
                .filter(linea->!linea.isEmpty())
                .collect(Collectors.joining("\n"));
    }

    //Conjunto de palabras clave para una verificacion rapidita
    private static final Set<String> PALABRAS_CLAVE= new HashSet<>(Set.of(
            "if","else","while","return","int","float","void","for"));
            //Este es nuestra lista de palabras clave

    //Funcion para verificar si un lexema es palabra clave REQUISITO DE PROYECTO
    private boolean esPalabraClave(String lexema){
        return PALABRAS_CLAVE.contains(lexema.toLowerCase());
        //Basicamente, si lo que le metes a la funcion se encuentra en el set
        //de palabras clave, devuelve verdadero
    }

    //Funcion principal de analisis lexico (Nuestra fase 1 de cualquier compilador)
    //Clasifica cada lexema en un tipo de token
    //Devuelve una lista de objetos Token con su valor y tipo
    //Este es REQUISITO ADICIONAL de la tarea
    public List<Token> analizar(String codigoLimpio){
        List<Token> tokens=new ArrayList<>();


        //Expresion regular para identificar todos los posibles tokens
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
        Pattern pattern= Pattern.compile(patronTokens);
        Matcher matcher = pattern.matcher(codigoLimpio);
        //el matcher reccorre la cadena de codigo limpio buscando coincidencias con nuestro patron en patronTokens


        //Dentro de este while, se verifica que grupo tuvo una coincidencia
        //(matcher.group("NOMBRE")!=null) y se crea el token correspondiente
        while(matcher.find()){
            if (matcher.group("ESPACIO")!=null){
                continue;
                //esto ignora los espacios
            }
            if(matcher.group("COMBINACION")!=null){
                String lexema = matcher.group("COMBINACION");
                //COMBINACIONES tiene combinaciones alfanumericas
                //por lo que puede tratarse de identificadores (variables, parametros, etc)
                //o alguna palabra clave (void, if, else, while, int, etc...)
                if(esPalabraClave(lexema)){
                    tokens.add(new Token(TipoToken.PALABRA_CLAVE, lexema));
                    //Si detecta que tiene alguna palabra clave entonces instancia un token tipo PALABRA_CLAVE
                } else {
                    tokens.add(new Token(TipoToken.IDENTIFICADOR, lexema));
                    //Si detecta cualquier otra combinacion alfanumerica instancia un token IDENTIFICADOR
                }
                continue;
            }
            if(matcher.group("NUMERO")!=null){
                tokens.add(new Token(TipoToken.NUMERO, matcher.group("NUMERO")));
                continue;
            }
            if(matcher.group("OPERADOR")!=null){
                tokens.add(new Token(TipoToken.OPERADOR, matcher.group("OPERADOR")));
                continue;
            }
            if(matcher.group("DELIMITADOR")!=null){
                tokens.add(new Token(TipoToken.DELIMITADOR, matcher.group("DELIMITADOR")));
                continue;
            }
            //Cualquier caracter que no coincida pues es clasificado como error
            if(matcher.group("ERROR")!=null && !matcher.group("ERROR").trim().isEmpty()){
                tokens.add(new Token(TipoToken.ERROR, matcher.group("ERROR")));
            }
        }
        return tokens;
    }

}
