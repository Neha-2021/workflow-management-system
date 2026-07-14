package orchestrator.execution.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import orchestrator.common.enums.WorkflowStatus;
import orchestrator.execution.entity.StepExecutionEntity;
import orchestrator.execution.entity.WorkflowExecutionEntity;
import orchestrator.execution.entity.enums.StepStatus;
import orchestrator.execution.repository.StepExecutionRepository;
import orchestrator.execution.repository.WorkflowExecutionRepository;
import orchestrator.workflow.entity.WorkflowStepEntity;
import orchestrator.workflow.repository.WorkflowStepRepository;
import orchestrator.workflow.service.publisher.ExecutionPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkflowExecutionStateServiceImpl implements WorkflowExecutionStateService {
  private final StepExecutionRepository stepExecutionRepository;
  private final WorkflowStepRepository workflowStepRepository;
  private final WorkflowExecutionRepository workflowExecutionRepository;
  private final ExecutionPublisher executionPublisher;

  public WorkflowExecutionStateServiceImpl(
      StepExecutionRepository stepExecutionRepository,
      WorkflowStepRepository workflowStepRepository,
      WorkflowExecutionRepository workflowExecutionRepository,
      ExecutionPublisher executionPublisher) {
    this.stepExecutionRepository = stepExecutionRepository;
    this.workflowStepRepository = workflowStepRepository;
    this.workflowExecutionRepository = workflowExecutionRepository;
    this.executionPublisher = executionPublisher;
  }

  @Override
  public void markRunning(StepExecutionEntity stepExecution) {
    stepExecution.setStatus(StepStatus.RUNNING);
    stepExecutionRepository.save(stepExecution);
  }

  @Override
  public void markSuccess(StepExecutionEntity stepExecution) {
    stepExecution.setStatus(StepStatus.SUCCESS);
    stepExecution.setCompletedAt(Instant.now());
    stepExecutionRepository.save(stepExecution);
  }

  @Override
  public void markFailed(StepExecutionEntity stepExecution, String errorMessage) {
    stepExecution.setStatus(StepStatus.FAILED);
    stepExecution.setErrorMessage(errorMessage);
    stepExecution.setCompletedAt(Instant.now());
    stepExecutionRepository.save(stepExecution);
  }

  @Transactional
  @Override
  public void scheduleNextStep(
      WorkflowExecutionEntity workflowExecution, WorkflowStepEntity currentStep) {
    Optional<WorkflowStepEntity> nextStep =
        workflowStepRepository.findByWorkflowDefinitionIdAndSequenceNumber(
            currentStep.getWorkflowDefinitionId(), currentStep.getSequenceNumber() + 1);

    if (nextStep.isPresent()) {
      StepExecutionEntity nextStepExecution =
          new StepExecutionEntity(
              UUID.randomUUID(),
              workflowExecution.getId(),
              nextStep.get().getId(),
              StepStatus.PENDING,
              0);
      nextStepExecution.setStartedAt(Instant.now());

      stepExecutionRepository.save(nextStepExecution);
      executionPublisher.publish(nextStepExecution.getId());
      return;
    }

    workflowExecution.setStatus(WorkflowStatus.COMPLETED);
    workflowExecution.setCompletedAt(Instant.now());
    workflowExecutionRepository.save(workflowExecution);
  }
}
