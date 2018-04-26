package com.alfaloop.android.alfabridge.nest.socket;

/**
 * Created by Chris on 2018/1/4.
 */

public class LogEntry {

    private String stringEntry;
    private long timestamp;

    public LogEntry(String stringEntry, long timestamp)
    {
        this.stringEntry = stringEntry;
        this.timestamp = timestamp;
    }

    public String getStringEntry()
    {
        return stringEntry;
    }

    public void setStringEntry(String stringEntry)
    {
        this.stringEntry = stringEntry;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

}
