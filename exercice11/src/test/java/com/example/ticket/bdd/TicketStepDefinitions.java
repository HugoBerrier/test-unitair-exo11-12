package com.example.ticket.bdd;

import com.example.ticket.repository.InMemoryTicketRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TicketStepDefinitions {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InMemoryTicketRepository ticketRepository;

    private Long currentTicketId;
    private MvcResult lastResult;

    @Before
    public void reset() {
        ticketRepository.clear();
        currentTicketId = null;
        lastResult = null;
    }

    @Given("no tickets exist")
    public void noTicketsExist() {
        ticketRepository.clear();
    }

    @Given("a ticket exists with title {string} and priority {string}")
    public void aTicketExists(String title, String priority) throws Exception {
        createTicket(title, priority);
    }

    @Given("a resolved ticket exists")
    public void aResolvedTicketExists() throws Exception {
        createTicket("Resolved issue", "MEDIUM");
        changeStatus("IN_PROGRESS");
        changeStatus("RESOLVED");
    }

    @When("I create a ticket with title {string} and priority {string}")
    public void iCreateATicket(String title, String priority) throws Exception {
        createTicket(title, priority);
    }

    @When("I change the ticket status to {string}")
    public void iChangeTheTicketStatus(String status) throws Exception {
        changeStatus(status);
    }

    @When("I try to change the ticket status to {string}")
    public void iTryToChangeTheTicketStatus(String status) throws Exception {
        changeStatus(status);
    }

    @When("I request ticket with id {int}")
    public void iRequestTicketWithId(int id) throws Exception {
        lastResult = mockMvc.perform(get("/api/tickets/" + id)).andReturn();
    }

    @Then("the ticket is created with status {string}")
    public void theTicketIsCreatedWithStatus(String status) throws Exception {
        assertEquals(201, lastResult.getResponse().getStatus());
        String actualStatus = JsonPath.read(lastResult.getResponse().getContentAsString(), "$.status");
        assertEquals(status, actualStatus);
    }

    @Then("the ticket status is {string}")
    public void theTicketStatusIs(String status) throws Exception {
        assertEquals(200, lastResult.getResponse().getStatus());
        String actualStatus = JsonPath.read(lastResult.getResponse().getContentAsString(), "$.status");
        assertEquals(status, actualStatus);
    }

    @Then("the status change is rejected with conflict")
    public void theStatusChangeIsRejectedWithConflict() {
        assertEquals(409, lastResult.getResponse().getStatus());
    }

    @Then("the ticket is not found")
    public void theTicketIsNotFound() {
        assertEquals(404, lastResult.getResponse().getStatus());
    }

    private void createTicket(String title, String priority) throws Exception {
        lastResult = mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"%s","priority":"%s"}
                                """.formatted(title, priority)))
                .andExpect(status().isCreated())
                .andReturn();
        Number id = JsonPath.read(lastResult.getResponse().getContentAsString(), "$.id");
        currentTicketId = id.longValue();
    }

    private void changeStatus(String status) throws Exception {
        lastResult = mockMvc.perform(patch("/api/tickets/" + currentTicketId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status":"%s"}
                                """.formatted(status)))
                .andReturn();
    }
}
