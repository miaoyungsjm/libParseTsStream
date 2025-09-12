package com.excellence.ggz.libparsetsstream.Interface;

import com.excellence.ggz.libparsetsstream.Section.entity.Section;

/**
 * @author miaoyun
 * 用于数据回调
 */
public interface OnParseListener {
    void onFinish(Section section, int pid);
}
