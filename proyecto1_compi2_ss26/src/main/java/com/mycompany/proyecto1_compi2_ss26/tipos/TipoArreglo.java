/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.tipos;

/**
 *
 * @author david
 */
public class TipoArreglo extends TipoDato {

    private final TipoDato tipoBase;
    private final int size;

    public TipoArreglo(TipoDato tipoBase, int size) {
        this.tipoBase = tipoBase;
        this.size = size;
    }

    public TipoDato getTipoBase() {
        return tipoBase;
    }

    public int getSize() {
        return size;
    }

}
