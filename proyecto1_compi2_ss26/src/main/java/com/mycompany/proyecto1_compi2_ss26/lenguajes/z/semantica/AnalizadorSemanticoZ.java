/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.lenguajes.z.semantica;

import com.mycompany.proyecto1_compi2_ss26.ast.NodoAST;
import com.mycompany.proyecto1_compi2_ss26.ast.expresiones.*;
import com.mycompany.proyecto1_compi2_ss26.ast.sentencias.*;
import com.mycompany.proyecto1_compi2_ss26.ast.z.NodoArchivoZ;
import com.mycompany.proyecto1_compi2_ss26.ast.z.expresiones.*;
import com.mycompany.proyecto1_compi2_ss26.ast.z.sentencias.*;
import com.mycompany.proyecto1_compi2_ss26.errores.RecolectorErrores;
import com.mycompany.proyecto1_compi2_ss26.modelos.Primitivo;
import com.mycompany.proyecto1_compi2_ss26.modelos.TipoLiteral;
import com.mycompany.proyecto1_compi2_ss26.tipos.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author david
 */
public class AnalizadorSemanticoZ {

    private static final int COLUMNA_PLACEHOLDER = 1;

    private final RecolectorErrores coleccionErrores;

    public AnalizadorSemanticoZ(RecolectorErrores coleccionErrores) {
        this.coleccionErrores = coleccionErrores;
    }

    public CatalogoExportado analizar(String rutaArchivo, NodoArchivoZ archivo) {
        validarNombreArchivo(rutaArchivo, archivo);

        RegistradorClasesZ registrador = new RegistradorClasesZ(coleccionErrores, new TablaTiposClase());
        DescriptorClase clase = registrador.registrar(archivo);

        for (NodoConstructorZ constructorAST : archivo.getConstructores()) {
            analizarCuerpo(constructorAST.getParametros(), constructorAST.getCuerpo(), clase, TipoVacio.INSTANCIA);
        }
        for (NodoFuncion metodoAST : archivo.getMetodos()) {
            TipoDato tipoRetorno = resolverTipoRetorno(metodoAST.getTipoRetornoTexto(), clase, metodoAST.getLinea());
            if (tipoRetorno == null) {
                tipoRetorno = TipoVacio.INSTANCIA; // ya se reportó el error dentro
            }
            analizarCuerpo(metodoAST.getParametros(), metodoAST.getCuerpo(), clase, tipoRetorno);
        }

        CatalogoExportado catalogo = new CatalogoExportado(rutaArchivo, coleccionErrores.getErrors());
        catalogo.getClases().put(clase.getNombre(), clase);
        return catalogo;
    }

    private void analizarCuerpo(List<NodoParametro> parametrosAST, List<NodoAST> cuerpo,
            DescriptorClase clase, TipoDato tipoRetorno) {
        ContextoZ ctx = new ContextoZ(clase, tipoRetorno);
        for (NodoParametro p : parametrosAST) {
            TipoDato tipo = resolverTipoTexto(p.getTipoTexto(), clase, p.getLinea());
            if (tipo != null) {
                ctx.getTabla().declarar(p.getNombre(), tipo);
            }
        }
        for (NodoAST sentencia : cuerpo) {
            validarSentencia(sentencia, ctx);
        }
    }

    private void validarNombreArchivo(String rutaArchivo, NodoArchivoZ archivo) {
        if (rutaArchivo == null) {
            return; // sin ruta conocida (ej. pruebas aisladas en memoria): no se valida
        }
        String nombreBase = extraerNombreBase(rutaArchivo);
        if (!nombreBase.equals(archivo.getNombreClase())) {
            coleccionErrores.addSemanticErrors(archivo.getLinea(), COLUMNA_PLACEHOLDER,
                    "El nombre de la clase '" + archivo.getNombreClase()
                    + "' no coincide con el nombre del archivo '" + nombreBase + ".z'");
        }
    }

