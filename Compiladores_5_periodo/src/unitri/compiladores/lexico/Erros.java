package unitri.compiladores.lexico;

public class Erros {
    private String imagem;
    private int linha;
    private int coluna;

    public Erros(String imagem, int linha, int coluna) {
        this.imagem = imagem;
        this.linha = linha;
        this.coluna = coluna;
    }

    @Override
    public String toString() {
        return "Erros{" +
                "imagem='" + imagem + '\'' +
                ", linha=" + linha +
                ", coluna=" + coluna +
                '}';
    }
}
