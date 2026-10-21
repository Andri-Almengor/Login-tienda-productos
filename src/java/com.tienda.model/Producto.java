
package com.tienda.model;

import java.math.BigDecimal;

public class Producto {

    private int id;
    private String nombre;
    private BigDecimal precio;
    private int stock;

    public Producto(int id, String nombre,
                    BigDecimal precio, int stock) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
    }

    public Producto(String nombre,
                    BigDecimal precio, int stock) {
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public int getStock() {
        return stock;
    }
}
