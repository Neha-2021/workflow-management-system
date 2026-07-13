package orchestrator.execution.engine;

import java.util.Optional;
import java.util.UUID;
import orchestrator.common.exception.WorkflowExecutionNotFoundException;
import orchestrator.common.exception.WorkflowStepNotFoundException;
import orchestrator.execution.activity.Activity;
import orchestrator.execution.activity.ActivityRegistry;
import orchestrator.execution.activity.ActivityResult;
import orchestrator.execution.entity.StepExecutionEntity;
import orchestrator.execution.entity.WorkflowExecutionEntity;
import orchestrator.execution.repository.StepExecutionRepository;
import orchestrator.execution.repository.WorkflowExecutionRepository;
import orchestrator.workflow.entity.WorkflowStepEntity;
import orchestrator.workflow.repository.WorkflowStepRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WorkflowEngine {
  private final StepExecutionRepository stepExecutionRepository;
  private final WorkflowExecutionRepository workflowExecutionRepository;
  private final WorkflowStepRepository workflowStepRepository;
  private final ActivityRegistry activityRegistry;

  private static final Logger log = LoggerFactory.getLogger(WorkflowEngine.class);

  public WorkflowEngine(
      StepExecutionRepository stepExecutionRepository,
      WorkflowExecutionRepository workflowExecutionRepository,
      WorkflowStepRepository workflowStepRepository,
      ActivityRegistry activityRegistry) {
    this.stepExecutionRepository = stepExecutionRepository;
    this.workflowExecutionRepository = workflowExecutionRepository;
    this.workflowStepRepository = workflowStepRepository;
    this.activityRegistry = activityRegistry;
  }

  public void execute(UUID stepExecutionId) {
    StepExecutionEntity stepExecutionEntity =
        findStepExecutionEntityElseThrowException(stepExecutionId);
    log.info("WorkflowEngine | Step execution fetched by id: {}", stepExecutionId);

    WorkflowStepEntity workflowStepEntity =
        findWorkflowStepEntityElseThrowException(stepExecutionEntity.getWorkflowStepId());
    log.info("WorkflowEngine | Step entity fetched");

    WorkflowExecutionEntity workflowExecutionEntity =
        findWorkflowExecutionEntityElseThrowException(stepExecutionEntity.getWorkflowExecutionId());
    log.info("WorkflowEngine | Workflow execution fetched");

    Activity activity = activityRegistry.getActivity(workflowStepEntity.getActivityName());
    log.info("WorkflowEngine | Proceeding to execute activity: {}", activity.getName());

    ActivityResult activityResult = activity.execute(workflowExecutionEntity.getInput());

    log.info(
        "WorkflowEngine | Activity {} executed, result: {}",
        workflowStepEntity.getActivityName(),
        activityResult.isSuccess() ? "SUCCESS" : "FAILED");
  }

  private StepExecutionEntity findStepExecutionEntityElseThrowException(UUID stepExecutionId) {
    Optional<StepExecutionEntity> stepExecutionEntity =
        stepExecutionRepository.findById(stepExecutionId);

    if (stepExecutionEntity.isEmpty()) {
      log.warn("WorkflowEngine | Step execution not found by id {}", stepExecutionId);
      throw new WorkflowStepNotFoundException(
          String.format("Step execution not found by '%s'", stepExecutionId));
    }
    return stepExecutionEntity.get();
  }

  private WorkflowStepEntity findWorkflowStepEntityElseThrowException(UUID workflowStepId) {
    Optional<WorkflowStepEntity> workflowStepEntity =
        workflowStepRepository.findById(workflowStepId);

    if (workflowStepEntity.isEmpty()) {
      log.warn("WorkflowEngine | Workflow step not found by id {}", workflowStepId);
      throw new WorkflowStepNotFoundException(
          String.format("Workflow step not found by '%s'", workflowStepId));
    }

    return workflowStepEntity.get();
  }

  private WorkflowExecutionEntity findWorkflowExecutionEntityElseThrowException(
      UUID workflowExecutionId) {
    Optional<WorkflowExecutionEntity> workflowExecutionEntity =
        workflowExecutionRepository.findById(workflowExecutionId);

    if (workflowExecutionEntity.isEmpty()) {
      log.warn("WorkflowEngine | Workflow execution not found by id {}", workflowExecutionId);
      throw new WorkflowExecutionNotFoundException(
          String.format("Workflow execution not found by id: '%s'", workflowExecutionId));
    }

    return workflowExecutionEntity.get();
  }
}
