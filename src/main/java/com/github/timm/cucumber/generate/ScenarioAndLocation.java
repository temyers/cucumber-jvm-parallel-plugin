package com.github.timm.cucumber.generate;

import gherkin.ast.Location;
import gherkin.ast.ScenarioDefinition;

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

    public ScenarioAndLocation(ScenarioDefinition scenarioDefinition, Location location) {
        this.scenarioDefinition = scenarioDefinition;
        this.location = location;
    }

    public ScenarioDefinition getScenario() {
        return scenarioDefinition;
    }

    public Location getLocation() {
        return location;
    }
}