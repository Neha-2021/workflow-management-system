package orchestrator.workflow.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import orchestrator.workflow.dto.request.ActivateWorkflowRequest;
import orchestrator.workflow.dto.request.CreateWorkflowDefinitionRequest;
import orchestrator.workflow.dto.request.CreateWorkflowStepRequest;
import orchestrator.workflow.dto.request.DeactivateWorkflowRequest;
import orchestrator.workflow.dto.response.CreateWorkflowDefinitionResponse;
import orchestrator.workflow.service.WorkflowDefinitionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(WorkflowDefinitionController.class)
class WorkflowDefinitionControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private WorkflowDefinitionService workflowDefinitionService;

  @Test
  void shouldCreateWorkflowDefinitionSuccessfully() throws Exception {
    CreateWorkflowDefinitionRequest request =
        new CreateWorkflowDefinitionRequest(
            "ORDER_WORKFLOW",
            "Order Processing",
            List.of(
                new CreateWorkflowStepRequest(1, "VALIDATE_ORDER", 30),
                new CreateWorkflowStepRequest(2, "PROCESS_PAYMENT", 60)));

    UUID workflowId = UUID.randomUUID();

    CreateWorkflowDefinitionResponse response = new CreateWorkflowDefinitionResponse(workflowId, 1);

    given(workflowDefinitionService.createWorkflow(request)).willReturn(response);

    mockMvc
        .perform(
            post("/api/v1/workflows")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.workflowId").value(workflowId.toString()))
        .andExpect(jsonPath("$.version").value(1));

    then(workflowDefinitionService)
        .should()
        .createWorkflow(any(CreateWorkflowDefinitionRequest.class));
  }

  @Test
  void shouldActivateWorkflowSuccessfully() throws Exception {
    ActivateWorkflowRequest request = new ActivateWorkflowRequest("ORDER_WORKFLOW", 2);

    mockMvc
        .perform(
            post("/api/v1/workflows/activate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());

    then(workflowDefinitionService).should().activateWorkflow(any(ActivateWorkflowRequest.class));
  }

  @Test
  void shouldReturnBadRequestWhenWorkflowNameIsBlankForActivate() throws Exception {
    ActivateWorkflowRequest request = new ActivateWorkflowRequest("", 1);

    mockMvc
        .perform(
            post("/api/v1/workflows/activate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());

    then(workflowDefinitionService).shouldHaveNoInteractions();
  }

  @Test
  void shouldDeactivateWorkflowSuccessfully() throws Exception {
    DeactivateWorkflowRequest request = new DeactivateWorkflowRequest("ORDER_WORKFLOW");

    mockMvc
        .perform(
            post("/api/v1/workflows/deactivate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());

    then(workflowDefinitionService)
        .should()
        .deactivateWorkflow(any(DeactivateWorkflowRequest.class));
  }

  @Test
  void shouldReturnBadRequestWhenWorkflowNameIsBlankForDeactivate() throws Exception {
    DeactivateWorkflowRequest request = new DeactivateWorkflowRequest("");

    mockMvc
        .perform(
            post("/api/v1/workflows/deactivate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());

    then(workflowDefinitionService).shouldHaveNoInteractions();
  }
}
