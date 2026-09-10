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
public class NodoSiempre extends NodoAST {

    private final List<NodoAST> cuerpo;

    public NodoSiempre(List<NodoAST> cuerpo, int line) {
        super(line);
        this.cuerpo = cuerpo;
    }

    public List<NodoAST> getCuerpo() {
        return cuerpo;
    }

}
