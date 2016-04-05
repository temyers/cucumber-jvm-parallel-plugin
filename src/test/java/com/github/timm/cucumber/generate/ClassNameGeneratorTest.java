package com.github.timm.cucumber.generate;

import org.junit.Test;

import java.io.File;
import java.util.*;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;


public class ClassNameGeneratorTest {

    ClassNameGenerator classNameGenerator=new ClassNameGenerator();

    @Test
    public void shouldGenerateExpectedTestClassNames() throws Exception {

        //using LinkedHasMap, as the order of insertion is important
        Map<File,String> inputOutput=new LinkedHashMap <File, String>();

        inputOutput.put(new File("my-domain_subSetScenarios.feature"), "MyDomainSubsetscenarios01IT.java");
        inputOutput.put(new File("my-PERSONNALdomain subSetPersonnalScenarios.feature"), "MyPersonnaldomainsubsetpersonnalscenarios02IT.java");
        inputOutput.put(new File("Avýplňový.feature"), "Avýplňový03IT.java");
        inputOutput.put(new File("123.feature"), "_12304IT.java");

        int fileCounter=1;

        for(Map.Entry<File,String> example : inputOutput.entrySet()){

            assertThat(classNameGenerator.generateClassNameFromFeatureFileName(example.getKey().getName(), fileCounter),equalTo(example.getValue()));

            fileCounter++;
        }
    }
}