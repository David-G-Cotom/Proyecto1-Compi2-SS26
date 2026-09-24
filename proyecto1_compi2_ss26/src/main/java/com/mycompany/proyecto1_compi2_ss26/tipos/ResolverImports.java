/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.tipos;

import com.mycompany.proyecto1_compi2_ss26.lenguajes.y.CompiladorY;
import com.mycompany.proyecto1_compi2_ss26.lenguajes.z.CompiladorZ;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author david
 */
public class ResolverImports {

    private final String carpetaRaizWorkspace;
    private final Map<String, CatalogoExportado> cache = new HashMap<>();

    public ResolverImports(String carpetaRaizWorkspace) {
        this.carpetaRaizWorkspace = carpetaRaizWorkspace;
    }

    public CatalogoExportado resolver(String rutaRelativaImport) throws Exception {
        String rutaAbsoluta = normalizar(rutaRelativaImport);

        if (cache.containsKey(rutaAbsoluta)) {
            return cache.get(rutaAbsoluta);
        }

        CatalogoExportado catalogo;
        if (rutaAbsoluta.endsWith(".y")) {
            catalogo = CompiladorY.compilarAislado(rutaAbsoluta);
        } else if (rutaAbsoluta.endsWith(".z")) {
            catalogo = CompiladorZ.compilarAislado(rutaAbsoluta);
        } else {
            throw new IllegalArgumentException("Extension de import invalida: " + rutaRelativaImport);
        }

        cache.put(rutaAbsoluta, catalogo);
        return catalogo;
    }

    private String normalizar(String rutaRelativaImport) {
        int ultimoPunto = rutaRelativaImport.lastIndexOf('.');
        String extension = rutaRelativaImport.substring(ultimoPunto); // ".y" o ".z"
        String sinExtension = rutaRelativaImport.substring(0, ultimoPunto);
        String rutaConSlashes = sinExtension.replace('.', '/') + extension;

        return Paths.get(carpetaRaizWorkspace, rutaConSlashes).normalize().toString();
    }

}
