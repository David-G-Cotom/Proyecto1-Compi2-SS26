/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.lenguajes.y;

import com.mycompany.YLexer;
import com.mycompany.YParser;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenFactory;
import org.antlr.v4.runtime.TokenSource;

/**
 *
 * @author david
 */
public class Indentador implements TokenSource {

    private static final int ESPACIOS_POR_NIVEL = 4;

    private final Lexer lexerCrudo;
    private final Deque<Integer> pilaIdentacion = new ArrayDeque<>();
    private final Deque<Token> pendientes = new ArrayDeque<>();
    private final List<String> erroresIdentacion = new ArrayList<>();

    public Indentador(Lexer lexerCrudo) {
        this.lexerCrudo = lexerCrudo;
        this.pilaIdentacion.push(0);
    }

    public List<String> getErroresIdentacion() {
        return erroresIdentacion;
    }

    @Override
    public Token nextToken() {
        if (!this.pendientes.isEmpty()) {
            return this.pendientes.poll();  //Eliminar y obtener primer elemento
        }
        Token token = this.lexerCrudo.nextToken();
        if (token.getType() == Token.EOF) {
            return this.evaluarEOF(token);
        }
        if (token.getType() != YLexer.NEWLINE) {
            return token;
        }
        return this.procesarNewLine(token);
    }

    private void cerrarNivelesPendientes(Token referencia) {
        while (this.pilaIdentacion.size() > 1) {
            this.pilaIdentacion.pop();  //Eliminar primer elemento
            this.pendientes.add(this.sintetico(YParser.DEDENT, referencia));
        }
    }

    private Token sintetico(int tipo, Token referencia) {
        CommonToken t = new CommonToken(tipo, tipo == YParser.INDENT ? "<INDENT>" : "<DEDENT>");
        t.setLine(referencia.getLine());
        t.setCharPositionInLine(referencia.getCharPositionInLine());
        return t;
    }

    private Token procesarNewLine(Token primerNewLine) {
        Token ultimoNewLine = primerNewLine;
        List<Token> comentariosIntermedios = new ArrayList<>();
        Token siguiente = this.lexerCrudo.nextToken();
        while (siguiente.getType() == YLexer.NEWLINE || this.esCanalOculto(siguiente)) {
            if (this.esCanalOculto(siguiente)) {
                comentariosIntermedios.add(siguiente);
            } else {
                ultimoNewLine = siguiente;
            }
            siguiente = this.lexerCrudo.nextToken();
        }
        this.pendientes.addAll(comentariosIntermedios);
        this.pendientes.add(ultimoNewLine);
        if (siguiente.getType() == Token.EOF) {
            return this.evaluarEOF(siguiente);
        }
        this.ajustarIdentacion(ultimoNewLine, siguiente);
        this.pendientes.add(siguiente); //Insertar elemento al final
        return this.pendientes.poll();  //Eliminar y obtener primer elemento
    }

    private boolean esCanalOculto(Token token) {
        return token.getChannel() != Token.DEFAULT_CHANNEL;
    }

    private void ajustarIdentacion(Token newlineToken, Token tokenSiguiente) {
        int nivel = this.calcularNivel(newlineToken.getText());
        int tope = this.pilaIdentacion.peek();  //Obtener primer elemento
        if (nivel > tope) {
            this.pilaIdentacion.push(nivel);
            this.pendientes.add(this.sintetico(YParser.INDENT, tokenSiguiente));
        } else if (nivel < tope) {
            while (this.pilaIdentacion.peek() > nivel) {
                this.pilaIdentacion.pop();  //Eliminar primer elemento
                this.pendientes.add(this.sintetico(YParser.DEDENT, tokenSiguiente));
            }
            if (this.pilaIdentacion.peek() != nivel) {
                this.erroresIdentacion.add("Identacion inconsistente en linea " + tokenSiguiente.getLine() + ": no calza con ningun nivel de indentacion abierto.");
                this.pilaIdentacion.push(nivel);    //Insertar elemento al principio
            }
        }
        //si nivel == tope, no se agrega nada mas
    }

    private int calcularNivel(String textoNewline) {
        int tabs = 0;
        int espacios = 0;
        for (int i = 0; i < textoNewline.length(); i++) {
            char c = textoNewline.charAt(i);
            if (c == '\t') {
                tabs++;
            } else if (c == ' ') {
                espacios++;
            }
        }
        return tabs + (espacios / ESPACIOS_POR_NIVEL);
    }

    private Token evaluarEOF(Token token) {
        this.cerrarNivelesPendientes(token);
        this.pendientes.add(token);
        return this.pendientes.poll();
    }

    @Override
    public int getLine() {
        return this.lexerCrudo.getLine();
    }

    @Override
    public int getCharPositionInLine() {
        return this.lexerCrudo.getCharPositionInLine();
    }

    @Override
    public CharStream getInputStream() {
        return this.lexerCrudo.getInputStream();
    }

    @Override
    public String getSourceName() {
        return this.lexerCrudo.getSourceName();
    }

    @Override
    public void setTokenFactory(TokenFactory<?> tf) {
        this.lexerCrudo.setTokenFactory(tf);
    }

    @Override
    public TokenFactory<?> getTokenFactory() {
        return this.lexerCrudo.getTokenFactory();
    }

}
