package com.excellence.ggz.libparsetsstream.Descriptor;

import static java.lang.Integer.toHexString;

/**
 * @author ggz
 * @date 2021/3/30
 */
public class ServiceDescriptor extends Descriptor {
    private int serviceType;
    private int serviceProviderNameLength;
    private byte[] serviceProviderName;
    private int serviceNameLength;
    private byte[] serviceName;

    public ServiceDescriptor(int descriptorTag, int descriptorLength, byte[] descriptorBuff,
                             int serviceType, int serviceProviderNameLength, byte[] serviceProviderName,
                             int serviceNameLength, byte[] serviceName) {
        super(descriptorTag, descriptorLength, descriptorBuff);
        this.serviceType = serviceType;
        this.serviceProviderNameLength = serviceProviderNameLength;
        this.serviceProviderName = serviceProviderName;
        this.serviceNameLength = serviceNameLength;
        this.serviceName = serviceName;
    }

    public int getServiceType() {
        return serviceType;
    }

    public void setServiceType(int serviceType) {
        this.serviceType = serviceType;
    }

    public int getServiceProviderNameLength() {
        return serviceProviderNameLength;
    }

    public void setServiceProviderNameLength(int serviceProviderNameLength) {
        this.serviceProviderNameLength = serviceProviderNameLength;
    }

    public byte[] getServiceProviderName() {
        return serviceProviderName;
    }

    public void setServiceProviderName(byte[] serviceProviderName) {
        this.serviceProviderName = serviceProviderName;
    }

    public int getServiceNameLength() {
        return serviceNameLength;
    }

    public void setServiceNameLength(int serviceNameLength) {
        this.serviceNameLength = serviceNameLength;
    }

    public byte[] getServiceName() {
        return serviceName;
    }

    public void setServiceName(byte[] serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public String toString() {
        return "[ServiceDescriptor] descriptorTag: 0x" + toHexString(descriptorTag) + "\n" +
                "[ServiceDescriptor] descriptorLength: 0x" + toHexString(descriptorLength) + "\n" +
                "[ServiceDescriptor] serviceType: 0x" + toHexString(serviceType) + "\n" +
                "[ServiceDescriptor] serviceProviderNameLength: 0x" + toHexString(serviceProviderNameLength) + "\n" +
                "[ServiceDescriptor] serviceProviderName: " + new String(serviceProviderName) + "\n" +
                "[ServiceDescriptor] serviceNameLength: 0x" + toHexString(serviceNameLength) + "\n" +
                "[ServiceDescriptor] serviceName: " + new String(serviceName) + "\n";
    }
}
