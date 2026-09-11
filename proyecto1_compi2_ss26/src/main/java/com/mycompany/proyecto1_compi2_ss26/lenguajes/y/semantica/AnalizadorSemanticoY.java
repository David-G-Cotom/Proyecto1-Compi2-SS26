/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.lenguajes.y.semantica;

import com.mycompany.proyecto1_compi2_ss26.ast.NodoAST;
import com.mycompany.proyecto1_compi2_ss26.ast.expresiones.NodoAccesoVariable;
import com.mycompany.proyecto1_compi2_ss26.ast.expresiones.NodoBinario;
import com.mycompany.proyecto1_compi2_ss26.ast.expresiones.NodoLiteral;
import com.mycompany.proyecto1_compi2_ss26.ast.expresiones.NodoLiteralCompuesto;
import com.mycompany.proyecto1_compi2_ss26.ast.expresiones.NodoLlamadaFuncion;
import com.mycompany.proyecto1_compi2_ss26.ast.expresiones.NodoSufijoCampo;
import com.mycompany.proyecto1_compi2_ss26.ast.expresiones.NodoSufijoIndice;
import com.mycompany.proyecto1_compi2_ss26.ast.expresiones.NodoUnario;
import com.mycompany.proyecto1_compi2_ss26.ast.sentencias.*;
import com.mycompany.proyecto1_compi2_ss26.ast.y.NodoArchivoY;
import com.mycompany.proyecto1_compi2_ss26.ast.y.sentencias.*;
import com.mycompany.proyecto1_compi2_ss26.errores.RecolectorErrores;
import com.mycompany.proyecto1_compi2_ss26.excepciones.ErrorConstante;
import com.mycompany.proyecto1_compi2_ss26.modelos.Primitivo;
import com.mycompany.proyecto1_compi2_ss26.tipos.CatalogoExportado;
import com.mycompany.proyecto1_compi2_ss26.tipos.DescriptorCampo;
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
public class AnalizadorSemanticoY {

    private final EvaluadorConstantesY evaluadorConstantes = new EvaluadorConstantesY();
    private final RecolectorErrores coleccionErrores;
    private static final int COLUMNA_PLACEHOLDER = 1;

    public AnalizadorSemanticoY(RecolectorErrores coleccionErrores) {
        this.coleccionErrores = coleccionErrores;
    }

    public CatalogoExportado analizar(String rutaArchivo, NodoArchivoY archivo) {
        RegistradorEstructurasY registradorEstructuras = new RegistradorEstructurasY(this.coleccionErrores);
        Map<String, DescriptorEstructura> estructuras = registradorEstructuras.registrar(archivo.getEstructuras());

        RegistradorFuncionesY registradorFunciones = new RegistradorFuncionesY(this.coleccionErrores);
        Map<String, List<DescriptorFuncion>> funciones = registradorFunciones.registrar(archivo.getFunciones(), estructuras);

        for (NodoFuncionY funcionAST : archivo.getFunciones()) {
            List<DescriptorFuncion> firmas = funciones.get(funcionAST.getNombre());
            DescriptorFuncion firmaExacta = this.buscarFirmaExacta(firmas, funcionAST, estructuras, registradorFunciones);
            TipoDato tipoRetorno = (firmaExacta == null) ? TipoVacio.INSTANCIA : firmaExacta.getTipoRetorno();
            this.analizarFuncion(funcionAST, estructuras, funciones, tipoRetorno);
        }

        CatalogoExportado catalogo = new CatalogoExportado(rutaArchivo, this.coleccionErrores.getErrors());
        catalogo.getEstructuras().putAll(estructuras);
        catalogo.getFunciones().putAll(funciones);
        return catalogo;
    }

