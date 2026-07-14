package orchestrator.execution.service;

import orchestrator.execution.entity.StepExecutionEntity;
import orchestrator.execution.entity.WorkflowExecutionEntity;
import orchestrator.workflow.entity.WorkflowStepEntity;

public interface WorkflowExecutionStateService {
  void markRunning(StepExecutionEntity stepExecution);

  void markSuccess(StepExecutionEntity stepExecution);

  void markFailed(StepExecutionEntity stepExecution, String errorMessage);

  void scheduleNextStep(WorkflowExecutionEntity workflowExecution, WorkflowStepEntity currentStep);
}
