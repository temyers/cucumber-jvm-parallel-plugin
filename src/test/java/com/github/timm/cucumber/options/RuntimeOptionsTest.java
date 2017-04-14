package com.github.timm.cucumber.options;

import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Copyright (c) 2008-2014 The Cucumber Organisation Adapted from Cucumbers RuntimeOptionsTest
 */
public class RuntimeOptionsTest {


    @Test
    public void assigns_filters_from_tags() {
        final RuntimeOptions options = new RuntimeOptions("--tags @keep_this somewhere_else:3");
        assertEquals(singletonList("@keep_this"), options.getFilters());
    }

    @Test
    public void strips_options() {
        final RuntimeOptions options = new RuntimeOptions("  --glue  somewhere   somewhere_else");
        assertEquals(1, options.getGlue().size());
        assertTrue(options.getGlue().contains("somewhere"));
    }

    @Test
    public void assigns_glue() {
        final RuntimeOptions options = new RuntimeOptions("--glue somewhere");
        assertEquals(singletonList("somewhere"), options.getGlue());
    }

    @Test
    public void parses_plugin() {
        final RuntimeOptions options = new RuntimeOptions(
                        asList("--plugin", "html:some/dir", "--glue", "somewhere"));
        assertEquals("html:some/dir", options.getPluginNames().get(0));
    }

    @Test
    public void parses_multiple_plugins() {
        final RuntimeOptions options = new RuntimeOptions(asList("--plugin", "html:some/dir",
                        "--plugin", "pretty", "--glue", "somewhere"));
        assertEquals(2, options.getPluginNames().size());
        assertTrue(options.getPluginNames().contains("pretty"));
    }

    @Test
    public void assigns_strict() {
        final RuntimeOptions options =
                        new RuntimeOptions(asList("--strict", "--glue", "somewhere"));
        assertTrue(options.isStrict());
    }

    @Test
    public void assigns_strict_short() {
        final RuntimeOptions options = new RuntimeOptions(asList("-s", "--glue", "somewhere"));
        assertTrue(options.isStrict());
    }

    @Test
    public void unknown_options_are_ignored() {
        try {
            new RuntimeOptions(asList("-concreteUnsupportedOption", "somewhere", "somewhere_else"));
        } catch (final Exception e) {
            fail();
        }
    }

    @Test
    public void shouldParseTagsWhenMultipleWhitespaceBetweenOptionArgs() {
        final RuntimeOptions parser =
                        new RuntimeOptions("--format html --tags   @tag1,@tag2      --tags      @foo");
        final List<String> tags = parser.getFilters();
        assertEquals(asList("@tag1,@tag2", "@foo"), tags);
    }


}
