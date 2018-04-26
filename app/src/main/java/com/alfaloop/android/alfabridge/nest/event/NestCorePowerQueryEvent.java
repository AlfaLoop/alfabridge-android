package com.alfaloop.android.alfabridge.nest.event;

/**
 * Created by Chris on 2017/9/2.
 */

public class NestCorePowerQueryEvent {
    private int source;
    private int percent;

    public NestCorePowerQueryEvent(int source, int percent) {
        this.source = source;
        this.percent = percent;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    @Override
    public String toString() {
        return "NestCorePowerQueryEvent{" +
                "source=" + source +
                ", percent=" + percent +
                '}';
    }
}
