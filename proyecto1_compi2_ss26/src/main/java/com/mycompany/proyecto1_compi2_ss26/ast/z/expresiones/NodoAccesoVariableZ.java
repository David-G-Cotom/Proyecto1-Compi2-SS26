/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.ast.z.expresiones;

import com.mycompany.proyecto1_compi2_ss26.ast.NodoAST;
import com.mycompany.proyecto1_compi2_ss26.ast.z.sentencias.NodoSegmentoAccesoZ;
import java.util.List;

/**
 *
 * @author david
 */
public class NodoAccesoVariableZ extends NodoAST {

    private final List<NodoSegmentoAccesoZ> segmentos;

    public NodoAccesoVariableZ(int linea, List<NodoSegmentoAccesoZ> segmentos) {
        super(linea);
        this.segmentos = segmentos;
    }

    public List<NodoSegmentoAccesoZ> getSegmentos() {
        return segmentos;
    }

}
