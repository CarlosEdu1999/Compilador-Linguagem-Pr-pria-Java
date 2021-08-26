package unitri.compiladores.sintatico;

import unitri.compiladores.lexico.TabelaSimbolos;
import unitri.compiladores.lexico.Token;

import java.io.File;
import java.util.ArrayList;

public class AnalisadorSintaticoGeradorArvore {

    private Token token;
    private int pToken;
    private static ArrayList<Token> tokens = new ArrayList<>();
    private static ArrayList<String> erros = new ArrayList<>();
    private String escopo = "";
    private static No raiz;

    public AnalisadorSintaticoGeradorArvore(ArrayList<Token> tokens) {
        this.tokens = tokens;
    }

    public static No getRaiz(){
        return raiz;
    }
    public void leToken() {
        if (this.token != null && this.token.getClasse().equals("ID")) {
            TabelaSimbolos.addSimbolo(this.token, escopo);
        }

        if (this.pToken < this.tokens.size()) {
            this.token = tokens.get(this.pToken);
            this.pToken++;
        }
    }

    public Token lookAhead() {
        return tokens.get(this.pToken);
    }

    public Token lastToken() {
        return tokens.get(this.pToken - 2);
    }

    public void analisar() {
        this.pToken = 0;
        leToken();
        raiz = ListDef();


    }

    public void geraErro(String imagem) {
        String erro;
        Token lastToken = lastToken();

        erro = "ERRO SINTÁTICO: era esperado um(a) " + imagem + ". Erro na Linha: " + lastToken.getLinha() + "\n";

        erros.add(erro);
    }

    //<ListDef> ::= <Def><ListDef> |
    private No ListDef() {
        No no = new No(TipoNo.NO_LISTDEF);
        if (this.token.getImagem().equals("tarefa")) {
            no.addFilho(Def());
            no.addFilho(ListDef());
        }
        return no;
    }

    //<Def> ::= ‘def’  id ‘(‘ <ListParam> ‘)’  ‘:’  <Tipo> ‘{‘ <ListComan> ‘}’
    // 'doTipo'<Tipo>'tarefa' ID '('<ListParam>')''{' <ListComan> '}'
    private No Def() {
        No no = new No(TipoNo.NO_DEF);

        if (token.getImagem().equals("tarefa")) {
            no.addFilho(new No(token));
            leToken();

            if (token.getClasse().equals("ID")) {
                escopo = token.getImagem();
                no.addFilho(new No(token));
                leToken();
                if (token.getImagem().equals("(")) {

                    no.addFilho(new No(token));
                    leToken();
                    no.addFilho(ListParam());
                    if (token.getImagem().equals(")")) {
                        no.addFilho(new No(token));
                        leToken();

                        if (token.getImagem().equals("doTipo")) {
                            no.addFilho(new No(token));
                            leToken();
                            no.addFilho(Tipo());
                            if (token.getImagem().equals("{")) {
                                no.addFilho(new No(token));
                                leToken();
                                no.addFilho(ListComan());
                                if (token.getImagem().equals("}")) {
                                    no.addFilho(new No(token));
                                    leToken();
                                } else {
                                    geraErro("}");
                                }
                            } else {
                                geraErro("{");
                            }
                        } else {
                            geraErro("doTipo");
                        }
                    } else {
                        geraErro(")");
                    }

                } else {
                    geraErro("(");
                }
            } else {
                geraErro("soma");
            }
        } else {
            geraErro("tarefa");
        }
        return no;
    }

