/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.ast.sentencias;

import com.mycompany.proyecto1_compi2_ss26.ast.NodoAST;
import java.util.List;

/**
 *
 * @author david
 */
public class NodoSwitch extends NodoAST {

    private final NodoAST valorEvaluado;
    private final List<NodoCaso> casos;
    private final NodoDefaultSwitch porDefecto;

    public NodoSwitch(int linea, NodoAST valorEvaluado, List<NodoCaso> casos, NodoDefaultSwitch porDefecto) {
        super(linea);
        this.valorEvaluado = valorEvaluado;
        this.casos = casos;
        this.porDefecto = porDefecto;
    }

    public NodoAST getValorEvaluado() {
        return valorEvaluado;
    }

    public List<NodoCaso> getCasos() {
        return casos;
    }

    public NodoDefaultSwitch getPorDefecto() {
        return porDefecto;
    }

}
