/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.lenguajes.z;

import com.mycompany.ZetarianoBaseVisitor;
import com.mycompany.ZetarianoParser;
import com.mycompany.proyecto1_compi2_ss26.ast.NodoAST;
import com.mycompany.proyecto1_compi2_ss26.ast.expresiones.*;
import com.mycompany.proyecto1_compi2_ss26.ast.sentencias.*;
import com.mycompany.proyecto1_compi2_ss26.ast.z.NodoArchivoZ;
import com.mycompany.proyecto1_compi2_ss26.ast.z.expresiones.*;
import com.mycompany.proyecto1_compi2_ss26.ast.z.sentencias.*;
import com.mycompany.proyecto1_compi2_ss26.modelos.ModoPaso;
import com.mycompany.proyecto1_compi2_ss26.modelos.TipoLiteral;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author david
 */
public class ConstructorASTZ extends ZetarianoBaseVisitor<NodoAST> {

    @Override
    public NodoAST visitArchivoZ(ZetarianoParser.ArchivoZContext ctx) {
        return visit(ctx.claseZ());
    }

    @Override
    public NodoAST visitClaseZ(ZetarianoParser.ClaseZContext ctx) {
        List<NodoAtributoZ> atributos = new ArrayList<>();
        List<NodoConstructorZ> constructores = new ArrayList<>();
        List<NodoFuncion> metodos = new ArrayList<>();

        for (ZetarianoParser.MiembroClaseContext m : ctx.miembroClase()) {
            if (m.atributoZ() != null) {
                atributos.add((NodoAtributoZ) visit(m.atributoZ()));
            } else if (m.constructorZ() != null) {
                constructores.add((NodoConstructorZ) visit(m.constructorZ()));
            } else if (m.metodoZ() != null) {
                metodos.add((NodoFuncion) visit(m.metodoZ()));
            }
        }

        return new NodoArchivoZ(ctx.getStart().getLine(), ctx.ID().getText(), atributos, constructores, metodos);
    }

    @Override
    public NodoAST visitAtributoZ(ZetarianoParser.AtributoZContext ctx) {
        List<NodoDeclaradorZ> declaradores = ctx.declaradorAtributoZ().stream()
                .map(this::construirDeclarador)
                .collect(Collectors.toList());
        return new NodoAtributoZ(ctx.getStart().getLine(), ctx.tipoZ().getText(), declaradores);
    }

    private NodoDeclaradorZ construirDeclarador(ZetarianoParser.DeclaradorAtributoZContext ctx) {
        NodoAST init = obtenerInicializador(ctx.literalCompuestoZ(), ctx.expresionZ());
        return new NodoDeclaradorZ(ctx.getStart().getLine(), ctx.ID().getText(), init);
    }

    private NodoDeclaradorZ construirDeclarador(ZetarianoParser.DeclaradorVariableZContext ctx) {
        NodoAST init = obtenerInicializador(ctx.literalCompuestoZ(), ctx.expresionZ());
        return new NodoDeclaradorZ(ctx.getStart().getLine(), ctx.ID().getText(), init);
    }

    private NodoAST obtenerInicializador(ZetarianoParser.LiteralCompuestoZContext literalCtx,
            ZetarianoParser.ExpresionZContext expresionCtx) {
        if (literalCtx != null) {
            return visitLiteralCompuestoZ(literalCtx);
        }
        if (expresionCtx != null) {
            return visit(expresionCtx);
        }
        return null;
    }

    @Override
    public NodoAST visitLiteralCompuestoZ(ZetarianoParser.LiteralCompuestoZContext ctx) {
        List<NodoAST> valores = ctx.expresionZ().stream().map(this::visit).collect(Collectors.toList());
        return new NodoLiteralCompuesto(valores, ctx.getStart().getLine());
    }

    @Override
    public NodoAST visitConstructorZ(ZetarianoParser.ConstructorZContext ctx) {
        List<NodoParametro> parametros = construirParametros(ctx.listaParametrosZ());
        List<NodoAST> cuerpo = ctx.sentenciaZ().stream().map(this::visit).collect(Collectors.toList());
        return new NodoConstructorZ(ctx.getStart().getLine(), ctx.ID().getText(), parametros, cuerpo);
    }

