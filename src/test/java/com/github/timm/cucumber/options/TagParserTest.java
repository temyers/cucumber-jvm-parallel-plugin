package com.github.timm.cucumber.options;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.List;

public class TagParserTest {

    @Test
    public void shouldParseNoTagsAsEmptyString() {
        final List<String> emptyList = asList();
        final String tags = TagParser.parseTags(emptyList);
        assertTrue(tags.isEmpty());
    }

    @Test
    public void shouldParseTagsWhenMultipleWhitespaceBetweenOptionArgs() {
        final String tags = TagParser.parseTags(asList("@tag1,@tag2", "@foo"));
        assertEquals("\"@tag1,@tag2\",\"@foo\"", tags);
    }

    @Test
    public void shouldParseTagsWhenMultipleWhitespaceBetweenTags() {
        final String tags = TagParser.parseTags(asList("@tag3,@tag4"));
        assertEquals("\"@tag3,@tag4\"", tags);
    }
}
