/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.tipos;

import java.util.List;

/**
 *
 * @author david
 */
public class DescriptorConstructor {

    private final List<DescriptorParametro> parametros;

    public DescriptorConstructor(List<DescriptorParametro> parametros) {
        this.parametros = parametros;
    }

    public List<DescriptorParametro> getParametros() {
        return parametros;
    }

}
