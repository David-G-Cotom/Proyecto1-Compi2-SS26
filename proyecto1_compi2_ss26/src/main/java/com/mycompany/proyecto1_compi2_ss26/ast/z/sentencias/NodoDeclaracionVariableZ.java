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
public class NodoDeclaracionVariableZ extends NodoAST {

    private final String tipoBaseTexto; // incluye corchetes, ej. "int[]"
    private final List<NodoDeclaradorZ> declaradores;

    public NodoDeclaracionVariableZ(int linea, String tipoBaseTexto, List<NodoDeclaradorZ> declaradores) {
        super(linea);
        this.tipoBaseTexto = tipoBaseTexto;
        this.declaradores = declaradores;
    }

    public String getTipoBaseTexto() {
        return tipoBaseTexto;
    }

    public List<NodoDeclaradorZ> getDeclaradores() {
        return declaradores;
    }

}
