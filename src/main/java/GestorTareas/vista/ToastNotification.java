/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GestorTareas.vista;

/**
 *
 * @author jesuz
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ToastNotification {
    public static void showToast(Window parent, String message, boolean isError) {
        JWindow toast = new JWindow(parent);
        toast.setLayout(new BorderLayout());
        toast.setSize(350, 80); 
        toast.setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(isError ? new Color(220, 53, 69) : new Color(40, 167, 69));
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        String displayMessage = message;
        if (message.contains("Bienvenid@ de nuevo")) {
            displayMessage = message.replace("Inicio de sesión correcto. Bienvenid@ de nuevo.", "Inicio de sesión correcto.<br>Bienvenid@<br>de nuevo.");
        }
        JLabel label = new JLabel("<html><center>" + displayMessage + "</center></html>", SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(label, BorderLayout.CENTER);

        toast.add(panel);
        toast.setVisible(true);

        Timer timer = new Timer(4000, e -> toast.dispose());
        timer.setRepeats(false);
        timer.start();
    }
}
