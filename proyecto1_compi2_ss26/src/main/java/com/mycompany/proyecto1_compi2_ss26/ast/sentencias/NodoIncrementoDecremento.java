/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.ast.sentencias;

import com.mycompany.proyecto1_compi2_ss26.ast.NodoAST;
import com.mycompany.proyecto1_compi2_ss26.ast.expresiones.NodoAccesoVariable;

/**
 *
 * @author david
 */
public class NodoIncrementoDecremento extends NodoAST {

    private final NodoAccesoVariable destino;
    private final String operador;

    public NodoIncrementoDecremento(NodoAccesoVariable destino, String operador, int line) {
        super(line);
        this.destino = destino;
        this.operador = operador;
    }

    public NodoAccesoVariable getDestino() {
        return destino;
    }

    public String getOperador() {
        return operador;
    }

}
