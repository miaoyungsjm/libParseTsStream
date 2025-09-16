package com.excellence.ggz.libparsetsstream.Packet;

import static java.lang.Integer.toHexString;

import com.excellence.ggz.libparsetsstream.Logger.LoggerManager;
import com.excellence.ggz.libparsetsstream.Section.CompletionSignal;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;

/**
 * @author ggz
 * @date 2021/3/8
 */
public class PacketManager extends Observable {
    private static final String TAG = PacketManager.class.getName();
    private static final int PACKET_HEADER_SYNC_BYTE = 0x47;
    private static final int MATCH_TIMES = 10;
    private static final int BUFF_SIZE = 204 * 11;
    private static final int HASH_MAP_CAPACITY = 300;
    public static final int PACKET_LENGTH_188 = 188;
    public static final int PACKET_LENGTH_204 = 204;
    private static volatile PacketManager sInstance = null;

    private final LoggerManager mLogger;
    private int mPacketStartPosition = -1;
    private int mPacketLength = -1;

    public static PacketManager getInstance() {
        if (sInstance == null) {
            synchronized (PacketManager.class) {
                if (sInstance == null) {
                    sInstance = new PacketManager();
                }
            }
        }
        return sInstance;
    }

    private PacketManager() {
        mLogger = LoggerManager.getInstance();
    }

    private boolean matchMethod(int fileIndex, int matchPacketLen, HashMap<Integer, MatchPosition> hashMap) {
        // 当前所在区间值
        int curIntervalPosition = fileIndex / matchPacketLen;
        // 当前区间相对位置
        int curRelativePosition = fileIndex % matchPacketLen;
        // 查表
        MatchPosition matchPos = hashMap.get(curRelativePosition);
        if (matchPos != null) {
            int startPosition = matchPos.getStartPosition();
            int intervalPosition = matchPos.getIntervalPosition();
            int accumulator = matchPos.getAccumulator();

            if (accumulator == MATCH_TIMES) {
                mPacketStartPosition = startPosition;
                mPacketLength = matchPacketLen;
                return true;
            }
            // 判断所在区间是否相邻
            if (curIntervalPosition - intervalPosition == 1) {
                // 相邻，accumulator 进行累加
                matchPos.setIntervalPosition(curIntervalPosition);
                matchPos.setAccumulator(++accumulator);
            } else {
                // 不相邻，重新记录开始位置，accumulator 重新累加
                matchPos.setStartPosition(fileIndex);
                matchPos.setIntervalPosition(curIntervalPosition);
                matchPos.setAccumulator(1);
            }
        } else {
            matchPos = new MatchPosition(fileIndex, curIntervalPosition, 1);
        }
        hashMap.put(curRelativePosition, matchPos);
//        mLogger.debug(TAG, "[matchMethod] " + matchPacketLen + " fileIndex: " + fileIndex +
//                        ", relativePosition: " + curRelativePosition +
//                        ", startPosition: " + matchPos.getStartPosition() +
//                        ", intervalPosition: " + matchPos.getIntervalPosition() +
//                        ", accumulator: " + matchPos.getAccumulator());
        return false;
    }

    public int matchPacketLength(String filePath) {
        mPacketStartPosition = -1;
        mPacketLength = -1;

        try {
            FileInputStream fis = new FileInputStream(filePath);
            BufferedInputStream bis = new BufferedInputStream(fis);
            int fileIndex = 0;
            byte[] buff = new byte[BUFF_SIZE];
            boolean isFinish = false;

            HashMap<Integer, MatchPosition> hashMap188 = new HashMap<>(HASH_MAP_CAPACITY);
            HashMap<Integer, MatchPosition> hashMap204 = new HashMap<>(HASH_MAP_CAPACITY);

            while (!isFinish) {
                // read one buff
                int err = bis.read(buff);
                if (err == -1) {
                    break;
                }
                // match packet length
                for (byte b : buff) {
                    if (b == PACKET_HEADER_SYNC_BYTE) {
                        // match 188
                        if (matchMethod(fileIndex, PACKET_LENGTH_188, hashMap188)) {
                            isFinish = true;
                            break;
                        }
                        // match 204
                        if (matchMethod(fileIndex, PACKET_LENGTH_204, hashMap204)) {
                            isFinish = true;
                            break;
                        }
                    }
                    fileIndex++;
                }
            }
            bis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mLogger.debug(TAG, "[matchPacketLength] packetStartPosition: " + mPacketStartPosition + "\n" +
                "[matchPacketLength] packetLength: " + mPacketLength);
        return mPacketLength;
    }

    public void filterPacket(String filePath, List<Integer> filterList) {
        CompletionSignal completionSignal = CompletionSignal.getInstance();
        completionSignal.addFilterList(filterList);

        if (mPacketLength == -1 || mPacketStartPosition == -1) {
            mLogger.error(TAG, "[filterPacketByPid] packetLength packetStartPosition IllegalArgument");
            return;
        }

        mLogger.debug(TAG, "[filterPacket] file open\n");
        try {
            int packetLength = mPacketLength;
            int packetStartPosition = mPacketStartPosition;
            FileInputStream fis = new FileInputStream(filePath);
            BufferedInputStream bis = new BufferedInputStream(fis);
            long pos = bis.skip(packetStartPosition);
            if (pos != packetStartPosition) {
                mLogger.error(TAG, "[filterPacketByPid] failed to skip packetStartPosition: " + packetStartPosition);
                return;
            }

            byte[] buff = new byte[packetLength * 50];
            int len;
            while ((len = bis.read(buff)) != -1) {
                for (int i = 0; i < len / packetLength; i++) {
                    if (completionSignal.isCompleted()) {
                        break;
                    }

                    byte[] onePacket = new byte[packetLength];
                    System.arraycopy(buff, packetLength * i, onePacket, 0, packetLength);
                    if (onePacket[0] == PACKET_HEADER_SYNC_BYTE) {
                        Packet packet = Packet.newInstance(onePacket);
                        if (packet.getTransportErrorIndicator() == 1) {
                            mLogger.debug(TAG, "[filterPacketByPid] error: transport_error_indicator == 1");
                            continue;
                        }

                        for (int j = 0; j < filterList.size(); j++) {
                            if (packet.getPid() == filterList.get(j)) {

                                // Publisher 发布
                                mLogger.debug(TAG, "[filterPacket] Publisher submit packetPid: 0x" + toHexString(packet.getPid()));
                                setChanged();
                                notifyObservers(packet);

                            }
                        }
                    } else {
                        mLogger.error(TAG, "[filterPacketByPid] error: stream is unstable, need to get new start position");
                    }
                }
                if (completionSignal.isCompleted()) {
                    break;
                }
            }
            bis.close();
            mLogger.debug(TAG, "[filterPacket] file close\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getPacketStartPosition() {
        return mPacketStartPosition;
    }

    public int getPacketLength() {
        return mPacketLength;
    }
}
