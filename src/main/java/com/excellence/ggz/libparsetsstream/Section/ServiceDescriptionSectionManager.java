package com.excellence.ggz.libparsetsstream.Section;

import static java.lang.Integer.toHexString;

import com.excellence.ggz.libparsetsstream.Packet.Packet;
import com.excellence.ggz.libparsetsstream.Section.entity.Section;
import com.excellence.ggz.libparsetsstream.Section.entity.Service;
import com.excellence.ggz.libparsetsstream.Section.entity.ServiceDescriptionSection;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * @author ggz
 * @date 2021/3/22
 */
public class ServiceDescriptionSectionManager extends AbstractSectionManager implements Observer {
    private static final String TAG = ServiceDescriptionSectionManager.class.getName();
    public static final int SDT_PID = 0x0011;
    public static final int SDT_TABLE_ID = 0x42;
    private static final int SDS_SECTION_HEADER = 8;
    private static final int CRC_32 = 4;

    private static volatile ServiceDescriptionSectionManager sInstance = null;

    private ServiceDescriptionSectionManager() {
    }

    public static ServiceDescriptionSectionManager getInstance() {
        if (sInstance == null) {
            synchronized (ServiceDescriptionSectionManager.class) {
                if (sInstance == null) {
                    sInstance = new ServiceDescriptionSectionManager();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void parseSection(Section section) {
        mLogger.debug(TAG, "[SDS] parse Section");

        int pid = section.getPid();
        int tableId = section.getTableId();
        int sectionSyntaxIndicator = section.getSectionSyntaxIndicator();
        int sectionLength = section.getSectionLength();
        byte[] buff = section.getSectionBuff();

        int transportStreamId = (((buff[0] & 0xFF) << 8) | buff[1] & 0xFF) & 0xFFFF;
        int versionNumber = (buff[2] >> 1) & 0x1F;
        int currentNextIndicator = buff[2] & 0x1;
        int sectionNumber = buff[3] & 0xFF;
        int lastSectionNumber = buff[4] & 0xFF;
        int originalNetworkId = (((buff[5] & 0xFF) << 8) | buff[6] & 0xFF) & 0xFFFF;
        byte[] crc32 = new byte[CRC_32];
        System.arraycopy(buff, buff.length - CRC_32, crc32, 0, CRC_32);

        int serviceLength = sectionLength - SDS_SECTION_HEADER - CRC_32;
        byte[] serviceBuff = new byte[serviceLength];
        System.arraycopy(buff, SDS_SECTION_HEADER, serviceBuff, 0, serviceLength);
        List<Service> serviceList = Service.newInstanceList(serviceBuff);

        ServiceDescriptionSection sds = new ServiceDescriptionSection(
                pid, tableId, sectionSyntaxIndicator, sectionLength,
                buff, transportStreamId, versionNumber, currentNextIndicator,
                sectionNumber, lastSectionNumber, originalNetworkId, serviceList,
                crc32);

        if (mParseListener != null) {
            mParseListener.onFinish(sds);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        Packet packet = (Packet) arg;
        mLogger.debug(TAG, "[SDS] get packet pid: 0x" + toHexString(packet.getPid()));

        if (packet.getPid() == SDT_PID) {
            mLogger.debug(TAG, "[SDS] assembleSection");
            assembleSection(SDT_TABLE_ID, packet);
        }
    }
}
