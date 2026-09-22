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
public class NodoNuevoArregloZ extends NodoAST {

    private final String tipoBaseTexto;
    private final List<NodoAST> dimensiones;

    public NodoNuevoArregloZ(int linea, String tipoBaseTexto, List<NodoAST> dimensiones) {
        super(linea);
        this.tipoBaseTexto = tipoBaseTexto;
        this.dimensiones = dimensiones;
    }

    public String getTipoBaseTexto() {
        return tipoBaseTexto;
    }

    public List<NodoAST> getDimensiones() {
        return dimensiones;
    }

}
