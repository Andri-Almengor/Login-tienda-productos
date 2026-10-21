
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.tienda.util.Html"%>

<%
    String usuario =
        (String) session.getAttribute("usuario");

    String token =
        (String) session.getAttribute("csrfToken");

    String error = request.getParameter("error");
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1">

    <title>Registrar Producto</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <link href="<%= request.getContextPath() %>/css/estilos.css"
          rel="stylesheet">
</head>

<body>

<nav class="navbar navbar-dark bg-primary">
    <div class="container">

        <a class="navbar-brand"
           href="<%= request.getContextPath() %>/productos">
            Sistema de Tienda
        </a>

        <div class="d-flex align-items-center gap-3">

            <span class="text-white d-none d-sm-inline">
                <%= Html.escapar(usuario) %>
            </span>

            <form action="<%= request.getContextPath() %>/logout"
                  method="POST">

                <input type="hidden"
                       name="csrfToken"
                       value="<%= Html.escapar(token) %>">

                <button type="submit"
                        class="btn btn-light btn-sm">
                    Cerrar sesión
                </button>
            </form>

        </div>
    </div>
</nav>

<div class="container py-5">

    <div class="row justify-content-center">
        <div class="col-md-7 col-lg-6">

            <div class="card shadow-sm border-0">

                <div class="card-body p-4">

                    <h3 class="mb-4">
                        Registrar nuevo producto
                    </h3>

                    <% if ("datos".equals(error)) { %>

                        <div class="alert alert-warning">
                            Revisa el nombre, el precio
                            y el stock ingresados.
                        </div>

                    <% } else if ("bd".equals(error)) { %>

                        <div class="alert alert-danger">
                            No se pudo guardar el producto.
                        </div>

                    <% } %>

                    <form action="<%= request.getContextPath() %>/productos"
                          method="POST">

                        <input type="hidden"
                               name="accion"
                               value="registrar">

                        <input type="hidden"
                               name="csrfToken"
                               value="<%= Html.escapar(token) %>">

                        <div class="mb-3">

                            <label class="form-label">
                                Nombre del producto
                            </label>

                            <input type="text"
                                   name="nombre"
                                   class="form-control"
                                   placeholder="Ej: Teclado"
                                   maxlength="120"
                                   required>

                        </div>

                        <div class="mb-3">

                            <label class="form-label">
                                Precio
                            </label>

                            <input type="number"
                                   name="precio"
                                   class="form-control"
                                   min="0"
                                   max="99999999.99"
                                   step="0.01"
                                   placeholder="0.00"
                                   required>

                        </div>

                        <div class="mb-4">

                            <label class="form-label">
                                Stock
                            </label>

                            <input type="number"
                                   name="stock"
                                   class="form-control"
                                   min="0"
                                   step="1"
                                   placeholder="0"
                                   required>

                        </div>

                        <div class="d-flex gap-2">

                            <button type="submit"
                                    class="btn btn-success">
                                Guardar producto
                            </button>

                            <a href="<%= request.getContextPath() %>/productos"
                               class="btn btn-secondary">
                                Cancelar
                            </a>

                        </div>

                    </form>

                </div>
            </div>

        </div>
    </div>

</div>

</body>
</html>
