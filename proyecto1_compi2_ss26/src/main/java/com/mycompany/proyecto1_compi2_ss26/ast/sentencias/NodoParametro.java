/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.ast.sentencias;

import com.mycompany.proyecto1_compi2_ss26.ast.NodoAST;
import com.mycompany.proyecto1_compi2_ss26.modelos.ModoPaso;

/**
 *
 * @author david
 */
public class NodoParametro extends NodoAST {

    private final String tipoTexto;
    private final String nombre;
    private final ModoPaso modoPaso;

    public NodoParametro(String tipoTexto, String nombre, ModoPaso modoPaso, int line) {
        super(line);
        this.tipoTexto = tipoTexto;
        this.nombre = nombre;
        this.modoPaso = modoPaso;
    }

    public String getTipoTexto() {
        return tipoTexto;
    }

    public String getNombre() {
        return nombre;
    }

    public ModoPaso getModoPaso() {
        return modoPaso;
    }

}
