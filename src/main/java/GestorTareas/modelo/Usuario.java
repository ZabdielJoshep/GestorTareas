/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GestorTareas.modelo;

/**
 *
 * @author jesuz
 */
import java.time.LocalDateTime;
import java.util.regex.Pattern;

public class Usuario {
    private int idUsuario;
    private String nombre;
    private String correoElectronico;
    private String contraseña; 


    public Usuario(int idUsuario, String nombre, String correoElectronico, String contraseña) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.correoElectronico = correoElectronico;
        this.contraseña = contraseña;
    }


    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCorreoElectronico() { return correoElectronico; }
    public void setCorreoElectronico(String correoElectronico) { this.correoElectronico = correoElectronico; }
    public String getContraseña() { return contraseña; }
    public void setContraseña(String contraseña) { this.contraseña = contraseña; }


    public static Usuario registrar(String nombre, String correo, String contraseña) {

        if (validarCorreo(correo)) {
 
            return new Usuario(0, nombre, correo, contraseña); 
        }
        return null;
    }

    public boolean iniciarSesion(String correo, String contraseña) {
        return this.correoElectronico.equals(correo) && this.contraseña.equals(contraseña);
    }

    public boolean cambiarContraseña(String contraseñaActual, String contraseñaNueva) {
        if (this.contraseña.equals(contraseñaActual)) {
            this.contraseña = contraseñaNueva;
            return true;
        }
        return false;
    }

    public void cerrarSesion() {
        System.out.println("Sesión cerrada para usuario: " + nombre);
    }

    public static boolean validarCorreo(String correo) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(correo).matches();
    }
}