/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1_compi2_ss26.tipos;

import com.mycompany.proyecto1_compi2_ss26.modelos.Primitivo;
import java.util.Objects;

/**
 *
 * @author david
 */
public class TipoPrimitivo extends TipoDato {

    private final Primitivo primitivo;

    public TipoPrimitivo(Primitivo primitivo) {
        this.primitivo = primitivo;
    }

    public Primitivo getPrimitivo() {
        return primitivo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.primitivo);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TipoPrimitivo other = (TipoPrimitivo) obj;
        return this.primitivo == other.primitivo;
    }

}
