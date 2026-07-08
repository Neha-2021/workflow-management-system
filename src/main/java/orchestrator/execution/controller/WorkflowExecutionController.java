package orchestrator.execution.controller;

import jakarta.validation.Valid;
import orchestrator.execution.dto.request.StartWorkflowExecutionRequest;
import orchestrator.execution.dto.response.StartWorkflowExecutionResponse;
import orchestrator.execution.service.WorkflowExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WorkflowExecutionController {
  private final WorkflowExecutionService workflowExecutionService;

  private static final Logger log = LoggerFactory.getLogger(WorkflowExecutionController.class);

  public WorkflowExecutionController(WorkflowExecutionService workflowExecutionService) {
    this.workflowExecutionService = workflowExecutionService;
  }

  @PostMapping("/api/v1/workflow-executions")
  public ResponseEntity<StartWorkflowExecutionResponse> startWorkflow(
      @RequestBody @Valid StartWorkflowExecutionRequest request) {
    log.info(
        "WorkflowExecutionController | Request received to start workflow {}",
        request.workflowName());
    StartWorkflowExecutionResponse response = workflowExecutionService.startWorkflow(request);
    log.info("WorkflowExecutionController | Workflow started successfully");
    return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
  }
}
