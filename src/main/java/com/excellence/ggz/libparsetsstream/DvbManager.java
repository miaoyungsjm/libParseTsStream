package com.excellence.ggz.libparsetsstream;

import static com.excellence.ggz.libparsetsstream.Section.ProgramAssociationSectionManager.PAT_PID;
import static com.excellence.ggz.libparsetsstream.Section.ServiceDescriptionSectionManager.SDT_PID;
import static java.lang.Integer.toHexString;

import com.excellence.ggz.libparsetsstream.Interface.OnParseListener;
import com.excellence.ggz.libparsetsstream.Interface.ParesTsStream;
import com.excellence.ggz.libparsetsstream.Logger.LoggerManager;
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
 * @date 2025/9/12
 */
public class DvbManager implements ParesTsStream {
    private static final String TAG = DvbManager.class.getName();

    private final LoggerManager mLogger;
    private final TsManager mTsManager;
    private ProgramAssociationSection mPat;
    private ServiceDescriptionSection mSdt;
    private final List<ProgramMapSection> mPmtList = new ArrayList<>();

    public DvbManager() {
        mLogger = LoggerManager.getInstance();
        mTsManager = TsManager.getInstance();
    }

    private List<Program> formatProgramList(ProgramAssociationSection pas, ServiceDescriptionSection sds) {
        List<Program> programList = pas.getProgramList();
        List<Service> serviceList = sds.getServiceList();
        for (Program program : programList) {
            int serviceId = program.getProgramNumber();
            for (Service service : serviceList) {
                if (service.getServiceId() == serviceId) {
                    String name = new String(service.getServiceDescriptor().getServiceName());
                    program.setProgramName(name);
                    break;
                }
            }
        }
        return new ArrayList<>(programList);
    }

    @Override
    public String parseTsStreamByFile(String filePath) {
        List<Program> programList = new ArrayList<>();

        OnParseListener listener = new OnParseListener() {
            @Override
            public void onFinish(Section section, int pid) {
                if (pid == PAT_PID) {
                    mPat = (ProgramAssociationSection) section;
                    mLogger.debug(TAG, "[DVB] onFinish\n" + mPat.toString());
                } else if (pid == SDT_PID) {
                    mSdt = (ServiceDescriptionSection) section;
                    mLogger.debug(TAG, "[DVB] onFinish\n" + mSdt.toString());
                } else {
                    ProgramMapSection pms = (ProgramMapSection) section;
                    mLogger.debug(TAG, "[DVB] onFinish\n" + pms.toString());
//                    mLogger.debug(TAG, "[PMS] onFinish pid: 0x" + toHexString(pms.getPid()));
                    mPmtList.add(pms);
                }
            }
        };

        // TODO: 耗时操作,业务逻辑
        List<Integer> filterList = new ArrayList<>();
        filterList.add(PAT_PID);
        filterList.add(SDT_PID);
        mLogger.debug(TAG, "[DvbManager] add filter pid: 0x" + toHexString(PAT_PID));
        mLogger.debug(TAG, "[DvbManager] add filter pid: 0x" + toHexString(SDT_PID));
        mTsManager.filterTsByPid(filePath, filterList, listener);
        if (mPat != null && mSdt != null) {
            programList = formatProgramList(mPat, mSdt);
        }

        if (mPat != null) {
            mPmtList.clear();
            filterList.clear();
            for (Program program : mPat.getProgramList()) {
                int programNumber = program.getProgramNumber();
                int pmtPid = program.getProgramMapPid();
                if (programNumber > 0) {
                    filterList.add(pmtPid);
                    mLogger.debug(TAG, "[DvbManager] add filter pid: 0x" + toHexString(pmtPid));
                }
            }
            mTsManager.filterTsByPid(filePath, filterList, listener);
        }

        Gson gson = new Gson();
        String jsonStr;
        if (!programList.isEmpty()) {
            jsonStr = gson.toJson(programList);
        } else {
            jsonStr = "";
        }
        return jsonStr;
    }
}
