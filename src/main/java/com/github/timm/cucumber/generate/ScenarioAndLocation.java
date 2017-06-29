package com.github.timm.cucumber.generate;

import gherkin.ast.Location;
import gherkin.ast.ScenarioDefinition;
import gherkin.ast.Tag;
import java.util.Set;

/**
 * A single test to create within a test run.
 */
public class ScenarioAndLocation {

    /**
     * The scenario this test case belongs to.
     */
    private final ScenarioDefinition scenarioDefinition;

    /**
     * Where this test case came from - Either the scenario, or the table
     * row in a scenario outline's examples block.
     */
    private final Location location;

    /**
     * The tags for this scenario - either from the scenario, the examples table or at feature level.
     */
    private final Set<Tag> tags;

    /**
     * Scenario object containing detail of the scenario, where the test came from and all the tags associated with it.
     * @param scenarioDefinition
     * @param location
     * @param tags
     */
    public ScenarioAndLocation(ScenarioDefinition scenarioDefinition, Location location, Set<Tag> tags) {
        this.scenarioDefinition = scenarioDefinition;
        this.location = location;
        this.tags = tags;
    }

    public ScenarioDefinition getScenario() {
        return scenarioDefinition;
    }

    public Location getLocation() {
        return location;
    }

    public Set<Tag> getTags() {
        return tags;
    }

}