
package com.tienda.util;

import com.tienda.model.Producto;
import java.math.BigDecimal;

public class ProductoValidator {

    public static Producto validar(
            String nombre,
            String precioTexto,
            String stockTexto) {

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException(
                "El nombre es obligatorio."
            );
        }

        nombre = nombre.trim();

        if (nombre.length() > 100) {
            throw new IllegalArgumentException(
                "El nombre no puede superar 100 caracteres."
            );
        }

        BigDecimal precio;

        try {
            precio = new BigDecimal(
                precioTexto.trim().replace(",", ".")
            );
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "Ingrese un precio válido."
            );
        }

        if (precio.signum() < 0
                || precio.scale() > 2
                || precio.compareTo(
                    new BigDecimal("99999999.99")) > 0) {

            throw new IllegalArgumentException(
                "El precio debe ser positivo o cero "
                + "y tener máximo dos decimales."
            );
        }

        int stock;

        try {
            stock = Integer.parseInt(stockTexto.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "Ingrese una cantidad entera válida."
            );
        }

        if (stock < 0) {
            throw new IllegalArgumentException(
                "El stock no puede ser negativo."
            );
        }

        return new Producto(nombre, precio, stock);
    }
}
