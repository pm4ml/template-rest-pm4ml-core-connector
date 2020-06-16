package com.modus.client.it;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

/**
 * Created by art on 4/20/20.
 */
@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"json:target/cucumber/test-report.json", "html:target/cucumber/html", "pretty"})
public class CucumberTestRunnerIT {
}
