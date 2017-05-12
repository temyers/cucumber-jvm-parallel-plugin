package com.github.timm.cucumber.generate;

import gherkin.ast.Node;
import gherkin.ast.ScenarioDefinition;

/**
 * A single test to create within a test run.
 */
public class ScenarioAndSourceLocation {

    /**
     * The scenario this test case belongs to.
     */
    private ScenarioDefinition scenarioDefinition;

    /**
     * Where this test case came from - Either the scenario, or the table
     * row in a scenario outline's examples block.
     */
    private Node source;

    public ScenarioAndSourceLocation(ScenarioDefinition scenarioDefinition, Node source) {
        this.scenarioDefinition = scenarioDefinition;
        this.source = source;
    }

    public ScenarioDefinition getScenarioDefinition() {
        return scenarioDefinition;
    }

    public Node getSource() {
        return source;
    }

}