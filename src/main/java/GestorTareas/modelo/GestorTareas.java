/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GestorTareas.modelo;

/**
 *
 * @author jesuz
 */
import java.sql.SQLException;
import java.util.List;

public class GestorTareas {
    private List<Tarea> listaTareas;
    private BaseDeDatos baseDeDatos;

    public GestorTareas(List<Tarea> listaTareas, BaseDeDatos baseDeDatos) {
        this.listaTareas = listaTareas;
        this.baseDeDatos = baseDeDatos;
    }

    public List<Tarea> getListaTareas() { return listaTareas; }
    public void setListaTareas(List<Tarea> listaTareas) { this.listaTareas = listaTareas; }
    public BaseDeDatos getBaseDeDatos() { return baseDeDatos; }
    public void setBaseDeDatos(BaseDeDatos baseDeDatos) { this.baseDeDatos = baseDeDatos; }

    public void cargarTareas(int idUsuario) throws SQLException {
        this.listaTareas = baseDeDatos.obtenerTareasPorUsuario(idUsuario);
    }

    public void guardarTarea(Tarea tarea, int idUsuario) throws SQLException {
        baseDeDatos.guardarTarea(tarea, idUsuario);
    }

    public void editarTarea(Tarea tarea) throws SQLException {
        baseDeDatos.editarTarea(tarea);
    }

    public void eliminarTarea(int idTarea) throws SQLException {
        baseDeDatos.eliminarTarea(idTarea);
    }

    public List<Tarea> filtrarTareas(String filtro) {
        return listaTareas.stream()
                .filter(t -> t.getTituloTarea().contains(filtro))
                .toList();
    }
}