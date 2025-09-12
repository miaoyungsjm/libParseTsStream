package com.excellence.ggz.libparsetsstream;

import static java.lang.Integer.toHexString;

import com.excellence.ggz.libparsetsstream.Interface.OnParseListener;
import com.excellence.ggz.libparsetsstream.Logger.LoggerManager;
import com.excellence.ggz.libparsetsstream.Packet.PacketManager;
import com.excellence.ggz.libparsetsstream.Section.ProgramAssociationSectionManager;
import com.excellence.ggz.libparsetsstream.Section.ProgramMapSectionManager;
import com.excellence.ggz.libparsetsstream.Section.ServiceDescriptionSectionManager;
import com.excellence.ggz.libparsetsstream.Section.entity.Section;

import java.util.List;

/**
 * @author ggz
 * @date 2021/3/30
 */
public class TsManager {
    private static volatile TsManager sInstance = null;
    private static final String TAG = TsManager.class.getName();
    private int mPacketLength = -1;
    private PacketManager mPacketManager;
    private ProgramAssociationSectionManager mPasManager;
    private ProgramMapSectionManager mPmsManager;
    private ServiceDescriptionSectionManager mSdsManager;
    private OnParseListener mCallback;

    private final LoggerManager mLogger;

    public static TsManager getInstance() {
        if (sInstance == null) {
            synchronized (TsManager.class) {
                if (sInstance == null) {
                    sInstance = new TsManager();
                }
            }
        }
        return sInstance;
    }

    private TsManager() {
        mLogger = LoggerManager.getInstance();

        // Publisher
        mPacketManager = PacketManager.getInstance();
        // Subscriber
        mPasManager = ProgramAssociationSectionManager.getInstance();
        mSdsManager = ServiceDescriptionSectionManager.getInstance();
        mPmsManager = ProgramMapSectionManager.getInstance();

        // 中断监听
        OnParseListener listener = new OnParseListener() {
            @Override
            public void onFinish(Section section, int pid) {
                if (mCallback != null) {
                    mCallback.onFinish(section, pid);
                }
                mLogger.debug(TAG, "[TsManager] onFinish pid: 0x" + toHexString(pid));
                mPacketManager.removeFilterPid(pid);
            }
        };
        mPasManager.setOnParseListener(listener);
        mSdsManager.setOnParseListener(listener);
        mPmsManager.setOnParseListener(listener);

        // Observable - Observer
        mPacketManager.addObserver(mPasManager);
        mPacketManager.addObserver(mSdsManager);
        mPacketManager.addObserver(mPmsManager);
    }

    public void filterTsByPid(String filePath, List<Integer> filterList, OnParseListener callback) {
        if (filePath.isEmpty() || filterList.isEmpty()) {
            mLogger.error(TAG, "[parseTsByFilterId] args IllegalArgument");
            return;
        }

        mPmsManager.clearFilterPid();
        mPmsManager.addFilterPid(filterList);
        mCallback = callback;

        mLogger.debug(TAG, "[TsManager] filterTsByPid start");
        mPacketLength = mPacketManager.matchPacketLength(filePath);
        mPacketManager.filterPacket(filePath, filterList);
    }
}
