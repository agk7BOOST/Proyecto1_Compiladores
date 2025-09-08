//Esta clase almacenara la informacion de cada token encontrado en el archivo limpio

public class Token {
    private final TipoToken tipo;
    private final String valor;

    //este es el contructor privado
    private Token(TipoToken tipo, String valor){
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

    public static class Builder {
        private TipoToken tipo;
        private String valor;

        // Métodos para "construir" el token paso a paso
        // Devuelven el propio Builder para poder encadenar llamadas
        public Builder conTipo(TipoToken tipo) {
            this.tipo = tipo;
            return this;
        }

        public Builder conValor(String valor) {
            this.valor = valor;
            return this;
        }

        // El metod final que crea el objeto Token
        public Token construir() {
            return new Token(this.tipo, this.valor);
        }
    }
}
