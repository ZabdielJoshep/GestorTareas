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
import GestorTareas.modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class VentanaCambiarContraseña extends JDialog {
    private JTextField emailField;
    private JPasswordField newPasswordField;
    private JButton changeButton;
    private JToggleButton showNewPasswordButton;
    private BaseDeDatos db;
    private VentanaLogin parent;

    public VentanaCambiarContraseña(VentanaLogin parent, BaseDeDatos db) {
        super(parent, "Cambiar Contraseña", true);
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
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Cambiar Contraseña", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 123, 255));
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1; gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Correo:"), gbc);
        gbc.gridy = 2;
        emailField = new JTextField(25);
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        panel.add(emailField, gbc);

        gbc.gridy = 3;
        panel.add(new JLabel("Nueva Contraseña:"), gbc);
        gbc.gridy = 4;
        JPanel newPassPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        newPassPanel.setBackground(Color.WHITE);
        newPasswordField = new JPasswordField(25);
        newPasswordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        newPasswordField.setEchoChar('\u2022');
        newPassPanel.add(newPasswordField);

        showNewPasswordButton = new JToggleButton(new ImageIcon(new ImageIcon(getClass().getResource("/ojo_tachado.png")).getImage().getScaledInstance(40, 30, Image.SCALE_SMOOTH)));
        showNewPasswordButton.setPreferredSize(new Dimension(40, 30));
        showNewPasswordButton.setBorderPainted(false);
        showNewPasswordButton.setContentAreaFilled(false);
        showNewPasswordButton.addActionListener(new ShowPasswordAction(newPasswordField));
        newPassPanel.add(showNewPasswordButton);
        panel.add(newPassPanel, gbc);

        gbc.gridy = 5; gbc.gridwidth = 2;
        changeButton = new JButton("Cambiar");
        changeButton.setBackground(new Color(0, 123, 255));
        changeButton.setForeground(Color.WHITE);
        changeButton.setFont(new Font("Arial", Font.BOLD, 14));
        changeButton.setPreferredSize(new Dimension(120, 30));
        changeButton.addActionListener(new ChangeAction());
        panel.add(changeButton, gbc);

        add(panel);
    }

    private class ChangeAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String email = emailField.getText().trim();
            String newPass = new String(newPasswordField.getPassword());

            if (email.isEmpty() || newPass.isEmpty()) {
                ToastNotification.showToast(VentanaCambiarContraseña.this, "Complete todos los campos.", true);
                return;
            }

            try {
                Usuario user = db.obtenerUsuarioPorCorreo(email);
                if (user != null) {
                    user.setContraseña(Encriptador.hashearContraseña(newPass));
                    db.editarUsuario(user);
                    ToastNotification.showToast(parent, "Contraseña cambiada exitosamente.", false);
                    dispose();
                } else {
                    ToastNotification.showToast(VentanaCambiarContraseña.this, "Correo no encontrado.", true);
                }
            } catch (SQLException ex) {
                ToastNotification.showToast(VentanaCambiarContraseña.this, "Error de base de datos: " + ex.getMessage(), true);
            }
        }
    }

    private class ShowPasswordAction implements ActionListener {
        private JPasswordField field;

        public ShowPasswordAction(JPasswordField field) {
            this.field = field;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JToggleButton button = (JToggleButton) e.getSource();
            if (button.isSelected()) {
                field.setEchoChar((char) 0);
                button.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/ojo.png")).getImage().getScaledInstance(40, 30, Image.SCALE_SMOOTH)));
            } else {
                field.setEchoChar('\u2022');
                button.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/ojo_tachado.png")).getImage().getScaledInstance(40, 30, Image.SCALE_SMOOTH)));
            }
        }
    }
}
