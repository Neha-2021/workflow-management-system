package orchestrator.workflow.service;

import orchestrator.workflow.dto.request.CreateWorkflowDefinitionRequest;
import orchestrator.workflow.dto.response.CreateWorkflowDefinitionResponse;

public interface WorkflowDefinitionService {

  CreateWorkflowDefinitionResponse createWorkflow(CreateWorkflowDefinitionRequest request);
}
