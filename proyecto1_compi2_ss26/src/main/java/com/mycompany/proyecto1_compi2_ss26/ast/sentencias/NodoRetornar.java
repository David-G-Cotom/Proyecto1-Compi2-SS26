/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.ast.sentencias;

import com.mycompany.proyecto1_compi2_ss26.ast.NodoAST;

/**
 *
 * @author david
 */
public class NodoRetornar extends NodoAST {

    private final NodoAST expresion;

    public NodoRetornar(NodoAST expresion, int line) {
        super(line);
        this.expresion = expresion;
    }

    public NodoAST getExpresion() {
        return expresion;
    }

}
