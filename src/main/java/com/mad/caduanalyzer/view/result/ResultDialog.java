package com.mad.caduanalyzer.view.result;

import com.mad.caduanalyzer.model.TelemetryFrame;
import com.mad.caduanalyzer.model.TelemetryPacket;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class ResultDialog extends JDialog {

    private final JTable frameTable;
    private final JTable packetTable;
    private final FrameTableModel frameTableModel;
    private final PacketTableModel packetTableModel;
    private final List<TelemetryFrame> allFrames;

    private final JTextField scFilterField = new JTextField(5);
    private final JTextField vcFilterField = new JTextField(5);
    private final JTextField apidFilterField = new JTextField(5);
    private final JTextField pusTypeFilterField = new JTextField(5);

    public ResultDialog(Window owner, List<TelemetryFrame> frames) {
        super(owner, "Processing Results", ModalityType.APPLICATION_MODAL);
        setSize(1000, 650);
        setLocationRelativeTo(owner);

        allFrames = new ArrayList<>(frames);

        // --- Frame table ---
        frameTableModel = new FrameTableModel(allFrames);
        frameTable = new JTable(frameTableModel);
        frameTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        var frameScroll = new JScrollPane(frameTable);

        // --- Packet table ---
        packetTableModel = new PacketTableModel();
        packetTable = new JTable(packetTableModel);
        packetTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        packetTable.setCellSelectionEnabled(true);

        // Copy/paste support
        packetTable.getInputMap().put(KeyStroke.getKeyStroke("ctrl C"), "copy");
        packetTable.getActionMap().put("copy", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                var selection = packetTable.getSelectedRows();
                var cols = packetTable.getSelectedColumns();
                var sb = new StringBuilder();
                for (var row : selection) {
                    for (int c = 0; c < cols.length; c++) {
                        sb.append(packetTable.getValueAt(row, cols[c]));
                        if (c < cols.length - 1) sb.append("\t");
                    }
                    sb.append("\n");
                }
                var clip = Toolkit.getDefaultToolkit().getSystemClipboard();
                clip.setContents(new java.awt.datatransfer.StringSelection(sb.toString()), null);
            }
        });

        // Double-click for full content
        packetTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = packetTable.getSelectedRow();
                    if (row >= 0) {
                        var packet = packetTableModel.getPacketAt(row);
                        JOptionPane.showMessageDialog(ResultDialog.this,
                                packet.getContent(),
                                "Packet Content (APID=" + packet.getApid() + ")",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });

        var packetScroll = new JScrollPane(packetTable);

        // --- Split pane ---
        var splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, frameScroll, packetScroll);
        splitPane.setResizeWeight(0.55);

        // --- Filter panel ---
        var filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("SC ID:"));
        filterPanel.add(scFilterField);
        filterPanel.add(new JLabel("VC ID:"));
        filterPanel.add(vcFilterField);
        filterPanel.add(new JLabel("Packet APID:"));
        filterPanel.add(apidFilterField);
        filterPanel.add(new JLabel("Packet PUS Type:"));
        filterPanel.add(pusTypeFilterField);

        var docListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applyFilters();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applyFilters();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applyFilters();
            }
        };

        scFilterField.getDocument().addDocumentListener(docListener);
        vcFilterField.getDocument().addDocumentListener(docListener);
        apidFilterField.getDocument().addDocumentListener(docListener);
        pusTypeFilterField.getDocument().addDocumentListener(docListener);

        // --- Layout ---
        setLayout(new BorderLayout());
        add(filterPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        // --- Selection listener ---
        frameTable.getSelectionModel().addListSelectionListener(e -> updatePacketTable());

        if (!allFrames.isEmpty()) {
            frameTable.setRowSelectionInterval(0, 0);
            updatePacketTable();
        }
    }

    private void applyFilters() {
        var scText = scFilterField.getText().trim();
        var vcText = vcFilterField.getText().trim();
        var apidText = apidFilterField.getText().trim();
        var pusTypeText = pusTypeFilterField.getText().trim();

        var filteredFrames = allFrames.stream()
                .filter(f -> scText.isEmpty() || Integer.toString(f.getSpacecraftId()).equals(scText))
                .filter(f -> vcText.isEmpty() || Integer.toString(f.getVirtualChannelId()).equals(vcText))
                .map(f -> {
                    var filteredPackets = f.getPackets().stream()
                            .filter(p -> apidText.isEmpty() || Integer.toString(p.getApid()).equals(apidText))
                            .filter(p -> pusTypeText.isEmpty() || Integer.toString(p.getPusServiceType()).equals(pusTypeText))
                            .toList();
                    return new TelemetryFrame(f.getSpacecraftId(), f.getVirtualChannelId(),
                            f.getVirtualChannelCounter(), f.getTimestamp(), filteredPackets);
                })
                .filter(f -> !f.getPackets().isEmpty())
                .toList();

        frameTableModel.setFrames(filteredFrames);
        if (!filteredFrames.isEmpty()) {
            frameTable.setRowSelectionInterval(0, 0);
        }
        updatePacketTable();
    }

    private void updatePacketTable() {
        var sel = frameTable.getSelectedRow();
        if (sel < 0) {
            packetTableModel.setPackets(Collections.emptyList());
            return;
        }
        var modelIndex = frameTable.convertRowIndexToModel(sel);
        var frame = frameTableModel.getFrameAt(modelIndex);
        packetTableModel.setPackets(frame.getPackets());
    }

    // --- Frame Table Model ---
    static class FrameTableModel extends AbstractTableModel {
        private final String[] columns = {"DateTime", "SC ID", "VC ID", "VC Counter", "Packet Count"};
        private List<TelemetryFrame> frames;
        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSXXX");

        public FrameTableModel(List<TelemetryFrame> frames) {
            this.frames = frames;
        }

        public void setFrames(List<TelemetryFrame> frames) {
            this.frames = frames;
            fireTableDataChanged();
        }

        public TelemetryFrame getFrameAt(int row) {
            return frames.get(row);
        }

        @Override
        public int getRowCount() {
            return frames.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            var f = frames.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> f.getTimestamp().format(formatter);
                case 1 -> f.getSpacecraftId();
                case 2 -> f.getVirtualChannelId();
                case 3 -> f.getVirtualChannelCounter();
                case 4 -> f.getPackets().size();
                default -> null;
            };
        }
    }

    // --- Packet Table Model ---
    static class PacketTableModel extends AbstractTableModel {
        private final List<TelemetryPacket> packets = new ArrayList<>();
        private final String[] columns = {
                "APID", "Seq", "PUS Type", "PUS Subtype", "Dest", "Preview",
                "Acked APID", "Acked Seq"
        };

        public void setPackets(List<TelemetryPacket> packets) {
            this.packets.clear();
            this.packets.addAll(packets);
            fireTableStructureChanged(); // refresh columns for PUS Type 1
        }

        public TelemetryPacket getPacketAt(int row) {
            return packets.get(row);
        }

        @Override
        public int getRowCount() {
            return packets.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            var p = packets.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> p.getApid();
                case 1 -> p.getSequenceCounter();
                case 2 -> p.getPusServiceType();
                case 3 -> p.getPusServiceSubType();
                case 4 -> p.getDestinationId();
                case 5 -> {
                    var content = p.getContent();
                    yield content.length() > 30 ? content.substring(0, 30) + "..." : content;
                }
                case 6 -> p.getPusServiceType() == 1 ? p.getAckedPacketApid() : null;
                case 7 -> p.getPusServiceType() == 1 ? p.getAckedPacketSequence() : null;
                default -> null;
            };
        }
    }
}
