/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.lenguajes.y;

import com.mycompany.YBaseVisitor;
import com.mycompany.YParser;
import com.mycompany.proyecto1_compi2_ss26.ast.NodoAST;
import com.mycompany.proyecto1_compi2_ss26.ast.expresiones.*;
import com.mycompany.proyecto1_compi2_ss26.ast.sentencias.*;
import com.mycompany.proyecto1_compi2_ss26.ast.y.NodoArchivoY;
import com.mycompany.proyecto1_compi2_ss26.ast.y.sentencias.*;
import com.mycompany.proyecto1_compi2_ss26.modelos.ModoPaso;
import com.mycompany.proyecto1_compi2_ss26.modelos.TipoLiteral;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 *
 * @author david
 */
public class ConstructorASTY extends YBaseVisitor<NodoAST> {

    @Override
    public NodoAST visitArchivoY(YParser.ArchivoYContext ctx) {
        List<NodoEstructura> estructuras = new ArrayList<>();
        if (ctx.seccionEstructuras() != null) {
            for (YParser.EstructuraContext e : ctx.seccionEstructuras().estructura()) {
                estructuras.add((NodoEstructura) visitEstructura(e));
            }
        }
        List<NodoFuncionY> funciones = ctx.seccionFunciones().funcion().stream()
                .map(f -> (NodoFuncionY) visitFuncion(f))
                .collect(Collectors.toList());
        return new NodoArchivoY(estructuras, funciones, ctx.getStart().getLine());
    }

    @Override
    public NodoAST visitEstructura(YParser.EstructuraContext ctx) {
        List<NodoCampoEstructura> campos = ctx.campoEstructura().stream()
                .map(c -> (NodoCampoEstructura) visitCampoEstructura(c))
                .collect(Collectors.toList());
        return new NodoEstructura(ctx.ID().getText(), campos, ctx.getStart().getLine());
    }

    @Override
    public NodoAST visitCampoEstructura(YParser.CampoEstructuraContext ctx) {
        NodoAST size = ctx.expresionConstante() != null ? visit(ctx.expresionConstante()) : null;
        return new NodoCampoEstructura(ctx.tipoDato().getText(), ctx.ID().getText(), size, ctx.getStart().getLine());
    }

    @Override
    public NodoAST visitFuncion(YParser.FuncionContext ctx) {
        List<NodoParametro> parametros = new ArrayList<>();
        if (ctx.listaParametros() != null) {
            for (YParser.ParametroContext p : ctx.listaParametros().parametro()) {
                parametros.add(this.construirParametro(p));
            }
        }
        String tipoRetorno = ctx.tipoDato() != null ? ctx.tipoDato().getText() : null;
        List<NodoAST> cuerpo = ctx.bloqueSentencias().sentencia().stream().map(this::visit).collect(Collectors.toList());
        return new NodoFuncionY(ctx.ID().getText(), parametros, tipoRetorno, cuerpo, ctx.getStart().getLine());
    }

    private NodoParametro construirParametro(YParser.ParametroContext ctx) {
        int linea = ctx.getStart().getLine();
        if (ctx.COR_ABRE() != null) {
            // [] tipoDato ID -> arreglo por referencia
            return new NodoParametro(ctx.tipoDato().getText(), ctx.ID(0).getText(), ModoPaso.POR_REFERENCIA, linea);
        } else if (ctx.LLA_ABRE() != null) {
            // {} ID ID -> estructura por referencia (primer ID = tipo, segundo ID = nombre)
            return new NodoParametro(ctx.ID(0).getText(), ctx.ID(1).getText(), ModoPaso.POR_REFERENCIA, linea);
        } else {
            // tipoDato ID -> primitivo por valor
            return new NodoParametro(ctx.tipoDato().getText(), ctx.ID(0).getText(), ModoPaso.POR_VALOR, linea);
        }
    }

    @Override
    public NodoAST visitDeclaracionVariable(YParser.DeclaracionVariableContext ctx) {
        List<NodoAST> dimensiones = ctx.expresionConstante().stream()
                .map(this::visit).collect(Collectors.toList());
        NodoAST inicializador = null;
        if (ctx.literalCompuesto() != null) {
            inicializador = visitLiteralCompuesto(ctx.literalCompuesto());
        } else if (ctx.expresion() != null) {
            inicializador = visit(ctx.expresion());
        }
        return new NodoDeclaracionVariable(ctx.tipoDato().getText(), ctx.ID().getText(), dimensiones, inicializador, ctx.getStart().getLine());
    }

