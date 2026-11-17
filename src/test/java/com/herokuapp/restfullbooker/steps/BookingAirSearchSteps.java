package com.herokuapp.restfullbooker.steps;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.herokuapp.restfullbooker.BaseTest;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;

public class BookingAirSearchSteps extends BaseTest {

    private static final String OFFER_PATH =
            "data.bookingAirSearch.originalResponse.unbundledOffers[0]";
    private static final String SEGMENT_PATH =
            OFFER_PATH + "[0].itineraryPart[0].segments[0]";

    private WireMockServer wireMock;
    private ValidatableResponse response;

    @Given("the booking air search stub is running")
    public void theBookingAirSearchStubIsRunning() {
        wireMock = new WireMockServer(WireMockConfiguration.options().
                bindAddress("127.0.0.1").dynamicPort());
        wireMock.start();
        wireMock.stubFor(post(urlEqualTo("/api/graphql")).
                withRequestBody(matchingJsonPath("$.operationName", WireMock.equalTo("bookingAirSearch"))).
                willReturn(aResponse().
                        withStatus(200).
                        withHeader("Content-Type", "application/json").
                        withBody(fixture("/booking-air-search-response.json"))));
    }

    @When("the client posts the bookingAirSearch body")
    public void theClientPostsTheBookingAirSearchBody() {
        response = given().
                contentType(ContentType.JSON).
                body(bookingAirSearchBody()).
        when().
                post("http://127.0.0.1:" + wireMock.port() + "/api/graphql").
        then();
    }

    @Then("the response is HTTP 200 and JSON")
    public void theResponseIsHttp200AndJson() {
        response.
                statusCode(200).
                contentType(containsString("application/json"));
    }

    @Then("bookingAirSearch is present with currency AUD")
    public void bookingAirSearchIsPresentWithCurrencyAud() {
        response.
                body("data.bookingAirSearch", notNullValue()).
                body("data.bookingAirSearch.__typename", notNullValue()).
                body("data.bookingAirSearch.originalResponse.currency", equalTo("AUD"));
    }

    @Then("the outbound itinerary is SYD to MEL departing 2026-12-07")
    public void theOutboundItineraryIsSydToMelDeparting() {
        response.
                body(SEGMENT_PATH + ".origin", equalTo("SYD")).
                body(SEGMENT_PATH + ".destination", equalTo("MEL")).
                body(SEGMENT_PATH + ".departure", startsWith("2026-12-07"));
    }

    @Then("there is at least one offer")
    public void thereIsAtLeastOneOffer() {
        response.body(OFFER_PATH, not(empty()));
    }

    @After
    public void stopBookingAirSearchStub() {
        if (wireMock != null && wireMock.isRunning()) {
            wireMock.stop();
        }
    }

    private static String fixture(String resource) {
        try (InputStream in = BookingAirSearchSteps.class.getResourceAsStream(resource)) {
            if (in == null) {
                throw new IllegalStateException("Missing fixture " + resource);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read fixture " + resource, e);
        }
    }
}
