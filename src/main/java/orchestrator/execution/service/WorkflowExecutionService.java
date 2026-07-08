package orchestrator.execution.service;

import orchestrator.execution.dto.request.StartWorkflowExecutionRequest;
import orchestrator.execution.dto.response.StartWorkflowExecutionResponse;

public interface WorkflowExecutionService {
  StartWorkflowExecutionResponse startWorkflow(StartWorkflowExecutionRequest request);
}
