/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.tipos;

/**
 *
 * @author david
 */
public class TipoNulo extends TipoDato {

    public static final TipoNulo INSTANCIA = new TipoNulo();

    private TipoNulo() {
    }

    @Override
    public String toString() {
        return "NULO";
    }

}
