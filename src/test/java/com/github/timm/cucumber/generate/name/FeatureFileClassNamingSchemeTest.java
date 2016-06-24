package com.github.timm.cucumber.generate.name;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;


@RunWith(Parameterized.class)
public class FeatureFileClassNamingSchemeTest {

    @Parameterized.Parameter(0)
    public String featureFileName;
    @Parameterized.Parameter(1)
    public String expectedClassName;

    ClassNamingScheme classNameGenerator = new FeatureFileClassNamingScheme();

    /**
     * Create params.
     */
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        Object[][] params = {
            {"my-domain_subSetScenarios.feature", "MyDomainSubsetscenarios"},
            {"my-PERSONNALdomain subSetPersonnalScenarios.feature", "MyPersonnaldomainsubsetpersonnalscenarios"},
            {"Avýplňový.feature", "Avýplňový"},
            {"123.feature", "_123"},
            {"some.test.feature", "SomeTest"},
        };

        return Arrays.asList(params);
    }

    @Test
    public void shouldGenerateExpectedTestClassNames() throws Exception {

        assertThat(classNameGenerator.generate(featureFileName),
            equalTo(expectedClassName));

    }
}
