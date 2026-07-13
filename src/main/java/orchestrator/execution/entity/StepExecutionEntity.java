package orchestrator.execution.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.Instant;
import java.util.UUID;
import orchestrator.common.entity.BaseEntity;
import orchestrator.execution.entity.enums.StepStatus;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "step_execution")
public class StepExecutionEntity extends BaseEntity {
  @Column(nullable = false)
  private UUID workflowExecutionId;

  @Column(nullable = false)
  private UUID workflowStepId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @NotNull
  private StepStatus status;

  @Column(nullable = false)
  @PositiveOrZero
  @NotNull
  private Integer retryCount = 0;

  @Column(nullable = false)
  @NotNull
  @CreatedDate
  private Instant startedAt;

  private Instant completedAt;

  @Column(columnDefinition = "TEXT")
  private String errorMessage;

  protected StepExecutionEntity() {
    // required by JPA
  }

  public StepExecutionEntity(
      UUID id,
      UUID workflowExecutionId,
      UUID workflowStepId,
      StepStatus status,
      Integer retryCount) {
    this.setId(id);
    this.workflowExecutionId = workflowExecutionId;
    this.workflowStepId = workflowStepId;
    this.status = status;
    this.retryCount = retryCount;
  }

  public void setWorkflowExecutionId(UUID workflowExecutionId) {
    this.workflowExecutionId = workflowExecutionId;
  }

  public void setWorkflowStepId(UUID workflowStepId) {
    this.workflowStepId = workflowStepId;
  }

  public void setStatus(StepStatus status) {
    this.status = status;
  }

  public void setStartedAt(Instant startedAt) {
    this.startedAt = startedAt;
  }

  public void setCompletedAt(Instant completedAt) {
    this.completedAt = completedAt;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public void setRetryCount(Integer retryCount) {
    this.retryCount = retryCount;
  }

  public UUID getWorkflowExecutionId() {
    return workflowExecutionId;
  }

  public UUID getWorkflowStepId() {
    return workflowStepId;
  }

  public StepStatus getStatus() {
    return status;
  }

  public Integer getRetryCount() {
    return retryCount;
  }

  public Instant getStartedAt() {
    return startedAt;
  }

  public Instant getCompletedAt() {
    return completedAt;
  }

  public String getErrorMessage() {
    return errorMessage;
  }
}