    private String extraerNombreBase(String rutaArchivo) {
        String nombre = rutaArchivo;
        int barra = Math.max(nombre.lastIndexOf('/'), nombre.lastIndexOf('\\'));
        if (barra >= 0) {
            nombre = nombre.substring(barra + 1);
        }
        int punto = nombre.lastIndexOf('.');
        if (punto >= 0) {
            nombre = nombre.substring(0, punto);
        }
        return nombre;
    }

    // =====================================================================
    // Validación de sentencias
    // =====================================================================
    private void validarSentencia(NodoAST nodo, ContextoZ ctx) {
        if (nodo instanceof NodoDeclaracionVariableZ nodoDeclaracionVariableZ) {
            validarDeclaracion(nodoDeclaracionVariableZ, ctx);
        } else if (nodo instanceof NodoAsignacionZ nodoAsignacionZ) {
            validarAsignacion(nodoAsignacionZ, ctx);
        } else if (nodo instanceof NodoIncDec nodoIncDec) {
            TipoDato tipo = inferirTipo(nodoIncDec.getDestino(), ctx);
            if (tipo != null && !esNumerico(tipo)) {
                error(nodo, "'++'/'--' solo aplica a variables numéricas");
            }
        } else if (nodo instanceof NodoAccesoVariableZ) {
            inferirTipo(nodo, ctx); // sentenciaLlamadaZ: valida existencia/firma, descarta el valor
        } else if (nodo instanceof NodoImprimirZ nodoImprimirZ) {
            inferirTipo(nodoImprimirZ.getExpresion(), ctx);
        } else if (nodo instanceof NodoLeer) {
            // sin validación adicional: readln() no requiere argumentos (mismo criterio que Y?)
        } else if (nodo instanceof NodoSiZ nodoSiZ) {
            validarSi(nodoSiZ, ctx);
        } else if (nodo instanceof NodoSwitch nodoSwitch) {
            validarSwitch(nodoSwitch, ctx);
        } else if (nodo instanceof NodoFor nodoFor) {
            validarPara(nodoFor, ctx);
        } else if (nodo instanceof NodoWhile nodoWhile) {
            validarWhile(nodoWhile, ctx);
        } else if (nodo instanceof NodoDoWhile nodoDoWhile) {
            validarDoWhile(nodoDoWhile, ctx);
        } else if (nodo instanceof NodoContinuar) {
            if (!ctx.isDentroDeCiclo()) {
                error(nodo, "'continue' usado fuera de un ciclo");
            }
        } else if (nodo instanceof NodoRomper) {
            if (!ctx.isDentroDeCiclo() && !ctx.isDentroDeSwitch()) {
                error(nodo, "'break' usado fuera de un ciclo o 'switch'");
            }
        } else if (nodo instanceof NodoRetornar nodoRetornar) {
            validarRetornar(nodoRetornar, ctx);
        }
    }

    private void validarDeclaracion(NodoDeclaracionVariableZ nodo, ContextoZ ctx) {
        TipoDato tipo = resolverTipoTexto(nodo.getTipoBaseTexto(), ctx.getClase(), nodo.getLinea());
        if (tipo == null) {
            return;
        }

        for (NodoDeclaradorZ declarador : nodo.getDeclaradores()) {
            if (!ctx.getTabla().declarar(declarador.getNombre(), tipo)) {
                error(declarador, "Variable '" + declarador.getNombre() + "' ya declarada en este ámbito");
                continue;
            }
            NodoAST init = declarador.getInicializador();
            if (init instanceof NodoLiteralCompuesto nodoLiteralCompuesto) {
                validarLiteralCompuesto(nodoLiteralCompuesto, tipo, ctx);
            } else if (init != null) {
                TipoDato tipoInit = inferirTipo(init, ctx);
                if (tipoInit != null && !compatibleAsignacion(tipo, tipoInit)) {
                    error(declarador, "No se puede inicializar '" + declarador.getNombre() + "' (" + tipo
                            + ") con un valor de tipo " + tipoInit);
                }
            }
        }
    }

