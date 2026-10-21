
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.tienda.model.Producto"%>
<%@page import="com.tienda.util.Html"%>

<%
    List<Producto> productos =
        (List<Producto>) request.getAttribute("productos");

    String usuario =
        (String) session.getAttribute("usuario");

    String token =
        (String) session.getAttribute("csrfToken");

    String mensaje =
        request.getParameter("mensaje");
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1">

    <title>Listado de Productos</title>

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

    <div class="d-flex justify-content-between
                align-items-center flex-wrap gap-3 mb-4">

        <div>
            <h2 class="mb-1">Productos registrados</h2>
            <p class="text-muted mb-0">
                Administración de inventario
            </p>
        </div>

        <a href="<%= request.getContextPath() %>/registro.jsp"
           class="btn btn-success">
            + Nuevo producto
        </a>

    </div>

    <% if ("creado".equals(mensaje)) { %>

        <div class="alert alert-success">
            Producto registrado correctamente.
        </div>

    <% } else if ("eliminado".equals(mensaje)) { %>

        <div class="alert alert-success">
            Producto eliminado correctamente.
        </div>

    <% } else if ("noexiste".equals(mensaje)) { %>

        <div class="alert alert-warning">
            El producto ya no existe.
        </div>

    <% } else if ("error".equals(mensaje)) { %>

        <div class="alert alert-danger">
            No se pudo completar la operación.
        </div>

    <% } %>

    <div class="card shadow-sm border-0">

        <div class="card-body">

            <div class="table-responsive">

                <table class="table table-striped
                              table-hover align-middle">

                    <thead class="table-dark">

                        <tr>
                            <th>ID</th>
                            <th>Nombre</th>
                            <th>Precio</th>
                            <th>Stock</th>
                            <th>Acciones</th>
                        </tr>

                    </thead>

                    <tbody>

                    <% if (productos != null
                            && !productos.isEmpty()) { %>

                        <% for (Producto producto : productos) { %>

                            <tr>

                                <td>
                                    <%= producto.getId() %>
                                </td>

                                <td>
                                    <%= Html.escapar(producto.getNombre()) %>
                                </td>

                                <td>
                                    <%= producto.getPrecio().toPlainString() %>
                                </td>

                                <td>
                                    <%= producto.getStock() %>
                                </td>

                                <td>

                                    <form action="<%= request.getContextPath() %>/productos"
                                          method="POST"
                                          onsubmit="return confirm('¿Seguro que deseas eliminar este producto?');">

                                        <input type="hidden"
                                               name="accion"
                                               value="eliminar">

                                        <input type="hidden"
                                               name="id"
                                               value="<%= producto.getId() %>">

                                        <input type="hidden"
                                               name="csrfToken"
                                               value="<%= Html.escapar(token) %>">

                                        <button type="submit"
                                                class="btn btn-danger btn-sm">
                                            Eliminar
                                        </button>

                                    </form>

                                </td>
                            </tr>

                        <% } %>

                    <% } else { %>

                        <tr>
                            <td colspan="5"
                                class="text-center py-4 text-muted">
                                No hay productos registrados.
                            </td>
                        </tr>

                    <% } %>

                    </tbody>

                </table>

            </div>

            <p class="text-muted small mb-0">

                Total de productos:
                <strong>
                    <%= productos == null ? 0 : productos.size() %>
                </strong>

            </p>

        </div>
    </div>

</div>

</body>
</html>
