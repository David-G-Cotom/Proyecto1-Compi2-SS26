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
public class NodoEstructura extends NodoAST {

    private final String nombre;
    private final List<NodoCampoEstructura> campos;

    public NodoEstructura(String nombre, List<NodoCampoEstructura> campos, int line) {
        super(line);
        this.nombre = nombre;
        this.campos = campos;
    }

    public String getNombre() {
        return nombre;
    }

    public List<NodoCampoEstructura> getCampos() {
        return campos;
    }

}
