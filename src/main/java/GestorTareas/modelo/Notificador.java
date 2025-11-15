/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GestorTareas.modelo;

/**
 *
 * @author jesuz
 */
public class Notificador {
    // Métodos
    public static void mostrarMensajeError(String mensaje) {
        System.out.println("ERROR: " + mensaje);
    }

    public static void mostrarMensajeExito(String mensaje) {
        System.out.println("ÉXITO: " + mensaje);
    }
}