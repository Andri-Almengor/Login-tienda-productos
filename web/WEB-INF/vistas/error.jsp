
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.tienda.util.Html"%>

<%
    String mensaje =
        (String) request.getAttribute("mensaje");

    if (mensaje == null) {
        mensaje = "Ocurrió un error inesperado.";
    }
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1">

    <title>Error - Tienda</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">
</head>

<body class="bg-light">

<div class="container py-5">

    <div class="alert alert-danger">

        <h4>No se pudo completar la solicitud</h4>

        <p><%= Html.escapar(mensaje) %></p>

        <a href="<%= request.getContextPath() %>/productos"
           class="btn btn-primary">
            Volver
        </a>

    </div>

</div>

</body>
</html>