    private DescriptorFuncion buscarFirmaExacta(List<DescriptorFuncion> firmas, NodoFuncionY funcionAST,
            Map<String, DescriptorEstructura> estructuras, RegistradorFuncionesY registradorFunciones) {
        if (firmas == null || firmas.isEmpty()) {
            return null;
        }

        // Convertir los parámetros del AST actual a una lista de TipoDato temporales para comparar
        List<TipoDato> tiposAST = new ArrayList<>();
        for (NodoParametro paramAST : funcionAST.getParametros()) {
            TipoDato tipo = this.resolverTipoTexto(paramAST.getTipoTexto(), estructuras, paramAST.getLinea());
            if (tipo == null) {
                return null;
            }
            tiposAST.add(tipo);
        }

        // Buscar entre las firmas registradas cuál coincide en tamaño y tipos
        for (DescriptorFuncion firma : firmas) {
            List<DescriptorParametro> paramsFirma = firma.getParametros();

            if (paramsFirma.size() != tiposAST.size()) {
                continue;
            }
            boolean coincide = true;
            for (int i = 0; i < paramsFirma.size(); i++) {
                if (!registradorFunciones.mismoTipo(paramsFirma.get(i).getTipo(), tiposAST.get(i))) {
                    coincide = false;
                    break;
                }
            }
            if (coincide) {
                return firma; // Firma encontrada
            }
        }

        return null;
    }

    private void analizarFuncion(NodoFuncionY funcionAST, Map<String, DescriptorEstructura> estructuras,
            Map<String, List<DescriptorFuncion>> funciones, TipoDato tipoRetorno) {
        Contexto ctx = new Contexto(new HashMap<>(estructuras), funciones, tipoRetorno);

        for (NodoParametro parametro : funcionAST.getParametros()) {
            TipoDato tipo = this.resolverTipoTexto(parametro.getTipoTexto(), ctx.getEstructurasVisibles(), parametro.getLinea());
            if (tipo != null) {
                ctx.getTabla().declarar(parametro.getNombre(), tipo);
            }
        }

        for (NodoAST sentencia : funcionAST.getCuerpo()) {
            this.validarSentencia(sentencia, ctx);
        }
    }

    private void validarSentencia(NodoAST nodo, Contexto ctx) {
        if (nodo instanceof NodoEstructura nodoEstructura) {
            this.registrarEstructuraLocal(nodoEstructura, ctx);
        } else if (nodo instanceof NodoDeclaracionVariable nodoDeclaracionVariable) {
            this.validarDeclaracion(nodoDeclaracionVariable, ctx);
        } else if (nodo instanceof NodoAsignacion nodoAsignacion) {
            this.validarAsignacion(nodoAsignacion, ctx);
        } else if (nodo instanceof NodoIncrementoDecremento nodoIncrementoDecremento) {
            TipoDato tipo = this.inferirTipo(nodoIncrementoDecremento.getDestino(), ctx);
            if (tipo != null && !this.esNumerico(tipo)) {
                this.error(nodo, "'++'/'--' solo aplica a variables numéricas");
            }
        } else if (nodo instanceof NodoLlamadaFuncion) {
            this.inferirTipo(nodo, ctx); // valida existencia/firma, descarta el valor de retorno
        } else if (nodo instanceof NodoImprimir nodoImprimir) {
            this.inferirTipo(nodoImprimir.getExpresion(), ctx);
        } else if (nodo instanceof NodoLeer) {
            // sin validación adicional: leer() no requiere argumentos
        } else if (nodo instanceof NodoSi nodoSi) {
            this.validarSi(nodoSi, ctx);
        } else if (nodo instanceof NodoElegir nodoElegir) {
            this.validarElegir(nodoElegir, ctx);
        } else if (nodo instanceof NodoPara nodoPara) {
            this.validarPara(nodoPara, ctx);
        } else if (nodo instanceof NodoMientras nodoMientras) {
            this.validarMientras(nodoMientras, ctx);
        } else if (nodo instanceof NodoHacerMientras nodoHacerMientras) {
            this.validarHacerMientras(nodoHacerMientras, ctx);
        } else if (nodo instanceof NodoContinuar) {
            if (!ctx.isDentroDeCiclo()) {
                this.error(nodo, "'continuar' usado fuera de un ciclo");
            }
        } else if (nodo instanceof NodoRomper) {
            if (!ctx.isDentroDeCiclo() && !ctx.isDentroDeCasoElegir()) {
                this.error(nodo, "'romper' usado fuera de un ciclo o 'elegir'");
            }
        } else if (nodo instanceof NodoRetornar nodoRetornar) {
            this.validarRetornar(nodoRetornar, ctx);
        }
    }

