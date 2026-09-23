/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.tipos;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author david
 */
public class TablaTiposClase {

    private final Map<String, Integer> idsPorNombre = new HashMap<>();
    private int siguienteId = 0;

    public int idPara(String nombreClase) {
        return idsPorNombre.computeIfAbsent(nombreClase, n -> siguienteId++);
    }

    public boolean tieneId(String nombreClase) {
        return idsPorNombre.containsKey(nombreClase);
    }

}
