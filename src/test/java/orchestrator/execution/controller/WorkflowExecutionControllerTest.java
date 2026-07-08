package orchestrator.execution.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.UUID;
import orchestrator.common.enums.WorkflowStatus;
import orchestrator.execution.dto.request.StartWorkflowExecutionRequest;
import orchestrator.execution.dto.response.StartWorkflowExecutionResponse;
import orchestrator.execution.service.WorkflowExecutionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(WorkflowExecutionController.class)
class WorkflowExecutionControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private WorkflowExecutionService workflowExecutionService;

  @Test
  void shouldStartWorkflowSuccessfully() throws Exception {

    ObjectNode input = objectMapper.createObjectNode();
    input.put("orderId", "123");

    StartWorkflowExecutionRequest request =
        new StartWorkflowExecutionRequest("ORDER_WORKFLOW", input);

    UUID executionId = UUID.randomUUID();

    StartWorkflowExecutionResponse response =
        new StartWorkflowExecutionResponse(executionId, WorkflowStatus.RUNNING);

    given(workflowExecutionService.startWorkflow(request)).willReturn(response);

    mockMvc
        .perform(
            post("/api/v1/workflow-executions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isAccepted())
        .andExpect(jsonPath("$.workflowExecutionId").value(executionId.toString()))
        .andExpect(jsonPath("$.status").value("RUNNING"));

    then(workflowExecutionService).should().startWorkflow(any(StartWorkflowExecutionRequest.class));
  }

  @Test
  void shouldReturnBadRequestWhenWorkflowNameIsBlank() throws Exception {

    ObjectNode input = objectMapper.createObjectNode();
    input.put("orderId", "123");

    StartWorkflowExecutionRequest request = new StartWorkflowExecutionRequest("", input);

    mockMvc
        .perform(
            post("/api/v1/workflow-executions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());

    then(workflowExecutionService).shouldHaveNoInteractions();
  }
}
