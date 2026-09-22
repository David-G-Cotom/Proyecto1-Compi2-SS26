/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.ast.z.sentencias;

import com.mycompany.proyecto1_compi2_ss26.ast.z.expresiones.NodoAccesoVariableZ;
import com.mycompany.proyecto1_compi2_ss26.ast.NodoAST;

/**
 *
 * @author david
 */
public class NodoAsignacionZ extends NodoAST {

    private final NodoAccesoVariableZ destino;
    private final String operador;
    private final NodoAST valor;

    public NodoAsignacionZ(int linea, NodoAccesoVariableZ destino, String operador, NodoAST valor) {
        super(linea);
        this.destino = destino;
        this.operador = operador;
        this.valor = valor;
    }

    public NodoAccesoVariableZ getDestino() {
        return destino;
    }

    public String getOperador() {
        return operador;
    }

    public NodoAST getValor() {
        return valor;
    }

}
