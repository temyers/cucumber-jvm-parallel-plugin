package com.github.timm.cucumber.generate.filter;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.fest.assertions.Assertions.assertThat;

import com.github.timm.cucumber.generate.ScenarioAndLocation;

import gherkin.AstBuilder;
import gherkin.Parser;
import gherkin.TokenMatcher;
import gherkin.ast.Feature;
import gherkin.ast.ScenarioDefinition;

import org.junit.Test;

import java.io.FileReader;
import java.util.Collection;

public class TagFilterTest {

    @Test
    public void shouldFindAllScenariosWhenTagsAreNull() {
        final TagFilter tagFilter = new TagFilter(null);
        final Feature feature = parseFeature("src/test/resources/features/filterByTag.feature");

        final Collection<ScenarioAndLocation> matchingScenariosAndExamples =
                        tagFilter.matchingScenariosAndExamples(feature);

        assertThat(matchingScenariosAndExamples).hasSize(3);
    }

    @Test
    public void shouldFindScenariosWithAndedTags() {

        final TagFilter tagFilter = new TagFilter(asList("@tag1", "@tag2"));
        final Feature feature = parseFeature("src/test/resources/features/filterByTag.feature");

        final Collection<ScenarioAndLocation> matchingScenariosAndExamples =
                        tagFilter.matchingScenariosAndExamples(feature);

        assertThat(matchingScenariosAndExamples).hasSize(1);
    }

    @Test
    public void shouldExcludeScenariosWhenFilteringByNotTags() throws Exception {

        final TagFilter tagFilter = new TagFilter(asList("@tag1", "~@tag2"));
        final Feature feature = parseFeature("src/test/resources/features/filterByTag.feature");

        final Collection<ScenarioAndLocation> matchingScenariosAndExamples =
                        tagFilter.matchingScenariosAndExamples(feature);

        assertThat(matchingScenariosAndExamples).hasSize(1);

        ScenarioAndLocation firstMatch = matchingScenariosAndExamples.iterator().next();
        assertThat(firstMatch.getScenario().getName())
                        .isEqualTo("with one tag");

    }

    @Test
    public void shouldFindScenariosWhenFilteringByOrTags() throws Exception {

        final TagFilter tagFilter = new TagFilter(singletonList("@tag1,@tag2"));
        final Feature feature = parseFeature("src/test/resources/features/filterByTag.feature");

        final Collection<ScenarioAndLocation> matchingScenariosAndExamples =
                        tagFilter.matchingScenariosAndExamples(feature);

        assertThat(matchingScenariosAndExamples).hasSize(3);
    }

    @Test
    public void shouldIncludeExamplesWhenFilteringByTagsAtExampleLevel() throws Exception {

        final TagFilter tagFilter = new TagFilter(singletonList("@english"));
        final Feature feature =
                        parseFeature("src/test/resources/features/multiple-example.feature");

        final Collection<ScenarioAndLocation> matchingScenariosAndExamples =
                        tagFilter.matchingScenariosAndExamples(feature);

        assertThat(matchingScenariosAndExamples).hasSize(3);

        ScenarioAndLocation firstMatch = matchingScenariosAndExamples.iterator().next();
        assertThat(firstMatch.getLocation().getLine()).isEqualTo(12);
    }

    @Test
    public void shouldIncludeScenariosWhenFilteringByTagsAtFeatureLevel() throws Exception {

        final TagFilter tagFilter = new TagFilter(singletonList("@featureTag"));
        final Feature feature = parseFeature("src/test/resources/features/filterByTag.feature");

        final Collection<ScenarioAndLocation> matchingScenariosAndExamples =
                        tagFilter.matchingScenariosAndExamples(feature);

        assertThat(matchingScenariosAndExamples).hasSize(3);

    }

    @Test
    public void shouldIncludeScenariosWhenFilteringByTagsAtScenarioLevel() throws Exception {

        final TagFilter tagFilter = new TagFilter(singletonList("@tag1"));
        final Feature feature = parseFeature("src/test/resources/features/filterByTag.feature");

        final Collection<ScenarioAndLocation> matchingScenariosAndExamples =
                        tagFilter.matchingScenariosAndExamples(feature);

        assertThat(matchingScenariosAndExamples).hasSize(2);

    }

    @Test
    public void shouldIncludeScenariosWhenFilteringByTagsAtScenarioOutlineLevel() throws Exception {

        final TagFilter tagFilter = new TagFilter(singletonList("@outlineTag"));
        final Feature feature =
                        parseFeature("src/test/resources/features/multiple-example.feature");

        final Collection<ScenarioAndLocation> matchingScenariosAndExamples =
                        tagFilter.matchingScenariosAndExamples(feature);

        assertThat(matchingScenariosAndExamples).hasSize(6);

    }

    private Feature parseFeature(final String file) {
        try {
            final Parser<Feature> parser = new Parser<Feature>(new AstBuilder());
            return parser.parse(new FileReader(file), new TokenMatcher());
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

}
