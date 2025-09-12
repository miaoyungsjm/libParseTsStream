package com.excellence.ggz.libparsetsstream.Descriptor;

/**
 * @author ggz
 * @date 2021/3/30
 */
public class SmoothingBufferDescriptor extends Descriptor {
    private int sbLeakRate;
    private int sbSize;

    public SmoothingBufferDescriptor(int descriptorTag, int descriptorLength, byte[] descriptorBuff) {
        super(descriptorTag, descriptorLength, descriptorBuff);
    }

    public int getSbLeakRate() {
        return sbLeakRate;
    }

    public void setSbLeakRate(int sbLeakRate) {
        this.sbLeakRate = sbLeakRate;
    }

    public int getSbSize() {
        return sbSize;
    }

    public void setSbSize(int sbSize) {
        this.sbSize = sbSize;
    }
}
