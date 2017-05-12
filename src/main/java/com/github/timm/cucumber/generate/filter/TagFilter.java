package com.github.timm.cucumber.generate.filter;

import com.github.timm.cucumber.generate.ScenarioAndLocation;

import gherkin.ast.Examples;
import gherkin.ast.Feature;
import gherkin.ast.ScenarioDefinition;
import gherkin.ast.ScenarioOutline;
import gherkin.ast.TableRow;
import gherkin.ast.Tag;

import java.util.ArrayList;
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
    public TagFilter(final List<String> tags) {

        final List<String> nullsafeTags = tags == null ? new ArrayList<String>() : tags;
        tagGroupsAnded = splitTagQuery(nullsafeTags);
    }

    private static List<List<String>> splitTagQuery(final List<String> tagQuery) {
        final List<List<String>> allTags = new ArrayList<List<String>>();
        for (String tags : tagQuery) {
            final List<String> queryPart = new ArrayList<String>();
            for (String part : tags.split(",")) {
                queryPart.add(part.trim());
            }
            allTags.add(queryPart);
        }

        return allTags;
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
    public Collection<ScenarioAndLocation> matchingScenariosAndExamples(
                    final Feature feature) {

        final List<ScenarioDefinition> allScenarios = feature.getScenarioDefinitions();

        final List<ScenarioAndLocation> matchingScenariosAndExamples =
                        new LinkedList<ScenarioAndLocation>();

        for (final ScenarioDefinition scenario : allScenarios) {
            final Set<Tag> allTagsForScenario = new HashSet<Tag>(scenario.getTags());
            allTagsForScenario.addAll(feature.getTags());
            if (scenario instanceof ScenarioOutline) {
                matchingScenariosAndExamples.addAll(
                                matchingExamples((ScenarioOutline) scenario, allTagsForScenario));
            } else {
                if (matches(allTagsForScenario)) {
                    matchingScenariosAndExamples
                                    .add(new ScenarioAndLocation(scenario, scenario.getLocation()));
                }

            }
        }
        return matchingScenariosAndExamples;

    }

    private Collection<ScenarioAndLocation> matchingExamples(final ScenarioOutline scenario,
                                                             final Set<Tag> allTagsForScenario) {

        final Collection<ScenarioAndLocation> matchingRows =
                        new LinkedList<ScenarioAndLocation>();

        for (final Examples example : scenario.getExamples()) {
            final Collection<Tag> allTagsForExample = new HashSet<Tag>(allTagsForScenario);
            allTagsForExample.addAll(example.getTags());
            if (matches(allTagsForExample)) {
                for (TableRow row : example.getTableBody()) {
                    matchingRows.add(new ScenarioAndLocation(scenario, row.getLocation()));
                }
            }
        }

        return matchingRows;
    }
}
