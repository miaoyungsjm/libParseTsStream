package com.excellence.ggz.libparsetsstream;

import com.excellence.ggz.libparsetsstream.Interface.ParesTsStream;
import com.excellence.ggz.libparsetsstream.Logger.LoggerManager;

/**
 * @author ggz
 */
public class TestProgram {
    private static final String INPUT_FILE1_PATH = "/Users/miaoyun/Documents/workspace/001.ts";
    private static final String INPUT_FILE2_PATH = "f:/bak/sx/tools/001.ts";
    private static final String TAG = TestProgram.class.getName();

    public static void main(String[] args) {
        ParesTsStream paresTsStream = TsManager.getInstance();
        String json = paresTsStream.parseTsStreamByFile(INPUT_FILE1_PATH);

        LoggerManager logger = LoggerManager.getInstance();
        logger.debug(TAG, json);
    }
}