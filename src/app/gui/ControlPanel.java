package app.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import static app.gui.UIFactory.BG_DARK;

/**
 * Right-side panel that stacks all control cards vertically:
 * DataStructureCard → SearchCard → SortCard → ViewCard
 *
 * MainWindow reads the public card fields to wire up action callbacks.
 */
public class ControlPanel extends JPanel {

    public final DataStructureCard dataStructureCard;
    public final SearchCard        searchCard;
    public final SortCard          sortCard;
    public final ViewCard          viewCard;

    public ControlPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(BG_DARK);
        setBorder(new EmptyBorder(12, 8, 12, 14));

        dataStructureCard = new DataStructureCard();
        searchCard        = new SearchCard();
        sortCard          = new SortCard();
        viewCard          = new ViewCard();

        add(dataStructureCard);
        add(Box.createVerticalStrut(10));
        add(searchCard);
        add(Box.createVerticalStrut(10));
        add(sortCard);
        add(Box.createVerticalStrut(10));
        add(viewCard);
        add(Box.createVerticalGlue());
    }
}