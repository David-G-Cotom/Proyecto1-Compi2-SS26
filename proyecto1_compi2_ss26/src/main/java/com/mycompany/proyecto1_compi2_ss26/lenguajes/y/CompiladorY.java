/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.lenguajes.y;

import com.mycompany.YLexer;
import com.mycompany.YParser;
import com.mycompany.proyecto1_compi2_ss26.ast.NodoAST;
import com.mycompany.proyecto1_compi2_ss26.ast.y.NodoArchivoY;
import com.mycompany.proyecto1_compi2_ss26.errores.RecolectorErrores;
import com.mycompany.proyecto1_compi2_ss26.lenguajes.y.semantica.AnalizadorSemanticoY;
import com.mycompany.proyecto1_compi2_ss26.tipos.CatalogoExportado;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

/**
 *
 * @author david
 */
public class CompiladorY {

    public static CatalogoExportado compilarAislado(String rutaArchivo) throws Exception {
        RecolectorErrores coleccionErrores = new RecolectorErrores();

        YLexer lexerCrudo = new YLexer(CharStreams.fromFileName(rutaArchivo));
        lexerCrudo.removeErrorListeners();
        lexerCrudo.addErrorListener(coleccionErrores);

        Indentador indentador = new Indentador(lexerCrudo, coleccionErrores);
        CommonTokenStream tokens = new CommonTokenStream(indentador);

        YParser parser = new YParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(coleccionErrores);
        YParser.ArchivoYContext arbolParseo = parser.archivoY();

        if (parser.getNumberOfSyntaxErrors() > 0 || !coleccionErrores.getLexicalErrors().isEmpty()) {
            return new CatalogoExportado(rutaArchivo, coleccionErrores.getErrors());
        }

        ConstructorASTY constructor = new ConstructorASTY();
        NodoAST raiz = constructor.visitArchivoY(arbolParseo);

        AnalizadorSemanticoY analizador = new AnalizadorSemanticoY(coleccionErrores);
        return analizador.analizar(rutaArchivo, (NodoArchivoY) raiz);
    }

}
