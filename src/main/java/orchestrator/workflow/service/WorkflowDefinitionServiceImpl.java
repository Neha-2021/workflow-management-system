package orchestrator.workflow.service;

import java.util.*;
import orchestrator.common.exception.InvalidWorkflowDefinitionException;
import orchestrator.workflow.dto.request.CreateWorkflowDefinitionRequest;
import orchestrator.workflow.dto.request.CreateWorkflowStepRequest;
import orchestrator.workflow.dto.response.CreateWorkflowDefinitionResponse;
import orchestrator.workflow.entity.WorkflowDefinitionEntity;
import orchestrator.workflow.entity.WorkflowStepEntity;
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
    Optional<WorkflowDefinitionEntity> workflowDefinitionEntity =
        workflowDefinitionRepository.findTopByNameOrderByDefinitionVersionDesc(workflowName);
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

  private WorkflowDefinitionEntity buildWorkflowDefinitionEntity(
      UUID workflowId, Integer definitionVersion, CreateWorkflowDefinitionRequest request) {
    return new WorkflowDefinitionEntity(
        workflowId, definitionVersion, request.name(), request.description());
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
