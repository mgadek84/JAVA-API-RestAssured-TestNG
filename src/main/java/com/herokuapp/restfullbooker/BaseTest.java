package com.herokuapp.restfullbooker;
import org.json.JSONObject;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class BaseTest {

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