package unitri.compiladores;

import unitri.compiladores.lexico.AnalisadorLexico;
import unitri.compiladores.lexico.ClassificadorPalavras;
import unitri.compiladores.lexico.TabelaSimbolos;
import unitri.compiladores.semantico.AnalisadorSemantico;
import unitri.compiladores.sintatico.AnalisadorSintaticoGeradorArvore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Main {

    public static void main(String[] args) {
        lexico();
        sintatico();
        semantico();
    }

    private static void semantico() {
        System.out.println("----->>>> ANALISADOR SEMANTICO <<<<-----");
        AnalisadorSemantico semantico = new AnalisadorSemantico(AnalisadorSintaticoGeradorArvore.getRaiz());
        semantico.analisar();

        if(!AnalisadorSemantico.getErros().isEmpty()){
            System.out.println("!!!!!! Analisador Semantico com os seguintes erros: !!!!!!");
            AnalisadorSemantico.mostraErros();
            System.exit(0);
        }else{
            System.out.println("@@@@@ Análise Semântica Concluída com Sucesso @@@@@");
            TabelaSimbolos.mostraSimbolos();
        }
    }

    private static void sintatico() {
        System.out.println("----->>>> ANALISADOR SINTÁTICO <<<<-----");
        AnalisadorSintaticoGeradorArvore sintatico = new AnalisadorSintaticoGeradorArvore(AnalisadorLexico.getTokens());
        sintatico.analisar();

        if(!AnalisadorSintaticoGeradorArvore.getErros().isEmpty()){
            System.out.println("!!!!!! Analisador Sintático com os seguintes erros: !!!!!!");

            System.out.println(AnalisadorSintaticoGeradorArvore.getErros());
            System.exit(0);
        }else{
            System.out.println("@@@@@ Análise Sintático Concluída com Sucesso @@@@@");
            AnalisadorSintaticoGeradorArvore.mostraArvore();
        }

    }

    private static void lexico() {
        try{
            File file = new File("C:\\Users\\Carlos\\Pictures\\Compiladores_5_periodo\\src\\soma5Periodo");
            BufferedReader br = new BufferedReader(new FileReader(file));
            ClassificadorPalavras.classifica(br);

            System.out.println("----->>>> ANALISADOR LÉXICO <<<<-----");
            if(!AnalisadorLexico.getErros().isEmpty()){
                System.out.println("!!!!!! Analisador Léxico com os seguintes erros: !!!!!!");
                AnalisadorLexico.printErros();
                System.exit(0);
            }else{
                System.out.println("@@@@@ Análise Léxico Concluída com Sucesso @@@@@");
                AnalisadorLexico.printTokens();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
