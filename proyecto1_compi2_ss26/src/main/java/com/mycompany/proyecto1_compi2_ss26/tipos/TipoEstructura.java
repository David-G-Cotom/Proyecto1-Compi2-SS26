/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.tipos;

/**
 *
 * @author david
 */
public class TipoEstructura extends TipoDato {

    private final DescriptorEstructura descriptor;

    public TipoEstructura(DescriptorEstructura descriptor) {
        this.descriptor = descriptor;
    }

    public DescriptorEstructura getDescriptor() {
        return descriptor;
    }

}
