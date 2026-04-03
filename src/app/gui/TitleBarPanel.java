package app.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.IntConsumer;

import static app.gui.UIFactory.*;

public class TitleBarPanel extends JPanel {

    private final JSlider datasetSlider;
    private final JLabel  sliderValueLabel;

    public TitleBarPanel(IntConsumer onApply, Runnable onUpload) {
        setLayout(new BorderLayout(16, 0));
        setBackground(BG_PANEL);
        setBorder(new EmptyBorder(14, 20, 14, 20));

        // Left: title + subtitle
        JLabel title = styledLabel("Dataset & Algorithm Explorer", FONT_TITLE, ACCENT);
        JLabel sub   = styledLabel(
                "Movies Dataset  ·  3 Data Structures  ·  2 Search Algorithms  ·  3 Sort Algorithms",
                FONT_BODY, TEXT_DIM);

        JPanel left = new JPanel(new GridLayout(2, 1, 0, 2));
        left.setOpaque(false);
        left.add(title);
        left.add(sub);

        JButton uploadBtn = accentButton("Upload CSV");
        uploadBtn.addActionListener(e -> onUpload.run());

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
        sliderPanel.add(uploadBtn);

        add(left,        BorderLayout.WEST);
        add(sliderPanel, BorderLayout.EAST);
    }

    /**
     * Updates the slider's maximum value and adjusts current value if needed.
     * @param max New maximum dataset size
     */
    public void setMaxDatasetSize(int max) {
        int currentValue = datasetSlider.getValue();
        datasetSlider.setMaximum(max);

        // Adjust tick spacing based on range
        if (max <= 100) {
            datasetSlider.setMajorTickSpacing(20);
        } else if (max <= 500) {
            datasetSlider.setMajorTickSpacing(100);
        } else {
            datasetSlider.setMajorTickSpacing(200);
        }

        // If current value exceeds new max, clamp it
        if (currentValue > max) {
            datasetSlider.setValue(max);
            sliderValueLabel.setText(max + " movies");
        }
    }

    /**
     * Sets the current slider value without triggering the change listener's text update
     * (though the listener will update it anyway, this is fine).
     * @param size New dataset size to display
     */
    public void setCurrentSize(int size) {
        datasetSlider.setValue(size);
        sliderValueLabel.setText(size + " movies");
    }

    /**
     * Gets the current slider value.
     * @return Current dataset size
     */
    public int getCurrentSize() {
        return datasetSlider.getValue();
    }
}