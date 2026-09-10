/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.tipos;

import com.mycompany.proyecto1_compi2_ss26.modelos.ModoPaso;

/**
 *
 * @author david
 */
public class DescriptorParametro {

    private final String nombre;
    private final TipoDato tipo;
    private final ModoPaso modoPaso;

    public DescriptorParametro(String nombre, TipoDato tipo, ModoPaso modoPaso) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.modoPaso = modoPaso;
    }

    public String getNombre() {
        return nombre;
    }

    public TipoDato getTipo() {
        return tipo;
    }

    public ModoPaso getModoPaso() {
        return modoPaso;
    }

}
