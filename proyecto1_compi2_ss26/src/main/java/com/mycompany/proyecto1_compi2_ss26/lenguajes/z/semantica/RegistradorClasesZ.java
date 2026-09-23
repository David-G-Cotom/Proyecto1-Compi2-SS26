/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.lenguajes.z.semantica;

import com.mycompany.proyecto1_compi2_ss26.ast.sentencias.NodoFuncion;
import com.mycompany.proyecto1_compi2_ss26.ast.sentencias.NodoParametro;
import com.mycompany.proyecto1_compi2_ss26.ast.z.NodoArchivoZ;
import com.mycompany.proyecto1_compi2_ss26.ast.z.sentencias.*;
import com.mycompany.proyecto1_compi2_ss26.errores.RecolectorErrores;
import com.mycompany.proyecto1_compi2_ss26.modelos.Primitivo;
import com.mycompany.proyecto1_compi2_ss26.tipos.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author david
 */
public class RegistradorClasesZ {

    private static final int COLUMNA_PLACEHOLDER = 1;

    private final RecolectorErrores coleccionErrores;
    private final TablaTiposClase tablaTiposClase;

    public RegistradorClasesZ(RecolectorErrores coleccionErrores, TablaTiposClase tablaTiposClase) {
        this.coleccionErrores = coleccionErrores;
        this.tablaTiposClase = tablaTiposClase;
    }

