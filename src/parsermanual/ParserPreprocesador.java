package parsermanual;

import parsermanual.tokenizador.*;

import javax.print.DocFlavor;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Stack;

import static parsermanual.tokenizador.TokenizadorPreprocesadorConstants.*;


public class ParserPreprocesador {
    private static TokenizadorPreprocesador tokenizador;
    public static String imagenes[] = TokenizadorPreprocesadorConstants.tokenImage;
    public static ParseTable table = new ParseTable();

    public static Token S = Token.newToken(100, "S");
    public static Token M = Token.newToken(101, "M");
    public static Token P = Token.newToken(102, "P");
    public static Token E = Token.newToken(103, "E");
    public static Token EP = Token.newToken(104, "EP");
    public static Token T = Token.newToken(105, "T");
    public static Token TP = Token.newToken(106, "TP");
    public static Token F = Token.newToken(107, "F");
    public static Token A = Token.newToken(108, "A");
    public static Token B = Token.newToken(109, "B");
    public static Token C = Token.newToken(110, "C");

    //Simbolo vacio
    public static Token EPS = Token.newToken(-1, "EPS");

    public static HashMap<Integer, Token> nonTerms = new HashMap<>();

    //Stacks para el algoritmo de parsing descente predictivo
    public static Stack<Token> source_s = new Stack<>();
    public static Stack<Token> parser_s = new Stack<>();

    public static void main(String[] args) {
        prepararNoTerminales();
        tokenizador = new TokenizadorPreprocesador(new StringReader("#define pi. 3.141516"));

        //tokenizador = new TokenizadorPreprocesador(new StringReader("a + 3.141516*2*funca()"));

        Token t = tokenizador.getNextToken();

        //Para S
        table.addProd(S, getToken(DIRECTIVA), new Token[]{getToken(DIRECTIVA), M});

        //Para M
        table.addProd(M, getToken(INCLUDE), new Token[]{getToken(INCLUDE), P});
        table.addProd(M, getToken(DEFINE), new Token[]{getToken(DEFINE), getToken(IDENTIFICADOR), A, E});

        //Para P
        table.addProd(P, getToken(PATH_IZQ), new Token[]{getToken(PATH_IZQ), getToken(PATH), getToken(PATH_DER)});
        table.addProd(P, getToken(COMILLA), new Token[]{getToken(COMILLA), getToken(PATH), getToken(COMILLA)});


        //Para E
        table.addProd(E, getToken(IDENTIFICADOR), new Token[]{T, EP});
        table.addProd(E, getToken(NUMERICO), new Token[]{T, EP});
        table.addProd(E, getToken(PAREN_IZQ), new Token[]{T, EP});

        //Para EP
        table.addProd(EP, getToken(PLUS), new Token[]{getToken(PLUS), T, EP});
        table.addProd(EP, getToken(MINUS), new Token[]{getToken(MINUS), T, EP});
        table.addProd(EP, getToken(PAREN_DER), new Token[]{EPS});
        table.addProd(EP, getToken(EOF), new Token[]{EPS});

        //Para T
        table.addProd(T, getToken(IDENTIFICADOR), new Token[]{F, TP});
        table.addProd(T, getToken(NUMERICO), new Token[]{F, TP});
        table.addProd(T, getToken(PAREN_IZQ), new Token[]{F, TP});

        //Para TP
        table.addProd(TP, getToken(PLUS), new Token[]{EPS});
        table.addProd(TP, getToken(MINUS), new Token[]{EPS});
        table.addProd(TP, getToken(PAREN_DER), new Token[]{EPS});
        table.addProd(TP, getToken(EOF), new Token[]{EPS});
        table.addProd(TP, getToken(MULTIPLY), new Token[]{getToken(MULTIPLY), F, TP});
        table.addProd(TP, getToken(DIVIDE), new Token[]{getToken(DIVIDE), F, TP});

        //Para F
        table.addProd(F, getToken(IDENTIFICADOR), new Token[]{getToken(IDENTIFICADOR), A});
        table.addProd(F, getToken(NUMERICO), new Token[]{getToken(NUMERICO)});
        table.addProd(F, getToken(PAREN_IZQ), new Token[]{getToken(PAREN_IZQ), E, getToken(PAREN_DER)});

        //Para A
        table.addProd(A, getToken(PAREN_IZQ), new Token[]{getToken(PAREN_IZQ), B, getToken(PAREN_DER)});
        table.addProd(A, getToken(PLUS), new Token[]{EPS});
        table.addProd(A, getToken(DIVIDE), new Token[]{EPS});
        table.addProd(A, getToken(MULTIPLY), new Token[]{EPS});
        table.addProd(A, getToken(MINUS), new Token[]{EPS});
        table.addProd(A, getToken(PAREN_DER), new Token[]{EPS});
        table.addProd(A, getToken(IDENTIFICADOR), new Token[]{EPS});
        table.addProd(A, getToken(NUMERICO), new Token[]{EPS});
        table.addProd(A, getToken(EOF), new Token[]{EPS});

        //Para B
        table.addProd(B, getToken(IDENTIFICADOR), new Token[]{getToken(IDENTIFICADOR), C});

        //Para C
        table.addProd(C, getToken(COMA), new Token[]{getToken(COMA), B});
        table.addProd(C, getToken(PAREN_DER), new Token[]{EPS});

        while (t.kind != 0) {
            if (t != EPS) {
                source_s.push(t);
            }
            try {
                t = tokenizador.getNextToken();
            } catch (TokenMgrError e) {
                //System.out.println("Símbolo no reconocido encontrado: " + String.valueOf(e.));
            }
        }

        source_s.push(getToken(EOF));

        Collections.reverse(source_s);

        parser_s.push(getToken(EOF));
        parser_s.push(S);

        printStacks();
        while (!source_s.isEmpty()) {
            Token last_in = source_s.peek();
            Token last_stack = parser_s.peek();

            //printStacks();
            ArrayList<Token> prod;
            if (table.isTerminal(last_stack)) {
                prod = table.getProd(last_stack, last_in);

                if (prod == null) {
                    System.out.println("No se esperaba símbolo: " + last_in.image);
                    System.out.println("S1: La cadena no pertenece");
                    return;
                }

                parser_s.pop();

                for (Token temp : prod) {
                    parser_s.push(temp);
                }

                if (parser_s.peek().kind == EPS.kind) {
                    parser_s.pop();
                }

            } else {
                if (last_in.kind == last_stack.kind) {
                    source_s.pop();
                    parser_s.pop();
                } else {
                    //Agregando símbolo faltante
                    //source_s.pop();
                    //source_s.push(last_stack);

                    System.out.println("Se esperaba símbolo: " + last_stack.image);
                    System.out.println("S2:La cadena no pertenece");
                    return;
                }
            }
            printStacks();
        }

        System.out.println("La cadena pertenece al lenguaje!");
    }

    public static Token getToken(int id) {
        return nonTerms.get(id);
    }

    public static void prepararNoTerminales() {
        for (int i = 0; i < TokenizadorPreprocesadorConstants.tokenImage.length; i++) {
            nonTerms.put(i, Token.newToken(i, imagenes[i]));
        }
    }

    public static void printStacks() {
        System.out.println("STACK PARSER:");
        for (Token t : parser_s) {
            System.out.println(t.toString());
        }

        System.out.println("STACK SOURCE:");
        for (Token t : source_s) {
            System.out.println(t.toString());
        }
        System.out.println("------------------------");
    }
}