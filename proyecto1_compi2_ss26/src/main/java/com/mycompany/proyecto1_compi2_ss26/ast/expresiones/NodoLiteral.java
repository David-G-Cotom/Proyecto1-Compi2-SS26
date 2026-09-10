/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.ast.expresiones;

import com.mycompany.proyecto1_compi2_ss26.ast.NodoAST;
import com.mycompany.proyecto1_compi2_ss26.modelos.TipoLiteral;

/**
 *
 * @author david
 */
public class NodoLiteral extends NodoAST {

    private final TipoLiteral tipo;
    private final String textoOriginal;

    public NodoLiteral(TipoLiteral tipo, String textoOriginal, int line) {
        super(line);
        this.tipo = tipo;
        this.textoOriginal = textoOriginal;
    }

    public TipoLiteral getTipo() {
        return tipo;
    }

    public String getTextoOriginal() {
        return textoOriginal;
    }

}