    @Override
    public NodoAST visitMetodoZ(ZetarianoParser.MetodoZContext ctx) {
        List<NodoParametro> parametros = construirParametros(ctx.listaParametrosZ());
        List<NodoAST> cuerpo = ctx.sentenciaZ().stream().map(this::visit).collect(Collectors.toList());
        return new NodoFuncion(ctx.ID().getText(), parametros,
                ctx.tipoRetornoZ().getText(), cuerpo, ctx.getStart().getLine());
    }

    private List<NodoParametro> construirParametros(ZetarianoParser.ListaParametrosZContext ctx) {
        if (ctx == null) {
            return new ArrayList<>();
        }
        List<NodoParametro> resultado = new ArrayList<>();
        for (ZetarianoParser.ParametroZContext p : ctx.parametroZ()) {
            String tipoTexto = p.tipoZ().getText();
            // Primitivo (sin '[]') -> por valor; cualquier otro (objeto, arreglo) -> por referencia.
            boolean esPrimitivoSimple = p.tipoZ().tipoPrimitivoZ() != null && !tipoTexto.contains("[");
            ModoPaso modo = esPrimitivoSimple ? ModoPaso.POR_VALOR : ModoPaso.POR_REFERENCIA;
            resultado.add(new NodoParametro(tipoTexto, p.ID().getText(), modo, p.getStart().getLine()));
        }
        return resultado;
    }

    // ---------- Declaración de variables ----------
    @Override
    public NodoAST visitDeclaracionVariableZ(ZetarianoParser.DeclaracionVariableZContext ctx) {
        List<NodoDeclaradorZ> declaradores = ctx.declaradorVariableZ().stream()
                .map(this::construirDeclarador)
                .collect(Collectors.toList());
        return new NodoDeclaracionVariableZ(ctx.getStart().getLine(), ctx.tipoZ().getText(), declaradores);
    }

    // ---------- Acceso, asignación, incremento/decremento ----------
    @Override
    public NodoAST visitAccesoVariableZ(ZetarianoParser.AccesoVariableZContext ctx) {
        List<NodoSegmentoAccesoZ> segmentos = ctx.segmentoAccesoZ().stream()
                .map(s -> (NodoSegmentoAccesoZ) visitSegmentoAccesoZ(s))
                .collect(Collectors.toList());
        return new NodoAccesoVariableZ(ctx.getStart().getLine(), segmentos);
    }

    @Override
    public NodoAST visitSegmentoAccesoZ(ZetarianoParser.SegmentoAccesoZContext ctx) {
        List<NodoAST> argumentos = null;
        if (ctx.PAR_ABRE() != null) {
            argumentos = ctx.listaArgumentosZ() == null
                    ? new ArrayList<>()
                    : ctx.listaArgumentosZ().expresionZ().stream().map(this::visit).collect(Collectors.toList());
        }
        List<NodoAST> indices = ctx.expresionZ().stream().map(this::visit).collect(Collectors.toList());
        return new NodoSegmentoAccesoZ(ctx.getStart().getLine(), ctx.ID().getText(), argumentos, indices);
    }

    @Override
    public NodoAST visitAsignacionZ(ZetarianoParser.AsignacionZContext ctx) {
        NodoAccesoVariableZ destino = (NodoAccesoVariableZ) visitAccesoVariableZ(ctx.accesoVariableZ());
        return new NodoAsignacionZ(ctx.getStart().getLine(), destino, ctx.op.getText(), visit(ctx.expresionZ()));
    }

    @Override
    public NodoAST visitIncrementoDecrementoZ(ZetarianoParser.IncrementoDecrementoZContext ctx) {
        NodoAccesoVariableZ destino = (NodoAccesoVariableZ) visitAccesoVariableZ(ctx.accesoVariableZ());
        return new NodoIncDecZ(ctx.getStart().getLine(), destino, ctx.op.getText());
    }

    // ---------- Expresiones ----------
    @Override
    public NodoAST visitNegacionExprZ(ZetarianoParser.NegacionExprZContext ctx) {
        return new NodoUnario("!", visit(ctx.expresionZ()), ctx.getStart().getLine());
    }

