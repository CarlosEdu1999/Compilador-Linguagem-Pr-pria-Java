package unitri.compiladores.lexico;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ClassificadorPalavras {

    public static int variavelAnterior;

    /*
    def minhaFuncao(var1:texto):nada{
    def minhaFuncao ( var1 : texto ) : nada {
    minhaFuncao(var1 = minhafucao ( var1
    nome = "esse é meu nome";
     */

    public static ArrayList<Token> classifica(BufferedReader texto){
        String linha = null;

        try {
            int nrLinha = 1;
            boolean first = true;
            while ((linha = texto.readLine()) != null){
                first = true;
                linha = linha.replaceAll("("
                        + "^\\p{Alpha}tarefa^\\p{Alpha}|^\\p{Alpha}inteiro^\\p{Alpha}|^\\p{Alpha}real^\\p{Alpha}|"
                        + "^\\p{Alpha}texto^\\p{Alpha}|^\\p{Alpha}nada^\\p{Alpha}|^\\p{Alpha}senao^\\p{Alpha}|"
                        + "^\\p{Alpha}caso^\\p{Alpha}|^\\p{Alpha}sempreQ^\\p{Alpha}|^\\p{Alpha}retruca^\\p{Alpha}|"
                        + "^\\p{Alpha}escuta^\\p{Alpha}|^\\p{Alpha}exibir^\\p{Alpha}|"
                        + "\\(|\\)|\\{|\\}|\\,|\\;|^\\p{Alpha}doTipo^\\p{Alpha}"
                        + "\\+|\\-|\\*|\\/|\\%|"
                        + "\\<=|\\>=|\\==|\\!=|\\<|\\>|"
                        + "\\+=|\\-=|\\*=|\\/=|\\\"|^\\p{Alpha}memoQV^\\p{Alpha}"
                        + ")", " $1 ");
                linha = linha.replace("  ", " ");

                String aux = linha;
                int coluna = 0;
                variavelAnterior = 0;

                StringTokenizer st = new StringTokenizer(linha);
                while (st.hasMoreElements()){
                    String imagem = st.nextToken();

                    //indica um comentário no código fonte.
                    if(imagem.equals("#")){
                        break;
                    }

                    if(!imagem.startsWith("\"")){
                        coluna = posicaoColuna(linha, imagem, first);
                        linha = linha.substring(linha.indexOf(imagem) + imagem.length());
                        AnalisadorLexico.analisar(imagem, nrLinha, coluna);
                    }else{
                        coluna = posicaoColuna(linha, imagem, first);
                        linha = linha.substring(linha.indexOf(imagem) - imagem.length());

                        if(!imagem.endsWith("\"") || imagem.length() == 1){
                            int init = variavelAnterior - imagem.length();
                            imagem = st.nextToken();
                            coluna = posicaoColuna(linha, imagem, first);
                            linha = linha.substring(linha.indexOf(imagem)+imagem.length());
                            while (!imagem.endsWith("\"")){
                                imagem = st.nextToken();
                                coluna = posicaoColuna(linha, imagem, first) + 1 - imagem.length();
                                linha = linha.substring(linha.indexOf(imagem) + imagem.length());
                            }
                            AnalisadorLexico.adicionaConstanteLiteralTexto(aux.substring(init, variavelAnterior-1), nrLinha, coluna);
                        }else{
                            AnalisadorLexico.adicionaConstanteLiteralTexto(aux.substring(coluna, variavelAnterior-1), nrLinha, coluna);
                        }
                    }
                    first = false;
                }
                nrLinha++;
            }
            AnalisadorLexico.finalCadeia();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static int posicaoColuna(String linha, String imagem, boolean first) {
        int incremento = (first)? 1 : 2;
        variavelAnterior += linha.indexOf(imagem) + incremento;
        return variavelAnterior;
    }
}
