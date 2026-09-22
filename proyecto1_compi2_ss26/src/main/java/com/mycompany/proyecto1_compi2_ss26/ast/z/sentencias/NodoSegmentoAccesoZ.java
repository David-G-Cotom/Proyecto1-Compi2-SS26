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
public class NodoSegmentoAccesoZ extends NodoAST {

    private final String nombre;
    private final List<NodoAST> argumentos;
    private final List<NodoAST> indices;

    public NodoSegmentoAccesoZ(int linea, String nombre, List<NodoAST> argumentos, List<NodoAST> indices) {
        super(linea);
        this.nombre = nombre;
        this.argumentos = argumentos;
        this.indices = indices;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean esLlamada() {
        return argumentos != null;
    }

    public List<NodoAST> getArgumentos() {
        return argumentos;
    }

    public List<NodoAST> getIndices() {
        return indices;
    }

}
