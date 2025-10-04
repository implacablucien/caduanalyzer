package com.mad.caduanalyzer.model;

import java.util.Objects;

public class FrameConfig {
    private int spacecraftId;
    private int virtualChannelId;
    private int frameSize;                 // before ASM
    private String asm;                    // hex string
    private boolean secondaryHeader;
    private int secondaryHeaderSize;       // after secondaryHeader
    private int securityHeaderSize;
    private int securityTrailerSize;
    private boolean operationalControlField;
    private boolean frameErrorControlWord;

    public FrameConfig() {}

    public FrameConfig(int spacecraftId, int virtualChannelId, int frameSize, String asm,
                       boolean secondaryHeader, int secondaryHeaderSize,
                       int securityHeaderSize, int securityTrailerSize,
                       boolean operationalControlField, boolean frameErrorControlWord) {
        this.spacecraftId = spacecraftId;
        this.virtualChannelId = virtualChannelId;
        this.frameSize = frameSize;
        this.asm = asm;
        this.secondaryHeader = secondaryHeader;
        this.secondaryHeaderSize = secondaryHeaderSize;
        this.securityHeaderSize = securityHeaderSize;
        this.securityTrailerSize = securityTrailerSize;
        this.operationalControlField = operationalControlField;
        this.frameErrorControlWord = frameErrorControlWord;
    }

    // getters & setters
    public int getSpacecraftId() { return spacecraftId; }
    public void setSpacecraftId(int spacecraftId) { this.spacecraftId = spacecraftId; }

    public int getVirtualChannelId() { return virtualChannelId; }
    public void setVirtualChannelId(int virtualChannelId) { this.virtualChannelId = virtualChannelId; }

    public int getFrameSize() { return frameSize; }
    public void setFrameSize(int frameSize) { this.frameSize = frameSize; }

    public String getAsm() { return asm; }
    public void setAsm(String asm) { this.asm = asm; }

    public boolean isSecondaryHeader() { return secondaryHeader; }
    public void setSecondaryHeader(boolean secondaryHeader) { this.secondaryHeader = secondaryHeader; }

    public int getSecondaryHeaderSize() { return secondaryHeaderSize; }
    public void setSecondaryHeaderSize(int secondaryHeaderSize) { this.secondaryHeaderSize = secondaryHeaderSize; }

    public int getSecurityHeaderSize() { return securityHeaderSize; }
    public void setSecurityHeaderSize(int securityHeaderSize) { this.securityHeaderSize = securityHeaderSize; }

    public int getSecurityTrailerSize() { return securityTrailerSize; }
    public void setSecurityTrailerSize(int securityTrailerSize) { this.securityTrailerSize = securityTrailerSize; }

    public boolean isOperationalControlField() { return operationalControlField; }
    public void setOperationalControlField(boolean operationalControlField) { this.operationalControlField = operationalControlField; }

    public boolean isFrameErrorControlWord() { return frameErrorControlWord; }
    public void setFrameErrorControlWord(boolean frameErrorControlWord) { this.frameErrorControlWord = frameErrorControlWord; }

    @Override
    public String toString() {
        return "SC=" + spacecraftId + " VC=" + virtualChannelId + " ASM=" + asm + " FS=" + frameSize;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        FrameConfig that = (FrameConfig) o;
        return spacecraftId == that.spacecraftId && virtualChannelId == that.virtualChannelId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(spacecraftId, virtualChannelId);
    }
}
