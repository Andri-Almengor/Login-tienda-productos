
package com.tienda.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter(urlPatterns = {
    "/productos",
    "/registro.jsp"
})
public class AutenticacionFilter implements Filter {

    @Override
    public void doFilter(
            ServletRequest servletRequest,
            ServletResponse servletResponse,
            FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request =
            (HttpServletRequest) servletRequest;

        HttpServletResponse response =
            (HttpServletResponse) servletResponse;

        response.setHeader(
            "Cache-Control",
            "no-store, no-cache, must-revalidate"
        );

        HttpSession sesion =
            request.getSession(false);

        boolean autenticado =
            sesion != null
            && sesion.getAttribute("usuario") != null;

        if (autenticado) {

            chain.doFilter(request, response);

        } else {

            response.sendRedirect(
                request.getContextPath()
                + "/index.jsp"
            );
        }
    }
}