    private void registrarEstructuraLocal(NodoEstructura nodo, Contexto ctx) {
        if (ctx.getEstructurasVisibles().containsKey(nodo.getNombre())) {
            this.error(nodo, "Estructura local '" + nodo.getNombre() + "' colisiona con una ya global");
            return;
        }
        List<DescriptorCampo> campos = new ArrayList<>();
        DescriptorEstructura descriptor = new DescriptorEstructura(nodo.getNombre(), campos);
        ctx.getEstructurasVisibles().put(nodo.getNombre(), descriptor);
        for (NodoCampoEstructura campoAST : nodo.getCampos()) {
            TipoDato tipoBase = this.resolverTipoTexto(campoAST.getTipo(), ctx.getEstructurasVisibles(), campoAST.getLinea());
            if (tipoBase == null) {
                continue;
            }
            TipoDato tipoFinal = tipoBase;
            if (campoAST.getArregloSize() != null) {
                try {
                    int size = this.evaluadorConstantes.evaluar(campoAST.getArregloSize());
                    if (size <= 0) {
                        this.error(campoAST, "Tamaño de arreglo debe ser positivo");
                        continue;
                    }
                    tipoFinal = new TipoArreglo(tipoBase, size);
                } catch (ErrorConstante e) {
                    this.error(campoAST, "Tamaño de arreglo inválido: " + e.getMessage());
                    continue;
                }
            }
            campos.add(new DescriptorCampo(campoAST.getNombre(), tipoFinal));
        }
    }

    private void validarDeclaracion(NodoDeclaracionVariable nodo, Contexto ctx) {
        TipoDato tipoBase = this.resolverTipoTexto(nodo.getTipo(), ctx.getEstructurasVisibles(), nodo.getLinea());
        if (tipoBase == null) {
            return;
        }

        TipoDato tipoFinal = tipoBase;
        for (NodoAST dimension : nodo.getDimensiones()) {
            try {
                int size = this.evaluadorConstantes.evaluar(dimension);
                if (size <= 0) {
                    this.error(nodo, "Tamaño de arreglo debe ser positivo");
                    return;
                }
                tipoFinal = new TipoArreglo(tipoFinal, size);
            } catch (ErrorConstante e) {
                this.error(nodo, "Tamaño de arreglo inválido: " + e.getMessage());
                return;
            }
        }

        if (!ctx.getTabla().declarar(nodo.getNombre(), tipoFinal)) {
            this.error(nodo, "Variable '" + nodo.getNombre() + "' ya declarada en este ambito");
            return;
        }

        if (nodo.getInicializador() instanceof NodoLiteralCompuesto nodoLiteralCompuesto) {
            this.validarLiteralCompuesto(nodoLiteralCompuesto, tipoFinal, ctx);
        } else if (nodo.getInicializador() != null) {
            TipoDato tipoInicializador = this.inferirTipo(nodo.getInicializador(), ctx);
            if (tipoInicializador != null && !this.isCompatibleAsignacion(tipoFinal, tipoInicializador)) {
                this.error(nodo, "No se puede inicializar '" + nodo.getNombre() + "' (" + tipoFinal
                        + ") con un valor de tipo " + tipoInicializador);
            }
        }
    }

