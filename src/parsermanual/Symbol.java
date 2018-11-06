/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parsermanual;

import java.util.ArrayList;

/**
 *
 * @author Rafael Pérez
 */
public class Symbol {
    enum SymbolType {
        TERMINAL, NON_TERMINAL
    }
    
    public ArrayList<Symbol> getProd() {
        return new ArrayList<Symbol>();
    }
}
