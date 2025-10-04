package com.mad.caduanalyzer.view;

import com.mad.caduanalyzer.model.TelemetryFrame;
import com.mad.caduanalyzer.view.result.AllPacketsDialog;
import com.mad.caduanalyzer.view.result.ResultDialog;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainResultWindow extends JFrame {

    private final List<TelemetryFrame> frames;

    public MainResultWindow(List<TelemetryFrame> frames) {
        super("Telemetry Processing Results");
        this.frames = frames;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        // --- Tabs ---
        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab 1: Frame View
        JPanel frameViewPanel = new JPanel(new BorderLayout());
        var resultDialog = new ResultDialog(this, frames);
        frameViewPanel.add(resultDialog.getContentPane(), BorderLayout.CENTER);
        tabbedPane.addTab("Frames", frameViewPanel);

        // Tab 2: All Packets View
        JPanel allPacketsPanel = new JPanel(new BorderLayout());
        var allPacketsDialog = new AllPacketsDialog(this, frames);
        allPacketsPanel.add(allPacketsDialog.getContentPane(), BorderLayout.CENTER);
        tabbedPane.addTab("All Packets", allPacketsPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    public void showWindow() {
        SwingUtilities.invokeLater(() -> setVisible(true));
    }
}
