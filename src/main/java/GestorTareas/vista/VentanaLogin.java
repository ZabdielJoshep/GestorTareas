/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GestorTareas.vista;

/**
 * @author jesuz
 */
import GestorTareas.modelo.BaseDeDatos;
import GestorTareas.modelo.Encriptador;
import GestorTareas.modelo.Notificador;
import GestorTareas.modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class VentanaLogin extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JToggleButton showPasswordButton;
    private BaseDeDatos db;

    public VentanaLogin() {
        db = new BaseDeDatos();
        try {
            db.conectar();
        } catch (SQLException e) {
            Notificador.mostrarMensajeError("Error al conectar a la BD: " + e.getMessage());
            System.exit(1);
        }

        setTitle("Gestor de Tareas - Iniciar Sesión");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Iniciar Sesión", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 123, 255));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel emailPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        emailPanel.setBackground(Color.WHITE);
        emailPanel.add(new JLabel("Correo Electrónico:"));
        emailField = new JTextField(25);
        emailField.setEditable(true); 
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 2), 
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailPanel.add(emailField);
        panel.add(emailPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel passPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        passPanel.setBackground(Color.WHITE);
        passPanel.add(new JLabel("Contraseña:"));
        passwordField = new JPasswordField(25);
        passwordField.setEditable(true);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 2),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setEchoChar('\u2022'); 
        passPanel.add(passwordField);

        showPasswordButton = new JToggleButton(new ImageIcon(new ImageIcon(getClass().getResource("/ojo_tachado.png")).getImage().getScaledInstance(40, 30, Image.SCALE_SMOOTH)));
        showPasswordButton.setPreferredSize(new Dimension(40, 30));
        showPasswordButton.setBorderPainted(false);
        showPasswordButton.setContentAreaFilled(false);
        showPasswordButton.addActionListener(new ShowPasswordAction());
        passPanel.add(showPasswordButton);
        panel.add(passPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        loginButton = new JButton("Iniciar Sesión");
        loginButton.setBackground(new Color(0, 123, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(new LoginAction());
        panel.add(loginButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        registerButton = new JButton("Crear Cuenta");
        registerButton.setBackground(Color.LIGHT_GRAY);
        registerButton.setForeground(Color.BLACK);
        registerButton.setFont(new Font("Arial", Font.PLAIN, 14));
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.addActionListener(e -> new VentanaRegistro(this, db).setVisible(true));
        panel.add(registerButton);

        add(panel);
    }

    private class LoginAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (email.isEmpty() || password.isEmpty()) {
                ToastNotification.showToast(VentanaLogin.this, "Complete todos los campos.", true); 
                return;
            }

            try {
                Usuario user = db.obtenerUsuarioPorCorreo(email);
                if (user != null && Encriptador.verificarContraseña(password, user.getContraseña())) {
                    dispose();
                    new InterfazPrincipal(user, db).setVisible(true);
                } else {
                    ToastNotification.showToast(VentanaLogin.this, "Credenciales incorrectas.", true);
                }
            } catch (SQLException ex) {
                ToastNotification.showToast(VentanaLogin.this, "Error de base de datos: " + ex.getMessage(), true);
            }
        }
    }

    private class ShowPasswordAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (showPasswordButton.isSelected()) {
                passwordField.setEchoChar((char) 0);
                showPasswordButton.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/ojo.png")).getImage().getScaledInstance(40, 30, Image.SCALE_SMOOTH)));
            } else {
                passwordField.setEchoChar('\u2022');
                showPasswordButton.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/ojo_tachado.png")).getImage().getScaledInstance(40, 30, Image.SCALE_SMOOTH)));
            }
        }
    }
}