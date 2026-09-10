/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.ast.expresiones;

import com.mycompany.proyecto1_compi2_ss26.ast.NodoAST;
import java.util.List;

/**
 *
 * @author david
 */
public class NodoLiteralCompuesto extends NodoAST {

    private final List<NodoAST> valores;

    public NodoLiteralCompuesto(List<NodoAST> valores, int line) {
        super(line);
        this.valores = valores;
    }

    public List<NodoAST> getValores() {
        return valores;
    }

}
