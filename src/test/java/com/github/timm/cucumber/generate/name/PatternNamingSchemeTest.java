package com.github.timm.cucumber.generate.name;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;


@RunWith(Parameterized.class)
public class PatternNamingSchemeTest {

    @Parameter(0)
    public String pattern;
    @Parameter(1)
    public String output;

    Counter mockCounter = mock(Counter.class);
    ClassNamingScheme mockFileNamingScheme = mock(ClassNamingScheme.class);

    /**
     * Create params.
     */
    @Parameters
    public static Collection<Object[]> params() {
        final Object[][] params = {

            {"{f}", "FeatureFile"},
            {"{c}", "01"},
            {"{f}{c}", "FeatureFile01"},
            {"Foo", "Foo"},

            // No validation is performed
            {"", ""},};

        return Arrays.asList(params);
    }

    @Before
    public void setupMocks() {
        given(mockCounter.next()).willReturn(1);
        given(mockFileNamingScheme.generate(any(String.class))).willReturn("FeatureFile");
    }

    @Test
    public void generateFilename() {
        final PatternNamingScheme namingScheme =
                        new PatternNamingScheme(pattern, mockCounter, mockFileNamingScheme);
        final String actual = namingScheme.generate("feature_file");
        assertThat(actual).isEqualTo(output);
    }

    @Test(expected = NullPointerException.class)
    public void patternIsNull() {
        final PatternNamingScheme namingScheme =
                        new PatternNamingScheme(null, mockCounter, mockFileNamingScheme);
        namingScheme.generate("feature_file");

    }
}