    @Override
    public NodoAST visitMenosUnarioExprZ(ZetarianoParser.MenosUnarioExprZContext ctx) {
        return new NodoUnario("-", visit(ctx.expresionZ()), ctx.getStart().getLine());
    }

    @Override
    public NodoAST visitMultDivModExprZ(ZetarianoParser.MultDivModExprZContext ctx) {
        return new NodoBinario(ctx.op.getText(), visit(ctx.expresionZ(0)), visit(ctx.expresionZ(1)), ctx.getStart().getLine());
    }

    @Override
    public NodoAST visitSumaRestaExprZ(ZetarianoParser.SumaRestaExprZContext ctx) {
        return new NodoBinario(ctx.op.getText(), visit(ctx.expresionZ(0)), visit(ctx.expresionZ(1)), ctx.getStart().getLine());
    }

    @Override
    public NodoAST visitRelacionalExprZ(ZetarianoParser.RelacionalExprZContext ctx) {
        return new NodoBinario(ctx.op.getText(), visit(ctx.expresionZ(0)), visit(ctx.expresionZ(1)), ctx.getStart().getLine());
    }

    @Override
    public NodoAST visitIgualdadExprZ(ZetarianoParser.IgualdadExprZContext ctx) {
        return new NodoBinario(ctx.op.getText(), visit(ctx.expresionZ(0)), visit(ctx.expresionZ(1)), ctx.getStart().getLine());
    }

    @Override
    public NodoAST visitAndExprZ(ZetarianoParser.AndExprZContext ctx) {
        return new NodoBinario("&&", visit(ctx.expresionZ(0)), visit(ctx.expresionZ(1)), ctx.getStart().getLine());
    }

    @Override
    public NodoAST visitOrExprZ(ZetarianoParser.OrExprZContext ctx) {
        return new NodoBinario("||", visit(ctx.expresionZ(0)), visit(ctx.expresionZ(1)), ctx.getStart().getLine());
    }

    @Override
    public NodoAST visitTernarioExprZ(ZetarianoParser.TernarioExprZContext ctx) {
        NodoAST condicion = visit(ctx.expresionZ(0));
        NodoAST siVerdadero = visit(ctx.expresionZ(1));
        NodoAST siFalso = visit(ctx.expresionZ(2));
        return new NodoTernarioZ(ctx.getStart().getLine(), condicion, siVerdadero, siFalso);
    }

    @Override
    public NodoAST visitPrimarioExprZ(ZetarianoParser.PrimarioExprZContext ctx) {
        return visit(ctx.primarioZ());
    }

    @Override
    public NodoAST visitPrimarioZ(ZetarianoParser.PrimarioZContext ctx) {
        if (ctx.literalZ() != null) {
            return visit(ctx.literalZ());
        }
        if (ctx.nuevoArregloZ() != null) {
            return visit(ctx.nuevoArregloZ());
        }
        if (ctx.nuevoObjetoZ() != null) {
            return visit(ctx.nuevoObjetoZ());
        }
        if (ctx.llamadaReadlnZ() != null) {
            return visit(ctx.llamadaReadlnZ());
        }
        if (ctx.KW_NULL() != null) {
            return new NodoNuloZ(ctx.getStart().getLine());
        }
        if (ctx.accesoVariableZ() != null) {
            return visit(ctx.accesoVariableZ());
        }
        return visit(ctx.expresionZ());
    }

    @Override
    public NodoAST visitLiteralZ(ZetarianoParser.LiteralZContext ctx) {
        int linea = ctx.getStart().getLine();
        if (ctx.ENTERO_LIT() != null) {
            return new NodoLiteral(TipoLiteral.ENTERO, ctx.getText(), linea);
        }
        if (ctx.FLOTANTE_LIT() != null) {
            return new NodoLiteral(TipoLiteral.FLOTANTE, ctx.getText(), linea);
        }
        if (ctx.CARACTER_LIT() != null) {
            return new NodoLiteral(TipoLiteral.CARACTER, ctx.getText(), linea);
        }
        if (ctx.CADENA_LIT() != null) {
            return new NodoLiteral(TipoLiteral.CADENA, ctx.getText(), linea);
        }
        return new NodoLiteral(TipoLiteral.BOOLEANO, ctx.getText(), linea);
    }

