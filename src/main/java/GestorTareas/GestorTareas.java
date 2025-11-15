/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package GestorTareas;

/**
 *
 * @author jesuz
 */
import GestorTareas.modelo.BaseDeDatos;
import GestorTareas.modelo.Notificador;
import GestorTareas.vista.VentanaLogin;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.UIManager;
import java.sql.SQLException;

public class GestorTareas {
    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            BaseDeDatos db = new BaseDeDatos();
            db.conectar();
            System.out.println("Conexion exitosa a la base de datos MySQL.");

            javax.swing.SwingUtilities.invokeLater(() -> {
                new VentanaLogin().setVisible(true);
            });

        } catch (SQLException e) {
            Notificador.mostrarMensajeError("No se pudo conectar con la base de datos.\nVerifica que MySQL (XAMPP) esté activo.");
            e.printStackTrace();
        }
    }
}