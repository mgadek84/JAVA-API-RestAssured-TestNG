package com.herokuapp.restfullbooker;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

public class BookingAirSearchTest extends BaseTest {

    private static final String OFFER_PATH =
            "data.bookingAirSearch.originalResponse.unbundledOffers[0]";
    private static final String SEGMENT_PATH =
            OFFER_PATH + "[0].itineraryPart[0].segments[0]";

    @Test
    public void bookingAirSearchTest() {
        WireMockServer wireMock = new WireMockServer(WireMockConfiguration.options().
                bindAddress("127.0.0.1").dynamicPort());
        wireMock.start();
        try {
            wireMock.stubFor(post(urlEqualTo("/api/graphql")).
                    withRequestBody(matchingJsonPath("$.operationName", equalTo("bookingAirSearch"))).
                    willReturn(aResponse().
                            withStatus(200).
                            withHeader("Content-Type", "application/json").
                            withBody(fixture("/booking-air-search-response.json"))));

            String url = "http://127.0.0.1:" + wireMock.port() + "/api/graphql";
            Response response = RestAssured.given().contentType(ContentType.JSON).
                    body(bookingAirSearchBody()).post(url);

            String snippet = bodySnippet(response);
            Assert.assertEquals(response.getStatusCode(), 200,
                    "bookingAirSearch expected HTTP 200 without session headers. status="
                            + response.getStatusCode() + " body=" + snippet);

            String contentType = response.getContentType();
            Assert.assertNotNull(contentType, "Content type is missing. body=" + snippet);
            Assert.assertTrue(contentType.contains("application/json"),
                    "Content type should be JSON. contentType=" + contentType + " body=" + snippet);

            Assert.assertNotNull(response.jsonPath().get("data.bookingAirSearch"),
                    "bookingAirSearch is missing. body=" + snippet);

            Assert.assertNotNull(response.jsonPath().get("data.bookingAirSearch.__typename"),
                    "__typename is missing. body=" + snippet);

            String currency = response.jsonPath().getString("data.bookingAirSearch.originalResponse.currency");
            Assert.assertEquals(currency, "AUD", "currency should be AUD. body=" + snippet);

            List<?> offers = response.jsonPath().getList(OFFER_PATH);
            Assert.assertNotNull(offers, "Outbound offers are missing. body=" + snippet);
            Assert.assertFalse(offers.isEmpty(), "Expected at least one outbound offer. body=" + snippet);

            String origin = response.jsonPath().getString(SEGMENT_PATH + ".origin");
            Assert.assertEquals(origin, "SYD", "Outbound origin should be SYD. body=" + snippet);

            String destination = response.jsonPath().getString(SEGMENT_PATH + ".destination");
            Assert.assertEquals(destination, "MEL", "Outbound destination should be MEL. body=" + snippet);

            String departure = response.jsonPath().getString(SEGMENT_PATH + ".departure");
            Assert.assertNotNull(departure, "Outbound departure is missing. body=" + snippet);
            Assert.assertTrue(departure.startsWith("2026-12-07"),
                    "Outbound departure date should be 2026-12-07. departure=" + departure);
        } finally {
            wireMock.stop();
        }
    }

    private static String fixture(String resource) {
        try (InputStream in = BookingAirSearchTest.class.getResourceAsStream(resource)) {
            if (in == null) {
                throw new IllegalStateException("Missing fixture " + resource);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read fixture " + resource, e);
        }
    }

    private static String bodySnippet(Response response) {
        String body = response.getBody() == null ? "" : response.asString();
        if (body == null) {
            return "";
        }
        String compact = body.replaceAll("\\s+", " ").trim();
        int max = 300;
        if (compact.length() <= max) {
            return compact;
        }
        return compact.substring(0, max) + "...";
    }
}
