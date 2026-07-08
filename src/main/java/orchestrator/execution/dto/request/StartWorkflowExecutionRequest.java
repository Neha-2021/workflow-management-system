package orchestrator.execution.dto.request;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;

public record StartWorkflowExecutionRequest(@NotBlank String workflowName, JsonNode input) {}
