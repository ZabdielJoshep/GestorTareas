/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GestorTareas.vista;

/**
 *
 * @author jesuz
 */
import GestorTareas.modelo.BaseDeDatos;
import GestorTareas.modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class InterfazPrincipal extends JFrame {
    private Usuario user;
    private BaseDeDatos db;
    private InterfazPanelPrincipal panelPrincipal;
    private InterfazPanelLateral panelLateral;
    private Timer timer;
    private JButton logoutIcon;

    public InterfazPrincipal(Usuario user, BaseDeDatos db) {
        this.user = user;
        this.db = db;

        setTitle("Gestor de Tareas - Panel Principal");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(26, 26, 26));

        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(26, 26, 26));
        logoutIcon = new JButton();
        ImageIcon logoutImg = new ImageIcon(new ImageIcon("E:\\Neetbeans\\GestorTareas\\src\\main\\resources/usuario.png").getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));  
        logoutIcon.setIcon(logoutImg);
        logoutIcon.setBackground(new Color(44, 62, 80)); 
        logoutIcon.setForeground(Color.WHITE);
        logoutIcon.setBorderPainted(false);
        logoutIcon.setFocusPainted(false);
        logoutIcon.setPreferredSize(new Dimension(50, 50));
        logoutIcon.addActionListener(e -> {
            JPopupMenu menu = new JPopupMenu();
            menu.setBackground(new Color(44, 62, 80));
            JMenuItem logoutItem = new JMenuItem("Cerrar Sesión");
            logoutItem.setForeground(Color.WHITE);
            logoutItem.setBackground(new Color(44, 62, 80));
            logoutItem.addActionListener(ev -> {
                int confirm = JOptionPane.showConfirmDialog(this, "¿Cerrar sesión?", "Confirmar", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    dispose();
                    new VentanaLogin().setVisible(true);
                }
            });
            menu.add(logoutItem);
            menu.show(logoutIcon, 0, logoutIcon.getHeight());
        });
        topPanel.add(logoutIcon, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        panelLateral = new InterfazPanelLateral(this, db, user);
        add(panelLateral, BorderLayout.WEST);

        panelPrincipal = new InterfazPanelPrincipal(this, db, user);
        add(panelPrincipal, BorderLayout.CENTER);

        timer = new Timer(1000, e -> panelPrincipal.updateDateTime());
        timer.start();
    }

    public void refreshTasks() {
        panelPrincipal.loadTasks();
        panelLateral.loadTasks();
    }
}