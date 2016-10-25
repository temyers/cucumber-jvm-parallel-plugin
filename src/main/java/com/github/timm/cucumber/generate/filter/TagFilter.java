package com.github.timm.cucumber.generate.filter;

import com.github.timm.cucumber.options.TagParser;
import gherkin.ast.Examples;
import gherkin.ast.Feature;
import gherkin.ast.Node;
import gherkin.ast.ScenarioDefinition;
import gherkin.ast.ScenarioOutline;
import gherkin.ast.TableRow;
import gherkin.ast.Tag;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * The TagFilter is responsible for identifying matching Scenarios/Outline Examples that match the given set of tags.
 */
public class TagFilter {

    private final List<List<String>> tagGroupsAnded;

    /**
     * Constructor. If <code>tags</code> is null, no filtering shall be performed.
     *
     * @param tags The tags to filter by.
     */
    public TagFilter(final String tags) {

        final String nullsafeTags = tags == null ? "" : tags;
        tagGroupsAnded = TagParser.splitQuotedTagsIntoParts(nullsafeTags);
    }

    private boolean matches(final Collection<Tag> tags) {
        // Tag groups are and'd together
        for (final List<String> tagGroup : tagGroupsAnded) {
            // individual tags are or'd together
            if (!anyMatch(tags, tagGroup)) {
                return false;
            }
        }
        return true;
    }

    private boolean anyMatch(final Collection<Tag> tags, final List<String> expectedTags) {
        for (final String tag : expectedTags) {
            if (tag.startsWith("~")) {
                for (final Tag actualTag : tags) {

                    if (actualTag.getName().equals(tag.substring(1))) {
                        return false;
                    }
                }
                return true;
            } else {
                for (final Tag actualTag : tags) {

                    if (actualTag.getName().equals(tag)) {
                        return true;
                    }
                }

            }
        }
        return false;
    }

    /**
     * Find the scenarios that match the required tags in the feature.
     *
     * @param feature
     * @return
     */
    public Collection<Node> matchingScenariosAndExamples(final Feature feature) {

        final List<ScenarioDefinition> allScenarios = feature.getScenarioDefinitions();

        final List<Node> matchingScenariosAndExamples = new LinkedList<Node>();

        for (final ScenarioDefinition scenario : allScenarios) {
            final Set<Tag> allTagsForScenario = new HashSet<Tag>(scenario.getTags());
            allTagsForScenario.addAll(feature.getTags());
            if (scenario instanceof ScenarioOutline) {
                matchingScenariosAndExamples.addAll(
                                matchingExamples((ScenarioOutline) scenario, allTagsForScenario));
            } else {
                if (matches(allTagsForScenario)) {
                    matchingScenariosAndExamples.add(scenario);
                }

            }
        }
        return matchingScenariosAndExamples;

    }

    private Collection<TableRow> matchingExamples(final ScenarioOutline scenario,
                    final Set<Tag> allTagsForScenario) {

        final Collection<TableRow> matchingRows = new LinkedList<TableRow>();

        for (final Examples example : scenario.getExamples()) {
            final Collection<Tag> allTagsForExample = new HashSet<Tag>(allTagsForScenario);
            allTagsForExample.addAll(example.getTags());
            if (matches(allTagsForExample)) {
                matchingRows.addAll(example.getTableBody());
            }
        }

        return matchingRows;
    }
}
