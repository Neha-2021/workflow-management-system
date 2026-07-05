package orchestrator.workflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ActivateWorkflowRequest(@NotBlank String workflowName, @NotNull Integer version) {}
