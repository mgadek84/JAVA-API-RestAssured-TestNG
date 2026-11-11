package com.herokuapp.restfullbooker;
import io.restassured.response.Response;
import org.testng.annotations.Test;

public class CreateBookingTest extends BaseTest {

    @Test
    public void createBookingTest() {

        // Create JSON body
        Response response = createBooking();
        response.print();

    }
}