    public DescriptorClase registrar(NodoArchivoZ archivo) {
        DescriptorClase descriptor = new DescriptorClase(archivo.getNombreClase(),
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        for (NodoAtributoZ atributoAST : archivo.getAtributos()) {
            registrarAtributo(atributoAST, descriptor);
        }
        for (NodoConstructorZ constructorAST : archivo.getConstructores()) {
            registrarConstructor(constructorAST, descriptor);
        }
        for (NodoFuncion metodoAST : archivo.getMetodos()) {
            registrarMetodo(metodoAST, descriptor);
        }

        tablaTiposClase.idPara(descriptor.getNombre());
        return descriptor;
    }

    private void registrarAtributo(NodoAtributoZ atributoAST, DescriptorClase descriptor) {
        TipoDato tipoBase = resolverTipoTexto(atributoAST.getTipoBaseTexto(), descriptor, atributoAST.getLinea());
        if (tipoBase == null) {
            return;
        }
        for (NodoDeclaradorZ declarador : atributoAST.getDeclaradores()) {
            if (buscarAtributo(descriptor, declarador.getNombre()) != null) {
                coleccionErrores.addSemanticErrors(declarador.getLinea(), COLUMNA_PLACEHOLDER,
                        "Atributo duplicado: '" + declarador.getNombre() + "' ya fue declarado en la clase '"
                        + descriptor.getNombre() + "'");
                continue;
            }
            descriptor.getAtributos().add(new DescriptorAtributo(declarador.getNombre(), tipoBase));
        }
    }

    private DescriptorAtributo buscarAtributo(DescriptorClase descriptor, String nombre) {
        for (DescriptorAtributo a : descriptor.getAtributos()) {
            if (a.getNombre().equals(nombre)) {
                return a;
            }
        }
        return null;
    }

    private void registrarConstructor(NodoConstructorZ constructorAST, DescriptorClase descriptor) {
        if (!constructorAST.getNombre().equals(descriptor.getNombre())) {
            coleccionErrores.addSemanticErrors(constructorAST.getLinea(), COLUMNA_PLACEHOLDER,
                    "El constructor '" + constructorAST.getNombre() + "' no coincide con el nombre de la clase '"
                    + descriptor.getNombre() + "'");
            return;
        }
        List<DescriptorParametro> parametros = resolverParametros(constructorAST.getParametros(), descriptor);
        if (existeFirmaConstructor(descriptor, parametros)) {
            coleccionErrores.addSemanticErrors(constructorAST.getLinea(), COLUMNA_PLACEHOLDER,
                    "Sobrecarga ambigua: ya existe un constructor de '" + descriptor.getNombre()
                    + "' con la misma firma (" + parametros.size() + " parámetro(s))");
            return;
        }
        descriptor.getConstructores().add(new DescriptorConstructor(parametros));
    }

    private void registrarMetodo(NodoFuncion metodoAST, DescriptorClase descriptor) {
        TipoDato tipoRetorno = resolverTipoRetorno(metodoAST.getTipoRetornoTexto(), descriptor, metodoAST.getLinea());
        if (tipoRetorno == null) {
            return;
        }
        List<DescriptorParametro> parametros = resolverParametros(metodoAST.getParametros(), descriptor);
        if (existeFirmaMetodo(descriptor, metodoAST.getNombre(), parametros)) {
            coleccionErrores.addSemanticErrors(metodoAST.getLinea(), COLUMNA_PLACEHOLDER,
                    "Sobrecarga ambigua: ya existe un método '" + metodoAST.getNombre() + "' en '"
                    + descriptor.getNombre() + "' con la misma firma (" + parametros.size() + " parámetro(s))");
            return;
        }
        descriptor.getMetodos().add(new DescriptorMetodo(metodoAST.getNombre(), parametros, tipoRetorno));
    }

    private List<DescriptorParametro> resolverParametros(List<NodoParametro> parametrosAST, DescriptorClase descriptor) {
        List<DescriptorParametro> resultado = new ArrayList<>();
        for (NodoParametro p : parametrosAST) {
            TipoDato tipo = resolverTipoTexto(p.getTipoTexto(), descriptor, p.getLinea());
            if (tipo == null) {
                continue;
            }
            resultado.add(new DescriptorParametro(p.getNombre(), tipo, p.getModoPaso()));
        }
        return resultado;
    }

    private boolean existeFirmaConstructor(DescriptorClase descriptor, List<DescriptorParametro> parametros) {
        for (DescriptorConstructor c : descriptor.getConstructores()) {
            if (mismaFirma(c.getParametros(), parametros)) {
                return true;
            }
        }
        return false;
    }

    private boolean existeFirmaMetodo(DescriptorClase descriptor, String nombre, List<DescriptorParametro> parametros) {
        for (DescriptorMetodo m : descriptor.getMetodos()) {
            if (m.getNombre().equals(nombre) && mismaFirma(m.getParametros(), parametros)) {
                return true;
            }
        }
        return false;
    }

    private boolean mismaFirma(List<DescriptorParametro> a, List<DescriptorParametro> b) {
        if (a.size() != b.size()) {
            return false;
        }
        for (int i = 0; i < a.size(); i++) {
            if (!mismoTipo(a.get(i).getTipo(), b.get(i).getTipo())) {
                return false;
            }
        }
        return true;
    }

    private boolean mismoTipo(TipoDato a, TipoDato b) {
        if (a instanceof TipoPrimitivo && b instanceof TipoPrimitivo) {
            return ((TipoPrimitivo) a).getPrimitivo() == ((TipoPrimitivo) b).getPrimitivo();
        }
        if (a instanceof TipoArregloZ && b instanceof TipoArregloZ) {
            // El tamaño NO participa en la identidad de tipo (ver TipoArregloZ) — estilo Java.
            return mismoTipo(((TipoArregloZ) a).getTipoBase(), ((TipoArregloZ) b).getTipoBase());
        }
        if (a instanceof TipoObjeto && b instanceof TipoObjeto) {
            return ((TipoObjeto) a).getDescriptor().getNombre().equals(((TipoObjeto) b).getDescriptor().getNombre());
        }
        return a instanceof TipoVacio && b instanceof TipoVacio;
    }

    // =====================================================================
    // Resolución de tipos por texto (ej. "int[][]", "Persona[]", "void")
    // =====================================================================
    TipoDato resolverTipoRetorno(String texto, DescriptorClase descriptorEnConstruccion, int linea) {
        if (texto.equals("void")) {
            return TipoVacio.INSTANCIA;
        }
        return resolverTipoTexto(texto, descriptorEnConstruccion, linea);
    }

    TipoDato resolverTipoTexto(String texto, DescriptorClase descriptorEnConstruccion, int linea) {
        int dimensiones = 0;
        String base = texto;
        while (base.endsWith("[]")) {
            dimensiones++;
            base = base.substring(0, base.length() - 2);
        }

        TipoDato tipoBase = resolverTipoBase(base, descriptorEnConstruccion, linea);
        if (tipoBase == null) {
            return null;
        }

        TipoDato tipoFinal = tipoBase;
        for (int i = 0; i < dimensiones; i++) {
            tipoFinal = new TipoArregloZ(tipoFinal, null); // sin tamaño: se declara sin tamaño en Zetariano
        }
        return tipoFinal;
    }

    private TipoDato resolverTipoBase(String texto, DescriptorClase descriptorEnConstruccion, int linea) {
        switch (texto) {
            case "int" -> {
                return new TipoPrimitivo(Primitivo.ENTERO);
            }
            case "double" -> {
                return new TipoPrimitivo(Primitivo.FLOTANTE);
            }
            case "char" -> {
                return new TipoPrimitivo(Primitivo.CARACTER);
            }
            case "boolean" -> {
                return new TipoPrimitivo(Primitivo.BOOLEANO);
            }
            case "String" -> {
                return new TipoPrimitivo(Primitivo.CADENA);
            }
            default -> {
                if (texto.equals(descriptorEnConstruccion.getNombre())) {
                    return new TipoObjeto(descriptorEnConstruccion); // auto-referencia
                }
                coleccionErrores.addSemanticErrors(linea, COLUMNA_PLACEHOLDER,
                        "Tipo desconocido: '" + texto + "' (si es una clase definida en otro archivo .z, "
                        + "la resolución de imports entre archivos aún no está implementada — Día 22)");
                return null;
            }
        }
    }

}
