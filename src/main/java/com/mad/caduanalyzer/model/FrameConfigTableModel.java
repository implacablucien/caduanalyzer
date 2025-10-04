package com.mad.caduanalyzer.model;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class FrameConfigTableModel extends AbstractTableModel {
    private final String[] columnNames = {
            "SC ID", "VC ID", "Frame Size", "ASM", "Secondary Header",
            "Secondary Header Size", "Sec Header Size", "Sec Trailer Size",
            "Op Ctrl Field", "FECW"
    };

    private List<FrameConfig> configs;

    public FrameConfigTableModel(List<FrameConfig> configs) {
        this.configs = configs;
    }

    public List<FrameConfig> getConfigs() { return configs; }

    public void setConfigs(List<FrameConfig> configs) {
        this.configs = configs;
        fireTableDataChanged();
    }

    public void addConfig(FrameConfig cfg) {
        configs.add(cfg);
        fireTableRowsInserted(configs.size() - 1, configs.size() - 1);
    }

    public void updateConfig(int index, FrameConfig cfg) {
        configs.set(index, cfg);
        fireTableRowsUpdated(index, index);
    }

    public void removeConfig(int index) {
        configs.remove(index);
        fireTableRowsDeleted(index, index);
    }

    @Override
    public int getRowCount() { return configs.size(); }

    @Override
    public int getColumnCount() { return columnNames.length; }

    @Override
    public String getColumnName(int col) { return columnNames[col]; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        FrameConfig c = configs.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> c.getSpacecraftId();
            case 1 -> c.getVirtualChannelId();
            case 2 -> c.getFrameSize();
            case 3 -> c.getAsm();
            case 4 -> c.isSecondaryHeader();
            case 5 -> c.getSecondaryHeaderSize();
            case 6 -> c.getSecurityHeaderSize();
            case 7 -> c.getSecurityTrailerSize();
            case 8 -> c.isOperationalControlField();
            case 9 -> c.isFrameErrorControlWord();
            default -> null;
        };
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case 0,1,2,5,6,7 -> Integer.class;
            case 4,8,9 -> Boolean.class;
            default -> String.class;
        };
    }

    @Override
    public boolean isCellEditable(int row, int col) { return false; }
}
