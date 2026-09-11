/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.lenguajes.y.semantica;

import com.mycompany.proyecto1_compi2_ss26.ast.NodoAST;
import com.mycompany.proyecto1_compi2_ss26.ast.expresiones.NodoBinario;
import com.mycompany.proyecto1_compi2_ss26.ast.expresiones.NodoLiteral;
import com.mycompany.proyecto1_compi2_ss26.excepciones.ErrorConstante;
import com.mycompany.proyecto1_compi2_ss26.modelos.TipoLiteral;

/**
 *
 * @author david
 */
public class EvaluadorConstantesY {

    public int evaluar(NodoAST nodo) {
        if (nodo instanceof NodoLiteral literal) {
            if (literal.getTipo() != TipoLiteral.ENTERO) {
                throw new ErrorConstante("Se esperaba un literal entero en una expresión constante");
            }
            return Integer.parseInt(literal.getTextoOriginal());
        }
        if (nodo instanceof NodoBinario binaria) {
            int izq = evaluar(binaria.getIzquierda());
            int der = evaluar(binaria.getDerecha());
            switch (binaria.getOperador()) {
                case "+" -> {
                    return izq + der;
                }
                case "-" -> {
                    return izq - der;
                }
                case "*" -> {
                    return izq * der;
                }
                case "/" -> {
                    if (der == 0) {
                        throw new ErrorConstante("División entre cero en expresión constante");
                    }
                    return izq / der;
                }
                default ->
                    throw new ErrorConstante("Operador no válido en expresión constante: " + binaria.getOperador());
            }
        }
        throw new ErrorConstante("Expresión no es constante (contiene algo distinto de literales enteros y aritmética)");
    }

}
