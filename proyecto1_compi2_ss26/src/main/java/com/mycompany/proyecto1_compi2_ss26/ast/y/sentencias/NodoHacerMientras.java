/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.ast.y.sentencias;

import com.mycompany.proyecto1_compi2_ss26.ast.NodoAST;
import java.util.List;

/**
 *
 * @author david
 */
public class NodoHacerMientras extends NodoAST {

    private final List<NodoAST> cuerpo;
    private final NodoAST condicion;

    public NodoHacerMientras(List<NodoAST> cuerpo, NodoAST condicion, int line) {
        super(line);
        this.cuerpo = cuerpo;
        this.condicion = condicion;
    }

    public List<NodoAST> getCuerpo() {
        return cuerpo;
    }

    public NodoAST getCondicion() {
        return condicion;
    }

}