    private void validarLiteralCompuesto(NodoLiteralCompuesto literal, TipoDato tipoEsperado, Contexto ctx) {
        switch (tipoEsperado) {
            case TipoArreglo tipoArreglo -> {
                TipoDato tipoElemento = tipoArreglo.getTipoBase();
                int sizeDeclarado = tipoArreglo.getSize();
                if (literal.getValores().size() != sizeDeclarado) {
                    this.error(literal, "El literal compuesto tiene " + literal.getValores().size()
                            + " valores, pero el arreglo declara tamaño " + sizeDeclarado);
                }
                for (NodoAST valor : literal.getValores()) {
                    TipoDato tipoValor = this.inferirTipo(valor, ctx);
                    if (tipoValor != null && !this.isCompatibleAsignacion(tipoElemento, tipoValor)) {
                        this.error(valor, "Elemento de tipo " + tipoValor + " no compatible con " + tipoElemento);
                    }
                }
            }
            case TipoEstructura tipoEstructura -> {
                List<DescriptorCampo> campos = tipoEstructura.getDescriptor().getCampos();
                if (literal.getValores().size() != campos.size()) {
                    this.error(literal, "El literal compuesto tiene " + literal.getValores().size()
                            + " valores, pero la estructura tiene " + campos.size() + " campos");
                }
                for (int i = 0; i < campos.size(); i++) {
                    TipoDato tipoValor = this.inferirTipo(literal.getValores().get(i), ctx);
                    if (tipoValor != null && !this.isCompatibleAsignacion(campos.get(i).getTipo(), tipoValor)) {
                        this.error(literal, "Campo #" + (i + 1) + " (" + campos.get(i).getNombre() + "): tipo "
                                + tipoValor + " no compatible con " + campos.get(i).getTipo());
                    }
                }
            }
            default ->
                this.error(literal, "No se puede usar un literal compuesto '{...}' para inicializar un tipo " + tipoEsperado);
        }
    }

    private void validarAsignacion(NodoAsignacion nodo, Contexto ctx) {
        TipoDato tipoDestino = this.inferirTipo(nodo.getDestino(), ctx);
        TipoDato tipoValor = this.inferirTipo(nodo.getValor(), ctx);
        if (tipoDestino != null && tipoValor != null && !this.isCompatibleAsignacion(tipoDestino, tipoValor)) {
            this.error(nodo, "No se puede asignar un valor de tipo " + tipoValor + " a '"
                    + nodo.getDestino().getNombre() + "' (" + tipoDestino + ")");
        }
    }

    private void validarSi(NodoSi nodo, Contexto ctx) {
        this.validarCondicionBooleana(nodo.getCondicion(), ctx);
        ctx.getTabla().entrarAmbito();
        for (NodoAST s : nodo.getCuerpoSi()) {
            this.validarSentencia(s, ctx);
        }
        ctx.getTabla().salirAmbito();

        for (NodoRamaSino rama : nodo.getRamasSino()) {
            this.validarCondicionBooleana(rama.getCondicion(), ctx);
            ctx.getTabla().entrarAmbito();
            for (NodoAST s : rama.getCuerpo()) {
                this.validarSentencia(s, ctx);
            }
            ctx.getTabla().salirAmbito();
        }

        if (nodo.getCuerpoContrario() != null) {
            ctx.getTabla().entrarAmbito();
            for (NodoAST s : nodo.getCuerpoContrario()) {
                this.validarSentencia(s, ctx);
            }
            ctx.getTabla().salirAmbito();
        }
    }

    private void validarElegir(NodoElegir nodo, Contexto ctx) {
        TipoDato tipoEvaluado = this.inferirTipo(nodo.getValorEvaluado(), ctx);
        boolean estabaEnCaso = ctx.isDentroDeCasoElegir();
        ctx.setDentroDeCasoElegir(true);

        for (NodoCaso caso : nodo.getCasos()) {
            TipoDato tipoCaso = this.inferirTipo(caso.getValorLiteral(), ctx);
            if (tipoEvaluado != null && tipoCaso != null && !this.isCompatibleAsignacion(tipoEvaluado, tipoCaso)
                    && !this.isCompatibleAsignacion(tipoCaso, tipoEvaluado)) {
                error(caso, "El tipo del 'caso' (" + tipoCaso + ") no es compatible con lo evaluado en 'elegir' (" + tipoEvaluado + ")");
            }
            ctx.getTabla().entrarAmbito();
            for (NodoAST s : caso.getCuerpo()) {
                validarSentencia(s, ctx);
            }
            ctx.getTabla().salirAmbito();
        }
        if (nodo.getSiempre() != null) {
            ctx.getTabla().entrarAmbito();
            for (NodoAST s : nodo.getSiempre().getCuerpo()) {
                validarSentencia(s, ctx);
            }
            ctx.getTabla().salirAmbito();
        }

        ctx.setDentroDeCasoElegir(estabaEnCaso);
    }

