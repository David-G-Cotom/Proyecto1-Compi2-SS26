/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.lenguajes.y.semantica;

import com.mycompany.proyecto1_compi2_ss26.ast.sentencias.NodoParametro;
import com.mycompany.proyecto1_compi2_ss26.ast.y.sentencias.NodoFuncionY;
import com.mycompany.proyecto1_compi2_ss26.errores.RecolectorErrores;
import com.mycompany.proyecto1_compi2_ss26.modelos.ModoPaso;
import com.mycompany.proyecto1_compi2_ss26.modelos.Primitivo;
import com.mycompany.proyecto1_compi2_ss26.tipos.DescriptorEstructura;
import com.mycompany.proyecto1_compi2_ss26.tipos.DescriptorFuncion;
import com.mycompany.proyecto1_compi2_ss26.tipos.DescriptorParametro;
import com.mycompany.proyecto1_compi2_ss26.tipos.TipoArreglo;
import com.mycompany.proyecto1_compi2_ss26.tipos.TipoDato;
import com.mycompany.proyecto1_compi2_ss26.tipos.TipoEstructura;
import com.mycompany.proyecto1_compi2_ss26.tipos.TipoPrimitivo;
import com.mycompany.proyecto1_compi2_ss26.tipos.TipoVacio;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author david
 */
public class RegistradorFuncionesY {

    private static final int COLUMNA_PLACEHOLDER = 1;

    private final RecolectorErrores coleccionErrores;

    public RegistradorFuncionesY(RecolectorErrores coleccionErrores) {
        this.coleccionErrores = coleccionErrores;
    }

    public Map<String, List<DescriptorFuncion>> registrar(List<NodoFuncionY> funcionesAST, Map<String, DescriptorEstructura> estructuras) {
        Map<String, List<DescriptorFuncion>> registro = new HashMap<>();

        for (NodoFuncionY funcionAST : funcionesAST) {
            List<DescriptorParametro> parametros = new ArrayList<>();
            boolean parametrosValidos = true;

            for (NodoParametro paramAST : funcionAST.getParametros()) {
                TipoDato tipo = this.resolverTipo(paramAST.getTipoTexto(), estructuras, paramAST.getLinea());
                if (tipo == null) {
                    parametrosValidos = false;
                    continue;
                }

                if (tipo instanceof TipoEstructura && paramAST.getModoPaso() == ModoPaso.POR_VALOR) {
                    this.coleccionErrores.addSemanticErrors(paramAST.getLinea(), COLUMNA_PLACEHOLDER,
                            "El parámetro '" + paramAST.getNombre() + "' es de tipo estructura ('"
                            + paramAST.getTipoTexto() + "') y debe declararse por referencia con "
                            + "'{} " + paramAST.getTipoTexto() + " " + paramAST.getNombre() + "'");
                    parametrosValidos = false;
                    continue;
                }

                parametros.add(new DescriptorParametro(paramAST.getNombre(), tipo, paramAST.getModoPaso()));
            }

            TipoDato tipoRetorno = funcionAST.getTipoRetornoTexto() == null
                    ? TipoVacio.INSTANCIA
                    : this.resolverTipo(funcionAST.getTipoRetornoTexto(), estructuras, funcionAST.getLinea());
            if (tipoRetorno == null) {
                parametrosValidos = false;
                tipoRetorno = TipoVacio.INSTANCIA;
            }

            if (!parametrosValidos) {
                continue;
            }

            DescriptorFuncion descriptor = new DescriptorFuncion(funcionAST.getNombre(), parametros, tipoRetorno);

            List<DescriptorFuncion> sobrecargas = registro.computeIfAbsent(funcionAST.getNombre(), k -> new ArrayList<>());
            for (DescriptorFuncion existente : sobrecargas) {
                if (this.mismaFirma(existente.getParametros(), parametros)) {
                    this.coleccionErrores.addSemanticErrors(funcionAST.getLinea(), COLUMNA_PLACEHOLDER,
                            "Sobrecarga inválida: ya existe una función '" + funcionAST.getNombre()
                            + "' con exactamente los mismos tipos de parámetro");
                }
            }
            sobrecargas.add(descriptor);
        }

        return registro;
    }

    private TipoDato resolverTipo(String texto, Map<String, DescriptorEstructura> estructuras, int linea) {
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
                DescriptorEstructura estructura = estructuras.get(texto);
                if (estructura == null) {
                    this.coleccionErrores.addSemanticErrors(linea, COLUMNA_PLACEHOLDER, "Tipo desconocido: '" + texto + "'");
                    return null;
                }
                return new TipoEstructura(estructura);
            }
        }
    }

    private boolean mismaFirma(List<DescriptorParametro> a, List<DescriptorParametro> b) {
        if (a.size() != b.size()) {
            return false;
        }
        for (int i = 0; i < a.size(); i++) {
            if (!this.mismoTipo(a.get(i).getTipo(), b.get(i).getTipo())) {
                return false;
            }
        }
        return true;
    }

    public boolean mismoTipo(TipoDato a, TipoDato b) {
        if (a instanceof TipoPrimitivo && b instanceof TipoPrimitivo) {
            return ((TipoPrimitivo) a).getPrimitivo() == ((TipoPrimitivo) b).getPrimitivo();
        }
        if (a instanceof TipoArreglo && b instanceof TipoArreglo) {
            return ((TipoArreglo) a).getSize() == ((TipoArreglo) b).getSize()
                    && mismoTipo(((TipoArreglo) a).getTipoBase(), ((TipoArreglo) b).getTipoBase());
        }
        if (a instanceof TipoEstructura && b instanceof TipoEstructura) {
            return ((TipoEstructura) a).getDescriptor().getNombre()
                    .equals(((TipoEstructura) b).getDescriptor().getNombre());
        }
        return a instanceof TipoVacio && b instanceof TipoVacio;
    }

}
