package com.excellence.ggz.libparsetsstream;

import static com.excellence.ggz.libparsetsstream.Section.ProgramAssociationSectionManager.PAT_PID;
import static com.excellence.ggz.libparsetsstream.Section.ServiceDescriptionSectionManager.SDT_PID;
import static java.lang.Integer.toHexString;

import com.excellence.ggz.libparsetsstream.Interface.ParesTsStream;
import com.excellence.ggz.libparsetsstream.Logger.LoggerManager;
import com.excellence.ggz.libparsetsstream.Packet.PacketManager;
import com.excellence.ggz.libparsetsstream.Section.AbstractSectionManager;
import com.excellence.ggz.libparsetsstream.Section.ProgramAssociationSectionManager;
import com.excellence.ggz.libparsetsstream.Section.ProgramMapSectionManager;
import com.excellence.ggz.libparsetsstream.Section.ServiceDescriptionSectionManager;
import com.excellence.ggz.libparsetsstream.Section.entity.Program;
import com.excellence.ggz.libparsetsstream.Section.entity.ProgramAssociationSection;
import com.excellence.ggz.libparsetsstream.Section.entity.ProgramMapSection;
import com.excellence.ggz.libparsetsstream.Section.entity.Section;
import com.excellence.ggz.libparsetsstream.Section.entity.Service;
import com.excellence.ggz.libparsetsstream.Section.entity.ServiceDescriptionSection;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ggz
 * @date 2021/3/30
 */
public class TsManager implements ParesTsStream {
    private static volatile TsManager sInstance = null;
    private static final String TAG = TsManager.class.getName();
    private int mPacketLength = -1;
    private ProgramAssociationSection mPat;
    private ServiceDescriptionSection mSdt;
    private final List<ProgramMapSection> mPmtList = new ArrayList<>();
    private List<Program> mProgramList = new ArrayList<>();
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
    }

    private void initCallBack() {
        mPasManager.setOnParseListener(new AbstractSectionManager.OnParseListener() {
            @Override
            public void onFinish(Section section) {
                mPat = (ProgramAssociationSection) section;
                mLogger.debug(TAG, "[PAS] onFinish\n" + mPat.toString());
                mPacketManager.removeFilterPid(PAT_PID);
                mPacketManager.deleteObserver(mPasManager);

                mPmtList.clear();
                for (Program program : mPat.getProgramList()) {
                    int programNumber = program.getProgramNumber();
                    int pmtPid = program.getProgramMapPid();
                    if (programNumber > 0) {
                        mPacketManager.addFilterPid(pmtPid);
                        mPmsManager.addFilterPid(pmtPid);
                        mLogger.debug(TAG, "[PAS] onFinish add filter pid: 0x" + toHexString(pmtPid));
                    }
                }
            }
        });

        mSdsManager.setOnParseListener(new AbstractSectionManager.OnParseListener() {
            @Override
            public void onFinish(Section section) {
                mSdt = (ServiceDescriptionSection) section;
                mLogger.debug(TAG, "[SDS] onFinish\n" + mSdt.toString());
                mPacketManager.removeFilterPid(SDT_PID);
                mPacketManager.deleteObserver(mSdsManager);
            }
        });

        mPmsManager.setOnParseListener(new AbstractSectionManager.OnParseListener() {
            @Override
            public void onFinish(Section section) {
                ProgramMapSection programMapSection = (ProgramMapSection) section;
                mLogger.debug(TAG, programMapSection.toString());
                mPmtList.add(programMapSection);

                int pmtPid = programMapSection.getPid();
                mLogger.debug(TAG, "[PMS] onFinish pid: 0x" + toHexString(pmtPid));
                mPacketManager.removeFilterPid(pmtPid);
                mPmsManager.removeFilterPid(pmtPid);
                if (mPmsManager.getFilterPidList().size() == 0) {
                    mPacketManager.deleteObserver(mPmsManager);
                }
            }
        });
    }

    private void formatProgramList() {
        if (mPat != null && mSdt != null) {
            mProgramList = mPat.getProgramList();
            List<Service> serviceList = mSdt.getServiceList();
            for (Program program : mProgramList) {
                int serviceId = program.getProgramNumber();
                for (Service service : serviceList) {
                    if (service.getServiceId() == serviceId) {
                        String name = new String(service.getServiceDescriptor().getServiceName());
                        program.setProgramName(name);
                        break;
                    }
                }
            }
        }
    }

    private List<Program> parseTsFile(String filePath) {
        // Publisher
        mPacketManager = PacketManager.getInstance();
        // Subscriber
        mPasManager = ProgramAssociationSectionManager.getInstance();
        mSdsManager = ServiceDescriptionSectionManager.getInstance();
        mPmsManager = ProgramMapSectionManager.getInstance();

        // pas、sds、pms 数据处理完后的工作
        initCallBack();

        // add Observer
        mPacketManager.addObserver(mPasManager);
        mPacketManager.addObserver(mSdsManager);
        mPacketManager.addObserver(mPmsManager);

        List<Integer> filterList = new ArrayList<>();
        filterList.add(PAT_PID);
        filterList.add(SDT_PID);
        // todo:耗时操作
        mPacketLength = mPacketManager.matchPacketLength(filePath);
        mPacketManager.filterPacket(filePath, filterList);

        formatProgramList();

        return mProgramList;
    }

    @Override
    public String parseTsStreamByFile(String filePath) {
        List<Program> programList = parseTsFile(filePath);

        Gson gson = new Gson();
        String jsonStr = "";
        if (!programList.isEmpty()) {
            jsonStr = gson.toJson(programList);
        }
        return jsonStr;
    }
}
