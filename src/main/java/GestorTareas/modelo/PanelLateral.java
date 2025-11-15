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
import java.util.stream.Collectors;

public class PanelLateral {
    private List<String> categorias;

    public PanelLateral(List<String> categorias) {
        this.categorias = categorias;
    }

    public List<String> getCategorias() { return categorias; }
    public void setCategorias(List<String> categorias) { this.categorias = categorias; }

    // Métodos
    public List<Tarea> filtrarPorCategoria(String categoria, List<Tarea> tareas) {
        return tareas.stream()
                .filter(t -> t.getAsignatura().equals(categoria))
                .collect(Collectors.toList());
    }
}