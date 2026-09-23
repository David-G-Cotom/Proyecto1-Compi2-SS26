/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.tipos;

/**
 *
 * @author david
 */
public class TipoObjeto extends TipoDato {

    private final DescriptorClase descriptor;

    public TipoObjeto(DescriptorClase descriptor) {
        this.descriptor = descriptor;
    }

    public DescriptorClase getDescriptor() {
        return descriptor;
    }

    @Override
    public String toString() {
        return descriptor.getNombre();
    }

}
