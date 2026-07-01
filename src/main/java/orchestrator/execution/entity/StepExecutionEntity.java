package orchestrator.execution.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import orchestrator.common.entity.BaseEntity;
import orchestrator.execution.entity.enums.StepStatus;

import java.time.Instant;
import java.util.UUID;

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
    private Instant startedAt;

    private Instant completedAt;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    protected StepExecutionEntity() {

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
