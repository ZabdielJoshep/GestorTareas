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
import GestorTareas.modelo.GestorTareas;
import GestorTareas.modelo.Tarea;
import GestorTareas.modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDateTime;

import com.github.lgooddatepicker.components.DateTimePicker;

public class TaskDialog extends JDialog {
    private JTextField titleField;
    private JTextArea descArea;
    private JTextField subjectField;
    private DateTimePicker dateTimePicker;
    private JComboBox<String> statusCombo;
    private JComboBox<String> priorityCombo;
    private JButton saveButton;
    private Tarea task;
    private Usuario user;
    private BaseDeDatos db;
    private GestorTareas gestor;
    private InterfazPrincipal parent;

    public TaskDialog(InterfazPrincipal parent, Tarea task, Usuario user, BaseDeDatos db) {
        super(parent, task == null ? "Crear Tarea" : "Editar Tarea", true);
        this.parent = parent;
        this.task = task;
        this.user = user;
        this.db = db;
        this.gestor = new GestorTareas(null, db);

        setSize(800, 750);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(new Color(26, 26, 26));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(26, 26, 26));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel(task == null ? "Crear Tarea" : "Editar Tarea", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1; gbc.gridy = 1;
        panel.add(createLabel("Título:"), gbc);
        gbc.gridx = 1;
        titleField = createTextField(task != null ? task.getTituloTarea() : "", 30);
        titleField.setPreferredSize(new Dimension(450, 35)); 
        panel.add(titleField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(createLabel("Descripción:"), gbc);
        gbc.gridx = 1;
        descArea = new JTextArea(task != null ? task.getDescripcionTarea() : "", 5, 30);
        descArea.setBackground(new Color(44, 62, 80));
        descArea.setForeground(Color.WHITE);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(52, 73, 94)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10))); 
        descArea.setFont(new Font("Arial", Font.PLAIN, 14));
        descArea.setEditable(true);
        descArea.setPreferredSize(new Dimension(450, 100));
        JScrollPane descScrollPane = new JScrollPane(descArea);
        descScrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(descScrollPane, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(createLabel("Asignatura:"), gbc);
        gbc.gridx = 1;
        subjectField = createTextField(task != null ? task.getAsignatura() : "", 30);
        subjectField.setPreferredSize(new Dimension(450, 35)); 
        panel.add(subjectField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(createLabel("Fecha Vencimiento:"), gbc);
        gbc.gridx = 1;
        dateTimePicker = new DateTimePicker();
        dateTimePicker.getDatePicker().getComponentDateTextField().setBackground(new Color(44, 62, 80));
        dateTimePicker.getDatePicker().getComponentDateTextField().setForeground(Color.WHITE);
        dateTimePicker.getTimePicker().getComponentTimeTextField().setBackground(new Color(44, 62, 80));
        dateTimePicker.getTimePicker().getComponentTimeTextField().setForeground(Color.WHITE);
        if (task != null) {
            dateTimePicker.setDateTimePermissive(task.getFechaDeVencimiento());
        }
        dateTimePicker.setPreferredSize(new Dimension(450, 35));
        panel.add(dateTimePicker, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(createLabel("Estado:"), gbc);
        gbc.gridx = 1;
        statusCombo = new JComboBox<>(new String[]{"Pendiente", "Completada"});
        styleComboBox(statusCombo);
        statusCombo.setPreferredSize(new Dimension(450, 35));
        if (task != null) statusCombo.setSelectedItem(task.getEstado().toString());
        panel.add(statusCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(createLabel("Prioridad:"), gbc);
        gbc.gridx = 1;
        priorityCombo = new JComboBox<>(new String[]{"Normal", "Importante"});
        styleComboBox(priorityCombo);
        priorityCombo.setPreferredSize(new Dimension(450, 35));
        if (task != null) priorityCombo.setSelectedItem(task.getPrioridad().toString());
        panel.add(priorityCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        saveButton = new JButton("Guardar");
        saveButton.setBackground(new Color(0, 123, 255));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(new Font("Arial", Font.BOLD, 18));
        saveButton.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        saveButton.setFocusPainted(false);
        saveButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveButton.addActionListener(e -> saveTask());
        panel.add(saveButton, gbc);

        add(panel);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.LEFT);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        return label;
    }

    private JTextField createTextField(String text, int columns) {
        JTextField field = new JTextField(text, columns);
        field.setBackground(new Color(44, 62, 80));
        field.setForeground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(52, 73, 94)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setEditable(true);
        return field;
    }

    private void styleComboBox(JComboBox<String> combo) {
        combo.setBackground(new Color(44, 62, 80));
        combo.setForeground(Color.WHITE);
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(52, 73, 94)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10))); 
        combo.setFont(new Font("Arial", Font.PLAIN, 14));
    }

    private void saveTask() {
        String title = titleField.getText().trim();
        String desc = descArea.getText().trim();
        String subject = subjectField.getText().trim();
        LocalDateTime fecha = dateTimePicker.getDateTimePermissive();

        if (title.isEmpty() || desc.isEmpty() || subject.isEmpty() || fecha == null) {
            ToastNotification.showToast((Window) this, "Complete todos los campos.", true); 
            return;
        }

        if (!fecha.isAfter(LocalDateTime.now())) {
            ToastNotification.showToast((Window) this, "La fecha de vencimiento debe ser futura.", true);
            return;
        }

        Tarea.Estado estado = Tarea.Estado.valueOf((String) statusCombo.getSelectedItem());
        Tarea.Prioridad prioridad = Tarea.Prioridad.valueOf((String) priorityCombo.getSelectedItem());

        try {
            if (task == null) {
                Tarea newTask = Tarea.crearTarea(title, desc, fecha, subject);
                newTask.setEstado(estado);
                newTask.setPrioridad(prioridad);
                gestor.guardarTarea(newTask, user.getIdUsuario());
                ToastNotification.showToast(parent, "Tarea creada.", false);
            } else {
                task.editarTarea(title, desc, fecha, subject, estado, prioridad);
                gestor.editarTarea(task);
                ToastNotification.showToast(parent, "Tarea editada.", false); 
            }
        } catch (SQLException ex) {
            ToastNotification.showToast(parent, "Error al guardar tarea: " + ex.getMessage(), true);
        }

        parent.refreshTasks();
        dispose();
    }
}
