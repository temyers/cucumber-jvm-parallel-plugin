package com.github.timm.cucumber.generate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class CucumberOptionsParserTest {
    private static final String CUCUMBER_OPTS_WITH_NO_TAGS = "--format json";
    private static final String CUCUMBER_OPTS_WITH_MULTI_WHITESPACE_BETWEEN_ARGS = "--format html --tags   @tag1,@tag2      --tags      @foo";
    private static final String CUCUMBER_OPTS_WITH_MULTI_WHITESPACE_BETWEEN_TAGS = "--tags @tag3,      @tag4 --format json";

    @Test
    public void shouldParseTagsAsNullWhenNoTagArgsExist() {
        CucumberOptionsParser parser = new CucumberOptionsParser(CUCUMBER_OPTS_WITH_NO_TAGS);
        String tags = parser.parseTags();
        assertNull(tags);
    }

    @Test
    public void shouldParseTagsWhenMultipleWhitespaceBetweenOptionArgs() {
        CucumberOptionsParser parser = new CucumberOptionsParser(CUCUMBER_OPTS_WITH_MULTI_WHITESPACE_BETWEEN_ARGS);
        String tags = parser.parseTags();
        assertEquals("\"@tag1,@tag2\",\"@foo\"", tags);
    }

    @Test
    public void shouldParseTagsWhenMultipleWhitespaceBetweenTags() {
        CucumberOptionsParser parser = new CucumberOptionsParser(CUCUMBER_OPTS_WITH_MULTI_WHITESPACE_BETWEEN_TAGS);
        String tags = parser.parseTags();
        assertEquals("\"@tag3,@tag4\"", tags);
    }
}
