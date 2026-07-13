package orchestrator.execution.service;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Optional;
import java.util.UUID;
import orchestrator.common.enums.WorkflowStatus;
import orchestrator.common.exception.WorkflowDefinitionNotFoundException;
import orchestrator.common.exception.WorkflowStepNotFoundException;
import orchestrator.execution.dto.request.StartWorkflowExecutionRequest;
import orchestrator.execution.dto.response.StartWorkflowExecutionResponse;
import orchestrator.execution.entity.StepExecutionEntity;
import orchestrator.execution.entity.WorkflowExecutionEntity;
import orchestrator.execution.entity.enums.StepStatus;
import orchestrator.execution.repository.StepExecutionRepository;
import orchestrator.execution.repository.WorkflowExecutionRepository;
import orchestrator.workflow.entity.WorkflowDefinitionEntity;
import orchestrator.workflow.entity.WorkflowStepEntity;
import orchestrator.workflow.enums.WorkflowDefinitionStatus;
import orchestrator.workflow.repository.WorkflowDefinitionRepository;
import orchestrator.workflow.repository.WorkflowStepRepository;
import orchestrator.workflow.service.publisher.ExecutionPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkflowExecutionServiceImpl implements WorkflowExecutionService {
  private final WorkflowExecutionRepository workflowExecutionRepository;
  private final StepExecutionRepository stepExecutionRepository;
  private final WorkflowDefinitionRepository workflowDefinitionRepository;
  private final WorkflowStepRepository workflowStepRepository;
  private final ExecutionPublisher executionPublisher;

  private static final Logger log = LoggerFactory.getLogger(WorkflowExecutionServiceImpl.class);

  public WorkflowExecutionServiceImpl(
      WorkflowExecutionRepository workflowExecutionRepository,
      StepExecutionRepository stepExecutionRepository,
      WorkflowStepRepository workflowStepRepository,
      WorkflowDefinitionRepository workflowDefinitionRepository,
      ExecutionPublisher executionPublisher) {
    this.workflowExecutionRepository = workflowExecutionRepository;
    this.stepExecutionRepository = stepExecutionRepository;
    this.workflowStepRepository = workflowStepRepository;
    this.workflowDefinitionRepository = workflowDefinitionRepository;
    this.executionPublisher = executionPublisher;
  }

  @Transactional
  @Override
  public StartWorkflowExecutionResponse startWorkflow(StartWorkflowExecutionRequest request) {
    String workflowName = request.workflowName();
    log.info("WorkflowExecutionServiceImpl | Proceeding to start workflow: {}", workflowName);
    Optional<WorkflowDefinitionEntity> existingWorkflowEntity =
        workflowDefinitionRepository.findTopByNameAndStatusOrderByDefinitionVersionDesc(
            workflowName, WorkflowDefinitionStatus.ACTIVE);

    if (existingWorkflowEntity.isEmpty()) {
      log.warn(
          "WorkflowExecutionServiceImpl | Active workflow not found by name: {}", workflowName);
      throw new WorkflowDefinitionNotFoundException(
          String.format("No active workflow definition found by name: '%s'", workflowName));
    }
    log.info("WorkflowExecutionServiceImpl | Active workflow found");

    Optional<WorkflowStepEntity> existingWorkflowStepEntity =
        workflowStepRepository.findByWorkflowDefinitionIdAndSequenceNumber(
            existingWorkflowEntity.get().getId(), 1);

    if (existingWorkflowStepEntity.isEmpty()) {
      log.warn(
          "WorkflowExecutionServiceImpl | First step for workflow: {} not found", workflowName);
      throw new WorkflowStepNotFoundException(
          String.format(
              "No workflow step found by workflowDefinitionId '%s for sequenceNo 1",
              existingWorkflowEntity.get().getId()));
    }

    UUID workflowExecutionId = UUID.randomUUID();
    WorkflowExecutionEntity workflowExecutionEntity =
        buildWorkflowExecutionEntity(
            request.input(), workflowExecutionId, existingWorkflowEntity.get().getId());

    StepExecutionEntity stepExecutionEntity =
        buildStepExecutionEntity(workflowExecutionId, existingWorkflowStepEntity.get());

    persistExecution(workflowExecutionEntity, stepExecutionEntity);

    executionPublisher.publish(stepExecutionEntity.getId());

    return new StartWorkflowExecutionResponse(workflowExecutionId, WorkflowStatus.RUNNING);
  }

  private void persistExecution(
      WorkflowExecutionEntity workflowExecutionEntity, StepExecutionEntity stepExecutionEntity) {
    workflowExecutionRepository.save(workflowExecutionEntity);
    stepExecutionRepository.save(stepExecutionEntity);
    log.info(
        "WorkflowExecutionServiceImpl | Step with executionId: {} initiated for workflow with executionId: {}",
        stepExecutionEntity.getId(),
        workflowExecutionEntity.getId());
  }

  private static StepExecutionEntity buildStepExecutionEntity(
      UUID workflowExecutionId, WorkflowStepEntity existingWorkflowStepEntity) {
    return new StepExecutionEntity(
        UUID.randomUUID(),
        workflowExecutionId,
        existingWorkflowStepEntity.getId(),
        StepStatus.PENDING,
        0);
  }

  private static WorkflowExecutionEntity buildWorkflowExecutionEntity(
      JsonNode input, UUID workflowExecutionId, UUID workflowDefinitionId) {
    return new WorkflowExecutionEntity(
        workflowExecutionId, workflowDefinitionId, input, WorkflowStatus.RUNNING);
  }
}