    private void validarPara(NodoPara nodo, Contexto ctx) {
        ctx.getTabla().entrarAmbito();
        this.validarSentencia(nodo.getInit(), ctx);
        this.validarCondicionBooleana(nodo.getCondicion(), ctx);
        this.validarSentencia(nodo.getActualizacion(), ctx);

        boolean estabaEnCiclo = ctx.isDentroDeCiclo();
        ctx.setDentroDeCiclo(true);
        for (NodoAST s : nodo.getCuerpo()) {
            validarSentencia(s, ctx);
        }
        ctx.setDentroDeCiclo(estabaEnCiclo);

        ctx.getTabla().salirAmbito();
    }

    private void validarMientras(NodoMientras nodo, Contexto ctx) {
        validarCondicionBooleana(nodo.getCondicion(), ctx);
        boolean estabaEnCiclo = ctx.isDentroDeCiclo();
        ctx.setDentroDeCiclo(true);
        ctx.getTabla().entrarAmbito();
        for (NodoAST s : nodo.getCuerpo()) {
            validarSentencia(s, ctx);
        }
        ctx.getTabla().salirAmbito();
        ctx.setDentroDeCiclo(estabaEnCiclo);
    }

    private void validarHacerMientras(NodoHacerMientras nodo, Contexto ctx) {
        boolean estabaEnCiclo = ctx.isDentroDeCiclo();
        ctx.setDentroDeCiclo(true);
        ctx.getTabla().entrarAmbito();
        for (NodoAST s : nodo.getCuerpo()) {
            validarSentencia(s, ctx);
        }
        ctx.getTabla().salirAmbito();
        ctx.setDentroDeCiclo(estabaEnCiclo);
        validarCondicionBooleana(nodo.getCondicion(), ctx);
    }

    private void validarRetornar(NodoRetornar nodo, Contexto ctx) {
        boolean esperaValor = !(ctx.getTipoRetornoFuncionActual() instanceof TipoVacio);
        if (esperaValor && nodo.getExpresion() == null) {
            error(nodo, "La función debe retornar un valor de tipo " + ctx.getTipoRetornoFuncionActual());
            return;
        }
        if (!esperaValor && nodo.getExpresion() != null) {
            error(nodo, "La función no retorna valor, pero 'retornar' trae una expresión");
            return;
        }
        if (esperaValor) {
            TipoDato tipoValor = inferirTipo(nodo.getExpresion(), ctx);
            if (tipoValor != null && !this.isCompatibleAsignacion(ctx.getTipoRetornoFuncionActual(), tipoValor)) {
                error(nodo, "Se esperaba retornar " + ctx.getTipoRetornoFuncionActual() + " pero se retorna " + tipoValor);
            }
        }
    }

    private void validarCondicionBooleana(NodoAST condicion, Contexto ctx) {
        TipoDato tipo = this.inferirTipo(condicion, ctx);
        if (tipo != null && !this.esBooleano(tipo)) {
            this.error(condicion, "Se esperaba una condición de tipo booleano, se obtuvo " + tipo);
        }
    }

    private TipoDato inferirTipo(NodoAST nodo, Contexto ctx) {
        if (nodo instanceof NodoLiteral nodoLiteral) {
            return switch (nodoLiteral.getTipo()) {
                case ENTERO ->
                    new TipoPrimitivo(Primitivo.ENTERO);
                case FLOTANTE ->
                    new TipoPrimitivo(Primitivo.FLOTANTE);
                case CARACTER ->
                    new TipoPrimitivo(Primitivo.CARACTER);
                case CADENA ->
                    new TipoPrimitivo(Primitivo.CADENA);
                default ->
                    new TipoPrimitivo(Primitivo.BOOLEANO);
            };
        }
        if (nodo instanceof NodoUnario nodoUnario) {
            TipoDato tipoOperando = this.inferirTipo(nodoUnario.getOperando(), ctx);
            if (tipoOperando != null && !this.esBooleano(tipoOperando)) {
                this.error(nodo, "'!' solo aplica a valores booleanos, se obtuvo " + tipoOperando);
            }
            return new TipoPrimitivo(Primitivo.BOOLEANO);
        }
        if (nodo instanceof NodoBinario nodoBinario) {
            return this.inferirTipoBinaria(nodoBinario, ctx);
        }
        if (nodo instanceof NodoAccesoVariable nodoAccesoVariable) {
            return this.inferirTipoAcceso(nodoAccesoVariable, ctx);
        }
        if (nodo instanceof NodoLlamadaFuncion nodoLlamadaFuncion) {
            return this.inferirTipoLlamada(nodoLlamadaFuncion, ctx);
        }
        if (nodo instanceof NodoLeer) {
            return new TipoPrimitivo(Primitivo.CADENA);
        }
        return null;
    }

