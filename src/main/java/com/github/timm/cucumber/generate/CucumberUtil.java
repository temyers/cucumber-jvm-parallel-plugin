package com.github.timm.cucumber.generate;

import cucumber.runtime.TagPredicate;
import cucumber.runtime.io.FileResource;
import cucumber.runtime.model.CucumberFeature;
import cucumber.util.Encoding;
import gherkin.AstBuilder;
import gherkin.Parser;
import gherkin.TokenMatcher;
import gherkin.ast.GherkinDocument;
import gherkin.pickles.Compiler;
import gherkin.pickles.Pickle;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

class CucumberUtil {

    static CucumberFeature parseFeature(File file) {

        try {
            final Parser<GherkinDocument> parser = new Parser<GherkinDocument>(new AstBuilder());
            final String gherkin = Encoding.readFile(new FileResource(file, file));
            final GherkinDocument gherkinDocument = parser.parse(gherkin, new TokenMatcher());
            return new CucumberFeature(gherkinDocument, file.getPath(), gherkin);
        } catch (final IOException e) {
            // should never happen
            throw new IllegalStateException(e);
        }

    }

    static Collection<Pickle> filterSteps(CucumberFeature cucumberFeature, TagPredicate tagPredicate) {
        Collection<Pickle> matchingPickles = new ArrayList<Pickle>();
        for (Pickle pickle : new Compiler().compile(cucumberFeature.getGherkinFeature())) {
            if (tagPredicate.apply(pickle.getTags())) {
                matchingPickles.add(pickle);
            }
        }

        return matchingPickles;
    }

}
