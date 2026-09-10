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
public class NodoCaso extends NodoAST {

    private final NodoAST valorLiteral;
    private final List<NodoAST> cuerpo;

    public NodoCaso(NodoAST valorLiteral, List<NodoAST> cuerpo, int line) {
        super(line);
        this.valorLiteral = valorLiteral;
        this.cuerpo = cuerpo;
    }

    public NodoAST getValorLiteral() {
        return valorLiteral;
    }

    public List<NodoAST> getCuerpo() {
        return cuerpo;
    }

}
