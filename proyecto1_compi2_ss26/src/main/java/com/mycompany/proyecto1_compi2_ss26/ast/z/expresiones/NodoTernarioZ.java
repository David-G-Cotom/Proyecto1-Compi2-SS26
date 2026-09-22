/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.ast.z.expresiones;

import com.mycompany.proyecto1_compi2_ss26.ast.NodoAST;

/**
 *
 * @author david
 */
public class NodoTernarioZ extends NodoAST {

    private final NodoAST condicion;
    private final NodoAST siVerdadero;
    private final NodoAST siFalso;

    public NodoTernarioZ(int linea, NodoAST condicion, NodoAST siVerdadero, NodoAST siFalso) {
        super(linea);
        this.condicion = condicion;
        this.siVerdadero = siVerdadero;
        this.siFalso = siFalso;
    }

    public NodoAST getCondicion() {
        return condicion;
    }

    public NodoAST getSiVerdadero() {
        return siVerdadero;
    }

    public NodoAST getSiFalso() {
        return siFalso;
    }

}
