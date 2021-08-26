package unitri.compiladores.semantico;

import unitri.compiladores.lexico.Erros;
import unitri.compiladores.lexico.TabelaSimbolos;
import unitri.compiladores.lexico.Token;
import unitri.compiladores.sintatico.No;

import java.util.ArrayList;

public class AnalisadorSemantico {
    private static No raiz;
    private static ArrayList<String> erros = new ArrayList<>();
    private static Token defAtual;


    public AnalisadorSemantico(No raiz) {
        this.raiz = raiz;
    }

    public static No getRaiz() {
        return raiz;
    }

    public static void setRaiz(No raiz) {
        AnalisadorSemantico.raiz = raiz;
    }

    public static ArrayList<String> getErros() {
        return erros;
    }

    public static void setErros(ArrayList<String> erros) {
        AnalisadorSemantico.erros = erros;
    }

    public static Token getDefAtual() {
        return defAtual;
    }

    public static void setDefAtual(Token defAtual) {
        AnalisadorSemantico.defAtual = defAtual;
    }

    public void analisar() {
        analisar(raiz);
    }

    public Object analisar(No no) {

        switch (no.getTipo()) {
            case NO_LISTDEF:
                return ListDef(no);
            case NO_DEF:
                return Def(no);
            case NO_LISTPARAM:
                return ListParam(no);
            case NO_LISTPARAM2:
                return ListParam2(no);
            case NO_PARAM:
                return Param(no);
            case NO_TIPO:
                return Tipo(no);
            case NO_LISTCOMAN:
                return ListComan(no);
            case NO_COMAN:
                return Coman(no);
            case NO_DECL:
                return Decl(no);
            case NO_LISTID:
                return ListId(no);
            case NO_LISTID2:
                return ListId2(no);
            case NO_ATRIB:
                return Atrib(no);
            case NO_EXPARIT:
                return ExpArit(no);
            case NO_EXPARIT2:
                return ExpArit2(no);
            case NO_TERMO:
                return Termo(no);
            case NO_TERMO2:
                return Termo2(no);
            case NO_FATOR:
                return Fator(no);
            case NO_OPERANDO:
                return Operando(no);
            case NO_OP1:
                return Op1(no);
            case NO_OP2:
                return Op2(no);
            case NO_OP3:
                return Op3(no);
            case NO_SE:
                return Se(no);
            case NO_SENAO:
                return Senao(no);
            case NO_EXPREL:
                return ExpRel(no);
            case NO_LACO:
                return Laco(no);
            case NO_RET:
                return Ret(no);
            case NO_ENTRADA:
                return Entrada(no);
            case NO_SAIDA:
                return Saida(no);
            case NO_CHAMADA:
                return Chamada(no);
            case NO_LISTARG:
                return ListArg(no);
            case NO_LISTARG2:
                return ListArg2(no);
        }

        return null;
    }

    private Object ListArg2(No no) {
        return null;
    }

    private Object ListArg(No no) {
        return null;
    }

    //              0   1      2      3   4
    //<Chamada> ::= id ‘(‘ <ListArg> ‘)’ ';'
    private Object Chamada(No no) {
        Token id = no.getFilho(0).getToken();

        ArrayList<Token> params = TabelaSimbolos.getParamDef(id);
        ArrayList<Token> listArg = (ArrayList<Token>) analisar(no.getFilho(2));

        if (params.size() == listArg.size()) {
            for (int i = 0; i < listArg.size(); i++) {
                if (!TabelaSimbolos.getTipo(params.get(i)).equals(listArg.get(i))) {
                    erros.add("Argumento " + listArg.get(i).getImagem() + " possui tipo incompatível com o argumento " + params.get(i).getImagem() + " declarado na função");
                }
            }
        }else{
            erros.add("Quantidade de argumentos incorreto para a função "+ id.getImagem());
        }

        return null;
    }

    private Object Saida(No no) {
        return null;
    }

    private Object Entrada(No no) {
        return null;
    }

    //              0        1     2
    //<Ret> ::= ‘retorna’ <Fator> ‘;’
    private Object Ret(No no) {
        String tipoDef = TabelaSimbolos.getTipo(defAtual);
        ArrayList<Token> fator = (ArrayList<Token>) analisar(no.getFilho(1));

        for (Token token :
                fator) {
            if (TabelaSimbolos.getTipo(token) == null) {
                erros.add("Variável " + token.getImagem() + " não declarada");
            } else {
                if (!TabelaSimbolos.getTipo(token).equals(tipoDef)) {
                    erros.add("Tipo retornado (" + token.getImagem() + ") é diferente do declarado na função");
                }
            }
        }

        return null;
    }

    //              0         1      2      3     4
    //<Laco> ::= ‘enquanto’  ‘(‘  <ExpRel> ’)’ <Coman>
    private Object Laco(No no) {
        analisar(no.getFilho(2));
        analisar(no.getFilho(4));
        return null;
    }

