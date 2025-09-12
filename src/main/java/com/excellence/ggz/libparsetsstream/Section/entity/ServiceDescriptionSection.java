package com.excellence.ggz.libparsetsstream.Section.entity;

import java.util.List;

import static java.lang.Integer.toHexString;

/**
 * @author ggz
 * @date 2021/3/31
 */
public class ServiceDescriptionSection extends Section {

    private int transportStreamId;
    private int versionNumber;
    private int currentNextIndicator;
    private int sectionNumber;
    private int lastSectionNumber;
    private int originalNetworkId;
    private List<Service> serviceList;
    private byte[] crc32;

    public ServiceDescriptionSection(int pid, int tableId, int sectionSyntaxIndicator, int sectionLength,
                                     byte[] sectionBuff, int transportStreamId, int versionNumber,
                                     int currentNextIndicator, int sectionNumber, int lastSectionNumber,
                                     int originalNetworkId, List<Service> serviceList, byte[] crc32) {
        super(pid, tableId, sectionSyntaxIndicator, sectionLength, sectionBuff);
        this.transportStreamId = transportStreamId;
        this.versionNumber = versionNumber;
        this.currentNextIndicator = currentNextIndicator;
        this.sectionNumber = sectionNumber;
        this.lastSectionNumber = lastSectionNumber;
        this.originalNetworkId = originalNetworkId;
        this.serviceList = serviceList;
        this.crc32 = crc32;
    }

    public int getTransportStreamId() {
        return transportStreamId;
    }

    public void setTransportStreamId(int transportStreamId) {
        this.transportStreamId = transportStreamId;
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

    public int getOriginalNetworkId() {
        return originalNetworkId;
    }

    public void setOriginalNetworkId(int originalNetworkId) {
        this.originalNetworkId = originalNetworkId;
    }

    public List<Service> getServiceList() {
        return serviceList;
    }

    public void setServiceList(List<Service> serviceList) {
        this.serviceList = serviceList;
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
        String sdt = "[SDT] pid: 0x" + toHexString(pid) + "\n" +
                "[SDT] tableId: 0x" + toHexString(tableId) + "\n" +
                "[SDT] sectionSyntaxIndicator: 0x" + toHexString(sectionSyntaxIndicator) + "\n" +
                "[SDT] sectionLength: 0x" + toHexString(sectionLength) + "\n" +
                "[SDT] transportStreamId: 0x" + toHexString(transportStreamId) + "\n" +
                "[SDT] versionNumber: 0x" + toHexString(versionNumber) + "\n" +
                "[SDT] currentNextIndicator: 0x" + toHexString(currentNextIndicator) + "\n" +
                "[SDT] sectionNumber: 0x" + toHexString(sectionNumber) + "\n" +
                "[SDT] lastSectionNumber: 0x" + toHexString(lastSectionNumber) + "\n" +
                "[SDT] originalNetworkId: 0x" + toHexString(originalNetworkId) + "\n" +
                "[SDT] serviceList: \n";
        builder.append(sdt);
        for (Service service : serviceList) {
            builder.append(service.toString());
        }
        return builder.toString();
    }
}
