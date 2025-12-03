/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GestorTareas.vista;

/**
 *
 * @author jesuz
 */
import GestorTareas.modelo.Calendario;
import GestorTareas.modelo.Tarea;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class CalendarioPanel extends JPanel {
    private Calendario calendario;
    private List<Tarea> tareas;

    public CalendarioPanel(Calendario calendario, List<Tarea> tareas) {
        this.calendario = calendario;
        this.tareas = tareas;
        updateVista();
    }

    public void updateVista() {
        removeAll();
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, 200));
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(52, 73, 94), 2), "Calendario", 0, 0, new Font("Arial", Font.BOLD, 16), Color.WHITE));
        setBackground(new Color(26, 26, 26));

        calendario.resaltarDiasConTareas();

        if (calendario.getVistaActual() == Calendario.VistaActual.Semanal) {
            mostrarVistaSemanal();
        } else {
            mostrarVistaMensual();
        }
        revalidate();
        repaint();
    }

    private void mostrarVistaSemanal() {
        JPanel weekPanel = new JPanel(new GridLayout(1, 7, 5, 5));
        weekPanel.setBackground(new Color(26, 26, 26));
        LocalDate today = LocalDate.now();
        for (int i = 0; i < 7; i++) {
            LocalDate day = today.plusDays(i);
            JButton dayButton = new JButton();
            dayButton.setLayout(new BorderLayout());
            dayButton.setBackground(getTaskColorForDay(day));
            dayButton.setForeground(Color.WHITE);
            dayButton.setBorder(BorderFactory.createLineBorder(new Color(52, 73, 94), 1));
            dayButton.setFocusPainted(false);
            dayButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JLabel dayLabel = new JLabel(day.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault()) + " " + day.getDayOfMonth(), SwingConstants.CENTER);
            dayLabel.setFont(new Font("Arial", Font.BOLD, 12));
            dayLabel.setForeground(Color.WHITE);
            dayButton.add(dayLabel, BorderLayout.CENTER);

            dayButton.addActionListener(e -> mostrarTareasDelDia(day));
            weekPanel.add(dayButton);
        }
        add(weekPanel, BorderLayout.CENTER);
    }

    private void mostrarVistaMensual() {
        JPanel monthPanel = new JPanel(new GridLayout(0, 7, 5, 5));
        monthPanel.setBackground(new Color(26, 26, 26));
        LocalDate firstDay = LocalDate.now().withDayOfMonth(1);
        for (int i = 0; i < firstDay.getDayOfWeek().getValue() % 7; i++) {
            monthPanel.add(new JLabel(""));
        }
        for (int day = 1; day <= LocalDate.now().lengthOfMonth(); day++) {
            LocalDate date = firstDay.plusDays(day - 1);
            JButton dayButton = new JButton(String.valueOf(day));
            dayButton.setBackground(getTaskColorForDay(date));
            dayButton.setForeground(Color.WHITE);
            dayButton.setBorder(BorderFactory.createLineBorder(new Color(52, 73, 94), 1));
            dayButton.setFocusPainted(false);
            dayButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            dayButton.addActionListener(e -> mostrarTareasDelDia(date));
            monthPanel.add(dayButton);
        }
        add(monthPanel, BorderLayout.CENTER);
    }

    private Color getTaskColorForDay(LocalDate date) {
        List<Tarea> dayTasks = tareas.stream()
                .filter(t -> t.getFechaDeVencimiento().toLocalDate().equals(date))
                .toList();
        if (dayTasks.isEmpty()) return new Color(44, 62, 80);
        boolean hasVencidas = dayTasks.stream().anyMatch(t -> t.getFechaDeVencimiento().isBefore(LocalDateTime.now()) && t.getEstado() == Tarea.Estado.Pendiente);
        if (hasVencidas) return Color.RED;
        boolean hasCompletadas = dayTasks.stream().anyMatch(t -> t.getEstado() == Tarea.Estado.Completada);
        if (hasCompletadas) return new Color(40, 167, 69);
        return new Color(255, 193, 7);
    }

    private void mostrarTareasDelDia(LocalDate date) {
        List<Tarea> tareasDia = tareas.stream()
                .filter(t -> t.getFechaDeVencimiento().toLocalDate().equals(date))
                .toList();
        if (tareasDia.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay tareas para " + date.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        } else {
            StringBuilder sb = new StringBuilder("Tareas para " + date.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ":\n");
            tareasDia.forEach(t -> sb.append("- ").append(t.getTituloTarea()).append(" (").append(t.getEstado()).append(")\n"));
            JOptionPane.showMessageDialog(this, sb.toString());
        }
    }
}