    private void validarLiteralCompuesto(NodoLiteralCompuesto literal, TipoDato tipoEsperado, ContextoZ ctx) {
        if (!(tipoEsperado instanceof TipoArregloZ)) {
            error(literal, "No se puede usar un literal compuesto '{...}' para inicializar un tipo " + tipoEsperado);
            return;
        }
        // A diferencia de Y? (arreglo de tamaño fijo declarado), en Zetariano un arreglo NO declara
        // tamaño en el tipo — el tamaño lo define el propio literal, así que no hay tamaño esperado
        // contra el cual comparar 'literal.getValores().size()'.
        TipoDato tipoElemento = ((TipoArregloZ) tipoEsperado).getTipoBase();
        for (NodoAST valor : literal.getValores()) {
            TipoDato tipoValor = inferirTipo(valor, ctx);
            if (tipoValor != null && !compatibleAsignacion(tipoElemento, tipoValor)) {
                error(valor, "Elemento de tipo " + tipoValor + " no compatible con " + tipoElemento);
            }
        }
    }

    private void validarAsignacion(NodoAsignacionZ nodo, ContextoZ ctx) {
        TipoDato tipoDestino = inferirTipo(nodo.getDestino(), ctx);
        TipoDato tipoValor = inferirTipo(nodo.getValor(), ctx);
        if (tipoDestino == null || tipoValor == null) {
            return;
        }

        if (nodo.getOperador().equals("=")) {
            if (!compatibleAsignacion(tipoDestino, tipoValor)) {
                error(nodo, "No se puede asignar un valor de tipo " + tipoValor + " a un destino de tipo " + tipoDestino);
            }
            return;
        }
        // Asignación compuesta (+= -= *= /= %=): ambos lados deben ser numéricos.
        if (!esNumerico(tipoDestino) || !esNumerico(tipoValor)) {
            error(nodo, "'" + nodo.getOperador() + "' requiere operandos numéricos (se obtuvo " + tipoDestino
                    + ", " + tipoValor + ")");
        }
    }

    private void validarSi(NodoSiZ nodo, ContextoZ ctx) {
        List<NodoAST> condiciones = nodo.getCondiciones();
        List<List<NodoAST>> bloques = nodo.getBloques();
        for (int i = 0; i < condiciones.size(); i++) {
            validarCondicionBooleana(condiciones.get(i), ctx);
            ctx.getTabla().entrarAmbito();
            for (NodoAST s : bloques.get(i)) {
                validarSentencia(s, ctx);
            }
            ctx.getTabla().salirAmbito();
        }
        if (nodo.getBloqueContrario() != null) {
            ctx.getTabla().entrarAmbito();
            for (NodoAST s : nodo.getBloqueContrario()) {
                validarSentencia(s, ctx);
            }
            ctx.getTabla().salirAmbito();
        }
    }

    private void validarSwitch(NodoSwitch nodo, ContextoZ ctx) {
        TipoDato tipoEvaluado = inferirTipo(nodo.getValorEvaluado(), ctx);
        boolean estabaEnSwitch = ctx.isDentroDeSwitch();
        ctx.setDentroDeSwitch(true);

        // Todos los 'case' comparten UN MISMO ámbito, no uno por caso
        // (igual que en Java) - una variable declarada en un caso sigue visible en los siguientes.
        ctx.getTabla().entrarAmbito();
        for (NodoCaso caso : nodo.getCasos()) {
            TipoDato tipoCaso = inferirTipo(caso.getValorLiteral(), ctx);
            if (tipoEvaluado != null && tipoCaso != null && !compatibleAsignacion(tipoEvaluado, tipoCaso)
                    && !compatibleAsignacion(tipoCaso, tipoEvaluado)) {
                error(caso, "El tipo del 'case' (" + tipoCaso + ") no es compatible con lo evaluado en 'switch' (" + tipoEvaluado + ")");
            }
            for (NodoAST s : caso.getCuerpo()) {
                validarSentencia(s, ctx);
            }
        }
        if (nodo.getPorDefecto() != null) {
            for (NodoAST s : nodo.getPorDefecto().getCuerpo()) {
                validarSentencia(s, ctx);
            }
        }
        ctx.getTabla().salirAmbito();

        ctx.setDentroDeSwitch(estabaEnSwitch);
    }