    @Override
    public NodoAST visitLiteralCompuesto(YParser.LiteralCompuestoContext ctx) {
        List<NodoAST> valores = ctx.expresion().stream().map(this::visit).collect(Collectors.toList());
        return new NodoLiteralCompuesto(valores, ctx.getStart().getLine());
    }

    @Override
    public NodoAST visitAsignacion(YParser.AsignacionContext ctx) {
        NodoAccesoVariable destino = (NodoAccesoVariable) visitAccesoVariable(ctx.accesoVariable());
        NodoAST valor = visit(ctx.expresion());
        return new NodoAsignacion(destino, valor, ctx.getStart().getLine());
    }

    @Override
    public NodoAST visitIncrementoDecremento(YParser.IncrementoDecrementoContext ctx) {
        NodoAccesoVariable destino = (NodoAccesoVariable) visitAccesoVariable(ctx.accesoVariable());
        return new NodoIncrementoDecremento(destino, ctx.op.getText(), ctx.getStart().getLine());
    }

    @Override
    public NodoAST visitAccesoVariable(YParser.AccesoVariableContext ctx) {
        List<NodoAST> sufijos = new ArrayList<>();
        for (YParser.SufijoAccesoContext s : ctx.sufijoAcceso()) {
            if (s.expresion() != null) {
                sufijos.add(new NodoSufijoIndice(visit(s.expresion()), s.getStart().getLine()));
            } else {
                sufijos.add(new NodoSufijoCampo(s.ID().getText(), s.getStart().getLine()));
            }
        }
        return new NodoAccesoVariable(ctx.ID().getText(), sufijos, ctx.getStart().getLine());
    }

    @Override
    public NodoAST visitNegacionExpr(YParser.NegacionExprContext ctx) {
        return new NodoUnario("!", visit(ctx.expresion()), ctx.getStart().getLine());
    }

    @Override
    public NodoAST visitMultDivExpr(YParser.MultDivExprContext ctx) {
        return new NodoBinario(ctx.op.getText(),
                visit(ctx.expresion(0)), visit(ctx.expresion(1)), ctx.getStart().getLine());
    }

    @Override
    public NodoAST visitSumaRestaExpr(YParser.SumaRestaExprContext ctx) {
        return new NodoBinario(ctx.op.getText(),
                visit(ctx.expresion(0)), visit(ctx.expresion(1)), ctx.getStart().getLine());
    }

    @Override
    public NodoAST visitRelacionalExpr(YParser.RelacionalExprContext ctx) {
        return new NodoBinario(ctx.op.getText(),
                visit(ctx.expresion(0)), visit(ctx.expresion(1)), ctx.getStart().getLine());
    }

    @Override
    public NodoAST visitIgualdadExpr(YParser.IgualdadExprContext ctx) {
        return new NodoBinario(ctx.op.getText(),
                visit(ctx.expresion(0)), visit(ctx.expresion(1)), ctx.getStart().getLine());
    }

    @Override
    public NodoAST visitAndExpr(YParser.AndExprContext ctx) {
        return new NodoBinario("&&",
                visit(ctx.expresion(0)), visit(ctx.expresion(1)), ctx.getStart().getLine());
    }

    @Override
    public NodoAST visitOrExpr(YParser.OrExprContext ctx) {
        return new NodoBinario("||",
                visit(ctx.expresion(0)), visit(ctx.expresion(1)), ctx.getStart().getLine());
    }

    @Override
    public NodoAST visitPrimarioExpr(YParser.PrimarioExprContext ctx) {
        return visit(ctx.primario());
    }

    @Override
    public NodoAST visitPrimario(YParser.PrimarioContext ctx) {
        if (ctx.literal() != null) {
            return visit(ctx.literal());
        }
        if (ctx.llamadaLeer() != null) {
            return visit(ctx.llamadaLeer());
        }
        if (ctx.llamadaFuncion() != null) {
            return visit(ctx.llamadaFuncion());
        }
        if (ctx.accesoVariable() != null) {
            return visit(ctx.accesoVariable());
        }
        return visit(ctx.expresion());
    }

    @Override
    public NodoAST visitLlamadaFuncion(YParser.LlamadaFuncionContext ctx) {
        List<NodoAST> args = ctx.listaArgumentos() == null
                ? List.of()
                : ctx.listaArgumentos().expresion().stream().map(this::visit).collect(Collectors.toList());
        return new NodoLlamadaFuncion(ctx.ID().getText(), args, ctx.getStart().getLine());
    }

