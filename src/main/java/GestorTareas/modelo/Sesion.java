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

public class Sesion {
    private String tokenSesion;
    private Usuario usuario;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaExpiracion;

    public Sesion(String tokenSesion, Usuario usuario, LocalDateTime fechaInicio, LocalDateTime fechaExpiracion) {
        this.tokenSesion = tokenSesion;
        this.usuario = usuario;
        this.fechaInicio = fechaInicio;
        this.fechaExpiracion = fechaExpiracion;
    }

    public String getTokenSesion() { return tokenSesion; }
    public void setTokenSesion(String tokenSesion) { this.tokenSesion = tokenSesion; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDateTime getFechaExpiracion() { return fechaExpiracion; }
    public void setFechaExpiracion(LocalDateTime fechaExpiracion) { this.fechaExpiracion = fechaExpiracion; }

    // Métodos
    public static boolean iniciarSesion(String correo, String contraseña) {
        return true;
    }

    public void cerrarSesion() {
        this.tokenSesion = null;
    }

    public boolean validarSesion() {
        return LocalDateTime.now().isBefore(fechaExpiracion);
    }
}