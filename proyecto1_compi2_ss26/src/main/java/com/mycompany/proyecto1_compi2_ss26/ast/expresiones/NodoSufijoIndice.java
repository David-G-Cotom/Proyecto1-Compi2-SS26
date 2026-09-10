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
public class NodoSufijoIndice extends NodoAST {

    private final NodoAST indice;

    public NodoSufijoIndice(NodoAST indice, int line) {
        super(line);
        this.indice = indice;
    }

    public NodoAST getIndice() {
        return indice;
    }

}
