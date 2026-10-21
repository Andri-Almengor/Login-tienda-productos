# Aplicación web de tienda con Java, JSP, Servlets, DAO y MySQL

**Universidad Castro Carazo**  
**Curso:** 2026C3-G01-PROGRAMACIÓN DE COMPUTADORAS II  
**Autor:** Andrick Ivan Almengor Quirós  
**Fecha:** 21 de octubre de 2026

> Informe académico y documentación técnica del proyecto. Versión sin imágenes; las pruebas se describen en texto.

## 1. Índice

- [2. Repositorio GitHub](#seccion-2)
- [3. Creación de la base de datos](#seccion-3)
  - [3.1 Diseño de las tablas](#seccion-3-1)
  - [3.2 Script SQL e inserciones iniciales](#seccion-3-2)
  - [3.3 Usuario MySQL y credenciales de acceso](#seccion-3-3)
  - [3.4 Consultas de comprobación](#seccion-3-4)
- [4. Explicación del código](#seccion-4)
  - [4.1 Arquitectura del sistema](#seccion-4-1)
  - [4.2 Modelo Producto.java](#seccion-4-2)
  - [4.3 Clase Conexion.java](#seccion-4-3)
  - [4.4 Clases ProductoDAO.java y UsuarioDAO.java](#seccion-4-4)
  - [4.5 Autenticación y control de sesión](#seccion-4-5)
  - [4.6 Controlador ProductoServlet.java](#seccion-4-6)
  - [4.7 Páginas JSP y Bootstrap](#seccion-4-7)
  - [4.8 Flujo general de funcionamiento](#seccion-4-8)
- [5. Configuración de JDBC, NetBeans y Tomcat](#seccion-5)
  - [5.1 Entorno y librerías](#seccion-5-1)
  - [5.2 Puerto MySQL 3305](#seccion-5-2)
  - [5.3 Compilación y despliegue](#seccion-5-3)
- [6. Solución de problemas encontrados](#seccion-6)
  - [6.1 Error de formato en web.xml](#seccion-6-1)
  - [6.2 Validación del inicio de sesión](#seccion-6-2)
  - [6.3 Comprobaciones JDBC](#seccion-6-3)
  - [6.4 Resumen del diagnóstico](#seccion-6-4)
- [7. Evidencias](#seccion-7)
- [8. Referencias APA](#seccion-8)

---

<a id="seccion-2"></a>

## 2. Repositorio GitHub

El repositorio destinado a almacenar el código del proyecto se llama Login-tienda-productos y se encuentra en la cuenta Andri-Almengor. Dirección: https://github.com/Andri-Almengor/Login-tienda-productos

El presente repositorio contiene esta documentación en `README.md`, y fue definido como destino del proyecto desarrollado con NetBeans. Esta actualización incluye el informe sin capturas. El código ejecutable y el script completo de la base de datos se deben incorporar desde los archivos locales del proyecto; esta documentación no sustituye dichos archivos.

<a id="seccion-3"></a>

## 3. Creación de la base de datos

La aplicación utiliza una base de datos relacional MySQL llamada tienda. Allí se almacenan los productos registrados y las credenciales de acceso de los usuarios. MySQL Workbench permitió preparar y consultar los registros mientras que Java accede a ellos utilizando JDBC.

<a id="seccion-3-1"></a>

### 3.1 Diseño de las tablas

La tabla productos comprende los campos id, nombre, precio y stock. La tabla usuarios almacena el nombre único del usuario y un hash de contraseña. Mantener separados el inventario y la autenticación permite asignar a cada clase sus propias responsabilidades.

```sql
CREATE DATABASE IF NOT EXISTS tienda CHARACTER SET utf8mb4;
USE tienda;
CREATE TABLE IF NOT EXISTS productos (
id INT AUTO_INCREMENT PRIMARY KEY,
nombre VARCHAR(120) NOT NULL,
precio DECIMAL(10,2) NOT NULL,
stock INT NOT NULL
);
CREATE TABLE IF NOT EXISTS usuarios (
id INT AUTO_INCREMENT PRIMARY KEY,
nombre_usuario VARCHAR(80) NOT NULL UNIQUE,
contrasena_hash VARCHAR(160) NOT NULL
);
```

El campo id utiliza INT, AUTO_INCREMENT y PRIMARY KEY, por lo que MySQL proporciona un identificador único al insertar un nuevo registro. DECIMAL(10,2) mantiene dos decimales para los precios; UNIQUE evita nombres de usuario repetidos.

<a id="seccion-3-2"></a>

### 3.2 Script SQL e inserciones iniciales

El requerimiento establecía un mínimo de cinco productos iniciales. Se incorporaron artículos informáticos de ejemplo mediante una instrucción INSERT con nombre, precio y stock.

```sql
INSERT INTO productos (nombre, precio, stock) VALUES
('Laptop Lenovo',350000.00,10),
('Mouse Logitech',8500.00,25),
('Teclado Redragon',25000.00,15),
('Monitor Samsung',95000.00,8),
('Audifonos Sony',18000.00,20);
```

Esta carga inicial debe ejecutarse una sola vez para no generar duplicados. La cantidad total visible en las capturas es superior a cinco porque en la instancia de práctica ya existían otros productos.

<a id="seccion-3-3"></a>

### 3.3 Usuario MySQL y credenciales de acceso

Se creó el usuario tienda_app para que la aplicación no utilizara directamente root. Sus permisos corresponden a la consulta de usuarios y a las operaciones SELECT, INSERT y DELETE sobre productos.

```sql
CREATE USER IF NOT EXISTS 'tienda_app'@'localhost'
IDENTIFIED BY 'CLAVE_LOCAL_NO_PUBLICAR';
GRANT SELECT, INSERT, DELETE ON tienda.*
TO 'tienda_app'@'localhost';
```

Para el inicio de sesión de la aplicación se utilizó el usuario de prueba admin. La contraseña se verifica mediante un hash PBKDF2 con HMAC-SHA256 utilizando PasswordUtil.java, evitando almacenarla como texto plano en la tabla usuarios. Las contraseñas de la conexión MySQL solo corresponden a un entorno local y no deben publicarse en repositorios abiertos.

<a id="seccion-3-4"></a>

### 3.4 Consultas de comprobación

Para confirmar el estado de la base de datos y su conexión se utilizaron las siguientes consultas de MySQL:

```sql
SELECT @@hostname AS servidor, @@port AS puerto,
USER() AS conexion, CURRENT_USER() AS usuario_autenticado;
SHOW GRANTS FOR 'tienda_app'@'localhost';
SELECT * FROM tienda.productos;
SELECT id, nombre_usuario FROM tienda.usuarios;
```

En esta computadora MySQL utiliza el puerto 3305. Es importante verificarlo con SELECT @@port antes de cambiar la URL JDBC; otra instalación podría utilizar el puerto 3306.

<a id="seccion-4"></a>

## 4. Explicación del código

La aplicación se estructuró con el patrón Modelo–Vista–Controlador (MVC) y una capa de acceso a datos DAO. Las vistas JSP presentan la información; los Servlets procesan las solicitudes HTTP; los DAO ejecutan consultas preparadas; y MySQL almacena las filas.

<a id="seccion-4-1"></a>

### 4.1 Arquitectura del sistema

Paquetes principales: com.tienda.model, com.tienda.dao, com.tienda.servlet, com.tienda.filter y com.tienda.util. Las páginas se organizan en Web Pages, incluyendo index.jsp, registro.jsp, css/estilos.css y WEB-INF/vistas/listar.jsp. WEB-INF/web.xml contiene la configuración de la aplicación.

<a id="seccion-4-2"></a>

### 4.2 Modelo Producto.java

Producto.java representa una fila de la tabla productos. Define id y stock como enteros, nombre como String y precio como BigDecimal. Sus constructores permiten crear objetos con ID al consultar la base de datos y sin ID al registrar un artículo nuevo.

```java
private int id; private String nombre;
private BigDecimal precio; private int stock;
```

<a id="seccion-4-3"></a>

### 4.3 Clase Conexion.java

Conexion.java reúne los datos de la conexión JDBC y utiliza DriverManager.getConnection para abrirla. La clase carga el controlador com.mysql.cj.jdbc.Driver y devuelve un objeto Connection para que los DAO se comuniquen con MySQL.

```text
jdbc:mysql://localhost:3305/tienda
?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
```

Las opciones useSSL=false y allowPublicKeyRetrieval=true corresponden a una configuración local de práctica. En un sistema publicado debe utilizarse una conexión protegida y un mecanismo seguro de gestión de credenciales.

<a id="seccion-4-4"></a>

### 4.4 Clases ProductoDAO.java y UsuarioDAO.java

ProductoDAO.java reúne los métodos insertar(), listar() y eliminar(). Estos métodos abren conexiones dentro de bloques try-with-resources y utilizan PreparedStatement para enviar los parámetros a las consultas. UsuarioDAO.java consulta el hash guardado de cada usuario y llama a PasswordUtil.verificar().

```sql
INSERT INTO productos (nombre, precio, stock) VALUES (?, ?, ?);
SELECT id, nombre, precio, stock FROM productos ORDER BY id ASC;
DELETE FROM productos WHERE id = ?;
SELECT contrasena_hash FROM usuarios WHERE nombre_usuario = ?;
```

PreparedStatement permite pasar los valores con setString, setBigDecimal y setInt sin concatenarlos directamente dentro del SQL. Esta práctica mejora el tratamiento de tipos y reduce el riesgo de inyección SQL.

<a id="seccion-4-5"></a>

### 4.5 Autenticación y control de sesión

index.jsp muestra el formulario de acceso. LoginServlet recibe por POST el nombre y la contraseña, consulta UsuarioDAO y, cuando los datos son válidos, crea una sesión HTTP. También almacena un token para proteger las operaciones que modifican datos y fija 30 minutos como tiempo de inactividad.

```java
@WebServlet("/login")
HttpSession sesion = request.getSession(true);
sesion.setAttribute("usuario", usuario);
sesion.setMaxInactiveInterval(1800);
```

AutenticacionFilter.java impide acceder directamente a /productos o /registro.jsp sin una sesión activa. LogoutServlet.java invalida la sesión y devuelve al usuario al inicio de sesión.

<a id="seccion-4-6"></a>

### 4.6 Controlador ProductoServlet.java

ProductoServlet.java está asociado a la ruta /productos. El método doGet solicita al DAO la lista completa; doPost distingue las acciones registrar y eliminar por el parámetro accion. Antes de procesar un registro verifica los campos y, antes de eliminar, comprueba la sesión y el token.

```text
GET /productos: listar productos (SELECT).
POST accion=registrar: insertar producto (INSERT).
POST accion=eliminar: borrar producto por ID (DELETE).
```

Al concluir las modificaciones se hace una redirección al listado para evitar el reenvío accidental de los formularios al recargar la página.

<a id="seccion-4-7"></a>

### 4.7 Páginas JSP y Bootstrap

index.jsp presenta el formulario de usuario y contraseña y las alertas de acceso. registro.jsp ofrece los campos nombre, precio y stock. listar.jsp recibe los objetos Producto y genera una tabla con las columnas ID, Nombre, Precio, Stock y Acciones. error.jsp muestra problemas de carga sin exponer el detalle interno al usuario.

Bootstrap 5 se utiliza para diseñar tarjetas, formularios, botones, mensajes y tablas responsivas. Antes de ejecutar DELETE se abre un cuadro de confirmación que permite aceptar o cancelar; Html.java escapa los nombres al colocarlos en el HTML.

<a id="seccion-4-8"></a>

### 4.8 Flujo general de funcionamiento

El usuario abre index.jsp e introduce sus credenciales. LoginServlet valida la información con UsuarioDAO y PasswordUtil; si el acceso se autoriza, crea la sesión y redirige a /productos. ProductoServlet obtiene el inventario con ProductoDAO y envía la lista a listar.jsp. Desde allí es posible abrir registro.jsp para crear productos o solicitar su eliminación. Al cerrar sesión, LogoutServlet invalida la sesión y regresa a la pantalla de acceso.

<a id="seccion-5"></a>

## 5. Configuración de JDBC, NetBeans y Tomcat

El proyecto fue preparado en Apache NetBeans para su ejecución mediante Apache Tomcat. En los registros del desarrollo aparece Tomcat 11.0.11 y Java 24.0.1. El proyecto requiere la biblioteca MySQL Connector/J y las importaciones de Jakarta Servlet.

<a id="seccion-5-1"></a>

### 5.1 Entorno y librerías

Para añadir el conector se accede a las propiedades del proyecto, luego a Libraries y finalmente Add JAR/Folder, donde se incorpora mysql-connector-j-*.jar. Al utilizar Tomcat 11, las importaciones de Servlet corresponden a jakarta.servlet y no a javax.servlet.

<a id="seccion-5-2"></a>

### 5.2 Puerto MySQL 3305

La URL de la base de datos utiliza localhost:3305. Si se descarga el proyecto para ejecutarlo en otro equipo, primero es necesario confirmar el puerto real de MySQL, la existencia de la base tienda y los permisos del usuario configurado.

<a id="seccion-5-3"></a>

### 5.3 Compilación y despliegue

El descriptor WEB-INF/web.xml define el elemento raíz compatible con Jakarta EE 6.1, la página de bienvenida index.jsp y el tiempo de sesión. Después se ejecutaron Clean and Build y Run desde NetBeans. La aplicación se probó desde http://localhost:8080/WebApplication1/.

<a id="seccion-6"></a>

## 6. Solución de problemas encontrados

Durante el desarrollo se presentó un problema de arranque de la aplicación que no estaba relacionado con la ejecución del servidor Tomcat en sí. También se capturó el comportamiento de validación negativa de credenciales.

<a id="seccion-6-1"></a>

### 6.1 Error de formato en web.xml

Tomcat informó que la aplicación se había desplegado en /WebApplication1, pero que el contexto no podía arrancar. En el registro se encontró la excepción SAXParseException en la línea 2, columna 6, con el mensaje: «El destino de la instrucción de procesamiento que coincide con [xX][mM][lL] no está permitido». El problema se debía a una declaración XML duplicada o ubicada fuera de su posición válida.

Se sustituyó web.xml por un descriptor limpio con un único elemento raíz web-app y sin la declaración XML problemática. Tras guardar y volver a desplegar el proyecto, el navegador pudo abrir el formulario de acceso y las funciones de productos.

```java
<web-app xmlns="https://jakarta.ee/xml/ns/jakartaee"
version="6.1"> ... </web-app>
```

<a id="seccion-6-2"></a>

### 6.2 Validación del inicio de sesión

Las primeras capturas muestran la advertencia «Usuario o contraseña incorrectos». Es el mensaje de la aplicación cuando la autenticación no prospera. Una evidencia posterior muestra el listado con el usuario admin en la barra superior, confirmando que la aplicación también permitió un inicio de sesión válido.

<a id="seccion-6-3"></a>

### 6.3 Comprobaciones JDBC

No se documenta un error persistente de conexión JDBC en el resultado final. Al reproducir la práctica, conviene comprobar la presencia del controlador Connector/J, el puerto 3305 y los privilegios de tienda_app. El registro de Tomcat y SELECT @@port ayudan a distinguir errores de despliegue de errores de conexión.

<a id="seccion-6-4"></a>

### 6.4 Resumen del diagnóstico

La revisión del log permitió identificar la causa en web.xml, corregir su encabezado y desplegar correctamente el proyecto. Las pruebas posteriores evidencian listado de productos, registro, confirmación de eliminación, eliminación efectiva y cierre de sesión.

<a id="seccion-7"></a>

## 7. Evidencias

Durante la práctica se registraron diez pruebas visuales. En esta versión destinada a GitHub no se adjuntan imágenes; se conservan las descripciones de lo que demuestra cada captura.

**Prueba 1. Pantalla de advertencia de inicio de sesión**  
**Resultado observado:** La página de autenticación informa que el usuario o contraseña son incorrectos.

**Prueba 2. Formulario con credenciales ingresadas**  
**Resultado observado:** Se muestra el usuario admin escrito en el formulario, mientras permanece visible la advertencia anterior.

**Prueba 3. Listado inicial de productos con sesión activa**  
**Resultado observado:** En la barra superior aparece admin y la tabla muestra once productos registrados.

**Prueba 4. Confirmación antes de eliminar un registro**  
**Resultado observado:** El navegador solicita confirmar la eliminación del producto seleccionado.

**Prueba 5. Listado después de eliminar un producto**  
**Resultado observado:** Aparece el mensaje de eliminación correcta y el contador disminuye a diez productos.

**Prueba 6. Formulario de registro de producto**  
**Resultado observado:** Los campos muestran el nombre Prueba, un precio de 100 y stock de 1.

**Prueba 7. Registro del producto de prueba**  
**Resultado observado:** La alerta confirma el registro y aparece el producto Prueba con identificador 12.

**Prueba 8. Confirmación para eliminar el producto de prueba**  
**Resultado observado:** Se solicita autorización antes de eliminar el artículo añadido.

**Prueba 9. Comprobación de eliminación del producto de prueba**  
**Resultado observado:** Se informa la eliminación exitosa y el inventario vuelve a mostrar diez artículos.

**Prueba 10. Cierre de sesión del administrador**  
**Resultado observado:** La pantalla de acceso muestra el aviso de que la sesión se cerró correctamente.

Las pruebas describen un inicio de sesión válido, el listado desde MySQL, el registro y la eliminación de productos y el cierre de sesión, observados durante la ejecución local.

<a id="seccion-8"></a>

## 8. Referencias APA

Almengor Quirós, A. I. (2026). Login-tienda-productos [Documentación del proyecto]. GitHub. https://github.com/Andri-Almengor/Login-tienda-productos

Apache Software Foundation. (s. f.). Apache NetBeans. https://netbeans.apache.org/

Apache Software Foundation. (s. f.). Apache Tomcat 11 documentation. https://tomcat.apache.org/tomcat-11.0-doc/

Bootstrap. (s. f.). Bootstrap documentation. https://getbootstrap.com/docs/5.3/getting-started/introduction/

Eclipse Foundation. (2024). Jakarta Servlet Specification, Version 6.1. https://jakarta.ee/specifications/servlet/6.1/

Oracle. (s. f.). JDBC basics. https://docs.oracle.com/javase/tutorial/jdbc/basics/

Oracle. (s. f.). MySQL 8.0 Reference Manual. https://dev.mysql.com/doc/refman/8.0/en/

Oracle. (s. f.). MySQL Connector/J Developer Guide. https://dev.mysql.com/doc/connector-j/en/

---

> **Seguridad:** no publiques contraseñas reales ni claves de conexión. Cualquier credencial de ejemplo deberá sustituirse por valores locales y nunca reutilizarse en producción.
