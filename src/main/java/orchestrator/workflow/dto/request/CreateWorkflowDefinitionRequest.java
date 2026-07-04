package orchestrator.workflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record CreateWorkflowDefinitionRequest(
    @NotBlank String name, String description, @NotEmpty List<CreateWorkflowStepRequest> steps) {}
