package orchestrator.execution.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import orchestrator.execution.queue.ExecutionQueue;
import orchestrator.execution.repository.StepExecutionRepository;
import orchestrator.execution.repository.WorkflowExecutionRepository;
import orchestrator.workflow.entity.WorkflowDefinitionEntity;
import orchestrator.workflow.entity.WorkflowStepEntity;
import orchestrator.workflow.enums.WorkflowDefinitionStatus;
import orchestrator.workflow.repository.WorkflowDefinitionRepository;
import orchestrator.workflow.repository.WorkflowStepRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WorkflowExecutionServiceImplTest {
  @Mock private WorkflowDefinitionRepository workflowDefinitionRepository;

  @Mock private WorkflowStepRepository workflowStepRepository;

  @Mock private StepExecutionRepository stepExecutionRepository;

  @Mock private WorkflowExecutionRepository workflowExecutionRepository;

  @Mock private ExecutionQueue executionQueue;

  @InjectMocks private WorkflowExecutionServiceImpl workflowExecutionService;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void shouldStartWorkflowSuccessfully() {
    StartWorkflowExecutionRequest request =
        new StartWorkflowExecutionRequest(
            "ORDER_WORKFLOW", objectMapper.createObjectNode().put("orderId", "123"));

    UUID workflowDefinitionId = UUID.randomUUID();

    WorkflowDefinitionEntity workflowDefinition =
        new WorkflowDefinitionEntity(
            workflowDefinitionId,
            2,
            "ORDER_WORKFLOW",
            "Order workflow",
            WorkflowDefinitionStatus.ACTIVE);

    WorkflowStepEntity firstStep = new WorkflowStepEntity();
    firstStep.setId(UUID.randomUUID());
    firstStep.setWorkflowDefinitionId(workflowDefinitionId);
    firstStep.setSequenceNumber(1);
    firstStep.setActivityName("VALIDATE_ORDER");

    given(
            workflowDefinitionRepository.findTopByNameAndStatusOrderByDefinitionVersionDesc(
                "ORDER_WORKFLOW", WorkflowDefinitionStatus.ACTIVE))
        .willReturn(Optional.of(workflowDefinition));

    given(
            workflowStepRepository.findByWorkflowDefinitionIdAndSequenceNumber(
                workflowDefinitionId, 1))
        .willReturn(Optional.of(firstStep));

    StartWorkflowExecutionResponse response = workflowExecutionService.startWorkflow(request);

    assertThat(response).isNotNull();
    assertThat(response.status()).isEqualTo(WorkflowStatus.RUNNING);
    assertThat(response.workflowExecutionId()).isNotNull();

    verify(workflowExecutionRepository).save(any(WorkflowExecutionEntity.class));
    verify(stepExecutionRepository).save(any(StepExecutionEntity.class));
    verify(executionQueue).submit(any());
  }

  @Test
  void shouldThrowWhenWorkflowDefinitionDoesNotExist() {

    StartWorkflowExecutionRequest request =
        new StartWorkflowExecutionRequest("ORDER_WORKFLOW", objectMapper.createObjectNode());

    given(
            workflowDefinitionRepository.findTopByNameAndStatusOrderByDefinitionVersionDesc(
                "ORDER_WORKFLOW", WorkflowDefinitionStatus.ACTIVE))
        .willReturn(Optional.empty());

    Throwable exception =
        assertThrows(
            WorkflowDefinitionNotFoundException.class,
            () -> workflowExecutionService.startWorkflow(request));

    assertThat(exception.getMessage())
        .isEqualTo("No active workflow definition found by name: 'ORDER_WORKFLOW'");

    verifyNoInteractions(workflowExecutionRepository);
    verifyNoInteractions(stepExecutionRepository);
  }

  @Test
  void shouldThrowWhenFirstWorkflowStepDoesNotExist() {

    StartWorkflowExecutionRequest request =
        new StartWorkflowExecutionRequest("ORDER_WORKFLOW", objectMapper.createObjectNode());

    UUID workflowDefinitionId = UUID.randomUUID();

    WorkflowDefinitionEntity workflowDefinition =
        new WorkflowDefinitionEntity(
            workflowDefinitionId, 1, "ORDER_WORKFLOW", "Workflow", WorkflowDefinitionStatus.ACTIVE);

    given(
            workflowDefinitionRepository.findTopByNameAndStatusOrderByDefinitionVersionDesc(
                "ORDER_WORKFLOW", WorkflowDefinitionStatus.ACTIVE))
        .willReturn(Optional.of(workflowDefinition));

    given(
            workflowStepRepository.findByWorkflowDefinitionIdAndSequenceNumber(
                workflowDefinitionId, 1))
        .willReturn(Optional.empty());

    Throwable exception =
        assertThrows(
            WorkflowStepNotFoundException.class,
            () -> workflowExecutionService.startWorkflow(request));

    assertThat(exception.getMessage()).contains(workflowDefinitionId.toString());

    verifyNoInteractions(workflowExecutionRepository);
    verifyNoInteractions(stepExecutionRepository);
  }

  @Test
  void shouldPersistWorkflowExecutionAndStepExecution() {

    StartWorkflowExecutionRequest request =
        new StartWorkflowExecutionRequest(
            "ORDER_WORKFLOW", objectMapper.createObjectNode().put("orderId", "123"));

    UUID workflowDefinitionId = UUID.randomUUID();

    WorkflowDefinitionEntity workflowDefinition =
        new WorkflowDefinitionEntity(
            workflowDefinitionId, 1, "ORDER_WORKFLOW", "Workflow", WorkflowDefinitionStatus.ACTIVE);

    WorkflowStepEntity firstStep = new WorkflowStepEntity();
    UUID workflowStepId = UUID.randomUUID();

    firstStep.setId(workflowStepId);
    firstStep.setWorkflowDefinitionId(workflowDefinitionId);
    firstStep.setSequenceNumber(1);
    firstStep.setActivityName("VALIDATE_ORDER");

    given(
            workflowDefinitionRepository.findTopByNameAndStatusOrderByDefinitionVersionDesc(
                "ORDER_WORKFLOW", WorkflowDefinitionStatus.ACTIVE))
        .willReturn(Optional.of(workflowDefinition));

    given(
            workflowStepRepository.findByWorkflowDefinitionIdAndSequenceNumber(
                workflowDefinitionId, 1))
        .willReturn(Optional.of(firstStep));

    workflowExecutionService.startWorkflow(request);

    ArgumentCaptor<WorkflowExecutionEntity> executionCaptor =
        ArgumentCaptor.forClass(WorkflowExecutionEntity.class);
    verify(workflowExecutionRepository).save(executionCaptor.capture());
    WorkflowExecutionEntity execution = executionCaptor.getValue();
    assertThat(execution.getWorkflowDefinitionId()).isEqualTo(workflowDefinitionId);
    assertThat(execution.getStatus()).isEqualTo(WorkflowStatus.RUNNING);

    ArgumentCaptor<StepExecutionEntity> stepCaptor =
        ArgumentCaptor.forClass(StepExecutionEntity.class);
    verify(stepExecutionRepository).save(stepCaptor.capture());
    StepExecutionEntity step = stepCaptor.getValue();
    assertThat(step.getWorkflowStepId()).isEqualTo(workflowStepId);
    assertThat(step.getStatus()).isEqualTo(StepStatus.PENDING);
    assertThat(step.getRetryCount()).isZero();

    verify(executionQueue).submit(step.getId());
  }
}
