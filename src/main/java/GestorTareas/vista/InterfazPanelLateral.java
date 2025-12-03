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
import GestorTareas.modelo.PanelLateral;
import GestorTareas.modelo.Tarea;
import GestorTareas.modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class InterfazPanelLateral extends JPanel {
    private Usuario user;
    private BaseDeDatos db;
    private JList<Tarea> taskTitlesList;
    private DefaultListModel<Tarea> titlesModel;
    private JTextField searchField;
    private InterfazPrincipal parent;
    private JButton selectedFilterButton;
    private PanelLateral panelLateral;
    private GestorTareas gestor;

    public InterfazPanelLateral(InterfazPrincipal parent, BaseDeDatos db, Usuario user) {
        this.parent = parent;
        this.db = db;
        this.user = user;

        List<String> categorias = obtenerCategoriasUnicas();
        this.panelLateral = new PanelLateral(categorias);
        this.gestor = new GestorTareas(null, db);

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(320, 0));
        setBackground(new Color(26, 26, 26));
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(44, 62, 80)));

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(new Color(26, 26, 26));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        searchField = new JTextField();
        searchField.setToolTipText("Buscar por título");
        searchField.setBackground(new Color(44, 62, 80));
        searchField.setForeground(Color.WHITE);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(52, 73, 94), 1),
                BorderFactory.createEmptyBorder(10, 40, 10, 10)));
        searchField.setFont(new Font("Arial", Font.PLAIN, 16));
        searchField.addActionListener(e -> filterTasks(searchField.getText()));

        URL lupaURL = getClass().getResource("/lupa.png");
        if (lupaURL != null) {
            ImageIcon lupaImg = new ImageIcon(new ImageIcon(lupaURL).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
            JLabel searchIcon = new JLabel(lupaImg);
            searchIcon.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
            searchPanel.add(searchIcon, BorderLayout.WEST);
        }
        searchPanel.add(searchField, BorderLayout.CENTER);
        add(searchPanel, BorderLayout.NORTH);

        titlesModel = new DefaultListModel<>();
        taskTitlesList = new JList<>(titlesModel);
        taskTitlesList.setCellRenderer(new TaskRenderer());
        taskTitlesList.setBackground(new Color(26, 26, 26));
        taskTitlesList.setForeground(Color.WHITE);
        taskTitlesList.setSelectionBackground(new Color(52, 73, 94));
        taskTitlesList.setFont(new Font("Arial", Font.PLAIN, 14));
        taskTitlesList.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        taskTitlesList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = taskTitlesList.locationToIndex(e.getPoint());
                if (index >= 0) {
                    Tarea selected = taskTitlesList.getSelectedValue();
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        showTaskDetails(selected);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(taskTitlesList);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        JPanel filterPanel = new JPanel(new GridLayout(4, 1, 0, 10));
        filterPanel.setBackground(new Color(26, 26, 26));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] filters = {"Mi Día", "Importante", "Tareas", "Todas"};
        String[] icons = {"/mi dia.png", "/importante.png", "/tareas.png", null};
        for (int i = 0; i < filters.length; i++) {
            JButton filterButton = createFilterButton(filters[i], icons[i]);
            final String filter = filters[i];
            filterButton.addActionListener(e -> {
                setSelectedFilter(filterButton);
                filterTasks(filter);
            });
            filterPanel.add(filterButton);
        }
        add(filterPanel, BorderLayout.SOUTH);

        loadTasks();
    }

    private List<String> obtenerCategoriasUnicas() {
        try {
            List<Tarea> tasks = db.obtenerTareasPorUsuario(user.getIdUsuario());
            return tasks.stream()
                    .map(Tarea::getAsignatura)
                    .distinct()
                    .collect(Collectors.toList());
        } catch (SQLException ex) {
            return List.of();
        }
    }

    private JButton createFilterButton(String text, String iconPath) {
        JButton button = new JButton();
        if (iconPath != null) {
            URL iconURL = getClass().getResource(iconPath);
            if (iconURL != null) {
                ImageIcon iconImg = new ImageIcon(new ImageIcon(iconURL).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
                button.setIcon(iconImg);
            }
        }
        button.setText(text);
        button.setBackground(new Color(44, 62, 80));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button != selectedFilterButton) {
                    button.setBackground(new Color(52, 73, 94));
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (button != selectedFilterButton) {
                    button.setBackground(new Color(44, 62, 80));
                }
            }
        });
        return button;
    }

    private void setSelectedFilter(JButton button) {
        if (selectedFilterButton != null) {
            selectedFilterButton.setBackground(new Color(44, 62, 80));
        }
        selectedFilterButton = button;
        selectedFilterButton.setBackground(new Color(0, 123, 255));
    }

    public void loadTasks() {
        try {
            titlesModel.clear();
            List<Tarea> tasks = db.obtenerTareasPorUsuario(user.getIdUsuario());
            tasks.stream()
                    .filter(t -> t.getEstado() == Tarea.Estado.Pendiente)
                    .forEach(titlesModel::addElement);
        } catch (SQLException ex) {
            ToastNotification.showToast(parent, "Error al cargar tareas: " + ex.getMessage(), true);
        }
    }

    private void filterTasks(String filter) {
        try {
            titlesModel.clear();
            List<Tarea> tasks = db.obtenerTareasPorUsuario(user.getIdUsuario());
            List<Tarea> filteredTasks = switch (filter) {
                case "Mi Día" -> tasks.stream()
                        .filter(t -> t.getEstado() == Tarea.Estado.Pendiente && t.getFechaDeVencimiento().toLocalDate().equals(LocalDate.now()))
                        .toList();
                case "Importante" -> tasks.stream()
                        .filter(t -> t.getEstado() == Tarea.Estado.Pendiente && t.getPrioridad() == Tarea.Prioridad.Importante)
                        .toList();
                case "Tareas" -> tasks.stream()
                        .filter(t -> t.getEstado() == Tarea.Estado.Pendiente)
                        .toList();
                case "Todas" -> tasks;
                default -> {
                    gestor.setListaTareas(tasks);
                    yield gestor.filtrarTareas(filter);
                }
            };
            if (panelLateral.getCategorias().contains(filter)) {
                filteredTasks = panelLateral.filtrarPorCategoria(filter, filteredTasks);
            }
            filteredTasks.forEach(titlesModel::addElement);
            ToastNotification.showToast(parent, "Filtro aplicado: " + filter, false);
        } catch (SQLException ex) {
            ToastNotification.showToast(parent, "Error al filtrar tareas: " + ex.getMessage(), true);
        }
    }

    private void showTaskDetails(Tarea task) {
        JDialog detailsDialog = new JDialog(parent, task.getTituloTarea(), true);
        detailsDialog.setSize(700, 600);
        detailsDialog.setLocationRelativeTo(parent);
        detailsDialog.getContentPane().setBackground(new Color(26, 26, 26));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(26, 26, 26));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JTextArea titleArea = new JTextArea("Título: " + task.getTituloTarea());
        titleArea.setFont(new Font("Arial", Font.BOLD, 24));
        titleArea.setForeground(Color.WHITE);
        titleArea.setBackground(new Color(26, 26, 26));
        titleArea.setLineWrap(true);
        titleArea.setWrapStyleWord(true);
        titleArea.setEditable(false);
        titleArea.setBorder(BorderFactory.createEmptyBorder());
        panel.add(titleArea, gbc);

        gbc.gridy = 1;
        JTextArea descArea = new JTextArea("Descripción: " + task.getDescripcionTarea());
        descArea.setFont(new Font("Arial", Font.PLAIN, 18));
        descArea.setForeground(Color.WHITE);
        descArea.setBackground(new Color(26, 26, 26));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setBorder(BorderFactory.createEmptyBorder());
        panel.add(descArea, gbc);

        gbc.gridy = 2;
        JTextArea subjectArea = new JTextArea("Asignatura: " + task.getAsignatura());
        subjectArea.setFont(new Font("Arial", Font.PLAIN, 18));
        subjectArea.setForeground(Color.WHITE);
        subjectArea.setBackground(new Color(26, 26, 26));
        subjectArea.setLineWrap(true);
        subjectArea.setWrapStyleWord(true);
        subjectArea.setEditable(false);
        subjectArea.setBorder(BorderFactory.createEmptyBorder());
        panel.add(subjectArea, gbc);

        gbc.gridy = 3;
        JTextArea dateArea = new JTextArea("Fecha Vencimiento: " + task.getFechaDeVencimiento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        dateArea.setFont(new Font("Arial", Font.PLAIN, 18));
        dateArea.setForeground(Color.WHITE);
        dateArea.setBackground(new Color(26, 26, 26));
        dateArea.setLineWrap(true);
        dateArea.setWrapStyleWord(true);
        dateArea.setEditable(false);
        dateArea.setBorder(BorderFactory.createEmptyBorder());
        panel.add(dateArea, gbc);

        gbc.gridy = 4;
        JTextArea statusArea = new JTextArea("Estado: " + task.getEstado());
        statusArea.setFont(new Font("Arial", Font.PLAIN, 18));
        statusArea.setForeground(Color.WHITE);
        statusArea.setBackground(new Color(26, 26, 26));
        statusArea.setLineWrap(true);
        statusArea.setWrapStyleWord(true);
        statusArea.setEditable(false);
        setBorder(BorderFactory.createEmptyBorder());
        panel.add(statusArea, gbc);

        gbc.gridy = 5;
        JTextArea priorityArea = new JTextArea("Prioridad: " + task.getPrioridad());
        priorityArea.setFont(new Font("Arial", Font.PLAIN, 18));
        priorityArea.setForeground(Color.WHITE);
        priorityArea.setBackground(new Color(26, 26, 26));
        priorityArea.setLineWrap(true);
        priorityArea.setWrapStyleWord(true);
        priorityArea.setEditable(false);
        setBorder(BorderFactory.createEmptyBorder());
        panel.add(priorityArea, gbc);

        gbc.gridwidth = 1; gbc.gridy = 6;
        JButton editButton = new JButton("Editar");
        editButton.setBackground(new Color(0, 123, 255));
        editButton.setForeground(Color.WHITE);
        editButton.setPreferredSize(new Dimension(120, 50));
        editButton.addActionListener(e -> {
            detailsDialog.dispose();
            new TaskDialog(parent, task, user, db).setVisible(true);
        });
        panel.add(editButton, gbc);

        gbc.gridx = 1;
        JButton completeButton = new JButton("Marcar Completada");
        completeButton.setBackground(new Color(40, 167, 69));
        completeButton.setForeground(Color.WHITE);
        completeButton.setPreferredSize(new Dimension(150, 50));
        completeButton.addActionListener(e -> {
            task.marcarComoCompletada();
            try {
                db.editarTarea(task);
                parent.refreshTasks();
                ToastNotification.showToast(parent, "Tarea completada.", false);
            } catch (SQLException ex) {
                ToastNotification.showToast(parent, "Error al completar tarea: " + ex.getMessage(), true);
            }
            detailsDialog.dispose();
        });
        panel.add(completeButton, gbc);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        JButton deleteButton = new JButton("Eliminar");
        deleteButton.setBackground(new Color(220, 53, 69));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setPreferredSize(new Dimension(120, 50));
        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(detailsDialog, "¿Eliminar tarea?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    db.eliminarTarea(task.getIdTarea());
                    parent.refreshTasks(); 
                    ToastNotification.showToast(parent, "Tarea eliminada.", false);
                } catch (SQLException ex) {
                    ToastNotification.showToast(parent, "Error al eliminar tarea: " + ex.getMessage(), true);
                }
            }
            detailsDialog.dispose();
        });
        panel.add(deleteButton, gbc);

        detailsDialog.add(panel);
        detailsDialog.setVisible(true);
    }

    private static class TaskRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            Tarea task = (Tarea) value;
            setOpaque(false);
            Color textColor = (task.getFechaDeVencimiento().isBefore(LocalDateTime.now()) && task.getEstado() == Tarea.Estado.Pendiente) ? Color.RED : Color.WHITE;
            String title = (task.getTituloTarea() != null && !task.getTituloTarea().trim().isEmpty()) ? task.getTituloTarea() : "Sin título";
            setText("<html><span style='color:" + (textColor == Color.RED ? "red" : "white") + ";'>" + title + "</span></html>");
            setPreferredSize(new Dimension(0, 40));
            setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            return this;
        }
    }
}
