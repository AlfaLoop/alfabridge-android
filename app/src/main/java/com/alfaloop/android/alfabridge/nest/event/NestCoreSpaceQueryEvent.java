package com.alfaloop.android.alfabridge.nest.event;

/**
 * Created by Chris on 2017/8/30.
 */

public class NestCoreSpaceQueryEvent {
    private int internalTotal;
    private int internalUsed;
    private int externalTotal;
    private int externalUsed;

    public NestCoreSpaceQueryEvent(int internalTotal, int internalUsed, int externalTotal, int externalUsed) {
        this.internalTotal = internalTotal;
        this.internalUsed = internalUsed;
        this.externalTotal = externalTotal;
        this.externalUsed = externalUsed;
    }

    public int getInternalTotal() {
        return internalTotal;
    }

    public void setInternalTotal(int internalTotal) {
        this.internalTotal = internalTotal;
    }

    public int getInternalUsed() {
        return internalUsed;
    }

    public void setInternalUsed(int internalUsed) {
        this.internalUsed = internalUsed;
    }

    public int getExternalTotal() {
        return externalTotal;
    }

    public void setExternalTotal(int externalTotal) {
        this.externalTotal = externalTotal;
    }

    public int getExternalUsed() {
        return externalUsed;
    }

    public void setExternalUsed(int externalUsed) {
        this.externalUsed = externalUsed;
    }

    @Override
    public String toString() {
        return "NestCoreSpaceQueryEvent{" +
                "internalTotal=" + internalTotal +
                ", internalUsed=" + internalUsed +
                ", externalTotal=" + externalTotal +
                ", externalUsed=" + externalUsed +
                '}';
    }
}
