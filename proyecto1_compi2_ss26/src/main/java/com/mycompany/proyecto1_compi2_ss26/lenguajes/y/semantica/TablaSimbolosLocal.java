/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.lenguajes.y.semantica;

import com.mycompany.proyecto1_compi2_ss26.tipos.TipoDato;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author david
 */
public class TablaSimbolosLocal {

    private final Deque<Map<String, TipoDato>> ambitos;

    public TablaSimbolosLocal() {
        this.ambitos = new ArrayDeque<>();
        this.entrarAmbito();
    }

    public void entrarAmbito() {
        this.ambitos.push(new HashMap<>());
    }

    public void salirAmbito() {
        this.ambitos.pop();
    }

    public boolean declarar(String nombre, TipoDato tipo) {
        Map<String, TipoDato> actual = this.ambitos.peek();
        if (actual.containsKey(nombre)) {
            return false;
        }
        actual.put(nombre, tipo);
        return true;
    }

    public TipoDato buscar(String nombre) {
        for (Map<String, TipoDato> ambito : this.ambitos) {
            if (ambito.containsKey(nombre)) {
                return ambito.get(nombre);
            }
        }
        return null;
    }

}
