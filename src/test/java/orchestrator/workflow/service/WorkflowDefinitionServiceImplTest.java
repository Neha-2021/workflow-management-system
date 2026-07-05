package orchestrator.workflow.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WorkflowDefinitionServiceImplTest {

  @Mock private WorkflowDefinitionRepository workflowDefinitionRepository;

  @Mock private WorkflowStepRepository workflowStepRepository;

  @InjectMocks private WorkflowDefinitionServiceImpl workflowDefinitionService;

  @Test
  void shouldCreateWorkflowDefinitionSuccessfullyWithVersion1() {
    CreateWorkflowDefinitionRequest request =
        new CreateWorkflowDefinitionRequest(
            "ORDER_WORKFLOW",
            "Order processing",
            List.of(
                new CreateWorkflowStepRequest(1, "VALIDATE_ORDER_REQUEST", 30),
                new CreateWorkflowStepRequest(2, "PROCESS_PAYMENT", 60),
                new CreateWorkflowStepRequest(3, "NOTIFY_CUSTOMER", 30)));

    given(workflowDefinitionRepository.findTopByNameOrderByDefinitionVersionDesc("ORDER_WORKFLOW"))
        .willReturn(Optional.empty());

    CreateWorkflowDefinitionResponse response = workflowDefinitionService.createWorkflow(request);

    assertThat(response.workflowId()).isNotNull();
    assertThat(response.version()).isEqualTo(1);

    ArgumentCaptor<WorkflowDefinitionEntity> captor =
        ArgumentCaptor.forClass(WorkflowDefinitionEntity.class);
    verify(workflowDefinitionRepository).save(captor.capture());
    WorkflowDefinitionEntity entity = captor.getValue();

    assertThat(entity.getName()).isEqualTo("ORDER_WORKFLOW");
    assertThat(entity.getDefinitionVersion()).isEqualTo(1);
    assertThat(entity.getDescription()).isEqualTo("Order processing");

    ArgumentCaptor<List<WorkflowStepEntity>> captor1 = ArgumentCaptor.forClass(List.class);
    verify(workflowStepRepository).saveAll(captor1.capture());
    List<WorkflowStepEntity> stepsEntity = captor1.getValue();

    assertThat(stepsEntity.size()).isEqualTo(3);
    assertThat(stepsEntity.get(0).getSequenceNumber()).isEqualTo(1);
    assertThat(stepsEntity.get(1).getSequenceNumber()).isEqualTo(2);
    assertThat(stepsEntity.get(2).getSequenceNumber()).isEqualTo(3);
  }

  @Test
  void shouldCreateWorkflowDefinitionSuccessfullyWithVersion2() {
    CreateWorkflowDefinitionRequest request =
        new CreateWorkflowDefinitionRequest(
            "ORDER_WORKFLOW",
            "Order processing V2",
            List.of(
                new CreateWorkflowStepRequest(1, "VALIDATE_ORDER_REQUEST", 30),
                new CreateWorkflowStepRequest(2, "PROCESS_PAYMENT", 60),
                new CreateWorkflowStepRequest(3, "NOTIFY_CUSTOMER", 30)));

    given(workflowDefinitionRepository.findTopByNameOrderByDefinitionVersionDesc("ORDER_WORKFLOW"))
        .willReturn(
            Optional.of(
                new WorkflowDefinitionEntity(
                    UUID.randomUUID(),
                    1,
                    "ORDER_WORKFLOW",
                    "Order processing V1",
                    WorkflowDefinitionStatus.INACTIVE)));

    CreateWorkflowDefinitionResponse response = workflowDefinitionService.createWorkflow(request);

    assertThat(response.workflowId()).isNotNull();
    assertThat(response.version()).isEqualTo(2);

    ArgumentCaptor<WorkflowDefinitionEntity> captor =
        ArgumentCaptor.forClass(WorkflowDefinitionEntity.class);
    verify(workflowDefinitionRepository).save(captor.capture());
    WorkflowDefinitionEntity entity = captor.getValue();

    assertThat(entity.getName()).isEqualTo("ORDER_WORKFLOW");
    assertThat(entity.getDefinitionVersion()).isEqualTo(2);
    assertThat(entity.getDescription()).isEqualTo("Order processing V2");

    ArgumentCaptor<List<WorkflowStepEntity>> captor1 = ArgumentCaptor.forClass(List.class);
    verify(workflowStepRepository).saveAll(captor1.capture());
    List<WorkflowStepEntity> stepsEntity = captor1.getValue();

    assertThat(stepsEntity.size()).isEqualTo(3);
    assertThat(stepsEntity.get(0).getSequenceNumber()).isEqualTo(1);
    assertThat(stepsEntity.get(1).getSequenceNumber()).isEqualTo(2);
    assertThat(stepsEntity.get(2).getSequenceNumber()).isEqualTo(3);
  }

  @Test
  void shouldThrowExWhenSequenceNumberHasDuplicates() {
    CreateWorkflowDefinitionRequest request =
        new CreateWorkflowDefinitionRequest(
            "ORDER_WORKFLOW",
            "Order processing",
            List.of(
                new CreateWorkflowStepRequest(1, "VALIDATE_ORDER_REQUEST", 30),
                new CreateWorkflowStepRequest(1, "PROCESS_PAYMENT", 60),
                new CreateWorkflowStepRequest(2, "NOTIFY_CUSTOMER", 30)));

    Throwable exception =
        assertThrows(
            InvalidWorkflowDefinitionException.class,
            () -> {
              workflowDefinitionService.createWorkflow(request);
            });

    assertThat(exception.getMessage())
        .isEqualTo("Workflow steps must have unique sequence numbers starting from 1.");

    verifyNoInteractions(workflowDefinitionRepository);
    verifyNoInteractions(workflowStepRepository);
  }

  @Test
  void shouldThrowExWhenSequenceNumberDoesNotStartWith1() {
    CreateWorkflowDefinitionRequest request =
        new CreateWorkflowDefinitionRequest(
            "ORDER_WORKFLOW",
            "Order processing",
            List.of(
                new CreateWorkflowStepRequest(2, "VALIDATE_ORDER_REQUEST", 30),
                new CreateWorkflowStepRequest(3, "PROCESS_PAYMENT", 60),
                new CreateWorkflowStepRequest(4, "NOTIFY_CUSTOMER", 30)));

    Throwable exception =
        assertThrows(
            InvalidWorkflowDefinitionException.class,
            () -> {
              workflowDefinitionService.createWorkflow(request);
            });

    assertThat(exception.getMessage())
        .isEqualTo("Workflow steps must have unique sequence numbers starting from 1.");

    verifyNoInteractions(workflowDefinitionRepository);
    verifyNoInteractions(workflowStepRepository);
  }

  @Test
  void shouldThrowExWhenSequenceNumberHasGaps() {
    CreateWorkflowDefinitionRequest request =
        new CreateWorkflowDefinitionRequest(
            "ORDER_WORKFLOW",
            "Order processing",
            List.of(
                new CreateWorkflowStepRequest(1, "VALIDATE_ORDER_REQUEST", 30),
                new CreateWorkflowStepRequest(3, "PROCESS_PAYMENT", 60),
                new CreateWorkflowStepRequest(4, "NOTIFY_CUSTOMER", 30)));

    Throwable exception =
        assertThrows(
            InvalidWorkflowDefinitionException.class,
            () -> {
              workflowDefinitionService.createWorkflow(request);
            });

    assertThat(exception.getMessage())
        .isEqualTo("Workflow steps must have unique sequence numbers starting from 1.");

    verifyNoInteractions(workflowDefinitionRepository);
    verifyNoInteractions(workflowStepRepository);
  }

  @Test
  void shouldThrowExWhenStepsIsEmpty() {
    CreateWorkflowDefinitionRequest request =
        new CreateWorkflowDefinitionRequest("ORDER_WORKFLOW", "Order processing", List.of());

    Throwable exception =
        assertThrows(
            InvalidWorkflowDefinitionException.class,
            () -> {
              workflowDefinitionService.createWorkflow(request);
            });

    assertThat(exception.getMessage()).isEqualTo("Sequence numbers cannot be empty in request");

    verifyNoInteractions(workflowDefinitionRepository);
    verifyNoInteractions(workflowStepRepository);
  }

  @Test
  void shouldActivateWorkflowSuccessfully() {
    ActivateWorkflowRequest request = new ActivateWorkflowRequest("ORDER_WORKFLOW", 2);

    WorkflowDefinitionEntity requested =
        new WorkflowDefinitionEntity(
            UUID.randomUUID(), 2, "ORDER_WORKFLOW", "V2", WorkflowDefinitionStatus.INACTIVE);

    WorkflowDefinitionEntity active =
        new WorkflowDefinitionEntity(
            UUID.randomUUID(), 1, "ORDER_WORKFLOW", "V1", WorkflowDefinitionStatus.ACTIVE);

    given(workflowDefinitionRepository.findByNameAndDefinitionVersion("ORDER_WORKFLOW", 2))
        .willReturn(Optional.of(requested));

    given(
            workflowDefinitionRepository.findByNameAndStatus(
                "ORDER_WORKFLOW", WorkflowDefinitionStatus.ACTIVE))
        .willReturn(Optional.of(active));

    workflowDefinitionService.activateWorkflow(request);

    assertThat(active.getStatus()).isEqualTo(WorkflowDefinitionStatus.INACTIVE);
    assertThat(requested.getStatus()).isEqualTo(WorkflowDefinitionStatus.ACTIVE);

    verify(workflowDefinitionRepository).save(active);
    verify(workflowDefinitionRepository).save(requested);
  }

  @Test
  void shouldActivateWorkflowWhenNoActiveVersionExists() {
    ActivateWorkflowRequest request = new ActivateWorkflowRequest("ORDER_WORKFLOW", 1);

    WorkflowDefinitionEntity requested =
        new WorkflowDefinitionEntity(
            UUID.randomUUID(), 1, "ORDER_WORKFLOW", "V1", WorkflowDefinitionStatus.INACTIVE);

    given(workflowDefinitionRepository.findByNameAndDefinitionVersion("ORDER_WORKFLOW", 1))
        .willReturn(Optional.of(requested));

    given(
            workflowDefinitionRepository.findByNameAndStatus(
                "ORDER_WORKFLOW", WorkflowDefinitionStatus.ACTIVE))
        .willReturn(Optional.empty());

    workflowDefinitionService.activateWorkflow(request);

    assertThat(requested.getStatus()).isEqualTo(WorkflowDefinitionStatus.ACTIVE);

    verify(workflowDefinitionRepository).save(requested);
    verify(workflowDefinitionRepository, never())
        .save(argThat(entity -> entity.getDefinitionVersion() != 1));
  }

  @Test
  void shouldReturnWhenWorkflowAlreadyActive() {
    ActivateWorkflowRequest request = new ActivateWorkflowRequest("ORDER_WORKFLOW", 2);

    WorkflowDefinitionEntity requested =
        new WorkflowDefinitionEntity(
            UUID.randomUUID(), 2, "ORDER_WORKFLOW", "V2", WorkflowDefinitionStatus.ACTIVE);

    given(workflowDefinitionRepository.findByNameAndDefinitionVersion("ORDER_WORKFLOW", 2))
        .willReturn(Optional.of(requested));

    workflowDefinitionService.activateWorkflow(request);

    verify(workflowDefinitionRepository, never()).findByNameAndStatus(any(), any());
    verify(workflowDefinitionRepository, never()).save(any());
  }

  @Test
  void shouldThrowWhenWorkflowDefinitionDoesNotExist() {
    ActivateWorkflowRequest request = new ActivateWorkflowRequest("ORDER_WORKFLOW", 10);

    given(workflowDefinitionRepository.findByNameAndDefinitionVersion("ORDER_WORKFLOW", 10))
        .willReturn(Optional.empty());

    assertThrows(
        WorkflowDefinitionNotFoundException.class,
        () -> workflowDefinitionService.activateWorkflow(request));

    verify(workflowDefinitionRepository, never()).findByNameAndStatus(any(), any());
    verify(workflowDefinitionRepository, never()).save(any());
  }

  @Test
  void shouldDeactivateWorkflowSuccessfully() {
    DeactivateWorkflowRequest request = new DeactivateWorkflowRequest("ORDER_WORKFLOW");

    WorkflowDefinitionEntity latest =
        new WorkflowDefinitionEntity(
            UUID.randomUUID(), 2, "ORDER_WORKFLOW", "V2", WorkflowDefinitionStatus.ACTIVE);

    given(workflowDefinitionRepository.findTopByNameOrderByDefinitionVersionDesc("ORDER_WORKFLOW"))
        .willReturn(Optional.of(latest));
    given(
            workflowDefinitionRepository.findByNameAndStatus(
                "ORDER_WORKFLOW", WorkflowDefinitionStatus.ACTIVE))
        .willReturn(Optional.of(latest));

    workflowDefinitionService.deactivateWorkflow(request);

    assertThat(latest.getStatus()).isEqualTo(WorkflowDefinitionStatus.INACTIVE);

    verify(workflowDefinitionRepository).save(latest);
  }

  @Test
  void shouldReturnWhenWorkflowAlreadyInactive() {
    DeactivateWorkflowRequest request = new DeactivateWorkflowRequest("ORDER_WORKFLOW");

    WorkflowDefinitionEntity latest =
        new WorkflowDefinitionEntity(
            UUID.randomUUID(), 2, "ORDER_WORKFLOW", "V2", WorkflowDefinitionStatus.INACTIVE);

    given(workflowDefinitionRepository.findTopByNameOrderByDefinitionVersionDesc("ORDER_WORKFLOW"))
        .willReturn(Optional.of(latest));

    given(
            workflowDefinitionRepository.findByNameAndStatus(
                "ORDER_WORKFLOW", WorkflowDefinitionStatus.ACTIVE))
        .willReturn(Optional.empty());

    workflowDefinitionService.deactivateWorkflow(request);

    verify(workflowDefinitionRepository, never()).save(any());
  }

  @Test
  void shouldThrowWhenWorkflowDoesNotExistDuringDeactivation() {
    DeactivateWorkflowRequest request = new DeactivateWorkflowRequest("ORDER_WORKFLOW");

    given(workflowDefinitionRepository.findTopByNameOrderByDefinitionVersionDesc("ORDER_WORKFLOW"))
        .willReturn(Optional.empty());

    assertThrows(
        WorkflowDefinitionNotFoundException.class,
        () -> workflowDefinitionService.deactivateWorkflow(request));

    verify(workflowDefinitionRepository, never()).findByNameAndStatus(any(), any());
    verify(workflowDefinitionRepository, never()).save(any());
  }
}
