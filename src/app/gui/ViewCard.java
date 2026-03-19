package app.gui;

import javax.swing.*;

import static app.gui.UIFactory.*;

/**
 * Control card with utility buttons:
 * View All Movies (show full current structure) and Reset Dataset (reload from scratch).
 */
public class ViewCard extends JPanel {

    private final JButton viewAllBtn;
    private final JButton resetBtn;

    public ViewCard() {
        setLayout(new java.awt.BorderLayout());
        setOpaque(false);

        JPanel card = card("View / Reset");

        viewAllBtn = accentButton("View All Movies");
        resetBtn   = accentButton("Reset Dataset");

        JPanel btnRow = row();
        btnRow.add(viewAllBtn);
        btnRow.add(Box.createHorizontalStrut(6));
        btnRow.add(resetBtn);

        card.add(btnRow);
        add(card);
    }

    public void onViewAll(Runnable action) { viewAllBtn.addActionListener(e -> action.run()); }
    public void onReset(Runnable action)   { resetBtn.addActionListener(e   -> action.run()); }
}