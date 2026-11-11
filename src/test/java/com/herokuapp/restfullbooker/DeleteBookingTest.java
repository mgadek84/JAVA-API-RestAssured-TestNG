package com.herokuapp.restfullbooker;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class DeleteBookingTest extends BaseTest {

    @Test
    public void deleteBookingTest() {

        // Create JSON body
        Response responseCreate = createBooking();
        responseCreate.print();

        // Get bookingId of new booking
        int bookingid = responseCreate.jsonPath().getInt("bookingid");

        Response responseUpdate = RestAssured.given().auth().preemptive().basic("admin", "password123").contentType(ContentType.JSON).
                delete("https://restful-booker.herokuapp.com/booking/" + bookingid);

        Response response = RestAssured.get("https://restful-booker.herokuapp.com/booking/" + bookingid);
        response.print();

    }
}
