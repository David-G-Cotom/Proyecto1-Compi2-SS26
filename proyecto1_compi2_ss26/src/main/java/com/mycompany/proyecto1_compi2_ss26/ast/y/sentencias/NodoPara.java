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
public class NodoPara extends NodoAST {

    private final NodoAST init;
    private final NodoAST condicion;
    private final NodoAST actualizacion;
    private final List<NodoAST> cuerpo;

    public NodoPara(NodoAST init, NodoAST condicion, NodoAST actualizacion, List<NodoAST> cuerpo, int line) {
        super(line);
        this.init = init;
        this.condicion = condicion;
        this.actualizacion = actualizacion;
        this.cuerpo = cuerpo;
    }

    public NodoAST getInit() {
        return init;
    }

    public NodoAST getCondicion() {
        return condicion;
    }

    public NodoAST getActualizacion() {
        return actualizacion;
    }

    public List<NodoAST> getCuerpo() {
        return cuerpo;
    }

}