    //                 0       1        2
    //<ExpRel> ::= <ExpArit> <Op3> <ExprArit>
    private Object ExpRel(No no) {
        ArrayList<Token> expAritE = (ArrayList<Token>) analisar(no.getFilho(0));
        ArrayList<Token> expAritD = (ArrayList<Token>) analisar(no.getFilho(2));

        String tipoE = TabelaSimbolos.getTipo(expAritE.get(0));
        String tipoD = TabelaSimbolos.getTipo(expAritD.get(0));

        if (tipoE == null) {
            erros.add("Operando do lado esquerdo (" + expAritE.get(0).getImagem() + ") da expressão relacional não foi declarado!");
        } else {
            for (Token token :
                    expAritE) {
                if (TabelaSimbolos.getTipo(token) == null) {
                    erros.add("A variável " + token.getImagem() + " não foi declarada!");
                } else {
                    if (!TabelaSimbolos.getTipo(token).equals(tipoE)) {
                        erros.add("Operandos (" + token.getImagem() + " e " + expAritE.get(0).getImagem() + ") com tipos incompatíveis");
                    }
                }
            }
        }

        if (tipoD == null) {
            erros.add("Operando do lado direito (" + expAritD.get(0).getImagem() + ") da expressão relacional não foi declarado!");
        } else {
            for (Token token :
                    expAritD) {
                if (TabelaSimbolos.getTipo(token) == null) {
                    erros.add("A variável " + token.getImagem() + " não foi declarada!");
                } else {
                    if (!TabelaSimbolos.getTipo(token).equals(tipoD)) {
                        erros.add("Operandos (" + token.getImagem() + " e " + expAritD.get(0).getImagem() + ") com tipos incompatíveis");
                    }
                }
            }
        }

        if (tipoE != null && tipoD != null && !tipoE.equals(tipoD)) {
            erros.add("Tipo da expressão do lado esquerdo é incompatível com a do lado direito");
        }
        return null;
    }

    //               0       1
    //<Senao> ::= ‘senao’ <Coman> |
    private Object Senao(No no) {
        if (!no.getFilhos().isEmpty()) {
            analisar(no.getFilho(1));
        }
        return null;
    }

    //          0    1     2      3    4        5
    //<Se> ::= ‘se’ ‘(‘ <ExpRel> ‘)’ <Coman> <Senao>
    private Object Se(No no) {
        analisar(no.getFilho(2));
        analisar(no.getFilho(4));
        analisar(no.getFilho(5));
        return null;
    }

    private Object Op3(No no) {
        return null;
    }

    private Object Op2(No no) {
        return null;
    }

    private Object Op1(No no) {
        return null;
    }

    /*
    <Operando> ::= id
			| cli
			| clr
			| clt
     */
    private Object Operando(No no) {
        return no.getFilho(0).getToken();
    }

    //                0            0        0      1      0
    //<Fator> ::= <Operando> | <Chamada> | ‘(‘ <ExpArit> ‘)’
    private Object Fator(No no) {
        if (no.getFilhos().size() == 1) {
            Token token = (Token) analisar(no.getFilho(0));
            ArrayList<Token> fator = new ArrayList<>();

            fator.add(token);
            return fator;
        } else {
            return analisar(no.getFilho(1));
        }
    }

    //                 0      1
    //<Termo2> ::= | <Op2> <Termo>
    private Object Termo2(No no) {
        if (no.getFilhos().isEmpty()) {
            return new ArrayList<Token>();
        } else {
            return analisar(no.getFilho(1));
        }
    }

    //               0      1
    //<Termo> ::= <Fator><Termo2>
    private Object Termo(No no) {
        ArrayList<Token> fator = (ArrayList<Token>) analisar(no.getFilho(0));
        ArrayList<Token> termo2 = (ArrayList<Token>) analisar(no.getFilho(1));

        fator.addAll(termo2);
        return fator;
    }

    //                    0       1
    //<ExpArit2> ::=  | <Op1> <ExpArit>
    private Object ExpArit2(No no) {
        if (no.getFilhos().isEmpty()) {
            return new ArrayList<Token>();
        } else {
            ArrayList<Token> expArit = (ArrayList<Token>) analisar(no.getFilho(1));
            return expArit;
        }
    }

    //                 0       1
    //<ExpArit> ::= <Termo><ExpArit2>
    private Object ExpArit(No no) {
        ArrayList<Token> termo = (ArrayList<Token>) analisar(no.getFilho(0));
        ArrayList<Token> expArit2 = (ArrayList<Token>) analisar(no.getFilho(1));

        termo.addAll(expArit2);
        return termo;
    }

