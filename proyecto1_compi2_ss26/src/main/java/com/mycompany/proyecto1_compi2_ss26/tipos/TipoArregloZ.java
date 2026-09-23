/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.tipos;

/**
 *
 * @author david
 */
public class TipoArregloZ extends TipoDato {

    private final TipoDato tipoBase;
    private final Integer tamanoConocido; // null = tamaño dinámico/no resuelto en semántica

    public TipoArregloZ(TipoDato tipoBase, Integer tamanoConocido) {
        this.tipoBase = tipoBase;
        this.tamanoConocido = tamanoConocido;
    }

    public TipoDato getTipoBase() {
        return tipoBase;
    }

    /**
     * Puede ser null
     * @return 
     */
    public Integer getTamanoConocido() {
        return tamanoConocido;
    }

    @Override
    public String toString() {
        return tipoBase + "[" + (tamanoConocido != null ? tamanoConocido : "?") + "]";
    }

}
