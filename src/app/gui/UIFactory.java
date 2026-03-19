package app.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Shared color palette, fonts, and Swing component factory methods.
 * All GUI classes import constants and helpers from here — nothing is duplicated.
 */
public class UIFactory {

    // ── Palette ────────────────────────────────────────────────────────────
    public static final Color BG_DARK   = new Color(18, 18, 30);
    public static final Color BG_PANEL  = new Color(28, 28, 45);
    public static final Color BG_CARD   = new Color(38, 38, 58);
    public static final Color ACCENT    = new Color(99, 179, 237);
    public static final Color ACCENT2   = new Color(154, 117, 234);
    public static final Color TEXT_MAIN = new Color(230, 230, 245);
    public static final Color TEXT_DIM  = new Color(140, 140, 170);
    public static final Color SUCCESS   = new Color(72, 199, 142);
    public static final Color WARNING   = new Color(252, 196, 25);
    public static final Color ROW_ALT   = new Color(33, 33, 52);

    // ── Fonts ──────────────────────────────────────────────────────────────
    public static final Font FONT_TITLE  = new Font("SansSerif", Font.BOLD, 22);
    public static final Font FONT_HEADER = new Font("SansSerif", Font.BOLD, 13);
    public static final Font FONT_BODY   = new Font("SansSerif", Font.PLAIN, 12);
    public static final Font FONT_MONO   = new Font("Monospaced", Font.PLAIN, 12);
    public static final Font FONT_STATUS = new Font("Monospaced", Font.BOLD, 13);

    // ── Component factories ────────────────────────────────────────────────

    public static JLabel styledLabel(String text, Font font, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        lbl.setForeground(color);
        return lbl;
    }

    public static JButton accentButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(BG_CARD);
        btn.setForeground(ACCENT);
        btn.setFont(FONT_BODY);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT, 1),
                new EmptyBorder(5, 12, 5, 12)));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(60, 90, 130)); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(BG_CARD); }
        });
        return btn;
    }

    public static <T> JComboBox<T> styledCombo(T[] items) {
        JComboBox<T> combo = new JComboBox<>(items);
        combo.setBackground(BG_CARD);
        combo.setForeground(TEXT_MAIN);
        combo.setFont(FONT_BODY);
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        combo.setAlignmentX(Component.LEFT_ALIGNMENT);
        return combo;
    }

    /**
     * Creates a titled dark card panel with a vertical BoxLayout.
     * Add child components to the returned panel directly.
     */
    public static JPanel card(String title) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 90), 1),
                new EmptyBorder(10, 12, 10, 12)));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JLabel titleLbl = styledLabel(title, FONT_HEADER, ACCENT2);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLbl.setBorder(new EmptyBorder(0, 0, 8, 0));
        card.add(titleLbl);
        return card;
    }

    /** Creates a left-aligned horizontal flow row panel. */
    public static JPanel row() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        return row;
    }

    public static void applyDarkLookAndFeel() {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        UIManager.put("Panel.background",      BG_DARK);
        UIManager.put("ScrollPane.background", BG_DARK);
        UIManager.put("ComboBox.background",   BG_CARD);
        UIManager.put("ComboBox.foreground",   TEXT_MAIN);
        UIManager.put("ScrollBar.thumb",       BG_PANEL);
        UIManager.put("ScrollBar.track",       BG_DARK);
    }

    /** Formats a nanosecond duration to a human-readable string rounded to 0.1. */
    public static String formatNano(long nanos) {
        double ms  = nanos / 1_000_000.0;
        double sec = ms / 1000.0;
        if (sec >= 0.1) return String.format("%.1f s",  sec);
        if (ms  >= 1.0) return String.format("%.1f ms", ms);
        return String.format("%.0f µs", nanos / 1000.0);
    }
}