    @Override
    public NodoAST visitNuevoObjetoZ(ZetarianoParser.NuevoObjetoZContext ctx) {
        List<NodoAST> args = ctx.listaArgumentosZ() == null
                ? List.of()
                : ctx.listaArgumentosZ().expresionZ().stream().map(this::visit).collect(Collectors.toList());
        return new NodoNuevoObjetoZ(ctx.getStart().getLine(), ctx.ID().getText(), args);
    }

    @Override
    public NodoAST visitNuevoArregloZ(ZetarianoParser.NuevoArregloZContext ctx) {
        List<NodoAST> dimensiones = ctx.expresionZ().stream().map(this::visit).collect(Collectors.toList());
        String tipoBase = ctx.tipoPrimitivoZ() != null ? ctx.tipoPrimitivoZ().getText() : ctx.ID().getText();
        return new NodoNuevoArregloZ(ctx.getStart().getLine(), tipoBase, dimensiones);
    }

    @Override
    public NodoAST visitLlamadaReadlnZ(ZetarianoParser.LlamadaReadlnZContext ctx) {
        return new NodoLeer(ctx.getStart().getLine());
    }

    // ---------- println / print / readln / return / break / continue ----------
    @Override
    public NodoAST visitSentenciaImprimirZ(ZetarianoParser.SentenciaImprimirZContext ctx) {
        boolean conSalto = ctx.KW_PRINTLN() != null;
        return new NodoImprimirZ(ctx.getStart().getLine(), conSalto, visit(ctx.expresionZ()));
    }

    @Override
    public NodoAST visitSentenciaLeerZ(ZetarianoParser.SentenciaLeerZContext ctx) {
        return new NodoLeer(ctx.getStart().getLine());
    }

    @Override
    public NodoAST visitSentenciaReturnZ(ZetarianoParser.SentenciaReturnZContext ctx) {
        NodoAST expr = ctx.expresionZ() != null ? visit(ctx.expresionZ()) : null;
        return new NodoRetornar(expr, ctx.getStart().getLine());
    }

    @Override
    public NodoAST visitSentenciaBreakZ(ZetarianoParser.SentenciaBreakZContext ctx) {
        return new NodoRomper(ctx.getStart().getLine());
    }

    @Override
    public NodoAST visitSentenciaContinueZ(ZetarianoParser.SentenciaContinueZContext ctx) {
        return new NodoContinuar(ctx.getStart().getLine());
    }

    @Override
    public NodoAST visitSentenciaLlamadaZ(ZetarianoParser.SentenciaLlamadaZContext ctx) {
        return visit(ctx.accesoVariableZ());
    }

    // ---------- Control de flujo ----------
    @Override
    public NodoAST visitSentenciaSiZ(ZetarianoParser.SentenciaSiZContext ctx) {
        List<NodoAST> condiciones = ctx.expresionZ().stream().map(this::visit).collect(Collectors.toList());
        List<List<NodoAST>> bloques = ctx.bloqueZ().stream()
                .map(this::construirBloque)
                .collect(Collectors.toList());

        List<NodoAST> bloqueContrario = null;
        if (bloques.size() == condiciones.size() + 1) {
            bloqueContrario = bloques.remove(bloques.size() - 1);
        }

        return new NodoSiZ(ctx.getStart().getLine(), condiciones, bloques, bloqueContrario);
    }

    private List<NodoAST> construirBloque(ZetarianoParser.BloqueZContext ctx) {
        if (ctx.LLA_ABRE() == null) {
            // Alternativa 'sentenciaZ' sola (sin llaves): un único elemento.
            return new ArrayList<>(List.of(visit(ctx.sentenciaZ(0))));
        }
        return ctx.sentenciaZ().stream().map(this::visit).collect(Collectors.toList());
    }

