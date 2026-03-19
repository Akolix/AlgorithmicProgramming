package app.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import static app.gui.UIFactory.*;

/**
 * Bottom status bar showing current operation status (left)
 * and last algorithm execution time (right).
 */
public class StatusBar extends JPanel {

    private final JLabel statusLabel;
    private final JLabel timingLabel;

    public StatusBar() {
        setLayout(new BorderLayout(10, 0));
        setBackground(BG_PANEL);
        setBorder(new EmptyBorder(8, 16, 8, 16));

        statusLabel = styledLabel("Ready.", FONT_BODY, TEXT_DIM);
        timingLabel = styledLabel("", FONT_STATUS, WARNING);

        add(statusLabel, BorderLayout.WEST);
        add(timingLabel, BorderLayout.EAST);
    }

    public void setStatus(String msg, Color color) {
        statusLabel.setText(msg);
        statusLabel.setForeground(color);
    }

    public void setTiming(String algorithmName, String time) {
        timingLabel.setText("⏱  " + algorithmName + ": " + time);
    }
}