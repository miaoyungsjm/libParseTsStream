package com.excellence.ggz.libparsetsstream.Section.entity;

import com.excellence.ggz.libparsetsstream.Descriptor.Descriptor;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.toHexString;

/**
 * @author ggz
 * @date 2021/4/7
 */
public class Component {
    private static final int COMPONENT_HEADER = 5;

    private int streamType;
    private int elementaryPid;
    private int esInfoLength;
    private List<Descriptor> esDescriptorList;

    public static List<Component> newInstanceList(byte[] buff) {
        List<Component> componentList = new ArrayList<>();
        int i = 0;
        while (i < buff.length) {
            int streamType = buff[i] & 0xFF;
            int elementaryPid = (((buff[1 + i] & 0x1F) << 8) | (buff[2 + i] & 0xFF)) & 0x1FFF;
            int esInfoLength = (((buff[3 + i] & 0x3) << 8) | (buff[4 + i] & 0xFF)) & 0x3FF;

            byte[] descriptorBuff = new byte[esInfoLength];
            System.arraycopy(buff, i + COMPONENT_HEADER,
                    descriptorBuff, 0, esInfoLength);
            List<Descriptor> esDescriptorList = Descriptor.newInstanceList(descriptorBuff);
            Component component = new Component(streamType, elementaryPid, esInfoLength,
                    esDescriptorList);
            componentList.add(component);
            int oneComponent = COMPONENT_HEADER + esInfoLength;
            i += oneComponent;
        }
        return componentList;
    }

    public Component(int streamType, int elementaryPid, int esInfoLength, List<Descriptor> esDescriptorList) {
        this.streamType = streamType;
        this.elementaryPid = elementaryPid;
        this.esInfoLength = esInfoLength;
        this.esDescriptorList = esDescriptorList;
    }

    public int getStreamType() {
        return streamType;
    }

    public void setStreamType(int streamType) {
        this.streamType = streamType;
    }

    public int getElementaryPid() {
        return elementaryPid;
    }

    public void setElementaryPid(int elementaryPid) {
        this.elementaryPid = elementaryPid;
    }

    public int getEsInfoLength() {
        return esInfoLength;
    }

    public void setEsInfoLength(int esInfoLength) {
        this.esInfoLength = esInfoLength;
    }

    public List<Descriptor> getEsDescriptorList() {
        return esDescriptorList;
    }

    public void setEsDescriptorList(List<Descriptor> esDescriptorList) {
        this.esDescriptorList = esDescriptorList;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        String component = "[Component] streamType: 0x" + toHexString(streamType) + "\n" +
                "[Component] elementaryPid: 0x" + toHexString(elementaryPid) + "\n" +
                "[Component] esInfoLength: 0x" + toHexString(esInfoLength) + "\n" +
                "[Component] esDescriptors: \n";
        builder.append(component);
        for (Descriptor descriptor : esDescriptorList) {
            builder.append(descriptor.toString());
        }
        return builder.toString();
    }
}
