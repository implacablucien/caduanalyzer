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
import java.util.List;
import java.util.*;

public class AllPacketsDialog extends JDialog {

    private final JTable packetTable;
    private final PacketTableModel packetTableModel;
    private final List<TelemetryFrame> allFrames;

    private final JTextField scFilterField = new JTextField(5);
    private final JTextField vcFilterField = new JTextField(5);
    private final JTextField apidFilterField = new JTextField(5);
    private final JTextField pusTypeFilterField = new JTextField(5);
    private final JTextField ackedApidField = new JTextField(5);
    private final JTextField ackedSeqField = new JTextField(5);

    public AllPacketsDialog(Window owner, List<TelemetryFrame> frames) {
        super(owner, "All Packets", ModalityType.APPLICATION_MODAL);
        setSize(1100, 650);
        setLocationRelativeTo(owner);

        allFrames = frames;

        packetTableModel = new PacketTableModel();
        packetTable = new JTable(packetTableModel);
        packetTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        packetTable.setCellSelectionEnabled(true);

        // Copy/paste
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

        // Double-click to show full content
        packetTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = packetTable.getSelectedRow();
                    if (row >= 0) {
                        var packet = packetTableModel.getPacketAt(row);
                        JOptionPane.showMessageDialog(AllPacketsDialog.this,
                                packet.getContent(),
                                "Packet Content (APID=" + packet.getApid() + ")",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });

        var scroll = new JScrollPane(packetTable);

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
        filterPanel.add(new JLabel("Acked APID:"));
        filterPanel.add(ackedApidField);
        filterPanel.add(new JLabel("Acked Seq:"));
        filterPanel.add(ackedSeqField);

        var docListener = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { applyFilters(); }
            @Override public void removeUpdate(DocumentEvent e) { applyFilters(); }
            @Override public void changedUpdate(DocumentEvent e) { applyFilters(); }
        };

        scFilterField.getDocument().addDocumentListener(docListener);
        vcFilterField.getDocument().addDocumentListener(docListener);
        apidFilterField.getDocument().addDocumentListener(docListener);
        pusTypeFilterField.getDocument().addDocumentListener(docListener);
        ackedApidField.getDocument().addDocumentListener(docListener);
        ackedSeqField.getDocument().addDocumentListener(docListener);

        setLayout(new BorderLayout());
        add(filterPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        // Initially populate table
        applyFilters();
    }

    private void applyFilters() {
        var scText = scFilterField.getText().trim();
        var vcText = vcFilterField.getText().trim();
        var apidText = apidFilterField.getText().trim();
        var pusTypeText = pusTypeFilterField.getText().trim();
        var ackedApidText = ackedApidField.getText().trim();
        var ackedSeqText = ackedSeqField.getText().trim();

        var allPackets = allFrames.stream()
                .flatMap(f -> f.getPackets().stream()
                        .map(p -> new PacketWithFrame(p, f)))
                .filter(pwf -> scText.isEmpty() || Integer.toString(pwf.frame.getSpacecraftId()).equals(scText))
                .filter(pwf -> vcText.isEmpty() || Integer.toString(pwf.frame.getVirtualChannelId()).equals(vcText))
                .filter(pwf -> apidText.isEmpty() || Integer.toString(pwf.packet.getApid()).equals(apidText))
                .filter(pwf -> pusTypeText.isEmpty() || Integer.toString(pwf.packet.getPusServiceType()).equals(pusTypeText))
                .filter(pwf -> ackedApidText.isEmpty() || (pwf.packet.getAckedPacketApid() != null && pwf.packet.getAckedPacketApid().toString().equals(ackedApidText)))
                .filter(pwf -> ackedSeqText.isEmpty() || (pwf.packet.getAckedPacketSequence() != null && pwf.packet.getAckedPacketSequence().toString().equals(ackedSeqText)))
                .toList();

        packetTableModel.setPackets(allPackets);
    }

    // Wrapper class to keep frame info with packet
    private record PacketWithFrame(TelemetryPacket packet, TelemetryFrame frame) {}

    // --- Table model ---
    private static class PacketTableModel extends AbstractTableModel {
        private final List<PacketWithFrame> packets = new ArrayList<>();
        private final String[] columns = {
                "SC ID", "VC ID", "VC Counter", "Timestamp",
                "APID", "Seq", "PUS Type", "PUS Subtype",
                "Dest", "Preview", "Acked APID", "Acked Seq"
        };
        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssXXX");

        public void setPackets(List<PacketWithFrame> newPackets) {
            packets.clear();
            packets.addAll(newPackets);
            fireTableStructureChanged();
        }

        public TelemetryPacket getPacketAt(int row) {
            return packets.get(row).packet();
        }

        @Override
        public int getRowCount() { return packets.size(); }

        @Override
        public int getColumnCount() { return columns.length; }

        @Override
        public String getColumnName(int column) { return columns[column]; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            var pwf = packets.get(rowIndex);
            var p = pwf.packet();
            var f = pwf.frame();
            return switch (columnIndex) {
                case 0 -> f.getSpacecraftId();
                case 1 -> f.getVirtualChannelId();
                case 2 -> f.getVirtualChannelCounter();
                case 3 -> f.getTimestamp().format(formatter);
                case 4 -> p.getApid();
                case 5 -> p.getSequenceCounter();
                case 6 -> p.getPusServiceType();
                case 7 -> p.getPusServiceSubType();
                case 8 -> p.getDestinationId();
                case 9 -> {
                    var content = p.getContent();
                    yield content.length() > 30 ? content.substring(0,30) + "..." : content;
                }
                case 10 -> p.getPusServiceType() == 1 ? p.getAckedPacketApid() : null;
                case 11 -> p.getPusServiceType() == 1 ? p.getAckedPacketSequence() : null;
                default -> null;
            };
        }
    }
}
