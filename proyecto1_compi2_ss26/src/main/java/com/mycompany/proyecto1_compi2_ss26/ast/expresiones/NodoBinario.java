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
public class NodoBinario extends NodoAST {

    private final String operado;
    private final NodoAST izquierda;
    private final NodoAST derecha;

    public NodoBinario(String operado, NodoAST izquierda, NodoAST derecha, int line) {
        super(line);
        this.operado = operado;
        this.izquierda = izquierda;
        this.derecha = derecha;
    }

    public String getOperado() {
        return operado;
    }

    public NodoAST getIzquierda() {
        return izquierda;
    }

    public NodoAST getDerecha() {
        return derecha;
    }

}
