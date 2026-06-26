package com.example.meeting.bdd;

import com.example.meeting.repository.InMemoryReservationRepository;
import com.example.meeting.repository.InMemoryRoomRepository;
import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReservationStepDefinitions {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InMemoryRoomRepository roomRepository;

    @Autowired
    private InMemoryReservationRepository reservationRepository;

    private MvcResult lastResult;

    @Before
    public void reset() {
        roomRepository.clear();
        reservationRepository.clear();
        lastResult = null;
    }

    @Given("no rooms exist")
    public void noRoomsExist() {
        roomRepository.clear();
        reservationRepository.clear();
    }

    @Given("a room exists with name {string} and capacity {int}")
    public void aRoomExists(String name, int capacity) throws Exception {
        lastResult = mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"%s","capacity":%d}
                                """.formatted(name, capacity)))
                .andExpect(status().isCreated())
                .andReturn();
    }

    @Given("a confirmed reservation exists for room {int} by {string} from {string} to {string}")
    public void aConfirmedReservationExists(int roomId, String bookedBy, String start, String end) throws Exception {
        createReservation(roomId, bookedBy, start, end, true);
    }

    @When("I create a reservation for room {int} by {string} from {string} to {string}")
    public void iCreateAReservation(int roomId, String bookedBy, String start, String end) throws Exception {
        createReservation(roomId, bookedBy, start, end, true);
    }

    @When("I try to create a reservation for room {int} by {string} from {string} to {string}")
    public void iTryToCreateAReservation(int roomId, String bookedBy, String start, String end) throws Exception {
        createReservation(roomId, bookedBy, start, end, false);
    }

    @Then("the reservation is created with status {string}")
    public void theReservationIsCreatedWithStatus(String status) throws Exception {
        assertEquals(201, lastResult.getResponse().getStatus());
        String actualStatus = JsonPath.read(lastResult.getResponse().getContentAsString(), "$.status");
        assertEquals(status, actualStatus);
    }

    @Then("the reservation is not found")
    public void theReservationIsNotFound() {
        assertEquals(404, lastResult.getResponse().getStatus());
    }

    @Then("the reservation is rejected with conflict")
    public void theReservationIsRejectedWithConflict() {
        assertEquals(409, lastResult.getResponse().getStatus());
    }

    private void createReservation(int roomId, String bookedBy, String start, String end,
                                   boolean expectCreated) throws Exception {
        var request = mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"roomId":%d,"bookedBy":"%s","startTime":"%s","endTime":"%s"}
                        """.formatted(roomId, bookedBy, start, end)));

        lastResult = expectCreated
                ? request.andExpect(status().isCreated()).andReturn()
                : request.andReturn();
    }
}
