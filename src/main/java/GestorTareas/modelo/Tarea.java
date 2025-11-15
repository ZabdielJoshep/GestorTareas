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

public class Tarea {
    public enum Estado { Pendiente, Completada }
    public enum Prioridad { Importante, Normal }

    private int idTarea;
    private String tituloTarea;
    private String descripcionTarea;
    private LocalDateTime fechaDeVencimiento;
    private String asignatura;
    private Estado estado;
    private Prioridad prioridad;

    public Tarea(int idTarea, String tituloTarea, String descripcionTarea, LocalDateTime fechaDeVencimiento, String asignatura, Estado estado, Prioridad prioridad) {
        this.idTarea = idTarea;
        this.tituloTarea = tituloTarea;
        this.descripcionTarea = descripcionTarea;
        this.fechaDeVencimiento = fechaDeVencimiento;
        this.asignatura = asignatura;
        this.estado = estado;
        this.prioridad = prioridad;
    }

    public int getIdTarea() { return idTarea; }
    public void setIdTarea(int idTarea) { this.idTarea = idTarea; }
    public String getTituloTarea() { return tituloTarea; }
    public void setTituloTarea(String tituloTarea) { this.tituloTarea = tituloTarea; }
    public String getDescripcionTarea() { return descripcionTarea; }
    public void setDescripcionTarea(String descripcionTarea) { this.descripcionTarea = descripcionTarea; }
    public LocalDateTime getFechaDeVencimiento() { return fechaDeVencimiento; }
    public void setFechaDeVencimiento(LocalDateTime fechaDeVencimiento) { this.fechaDeVencimiento = fechaDeVencimiento; }
    public String getAsignatura() { return asignatura; }
    public void setAsignatura(String asignatura) { this.asignatura = asignatura; }
    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
    public Prioridad getPrioridad() { return prioridad; }
    public void setPrioridad(Prioridad prioridad) { this.prioridad = prioridad; }

    public static Tarea crearTarea(String titulo, String descripcion, LocalDateTime fechaVencimiento, String asignatura) {
        return new Tarea(0, titulo, descripcion, fechaVencimiento, asignatura, Estado.Pendiente, Prioridad.Normal);
    }

    public void editarTarea(String titulo, String descripcion, LocalDateTime fechaVencimiento, String asignatura, Estado estado, Prioridad prioridad) {
        this.tituloTarea = titulo;
        this.descripcionTarea = descripcion;
        this.fechaDeVencimiento = fechaVencimiento;
        this.asignatura = asignatura;
        this.estado = estado;
        this.prioridad = prioridad;
    }

    public void marcarComoCompletada() {
        this.estado = Estado.Completada;
    }

    public boolean validarFechaVencimiento() {
        return fechaDeVencimiento != null && fechaDeVencimiento.isAfter(LocalDateTime.now());
    }
}