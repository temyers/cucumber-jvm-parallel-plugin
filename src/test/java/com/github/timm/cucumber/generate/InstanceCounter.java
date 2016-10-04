package com.github.timm.cucumber.generate;

import com.github.timm.cucumber.generate.name.Counter;

public class InstanceCounter implements Counter {

    private int counter = 1;

    public int next() {
        return counter++;
    }

}