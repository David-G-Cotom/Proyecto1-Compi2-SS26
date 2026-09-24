/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.lenguajes.z;

import com.mycompany.ZetarianoLexer;
import com.mycompany.ZetarianoParser;
import com.mycompany.proyecto1_compi2_ss26.ast.NodoAST;
import com.mycompany.proyecto1_compi2_ss26.ast.z.NodoArchivoZ;
import com.mycompany.proyecto1_compi2_ss26.errores.RecolectorErrores;
import com.mycompany.proyecto1_compi2_ss26.lenguajes.z.semantica.AnalizadorSemanticoZ;
import com.mycompany.proyecto1_compi2_ss26.tipos.CatalogoExportado;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

/**
 *
 * @author david
 */
public class CompiladorZ {

    public static CatalogoExportado compilarAislado(String rutaArchivo) throws Exception {
        RecolectorErrores coleccionErrores = new RecolectorErrores();

        ZetarianoLexer lexer = new ZetarianoLexer(CharStreams.fromFileName(rutaArchivo));
        lexer.removeErrorListeners();
        lexer.addErrorListener(coleccionErrores);

        CommonTokenStream tokens = new CommonTokenStream(lexer);

        ZetarianoParser parser = new ZetarianoParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(coleccionErrores);
        ZetarianoParser.ArchivoZContext arbolParseo = parser.archivoZ();

        if (parser.getNumberOfSyntaxErrors() > 0 || !coleccionErrores.getLexicalErrors().isEmpty()) {
            return new CatalogoExportado(rutaArchivo, coleccionErrores.getErrors());
        }

        ConstructorASTZ constructor = new ConstructorASTZ();
        NodoAST raiz = constructor.visitArchivoZ(arbolParseo);

        AnalizadorSemanticoZ analizador = new AnalizadorSemanticoZ(coleccionErrores);
        return analizador.analizar(rutaArchivo, (NodoArchivoZ) raiz);
    }

}
