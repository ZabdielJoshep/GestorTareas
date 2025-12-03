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
import GestorTareas.modelo.Sesion;
import GestorTareas.modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDateTime;

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
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Iniciar Sesión", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 123, 255));
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1; gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Correo Electrónico:"), gbc);
        gbc.gridy = 2;
        emailField = new JTextField(25);
        emailField.setEditable(true); 
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 2), 
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(emailField, gbc);

        gbc.gridy = 3;
        panel.add(new JLabel("Contraseña:"), gbc);
        gbc.gridy = 4;
        JPanel passPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        passPanel.setBackground(Color.WHITE);
        passwordField = new JPasswordField(20);
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
        panel.add(passPanel, gbc);

        gbc.gridy = 5; gbc.gridwidth = 2;
        JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        linkPanel.setBackground(Color.WHITE);
        JLabel changePassLabel = new JLabel("<html><u>¿Olvidaste tu contraseña? Cambiar aquí</u></html>", SwingConstants.CENTER);
        changePassLabel.setForeground(new Color(0, 123, 255));
        changePassLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        changePassLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        changePassLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new VentanaCambiarContraseña(VentanaLogin.this, db).setVisible(true);
            }
        });
        linkPanel.add(changePassLabel);
        panel.add(linkPanel, gbc);

        gbc.gridy = 6;
        loginButton = new JButton("Iniciar Sesión");
        loginButton.setBackground(new Color(0, 123, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.addActionListener(new LoginAction());
        panel.add(loginButton, gbc);

        gbc.gridy = 7;
        registerButton = new JButton("Crear Cuenta");
        registerButton.setBackground(Color.LIGHT_GRAY);
        registerButton.setForeground(Color.BLACK);
        registerButton.setFont(new Font("Arial", Font.PLAIN, 14));
        registerButton.addActionListener(e -> {
            VentanaRegistro registro = new VentanaRegistro(VentanaLogin.this, db);
            registro.setVisible(true);
        });
        panel.add(registerButton, gbc);

        add(panel);

        if (getTitle().contains("Registro Exitoso")) {
            ToastNotification.showToast(this, "Cuenta creada exitosamente.", false);
            setTitle("Gestor de Tareas - Iniciar Sesión");
        }
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

        
        if (!Sesion.iniciarSesion(email, password)) {
            ToastNotification.showToast(VentanaLogin.this, "Inicio de sesion fallido.", true);
            return;
        }

        try {
            Usuario user = db.obtenerUsuarioPorCorreo(email);
            if (user != null && Encriptador.verificarContraseña(password, user.getContraseña())) {
                String token = "token-" + user.getIdUsuario() + "-" + System.currentTimeMillis();
                Sesion sesion = new Sesion(token, user, LocalDateTime.now(), LocalDateTime.now().plusHours(1));
                dispose();
                new InterfazPrincipal(user, db, sesion).setVisible(true);
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