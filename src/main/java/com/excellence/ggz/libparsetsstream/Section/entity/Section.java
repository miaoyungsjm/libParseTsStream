package com.excellence.ggz.libparsetsstream.Section.entity;

import static java.lang.Integer.toHexString;

/**
 * @author ggz
 * @date 2021/3/22
 */
public class Section {

    public int pid;
    public int tableId;
    public int sectionSyntaxIndicator;
    public int sectionLength;
    public byte[] sectionBuff;

    private int remainLength;

    public Section(int pid, int tableId, int sectionSyntaxIndicator,
                   int sectionLength, byte[] sectionBuff) {
        this.pid = pid;
        this.tableId = tableId;
        this.sectionSyntaxIndicator = sectionSyntaxIndicator;
        this.sectionLength = sectionLength;
        this.sectionBuff = sectionBuff;
    }

    public int getPid() {
        return pid;
    }

    public int getTableId() {
        return tableId;
    }

    public int getSectionSyntaxIndicator() {
        return sectionSyntaxIndicator;
    }

    public int getSectionLength() {
        return sectionLength;
    }

    public byte[] getSectionBuff() {
        return sectionBuff;
    }

    public int getRemainLength() {
        return remainLength;
    }

    public void setRemainLength(int remainLength) {
        this.remainLength = remainLength;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        String section = "[Section] pid: 0x" + toHexString(pid) + "\n" +
                "[Section] tableId: 0x" + toHexString(tableId) + "\n" +
                "[Section] sectionSyntaxIndicator: 0x" + toHexString(sectionSyntaxIndicator) + "\n" +
                "[Section] sectionLength: 0x" + toHexString(sectionLength) + "\n" +
                "[Section] sectionBuff: \n";
        builder.append(section);
        for (int i = 0; i < sectionBuff.length; i++) {
            String tmp = "0x" + toHexString(sectionBuff[i] & 0xFF) + ", ";
            builder.append(tmp);
            if (i > 0 && i % 20 == 0) {
                builder.append("\n");
            }
        }
        return builder.toString();
    }
}
