package orchestrator.execution.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;
import orchestrator.common.entity.BaseEntity;
import orchestrator.common.enums.WorkflowStatus;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "workflow_execution")
public class WorkflowExecutionEntity extends BaseEntity {
  @Column(nullable = false)
  private UUID workflowDefinitionId;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "jsonb")
  private JsonNode input;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @NotNull
  private WorkflowStatus status;

  @Column(nullable = false)
  @NotNull
  @CreatedDate
  private Instant startedAt;

  private Instant completedAt;

  protected WorkflowExecutionEntity() {}

  public WorkflowExecutionEntity(
      UUID id, UUID workflowDefinitionId, JsonNode input, WorkflowStatus status) {
    this.setId(id);
    this.workflowDefinitionId = workflowDefinitionId;
    this.input = input;
    this.status = status;
  }

  public void setWorkflowDefinitionId(UUID workflowDefinitionId) {
    this.workflowDefinitionId = workflowDefinitionId;
  }

  public void setStatus(WorkflowStatus status) {
    this.status = status;
  }

  public void setCompletedAt(Instant completedAt) {
    this.completedAt = completedAt;
  }

  public void setInput(JsonNode input) {
    this.input = input;
  }

  public void setStartedAt(Instant startedAt) {
    this.startedAt = startedAt;
  }

  public UUID getWorkflowDefinitionId() {
    return workflowDefinitionId;
  }

  public JsonNode getInput() {
    return input;
  }

  public WorkflowStatus getStatus() {
    return status;
  }

  public Instant getStartedAt() {
    return startedAt;
  }

  public Instant getCompletedAt() {
    return completedAt;
  }
}
