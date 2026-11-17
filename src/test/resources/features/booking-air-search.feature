Feature: Booking air search

  Scenario: Sydney to Melbourne on 2026-12-07 returns an offer
    Given the booking air search stub is running
    When the client posts the bookingAirSearch body
    Then the response is HTTP 200 and JSON
    And bookingAirSearch is present with currency AUD
    And the outbound itinerary is SYD to MEL departing 2026-12-07
    And there is at least one offer
