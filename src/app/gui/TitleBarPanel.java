package app.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.IntConsumer;

import static app.gui.UIFactory.*;

/**
 * Top bar containing the application title, subtitle,
 * and the dataset-size slider with an Apply button.
 */
public class TitleBarPanel extends JPanel {

    private final JSlider datasetSlider;
    private final JLabel  sliderValueLabel;

    /**
     * @param onApply Called with the selected dataset size when "Apply" is clicked.
     */
    public TitleBarPanel(IntConsumer onApply) {
        setLayout(new BorderLayout(16, 0));
        setBackground(BG_PANEL);
        setBorder(new EmptyBorder(14, 20, 14, 20));

        // Left: title + subtitle
        JLabel title = styledLabel("🎬  Dataset & Algorithm Explorer", FONT_TITLE, ACCENT);
        JLabel sub   = styledLabel(
                "Movies Dataset  ·  3 Data Structures  ·  2 Search Algorithms  ·  3 Sort Algorithms",
                FONT_BODY, TEXT_DIM);

        JPanel left = new JPanel(new GridLayout(2, 1, 0, 2));
        left.setOpaque(false);
        left.add(title);
        left.add(sub);

        // Right: slider + apply button
        datasetSlider    = new JSlider(5, 50, 50);
        sliderValueLabel = styledLabel("50 movies", FONT_STATUS, WARNING);

        datasetSlider.setOpaque(false);
        datasetSlider.setForeground(ACCENT);
        datasetSlider.setMajorTickSpacing(15);
        datasetSlider.setPaintTicks(true);
        datasetSlider.setPreferredSize(new Dimension(200, 40));
        datasetSlider.addChangeListener(e ->
                sliderValueLabel.setText(datasetSlider.getValue() + " movies"));

        JButton applyBtn = accentButton("Apply");
        applyBtn.addActionListener(e -> onApply.accept(datasetSlider.getValue()));

        JPanel sliderPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        sliderPanel.setOpaque(false);
        sliderPanel.add(styledLabel("Dataset size:", FONT_BODY, TEXT_DIM));
        sliderPanel.add(datasetSlider);
        sliderPanel.add(sliderValueLabel);
        sliderPanel.add(applyBtn);

        add(left,        BorderLayout.WEST);
        add(sliderPanel, BorderLayout.EAST);
    }
}