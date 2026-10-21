
package com.tienda.util;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;

import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordUtil {

    private static final int ITERACIONES = 210000;

    public static boolean verificar(
            String clave, String hashGuardado) {

        if (clave == null || hashGuardado == null) {
            return false;
        }

        String[] partes = hashGuardado.split(":");

        if (partes.length != 2) {
            return false;
        }

        try {
            byte[] salt = Base64.getDecoder()
                                .decode(partes[0]);

            byte[] esperado = Base64.getDecoder()
                                    .decode(partes[1]);

            PBEKeySpec especificacion = new PBEKeySpec(
                clave.toCharArray(),
                salt,
                ITERACIONES,
                esperado.length * 8
            );

            byte[] calculado;

            try {
                SecretKeyFactory fabrica =
                    SecretKeyFactory.getInstance(
                        "PBKDF2WithHmacSHA256"
                    );

                calculado = fabrica
                    .generateSecret(especificacion)
                    .getEncoded();

            } finally {
                especificacion.clearPassword();
            }

            return MessageDigest.isEqual(
                esperado, calculado
            );

        } catch (GeneralSecurityException |
                 IllegalArgumentException e) {

            return false;
        }
    }
}
