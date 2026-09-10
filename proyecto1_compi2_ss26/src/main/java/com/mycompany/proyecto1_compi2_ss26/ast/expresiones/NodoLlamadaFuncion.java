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
public class NodoLlamadaFuncion extends NodoAST {

    private final String nombre;
    private final List<NodoAST> argumentos;

    public NodoLlamadaFuncion(String nombre, List<NodoAST> argumentos, int line) {
        super(line);
        this.nombre = nombre;
        this.argumentos = argumentos;
    }

    public String getNombre() {
        return nombre;
    }

    public List<NodoAST> getArgumentos() {
        return argumentos;
    }

}
