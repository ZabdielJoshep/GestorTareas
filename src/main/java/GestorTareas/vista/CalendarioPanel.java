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
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public class CalendarioPanel extends JPanel {
    public CalendarioPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, 200));
        setBorder(BorderFactory.createTitledBorder("Calendario"));

        JPanel weekPanel = new JPanel(new GridLayout(1, 7));
        LocalDate today = LocalDate.now();
        for (int i = 0; i < 7; i++) {
            LocalDate day = today.plusDays(i);
            JButton dayButton = new JButton(day.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + day.getDayOfMonth());
            dayButton.setBackground(Color.LIGHT_GRAY);
            weekPanel.add(dayButton);
        }
        add(weekPanel, BorderLayout.CENTER);

        JPanel monthPanel = new JPanel(new GridLayout(0, 7));
        LocalDate firstDay = today.withDayOfMonth(1);
        for (int i = 0; i < firstDay.getDayOfWeek().getValue() % 7; i++) {
            monthPanel.add(new JLabel(""));
        }
        for (int day = 1; day <= today.lengthOfMonth(); day++) {
            JButton dayButton = new JButton(String.valueOf(day));
            if (day == today.getDayOfMonth()) dayButton.setBackground(Color.BLUE);
            monthPanel.add(dayButton);
        }
        add(monthPanel, BorderLayout.SOUTH);
    }
}