    private void validarPara(NodoFor nodo, ContextoZ ctx) {
        ctx.getTabla().entrarAmbito();
        if (nodo.getInit() != null) {
            validarSentencia(nodo.getInit(), ctx);
        }
        if (nodo.getCondicion() != null) {
            validarCondicionBooleana(nodo.getCondicion(), ctx);
        }
        if (nodo.getActualizacion() != null) {
            validarSentencia(nodo.getActualizacion(), ctx);
        }

        boolean estabaEnCiclo = ctx.isDentroDeCiclo();
        ctx.setDentroDeCiclo(true);
        for (NodoAST s : nodo.getCuerpo()) {
            validarSentencia(s, ctx);
        }
        ctx.setDentroDeCiclo(estabaEnCiclo);

        ctx.getTabla().salirAmbito();
    }

    private void validarWhile(NodoWhile nodo, ContextoZ ctx) {
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

    private void validarDoWhile(NodoDoWhile nodo, ContextoZ ctx) {
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

    private void validarRetornar(NodoRetornar nodo, ContextoZ ctx) {
        boolean esperaValor = !(ctx.getTipoRetornoActual() instanceof TipoVacio);
        if (esperaValor && nodo.getExpresion() == null) {
            error(nodo, "Se debe retornar un valor de tipo " + ctx.getTipoRetornoActual());
            return;
        }
        if (!esperaValor && nodo.getExpresion() != null) {
            error(nodo, "El método/constructor no retorna valor, pero 'return' trae una expresión");
            return;
        }
        if (esperaValor) {
            TipoDato tipoValor = inferirTipo(nodo.getExpresion(), ctx);
            if (tipoValor != null && !compatibleAsignacion(ctx.getTipoRetornoActual(), tipoValor)) {
                error(nodo, "Se esperaba retornar " + ctx.getTipoRetornoActual() + " pero se retorna " + tipoValor);
            }
        }
    }

    private void validarCondicionBooleana(NodoAST condicion, ContextoZ ctx) {
        TipoDato tipo = inferirTipo(condicion, ctx);
        if (tipo != null && !esBooleano(tipo)) {
            error(condicion, "Se esperaba una condición de tipo booleano, se obtuvo " + tipo);
        }
    }

    // =====================================================================
    // Inferencia de tipos de expresiones
    // =====================================================================
    private TipoDato inferirTipo(NodoAST nodo, ContextoZ ctx) {
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
                case NULO ->
                    TipoNulo.INSTANCIA;
                default ->
                    new TipoPrimitivo(Primitivo.BOOLEANO);
            };
        }
        if (nodo instanceof NodoNuloZ) {
            return TipoNulo.INSTANCIA;
        }
        if (nodo instanceof NodoUnario nodoUnario) {
            return inferirTipoUnaria(nodoUnario, ctx);
        }
        if (nodo instanceof NodoBinario nodoBinario) {
            return inferirTipoBinaria(nodoBinario, ctx);
        }
        if (nodo instanceof NodoTernarioZ nodoTernarioZ) {
            return inferirTipoTernario(nodoTernarioZ, ctx);
        }
        if (nodo instanceof NodoAccesoVariableZ nodoAccesoVariableZ) {
            return inferirTipoAcceso(nodoAccesoVariableZ, ctx);
        }
        if (nodo instanceof NodoLeer) {
            return new TipoPrimitivo(Primitivo.CADENA);
        }
        if (nodo instanceof NodoNuevoObjetoZ nodoNuevoObjetoZ) {
            return inferirTipoNuevoObjeto(nodoNuevoObjetoZ, ctx);
        }
        if (nodo instanceof NodoNuevoArregloZ nodoNuevoArregloZ) {
            return inferirTipoNuevoArreglo(nodoNuevoArregloZ, ctx);
        }
        return null; // NodoLiteralCompuesto y otros se validan aparte (contexto-dependientes)
    }

