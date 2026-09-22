/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.ast.z.sentencias;

import com.mycompany.proyecto1_compi2_ss26.ast.NodoAST;
import com.mycompany.proyecto1_compi2_ss26.ast.sentencias.NodoParametro;
import java.util.List;

/**
 *
 * @author david
 */
public class NodoConstructorZ extends NodoAST {

    private final String nombre;
    private final List<NodoParametro> parametros;
    private final List<NodoAST> cuerpo;

    public NodoConstructorZ(int linea, String nombre, List<NodoParametro> parametros, List<NodoAST> cuerpo) {
        super(linea);
        this.nombre = nombre;
        this.parametros = parametros;
        this.cuerpo = cuerpo;
    }

    public String getNombre() {
        return nombre;
    }

    public List<NodoParametro> getParametros() {
        return parametros;
    }

    public List<NodoAST> getCuerpo() {
        return cuerpo;
    }

}
