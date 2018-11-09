package parsermanual;

import parsermanual.tokenizador.*;

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
    public static Token SYNCH = Token.newToken(-2, "SYNCH");

    //Simbolo vacio
    public static Token EPS = Token.newToken(-1, "EPS");

    public static HashMap<Integer, Token> terms = new HashMap<>();

    //Stacks para el algoritmo de parsing descente predictivo
    public static Stack<Token> source_s = new Stack<>();
    public static Stack<Token> parser_s = new Stack<>();

    public static void main(String[] args) {
        prepararTerminales();
        prepararNoTerminales();

        tokenizador = new TokenizadorPreprocesador(new StringReader("#define pi def*3.141516/2*2 + 4+512*func(a,b)"));

        Token t = tokenizador.getNextToken();

        while (t.kind != 0) {
            source_s.push(t);
            t = tokenizador.getNextToken();
        }

        source_s.push(getToken(EOF));

        //Los no terminales tienen que invertirse en orden LIFO
        Collections.reverse(source_s);

        //Primeros elementos del Stack
        parser_s.push(getToken(EOF));
        parser_s.push(S);

        Token last_in, last_stack, prev_in;

        boolean errores = false;

        while (!source_s.isEmpty()) {
            last_in = source_s.peek();
            last_stack = parser_s.peek();

            //Arreglo de producciones correspondientes a el no terminal de la
            //cadena de entrada que se está parseando.
            ArrayList<Token> prod;

            if (table.isNonTerminal(last_stack)) {

                //Obtener producción asignada al terminal
                prod = table.getProd(last_stack, last_in);

                //De no existir producción, se entra en modo pánico y se elimina el Token
                if (prod == null) {
                    mostrarError(last_in);
                    errores = true;
                    source_s.pop();
                } else {

                    parser_s.pop();

                    for (Token temp : prod) {
                        parser_s.push(temp);
                    }

                    //Comparar los 2 terminales
                    if (parser_s.peek().kind == EPS.kind) {
                        parser_s.pop();
                    }

                    if (parser_s.peek() == SYNCH) {
                        errores = true;
                        parser_s.pop();
                    }
                }

            } else {
                //Correción de errores
                if (last_in.kind == last_stack.kind) {
                    source_s.pop();
                    parser_s.pop();
                } else {
                    mostrarError(last_in);
                    System.err.println("Ocurrió un error interno.");
                    return;
                }
            }

            //Token de entrada previo
            prev_in = last_in;
        }

        System.out.println("Parsing terminado " + (errores ? "con"  : "sin") + " errores.");
    }

    public static void mostrarError(Token e) {
        System.err.println("No se esperaba símbolo: '" + e.image + "' en la línea " + e.beginLine + ", columna " + e.beginColumn);
    }

    public static Token getToken(int id) {
        return terms.get(id);
    }

    public static void prepararTerminales() {
        for (int i = 0; i < TokenizadorPreprocesadorConstants.tokenImage.length; i++) {
            terms.put(i, Token.newToken(i, imagenes[i]));
        }
    }

    public static void prepararNoTerminales() {

        //Para S
        table.addProd(S, getToken(DIRECTIVA), new Token[]{getToken(DIRECTIVA), M});

        //SYNCH S
        table.addProd(S, getToken(EOF), new Token[]{SYNCH});

        //Para M
        table.addProd(M, getToken(INCLUDE), new Token[]{getToken(INCLUDE), P});
        table.addProd(M, getToken(DEFINE), new Token[]{getToken(DEFINE), getToken(IDENTIFICADOR), A, E});

        //SYNCH M
        table.addProd(M, getToken(EOF), new Token[]{SYNCH});

        //Para P
        table.addProd(P, getToken(PATH_IZQ), new Token[]{getToken(PATH_IZQ), getToken(PATH), getToken(PATH_DER)});
        table.addProd(P, getToken(COMILLA), new Token[]{getToken(COMILLA), getToken(PATH), getToken(COMILLA)});

        //SYNCH P
        table.addProd(P, getToken(PATH_DER), new Token[]{SYNCH});
        table.addProd(P, getToken(COMILLA), new Token[]{SYNCH});

        //Para E
        table.addProd(E, getToken(IDENTIFICADOR), new Token[]{T, EP});
        table.addProd(E, getToken(NUMERICO), new Token[]{T, EP});
        table.addProd(E, getToken(PAREN_IZQ), new Token[]{T, EP});

        //SYNCH E
        table.addProd(E, getToken(EOF), new Token[]{SYNCH});
        table.addProd(E, getToken(PAREN_DER), new Token[]{SYNCH});

        //Para EP
        table.addProd(EP, getToken(PLUS), new Token[]{getToken(PLUS), T, EP});
        table.addProd(EP, getToken(MINUS), new Token[]{getToken(MINUS), T, EP});
        table.addProd(EP, getToken(PAREN_DER), new Token[]{EPS});
        table.addProd(EP, getToken(EOF), new Token[]{EPS});

        //Para T
        table.addProd(T, getToken(IDENTIFICADOR), new Token[]{F, TP});
        table.addProd(T, getToken(NUMERICO), new Token[]{F, TP});
        table.addProd(T, getToken(PAREN_IZQ), new Token[]{F, TP});

        //SYNCH T
        table.addProd(T, getToken(PLUS), new Token[]{SYNCH});
        table.addProd(T, getToken(MINUS), new Token[]{SYNCH});
        table.addProd(T, getToken(PAREN_DER), new Token[]{SYNCH});
        table.addProd(T, getToken(EOF), new Token[]{SYNCH});

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

        //SYNCH F
        table.addProd(F, getToken(PLUS), new Token[]{SYNCH});
        table.addProd(F, getToken(MINUS), new Token[]{SYNCH});
        table.addProd(F, getToken(PAREN_DER), new Token[]{SYNCH});
        table.addProd(F, getToken(EOF), new Token[]{SYNCH});

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

        //SYNCH B
        table.addProd(B, getToken(PAREN_DER), new Token[]{SYNCH});

        //Para C
        table.addProd(C, getToken(COMA), new Token[]{getToken(COMA), B});
        table.addProd(C, getToken(PAREN_DER), new Token[]{EPS});
    }

}