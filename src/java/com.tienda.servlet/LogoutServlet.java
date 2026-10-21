
package com.tienda.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

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

        HttpSession sesion = request.getSession(false);

        if (sesion != null) {

            String esperado = (String)
                sesion.getAttribute("csrfToken");

            String recibido =
                request.getParameter("csrfToken");

            if (esperado == null
                    || !esperado.equals(recibido)) {

                response.sendError(
                    HttpServletResponse.SC_FORBIDDEN
                );
                return;
            }

            sesion.invalidate();
        }

        response.sendRedirect(
            request.getContextPath()
            + "/index.jsp?salida=1"
        );
    }
}