    private TipoDato inferirTipoBinaria(NodoBinario nodo, Contexto ctx) {
        TipoDato izq = this.inferirTipo(nodo.getIzquierda(), ctx);
        TipoDato der = this.inferirTipo(nodo.getDerecha(), ctx);
        String op = nodo.getOperador();

        switch (op) {
            case "+", "-", "*", "/" -> {
                if (izq == null || der == null) {
                    return null;
                }
                if (!this.esNumerico(izq) || !this.esNumerico(der)) {
                    this.error(nodo, "Operador '" + op + "' requiere operandos numéricos (se obtuvo " + izq + ", " + der + ")");
                    return new TipoPrimitivo(Primitivo.ENTERO);
                }
                boolean esFlotante = this.esTipoPrimitivo(izq, Primitivo.FLOTANTE)
                        || this.esTipoPrimitivo(der, Primitivo.FLOTANTE);
                return new TipoPrimitivo(esFlotante ? Primitivo.FLOTANTE : Primitivo.ENTERO);
            }
            case "<", ">", "<=", ">=" -> {
                if (izq != null && der != null && (!this.esNumerico(izq) || !this.esNumerico(der))) {
                    this.error(nodo, "Operador '" + op + "' requiere operandos numéricos");
                }
                return new TipoPrimitivo(Primitivo.BOOLEANO);
            }
            case "==", "!=" -> {
                if (izq != null && der != null && !this.isCompatibleAsignacion(izq, der) && !this.isCompatibleAsignacion(der, izq)) {
                    this.error(nodo, "No se pueden comparar tipos incompatibles: " + izq + " y " + der);
                }
                return new TipoPrimitivo(Primitivo.BOOLEANO);
            }
            case "&&", "||" -> {
                if (izq != null && !this.esBooleano(izq)) {
                    this.error(nodo, "'" + op + "' requiere operandos booleanos");
                }
                if (der != null && !this.esBooleano(der)) {
                    this.error(nodo, "'" + op + "' requiere operandos booleanos");
                }
                return new TipoPrimitivo(Primitivo.BOOLEANO);
            }

            default -> {
                return null;
            }
        }
    }

    private TipoDato inferirTipoAcceso(NodoAccesoVariable nodo, Contexto ctx) {
        TipoDato tipoActual = ctx.getTabla().buscar(nodo.getNombre());
        if (tipoActual == null) {
            this.error(nodo, "Variable no declarada: '" + nodo.getNombre() + "'");
            return null;
        }
        for (NodoAST sufijo : nodo.getSufijos()) {
            switch (sufijo) {
                case NodoSufijoIndice nodoSufijoIndice -> {
                    TipoDato tipoIndice = this.inferirTipo(nodoSufijoIndice.getIndice(), ctx);
                    if (tipoIndice != null && !this.esTipoPrimitivo(tipoIndice, Primitivo.ENTERO)) {
                        this.error(sufijo, "El índice de un arreglo debe ser entero");
                    }
                    if (!(tipoActual instanceof TipoArreglo)) {
                        this.error(sufijo, "'" + nodo.getNombre() + "' no es un arreglo, no se puede indexar");
                        return null;
                    }
                    tipoActual = ((TipoArreglo) tipoActual).getTipoBase();
                }
                case NodoSufijoCampo nodoSufijoCampo -> {
                    String nombreCampo = nodoSufijoCampo.getNombreCampo();
                    if (!(tipoActual instanceof TipoEstructura)) {
                        this.error(sufijo, "No se puede acceder al campo '" + nombreCampo + "': el tipo actual no es una estructura");
                        return null;
                    }
                    DescriptorCampo campo = ((TipoEstructura) tipoActual).getDescriptor().buscarCampo(nombreCampo);
                    if (campo == null) {
                        this.error(sufijo, "La estructura '" + ((TipoEstructura) tipoActual).getDescriptor().getNombre()
                                + "' no tiene un campo '" + nombreCampo + "'");
                        return null;
                    }
                    tipoActual = campo.getTipo();
                }
                default -> {
                }
            }
        }
        return tipoActual;
    }

