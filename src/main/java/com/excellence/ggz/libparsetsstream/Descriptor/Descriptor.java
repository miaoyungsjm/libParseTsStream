package com.excellence.ggz.libparsetsstream.Descriptor;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.toHexString;

/**
 * @author ggz
 * @date 2021/3/30
 */
public class Descriptor {
    public int descriptorTag;
    public int descriptorLength;
    public byte[] descriptorBuff;

    public static Descriptor newInstance(byte[] buff) {
        int descriptorTag = buff[0] & 0xFF;
        int descriptorLength = buff[1] & 0xFF;
        byte[] descriptorBuff = new byte[descriptorLength];
        System.arraycopy(buff, 2, descriptorBuff, 0, descriptorLength);
        return new Descriptor(descriptorTag, descriptorLength, descriptorBuff);
    }

    public static List<Descriptor> newInstanceList(byte[] buff) {
        List<Descriptor> descriptorList = new ArrayList<>();
        int i = 0;
        while (i < buff.length) {
            int descriptorTag = buff[i] & 0xFF;
            int descriptorLength = buff[1 + i] & 0xFF;
            byte[] descriptorBuff = new byte[descriptorLength];
            System.arraycopy(buff, 2 + i, descriptorBuff, 0, descriptorLength);
            Descriptor descriptor = new Descriptor(descriptorTag, descriptorLength, descriptorBuff);
            descriptorList.add(descriptor);
            int oneDescriptor = 2 + descriptorLength;
            i += oneDescriptor;
        }
        return descriptorList;
    }

    public Descriptor(int descriptorTag, int descriptorLength, byte[] descriptorBuff) {
        this.descriptorTag = descriptorTag;
        this.descriptorLength = descriptorLength;
        this.descriptorBuff = descriptorBuff;
    }

    public int getDescriptorTag() {
        return descriptorTag;
    }

    public void setDescriptorTag(int descriptorTag) {
        this.descriptorTag = descriptorTag;
    }

    public int getDescriptorLength() {
        return descriptorLength;
    }

    public void setDescriptorLength(int descriptorLength) {
        this.descriptorLength = descriptorLength;
    }

    public byte[] getDescriptorBuff() {
        return descriptorBuff;
    }

    public void setDescriptorBuff(byte[] descriptorBuff) {
        this.descriptorBuff = descriptorBuff;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        String descriptor = "[Descriptor] descriptorTag: 0x" + toHexString(descriptorTag) + "\n" +
                "[Descriptor] descriptorLength: 0x" + toHexString(descriptorLength) + "\n" +
                "[Descriptor] descriptorBuff: \n";
        builder.append(descriptor);
        for (int i = 0; i < descriptorBuff.length; i++) {
            String tmp = "0x" + toHexString(descriptorBuff[i] & 0xFF) + ", ";
            builder.append(tmp);
            if (i > 0 && i % 20 == 0) {
                builder.append("\n");
            }
        }
        builder.append("\n");
        return builder.toString();
    }
}
