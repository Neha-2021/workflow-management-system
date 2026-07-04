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
import orchestrator.workflow.dto.request.CreateWorkflowDefinitionRequest;
import orchestrator.workflow.dto.request.CreateWorkflowStepRequest;
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
}
