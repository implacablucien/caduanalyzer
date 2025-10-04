package com.mad.caduanalyzer.model;

public class TelemetryPacket {
    private int apid;
    private int sequenceCounter;
    private int pusServiceType;
    private int pusServiceSubType;
    private int destinationId;
    private String content;

    // For PUS Type 1, additional fields
    private Integer ackedPacketApid;
    private Integer ackedPacketSequence;

    public TelemetryPacket(int apid, int sequenceCounter, int pusServiceType, int pusServiceSubType,
                           int destinationId, String content) {
        this.apid = apid;
        this.sequenceCounter = sequenceCounter;
        this.pusServiceType = pusServiceType;
        this.pusServiceSubType = pusServiceSubType;
        this.destinationId = destinationId;
        this.content = content;
        this.ackedPacketApid = null;
        this.ackedPacketSequence = null;
    }

    public TelemetryPacket(int apid, int sequenceCounter, int pusServiceType, int pusServiceSubType,
                           int destinationId, String content,
                           Integer ackedPacketApid, Integer ackedPacketSequence) {
        this.apid = apid;
        this.sequenceCounter = sequenceCounter;
        this.pusServiceType = pusServiceType;
        this.pusServiceSubType = pusServiceSubType;
        this.destinationId = destinationId;
        this.content = content;
        this.ackedPacketApid = ackedPacketApid;
        this.ackedPacketSequence = ackedPacketSequence;
    }

    public int getApid() { return apid; }
    public int getSequenceCounter() { return sequenceCounter; }
    public int getPusServiceType() { return pusServiceType; }
    public int getPusServiceSubType() { return pusServiceSubType; }
    public int getDestinationId() { return destinationId; }
    public String getContent() { return content; }

    public Integer getAckedPacketApid() { return ackedPacketApid; }
    public Integer getAckedPacketSequence() { return ackedPacketSequence; }
}
