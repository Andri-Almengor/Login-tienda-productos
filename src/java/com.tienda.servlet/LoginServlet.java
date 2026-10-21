
package com.tienda.servlet;

import com.tienda.dao.UsuarioDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO =
        new UsuarioDAO();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.sendRedirect(
            request.getContextPath() + "/index.jsp"
        );
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String usuario =
            request.getParameter("usuario");

        String contrasena =
            request.getParameter("contrasena");

        if (usuario == null || contrasena == null) {
            response.sendRedirect(
                request.getContextPath()
                + "/index.jsp?error=1"
            );
            return;
        }

        usuario = usuario.trim();

        try {

            boolean valido = usuarioDAO.validar(
                usuario, contrasena
            );

            if (valido) {

                HttpSession anterior =
                    request.getSession(false);

                if (anterior != null) {
                    anterior.invalidate();
                }

                HttpSession sesion =
                    request.getSession(true);

                sesion.setAttribute(
                    "usuario", usuario
                );

                sesion.setAttribute(
                    "csrfToken",
                    UUID.randomUUID().toString()
                );

                sesion.setMaxInactiveInterval(1800);

                response.sendRedirect(
                    request.getContextPath()
                    + "/productos"
                );

            } else {

                response.sendRedirect(
                    request.getContextPath()
                    + "/index.jsp?error=1"
                );
            }

        } catch (SQLException e) {

            log("Error al iniciar sesion", e);

            response.sendRedirect(
                request.getContextPath()
                + "/index.jsp?error=bd"
            );
        }
    }
}
