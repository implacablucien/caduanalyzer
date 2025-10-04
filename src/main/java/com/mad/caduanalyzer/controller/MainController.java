package com.mad.caduanalyzer.controller;

import com.mad.caduanalyzer.model.FrameConfig;
import com.mad.caduanalyzer.model.TelemetryFrame;
import com.mad.caduanalyzer.model.TelemetryPacket;
import com.mad.caduanalyzer.view.ConfigDialog;
import com.mad.caduanalyzer.view.MainFrame;
import com.mad.caduanalyzer.view.MainResultWindow;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.io.File;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class MainController {
    private final MainFrame view;
    private final ObjectMapper mapper = new ObjectMapper();
    private static final File CONFIG_FILE = new File("fat_config.json");

    public MainController(MainFrame view, List<FrameConfig> initialConfigs) {
        this.view = view;
        var model = view.getTableModel();
        model.setConfigs(initialConfigs);

        // Wire actions
        view.getAddButton().addActionListener(e -> onAdd());
        view.getEditButton().addActionListener(e -> onEdit());
        view.getRemoveButton().addActionListener(e -> onRemove());
        view.getImportItem().addActionListener(e -> onImport());
        view.getExportItem().addActionListener(e -> onExport());
        view.getProcessFilesButton().addActionListener(e -> onProcessFiles());

        loadConfig();
        view.log("Ready. Configure frames and process telemetry files.");
    }

    // --- Configuration management ---

    private void onAdd() {
        var cfg = ConfigDialog.showDialog(view, null);
        if (cfg != null) {
            if (isDuplicate(cfg, -1)) {
                JOptionPane.showMessageDialog(view,
                        "Duplicate spacecraftId+VC combination exists.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            view.getTableModel().addConfig(cfg);
            view.log("Added: " + cfg);
            saveConfig();
        }
    }

    private void onEdit() {
        var sel = view.getTable().getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(view, "Select a row to edit.", "No selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        var modelIndex = view.getTable().convertRowIndexToModel(sel);
        var existing = view.getTableModel().getConfigs().get(modelIndex);
        var edited = ConfigDialog.showDialog(view, existing);
        if (edited != null) {
            if (isDuplicate(edited, modelIndex)) {
                JOptionPane.showMessageDialog(view,
                        "Duplicate spacecraftId+VC combination exists.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            view.getTableModel().updateConfig(modelIndex, edited);
            view.log("Updated: " + edited);
            saveConfig();
        }
    }

    private void onRemove() {
        var sel = view.getTable().getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(view, "Select a row to remove.", "No selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        var modelIndex = view.getTable().convertRowIndexToModel(sel);
        var cfg = view.getTableModel().getConfigs().get(modelIndex);
        var r = JOptionPane.showConfirmDialog(view, "Remove configuration: " + cfg + " ?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            view.getTableModel().removeConfig(modelIndex);
            view.log("Removed: " + cfg);
            saveConfig();
        }
    }

    private boolean isDuplicate(FrameConfig cfg, int ignoreIndex) {
        var list = view.getTableModel().getConfigs();
        return list.stream().anyMatch(c ->
                c.getSpacecraftId() == cfg.getSpacecraftId() &&
                        c.getVirtualChannelId() == cfg.getVirtualChannelId() &&
                        list.indexOf(c) != ignoreIndex);
    }

    private void onImport() {
        var chooser = new JFileChooser();
        if (chooser.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
            var file = chooser.getSelectedFile();
            try {
                var list = mapper.readValue(file, new TypeReference<List<FrameConfig>>() {});
                view.setConfigs(list);
                view.log("Imported " + list.size() + " configurations from " + file.getAbsolutePath());
                saveConfig();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(view, "Failed to import: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onExport() {
        var chooser = new JFileChooser();
        if (chooser.showSaveDialog(view) == JFileChooser.APPROVE_OPTION) {
            var file = chooser.getSelectedFile();
            try {
                var list = view.getTableModel().getConfigs();
                mapper.writerWithDefaultPrettyPrinter().writeValue(file, list);
                view.log("Exported " + list.size() + " configurations to " + file.getAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(view, "Failed to export: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveConfig() {
        try {
            var list = view.getTableModel().getConfigs();
            mapper.writerWithDefaultPrettyPrinter().writeValue(CONFIG_FILE, list);
        } catch (Exception ex) {
            view.log("Failed to save configuration: " + ex.getMessage());
        }
    }

    private void loadConfig() {
        if (!CONFIG_FILE.exists()) return;
        try {
            var list = mapper.readValue(CONFIG_FILE, new TypeReference<List<FrameConfig>>() {});
            view.setConfigs(list);
            view.log("Loaded last configuration from " + CONFIG_FILE.getAbsolutePath());
        } catch (Exception ex) {
            view.log("Failed to load last configuration: " + ex.getMessage());
        }
    }

    // --- Telemetry processing ---

    private void onProcessFiles() {
        var chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);
        int rc = chooser.showOpenDialog(view);
        if (rc != JFileChooser.APPROVE_OPTION) return;

        var files = chooser.getSelectedFiles();
        var processedFrames = new ArrayList<TelemetryFrame>();

        // MOCK processing: each file produces 5 frames, each with 2 packets
        for (var file : files) {
            for (int i = 0; i < 5; i++) {
                var packets = new ArrayList<TelemetryPacket>();
                for (int j = 0; j < 2; j++) {
                    packets.add(new TelemetryPacket(
                            100 + j, i, 1, 2, 42, "DEADBEEF"
                    ));
                }
                processedFrames.add(new TelemetryFrame(
                        42, i % 2, i, OffsetDateTime.now(), packets
                ));
            }
        }

        var mainResultWindow = new MainResultWindow(processedFrames);
        mainResultWindow.showWindow();
    }
}
