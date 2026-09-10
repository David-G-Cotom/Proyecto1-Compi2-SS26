/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.ast.y;

import com.mycompany.proyecto1_compi2_ss26.ast.y.sentencias.NodoFuncionY;
import com.mycompany.proyecto1_compi2_ss26.ast.NodoAST;
import com.mycompany.proyecto1_compi2_ss26.ast.sentencias.NodoEstructura;
import java.util.List;

/**
 *
 * @author david
 */
public class NodoArchivoY extends NodoAST {

    private final List<NodoEstructura> estructuras;
    private final List<NodoFuncionY> funciones;

    public NodoArchivoY(List<NodoEstructura> estructuras, List<NodoFuncionY> funciones, int line) {
        super(line);
        this.estructuras = estructuras;
        this.funciones = funciones;
    }

    public List<NodoEstructura> getEstructuras() {
        return estructuras;
    }

    public List<NodoFuncionY> getFunciones() {
        return funciones;
    }

}
