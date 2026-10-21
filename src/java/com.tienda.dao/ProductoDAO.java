
package com.tienda.dao;

import com.tienda.model.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    // Registrar un producto
    public boolean insertar(Producto producto)
            throws SQLException {

        String sql = "INSERT INTO productos "
                   + "(nombre, precio, stock) "
                   + "VALUES (?, ?, ?)";

        try (
            Connection conexion =
                Conexion.obtenerConexion();

            PreparedStatement ps =
                conexion.prepareStatement(sql)
        ) {

            ps.setString(1, producto.getNombre());
            ps.setBigDecimal(2, producto.getPrecio());
            ps.setInt(3, producto.getStock());

            return ps.executeUpdate() > 0;
        }
    }

    // Consultar todos los productos
    public List<Producto> listar()
            throws SQLException {

        List<Producto> productos = new ArrayList<>();

        String sql = "SELECT id, nombre, precio, stock "
                   + "FROM productos ORDER BY id ASC";

        try (
            Connection conexion =
                Conexion.obtenerConexion();

            PreparedStatement ps =
                conexion.prepareStatement(sql);

            ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                Producto producto = new Producto(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getBigDecimal("precio"),
                    rs.getInt("stock")
                );

                productos.add(producto);
            }
        }

        return productos;
    }

    // Eliminar un producto por ID
    public boolean eliminar(int id)
            throws SQLException {

        String sql =
            "DELETE FROM productos WHERE id = ?";

        try (
            Connection conexion =
                Conexion.obtenerConexion();

            PreparedStatement ps =
                conexion.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;
        }
    }
}
