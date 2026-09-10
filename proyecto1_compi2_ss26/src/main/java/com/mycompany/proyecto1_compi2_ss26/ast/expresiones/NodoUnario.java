/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.ast.expresiones;

import com.mycompany.proyecto1_compi2_ss26.ast.NodoAST;

/**
 *
 * @author david
 */
public class NodoUnario extends NodoAST {

    private final String operador;
    private final NodoAST operando;

    public NodoUnario(String operador, NodoAST operando, int line) {
        super(line);
        this.operador = operador;
        this.operando = operando;
    }

    public String getOperador() {
        return operador;
    }

    public NodoAST getOperando() {
        return operando;
    }

}
