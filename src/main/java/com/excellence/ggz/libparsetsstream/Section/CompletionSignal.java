package com.excellence.ggz.libparsetsstream.Section;

import static java.lang.Integer.toHexString;

import com.excellence.ggz.libparsetsstream.Logger.LoggerManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author miaoyun
 */
public class CompletionSignal {
    private static final String TAG = CompletionSignal.class.getName();
    private static volatile CompletionSignal sInstance = null;

    private final LoggerManager mLogger;
    private final List<Integer> mFilterList = new ArrayList<>();
    private final HashMap<Integer, Boolean> mStatusMap = new HashMap<Integer, Boolean>();
    private boolean isCompleted = false;

    public static CompletionSignal getInstance() {
        if (sInstance == null) {
            synchronized (CompletionSignal.class) {
                if (sInstance == null) {
                    sInstance = new CompletionSignal();
                }
            }
        }
        return sInstance;
    }

    private CompletionSignal() {
        mLogger = LoggerManager.getInstance();
    }

    public List<Integer> getFilterList() {
        return mFilterList;
    }

    public void addFilterList(List<Integer> list) {
        mFilterList.clear();
        mStatusMap.clear();
        isCompleted = false;

        StringBuilder builder = new StringBuilder();
        builder.append("size: ").append(list.size()).append("\n");
        for (Integer pid : list) {
            mFilterList.add(pid);
            mStatusMap.put(pid, false);
            builder.append("0x").append(toHexString(pid)).append("\n");
        }
        mLogger.debug(TAG, "[CompletionSignal] addFilterList " + builder);
    }

    public boolean checkStatusMap(Integer pid) {
        Boolean status = mStatusMap.get(pid);
        if (status == null) {
            status = false;
        }
        mLogger.debug(TAG, "[CompletionSignal] checkStatusMap: 0x" + toHexString(pid) +
                " " + status);
        return status;
    }

    public void refreshStatusMap(Integer pid) {
        mStatusMap.put(pid, true);

        StringBuilder builder = new StringBuilder();
        builder.append("size: ").append(mStatusMap.size()).append("\n");
        boolean result = true;
        for (Map.Entry<Integer, Boolean> entry : mStatusMap.entrySet()) {
            Integer key = entry.getKey();
            Boolean value = entry.getValue();
            builder.append("0x").append(toHexString(key)).append(" ").append(value).append("\n");
            if (!value) {
                result = false;
                break;
            }
        }
        mLogger.debug(TAG, "[CompletionSignal] refreshStatusMap " + builder.toString());
        isCompleted = result;
    }

    public boolean isCompleted() {
        return isCompleted;
    }
}
