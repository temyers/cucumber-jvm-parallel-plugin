package com.github.timm.cucumber;

import com.github.timm.cucumber.generate.name.Counter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class counts modulo n, where n is an integer extracted from the configured naming pattern
 * using regex "{c:(\d*)}".
 */
public class ModuloCounter implements Counter {

    private final int module;
    private int counter = 0;

    /**
     * Generates a counter from the pattern string.
     * The pattern string is used to infer the module to count over.
     * @param patternString the configure naming pattern string
     */
    public ModuloCounter(String patternString) {
        this.module = getModulo(patternString);
    }

    public int next() {
        return this.counter++ % this.module;
    }

    private int getModulo(String extractFrom) {
        Matcher matcher = Pattern.compile(".*\\{c:(\\d*)}.*").matcher(extractFrom);
        return matcher.matches()
            ? Integer.decode(matcher.group(1))
            : 1;
    }
}
