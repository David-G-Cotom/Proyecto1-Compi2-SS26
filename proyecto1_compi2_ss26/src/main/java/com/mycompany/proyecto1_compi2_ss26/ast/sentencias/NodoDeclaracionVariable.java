/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.ast.sentencias;

import com.mycompany.proyecto1_compi2_ss26.ast.NodoAST;
import java.util.List;

/**
 *
 * @author david
 */
public class NodoDeclaracionVariable extends NodoAST {

    private final String tipo;
    private final String nombre;
    private final List<NodoAST> dimensiones;
    private final NodoAST inicializador;

    public NodoDeclaracionVariable(String tipo, String nombre, List<NodoAST> dimensiones, NodoAST inicializador, int line) {
        super(line);
        this.tipo = tipo;
        this.nombre = nombre;
        this.dimensiones = dimensiones;
        this.inicializador = inicializador;
    }

    public String getTipo() {
        return tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public List<NodoAST> getDimensiones() {
        return dimensiones;
    }

    public NodoAST getInicializador() {
        return inicializador;
    }

}