    private TipoDato inferirTipoUnaria(NodoUnario nodo, ContextoZ ctx) {
        TipoDato tipoOperando = inferirTipo(nodo.getOperando(), ctx);
        if (nodo.getOperador().equals("!")) {
            if (tipoOperando != null && !esBooleano(tipoOperando)) {
                error(nodo, "'!' solo aplica a valores booleanos, se obtuvo " + tipoOperando);
            }
            return new TipoPrimitivo(Primitivo.BOOLEANO);
        }
        // Menos unario ("-"): SÍ existe en Zetariano, a diferencia de Y? (confirmado).
        if (tipoOperando != null && !esNumerico(tipoOperando)) {
            error(nodo, "'-' unario solo aplica a valores numéricos, se obtuvo " + tipoOperando);
            return new TipoPrimitivo(Primitivo.ENTERO);
        }
        return tipoOperando != null ? tipoOperando : new TipoPrimitivo(Primitivo.ENTERO);
    }

    private TipoDato inferirTipoBinaria(NodoBinario nodo, ContextoZ ctx) {
        TipoDato izq = inferirTipo(nodo.getIzquierda(), ctx);
        TipoDato der = inferirTipo(nodo.getDerecha(), ctx);
        String op = nodo.getOperador();

        switch (op) {
            case "+", "-", "*", "/", "%" -> {
                if (izq == null || der == null) {
                    return null;
                }
                if (!esNumerico(izq) || !esNumerico(der)) {
                    error(nodo, "Operador '" + op + "' requiere operandos numéricos (se obtuvo " + izq + ", " + der + ")");
                    return new TipoPrimitivo(Primitivo.ENTERO);
                }
                boolean esFlotante = esTipoPrimitivo(izq, Primitivo.FLOTANTE)
                        || esTipoPrimitivo(der, Primitivo.FLOTANTE);
                return new TipoPrimitivo(esFlotante ? Primitivo.FLOTANTE : Primitivo.ENTERO);
            }
            case "<", ">", "<=", ">=" -> {
                if (izq != null && der != null && (!esNumerico(izq) || !esNumerico(der))) {
                    error(nodo, "Operador '" + op + "' requiere operandos numéricos");
                }
                return new TipoPrimitivo(Primitivo.BOOLEANO);
            }
            case "==", "!=" -> {
                if (izq != null && der != null && !compatibleAsignacion(izq, der) && !compatibleAsignacion(der, izq)) {
                    error(nodo, "No se pueden comparar tipos incompatibles: " + izq + " y " + der);
                }
                return new TipoPrimitivo(Primitivo.BOOLEANO);
            }
            case "&&", "||" -> {
                if (izq != null && !esBooleano(izq)) {
                    error(nodo, "'" + op + "' requiere operandos booleanos");
                }
                if (der != null && !esBooleano(der)) {
                    error(nodo, "'" + op + "' requiere operandos booleanos");
                }
                return new TipoPrimitivo(Primitivo.BOOLEANO);
            }
            default -> {
                return null;
            }
        }
    }

    private TipoDato inferirTipoTernario(NodoTernarioZ nodo, ContextoZ ctx) {
        validarCondicionBooleana(nodo.getCondicion(), ctx);
        TipoDato tipoV = inferirTipo(nodo.getSiVerdadero(), ctx);
        TipoDato tipoF = inferirTipo(nodo.getSiFalso(), ctx);
        if (tipoV == null || tipoF == null) {
            return tipoV != null ? tipoV : tipoF;
        }
        if (compatibleAsignacion(tipoV, tipoF)) {
            return tipoV;
        }
        if (compatibleAsignacion(tipoF, tipoV)) {
            return tipoF;
        }
        error(nodo, "Las dos ramas del ternario tienen tipos incompatibles: " + tipoV + " y " + tipoF);
        return tipoV;
    }