    @Override
    public NodoAST visitLlamadaLeer(YParser.LlamadaLeerContext ctx) {
        return new NodoLeer(ctx.getStart().getLine());
    }

    @Override
    public NodoAST visitLiteral(YParser.LiteralContext ctx) {
        int linea = ctx.getStart().getLine();
        String texto = ctx.getText();
        if (ctx.ENTERO_LIT() != null) {
            return new NodoLiteral(TipoLiteral.ENTERO, texto, linea);
        }
        if (ctx.FLOTANTE_LIT() != null) {
            return new NodoLiteral(TipoLiteral.FLOTANTE, texto, linea);
        }
        if (ctx.CARACTER_LIT() != null) {
            return new NodoLiteral(TipoLiteral.CARACTER, texto, linea);
        }
        if (ctx.CADENA_LIT() != null) {
            return new NodoLiteral(TipoLiteral.CADENA, texto, linea);
        }
        return new NodoLiteral(TipoLiteral.BOOLEANO, texto, linea);
    }

    @Override
    public NodoAST visitSentenciaImprimir(YParser.SentenciaImprimirContext ctx) {
        return new NodoImprimir(visit(ctx.expresion()), ctx.getStart().getLine());
    }

    @Override
    public NodoAST visitSentenciaLeer(YParser.SentenciaLeerContext ctx) {
        return new NodoLeer(ctx.getStart().getLine());
    }

    @Override
    public NodoAST visitSentenciaContinuar(YParser.SentenciaContinuarContext ctx) {
        return new NodoContinuar(ctx.getStart().getLine());
    }

    @Override
    public NodoAST visitSentenciaRomper(YParser.SentenciaRomperContext ctx) {
        return new NodoRomper(ctx.getStart().getLine());
    }

    @Override
    public NodoAST visitSentenciaRetornar(YParser.SentenciaRetornarContext ctx) {
        NodoAST expr = ctx.expresion() != null ? visit(ctx.expresion()) : null;
        return new NodoRetornar(expr, ctx.getStart().getLine());
    }

    @Override
    public NodoAST visitSentenciaSi(YParser.SentenciaSiContext ctx) {
        List<ParseTree> hijos = ctx.children;
        int i = 0;

        while (!(hijos.get(i) instanceof YParser.ExpresionContext)) {
            i++;
        }
        NodoAST condicion = visit(hijos.get(i));
        i++;

        int inicioBloqueSi = avanzarHasta(hijos, i, YParser.INDENT) + 1;
        List<NodoAST> cuerpoSi = leerBloqueSentencias(hijos, inicioBloqueSi);
        i = avanzarHasta(hijos, inicioBloqueSi, YParser.DEDENT) + 1;

        List<NodoRamaSino> ramasSino = new ArrayList<>();
        while (i < hijos.size() && esToken(hijos.get(i), YParser.KW_SINO)) {
            i++; // consume KW_SINO
            while (!(hijos.get(i) instanceof YParser.ExpresionContext)) {
                i++;
            }
            NodoAST condSino = visit(hijos.get(i));
            int lineaSino = ((YParser.ExpresionContext) hijos.get(i)).getStart().getLine();
            i++;

            int inicioBloqueSino = avanzarHasta(hijos, i, YParser.INDENT) + 1;
            List<NodoAST> cuerpoSino = leerBloqueSentencias(hijos, inicioBloqueSino);
            i = avanzarHasta(hijos, inicioBloqueSino, YParser.DEDENT) + 1;

            ramasSino.add(new NodoRamaSino(condSino, cuerpoSino, lineaSino));
        }

        List<NodoAST> cuerpoContrario = null;
        if (i < hijos.size() && esToken(hijos.get(i), YParser.KW_CONTRARIO)) {
            i++;
            int inicioBloqueContrario = avanzarHasta(hijos, i, YParser.INDENT) + 1;
            cuerpoContrario = leerBloqueSentencias(hijos, inicioBloqueContrario);
        }

        return new NodoSi(condicion, cuerpoSi, ramasSino, cuerpoContrario, ctx.getStart().getLine());
    }

    private boolean esToken(ParseTree nodo, int tipoToken) {
        return nodo instanceof TerminalNode && ((TerminalNode) nodo).getSymbol().getType() == tipoToken;
    }

    /**
     * Retorna el índice del primer hijo, desde 'desde', que sea el token
     * buscado.
     */
    private int avanzarHasta(List<ParseTree> hijos, int desde, int tipoToken) {
        int i = desde;
        while (!esToken(hijos.get(i), tipoToken)) {
            i++;
        }
        return i;
    }

