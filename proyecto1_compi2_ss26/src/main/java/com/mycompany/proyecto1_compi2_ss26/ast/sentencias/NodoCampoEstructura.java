/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.ast.sentencias;

import com.mycompany.proyecto1_compi2_ss26.ast.NodoAST;

/**
 *
 * @author david
 */
public class NodoCampoEstructura extends NodoAST {

    private final String tipo;
    private final String nombre;
    private final NodoAST arregloSize;

    public NodoCampoEstructura(String tipo, String nombre, NodoAST arregloSize, int line) {
        super(line);
        this.tipo = tipo;
        this.nombre = nombre;
        this.arregloSize = arregloSize;
    }

    public String getTipo() {
        return tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public NodoAST getArregloSize() {
        return arregloSize;
    }

}
