package orchestrator.workflow.controller;

import jakarta.validation.Valid;
import orchestrator.workflow.dto.request.ActivateWorkflowRequest;
import orchestrator.workflow.dto.request.CreateWorkflowDefinitionRequest;
import orchestrator.workflow.dto.request.DeactivateWorkflowRequest;
import orchestrator.workflow.dto.response.CreateWorkflowDefinitionResponse;
import orchestrator.workflow.service.WorkflowDefinitionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WorkflowDefinitionController {
  private final WorkflowDefinitionService workflowDefinitionService;

  private static final Logger log = LoggerFactory.getLogger(WorkflowDefinitionController.class);

  public WorkflowDefinitionController(WorkflowDefinitionService workflowDefinitionService) {
    this.workflowDefinitionService = workflowDefinitionService;
  }

  @PostMapping("/api/v1/workflows")
  public ResponseEntity<CreateWorkflowDefinitionResponse> create(
      @RequestBody @Valid CreateWorkflowDefinitionRequest request) {
    log.info("Request received to create workflow definition: {}", request.name());
    CreateWorkflowDefinitionResponse response = workflowDefinitionService.createWorkflow(request);
    log.info(
        "Workflow '{}' created successfully with ID: {}", request.name(), response.workflowId());
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PostMapping("/api/v1/workflows/activate")
  public ResponseEntity<Void> activate(@RequestBody @Valid ActivateWorkflowRequest request) {
    log.info("Request received to activate workflow definition: {}", request.workflowName());
    workflowDefinitionService.activateWorkflow(request);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/api/v1/workflows/deactivate")
  public ResponseEntity<Void> deactivate(@RequestBody @Valid DeactivateWorkflowRequest request) {
    log.info("Request received to deactivate workflow definition: {}", request.workflowName());
    workflowDefinitionService.deactivateWorkflow(request);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
