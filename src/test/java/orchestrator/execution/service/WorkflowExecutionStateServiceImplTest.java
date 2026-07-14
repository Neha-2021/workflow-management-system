package orchestrator.execution.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WorkflowExecutionStateServiceImplTest {
  @Mock private StepExecutionRepository stepExecutionRepository;

  @Mock private WorkflowStepRepository workflowStepRepository;

  @Mock private WorkflowExecutionRepository workflowExecutionRepository;

  @Mock private ExecutionPublisher executionPublisher;

  @InjectMocks private WorkflowExecutionStateServiceImpl workflowExecutionStateService;

  @Test
  void shouldMarkStepRunning() {
    StepExecutionEntity stepExecution = buildStepExecution();

    workflowExecutionStateService.markRunning(stepExecution);

    assertThat(stepExecution.getStatus()).isEqualTo(StepStatus.RUNNING);
    verify(stepExecutionRepository).save(stepExecution);
  }

  @Test
  void shouldMarkStepSuccess() {
    StepExecutionEntity stepExecution = buildStepExecution();

    workflowExecutionStateService.markSuccess(stepExecution);

    assertThat(stepExecution.getStatus()).isEqualTo(StepStatus.SUCCESS);
    assertThat(stepExecution.getCompletedAt()).isNotNull();
    verify(stepExecutionRepository).save(stepExecution);
  }

  @Test
  void shouldMarkStepFailed() {
    StepExecutionEntity stepExecution = buildStepExecution();

    workflowExecutionStateService.markFailed(stepExecution, "Validation failed");

    assertThat(stepExecution.getStatus()).isEqualTo(StepStatus.FAILED);
    assertThat(stepExecution.getErrorMessage()).isEqualTo("Validation failed");
    assertThat(stepExecution.getCompletedAt()).isNotNull();
    verify(stepExecutionRepository).save(stepExecution);
  }

  @Test
  void shouldScheduleNextStepWhenNextWorkflowStepExists() {
    UUID workflowDefinitionId = UUID.randomUUID();
    UUID workflowExecutionId = UUID.randomUUID();

    WorkflowExecutionEntity workflowExecution =
        new WorkflowExecutionEntity(
            workflowExecutionId,
            workflowDefinitionId,
            new ObjectMapper().createObjectNode(),
            WorkflowStatus.RUNNING);

    WorkflowStepEntity currentStep = new WorkflowStepEntity();
    currentStep.setId(UUID.randomUUID());
    currentStep.setWorkflowDefinitionId(workflowDefinitionId);
    currentStep.setSequenceNumber(1);

    WorkflowStepEntity nextStep = new WorkflowStepEntity();
    UUID nextStepId = UUID.randomUUID();
    nextStep.setId(nextStepId);
    nextStep.setWorkflowDefinitionId(workflowDefinitionId);
    nextStep.setSequenceNumber(2);

    given(
            workflowStepRepository.findByWorkflowDefinitionIdAndSequenceNumber(
                workflowDefinitionId, 2))
        .willReturn(Optional.of(nextStep));

    workflowExecutionStateService.scheduleNextStep(workflowExecution, currentStep);

    ArgumentCaptor<StepExecutionEntity> captor = ArgumentCaptor.forClass(StepExecutionEntity.class);
    verify(stepExecutionRepository).save(captor.capture());

    StepExecutionEntity savedStepExecution = captor.getValue();
    assertThat(savedStepExecution.getWorkflowExecutionId()).isEqualTo(workflowExecutionId);
    assertThat(savedStepExecution.getWorkflowStepId()).isEqualTo(nextStepId);
    assertThat(savedStepExecution.getStatus()).isEqualTo(StepStatus.PENDING);
    assertThat(savedStepExecution.getRetryCount()).isZero();
    assertThat(savedStepExecution.getStartedAt()).isNotNull();

    verify(executionPublisher).publish(savedStepExecution.getId());
    verifyNoInteractions(workflowExecutionRepository);
  }

  @Test
  void shouldMarkWorkflowCompletedWhenNextWorkflowStepDoesNotExist() {
    UUID workflowDefinitionId = UUID.randomUUID();

    WorkflowExecutionEntity workflowExecution =
        new WorkflowExecutionEntity(
            UUID.randomUUID(),
            workflowDefinitionId,
            new ObjectMapper().createObjectNode(),
            WorkflowStatus.RUNNING);

    WorkflowStepEntity currentStep = new WorkflowStepEntity();
    currentStep.setId(UUID.randomUUID());
    currentStep.setWorkflowDefinitionId(workflowDefinitionId);
    currentStep.setSequenceNumber(1);

    given(
            workflowStepRepository.findByWorkflowDefinitionIdAndSequenceNumber(
                workflowDefinitionId, 2))
        .willReturn(Optional.empty());

    workflowExecutionStateService.scheduleNextStep(workflowExecution, currentStep);

    assertThat(workflowExecution.getStatus()).isEqualTo(WorkflowStatus.COMPLETED);
    assertThat(workflowExecution.getCompletedAt()).isNotNull();
    verify(workflowExecutionRepository).save(workflowExecution);
    verifyNoInteractions(executionPublisher);
  }

  private StepExecutionEntity buildStepExecution() {
    return new StepExecutionEntity(
        UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), StepStatus.PENDING, 0);
  }
}
