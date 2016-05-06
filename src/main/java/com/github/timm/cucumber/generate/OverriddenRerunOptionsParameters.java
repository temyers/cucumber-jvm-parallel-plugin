package com.github.temyers.generate;

/**
 * Created by sugad.mankar on 5/5/2016.
 */
public class OverriddenRerunOptionsParameters {
    private int retryCount;

    public OverriddenRerunOptionsParameters setRetryCount(int  retryCount) {
        this.retryCount = retryCount;
        return this;
    }

    public int getRetryCount() {
        return retryCount;
    }

}
