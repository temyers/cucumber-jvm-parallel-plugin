package com.github.timm.cucumber.options;

import static java.util.Arrays.asList;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class TagParserShouldSplitQuotedTagsTest {

    @Parameter(0)
    public String sourceTags;

    @Parameter(1)
    public List<List<String>> expectedTags;

    @Parameters
    public static Collection<Object[]> params() {

        // @formatter:off
        final Object[][] params = {

            // SINGLE tag
            {"\"@tag\"", asList(asList("@tag"))},
            // OR
            {"\"@tag1,@tag2\"", asList(asList("@tag1", "@tag2"))},
            // AND
            {"\"@tag1\",\"@tag2\"", asList(asList("@tag1"), asList("@tag2"))},
            // mixture AND and OR
            {
                "\"@tag1,@tag2\",\"~@notMe\"",
                asList(asList("@tag1", "@tag2"), asList("~@notMe"))},

            {"\"@feature1,@feature2\"", asList(asList("@feature1", "@feature2"))}

        };
        // @formatter:on
        return Arrays.asList(params);

    }

    @Test
    public void test() {
        Assert.assertThat(TagParser.splitQuotedTagsIntoParts(sourceTags),
                        Matchers.equalTo(expectedTags));
    }

}
