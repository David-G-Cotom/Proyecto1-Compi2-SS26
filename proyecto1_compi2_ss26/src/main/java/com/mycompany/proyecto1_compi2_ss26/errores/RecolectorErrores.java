/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.errores;

import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

/**
 *
 * @author david
 */
public class RecolectorErrores extends BaseErrorListener {

    private final List<Error> errors = new ArrayList<>();

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        TipoError type = (recognizer instanceof Lexer) ? TipoError.LEXICO : TipoError.SINTACTICO;
        String symbol = (offendingSymbol != null) ? offendingSymbol.toString() : "";
        this.errors.add(new Error(type, line, charPositionInLine + 1, msg, symbol));
    }

    public List<Error> getErrors() {
        return errors;
    }

    public List<Error> getLexicalErrors() {
        return this.errors.stream().filter(error -> error.getType() == TipoError.LEXICO).toList();
    }

    public List<Error> getSyntaxErrors() {
        return this.errors.stream().filter(error -> error.getType() == TipoError.SINTACTICO).toList();
    }

    public List<Error> getSemanticErrors() {
        return this.errors.stream().filter(error -> error.getType() == TipoError.SEMANTICO).toList();
    }

    public void addSemanticErrors(int line, int column, String message) {
        this.errors.add(new Error(TipoError.SEMANTICO, line, column, message, null));
    }

    public void addLexicalError(int line, int column, String message, String symbol) {
        this.errors.add(new Error(TipoError.LEXICO, line, column, message, symbol));
    }

    public boolean isEmpty() {
        return this.errors.isEmpty();
    }

    public void clear() {
        this.errors.clear();
    }

}
