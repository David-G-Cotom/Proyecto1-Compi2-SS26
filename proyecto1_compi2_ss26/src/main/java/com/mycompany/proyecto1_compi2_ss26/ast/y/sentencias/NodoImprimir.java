/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.ast.y.sentencias;

import com.mycompany.proyecto1_compi2_ss26.ast.NodoAST;

/**
 *
 * @author david
 */
public class NodoImprimir extends NodoAST {

    private final NodoAST expresion;

    public NodoImprimir(NodoAST expresion, int line) {
        super(line);
        this.expresion = expresion;
    }

    public NodoAST getExpresion() {
        return expresion;
    }

}
