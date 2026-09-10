/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.ast.y.sentencias;

import com.mycompany.proyecto1_compi2_ss26.ast.NodoAST;
import java.util.List;

/**
 *
 * @author david
 */
public class NodoSi extends NodoAST {

    private final NodoAST condicion;
    private final List<NodoAST> cuerpoSi;
    private final List<NodoRamaSino> ramasSino;
    private final List<NodoAST> cuerpoContrario;

    public NodoSi(NodoAST condicion, List<NodoAST> cuerpoSi, List<NodoRamaSino> ramasSino, List<NodoAST> cuerpoContrario, int line) {
        super(line);
        this.condicion = condicion;
        this.cuerpoSi = cuerpoSi;
        this.ramasSino = ramasSino;
        this.cuerpoContrario = cuerpoContrario;
    }

    public NodoAST getCondicion() {
        return condicion;
    }

    public List<NodoAST> getCuerpoSi() {
        return cuerpoSi;
    }

    public List<NodoRamaSino> getRamasSino() {
        return ramasSino;
    }

    public List<NodoAST> getCuerpoContrario() {
        return cuerpoContrario;
    }

}