    //<ListComan> ::=  | <Coman><ListComan>
    private No ListComan() {
        No no = new No(TipoNo.NO_LISTCOMAN);
        if (token.getClasse().equals("ID")
                || token.getImagem().equals("caso")
                || token.getImagem().equals("sempreQ")
                || token.getImagem().equals("retruca")
                || token.getImagem().equals("escuta")
                || token.getImagem().equals("exibir")
                || token.getImagem().equals("{")) {
            no.addFilho(Coman());
            no.addFilho(ListComan());
        }
        return no;
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
			| ‘{’ <ListComan> ‘}’
     */
    private No Coman() {
        No no = new No(TipoNo.NO_COMAN);
        if (token.getClasse().equals("ID")) {
            Token lookAhead = lookAhead();
            if (lookAhead.getImagem().equals("=")) {
                no.addFilho(Atrib());
            } else if (lookAhead.getImagem().equals("(")) {
                no.addFilho(Chamada());
                if (token.getImagem().equals(";")) {
                    no.addFilho(new No(token));
                } else {
                    geraErro(";");
                }
            } else {
                no.addFilho(Decl());
            }
        } else if (token.getImagem().equals("caso")) {
            no.addFilho(Se());
        } else if (token.getImagem().equals("sempreQ")) {
            no.addFilho(Laco());
        } else if (token.getImagem().equals("retruca")) {
            no.addFilho(Ret());
        } else if (token.getImagem().equals("escuta")) {
            no.addFilho(Entrada());
        } else if (token.getImagem().equals("exibir")) {
            no.addFilho(Saida());
        } else if (token.getImagem().equals("{")) {
            no.addFilho(new No(token));
            leToken();
            no.addFilho(ListComan());
            if (token.getImagem().equals("}")) {
                no.addFilho(new No(token));
                leToken();
            } else {
                geraErro("}");
            }
        } else {
            geraErro("identificador | retruca | caso | sempreQ | escuta | exibir | memoQV | (");
        }
        return no;
    }

    //<Saida> ::= ‘exibir’ ‘(‘ <Operando> ‘)’ ‘;’
    private No Saida() {
        No no = new No(TipoNo.NO_SAIDA);
        if (token.getImagem().equals("exibir")) {
            no.addFilho(new No(token));
            leToken();
            if (token.getImagem().equals("(")) {
                no.addFilho(new No(token));
                leToken();
                no.addFilho(Operando());
                if (token.getImagem().equals(")")) {
                    no.addFilho(new No(token));
                    leToken();
                    if (token.getImagem().equals(";")) {
                        no.addFilho(new No(token));
                        leToken();
                    } else {
                        geraErro(";");
                    }
                } else {
                    geraErro(")");
                }
            } else {
                geraErro("(");
            }
        } else {
            geraErro("exibir");
        }
        return no;
    }

    /*
    <Operando> ::= id
			| cli
			| clr
			| clt
     */
    private No Operando() {
        No no = new No(TipoNo.NO_OPERANDO);
        if (token.getClasse().equals("ID")
                || token.getClasse().equals("CLI")
                || token.getClasse().equals("CLR")
                || token.getClasse().equals("CLT")) {
            no.addFilho(new No(token));
            leToken();
        }
        return no;
    }

    //<Entrada> ::= ‘escuta’ ‘(‘ id ‘)’ ‘;’
    private No Entrada() {
        No no = new No(TipoNo.NO_ENTRADA);
        if (token.getImagem().equals("escuta")) {
            no.addFilho(new No(token));
            leToken();
            if (token.getImagem().equals("(")) {
                no.addFilho(new No(token));
                leToken();
                if (token.getClasse().equals("CLT")) {
                    no.addFilho(new No(token));
                    leToken();
                    if (token.getImagem().equals(")")) {
                        no.addFilho(new No(token));
                        leToken();
                        if (token.getImagem().equals(";")) {
                            no.addFilho(new No(token));
                            leToken();
                        } else {
                            geraErro(";");
                        }
                    } else {
                        geraErro(")");
                    }
                } else {
                    geraErro("identificador");
                }
            } else {
                geraErro("(");
            }
        } else {
            geraErro("escuta");
        }
        return no;
    }

    //<Ret> ::= ‘retruca’ <Fator> ‘;’
    private No Ret() {
        No no = new No(TipoNo.NO_RET);
        if (token.getImagem().equals("retruca")) {
            no.addFilho(new No(token));
            leToken();
            no.addFilho(Fator());
            if (token.getImagem().equals(";")) {
                no.addFilho(new No(token));
                leToken();
            } else {
                geraErro(";");
            }
        } else {
            geraErro("retruca");
        }

        return no;
    }

    //<Fator> ::= <Operando> | <Chamada> | ‘(‘ <ExpArit> ‘)’
    private No Fator() {
        No no = new No(TipoNo.NO_FATOR);
        if (token.getClasse().equals("ID")) {
            Token lookAhead = lookAhead();
            if (lookAhead.getImagem().equals("(")) {
                no.addFilho(Chamada());
            } else {
                no.addFilho(Operando());
            }
        } else if (token.getClasse().equals("CLI")
                || token.getClasse().equals("CLR")
                || token.getClasse().equals("CLT")) {
            no.addFilho(Operando());
        } else if (token.getImagem().equals("(")) {
            no.addFilho(new No(token));
            leToken();
            no.addFilho(ExpArit());
            if (token.getImagem().equals(")")) {
                no.addFilho(new No(token));
                leToken();
            } else {
                geraErro(")");
            }
        } else {
            geraErro("(");
        }

        return no;
    }

    //<ExpArit> ::= <Termo><ExpArit2>
    private No ExpArit() {
        No no = new No(TipoNo.NO_EXPARIT);
        no.addFilho(Termo());
        no.addFilho(ExpArit2());
        return no;
    }

    //<ExpArit2> ::=  | <Op1> <ExpArit>
    private No ExpArit2() {
        No no = new No(TipoNo.NO_EXPARIT2);
        if (token.getImagem().equals("+") || token.getImagem().equals("-")) {
            no.addFilho(Op1());
            no.addFilho(ExpArit());
        }

        return no;
    }

    //<Op1> ::= ‘+’
    //		| ‘-‘
    private No Op1() {
        No no = new No(TipoNo.NO_OP1);
        if (token.getImagem().equals("+") || token.getImagem().equals("-")) {
            no.addFilho(new No(token));
            leToken();
        } else {
            geraErro("+ ou -");
        }
        return no;
    }

    //<Termo> ::= <Fator><Termo2>
    private No Termo() {
        No no = new No(TipoNo.NO_TERMO);
        no.addFilho(Fator());
        no.addFilho(Termo2());
        return no;
    }

    //<Termo2> ::= | <Op2> <Termo>
    private No Termo2() {
        No no = new No(TipoNo.NO_TERMO2);
        if (token.getImagem().equals("*") || token.getImagem().equals("/")) {
            no.addFilho(Op2());
            no.addFilho(Termo());
        }

        return no;
    }

    /*
    <Op2> ::= ‘*’
		| ‘/‘
     */
    private No Op2() {
        No no = new No(TipoNo.NO_OP2);
        if (token.getImagem().equals("*") || token.getImagem().equals("/")) {
            no.addFilho(new No(token));
            leToken();
        } else {
            geraErro("* ou /");
        }
        return no;
    }

    //<Laco> ::= ‘sempreQ’  ‘(‘  <ExpRel> ’)’ <Coman>
    private No Laco() {
        No no = new No(TipoNo.NO_LACO);
        if (token.getImagem().equals("sempreQ")) {
            no.addFilho(new No(token));
            leToken();
            if (token.getImagem().equals("(")) {
                no.addFilho(new No(token));
                leToken();
                no.addFilho(ExpRel());
                if (token.getImagem().equals(")")) {
                    no.addFilho(new No(token));
                    leToken();
                    no.addFilho(Coman());
                } else {
                    geraErro(")");
                }
            } else {
                geraErro("(");
            }
        } else {
            geraErro("sempreQ");
        }

        return no;
    }

    //<ExpRel> ::= <ExpArit> <Op3> <ExprArit>
    private No ExpRel() {
        No no = new No(TipoNo.NO_EXPREL);
        no.addFilho(ExpArit());
        no.addFilho(Op3());
        no.addFilho(ExpArit());
        return no;
    }

    /*
    <Op3> ::= ‘>’
		| ‘>=‘
		| ‘<‘
		| ‘<=‘
		| ‘==‘
		| ‘!=‘
     */
    private No Op3() {
        No no = new No(TipoNo.NO_OP3);
        if (token.getImagem().equals(">")
                || token.getImagem().equals(">=")
                || token.getImagem().equals("<")
                || token.getImagem().equals("<=")
                || token.getImagem().equals("==")
                || token.getImagem().equals("!=")) {
            no.addFilho(new No(token));
            leToken();
        }else{
            geraErro("comparador lógico");
        }
        return no;
    }

    //<Se> ::= ‘caso’ ‘(‘ <ExpRel> ‘)’ <Coman> <Senao>
    private No Se() {
        No no = new No(TipoNo.NO_SE);
        if (token.getImagem().equals("caso")) {
            no.addFilho(new No(token));
            leToken();
            if (token.getImagem().equals("(")) {
                no.addFilho(new No(token));
                leToken();
                no.addFilho(ExpRel());
                if (token.getImagem().equals(")")) {
                    no.addFilho(new No(token));
                    leToken();
                    no.addFilho(Coman());
                    no.addFilho(Senao());
                } else {
                    geraErro(")");
                }
            } else {
                geraErro("(");
            }
        } else {
            geraErro("caso");
        }

        return no;
    }

    //<Senao> ::= ‘senao’ <Coman> |
    private No Senao() {
        No no = new No(TipoNo.NO_SENAO);
        if (token.getImagem().equals("senao")) {
            no.addFilho(new No(token));
            leToken();
            no.addFilho(Coman());
        }
        return no;
    }

    //<Decl> ::= <ListId> ‘:’ <Tipo> ‘;’
    private No Decl() {
        No no = new No(TipoNo.NO_DECL);
        no.addFilho(ListId());
        raiz = no;
        if (token.getImagem().equals("doTipo")) {
            no.addFilho(new No(token));
            leToken();
            no.addFilho(Tipo());
            if (token.getImagem().equals(";")) {
                no.addFilho(new No(token));
                leToken();
            } else
                geraErro(";");
        } else {
            geraErro("doTipo");
        }
        return no;
    }

    //<ListId> ::= id <ListId2>
    private No ListId() {
        No no = new No(TipoNo.NO_LISTID);
        no.addFilho(new No(token));
        leToken();
        no.addFilho(ListId2());
        return no;
    }

    //<ListId2> ::= ‘,’ id <ListId2> |
    private No ListId2() {
        No no = new No(TipoNo.NO_LISTID2);
        if (token.getImagem().equals(",")) {
            no.addFilho(new No(token));
            leToken();
            if (token.getClasse().equals("ID")) {
                no.addFilho(new No(token));
                leToken();
                no.addFilho(ListId2());
            } else {
                geraErro("identificador");
            }
        }
        return no;
    }

    //<Chamada> ::= id ‘(‘ <ListArg> ‘)’';'
    private No Chamada() {
        No no = new No(TipoNo.NO_CHAMADA);
        no.addFilho(new No(token));
        leToken();
        if (token.getImagem().equals("(")) {
            no.addFilho(new No(token));
            leToken();
            no.addFilho(ListArg());
            if (token.getImagem().equals(")")) {
                no.addFilho(new No(token));
                leToken();
            } else {
                geraErro(")");
            }

        } else {
            geraErro("(");
        }

        return no;
    }

    //<ListArg> ::=  | <Operando> <ListArg2>
    private No ListArg() {
        No no = new No(TipoNo.NO_LISTARG);
        if (token.getClasse().equals("ID")) {
            no.addFilho(Operando());
            no.addFilho(ListArg2());
        }
        return no;
    }

    //<ListArg2> ::=  | ‘,’ <Operando><ListArg2>
    private No ListArg2() {
        No no = new No(TipoNo.NO_LISTARG2);
        if (token.getImagem().equals(",")) {
            no.addFilho(new No(token));
            leToken();
            no.addFilho(Operando());
            no.addFilho(ListArg2());
        }
        return no;
    }

    //<Atrib> ::= id ‘=‘ <ExpArit> ‘;’
    private No Atrib() {
        No no = new No(TipoNo.NO_ATRIB);
        no.addFilho(new No(token));
        leToken();
        if (token.getImagem().equals("=")) {
            no.addFilho(new No(token));
            leToken();
            no.addFilho(ExpArit());
            if (token.getImagem().equals(";")) {
                no.addFilho(new No(token));
                leToken();
            } else {
                geraErro(";");
            }

        } else {
            geraErro("memoQV");
        }

        return no;
    }

    //<ListParam> ::= <Param><ListParam2> |
    private No ListParam() {
        No no = new No(TipoNo.NO_LISTPARAM);
        if (token.getImagem().equals("doTipo")) {
            no.addFilho(Param());
            no.addFilho(ListParam2());
        }

        return no;
    }

    //<ListParam2> ::=  ‘,’ <Param><ListParam2> |
    private No ListParam2() {
        No no = new No(TipoNo.NO_LISTPARAM2);
        if (token.getImagem().equals(",")) {
            no.addFilho(new No(token));
            leToken();
            no.addFilho(Param());
            no.addFilho(ListParam2());
        }

        return no;
    }
//<Param> ::='doTipo'<Tipo> ID
    //<Param> ::= id ‘:’ <Tipo>
    private No Param() {
        No no = new No(TipoNo.NO_PARAM);
            if (token.getImagem().equals("doTipo")) {
                no.addFilho(new No(token));
                leToken();
                no.addFilho(Tipo());
            } else {
                geraErro("doTipo");
            }if (token.getClasse().equals("ID")) {
            no.addFilho(new No(token));
            leToken();
        } else {
            geraErro("identificador");
        }
        return no;
    }

    /*
    <Tipo> ::= ‘inteiro’
		| ‘real’
		| ‘texto’
		| ‘nada’
		|
     */
    private No Tipo() {
        No no = new No(TipoNo.NO_TIPO);
        if (token.getClasse().equals("PR")) {
            if (token.getImagem().equals("inteiro") ||
                    token.getImagem().equals("real") ||
                    token.getImagem().equals("texto") ||
                    token.getImagem().equals("nada")) {
                no.addFilho(new No(token));
                leToken();
            } else {
                geraErro("as palavras reservadas inteiro, real, texto ou nada");
            }
        } else {
            geraErro("palavra reservada");
        }

        return no;
    }
    
    public static ArrayList<Token> getTokens(){
        return tokens;
    }
    
    public static ArrayList<String> getErros(){
        return erros;
    }
    
    public static void printTokens(){
        System.out.println("Impressão Tokens");
        for (Token token:
             tokens) {
            System.out.println(token);
        }
    }
    
    public static void printErroTokens(){
        System.out.println("Impressão Erros");
        for (String erro :
                erros) {
            System.out.println(erro);
        }
    }

    public static void mostraArvore() {
        mostraNo(raiz, "  ");
    }

    private static void mostraNo(No no, String esp) {
        System.out.println(esp + no);
        for(No noFilho : no.getFilhos()){
            mostraNo(noFilho, esp + "  ");
        }
    }

}

