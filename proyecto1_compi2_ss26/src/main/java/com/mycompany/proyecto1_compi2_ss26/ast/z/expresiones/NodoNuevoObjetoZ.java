/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.ast.z.expresiones;

import com.mycompany.proyecto1_compi2_ss26.ast.NodoAST;
import java.util.List;

/**
 *
 * @author david
 */
public class NodoNuevoObjetoZ extends NodoAST {

    private final String nombreClase;
    private final List<NodoAST> argumentos;

    public NodoNuevoObjetoZ(int linea, String nombreClase, List<NodoAST> argumentos) {
        super(linea);
        this.nombreClase = nombreClase;
        this.argumentos = argumentos;
    }

    public String getNombreClase() {
        return nombreClase;
    }

    public List<NodoAST> getArgumentos() {
        return argumentos;
    }

}
