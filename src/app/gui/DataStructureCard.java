package app.gui;

import javax.swing.*;
import java.awt.*;

import static app.gui.UIFactory.*;

/**
 * Control card for selecting which data structure to operate on.
 * Shows a description of the selected structure below the dropdown.
 */
public class DataStructureCard extends JPanel {

    private static final String[] NAMES = {
            "CustomArrayList   (Dynamic Array)",
            "CustomLinkedList  (Doubly Linked)",
            "CustomBST         (Binary Search Tree)"
    };

    private static final String[] DESCRIPTIONS = {
            "Dynamic array — capacity doubles when full  |  get O(1)  ·  add O(1) amortized",
            "Doubly linked list — head & tail pointers   |  add O(1)  ·  get O(n)",
            "Binary Search Tree — ordered by field       |  insert O(log n)  ·  traverse O(n)"
    };

    private final JComboBox<String> dsCombo;
    private final JLabel            descLabel;

    public DataStructureCard() {
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel card = card("Data Structure");

        dsCombo   = styledCombo(NAMES);
        descLabel = styledLabel(DESCRIPTIONS[0], FONT_BODY, TEXT_DIM);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        dsCombo.addActionListener(e -> descLabel.setText(DESCRIPTIONS[dsCombo.getSelectedIndex()]));
        dsCombo.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(dsCombo);
        card.add(Box.createVerticalStrut(6));
        card.add(descLabel);

        add(card);
    }

    /** Returns the 0-based index of the selected data structure. */
    public int getSelectedIndex() {
        return dsCombo.getSelectedIndex();
    }

    /** Returns the short display name of the selected structure. */
    public String getSelectedName() {
        switch (dsCombo.getSelectedIndex()) {
            case 1:  return "CustomLinkedList";
            case 2:  return "CustomBST";
            default: return "CustomArrayList";
        }
    }
}