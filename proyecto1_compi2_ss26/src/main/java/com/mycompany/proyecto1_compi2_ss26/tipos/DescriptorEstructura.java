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
public class DescriptorEstructura {

    private final String nombre;
    private final List<DescriptorCampo> campos;

    public DescriptorEstructura(String nombre, List<DescriptorCampo> campos) {
        this.nombre = nombre;
        this.campos = campos;
    }

    public String getNombre() {
        return nombre;
    }

    public List<DescriptorCampo> getCampos() {
        return campos;
    }

    public DescriptorCampo buscarCampo(String nombreCampo) {
        for (DescriptorCampo campo : this.campos) {
            if (campo.getNombre().equals(nombreCampo)) {
                return campo;
            }
        }
        return null;
    }

}
