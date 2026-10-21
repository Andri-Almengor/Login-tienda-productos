
package com.tienda.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private static final String URL =
        "jdbc:mysql://localhost:3305/tienda"
        + "?useSSL=false"
        + "&allowPublicKeyRetrieval=true"
        + "&serverTimezone=UTC";

    private static final String USUARIO = "tienda_app";
    private static final String CLAVE = "Tienda2026!";

    public static Connection obtenerConexion()
            throws SQLException {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                "No se encontro el driver MySQL.", e
            );
        }

        return DriverManager.getConnection(
            URL, USUARIO, CLAVE
        );
    }
}
