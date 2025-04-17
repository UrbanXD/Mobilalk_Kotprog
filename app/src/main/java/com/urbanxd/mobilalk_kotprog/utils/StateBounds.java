package com.urbanxd.mobilalk_kotprog.utils;

public class StateBounds {
    public long minBound = 0;
    public Long maxBound = null;

    public StateBounds() { }

    public StateBounds(long minBound, Long maxBound) {
        this.minBound = minBound;
        this.maxBound = maxBound;
    }

    public void setMinBound(long minBound) {
        this.minBound = minBound;
    }

    public void setMaxBound(Long maxBound) {
        this.maxBound = maxBound;
    }
}