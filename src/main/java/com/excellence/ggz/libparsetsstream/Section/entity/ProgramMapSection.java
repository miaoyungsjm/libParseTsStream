package com.excellence.ggz.libparsetsstream.Section.entity;

import com.excellence.ggz.libparsetsstream.Descriptor.Descriptor;

import java.util.List;

import static java.lang.Integer.toHexString;

/**
 * @author ggz
 * @date 2021/4/7
 */
public class ProgramMapSection extends Section {

    private int programNumber;
    private int versionNumber;
    private int currentNextIndicator;
    private int sectionNumber;
    private int lastSectionNumber;
    private int pcrPid;
    private int programInfoLength;
    private List<Descriptor> programInfoDescriptorList;
    private List<Component> componentList;
    private byte[] crc32;

    public ProgramMapSection(int pid, int tableId, int sectionSyntaxIndicator, int sectionLength,
                             byte[] sectionBuff, int programNumber, int versionNumber, int currentNextIndicator,
                             int sectionNumber, int lastSectionNumber, int pcrPid, int programInfoLength,
                             List<Descriptor> programInfoDescriptorList, List<Component> componentList,
                             byte[] crc32) {
        super(pid, tableId, sectionSyntaxIndicator, sectionLength, sectionBuff);
        this.programNumber = programNumber;
        this.versionNumber = versionNumber;
        this.currentNextIndicator = currentNextIndicator;
        this.sectionNumber = sectionNumber;
        this.lastSectionNumber = lastSectionNumber;
        this.pcrPid = pcrPid;
        this.programInfoLength = programInfoLength;
        this.programInfoDescriptorList = programInfoDescriptorList;
        this.componentList = componentList;
        this.crc32 = crc32;
    }

    public int getProgramNumber() {
        return programNumber;
    }

    public void setProgramNumber(int programNumber) {
        this.programNumber = programNumber;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(int versionNumber) {
        this.versionNumber = versionNumber;
    }

    public int getCurrentNextIndicator() {
        return currentNextIndicator;
    }

    public void setCurrentNextIndicator(int currentNextIndicator) {
        this.currentNextIndicator = currentNextIndicator;
    }

    public int getSectionNumber() {
        return sectionNumber;
    }

    public void setSectionNumber(int sectionNumber) {
        this.sectionNumber = sectionNumber;
    }

    public int getLastSectionNumber() {
        return lastSectionNumber;
    }

    public void setLastSectionNumber(int lastSectionNumber) {
        this.lastSectionNumber = lastSectionNumber;
    }

    public int getPcrPid() {
        return pcrPid;
    }

    public void setPcrPid(int pcrPid) {
        this.pcrPid = pcrPid;
    }

    public int getProgramInfoLength() {
        return programInfoLength;
    }

    public void setProgramInfoLength(int programInfoLength) {
        this.programInfoLength = programInfoLength;
    }

    public List<Descriptor> getProgramInfoDescriptorList() {
        return programInfoDescriptorList;
    }

    public void setProgramInfoDescriptorList(List<Descriptor> programInfoDescriptorList) {
        this.programInfoDescriptorList = programInfoDescriptorList;
    }

    public List<Component> getComponentList() {
        return componentList;
    }

    public void setComponentList(List<Component> componentList) {
        this.componentList = componentList;
    }

    public byte[] getCrc32() {
        return crc32;
    }

    public void setCrc32(byte[] crc32) {
        this.crc32 = crc32;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        String pmt = "[PMT] pid: 0x" + toHexString(pid) + "\n" +
                "[PMT] tableId: 0x" + toHexString(tableId) + "\n" +
                "[PMT] sectionSyntaxIndicator: 0x" + toHexString(sectionSyntaxIndicator) + "\n" +
                "[PMT] sectionLength: 0x" + toHexString(sectionLength) + "\n" +
                "[PMT] programNumber: 0x" + toHexString(programNumber) + "\n" +
                "[PMT] versionNumber: 0x" + toHexString(versionNumber) + "\n" +
                "[PMT] currentNextIndicator: 0x" + toHexString(currentNextIndicator) + "\n" +
                "[PMT] sectionNumber: 0x" + toHexString(sectionNumber) + "\n" +
                "[PMT] lastSectionNumber: 0x" + toHexString(lastSectionNumber) + "\n" +
                "[PMT] pcrPid: 0x" + toHexString(pcrPid) + "\n" +
                "[PMT] programInfoLength: 0x" + toHexString(programInfoLength) + "\n" +
                "[PMT] programInfoDescriptorList: \n";
        builder.append(pmt);
        for (Descriptor descriptor : programInfoDescriptorList) {
            builder.append(descriptor.toString());
        }
        for (Component component : componentList) {
            builder.append(component.toString());
        }
        return builder.toString();
    }
}
