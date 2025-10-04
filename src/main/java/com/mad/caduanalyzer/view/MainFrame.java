package com.mad.caduanalyzer.view;

import com.mad.caduanalyzer.model.FrameConfig;
import com.mad.caduanalyzer.model.FrameConfigTableModel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {
    private final JMenuItem importItem;
    private final JMenuItem exportItem;
    private final JButton addButton;
    private final JButton editButton;
    private final JButton removeButton;
    private final JButton processFilesButton;
    private final JTable table;
    private final FrameConfigTableModel tableModel;
    private final JTextArea logArea;

    public MainFrame() {
        setTitle("Frame Analysis Tool");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 550);
        setLocationRelativeTo(null);

        // Menu bar
        var menuBar = new JMenuBar();
        var fileMenu = new JMenu("File");
        importItem = new JMenuItem("Import JSON...");
        exportItem = new JMenuItem("Export JSON...");
        fileMenu.add(importItem);
        fileMenu.add(exportItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        // Table
        tableModel = new FrameConfigTableModel(new java.util.ArrayList<>());
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        var tableScroll = new JScrollPane(table);

        // Buttons
        addButton = new JButton("Add");
        editButton = new JButton("Edit...");
        removeButton = new JButton("Remove");
        processFilesButton = new JButton("Process Files...");

        var buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(processFilesButton);

        // Log area
        logArea = new JTextArea(6, 90);
        logArea.setEditable(false);
        var logScroll = new JScrollPane(logArea);

        // Layout
        var splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScroll, logScroll);
        splitPane.setResizeWeight(0.75);

        add(buttonPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }

    // Getters for controller
    public JMenuItem getImportItem() { return importItem; }
    public JMenuItem getExportItem() { return exportItem; }
    public JButton getAddButton() { return addButton; }
    public JButton getEditButton() { return editButton; }
    public JButton getRemoveButton() { return removeButton; }
    public JButton getProcessFilesButton() { return processFilesButton; }
    public JTable getTable() { return table; }
    public FrameConfigTableModel getTableModel() { return tableModel; }

    public void log(String message) { logArea.append(message + "\n"); }

    public void setConfigs(List<FrameConfig> configs) { tableModel.setConfigs(configs); }
}
