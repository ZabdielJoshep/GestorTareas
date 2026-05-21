package GestorTareas.modelo;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class Encriptador {

    public static String hashearContraseña(String contraseña) {

        return BCrypt.withDefaults()
                .hashToString(10, contraseña.toCharArray());
    }

    public static boolean verificarContraseña(String contraseña, String hash) {
        if(hash.startsWith("$2a$")
                || hash.startsWith("$2b$")
                || hash.startsWith("$2y$")) {

            BCrypt.Result result = BCrypt.verifyer()
                    .verify(contraseña.toCharArray(), hash);

            return result.verified;
        }
        return contraseña.equals(hash);
    }
}