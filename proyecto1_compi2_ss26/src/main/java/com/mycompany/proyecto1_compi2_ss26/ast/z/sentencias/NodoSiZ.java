/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.ast.z.sentencias;

import com.mycompany.proyecto1_compi2_ss26.ast.NodoAST;
import java.util.List;

/**
 *
 * @author david
 */
public class NodoSiZ extends NodoAST {

    private final List<NodoAST> condiciones;      // una por 'if'/'else if'
    private final List<List<NodoAST>> bloques;    // un bloque por cada condición, mismo orden
    private final List<NodoAST> bloqueContrario;   // null si no hay 'else' final

    public NodoSiZ(int linea, List<NodoAST> condiciones, List<List<NodoAST>> bloques, List<NodoAST> bloqueContrario) {
        super(linea);
        this.condiciones = condiciones;
        this.bloques = bloques;
        this.bloqueContrario = bloqueContrario;
    }

    public List<NodoAST> getCondiciones() {
        return condiciones;
    }

    public List<List<NodoAST>> getBloques() {
        return bloques;
    }

    public List<NodoAST> getBloqueContrario() {
        return bloqueContrario;
    }

}
