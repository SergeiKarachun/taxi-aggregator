package by.sergo.driverservice.component;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "classpath:features",
        glue = "by/sergo/driverservice/component",
        dryRun = false,
        snippets = CucumberOptions.SnippetType.UNDERSCORE
)
public class ComponentTest {
}
