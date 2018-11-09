/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parsermanual;

import java.util.*;

import javafx.util.Pair;
import parsermanual.tokenizador.Token;

/**
 * @author Rafael Pérez
 */
public class ParseTable {
    HashMap<Token, HashMap<Token, ArrayList<Token>>> table;

    public ParseTable() {
        table = new HashMap<>();
    }

    public void addProd(Token nonterm, Token term, Token[] prods) {

        //Nueva produccion
        ArrayList<Token> resultado_prod = new ArrayList<>();
        for (Token t : prods) {
            resultado_prod.add(t);
        }
        Collections.reverse(resultado_prod);

        if (!table.containsKey(nonterm)) {
            HashMap<Token, ArrayList<Token>> prod = new HashMap<>();
            table.put(nonterm, prod);
        }

        table.get(nonterm).put(term, resultado_prod);
    }

    public ArrayList<Token> getProd(Token nonterm, Token term) {
        if(table.containsKey(nonterm)) {
            HashMap<Token, ArrayList<Token>> mapFound = table.get(nonterm);
            for(HashMap.Entry<Token, ArrayList<Token>> entry: mapFound.entrySet()) {
                if(entry.getKey().kind == term.kind) {
                    return entry.getValue();
                }
            }

        }

        return null;
    }

    public boolean isNonTerminal(Token test) {
        return table.containsKey(test);
    }
}
