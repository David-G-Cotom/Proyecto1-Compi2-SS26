/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.ast.z;

import com.mycompany.proyecto1_compi2_ss26.ast.z.sentencias.NodoAtributoZ;
import com.mycompany.proyecto1_compi2_ss26.ast.z.sentencias.NodoConstructorZ;
import com.mycompany.proyecto1_compi2_ss26.ast.NodoAST;
import com.mycompany.proyecto1_compi2_ss26.ast.sentencias.NodoFuncion;
import java.util.List;

/**
 *
 * @author david
 */
public class NodoArchivoZ extends NodoAST {

    private final String nombreClase;
    private final List<NodoAtributoZ> atributos;
    private final List<NodoConstructorZ> constructores;
    private final List<NodoFuncion> metodos;

    public NodoArchivoZ(int linea, String nombreClase, List<NodoAtributoZ> atributos,
            List<NodoConstructorZ> constructores, List<NodoFuncion> metodos) {
        super(linea);
        this.nombreClase = nombreClase;
        this.atributos = atributos;
        this.constructores = constructores;
        this.metodos = metodos;
    }

    public String getNombreClase() {
        return nombreClase;
    }

    public List<NodoAtributoZ> getAtributos() {
        return atributos;
    }

    public List<NodoConstructorZ> getConstructores() {
        return constructores;
    }

    public List<NodoFuncion> getMetodos() {
        return metodos;
    }

}
