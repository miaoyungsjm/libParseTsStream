package com.excellence.ggz.libparsetsstream.Section.entity;

import java.util.List;

import static java.lang.Integer.toHexString;

public class ProgramAssociationSection extends Section {

    private int transportStreamId;
    private int versionNumber;
    private int currentNextIndicator;
    private int sectionNumber;
    private int lastSectionNumber;
    private List<Program> programList;
    private byte[] crc32;

    public ProgramAssociationSection(int pid, int tableId, int sectionSyntaxIndicator, int sectionLength,
                                     byte[] sectionBuff, int transportStreamId, int versionNumber,
                                     int currentNextIndicator, int sectionNumber, int lastSectionNumber,
                                     List<Program> programList, byte[] crc32) {
        super(pid, tableId, sectionSyntaxIndicator, sectionLength, sectionBuff);
        this.transportStreamId = transportStreamId;
        this.versionNumber = versionNumber;
        this.currentNextIndicator = currentNextIndicator;
        this.sectionNumber = sectionNumber;
        this.lastSectionNumber = lastSectionNumber;
        this.programList = programList;
        this.crc32 = crc32;
    }

    public int getTransportStreamId() {
        return transportStreamId;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public int getCurrentNextIndicator() {
        return currentNextIndicator;
    }

    public int getSectionNumber() {
        return sectionNumber;
    }

    public int getLastSectionNumber() {
        return lastSectionNumber;
    }

    public List<Program> getProgramList() {
        return programList;
    }

    public byte[] getCrc32() {
        return crc32;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        String pat = "[PAT] pid: 0x" + toHexString(pid) + "\n" +
                "[PAT] tableId: 0x" + toHexString(tableId) + "\n" +
                "[PAT] sectionSyntaxIndicator: 0x" + toHexString(sectionSyntaxIndicator) + "\n" +
                "[PAT] sectionLength: 0x" + toHexString(sectionLength) + "\n" +
                "[PAT] transportStreamId: 0x" + toHexString(transportStreamId) + "\n" +
                "[PAT] versionNumber: 0x" + toHexString(versionNumber) + "\n" +
                "[PAT] currentNextIndicator: 0x" + toHexString(currentNextIndicator) + "\n" +
                "[PAT] sectionNumber: 0x" + toHexString(sectionNumber) + "\n" +
                "[PAT] lastSectionNumber: 0x" + toHexString(lastSectionNumber) + "\n" +
                "[PAT] programList: \n";
        builder.append(pat);
        for (Program program : programList) {
            builder.append(program.toString());
        }
        return builder.toString();
    }
}
