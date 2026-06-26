Feature: Support ticket management

  As a support agent
  I want to manage support tickets
  So that customer issues are tracked

  Scenario: Create a valid ticket
    Given no tickets exist
    When I create a ticket with title "Server down" and priority "HIGH"
    Then the ticket is created with status "OPEN"

  Scenario: Resolve a ticket
    Given a ticket exists with title "Bug fix" and priority "LOW"
    When I change the ticket status to "IN_PROGRESS"
    And I change the ticket status to "RESOLVED"
    Then the ticket status is "RESOLVED"

  Scenario: Refuse to modify a resolved ticket
    Given a resolved ticket exists
    When I try to change the ticket status to "IN_PROGRESS"
    Then the status change is rejected with conflict

  Scenario: Consult a non-existent ticket
    Given no tickets exist
    When I request ticket with id 999
    Then the ticket is not found
