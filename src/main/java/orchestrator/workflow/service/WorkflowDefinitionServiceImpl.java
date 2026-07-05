package orchestrator.workflow.service;

import java.util.*;
import orchestrator.common.exception.InvalidWorkflowDefinitionException;
import orchestrator.common.exception.WorkflowDefinitionNotFoundException;
import orchestrator.workflow.dto.request.ActivateWorkflowRequest;
import orchestrator.workflow.dto.request.CreateWorkflowDefinitionRequest;
import orchestrator.workflow.dto.request.CreateWorkflowStepRequest;
import orchestrator.workflow.dto.request.DeactivateWorkflowRequest;
import orchestrator.workflow.dto.response.CreateWorkflowDefinitionResponse;
import orchestrator.workflow.entity.WorkflowDefinitionEntity;
import orchestrator.workflow.entity.WorkflowStepEntity;
import orchestrator.workflow.enums.WorkflowDefinitionStatus;
import orchestrator.workflow.repository.WorkflowDefinitionRepository;
import orchestrator.workflow.repository.WorkflowStepRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkflowDefinitionServiceImpl implements WorkflowDefinitionService {
  private final WorkflowDefinitionRepository workflowDefinitionRepository;
  private final WorkflowStepRepository workflowStepRepository;

  private static final Logger log = LoggerFactory.getLogger(WorkflowDefinitionServiceImpl.class);

  public WorkflowDefinitionServiceImpl(
      WorkflowDefinitionRepository workflowDefinitionRepository,
      WorkflowStepRepository workflowStepRepository) {
    this.workflowDefinitionRepository = workflowDefinitionRepository;
    this.workflowStepRepository = workflowStepRepository;
  }

  @Transactional
  @Override
  public CreateWorkflowDefinitionResponse createWorkflow(CreateWorkflowDefinitionRequest request) {
    log.info("WorkflowDefinitionServiceImpl | Validating steps");
    validateSteps(request.steps());
    Integer definitionVersion = determineDefinitionVersion(request.name());
    UUID workflowId = UUID.randomUUID();
    WorkflowDefinitionEntity workflowDefinition =
        buildWorkflowDefinitionEntity(workflowId, definitionVersion, request);
    List<WorkflowStepEntity> stepEntities = buildStepEntities(workflowId, request);
    workflowDefinitionRepository.save(workflowDefinition);
    log.info("WorkflowDefinitionServiceImpl | Workflow definition persisted");

    workflowStepRepository.saveAll(stepEntities);
    log.info("WorkflowDefinitionServiceImpl | Workflow steps persisted");
    CreateWorkflowDefinitionResponse response =
        new CreateWorkflowDefinitionResponse(workflowId, definitionVersion);
    log.info("WorkflowDefinitionServiceImpl | Returning response: {}", response);
    return response;
  }

  @Transactional
  @Override
  public void activateWorkflow(ActivateWorkflowRequest request) {
    String workflowName = request.workflowName();
    Optional<WorkflowDefinitionEntity> existingEntity =
        workflowDefinitionRepository.findByNameAndDefinitionVersion(
            workflowName, request.version());

    if (existingEntity.isEmpty()) {
      log.warn(
          "WorkflowDefinitionServiceImpl | No workflow definition found by name: {}", workflowName);
      throw new WorkflowDefinitionNotFoundException(
          String.format("Workflow '%s' version '%d' not found", workflowName, request.version()));
    }

    if (existingEntity.get().getStatus() == WorkflowDefinitionStatus.ACTIVE) {
      log.info("WorkflowDefinitionServiceImpl | Workflow definition is already active");
      return;
    }

    Optional<WorkflowDefinitionEntity> currentActiveEntity = fetchActiveWorkflow(workflowName);

    if (currentActiveEntity.isPresent()) {
      log.info(
          "WorkflowDefinitionServiceImpl | Active workflow definition found with name: '{}' and "
              + "version '{}'",
          workflowName,
          currentActiveEntity.get().getDefinitionVersion());
      currentActiveEntity.get().setStatus(WorkflowDefinitionStatus.INACTIVE);
      workflowDefinitionRepository.save(currentActiveEntity.get());
      log.info(
          "WorkflowDefinitionServiceImpl | Deactivated the active version {}",
          currentActiveEntity.get().getDefinitionVersion());
    }

    existingEntity.get().setStatus(WorkflowDefinitionStatus.ACTIVE);
    workflowDefinitionRepository.save(existingEntity.get());

    log.info(
        "WorkflowDefinitionServiceImpl | Activated the workflow: '{}' with version {}",
        workflowName,
        existingEntity.get().getDefinitionVersion());
  }

  @Override
  public void deactivateWorkflow(DeactivateWorkflowRequest request) {
    String workflowName = request.workflowName();
    Optional<WorkflowDefinitionEntity> existingEntity = findByWorkflowName(workflowName);

    if (existingEntity.isEmpty()) {
      log.warn(
          "WorkflowDefinitionServiceImpl | No workflow definition found by name: {}", workflowName);
      throw new WorkflowDefinitionNotFoundException(
          String.format("Workflow '%s' not found", workflowName));
    }
    Optional<WorkflowDefinitionEntity> existingActiveEntity = fetchActiveWorkflow(workflowName);

    if (existingActiveEntity.isEmpty()) {
      log.info(
          "WorkflowDefinitionServiceImpl | Workflow definition with name {} is already inactive",
          workflowName);
      return;
    }
    existingActiveEntity.get().setStatus(WorkflowDefinitionStatus.INACTIVE);
    workflowDefinitionRepository.save(existingActiveEntity.get());
    log.info(
        "WorkflowDefinitionServiceImpl | Deactivated the active version {}",
        existingActiveEntity.get().getDefinitionVersion());
  }

  private Optional<WorkflowDefinitionEntity> fetchActiveWorkflow(String workflowName) {
    return workflowDefinitionRepository.findByNameAndStatus(
        workflowName, WorkflowDefinitionStatus.ACTIVE);
  }

  private void validateSteps(List<CreateWorkflowStepRequest> createWorkflowStepRequest) {
    List<Integer> sequenceNumbers =
        new ArrayList<>(
            createWorkflowStepRequest.stream()
                .map(CreateWorkflowStepRequest::sequenceNumber)
                .toList());

    validateSequenceNumbersInRequest(sequenceNumbers);
    log.info("WorkflowDefinitionServiceImpl | Steps validated");
  }

  private static void validateSequenceNumbersInRequest(List<Integer> sequenceNumbers) {
    if (sequenceNumbers.isEmpty()) {
      log.error("WorkflowDefinitionServiceImpl | Sequence number is empty");
      throw new InvalidWorkflowDefinitionException("Sequence numbers cannot be empty in request");
    }
    Collections.sort(sequenceNumbers);
    for (int i = 0; i < sequenceNumbers.size(); i++) {
      if (sequenceNumbers.get(i) != i + 1) {
        log.error(
            "WorkflowDefinitionServiceImpl | "
                + "Sequence numbers are not unique or doesn't start with 1 or have gaps");
        throw new InvalidWorkflowDefinitionException(
            "Workflow steps must have unique sequence numbers starting from 1.");
      }
    }
  }

  private Integer determineDefinitionVersion(String workflowName) {
    Optional<WorkflowDefinitionEntity> workflowDefinitionEntity = findByWorkflowName(workflowName);
    if (workflowDefinitionEntity.isEmpty()) {
      log.info(
          "WorkflowDefinitionServiceImpl | No existing workflow definition found with name: {}",
          workflowName);
      return 1;
    }
    log.info(
        "WorkflowDefinitionServiceImpl | Workflow definition found with name: {}", workflowName);
    return (workflowDefinitionEntity.get().getDefinitionVersion() + 1);
  }

  private Optional<WorkflowDefinitionEntity> findByWorkflowName(String workflowName) {
    return workflowDefinitionRepository.findTopByNameOrderByDefinitionVersionDesc(workflowName);
  }

  private WorkflowDefinitionEntity buildWorkflowDefinitionEntity(
      UUID workflowId, Integer definitionVersion, CreateWorkflowDefinitionRequest request) {
    return new WorkflowDefinitionEntity(
        workflowId,
        definitionVersion,
        request.name(),
        request.description(),
        WorkflowDefinitionStatus.INACTIVE);
  }

  private List<WorkflowStepEntity> buildStepEntities(
      UUID workflowId, CreateWorkflowDefinitionRequest request) {
    List<WorkflowStepEntity> entities = new ArrayList<>();
    for (CreateWorkflowStepRequest step : request.steps()) {
      WorkflowStepEntity entity = new WorkflowStepEntity();
      entity.setId(UUID.randomUUID());
      entity.setWorkflowDefinitionId(workflowId);
      entity.setActivityName(step.activityName());
      entity.setSequenceNumber(step.sequenceNumber());
      entity.setTimeoutSeconds(step.timeoutSeconds());

      entities.add(entity);
    }
    return entities;
  }
}
