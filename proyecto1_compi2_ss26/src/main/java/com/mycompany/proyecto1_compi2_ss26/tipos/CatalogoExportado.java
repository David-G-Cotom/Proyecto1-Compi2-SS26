/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.tipos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author david
 */
public class CatalogoExportado {

    private final String rutaArchivo;
    private final Map<String, DescriptorEstructura> estructuras = new HashMap<>();
    private final Map<String, List<DescriptorFuncion>> funciones = new HashMap<>();
    private final Map<String, DescriptorClase> clases = new HashMap<>();
    private final List<Object> errores;

    public CatalogoExportado(String rutaArchivo, List<Object> errores) {
        this.rutaArchivo = rutaArchivo;
        this.errores = errores;
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public Map<String, DescriptorEstructura> getEstructuras() {
        return estructuras;
    }

    public Map<String, List<DescriptorFuncion>> getFunciones() {
        return funciones;
    }

    public Map<String, DescriptorClase> getClases() {
        return clases;
    }

    public List<Object> getErrores() {
        return errores;
    }

    public boolean tieneErrores() {
        return !this.errores.isEmpty();
    }

}
