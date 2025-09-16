package com.excellence.ggz.libparsetsstream.Section;

import static com.excellence.ggz.libparsetsstream.Section.ProgramAssociationSectionManager.PAT_PID;
import static com.excellence.ggz.libparsetsstream.Section.ServiceDescriptionSectionManager.SDT_PID;
import static java.lang.Integer.toHexString;

import com.excellence.ggz.libparsetsstream.Descriptor.Descriptor;
import com.excellence.ggz.libparsetsstream.Packet.Packet;
import com.excellence.ggz.libparsetsstream.Section.entity.Component;
import com.excellence.ggz.libparsetsstream.Section.entity.ProgramMapSection;
import com.excellence.ggz.libparsetsstream.Section.entity.Section;

import java.util.List;
import java.util.concurrent.Flow;

/**
 * @author ggz
 * @date 2021/3/22
 */
public class ProgramMapSectionManager extends AbstractSectionManager implements Flow.Subscriber<Packet> {
    private static final String TAG = ProgramMapSectionManager.class.getName();
    public static final int PMT_TABLE_ID = 0x02;
    private static final int PMS_SECTION_HEADER = 9;
    private static final int CRC_32 = 4;

    private static volatile ProgramMapSectionManager sInstance = null;

    private Flow.Subscription mSubscription;

    public static ProgramMapSectionManager getInstance() {
        if (sInstance == null) {
            synchronized (ProgramMapSectionManager.class) {
                if (sInstance == null) {
                    sInstance = new ProgramMapSectionManager();
                }
            }
        }
        return sInstance;
    }

    private ProgramMapSectionManager() {
    }

    @Override
    public void parseSection(Section section) {
        mLogger.debug(TAG, "[PMS] parseSection");

        int pid = section.getPid();
        int tableId = section.getTableId();
        int sectionSyntaxIndicator = section.getSectionSyntaxIndicator();
        int sectionLength = section.getSectionLength();
        byte[] buff = section.getSectionBuff();

        int programNumber = (((buff[0] & 0xFF) << 8) | (buff[1] & 0xFF)) & 0xFFFF;
        int versionNumber = (buff[2] >> 1) & 0x1F;
        int currentNextIndicator = buff[2] & 0x1;
        int sectionNumber = buff[3] & 0xFF;
        int lastSectionNumber = buff[4] & 0xFF;
        int pcrPid = (((buff[5] & 0x1F) << 8) | (buff[6] & 0xFF)) & 0x1FFF;
        int programInfoLength = (((buff[7] & 0x3) << 8) | (buff[8] & 0xFF)) & 0x3FF;
        byte[] crc32 = new byte[CRC_32];
        System.arraycopy(buff, buff.length - CRC_32, crc32, 0, CRC_32);

        byte[] programInfoBuff = new byte[programInfoLength];
        System.arraycopy(buff, PMS_SECTION_HEADER, programInfoBuff, 0, programInfoLength);
        List<Descriptor> programInfoDescriptorList = Descriptor.newInstanceList(programInfoBuff);

        int componentLength = sectionLength - PMS_SECTION_HEADER - programInfoLength - CRC_32;
        byte[] componentBuff = new byte[componentLength];
        System.arraycopy(buff, PMS_SECTION_HEADER + programInfoLength,
                componentBuff, 0, componentLength);
        List<Component> componentList = Component.newInstanceList(componentBuff);

        ProgramMapSection pms = new ProgramMapSection(
                pid, tableId, sectionSyntaxIndicator, sectionLength,
                buff, programNumber, versionNumber, currentNextIndicator,
                sectionNumber, lastSectionNumber, pcrPid, programInfoLength,
                programInfoDescriptorList, componentList, crc32);

        if (mOnParseListener != null) {
            mOnParseListener.onFinish(pms, pid);
        }
        mCompletionSignal.refreshStatusMap(pid);
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        mLogger.debug(TAG, "[PMS] onSubscribe");
        mSubscription = subscription;
        mSubscription.request(1);
    }

    @Override
    public void onNext(Packet packet) {
        mLogger.debug(TAG, "[PMS] onNext get packet pid: 0x" + toHexString(packet.getPid()));
        if (packet.getPid() != PAT_PID && packet.getPid() != SDT_PID) {
            boolean isCompleted = mCompletionSignal.checkStatusMap(packet.getPid());
            if (!isCompleted) {
                mLogger.debug(TAG, "[PMS] onNext assembleSection pid: 0x" + toHexString(packet.getPid()));
                assembleSection(PMT_TABLE_ID, packet);
            }
        }
        mSubscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {
        mLogger.error(TAG, throwable.getMessage());
    }

    @Override
    public void onComplete() {
        mLogger.debug(TAG, "[PMS] onComplete");
        mSubscription.cancel();
        clearSection();
    }
}
