package com.github.timm.cucumber.generate.name;

/**
 * Created by mankar on 07/06/2016.
 */
public class TagNamingScheme implements ClassNamingScheme {

    private static int fileCounter = 1;

    public String generate(String tag) {
        String tagName = tag.replaceAll("@", "");
        return String.format("Tag_" + tagName + "_%02dIT.java", fileCounter++);
    }
}
