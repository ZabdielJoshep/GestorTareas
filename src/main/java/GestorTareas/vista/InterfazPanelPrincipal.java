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
import GestorTareas.modelo.Tarea;
import GestorTareas.modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class InterfazPanelPrincipal extends JPanel {
    private Usuario user;
    private BaseDeDatos db;
    private JList<Tarea> taskList;
    private DefaultListModel<Tarea> taskModel;
    private InterfazPrincipal parent;
    private JLabel timeLabel;
    private JLabel dateLabel;
    private Timer vencimientoTimer;
    private static Set<String> notifiedVencidas = new HashSet<>();

    public InterfazPanelPrincipal(InterfazPrincipal parent, BaseDeDatos db, Usuario user) {
        this.parent = parent;
        this.db = db;
        this.user = user;

        setLayout(new BorderLayout());
        setBackground(new Color(26, 26, 26));


        JPanel dateTimePanel = new JPanel(new BorderLayout());
        dateTimePanel.setOpaque(false);
        dateTimePanel.setBorder(BorderFactory.createEmptyBorder(50, 20, 20, 20));

        timeLabel = new JLabel();
        timeLabel.setFont(new Font("Arial", Font.BOLD, 72));
        timeLabel.setForeground(Color.WHITE);
        timeLabel.setHorizontalAlignment(SwingConstants.LEFT);
        dateTimePanel.add(timeLabel, BorderLayout.NORTH);

        dateLabel = new JLabel();
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 36));
        dateLabel.setForeground(Color.WHITE);
        dateLabel.setHorizontalAlignment(SwingConstants.LEFT);
        dateTimePanel.add(dateLabel, BorderLayout.SOUTH);

        JLabel background = new JLabel(new ImageIcon(getClass().getResource("/fondo.png")));
        background.setLayout(new BorderLayout());
        background.add(dateTimePanel, BorderLayout.NORTH);
        add(background, BorderLayout.CENTER);

        taskModel = new DefaultListModel<>();
        taskList = new JList<>(taskModel);
        taskList.setCellRenderer(new TaskRenderer());
        taskList.setOpaque(false);
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskList.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        taskList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = taskList.locationToIndex(e.getPoint());
                if (index >= 0) {
                    Tarea selected = taskList.getSelectedValue();
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        showTaskDetails(selected);
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int index = taskList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        taskList.setSelectedIndex(index);
                        Tarea selected = taskList.getSelectedValue();
                        showContextMenu(selected, e.getX(), e.getY());
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(taskList);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        background.add(scrollPane, BorderLayout.CENTER);

        JButton addButton = new JButton("+") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.BLACK);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 48));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth("+")) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 5;
                g2.drawString("+", x, y);
                g2.dispose();
            }
        };
        addButton.setPreferredSize(new Dimension(80, 80));
        addButton.setBorderPainted(false);
        addButton.setContentAreaFilled(false);
        addButton.setFocusPainted(false);
        addButton.addActionListener(e -> new TaskDialog(parent, null, user, db).setVisible(true));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(addButton);
        background.add(buttonPanel, BorderLayout.SOUTH);

        updateDateTime();
        loadTasks();

        ToastNotification.showToast(parent, "Inicio de sesión correcto. Bienvenid@ \n de nuevo.", false);

        vencimientoTimer = new Timer();
        vencimientoTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkVencimientos();
            }
        }, 0, 60000);
    }

    private void checkVencimientos() {
        try {
            List<Tarea> tasks = db.obtenerTareasPorUsuario(user.getIdUsuario());
            for (Tarea task : tasks) {
                if (task.getFechaDeVencimiento().isBefore(LocalDateTime.now()) && task.getEstado() == Tarea.Estado.Pendiente) {
                    String taskId = String.valueOf(task.getIdTarea());
                    if (!notifiedVencidas.contains(taskId)) {
                        SwingUtilities.invokeLater(() -> ToastNotification.showToast(parent, "¡Tarea vencida! " + task.getTituloTarea() + " está pendiente.", true));
                        notifiedVencidas.add(taskId);
                    }
                    task.setEstado(Tarea.Estado.Pendiente);
                    db.editarTarea(task);
                }
            }
        } catch (SQLException ex) {
            SwingUtilities.invokeLater(() -> ToastNotification.showToast(parent, "Error al verificar vencimientos: " + ex.getMessage(), true));
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
        statusArea.setBorder(BorderFactory.createEmptyBorder());
        panel.add(statusArea, gbc);

        gbc.gridy = 5;
        JTextArea priorityArea = new JTextArea("Prioridad: " + task.getPrioridad());
        priorityArea.setFont(new Font("Arial", Font.PLAIN, 18));
        priorityArea.setForeground(Color.WHITE);
        priorityArea.setBackground(new Color(26, 26, 26));
        priorityArea.setLineWrap(true);
        priorityArea.setWrapStyleWord(true);
        priorityArea.setEditable(false);
        priorityArea.setBorder(BorderFactory.createEmptyBorder());
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

    private void showContextMenu(Tarea task, int x, int y) {
        JPopupMenu menu = new JPopupMenu();
        menu.setBackground(new Color(44, 62, 80));

        JMenuItem completeItem = new JMenuItem("Marcar Completada");
        completeItem.setForeground(Color.WHITE);
        completeItem.setBackground(new Color(44, 62, 80));
        completeItem.addActionListener(e -> {
            task.marcarComoCompletada();
            try {
                db.editarTarea(task);
                parent.refreshTasks(); 
                ToastNotification.showToast(parent, "Tarea completada.", false);
            } catch (SQLException ex) {
                ToastNotification.showToast(parent, "Error al completar tarea: " + ex.getMessage(), true);
            }
        });
        menu.add(completeItem);

        JMenuItem deleteItem = new JMenuItem("Eliminar");
        deleteItem.setForeground(Color.WHITE);
        deleteItem.setBackground(new Color(44, 62, 80));
        deleteItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(parent, "¿Eliminar tarea?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    db.eliminarTarea(task.getIdTarea());
                    parent.refreshTasks();
                    ToastNotification.showToast(parent, "Tarea eliminada.", false);
                } catch (SQLException ex) {
                    ToastNotification.showToast(parent, "Error al eliminar tarea: " + ex.getMessage(), true);
                }
            }
        });
        menu.add(deleteItem);

        menu.show(taskList, x, y);
    }

    public void updateDateTime() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM 'de' yyyy");
        LocalDateTime now = LocalDateTime.now();
        timeLabel.setText(now.format(timeFormatter));
        dateLabel.setText(now.format(dateFormatter));
    }

    public void loadTasks() {
        try {
            taskModel.clear();
            List<Tarea> tasks = db.obtenerTareasPorUsuario(user.getIdUsuario());
            LocalDate today = LocalDate.now();
            tasks.stream()
                .filter(t -> t.getEstado() == Tarea.Estado.Pendiente &&
                             (t.getPrioridad() == Tarea.Prioridad.Importante ||
                              t.getFechaDeVencimiento().toLocalDate().isBefore(today.plusDays(3))))
                .sorted(Comparator.comparing((Tarea t) -> t.getPrioridad() == Tarea.Prioridad.Importante ? 0 : 1)
                        .thenComparing(Tarea::getFechaDeVencimiento))
                                .limit(2) 
                .forEach(task -> {
                    if (task.getFechaDeVencimiento().isBefore(LocalDateTime.now())) {
                        String taskId = String.valueOf(task.getIdTarea());
                        if (!notifiedVencidas.contains(taskId)) {
                            ToastNotification.showToast(parent, "Tarea vencida: " + task.getTituloTarea() + " está pendiente.", true);
                            notifiedVencidas.add(taskId);
                        }
                    }
                    taskModel.addElement(task);
                });
        } catch (SQLException ex) {
            ToastNotification.showToast(parent, "Error al cargar tareas: " + ex.getMessage(), true);
        }
    }

    private static class TaskRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            Tarea task = (Tarea) value;
            setOpaque(false);
            Color textColor = (task.getFechaDeVencimiento().isBefore(LocalDateTime.now()) && task.getEstado() == Tarea.Estado.Pendiente) ? Color.RED : Color.WHITE;
            setText("<html><b style='font-size:18px; color:" + (textColor == Color.RED ? "red" : "white") + ";'>" + task.getTituloTarea() + "</b><br><br>" +
                    "<b style='font-size:14px; color:" + (textColor == Color.RED ? "red" : "white") + ";'>Descripción:</b> <span style='font-size:14px; color:" + (textColor == Color.RED ? "red" : "white") + ";'>" + task.getDescripcionTarea() + "</span><br>" +
                    "<b style='font-size:14px; color:" + (textColor == Color.RED ? "red" : "white") + ";'>Materia:</b> <span style='font-size:14px; color:" + (textColor == Color.RED ? "red" : "white") + ";'>" + task.getAsignatura() + "</span></html>");
            setPreferredSize(new Dimension(0, 180));
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            return this;
        }
    }
}
