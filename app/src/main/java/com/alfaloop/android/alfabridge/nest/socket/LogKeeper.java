package com.alfaloop.android.alfabridge.nest.socket;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class LogKeeper {

    private List<LogEntry> entries;

    public LogKeeper()
    {
        this.entries = new ArrayList<LogEntry>();
    }

    public List<LogEntry> getLogs()
    {
        return entries;
    }

    public void addLog(String log)
    {
        this.entries.add(new LogEntry(log, Calendar.getInstance().getTime().getTime()));
    }

    public void clear()
    {
        this.entries.clear();
    }

}
