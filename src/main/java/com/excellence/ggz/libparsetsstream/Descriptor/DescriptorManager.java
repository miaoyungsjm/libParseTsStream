package com.excellence.ggz.libparsetsstream.Descriptor;

/**
 * @author ggz
 * @date 2021/3/30
 */
public class DescriptorManager {
    private static final int TAG_SMOOTHING_BUFFER_DESCRIPTOR = 0x10;
    private static final int TAG_MAXIMUM_BITRATE_DESCRIPTOR = 0x0E;
    private static final int TAG_CONDITIONAL_ACCESS_DESCRIPTOR = 0x09;
    private static final int TAG_SERVICE_DESCRIPTOR = 0x48;

    private static volatile DescriptorManager sInstance = null;

    private DescriptorManager() {
    }

    public static DescriptorManager getInstance() {
        if (sInstance == null) {
            synchronized (DescriptorManager.class) {
                if (sInstance == null) {
                    sInstance = new DescriptorManager();
                }
            }
        }
        return sInstance;
    }

    public Descriptor parseDescriptor(Descriptor des) {
        int tag = des.getDescriptorTag();
        Descriptor descriptor;
        switch (tag) {
            case TAG_CONDITIONAL_ACCESS_DESCRIPTOR:
                descriptor = parseConditionalAccessDescriptor(des);
                break;
            case TAG_SERVICE_DESCRIPTOR:
                descriptor = parseServiceDescriptor(des);
                break;
            case TAG_MAXIMUM_BITRATE_DESCRIPTOR:
                descriptor = parseMaximumBitrateDescriptor(des);
                break;
            case TAG_SMOOTHING_BUFFER_DESCRIPTOR:
                descriptor = parseSmoothingBufferDescriptor(des);
                break;
            default:
                descriptor = des;
                break;
        }
        return descriptor;
    }

    private ConditionalAccessDescriptor parseConditionalAccessDescriptor(Descriptor descriptor) {
        int tag = descriptor.getDescriptorTag();
        int length = descriptor.getDescriptorLength();
        byte[] buff = descriptor.getDescriptorBuff();
        return new ConditionalAccessDescriptor(tag, length, buff);
    }

    private MaximumBitrateDescriptor parseMaximumBitrateDescriptor(Descriptor descriptor) {
        int tag = descriptor.getDescriptorTag();
        int length = descriptor.getDescriptorLength();
        byte[] buff = descriptor.getDescriptorBuff();
        return new MaximumBitrateDescriptor(tag, length, buff);
    }

    private SmoothingBufferDescriptor parseSmoothingBufferDescriptor(Descriptor descriptor) {
        int tag = descriptor.getDescriptorTag();
        int length = descriptor.getDescriptorLength();
        byte[] buff = descriptor.getDescriptorBuff();
        return new SmoothingBufferDescriptor(tag, length, buff);
    }

    private ServiceDescriptor parseServiceDescriptor(Descriptor descriptor) {
        int descriptorTag = descriptor.getDescriptorTag();
        int descriptorLength = descriptor.getDescriptorLength();
        byte[] descriptorBuff = descriptor.getDescriptorBuff();

        int serviceType = descriptorBuff[0] & 0xFF;
        int serviceProviderNameLength = descriptorBuff[1] & 0xFF;
        byte[] serviceProviderByte = new byte[serviceProviderNameLength];
        System.arraycopy(descriptorBuff, 2, serviceProviderByte, 0, serviceProviderNameLength);

        int serviceNameLength = descriptorBuff[2 + serviceProviderNameLength] & 0xFF;
        int startServiceNameBuffPos = serviceProviderNameLength + 3;
        byte[] serviceByte = new byte[serviceNameLength];
        System.arraycopy(descriptorBuff, startServiceNameBuffPos, serviceByte, 0, serviceNameLength);

        return new ServiceDescriptor(descriptorTag, descriptorLength, descriptorBuff, serviceType, serviceProviderNameLength,
                serviceProviderByte, serviceNameLength, serviceByte);
    }
}
