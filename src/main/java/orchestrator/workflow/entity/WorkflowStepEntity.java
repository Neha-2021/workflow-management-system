package orchestrator.workflow.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import orchestrator.common.entity.BaseEntity;

import java.util.UUID;


@Entity
@Table(name = "workflow_step")
public class WorkflowStepEntity extends BaseEntity {
    @NotNull
    @Column(nullable = false)
    private UUID workflowDefinitionId;

    @Positive
    @NotNull
    private Integer sequenceNumber;

    @NotBlank
    private String activityName;

    @Positive
    private Integer timeoutSeconds;

    protected WorkflowStepEntity() {
        // required by JPA
    }

    public UUID getWorkflowDefinitionId() {
        return workflowDefinitionId;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public Integer getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public void setTimeoutSeconds(Integer timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public void setWorkflowDefinitionId(UUID workflowDefinitionId) {
        this.workflowDefinitionId = workflowDefinitionId;
    }
}