    /**
     * Lee sentencias consecutivas hasta (sin incluir) el próximo DEDENT.
     */
    private List<NodoAST> leerBloqueSentencias(List<ParseTree> hijos, int desde) {
        List<NodoAST> resultado = new ArrayList<>();
        int i = desde;
        while (!esToken(hijos.get(i), YParser.DEDENT)) {
            if (hijos.get(i) instanceof YParser.SentenciaContext) {
                resultado.add(visit(hijos.get(i)));
            }
            i++;
        }
        return resultado;
    }

    @Override
    public NodoAST visitSentenciaElegir(YParser.SentenciaElegirContext ctx) {
        NodoAST valor = visit(ctx.expresion());
        List<NodoCaso> casos = ctx.casoElegir().stream()
                .map(c -> (NodoCaso) visitCasoElegir(c))
                .collect(Collectors.toList());
        NodoSiempre siempre = ctx.siempreElegir() != null
                ? (NodoSiempre) visitSiempreElegir(ctx.siempreElegir()) : null;
        return new NodoElegir(valor, casos, siempre, ctx.getStart().getLine());
    }

    @Override
    public NodoAST visitCasoElegir(YParser.CasoElegirContext ctx) {
        NodoAST valorLiteral = visit(ctx.valorCaso());
        List<NodoAST> cuerpo = ctx.bloqueSentencias().sentencia().stream().map(this::visit).collect(Collectors.toList());
        return new NodoCaso(valorLiteral, cuerpo, ctx.getStart().getLine());
    }

    @Override
    public NodoAST visitSiempreElegir(YParser.SiempreElegirContext ctx) {
        List<NodoAST> cuerpo = ctx.bloqueSentencias().sentencia().stream().map(this::visit).collect(Collectors.toList());
        return new NodoSiempre(cuerpo, ctx.getStart().getLine());
    }

    @Override
    public NodoAST visitSentenciaPara(YParser.SentenciaParaContext ctx) {
        NodoAST init = visit(ctx.paraInit());
        NodoAST condicion = visit(ctx.expresion());
        NodoAST actualizacion = visit(ctx.paraActualizacion());
        List<NodoAST> cuerpo = ctx.bloqueSentencias().sentencia().stream().map(this::visit).collect(Collectors.toList());
        return new NodoPara(init, condicion, actualizacion, cuerpo, ctx.getStart().getLine());
    }

    @Override
    public NodoAST visitParaInit(YParser.ParaInitContext ctx) {
        if (ctx.tipoDato() != null) {
            NodoAST inicializador = ctx.expresion() != null ? visit(ctx.expresion()) : null;
            return new NodoDeclaracionVariable(ctx.tipoDato().getText(), ctx.ID().getText(), List.of(), inicializador, ctx.getStart().getLine());
        }
        NodoAccesoVariable destino = (NodoAccesoVariable) visitAccesoVariable(ctx.accesoVariable());
        return new NodoAsignacion(destino, visit(ctx.expresion()), ctx.getStart().getLine());
    }

    @Override
    public NodoAST visitParaActualizacion(YParser.ParaActualizacionContext ctx) {
        NodoAccesoVariable destino = (NodoAccesoVariable) visitAccesoVariable(ctx.accesoVariable());
        if (ctx.op != null) {
            return new NodoIncrementoDecremento(destino, ctx.op.getText(), ctx.getStart().getLine());
        }
        return new NodoAsignacion(destino, visit(ctx.expresion()), ctx.getStart().getLine());
    }

    @Override
    public NodoAST visitSentenciaMientras(YParser.SentenciaMientrasContext ctx) {
        NodoAST condicion = visit(ctx.expresion());
        List<NodoAST> cuerpo = ctx.bloqueSentencias().sentencia().stream().map(this::visit).collect(Collectors.toList());
        return new NodoMientras(condicion, cuerpo, ctx.getStart().getLine());
    }

    @Override
    public NodoAST visitSentenciaHacerMientras(YParser.SentenciaHacerMientrasContext ctx) {
        List<NodoAST> cuerpo = ctx.bloqueSentencias().sentencia().stream().map(this::visit).collect(Collectors.toList());
        NodoAST condicion = visit(ctx.expresion());
        return new NodoHacerMientras(cuerpo, condicion, ctx.getStart().getLine());
    }

    @Override
    public NodoAST visitSentencia(YParser.SentenciaContext ctx) {
        ParseTree hijo = ctx.getChild(0);
        return visit(hijo);
    }

}
