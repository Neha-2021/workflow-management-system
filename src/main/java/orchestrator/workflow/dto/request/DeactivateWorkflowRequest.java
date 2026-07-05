package orchestrator.workflow.dto.request;

import jakarta.validation.constraints.NotBlank;

public record DeactivateWorkflowRequest(@NotBlank String workflowName) {}
