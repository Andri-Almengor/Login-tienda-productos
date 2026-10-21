
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
    if (session.getAttribute("usuario") != null) {
        response.sendRedirect(
            request.getContextPath() + "/productos"
        );
        return;
    }

    String error = request.getParameter("error");
    String salida = request.getParameter("salida");
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1">

    <title>Iniciar sesión - Tienda</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <link href="<%= request.getContextPath() %>/css/estilos.css"
          rel="stylesheet">
</head>

<body>

<div class="container">
    <div class="row justify-content-center
                align-items-center min-vh-100">

        <div class="col-md-5 col-lg-4">

            <div class="card shadow border-0">

                <div class="card-body p-4">

                    <h2 class="text-center mb-2">
                        Sistema de Tienda
                    </h2>

                    <p class="text-center text-muted mb-4">
                        Inicia sesión para continuar
                    </p>

                    <% if ("1".equals(error)) { %>

                        <div class="alert alert-danger">
                            Usuario o contraseña incorrectos.
                        </div>

                    <% } else if ("bd".equals(error)) { %>

                        <div class="alert alert-danger">
                            No se pudo conectar con la
                            base de datos.
                        </div>

                    <% } %>

                    <% if ("1".equals(salida)) { %>

                        <div class="alert alert-success">
                            Sesión cerrada correctamente.
                        </div>

                    <% } %>

                    <form action="<%= request.getContextPath() %>/login"
                          method="POST">

                        <div class="mb-3">
                            <label class="form-label">
                                Usuario
                            </label>

                            <input type="text"
                                   name="usuario"
                                   class="form-control"
                                   maxlength="80"
                                   autocomplete="username"
                                   required>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">
                                Contraseña
                            </label>

                            <input type="password"
                                   name="contrasena"
                                   class="form-control"
                                   autocomplete="current-password"
                                   required>
                        </div>

                        <button type="submit"
                                class="btn btn-primary w-100">
                            Iniciar sesión
                        </button>

                    </form>

                </div>
            </div>

        </div>
    </div>
</div>

</body>
</html>
