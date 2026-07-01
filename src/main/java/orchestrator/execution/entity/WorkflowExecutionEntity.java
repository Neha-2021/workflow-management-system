package orchestrator.execution.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import orchestrator.common.entity.BaseEntity;
import orchestrator.common.enums.WorkflowStatus;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

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
    private Instant startedAt;

    private Instant completedAt;

    protected WorkflowExecutionEntity() {
        // repository usage
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
