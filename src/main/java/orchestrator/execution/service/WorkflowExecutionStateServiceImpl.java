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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkflowExecutionStateServiceImpl implements WorkflowExecutionStateService {
  private final StepExecutionRepository stepExecutionRepository;
  private final WorkflowStepRepository workflowStepRepository;
  private final WorkflowExecutionRepository workflowExecutionRepository;
  private final ExecutionPublisher executionPublisher;

  private static final Logger log =
      LoggerFactory.getLogger(WorkflowExecutionStateServiceImpl.class);

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
    log.info(
        "WorkflowExecutionStateServiceImpl | Marking step execution running: {}",
        stepExecution.getId());
    stepExecution.setStatus(StepStatus.RUNNING);
    stepExecutionRepository.save(stepExecution);
    log.info(
        "WorkflowExecutionStateServiceImpl | Step execution marked running: {}",
        stepExecution.getId());
  }

  @Override
  public void markSuccess(StepExecutionEntity stepExecution) {
    log.info(
        "WorkflowExecutionStateServiceImpl | Marking step execution successful: {}",
        stepExecution.getId());
    stepExecution.setStatus(StepStatus.SUCCESS);
    stepExecution.setCompletedAt(Instant.now());
    stepExecutionRepository.save(stepExecution);
    log.info(
        "WorkflowExecutionStateServiceImpl | Step execution marked successful: {}",
        stepExecution.getId());
  }

  @Override
  public void markFailed(StepExecutionEntity stepExecution, String errorMessage) {
    log.info(
        "WorkflowExecutionStateServiceImpl | Marking step execution failed: {}",
        stepExecution.getId());
    stepExecution.setStatus(StepStatus.FAILED);
    stepExecution.setErrorMessage(errorMessage);
    stepExecution.setCompletedAt(Instant.now());
    stepExecutionRepository.save(stepExecution);
    log.info(
        "WorkflowExecutionStateServiceImpl | Step execution marked failed: {}",
        stepExecution.getId());
  }

  @Transactional
  @Override
  public void scheduleNextStep(
      WorkflowExecutionEntity workflowExecution, WorkflowStepEntity currentStep) {
    Integer nextSequenceNumber = currentStep.getSequenceNumber() + 1;
    log.info(
        "WorkflowExecutionStateServiceImpl | Looking for next step for workflowDefinitionId: {}, sequenceNumber: {}",
        currentStep.getWorkflowDefinitionId(),
        nextSequenceNumber);
    Optional<WorkflowStepEntity> nextStep =
        workflowStepRepository.findByWorkflowDefinitionIdAndSequenceNumber(
            currentStep.getWorkflowDefinitionId(), nextSequenceNumber);

    if (nextStep.isPresent()) {
      log.info(
          "WorkflowExecutionStateServiceImpl | Next workflow step found: {}, creating step execution for workflowExecutionId: {}",
          nextStep.get().getId(),
          workflowExecution.getId());
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
      log.info(
          "WorkflowExecutionStateServiceImpl | Next step execution persisted and published: {}",
          nextStepExecution.getId());
      return;
    }

    log.info(
        "WorkflowExecutionStateServiceImpl | No next workflow step found, marking workflow execution completed: {}",
        workflowExecution.getId());
    workflowExecution.setStatus(WorkflowStatus.COMPLETED);
    workflowExecution.setCompletedAt(Instant.now());
    workflowExecutionRepository.save(workflowExecution);
    log.info(
        "WorkflowExecutionStateServiceImpl | Workflow execution marked completed: {}",
        workflowExecution.getId());
  }
}
