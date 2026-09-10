/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.ast.expresiones;

import com.mycompany.proyecto1_compi2_ss26.ast.NodoAST;
import java.util.List;

/**
 *
 * @author david
 */
public class NodoAccesoVariable extends NodoAST {

    private final String nombre;
    private final List<NodoAST> sufijos;

    public NodoAccesoVariable(String nombre, List<NodoAST> sufijos, int line) {
        super(line);
        this.nombre = nombre;
        this.sufijos = sufijos;
    }

    public String getNombre() {
        return nombre;
    }

    public List<NodoAST> getSufijos() {
        return sufijos;
    }

}
