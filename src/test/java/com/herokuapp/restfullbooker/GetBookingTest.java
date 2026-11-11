package com.herokuapp.restfullbooker;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class GetBookingTest {

    @Test
    public void getBookingEndPoint() {
        Response response = RestAssured.get("https://restful-booker.herokuapp.com/booking/4636s");
        response.print();

        // Verify response 200
        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200, but it's not");

        // Verify all fields
        SoftAssert softAssert = new SoftAssert();
        String actualFirstName = response.jsonPath().getString("firstname");
        softAssert.assertEquals(actualFirstName, "Mark", "Value in response does not match expected value");

        String actualLastName = response.jsonPath().getString("lastname");
        softAssert.assertEquals(actualFirstName, "Mark");

        int Price = response.jsonPath().get("totalprice");
        softAssert.assertEquals(Price, 742, "Value has changed!");

        boolean depositpaid = response.jsonPath().getBoolean("depositpaid");
        softAssert.assertTrue(depositpaid, "Error, verify! Deposit should be true but it's not");

        String actualCheckin = response.jsonPath().getString("bookingdates.checkin");
        softAssert.assertEquals(actualCheckin, "2023-04-06", "Incorrect checkin date!");

        String actualCheckout = response.jsonPath().getString("bookingdates.checkout");
        softAssert.assertEquals(actualCheckout, "2023-04-06", "Incorrect checkout date!");

        softAssert.assertAll();

        // {"firstname":"Mark",
        // "lastname":"Jones",
        // "totalprice":693,
        // "depositpaid":false,
        // "bookingdates":
            // {"checkin":"2023-04-06",
            // "checkout":"2025-04-27"},
        // "additionalneeds":"Breakfast"}



    }
}
