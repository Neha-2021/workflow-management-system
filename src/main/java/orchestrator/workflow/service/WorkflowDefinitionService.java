package orchestrator.workflow.service;

import orchestrator.workflow.dto.request.ActivateWorkflowRequest;
import orchestrator.workflow.dto.request.CreateWorkflowDefinitionRequest;
import orchestrator.workflow.dto.request.DeactivateWorkflowRequest;
import orchestrator.workflow.dto.response.CreateWorkflowDefinitionResponse;

public interface WorkflowDefinitionService {

  CreateWorkflowDefinitionResponse createWorkflow(CreateWorkflowDefinitionRequest request);

  void activateWorkflow(ActivateWorkflowRequest request);

  void deactivateWorkflow(DeactivateWorkflowRequest request);
}
