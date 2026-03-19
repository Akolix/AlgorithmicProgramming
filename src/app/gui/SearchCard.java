package app.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import static app.gui.UIFactory.*;

/**
 * Control card for search operations.
 * Contains a text field and buttons for Linear Search and Binary Search.
 * Binary Search note: requires sorted data — MainWindow handles pre-sorting.
 */
public class SearchCard extends JPanel {

    private static final String PLACEHOLDER = "e.g. Dark Knight  or  Action";

    private final JTextField searchField;
    private final JButton    linearBtn;
    private final JButton    binaryBtn;

    public SearchCard() {
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel card = card("Search Algorithms");

        searchField = new JTextField(PLACEHOLDER);
        searchField.setBackground(BG_DARK);
        searchField.setForeground(TEXT_DIM);
        searchField.setFont(FONT_BODY);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT, 1),
                new EmptyBorder(4, 8, 4, 8)));
        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        searchField.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals(PLACEHOLDER)) {
                    searchField.setText("");
                    searchField.setForeground(TEXT_MAIN);
                }
            }
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isBlank()) {
                    searchField.setText(PLACEHOLDER);
                    searchField.setForeground(TEXT_DIM);
                }
            }
        });

        linearBtn = accentButton("Linear Search  O(n)");
        binaryBtn = accentButton("Binary Search  O(log n)");
        binaryBtn.setToolTipText("Searches by exact title — data will be sorted first");

        JPanel btnRow = row();
        btnRow.add(linearBtn);
        btnRow.add(Box.createHorizontalStrut(6));
        btnRow.add(binaryBtn);

        card.add(searchField);
        card.add(Box.createVerticalStrut(8));
        card.add(btnRow);

        add(card);
    }

    /** Returns the trimmed search query, or empty string if placeholder is shown. */
    public String getQuery() {
        String text = searchField.getText().trim();
        return text.equals(PLACEHOLDER) ? "" : text;
    }

    public void onLinearSearch(Runnable action) { linearBtn.addActionListener(e -> action.run()); }
    public void onBinarySearch(Runnable action) { binaryBtn.addActionListener(e -> action.run()); }
}