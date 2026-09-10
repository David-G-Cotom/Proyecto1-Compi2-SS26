/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.errores;

/**
 *
 * @author david
 */
public class Error {

    private final TipoError type;
    private final int line;
    private final int column;
    private final String message;
    private final String symbol;

    public Error(TipoError type, int line, int column, String message, String symbol) {
        this.type = type;
        this.line = line;
        this.column = column;
        this.message = message;
        this.symbol = symbol;
    }

    public TipoError getType() {
        return type;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String getMessage() {
        return message;
    }

    public String getSymbol() {
        return symbol;
    }

}
