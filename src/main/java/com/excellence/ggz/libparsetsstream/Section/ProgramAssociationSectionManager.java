package com.excellence.ggz.libparsetsstream.Section;

import static java.lang.Integer.toHexString;

import com.excellence.ggz.libparsetsstream.Packet.Packet;
import com.excellence.ggz.libparsetsstream.Section.entity.Program;
import com.excellence.ggz.libparsetsstream.Section.entity.ProgramAssociationSection;
import com.excellence.ggz.libparsetsstream.Section.entity.Section;

import java.util.List;
import java.util.concurrent.Flow;

/**
 * @author ggz
 * @date 2021/3/22
 */
public class ProgramAssociationSectionManager extends AbstractSectionManager implements Flow.Subscriber<Packet> {
    private static final String TAG = ProgramAssociationSectionManager.class.getName();
    public static final int PAT_PID = 0x0000;
    public static final int PAT_TABLE_ID = 0x00;
    private static final int PAS_SECTION_HEADER = 5;
    private static final int CRC_32 = 4;

    private static volatile ProgramAssociationSectionManager sInstance = null;

    private Flow.Subscription mSubscription;

    private ProgramAssociationSectionManager() {
    }

    public static ProgramAssociationSectionManager getInstance() {
        if (sInstance == null) {
            synchronized (ProgramAssociationSectionManager.class) {
                if (sInstance == null) {
                    sInstance = new ProgramAssociationSectionManager();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void parseSection(Section section) {
        mLogger.debug(TAG, "[PAS] parseSection");

        int pid = section.getPid();
        int tableId = section.getTableId();
        int sectionSyntaxIndicator = section.getSectionSyntaxIndicator();
        int sectionLength = section.getSectionLength();
        byte[] buff = section.getSectionBuff();

        int transportStreamId = (((buff[0] & 0xFF) << 8) | (buff[1] & 0xFF)) & 0xFFFF;
        int versionNumber = (buff[2] >> 1) & 0x1F;
        int currentNextIndicator = buff[2] & 0x1;
        int sectionNumber = buff[3] & 0xFF;
        int lastSectionNumber = buff[4] & 0xFF;
        byte[] crc32 = new byte[CRC_32];
        System.arraycopy(buff, buff.length - CRC_32, crc32, 0, CRC_32);

        int programLength = sectionLength - PAS_SECTION_HEADER - CRC_32;
        byte[] programBuff = new byte[programLength];
        System.arraycopy(buff, PAS_SECTION_HEADER, programBuff, 0, programLength);
        List<Program> programList = Program.newInstanceList(programBuff);

        ProgramAssociationSection pas = new ProgramAssociationSection(
                pid, tableId, sectionSyntaxIndicator, sectionLength,
                buff, transportStreamId, versionNumber, currentNextIndicator,
                sectionNumber, lastSectionNumber, programList, crc32);

        if (mOnParseListener != null) {
            mOnParseListener.onFinish(pas, pid);
        }
        // 共享状态更新
        mCompletionSignal.refreshStatusMap(pid);
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        mLogger.debug(TAG, "[PAS] onSubscribe");
        mSubscription = subscription;
        mSubscription.request(1);
    }

    @Override
    public void onNext(Packet packet) {
        mLogger.debug(TAG, "[PAS] onNext get packet pid: 0x" + toHexString(packet.getPid()));
        if (packet.getPid() == PAT_PID) {
            boolean isCompleted = mCompletionSignal.checkStatusMap(PAT_PID);
            if (!isCompleted) {
                mLogger.debug(TAG, "[PAS] onNext assembleSection");
                assembleSection(PAT_TABLE_ID, packet);
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
        mLogger.debug(TAG, "[PAS] onComplete");
        mSubscription.cancel();
        clearSection();
    }
}
