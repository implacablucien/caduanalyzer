package com.mad.caduanalyzer.view;

import com.mad.caduanalyzer.model.FrameConfig;

import javax.swing.*;
import java.awt.*;

public class ConfigDialog extends JDialog {
    private JTextField scField, vcField, frameSizeField, asmField, secHeaderSizeField,
            secHdrSizeField, secTrailerSizeField;
    private JCheckBox secondaryHeaderBox, opControlBox, fecwBox;
    private boolean ok;
    private FrameConfig result;

    private ConfigDialog(Window owner, FrameConfig initial) {
        super(owner, "Frame Configuration", ModalityType.APPLICATION_MODAL);
        initComponents(initial);
        pack();
        setLocationRelativeTo(owner);
    }

    private void initComponents(FrameConfig initial) {
        scField = new JTextField(initial != null ? String.valueOf(initial.getSpacecraftId()) : "0");
        vcField = new JTextField(initial != null ? String.valueOf(initial.getVirtualChannelId()) : "0");
        frameSizeField = new JTextField(initial != null ? String.valueOf(initial.getFrameSize()) : "0");
        asmField = new JTextField(initial != null ? initial.getAsm() : "");
        secondaryHeaderBox = new JCheckBox("", initial != null && initial.isSecondaryHeader());
        secHeaderSizeField = new JTextField(initial != null ? String.valueOf(initial.getSecondaryHeaderSize()) : "0");
        secHdrSizeField = new JTextField(initial != null ? String.valueOf(initial.getSecurityHeaderSize()) : "0");
        secTrailerSizeField = new JTextField(initial != null ? String.valueOf(initial.getSecurityTrailerSize()) : "0");
        opControlBox = new JCheckBox("", initial != null && initial.isOperationalControlField());
        fecwBox = new JCheckBox("", initial != null && initial.isFrameErrorControlWord());

        JPanel grid = new JPanel(new GridLayout(0,2,6,6));
        grid.add(new JLabel("Spacecraft ID:")); grid.add(scField);
        grid.add(new JLabel("Virtual Channel ID:")); grid.add(vcField);
        grid.add(new JLabel("Frame Size:")); grid.add(frameSizeField);
        grid.add(new JLabel("ASM (hex):")); grid.add(asmField);
        grid.add(new JLabel("Secondary Header:")); grid.add(secondaryHeaderBox);
        grid.add(new JLabel("Secondary Header Size:")); grid.add(secHeaderSizeField);
        grid.add(new JLabel("Security Header Size:")); grid.add(secHdrSizeField);
        grid.add(new JLabel("Security Trailer Size:")); grid.add(secTrailerSizeField);
        grid.add(new JLabel("Operational Control Field:")); grid.add(opControlBox);
        grid.add(new JLabel("FECW:")); grid.add(fecwBox);

        JButton okBtn = new JButton("OK");
        JButton cancelBtn = new JButton("Cancel");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(okBtn); buttons.add(cancelBtn);

        okBtn.addActionListener(e -> onOk());
        cancelBtn.addActionListener(e -> onCancel());

        getContentPane().setLayout(new BorderLayout(8,8));
        getContentPane().add(grid, BorderLayout.CENTER);
        getContentPane().add(buttons, BorderLayout.SOUTH);
    }

    private void onOk() {
        try {
            int sc = Integer.parseInt(scField.getText().trim());
            int vc = Integer.parseInt(vcField.getText().trim());
            int fs = Integer.parseInt(frameSizeField.getText().trim());
            String asm = asmField.getText().trim();
            boolean secondaryHeader = secondaryHeaderBox.isSelected();
            int secondaryHeaderSize = Integer.parseInt(secHeaderSizeField.getText().trim());
            int secHeader = Integer.parseInt(secHdrSizeField.getText().trim());
            int secTrailer = Integer.parseInt(secTrailerSizeField.getText().trim());
            boolean opControl = opControlBox.isSelected();
            boolean fecw = fecwBox.isSelected();

            if (!asm.isEmpty() && !asm.matches("([0-9A-Fa-f]{2})*")) {
                JOptionPane.showMessageDialog(this, "ASM must be even-length hex string", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            result = new FrameConfig(sc, vc, fs, asm, secondaryHeader, secondaryHeaderSize,
                    secHeader, secTrailer, opControl, fecw);
            ok = true;
            setVisible(false);
            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid integer input", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCancel() { ok = false; result = null; setVisible(false); dispose(); }

    public static FrameConfig showDialog(Component owner, FrameConfig initial) {
        Window w = owner instanceof Window ? (Window) owner : SwingUtilities.getWindowAncestor(owner);
        ConfigDialog d = new ConfigDialog(w, initial != null ? initial : new FrameConfig());
        d.setVisible(true);
        return d.ok ? d.result : null;
    }
}