    //            0   1      2      3
    //<Atrib> ::= id ‘=‘ <ExpArit> ‘;’
    private Object Atrib(No no) {
        Token id = no.getFilho(0).getToken();
        String tipo = TabelaSimbolos.getTipo(id);

        if (tipo != null) {
            ArrayList<Token> expArit = (ArrayList<Token>) analisar(no.getFilho(2));

            for (Token operando :
                    expArit) {
                String tipoOperando = TabelaSimbolos.getTipo(operando);

                if (tipoOperando == null) {
                    erros.add("A variável do lado direito da atribuição  " + operando.getImagem() + " não foi declarada");
                } else if (!tipoOperando.equals(tipo)) {
                    erros.add("Tipos incompatíveis " + operando.getImagem());
                }

            }
        } else {
            erros.add("Variável " + id.getImagem() + " não foi declarada!");
        }

        return null;

    }

    //              0   1       2
    //<ListId2> ::= ‘,’ id <ListId2> |
    private Object ListId2(No no) {
        if (no.getFilhos().isEmpty()) {
            return new ArrayList<Token>();
        } else {
            Token id = no.getFilho(1).getToken();
            ArrayList<Token> listId2 = (ArrayList<Token>) analisar(no.getFilho(2));
            listId2.add(0, id);
            return listId2;
        }
    }

    //              0     1
    //<ListId> ::= id <ListId2>
    private Object ListId(No no) {
        Token id = no.getFilho(0).getToken();
        ArrayList<Token> listId2 = (ArrayList<Token>) analisar(no.getFilho(1));

        listId2.add(0, id);
        return listId2;
    }

    //              0      1     2    3
    //<Decl> ::= <ListId> ‘:’ <Tipo> ‘;’
    private Object Decl(No no) {
        ArrayList<Token> listId = (ArrayList<Token>) analisar(no.getFilho(0));
        String tipo = (String) analisar(no.getFilho(2));

        for (Token id :
                listId) {
            if (TabelaSimbolos.getTipo(id) != null) {
                erros.add("Variável " + id.getImagem() + " já foi declarada anteriormente");
            } else {
                TabelaSimbolos.setTipo(id, tipo);
            }
        }
        return null;
    }

    /*
    <Coman> ::= <Decl> x
			| <Atrib> x
			| <Se>
			| <Laco>
			| <Ret>
			| <Entrada>
			| <Saida>
			| <Chamada> ‘;’ x
			   0       1
			| ‘{’ <ListComan> ‘}’
     */
    private Object Coman(No no) {
        if (no.getFilhos().size() > 1) {
            analisar(no.getFilho(1));
        } else {
            analisar(no.getFilho(0));
        }
        return null;
    }

    //                    0        1
    //<ListComan> ::=  <Coman><ListComan> |
    private Object ListComan(No no) {
        if (!no.getFilhos().isEmpty()) {
            analisar(no.getFilho(0));
            analisar(no.getFilho(1));
        }
        return null;
    }

    /*
    <Tipo> ::= ‘inteiro’
		| ‘real’
		| ‘texto’
		| ‘nada’
		|
     */
    private Object Tipo(No no) {
        return no.getFilho(0).getToken().getImagem();
    }

    //            0   1    2
    //<Param> ::= id ‘:’ <Tipo>
    private Object Param(No no) {
        Token token = no.getFilho(2).getToken();
        String tipo = (String) analisar(no.getFilho(1));
        String tipoId = TabelaSimbolos.getTipo(token);
        if (tipoId == null) {
            TabelaSimbolos.setTipo(token, tipo);
            TabelaSimbolos.setParam(token);
        } else {
            erros.add("A variável " + token.getImagem() + " já foi declarada!");
        }

        return null;
    }

    //                  0      1        2
    //<ListParam2> ::=  ‘,’ <Param><ListParam2> |
    private Object ListParam2(No no) {
        if (!no.getFilhos().isEmpty()) {
            analisar(no.getFilho(1));
            analisar(no.getFilho(2));
        }
        return null;
    }

    //                   0        1
    //<ListParam> ::= <Param><ListParam2> |
    private Object ListParam(No no) {
        if (!no.getFilhos().isEmpty()) {
            analisar(no.getFilho(0));
            analisar(no.getFilho(1));
        }

        return null;
    }

    //            0    1   2      3       4     5     6     7      8       9
    //<Def> ::= ‘def’  id ‘(‘ <ListParam> ‘)’  ‘:’  <Tipo> ‘{‘ <ListComan> ‘}’
    private Object Def(No no) {
        Token id = no.getFilho(1).getToken();
        String tipo = (String) analisar(no.getFilho(6));

        defAtual = id;

        if (TabelaSimbolos.getTipo(id) != null) {
            erros.add("Essa função " + id.getImagem() + " já foi declarada!");
        } else {
            TabelaSimbolos.setTipo(id, tipo);
            analisar(no.getFilho(3));
            analisar(no.getFilho(8));
        }
        return null;
    }

    //                0      1
    //<ListDef> ::= <Def><ListDef> |
    private Object ListDef(No no) {
        if (!no.getFilhos().isEmpty()) {
            analisar(no.getFilho(0));
            analisar(no.getFilho(1));
        }

        return null;
    }

    public static void mostraErros(){
        for (String erro :
                erros) {
            System.out.println(erro);
        }
    }
}
