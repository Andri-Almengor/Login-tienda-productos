
package com.tienda.dao;

import com.tienda.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    public boolean validar(
            String usuario, String contrasena)
            throws SQLException {

        String sql =
            "SELECT contrasena_hash "
            + "FROM usuarios "
            + "WHERE nombre_usuario = ?";

        try (
            Connection conexion =
                Conexion.obtenerConexion();

            PreparedStatement ps =
                conexion.prepareStatement(sql)
        ) {

            ps.setString(1, usuario);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    String hash =
                        rs.getString("contrasena_hash");

                    return PasswordUtil.verificar(
                        contrasena, hash
                    );
                }
            }
        }

        return false;
    }
}
