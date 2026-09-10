/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.ast.y.sentencias;

import com.mycompany.proyecto1_compi2_ss26.ast.y.sentencias.NodoSiempre;
import com.mycompany.proyecto1_compi2_ss26.ast.NodoAST;
import java.util.List;

/**
 *
 * @author david
 */
public class NodoElegir extends NodoAST {

    private final NodoAST valorEvaluado;
    private final List<NodoCaso> casos;
    private final NodoSiempre siempre;

    public NodoElegir(NodoAST valorEvaluado, List<NodoCaso> casos, NodoSiempre siempre, int line) {
        super(line);
        this.valorEvaluado = valorEvaluado;
        this.casos = casos;
        this.siempre = siempre;
    }

    public NodoAST getValorEvaluado() {
        return valorEvaluado;
    }

    public List<NodoCaso> getCasos() {
        return casos;
    }

    public NodoSiempre getSiempre() {
        return siempre;
    }

}
