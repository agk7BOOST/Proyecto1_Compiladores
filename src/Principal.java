import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Principal {
    public static void main(String[] args) {
        //La ruta del archivo donde esta el codigo fuente a analizar
        String archivoEntrada= "src/pruebas/dirtyFor.txt";
        //Ruta para guardar el codigo limpio
        String archivoSalida="src/pruebas/cleanCode.txt";

        AnalizadorLexico lexer= new AnalizadorLexico();

        try {
            //1. Limpiar el codigo
            System.out.println("--- Limpiando el codigo fuente... ---");
            String codigoLimpio = lexer.limpiarCodigo(archivoEntrada);
//
            //2. Guardar el codigo limpio en un nuevo archivo
            Files.writeString(Paths.get(archivoSalida), codigoLimpio);
            System.out.println("Codigio limpio guardado en: "+ archivoSalida);
            System.out.println("Contenido del codigo limpio:\n"+ codigoLimpio);

            //3. Analizar el codigo limpio para obtener tokens
            System.out.println("\n--- Analisis Lexico ---");
            List<Token> tokens = lexer.analizar(codigoLimpio);

            //4. Imprimir los tokens encontrados en la consola
            for(Token token : tokens){
                System.out.println(token);
                //cuando System.out.println(token) le pregunta al objeto token
                // "Che, ¿cómo te represento como texto?",
                // tu objeto ya no responde con la versión por defecto (nombre de clase junto al codigo de memoria).
                // En su lugar, responde con el metodo que programe en la clase token usando herencia y polimorfismo:
                // return String.format("<%s> -> <%s>", tipo, valor);.
            }
        } catch (IOException e){
            //el catch agarra la excepcion y la guarda en el parametro "e"
            //asi imprimimos el mensaje
            System.out.println("Error al leer o escribir el archivo: " + e.getMessage());
        }
    }
}
