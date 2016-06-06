package com.github.timm.cucumber.generate.name;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;


public class FeatureFileClassNamingSchemeTest {

    ClassNamingScheme classNameGenerator = new FeatureFileClassNamingScheme();

    @Test public void shouldGenerateExpectedTestClassNames() throws Exception {

        //using LinkedHasMap, as the order of insertion is important
        final Map<File, String> inputOutput = new LinkedHashMap<File, String>();

        inputOutput.put(new File("my-domain_subSetScenarios.feature"), "MyDomainSubsetscenarios");
        inputOutput.put(new File("my-PERSONNALdomain subSetPersonnalScenarios.feature"),
            "MyPersonnaldomainsubsetpersonnalscenarios");
        inputOutput.put(new File("Avýplňový.feature"), "Avýplňový");
        inputOutput.put(new File("123.feature"), "_123");

        for (final Map.Entry<File, String> example : inputOutput.entrySet()) {

            assertThat(classNameGenerator.generate(example.getKey().getName()),
                equalTo(example.getValue()));

        }
    }
}
