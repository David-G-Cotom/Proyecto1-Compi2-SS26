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
public class DescriptorClase {

    private final String nombre;
    private final List<DescriptorAtributo> atributos;
    private final List<DescriptorConstructor> constructores;
    private final List<DescriptorMetodo> metodos;

    public DescriptorClase(String nombre, List<DescriptorAtributo> atributos, List<DescriptorConstructor> constructores, List<DescriptorMetodo> metodos) {
        this.nombre = nombre;
        this.atributos = atributos;
        this.constructores = constructores;
        this.metodos = metodos;
    }

    public String getNombre() {
        return nombre;
    }

    public List<DescriptorAtributo> getAtributos() {
        return atributos;
    }

    public List<DescriptorConstructor> getConstructores() {
        return constructores;
    }

    public List<DescriptorMetodo> getMetodos() {
        return metodos;
    }

}
