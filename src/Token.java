//Esta clase almacenara la informacion de cada token encontrado en el archivo limpio

public class Token {
    private final TipoToken tipo;
    private final String valor;

    public Token(TipoToken tipo, String valor){
        this.tipo=tipo;
        this.valor=valor;
    }

    @Override
    public String toString(){
        //Aca usamos herencia y polimorfismo, toString es un metodo de la superclase object
        //Va imprimir los atributos del token en el formato q pide el profe:
        // <TIPO_DE_TOKEN> -> <VALOR_DEL_TOKEN>
        return String.format("<%s> -> <%s>", tipo, valor);
    }
}
