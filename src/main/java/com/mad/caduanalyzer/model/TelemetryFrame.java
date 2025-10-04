package com.mad.caduanalyzer.model;

import java.time.OffsetDateTime;
import java.util.List;

public class TelemetryFrame {
    private int spacecraftId;
    private int virtualChannelId;
    private int virtualChannelCounter;
    private OffsetDateTime timestamp; // new field
    private List<TelemetryPacket> packets;

    public TelemetryFrame(int spacecraftId, int virtualChannelId, int virtualChannelCounter,
                          OffsetDateTime timestamp, List<TelemetryPacket> packets) {
        this.spacecraftId = spacecraftId;
        this.virtualChannelId = virtualChannelId;
        this.virtualChannelCounter = virtualChannelCounter;
        this.timestamp = timestamp;
        this.packets = packets;
    }

    public int getSpacecraftId() { return spacecraftId; }
    public int getVirtualChannelId() { return virtualChannelId; }
    public int getVirtualChannelCounter() { return virtualChannelCounter; }
    public OffsetDateTime getTimestamp() { return timestamp; }
    public List<TelemetryPacket> getPackets() { return packets; }
}
