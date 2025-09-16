package com.excellence.ggz.libparsetsstream;

import com.excellence.ggz.libparsetsstream.Interface.OnParseListener;
import com.excellence.ggz.libparsetsstream.Logger.LoggerManager;
import com.excellence.ggz.libparsetsstream.Packet.PacketManager;
import com.excellence.ggz.libparsetsstream.Section.ProgramAssociationSectionManager;
import com.excellence.ggz.libparsetsstream.Section.ProgramMapSectionManager;
import com.excellence.ggz.libparsetsstream.Section.ServiceDescriptionSectionManager;

import java.util.List;

/**
 * @author ggz
 * @date 2021/3/30
 */
public class TsManager {
    private static final String TAG = TsManager.class.getName();
    private static volatile TsManager sInstance = null;

    private int mPacketLength = -1;
    private PacketManager mPacketManager;
    private ProgramAssociationSectionManager mPasManager;
    private ProgramMapSectionManager mPmsManager;
    private ServiceDescriptionSectionManager mSdsManager;

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
    }

    public void filterTsByPid(String filePath, List<Integer> filterList, OnParseListener callback) {
        if (filePath.isEmpty() || filterList.isEmpty()) {
            mLogger.error(TAG, "[parseTsByFilterId] args IllegalArgument");
            return;
        }

        mPasManager.setOnParseListener(callback);
        mSdsManager.setOnParseListener(callback);
        mPmsManager.setOnParseListener(callback);

        // Observable - Observer
        mPacketManager.addObserver(mPasManager);
        mPacketManager.addObserver(mSdsManager);
        mPacketManager.addObserver(mPmsManager);

        mPacketLength = mPacketManager.matchPacketLength(filePath);
        mPacketManager.filterPacket(filePath, filterList);

        mPacketManager.deleteObserver(mPasManager);
        mPacketManager.deleteObserver(mSdsManager);
        mPacketManager.deleteObserver(mPmsManager);
        // 释放资源
        mPasManager.clearSection();
        mSdsManager.clearSection();
        mPmsManager.clearSection();
    }
}
