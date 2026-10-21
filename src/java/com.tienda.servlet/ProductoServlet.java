
package com.tienda.servlet;

import com.tienda.dao.ProductoDAO;
import com.tienda.model.Producto;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/productos")
public class ProductoServlet extends HttpServlet {

    private final ProductoDAO productoDAO =
        new ProductoDAO();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {

            List<Producto> productos =
                productoDAO.listar();

            request.setAttribute(
                "productos", productos
            );

            request.getRequestDispatcher(
                "/WEB-INF/vistas/listar.jsp"
            ).forward(request, response);

        } catch (SQLException e) {

            log("Error consultando productos", e);

            mostrarError(
                request, response,
                "No se pudo cargar el listado."
            );
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession sesion =
            request.getSession(false);

        if (sesion == null
                || sesion.getAttribute("usuario") == null) {

            response.sendRedirect(
                request.getContextPath()
                + "/index.jsp"
            );
            return;
        }

        // Comprobacion de seguridad para formularios
        String tokenEsperado = (String)
            sesion.getAttribute("csrfToken");

        String tokenRecibido =
            request.getParameter("csrfToken");

        if (tokenEsperado == null
                || !tokenEsperado.equals(tokenRecibido)) {

            response.sendError(
                HttpServletResponse.SC_FORBIDDEN,
                "Formulario no valido."
            );
            return;
        }

        String accion =
            request.getParameter("accion");

        if ("registrar".equals(accion)) {

            registrar(request, response);

        } else if ("eliminar".equals(accion)) {

            eliminar(request, response);

        } else {

            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "Accion no valida."
            );
        }
    }

    private void registrar(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        String nombre =
            request.getParameter("nombre");

        String precioTexto =
            request.getParameter("precio");

        String stockTexto =
            request.getParameter("stock");

        if (nombre == null
                || precioTexto == null
                || stockTexto == null) {

            errorRegistro(request, response, "datos");
            return;
        }

        nombre = nombre.trim();

        if (nombre.isEmpty() || nombre.length() > 120) {
            errorRegistro(request, response, "datos");
            return;
        }

        try {

            BigDecimal precio =
                new BigDecimal(precioTexto.trim())
                    .setScale(
                        2, RoundingMode.UNNECESSARY
                    );

            int stock =
                Integer.parseInt(stockTexto.trim());

            if (precio.compareTo(BigDecimal.ZERO) < 0
                    || precio.compareTo(
                        new BigDecimal("99999999.99")
                    ) > 0
                    || stock < 0) {

                errorRegistro(request, response, "datos");
                return;
            }

            Producto producto =
                new Producto(nombre, precio, stock);

            boolean registrado =
                productoDAO.insertar(producto);

            if (registrado) {

                response.sendRedirect(
                    request.getContextPath()
                    + "/productos?mensaje=creado"
                );

            } else {

                errorRegistro(request, response, "bd");
            }

        } catch (NumberFormatException |
                 ArithmeticException e) {

            errorRegistro(request, response, "datos");

        } catch (SQLException e) {

            log("Error registrando producto", e);
            errorRegistro(request, response, "bd");
        }
    }

    private void eliminar(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        try {

            int id = Integer.parseInt(
                request.getParameter("id")
            );

            if (id <= 0) {
                throw new NumberFormatException();
            }

            boolean eliminado =
                productoDAO.eliminar(id);

            if (eliminado) {

                response.sendRedirect(
                    request.getContextPath()
                    + "/productos?mensaje=eliminado"
                );

            } else {

                response.sendRedirect(
                    request.getContextPath()
                    + "/productos?mensaje=noexiste"
                );
            }

        } catch (NumberFormatException e) {

            response.sendRedirect(
                request.getContextPath()
                + "/productos?mensaje=error"
            );

        } catch (SQLException e) {

            log("Error eliminando producto", e);

            response.sendRedirect(
                request.getContextPath()
                + "/productos?mensaje=error"
            );
        }
    }

    private void errorRegistro(
            HttpServletRequest request,
            HttpServletResponse response,
            String tipo)
            throws IOException {

        response.sendRedirect(
            request.getContextPath()
            + "/registro.jsp?error=" + tipo
        );
    }

    private void mostrarError(
            HttpServletRequest request,
            HttpServletResponse response,
            String mensaje)
            throws ServletException, IOException {

        request.setAttribute("mensaje", mensaje);

        response.setStatus(
            HttpServletResponse.SC_INTERNAL_SERVER_ERROR
        );

        request.getRequestDispatcher(
            "/WEB-INF/vistas/error.jsp"
        ).forward(request, response);
    }
}
