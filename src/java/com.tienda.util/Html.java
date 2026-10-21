
package com.tienda.util;

public class Html {

    public static String escapar(String texto) {

        if (texto == null) {
            return "";
        }

        return texto
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");
    }
}
