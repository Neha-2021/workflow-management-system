package orchestrator.workflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CreateWorkflowStepRequest(
    @NotBlank Integer sequenceNumber,
    @NotBlank String activityName,
    @Positive Integer timeoutSeconds) {}
