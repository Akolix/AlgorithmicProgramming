package app.gui;

import app.dataset.Movie;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

import static app.gui.UIFactory.*;

/**
 * Left-side panel containing:
 *   - A styled JTable showing movie results
 *   - A read-only log area showing each algorithm execution with its timing
 */
public class TablePanel extends JPanel {

    private final DefaultTableModel tableModel;
    private final JTextArea         logArea;

    public TablePanel() {
        setLayout(new BorderLayout(0, 8));
        setBackground(BG_DARK);
        setBorder(new EmptyBorder(12, 14, 8, 8));

        // ── Table ──────────────────────────────────────────────────────────
        String[] columns = {"#", "Title", "Year", "Genre", "Rating ★", "Box Office ($M)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(tableModel);
        styleTable(table);

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBackground(BG_CARD);
        tableScroll.getViewport().setBackground(BG_CARD);
        tableScroll.setBorder(BorderFactory.createLineBorder(BG_PANEL, 1));

        // ── Log area ───────────────────────────────────────────────────────
        logArea = new JTextArea(5, 40);
        logArea.setEditable(false);
        logArea.setBackground(new Color(12, 12, 22));
        logArea.setForeground(SUCCESS);
        logArea.setFont(FONT_MONO);
        logArea.setBorder(new EmptyBorder(6, 8, 6, 8));

        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createLineBorder(BG_PANEL));
        logScroll.setPreferredSize(new Dimension(0, 115));

        JPanel logPanel = new JPanel(new BorderLayout(0, 4));
        logPanel.setOpaque(false);
        logPanel.add(styledLabel("Execution Log", FONT_HEADER, TEXT_DIM), BorderLayout.NORTH);
        logPanel.add(logScroll, BorderLayout.CENTER);

        add(styledLabel("Movie Results", FONT_HEADER, ACCENT2), BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);
        add(logPanel,    BorderLayout.SOUTH);
    }

    /** Replaces table contents with the given movie list. */
    public void populate(List<Movie> movies) {
        tableModel.setRowCount(0);
        int i = 1;
        for (Movie m : movies) {
            tableModel.addRow(new Object[]{
                    i++,
                    m.getTitle(),
                    m.getYear(),
                    m.getGenre(),
                    String.format("%.1f", m.getRating()),
                    String.format("$%.0f M", m.getBoxOffice())
            });
        }
    }

    /** Appends a line to the execution log and scrolls to the bottom. */
    public void log(String msg) {
        logArea.append(msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    // ── Table styling ──────────────────────────────────────────────────────

    private void styleTable(JTable table) {
        table.setBackground(BG_CARD);
        table.setForeground(TEXT_MAIN);
        table.setFont(FONT_BODY);
        table.setRowHeight(24);
        table.setGridColor(BG_PANEL);
        table.setSelectionBackground(new Color(99, 179, 237, 60));
        table.setSelectionForeground(TEXT_MAIN);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader header = table.getTableHeader();
        header.setBackground(BG_PANEL);
        header.setForeground(ACCENT);
        header.setFont(FONT_HEADER);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT));

        int[] widths = {35, 260, 55, 100, 70, 120};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                                                           boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setBackground(sel ? new Color(99, 179, 237, 60)
                        : (row % 2 == 0 ? BG_CARD : ROW_ALT));
                setForeground(TEXT_MAIN);
                setBorder(new EmptyBorder(0, 6, 0, 6));
                return this;
            }
        });
    }
}