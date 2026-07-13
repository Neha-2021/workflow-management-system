package orchestrator.execution.engine;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verifyNoInteractions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Optional;
import java.util.UUID;
import orchestrator.common.enums.WorkflowStatus;
import orchestrator.common.exception.WorkflowExecutionNotFoundException;
import orchestrator.common.exception.WorkflowStepNotFoundException;
import orchestrator.execution.activity.Activity;
import orchestrator.execution.activity.ActivityNames;
import orchestrator.execution.activity.ActivityRegistry;
import orchestrator.execution.activity.ActivityResult;
import orchestrator.execution.entity.StepExecutionEntity;
import orchestrator.execution.entity.WorkflowExecutionEntity;
import orchestrator.execution.entity.enums.StepStatus;
import orchestrator.execution.repository.StepExecutionRepository;
import orchestrator.execution.repository.WorkflowExecutionRepository;
import orchestrator.workflow.entity.WorkflowStepEntity;
import orchestrator.workflow.repository.WorkflowStepRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WorkflowEngineTest {
  @Mock private StepExecutionRepository stepExecutionRepository;

  @Mock private WorkflowExecutionRepository workflowExecutionRepository;

  @Mock private WorkflowStepRepository workflowStepRepository;

  @Mock private ActivityRegistry activityRegistry;

  @Mock private Activity activity;

  @InjectMocks private WorkflowEngine workflowEngine;

  @Test
  void shouldExecuteActivitySuccessfully() {

    UUID workflowExecutionId = UUID.randomUUID();
    UUID workflowStepId = UUID.randomUUID();
    UUID stepExecutionId = UUID.randomUUID();

    StepExecutionEntity stepExecution =
        new StepExecutionEntity(
            stepExecutionId, workflowExecutionId, workflowStepId, StepStatus.PENDING, 0);

    WorkflowStepEntity workflowStep = new WorkflowStepEntity();
    workflowStep.setId(workflowStepId);
    workflowStep.setActivityName(ActivityNames.VALIDATE_ORDER);

    ObjectNode input = new ObjectMapper().createObjectNode();
    input.put("orderId", "123");

    WorkflowExecutionEntity workflowExecution =
        new WorkflowExecutionEntity(
            workflowExecutionId, UUID.randomUUID(), input, WorkflowStatus.RUNNING);

    given(stepExecutionRepository.findById(stepExecutionId)).willReturn(Optional.of(stepExecution));

    given(workflowStepRepository.findById(workflowStepId)).willReturn(Optional.of(workflowStep));

    given(workflowExecutionRepository.findById(workflowExecutionId))
        .willReturn(Optional.of(workflowExecution));

    given(activityRegistry.getActivity(ActivityNames.VALIDATE_ORDER)).willReturn(activity);

    given(activity.execute(input)).willReturn(ActivityResult.success());

    workflowEngine.execute(stepExecutionId);

    then(activity).should().execute(input);
  }

  @Test
  void shouldThrowWhenStepExecutionDoesNotExist() {

    UUID stepExecutionId = UUID.randomUUID();

    given(stepExecutionRepository.findById(stepExecutionId)).willReturn(Optional.empty());

    Throwable exception =
        assertThrows(
            WorkflowStepNotFoundException.class, () -> workflowEngine.execute(stepExecutionId));

    assertThat(exception.getMessage()).contains(stepExecutionId.toString());

    verifyNoInteractions(workflowStepRepository, workflowExecutionRepository, activityRegistry);
  }

  @Test
  void shouldThrowWhenWorkflowStepDoesNotExist() {

    UUID workflowExecutionId = UUID.randomUUID();
    UUID workflowStepId = UUID.randomUUID();
    UUID stepExecutionId = UUID.randomUUID();

    StepExecutionEntity stepExecution =
        new StepExecutionEntity(
            stepExecutionId, workflowExecutionId, workflowStepId, StepStatus.PENDING, 0);

    given(stepExecutionRepository.findById(stepExecutionId)).willReturn(Optional.of(stepExecution));

    given(workflowStepRepository.findById(workflowStepId)).willReturn(Optional.empty());

    Throwable exception =
        assertThrows(
            WorkflowStepNotFoundException.class, () -> workflowEngine.execute(stepExecutionId));

    assertThat(exception.getMessage()).contains(workflowStepId.toString());

    verifyNoInteractions(workflowExecutionRepository, activityRegistry);
  }

  @Test
  void shouldThrowWhenWorkflowExecutionDoesNotExist() {

    UUID workflowExecutionId = UUID.randomUUID();
    UUID workflowStepId = UUID.randomUUID();
    UUID stepExecutionId = UUID.randomUUID();

    StepExecutionEntity stepExecution =
        new StepExecutionEntity(
            stepExecutionId, workflowExecutionId, workflowStepId, StepStatus.PENDING, 0);

    WorkflowStepEntity workflowStep = new WorkflowStepEntity();
    workflowStep.setId(workflowStepId);
    workflowStep.setActivityName(ActivityNames.VALIDATE_ORDER);

    given(stepExecutionRepository.findById(stepExecutionId)).willReturn(Optional.of(stepExecution));

    given(workflowStepRepository.findById(workflowStepId)).willReturn(Optional.of(workflowStep));

    given(workflowExecutionRepository.findById(workflowExecutionId)).willReturn(Optional.empty());

    Throwable exception =
        assertThrows(
            WorkflowExecutionNotFoundException.class,
            () -> workflowEngine.execute(stepExecutionId));

    assertThat(exception.getMessage()).contains(workflowExecutionId.toString());

    verifyNoInteractions(activityRegistry);
  }

  @Test
  void shouldInvokeResolvedActivity() {

    UUID workflowExecutionId = UUID.randomUUID();
    UUID workflowStepId = UUID.randomUUID();
    UUID stepExecutionId = UUID.randomUUID();

    StepExecutionEntity stepExecution =
        new StepExecutionEntity(
            stepExecutionId, workflowExecutionId, workflowStepId, StepStatus.PENDING, 0);

    WorkflowStepEntity workflowStep = new WorkflowStepEntity();
    workflowStep.setId(workflowStepId);
    workflowStep.setActivityName(ActivityNames.VALIDATE_ORDER);

    JsonNode input = new ObjectMapper().createObjectNode();

    WorkflowExecutionEntity workflowExecution =
        new WorkflowExecutionEntity(
            workflowExecutionId, UUID.randomUUID(), input, WorkflowStatus.RUNNING);

    given(stepExecutionRepository.findById(stepExecutionId)).willReturn(Optional.of(stepExecution));

    given(workflowStepRepository.findById(workflowStepId)).willReturn(Optional.of(workflowStep));

    given(workflowExecutionRepository.findById(workflowExecutionId))
        .willReturn(Optional.of(workflowExecution));

    given(activityRegistry.getActivity(ActivityNames.VALIDATE_ORDER)).willReturn(activity);

    given(activity.execute(any())).willReturn(ActivityResult.success());

    workflowEngine.execute(stepExecutionId);

    then(activityRegistry).should().getActivity(ActivityNames.VALIDATE_ORDER);

    then(activity).should().execute(any());
  }
}
