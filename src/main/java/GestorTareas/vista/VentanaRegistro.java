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

public class VentanaRegistro extends JDialog {
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton createButton;
    private JToggleButton showPasswordButton;
    private BaseDeDatos db;
    private VentanaLogin parent;

    public VentanaRegistro(VentanaLogin parent, BaseDeDatos db) {
        super(parent, "Crear Cuenta", true);
        this.parent = parent;
        this.db = db;

        setSize(450, 350);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        JLabel titleLabel = new JLabel("Crear Cuenta", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 123, 255));
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1; gbc.gridy = 1;
        panel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        nameField = new JTextField(20);
        nameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        panel.add(new JLabel("Correo:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        emailField = new JTextField(20);
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        panel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        panel.add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(18); 
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        passwordField.setEchoChar('\u2022'); 
        panel.add(passwordField, gbc);

        gbc.gridx = 2;
        showPasswordButton = new JToggleButton(new ImageIcon(new ImageIcon(getClass().getResource("/ojo_tachado.png")).getImage().getScaledInstance(40, 30, Image.SCALE_SMOOTH)));
        showPasswordButton.setPreferredSize(new Dimension(40, 30));
        showPasswordButton.setBorderPainted(false);
        showPasswordButton.setContentAreaFilled(false);
        showPasswordButton.addActionListener(new ShowPasswordAction());
        panel.add(showPasswordButton, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 3;
        createButton = new JButton("Crear Cuenta");
        createButton.setBackground(new Color(0, 123, 255));
        createButton.setForeground(Color.WHITE);
        createButton.setFont(new Font("Arial", Font.BOLD, 16));
        createButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        createButton.addActionListener(new RegisterAction());
        panel.add(createButton, gbc);

        add(panel);
    }
    
    private class RegisterAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            ToastNotification.showToast(VentanaRegistro.this, "Complete todos los campos.", true); 
            return;
        }

        if (!Usuario.validarCorreo(email)) {
            ToastNotification.showToast(VentanaRegistro.this, "Correo invalido.", true); 
            return;
        }

        try {
            if (db.obtenerUsuarioPorCorreo(email) != null) {
                ToastNotification.showToast(VentanaRegistro.this, "Correo ya en uso.", true); 
                return;
            }

            String hashedPassword = Encriptador.hashearContraseña(password);
            Usuario newUser = Usuario.registrar(name, email, hashedPassword);
            if (newUser != null) {
                db.guardarUsuario(newUser);
                ToastNotification.showToast(parent, "Cuenta creada exitosamente.", false);
                dispose();
            } else {
                ToastNotification.showToast(VentanaRegistro.this, "Error al crear cuenta.", true); 
            }
        } catch (SQLException ex) {
            ToastNotification.showToast(VentanaRegistro.this, "Error de base de datos: " + ex.getMessage(), true); 
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