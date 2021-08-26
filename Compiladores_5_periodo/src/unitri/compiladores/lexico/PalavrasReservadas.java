package unitri.compiladores.lexico;

import java.util.Arrays;
import java.util.List;

public class PalavrasReservadas {
    public static  final List<String> pr = initPalavraReservada();
    public static  final List<String> opa = initOperadorAritmetico();
    public static  final List<String> de = initDelimitador();
    public static  final List<String> opc = initOperadorComparativo();
    public static  final List<String> oa = initOperadorAtribuicao();

    private static List<String> initOperadorAtribuicao() {
        return Arrays.asList("=");
    }

    private static List<String> initOperadorComparativo() {
        return Arrays.asList("<","<=","==", ">=",">","!=");
    }

    private static List<String> initOperadorAritmetico() {
        return Arrays.asList("+","-","*","/");
    }

    

    private static List<String> initDelimitador() {
        return Arrays.asList("{","}","(",")",",",";","doTipo");
    }

    private static List<String> initPalavraReservada() {
        return Arrays.asList("tarefa","inteiro","real","texto","nada","caso","senao","sempreQ"
        ,"retruca","escuta","exibir");
    }
    public static boolean isPalavraReservada(String imagem){
        return pr.contains(imagem);
    }
    public static boolean isDelimitador(String imagem){
        return de.contains(imagem);
    }
    public static boolean isOperadorAritimetico(String imagem){
        return opa.contains(imagem);
    }
    public static boolean isOperadorComparativo(String imagem){
        return opc.contains(imagem);
    }
    public static boolean isOperadorAtribuicao(String imagem){
        return oa.contains(imagem);
    }
}
