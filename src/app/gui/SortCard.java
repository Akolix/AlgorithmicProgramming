package app.gui;

import javax.swing.*;

import static app.gui.UIFactory.*;

/**
 * Control card for sort operations.
 * Allows selecting a field, direction, and one of three sort algorithms:
 * Bubble Sort, Merge Sort, or Selection Sort.
 */
public class SortCard extends JPanel {

    private final JComboBox<String> fieldCombo;
    private final JComboBox<String> directionCombo;
    private final JButton           bubbleBtn;
    private final JButton           mergeBtn;
    private final JButton           selectionBtn;

    public SortCard() {
        setLayout(new java.awt.BorderLayout());
        setOpaque(false);

        JPanel card = card("Sort Algorithms");

        fieldCombo     = styledCombo(new String[]{"Title", "Year", "Rating", "BoxOffice"});
        directionCombo = styledCombo(new String[]{"Ascending ↑", "Descending ↓"});

        JPanel row1 = row();
        row1.add(styledLabel("Sort by:", FONT_BODY, TEXT_DIM));
        row1.add(Box.createHorizontalStrut(6));
        row1.add(fieldCombo);
        row1.add(Box.createHorizontalStrut(10));
        row1.add(directionCombo);

        bubbleBtn    = accentButton("Bubble Sort");
        mergeBtn     = accentButton("Merge Sort");
        selectionBtn = accentButton("Selection Sort");

        bubbleBtn.setToolTipText("O(n²) — repeated adjacent swaps");
        mergeBtn.setToolTipText("O(n log n) — divide and conquer");
        selectionBtn.setToolTipText("O(n²) — finds minimum each pass");

        JPanel btnRow = row();
        btnRow.add(bubbleBtn);
        btnRow.add(Box.createHorizontalStrut(6));
        btnRow.add(mergeBtn);
        btnRow.add(Box.createHorizontalStrut(6));
        btnRow.add(selectionBtn);

        card.add(row1);
        card.add(Box.createVerticalStrut(8));
        card.add(btnRow);

        add(card);
    }

    /** Returns the selected sort field in lowercase (e.g. "title", "rating"). */
    public String getSortField() {
        return ((String) fieldCombo.getSelectedItem()).toLowerCase();
    }

    /** Returns true if Ascending is selected. */
    public boolean isAscending() {
        return directionCombo.getSelectedIndex() == 0;
    }

    public void onBubbleSort(Runnable action)    { bubbleBtn.addActionListener(e    -> action.run()); }
    public void onMergeSort(Runnable action)     { mergeBtn.addActionListener(e     -> action.run()); }
    public void onSelectionSort(Runnable action) { selectionBtn.addActionListener(e -> action.run()); }
}