    private TipoDato inferirTipoNuevoObjeto(NodoNuevoObjetoZ nodo, ContextoZ ctx) {
        if (!nodo.getNombreClase().equals(ctx.getClase().getNombre())) {
            error(nodo, "Clase desconocida: '" + nodo.getNombreClase()
                    + "' (instanciar clases de otro archivo .z aún no está soportado — Día 22)");
            return null;
        }
        List<TipoDato> tiposArgumentos = new ArrayList<>();
        for (NodoAST arg : nodo.getArgumentos()) {
            tiposArgumentos.add(inferirTipo(arg, ctx));
        }
        for (DescriptorConstructor firma : ctx.getClase().getConstructores()) {
            if (coincideFirma(firma.getParametros(), tiposArgumentos)) {
                return new TipoObjeto(ctx.getClase());
            }
        }
        error(nodo, "Ningún constructor de '" + ctx.getClase().getNombre() + "' coincide con los argumentos dados");
        return new TipoObjeto(ctx.getClase());
    }

    private TipoDato inferirTipoNuevoArreglo(NodoNuevoArregloZ nodo, ContextoZ ctx) {
        TipoDato tipoBase = resolverTipoTexto(nodo.getTipoBaseTexto(), ctx.getClase(), nodo.getLinea());
        if (tipoBase == null) {
            return null;
        }

        // Se construye de la dimensión MÁS INTERNA hacia afuera: la última expresión de
        // 'dimensiones' es el tamaño del arreglo más anidado.
        TipoDato tipoActual = tipoBase;
        for (int i = nodo.getDimensiones().size() - 1; i >= 0; i--) {
            NodoAST dimExpr = nodo.getDimensiones().get(i);
            TipoDato tipoDim = inferirTipo(dimExpr, ctx);
            if (tipoDim != null && !esTipoPrimitivo(tipoDim, Primitivo.ENTERO)) {
                error(dimExpr, "El tamaño de un arreglo debe ser entero, se obtuvo " + tipoDim);
            }
            Integer tamano = intentarResolverEnteroConstante(dimExpr);
            tipoActual = new TipoArregloZ(tipoActual, tamano);
        }
        return tipoActual;
    }

