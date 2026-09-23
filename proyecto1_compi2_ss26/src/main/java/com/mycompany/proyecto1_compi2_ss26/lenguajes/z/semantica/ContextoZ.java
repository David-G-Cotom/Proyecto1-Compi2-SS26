/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.lenguajes.z.semantica;

import com.mycompany.proyecto1_compi2_ss26.lenguajes.semantica.TablaSimbolosLocal;
import com.mycompany.proyecto1_compi2_ss26.tipos.DescriptorClase;
import com.mycompany.proyecto1_compi2_ss26.tipos.TipoDato;

/**
 *
 * @author david
 */
public class ContextoZ {

    private final DescriptorClase clase;
    private final TablaSimbolosLocal tabla;
    private final TipoDato tipoRetornoActual; // TipoVacio.INSTANCIA para constructores
    private boolean dentroDeCiclo = false;
    private boolean dentroDeSwitch = false;

    public ContextoZ(DescriptorClase clase, TipoDato tipoRetornoActual) {
        this.tabla = new TablaSimbolosLocal();
        this.clase = clase;
        this.tipoRetornoActual = tipoRetornoActual;
    }

    public DescriptorClase getClase() {
        return clase;
    }

    public TablaSimbolosLocal getTabla() {
        return tabla;
    }

    public TipoDato getTipoRetornoActual() {
        return tipoRetornoActual;
    }

    public boolean isDentroDeCiclo() {
        return dentroDeCiclo;
    }

    public void setDentroDeCiclo(boolean dentroDeCiclo) {
        this.dentroDeCiclo = dentroDeCiclo;
    }

    public boolean isDentroDeSwitch() {
        return dentroDeSwitch;
    }

    public void setDentroDeSwitch(boolean dentroDeSwitch) {
        this.dentroDeSwitch = dentroDeSwitch;
    }

}
