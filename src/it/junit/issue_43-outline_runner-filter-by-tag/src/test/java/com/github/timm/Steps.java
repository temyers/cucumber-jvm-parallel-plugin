package com.github.timm;

import cucumber.api.java.en.*;

public class Steps {
    @Given("^I have a feature with (\\d+) scenarios$")
    public void i_have_a_feature_with_scenarios(final int arg1) throws Throwable {
    }

    @When("^I generate runners per scenario$")
    public void i_generate_runners_per_scenario() throws Throwable {
    }

    @Then("^a runner should be created for each scenario$")
    public void a_runner_should_be_created_for_each_scenario() throws Throwable {
    }

    @Given("^I have a scenario with (\\d+) examples$")
    public void i_have_a_scenario_with_examples(final int arg1) throws Throwable {
    }

    @Then("^a runner should be created for foo example$")
    public void a_runner_should_be_created_for_foo_example() throws Throwable {
    }

    @Then("^a runner should be created for bar example$")
    public void a_runner_should_be_created_for_bar_example() throws Throwable {
    }

    @Then("^a runner should be created for baz example$")
    public void a_runner_should_be_created_for_baz_example() throws Throwable {
    }
    @Given("^I have feature files$")
    public void i_have_feature_files() throws Throwable {
    }

    @When("^I generate Maven sources$")
    public void i_generate_Maven_sources() throws Throwable {
    }

    @Then("^the file \"(.*?)\" should exist$")
    public void the_file_should_exist(final String arg1) throws Throwable {
    }

    @Then("^it should contain:$")
    public void it_should_contain(final String arg1) throws Throwable {
    }
}
