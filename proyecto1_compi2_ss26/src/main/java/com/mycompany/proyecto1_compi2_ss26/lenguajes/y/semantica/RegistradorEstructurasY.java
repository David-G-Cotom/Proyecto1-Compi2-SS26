/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.lenguajes.y.semantica;

import com.mycompany.proyecto1_compi2_ss26.ast.sentencias.NodoCampoEstructura;
import com.mycompany.proyecto1_compi2_ss26.ast.sentencias.NodoEstructura;
import com.mycompany.proyecto1_compi2_ss26.errores.RecolectorErrores;
import com.mycompany.proyecto1_compi2_ss26.excepciones.ErrorConstante;
import com.mycompany.proyecto1_compi2_ss26.modelos.Primitivo;
import com.mycompany.proyecto1_compi2_ss26.tipos.DescriptorCampo;
import com.mycompany.proyecto1_compi2_ss26.tipos.DescriptorEstructura;
import com.mycompany.proyecto1_compi2_ss26.tipos.TipoArreglo;
import com.mycompany.proyecto1_compi2_ss26.tipos.TipoDato;
import com.mycompany.proyecto1_compi2_ss26.tipos.TipoEstructura;
import com.mycompany.proyecto1_compi2_ss26.tipos.TipoPrimitivo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author david
 */
public class RegistradorEstructurasY {

    private static final int COLUMNA_PLACEHOLDER = 1;

    private final EvaluadorConstantesY evaluadorConstantes = new EvaluadorConstantesY();
    private final RecolectorErrores coleccionErrores;

    public RegistradorEstructurasY(RecolectorErrores coleccionErrores) {
        this.coleccionErrores = coleccionErrores;
    }

    public Map<String, DescriptorEstructura> registrar(List<NodoEstructura> estructurasAST) {
        Map<String, DescriptorEstructura> descriptores = new HashMap<>();

        // Registrar nombres
        for (NodoEstructura nodo : estructurasAST) {
            if (descriptores.containsKey(nodo.getNombre())) {
                this.coleccionErrores.addSemanticErrors(nodo.getLinea(), COLUMNA_PLACEHOLDER,
                        "Estructura duplicada: '" + nodo.getNombre() + "' ya fue definida");
                continue;
            }
            descriptores.put(nodo.getNombre(), new DescriptorEstructura(nodo.getNombre(), new ArrayList<>()));
        }

        // Resolver campos
        for (NodoEstructura nodo : estructurasAST) {
            DescriptorEstructura descriptor = descriptores.get(nodo.getNombre());
            if (descriptor == null) {
                continue;
            }

            List<DescriptorCampo> campos = descriptor.getCampos();
            for (NodoCampoEstructura campoAST : nodo.getCampos()) {
                TipoDato tipoCampo = this.resolverTipoCampo(campoAST, descriptores);
                if (tipoCampo == null) {
                    continue;
                }
                campos.add(new DescriptorCampo(campoAST.getNombre(), tipoCampo));
            }
        }

        return descriptores;
    }

    private TipoDato resolverTipoCampo(NodoCampoEstructura campoAST, Map<String, DescriptorEstructura> descriptores) {
        TipoDato tipoBase = this.resolverTipo(campoAST.getTipo(), descriptores, campoAST.getLinea());
        if (tipoBase == null) {
            return null;
        }

        if (campoAST.getArregloSize() == null) {
            return tipoBase;
        }

        int size;
        try {
            size = this.evaluadorConstantes.evaluar(campoAST.getArregloSize());
        } catch (ErrorConstante e) {
            this.coleccionErrores.addSemanticErrors(campoAST.getLinea(), COLUMNA_PLACEHOLDER,
                    "Tamaño de arreglo inválido en campo '" + campoAST.getNombre() + "': " + e.getMessage());
            return null;
        }

        if (size <= 0) {
            this.coleccionErrores.addSemanticErrors(campoAST.getLinea(), COLUMNA_PLACEHOLDER,
                    "El tamaño de arreglo debe ser positivo (campo '" + campoAST.getNombre() + "', valor: " + size + ")");
            return null;
        }

        return new TipoArreglo(tipoBase, size);
    }

    private TipoDato resolverTipo(String texto, Map<String, DescriptorEstructura> descriptores, int linea) {
        switch (texto) {
            case "entero" -> {
                return new TipoPrimitivo(Primitivo.ENTERO);
            }
            case "flotante" -> {
                return new TipoPrimitivo(Primitivo.FLOTANTE);
            }
            case "caracter" -> {
                return new TipoPrimitivo(Primitivo.CARACTER);
            }
            case "cadena" -> {
                return new TipoPrimitivo(Primitivo.CADENA);
            }
            case "bool" -> {
                return new TipoPrimitivo(Primitivo.BOOLEANO);
            }
            default -> {
                DescriptorEstructura estructuraReferenciada = descriptores.get(texto);
                if (estructuraReferenciada == null) {
                    this.coleccionErrores.addSemanticErrors(linea, COLUMNA_PLACEHOLDER, "Tipo desconocido: '" + texto + "'");
                    return null;
                }
                return new TipoEstructura(estructuraReferenciada);
            }
        }
    }

}
