/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.ast.z.sentencias;

import com.mycompany.proyecto1_compi2_ss26.ast.NodoAST;

/**
 *
 * @author david
 */
public class NodoDeclaradorZ extends NodoAST {

    private final String nombre;
    private final NodoAST inicializador;

    public NodoDeclaradorZ(int linea, String nombre, NodoAST inicializador) {
        super(linea);
        this.nombre = nombre;
        this.inicializador = inicializador;
    }

    public String getNombre() {
        return nombre;
    }

    public NodoAST getInicializador() {
        return inicializador;
    }

}
