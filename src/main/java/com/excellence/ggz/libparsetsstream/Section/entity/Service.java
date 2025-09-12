package com.excellence.ggz.libparsetsstream.Section.entity;

import com.excellence.ggz.libparsetsstream.Descriptor.Descriptor;
import com.excellence.ggz.libparsetsstream.Descriptor.DescriptorManager;
import com.excellence.ggz.libparsetsstream.Descriptor.ServiceDescriptor;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.toHexString;

/**
 * @author ggz
 * @date 2021/3/30
 */
public class Service {
    private static final int SERVICE_HEADER = 5;

    private int serviceId;
    private int eitScheduleFlag;
    private int eitPresentFollowingFlag;
    private int runningStatus;
    private int freeCaMode;
    private int descriptorsLoopLength;
    private ServiceDescriptor serviceDescriptor;

    public static List<Service> newInstanceList(byte[] buff) {
        List<Service> serviceList = new ArrayList<>();
        int i = 0;
        while (i < buff.length) {
            int serviceId = (((buff[i] & 0xFF) << 8) | (buff[1 + i] & 0xFF)) & 0xFFFF;
            int eitScheduleFollowingFlag = (buff[2 + i] >> 1) & 0x1;
            int eitPresentFollowingFlag = (buff[2 + i]) & 0x1;
            int runningStatus = (buff[3 + i] >> 5) & 0x7;
            int freeCaMode = (buff[3 + i] >> 4) & 0x1;
            int descriptorLoopLength = (((buff[3 + i] & 0xF) << 4) | (buff[4 + i] & 0xFF)) & 0xFFF;

            byte[] descriptorBuff = new byte[descriptorLoopLength];
            System.arraycopy(buff, i + SERVICE_HEADER,
                    descriptorBuff, 0, descriptorLoopLength);
            List<Descriptor> descriptorList = Descriptor.newInstanceList(descriptorBuff);
            DescriptorManager descriptorManager = DescriptorManager.getInstance();
            ServiceDescriptor serviceDescriptor =
                    (ServiceDescriptor) descriptorManager.parseDescriptor(descriptorList.get(0));

            Service service = new Service(serviceId, eitScheduleFollowingFlag, eitPresentFollowingFlag,
                    runningStatus, freeCaMode, descriptorLoopLength, serviceDescriptor);
            serviceList.add(service);

            int oneService = SERVICE_HEADER + descriptorLoopLength;
            i += oneService;
        }
        return serviceList;
    }

    public Service(int serviceId, int eitScheduleFlag, int eitPresentFollowingFlag,
                   int runningStatus, int freeCaMode, int descriptorsLoopLength,
                   ServiceDescriptor serviceDescriptor) {
        this.serviceId = serviceId;
        this.eitScheduleFlag = eitScheduleFlag;
        this.eitPresentFollowingFlag = eitPresentFollowingFlag;
        this.runningStatus = runningStatus;
        this.freeCaMode = freeCaMode;
        this.descriptorsLoopLength = descriptorsLoopLength;
        this.serviceDescriptor = serviceDescriptor;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public int getEitScheduleFlag() {
        return eitScheduleFlag;
    }

    public void setEitScheduleFlag(int eitScheduleFlag) {
        this.eitScheduleFlag = eitScheduleFlag;
    }

    public int getEitPresentFollowingFlag() {
        return eitPresentFollowingFlag;
    }

    public void setEitPresentFollowingFlag(int eitPresentFollowingFlag) {
        this.eitPresentFollowingFlag = eitPresentFollowingFlag;
    }

    public int getRunningStatus() {
        return runningStatus;
    }

    public void setRunningStatus(int runningStatus) {
        this.runningStatus = runningStatus;
    }

    public int getFreeCaMode() {
        return freeCaMode;
    }

    public void setFreeCaMode(int freeCaMode) {
        this.freeCaMode = freeCaMode;
    }

    public int getDescriptorsLoopLength() {
        return descriptorsLoopLength;
    }

    public void setDescriptorsLoopLength(int descriptorsLoopLength) {
        this.descriptorsLoopLength = descriptorsLoopLength;
    }

    public ServiceDescriptor getServiceDescriptor() {
        return serviceDescriptor;
    }

    public void setServiceDescriptor(ServiceDescriptor serviceDescriptor) {
        this.serviceDescriptor = serviceDescriptor;
    }

    @Override
    public String toString() {
        return "[Service] serviceId: 0x" + toHexString(serviceId) + "\n" +
                "[Service] eitScheduleFlag: 0x" + toHexString(eitScheduleFlag) + "\n" +
                "[Service] eitPresentFollowingFlag: 0x" + toHexString(eitPresentFollowingFlag) + "\n" +
                "[Service] runningStatus: 0x" + toHexString(runningStatus) + "\n" +
                "[Service] freeCaMode: 0x" + toHexString(freeCaMode) + "\n" +
                "[Service] descriptorsLoopLength: 0x" + toHexString(descriptorsLoopLength) + "\n" +
                "[Service] serviceDescriptor: \n" +
                serviceDescriptor.toString();
    }
}
