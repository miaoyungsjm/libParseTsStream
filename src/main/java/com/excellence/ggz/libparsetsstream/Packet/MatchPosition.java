package com.excellence.ggz.libparsetsstream.Packet;

/**
 * @author ggz
 * @date 2021/3/8
 */
public class MatchPosition {
    private int startPosition;
    private int intervalPosition;
    private int accumulator;

    public MatchPosition(int startPosition, int intervalPosition, int accumulator) {
        this.startPosition = startPosition;
        this.intervalPosition = intervalPosition;
        this.accumulator = accumulator;
    }

    public int getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }

    public int getIntervalPosition() {
        return intervalPosition;
    }

    public void setIntervalPosition(int intervalPosition) {
        this.intervalPosition = intervalPosition;
    }

    public int getAccumulator() {
        return accumulator;
    }

    public void setAccumulator(int accumulator) {
        this.accumulator = accumulator;
    }
}
