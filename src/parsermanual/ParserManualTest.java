///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package parsermanual;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Stack;
//import javafx.util.Pair;
///**
// *
// * @author Rafael Pérez
// */
//public class ParserManualTest {
//
//    enum TOKEN {
//        CHAR_A,
//        PLUS,
//        ASTERISK,
//        LEFT_P,
//        RIGHT_P,
//        EOF,
//        EPS,
//        //No terminales
//        E,  //-> TA
//        A,  //-> +TA | EPS
//        T,  //-> FB
//        B,  //->ASKTERISK F B|E
//        F   //LEFT_P E RIGHT_P | CHAR_A
//    }
//
//    static Stack<TOKEN> source_s;
//    static Stack<TOKEN> parser_s;
//    static ParseTable table = new ParseTable();
//
//    public static void printStacks() {
//        System.out.println("STACK PARSER:");
//        for(TOKEN t: parser_s) {
//            System.out.println(t.toString());
//        }
//
//        System.out.println("STACK SOURCE:");
//        for(TOKEN t: source_s){
//            System.out.println(t.toString());
//        }
//        System.out.println("------------------------");
//    }
//
//    public static void _main_(String[] args) {
//        source_s = new Stack<>();
//        parser_s = new Stack<>();
//
//        //Para E
//        table.addProd(TOKEN.E, TOKEN.CHAR_A, new TOKEN[]{TOKEN.T, TOKEN.A});
//        table.addProd(TOKEN.E, TOKEN.LEFT_P, new TOKEN[]{TOKEN.T, TOKEN.A});
//
//        //Para T
//        table.addProd(TOKEN.T, TOKEN.CHAR_A, new TOKEN[]{TOKEN.F, TOKEN.B});
//        table.addProd(TOKEN.T, TOKEN.LEFT_P, new TOKEN[]{TOKEN.F, TOKEN.B});
//
//        //Para F
//        table.addProd(TOKEN.F, TOKEN.CHAR_A, new TOKEN[]{TOKEN.CHAR_A});
//        table.addProd(TOKEN.F, TOKEN.LEFT_P, new TOKEN[]{TOKEN.LEFT_P, TOKEN.E, TOKEN.RIGHT_P});
//
//        //Para A
//        table.addProd(TOKEN.A, TOKEN.PLUS, new TOKEN[]{TOKEN.PLUS, TOKEN.T, TOKEN.A});
//        table.addProd(TOKEN.A, TOKEN.RIGHT_P, new TOKEN[]{TOKEN.EPS});
//        table.addProd(TOKEN.A, TOKEN.EOF, new TOKEN[]{TOKEN.EPS});
//
//        //Para B
//        table.addProd(TOKEN.B, TOKEN.PLUS, new TOKEN[]{TOKEN.EPS});
//        table.addProd(TOKEN.B, TOKEN.ASTERISK, new TOKEN[]{TOKEN.ASTERISK, TOKEN.F, TOKEN.B});
//        table.addProd(TOKEN.B, TOKEN.RIGHT_P, new TOKEN[]{TOKEN.EPS});
//        table.addProd(TOKEN.B, TOKEN.EOF, new TOKEN[]{TOKEN.EPS});
//
//        ArrayList<TOKEN> prod;
//
//        TOKEN cadena_in[] = {TOKEN.CHAR_A, TOKEN.PLUS, TOKEN.CHAR_A, TOKEN.ASTERISK, TOKEN.CHAR_A};
//
//        source_s.push(TOKEN.EOF);
//        parser_s.push(TOKEN.EOF);
//        parser_s.push(TOKEN.E);
//        for(int i = cadena_in.length - 1;  i >= 0; i--) {
//            source_s.push(cadena_in[i]);
//        }
//
//        while(!source_s.isEmpty()) {
//            TOKEN last_in = source_s.peek();
//            TOKEN last_stack = parser_s.peek();
//
//            printStacks();
//            if(table.isTerminal(last_stack)) {
//                prod = table.getProd(last_stack, last_in);
//
//                if(prod == null) {
//                    System.out.println("La cadena no pertenece");
//                    return;
//                }
//
//                parser_s.pop();
//
//                for(TOKEN t : prod) {
//                    parser_s.push(t);
//                }
//
//                if(parser_s.peek() == TOKEN.EPS) {
//                    parser_s.pop();
//                }
//
//            } else {
//                if(last_in == last_stack) {
//                    source_s.pop();
//                    parser_s.pop();
//                } else {
//                    System.out.println("La cadena no pertenece");
//                    return;
//                }
//            }
//        }
//
//        System.out.println("La cadena si pertenece a la gramatica");
//    }
//
//}