    /**
     * Solo reconoce un literal entero DIRECTO como constante. No resuleve
     * expresiones (ej. '2+3'). Si no es un literal, retorna null (tamaño no
     * resuelto - no es un error).
     */
    private Integer intentarResolverEnteroConstante(NodoAST expr) {
        if (expr instanceof NodoLiteral && ((NodoLiteral) expr).getTipo() == TipoLiteral.ENTERO) {
            try {
                return Integer.valueOf(((NodoLiteral) expr).getTextoOriginal());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private TipoDato inferirTipoAcceso(NodoAccesoVariableZ nodo, ContextoZ ctx) {
        List<NodoSegmentoAccesoZ> segmentos = nodo.getSegmentos();
        if (segmentos.isEmpty()) {
            return null;
        }

        TipoDato tipoActual = resolverPrimerSegmento(segmentos.get(0), ctx);
        if (tipoActual == null) {
            return null;
        }
        tipoActual = aplicarIndices(segmentos.get(0), tipoActual, ctx);
        if (tipoActual == null) {
            return null;
        }

        for (int i = 1; i < segmentos.size(); i++) {
            NodoSegmentoAccesoZ segmento = segmentos.get(i);
            tipoActual = resolverSegmentoEncadenado(segmento, tipoActual, ctx);
            if (tipoActual == null) {
                return null;
            }
            tipoActual = aplicarIndices(segmento, tipoActual, ctx);
            if (tipoActual == null) {
                return null;
            }
        }
        return tipoActual;
    }

    private TipoDato aplicarIndices(NodoSegmentoAccesoZ segmento, TipoDato tipoActual, ContextoZ ctx) {
        for (NodoAST indiceAST : segmento.getIndices()) {
            TipoDato tipoIndice = inferirTipo(indiceAST, ctx);
            if (tipoIndice != null && !esTipoPrimitivo(tipoIndice, Primitivo.ENTERO)) {
                error(indiceAST, "El índice de un arreglo debe ser entero");
            }
            if (!(tipoActual instanceof TipoArregloZ)) {
                error(segmento, "'" + segmento.getNombre() + "' no es un arreglo, no se puede indexar");
                return null;
            }
            tipoActual = ((TipoArregloZ) tipoActual).getTipoBase();
        }
        return tipoActual;
    }

    /**
     * Sombra confirmada: variable local/parámetro tiene precedencia sobre
     * atributo de la clase.
     */
    private TipoDato resolverPrimerSegmento(NodoSegmentoAccesoZ segmento, ContextoZ ctx) {
        TipoDato tipoLocal = ctx.getTabla().buscar(segmento.getNombre());
        if (tipoLocal != null) {
            if (segmento.esLlamada()) {
                error(segmento, "'" + segmento.getNombre() + "' es una variable, no se puede llamar como método");
                return null;
            }
            return tipoLocal;
        }

        if (segmento.esLlamada()) {
            return resolverLlamadaMetodo(segmento, ctx.getClase(), ctx);
        }

        DescriptorAtributo atributo = buscarAtributo(ctx.getClase(), segmento.getNombre());
        if (atributo == null) {
            error(segmento, "Variable o atributo no declarado: '" + segmento.getNombre() + "'");
            return null;
        }
        return atributo.getTipo();
    }

    private TipoDato resolverSegmentoEncadenado(NodoSegmentoAccesoZ segmento, TipoDato tipoAnterior, ContextoZ ctx) {
        if (!(tipoAnterior instanceof TipoObjeto)) {
            error(segmento, "No se puede acceder a '." + segmento.getNombre() + "': el valor anterior no es un objeto");
            return null;
        }
        DescriptorClase clase = ((TipoObjeto) tipoAnterior).getDescriptor();
        if (segmento.esLlamada()) {
            return resolverLlamadaMetodo(segmento, clase, ctx);
        }
        DescriptorAtributo atributo = buscarAtributo(clase, segmento.getNombre());
        if (atributo == null) {
            error(segmento, "La clase '" + clase.getNombre() + "' no tiene un atributo '" + segmento.getNombre() + "'");
            return null;
        }
        return atributo.getTipo();
    }

    private TipoDato resolverLlamadaMetodo(NodoSegmentoAccesoZ segmento, DescriptorClase clase, ContextoZ ctx) {
        List<TipoDato> tiposArgumentos = new ArrayList<>();
        for (NodoAST arg : segmento.getArgumentos()) {
            tiposArgumentos.add(inferirTipo(arg, ctx));
        }
        boolean existeAlgunMetodoConEseNombre = false;
        for (DescriptorMetodo metodo : clase.getMetodos()) {
            if (!metodo.getNombre().equals(segmento.getNombre())) {
                continue;
            }
            existeAlgunMetodoConEseNombre = true;
            if (coincideFirma(metodo.getParametros(), tiposArgumentos)) {
                return metodo.getTipoRetorno();
            }
        }
        if (existeAlgunMetodoConEseNombre) {
            error(segmento, "Ninguna sobrecarga de '" + segmento.getNombre() + "' en '" + clase.getNombre()
                    + "' coincide con los argumentos dados");
        } else {
            error(segmento, "Método no declarado: '" + segmento.getNombre() + "' en la clase '" + clase.getNombre() + "'");
        }
        return null;
    }

    private boolean coincideFirma(List<DescriptorParametro> parametros, List<TipoDato> tiposArgumentos) {
        if (parametros.size() != tiposArgumentos.size()) {
            return false;
        }
        for (int i = 0; i < tiposArgumentos.size(); i++) {
            TipoDato esperado = parametros.get(i).getTipo();
            TipoDato recibido = tiposArgumentos.get(i);
            if (recibido != null && !compatibleAsignacion(esperado, recibido)) {
                return false;
            }
        }
        return true;
    }

    private DescriptorAtributo buscarAtributo(DescriptorClase clase, String nombre) {
        for (DescriptorAtributo a : clase.getAtributos()) {
            if (a.getNombre().equals(nombre)) {
                return a;
            }
        }
        return null;
    }

    // =====================================================================
    // Utilidades de tipo
    // =====================================================================
    private TipoDato resolverTipoRetorno(String texto, DescriptorClase clase, int linea) {
        if (texto.equals("void")) {
            return TipoVacio.INSTANCIA;
        }
        return resolverTipoTexto(texto, clase, linea);
    }

    /**
     * Duplicado deliberadamente de RegistradorClasesZ (mismo patrón que Y?:
     * cada analizador resuelve sus propios tipos por texto de forma
     * independiente del registrador).
     */
    private TipoDato resolverTipoTexto(String texto, DescriptorClase clase, int linea) {
        int dimensiones = 0;
        String base = texto;
        while (base.endsWith("[]")) {
            dimensiones++;
            base = base.substring(0, base.length() - 2);
        }
        TipoDato tipoBase = resolverTipoBase(base, clase, linea);
        if (tipoBase == null) {
            return null;
        }
        TipoDato tipoFinal = tipoBase;
        for (int i = 0; i < dimensiones; i++) {
            tipoFinal = new TipoArregloZ(tipoFinal, null);
        }
        return tipoFinal;
    }

    private TipoDato resolverTipoBase(String texto, DescriptorClase clase, int linea) {
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
                if (texto.equals(clase.getNombre())) {
                    return new TipoObjeto(clase);
                }
                coleccionErrores.addSemanticErrors(linea, COLUMNA_PLACEHOLDER,
                        "Tipo desconocido: '" + texto + "' (clases de otro archivo .z aún no soportadas — Día 22)");
                return null;
            }
        }
    }

    private boolean esNumerico(TipoDato tipo) {
        return esTipoPrimitivo(tipo, Primitivo.ENTERO) || esTipoPrimitivo(tipo, Primitivo.FLOTANTE);
    }

    private boolean esBooleano(TipoDato tipo) {
        return esTipoPrimitivo(tipo, Primitivo.BOOLEANO);
    }

    private boolean esTipoPrimitivo(TipoDato tipo, Primitivo cual) {
        return tipo instanceof TipoPrimitivo && ((TipoPrimitivo) tipo).getPrimitivo() == cual;
    }

    /**
     * Compatibilidad de asignación 'destino = origen'. - Primitivos: idéntico,
     * EXCEPTO ENTERO -> FLOTANTE - Objetos: mismo nombre de clase. - Arreglos:
     * mismo tipoBase recursivamente, el TAMAÑO NUNCA se compara - 'null'
     * (TipoNulo): compatible con TipoObjeto y TipoArregloZ
     */
    private boolean compatibleAsignacion(TipoDato destino, TipoDato origen) {
        if (origen instanceof TipoNulo) {
            return destino instanceof TipoObjeto || destino instanceof TipoArregloZ;
        }
        if (destino instanceof TipoPrimitivo && origen instanceof TipoPrimitivo) {
            Primitivo d = ((TipoPrimitivo) destino).getPrimitivo();
            Primitivo o = ((TipoPrimitivo) origen).getPrimitivo();
            if (d == o) {
                return true;
            }
            return d == Primitivo.FLOTANTE && o == Primitivo.ENTERO;
        }
        if (destino instanceof TipoObjeto && origen instanceof TipoObjeto) {
            return ((TipoObjeto) destino).getDescriptor().getNombre()
                    .equals(((TipoObjeto) origen).getDescriptor().getNombre());
        }
        if (destino instanceof TipoArregloZ && origen instanceof TipoArregloZ) {
            return compatibleAsignacion(((TipoArregloZ) destino).getTipoBase(), ((TipoArregloZ) origen).getTipoBase());
        }
        return false;
    }

    private void error(NodoAST nodo, String mensaje) {
        coleccionErrores.addSemanticErrors(nodo.getLinea(), COLUMNA_PLACEHOLDER, mensaje);
    }

}
