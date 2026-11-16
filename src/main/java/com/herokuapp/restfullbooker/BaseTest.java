package com.herokuapp.restfullbooker;
import org.json.JSONArray;
import org.json.JSONObject;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class BaseTest {

    protected String bookingAirSearchBody() {
        JSONObject from = new JSONObject();
        from.put("useNearbyLocations", false);
        from.put("code", "SYD");

        JSONObject to = new JSONObject();
        to.put("useNearbyLocations", false);
        to.put("code", "MEL");

        JSONObject when = new JSONObject();
        when.put("date", "2026-12-07");

        JSONObject itineraryPart = new JSONObject();
        itineraryPart.put("from", from);
        itineraryPart.put("to", to);
        itineraryPart.put("when", when);

        JSONObject passengers = new JSONObject();
        passengers.put("ADT", 1);

        JSONObject airSearchInput = new JSONObject();
        airSearchInput.put("cabinClass", "First");
        airSearchInput.put("awardBooking", false);
        airSearchInput.put("promoCodes", new JSONArray());
        airSearchInput.put("searchType", "BRANDED");
        airSearchInput.put("itineraryParts", new JSONArray().put(itineraryPart));
        airSearchInput.put("passengers", passengers);

        JSONObject body = new JSONObject();
        body.put("operationName", "bookingAirSearch");
        body.put("variables", new JSONObject().put("airSearchInput", airSearchInput));
        body.put("extensions", new JSONObject());
        body.put("query", "query bookingAirSearch($airSearchInput: CustomAirSearchInput) { bookingAirSearch(airSearchInput: $airSearchInput) { originalResponse __typename } }");
        return body.toString();
    }

    protected Response createBooking() {

        JSONObject body = new JSONObject();
        body.put("firstname", "Michał");
        body.put("lastname", "Gądek");
        body.put("totalprice", 220);
        body.put("depositpaid", true);

        JSONObject bookingdates = new JSONObject();
        bookingdates.put("checkin", "2026-11-11");
        bookingdates.put("checkout", "2026-11-21");
        body.put("bookingdates", bookingdates);
        body.put("additionalneeds", "Animals");


        // Get Response
        Response response = RestAssured.given().contentType(ContentType.JSON).
                body(body.toString()).post("https://restful-booker.herokuapp.com/booking");
        return response;

    }
}