    @Override
    public NodoAST visitSentenciaSwitchZ(ZetarianoParser.SentenciaSwitchZContext ctx) {
        NodoAST valor = visit(ctx.expresionZ());
        List<NodoCaso> casos = ctx.casoSwitchZ().stream()
                .map(c -> (NodoCaso) visitCasoSwitchZ(c))
                .collect(Collectors.toList());
        NodoDefaultSwitch defaultCaso = ctx.defaultSwitchZ() != null
                ? (NodoDefaultSwitch) visitDefaultSwitchZ(ctx.defaultSwitchZ()) : null;
        return new NodoSwitch(ctx.getStart().getLine(), valor, casos, defaultCaso);
    }

    @Override
    public NodoAST visitCasoSwitchZ(ZetarianoParser.CasoSwitchZContext ctx) {
        NodoAST valorLiteral = visit(ctx.valorCasoZ().literalZ());
        List<NodoAST> cuerpo = ctx.sentenciaZ().stream().map(this::visit).collect(Collectors.toList());
        return new NodoCaso(valorLiteral, cuerpo, ctx.getStart().getLine());
    }

    @Override
    public NodoAST visitDefaultSwitchZ(ZetarianoParser.DefaultSwitchZContext ctx) {
        List<NodoAST> cuerpo = ctx.sentenciaZ().stream().map(this::visit).collect(Collectors.toList());
        return new NodoDefaultSwitch(ctx.getStart().getLine(), cuerpo);
    }

    @Override
    public NodoAST visitSentenciaForZ(ZetarianoParser.SentenciaForZContext ctx) {
        NodoAST init = ctx.forInitZ() != null ? visit(ctx.forInitZ()) : null;
        NodoAST condicion = ctx.expresionZ() != null ? visit(ctx.expresionZ()) : null;
        NodoAST actualizacion = ctx.forActualizacionZ() != null ? visit(ctx.forActualizacionZ()) : null;
        List<NodoAST> cuerpo = construirBloque(ctx.bloqueZ());
        return new NodoFor(init, condicion, actualizacion, cuerpo, ctx.getStart().getLine());
    }

    @Override
    public NodoAST visitForInitZ(ZetarianoParser.ForInitZContext ctx) {
        if (ctx.tipoZ() != null) {
            List<NodoDeclaradorZ> declaradores = ctx.declaradorVariableZ().stream()
                    .map(this::construirDeclarador)
                    .collect(Collectors.toList());
            return new NodoDeclaracionVariableZ(ctx.getStart().getLine(), ctx.tipoZ().getText(), declaradores);
        }
        NodoAccesoVariableZ destino = (NodoAccesoVariableZ) visitAccesoVariableZ(ctx.accesoVariableZ());
        return new NodoAsignacionZ(ctx.getStart().getLine(), destino, "=", visit(ctx.expresionZ()));
    }

    @Override
    public NodoAST visitForActualizacionZ(ZetarianoParser.ForActualizacionZContext ctx) {
        NodoAccesoVariableZ destino = (NodoAccesoVariableZ) visitAccesoVariableZ(ctx.accesoVariableZ());
        if (ctx.expresionZ() == null) {
            return new NodoIncDecZ(ctx.getStart().getLine(), destino, ctx.op.getText());
        }
        return new NodoAsignacionZ(ctx.getStart().getLine(), destino, ctx.op.getText(), visit(ctx.expresionZ()));
    }

    @Override
    public NodoAST visitSentenciaWhileZ(ZetarianoParser.SentenciaWhileZContext ctx) {
        NodoAST condicion = visit(ctx.expresionZ());
        List<NodoAST> cuerpo = construirBloque(ctx.bloqueZ());
        return new NodoWhile(ctx.getStart().getLine(), condicion, cuerpo);
    }

    @Override
    public NodoAST visitSentenciaDoWhileZ(ZetarianoParser.SentenciaDoWhileZContext ctx) {
        List<NodoAST> cuerpo = construirBloque(ctx.bloqueZ());
        NodoAST condicion = visit(ctx.expresionZ());
        return new NodoDoWhile(ctx.getStart().getLine(), cuerpo, condicion);
    }

    // ---------- sentenciaZ (delega según la alternativa que haya matcheado) ----------
    @Override
    public NodoAST visitSentenciaZ(ZetarianoParser.SentenciaZContext ctx) {
        return visit(ctx.getChild(0));
    }

}
