package com.github.timm.cucumber.generate.name;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class ClassNamingSchemeFactoryTest {

    @Parameter(0)
    public String namingScheme;
    @Parameter(1)
    public String pattern;
    @Parameter(2)
    public String expectedResult;
    @Parameter(3)
    public Integer nextCount;

    Counter mockCounter = mock(Counter.class);
    ClassNamingSchemeFactory factory = new ClassNamingSchemeFactory(mockCounter);

    /**
     * Create params.
     */
    @Parameters
    public static Collection<Object[]> params() {
        final Object[][] params = {
            {"simple", "", "Parallel01IT", 1},
            {"simple", "", "Parallel02IT", 2},
            {"feature-title", "", "Feature101IT", 1},
            {"feature-title", "", "Feature102IT", 2},
            {"pattern", "{f}", "Feature1", 2},
            {"pattern", "{c}", "02", 2},
            {"pattern", "{f}_{c}IT", "Feature1_02IT", 2},};

        return Arrays.asList(params);
    }

    @Test
    public void createWithValidParams() throws Exception {


        final ClassNamingScheme create = factory.create(namingScheme, pattern);

        given(mockCounter.next()).willReturn(nextCount);
        final String actual = create.generate("feature1.feature");
        assertThat(actual).isEqualTo(expectedResult);
    }

}
