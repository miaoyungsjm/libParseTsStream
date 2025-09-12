package com.excellence.ggz.libparsetsstream.Section.entity;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.toHexString;

/**
 * @author ggz
 * @date 2021/4/7
 */
public class Program {
    private static final int PROGRAM_LENGTH = 4;

    private int programNumber;
    private int networkId;
    private int programMapPid;
    private String programName = "";

    public static List<Program> newInstanceList(byte[] buff) {
        List<Program> programList = new ArrayList<>();
        for (int i = 0; i < buff.length; i += PROGRAM_LENGTH) {
            int programNumber = (((buff[i] & 0xFF) << 8) | (buff[1 + i] & 0xFF)) & 0xFFFF;
            int networkPid = -1;
            int programMapPid = -1;
            if (programNumber == 0) {
                networkPid = (((buff[2 + i] & 0x1F) << 8) | (buff[3 + i] & 0xFF)) & 0x1FFF;
            } else {
                programMapPid = (((buff[2 + i] & 0x1F) << 8) | (buff[3 + i] & 0xFF)) & 0x1FFF;
            }
            Program program = new Program(programNumber, networkPid, programMapPid);
            programList.add(program);
        }
        return programList;
    }

    public Program(int programNumber, int networkId, int programMapPid) {
        this.programNumber = programNumber;
        this.networkId = networkId;
        this.programMapPid = programMapPid;
    }

    public int getProgramNumber() {
        return programNumber;
    }

    public int getNetworkId() {
        return networkId;
    }

    public int getProgramMapPid() {
        return programMapPid;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    @Override
    public String toString() {
        return "[Program] programNumber: 0x" + toHexString(programNumber) + "\n" +
                "[Program] networkId: 0x" + toHexString(networkId) + "\n" +
                "[Program] programMapPid: 0x" + toHexString(programMapPid) + "\n" +
                "[Program] programName: " + programName + "\n";
    }
}
