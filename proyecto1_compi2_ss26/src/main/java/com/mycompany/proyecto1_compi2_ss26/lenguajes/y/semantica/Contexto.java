/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.lenguajes.y.semantica;

import com.mycompany.proyecto1_compi2_ss26.tipos.DescriptorEstructura;
import com.mycompany.proyecto1_compi2_ss26.tipos.DescriptorFuncion;
import com.mycompany.proyecto1_compi2_ss26.tipos.TipoDato;
import java.util.List;
import java.util.Map;

/**
 *
 * @author david
 */
public class Contexto {

    private final Map<String, DescriptorEstructura> estructurasVisibles;
    private final Map<String, List<DescriptorFuncion>> funcionesVisibles;
    private final TablaSimbolosLocal tabla;
    private final TipoDato tipoRetornoFuncionActual;
    private boolean dentroDeCiclo = false;
    private boolean dentroDeCasoElegir = false;

    public Contexto(Map<String, DescriptorEstructura> estructurasVisibles, Map<String, List<DescriptorFuncion>> funcionesVisibles, TipoDato tipoRetornoFuncionActual) {
        this.tabla = new TablaSimbolosLocal();
        this.estructurasVisibles = estructurasVisibles;
        this.funcionesVisibles = funcionesVisibles;
        this.tipoRetornoFuncionActual = tipoRetornoFuncionActual;
    }

    public Map<String, DescriptorEstructura> getEstructurasVisibles() {
        return estructurasVisibles;
    }

    public Map<String, List<DescriptorFuncion>> getFuncionesVisibles() {
        return funcionesVisibles;
    }

    public TablaSimbolosLocal getTabla() {
        return tabla;
    }

    public TipoDato getTipoRetornoFuncionActual() {
        return tipoRetornoFuncionActual;
    }

    public boolean isDentroDeCiclo() {
        return dentroDeCiclo;
    }

    public void setDentroDeCiclo(boolean dentroDeCiclo) {
        this.dentroDeCiclo = dentroDeCiclo;
    }

    public boolean isDentroDeCasoElegir() {
        return dentroDeCasoElegir;
    }

    public void setDentroDeCasoElegir(boolean dentroDeCasoElegir) {
        this.dentroDeCasoElegir = dentroDeCasoElegir;
    }

}
