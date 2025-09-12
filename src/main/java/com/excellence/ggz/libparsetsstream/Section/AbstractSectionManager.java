package com.excellence.ggz.libparsetsstream.Section;

import static com.excellence.ggz.libparsetsstream.Packet.PacketManager.PACKET_LENGTH_204;

import com.excellence.ggz.libparsetsstream.Interface.OnParseListener;
import com.excellence.ggz.libparsetsstream.Logger.LoggerManager;
import com.excellence.ggz.libparsetsstream.Packet.Packet;
import com.excellence.ggz.libparsetsstream.Section.entity.Section;

import java.util.HashMap;


/**
 * @author ggz
 * @date 2021/3/22
 */
public abstract class AbstractSectionManager {
    private static final int AF_STATUS_PAYLOAD_ONLY = 0x01;
    private static final int SECTION_HEADER_LENGTH = 3;
    private static final int PAYLOAD_POINTER_FIELD = 1;
    private static final int CRC_16 = 16;
    private static final int CONTINUITY_COUNTER_MAXIMUM = 0xF;

    private HashMap<Integer, Section> mSectionMap = new HashMap<>();

    public OnParseListener mOnParseListener = null;

    public final LoggerManager mLogger = LoggerManager.getInstance();

    public void assembleSection(int inputTableId, Packet packet) {
        int packetLength = packet.getPacketLength();
        int pid = packet.getPid();
        int adaptationFieldControl = packet.getAdaptationFieldControl();
        int payloadUnitStartIndicator = packet.getPayloadUnitStartIndicator();
        int continuityCounter = packet.getContinuityCounter();
        byte[] payLoad = packet.getPayLoad();

        Section section = mSectionMap.get(pid);

        if (adaptationFieldControl == AF_STATUS_PAYLOAD_ONLY) {
            if (payloadUnitStartIndicator == 1) {
                // payload_unit_start_indicator == 1，packet carries the first byte of a PSI section
                int tableId = payLoad[PAYLOAD_POINTER_FIELD] & 0xFF;
                int sectionSyntaxIndicator = (payLoad[PAYLOAD_POINTER_FIELD + 1] >> 7) & 0x1;
                int sectionLength = ((payLoad[PAYLOAD_POINTER_FIELD + 1] & 0x3) << 8 |
                        payLoad[PAYLOAD_POINTER_FIELD + 2] & 0xFF) & 0x3FF;
                byte[] buff = new byte[sectionLength];

                if (tableId == inputTableId) {
                    // the maximum value of section length in one packet
                    int effectiveSectionLength;
                    int payloadLength = payLoad.length;
                    if (packetLength == PACKET_LENGTH_204) {
                        effectiveSectionLength = payloadLength - PAYLOAD_POINTER_FIELD
                                - SECTION_HEADER_LENGTH - CRC_16;
                    } else {
                        effectiveSectionLength = payloadLength - PAYLOAD_POINTER_FIELD
                                - SECTION_HEADER_LENGTH;
                    }

                    int remainLength;
                    int sectionStartPos = PAYLOAD_POINTER_FIELD + SECTION_HEADER_LENGTH;
                    if (sectionLength > effectiveSectionLength) {
                        // incomplete
                        System.arraycopy(payLoad, sectionStartPos, buff, 0, effectiveSectionLength);
                        remainLength = sectionLength - effectiveSectionLength;
                    } else {
                        // complete
                        System.arraycopy(payLoad, sectionStartPos, buff, 0, sectionLength);
                        remainLength = 0;
                    }

                    section = new Section(pid, tableId, sectionSyntaxIndicator, sectionLength, buff);
                    section.setRemainLength(remainLength);

                    mSectionMap.put(pid, section);
                }
            } else {
                if (section == null) {
                    return;
                }

                int sectionLength = section.getSectionLength();
                int remainLength = section.getRemainLength();
                byte[] sectionBuff = section.getSectionBuff();

                // the maximum value of section length in one packet
                int effectiveSectionLength;
                int payloadLength = payLoad.length;
                if (packetLength == PACKET_LENGTH_204) {
                    effectiveSectionLength = payloadLength - CRC_16;
                } else {
                    effectiveSectionLength = payloadLength;
                }

                int buffStartPos = sectionLength - remainLength;
                if (remainLength > effectiveSectionLength) {
                    // incomplete
                    System.arraycopy(payLoad, 0, sectionBuff, buffStartPos, effectiveSectionLength);
                    remainLength = remainLength - effectiveSectionLength;
                } else {
                    // complete
                    System.arraycopy(payLoad, 0, sectionBuff, buffStartPos, remainLength);
                    remainLength = 0;
                }

                section.setRemainLength(remainLength);
            }

            if (section != null) {
                mLogger.debug(AbstractSectionManager.class.getName(),
                        "[AbstractSection] assembleSection working...\n" + section.toString());
                if (section.getRemainLength() == 0) {
                    parseSection(section);
                }
            }
        } else {
            // todo: adaptation_field()
        }
    }

    public abstract void parseSection(Section section);

    public void setOnParseListener(OnParseListener listener) {
        mOnParseListener = listener;
    }
}
