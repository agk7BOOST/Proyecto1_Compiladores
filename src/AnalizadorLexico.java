// Archivo: src/AnalizadorLexico.java
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AnalizadorLexico {

    private static final Set<String> PALABRAS_CLAVE = new HashSet<>(Arrays.asList(
            "if", "else", "while", "return", "int", "float", "void", "for", "imprimir"
    ));

    public String limpiarCodigo(String codigoFuente) {
        String sinComentariosDeBloque = codigoFuente.replaceAll("(?s)/\\*.*?\\*/", "");
        String sinComentarios = sinComentariosDeBloque.replaceAll("//.*", "");
        return Arrays.stream(sinComentarios.split("\\r?\\n"))
                .map(String::trim)
                .filter(linea -> !linea.isEmpty())
                .collect(Collectors.joining("\n"));
    }

    private boolean esPalabraClave(String lexema) {
        return PALABRAS_CLAVE.contains(lexema.toLowerCase());
    }

    public List<Token> analizar(String codigoLimpio) {
        List<Token> tokens = new ArrayList<>();
        // Expresión regular actualizada para incluir strings.
        String patronTokens =
                "(?<STRING>\"[^\"]*\")" // Reconoce cualquier cosa entre comillas
                        + "|(?<OPERADOR>==|!=|<=|>=|&&|\\|\\||[+\\-*/=<>])"
                        + "|(?<COMBINACION>[a-zA-Z_][a-zA-Z0-9_]*)"
                        + "|(?<NUMERO>\\d+)"
                        + "|(?<DELIMITADOR>[;(){}\\[\\]])"
                        + "|(?<ESPACIO>\\s+)"
                        + "|(?<ERROR>.)";
        Pattern pattern = Pattern.compile(patronTokens);
        Matcher matcher = pattern.matcher(codigoLimpio);

        while (matcher.find()) {
            if (matcher.group("ESPACIO") != null) continue;

            if (matcher.group("STRING") != null) {
                tokens.add(new Token.Builder().conTipo(TipoToken.STRING).conValor(matcher.group("STRING")).construir());
            } else if (matcher.group("COMBINACION") != null) {
                String lexema = matcher.group("COMBINACION");
                if (esPalabraClave(lexema)) {
                    tokens.add(new Token.Builder().conTipo(TipoToken.PALABRA_CLAVE).conValor(lexema).construir());
                } else {
                    tokens.add(new Token.Builder().conTipo(TipoToken.IDENTIFICADOR).conValor(lexema).construir());
                }
            } else if (matcher.group("NUMERO") != null) {
                tokens.add(new Token.Builder().conTipo(TipoToken.NUMERO).conValor(matcher.group("NUMERO")).construir());
            } else if (matcher.group("OPERADOR") != null) {
                tokens.add(new Token.Builder().conTipo(TipoToken.OPERADOR).conValor(matcher.group("OPERADOR")).construir());
            } else if (matcher.group("DELIMITADOR") != null) {
                tokens.add(new Token.Builder().conTipo(TipoToken.DELIMITADOR).conValor(matcher.group("DELIMITADOR")).construir());
            } else if (matcher.group("ERROR") != null) {
                tokens.add(new Token.Builder().conTipo(TipoToken.ERROR).conValor(matcher.group("ERROR")).construir());
            }
        }
        return tokens;
    }
}