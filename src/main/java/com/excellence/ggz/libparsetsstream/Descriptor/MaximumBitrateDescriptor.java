package com.excellence.ggz.libparsetsstream.Descriptor;

/**
 * @author ggz
 * @date 2021/3/30
 */
public class MaximumBitrateDescriptor extends Descriptor {
    private int maximumBitrate;

    public MaximumBitrateDescriptor(int descriptorTag, int descriptorLength, byte[] descriptorBuff) {
        super(descriptorTag, descriptorLength, descriptorBuff);
    }

    public int getMaximumBitrate() {
        return maximumBitrate;
    }

    public void setMaximumBitrate(int maximumBitrate) {
        this.maximumBitrate = maximumBitrate;
    }
}
