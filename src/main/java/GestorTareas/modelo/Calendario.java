/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GestorTareas.modelo;

/**
 *
 * @author jesuz
 */
import java.util.List;

public class Calendario {
    public enum VistaActual { Semanal, Mensual }

    private VistaActual vistaActual;
    private List<Tarea> tareas;

    public Calendario(VistaActual vistaActual, List<Tarea> tareas) {
        this.vistaActual = vistaActual;
        this.tareas = tareas;
    }

    public VistaActual getVistaActual() { return vistaActual; }
    public void setVistaActual(VistaActual vistaActual) { this.vistaActual = vistaActual; }
    public List<Tarea> getTareas() { return tareas; }
    public void setTareas(List<Tarea> tareas) { this.tareas = tareas; }

    // Métodos
    public void mostrarVistaSemanal() {
        this.vistaActual = VistaActual.Semanal;
        System.out.println("Mostrando vista semanal");
    }

    public void mostrarVistaMensual() {
        this.vistaActual = VistaActual.Mensual;
        System.out.println("Mostrando vista mensual");
    }

    public void resaltarDiasConTareas() {
        System.out.println("Resaltando días con tareas");
    }
}