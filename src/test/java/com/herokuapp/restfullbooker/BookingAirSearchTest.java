package com.herokuapp.restfullbooker;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "classpath:features/booking-air-search.feature",
        glue = "com.herokuapp.restfullbooker.steps"
)
public class BookingAirSearchTest extends AbstractTestNGCucumberTests {
}
