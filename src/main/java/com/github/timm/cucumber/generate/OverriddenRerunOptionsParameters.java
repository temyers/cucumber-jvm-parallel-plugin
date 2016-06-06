package com.github.timm.cucumber.generate;

/**
 * Created by sugad.mankar.
 */
public class OverriddenRerunOptionsParameters {
    private int retryCount;

    public OverriddenRerunOptionsParameters setRetryCount(int retryCount) {
        this.retryCount = retryCount;
        return this;
    }

    public int getRetryCount() {
        return retryCount;
    }

}
