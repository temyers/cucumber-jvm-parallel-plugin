package com.github.timm.cucumber.generate;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CucumberOptionsParserTest {
    private static final String CUCUMBER_OPTS_WITH_MULTI_WHITESPACE_BETWEEN_ARGS = "--format html --tags   @tag1,@tag2      --tags      @foo";
    private static final String CUCUMBER_OPTS_WITH_MULTI_WHITESPACE_BETWEEN_TAGS = "--tags @tag3,      @tag4 --format json";

    @Test
    public void shouldParseTagsWhenMultipleWhitespaceBetweenArgs() {
        CucumberOptionsParser parser = new CucumberOptionsParser(CUCUMBER_OPTS_WITH_MULTI_WHITESPACE_BETWEEN_ARGS);
        String tags = parser.parse(CucumberOptionsParser.TAG_DELIMITERS);
        assertEquals("\"@tag1,@tag2\",\"@foo\"", tags);
    }

    @Test
    public void shouldParseTagsWhenMultipleWhitespaceBetweenTags() {
        CucumberOptionsParser parser = new CucumberOptionsParser(CUCUMBER_OPTS_WITH_MULTI_WHITESPACE_BETWEEN_TAGS);
        String tags = parser.parse(CucumberOptionsParser.TAG_DELIMITERS);
        assertEquals("\"@tag3,@tag4\"", tags);
    }
}
