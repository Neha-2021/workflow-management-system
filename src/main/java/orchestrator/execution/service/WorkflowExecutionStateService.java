package orchestrator.execution.service;

import java.util.UUID;
import orchestrator.execution.entity.WorkflowExecutionEntity;
import orchestrator.workflow.entity.WorkflowStepEntity;

public interface WorkflowExecutionStateService {
  void markRunning(UUID stepExecutionId);

  void markSuccess(UUID stepExecutionId);

  void markFailed(UUID stepExecutionId, String errorMessage);

  void scheduleNextStep(WorkflowExecutionEntity workflowExecution, WorkflowStepEntity currentStep);
}
