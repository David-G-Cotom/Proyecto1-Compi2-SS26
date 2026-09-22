/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.ast.z.sentencias;

import com.mycompany.proyecto1_compi2_ss26.ast.NodoAST;

/**
 *
 * @author david
 */
public class NodoImprimirZ extends NodoAST {

    private final boolean saltoLinea;
    private final NodoAST expresion;

    public NodoImprimirZ(int linea, boolean saltoLinea, NodoAST expresion) {
        super(linea);
        this.saltoLinea = saltoLinea;
        this.expresion = expresion;
    }

    public boolean isSaltoLinea() {
        return saltoLinea;
    }

    public NodoAST getExpresion() {
        return expresion;
    }

}
