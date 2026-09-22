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
public class NodoFuncion extends NodoAST {

    private final String nombre;
    private final List<NodoParametro> parametros;
    private final String tipoRetornoTexto;
    private final List<NodoAST> cuerpo;

    public NodoFuncion(String nombre, List<NodoParametro> parametros, String tipoRetornoTexto, List<NodoAST> cuerpo, int line) {
        super(line);
        this.nombre = nombre;
        this.parametros = parametros;
        this.tipoRetornoTexto = tipoRetornoTexto;
        this.cuerpo = cuerpo;
    }

    public String getNombre() {
        return nombre;
    }

    public List<NodoParametro> getParametros() {
        return parametros;
    }

    public String getTipoRetornoTexto() {
        return tipoRetornoTexto;
    }

    public List<NodoAST> getCuerpo() {
        return cuerpo;
    }

}
