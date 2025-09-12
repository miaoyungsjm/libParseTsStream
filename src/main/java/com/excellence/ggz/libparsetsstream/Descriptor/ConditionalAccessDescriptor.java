package com.excellence.ggz.libparsetsstream.Descriptor;

import java.util.List;

/**
 * @author ggz
 * @date 2021/3/30
 */
public class ConditionalAccessDescriptor extends Descriptor {
    private int caSystemId;
    private int caPid;
    List<Integer> privateDataByteList;

    public ConditionalAccessDescriptor(int descriptorTag, int descriptorLength, byte[] descriptorBuff) {
        super(descriptorTag, descriptorLength, descriptorBuff);
    }

    public int getCaSystemId() {
        return caSystemId;
    }

    public void setCaSystemId(int caSystemId) {
        this.caSystemId = caSystemId;
    }

    public int getCaPid() {
        return caPid;
    }

    public void setCaPid(int caPid) {
        this.caPid = caPid;
    }

    public List<Integer> getPrivateDataByteList() {
        return privateDataByteList;
    }

    public void setPrivateDataByteList(List<Integer> privateDataByteList) {
        this.privateDataByteList = privateDataByteList;
    }
}
