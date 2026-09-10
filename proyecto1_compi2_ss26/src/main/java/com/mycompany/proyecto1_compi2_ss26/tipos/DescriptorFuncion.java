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
public class DescriptorFuncion {

    private final String nombre;
    private final List<DescriptorParametro> parametros;
    private final TipoDato tipoRetorno;

    public DescriptorFuncion(String nombre, List<DescriptorParametro> parametros, TipoDato tipoRetorno) {
        this.nombre = nombre;
        this.parametros = parametros;
        this.tipoRetorno = tipoRetorno;
    }

    public String getNombre() {
        return nombre;
    }

    public List<DescriptorParametro> getParametros() {
        return parametros;
    }

    public TipoDato getTipoRetorno() {
        return tipoRetorno;
    }

}