    private TipoDato inferirTipoLlamada(NodoLlamadaFuncion nodo, Contexto ctx) {
        List<DescriptorFuncion> sobrecargas = ctx.getFuncionesVisibles().get(nodo.getNombre());
        if (sobrecargas == null || sobrecargas.isEmpty()) {
            this.error(nodo, "Función no declarada: '" + nodo.getNombre() + "'");
            return null;
        }
        List<TipoDato> tiposArgumentos = new ArrayList<>();
        for (NodoAST arg : nodo.getArgumentos()) {
            tiposArgumentos.add(this.inferirTipo(arg, ctx));
        }
        for (DescriptorFuncion firma : sobrecargas) {
            if (firma.getParametros().size() != tiposArgumentos.size()) {
                continue;
            }
            boolean coincide = true;
            for (int i = 0; i < tiposArgumentos.size(); i++) {
                TipoDato esperado = firma.getParametros().get(i).getTipo();
                TipoDato recibido = tiposArgumentos.get(i);
                if (recibido != null && !this.isCompatibleAsignacion(esperado, recibido)) {
                    coincide = false;
                    break;
                }
            }
            if (coincide) {
                return firma.getTipoRetorno();
            }
        }
        this.error(nodo, "Ninguna sobrecarga de '" + nodo.getNombre() + "' coincide con los argumentos dados");
        return null;
    }

    private TipoDato resolverTipoTexto(String texto, Map<String, DescriptorEstructura> estructuras, int linea) {
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

    private boolean esNumerico(TipoDato tipo) {
        return this.esTipoPrimitivo(tipo, Primitivo.ENTERO) || this.esTipoPrimitivo(tipo, Primitivo.FLOTANTE);
    }

    private boolean esBooleano(TipoDato tipo) {
        return this.esTipoPrimitivo(tipo, Primitivo.BOOLEANO);
    }

    private boolean esTipoPrimitivo(TipoDato tipo, Primitivo cual) {
        return tipo instanceof TipoPrimitivo && ((TipoPrimitivo) tipo).getPrimitivo() == cual;
    }

    private boolean isCompatibleAsignacion(TipoDato destino, TipoDato origen) {
        if (destino instanceof TipoPrimitivo && origen instanceof TipoPrimitivo) {
            Primitivo d = ((TipoPrimitivo) destino).getPrimitivo();
            Primitivo o = ((TipoPrimitivo) origen).getPrimitivo();
            if (d == o) {
                return true;
            }
            return d == Primitivo.FLOTANTE && o == Primitivo.ENTERO;
        }
        if (destino instanceof TipoEstructura && origen instanceof TipoEstructura) {
            return ((TipoEstructura) destino).getDescriptor().getNombre()
                    .equals(((TipoEstructura) origen).getDescriptor().getNombre());
        }
        if (destino instanceof TipoArreglo && origen instanceof TipoArreglo) {
            return ((TipoArreglo) destino).getSize() == ((TipoArreglo) origen).getSize()
                    && this.isCompatibleAsignacion(((TipoArreglo) destino).getTipoBase(), ((TipoArreglo) origen).getTipoBase());
        }
        return false;
    }

    private void error(NodoAST nodo, String mensaje) {
        this.coleccionErrores.addSemanticErrors(nodo.getLinea(), COLUMNA_PLACEHOLDER, mensaje);
    }

}
