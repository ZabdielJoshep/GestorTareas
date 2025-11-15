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
import java.util.List;
import java.util.stream.Collectors;

public class PanelPrincipal {
    private List<Tarea> listaTareas;
    private PanelLateral panelLateral;

    public PanelPrincipal(List<Tarea> listaTareas, PanelLateral panelLateral) {
        this.listaTareas = listaTareas;
        this.panelLateral = panelLateral;
    }

    public List<Tarea> getListaTareas() { return listaTareas; }
    public void setListaTareas(List<Tarea> listaTareas) { this.listaTareas = listaTareas; }
    public PanelLateral getPanelLateral() { return panelLateral; }
    public void setPanelLateral(PanelLateral panelLateral) { this.panelLateral = panelLateral; }

    // Métodos
    public void mostrarTareas() {
        for (Tarea tarea : listaTareas) {
            System.out.println(tarea.getTituloTarea());
        }
    }

    public List<Tarea> filtrarTareas(Tarea.Estado filtroEstado) {
        return listaTareas.stream()
                .filter(t -> t.getEstado() == filtroEstado)
                .collect(Collectors.toList());
    }

    public void ordenarTareasPorFecha() {
        listaTareas.sort((t1, t2) -> t1.getFechaDeVencimiento().compareTo(t2.getFechaDeVencimiento()));
    }

    public void mostrarFechaHoraActual() {
        System.out.println("Fecha y hora actual: " + LocalDateTime.now());
    }
}