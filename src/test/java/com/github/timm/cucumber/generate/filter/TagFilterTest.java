package com.github.timm.cucumber.generate.filter;

import static org.fest.assertions.Assertions.assertThat;

import gherkin.AstBuilder;
import gherkin.Parser;
import gherkin.TokenMatcher;
import gherkin.ast.Feature;
import gherkin.ast.Node;
import gherkin.ast.ScenarioDefinition;
import org.junit.Test;

import java.io.FileReader;
import java.util.Collection;

public class TagFilterTest {

    @Test
    public void shouldFindAllScenariosWhenTagsAreNull() {
        final TagFilter tagFilter = new TagFilter(null);
        final Feature feature = parseFeature("src/test/resources/features/filterByTag.feature");

        final Collection<Node> matchingScenariosAndExamples =
                        tagFilter.matchingScenariosAndExamples(feature);

        assertThat(matchingScenariosAndExamples).hasSize(3);
    }

    @Test
    public void shouldFindScenariosWithAndedTags() {

        final TagFilter tagFilter = new TagFilter("\"@tag1\",\"@tag2\"");
        final Feature feature = parseFeature("src/test/resources/features/filterByTag.feature");

        final Collection<Node> matchingScenariosAndExamples =
                        tagFilter.matchingScenariosAndExamples(feature);

        assertThat(matchingScenariosAndExamples).hasSize(1);
    }

    @Test
    public void shouldExcludeScenariosWhenFilteringByNotTags() throws Exception {

        final TagFilter tagFilter = new TagFilter("\"@tag1\",\"~@tag2\"");
        final Feature feature = parseFeature("src/test/resources/features/filterByTag.feature");

        final Collection<Node> matchingScenariosAndExamples =
                        tagFilter.matchingScenariosAndExamples(feature);

        assertThat(matchingScenariosAndExamples).hasSize(1);
        assertThat(((ScenarioDefinition) (matchingScenariosAndExamples.iterator().next()))
                        .getName()).isEqualTo("with one tag");

    }

    @Test
    public void shouldFindScenariosWhenFilteringByOrTags() throws Exception {

        final TagFilter tagFilter = new TagFilter("\"@tag1,@tag2\"");
        final Feature feature = parseFeature("src/test/resources/features/filterByTag.feature");

        final Collection<Node> matchingScenariosAndExamples =
                        tagFilter.matchingScenariosAndExamples(feature);

        assertThat(matchingScenariosAndExamples).hasSize(3);
    }

    @Test
    public void shouldIncludeExamplesWhenFilteringByTagsAtExampleLevel() throws Exception {

        final TagFilter tagFilter = new TagFilter("\"@english\"");
        final Feature feature =
                        parseFeature("src/test/resources/features/multiple-example.feature");

        final Collection<Node> matchingScenariosAndExamples =
                        tagFilter.matchingScenariosAndExamples(feature);

        assertThat(matchingScenariosAndExamples).hasSize(3);

        assertThat(matchingScenariosAndExamples.iterator().next().getLocation().getLine())
                        .isEqualTo(12);
    }

    @Test
    public void shouldIncludeScenariosWhenFilteringByTagsAtFeatureLevel() throws Exception {

        final TagFilter tagFilter = new TagFilter("\"@featureTag\"");
        final Feature feature = parseFeature("src/test/resources/features/filterByTag.feature");

        final Collection<Node> matchingScenariosAndExamples =
                        tagFilter.matchingScenariosAndExamples(feature);

        assertThat(matchingScenariosAndExamples).hasSize(3);

    }

    @Test
    public void shouldIncludeScenariosWhenFilteringByTagsAtScenarioLevel() throws Exception {

        final TagFilter tagFilter = new TagFilter("\"@tag1\"");
        final Feature feature = parseFeature("src/test/resources/features/filterByTag.feature");

        final Collection<Node> matchingScenariosAndExamples =
                        tagFilter.matchingScenariosAndExamples(feature);

        assertThat(matchingScenariosAndExamples).hasSize(2);

    }

    @Test
    public void shouldIncludeScenariosWhenFilteringByTagsAtScenarioOutlineLevel() throws Exception {

        final TagFilter tagFilter = new TagFilter("\"@outlineTag\"");
        final Feature feature =
                        parseFeature("src/test/resources/features/multiple-example.feature");

        final Collection<Node> matchingScenariosAndExamples =
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
