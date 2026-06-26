Feature: Réservation de salle de réunion

  As a user
  I want to book meeting rooms
  So that I can schedule meetings

  Scenario: Reservation accepted when room exists and slot is free
    Given a room exists with name "Salle A" and capacity 10
    When I create a reservation for room 1 by "Alice" from "2026-05-22T10:00:00" to "2026-05-22T11:00:00"
    Then the reservation is created with status "CONFIRMED"

  Scenario: Reservation refused when room does not exist
    Given no rooms exist
    When I try to create a reservation for room 99 by "Alice" from "2026-05-22T10:00:00" to "2026-05-22T11:00:00"
    Then the reservation is not found

  Scenario: Reservation refused when slot overlaps existing reservation
    Given a room exists with name "Salle A" and capacity 10
    And a confirmed reservation exists for room 1 by "Alice" from "2026-05-22T10:00:00" to "2026-05-22T11:00:00"
    When I try to create a reservation for room 1 by "Bob" from "2026-05-22T10:30:00" to "2026-05-22T11:30:00"
    Then the reservation is rejected with conflict
