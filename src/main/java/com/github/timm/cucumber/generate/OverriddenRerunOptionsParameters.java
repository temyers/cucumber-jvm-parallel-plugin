package com.github.timm.cucumber.generate;

/**
 * Created by sugad.mankar.
 */
public class OverriddenRerunOptionsParameters {
	   private int retryCount;

	    public OverriddenRerunOptionsParameters setJUnitRetryCount(int  retryCount) {
	        this.retryCount = retryCount;
	        return this;
	    }

	    public int getJUnitRetryCount() {
	        return retryCount;
	    }

}
