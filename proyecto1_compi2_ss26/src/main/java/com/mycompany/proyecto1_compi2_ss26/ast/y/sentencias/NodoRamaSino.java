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
public class NodoRamaSino extends NodoAST {

    private final NodoAST condicion;
    private final List<NodoAST> cuerpo;

    public NodoRamaSino(NodoAST condicion, List<NodoAST> cuerpo, int line) {
        super(line);
        this.condicion = condicion;
        this.cuerpo = cuerpo;
    }

    public NodoAST getCondicion() {
        return condicion;
    }

    public List<NodoAST> getCuerpo() {
        return cuerpo;
    }

}
